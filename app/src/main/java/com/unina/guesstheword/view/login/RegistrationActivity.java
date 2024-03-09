package com.unina.guesstheword.view.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.gridlayout.widget.GridLayout;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.unina.guesstheword.control.Controller;
import com.unina.guesstheword.databinding.ActivityRegistrationBinding;

import java.util.concurrent.CompletableFuture;

public class RegistrationActivity extends LoginParentActivity {
    private ActivityRegistrationBinding binding;

    private GridLayout avatarsGrid;
    private ImageView mainAvatarImageView;
    private int mainAvatarImageId=0;
    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText repeatPasswordEditText;
    private Button signUpButton;
    private ProgressBar loadingProgressBar;

    private RegistrationViewModel registrationViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        usernameEditText = binding.registrationUsername;
        emailEditText = binding.registrationEmail;
        passwordEditText = binding.registrationPassword;
        repeatPasswordEditText = binding.registrationRepeatPassword;
        signUpButton = binding.signUpButton;
        loadingProgressBar = binding.loading;
        mainAvatarImageView = binding.imageViewAvatar;
        avatarsGrid = binding.avatarsGrid;

        registrationViewModel = new RegistrationViewModel();
        registrationViewModel.getRegistrationFormState().observe((LifecycleOwner) this, new Observer<RegistrationFormState>() {
            @Override
            public void onChanged(@Nullable RegistrationFormState registrationFormState) {
                if (registrationFormState == null) {
                    return;
                }
                if (registrationFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(registrationFormState.getUsernameError()));
                } else {
                    usernameEditText.setError(null);
                }
                if (registrationFormState.getEmailError() != null) {
                    emailEditText.setError(getString(registrationFormState.getEmailError()));
                } else {
                    emailEditText.setError(null);
                }
                if (registrationFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(registrationFormState.getPasswordError()));
                } else {
                    passwordEditText.setError(null);
                }
                if (registrationFormState.getRepeatPasswordError() != null) {
                    repeatPasswordEditText.setError(getString(registrationFormState.getRepeatPasswordError()));
                } else {
                    repeatPasswordEditText.setError(null);
                }
                signUpButton.setEnabled(registrationFormState.isDataValid());
            }
        });

//        registrationViewModel.getRegistrationResult().observe(this, new Observer<SignResult>() {
//            @Override
//            public void onChanged(@Nullable SignResult signResult) {
//                if (signResult == null) {
//                    return;
//                }
//
//                loadingProgressBar.setVisibility(View.GONE);
//                if(signResult.getSuccess() != null) {
//                    updateUiWithUser(signResult.getSuccess());
//                    setResult(Activity.RESULT_OK);
//
//                    //Complete and destroy login activity once successful
//                    finish();
//                } else if (signResult.getError() != null) {
//                    showRegistrationFailed(signResult.getError());
//                    showRegistrationFailed(getString(R.string.registration_failed));
//                }
//            }
//        });
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
            public void afterTextChanged(android.text.Editable s) {
                registrationViewModel.isValidSignUpInput(usernameEditText.getText().toString(),
                        emailEditText.getText().toString(), passwordEditText.getText().toString(),
                        repeatPasswordEditText.getText().toString(), mainAvatarImageId);
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        emailEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        repeatPasswordEditText.addTextChangedListener(afterTextChangedListener);

        mainAvatarImageView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1,
                                       int i2, int i3, int i4, int i5,
                                       int i6, int i7) {
                registrationViewModel.isValidSignUpInput(usernameEditText.getText().toString(),
                        emailEditText.getText().toString(), passwordEditText.getText().toString(),
                        repeatPasswordEditText.getText().toString(), mainAvatarImageId);
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
//                registrationViewModel.signUp(usernameEditText.getText().toString(),
//                        emailEditText.getText().toString(), passwordEditText.getText().toString(),
//                        mainAvatarImageId, v.getContext());
                signUp();
            }
        });

        // Per far sì che quando clicchi sullo schermo la griglia, se presente, scompare,
        // ovunque clicchi, non importa dove:
        ConstraintLayout registrationContainer = binding.registrationContainer;
        registrationContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                avatarsGrid.setVisibility(View.GONE);
            }
        });

    }

    public void showOrToggleImageGrid(View view) {
        if (avatarsGrid.getVisibility() == View.VISIBLE)
            avatarsGrid.setVisibility(View.GONE);
        else
            avatarsGrid.setVisibility(View.VISIBLE);
    }

    public void selectAvatarImage(View view) {
        if (view.getId() == binding.SelectableAvatarImage1.getId()) {
            mainAvatarImageView.setImageDrawable(binding.SelectableAvatarImage1.getDrawable());
            mainAvatarImageId = 1;
        } else if (view.getId() == binding.SelectableAvatarImage2.getId()) {
            mainAvatarImageView.setImageDrawable(binding.SelectableAvatarImage2.getDrawable());
            mainAvatarImageId = 2;
        } else if (view.getId() == binding.SelectableAvatarImage3.getId()) {
            mainAvatarImageView.setImageDrawable(binding.SelectableAvatarImage3.getDrawable());
            mainAvatarImageId = 3;
        } else if (view.getId() == binding.SelectableAvatarImage4.getId()) {
            mainAvatarImageView.setImageDrawable(binding.SelectableAvatarImage4.getDrawable());
            mainAvatarImageId = 4;
        } else if (view.getId() == binding.SelectableAvatarImage5.getId()) {
            mainAvatarImageView.setImageDrawable(binding.SelectableAvatarImage5.getDrawable());
            mainAvatarImageId = 5;
        } else if (view.getId() == binding.SelectableAvatarImage6.getId()) {
            mainAvatarImageView.setImageDrawable(binding.SelectableAvatarImage6.getDrawable());
            mainAvatarImageId = 6;
        } else if (view.getId() == binding.SelectableAvatarImage7.getId()) {
            mainAvatarImageView.setImageDrawable(binding.SelectableAvatarImage7.getDrawable());
            mainAvatarImageId = 7;
        } else if (view.getId() == binding.SelectableAvatarImage8.getId()) {
            mainAvatarImageView.setImageDrawable(binding.SelectableAvatarImage8.getDrawable());
            mainAvatarImageId = 8;
        } else if (view.getId() == binding.SelectableAvatarImage9.getId()) {
            mainAvatarImageView.setImageDrawable(binding.SelectableAvatarImage9.getDrawable());
            mainAvatarImageId = 9;
        } else if (view.getId() == binding.SelectableAvatarImage10.getId()) {
            mainAvatarImageView.setImageDrawable(binding.SelectableAvatarImage10.getDrawable());
            mainAvatarImageId = 10;
        } else if (view.getId() == binding.SelectableAvatarImage11.getId()) {
            mainAvatarImageView.setImageDrawable(binding.SelectableAvatarImage11.getDrawable());
            mainAvatarImageId = 11;
        } else if (view.getId() == binding.SelectableAvatarImage12.getId()) {
            mainAvatarImageView.setImageDrawable(binding.SelectableAvatarImage12.getDrawable());
            mainAvatarImageId = 12;
        } else if (view.getId() == binding.SelectableAvatarImage13.getId()) {
            mainAvatarImageView.setImageDrawable(binding.SelectableAvatarImage13.getDrawable());
            mainAvatarImageId = 13;
        } else if (view.getId() == binding.SelectableAvatarImage14.getId()) {
            mainAvatarImageView.setImageDrawable(binding.SelectableAvatarImage14.getDrawable());
            mainAvatarImageId = 14;
        } else if (view.getId() == binding.SelectableAvatarImage15.getId()) {
            mainAvatarImageView.setImageDrawable(binding.SelectableAvatarImage15.getDrawable());
            mainAvatarImageId = 15;
        } else if (view.getId() == binding.SelectableAvatarImage16.getId()) {
            mainAvatarImageView.setImageDrawable(binding.SelectableAvatarImage16.getDrawable());
            mainAvatarImageId = 16;
        }
        avatarsGrid.setVisibility(View.GONE);
    }

    public void goToLoginActivity(View view) {
        Intent switchActivities = new Intent(this, LoginActivity.class);
        startActivity(switchActivities);
    }

    private void signUp(){
        /*User user = new User(emailEditText.getText().toString(),
                passwordEditText.getText().toString(),usernameEditText.getText().toString(),mainAvatarImageId);
        Controller.getInstance().setUser(user);
        SharedPreferencesManager.getInstance().saveUserData(user);
        updateUiWithUser(Controller.getInstance().getUser());*/

        loadingProgressBar.setVisibility(View.VISIBLE);
        boolean success = Controller.getInstance().SignUp(emailEditText.getText().toString(),
                passwordEditText.getText().toString(),usernameEditText.getText().toString(),mainAvatarImageId);
            if(success) updateUiWithUser(Controller.getInstance().getUser());
            else loadingProgressBar.setVisibility(View.GONE);
    }


}
