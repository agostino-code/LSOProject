package com.unina.guesstheword.view.game;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.unina.guesstheword.control.GameChatController;
import com.unina.guesstheword.data.model.ServerMessage;
import com.unina.guesstheword.data.model.ServerNotification;
import com.unina.guesstheword.data.model.WhatHappened;
import com.unina.guesstheword.view.GeneralActivity;
import com.unina.guesstheword.view.menu.MenuActivity;

/**
 * Actual game window, when the user joins a room it opens this activity
 */
public class GameActivity extends GeneralActivity {
    private GameChatController gameChatController = GameChatController.getInstance();

    private String messageToSend;

    private ImageButton backButton;
    private TextView roomNameTextView;
    private TextView incompleteWordTextView;
    private ImageButton showPlayersButton;
    private EditText editMessage;
    private Button sendButton;
    private RecyclerView chatRecyclerView;
    private MessagesAdapter adapter;
    private ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.unina.guesstheword.databinding.ActivityGameBinding binding = com.unina.guesstheword.databinding.ActivityGameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        backButton = binding.gameBackButton;
        roomNameTextView = binding.gameRoomName;
        incompleteWordTextView = binding.incompleteWord;
        showPlayersButton = binding.showPlayersButton;
        editMessage = binding.gameChatEditMessage;
        sendButton = binding.gameChatSendButton;
        chatRecyclerView = binding.gameChatRecycler;
        progressBar = binding.loading;

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        roomNameTextView.setText(gameChatController.getRoom().getName());
        incompleteWordTextView.setText(gameChatController.getIncompleteWord());

        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MessagesAdapter(gameChatController.getChat(), this);
        chatRecyclerView.setAdapter(adapter);

        editMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                messageToSend = editMessage.getText().toString();
                sendButton.setEnabled(!messageToSend.isEmpty());
            }
        });

        new Thread(() -> {
            while(true) {
                gameChatController.updateGame();
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit from the room");
        builder.setMessage("Are you sure you want to exit from the room? You will lose all your points.");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                gameChatController.sendExitNotification();
//                sendNotificationToServer(new ServerNotification(gameChatController.getMainPlayer(), WhatHappened.LEFT));
                GameChatController.close();
                goBackToMenu();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel(); // Close the dialog
            }
        });
        builder.setCancelable(true); // Allow the user to cancel the dialog by pressing outside the dialog
        builder.show();
    }

    @Override
    public void onDestroy() {
        GameChatController.close();
        super.onDestroy();
    }

    public void sendMessage(View view) {
        if(messageToSend.isEmpty())
            return;
//        ServerMessage serverMessage = new ServerMessage(messageToSend, gameChatController.getWordToGuess(), gameChatController.getMainPlayer());
//        sendChatMessageToServer(serverMessage);
        gameChatController.sendMessage(messageToSend);
        adapter = new MessagesAdapter(gameChatController.getChat(), GameActivity.this);
        chatRecyclerView.setAdapter(adapter);
        editMessage.setText("");
    }

    public void showBottomSheetDialog(View view) {
        PlayersBottomSheetDialog dialog = new PlayersBottomSheetDialog(this);
        dialog.show();
    }

//    private void sendNotificationToServer(ServerNotification serverNotification) {
//        progressBar.setVisibility(ProgressBar.VISIBLE);
//        //TODO per agostino: send notification to server (to other players)
//        progressBar.setVisibility(ProgressBar.GONE);
//    }

//    private void sendChatMessageToServer(ServerMessage serverMessage) {
//        progressBar.setVisibility(ProgressBar.VISIBLE);
//        //TODO per agostino: send message to server (to other players)
//        progressBar.setVisibility(ProgressBar.GONE);
//    }

    public void showErrorMessage(String errorMessage) {
        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
    }

    public void goBackToMenu() {
        Intent switchActivities = new Intent(this, MenuActivity.class);
        startActivity(switchActivities);
    }
}


















