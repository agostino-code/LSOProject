package com.unina.guesstheword.data.model;

import androidx.annotation.NonNull;

public class Game {
    private static final int pointsGap = 15;

    private final String word;
    private int pointsForGuesser;
    private final int pointsForChooser;

    /**
     * Letters of "word" (without repetitions and without spaces) mixed in a random order
     */
    private StringBuilder mixedLetters;
    private String revealedLetters;
    private String incompleteWord;
    private long initialTime;

    /**
     * Constructor called when the game has just started
     * @param wordChosen chosen by the player. (It automatically calculates the mixed letters)
     */
    public Game(@NonNull WordChosen wordChosen) {
        this.word = wordChosen.getWord();
        this.mixedLetters = new StringBuilder(wordChosen.getMixedLetters());
        pointsForGuesser = word.length();
        pointsForChooser = Math.max(1, pointsGap - word.length());
        revealedLetters = "";
        incompleteWord = calculateIncompleteWord();
        initialTime = System.currentTimeMillis();
    }

    /**
     * Constructor called when a player enters in a pre-existing game
     * @param wordChosen chosen by the player. (It automatically calculates the mixed letters)
     * @param revealedLetters letters that are already revealed
     */
    public Game(@NonNull WordChosen wordChosen, String revealedLetters) {
        this.word = wordChosen.getWord();
        this.mixedLetters = new StringBuilder(wordChosen.getMixedLetters());
        pointsForGuesser = word.length();
        pointsForChooser = Math.max(0, pointsGap - word.length());
        this.revealedLetters = revealedLetters;
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
        pointsForGuesser = 0;
        for(int i=0; i<incompleteWord.length(); i++){
            if(incompleteWord.charAt(i) == '_')
                pointsForGuesser++;
        }
        return pointsForGuesser; //TODO: testa se funziona
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
                ret.append("_").append(" ");
            else
                ret.append(letter).append(" ");
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
        return mixedLetters.length() == 0;
    }
}