package com.example.guesstheword.ui.game;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Actual game window, when the user joins a room it opens this activity
 */
public class GameActivity extends AppCompatActivity {

    //TODO:

    private EditText editMessage;
    private Button sendButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.example.guesstheword.databinding.ActivityGameBinding binding = com.example.guesstheword.databinding.ActivityGameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        editMessage = binding.gameChatEditMessage;
        sendButton = binding.gameChatSendButton;

        sendButton.setEnabled(false);

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                messageChanged();
            }
        };
        editMessage.addTextChangedListener(afterTextChangedListener);

        sendButton.setOnClickListener(v -> {

        });
    }

    private void messageChanged() {
        String messageToSend = editMessage.getText().toString();
        sendButton.setEnabled(!messageToSend.isEmpty());
    }
}


















