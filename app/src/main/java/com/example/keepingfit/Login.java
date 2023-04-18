package com.example.keepingfit;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class Login extends AppCompatActivity {

    EditText mEmail, mPassword;
    TextView mSignUpBtn,mForgotPass;
    CardView mLoginBtn;
    FirebaseAuth fAuth;
    BottomSheetDialog forgotPassDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mLoginBtn = findViewById(R.id.loginbtn);
        mSignUpBtn = findViewById(R.id.signUpBtn);
        mForgotPass = findViewById(R.id.forgotPass);

        fAuth = FirebaseAuth.getInstance();

        LoadingDialog loadingDialog = new LoadingDialog(Login.this);

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

            fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(Login.this, "User Logged In.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(Login.this, "Error!." + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
                loadingDialog.dismissDialog();
            });
        });

        mSignUpBtn.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Register.class)));

        forgotPassDialog = new BottomSheetDialog(Login.this);

        createDialog();

        mForgotPass.setOnClickListener(v -> forgotPassDialog.show());

        forgotPassDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = fAuth.getCurrentUser();
        if(currentUser != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }

    private void createDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_forgot_pass, null);

        Button send = view.findViewById(R.id.send);
        EditText email = view.findViewById(R.id.email);

        send.setOnClickListener(v -> {

            forgotPassDialog.dismiss();

            String mail = email.getText().toString().trim();

            if (TextUtils.isEmpty(mail)) {
                Toast.makeText(Login.this, "Email address is required to reset password", Toast.LENGTH_SHORT).show();
                return;
            }

            fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(unused ->
                    Toast.makeText(Login.this, "Reset Link Sent To Your Email.", Toast.LENGTH_SHORT).show()
            ).addOnFailureListener(e ->
                    Toast.makeText(Login.this, "Error ! Reset Link is Not Sent" + e.getMessage(), Toast.LENGTH_SHORT).show());
        });
        forgotPassDialog.setContentView(view);
    }
}