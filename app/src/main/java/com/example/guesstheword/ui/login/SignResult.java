package com.example.guesstheword.ui.login;

import androidx.annotation.Nullable;
import com.example.guesstheword.data.model.User;

/**
 * Authentication result : success (user details) or error message.
 */
class SignResult {
    @Nullable
    private User success;
    @Nullable
    private String error;

    SignResult(@Nullable String error) {
        this.error = error;
    }

    SignResult(@Nullable User success) {
        this.success = success;
    }

    @Nullable
    User getSuccess() {
        return success;
    }

    @Nullable
    String getError() {
        return error;
    }
}