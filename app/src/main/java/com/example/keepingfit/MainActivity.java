package com.example.keepingfit;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.keepingfit.Firestore.BodyCompositionHelper;
import com.example.keepingfit.Fragment.DashboardFragment;
import com.example.keepingfit.Fragment.HomeFragment;
import com.example.keepingfit.Fragment.PostFragment;
import com.example.keepingfit.Fragment.ProfileFragment;
import com.example.keepingfit.Fragment.RecipeFragment;
import com.example.keepingfit.unpublished.Login;
import com.example.keepingfit.user.BodyCompositionActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    BottomNavigationView bottomNavigationView;
    BodyCompositionHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottonnav);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        loadFragment(new HomeFragment());

        helper = new BodyCompositionHelper(this);
        helper.getLatestBodyCompositionData(bodyCompositionModel -> {
            if (bodyCompositionModel == null) {
                startActivity(new Intent(MainActivity.this, BodyCompositionActivity.class));
                finish();
            }
        });
    }

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        switch (item.getItemId()) {
            case R.id.navigation_home:
                fragment = new HomeFragment();
                break;
            case R.id.navigation_dashboard:
                fragment = new DashboardFragment();
                break;
            case R.id.navigation_recipe:
                fragment = new RecipeFragment();
                break;
            case R.id.navigation_post:
                fragment = new PostFragment();
                break;
            case R.id.navigation_profile:
                fragment = new ProfileFragment();
                break;
        }
        if (fragment != null) {
            loadFragment(fragment);
        }
        return true;
    }
    void loadFragment(Fragment fragment) {
        //to attach fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.relativelayout, fragment).commit();
    }

    public void louGout(View view) {
        mAuth.signOut();
        startActivity(new Intent(this, Login.class));
        finish();
    }

    public void resetPass(View view) {
    }
}