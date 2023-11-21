package com.example.guesstheword.ui.menu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.guesstheword.data.model.User;
import com.example.guesstheword.ui.UserView;
import org.json.JSONException;

public class MenuActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.example.guesstheword.databinding.ActivityMenuBinding binding = com.example.guesstheword.databinding.ActivityMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ImageView profileImageView = binding.menuProfileImage;
        TextView usernameView = binding.menuProfileName;

        Intent messageIntent = getIntent();
        try {
            User user = new User(messageIntent.getStringExtra("jsonUser"));
            UserView mainUser = new UserView(user, this);
            profileImageView.setImageDrawable(mainUser.getAvatarDrawable());
            usernameView.setText(mainUser.getUsername());
        } catch (JSONException e) {
            showErrorMessage("Error while parsing the user data");
        }
    }

    public void goToFindGameActivity(View v) {
        Intent switchActivities = new Intent(this, FindGameActivity.class);
        startActivity(switchActivities);
    }

    public void goToCreateGameActivity(View v) {
        Intent switchActivities = new Intent(this, CreateGameActivity.class);
        startActivity(switchActivities);
    }

    public void showErrorMessage(String errorMessage) {
        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
    }
}
