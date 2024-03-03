package com.unina.guesstheword;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.unina.guesstheword.control.Controller;

public class GuessTheWordApplication extends Application {

    private static GuessTheWordApplication instance;
    private Activity currentActivity;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static Context getAppContext() {
        return instance.getApplicationContext();
    }

    public Activity getCurrentActivity() {
        return currentActivity;
    }

    public void setCurrentActivity(Activity currentActivity) {
        this.currentActivity = currentActivity;
    }

    public static GuessTheWordApplication getInstance() {
        return instance;
    }
}
