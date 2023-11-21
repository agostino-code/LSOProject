package com.example.guesstheword.server;

import com.example.guesstheword.data.model.*;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class prepares the JSON format for every request the client need to send to the server
 */
public class JSONGeneratorOf {

    /**
     * Quando un utente vuole fare il login
     */
    public static String signInRequest(String email, String password) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("RequestType", "SIGN_IN_REQUEST");
        jsonObject.put("email", email);
        jsonObject.put("password", password);
        return jsonObject.toString();
    }

    /**
     * Quando un utente vuole registrarsi
     */
    public static String signUpRequest(User newUser) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("RequestType", "SIGN_UP_REQUEST");
        jsonObject.put("newUser", newUser.toJSON());
        return jsonObject.toString();
    }

    /**
     * Quando un utente chiede al server di vedere tutte le stanze di gioco attualmente aperte
     */
    public static String findGamesRequest() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("RequestType", "FIND_GAMES_REQUEST");
        // Non ci sono parametri da includere in questo caso
        return jsonObject.toString();
    }

    /**
     * Quando, dopo aver ottenuto la lista di tutte le stanze di gioco aperte,
     * un utente clicca su una di queste per entrarci e ottenere tutti i dati
     * relativi a quella stanza
     * (Il server dovrà poi notificare tutti gli altri giocatori che questo qui è joinato)
     */
    public static String enterInThisGameRequest(Room room) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("RequestType", "ENTER_IN_THIS_GAME_REQUEST");
        jsonObject.put("port", room.getPort());
        return jsonObject.toString();
    }

    /**
     * Quando un utente vuole creare una nuova stanza di gioco,
     * e manda tutti i dati scelti da lui relativi a quella stanza al server,
     * così che il server la possa creare. (Una volta creata,
     * il server dovrà generare la porta (primary key) di quella stanza e passarla subito al client)
     */
    public static String createGameRequest(Room room, User host) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("RequestType", "CREATE_GAME_REQUEST");
        jsonObject.put("room", room.toJSON());
        jsonObject.put("hostUsername", host.getUsername());
        return jsonObject.toString();
    }

    /**
     * Quando un utente invia un messaggio in chat nella stanza di gioco.
     * (Dentro sendMessageRequest viene implicitamente gestita anche la richiesta al server
     * di quando un giocatore indovina la parola, tramite isGuessed, booleano calcolato dal
     * client ad ogni messaggio inviato)
     */
    public static String sendMessageRequest(ServerMessage serverMessage, Room room) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("RequestType", "SEND_MESSAGE_REQUEST");
        jsonObject.put("message", serverMessage.getMessage());
        jsonObject.put("isGuessed", serverMessage.isGuessed());
        jsonObject.put("senderUsername", serverMessage.getSender().getUsername());
        jsonObject.put("port", room.getPort());
        return jsonObject.toString();
    }

    /**
     * Quando il chooser sceglie la parola con cui giocare,
     * oppure se il chooser non sceglie in tempo, ne viene scelta una a caso
     * e viene comunque mandata al server.
     */
    public static String wordChosenRequest(WordChosen wordChosen, Room room) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("RequestType", "WORD_CHOSEN_REQUEST");
        jsonObject.put("wordChosen", wordChosen.toJSON());
        jsonObject.put("port", room.getPort());
        return jsonObject.toString();
    }

    /**
     * Quando un giocatore vuole uscire dalla stanza di gioco attuale.
     * (Il server dovrà poi notificare tutti gli altri giocatori che questo qui ha leftato)
     */
    public static String exitFromThisGameRequest(Player player, Room room) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("RequestType", "EXIT_FROM_THIS_GAME_REQUEST");
        jsonObject.put("username", player.getUsername());
        jsonObject.put("port", room.getPort());
        return jsonObject.toString();
    }
}

