package com.unina.guesstheword.view.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.unina.guesstheword.GuessTheWordApplication;
import com.unina.guesstheword.control.Controller;
import com.unina.guesstheword.databinding.ActivityLoginBinding;

import java.util.concurrent.CompletableFuture;


public class LoginActivity extends LoginParentActivity {

    private LoginViewModel loginViewModel;

    private EditText emailEditText;
    private EditText passwordEditText;
    private ProgressBar loadingProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GuessTheWordApplication.getInstance().setCurrentActivity(this);
        Controller.getInstance();
        ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new LoginViewModel();

        emailEditText = binding.loginEmail;
        passwordEditText = binding.loginPassword;
        Button loginButton = binding.signInButton;
        loadingProgressBar = binding.loading;

        loginViewModel.getLoginFormState().observe(this, loginFormState -> {
            if (loginFormState == null) {
                return;
            }
            loginButton.setEnabled(loginFormState.isDataValid());
            if (loginFormState.getEmailError() != null) {
                emailEditText.setError(getString(loginFormState.getEmailError()));
            }
            if (loginFormState.getPasswordError() != null) {
                passwordEditText.setError(getString(loginFormState.getPasswordError()));
            }
        });

        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.isValidLoginInput(emailEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        });

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.isValidLoginInput(emailEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        });
        passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                signIn();
            }
            return false;
        });

        loginButton.setOnClickListener(v -> signIn());

    }

    private void signIn() {
        loadingProgressBar.setVisibility(View.VISIBLE);
        boolean success = Controller.getInstance().SignIn(emailEditText.getText().toString(),
                passwordEditText.getText().toString());
            if (success) updateUiWithUser(Controller.getInstance().getUser());
            else loadingProgressBar.setVisibility(View.GONE);
    }

    public void goToRegistrationActivity(View view) {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }

}