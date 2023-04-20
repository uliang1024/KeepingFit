package com.example.keepingfit.Firestore;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;
import java.util.Map;

public interface FirestoreAdapter {
    // 創建新文檔，回傳創建的文檔 ID
    String createDocument(String collection, Map<String, Object> data);

    // 根據文檔 ID 讀取單個文檔
    DocumentSnapshot readDocument(String collection, String documentId);

    // 讀取指定集合的所有文檔
    List<DocumentSnapshot> readAllDocuments(String collection);

    // 根據文檔 ID 更新文檔
    void updateDocument(String collection, String documentId, Map<String, Object> data);

    // 根據文檔 ID 刪除文檔
    void deleteDocument(String collection, String documentId);
}