package com.unina.guesstheword.data.model;

import androidx.annotation.NonNull;

public enum PlayerStatus {
    SPECTATOR("SPECTATOR"),
    GUESSER("GUESSER"),
    CHOOSER("CHOOSER");

    private final String status;

    PlayerStatus(String status) {
        this.status = status;
    }

    public static PlayerStatus fromString(String status) {
        for (PlayerStatus playerStatus : PlayerStatus.values()) {
            if (playerStatus.status.equalsIgnoreCase(status)) {
                return playerStatus;
            }
        }
        throw new IllegalArgumentException("No constant with text " + status + " found");
    }

    public String getStatus() {
        return status;
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