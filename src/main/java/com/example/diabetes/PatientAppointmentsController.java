package com.example.diabetes;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.List;

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

    @FXML
    private void handleBack() {
        try {
            UIUtils.switchScene(backButton, "patient_dashboard.fxml", "Patient Dashboard");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
