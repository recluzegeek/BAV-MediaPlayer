package com.example.demo;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

public class HelloController {
    @FXML
    private Button playButton;
    @FXML
    private Label elapsedTime;
    @FXML
    private Label totalDuration;

    @FXML
    private MediaPlayer mediaPlayer;
    @FXML
    private MediaView mediaView;

    @FXML
    private Slider volumeSlider;

    @FXML
    private Slider progressBar;

    public void chooseFile() throws IOException {
        FileChooser fileChooser = new FileChooser();    //FileChooser obj to navigate between directories and choose a file...
        File file = fileChooser.showOpenDialog(null);
        String path = file.toURI().toString(); ///To get the path of the file selected by the user,,,,
        System.out.println("Path: " + path);
//        File recentFiles = new File(".\\src\\main\\java\\com.example.beatsaudiovisualizer\\recent-media.txt");
        File recentFiles = new File("src\\main\\java\\com\\example\\demo\\recent-media.txt");
        FileWriter fw = new FileWriter(recentFiles, true);
        fw.write(path + "\n");
        fw.close();

        if (path != null) {    ///if the path contains something other than null pointing....
            Media media = new Media(path);
            mediaPlayer = new MediaPlayer(media);
            mediaView.setMediaPlayer(mediaPlayer);
            if (Objects.requireNonNull(mediaPlayer).getStatus().equals(MediaPlayer.Status.PLAYING)) {
                mediaPlayer.stop();
                System.out.println("\nVideo Stopped...!");
            }

            //Creating Bindings

            DoubleProperty widthProperty = mediaView.fitWidthProperty();
            DoubleProperty heightProperty = mediaView.fitHeightProperty();

            widthProperty.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
            heightProperty.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));
//            mediaView.setPreserveRatio(true);

            volumeSlider.setValue(mediaPlayer.getVolume() * 100);
            volumeSlider.valueProperty().addListener(observable -> mediaPlayer.setVolume(volumeSlider.getValue() / 100));

            mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> progressBar.setValue(newValue.toSeconds()));
            progressBar.setOnMousePressed(event -> mediaPlayer.seek(Duration.seconds(progressBar.getValue())));
            progressBar.setOnMouseDragged(event1 -> mediaPlayer.seek(Duration.seconds(progressBar.getValue())));

            mediaPlayer.currentTimeProperty().addListener(ov -> {
                if (!progressBar.isValueChanging()) {
                    double total = mediaPlayer.getTotalDuration().toMillis();
                    double current = mediaPlayer.getCurrentTime().toMillis();
                    elapsedTime.setText(getTimeString(current));
                    totalDuration.setText(getTimeString(total));
                }
            });


            mediaPlayer.setOnReady(() -> {
                Duration total = media.getDuration();
                progressBar.setMax(total.toSeconds());

            });
            mediaPlayer.play();
        }
    }

    public String getTimeString(double millis) {
        millis /= 1000;
        String s = formatTime(millis % 60);
        millis /= 60;
        String m = formatTime(millis % 60);
        millis /= 60;
        String h = formatTime(millis % 24);
        return h + ":" + m + ":" + s;
    }

    private String formatTime(double time) {
        int t = (int) time;
        if (t > 9) {
            return String.valueOf(t);
        }
        return "0" + t;
    }


    public void play_pauseVideo() {
        if (mediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING)) {
            mediaPlayer.pause();
            playButton.setText(">>");
        } else {
            mediaPlayer.play();
            playButton.setText("||");
        }
    }

    private void stopPlaying() {
        if (Objects.requireNonNull(mediaPlayer).getStatus().equals(MediaPlayer.Status.PLAYING)) {
            mediaPlayer.stop();
            mediaPlayer = null;
            System.out.println("\nVideo Stopped....!!!");
        }
    }


    public void skip5() {
        mediaPlayer.seek(mediaPlayer.getCurrentTime().add(javafx.util.Duration.seconds(5)));
    }

    public void furtherSpeedUpVideo() {
        mediaPlayer.setRate(1.5);
    }

    public void back5() {
        mediaPlayer.seek(mediaPlayer.getCurrentTime().add(javafx.util.Duration.seconds(-5)));
    }

    public void furtherSlowDownVideo() {
        mediaPlayer.setRate(0.5);
    }
}