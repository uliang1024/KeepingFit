package com.example.keepingfit.Firestore;

import android.net.Uri;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FirestoreModel {
    private String name;
    private String email;
    private Uri photoUri;

    public FirestoreModel() {}

    public FirestoreModel(String name, String email, Uri photoUri) {
        this.name = name;
        this.email = email;
        this.photoUri = photoUri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Uri getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(Uri photoUri) {
        this.photoUri = photoUri;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("name", name == null ? "" : name);
        result.put("email", email == null ? "" : email);
        result.put("photoUri", photoUri == null ? "" : photoUri);
        return result;
    }
}