package com.unina.guesstheword.data.model;

import org.json.JSONException;
import org.json.JSONObject;

public class GameChatResponse {
    private GameChatResponseType type;
    private String data;

    public GameChatResponseType getType() { return type; }

    public void setType(GameChatResponseType value) { this.type = value; }
    public String getData() { return data; }
    public void setData(String value) { this.data = value; }

    public GameChatResponse(GameChatResponseType type, String data){
        this.type = type;
        this.data = data;
    }
    public GameChatResponse(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);
            setType(GameChatResponseType.valueOf(jsonObject.getString("responseType")));
            setData(jsonObject.getString("data"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public String toJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("responseType", getType());
            jsonObject.put("data", new JSONObject(getData()));

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return jsonObject.toString();
    }

}
