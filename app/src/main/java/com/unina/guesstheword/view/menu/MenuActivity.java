package com.unina.guesstheword.view.menu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;

import com.unina.guesstheword.control.Controller;
import com.unina.guesstheword.data.model.User;
import com.unina.guesstheword.view.UserView;
import com.unina.guesstheword.view.GeneralActivity;
import com.unina.guesstheword.view.login.LoginActivity;

public class MenuActivity extends GeneralActivity {
    private final OnBackPressedCallback callback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {}
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getOnBackPressedDispatcher().addCallback(this, callback);

        com.unina.guesstheword.databinding.ActivityMenuBinding binding = com.unina.guesstheword.databinding.ActivityMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ImageView profileImageView = binding.menuProfileImage;
        TextView usernameView = binding.menuProfileName;

        //TODO: Check if the user is logged in
        //If the user is logged in, send the user's data to the server
        //Controller get the user's data from SharedPreferencesManager!!
//        User user= SharedPreferencesManager.getInstance().getUserData();
        User user = Controller.getInstance().getUser();
        UserView mainUser = new UserView(user, this);
        profileImageView.setImageDrawable(mainUser.getAvatarDrawable());
        usernameView.setText(mainUser.getUsername());
    }

    public void goToFindGameActivity(View v) {
        if(!Controller.getInstance().isConnectionAlive())
            showErrorMessage("No connection available");
        else {
            Intent switchActivities = new Intent(this, FindGameActivity.class);
            startActivity(switchActivities);
        }
    }

    public void goToCreateGameActivity(View v) {
        Intent switchActivities = new Intent(this, CreateGameActivity.class);
        startActivity(switchActivities);
    }

    public void showErrorMessage(String errorMessage) {
        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
    }

    public void goToLoginActivity() {
        Intent switchActivities = new Intent(this, LoginActivity.class);
        startActivity(switchActivities);
    }
}
