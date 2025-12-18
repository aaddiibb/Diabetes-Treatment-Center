package com.example.diabetes;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class RoleSelectionController {

    @FXML private Button patientButton;
    @FXML private Button doctorButton;

    @FXML
    private void handlePatientSelection() {
        openAuthChoice("PATIENT");
    }

    @FXML
    private void handleDoctorSelection() {
        openAuthChoice("DOCTOR");
    }

    private void openAuthChoice(String role) {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("auth_choice.fxml"));
            Parent root = loader.load();

            AuthChoiceController controller = loader.getController();
            controller.setRole(role);

            Stage stage = (Stage) patientButton.getScene().getWindow();
            Scene scene = new Scene(root, 800, 500);
            scene.getStylesheets().add(HelloApplication.class.getResource("styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Continue as " + role);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
