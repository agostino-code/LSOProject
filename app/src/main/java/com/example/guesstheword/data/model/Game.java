package com.example.guesstheword.data.model;

import androidx.annotation.NonNull;

public class Game {
    private static final int pointsGap = 15;

    private final String word;
    private final int pointsForGuesser;
    private final int pointsForChooser;
    private StringBuilder mixedLetters;
    private String revealedLetters;
    private String incompleteWord;
    private long initialTime;

    /**
     * @param word chosen by the player
     * @param mixedLetters
     */
    public Game(@NonNull String word, @NonNull String mixedLetters) {
        this.word = word;
        this.mixedLetters = new StringBuilder(mixedLetters);
        pointsForGuesser = word.length();
        pointsForChooser = Math.max(0, pointsGap - pointsForGuesser);
        revealedLetters = "";
        incompleteWord = calculateIncompleteWord();
        initialTime = System.currentTimeMillis();
    }

    /*
     * Getters
     */
    public String getWord() {
        return word;
    }

    public int getPointsForGuesser() {
        return pointsForGuesser;
    }

    public int getPointsForChooser() {
        return pointsForChooser;
    }

    public String getIncompleteWord() {
        return incompleteWord;
    }

    /*
     * Auxiliary methods
     */
    private String calculateIncompleteWord() {
        StringBuilder ret = new StringBuilder();
        for (char letter : word.toCharArray()) {
            if (revealedLetters.indexOf(letter) == -1)
                ret.append("_");
            else
                ret.append(letter);
        }
        return ret.toString();
    }

    /*
     * Methods
     */

    /**
     * function called during the game to reveal one more letter to help the guessers
     * @return - it returns the letter just revealed
     */
    public char revealOneMoreLetter() {
        char newRevealedLetter = mixedLetters.charAt(0);
        revealedLetters = revealedLetters + newRevealedLetter;
        mixedLetters = mixedLetters.deleteCharAt(0);
        incompleteWord = calculateIncompleteWord();
        return newRevealedLetter;
    }

    /**
     * @param timePerRound in milliseconds
     * @return true if the current round is finished (then you have to reveal one more letter), otherwise false
     */
    public boolean isRoundFinished(long timePerRound) {
        long currentTime = System.currentTimeMillis();
        long timePassed = currentTime - initialTime;

        if(timePassed < timePerRound)
            return false;

        initialTime = currentTime;
        return true;
    }

    public boolean isWordGuessed(String word) {
        return this.word.equals(word);
    }

    public boolean isWordFullRevealed() {
        return word.equals(incompleteWord);
    }
}