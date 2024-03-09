package com.unina.guesstheword.data.model;

import androidx.annotation.NonNull;

public enum Language {
    ENGLISH("en"),
    ITALIAN("it"),

    SPANISH("es"),

    GERMAN("de");
    // Add other languages as needed

    private final String languageCode;

    Language(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getLanguageCode() {
        return this.languageCode;
    }

    public static Language fromString(String languageCode) {
        for (Language language : Language.values()) {
            if (language.getLanguageCode().equalsIgnoreCase(languageCode)) {
                return language;
            }
        }
        throw new IllegalArgumentException("No constant with text " + languageCode + " found");
    }

    public String toViewString() {
        switch (this) {
            case ITALIAN:
                return "Italian";
            case ENGLISH:
                return "English";
            case SPANISH:
                return "Spanish";
            case GERMAN:
                return "German";
            default:
                return "English";
        }
    }
}