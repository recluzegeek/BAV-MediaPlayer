package com.example.demo;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("hello-view.fxml")));

        Scene scene = new Scene(root);
        stage.setTitle("BAV MediaPlayer");
//        scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("main.css")).toExternalForm());
//        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("main.css")).toExternalForm());
        stage.setMinHeight(400);    ////These lines of codes set the min-height and min-width of the stage
        stage.setMinWidth(430);     ////Preventing them from collapsing.......


        scene.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
//                System.out.println("In the condiotin");
                stage.setFullScreen(!stage.isFullScreen());
            }
        });
//        HelloController.updateValues();
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}