package com.example.keepingfit.unpublished;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.keepingfit.Firestore.FirestoreHelper;
import com.example.keepingfit.Firestore.FirestoreModel;
import com.example.keepingfit.MainActivity;
import com.example.keepingfit.R;
import com.example.keepingfit.alertdialog.LoadingDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Objects;

public class Register extends AppCompatActivity {

    EditText mUsername, mEmail, mPassword, mRePassword;
    TextView mSignInBtn;
    CardView mSignUpBtn;
    FirebaseAuth mAuth;
    FirebaseUser user;
    private static final String USERS_COLLECTION = "Users";

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

        mAuth = FirebaseAuth.getInstance();

        LoadingDialog loadingDialog = new LoadingDialog(Register.this);

        mSignUpBtn.setOnClickListener(v -> {
            String email = mEmail.getText().toString().trim();
            String password = mPassword.getText().toString().trim();
            String rePassword = mRePassword.getText().toString().trim();
            String username = mUsername.getText().toString().trim();

            loadingDialog.startLoadingDialog();

            if (TextUtils.isEmpty(username)) {
                mEmail.setError("username is Required.");
                loadingDialog.dismissDialog();
                return;
            }

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

            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    user = mAuth.getCurrentUser();
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(username)
                            .build();
                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(task2 -> {
                                if (task2.isSuccessful()) {
                                    FirestoreModel model = new FirestoreModel(user.getDisplayName(), user.getEmail(), user.getPhotoUrl());
                                    FirestoreHelper firestoreHelper = new FirestoreHelper();
                                    firestoreHelper.addData(USERS_COLLECTION, user.getUid(), model, task1 -> {
                                        if (task1.isSuccessful()) {
                                            Log.d(TAG, "Document added successfully!");
                                        } else {
                                            Log.w(TAG, "Error adding document", task1.getException());
                                        }
                                    });
                                    updateUI(user);
                                }
                            });
                } else {
                    Toast.makeText(Register.this, "Error ! ." + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }
                loadingDialog.dismissDialog();
            });

        });

        mSignInBtn.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Login.class)));
    }

    private void updateUI(FirebaseUser user) {
        if(user != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        } else {
            Toast.makeText(this, "登入失敗", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        user = mAuth.getCurrentUser();
        if(user != null){
            reload();
        }
    }

    private void reload() {
        user.reload()
                .addOnSuccessListener(aVoid -> {
                    // 資料已重新載入，更新使用者介面
                    updateUI(user);
                })
                .addOnFailureListener(e -> Toast.makeText(Register.this, "載入使用者資料失敗", Toast.LENGTH_SHORT).show());
    }
}