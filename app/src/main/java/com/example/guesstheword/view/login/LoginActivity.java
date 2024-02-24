package com.example.guesstheword.view.login;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import com.example.guesstheword.control.Controller;
import com.example.guesstheword.databinding.ActivityLoginBinding;

import java.util.concurrent.CompletableFuture;


public class LoginActivity extends LoginParentActivity {

    private LoginViewModel loginViewModel;

    private EditText emailEditText;
    private EditText passwordEditText;
    private ProgressBar loadingProgressBar;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (Controller.getInstance().isUserLoggedIn()) {
                CompletableFuture<Boolean> successFuture = Controller.getInstance().SignIn(Controller.getInstance().getUser().getEmail(),
                        Controller.getInstance().getUser().getPassword());
                successFuture.thenAccept(success -> {
                    if (success) updateUiWithUser(Controller.getInstance().getUser());
                    else Controller.getInstance().SignOut();
                });

            }
            ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            loginViewModel = new LoginViewModel();

            emailEditText = binding.loginEmail;
            passwordEditText = binding.loginPassword;
            Button loginButton = binding.signInButton;
            loadingProgressBar = binding.loginLoading;

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

            TextWatcher afterTextChangedListener = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // ignore
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // ignore
                }

                @Override
                public void afterTextChanged(Editable s) {
                    loginViewModel.isValidLoginInput(emailEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
            };
            emailEditText.addTextChangedListener(afterTextChangedListener);
            passwordEditText.addTextChangedListener(afterTextChangedListener);
            passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
//                        loginViewModel.login(emailEditText.getText().toString(),
//                                passwordEditText.getText().toString(), v.getContext());
                    signIn();
                }
                return false;
            });

            loginButton.setOnClickListener(v -> {
//                loadingProgressBar.setVisibility(View.VISIBLE);
////                    loginViewModel.login(emailEditText.getText().toString(),
////                            passwordEditText.getText().toString(), v.getContext());
//                Controller.getInstance().SignIn(emailEditText.getText().toString(),
//                        passwordEditText.getText().toString());
                signIn();
            });

        }
        /*
        @Override
        protected void onStart() {
            super.onStart();
//            bindService(new Intent(this, SocketService.class), mConnection,
//                    Context.BIND_AUTO_CREATE);
//            // Bind to the service.
            if(Controller.getInstance().getUser() != null){
                updateUiWithUser(Controller.getInstance().getUser());
            }
        }
        */

        //Sign in local
    private void signIn() {
        loadingProgressBar.setVisibility(View.VISIBLE);
        CompletableFuture<Boolean> successFuture = Controller.getInstance().SignIn(emailEditText.getText().toString(),
                passwordEditText.getText().toString());
        successFuture.thenAccept(success -> {
            if(success) updateUiWithUser(Controller.getInstance().getUser());
            else loadingProgressBar.setVisibility(View.GONE);
        });
    }

    public void goToRegistrationActivity(View view) {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }


//        @Override
//        protected void onStop() {
//            super.onStop();
//            // Unbind from the service.
//            if (bound) {
//                unbindService(mConnection);
//                bound = false;
//            }
//        }

    }