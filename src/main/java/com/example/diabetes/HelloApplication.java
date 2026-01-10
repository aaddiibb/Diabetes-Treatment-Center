package com.example.diabetes;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // ✅ Initialize DB once at app start
        DatabaseManager.getInstance().initialize();

        // Set application icon
        try {
            Image icon = new Image(getClass().getResourceAsStream("logo.png"));
            stage.getIcons().add(icon);
        } catch (Exception e) {
            System.err.println("Could not load application icon: " + e.getMessage());
        }

        FXMLLoader fxmlLoader = new FXMLLoader(
                HelloApplication.class.getResource("role_selection.fxml")
        );

        Scene scene = new Scene(fxmlLoader.load(), 800, 500);
        scene.getStylesheets().add(
                HelloApplication.class.getResource("styles.css").toExternalForm()
        );

        stage.setTitle("Diabetes Treatment Center");
        stage.setScene(scene);

        stage.setOnCloseRequest(

                e -> DatabaseManager.getInstance().close());
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
