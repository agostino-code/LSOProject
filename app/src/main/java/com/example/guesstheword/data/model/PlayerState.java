package com.example.guesstheword.data.model;

import androidx.annotation.NonNull;

public enum PlayerState {
    SPECTATOR,
    GUESSER,
    CHOOSER;

    @NonNull
    @Override
    public String toString() {
        switch (this) {
            case SPECTATOR:
                return "SPECTATOR";
            case GUESSER:
                return "GUESSER";
            case CHOOSER:
                return "CHOOSER";
            default:
                return "null";
        }
    }
}