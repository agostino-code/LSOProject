package com.example.guesstheword.data.model;

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
    private boolean isGaming;
    private int port;
    private int round;
    private final Language language;
    @Nullable
    private final LinkedList<Player> players;
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
        isGaming = false;
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
     * @param port               given by the server
     * @param round              How many games were played?
     * @param language           of the words that will be guessed
     * @param players            List of the players already in game
     * @param guest              player which is entering this room
     */
    public Room(String name, int maxNumberOfPlayers, boolean IsGaming, int port, int round, @NonNull Language language, @NonNull LinkedList<Player> players, @NonNull Player guest) {
        this.name = name;
        this.maxNumberOfPlayers = maxNumberOfPlayers;
        this.isGaming = IsGaming;
        this.port = port;
        this.round = round;
        this.language = language;
        this.players = players;
        int i = 0;
        for (Player player : players) {
            if (player.getState() == PlayerState.CHOOSER)
                indexOfChooser = i;
            i++;
        }
        this.players.add(guest);
        numberOfPlayers = this.players.size();
    }

    /**
     * Constructor called to create an incomplete room when a player is searching for a room in FindGameActivity
     */
    public Room(String name, int numberOfPlayers, int maxNumberOfPlayers, int port, @NonNull Language language) {
        this.name = name;
        this.numberOfPlayers = numberOfPlayers;
        this.maxNumberOfPlayers = maxNumberOfPlayers;
        this.port = port;
        this.language = language;
        players = null;
    }

    public Room(String jsonRoom) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonRoom);
        name = jsonObject.getString("name");
        numberOfPlayers = jsonObject.getInt("numberOfPlayers");
        maxNumberOfPlayers = jsonObject.getInt("maxNumberOfPlayers");
        isGaming = jsonObject.getBoolean("isGaming");
        port = jsonObject.getInt("port");
        round = jsonObject.getInt("round");
        language = Language.valueOf(jsonObject.getString("language"));
        if (jsonObject.get("players") == JSONObject.NULL) {
            players = null;
        } else {
            players = new LinkedList<Player>();
            JSONArray playersArray = jsonObject.getJSONArray("players");
            for (int i = 0; i < playersArray.length(); i++) {
                players.add(new Player(playersArray.getJSONObject(i).toString()));
            }
        }
        indexOfChooser = jsonObject.getInt("indexOfChooser");
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

    public boolean isGaming() {
        return isGaming;
    }

    public int getPort() {
        return port;
    }

    public int getRound() {
        return round;
    }

    public Language getLanguage() {
        return language;
    }

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
        isGaming = inGame;
    }

    /**
     * this function set the given player as the chooser and all the other players as the guesser
     */
    public void setChooser(String chooserUsername) {
        int i=0;
        for (Player player : players) {
            if (player.getUsername().equals(chooserUsername)) {
                player.setState(PlayerState.CHOOSER);
                indexOfChooser = i;
            }
            else
                player.setState(PlayerState.GUESSER);
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
            player.setState(null);
    }

    public JSONObject toJSONObject() throws JSONException {
        JSONObject jsonRoom = new JSONObject();

        jsonRoom.put("name", this.name);
        jsonRoom.put("numberOfPlayers", this.numberOfPlayers);
        jsonRoom.put("maxNumberOfPlayers", this.maxNumberOfPlayers);
        jsonRoom.put("isGaming", this.isGaming);
        jsonRoom.put("port", this.port);
        jsonRoom.put("round", this.round);
        jsonRoom.put("language", this.language.toString()); // TODO: è appropriato per il server questo toString?

        if(this.players == null)
            jsonRoom.put("players", JSONObject.NULL);
        else {
            JSONArray playersArray = new JSONArray();
            for (Player player : this.players) {
                playersArray.put(player.toJSON());
            }
            jsonRoom.put("players", playersArray);
        }
        jsonRoom.put("indexOfChooser", this.indexOfChooser);

        return jsonRoom;
    }

    @Override
    public String toJSON() throws JSONException {
        return this.toJSONObject().toString();
    }
}