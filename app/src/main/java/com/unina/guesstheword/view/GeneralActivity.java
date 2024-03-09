package com.unina.guesstheword.view;

import android.app.Activity;

import com.unina.guesstheword.GuessTheWordApplication;

public class GeneralActivity extends Activity {

    @Override
    protected void onResume() {
        super.onResume();
        GuessTheWordApplication.getInstance().setCurrentActivity(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (GuessTheWordApplication.getInstance().getCurrentActivity() == this) {
            GuessTheWordApplication.getInstance().setCurrentActivity(null);
        }
    }
}
