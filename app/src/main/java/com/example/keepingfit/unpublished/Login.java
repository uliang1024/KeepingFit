package com.example.keepingfit.unpublished;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.keepingfit.Firestore.FirestoreHelper;
import com.example.keepingfit.Firestore.FirestoreModel;
import com.example.keepingfit.MainActivity;
import com.example.keepingfit.R;
import com.example.keepingfit.alertdialog.LoadingDialog;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class Login extends AppCompatActivity {

    EditText mEmail, mPassword;
    TextView mSignUpBtn,mForgotPass;
    CardView mLoginBtn;
    ImageView googleBtn,facebookBtn;
    FirebaseAuth mAuth;
    FirebaseUser user;
    BottomSheetDialog forgotPassDialog;
    LoadingDialog loadingDialog;
    SignInClient oneTapClient;
    BeginSignInRequest signInRequest;
    int REQ_ONE_TAP = 1;
    private static final String USERS_COLLECTION = "Users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mLoginBtn = findViewById(R.id.loginbtn);
        mSignUpBtn = findViewById(R.id.signUpBtn);
        mForgotPass = findViewById(R.id.forgotPass);
        googleBtn = findViewById(R.id.googleBtn);
        facebookBtn = findViewById(R.id.facebookBtn);

        mAuth = FirebaseAuth.getInstance();

        loadingDialog = new LoadingDialog(Login.this);

        mLoginBtn.setOnClickListener(v -> {
            String email = mEmail.getText().toString().trim();
            String password = mPassword.getText().toString().trim();

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

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "signInWithEmail:success");
                    user = mAuth.getCurrentUser();
                    updateUI(user);
                    login();
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                    Toast.makeText(Login.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }
                loadingDialog.dismissDialog();
            });
        });

        mSignUpBtn.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Register.class)));

        forgotPassDialog = new BottomSheetDialog(Login.this);

        createDialog();

        mForgotPass.setOnClickListener(v -> forgotPassDialog.show());

        forgotPassDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        //google 登入
        oneTapClient = Identity.getSignInClient(this);
        signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(getString(R.string.default_web_client_id))
                        // Only show accounts previously used to sign in.
                        .setFilterByAuthorizedAccounts(true)
                        .build())
                .build();
        googleBtn.setOnClickListener(v -> {
            loadingDialog.startLoadingDialog();
            oneTapClient.beginSignIn(signInRequest)
                    .addOnSuccessListener(this, result -> {
                        try {
                            startIntentSenderForResult(
                                    result.getPendingIntent().getIntentSender(), REQ_ONE_TAP,
                                    null, 0, 0, 0);
                        } catch (IntentSender.SendIntentException e) {
                            Log.e(TAG, "Couldn't start One Tap UI: " + e.getLocalizedMessage());
                        }
                    })
                    .addOnFailureListener(this, e -> {
                        // No saved credentials found. Launch the One Tap sign-up flow, or
                        // do nothing and continue presenting the signed-out UI.
                        Log.d(TAG, e.getLocalizedMessage());
                    });
        });
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
                .addOnFailureListener(e -> Toast.makeText(Login.this, "載入使用者資料失敗", Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_ONE_TAP) {
            try {
                SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
                String idToken = credential.getGoogleIdToken();
                if (idToken != null) {
                    // Got an ID token from Google. Use it to authenticate
                    // with Firebase.
                    AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
                    mAuth.signInWithCredential(firebaseCredential)
                            .addOnCompleteListener(this, task -> {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    assert user != null;
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
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithCredential:failure", task.getException());
                                    updateUI(null);
                                }
                            });
                }
            } catch (ApiException e) {
                // ...
            }
        }
        loadingDialog.dismissDialog();
    }

    private void updateUI(FirebaseUser user) {
        if(user != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        } else {
            Toast.makeText(this, "登入失敗", Toast.LENGTH_SHORT).show();
        }
    }
    private void createDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_forgot_pass, findViewById(android.R.id.content), false);

        Button send = view.findViewById(R.id.send);
        EditText email = view.findViewById(R.id.email);

        send.setOnClickListener(v -> {

            String mail = email.getText().toString().trim();

            if (TextUtils.isEmpty(mail)) {
                Toast.makeText(Login.this, "Email address is required to reset password", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.sendPasswordResetEmail(mail).addOnSuccessListener(unused ->
                    Toast.makeText(Login.this, "Reset Link Sent To Your Email.", Toast.LENGTH_SHORT).show()
            ).addOnFailureListener(e ->
                    Toast.makeText(Login.this, "Error ! Reset Link is Not Sent" + e.getMessage(), Toast.LENGTH_SHORT).show());

            forgotPassDialog.dismiss();
        });
        forgotPassDialog.setContentView(view);
    }

    private void login() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }
}