package com.example.keepingfit;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
        userId = fAuth.getCurrentUser().getUid();
        FirebaseUser user = fAuth.getCurrentUser();

        sendCode.setOnClickListener(v -> {
            user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(VerificationActivity.this, "Verification Email Sent.", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@org.jetbrains.annotations.NotNull Exception e) {
                    Log.d("tag", "onFailure: Email not sent " + e.getMessage());
                }
            });
        });

        loginBtn.setOnClickListener(v -> {
            startActivity(new Intent(VerificationActivity.this, Login.class));
            finish();
        });
    }
}