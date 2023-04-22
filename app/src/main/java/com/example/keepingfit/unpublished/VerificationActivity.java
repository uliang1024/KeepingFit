package com.example.keepingfit.unpublished;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.keepingfit.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class VerificationActivity extends AppCompatActivity {

    CardView sendCode;
    TextView loginBtn;
    FirebaseAuth mAuth;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        sendCode = findViewById(R.id.sendCode);
        loginBtn = findViewById(R.id.loginBtn);

        sendCode.setOnClickListener(v -> {
            if (user != null) {
                user.sendEmailVerification().addOnSuccessListener(unused ->
                        Toast.makeText(VerificationActivity.this, "Verification Email Sent.",
                                Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> Log.d("tag", "onFailure: Email not sent " + e.getMessage()));
            }
        });

        loginBtn.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(VerificationActivity.this, Login.class));
            finish();
        });
    }
}