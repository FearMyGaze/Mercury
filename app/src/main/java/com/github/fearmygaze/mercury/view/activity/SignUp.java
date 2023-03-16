package com.github.fearmygaze.mercury.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.firebase.Auth;
import com.github.fearmygaze.mercury.util.RegEx;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class SignUp extends AppCompatActivity {

    ShapeableImageView goBack, userImage;
    TextInputLayout displayNameError, usernameError, emailError, passwordError;
    TextInputEditText displayName, username, email, password;
    MaterialButton chooseImage, createAccount;

    Uri imageData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        goBack = findViewById(R.id.signupGoBack);
        displayNameError = findViewById(R.id.signUpDisplayNameError);
        displayName = findViewById(R.id.signUpDisplayName);
        usernameError = findViewById(R.id.signUpUsernameError);
        username = findViewById(R.id.signUpUsername);
        emailError = findViewById(R.id.signUpEmailError);
        email = findViewById(R.id.signUpEmail);
        passwordError = findViewById(R.id.signUpPasswordError);
        password = findViewById(R.id.signUpPassword);
        userImage = findViewById(R.id.signUpUserImage);
        chooseImage = findViewById(R.id.signUpChooseImage);
        createAccount = findViewById(R.id.signUpCreateAccount);

        goBack.setOnClickListener(v -> onBackPressed());

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (RegEx.isEmailValid(email, emailError, SignUp.this)) {
                    usernameError.setEnabled(true);
                }
            }
        });

        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (RegEx.isUsernameValid(username, usernameError, SignUp.this)) {
                    displayNameError.setEnabled(true);
                }
            }
        });

        displayName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (RegEx.isNameValid(displayName, displayNameError, SignUp.this)) {
                    passwordError.setEnabled(true);
                }
            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (RegEx.isPasswordValid(password, passwordError, SignUp.this)) {
                    chooseImage.setEnabled(true);
                }
            }
        });

        chooseImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
            pickImage.launch(intent);
        });

        createAccount.setOnClickListener(v -> {//TODO: I need to find a way to say the user we process the data
            String sEmail = Objects.requireNonNull(email.getText()).toString().trim();
            String sUsername = Objects.requireNonNull(username.getText()).toString().trim();
            String sDisplayName = Objects.requireNonNull(displayName.getText()).toString().trim();
            String sPassword = Objects.requireNonNull(password.getText()).toString().trim();
            if (!emailError.isErrorEnabled() && !usernameError.isErrorEnabled() && !displayNameError.isErrorEnabled() && !passwordError.isErrorEnabled()) {
                Auth.signUpForm(sEmail, emailError, sUsername, usernameError, sDisplayName, sPassword, imageData, SignUp.this, new Auth.OnResultListener() {
                    @Override
                    public void onResult(boolean result) {
                        if (result) {
                            startActivity(new Intent(SignUp.this, SignIn.class));
                            finish();
                        } else
                            Toast.makeText(SignUp.this, getString(R.string.blameError), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(String message) {
                        Toast.makeText(SignUp.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SignUp.this, SignIn.class));
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                switch (result.getResultCode()){
                    case RESULT_OK:
                        if (result.getData() != null){
                            imageData = result.getData().getData();
                            Glide.with(userImage).load(imageData).centerCrop().apply(new RequestOptions().override(1024)).into(userImage);
                            userImage.setImageURI(imageData);
                            createAccount.setEnabled(true);
                        }
                        break;
                    case RESULT_CANCELED:
                        break;
                    default:
                        Toast.makeText(SignUp.this, "ERROR", Toast.LENGTH_SHORT).show();
                }
            }
    );
}