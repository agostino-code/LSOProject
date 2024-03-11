package com.unina.guesstheword.view.game;

import androidx.annotation.NonNull;
import com.unina.guesstheword.control.GameChatController;
import com.unina.guesstheword.data.model.ChatMessage;
import com.unina.guesstheword.data.model.Player;
import com.unina.guesstheword.data.model.ServerMessage;

/**
 * Message received by the other players of the room
 */
public class MessageReceivedView extends ChatMessage {
    private final Player sender;

    /**
     * The message "message" in chat received from the player "sender"
     */
    public MessageReceivedView(@NonNull String message, @NonNull Player sender) {
        super(message);
        this.sender = sender;
    }

    /**
     * This constructor create a message from the information received by the server
     *
     * @param serverMessage message by the server
     */
    public MessageReceivedView(@NonNull ServerMessage serverMessage) {
        super(serverMessage.getMessage());
        String username = serverMessage.getUsername();
        //Get object player from Game Chat Controller from the username
        sender = GameChatController.getInstance().getRoom().getPlayer(username);
    }
    /*
     * Getters
     */
    public Player getSender() {
        return sender;
    }
}
