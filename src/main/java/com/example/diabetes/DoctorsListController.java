package com.example.diabetes;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class DoctorsListController {

    @FXML private VBox doctorsContainer;
    @FXML private ComboBox<String> specialtyFilter;
    @FXML private Button backButton;

    private DoctorDAO doctorDAO; // <-- NOT final, not created here

    @FXML
    public void initialize() {
        try {
            // Safety check (if injection fails you'll know immediately)
            if (specialtyFilter == null || doctorsContainer == null || backButton == null) {
                UIUtils.showError("FXML Injection Error",
                        "One or more @FXML fields are null. Check fx:id in doctors_list.fxml");
                return;
            }

            // Create DAO INSIDE initialize (so any error is caught and shown)
            doctorDAO = new DoctorDAO();

            specialtyFilter.getItems().setAll(
                    "All Specialties", "Endocrinologist", "Diabetologist", "General Physician", "Cardiologist"
            );
            specialtyFilter.setValue("All Specialties");
            specialtyFilter.setOnAction(e -> loadDoctors());

            loadDoctors();

        } catch (Exception e) {
            e.printStackTrace();
            UIUtils.showError("Doctors Screen Failed To Load",
                    e.getClass().getName() + "\n" + e.getMessage());
        }
    }

    private void loadDoctors() {
        try {
            List<Doctor> doctors = doctorDAO.findAll();
            String selected = specialtyFilter.getValue();

            if (selected != null && !"All Specialties".equals(selected)) {
                doctors = doctors.stream()
                        .filter(d -> selected.equals(d.getSpecialty()))
                        .toList();
            }

            doctorsContainer.getChildren().clear();

            if (doctors.isEmpty()) {
                Label l = new Label("No doctors found");
                l.getStyleClass().add("subtitle");
                doctorsContainer.getChildren().add(l);
                return;
            }

            for (Doctor d : doctors) doctorsContainer.getChildren().add(createDoctorCard(d));

        } catch (Exception e) {
            e.printStackTrace();
            UIUtils.showError("Error", "Failed to load doctors: " + e.getMessage());
        }
    }

    private VBox createDoctorCard(Doctor doctor) {
        VBox card = new VBox(8);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(16));

        Label name = new Label(doctor.getName());
        name.getStyleClass().add("title-medium");

        Label spec = new Label(doctor.getSpecialty());
        spec.getStyleClass().addAll("text-small", "bright-text");

        Label clinic = new Label("Clinic: " + safe(doctor.getClinicAddress()));
        clinic.getStyleClass().add("bright-text");
        
        Label days = new Label("Available: " + safe(doctor.getAvailableDays()));
        days.getStyleClass().add("bright-text");
        
        Label email = new Label("Email: " + safe(doctor.getEmail()));
        email.getStyleClass().add("text-small");

        Button book = new Button("Book Appointment");
        book.getStyleClass().add("primary-button");
        book.setOnAction(e -> openBooking(doctor));

        HBox actions = new HBox(book);
        actions.setAlignment(Pos.CENTER_RIGHT);

        card.getChildren().addAll(name, spec, clinic, days, email, actions);
        return card;
    }

    private void openBooking(Doctor doctor) {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("appointment_booking.fxml"));
            Parent root = loader.load();

            AppointmentBookingController controller = loader.getController();
            controller.setDoctor(doctor);

            Stage stage = (Stage) backButton.getScene().getWindow();
            Scene scene = new Scene(root, 800, 500);
            scene.getStylesheets().add(HelloApplication.class.getResource("styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Book Appointment");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            UIUtils.showError("Error", "Failed to open booking: " + e.getMessage());
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

    private String safe(String s) { return (s == null || s.isBlank()) ? "-" : s; }
}
