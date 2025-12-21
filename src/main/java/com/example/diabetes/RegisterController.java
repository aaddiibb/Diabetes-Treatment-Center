package com.example.diabetes;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RegisterController {

    private String role; // PATIENT / DOCTOR

    @FXML private Label titleLabel;

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;

    // Doctor-only section
    @FXML private VBox doctorFieldsBox;
    @FXML private TextField specialtyField;
    @FXML private TextField clinicField;
    @FXML private TextField availableDaysField;

    @FXML private Button registerButton;
    @FXML private Hyperlink backLink;

    private final UserDAO userDAO = new UserDAO();
    private final DoctorDAO doctorDAO = new DoctorDAO();

    public void setRole(String role) {
        this.role = role;
        if (titleLabel != null) titleLabel.setText(role.equals("PATIENT") ? "Create Patient Account" : "Create Doctor Account");
        if (doctorFieldsBox != null) doctorFieldsBox.setManaged("DOCTOR".equals(role));
        if (doctorFieldsBox != null) doctorFieldsBox.setVisible("DOCTOR".equals(role));
    }

    @FXML
    private void initialize() {
        // default hidden until role is set
        if (doctorFieldsBox != null) {
            doctorFieldsBox.setManaged(false);
            doctorFieldsBox.setVisible(false);
        }
    }

    @FXML
    private void handleRegister() {
        try {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String pass = passwordField.getText();
            String confirm = confirmPasswordField.getText();

            if (name.isEmpty() || email.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
                UIUtils.showError("Validation Error", "Please fill all required fields.");
                return;
            }
            if (!pass.equals(confirm)) {
                UIUtils.showError("Validation Error", "Passwords do not match.");
                return;
            }

            if (userDAO.findByEmail(email) != null) {
                UIUtils.showError("Validation Error", "Email already exists.");
                return;
            }

            User u = new User();
            u.setRole(role);
            u.setName(name);
            u.setEmail(email);
            u.setPhone(phone);
            u.setPasswordHash(PasswordUtils.hashForStorage(pass));

            u = userDAO.create(u);
            
            System.out.println("DEBUG: User created with ID: " + u.getId());

            if ("DOCTOR".equals(role)) {
                String specialty = specialtyField.getText().trim();
                if (specialty.isEmpty()) {
                    UIUtils.showError("Validation Error", "Doctor specialty is required.");
                    return;
                }
                System.out.println("DEBUG: Creating doctor profile for user ID: " + u.getId());
                doctorDAO.createDoctorProfile(
                        u.getId(),
                        specialty,
                        clinicField.getText().trim(),
                        availableDaysField.getText().trim()
                );
            }

            SessionManager.getInstance().setCurrentUser(u);

            if ("PATIENT".equals(role)) {
                UIUtils.switchScene(registerButton, "patient_dashboard.fxml", "Patient Dashboard");
            } else {
                UIUtils.switchScene(registerButton, "doctor_interface.fxml", "Doctor Dashboard");
            }

        } catch (Exception e) {
            e.printStackTrace();
            UIUtils.showError("Error", "Failed to create account: " + e.getMessage());
        }
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("auth_choice.fxml"));
            Parent root = loader.load();
            AuthChoiceController controller = loader.getController();
            controller.setRole(role);

            Stage stage = (Stage) backLink.getScene().getWindow();
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
