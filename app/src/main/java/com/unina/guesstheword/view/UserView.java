package com.unina.guesstheword.view;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import com.unina.guesstheword.R;
import com.unina.guesstheword.data.model.User;

/**
 * Class exposing authenticated user details to the UI.
 */
public class UserView extends User {
    private final Context context;

    public UserView(@NonNull User user, @NonNull Context context) {
        super(user);
        this.context = context;
    }

    public Drawable getAvatarDrawable() {
        switch (super.getAvatar()) {
            case User.AVATAR_1:
                return context.getResources().getDrawable(R.drawable.avatar1);
            case User.AVATAR_2:
                return context.getResources().getDrawable(R.drawable.avatar2);
            case User.AVATAR_3:
                return context.getResources().getDrawable(R.drawable.avatar3);
            case User.AVATAR_4:
                return context.getResources().getDrawable(R.drawable.avatar4);
            case User.AVATAR_5:
                return context.getResources().getDrawable(R.drawable.avatar5);
            case User.AVATAR_6:
                return context.getResources().getDrawable(R.drawable.avatar6);
            case User.AVATAR_7:
                return context.getResources().getDrawable(R.drawable.avatar7);
            case User.AVATAR_8:
                return context.getResources().getDrawable(R.drawable.avatar8);
            case User.AVATAR_9:
                return context.getResources().getDrawable(R.drawable.avatar9);
            case User.AVATAR_10:
                return context.getResources().getDrawable(R.drawable.avatar10);
            case User.AVATAR_11:
                return context.getResources().getDrawable(R.drawable.avatar11);
            case User.AVATAR_12:
                return context.getResources().getDrawable(R.drawable.avatar12);
            case User.AVATAR_13:
                return context.getResources().getDrawable(R.drawable.avatar13);
            case User.AVATAR_14:
                return context.getResources().getDrawable(R.drawable.avatar14);
            case User.AVATAR_15:
                return context.getResources().getDrawable(R.drawable.avatar15);
            case User.AVATAR_16:
                return context.getResources().getDrawable(R.drawable.avatar16);
            default:
                return context.getResources().getDrawable(android.R.drawable.ic_menu_gallery);
        }
    }
}