package com.example.keepingfit.user;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.keepingfit.BaseActivity;
import com.example.keepingfit.Firestore.BodyCompositionHelper;
import com.example.keepingfit.Firestore.BodyCompositionModel;
import com.example.keepingfit.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Calendar;

import soup.neumorphism.NeumorphCardView;
import soup.neumorphism.ShapeType;

public class BodyCompositionActivity extends BaseActivity {

    BottomSheetDialog bodyMetricsDialog;
    BodyCompositionHelper helper;
    DisplayMetrics displayMetrics = new DisplayMetrics();
    private int year, month, day;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body_composition);

        bodyMetricsDialog = new BottomSheetDialog(BodyCompositionActivity.this);
        createBodyMetricsDialog();
        bodyMetricsDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        bodyMetricsDialog.show();

        helper = new BodyCompositionHelper(this);
        helper.getLatestBodyCompositionData(bodyCompositionModel -> {

        });
    }

    private TextView datePickerButton;
    private Button sportsButton;
    private final int[] selectedChoice = {-1};
    String gender = null, selectedDate = null;
    float sports = 0;
    int age = 0;

    private void createBodyMetricsDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_body_composition, findViewById(android.R.id.content), false);

        NeumorphCardView maleButton = view.findViewById(R.id.maleButton);
        NeumorphCardView femaleButton = view.findViewById(R.id.femaleButton);
        datePickerButton = view.findViewById(R.id.date_picker_button);
        sportsButton = view.findViewById(R.id.sports_button);
        TextView confirmButton = view.findViewById(R.id.confirm_button);
        EditText heightButton = view.findViewById(R.id.height_button);
        EditText weightButton = view.findViewById(R.id.weight_button);

        maleButton.setOnClickListener(v -> {
            maleButton.setShapeType(ShapeType.PRESSED);
            femaleButton.setShapeType(ShapeType.FLAT);
            gender = "male";
        });
        femaleButton.setOnClickListener(v -> {
            maleButton.setShapeType(ShapeType.FLAT);
            femaleButton.setShapeType(ShapeType.PRESSED);
            gender = "female";
        });


        datePickerButton.setOnClickListener(v -> {
            showDatePicker();
        });

        sportsButton.setOnClickListener(v -> {
            showSportsPicker();
        });

        confirmButton.setOnClickListener(v -> {
            // 取得輸入的身高體重字串
            String heightText = heightButton.getText().toString();
            String weightText = weightButton.getText().toString();
            if (!TextUtils.isEmpty(heightText) && !TextUtils.isEmpty(weightText)) {
                if (gender != null) {
                    // 設定年齡（確保 age 大於1）
                    if (age > 1) {
                        // 設定運動水平（確保 sports 大於0）
                        if (sports > 0) {
                            // 創建一個 BodyCompositionModel 的實例並設置相關屬性
                            BodyCompositionModel bodyComposition = new BodyCompositionModel();
                            // 設定身高
                            bodyComposition.setHeight(Float.parseFloat(heightText));
                            // 設定體重
                            bodyComposition.setWeight(Float.parseFloat(weightText));
                            bodyComposition.setGender(gender);
                            bodyComposition.setAge(age);
                            bodyComposition.setSports(sports);
                            float bmi = bodyComposition.getBmi();
                            bodyComposition.setBmi(bmi);

                            // 調用 saveBodyComposition 方法，將 BodyCompositionModel 物件保存到 Firestore
                            helper.saveBodyComposition(bodyComposition);

                            bodyMetricsDialog.dismiss();
                        } else {
                            // 處理運動程度小於等於0的情況
                            Toast.makeText(this, "請選擇活動程度", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // 處理年齡小於等於1的情況
                        Toast.makeText(this, "選擇出生年月日", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // 處理 gender 為 null 的情況
                    Toast.makeText(this, "請選性別", Toast.LENGTH_SHORT).show();
                }
            } else {
                // 處理身高或體重為空的情況
                Toast.makeText(this, "請輸入身高體重", Toast.LENGTH_SHORT).show();
            }
        });

        bodyMetricsDialog.setOnDismissListener(dialogInterface -> {

        });

        bodyMetricsDialog.setContentView(view);
    }

    private void showDatePicker() {
        // 取得目前日期
        Calendar currentCalendar = Calendar.getInstance();
        int currentYear = currentCalendar.get(Calendar.YEAR);
        int currentMonth = currentCalendar.get(Calendar.MONTH) + 1; // 月從0開始，因此需要加1
        int currentDay = currentCalendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                R.style.MyDatePickerDialog,
                (DatePickerDialog.OnDateSetListener) (view, year, month, dayOfMonth) -> {
                    // 處理日期選擇
                    // 將選取的日期設定為datePickerButton的文本
                    selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth; // 月份加1因为月份从0开始
                    datePickerButton.setText(selectedDate);

                    // 计算年龄
                    age = currentYear - year; // 计算年份差异
                    if (currentMonth < month + 1 || (currentMonth == month + 1 && currentDay < dayOfMonth)) {
                        age--; // 如果还没过生日，年龄减1
                    }
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void showSportsPicker() {
        String[] sportNames = {"整天坐著、躺著", "坐著工作, 沒有在運動(1.3)", "坐著工作, 輕微的運動(1.4)", "適度的體力工作, 沒有在運動(1.5)", "適度的體力工作, 輕微的運動(1.6)", "適度的體力工作, 劇烈的運動(1.7)", "繁重的工作/劇烈的運動(1.8)", "超過平均的體力工作/運動(2.0)"};
        float[] sportValues = {1.2f,1.3f,1.4f,1.5f,1.6f,1.7f,1.8f,2.0f};
        new MaterialAlertDialogBuilder(this)
                .setTitle("活動程度")
                .setNeutralButton("取消重選", (dialog, which) -> {
                    selectedChoice[0] = -1; // 修改数组中的元素
                    sportsButton.setText("活動程度");
                })
                .setPositiveButton("確定", (dialog, which) -> {
                    if (selectedChoice[0] == -1) {
                        // 用户尚未选择任何选项
                        Toast.makeText(this, "尚未选择", Toast.LENGTH_SHORT).show();
                    } else {
                        // 用户选择了选项
                        sportsButton.setText(sportNames[selectedChoice[0]]);
                        sports = sportValues[selectedChoice[0]];
                        sports = Math.round(sports * 10.0f) / 10.0f;
                    }
                })
                .setSingleChoiceItems(sportNames, selectedChoice[0], (dialog, which) -> {
                    selectedChoice[0] = which; // 修改数组中的元素
                })
                .show();
    }

    @Override
    public void onStart() {
        super.onStart();
        // 拿到系统的 bottom_sheet
        View view = bodyMetricsDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (view != null) {
            // 获取behavior
            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(view);
            // 设置弹出高度
            int screenHeight = displayMetrics.heightPixels;
            behavior.setPeekHeight(screenHeight, true);
        }
    }
}