package com.example.guesstheword.view;

import android.graphics.drawable.Drawable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.guesstheword.R;
import com.example.guesstheword.data.model.User;

/**
 * Class exposing authenticated user details to the UI.
 */
public class UserView extends User {
    private final AppCompatActivity activity;

    public UserView(User user, AppCompatActivity activity) {
        super(user);
        this.activity = activity;
    }

    public Drawable getAvatarDrawable() {
        switch (super.getAvatar()) {
            case User.AVATAR_1:
                return activity.getResources().getDrawable(R.drawable.avatar1);
            case User.AVATAR_2:
                return activity.getResources().getDrawable(R.drawable.avatar2);
            case User.AVATAR_3:
                return activity.getResources().getDrawable(R.drawable.avatar3);
            case User.AVATAR_4:
                return activity.getResources().getDrawable(R.drawable.avatar4);
            case User.AVATAR_5:
                return activity.getResources().getDrawable(R.drawable.avatar5);
            case User.AVATAR_6:
                return activity.getResources().getDrawable(R.drawable.avatar6);
            case User.AVATAR_7:
                return activity.getResources().getDrawable(R.drawable.avatar7);
            case User.AVATAR_8:
                return activity.getResources().getDrawable(R.drawable.avatar8);
            case User.AVATAR_9:
                return activity.getResources().getDrawable(R.drawable.avatar9);
            case User.AVATAR_10:
                return activity.getResources().getDrawable(R.drawable.avatar10);
            case User.AVATAR_11:
                return activity.getResources().getDrawable(R.drawable.avatar11);
            case User.AVATAR_12:
                return activity.getResources().getDrawable(R.drawable.avatar12);
            case User.AVATAR_13:
                return activity.getResources().getDrawable(R.drawable.avatar13);
            case User.AVATAR_14:
                return activity.getResources().getDrawable(R.drawable.avatar14);
            case User.AVATAR_15:
                return activity.getResources().getDrawable(R.drawable.avatar15);
            case User.AVATAR_16:
                return activity.getResources().getDrawable(R.drawable.avatar16);
            default:
                return activity.getResources().getDrawable(android.R.drawable.ic_menu_gallery);
        }
    }
}