package com.example.keepingfit.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.keepingfit.FastingPlan.ChooseFasting;
import com.example.keepingfit.Firestore.FastingHelper;
import com.example.keepingfit.Firestore.FastingModel;
import com.example.keepingfit.R;

public class HomeFragment extends Fragment {

    Button start_button;
    TextView status;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FastingHelper helper = new FastingHelper(getContext());
        helper.getCurrentFastingPlan(new FastingHelper.OnFastingPlanCallback() {
            @Override
            public void onFastingPlanFound(FastingModel fastingModel) {
                start_button.setVisibility(View.GONE);
            }

            @Override
            public void onFastingPlanNotFound() {
                start_button.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFastingPlanError(String error) {
                // 處理錯誤情況
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        start_button = view.findViewById(R.id.start_button);

        start_button.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), ChooseFasting.class));
        });

        return view;
    }
}