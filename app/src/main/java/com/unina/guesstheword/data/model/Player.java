package com.unina.guesstheword.data.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

public class Player implements JSONData {
    @NonNull
    private PlayerStatus status;
    private int score;

    private String username;

    private int avatar;

    /**
     * Constructor called to create the main player who enters a new room
     */
    public Player(@NonNull User user, boolean isRoomGaming) {
        this.avatar = user.getAvatar();
        this.username = user.getUsername();
        status = isRoomGaming ? PlayerStatus.SPECTATOR : PlayerStatus.GUESSER;
        score = 0;
    }

    /**
     * Constructor called to create the main player who creates a room
     * @param user
     */
    public Player(@NonNull User user) {
        this.avatar = user.getAvatar();
        this.username = user.getUsername();
        status = PlayerStatus.GUESSER;
        score = 0;
    }

    /**
     * Constructor called to create the other players of the room
     * @param avatar chosen between 16 images (must be a number between 1-16, you can use the MACROS of the User class)
     */
    public Player(@NonNull PlayerStatus status, int score, @NonNull String username, int avatar) {
        this.status = status;
        this.score = score;
        this.username = username;
        this.avatar = avatar;
    }

    /*
    public Player(@NonNull String username, int avatar) {
        user = new User(username, avatar);
        status = PlayerStatus.GUESSER;
        score = 0;
    }*/

    /**
     * Copy constructor
     * @param player player to copy
     */
    public Player(@NonNull Player player) {
        status = player.status;
        score = player.score;
        username = player.username;
        avatar = player.avatar;
    }

    /**
     * Consructor from a JSON string
     * @param json JSON string
     * @throws JSONException
     */
    public Player(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        if(jsonObject.isNull("status")) {
            status = PlayerStatus.GUESSER;
        } else {
            status = PlayerStatus.fromString(jsonObject.getString("status"));
        }
        if(jsonObject.isNull("score")) {
            score = 0;
        } else {
            score = jsonObject.getInt("score");
        }
        username = jsonObject.getString("username");

        if(jsonObject.isNull("avatar")) {
            avatar = 1;
        } else {
            avatar = jsonObject.getInt("avatar");
        }
    }

    /*
     * Setters
     */
    public void setStatus(@NonNull PlayerStatus status) {
        this.status = status;
    }

    public void addPoints(int points) {
        this.score = this.score + points;
    }

    public void resetPoints() {
        score = 0;
    }

    /*
     * Getters
     */
    @NonNull
    public PlayerStatus getStatus() {
        return status;
    }

    public int getScore() {
        return score;
    }

    public String getUsername() {
        return username;
    }

    /**
     * @return avatar chosen between 16 images (a number between 1-16)
     */
    public int getAvatar() {
        return avatar;
    }

    /*
     * Override methods
     */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Player other = (Player) obj;
        return getUsername().equals(other.getUsername());
    }

    public JSONObject toJSONObject() throws JSONException {
        JSONObject jsonPlayer = new JSONObject();
        jsonPlayer.put("status", status.getStatus());
        jsonPlayer.put("score", this.score);
        jsonPlayer.put("username", this.username);
        jsonPlayer.put("avatar", this.avatar);
        return jsonPlayer;
    }

    @Override
    public String toJSON() throws JSONException {
        return this.toJSONObject().toString();
    }
}