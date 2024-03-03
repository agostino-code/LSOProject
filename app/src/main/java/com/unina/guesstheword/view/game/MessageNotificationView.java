package com.unina.guesstheword.view.game;

import android.graphics.Color;
import androidx.annotation.NonNull;
import com.unina.guesstheword.data.model.ChatMessage;
import com.unina.guesstheword.data.model.ServerNotification;

/**
 *  Message of notification that appears for every player in the room.
 *  For example when a player joins or leaves the room, or for every other generic notification
 */
public class MessageNotificationView extends ChatMessage {
    private final int color;

    public MessageNotificationView(@NonNull String message, int color) {
        super(message);
        this.color = color;
    }

    public MessageNotificationView(@NonNull ServerNotification serverNotification) {
        super(serverNotification.showWhatHappened());
        switch (serverNotification.getWhatHappened()) {
            case JOINED:
                color = Color.GREEN;
                break;
            case LEFT:
                color = Color.RED;
                break;
            default:
                color = Color.WHITE;
        }
    }

    public int getColor() {
        return color;
    }
}
