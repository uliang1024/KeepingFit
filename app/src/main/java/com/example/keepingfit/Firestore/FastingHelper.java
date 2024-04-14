package com.example.keepingfit.Firestore;

import android.content.Context;
import android.util.Log;

import com.example.keepingfit.BaseActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class FastingHelper extends BaseActivity {

    private static final String TAG = "FastingHelper";
    private static final String USERS_COLLECTION = "users";
    private static final String FASTING_INFO_COLLECTION = "fastingInfo";
    private static final String TIMESTAMP_FIELD = "createdAt";
    FirebaseAuth mAuth;
    FirebaseFirestore mStore;
    FirebaseUser user;
    String userId;

    public FastingHelper(Context context) {
        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();
        assert user != null;
        userId = user.getUid();
    }

    public void saveFastingLog(FastingModel model) {
        Map<String, Object> data = model.toMap();

        mStore.collection(USERS_COLLECTION)
                .document(userId)
                .collection(FASTING_INFO_COLLECTION)
                .add(data)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Body composition data added with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding body composition data", e);
                });
    }

    public void getCurrentFastingPlan(OnFastingPlanCallback callback) {
        mStore.collection(USERS_COLLECTION)
                .document(userId)
                .collection(FASTING_INFO_COLLECTION)
                .whereEqualTo("status", "active")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // 找到了进行中的计划
                        FastingModel currentPlan = null;

                        for (FastingModel fastingModel : queryDocumentSnapshots.toObjects(FastingModel.class)) {
                            currentPlan = fastingModel;
                            break;
                        }

                        if (currentPlan != null) {
                            callback.onFastingPlanFound(currentPlan);
                        } else {
                            callback.onFastingPlanNotFound();
                        }
                    } else {
                        // 未找到进行中的计划
                        callback.onFastingPlanNotFound();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting fasting plan", e);
                    callback.onFastingPlanError(e.getMessage());
                });
    }

    public interface OnFastingPlanCallback {
        void onFastingPlanFound(FastingModel fastingModel);
        void onFastingPlanNotFound();
        void onFastingPlanError(String error);
    }
}