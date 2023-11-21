package com.example.guesstheword.ui.login;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.guesstheword.R;
import com.example.guesstheword.data.DAO.UserDAO;
import org.json.JSONException;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginViewModel extends ViewModel {

    private final MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private final MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String email, String password, Context context) {
        try {
            UserDAO userDAO = new UserDAO(context);
            userDAO.retriveUser(email, password);
            loginResult.setValue(new LoginResult(userDAO.getUser()));
        } catch (UserDAO.UserNotFoundException e) {
            loginResult.setValue(new LoginResult(R.string.user_not_found));
            loginFormState.setValue(new LoginFormState(R.string.user_not_found, null));
        } catch (UserDAO.WrongPasswordException e) {
            loginResult.setValue(new LoginResult(R.string.wrong_password));
            loginFormState.setValue(new LoginFormState(null, R.string.wrong_password));
        } catch (JSONException e) {
            loginResult.setValue(new LoginResult(R.string.login_failed));
        } catch (IOException e) {
            loginResult.setValue(new LoginResult(R.string.connection_failed));
        }

        /*
        // can be launched in a separate asynchronous job
        Result<LoggedInUser> result = loginRepository.login(email, password);

        if (result instanceof Result.Success) {
            LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();

        } else {
            loginResult.setValue(new LoginResult(R.string.login_failed));
        }
        */
    }

    public void loginDataChanged(String email, String password) {
        if (!isEmailValid(email)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_email, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    /**
     * A placeholder email validation check
     */
    private boolean isEmailValid(String email) {
        if (email == null) {
            return false;
        }

        final String regular_expression = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        Pattern pattern = Pattern.compile(regular_expression);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();

        /*
        if (email.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches();
        } else {
            return !email.trim().isEmpty();
        }
        */
    }

    /**
     * A placeholder password validation check
     */
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}