package com.example.guesstheword.data.model;

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
        // Creare un oggetto JSON
        JSONObject jsonObject = new JSONObject();

        // Aggiungere i campi al JSON
        try {
            jsonObject.put("requestType", getRequestType());
            jsonObject.put("data", getObject().toJson());

            // Convertire l'oggetto JSON in una stringa
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
