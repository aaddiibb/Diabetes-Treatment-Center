package com.example.diabetes;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // ✅ Initialize DB once at app start
        DatabaseManager.getInstance().initialize();

        FXMLLoader fxmlLoader = new FXMLLoader(
                HelloApplication.class.getResource("role_selection.fxml")
        );

        Scene scene = new Scene(fxmlLoader.load(), 800, 500);
        scene.getStylesheets().add(
                HelloApplication.class.getResource("styles.css").toExternalForm()
        );

        stage.setTitle("Diabetes Prediction System");
        stage.setScene(scene);

        stage.setOnCloseRequest(e -> DatabaseManager.getInstance().close());
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
