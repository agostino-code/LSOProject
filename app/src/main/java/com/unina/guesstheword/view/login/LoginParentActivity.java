package com.unina.guesstheword.view.login;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.unina.guesstheword.GuessTheWordApplication;
import com.unina.guesstheword.R;
import com.unina.guesstheword.control.Controller;
import com.unina.guesstheword.data.model.User;
import com.unina.guesstheword.view.menu.MenuActivity;

public class LoginParentActivity extends AppCompatActivity {
    protected void updateUiWithUser(User user) {
        Intent switchActivities = new Intent(this, MenuActivity.class);
        String welcome = getString(R.string.welcome) + " " + user.getUsername() + "!";
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
        startActivity(switchActivities);
    }
}
