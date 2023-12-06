package com.example.guesstheword.ui.login;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.*;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import com.example.guesstheword.data.SharedPreferencesManager;
import com.example.guesstheword.data.model.User;
import com.example.guesstheword.databinding.ActivityLoginBinding;
import com.example.guesstheword.server.SocketService;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;

    private EditText emailEditText;
    private EditText passwordEditText;
    private ProgressBar loadingProgressBar;

    public void goToRegistrationActivity(View view) {
        Intent switchActivities = new Intent(this, RegistrationActivity.class);
        startActivity(switchActivities);
    }

        /** Messenger for communicating with the service. */
        Messenger mService = null;

        /** Flag indicating whether we have called bind on the service. */
        boolean bound;

        /**
         * Class for interacting with the main interface of the service.
         */
        private final ServiceConnection mConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName className, IBinder service) {
                // This is called when the connection with the service has been
                // established, giving us the object we can use to
                // interact with the service.  We are communicating with the
                // service using a Messenger, so here we get a client-side
                // representation of that from the raw IBinder object.
                mService = new Messenger(service);
                bound = true;
            }

            public void onServiceDisconnected(ComponentName className) {
                // This is called when the connection with the service has been
                // unexpectedly disconnected&mdash;that is, its process crashed.
                mService = null;
                bound = false;
            }
        };

        public void SignIn(View v) {
            if (!bound) return;
            // Create and send a message to the service, using a supported 'what' value.

            User user = new User(emailEditText.getText().toString(), passwordEditText.getText().toString());
            Message msg = Message.obtain(null, SocketService.SIGN_IN, user);
//            msg.obj = new Request("SIGN_IN", user);
//            msg = Message.obtain(null, SocketManager.SIGN_IN, 0, 0);
            try {
                mService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            com.example.guesstheword.databinding.ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
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

//            loginViewModel.getLoginResult().observe(this, new Observer<SignResult>() {
//                @Override
//                public void onChanged(@Nullable SignResult signResult) {
//                    if (signResult == null) {
//                        return;
//                    }
//                    loadingProgressBar.setVisibility(View.GONE);
//                    if (signResult.getError() != null) {
//                        showLoginFailed(signResult.getError());
//                        showLoginFailed(getString(R.string.login_failed));
//                    }
//                    if (signResult.getSuccess() != null) {
//                        updateUiWithUser(signResult.getSuccess());
//                        setResult(Activity.RESULT_OK);
//
//                        //Complete and destroy login activity once successful
//                        finish();
//                    }
//                }
//            });

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
                    loginViewModel.loginDataChanged(emailEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
            };
            emailEditText.addTextChangedListener(afterTextChangedListener);
            passwordEditText.addTextChangedListener(afterTextChangedListener);
//            passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
//                if (actionId == EditorInfo.IME_ACTION_DONE) {
////                        loginViewModel.login(emailEditText.getText().toString(),
////                                passwordEditText.getText().toString(), v.getContext());
//                    SignIn(v);
//                }
//                return false;
//            });

            loginButton.setOnClickListener(v -> {
                loadingProgressBar.setVisibility(View.VISIBLE);
//                    loginViewModel.login(emailEditText.getText().toString(),
//                            passwordEditText.getText().toString(), v.getContext());
                SignIn(v);
            });
        }

        @Override
        protected void onStart() {
            super.onStart();
            bindService(new Intent(this, SocketService.class), mConnection,
                    Context.BIND_AUTO_CREATE);
            // Bind to the service.
            if(SharedPreferencesManager.getInstance().isUserLoggedIn()){
                //TODO: go to main activity
                //Send the user's data to the server
                User user = SharedPreferencesManager.getInstance().getUserData();
                Message msg = Message.obtain(null, SocketService.SIGN_IN, user);
                try {
                    mService.send(msg);
                } catch (RemoteException e) {
                    SharedPreferencesManager.getInstance().deleteUserData();
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onStop() {
            super.onStop();
            // Unbind from the service.
            if (bound) {
                unbindService(mConnection);
                bound = false;
            }
        }

    }