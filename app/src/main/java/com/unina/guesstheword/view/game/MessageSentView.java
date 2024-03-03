package com.unina.guesstheword.view.game;

import androidx.annotation.NonNull;
import com.unina.guesstheword.data.model.ChatMessage;
import com.unina.guesstheword.data.model.ServerMessage;

/**
 * Message sent by the main player. The user
 */
public class MessageSentView extends ChatMessage {

    public MessageSentView(@NonNull String message) {
        super(message);
    }

    /**
     * This constructor create a message from the information to send to the server
     *
     * @param serverMessage message for the server
     */
    public MessageSentView(@NonNull ServerMessage serverMessage) {
        super(serverMessage.getMessage());
    }
}
