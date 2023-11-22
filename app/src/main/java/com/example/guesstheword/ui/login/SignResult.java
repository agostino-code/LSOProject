package com.example.guesstheword.ui.login;

import androidx.annotation.Nullable;
import com.example.guesstheword.data.model.User;

/**
 * Authentication result : success (user details) or error message.
 */
class SignResult {
    @Nullable
    private final User success;
    @Nullable
    private final String error;

    SignResult(@Nullable String error) {
        this.error = error;
        success = null;
    }

    SignResult(@Nullable User success) {
        this.success = success;
        error = null;
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