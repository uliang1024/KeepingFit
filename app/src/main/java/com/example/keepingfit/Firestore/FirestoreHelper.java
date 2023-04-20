package com.example.keepingfit.Firestore;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public class FirestoreHelper {

    private FirebaseFirestore db;

    public FirestoreHelper() {
        db = FirebaseFirestore.getInstance();
    }

    // 新增資料
    public void addData(String collectionName,String uid, FirestoreModel model, OnCompleteListener<Void> listener) {
        db.collection(collectionName)
                .document(uid)
                .set(model)
                .addOnCompleteListener(listener);
    }

    // 讀取資料
    public void readData(String collectionName, OnCompleteListener<QuerySnapshot> listener) {
        db.collection(collectionName)
                .get()
                .addOnCompleteListener(listener);
    }

    // 更新資料
    public void updateData(String collectionName, String documentId, Map<String, Object> updates, OnCompleteListener<Void> listener) {
        db.collection(collectionName)
                .document(documentId)
                .update(updates)
                .addOnCompleteListener(listener);
    }

    // 刪除資料
    public void deleteData(String collectionName, String documentId, OnCompleteListener<Void> listener) {
        db.collection(collectionName)
                .document(documentId)
                .delete()
                .addOnCompleteListener(listener);
    }
}