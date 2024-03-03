package com.unina.guesstheword.view.game;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.unina.guesstheword.R;
import com.unina.guesstheword.view.PlayerView;

public class MessageReceivedHolder extends RecyclerView.ViewHolder {

    private ImageView playerAvatarImageView;
    private TextView playerNameTextView;
    private TextView messageTextView;

    public MessageReceivedHolder(@NonNull View itemView) {
        super(itemView);
        playerAvatarImageView = itemView.findViewById(R.id.chatPlayerAvatarImageView);
        playerNameTextView = itemView.findViewById(R.id.chatPlayerNameTextView);
        messageTextView = itemView.findViewById(R.id.ReceivedMessageTextView);
    }

    public void bind(MessageReceivedView message) {
        PlayerView player = new PlayerView(message.getSender(), itemView.getContext());
        playerAvatarImageView.setImageDrawable(player.getAvatarDrawable());
        playerNameTextView.setText(player.getUsername());
        messageTextView.setText(message.getMessage());
    }
}
