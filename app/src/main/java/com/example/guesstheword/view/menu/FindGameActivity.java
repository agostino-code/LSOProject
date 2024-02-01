package com.example.guesstheword.view.menu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.guesstheword.control.Controller;
import com.example.guesstheword.control.GameChatController;
import com.example.guesstheword.data.model.Game;
import com.example.guesstheword.data.model.Language;
import com.example.guesstheword.data.model.Player;
import com.example.guesstheword.databinding.ActivityFindGameBinding;
import com.example.guesstheword.data.model.Room;
import com.example.guesstheword.service.SocketService;
import com.example.guesstheword.view.BoundServiceActivity;
import com.example.guesstheword.view.game.GameActivity;

import java.util.ArrayList;
import java.util.LinkedList;

public class FindGameActivity extends BoundServiceActivity {
    private ActivityFindGameBinding binding;
    private ProgressBar progressBar;
    private RecyclerView roomsRecyclerView;
    private RoomsAdapter adapter;
    private ArrayList<Room> rooms;

    @Override
    protected Class<?> getServiceClass() {
        return SocketService.class;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFindGameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        roomsRecyclerView = binding.roomsRecyclerView;
        progressBar = binding.findGameLoading;

        rooms = retrieveRoomsFromServer();

        if(rooms == null) {
            roomsRecyclerView.setAdapter(null);
        } else {
            adapter = new RoomsAdapter(rooms, new RoomsAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Room room) {
                    if(room == null) {
                        showFindGameFailed("Error: room not found");
                        return;
                    }

                    Room completeClickedRoom = retrieveCompleteRoomFromServer(room.getPort());
                    Game currentGame = null;
                    if(room.isGaming())
                        currentGame = retrieveCurrentGame(room.getPort());
                    Player mainPlayer = new Player(Controller.getInstance().getUser(), room.isGaming());
                    GameChatController.setInstance(mainPlayer, completeClickedRoom, currentGame);
                    goToGameActivity();
                }
            });
            roomsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            roomsRecyclerView.setAdapter(adapter);
        }
    }

    private ArrayList<Room> retrieveRoomsFromServer() {
        progressBar.setVisibility(ProgressBar.VISIBLE);
        //TODO: get rooms from server
        progressBar.setVisibility(ProgressBar.GONE);

        ArrayList<Room> test = new ArrayList<Room>(); //stanze fittizie per test, da rimuovere
        test.add(new Room("Test", 10, 10, 80,  Language.ENGLISH));
        test.add(new Room("Squadrone burberone", 1, 5, 81, Language.ITALIAN));
        return test;
    }

    private Room retrieveCompleteRoomFromServer(int port) {
        progressBar.setVisibility(ProgressBar.VISIBLE);
        //TODO: get room from server
        progressBar.setVisibility(ProgressBar.GONE);

        LinkedList<Player> players = new LinkedList<Player>(); //giocatori fittizi per test, da rimuovere
        players.add(new Player(null, 0, "Test", 1));
        Room test = new Room("Squadrone burberone", 5, false,
                81, 5, Language.ITALIAN, players, new Player(Controller.getInstance().getUser()));
        return test;
    }

    private Game retrieveCurrentGame(int port) {
        progressBar.setVisibility(ProgressBar.VISIBLE);
        //TODO: get current game from server
        progressBar.setVisibility(ProgressBar.GONE);
        return null;
    }

    private void showFindGameFailed(String errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    private void goToGameActivity() {
        Intent switchActivities = new Intent(this, GameActivity.class);
        startActivity(switchActivities);
    }
}
