package com.unina.guesstheword.view.game;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.unina.guesstheword.R;
import com.unina.guesstheword.control.Controller;
import com.unina.guesstheword.data.model.Player;
import com.unina.guesstheword.view.PlayerView;

public class MessageSentHolder extends RecyclerView.ViewHolder {

    private ImageView playerAvatarImageView;
    private TextView playerNameTextView;
    private TextView messageTextView;

    public MessageSentHolder(@NonNull View itemView) {
        super(itemView);
        playerAvatarImageView = itemView.findViewById(R.id.chatMainPlayerAvatarImageView);
        playerNameTextView = itemView.findViewById(R.id.chatMainPlayerNameTextView);
        messageTextView = itemView.findViewById(R.id.SentMessageTextView);
    }

    public void bind(MessageSentView message) {
        PlayerView player = new PlayerView(new Player(Controller.getInstance().getUser()), itemView.getContext());
        playerAvatarImageView.setImageDrawable(player.getAvatarDrawable());
        playerNameTextView.setText(player.getUsername());
        messageTextView.setText(message.getMessage());
    }
}
