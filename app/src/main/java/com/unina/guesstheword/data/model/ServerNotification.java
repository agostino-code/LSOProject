package com.unina.guesstheword.data.model;

import androidx.annotation.NonNull;
import org.json.JSONException;
import org.json.JSONObject;

/*
 * JoinedNotification e HasLeftNotification dovrebbero essere delle classi, sarebbe molto più elegante
 * rispetto all'enum, ma quest'oggetto è il mezzo di comunicazione con il server, che purtroppo
 * siamo costretti a scriverlo in C che non supporta il polimorfismo, quindi accontentiamoci
 */
/**
 * Notification from the server or for the server of a player joining or leaving the room
 *
 */
public class ServerNotification implements JSONData{
    private final Player player;
    private final WhatHappened whatHappened;

    /*
     * Constructor
     */
    public ServerNotification(@NonNull Player player, @NonNull WhatHappened whatHappened) {
        this.player = player;
        this.whatHappened = whatHappened;
    }

    public ServerNotification(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        player = new Player(jsonObject.getString("player"));
        whatHappened = WhatHappened.valueOf(jsonObject.getString("whatHappened"));
    }

    /*
     * Getters
     */
    public Player getPlayer() {
        return player;
    }

    public WhatHappened getWhatHappened() {
        return whatHappened;
    }

    /**
     * it calls the toString method
     *
     * @return string showing the message explaining what happened.
     */
    public String showWhatHappened() {
        return toString();
    }

    /**
     * @return string showing the message explaining what happened.
     */
    @NonNull
    @Override
    public String toString() {
        String ret = "";
        switch (whatHappened) {
            case JOINED:
                ret = player.getUsername() + " joined the room";
                break;
            case LEFT:
                ret = player.getUsername() + " left the room";
                break;
        }
        return ret;
    }

    //toJSON
    public JSONObject toJSONObject() throws JSONException {
            JSONObject jsonRoom = new JSONObject();
                jsonRoom.put("player", new JSONObject(player.toJSON()));
                jsonRoom.put("whatHappened", this.whatHappened.getValue());
            return jsonRoom;
    }

    @Override
    public String toJSON() throws JSONException {
        return this.toJSONObject().toString();
    }
}