package com.example.diabetes;

public class PredictionResult {
    private boolean diabetic;
    private double riskPercentage;
    private double[] inputValues;
    private String[] fieldNames;

    public PredictionResult(boolean diabetic, double riskPercentage, double[] inputValues) {
        this.diabetic = diabetic;
        this.riskPercentage = riskPercentage;
        this.inputValues = inputValues;
        this.fieldNames = new String[]{
                "Pregnancies", "Glucose", "Blood Pressure", "Skin Thickness",
                "Insulin", "BMI", "DPF", "Age"
        };
    }

    public boolean isDiabetic() {
        return diabetic;
    }

    public double getRiskPercentage() {
        return Math.round(riskPercentage * 100.0) / 100.0;
    }

    public String getRecommendation() {
        if (diabetic) {
            return "High risk detected! Please consult with a healthcare professional immediately. " +
                   "Monitor your blood glucose regularly and consider lifestyle modifications.";
        } else {
            return "Your risk is relatively low. Continue maintaining a healthy lifestyle with " +
                   "regular exercise and balanced nutrition. Regular check-ups are recommended.";
        }
    }

    public String getDetailsString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fieldNames.length; i++) {
            if (i < inputValues.length) {
                sb.append(String.format("%s: %.2f\n", fieldNames[i], inputValues[i]));
            }
        }
        sb.append(String.format("\nPrediction Confidence: %.2f%%", riskPercentage));
        return sb.toString();
    }
}
