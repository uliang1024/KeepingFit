package com.example.keepingfit.Firestore;

import android.content.Context;
import android.util.Log;

import com.example.keepingfit.BaseActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class BodyCompositionHelper extends BaseActivity {

    private static final String TAG = "BodyCompositionHelper";
    private static final String USERS_COLLECTION = "users";
    private static final String BODY_INFO_COLLECTION = "bodyInfo";
    private static final String TIMESTAMP_FIELD = "createdAt";
    FirebaseAuth mAuth;
    FirebaseFirestore mStore;
    FirebaseUser user;
    String userId;

    public BodyCompositionHelper(Context context) {
        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();
        assert user != null;
        userId = user.getUid();
    }

    public void saveBodyComposition(BodyCompositionModel model) {
        Map<String, Object> data = model.toMap();

        mStore.collection(USERS_COLLECTION)
                .document(userId)
                .collection(BODY_INFO_COLLECTION)
                .add(data)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Body composition data added with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding body composition data", e);
                });
    }

    public void getLatestBodyCompositionData(BodyCompositionListener listener) {
        mStore.collection(USERS_COLLECTION)
                .document(userId)
                .collection(BODY_INFO_COLLECTION)
                .orderBy(TIMESTAMP_FIELD, com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.size() > 0) {
                        DocumentReference documentReference = queryDocumentSnapshots.getDocuments().get(0).getReference();
                        documentReference.get().addOnSuccessListener(documentSnapshot -> {
                            BodyCompositionModel bodyCompositionModel = documentSnapshot.toObject(BodyCompositionModel.class);
                            if (bodyCompositionModel != null) {
                                listener.onDataReceived(bodyCompositionModel);
                            }
                        });
                    } else {
                        listener.onDataReceived(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting latest body composition data", e);
                });
    }

    public interface BodyCompositionListener {
        void onDataReceived(BodyCompositionModel data);
    }
}