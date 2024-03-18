package com.unina.guesstheword.control;

import static java.lang.Thread.sleep;

import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.unina.guesstheword.GuessTheWordApplication;
import com.unina.guesstheword.data.model.*;
import com.unina.guesstheword.service.MulticastServer;
import com.unina.guesstheword.service.WordsGenerator;
import com.unina.guesstheword.view.game.MessageNotificationView;
import com.unina.guesstheword.view.game.MessageReceivedView;
import com.unina.guesstheword.view.game.MessageSentView;
import com.unina.guesstheword.view.game.RandomWordsDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

public class GameChatController {
    private static GameChatController instance = null;

    private final MutableLiveData<PlayerStatus> mainPlayerStatusLiveData = new MutableLiveData<>();
    private final Player mainPlayer;

    private final Room room;

    private final MutableLiveData<Game> currentGameLiveData = new MutableLiveData<>();
    @Nullable
    private Game currentGame;

    private final MutableLiveData<LinkedList<ChatMessage>> chatLiveData = new MutableLiveData<>();
    private final LinkedList<ChatMessage> chat;
    private long initialTime;

    private MulticastServer multicast;

    /**
     * Called when the user exit the room
     */
    public static synchronized void close() {
        instance = null;
        WordsGenerator.resetInstance();
    }

    public static boolean isInstanceNull() {
        return instance == null;
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
        currentGameLiveData.setValue(null);
        chat = new LinkedList<>();
        chatLiveData.setValue(chat);
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
        mainPlayerStatusLiveData.setValue(this.mainPlayer.getStatus());
        this.room = room;
        this.currentGame = currentGame;
        currentGameLiveData.setValue(this.currentGame);
        chat = new LinkedList<>();
        chatLiveData.setValue(chat);
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
    public MutableLiveData<LinkedList<ChatMessage>> getChatLiveData() {
        return chatLiveData;
    }

    public MutableLiveData<PlayerStatus> getMainPlayerStatusLiveData() {
        return mainPlayerStatusLiveData;
    }

    public MutableLiveData<Game> getCurrentGameLiveData() {
        return currentGameLiveData;
    }

    public LinkedList<ChatMessage> getChat() {
        return chat;
    }

    public Player getMainPlayer() {
        return mainPlayer;
    }

    public Room getRoom() {
        return room;
    }

    public LinkedList<Player> getPlayers() {
        if (room == null)
            return null;
        return room.getPlayers();
    }

    public int getNumberOfPlayers() {
        if (room == null)
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
        ServerMessage serverMessage;
        if (currentGame != null) {
            switch (mainPlayer.getStatus()) {
                case SPECTATOR:
                    return;
                case CHOOSER:
                    serverMessage = new ServerMessage(message, false, mainPlayer.getUsername());
                    chat.add(new MessageSentView(serverMessage));
                    break;
                case GUESSER:
                    serverMessage = new ServerMessage(message, currentGame.getWord(), mainPlayer.getUsername());

                    if (serverMessage.isGuessed()) {
                        String notification = mainPlayer.getUsername() + " guessed the word! (+ " +
                                currentGame.getPointsForGuesser() + " points). The word was " + currentGame.getWord();
                        chat.add(new MessageNotificationView(notification, Color.GREEN));
                        finishGame(mainPlayer.getUsername());
                    } else {
                        chat.add(new MessageSentView(serverMessage));
                    }
                    break;
                default:
                    return;
            }
        } else {
            serverMessage = new ServerMessage(message, null, mainPlayer.getUsername());
            chat.add(new MessageSentView(serverMessage));
        }

        GameChatResponse response;
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
        Player sender = room.getPlayer(serverMessage.getUsername());
        if (sender == null)
            return;

        if (serverMessage.isGuessed()) {
            if(currentGame != null && sender.getStatus() == PlayerStatus.GUESSER) {
                String notification = serverMessage.getUsername() + " guessed the word! (+ " +
                        currentGame.getPointsForGuesser() + " points). The word was " + currentGame.getWord();
                chat.add(new MessageNotificationView(notification, Color.GREEN));
                finishGame(serverMessage.getUsername());
            }
        } else {
            chat.add(new MessageReceivedView(serverMessage));
        }
        if (GuessTheWordApplication.getInstance().getCurrentActivity() != null)
            GuessTheWordApplication.getInstance().getCurrentActivity().runOnUiThread(() -> chatLiveData.setValue(chat));
    }

    /**
     * This function is used to terminate a game and is called or when a player guess the word
     * (the guesser wins) or when nobody guesses the word but the time finishes (the chooser wins)
     */
    private void finishGame(String winnerUsername) {
        if (currentGame == null)
            return;

        if (winnerUsername != null) {
            Player winner = room.getPlayer(winnerUsername);
            if (winner != null) {
                switch (winner.getStatus()) {
                    case GUESSER:
                        winner.addPoints(currentGame.getPointsForGuesser());
                        break;
                    case CHOOSER:
                        winner.addPoints(currentGame.getPointsForChooser());
                        break;
                }
            }
        }

        room.setIsInGame(false);
        room.resetStateOfAllPlayers();
        room.incrementRound();
        mainPlayer.setStatus(PlayerStatus.GUESSER);
        GuessTheWordApplication.getInstance().getCurrentActivity().runOnUiThread(() -> mainPlayerStatusLiveData.setValue(mainPlayer.getStatus()));
        currentGame = null;
        GuessTheWordApplication.getInstance().getCurrentActivity().runOnUiThread(() -> currentGameLiveData.setValue(null));
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
        GuessTheWordApplication.getInstance().getCurrentActivity().runOnUiThread(() -> chatLiveData.setValue(chat));
        if (mainPlayer.equals(room.getChooser()))
            mainPlayer.setStatus(PlayerStatus.CHOOSER);
        else
            mainPlayer.setStatus(PlayerStatus.GUESSER);
        GuessTheWordApplication.getInstance().getCurrentActivity().runOnUiThread(() -> mainPlayerStatusLiveData.setValue(mainPlayer.getStatus()));
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
     */
    public void manageServerNotification(ServerNotification serverNotification) {
        switch (serverNotification.getWhatHappened()) {
            case JOINED:
                chat.add(new MessageNotificationView(serverNotification));
                if (currentGame != null)
                    serverNotification.getPlayer().setStatus(PlayerStatus.SPECTATOR);
                room.addPlayer(serverNotification.getPlayer());
                break;
            case LEFT:
                if(room.hasPlayer(serverNotification.getPlayer().getUsername())) {
                    room.removePlayer(serverNotification.getPlayer());
                    chat.add(new MessageNotificationView(serverNotification));
                }
                break;
        }
        GuessTheWordApplication.getInstance().getCurrentActivity().runOnUiThread(() -> chatLiveData.setValue(chat));
    }

    public void listenServer(@Nullable GameChatResponse response) {
        if (response == null)
            return;

        GameChatResponseType type = response.getType();
        try {
            if (type == GameChatResponseType.SERVER_MESSAGE) {
                ServerMessage serverMessage = new ServerMessage(response.getData());
                if (!serverMessage.getUsername().equals(mainPlayer.getUsername()))
                    receiveMessage(serverMessage);
            } else if (type == GameChatResponseType.SERVER_NOTIFICATION) {
                ServerNotification serverNotification = new ServerNotification(response.getData());
                if (!serverNotification.getPlayer().getUsername().equals(mainPlayer.getUsername()))
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
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateGame(RandomWordsDialog randomWordsDialog) {
        if (currentGame != null) {
            if (currentGame.isRoundFinished(Room.TIME_PER_ROUND_IN_MILLISECONDS)) {
                char revealedLetter = currentGame.revealOneMoreLetter();
                GuessTheWordApplication.getInstance().getCurrentActivity().runOnUiThread(() -> currentGameLiveData.setValue(currentGame));
                String notification1 = "letter " + revealedLetter +
                        " revealed. Now the known word is\n" + currentGame.getIncompleteWord();
                chat.add(new MessageNotificationView(notification1, Color.YELLOW));
                if (currentGame.isWordFullRevealed()) {
                    if (room.thereIsAChooser()) {
                        String notification2 = "Nobody guessed the word. " + room.getChooser().getUsername() +
                                " wins the game! (+ " + currentGame.getPointsForChooser() + " points)";
                        chat.add(new MessageNotificationView(notification2, Color.GREEN));
                        finishGame(room.getChooser().getUsername());
                    } else {
                        String notification2 = "Nobody guessed the word and the chooser left the game :(. The game is finished!";
                        chat.add(new MessageNotificationView(notification2, Color.GREEN));
                        finishGame(null);
                    }
                }
                GuessTheWordApplication.getInstance().getCurrentActivity().runOnUiThread(() -> chatLiveData.setValue(chat));
            }
        } else if (mainPlayer.getStatus() == PlayerStatus.CHOOSER) {
            if (isChoosingTimeFinished(Room.CHOOSING_TIME_IN_MILLISECONDS)) {
                randomWordsDialog.dismiss();
                WordChosen randomWord = new WordChosen(WordsGenerator.getInstance(room).getRandomWord());
                try {
                    multicast.sendMessages(new GameChatResponse(GameChatResponseType.WORD_CHOSEN, randomWord.toJSON()).toJSON());
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
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
        GuessTheWordApplication.getInstance().getCurrentActivity().runOnUiThread(() -> currentGameLiveData.setValue(currentGame));
        room.setIsInGame(true);
        String notification = "Word chosen! The word to guess is\n" + currentGame.getIncompleteWord();
        chat.add(new MessageNotificationView(notification, Color.YELLOW));
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
    public boolean sendExitNotification() {
        ServerNotification serverNotification = new ServerNotification(mainPlayer, WhatHappened.LEFT);
        try {
            multicast.stopReceivingMessages();
            GameChatResponse response = new GameChatResponse(GameChatResponseType.SERVER_NOTIFICATION, serverNotification.toJSON());
            multicast.sendMessages(response.toJSON());
            sleep(1000);
            multicast.close();
            return true;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            return false;
        }
    }

    public MulticastServer getMulticastServer() {
        return multicast;
    }
}