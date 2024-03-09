package com.unina.guesstheword.data.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

public class ServerMessage implements JSONData {
    private final String message;
    private final boolean isGuessed;
    private final String username;

    /**
     * Constructor
     *
     * @param message     message sent by the player
     * @param wordToGuess word to guess, if the room is gaming (can be null)
     * @param username      player who sent the message
     */
    public ServerMessage(@NonNull String message, @Nullable String wordToGuess, @NonNull String username) {
        this.message = message;
        isGuessed = message.equals(wordToGuess);
        this.username = username;
    }

    public ServerMessage(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        message = jsonObject.getString("message");
        isGuessed = jsonObject.getBoolean("isGuessed");
        username = jsonObject.getString("username");

    }

    public String getMessage() {
        return message;
    }

    public boolean isGuessed() {
        return isGuessed;
    }

    public String getUsername() {
        return username;
    }

    //toJSON
    public String toJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("message", message);
            jsonObject.put("isGuessed", isGuessed);
            jsonObject.put("username", username);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return jsonObject.toString();
    }

}