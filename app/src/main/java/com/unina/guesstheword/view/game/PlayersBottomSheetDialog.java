package com.unina.guesstheword.view.game;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.unina.guesstheword.R;
import com.unina.guesstheword.control.GameChatController;
import com.unina.guesstheword.data.model.Player;

import java.util.LinkedList;

public class PlayersBottomSheetDialog extends Dialog {
    private final GameChatController gameChatController = GameChatController.getInstance();

    private RecyclerView playersRecyclerView;

    public PlayersBottomSheetDialog(@NonNull Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_players_bottom_sheet);

        LinkedList<Player> players = gameChatController.getPlayers();
        if(players != null) {
            playersRecyclerView = findViewById(R.id.playersRecyclerView);
            playersRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            playersRecyclerView.setAdapter(new PlayersAdapter(gameChatController.getRoom().getPlayers(), context));
        }

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        getWindow().setGravity(Gravity.BOTTOM);
    }
}
