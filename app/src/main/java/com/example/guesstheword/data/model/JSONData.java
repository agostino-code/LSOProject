package com.example.guesstheword.data.model;

import org.json.JSONException;

public interface JSONData {

    abstract String toJson() throws JSONException;
}
