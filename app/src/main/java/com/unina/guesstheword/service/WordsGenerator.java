package com.unina.guesstheword.service;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.unina.guesstheword.data.model.ChatMessage;
import com.unina.guesstheword.data.model.Room;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * This class extract from the API of "<a href="https://random-word-api.herokuapp.com/word">...</a>" 10 random
 * words in the language given.
 */
public class WordsGenerator {
    private static WordsGenerator instance = null;

    private Integer numberOfWords = 10;
    private String language;

    private ArrayList<String> words;
    private String baseUrl = "https://random-word-api.herokuapp.com/";
    private Retrofit retrofit;
    private final Random randomNumberGenerator;

    public static WordsGenerator getInstance(Room room) {
        if(instance == null)
            instance = new WordsGenerator(room);
        return instance;
    }

    public static void resetInstance() {
        instance = null;
    }

    private WordsGenerator(Room room) {
        words = new ArrayList<>(10);
        language = room.getLanguage().getLanguageCode();
        randomNumberGenerator = new Random();

        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public Boolean extractWordsFromInternet() {
        AtomicBoolean result = new AtomicBoolean(false);
        CompletableFuture.runAsync(() -> {
            WordApiService wordApiService = retrofit.create(WordApiService.class);
            try {
                Response<List<String>> response = wordApiService.getWords(numberOfWords, language).execute();
                if (response.isSuccessful()) {
                    words = new ArrayList<>(response.body());
                    result.set(true);
                } else {
                    result.set(false);
                }
            } catch (IOException e) {
                result.set(false);
            }
        }).join();
        return result.get();
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public ArrayList<String> getWords() {
        return words;
    }

    public String getRandomWord() {
        if(words == null || words.isEmpty())
            return "ciao";

        int randomIndex = randomNumberGenerator.nextInt(words.size());
        return words.get(randomIndex);
    }
}
