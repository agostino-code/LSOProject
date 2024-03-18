package com.unina.guesstheword.data.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

public class ServerMessage implements JSONData {
    private final String message;
    private final boolean isGuessed;
    private final String username;

    public ServerMessage(@NonNull String message, boolean isGuessed, @NonNull String username) {
        this.message = message;
        this.isGuessed = isGuessed;
        this.username = username;
    }

    /**
     * Constructor
     *
     * @param message     message sent by the player
     * @param wordToGuess word to guess, if the room is gaming (can be null)
     * @param username      player who sent the message
     */
    public ServerMessage(@NonNull String message, @Nullable String wordToGuess, @NonNull String username) {
        this.message = message;
        this.username = username;

        if(wordToGuess == null)
            isGuessed = false;
        else {
            String trimmedAndLowerCaseMessage = message.trim().toLowerCase();
            String lowerCaseWordToGuess = wordToGuess.toLowerCase();
            isGuessed = trimmedAndLowerCaseMessage.equals(lowerCaseWordToGuess);
        }
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

    public JSONObject toJSONObject() throws JSONException{
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", this.message);
        jsonObject.put("isGuessed", this.isGuessed);
        jsonObject.put("username", this.username);
        return jsonObject;
    }

    public String toJSON() throws JSONException{
        return this.toJSONObject().toString();
    }
}