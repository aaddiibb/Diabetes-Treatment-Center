package com.example.diabetes;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Optional;

public class PatientAppointmentsController {

    @FXML private VBox appointmentsContainer;
    @FXML private VBox emptyState;
    @FXML private Button backButton;

    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final DoctorDAO doctorDAO = new DoctorDAO();

    @FXML
    public void initialize() {
        load();
    }

    private void load() {
        try {
            User me = SessionManager.getInstance().getCurrentUser();
            if (me == null) {
                emptyState.setVisible(true);
                appointmentsContainer.setVisible(false);
                return;
            }

            List<Appointment> list = appointmentDAO.findByPatientId(me.getId());

            if (list.isEmpty()) {
                emptyState.setVisible(true);
                appointmentsContainer.setVisible(false);
                return;
            }

            emptyState.setVisible(false);
            appointmentsContainer.setVisible(true);

            appointmentsContainer.getChildren().clear();
            for (Appointment a : list) {
                appointmentsContainer.getChildren().add(card(a));
            }
        } catch (Exception e) {
            e.printStackTrace();
            emptyState.setVisible(true);
            appointmentsContainer.setVisible(false);
        }
    }

    private VBox card(Appointment a) {
        VBox card = new VBox(10);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(16));

        Label status = new Label(a.getStatus());
        status.getStyleClass().add("badge");

        Label docName = new Label("Doctor: Unknown");
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(8);

        try {
            Doctor d = doctorDAO.findByUserId(a.getDoctorUserId());
            if (d != null) {
                docName.setText("Doctor: Dr. " + d.getName());
                addRow(grid, 0, "Specialty:", d.getSpecialty());
                addRow(grid, 1, "Location:", safe(d.getClinicAddress()));
            }
        } catch (Exception ignored) {}

        if (a.getScheduledAt() != null && !a.getScheduledAt().isBlank()) {
            addRow(grid, 2, "Scheduled:", a.getScheduledAt());
        }

        if (a.getPatientNote() != null && !a.getPatientNote().isBlank()) {
            Label n = new Label("Your note: " + a.getPatientNote());
            n.setWrapText(true);
            card.getChildren().addAll(status, docName, grid, n);
        } else {
            card.getChildren().addAll(status, docName, grid);
        }

        if (a.getDoctorNote() != null && !a.getDoctorNote().isBlank()) {
            Label dn = new Label("Doctor note: " + a.getDoctorNote());
            dn.setWrapText(true);
            card.getChildren().add(dn);
        }

        // Add cancel button for appointments that are not already completed or cancelled
        if (!"COMPLETED".equals(a.getStatus()) && !"CANCELLED".equals(a.getStatus())) {
            Button cancelBtn = new Button("Cancel Appointment");
            cancelBtn.getStyleClass().add("danger-button");
            cancelBtn.setPrefWidth(200);
            cancelBtn.setPrefHeight(40);
            cancelBtn.setOnAction(e -> handleCancelAppointment(a));

            HBox buttonBox = new HBox(cancelBtn);
            buttonBox.setPadding(new Insets(10, 0, 0, 0));
            card.getChildren().add(buttonBox);
        }

        return card;
    }

    private void addRow(GridPane g, int row, String k, String v) {
        Label key = new Label(k);
        key.getStyleClass().add("text-small");
        Label val = new Label(v);
        val.setWrapText(true);
        g.add(key, 0, row);
        g.add(val, 1, row);
    }

    private String safe(String s) { return (s == null || s.isBlank()) ? "-" : s; }

    private void handleCancelAppointment(Appointment a) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Cancel Appointment");
        confirmAlert.setHeaderText("Are you sure you want to cancel this appointment?");
        confirmAlert.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                appointmentDAO.cancelAppointment(a.getId());

                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Appointment Cancelled");
                successAlert.setHeaderText(null);
                successAlert.setContentText("Your appointment has been cancelled successfully.");
                successAlert.showAndWait();

                // Reload the appointments list
                load();
            } catch (Exception e) {
                e.printStackTrace();
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error");
                errorAlert.setHeaderText("Failed to cancel appointment");
                errorAlert.setContentText("An error occurred. Please try again.");
                errorAlert.showAndWait();
            }
        }
    }

    @FXML
    private void handleBack() {
        try {
            UIUtils.switchScene(backButton, "patient_dashboard.fxml", "Patient Dashboard");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

