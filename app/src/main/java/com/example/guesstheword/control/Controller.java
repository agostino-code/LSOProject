package com.example.guesstheword.control;

import android.os.Message;
import android.os.Messenger;
import android.widget.Toast;
import com.example.guesstheword.data.SharedPreferencesManager;
import com.example.guesstheword.data.model.Language;
import com.example.guesstheword.data.model.Player;
import com.example.guesstheword.data.model.Response;
import com.example.guesstheword.data.model.Room;
import com.example.guesstheword.data.model.User;
import com.example.guesstheword.service.ServiceManager;
import com.example.guesstheword.service.SocketService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import static com.example.guesstheword.GuessTheWordApplication.getAppContext;

public class Controller {

    //Singleton
    private static Controller instance = null;

    private User user = null;

    private Controller() {
        // Private constructor to prevent instantiation
        /* TODO: Check if the user is logged in
         * If the user is logged in, send the user's data to the server
         * */
        if(SharedPreferencesManager.getInstance().isUserLoggedIn()){
             user = SharedPreferencesManager.getInstance().getUserData();
        }
    }

    public static synchronized Controller getInstance() {
        if (instance == null) {
            instance = new Controller();
        }
        return instance;
    }

    /**
     * Messenger for communicating with the service.
     *
     */

    public CompletableFuture<Boolean> SignIn(String email, String password) {
        Messenger mService = ServiceManager.getInstance().getService();
        boolean bound = ServiceManager.getInstance().isBound();

        if (!bound) return CompletableFuture.completedFuture(false);

        user = new User(email, password);
        Message msg = Message.obtain(null, SocketService.SIGN_IN, user);

        // Send the message to the service
        try {
            mService.send(msg);
        } catch (Exception e) {
            Toast.makeText(getAppContext(), "Errore nell'invio della richiesta al server!", Toast.LENGTH_SHORT).show();
            return CompletableFuture.completedFuture(false);
        }

        // Initialize responseFuture
        CompletableFuture<Response> responseFuture = ServiceManager.getInstance().getResponseFuture();

        return responseFuture.thenApply(response -> {
            if (response.getResponseType().equals("SUCCESS")) {
                User user = null;
                try {
                    user = new User(response.getData());
                } catch (JSONException e) {
                    Toast.makeText(getAppContext(), "Errore nella lettura dei dati ricevuti dal server!", Toast.LENGTH_SHORT).show();
                }
                if (user != null) {
                    Controller.getInstance().setUser(user);
                    SharedPreferencesManager.getInstance().saveUserData(user);
                    Toast.makeText(getAppContext(), "Login effettuato con successo!", Toast.LENGTH_SHORT).show();
                    return true;
                } else {
                    Toast.makeText(getAppContext(), "Errore nella lettura dei dati ricevuti dal server!", Toast.LENGTH_SHORT).show();
                    return false;
                }
            } else {
//                Toast.makeText(getAppContext(), response.getData(), Toast.LENGTH_SHORT).show();
                return false;
            }
        });


    }

    public CompletableFuture<Boolean> SignUp(String email, String password,String username,int avatar) {
        Messenger mService = ServiceManager.getInstance().getService();
        boolean bound = ServiceManager.getInstance().isBound();

        if (!bound) return CompletableFuture.completedFuture(false);

        user = new User(email, password,username,avatar);
        Message msg = Message.obtain(null, SocketService.SIGN_UP, user);

        // Send the message to the service
        try {
            mService.send(msg);
        } catch (Exception e) {
            Toast.makeText(getAppContext(), "Errore nell'invio della richiesta al server!", Toast.LENGTH_SHORT).show();
            return CompletableFuture.completedFuture(false);
        }

        // Initialize responseFuture
        CompletableFuture<Response> responseFuture = ServiceManager.getInstance().getResponseFuture();

        return responseFuture.thenApply(response -> {
            if (response.getResponseType().equals("SUCCESS")) {
                    Controller.getInstance().setUser(user);
                    SharedPreferencesManager.getInstance().saveUserData(user);
                    return true;
            } else {
//                Toast.makeText(getAppContext(), response.getData(), Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    public void SignOut(){
        SharedPreferencesManager.getInstance().deleteUserData();
        user = null;
    }

    /**
     * Creates the room, sends the room to the server, waits for a response getting the port
     * and creates the GameChatController
     */
    public CompletableFuture<Boolean> createRoom(String roomName, int maxPlayers, String language) {
        Messenger mService = ServiceManager.getInstance().getService();
        boolean bound = ServiceManager.getInstance().isBound();

        if (!bound) return CompletableFuture.completedFuture(false);

        Room room = new Room(roomName, maxPlayers, getLanguage(language), new Player(user));
        Message msg = Message.obtain(null, SocketService.NEW_ROOM, room);

        try {
            mService.send(msg);
        } catch (Exception e) {
            Toast.makeText(getAppContext(), "Errore nell'invio della richiesta al server!", Toast.LENGTH_SHORT).show();
            return CompletableFuture.completedFuture(false);
        }

        // Initialize responseFuture
        CompletableFuture<Response> responseFuture = ServiceManager.getInstance().getResponseFuture();

        return responseFuture.thenApply(response -> {
            if (response.getResponseType().equals("SUCCESS")) {
                room.setPort(Integer.parseInt(response.getData()));
                GameChatController.setInstance(new Player(user), room);
                return true;
            } else {
                return false;
            }
        });
    }

    /**
     * Sends a request to the server to get the list of rooms and returns it
     */
    public CompletableFuture<ArrayList<Room>> findGame() {
        Messenger mService = ServiceManager.getInstance().getService();
        boolean bound = ServiceManager.getInstance().isBound();

        if (!bound) return CompletableFuture.completedFuture(null);

        Message msg = Message.obtain(null, SocketService.LIST_ROOMS);

        try {
            mService.send(msg);
        } catch (Exception e) {
            Toast.makeText(getAppContext(), "Errore nell'invio della richiesta al server!", Toast.LENGTH_SHORT).show();
            return CompletableFuture.completedFuture(null);
        }

        // Initialize responseFuture
        CompletableFuture<Response> responseFuture = ServiceManager.getInstance().getResponseFuture();

        return responseFuture.thenApply(response -> {
            if (response.getResponseType().equals("SUCCESS")) {
                try {
                    if(response.getData().isEmpty()) {
                        Toast.makeText(getAppContext(), "Nessuna stanza trovata!", Toast.LENGTH_SHORT).show();
                        return null;
                    }
                    JSONArray jsonRooms = new JSONArray(response.getData());
                    if (jsonRooms.length() == 0) {
                        Toast.makeText(getAppContext(), "Nessuna stanza trovata!", Toast.LENGTH_SHORT).show();
                        return null;
                    }

                    //Iterate through the JSON array and create a list of rooms
                    ArrayList<Room> rooms = new ArrayList<>();
                    for (int i = 0; i < jsonRooms.length(); i++) {
                        JSONObject jsonRoom = jsonRooms.getJSONObject(i);
                        rooms.add(new Room(jsonRoom.toString()));
                    }
                    return rooms;
                } catch (JSONException e) {
                    Toast.makeText(getAppContext(), "Errore nella lettura dei dati ricevuti dal server!", Toast.LENGTH_SHORT).show();
                    return null;
                }
            } else {
                return null;
            }
        });
    }

    public CompletableFuture<Boolean> joinRoom(int port) {
        Messenger mService = ServiceManager.getInstance().getService();
        boolean bound = ServiceManager.getInstance().isBound();

        if (!bound) return CompletableFuture.completedFuture(false);

        Message msg = Message.obtain(null, SocketService.JOIN_ROOM, port);

        try {
            mService.send(msg);
        } catch (Exception e) {
            Toast.makeText(getAppContext(), "Errore nell'invio della richiesta al server!", Toast.LENGTH_SHORT).show();
            return CompletableFuture.completedFuture(false);
        }

        // Initialize responseFuture
        CompletableFuture<Response> responseFuture = ServiceManager.getInstance().getResponseFuture();

        return responseFuture.thenApply(response -> {
            if (response.getResponseType().equals("SUCCESS")) {
                try {
                    GameChatController.setInstance(new Player(user), new Room(response.getData()));
                } catch (JSONException e) {
                    Toast.makeText(getAppContext(), "Errore nella lettura dei dati ricevuti dal server!", Toast.LENGTH_SHORT).show();
                    return false;
                }
                return true;
            } else {
                return false;
            }
        });
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isUserLoggedIn(){
        return user != null;
    }

    public Language getLanguage(String language) {
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
}
