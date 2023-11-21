package com.example.guesstheword.data.model;

import androidx.annotation.NonNull;

public enum Language {
    ITALIAN,
    ENGLISH,
    SPANISH,
    GERMAN;

    @NonNull
    @Override
    public String toString() {
        switch (this) {
            case ITALIAN:
                return "it";
            case ENGLISH:
                return "en";
            case SPANISH:
                return "es";
            case GERMAN:
                return "de";
            default:
                return "en";
        }
    }
}