package com.example.guesstheword.view.menu;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class FindGameActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.example.guesstheword.databinding.ActivityFindGameBinding binding = com.example.guesstheword.databinding.ActivityFindGameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}
