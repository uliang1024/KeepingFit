package com.example.keepingfit.FastingPlan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.keepingfit.R;

import soup.neumorphism.NeumorphCardView;

public class ChooseFasting extends AppCompatActivity {

    NeumorphCardView fastingMethod1, fastingMethod2, fastingMethod3, fastingMethod4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_fasting);

        fastingMethod1 = findViewById(R.id.fasting_method_1);
        fastingMethod2 = findViewById(R.id.fasting_method_2);
        fastingMethod3 = findViewById(R.id.fasting_method_3);
        fastingMethod4 = findViewById(R.id.fasting_method_4);

        fastingMethod1.setOnClickListener(view -> nextStep("14/5"));

        fastingMethod2.setOnClickListener(view -> nextStep("16/5"));

        fastingMethod3.setOnClickListener(view -> nextStep("18/5"));

        fastingMethod4.setOnClickListener(view -> nextStep("18/7"));
    }

    public void nextStep(String identifier) {
        Intent intent = new Intent(this, FastingTime.class);
        intent.putExtra("method", identifier);
        startActivity(intent);
    }
}