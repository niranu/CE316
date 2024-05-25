package com.example.ce316;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class HelpController {

    @FXML
    private Parent root;
    private Stage stage;
    private Scene scene;


    @FXML
    private Button prevButton;

    @FXML
    private Button nextButton;

    @FXML
    private StackPane imageStackPane;

    private ImageView[] images;
    private int currentIndex;

    public void initialize() {
        // Load images
        Image image1 = new Image(getClass().getResourceAsStream("/com/example/ce316/helpImages/CreateProjectHelp.jpg"));
        Image image2 = new Image(getClass().getResourceAsStream("/com/example/ce316/helpImages/EditProjectHelp.jpg"));
        Image image3 = new Image(getClass().getResourceAsStream("/com/example/ce316/helpImages/AssignmentReportsHelp.jpg"));
        Image image4 = new Image(getClass().getResourceAsStream("/com/example/ce316/helpImages/ConfigHelp.jpg"));

        // Initialize array of images
        images = new ImageView[]{new ImageView(image1), new ImageView(image2), new ImageView(image3), new ImageView(image4)};

        // Set initial image
        imageStackPane.getChildren().add(images[0]);
        currentIndex = 0;

        // Set button actions
        prevButton.setOnAction(event -> showPreviousImage());
        nextButton.setOnAction(event -> showNextImage());
    }


    private void showPreviousImage() {
        if (currentIndex > 0) {
            currentIndex--;
            updateImage();
        }
    }

    private void showNextImage() {
        if (currentIndex < images.length - 1) {
            currentIndex++;
            updateImage();
        }
    }

    private void updateImage() {
        imageStackPane.getChildren().clear();
        imageStackPane.getChildren().add(images[currentIndex]);
    }
    public void setBackButton(ActionEvent e) throws IOException {
        root = FXMLLoader.load(getClass().getResource("hello-view.fxml"));
        stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


}