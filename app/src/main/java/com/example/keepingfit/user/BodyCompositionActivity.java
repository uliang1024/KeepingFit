package com.example.keepingfit.user;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.keepingfit.BaseActivity;
import com.example.keepingfit.Firestore.BodyCompositionHelper;
import com.example.keepingfit.MainActivity;
import com.example.keepingfit.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Objects;

import soup.neumorphism.NeumorphCardView;
import soup.neumorphism.NeumorphFloatingActionButton;
import soup.neumorphism.ShapeType;

public class BodyCompositionActivity extends BaseActivity {

    BottomSheetDialog resetPassDialog;
    DisplayMetrics displayMetrics = new DisplayMetrics();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body_composition);

        resetPassDialog = new BottomSheetDialog(BodyCompositionActivity.this);
        createResetPassDialog();
        resetPassDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        resetPassDialog.show();

        BodyCompositionHelper helper = new BodyCompositionHelper(this);
        helper.getLatestBodyCompositionData(bodyCompositionModel -> {

        });




//        RecyclerView recyclerView = findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        BodyCompositionHelper helper = new BodyCompositionHelper(this);
//        helper.getLatestBodyCompositionData(bodyCompositionModel -> {
//            data = new ArrayList<>();
//            bodyCompositionModel.toMap().forEach((key, value) -> {
//                Log.d("key", "key:" + key + " value:" + value);
//                Map<String, String> item = new HashMap<>();
//                item.put("name", key);
//                item.put("value", String.valueOf(value));
//                data.add(item);
//            });
//
//            BodyCompositionAdapter adapter = new BodyCompositionAdapter(data);
//            recyclerView.setAdapter(adapter);
//        });
    }

    private void createResetPassDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_body_composition, findViewById(android.R.id.content), false);

        NeumorphCardView maleButton = view.findViewById(R.id.maleButton);
        NeumorphCardView femaleButton = view.findViewById(R.id.femaleButton);
//        EditText reNewPass = view.findViewById(R.id.reNewPassword);
//        TextView submit = view.findViewById(R.id.submit);

        maleButton.setOnClickListener(v -> {
            maleButton.setShapeType(ShapeType.PRESSED);
            femaleButton.setShapeType(ShapeType.FLAT);
        });
        femaleButton.setOnClickListener(v -> {
            maleButton.setShapeType(ShapeType.FLAT);
            femaleButton.setShapeType(ShapeType.PRESSED);
        });

//        submit.setOnClickListener(v -> {
//
//        });
        resetPassDialog.setOnDismissListener(dialogInterface -> {

        });

        resetPassDialog.setContentView(view);
    }

    @Override
    public void onStart() {
        super.onStart();
        // 拿到系统的 bottom_sheet
        View view = resetPassDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (view != null) {
            // 获取behavior
            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(view);
            // 设置弹出高度
            int screenHeight = displayMetrics.heightPixels;
            behavior.setPeekHeight(screenHeight, true);
        }
    }
}