package com.example.guesstheword;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;

public class GuessTheWordApplication extends Application {
    private static Context context;

    public void onCreate() {
        super.onCreate();
        GuessTheWordApplication.context = getApplicationContext();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public static Context getAppContext() {
        return GuessTheWordApplication.context;
    }
}
