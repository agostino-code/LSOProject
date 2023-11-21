package com.example.guesstheword.data.model;

import androidx.annotation.NonNull;

public class ServerMessage {
    private final String message;
    private final boolean isGuessed;
    private final Player sender;

    public ServerMessage(@NonNull String message, String wordToGuess, @NonNull Player sender) {
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