package com.example.keepingfit.Firestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BodyCompositionModel {
    private float height;
    private float weight;
    private int age;
    private String gender;
    private float bodyFatPercentage;
    private float bmi;

    private final Date date;

    public BodyCompositionModel() {
        this.age = 0;
        this.gender = "";
        this.height = 0;
        this.weight = 0;
        this.bodyFatPercentage = 0;
        this.bmi = 0;
        this.date = new Date();
    }

    public BodyCompositionModel(float height, float weight, float bmi, float bodyFatPercentage, int age, String gender) {
        this.height = height;
        this.weight = weight;
        this.bmi = bmi;
        this.bodyFatPercentage = bodyFatPercentage;
        this.age = age;
        this.gender = gender;
        this.date = new Date();
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getBmi() {
        float heightInMeters = height / 100f; // 將身高從公分轉換為米
        bmi = weight / (heightInMeters * heightInMeters);
        return bmi;
    }

    public void setBmi(float bmi) {
        // 這個方法可以保留，因為我們不需要手動設置 BMI
    }

    public float getBodyFatPercentage() {
        return bodyFatPercentage;
    }

    public void setBodyFatPercentage(float bodyFatPercentage) {
        this.bodyFatPercentage = bodyFatPercentage;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("height", height);
        map.put("weight", weight);
        map.put("bmi", bmi);
        map.put("bodyFatPercentage", bodyFatPercentage);
        map.put("age", age);
        map.put("gender", gender);
        map.put("createdAt", date);
        return map;
    }
}