package com.example.guesstheword.ui.menu;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.guesstheword.databinding.ActivityCreateGameBinding;

public class CreateGameActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.example.guesstheword.databinding.ActivityCreateGameBinding binding = com.example.guesstheword.databinding.ActivityCreateGameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}
