package com.unina.guesstheword.data.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WordChosen {
    private final String word;
    private final String mixedLetters;

    public WordChosen(String word) {
        this.word = word;
        mixedLetters = removeDuplicatesAndShuffle(word);
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

    public JSONObject toJSON() throws JSONException {
        JSONObject jsonWordChosen = new JSONObject();
        jsonWordChosen.put("word", word);
        jsonWordChosen.put("mixedLetters", mixedLetters);
        return jsonWordChosen;
    }
}
