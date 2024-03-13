package com.unina.guesstheword.data.model;

import android.graphics.Color;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.unina.guesstheword.GuessTheWordApplication;
import com.unina.guesstheword.view.game.MessageNotificationView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

/**
 * This class extract from the API of "<a href="https://random-word-api.herokuapp.com/word">...</a>" 10 random
 * words in the language given.
 */
public class WordsGenerator {
    private static WordsGenerator instance = null;

    private ArrayList<String> words;
    private String url = "https://random-word-api.herokuapp.com/word";
    private final LinkedList<ChatMessage> chat;
    private final Random randomNumberGenerator;

    public static WordsGenerator getInstance(Room room, LinkedList<ChatMessage> chat) {
        if(instance == null)
            instance = new WordsGenerator(room, chat);
        return instance;
    }

    public static void resetInstance() {
        instance = null;
    }

    private WordsGenerator(Room room, LinkedList<ChatMessage> chat) {
        String language = room.getLanguage().toString();
        int numberOfWords = 10;
        url = url + "?number=" + numberOfWords + "&lang=" + language;
        this.chat = chat;
        randomNumberGenerator = new Random();
    }

    public CompletableFuture<Boolean> extractWordsFromInternet() {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("words");
                    words = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        words.add(jsonArray.getString(i));
                    }
                    future.complete(true);
                } catch (JSONException e) {
                    future.complete(false);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                future.complete(false);
            }
        });
        return future;
    }

    public String getUrl() {
        return url;
    }

    public ArrayList<String> getWords() {
        return words;
    }

    public LinkedList<ChatMessage> getChat() {
        return chat;
    }

    public String getRandomWord() {
        int randomIndex = randomNumberGenerator.nextInt(words.size());
        return words.get(randomIndex);
    }
}
