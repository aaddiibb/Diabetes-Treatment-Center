package com.example.diabetes;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class AuthChoiceController {

    @FXML private Label titleLabel;
    @FXML private Button loginButton;
    @FXML private Button registerButton;
    @FXML private Button backButton;

    private String role;

    public void setRole(String role) {
        this.role = role;
        if (titleLabel != null) titleLabel.setText("Continue as " + role);
    }

    @FXML
    private void handleLogin() {
        open("login.fxml", "Login - " + role, controller -> ((LoginController)controller).setRole(role));
    }

    @FXML
    private void handleRegister() {
        open("register.fxml", "Create Account - " + role, controller -> ((RegisterController)controller).setRole(role));
    }

    @FXML
    private void handleBack() {
        try {
            Stage stage = (Stage) backButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("role_selection.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 800, 500);
            scene.getStylesheets().add(HelloApplication.class.getResource("styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Diabetes Prediction System");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private interface ControllerConsumer { void accept(Object c); }

    private void open(String fxml, String title, ControllerConsumer consumer) {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource(fxml));
            Parent root = loader.load();
            consumer.accept(loader.getController());

            Stage stage = (Stage) backButton.getScene().getWindow();
            Scene scene = new Scene(root, 800, 500);
            scene.getStylesheets().add(HelloApplication.class.getResource("styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle(title);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
