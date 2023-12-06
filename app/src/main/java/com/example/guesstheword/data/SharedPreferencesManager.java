package com.example.guesstheword.data;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.guesstheword.GuessTheWordApplication;
import com.example.guesstheword.data.model.User;

public class SharedPreferencesManager {
    //Class that manages the SharedPreferences
    //It is a Singleton
    //It is used to store the user's data

    private static SharedPreferencesManager instance;
    private static final String SHARED_PREFS = "SHARED_PREFS";
    private static final String USERNAME = "USERNAME";
    private static final String AVATAR = "AVATAR";
    private static final String EMAIL = "EMAIL";
    private static final String PASSWORD = "PASSWORD";

    private SharedPreferencesManager() {}

    public static SharedPreferencesManager getInstance() {
        if(instance == null) {
            instance = new SharedPreferencesManager();
        }
        return instance;
    }

    public void saveUserData(User user) {
        //Save the user's data in the SharedPreferences
        SharedPreferences.Editor editor = GuessTheWordApplication.getAppContext().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).edit();
        editor.putString(USERNAME, user.getUsername());
        editor.putInt(AVATAR, user.getAvatar());
        editor.putString(EMAIL, user.getEmail());
        editor.putString(PASSWORD, user.getPassword());
        editor.apply();
    }

    public User getUserData() {
        //Get the user's data from the SharedPreferences
        SharedPreferences sharedPreferences = GuessTheWordApplication.getAppContext().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        String username = sharedPreferences.getString(USERNAME, "");
        int avatar = sharedPreferences.getInt(AVATAR, 0);
        String email = sharedPreferences.getString(EMAIL, "");
        String password = sharedPreferences.getString(PASSWORD, "");
        return new User(email, password, username, avatar);
    }

    public void deleteUserData() {
        //Delete the user's data from the SharedPreferences
        SharedPreferences.Editor editor = GuessTheWordApplication.getAppContext().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
    }

    public boolean isUserLoggedIn() {
        //Check if the user is logged in
        SharedPreferences sharedPreferences = GuessTheWordApplication.getAppContext().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.contains(USERNAME);
    }


}
