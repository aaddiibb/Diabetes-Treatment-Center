package com.example.diabetes;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {

    private String role; // PATIENT or DOCTOR

    @FXML private Label titleLabel;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Hyperlink backLink;

    private final UserDAO userDAO = new UserDAO();

    public void setRole(String role) {
        this.role = role;
        if (titleLabel != null) titleLabel.setText(role.equals("PATIENT") ? "Patient Login" : "Doctor Login");
    }

    @FXML
    private void handleLogin() {
        try {
            String email = emailField.getText().trim();
            String password = passwordField.getText();

            if (email.isEmpty() || password.isEmpty()) {
                UIUtils.showError("Validation Error", "Please enter both email and password.");
                return;
            }

            System.out.println("DEBUG: Attempting login for email: " + email);
            User user = userDAO.authenticate(email, password);
            if (user == null) {
                System.out.println("DEBUG: Login failed - user not found or password incorrect");
                UIUtils.showError("Login Failed", "Invalid email or password.");
                return;
            }
            System.out.println("DEBUG: Login successful for user: " + user.getName() + " (Role: " + user.getRole() + ")");

            if (role != null && !role.equals(user.getRole())) {
                UIUtils.showError("Role Mismatch", "You selected " + role + " but this account is " + user.getRole() + ".");
                return;
            }

            SessionManager.getInstance().setCurrentUser(user);

            if ("PATIENT".equals(user.getRole())) {
                UIUtils.switchScene(loginButton, "patient_dashboard.fxml", "Patient Dashboard");
            } else {
                UIUtils.switchScene(loginButton, "doctor_interface.fxml", "Doctor Dashboard");
            }
        } catch (Exception e) {
            e.printStackTrace();
            UIUtils.showError("Error", "Something went wrong: " + e.getMessage());
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
