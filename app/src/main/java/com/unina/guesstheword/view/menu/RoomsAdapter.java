package com.unina.guesstheword.view.menu;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.unina.guesstheword.R;
import com.unina.guesstheword.data.model.Room;

import java.util.List;

public class RoomsAdapter extends RecyclerView.Adapter<RoomsAdapter.RoomViewHolder> {

    private static List<Room> roomList;
    private static OnItemClickListener onItemClickListener;

    public RoomsAdapter(@NonNull List<Room> roomList, OnItemClickListener onItemClickListener) {
        this.roomList = roomList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_room, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Room room = roomList.get(position);

        // Bind room data to the ViewHolder
        holder.roomNameTextView.setText(room.getName());
        holder.languageTextView.setText(room.getLanguage().toViewString());
        String playersInfo = room.getNumberOfPlayers() + "/" + room.getMaxNumberOfPlayers();
        holder.playersInfoTextView.setText(playersInfo);

        // Set the ViewHolder not clickable if the room is full
        boolean isEnabled = room.getNumberOfPlayers() < room.getMaxNumberOfPlayers();
        holder.itemView.setEnabled(isEnabled);
        holder.roomNameTextView.setEnabled(isEnabled);
        holder.languageTextView.setEnabled(isEnabled);
        holder.playersInfoTextView.setEnabled(isEnabled);
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(Room room);
    }

    static class RoomViewHolder extends RecyclerView.ViewHolder {

        TextView roomNameTextView;
        TextView languageTextView;
        TextView playersInfoTextView;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            roomNameTextView = itemView.findViewById(R.id.roomNameTextView);
            languageTextView = itemView.findViewById(R.id.roomLanguageTextView);
            playersInfoTextView = itemView.findViewById(R.id.roomPlayersTextView);

            if(itemView.isEnabled()) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            onItemClickListener.onItemClick(roomList.get(position));
                        }
                    }
                });
            }
        }
    }
}


