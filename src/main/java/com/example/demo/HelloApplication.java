package com.example.demo;

/*optimized imports*/

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/*Main Class from your Program will Run...*/

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("hello-view.fxml")));   /*hello-view.fxml is the file containing your GUI*/

        /*Setting the scene*/

        Scene scene = new Scene(root);

        /*Set the title of the window....*/

        stage.setTitle("BAV MediaPlayer");
//        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("main.css")).toExternalForm());  /*Uncomment this line to add your CSS file*/

        /*Setting the min-height && min-width of the stage so it doesn't collapsed*/

        stage.setMinHeight(400);
        stage.setMinWidth(430);

        /*Line of code to enter or exit from the fullscreen on double-clicking the mediaview*/

        scene.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                stage.setFullScreen(!stage.isFullScreen());
            }
        });
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}