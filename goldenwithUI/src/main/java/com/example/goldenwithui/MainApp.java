package com.example.goldenwithui;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Create the primary stage
        primaryStage.setTitle("JavaFX Button Click Example");

        // Create a button
        Button openButton = new Button("Open New Page");

        // Set up the event handler for the button's click event
      //  openButton.setOnAction(e -> openNewPage(primaryStage));

        // Create the layout and add the button
        StackPane root = new StackPane();
        root.getChildren().add(openButton);

        // Set the scene and show the primary stage
        primaryStage.setScene(new Scene(root, 300, 250));
        primaryStage.show();
    }

    // Method to open the new page (new scene)
    private void openNewPage(Stage primaryStage) {
        // Create a new stage for the new scene
        Stage newStage = new Stage();
        newStage.setTitle("New Page");

        // Create the layout for the new scene
        StackPane newRoot = new StackPane();
        newRoot.getChildren().add(new Button("This is a new page!"));

        // Set the scene for the new stage and show it
        newStage.setScene(new Scene(newRoot, 300, 200));
        newStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
