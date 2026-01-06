package com.example.diabetes;

import java.time.LocalDateTime;

public class PredictionHistory {
    private int id;
    private int userId;
    private LocalDateTime predictionDate;
    private double riskPercentage;
    private String riskLevel;
    private int pregnancies;
    private double glucose;
    private double bloodPressure;
    private double skinThickness;
    private double insulin;
    private double bmi;
    private double dpf;
    private int age;
    private String notes;

    public PredictionHistory(int userId, LocalDateTime predictionDate, double riskPercentage,
                           String riskLevel, int pregnancies, double glucose, double bloodPressure,
                           double skinThickness, double insulin, double bmi, double dpf, int age) {
        this.userId = userId;
        this.predictionDate = predictionDate;
        this.riskPercentage = riskPercentage;
        this.riskLevel = riskLevel;
        this.pregnancies = pregnancies;
        this.glucose = glucose;
        this.bloodPressure = bloodPressure;
        this.skinThickness = skinThickness;
        this.insulin = insulin;
        this.bmi = bmi;
        this.dpf = dpf;
        this.age = age;
    }

    public PredictionHistory(int id, int userId, LocalDateTime predictionDate, double riskPercentage,
                           String riskLevel, int pregnancies, double glucose, double bloodPressure,
                           double skinThickness, double insulin, double bmi, double dpf, int age, String notes) {
        this(userId, predictionDate, riskPercentage, riskLevel, pregnancies, glucose, bloodPressure,
             skinThickness, insulin, bmi, dpf, age);
        this.id = id;
        this.notes = notes;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public LocalDateTime getPredictionDate() { return predictionDate; }
    public void setPredictionDate(LocalDateTime predictionDate) { this.predictionDate = predictionDate; }

    public double getRiskPercentage() { return riskPercentage; }
    public void setRiskPercentage(double riskPercentage) { this.riskPercentage = riskPercentage; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

    public int getPregnancies() { return pregnancies; }
    public void setPregnancies(int pregnancies) { this.pregnancies = pregnancies; }

    public double getGlucose() { return glucose; }
    public void setGlucose(double glucose) { this.glucose = glucose; }

    public double getBloodPressure() { return bloodPressure; }
    public void setBloodPressure(double bloodPressure) { this.bloodPressure = bloodPressure; }

    public double getSkinThickness() { return skinThickness; }
    public void setSkinThickness(double skinThickness) { this.skinThickness = skinThickness; }

    public double getInsulin() { return insulin; }
    public void setInsulin(double insulin) { this.insulin = insulin; }

    public double getBmi() { return bmi; }
    public void setBmi(double bmi) { this.bmi = bmi; }

    public double getDpf() { return dpf; }
    public void setDpf(double dpf) { this.dpf = dpf; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
