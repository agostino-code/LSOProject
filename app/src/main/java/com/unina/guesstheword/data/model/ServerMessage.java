package com.unina.guesstheword.data.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.unina.guesstheword.service.MulticastServer;

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
    public ServerMessage(@NonNull String message, @Nullable String wordToGuess, @NonNull Player sender, MulticastServer server) {
        this.message = message;
        isGuessed = message.equals(wordToGuess);
        this.sender = sender;
        if (isGuessed) {
            server.sendMessages("The word has been guessed by " + sender.getUsername());
        } else {
            server.sendMessages(sender.getUsername() + " said: " + message);
        }
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