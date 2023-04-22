package com.example.keepingfit.Firestore;

import static android.content.ContentValues.TAG;

import android.util.Log;

import com.example.keepingfit.BaseActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public class FirestoreHelper extends BaseActivity {
    FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private static final String USERS_COLLECTION = "users";

    public FirestoreHelper() {}

    // 新增資料
    public void addData(String uid, FirestoreModel model) {
        Map<String, Object> data = model.toMap();
        mStore.collection(USERS_COLLECTION)
                .document(uid)
                .set(data)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "User information added with ID: " + uid);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "錯", e);
                });
    }

    // 讀取資料
    public void readData(OnCompleteListener<QuerySnapshot> listener) {
        mStore.collection(USERS_COLLECTION)
                .get()
                .addOnCompleteListener(listener);
    }

    // 更新資料
    public void updateData(Map<String, Object> updates, OnCompleteListener<Void> listener) {
        mStore.collection(USERS_COLLECTION)
                .document(userId)
                .update(updates)
                .addOnCompleteListener(listener);
    }

    // 刪除資料
    public void deleteData(OnCompleteListener<Void> listener) {
        mStore.collection(USERS_COLLECTION)
                .document(userId)
                .delete()
                .addOnCompleteListener(listener);
    }
}