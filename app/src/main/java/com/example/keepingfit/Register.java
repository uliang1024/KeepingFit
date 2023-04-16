package com.example.keepingfit;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class Register extends AppCompatActivity {

    EditText mUsername, mEmail, mPassword, mRePassword;
    TextView mSignInBtn;
    CardView mSignUpBtn;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mUsername = findViewById(R.id.username);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mRePassword = findViewById(R.id.repassword);
        mSignUpBtn = findViewById(R.id.signupbtn);
        mSignInBtn = findViewById(R.id.signInBtn);

        fAuth = FirebaseAuth.getInstance();

        LoadingDialog loadingDialog = new LoadingDialog(Register.this);

        if (fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        mSignUpBtn.setOnClickListener(v -> {
            String email = mEmail.getText().toString().trim();
            String password = mPassword.getText().toString().trim();
            String rePassword = mRePassword.getText().toString().trim();

            loadingDialog.startLoadingDialog();

            if (TextUtils.isEmpty(email)) {
                mEmail.setError("Email is Required.");
                loadingDialog.dismissDialog();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                mPassword.setError("Password is Required.");
                loadingDialog.dismissDialog();
                return;
            }

            if (password.length() < 6) {
                mPassword.setError("Password Must be >= 6 Characters.");
                loadingDialog.dismissDialog();
                return;
            }

            if (!rePassword.equals(password)) {
                mRePassword.setError("Your new and confirm password are different. Please enter your passwords again.");
                loadingDialog.dismissDialog();
                return;
            }

            loadingDialog.dismissDialog();

            fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {

                    FirebaseUser fUser = fAuth.getCurrentUser();
                    fUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(Register.this, "Verification Email Sent.", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@org.jetbrains.annotations.NotNull Exception e) {
                            Log.d(TAG, "onFailure: Email not sent " + e.getMessage());
                        }
                    });

                    Toast.makeText(Register.this, "User Created.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                } else {
                    Toast.makeText(Register.this, "Error ! ." + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
                loadingDialog.dismissDialog();
            });

        });

        mSignInBtn.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), Login.class));
        });
    }
}