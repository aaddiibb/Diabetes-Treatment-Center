package com.example.diabetes;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class UIUtils {

    public static void showInfo(String title, String message) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }

    public static void showError(String title, String message) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }

    public static FXMLLoader switchScene(Node anyNodeOnCurrentScene, String fxml, String title) throws Exception {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource(fxml));
            Parent root = loader.load();

            Stage stage = (Stage) anyNodeOnCurrentScene.getScene().getWindow();
            Scene scene = new Scene(root, 800, 500);
            scene.getStylesheets().add(HelloApplication.class.getResource("styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle(title);
            stage.show();
            return loader;

        } catch (Exception e) {
            e.printStackTrace();

            Throwable root = e;
            while (root.getCause() != null) root = root.getCause();

            showError("FXML Load Failed",
                    "Failed to load: " + fxml + "\n\nRoot cause:\n" + root.getClass().getName() + "\n" + root.getMessage());

            throw e;
        }
    }

}
