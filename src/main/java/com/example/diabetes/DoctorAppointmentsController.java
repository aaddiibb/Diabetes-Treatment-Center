package com.example.diabetes;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class DoctorAppointmentsController {

    @FXML private VBox appointmentsContainer;
    @FXML private VBox emptyState;
    @FXML private ToggleButton requestedTab;
    @FXML private ToggleButton scheduledTab;
    @FXML private ToggleButton completedTab;
    @FXML private ToggleButton allTab;
    @FXML private Button backButton;
    @FXML private Button refreshButton;

    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final UserDAO userDAO = new UserDAO();

    private String filter = "REQUESTED";

    @FXML
    public void initialize() {
        load();
    }

    @FXML
    private void handleFilterChange() {
        if (requestedTab.isSelected()) filter = "REQUESTED";
        else if (scheduledTab.isSelected()) filter = "SCHEDULED";
        else if (completedTab.isSelected()) filter = "COMPLETED";
        else filter = null;
        load();
    }

    @FXML
    private void handleRefresh() {
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

            List<Appointment> list = (filter == null)
                    ? appointmentDAO.findByDoctorId(me.getId())
                    : appointmentDAO.findByDoctorIdAndStatus(me.getId(), filter);

            if (list.isEmpty()) {
                emptyState.setVisible(true);
                appointmentsContainer.setVisible(false);
                return;
            }

            emptyState.setVisible(false);
            appointmentsContainer.setVisible(true);

            appointmentsContainer.getChildren().clear();
            for (Appointment a : list) appointmentsContainer.getChildren().add(card(a));

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

        String patientName = "Unknown";
        try {
            User p = userDAO.findById(a.getPatientUserId());
            if (p != null) patientName = p.getName() + " (" + p.getEmail() + ")";
        } catch (Exception ignored) {}

        Label patient = new Label("Patient: " + patientName);

        card.getChildren().addAll(status, patient);

        if (a.getPatientNote() != null && !a.getPatientNote().isBlank()) {
            Label note = new Label("Patient note: " + a.getPatientNote());
            note.setWrapText(true);
            card.getChildren().add(note);
        }

        if (a.getScheduledAt() != null && !a.getScheduledAt().isBlank()) {
            card.getChildren().add(new Label("Scheduled: " + a.getScheduledAt()));
        }

        if ("REQUESTED".equals(a.getStatus())) {
            Button schedule = new Button("Schedule");
            schedule.getStyleClass().add("primary-button");
            schedule.setOnAction(e -> schedule(a));

            Button decline = new Button("Decline");
            decline.getStyleClass().add("secondary-button");
            decline.setOnAction(e -> decline(a));

            HBox actions = new HBox(10, schedule, decline);
            card.getChildren().add(actions);
        }

        if ("SCHEDULED".equals(a.getStatus())) {
            Button complete = new Button("Mark Completed");
            complete.getStyleClass().add("primary-button");
            complete.setOnAction(e -> complete(a));
            card.getChildren().add(complete);
        }

        return card;
    }

    private void schedule(Appointment a) {
        TextInputDialog d = new TextInputDialog();
        d.setTitle("Schedule Appointment");
        d.setHeaderText("Enter date & time");
        d.setContentText("Example: 2025-12-15 10:00 AM");
        d.showAndWait().ifPresent(time -> {
            try {
                appointmentDAO.updateStatus(a.getId(), "SCHEDULED", time.trim(), null);
                UIUtils.showInfo("Success", "Appointment scheduled.");
                load();
            } catch (Exception e) {
                e.printStackTrace();
                UIUtils.showError("Error", "Failed to schedule: " + e.getMessage());
            }
        });
    }

    private void decline(Appointment a) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Decline Appointment");
        alert.setHeaderText("Decline this request?");
        alert.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    appointmentDAO.updateStatus(a.getId(), "CANCELLED", null, "Declined by doctor");
                    UIUtils.showInfo("Success", "Declined.");
                    load();
                } catch (Exception e) {
                    e.printStackTrace();
                    UIUtils.showError("Error", "Failed to decline: " + e.getMessage());
                }
            }
        });
    }

    private void complete(Appointment a) {
        TextInputDialog d = new TextInputDialog();
        d.setTitle("Complete Appointment");
        d.setHeaderText("Add doctor note (optional)");
        d.setContentText("Note:");
        d.showAndWait().ifPresent(note -> {
            try {
                appointmentDAO.updateStatus(a.getId(), "COMPLETED", a.getScheduledAt(), note.trim());
                UIUtils.showInfo("Success", "Marked as completed.");
                load();
            } catch (Exception e) {
                e.printStackTrace();
                UIUtils.showError("Error", "Failed to complete: " + e.getMessage());
            }
        });
    }

    @FXML
    private void handleBack() {
        try {
            UIUtils.switchScene(backButton, "doctor_interface.fxml", "Doctor Dashboard");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
