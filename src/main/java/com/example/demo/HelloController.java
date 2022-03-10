package com.example.demo;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class HelloController {
    private static int recentFilesCount = 0;
    private Duration duration;
    @FXML
    private Label timeBar;
    @FXML
    private HBox mediaBar;
    @FXML
    private Label playTime;
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


            //Creating Bindings

            DoubleProperty widthProperty = mediaView.fitWidthProperty();
            DoubleProperty heightProperty = mediaView.fitHeightProperty();

            widthProperty.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
            heightProperty.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));

            volumeSlider.setValue(mediaPlayer.getVolume() * 100);
            volumeSlider.valueProperty().addListener(observable -> mediaPlayer.setVolume(volumeSlider.getValue() / 100));
//            volumeSlider.valueProperty().addListener(observable -> mediaPlayer.setVolume(volumeSlider.getVolume() / 100));

            mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> progressBar.setValue(newValue.toSeconds()));
            mediaPlayer.currentTimeProperty().addListener(new InvalidationListener() {
                public void invalidated(Observable ov) {
                    updateValues();
                }
            });
            progressBar.setOnMousePressed(event -> mediaPlayer.seek(Duration.seconds(progressBar.getValue())));
            progressBar.setOnMouseDragged(event1 -> mediaPlayer.seek(Duration.seconds(progressBar.getValue())));

            mediaPlayer.setOnReady(() -> {
                Duration total = media.getDuration();
                progressBar.setMax(total.toSeconds());
                updateValues();
            });
            mediaPlayer.play();
        }
    }

    protected void updateValues() {
        if (playTime != null && progressBar != null) {
            Platform.runLater(new Runnable() {
                public void run() {
                    Duration currentTime = mediaPlayer.getCurrentTime();
                    playTime.setText(formatTime(currentTime, duration));
                    progressBar.setDisable(duration.isUnknown());
                    if (!progressBar.isDisabled()
                            && duration.greaterThan(Duration.ZERO)
                            && !progressBar.isValueChanging()) {
                        progressBar.setValue(currentTime.divide(duration).toMillis()
                                * 100.0);
                    }
                }
            });
        }
    }

    private static String formatTime(Duration elapsed, Duration duration) {
        int intElapsed = (int) Math.floor(elapsed.toSeconds());
        int elapsedHours = intElapsed / (60 * 60);
        if (elapsedHours > 0) {
            intElapsed -= elapsedHours * 60 * 60;
        }
        int elapsedMinutes = intElapsed / 60;
        int elapsedSeconds = intElapsed - elapsedHours * 60 * 60
                - elapsedMinutes * 60;

        if (duration.greaterThan(Duration.ZERO)) {
            int intDuration = (int) Math.floor(duration.toSeconds());
            int durationHours = intDuration / (60 * 60);
            if (durationHours > 0) {
                intDuration -= durationHours * 60 * 60;
            }
            int durationMinutes = intDuration / 60;
            int durationSeconds = intDuration - durationHours * 60 * 60 -
                    durationMinutes * 60;
            if (durationHours > 0) {
                return String.format("%d:%02d:%02d/%d:%02d:%02d",
                        elapsedHours, elapsedMinutes, elapsedSeconds,
                        durationHours, durationMinutes, durationSeconds);
            } else {
                return String.format("%02d:%02d/%02d:%02d",
                        elapsedMinutes, elapsedSeconds, durationMinutes,
                        durationSeconds);
            }
        } else {
            if (elapsedHours > 0) {
                return String.format("%d:%02d:%02d", elapsedHours,
                        elapsedMinutes, elapsedSeconds);
            } else {
                return String.format("%02d:%02d", elapsedMinutes,
                        elapsedSeconds);
            }
        }
    }


    public void play_pauseVideo() {
        if (mediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING)) {
            mediaPlayer.pause();
        } else {
            mediaPlayer.play();
        }
    }

    public void stopVideo() {
        mediaPlayer.stop();
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