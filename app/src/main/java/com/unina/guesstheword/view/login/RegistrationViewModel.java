package com.unina.guesstheword.view.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.unina.guesstheword.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationViewModel extends ViewModel {
//    private final MutableLiveData<SignResult> registrationResult = new MutableLiveData<>();
    private final MutableLiveData<RegistrationFormState> registrationFormState = new MutableLiveData<>();

//    public LiveData<SignResult> getRegistrationResult() {
//        return registrationResult;
//    }

    public LiveData<RegistrationFormState> getRegistrationFormState() {
        return registrationFormState;
    }
    /**
     * It creates the main User, if valid, and it sends it to the server
     *
     * @param email    must be a new email, not an already existing one on the database
     * @param username must be a new username, not an already existing one on the database
     */

    /**
     * Called every time the user modifies the registration's edit-texts. It checks if the data are valid and it updates the registrationFormState
     * @param email    must respect the RFC 5322 email format (unique attribute)
     * @param password at least 5 characters
     * @param repeatPassword must be the same of password
     * @param username (this will be used as the User ID, as Primary key) must be alphanumeric and start with a letter, at least 1 character and max 30 characters
     */
    public void isValidSignUpInput(String username, String email, String password, String repeatPassword, int avatarId) {
        if (!isUsernameValid(username)) {
            registrationFormState.setValue(new RegistrationFormState(R.string.invalid_username, null, null, null, true));
        } else if (!isEmailValid(email)) {
            registrationFormState.setValue(new RegistrationFormState(null, R.string.invalid_email, null, null, true));
        } else if (!isPasswordValid(password)) {
            registrationFormState.setValue(new RegistrationFormState(null, null, R.string.invalid_password, null, true));
        } else if (!isRepeatPasswordValid(password, repeatPassword)) {
            registrationFormState.setValue(new RegistrationFormState(null, null, null, R.string.invalid_repeat_password, true));
        } else if (!isAvatarSelected(avatarId)) {
            registrationFormState.setValue(new RegistrationFormState(null, null, null, null, false));
        } else {
            registrationFormState.setValue(new RegistrationFormState(true));
        }
    }

    /**
     * A placeholder email validation check.
     * must respect the RFC 5322 email format
     */
    private boolean isEmailValid(String email) {
        if (email == null) {
            return false;
        }

        final String regular_expression = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        Pattern pattern = Pattern.compile(regular_expression);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /**
     * A placeholder password validation check.
     * at least 5 characters
     */
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }

    /**
     * A placeholder repeat password validation check.
     * must be the same of password
     */
    private boolean isRepeatPasswordValid(String password, String repeat_password) {
        return repeat_password.equals(password);
    }

    /**
     * A placeholder username validation check.
     * must be alphanumeric and start with a letter, at least 1 character and max 30 characters
     */
    private boolean isUsernameValid(String username) {
        if (username == null)
            return false;

        final String regular_expression = "^[A-Za-z][A-Za-z0-9_!?]{0,29}$";
        Pattern pattern = Pattern.compile(regular_expression);
        Matcher matcher = pattern.matcher(username);
        return matcher.matches();
    }

    private boolean isAvatarSelected(int avatarId) {
        return avatarId != 0;
    }
}
