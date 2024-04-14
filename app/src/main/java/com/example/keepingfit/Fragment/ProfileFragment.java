package com.example.keepingfit.Fragment;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.keepingfit.BaseActivity;
import com.example.keepingfit.Firestore.BodyCompositionHelper;
import com.example.keepingfit.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Objects;

public class ProfileFragment extends Fragment implements EventListener<DocumentSnapshot> {

    private BaseActivity baseActivity;
    String OLD_PASS_REQUIRED = "Old password is required to reset password";
    String NEW_PASS_REQUIRED = "New password is required to reset password";
    String CONFIRM_PASS_REQUIRED = "Re-enter new password is required to reset password";
    private static final String USERS_COLLECTION = "users";
    TextView name, email, weightValue, heightValue, genderValue;
    ImageView userPhotoUrl;
    DisplayMetrics displayMetrics = new DisplayMetrics();
    BodyCompositionHelper helper;
    BottomSheetDialog resetPassDialog;
    private ViewGroup container;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
        if (value != null) {
            name.setText(value.getString("name"));
            email.setText(value.getString("email"));
            Glide.with(requireContext()).load(value.getString("photoUri")).into(userPhotoUrl);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Activity activity = requireActivity();

        if (activity instanceof BaseActivity) {
            baseActivity = (BaseActivity) activity;
        }

        if (baseActivity != null && baseActivity.user != null) {
            DocumentReference documentReference = baseActivity.mStore.collection(USERS_COLLECTION).document(baseActivity.userId);
            documentReference.addSnapshotListener(this);
        }
        helper = new BodyCompositionHelper(getContext());
        helper.getLatestBodyCompositionData(bodyCompositionModel -> {
            if (bodyCompositionModel != null) {
                float weight = bodyCompositionModel.getWeight();
                float height = bodyCompositionModel.getHeight();

                String weightText = String.valueOf(weight);
                String heightText = String.valueOf(height);
                weightValue.setText(weightText);
                heightValue.setText(heightText);
                genderValue.setText(bodyCompositionModel.getGender());
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        this.container = container;

        assert container != null;
        name = view.findViewById(R.id.userName);
        email = view.findViewById(R.id.email);
        userPhotoUrl = view.findViewById(R.id.userPhotoUrl);
        weightValue = view.findViewById(R.id.weightValue);
        heightValue = view.findViewById(R.id.heightValue);
        genderValue = view.findViewById(R.id.genderValue);
        ConstraintLayout resetPassBtn = view.findViewById(R.id.resetPassBtn);

        resetPassDialog = new BottomSheetDialog(container.getContext());
        createResetPassDialog();
        resetPassDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        WindowManager windowManager = requireActivity().getWindowManager();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        resetPassBtn.setOnClickListener(v -> {
            resetPassDialog.show();
        });

        // Inflate the layout for this fragment
        return view;
    }

    private void createResetPassDialog() {
        View view = null;
        if (container != null) {
            view = getLayoutInflater().inflate(R.layout.dialog_reset_pass, (ViewGroup) container, false);
        }

        assert view != null;
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
                Toast.makeText(getContext(), OLD_PASS_REQUIRED, Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(pass2)) {
                Toast.makeText(getContext(), NEW_PASS_REQUIRED, Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(pass3)) {
                Toast.makeText(getContext(), CONFIRM_PASS_REQUIRED, Toast.LENGTH_SHORT).show();
                return;
            }

            baseActivity.mAuth.signInWithEmailAndPassword(Objects.requireNonNull(baseActivity.user.getEmail()), pass1).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (!pass3.equals(pass2)) {
                        Toast.makeText(requireContext(),  "passwords do not match", Toast.LENGTH_SHORT).show();
                    } else {
                        baseActivity.user.updatePassword(pass2).addOnCompleteListener(task2 -> {
                            if (task2.isSuccessful()) {
                                Toast.makeText(requireContext(), "Password updated successfully", Toast.LENGTH_SHORT).show();
                                resetPassDialog.dismiss();
                            } else {
                                Toast.makeText(requireContext(), "Password update failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }else {
                    Toast.makeText(requireContext(), "password is incorrect !." , Toast.LENGTH_SHORT).show();
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