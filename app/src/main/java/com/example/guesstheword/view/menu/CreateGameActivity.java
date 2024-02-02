package com.example.guesstheword.view.menu;

import static com.example.guesstheword.GuessTheWordApplication.getAppContext;

import android.content.Intent;
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

import com.example.guesstheword.R;
import com.example.guesstheword.control.Controller;
import com.example.guesstheword.control.GameChatController;
import com.example.guesstheword.data.model.Language;
import com.example.guesstheword.data.model.Player;
import com.example.guesstheword.data.model.Room;
import com.example.guesstheword.databinding.ActivityCreateGameBinding;
import com.example.guesstheword.service.ServiceManager;
import com.example.guesstheword.service.SocketService;
import com.example.guesstheword.view.BoundServiceActivity;
import com.example.guesstheword.view.game.GameActivity;

import java.util.concurrent.CompletableFuture;

public class CreateGameActivity extends BoundServiceActivity{
    private String roomName = null;

    private EditText roomNameEditText;
    private Spinner languageSpinner;
    private Spinner maxPlayersSpinner;
    private Button createGameButton;
    private ProgressBar progressBar;

    Intent serviceIntent;

    @Override
    protected Class<?> getServiceClass() {
        return SocketService.class;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityCreateGameBinding binding = ActivityCreateGameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        createGameButton = binding.createGameButton;
        languageSpinner = findViewById(R.id.spinnerLanguage);
        maxPlayersSpinner = findViewById(R.id.spinnerMaxPlayers);
        roomNameEditText = findViewById(R.id.editTextRoomName);
        progressBar = binding.createGameLoading;

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
                CompletableFuture<Boolean> successFuture = Controller.getInstance().createRoom(roomName,
                        Integer.parseInt(maxPlayersSpinner.getSelectedItem().toString()),
                        languageSpinner.getSelectedItem().toString());
                successFuture.thenAccept(success -> {
                    if (success) {
                        startRoomSocket(GameChatController.getInstance().getRoom());
                        goToGameActivity();
                        Toast.makeText(getAppContext(), "Stanza creata con successo!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Error creating the room", Toast.LENGTH_SHORT).show();
                    }
                });

                progressBar.setVisibility(ProgressBar.GONE);
            }
        });
    }

    private void sendRoomToServer(Room room) {

        //TODO: send room to server
        progressBar.setVisibility(ProgressBar.GONE);
    }

    private void startRoomSocket(Room room) {
        serviceIntent = new Intent(this, SocketService.class);
        serviceIntent.putExtra(SocketService.EXTRA_PORT, room.getPort()); // replace 3000 with your desired port
        startService(serviceIntent);
    }

    private void goToGameActivity() {
        Intent switchActivities = new Intent(this, GameActivity.class);
        startActivity(switchActivities);
    }
}
