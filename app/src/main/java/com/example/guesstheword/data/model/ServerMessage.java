package com.example.guesstheword.data.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ServerMessage {
    private final String message;
    private final boolean isGuessed;
    private final Player sender;

    /**
     * Constructor
     *
     * @param message     message sent by the player
     * @param wordToGuess word to guess, if the room is gaming (can be null)
     * @param sender      player who sent the message
     */
    public ServerMessage(@NonNull String message, @Nullable String wordToGuess, @NonNull Player sender) {
        this.message = message;
        isGuessed = message.equals(wordToGuess);
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public boolean isGuessed() {
        return isGuessed;
    }

    public Player getSender() {
        return sender;
    }
}