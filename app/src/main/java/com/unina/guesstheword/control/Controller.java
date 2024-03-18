package com.unina.guesstheword.control;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.unina.guesstheword.Constants;
import com.unina.guesstheword.GuessTheWordApplication;
import com.unina.guesstheword.R;
import com.unina.guesstheword.data.model.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public class Controller {


    private static Controller instance;

    private Socket socket;
    User user;

    Controller() {
        if (!connectToServer())
            showNoConnectionAlert();
    }

    public boolean connectToServer() {
        socket = new Socket();
        AtomicBoolean isConnected = new AtomicBoolean(false);
        CompletableFuture.runAsync(() -> {
            try {
                socket.connect(new InetSocketAddress(Constants.HOSTNAME, Constants.PORT), 5000);
                isConnected.set(true);
            } catch (IOException e) {
                isConnected.set(false);
            }
        }).join();
        return isConnected.get();
    }

    public void showNoConnectionAlert() {
        Activity currentActivity = GuessTheWordApplication.getInstance().getCurrentActivity();
        currentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(currentActivity)
                        .setTitle("Connection failed")
                        .setCancelable(false)
                        .setMessage("The connection to the server has failed. Please check your internet connection and try again.")
                        .setPositiveButton("RETRY", (dialog, which) -> {
                            dialog.dismiss();
                            ProgressBar progressBar = (ProgressBar) currentActivity.findViewById(R.id.loading);
                            progressBar.setVisibility(View.VISIBLE);
                            new Thread(() -> {
                                currentActivity.runOnUiThread(() -> {
                                    if (!connectToServer()) {
                                        progressBar.setVisibility(View.GONE);
                                        showNoConnectionAlert();
                                    }
                                    progressBar.setVisibility(View.GONE);
                                    //System.exit(0)
                                });
                            }).start();
                        })
                        .show();
            }
        });
    }

    public void showConnectionLostAlert() {
        Activity currentActivity = GuessTheWordApplication.getInstance().getCurrentActivity();
        currentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(currentActivity)
                        .setTitle("Connection lost")
                        .setMessage("If the problem persists please restart the application.")
                        .setPositiveButton("OK", null)
                        .show();
            }
        });
    }

    public boolean isConnectionAlive() {
        AtomicBoolean isPongReceived = new AtomicBoolean(false);
        CompletableFuture.runAsync(() -> {
            try {
                OutputStream outputStream = socket.getOutputStream();
                outputStream.write("PING".getBytes());
                outputStream.flush();
                BufferedReader inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                char[] buffer = new char[1024];
                int charsRead = inputReader.read(buffer, 0, 1024);
                String receivedString = new String(buffer, 0, charsRead);
                if (receivedString.equals("PONG")) {
                    isPongReceived.set(true);
                }
            } catch (Exception e) {
                isPongReceived.set(false);
            }
        }).join();
        return isPongReceived.get();
    }

    public static Controller getInstance() {
        if (instance == null) {
            instance = new Controller();
        }
        return instance;
    }

    public boolean SignIn(String email, String password) {
        if (!isConnectionAlive()) {
            showConnectionLostAlert();
            return false;
        }

        User user = new User(email, password);
        Request request = new Request("SIGN_IN", user);
        Response response = sendRequestAndGetResponse(request);
        if (response.getResponseType().equals("SUCCESS")) {
            try {
                this.user = new User(response.getData());
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean SignUp(String email, String password, String username, int avatar) {
        if (!isConnectionAlive()) {
            showConnectionLostAlert();
            return false;
        }

        User user = new User(email, password, username, avatar);
        Request request = new Request("SIGN_UP", user);
        Response response = sendRequestAndGetResponse(request);
        if (response.getResponseType().equals("SUCCESS")) {
            this.user = user;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Creates the room, sends the room to the server, waits for a response getting the port
     * and creates the GameChatController
     */
    public boolean newRoom(String roomName, int maxPlayers, String language) {
        if (!isConnectionAlive()) {
            showConnectionLostAlert();
            return false;
        }

        Player host = new Player(user);
        Room room = new Room(roomName, maxPlayers, getLanguage(language), host);
        Request request = new Request("NEW_ROOM", room);
        Response response = sendRequestAndGetResponse(request);
        if (response.getResponseType().equals("SUCCESS")) {
            room.setAddress(response.getData());
            Player player = new Player(user);
            GameChatController.setInstance(player, room);
            return true;
        } else {
            return false;
        }

    }

    /**
     * Sends a request to the server to get the list of rooms and returns it
     */
    public ArrayList<Room> listOfRooms() {
        if (!isConnectionAlive()) {
            showConnectionLostAlert();
            return null;
        }

        Request request = new Request("LIST_ROOMS", null);
        Response response = sendRequestAndGetResponse(request);
        if (response.getResponseType().equals("SUCCESS")) {
            try {
                ArrayList<Room> rooms = new ArrayList<>();
                JSONArray jsonArray = new JSONArray(response.getData());
                for (int i = 0; i < jsonArray.length(); i++) {
                    rooms.add(new Room(jsonArray.getString(i))); // replace 0 and Language.ENGLISH with actual values
                } //TODO: sicuro che jsonArray.getString(i) restituisca un oggetto Room?
                return rooms;
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        } else {
            return null;
        }
    }

    /**
     * join the room with the specified port and creates the GameChatController
     */
    public boolean joinRoom(Room room) {
        if (!isConnectionAlive()) {
            showConnectionLostAlert();
            return false;
        }
        Request request = new Request("JOIN_ROOM", room);
        Response response = sendRequestAndGetResponse(request);
        if (response.getResponseType().equals("SUCCESS")) {
            try {
                Room jsonroom = new Room(response.getData());
                Player player = new Player(user, jsonroom.isInGame());

                Room newroom = new Room(jsonroom.getName(),
                        jsonroom.getMaxNumberOfPlayers(),
                        jsonroom.isInGame(),
                        jsonroom.getAddress(),
                        jsonroom.getRound(),
                        jsonroom.getLanguage(),
                        jsonroom.getPlayers(),
                        player);

                //Get json "word" and convert it to string
                if (newroom.isInGame()) {
                    //Game
                    JSONObject jsonObject = new JSONObject(response.getData());
                    String word = jsonObject.getString("word");
                    String mixedLetters = jsonObject.getString("mixedletters");
                    WordChosen wordChosen = new WordChosen(word, mixedLetters);
                    String revealedLetters = jsonObject.getString("revealedletters");
                    Game game = new Game(wordChosen, revealedLetters);

                    //GameChatController
                    GameChatController.setInstance(player, newroom, game);
                } else {
                    GameChatController.setInstance(player, newroom, null);
                }
                GameChatController.getInstance().sendJoinNotification();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            return true;
        } else {
            return false;
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Language getLanguage(String language) {
        switch (language) {
            case "Italian":
                return Language.ITALIAN;
            case "Spanish":
                return Language.SPANISH;
            case "German":
                return Language.GERMAN;
            case "English":
            default:
                return Language.ENGLISH;
        }
    }

    Response sendRequestAndGetResponse(Request request) {
        final Response[] response = new Response[1];
        CompletableFuture.runAsync(() -> {
            try {
                OutputStream outputStream = socket.getOutputStream();
                outputStream.write(request.toString().getBytes());
                outputStream.flush();
                BufferedReader inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                char[] buffer = new char[1024];
                int charsRead = inputReader.read(buffer, 0, 1024);
                String receivedString = new String(buffer, 0, charsRead);
                response[0] = new Response(receivedString);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).join();
        if (response[0].getResponseType().equals("ERROR")) {
//            GuessTheWordApplication.getInstance().getCurrentActivity().runOnUiThread(() -> {
//                Toast.makeText(GuessTheWordApplication.getInstance().getCurrentActivity(), response[0].getData(), Toast.LENGTH_SHORT).show();
//            });
            Toast.makeText(GuessTheWordApplication.getInstance().getCurrentActivity(), response[0].getData(), Toast.LENGTH_SHORT).show();
        }
        return response[0];
    }
}
