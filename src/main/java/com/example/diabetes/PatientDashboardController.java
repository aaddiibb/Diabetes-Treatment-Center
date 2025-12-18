package com.example.diabetes;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class PatientDashboardController {

    @FXML private Label titleLabel;
    @FXML private Button bookAppointmentButton;
    @FXML private Button myAppointmentsButton;

    @FXML
    private void handleBookAppointment() {
        try {
            if (bookAppointmentButton == null) {
                UIUtils.showError("FXML Injection Error",
                        "bookAppointmentButton is null. Check fx:id in patient_dashboard.fxml");
                return;
            }

            // Debug: confirms resource exists
            var url = HelloApplication.class.getResource("doctors_list.fxml");
            if (url == null) {
                UIUtils.showError("Missing File",
                        "doctors_list.fxml not found in resources/com.example.diabetes/");
                return;
            }

            UIUtils.switchScene(bookAppointmentButton, "doctors_list.fxml", "Doctors List");
        } catch (Exception e) {
            e.printStackTrace();
            UIUtils.showError("Navigation Error", String.valueOf(e));
        }
    }

    @FXML
    private void handleMyAppointments() {
        try {
            UIUtils.switchScene(myAppointmentsButton, "patient_appointments.fxml", "My Appointments");
        } catch (Exception e) {
            e.printStackTrace();
            UIUtils.showError("Navigation Error", String.valueOf(e));
        }
    }

    @FXML
    private void handleLogout() {
        SessionManager.getInstance().clear();
        try {
            UIUtils.switchScene(titleLabel, "role_selection.fxml", "Diabetes Prediction System");
        } catch (Exception e) {
            e.printStackTrace();
            UIUtils.showError("Navigation Error", String.valueOf(e));
        }
    }
}
