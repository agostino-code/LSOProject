package com.example.guesstheword.data.model;

import androidx.annotation.NonNull;

/**
 * Message that will appear in chat
 */
public abstract class ChatMessage {
    private final String message;

    /**
     * The message "message" written;
     */
    public ChatMessage(@NonNull String message) {
        this.message = message;
    }

    /*
     * Getters
     */
    protected String getMessage() {
        return message;
    }
}