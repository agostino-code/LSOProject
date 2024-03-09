package com.unina.guesstheword.data.model;

public enum  WhatHappened {
    JOINED("JOINED"),
    LEFT("LEFT");

    private final String value;

    WhatHappened(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}