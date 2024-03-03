package com.unina.guesstheword.view.game;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.unina.guesstheword.R;

public class MessageNotificationHolder extends RecyclerView.ViewHolder {

    private TextView notificationMessageTextView;

    public MessageNotificationHolder(@NonNull View itemView) {
        super(itemView);
        notificationMessageTextView = itemView.findViewById(R.id.NotificationMessageTextView);
    }

    public void bind(MessageNotificationView message) {
        notificationMessageTextView.setText(message.getMessage());
        notificationMessageTextView.setTextColor(message.getColor());
    }
}
