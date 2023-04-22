package com.example.keepingfit;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.keepingfit.unpublished.Login;
import com.example.keepingfit.unpublished.VerificationActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class BaseActivity extends AppCompatActivity {
    protected FirebaseAuth mAuth;
    protected FirebaseFirestore mStore;
    protected FirebaseUser user;
    protected String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();

        if (user != null) {
            if (!user.isEmailVerified()) {
                userId = user.getUid();
                startActivity(new Intent(this, VerificationActivity.class));
                finish();
            } else {
                userId = user.getUid();
            }
        } else {
            startActivity(new Intent(this, Login.class));
            finish();
        }
    }
}