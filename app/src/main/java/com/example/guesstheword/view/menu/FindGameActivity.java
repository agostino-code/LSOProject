package com.example.guesstheword.view.menu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.guesstheword.R;
import com.example.guesstheword.control.Controller;
import com.example.guesstheword.data.model.Language;
import com.example.guesstheword.data.model.Player;
import com.example.guesstheword.data.model.PlayerState;
import com.example.guesstheword.databinding.ActivityFindGameBinding;
import com.example.guesstheword.data.model.Room;
import com.example.guesstheword.view.game.GameActivity;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.LinkedList;

public class FindGameActivity extends AppCompatActivity {
    private Controller controller = Controller.getInstance();

    private ActivityFindGameBinding binding;
    private RecyclerView roomsRecyclerView;
    private RoomsAdapter adapter;
    private ArrayList<Room> rooms;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFindGameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        roomsRecyclerView = binding.roomsRecyclerView;

        rooms = retrieveRoomsFromServer();

        adapter = new RoomsAdapter(rooms, new RoomsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Room room) {
                Room completeClickedRoom = retrieveCompleteRoomFromServer(room.getPort());
                goToGameActivity(completeClickedRoom);
            }
        });
        roomsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        roomsRecyclerView.setAdapter(adapter);
    }

    private ArrayList<Room> retrieveRoomsFromServer() {
        //TODO: get rooms from server
        ArrayList<Room> test = new ArrayList<Room>();
        test.add(new Room("Test", 10, 10, 80,  Language.ENGLISH));
        test.add(new Room("Squadrone burberone", 1, 5, 81, Language.ITALIAN));
        return test;
    }

    private Room retrieveCompleteRoomFromServer(int port) {
        //TODO: get room from server
        LinkedList<Player> players = new LinkedList<Player>();
        players.add(new Player(null, 0, "Test", 1));
        Room test = new Room("Squadrone burberone", 5, false,
                81, 5, Language.ITALIAN, players, new Player(controller.getUser()));
        return test;
    }

    private void showFindGameFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    private void goToGameActivity(Room room) {
        if(room == null) {
            showFindGameFailed(R.string.create_game_failed);
            return;
        }
        Intent switchActivities = new Intent(this, GameActivity.class);
        /*try {
            switchActivities.putExtra("jsonRoom", room.toJSON().toString());
        } catch(JSONException exc) {
            showFindGameFailed(R.string.create_game_failed);
        }*/
        startActivity(switchActivities);
    }
}
