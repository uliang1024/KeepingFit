package com.example.keepingfit.Firestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FastingModel {
    private long combinedTimestamp;
    private String method;
    private String status;
    private final Date date;

    public FastingModel() {
        this.combinedTimestamp = 0;
        this.method = "14/5";
        this.date = new Date();
        this.status = "active";
    }

    public FastingModel(long combinedTimestamp, String method, String status) {
        this.combinedTimestamp = combinedTimestamp;
        this.method = method;
        this.date = new Date();
        this.status = status;
    }

    public long getCombinedTimestamp() {
        return combinedTimestamp;
    }

    public void setHCombinedTimestamp(long combinedTimestamp) {
        this.combinedTimestamp = combinedTimestamp;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("combinedTimestamp", combinedTimestamp);
        map.put("method", method);
        map.put("createdAt", date);
        map.put("status", status);
        return map;
    }
}