package com.unina.guesstheword.view.game;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.unina.guesstheword.R;
import com.unina.guesstheword.data.model.ChatMessage;

import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_SENT_MESSAGE = 1;
    private static final int TYPE_RECEIVED_MESSAGE = 2;
    private static final int TYPE_NOTIFICATION_MESSAGE = 3;

    private List<ChatMessage> chatMessages;
    private Context context;

    public MessagesAdapter(List<ChatMessage> chatMessages, Context context) {
        this.chatMessages = chatMessages;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        switch (viewType) {
            case TYPE_SENT_MESSAGE:
                View sentView = inflater.inflate(R.layout.item_sent_chat_message, parent, false);
                return new MessageSentHolder(sentView);
            case TYPE_RECEIVED_MESSAGE:
                View receivedView = inflater.inflate(R.layout.item_received_chat_message, parent, false);
                return new MessageReceivedHolder(receivedView);
            case TYPE_NOTIFICATION_MESSAGE:
                View notificationView = inflater.inflate(R.layout.item_notification_chat_message, parent, false);
                return new MessageNotificationHolder(notificationView);
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = chatMessages.get(position);

        switch (getItemViewType(position)) {
            case TYPE_SENT_MESSAGE:
                ((MessageSentHolder) holder).bind((MessageSentView) message);
                break;
            case TYPE_RECEIVED_MESSAGE:
                ((MessageReceivedHolder) holder).bind((MessageReceivedView) message);
                break;
            case TYPE_NOTIFICATION_MESSAGE:
                ((MessageNotificationHolder) holder).bind((MessageNotificationView) message);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = chatMessages.get(position);

        if(message instanceof MessageSentView)
            return TYPE_SENT_MESSAGE;
        else if(message instanceof MessageReceivedView)
            return TYPE_RECEIVED_MESSAGE;
        else if(message instanceof MessageNotificationView)
            return TYPE_NOTIFICATION_MESSAGE;
        else
            throw new IllegalArgumentException("Invalid message type");
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }
}
