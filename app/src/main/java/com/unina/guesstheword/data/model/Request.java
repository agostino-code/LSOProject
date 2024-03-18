package com.unina.guesstheword.data.model;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

public class Request {
    private final String requestType;
    private final JSONData data;

    public String getRequestType() { return requestType; }
//    public void setRequestType(String value) { this.requestType = value; }

    public JSONData getObject() { return data; }
//    public void setObject(JSONData value) { this.data = value; }

    @NotNull
    @Override
    public String toString(){
        // Create a JSON object
        JSONObject jsonObject = new JSONObject();

        // Add fields to the JSON object
        try {
            jsonObject.put("requestType", getRequestType());

            // Parse the JSON string back to a JSON object
            if(getObject() == null)
                jsonObject.put("data", new JSONObject());
            else
                jsonObject.put("data", new JSONObject(getObject().toJSON()));

            // Convert the JSON object to a string
            return jsonObject.toString();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public Request(String requestType, JSONData data){
        this.requestType=requestType;
        this.data=data;
    }




}
