package com.example.ce316;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        // Load FXML
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 700, 500);

        // Set the icon
        Image applicationIcon = new Image(getClass().getResourceAsStream("/Image/Logo.png"));
        stage.getIcons().add(applicationIcon);

        // Set up the stage
        stage.setTitle("Integrated Assignment Environment");
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
