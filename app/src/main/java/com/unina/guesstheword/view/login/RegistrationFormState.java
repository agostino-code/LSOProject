package com.unina.guesstheword.view.login;

import androidx.annotation.Nullable;

/**
 * Data validation state of the login form. It says if there are error or not
 */
public class RegistrationFormState {
    @Nullable
    private final Integer usernameError;
    @Nullable
    private final Integer emailError;
    @Nullable
    private final Integer passwordError;
    @Nullable
    private final Integer repeatPasswordError;
    private final boolean isAvatarSelected;
    private final boolean isDataValid;

    RegistrationFormState(@Nullable Integer usernameError, @Nullable Integer emailError, @Nullable Integer passwordError, @Nullable Integer repeatPasswordError, boolean isAvatarSelected) {
        this.usernameError = usernameError;
        this.emailError = emailError;
        this.passwordError = passwordError;
        this.repeatPasswordError = repeatPasswordError;
        this.isAvatarSelected = isAvatarSelected;
        this.isDataValid = false;
    }

    RegistrationFormState(boolean isDataValid) {
        this.usernameError = null;
        this.emailError = null;
        this.passwordError = null;
        this.repeatPasswordError = null;
        this.isAvatarSelected = true;
        this.isDataValid = isDataValid;
    }

    @Nullable
    public Integer getUsernameError() {
        return usernameError;
    }

    @Nullable
    public Integer getEmailError() {
        return emailError;
    }

    @Nullable
    public Integer getPasswordError() {
        return passwordError;
    }

    @Nullable
    public Integer getRepeatPasswordError() {
        return repeatPasswordError;
    }

    public boolean isAvatarSelected() {
        return isAvatarSelected;
    }

    public boolean isDataValid() {
        return isDataValid;
    }
}
