package com.example.diabetes;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class AppointmentBookingController {

    @FXML private Label doctorNameLabel;
    @FXML private Label doctorSpecialtyLabel;
    @FXML private Label doctorClinicLabel;
    @FXML private Label doctorAvailabilityLabel;
    @FXML private TextArea patientNotesArea;
    @FXML private Button cancelButton;
    @FXML private Button bookButton;

    private Doctor selectedDoctor;
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();

    public void setDoctor(Doctor doctor) {
        this.selectedDoctor = doctor;
        if (doctor == null) return;
        doctorNameLabel.setText(doctor.getName());
        doctorSpecialtyLabel.setText(doctor.getSpecialty());
        doctorClinicLabel.setText(doctor.getClinicAddress());
        doctorAvailabilityLabel.setText(doctor.getAvailableDays());
    }

    @FXML
    private void handleBook() {
        try {
            if (selectedDoctor == null) {
                UIUtils.showError("Error", "No doctor selected");
                return;
            }

            User me = SessionManager.getInstance().getCurrentUser();
            if (me == null) {
                UIUtils.showError("Error", "Please login first.");
                return;
            }

            Appointment a = new Appointment();
            a.setPatientUserId(me.getId());
            a.setDoctorUserId(selectedDoctor.getUserId());
            a.setStatus("REQUESTED");
            a.setPatientNote(patientNotesArea.getText().trim());

            appointmentDAO.create(a);

            UIUtils.showInfo("Success",
                    "Appointment request sent!\n\nDr. " + selectedDoctor.getName() +
                            " will review and schedule it. Check 'My Appointments'.");

            handleCancel();

        } catch (Exception e) {
            e.printStackTrace();
            UIUtils.showError("Error", "Failed to book: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        try {
            UIUtils.switchScene(cancelButton, "doctors_list.fxml", "Doctors List");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
