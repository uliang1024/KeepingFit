package com.example.keepingfit;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.keepingfit.Firestore.BodyCompositionHelper;
import com.example.keepingfit.unpublished.Login;
import com.example.keepingfit.user.BodyCompositionActivity;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentReference;

import java.util.Objects;

public class MainActivity extends BaseActivity {

    String OLD_PASS_REQUIRED = "Old password is required to reset password";
    String NEW_PASS_REQUIRED = "New password is required to reset password";
    String CONFIRM_PASS_REQUIRED = "Re-enter new password is required to reset password";
    private static final String USERS_COLLECTION = "users";
    TextView name, email;
    ImageView userPhotoUrl;
    BottomSheetDialog resetPassDialog;
    DisplayMetrics displayMetrics = new DisplayMetrics();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = findViewById(R.id.userName);
        email = findViewById(R.id.email);
        userPhotoUrl = findViewById(R.id.userPhotoUrl);

        DocumentReference documentReference = mStore.collection(USERS_COLLECTION).document(userId);
        documentReference.addSnapshotListener(this, (value, error) -> {
            if (value!= null) {
                name.setText(value.getString("name"));
                email.setText(value.getString("email"));
                Glide.with(this).load(value.getString("photoUri")).into(userPhotoUrl);
            }
        });

        BodyCompositionHelper helper = new BodyCompositionHelper(this);
        helper.getLatestBodyCompositionData(bodyCompositionModel -> {
            if (bodyCompositionModel == null) {
                startActivity(new Intent(MainActivity.this, BodyCompositionActivity.class));
                finish();
            }
        });

        resetPassDialog = new BottomSheetDialog(MainActivity.this);
        createResetPassDialog();
        resetPassDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
    }
    private void createResetPassDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_reset_pass, findViewById(android.R.id.content), false);

        EditText oldPass = view.findViewById(R.id.oldPassword);
        EditText newPass = view.findViewById(R.id.newPassword);
        EditText reNewPass = view.findViewById(R.id.reNewPassword);
        ImageView closeButton = view.findViewById(R.id.close_button);
        TextView submit = view.findViewById(R.id.submit);

        submit.setOnClickListener(v -> {

            String pass1 = oldPass.getText().toString().trim();
            String pass2 = newPass.getText().toString().trim();
            String pass3 = reNewPass.getText().toString().trim();

            if (TextUtils.isEmpty(pass1)) {
                Toast.makeText(MainActivity.this, OLD_PASS_REQUIRED, Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(pass2)) {
                Toast.makeText(MainActivity.this, NEW_PASS_REQUIRED, Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(pass3)) {
                Toast.makeText(MainActivity.this, CONFIRM_PASS_REQUIRED, Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(Objects.requireNonNull(user.getEmail()), pass1).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (!pass3.equals(pass2)) {
                        Toast.makeText(MainActivity.this,  "passwords do not match", Toast.LENGTH_SHORT).show();
                    } else {
                        user.updatePassword(pass2).addOnCompleteListener(task2 -> {
                            if (task2.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                                resetPassDialog.dismiss();
                            } else {
                                Toast.makeText(MainActivity.this, "Password update failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }else {
                    Toast.makeText(MainActivity.this, "password is incorrect !." , Toast.LENGTH_SHORT).show();
                }
            });


        });

        closeButton.setOnClickListener(v -> resetPassDialog.dismiss());

        resetPassDialog.setOnDismissListener(dialogInterface -> {
            //清除EditText的內容
            oldPass.setText("");
            newPass.setText("");
            reNewPass.setText("");
        });

        resetPassDialog.setContentView(view);
    }
    public void resetPass(View view) {
        resetPassDialog.show();
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

    public void louGout(View view) {
        mAuth.signOut();
        startActivity(new Intent(this, Login.class));
        finish();
    }
}