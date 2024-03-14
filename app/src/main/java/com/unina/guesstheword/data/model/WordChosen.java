package com.unina.guesstheword.data.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WordChosen implements JSONData {
    private final String word;
    private final String mixedLetters;

    /**
     * Constructor called when the word is first chosen by the chooser
     */
    public WordChosen(String word) {
        this.word = word;
        mixedLetters = removeDuplicatesAndShuffle(word);
    }

    /**
     * Constructor called when a user joins an in-gaming room
     */
    public WordChosen(String word, String mixedLetters) {
        this.word = word;
        this.mixedLetters = mixedLetters;
    }

    /**
     * json Constructor
     */
    public WordChosen(JSONObject json) throws JSONException {
        word = json.getString("word");
        mixedLetters = json.getString("mixedletters");
    }

    public String getWord() {
        return word;
    }

    public String getMixedLetters() {
        return mixedLetters;
    }

    private String removeDuplicatesAndShuffle(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        // Passo 1: Rimuovi i duplicati
        List<Character> seenCharacters = new ArrayList<>();

        for (char c : input.toCharArray()) {
            if (!seenCharacters.contains(c)) {
                seenCharacters.add(c);
            }
        }

        // Passo 2: Riordina casualmente i caratteri
        Collections.shuffle(seenCharacters);

        StringBuilder resultBuilder = new StringBuilder();
        for (char c : seenCharacters) {
            resultBuilder.append(c);
        }

        return resultBuilder.toString();
    }

    @Override
    public String toJSON() throws JSONException {
        JSONObject jsonWordChosen = new JSONObject();
        jsonWordChosen.put("word", word);
        jsonWordChosen.put("mixedletters", mixedLetters);
        return jsonWordChosen.toString();
    }
}
