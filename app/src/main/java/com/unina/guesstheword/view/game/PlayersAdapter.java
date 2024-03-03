package com.unina.guesstheword.view.game;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.unina.guesstheword.R;
import com.unina.guesstheword.data.model.Player;
import com.unina.guesstheword.view.PlayerView;

import java.util.List;

public class PlayersAdapter extends RecyclerView.Adapter<PlayersAdapter.PlayerViewHolder> {

    private static List<Player> playerList;
    private Context context;

    public PlayersAdapter(@NonNull List<Player> playerList, Context context) {
        this.playerList = playerList;
        this.context = context;
    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_player, parent, false);
        return new PlayerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayersAdapter.PlayerViewHolder holder, int position) {
        Player player = playerList.get(position);
        PlayerView playerView = new PlayerView(player, context);

        // Bind room data to the ViewHolder
        holder.playerAvatarImageView.setImageDrawable(playerView.getAvatarDrawable());
        holder.playerUserNameTextView.setText(player.getUsername());
        holder.playerPointsTextView.setText(String.valueOf(player.getScore()));
        if(player.getStatus() == null)
            holder.playerStateTextView.setText("Not playing");
        else
            holder.playerStateTextView.setText(player.getStatus().toViewString());
    }

    @Override
    public int getItemCount() {
        return playerList.size();
    }

    static class PlayerViewHolder extends RecyclerView.ViewHolder {

        ImageView playerAvatarImageView;
        TextView playerUserNameTextView;
        TextView playerStateTextView;
        TextView playerPointsTextView;

        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            playerAvatarImageView = itemView.findViewById(R.id.playerAvatarImageView);
            playerUserNameTextView = itemView.findViewById(R.id.playerUserNameTextView);
            playerStateTextView = itemView.findViewById(R.id.playerStateTextView);
            playerPointsTextView = itemView.findViewById(R.id.playerPointsTextView);
        }
    }
}

