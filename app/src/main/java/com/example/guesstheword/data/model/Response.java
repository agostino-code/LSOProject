package com.example.guesstheword.data.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Response {
    private String responseType;
    private String data;

    public String getResponseType() { return responseType; }
    public void setResponseType(String value) { this.responseType = value; }

    public String getObject() { return data; }
    public void setObject(String value) { this.data = value; }

    public Response(String json) throws JSONException {
        JSONObject jsonObject= new JSONObject(json);
        setResponseType(jsonObject.getString("responseType"));
        setObject(jsonObject.getString("data"));
    }




}
