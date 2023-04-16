package com.example.keepingfit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth fAuth;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fAuth = FirebaseAuth.getInstance();

        userId = fAuth.getCurrentUser().getUid();
        FirebaseUser user = fAuth.getCurrentUser();

        if (!user.isEmailVerified()) {
            startActivity(new Intent(this, VerificationActivity.class));
        }
    }

    public void louGout(View view) {
        fAuth.signOut();
        startActivity(new Intent(this, Login.class));
        finish();
    }
}