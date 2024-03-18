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

    private String address;
    private int round = 0;
    private final Language language;
    @Nullable
    private LinkedList<Player> players;

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
        players = new LinkedList<>();
        players.add(host);
        numberOfPlayers = players.size();
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
        this.address = address;
        this.round = round;
        this.language = language;
        this.players = players;
        int i = 0;
        this.players.add(guest);
        numberOfPlayers = this.players.size();
    }

    /**
     * Constructor called to create an incomplete room when a player is searching for a room in FindGameActivity
     */
    public Room(String name, int numberOfPlayers, int maxNumberOfPlayers, String address, @NonNull Language language) {
        this.name = name;
        this.numberOfPlayers = numberOfPlayers;
        this.maxNumberOfPlayers = maxNumberOfPlayers;
        this.address = address;
        this.language = language;
        players = null;
    }

    public Room(String jsonRoom) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonRoom);
        name = jsonObject.getString("name");
        numberOfPlayers = jsonObject.getInt("numberOfPlayers");
        maxNumberOfPlayers = jsonObject.getInt("maxNumberOfPlayers");
        inGame = jsonObject.getBoolean("inGame");
        address =  jsonObject.getString("address");
        language = Language.fromString(jsonObject.getString("language"));
        try{
            jsonObject.get("players");
            players = new LinkedList<>();
            JSONArray playersArray = jsonObject.getJSONArray("players");
            for (int i = 0; i < playersArray.length(); i++) {
                players.add(new Player(playersArray.getJSONObject(i).toString()));
            }
        } catch (JSONException e) {
            players = null;
        }
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
        for (Player player : players) {
            if (player.getStatus() == PlayerStatus.CHOOSER)
                return player;
        }
        return null;
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

        for (Player player : players) {
            if (player.getUsername().equals(chooserUsername))
                player.setStatus(PlayerStatus.CHOOSER);
            else
                player.setStatus(PlayerStatus.GUESSER);
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
            player.setStatus(PlayerStatus.GUESSER);
    }

    public Player getPlayer(String username) {
        for (Player player : players) {
            if (player.getUsername().equals(username))
                return player;
        }
        return null;
    }

    public boolean thereIsAChooser() {
        for (Player player : players) {
            if (player.getStatus() == PlayerStatus.CHOOSER)
                return true;
        }
        return false;
    }

    public boolean hasPlayer(String username) {
        for (Player player : players) {
            if (player.getUsername().equals(username))
                return true;
        }
        return false;
    }

    public JSONObject toJSONObject() throws JSONException {
        JSONObject jsonRoom = new JSONObject();
        jsonRoom.put("name", this.name);
        jsonRoom.put("maxNumberOfPlayers", this.maxNumberOfPlayers);
        jsonRoom.put("language", this.language.getLanguageCode());
        if(address != null)
            jsonRoom.put("address", this.address);
        return jsonRoom;
    }

    @Override
    public String toJSON() throws JSONException {
        return this.toJSONObject().toString();
    }
}