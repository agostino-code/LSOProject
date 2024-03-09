package com.unina.guesstheword.view.menu;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.unina.guesstheword.GuessTheWordApplication;
import com.unina.guesstheword.R;
import com.unina.guesstheword.control.Controller;
import com.unina.guesstheword.data.model.Room;
import com.unina.guesstheword.databinding.ActivityCreateGameBinding;
import com.unina.guesstheword.view.game.GameActivity;
import com.unina.guesstheword.view.GeneralActivity;

public class CreateGameActivity extends GeneralActivity {
    private String roomName = null;

    private EditText roomNameEditText;
    private Spinner languageSpinner;
    private Spinner maxPlayersSpinner;
    private Button createGameButton;
    private ProgressBar progressBar;

    Intent serviceIntent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCreateGameBinding binding = ActivityCreateGameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        createGameButton = binding.createGameButton;
        languageSpinner = findViewById(R.id.spinnerLanguage);
        maxPlayersSpinner = findViewById(R.id.spinnerMaxPlayers);
        roomNameEditText = findViewById(R.id.editTextRoomName);
        progressBar = binding.loading;

        ArrayAdapter<CharSequence> languagesAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.languages,
                android.R.layout.simple_spinner_item
        );
        languagesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(languagesAdapter);

        ArrayAdapter<CharSequence> maxPlayersAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.max_players,
                android.R.layout.simple_spinner_item
        );
        maxPlayersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        maxPlayersSpinner.setAdapter(maxPlayersAdapter);

        roomNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2){

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2){

            }

            @Override
            public void afterTextChanged(Editable editable){
                if(roomNameEditText.getText().toString().length() > 0 && roomNameEditText.getText().toString().length() < 20){
                    roomName = roomNameEditText.getText().toString();
                    createGameButton.setEnabled(true);
                }
                else{
                    roomName = null;
                    createGameButton.setEnabled(false);
                    roomNameEditText.setError("Room name must be between 1 and 20 characters");
                }
            }
        });

        createGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(ProgressBar.VISIBLE);

                //send room to server and creates the GameChatController
                boolean success = Controller.getInstance().newRoom(roomName,
                        Integer.parseInt(maxPlayersSpinner.getSelectedItem().toString()),
                        languageSpinner.getSelectedItem().toString());
                    if (success) {
//                        startRoomSocket(GameChatController.getInstance().getRoom());
                        goToGameActivity();
                        Toast.makeText(getApplicationContext(), "Room created successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Error creating the room", Toast.LENGTH_SHORT).show();
                    }

                progressBar.setVisibility(ProgressBar.GONE);
            }
        });
    }

//    private void startRoomSocket(Room room) {
//
//
//    }

    private void goToGameActivity() {
        Intent switchActivities = new Intent(this, GameActivity.class);
        startActivity(switchActivities);
    }
}
