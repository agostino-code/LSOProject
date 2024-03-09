package com.unina.guesstheword.view.menu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.unina.guesstheword.control.Controller;
import com.unina.guesstheword.control.GameChatController;
import com.unina.guesstheword.data.model.Game;
import com.unina.guesstheword.data.model.Player;
import com.unina.guesstheword.databinding.ActivityFindGameBinding;
import com.unina.guesstheword.data.model.Room;
import com.unina.guesstheword.view.game.GameActivity;
import com.unina.guesstheword.view.GeneralActivity;

import java.util.ArrayList;

public class FindGameActivity extends GeneralActivity {
    private ActivityFindGameBinding binding;
    private ProgressBar progressBar;
    private RecyclerView roomsRecyclerView;
    private RoomsAdapter adapter;
    private ArrayList<Room> rooms;
    Intent serviceIntent;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFindGameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        roomsRecyclerView = binding.roomsRecyclerView;
        progressBar = binding.loading;

        rooms = retrieveRoomsFromServer();

        if(rooms == null) {
            roomsRecyclerView.setAdapter(null);
        } else {
            adapter = new RoomsAdapter(rooms, new RoomsAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Room room) {
                    if(room == null) {
                        Toast.makeText(getApplicationContext(), "Error: room not found", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Room completeClickedRoom = retrieveCompleteRoomFromServer(room);
                    Game currentGame = null;
                    if(room.isInGame())
                        currentGame = retrieveCurrentGame(room.getPort());
                    Player mainPlayer = new Player(Controller.getInstance().getUser(), room.isInGame());
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
        ArrayList<Room> rooms = Controller.getInstance().listOfRooms();

        if(rooms == null) {
            progressBar.setVisibility(ProgressBar.GONE);
            return null;
        }
        progressBar.setVisibility(ProgressBar.GONE);
        return rooms;
    }

    private Room retrieveCompleteRoomFromServer(Room room) {
        progressBar.setVisibility(ProgressBar.VISIBLE);

        boolean success = Controller.getInstance().joinRoom(room);
            if(success) {
                startRoomSocket(GameChatController.getInstance().getRoom());
                goToGameActivity();
                Toast.makeText(getApplicationContext(), "Entrato nella stanza con successo!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Error entering the room", Toast.LENGTH_SHORT).show();
            }
        progressBar.setVisibility(ProgressBar.GONE);

        /*
        LinkedList<Player> players = new LinkedList<Player>(); //giocatori fittizi per test, da rimuovere
        players.add(new Player(null, 0, "Test", 1));
        Room test = new Room("Squadrone burberone", 5, false,
                81, 5, Language.ITALIAN, players, new Player(Controller.getInstance().getUser()));
         */
        return GameChatController.getInstance().getRoom();
    }

    private void startRoomSocket(Room room) {
//        serviceIntent = new Intent(this, SocketService.class);
//        serviceIntent.putExtra(SocketService.EXTRA_PORT, room.getPort()); // replace 3000 with your desired port
//        startService(serviceIntent);
    }

    private Game retrieveCurrentGame(int port) {
        progressBar.setVisibility(ProgressBar.VISIBLE);
        //TODO: get current game from server
        progressBar.setVisibility(ProgressBar.GONE);
        return null;
    }

    private void goToGameActivity() {
        Intent switchActivities = new Intent(this, GameActivity.class);
        startActivity(switchActivities);
    }
}
