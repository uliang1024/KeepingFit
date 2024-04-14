package com.example.keepingfit.FastingPlan;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.keepingfit.Firestore.BodyCompositionHelper;
import com.example.keepingfit.Firestore.BodyCompositionModel;
import com.example.keepingfit.Firestore.FastingHelper;
import com.example.keepingfit.Firestore.FastingModel;
import com.example.keepingfit.MainActivity;
import com.example.keepingfit.R;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FastingTime extends AppCompatActivity {

    private TextView mShowSelectedDateText;

    long datestamp = 0, timestamp = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fasting_time);

        Button mPickDateButton = findViewById(R.id.pick_date_button);
        mShowSelectedDateText = findViewById(R.id.show_selected_date);
        Button pickTimeButton = findViewById(R.id.pick_time_button);
        TextView previewPickedTimeTextView = findViewById(R.id.preview_picked_time_textView);
        Button rightNowButton = findViewById(R.id.right_now_button);
        Button finishedButton = findViewById(R.id.finished_button);

        MaterialDatePicker.Builder<Long> materialDateBuilder = MaterialDatePicker.Builder.datePicker();

        materialDateBuilder.setTitleText("選擇開始日期");

        final MaterialDatePicker<Long> materialDatePicker = materialDateBuilder.build();

        mPickDateButton.setOnClickListener(
                v -> materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER"));

        materialDatePicker.addOnPositiveButtonClickListener(
                new MaterialPickerOnPositiveButtonClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onPositiveButtonClick(Object selection) {
                        mShowSelectedDateText.setText(materialDatePicker.getHeaderText());
                        datestamp = (long) selection;
                        Log.i(TAG, "onPositiveButtonClick: onPositiveButtonClick: " + datestamp);
                    }
                });

        // Handle the pick time button to open the time picker dialog
        pickTimeButton.setOnClickListener(view -> {
            MaterialTimePicker materialTimePicker = new MaterialTimePicker.Builder()
                    .setTitleText("選擇開始時間")
                    .setHour(12)
                    .setMinute(10)
                    .setTimeFormat(TimeFormat.CLOCK_12H)
                    .build();

            materialTimePicker.show(getSupportFragmentManager(), "MainActivity");

            materialTimePicker.addOnPositiveButtonClickListener(view1 -> {
                int pickedHour = materialTimePicker.getHour();
                int pickedMinute = materialTimePicker.getMinute();

                // 使用 SimpleDateFormat 格式化时间
                SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.US);
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, pickedHour);
                calendar.set(Calendar.MINUTE, pickedMinute);
                String formattedTime = timeFormat.format(calendar.getTime());

                // 获取时间戳
                timestamp = calendar.getTimeInMillis();

                Log.i(TAG, "timestamptimestamptimestamptimestamp"+ timestamp);

                // 更新 TextView
                previewPickedTimeTextView.setText(formattedTime);
            });
        });


        rightNowButton.setOnClickListener(view ->{
            // 获取当前时间戳
            long currentTimeMillis = System.currentTimeMillis();
            saveFirebase(currentTimeMillis);
        });

        finishedButton.setOnClickListener(view -> {

            if (datestamp != 0 && timestamp != 0) {
                Calendar dateCalendar = Calendar.getInstance();
                dateCalendar.setTimeInMillis(datestamp);

                Calendar timeCalendar = Calendar.getInstance();
                timeCalendar.setTimeInMillis(timestamp);

                // 将时间对象的时和分添加到日期对象
                dateCalendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY));
                dateCalendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));

                // 获取组合后的时间戳
                long combinedTimestamp = dateCalendar.getTimeInMillis();
                saveFirebase(combinedTimestamp);
            } else {
                Toast.makeText(this, "請設定好時間", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void saveFirebase(long startTime) {
        String method = getIntent().getStringExtra("method");

        FastingModel fastingModel = new FastingModel();
        fastingModel.setMethod(method);
        fastingModel.setHCombinedTimestamp(startTime);

        FastingHelper helper = new FastingHelper(this);

        helper.saveFastingLog(fastingModel);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}