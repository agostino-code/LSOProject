package com.unina.guesstheword.control;

import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.unina.guesstheword.GuessTheWordApplication;
import com.unina.guesstheword.data.model.*;
import com.unina.guesstheword.service.MulticastServer;
import com.unina.guesstheword.view.game.MessageNotificationView;
import com.unina.guesstheword.view.game.MessageReceivedView;
import com.unina.guesstheword.view.game.MessageSentView;
import com.unina.guesstheword.view.game.RandomWordsDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

public class GameChatController {
    private static GameChatController instance = null;

    private final MutableLiveData<PlayerStatus> mainPlayerStatusLiveData = new MutableLiveData<>();
    private final Player mainPlayer;
    private final Room room;
    @Nullable
    private Game currentGame;
    private final MutableLiveData<LinkedList<ChatMessage>> chatLiveData = new MutableLiveData<>(new LinkedList<>());
    private final LinkedList<ChatMessage> chat = chatLiveData.getValue();
    private long initialTime;

    private MulticastServer multicast;

    public MutableLiveData<LinkedList<ChatMessage>> getChatLiveData() {
        return chatLiveData;
    }

    public MutableLiveData<PlayerStatus> getMainPlayerStatusLiveData() {
        return mainPlayerStatusLiveData;
    }

    /**
     * Called when the user exit the room
     */
    public static synchronized void close() {
        instance = null;
        WordsGenerator.resetInstance();
    }

    /**
     * @return the instance of the GameChatController
     */
    public static synchronized GameChatController getInstance() {
        if (instance == null) {
            throw new IllegalStateException("GameChatController not initialized");
        }
        return instance;
    }


    /**
     * Constructor called when the room is first created
     *
     * @param host Player which created the room
     * @param room the room just created
     */
    private GameChatController(@NonNull Player host, @NonNull Room room) {
        this.mainPlayer = host;
        mainPlayerStatusLiveData.setValue(mainPlayer.getStatus());
        this.room = room;
        currentGame = null;
        connectToMulticast();
    }

    /**
     * It calls the Constructor called when the room is first created
     *
     * @param host Player which created the room
     * @param room the room just created
     */
    public static synchronized void setInstance(@NonNull Player host, @NonNull Room room) {
        if (instance == null) {
            instance = new GameChatController(host, room);
        }
    }

    /**
     * Constructor called when a player enter in a pre-existing room
     *
     * @param mainPlayer  Player entered in the room
     * @param room        the pre-existing room
     * @param currentGame current game played, if exists (can be null)
     */
    private GameChatController(@NonNull Player mainPlayer, @NonNull Room room, @Nullable Game currentGame) {
        this.mainPlayer = mainPlayer;
        mainPlayerStatusLiveData.setValue(mainPlayer.getStatus());
        this.room = room;
        this.currentGame = currentGame;
        connectToMulticast();
    }

    public void connectToMulticast() {
        multicast = new MulticastServer(room.getAddress());
    }

    /**
     * It calls the Constructor called when a player enter in a pre-existing room
     *
     * @param mainPlayer  Player entered in the room
     * @param room        the pre-existing room
     * @param currentGame current game played, if exists (can be null)
     */
    public static synchronized void setInstance(@NonNull Player mainPlayer, @NonNull Room room, @Nullable Game currentGame) {
        if (instance == null) {
            instance = new GameChatController(mainPlayer, room, currentGame);
        }
    }

    /*
     * Getters
     */
    public LinkedList<ChatMessage> getChat() {
        return chat;
    }

    public String getWordToGuess() {
        if(currentGame == null)
            return null;
        return currentGame.getWord();
    }

    public Player getMainPlayer() {
        return mainPlayer;
    }

    public Room getRoom() {
        return room;
    }

    public String getIncompleteWord() {
        if(currentGame == null)
            return null;
        return currentGame.getIncompleteWord();
    }

    public LinkedList<Player> getPlayers() {
        if(room == null)
            return null;
        return room.getPlayers();
    }

    public int getNumberOfPlayers() {
        if(room == null)
            return 0;
        return room.getNumberOfPlayers();
    }

    /**
     * This function must be called every time the main player (the user) send a message
     * in chat.
     * It does mainly 3 things:
     * 1. check if the main player in the message didn't guess the word.
     * 2. if he didn't, it adds the message to the chat UI.
     * 3. otherwise write in chat that the main player won and finishes the round.
     *
     * @param message sent by the main player, the user
     */
    public void sendMessage(@NonNull String message) {
        if (mainPlayer.getStatus() == PlayerStatus.CHOOSER)
            throw new IllegalStateException("Chooser can't send messages");
        if (mainPlayer.getStatus() == PlayerStatus.SPECTATOR)
            throw new IllegalStateException("Spectator can't send messages");

        ServerMessage serverMessage;
        if (room.isInGame()) {
            serverMessage = new ServerMessage(message, currentGame != null ? currentGame.getWord() : null, mainPlayer.getUsername());
        } else {
            serverMessage = new ServerMessage(message, null, mainPlayer.getUsername());

            if (serverMessage.isGuessed()) {
                String notification = mainPlayer.getUsername() + " guessed the word! (+ " +
                        currentGame.getPointsForGuesser() + " points)";
                chat.add(new MessageNotificationView(notification, Color.GREEN));
                finishGame(mainPlayer.getUsername());
            } else {
                chat.add(new MessageSentView(serverMessage));
            }
        }
        GameChatResponse response = null;
        try {
            response = new GameChatResponse(GameChatResponseType.SERVER_MESSAGE, serverMessage.toJSON());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        GuessTheWordApplication.getInstance().getCurrentActivity().runOnUiThread(() -> chatLiveData.setValue(chat));
        multicast.sendMessages(response.toJSON());
    }

    /**
     * This function must be called every time is received a message from the server by other players.
     * It does mainly 3 things:
     * 1. check if the player in the message didn't guess the word.
     * 2. if he didn't, it adds the message to the chat UI.
     * 3. otherwise write in chat that this player won and finishes the round.
     *
     * @param serverMessage sent by the server
     */
    public void receiveMessage(@NonNull ServerMessage serverMessage) {
        if (serverMessage.isGuessed()) {
            String notification = serverMessage.getUsername() + " guessed the word! (+ " +
                    currentGame.getPointsForGuesser() + " points)";
            chat.add(new MessageNotificationView(notification, Color.GREEN));
            finishGame(serverMessage.getUsername());
        } else {
            chat.add(new MessageReceivedView(serverMessage));
        }
        GuessTheWordApplication.getInstance().getCurrentActivity().runOnUiThread(() -> chatLiveData.setValue(chat));
    }

    /**
     * This function is used to terminate a game and is called or when a player guess the word
     * (the guesser wins) or when nobody guesses the word but the time finishes (the chooser wins)
     */
    private void finishGame(String winnerUsername) {
        Player winner = room.getPlayer(winnerUsername);
        switch (winner.getStatus()) {
            case GUESSER:
                winner.addPoints(currentGame.getPointsForGuesser());
                break;
            case CHOOSER:
                winner.addPoints(currentGame.getPointsForChooser());
                break;
        }

        if (mainPlayer.getUsername().equals(winnerUsername))
            switch (mainPlayer.getStatus()) {
                case GUESSER:
                    mainPlayer.addPoints(currentGame.getPointsForGuesser());
                    break;
                case CHOOSER:
                    mainPlayer.addPoints(currentGame.getPointsForChooser());
                    break;
            }

        room.setIsInGame(false);
        room.resetStateOfAllPlayers();
        room.incrementRound();
        currentGame = null;
        mainPlayer.setStatus(null);
    }


    /**
     * This function is called right after the server picks a Chooser.
     * It sets the chooser in the room and adds a notification in the chat.
     */
    public void startChoosingPeriod(String chooserUsername) {
        initialTime = System.currentTimeMillis();
        room.setChooser(chooserUsername);
        String notification = "A new game is starting! wait until " + chooserUsername + " chooses a word";
        chat.add(new MessageNotificationView(notification, Color.YELLOW));
        if (mainPlayer.equals(room.getChooser()))
            mainPlayer.setStatus(PlayerStatus.CHOOSER);
        else
            mainPlayer.setStatus(PlayerStatus.GUESSER);
    }

    /**
     * Function called when a there is a notification from the server about a player which joined
     * or left the room.
     * it adds or remove the player from the room and adds in the chat a message of notification
     * of that.
     * Furthermore, if is there only 1 player the game can't start, the count down for the
     * start of the game start when there are at least 2 players. So it also checks if the
     * players become 2 and if they are then it also start the timer. The game will start in
     * "WAIT_TIME" seconds (unless the players are less than 2 again)
     *
     * @param serverNotification
     */
    public void manageServerNotification(ServerNotification serverNotification) {
        switch (serverNotification.getWhatHappened()) {
            case JOINED:
                chat.add(new MessageNotificationView(serverNotification));
                if (room.isInGame())
                    serverNotification.getPlayer().setStatus(PlayerStatus.SPECTATOR);
                room.addPlayer(serverNotification.getPlayer());
                break;
            case LEFT:
                room.removePlayer(serverNotification.getPlayer());
                chat.add(new MessageNotificationView(serverNotification));
                break;
        }
        GuessTheWordApplication.getInstance().getCurrentActivity().runOnUiThread(() -> chatLiveData.setValue(chat));
    }

    public void listenServer(GameChatResponse response) {
        GameChatResponseType type = response.getType();
        try {
            if (type == GameChatResponseType.SERVER_MESSAGE) {
                ServerMessage serverMessage = new ServerMessage(response.getData());
                if(!serverMessage.getUsername().equals(mainPlayer.getUsername()))
                    receiveMessage(serverMessage);
            } else if (type == GameChatResponseType.SERVER_NOTIFICATION) {
                ServerNotification serverNotification = new ServerNotification(response.getData());
                if(!serverNotification.getPlayer().getUsername().equals(mainPlayer.getUsername()))
                    manageServerNotification(serverNotification);
            } else if (type == GameChatResponseType.NEW_CHOOSER) {
                startChoosingPeriod(response.getData());
            } else if (type == GameChatResponseType.WORD_CHOSEN) {
                if (mainPlayer.getStatus() == PlayerStatus.GUESSER) {
                    JSONObject chosenWordJson = new JSONObject(response.getData());
                    WordChosen wordChosen = new WordChosen(chosenWordJson);
                    startGame(wordChosen);
                }
            }
        }catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateGame(RandomWordsDialog randomWordsDialog) {
        if (room.isInGame()) {
            if (currentGame.isRoundFinished(Room.TIME_PER_ROUND_IN_MILLISECONDS)) {
                char revealedLetter = currentGame.revealOneMoreLetter();
                String notification1 = "letter " + revealedLetter +
                        " revealed. Now the known word is " + currentGame.getIncompleteWord();
                chat.add(new MessageNotificationView(notification1, Color.YELLOW));
                GuessTheWordApplication.getInstance().getCurrentActivity().runOnUiThread(() -> chatLiveData.setValue(chat));
                if (currentGame.isWordFullRevealed()) {
                    String notification2 = "Nobody guessed the word. " + room.getChooser().getUsername() +
                            " wins the game! (+ " + currentGame.getPointsForChooser() + " points)";
                    chat.add(new MessageNotificationView(notification2, Color.GREEN));
                    finishGame(room.getChooser().getUsername());
                }
            }
        } else if(mainPlayer.getStatus() == PlayerStatus.CHOOSER) {
            if (isChoosingTimeFinished(Room.CHOOSING_TIME_IN_MILLISECONDS)) {
                randomWordsDialog.dismiss();
                WordChosen randomWord = new WordChosen(WordsGenerator.getInstance(room, chat).getRandomWord());
                startGame(randomWord);
            }
        }

    }

    /**
     * @param choosingTime in milliseconds
     * @return true if the time for the chooser is finished (then you have to choose a random word for him), otherwise false
     */
    public boolean isChoosingTimeFinished(long choosingTime) {
        long currentTime = System.currentTimeMillis();
        long timePassed = currentTime - initialTime;
        return timePassed >= choosingTime;
    }

    public void startGame(WordChosen wordChosen) {
        currentGame = new Game(wordChosen);
        room.setIsInGame(true);
        String notification = "Word chosen! The word to guess is " + currentGame.getIncompleteWord();
        chat.add(new MessageNotificationView(notification, Color.YELLOW));
        try {
            multicast.sendMessages(new GameChatResponse(GameChatResponseType.WORD_CHOSEN, wordChosen.toJSON()).toJSON());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        GuessTheWordApplication.getInstance().getCurrentActivity().runOnUiThread(() -> chatLiveData.setValue(chat));
    }

    public void sendJoinNotification() {

        ServerNotification serverNotification = new ServerNotification(mainPlayer, WhatHappened.JOINED);
        try {
            GameChatResponse response = new GameChatResponse(GameChatResponseType.SERVER_NOTIFICATION, serverNotification.toJSON());
            multicast.sendMessages(response.toJSON());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * This function is called when the main player exit the game.
     * You must send the ServerNotification returned by this method to all the other players
     */
    public void sendExitNotification() {
        ServerNotification serverNotification = new ServerNotification(mainPlayer, WhatHappened.LEFT);
        try {
            multicast.sendMessages(serverNotification.toJSON());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        multicast.close();
        //T
    }

    public MulticastServer getMulticastServer() {
        return multicast;
    }
}