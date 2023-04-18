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

public class VerificationActivity extends AppCompatActivity {

    CardView sendCode;
    FirebaseAuth fAuth;
    String userId;
    TextView loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        sendCode = findViewById(R.id.sendCode);
        loginBtn = findViewById(R.id.loginBtn);

        fAuth = FirebaseAuth.getInstance();
        FirebaseUser user = fAuth.getCurrentUser();
        if(user != null) {
            userId = user.getUid();
            sendCode.setOnClickListener(v -> user.sendEmailVerification().addOnSuccessListener(unused ->
                    Toast.makeText(VerificationActivity.this, "Verification Email Sent.",
                            Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> Log.d("tag", "onFailure: Email not sent " + e.getMessage())));
        }

        loginBtn.setOnClickListener(v -> {
            startActivity(new Intent(VerificationActivity.this, Login.class));
            finish();
        });
    }
}