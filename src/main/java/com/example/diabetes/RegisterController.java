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
    
    // Email requirement check label
    @FXML private Label gmailCheckLabel;
    
    // Password requirement check labels
    @FXML private Label minLengthCheckLabel;
    @FXML private Label capitalLetterCheckLabel;

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
        
        // Add real-time email validation listener
        if (emailField != null) {
            emailField.textProperty().addListener((obs, oldVal, newVal) -> {
                validateEmail(newVal);
            });
        }
        
        // Add real-time password validation listener
        if (passwordField != null) {
            passwordField.textProperty().addListener((obs, oldVal, newVal) -> {
                validatePasswordRequirements(newVal);
            });
        }
    }
    
    /**
     * Validates email and updates UI indicator
     */
    private void validateEmail(String email) {
        boolean isGmailValid = email.toLowerCase().contains("@gmail.com");
        
        if (gmailCheckLabel != null) {
            if (isGmailValid) {
                gmailCheckLabel.setText("✓");
                gmailCheckLabel.setStyle("-fx-font-size: 16; -fx-text-fill: #4caf50;");
            } else {
                gmailCheckLabel.setText("✗");
                gmailCheckLabel.setStyle("-fx-font-size: 16; -fx-text-fill: #d32f2f;");
            }
        }
    }
    
    /**
     * Checks if email is valid (must be gmail)
     */
    private boolean isEmailValid(String email) {
        return email.toLowerCase().contains("@gmail.com");
    }
    
    /**
     * Validates password requirements and updates UI indicators
     */
    private void validatePasswordRequirements(String password) {
        boolean hasMinLength = password.length() >= 8;
        boolean hasCapitalLetter = password.matches(".*[A-Z].*");
        
        // Update minimum length indicator
        if (minLengthCheckLabel != null) {
            if (hasMinLength) {
                minLengthCheckLabel.setText("✓");
                minLengthCheckLabel.setStyle("-fx-font-size: 16; -fx-text-fill: #4caf50;");
            } else {
                minLengthCheckLabel.setText("✗");
                minLengthCheckLabel.setStyle("-fx-font-size: 16; -fx-text-fill: #d32f2f;");
            }
        }
        
        // Update capital letter indicator
        if (capitalLetterCheckLabel != null) {
            if (hasCapitalLetter) {
                capitalLetterCheckLabel.setText("✓");
                capitalLetterCheckLabel.setStyle("-fx-font-size: 16; -fx-text-fill: #4caf50;");
            } else {
                capitalLetterCheckLabel.setText("✗");
                capitalLetterCheckLabel.setStyle("-fx-font-size: 16; -fx-text-fill: #d32f2f;");
            }
        }
    }
    
    /**
     * Checks if password meets all requirements
     */
    private boolean isPasswordValid(String password) {
        return password.length() >= 8 && password.matches(".*[A-Z].*");
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
            
            // Validate email format (must be gmail)
            if (!isEmailValid(email)) {
                UIUtils.showError("Validation Error", "Email must be a Gmail account (include @gmail.com).");
                return;
            }
            
            // Validate password requirements
            if (!isPasswordValid(pass)) {
                UIUtils.showError("Validation Error", "Password must be at least 8 characters and contain at least one capital letter.");
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
