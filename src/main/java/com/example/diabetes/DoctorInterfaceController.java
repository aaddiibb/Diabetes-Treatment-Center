package com.example.diabetes;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class DoctorInterfaceController {

    @FXML private Label titleLabel;
    @FXML private Button appointmentsButton;

    @FXML
    private void handleAppointments() {
        try {
            UIUtils.switchScene(appointmentsButton, "doctor_appointments.fxml", "Doctor Appointments");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        SessionManager.getInstance().clear();
        try {
            UIUtils.switchScene(titleLabel, "role_selection.fxml", "Diabetes Prediction System");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
