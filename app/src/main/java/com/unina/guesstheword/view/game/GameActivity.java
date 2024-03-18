package com.unina.guesstheword.view.game;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.unina.guesstheword.GuessTheWordApplication;
import com.unina.guesstheword.control.GameChatController;
import com.unina.guesstheword.data.model.PlayerStatus;
import com.unina.guesstheword.view.GeneralActivity;
import com.unina.guesstheword.view.menu.MenuActivity;

/**
 * Actual game window, when the user joins a room it opens this activity
 */
public class GameActivity extends GeneralActivity {
    private GameChatController gameChatController = GameChatController.getInstance();
    private boolean running = true;

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
    private RandomWordsDialog randomWordsDialog;
    private final OnBackPressedCallback callback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            exitGame();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.unina.guesstheword.databinding.ActivityGameBinding binding = com.unina.guesstheword.databinding.ActivityGameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getOnBackPressedDispatcher().addCallback(this, callback);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        backButton = binding.gameBackButton;
        roomNameTextView = binding.gameRoomName;
        incompleteWordTextView = binding.incompleteWord;
        showPlayersButton = binding.showPlayersButton;
        editMessage = binding.gameChatEditMessage;
        sendButton = binding.gameChatSendButton;
        chatRecyclerView = binding.gameChatRecycler;
        progressBar = binding.loading;

        backButton.setOnClickListener(v -> exitGame());

        roomNameTextView.setText(gameChatController.getRoom().getName());

        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MessagesAdapter(gameChatController.getChat(), this);
        chatRecyclerView.setAdapter(adapter);

        gameChatController.getCurrentGameLiveData().observe(this, game -> {
            if (game != null) {
                if (gameChatController.getMainPlayer().getStatus() == PlayerStatus.CHOOSER)
                    incompleteWordTextView.setText(game.getWord());
                else
                    incompleteWordTextView.setText(game.getIncompleteWord());
            }
        });

        gameChatController.getChatLiveData().observe(this, chat -> {
            adapter = new MessagesAdapter(chat, this);
            chatRecyclerView.setAdapter(adapter);
            chatRecyclerView.scrollToPosition(adapter.getItemCount() - 1);
        });

        //Chooser show the random words dialog
        gameChatController.getMainPlayerStatusLiveData().observe(this, playerStatus -> {
            if (playerStatus == PlayerStatus.CHOOSER) {
                sendButton.setEnabled(true);
                editMessage.setEnabled(true);
                randomWordsDialog = new RandomWordsDialog(this);
                randomWordsDialog.show();
            }
            if (playerStatus == PlayerStatus.SPECTATOR) {
                sendButton.setEnabled(false);
                editMessage.setEnabled(false);
            }
            if (playerStatus == PlayerStatus.GUESSER) {
                sendButton.setEnabled(true);
                editMessage.setEnabled(true);
            }
        });

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
            while (running) {
                gameChatController.updateGame(randomWordsDialog);
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        GameChatController.getInstance().sendExitNotification();
        running = false;
        GameChatController.close();
        super.onDestroy();
    }

    public void sendMessage(View view) {
        if (messageToSend.isEmpty())
            return;
//        ServerMessage serverMessage = new ServerMessage(messageToSend, gameChatController.getWordToGuess(), gameChatController.getMainPlayer());
//        sendChatMessageToServer(serverMessage);
        if (gameChatController.getMainPlayerStatusLiveData().getValue() != PlayerStatus.SPECTATOR) {
            gameChatController.sendMessage(messageToSend);
            editMessage.setText("");
            adapter.notifyDataSetChanged();
        }
    }

    public void showBottomSheetDialog(View view) {
        PlayersBottomSheetDialog dialog = new PlayersBottomSheetDialog(this);
        dialog.show();
    }

    public void showErrorMessage(String errorMessage) {
        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
    }

    public void goBackToMenu() {
        Intent switchActivities = new Intent(this, MenuActivity.class);
        startActivity(switchActivities);
    }

    private void exitGame() {
        AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
        builder.setTitle("Exit from the room");
        builder.setMessage("Are you sure you want to exit from the room? You will lose all your points.");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                boolean success = gameChatController.sendExitNotification();
                if (success) {
                    running = false;
                    GameChatController.close();
                    goBackToMenu();
                } else
                    showErrorMessage("Connection error, try again later or restart the application.");
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
}


















