package com.example.guesstheword.control;

import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.guesstheword.data.model.ChatMessage;
import com.example.guesstheword.data.model.Game;
import com.example.guesstheword.data.model.Player;
import com.example.guesstheword.data.model.PlayerState;
import com.example.guesstheword.data.model.Room;
import com.example.guesstheword.data.model.ServerMessage;
import com.example.guesstheword.data.model.ServerNotification;
import com.example.guesstheword.data.model.WhatHappened;
import com.example.guesstheword.data.model.WordChosen;
import com.example.guesstheword.view.game.MessageNotificationView;
import com.example.guesstheword.view.game.MessageReceivedView;
import com.example.guesstheword.view.game.MessageSentView;

import java.util.LinkedList;
import java.util.List;

public class GameChatController {
    private static GameChatController instance = null;

    private final Player mainPlayer;
    private final Room room;
    @Nullable
    private Game currentGame;
    private final LinkedList<ChatMessage> chat;
    private long initialTime;

    /**
     * Called when the user exit the room
     */
    public static synchronized void close() {
        instance = null;
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
        this.room = room;
        currentGame = null;
        chat = new LinkedList<ChatMessage>();
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
        this.room = room;
        this.currentGame = currentGame;
        chat = new LinkedList<ChatMessage>();
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
    public List<ChatMessage> getChat() {
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
     * @return the message to send to the server
     */
    public ServerMessage sendMessage(@NonNull String message) {
        if (mainPlayer.getState() == PlayerState.CHOOSER)
            throw new IllegalStateException("Chooser can't send messages");
        if (mainPlayer.getState() == PlayerState.SPECTATOR)
            throw new IllegalStateException("Spectator can't send messages");

        ServerMessage serverMessage;
        if (room.isGaming())
            serverMessage = new ServerMessage(message, currentGame.getWord(), mainPlayer);
        else
            serverMessage = new ServerMessage(message, null, mainPlayer);
        if (serverMessage.isGuessed()) {
            String notification = mainPlayer.getUsername() + " guessed the word! (+ " +
                    currentGame.getPointsForGuesser() + " points)";
            chat.add(new MessageNotificationView(notification, Color.GREEN));
            finishGame(mainPlayer);
        } else {
            chat.add(new MessageSentView(serverMessage));
        }
        return serverMessage;
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
            Player winner = serverMessage.getSender();
            String notification = winner.getUsername() + " guessed the word! (+ " +
                    currentGame.getPointsForGuesser() + " points)";
            chat.add(new MessageNotificationView(notification, Color.GREEN));
            finishGame(winner);
        } else {
            chat.add(new MessageReceivedView(serverMessage));
        }
    }

    /**
     * This function is used to terminate a game and is called or when a player guess the word
     * (the guesser wins) or when nobody guesses the word but the time finishes (the chooser wins)
     */
    private void finishGame(Player winner) {
        switch (winner.getState()) {
            case GUESSER:
                winner.addPoints(currentGame.getPointsForGuesser());
                break;
            case CHOOSER:
                winner.addPoints(currentGame.getPointsForChooser());
                break;
        }
        room.setIsInGame(false);
        room.resetStateOfAllPlayers();
        currentGame = null;
        //TODO: do a request to the server for the next chooser.
        //if (room.getNumberOfPlayers() > 1) {
        //    startChoosingPeriod();
        //}
    }


    /**
     * This function is called right after the server picks a Chooser.
     * It sets the chooser in the room and adds a notification in the chat.
     */
    public void startChoosingPeriod(String chooser) {
        initialTime = System.currentTimeMillis();
        room.setChooser(chooser);
        String notification = "A new game is starting! wait until " + chooser + " chooses a word";
        chat.add(new MessageNotificationView(notification, Color.YELLOW));
        if (mainPlayer.equals(room.getChooser()))
            mainPlayer.setState(PlayerState.CHOOSER);
        else
            mainPlayer.setState(PlayerState.GUESSER);
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
                if (room.isGaming())
                    serverNotification.getPlayer().setState(PlayerState.SPECTATOR);
                room.addPlayer(serverNotification.getPlayer());
                //TODO: do a request to the server for the next chooser.
                //if (room.getNumberOfPlayers() == 2)
                    //startChoosingPeriod();
                break;
            case LEFT:
                room.removePlayer(serverNotification.getPlayer());
                chat.add(new MessageNotificationView(serverNotification));
                break;
        }
    }

    /*
    public void updateGame() {
        if (room.isGaming()) {
            if (currentGame.isRoundFinished(Room.TIME_PER_ROUND_IN_MILLISECONDS)) {
                char revealedLetter = currentGame.revealOneMoreLetter();
                String notification1 = "letter " + revealedLetter +
                        " revealed. Now the known word is " + currentGame.getIncompleteWord();
                chat.add(new MessageNotificationView(notification1, Color.YELLOW));
                if (currentGame.isWordFullRevealed()) {
                    String notification2 = "Nobody guessed the word. " + room.getChooser().getUsername() +
                            " wins the game! (+ " + currentGame.getPointsForChooser() + " points)";
                    chat.add(new MessageNotificationView(notification2, Color.GREEN));
                    finishGame(room.getChooser());
                }
            }
        } else {
            if (isChoosingTimeFinished(Room.CHOOSING_TIME_IN_MILLISECONDS)) {
                //TODO: send the random word to the server
            } else {

            }
        }
    }
    */

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
    }

    /**
     * This function is called when the main player exit the game.
     * You must send the ServerNotification returned by this method to all the other players
     */
    public ServerNotification generateExitNotification() {
        return new ServerNotification(mainPlayer, WhatHappened.LEFT);
    }
}