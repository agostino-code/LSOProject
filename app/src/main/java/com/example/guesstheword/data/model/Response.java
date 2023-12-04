package com.example.guesstheword.data.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Response {
    private String responseType;
    private String data;

    public String getResponseType() { return responseType; }
    public void setResponseType(String value) { this.responseType = value; }

    public String getMessage() { return data; }
    public void setMessage(String value) { this.data = value; }

    public Response(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);
            setResponseType(jsonObject.getString("responseType"));
            setMessage(jsonObject.getString("data"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }




}
