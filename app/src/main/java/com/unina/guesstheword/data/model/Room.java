package com.unina.guesstheword.data.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.util.LinkedList;

public class Room implements JSONData {
    public final static long CHOOSING_TIME_IN_MILLISECONDS = 30000;
    public final static long TIME_PER_ROUND_IN_MILLISECONDS = 15000;

    private final String name;
    private int numberOfPlayers;
    private final int maxNumberOfPlayers;
    private boolean inGame;
//    private int port;

    private String address;
    private int round;
    private final Language language;
    @Nullable
    private LinkedList<Player> players;
    private int indexOfChooser;

    /**
     * Constructor called when the room is first created
     *
     * @param name               of the room (can be null, a room is not forced to have a name)
     * @param maxNumberOfPlayers chosen by the host
     * @param language           of the words that will be guessed
     * @param host               Player which created the room
     */
    public Room(String name, int maxNumberOfPlayers, @NonNull Language language, @NonNull Player host) {
        this.name = name;
        this.maxNumberOfPlayers = maxNumberOfPlayers;
        this.language = language;
        inGame = false;
        round = 0;
        players = new LinkedList<Player>();
        players.add(host);
        numberOfPlayers = players.size();
        indexOfChooser = -1;
        //TODO: create port to pass to the server
    }

    /**
     * Constructor called when a player enter in a pre-existing room
     *
     * @param name               of the room (can be null, a room is not forced to have a name)
     * @param maxNumberOfPlayers chosen by the host
     * @param IsGaming           Is the pre-existing room already gaming?
//     * @param port               given by the server
     * @param address            given by the server
     * @param round              How many games were played?
     * @param language           of the words that will be guessed
     * @param players            List of the players already in game
     * @param guest              player which is entering this room
     */
    public Room(String name, int maxNumberOfPlayers, boolean IsGaming, String address, int round, @NonNull Language language, @NonNull LinkedList<Player> players, @NonNull Player guest) {
        this.name = name;
        this.maxNumberOfPlayers = maxNumberOfPlayers;
        this.inGame = IsGaming;
//        this.port = port;
        this.address = address;
        this.round = round;
        this.language = language;
        this.players = players;
        int i = 0;
        for (Player player : players) {
            if (player.getStatus() == PlayerStatus.CHOOSER)
                indexOfChooser = i;
            i++;
        }
        this.players.add(guest);
        numberOfPlayers = this.players.size();
    }

    /**
     * Constructor called to create an incomplete room when a player is searching for a room in FindGameActivity
     */
    public Room(String name, int numberOfPlayers, int maxNumberOfPlayers,String address, @NonNull Language language) {
        this.name = name;
        this.numberOfPlayers = numberOfPlayers;
        this.maxNumberOfPlayers = maxNumberOfPlayers;
        this.address = address;
//        this.port = port;
        this.language = language;
        players = null;
    }

    public Room(String name, int maxPlayers, Language language) throws JSONException {
        this.name = name;
        this.maxNumberOfPlayers = maxPlayers;
        this.language = language;
        this.inGame = false;
        this.round = 0;
        this.players = new LinkedList<Player>();
        this.numberOfPlayers = 0;
        this.indexOfChooser = -1;
    }
    public Room(String jsonRoom) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonRoom);
        name = jsonObject.getString("name");
        numberOfPlayers = jsonObject.getInt("numberOfPlayers");
        maxNumberOfPlayers = jsonObject.getInt("maxNumberOfPlayers");
        inGame = jsonObject.getBoolean("inGame");
//        port = jsonObject.getInt("port");
        address =  jsonObject.getString("address");
        round = jsonObject.getInt("round");
        language = Language.fromString(jsonObject.getString("language"));
        try{
            jsonObject.get("players");
            players = new LinkedList<Player>();
            JSONArray playersArray = jsonObject.getJSONArray("players");
            for (int i = 0; i < playersArray.length(); i++) {
                players.add(new Player(playersArray.getJSONObject(i).toString()));
            }
        } catch (JSONException e) {
            players = null;
        }
//        indexOfChooser = jsonObject.getInt("indexOfChooser");
    }

    /*
     * Getters
     */
    public String getName() {
        return name;
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public int getMaxNumberOfPlayers() {
        return maxNumberOfPlayers;
    }

    public boolean isInGame() {
        return inGame;
    }

//    public int getPort() {
//        return port;
//    }

    public String getAddress() {
        return address;
    }
    public int getRound() {
        return round;
    }

    public Language getLanguage() {
        return language;
    }

    @Nullable
    public LinkedList<Player> getPlayers() {
        return players;
    }

    public Player getChooser() {
        return players.get(indexOfChooser);
    }

    /*
     * Setters
     */
    public void setIsInGame(boolean inGame) {
        this.inGame = inGame;
    }

//    public void setPort(int port) {
//        this.port = port;
//    }

    public void setAddress(String address) {
        this.address = address;
    }
    /**
     * this function set the given player as the chooser and all the other players as the guesser
     */
    public void setChooser(String chooserUsername) {
        if(chooserUsername == null)
            return;

        int i=0;
        for (Player player : players) {
            if (player.getUsername().equals(chooserUsername)) {
                player.setStatus(PlayerStatus.CHOOSER);
                indexOfChooser = i;
            }
            else
                player.setStatus(PlayerStatus.GUESSER);
            i++;
        }
    }

    /*
     * Methods
     */
    public boolean isRoomFull() {
        return numberOfPlayers >= maxNumberOfPlayers;
    }

    /**
     * @param player which joined the room
     */
    public void addPlayer(Player player) {
        if (isRoomFull())
            throw new IndexOutOfBoundsException("the room is already full!");
        players.add(player);
        numberOfPlayers = players.size();
    }

    /**
     * @param player which has left the room
     */
    public void removePlayer(Player player) {
        players.remove(player);
        numberOfPlayers = players.size();
    }

    public void incrementRound() {
        round++;
    }

    public void resetStateOfAllPlayers() {
        for (Player player : players)
            player.setStatus(null);
    }

    public JSONObject toJSONObject() throws JSONException {
        JSONObject jsonRoom = new JSONObject();
        jsonRoom.put("name", this.name);
        jsonRoom.put("maxNumberOfPlayers", this.maxNumberOfPlayers);
        jsonRoom.put("language", this.language.getLanguageCode());
        return jsonRoom;
    }

    @Override
    public String toJSON() throws JSONException {
        return this.toJSONObject().toString();
    }
}