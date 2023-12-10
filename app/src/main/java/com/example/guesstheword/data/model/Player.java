package com.example.guesstheword.data.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

public class Player implements JSONData {
    @Nullable
    private PlayerState state;
    private int points;
    private final User user;

    /**
     * Constructor called to create the main player who enters a new room
     */
    public Player(@NonNull User user, boolean isRoomGaming) {
        this.user = user;
        state = isRoomGaming ? PlayerState.SPECTATOR : null;
        points = 0;
    }

    /**
     * Constructor called to create the main player who creates a room
     * @param user
     */
    public Player(@NonNull User user) {
        this.user = user;
        state = null;
        points = 0;
    }

    /**
     * Constructor called to create the other players of the room
     * @param avatar chosen between 16 images (must be a number between 1-16, you can use the MACROS of the User class)
     */
    public Player(@Nullable PlayerState state, int points, @NonNull String username, int avatar) {
        this.state = state;
        this.points = points;
        user = new User(username, avatar);
    }

    /**
     * Constructor called when a new player joins the room
     * @param avatar chosen between 16 images (must be a number between 1-16, you can use the MACROS of the User class)
     */
    public Player(@NonNull String username, int avatar) {
        user = new User(username, avatar);
        state = null;
        points = 0;
    }

    public Player(String jsonPlayer) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonPlayer);
        if (jsonObject.has("state")) {
            state = PlayerState.valueOf(jsonObject.getString("state"));
        } else {
            state = null;
        }
        points = jsonObject.getInt("points");
        user = new User(jsonObject.getString("user"));
    }

    /*
     * Setters
     */
    public void setState(@Nullable PlayerState state) {
        this.state = state;
    }

    public void addPoints(int points) {
        this.points = this.points + points;
    }

    public void resetPoints() {
        points = 0;
    }

    /*
     * Getters
     */
    @Nullable
    public PlayerState getState() {
        return state;
    }

    public int getPoints() {
        return points;
    }

    public String getUsername() {
        return user.getUsername();
    }

    /**
     * @return avatar chosen between 16 images (a number between 1-16)
     */
    public int getAvatar() {
        return user.getAvatar();
    }

    /*
     * Override methods
     */
    public boolean equals(Player other) {
        return getUsername().equals(other.getUsername());
    }

    public JSONObject toJSONObject() throws JSONException {
        JSONObject jsonPlayer = new JSONObject();

        if (state != null) {
            jsonPlayer.put("state", state.toString());
        } else {
            jsonPlayer.put("state", JSONObject.NULL);
        }
        jsonPlayer.put("points", this.points);
        jsonPlayer.put("user", this.user.toJSON());

        return jsonPlayer;
    }

    @Override
    public String toJSON() throws JSONException {
        return this.toJSONObject().toString();
    }
}