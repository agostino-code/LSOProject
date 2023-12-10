package com.example.guesstheword.view.menu;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import com.example.guesstheword.R;
import com.example.guesstheword.control.Controller;
import com.example.guesstheword.data.model.Language;
import com.example.guesstheword.data.model.Player;
import com.example.guesstheword.data.model.Room;
import com.example.guesstheword.databinding.ActivityCreateGameBinding;
import com.example.guesstheword.view.game.GameActivity;

public class CreateGameActivity extends AppCompatActivity {

    Controller controller = Controller.getInstance();

    String roomName = null;

    private EditText roomNameEditText;
    private Spinner languageSpinner;
    private Spinner maxPlayersSpinner;
    private Button createGameButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityCreateGameBinding binding = ActivityCreateGameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        createGameButton = binding.createGameButton;
        languageSpinner = findViewById(R.id.spinnerLanguage);
        maxPlayersSpinner = findViewById(R.id.spinnerMaxPlayers);
        roomNameEditText = findViewById(R.id.editTextRoomName);

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
                Player host = new Player(controller.getUser());
                Room room = new Room(roomName, Integer.parseInt(maxPlayersSpinner.getSelectedItem().toString()),
                        getLanguage(languageSpinner.getSelectedItem().toString()), host);
                sendRoomToServer(room);
                goToGameActivity(room);
            }
        });
    }

    private Language getLanguage(String language) {
        switch (language) {
            case "Italian":
                return Language.ITALIAN;
            case "English":
                return Language.ENGLISH;
            case "Spanish":
                return Language.SPANISH;
            case "German":
                return Language.GERMAN;
            default:
                return Language.ENGLISH;
        }
    }

    private void sendRoomToServer(Room room) {
        //TODO: send room to server
    }

    private void goToGameActivity(Room room) {
        Intent switchActivities = new Intent(this, GameActivity.class);
        /*try {
            switchActivities.putExtra("jsonRoom", room.toJSON().toString());
        } catch(JSONException exc) {
            showCreateGameFailed(R.string.create_game_failed);
        }*/
        startActivity(switchActivities);
    }

    private void showCreateGameFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}
