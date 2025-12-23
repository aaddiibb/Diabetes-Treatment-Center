package com.example.diabetes;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PredictionController {

    @FXML private TextField nameField;
    @FXML private TextField ageField;
    @FXML private TextField pregnanciesField;
    @FXML private TextField glucoseField;
    @FXML private TextField bloodPressureField;
    @FXML private TextField skinThicknessField;
    @FXML private TextField insulinField;
    @FXML private TextField bmiField;
    @FXML private TextField dpfField;
    
    @FXML private Button predictBtn;
    @FXML private Button clearBtn;
    @FXML private Button backBtn;
    
    @FXML private Label loadingLabel;
    @FXML private VBox resultContainer;

    private final DiabetesPredictionModel predictionModel = new DiabetesPredictionModel();

    @FXML
    private void handlePredict() {
        try {
            // Validate all fields
            if (!validateInputs()) {
                return;
            }

            // Disable buttons during prediction
            predictBtn.setDisable(true);
            loadingLabel.setVisible(true);

            // Get user input
            String name = nameField.getText().trim();
            int age = Integer.parseInt(ageField.getText().trim());
            int pregnancies = Integer.parseInt(pregnanciesField.getText().trim());
            double glucose = Double.parseDouble(glucoseField.getText().trim());
            double bloodPressure = Double.parseDouble(bloodPressureField.getText().trim());
            double skinThickness = Double.parseDouble(skinThicknessField.getText().trim());
            double insulin = Double.parseDouble(insulinField.getText().trim());
            double bmi = Double.parseDouble(bmiField.getText().trim());
            double dpf = Double.parseDouble(dpfField.getText().trim());

            // Make prediction
            PredictionResult result = predictionModel.predict(
                    pregnancies, glucose, bloodPressure, skinThickness, 
                    insulin, bmi, dpf, age
            );

            // Display results
            displayResults(name, result);

            // Save prediction to database
            savePredictionToDatabase(name, result);

        } catch (NumberFormatException e) {
            UIUtils.showError("Input Error", "Please enter valid numbers for all fields.");
            predictBtn.setDisable(false);
            loadingLabel.setVisible(false);
        } catch (Exception e) {
            e.printStackTrace();
            UIUtils.showError("Error", "Failed to make prediction: " + e.getMessage());
            predictBtn.setDisable(false);
            loadingLabel.setVisible(false);
        }
    }

    @FXML
    private void handleClear() {
        nameField.clear();
        ageField.clear();
        pregnanciesField.clear();
        glucoseField.clear();
        bloodPressureField.clear();
        skinThicknessField.clear();
        insulinField.clear();
        bmiField.clear();
        dpfField.clear();
        resultContainer.getChildren().clear();
        resultContainer.setVisible(false);
        loadingLabel.setVisible(false);
        predictBtn.setDisable(false);
    }

    @FXML
    private void handleBack() {
        try {
            UIUtils.switchScene(backBtn, "patient_dashboard.fxml", "Patient Dashboard");
        } catch (Exception e) {
            e.printStackTrace();
            UIUtils.showError("Navigation Error", String.valueOf(e));
        }
    }

    private boolean validateInputs() {
        if (nameField.getText().trim().isEmpty()) {
            UIUtils.showError("Validation Error", "Name is required.");
            return false;
        }

        try {
            int age = Integer.parseInt(ageField.getText().trim());
            if (age < 1 || age > 120) {
                UIUtils.showError("Validation Error", "Age must be between 1 and 120.");
                return false;
            }

            int pregnancies = Integer.parseInt(pregnanciesField.getText().trim());
            if (pregnancies < 0) {
                UIUtils.showError("Validation Error", "Pregnancies cannot be negative.");
                return false;
            }

            double glucose = Double.parseDouble(glucoseField.getText().trim());
            if (glucose < 0 || glucose > 1000) {
                UIUtils.showError("Validation Error", "Glucose must be between 0 and 1000 mg/dL.");
                return false;
            }

            double bloodPressure = Double.parseDouble(bloodPressureField.getText().trim());
            if (bloodPressure < 0 || bloodPressure > 300) {
                UIUtils.showError("Validation Error", "Blood Pressure must be between 0 and 300 mmHg.");
                return false;
            }

            double skinThickness = Double.parseDouble(skinThicknessField.getText().trim());
            if (skinThickness < 0 || skinThickness > 150) {
                UIUtils.showError("Validation Error", "Skin Thickness must be between 0 and 150 mm.");
                return false;
            }

            double insulin = Double.parseDouble(insulinField.getText().trim());
            if (insulin < 0 || insulin > 1000) {
                UIUtils.showError("Validation Error", "Insulin must be between 0 and 1000 μU/ml.");
                return false;
            }

            double bmi = Double.parseDouble(bmiField.getText().trim());
            if (bmi < 10 || bmi > 100) {
                UIUtils.showError("Validation Error", "BMI must be between 10 and 100 kg/m².");
                return false;
            }

            double dpf = Double.parseDouble(dpfField.getText().trim());
            if (dpf < 0 || dpf > 3) {
                UIUtils.showError("Validation Error", "Diabetes Pedigree Function must be between 0 and 3.");
                return false;
            }

            return true;
        } catch (NumberFormatException e) {
            UIUtils.showError("Input Error", "Please enter valid numbers for all fields.");
            return false;
        }
    }

    private void displayResults(String name, PredictionResult result) {
        resultContainer.getChildren().clear();

        // Title
        Label resultTitle = new Label("Prediction Results for " + name);
        resultTitle.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");

        // Risk Label
        Label riskLabel = new Label("Diabetes Risk: " + result.getRiskPercentage() + "%");
        riskLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: " 
                + (result.isDiabetic() ? "#d32f2f" : "#4caf50") + ";");

        // Prediction
        Label predictionLabel = new Label("Prediction: " + (result.isDiabetic() ? "HIGH RISK" : "LOW RISK"));
        predictionLabel.setStyle("-fx-font-size: 13; -fx-font-weight: bold; -fx-text-fill: "
                + (result.isDiabetic() ? "#d32f2f" : "#4caf50") + ";");

        // Recommendation
        Label recommendationLabel = new Label("Recommendation: " + result.getRecommendation());
        recommendationLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #333333; -fx-wrap-text: true;");
        recommendationLabel.setWrapText(true);

        // Separator
        Separator separator = new Separator();

        // Details
        Label detailsLabel = new Label("Input Summary:");
        detailsLabel.setStyle("-fx-font-size: 12; -fx-font-weight: bold; -fx-text-fill: #555555;");

        TextArea detailsArea = new TextArea();
        detailsArea.setEditable(false);
        detailsArea.setWrapText(true);
        detailsArea.setPrefRowCount(6);
        detailsArea.setText(result.getDetailsString());

        resultContainer.getChildren().addAll(resultTitle, riskLabel, predictionLabel, 
                recommendationLabel, separator, detailsLabel, detailsArea);
        resultContainer.setVisible(true);
        loadingLabel.setVisible(false);
        predictBtn.setDisable(false);
    }

    private void savePredictionToDatabase(String name, PredictionResult result) {
        try {
            // Get current user
            User currentUser = SessionManager.getInstance().getCurrentUser();
            if (currentUser != null) {
                System.out.println("DEBUG: Saving prediction for user: " + currentUser.getId());
                System.out.println("DEBUG: Risk Level: " + (result.isDiabetic() ? "HIGH" : "LOW"));
                System.out.println("DEBUG: Risk Percentage: " + result.getRiskPercentage());
                // In a real application, you would save this to a database
                // For now, we just log it
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
