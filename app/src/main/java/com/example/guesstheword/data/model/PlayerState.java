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

    public String toViewString() {
        switch (this) {
            case SPECTATOR:
                return "Spectator";
            case GUESSER:
                return "Guesser";
            case CHOOSER:
                return "Chooser";
            default:
                return "Not playing";
        }
    }
}