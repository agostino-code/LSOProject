package com.unina.guesstheword.view.game;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.unina.guesstheword.R;
import com.unina.guesstheword.control.GameChatController;
import com.unina.guesstheword.data.model.GameChatResponse;
import com.unina.guesstheword.data.model.GameChatResponseType;
import com.unina.guesstheword.data.model.WordChosen;
import com.unina.guesstheword.service.WordsGenerator;

import org.json.JSONException;

import java.util.concurrent.CompletableFuture;

public class RandomWordsDialog extends Dialog {
    private final GameChatController gameChatController = GameChatController.getInstance();
    private final WordsGenerator wordsGenerator;

    private final TextView randomWord1;
    private final TextView randomWord2;
    private final TextView randomWord3;
    private final TextView randomWord4;
    private final TextView randomWord5;
    private final TextView randomWord6;
    private final TextView randomWord7;
    private final TextView randomWord8;
    private final TextView randomWord9;
    private final TextView randomWord10;

    public RandomWordsDialog(@NonNull Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_random_words);

        //Errore randomWord1 isran
        randomWord1 = findViewById(R.id.randomWord1);
        randomWord2 = findViewById(R.id.randomWord2);
        randomWord3 = findViewById(R.id.randomWord3);
        randomWord4 = findViewById(R.id.randomWord4);
        randomWord5 = findViewById(R.id.randomWord5);
        randomWord6 = findViewById(R.id.randomWord6);
        randomWord7 = findViewById(R.id.randomWord7);
        randomWord8 = findViewById(R.id.randomWord8);
        randomWord9 = findViewById(R.id.randomWord9);
        randomWord10 = findViewById(R.id.randomWord10);

        wordsGenerator = WordsGenerator.getInstance(gameChatController.getRoom());
        Boolean result = wordsGenerator.extractWordsFromInternet();
        if (result) {
            randomWord1.setText(wordsGenerator.getWords().get(0));
            randomWord2.setText(wordsGenerator.getWords().get(1));
            randomWord3.setText(wordsGenerator.getWords().get(2));
            randomWord4.setText(wordsGenerator.getWords().get(3));
            randomWord5.setText(wordsGenerator.getWords().get(4));
            randomWord6.setText(wordsGenerator.getWords().get(5));
            randomWord7.setText(wordsGenerator.getWords().get(6));
            randomWord8.setText(wordsGenerator.getWords().get(7));
            randomWord9.setText(wordsGenerator.getWords().get(8));
            randomWord10.setText(wordsGenerator.getWords().get(9));
        } else {
            gameChatController.getChat().add(new MessageNotificationView("Error in extracting words from the internet", Color.RED));
        }

        randomWord1.setOnClickListener(v -> onWordClick(randomWord1));
        randomWord2.setOnClickListener(v -> onWordClick(randomWord2));
        randomWord3.setOnClickListener(v -> onWordClick(randomWord3));
        randomWord4.setOnClickListener(v -> onWordClick(randomWord4));
        randomWord5.setOnClickListener(v -> onWordClick(randomWord5));
        randomWord6.setOnClickListener(v -> onWordClick(randomWord6));
        randomWord7.setOnClickListener(v -> onWordClick(randomWord7));
        randomWord8.setOnClickListener(v -> onWordClick(randomWord8));
        randomWord9.setOnClickListener(v -> onWordClick(randomWord9));
        randomWord10.setOnClickListener(v -> onWordClick(randomWord10));

        setCancelable(false);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        getWindow().setGravity(Gravity.TOP);
    }

    private void onWordClick(TextView word) {
        WordChosen wordChosen = new WordChosen(word.getText().toString());
        try {
            gameChatController.getMulticastServer().sendMessages(new GameChatResponse(GameChatResponseType.WORD_CHOSEN, wordChosen.toJSON()).toJSON());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        gameChatController.startGame(wordChosen);
        dismiss();
    }
}
