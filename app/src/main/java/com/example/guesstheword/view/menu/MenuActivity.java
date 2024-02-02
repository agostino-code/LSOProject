package com.example.guesstheword.view.menu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guesstheword.data.SharedPreferencesManager;
import com.example.guesstheword.data.model.User;
import com.example.guesstheword.service.SocketService;
import com.example.guesstheword.view.BoundServiceActivity;
import com.example.guesstheword.view.UserView;

public class MenuActivity extends BoundServiceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.example.guesstheword.databinding.ActivityMenuBinding binding = com.example.guesstheword.databinding.ActivityMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ImageView profileImageView = binding.menuProfileImage;
        TextView usernameView = binding.menuProfileName;

        //TODO: Check if the user is logged in
        //If the user is logged in, send the user's data to the server
        //Controller get the user's data from SharedPreferencesManager!!
        User user= SharedPreferencesManager.getInstance().getUserData();

        UserView mainUser = new UserView(user, this);
        profileImageView.setImageDrawable(mainUser.getAvatarDrawable());
        usernameView.setText(mainUser.getUsername());
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

    @Override
    protected Class<?> getServiceClass() {
        return SocketService.class;
    }
}
