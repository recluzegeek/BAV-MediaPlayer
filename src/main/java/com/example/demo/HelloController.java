/*This is dev-changeAudioOnScrolling branch, and we'll focus on beta-feature of
 * changing the audio while the mouse scrolls within the scene....
 * */

package com.example.demo;

/*optimized imports for the file*/

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class HelloController {
    /*Path to the file where all the recent media will be placed...*/
    private final String recentTextFilepath = "src\\main\\java\\com\\example\\demo\\recent-media.txt";
    @FXML
    private Label showVolumeButton;
    @FXML
    private MenuItem exitButton;
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

/*Choose File method for selecting file from the directory using FileChooser Class
    Then ready the file to be played and some other methods to be performed while initializing the MediaPlayer*/

    public void chooseFile() throws IOException {
        /*Sets the initial directory to the Videos like on Windows it'll be C:\Users\<user-name>\Videos*/
        File file1 = new File(System.getProperty("user.home"), "\\Videos");
        FileChooser fileChooser = new FileChooser();
        configureFileChooser(fileChooser, file1);
        File file = fileChooser.showOpenDialog(null);
        String path = file.toURI().toString(); /*To get the path of the file selected by the user.*/
        System.out.println("Path: " + path);

        /*FileWriter obj to write recent media to
         * txt file and can clear the file on demand...
         * */

        FileWriter fw = new FileWriter(recentTextFilepath, true);
        fw.write(path + "\n");
        fw.close();

        /*To stop a video, when clicked on OpenFile Button while playing a media file*/
        // stop previous media player and clean up
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.setOnPaused(null);
            mediaPlayer.setOnPlaying(null);
            mediaPlayer.setOnReady(null);
        }

        /*if the path contains something other than null, then ready the MediaPlayer...*/

        if (path != null) {
            Media media = new Media(path);
            mediaPlayer = new MediaPlayer(media);
            mediaView.setMediaPlayer(mediaPlayer);

            //Creating Bindings for the media to resize with the resizing of the window

            DoubleProperty widthProperty = mediaView.fitWidthProperty();
            DoubleProperty heightProperty = mediaView.fitHeightProperty();

            widthProperty.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
            heightProperty.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));

            mediaView.setPreserveRatio(true);     /*This line sets the scaling of the video....*/

            /*Setting the VolumeSlider of the Player
             * first line sets the slider and the next line has a listener which listens for any changes on the slider
             * and then sets the volume of the player according to the value set by the user...
             * */
//                This line sets the default value of the volume and set it to 70
            volumeSlider.setValue(mediaPlayer.getVolume() * 70);
//                  This line listens for any changes on the volumeSlider via the mouse-clicks and sets the value of the volumeSlider...
            volumeSlider.valueProperty().addListener(observable -> mediaPlayer.setVolume(volumeSlider.getValue() / 100));
//                  This line listens for the changes on the volumeSlider and displays its value on the showVolumeButton Label.....
            volumeSlider.valueProperty().addListener(observable -> showVolumeButton.setText(Double.toString((int) volumeSlider.getValue()) + "%"));

            /*Under Development Portion...This Portion has been completed🤩🤩🤩😀😀😏😑😑😜 */
            /*--------------------------------------------------------------------------------------------------------------------*/
            /*This code block listens for any Mouse Scrolling and then sets the volume according to the scrolling level*/

            volumeSlider.addEventHandler(ScrollEvent.SCROLL, event -> {
                /*Movement variable captures how much mouse has Scrolled and then divides it by 4 to get an avg value of 8-9
                 * which means whenever user scrolls he'll experience a volume change of 8-9 units depending on the scrolling
                 * either positive or negative....
                 */

                double movement = event.getDeltaY() / 4;
//               Make changes to the volume by adding the movement variable to the current value of the volumeSlider....
                int volume = (int) (volumeSlider.getValue() + movement);
//                If the value of the volume is greater than 100, make it equals to 100,
//                else if the value is negative make it equal to 0....
                if (volume < 0) {
                    volume = 0;
                } else if (volume > 100) {
                    volume = 100;
                }
                mediaPlayer.setVolume(volume);      /*Sets the value of the volume to the Media*/
                volumeSlider.adjustValue(volume);   /*Sets the value of the Volume to the volumeSlider*/
                showVolumeButton.setText(Double.toString(volume) + "%");/*Displays the value of the volume to the showVolumeButton Label*/
                System.out.println("Volume: " + volume);
            });

            /*--------------------------------------------------------------------------------------------------------------------*/

            /*Setting shortcut for the application which obviously didn't work in the first try...
             * will work on this latter on....Leaving here as it is, so I won't forget about this feature....
             * */
            exitButton.setAccelerator(new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN));

            /*This line sets the progress-bar...Increases as the video plays. Min value is 0 and Max value is set to the duration of the video*/

            mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> progressBar.setValue(newValue.toSeconds()));
            /*This line sets the new value of the progressBar whenever mouse-button is pressed on to the progressbar*/
            progressBar.setOnMousePressed(event1 -> mediaPlayer.seek(Duration.seconds(progressBar.getValue())));
            /*This line sets the new value of the progressBar whenever mouse-button is dragged over the progressbar*/
            progressBar.setOnMouseDragged(event -> mediaPlayer.seek(Duration.seconds(progressBar.getValue())));

            /*Sets the elapsed time which is a Label on the Player GUI with the fx:id elapsedTime and a totalDuration
             * which are calculated in the getTimeString() method...and then set their values using listener.
             * */

            mediaPlayer.currentTimeProperty().addListener(ov -> {
                if (!progressBar.isValueChanging()) {
                    double total = mediaPlayer.getTotalDuration().toMillis();
                    double current = mediaPlayer.getCurrentTime().toMillis();
                    elapsedTime.setText(getTimeString(current));
                    totalDuration.setText(getTimeString(total));
                }
            });

            /*After all checks, we're ready to launch our media player*/

            mediaPlayer.setOnReady(() -> {
                Duration total = media.getDuration();
                progressBar.setMax(total.toSeconds());

            });

            /*Plays the Media captured by the user...*/
            mediaPlayer.play();
        }
    }

    /*Method to set the initial directory of the FileChooser to the Videos Directory
     * means whenever fileChooser dialog appears, it should already be in the somewhat
     * specified directory like in this example it is set to open to C:\Users\<user-name>\Videos
     *You can set it to the Music directory of the user by C:\Users\<user-name>\Music
     * System.getProperty("user.home") gives us the user-directory
     */

    private static void configureFileChooser(final FileChooser fileChooser, File file1) {
        fileChooser.setTitle("Select File to Play");
        fileChooser.setInitialDirectory(file1);
    }

    /*Method to calculate the Elapsed Time of the Media...HH:MM:SS format*/

    public String getTimeString(double millis) {
        millis /= 1000;
        String s = formatTime(millis % 60);
        millis /= 60;
        String m = formatTime(millis % 60);
        millis /= 60;
        String h = formatTime(millis % 24);
        return h + ":" + m + ":" + s;
    }

    /*For formatting the time (Elapsed + Total)*/

    private String formatTime(double time) {
        int t = (int) time;
        if (t > 9) {
            return String.valueOf(t);
        }
        return "0" + t;
    }

    /*Method to clear recent played media items from the file..*/

    public void clearRecentFile() throws IOException {
        FileWriter writer = new FileWriter(recentTextFilepath);
        writer.write("");
        writer.close();
        System.out.println("Cleared all recent media");
    }

    /*Method to be called whenever Play-Pause button is clicked...Uses if-else to switch the signs for play-pause*/

    public void play_pauseVideo() {
        if (mediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING)) {
            playButton.setText("||");
            mediaPlayer.pause();
        } else {
            playButton.setText(">>");
            mediaPlayer.play();
        }
    }

    /*Close the Program*/

    public void closeProgram() {
        Platform.exit();
    }

    /*Method to get call whenever you want to skip your media...Pass the value as an argument and enter the
     * double argument to the method
     */

    public void skip() {
        mediaPlayer.seek(mediaPlayer.getCurrentTime().add(javafx.util.Duration.seconds(5)));
//        mediaPlayer.seek(mediaPlayer.getCurrentTime().add(javafx.util.Duration.seconds(-5))); /*Just pass the desired amount of time in the arguments and pass it*/
    }

    /*Method to set Rate of the Media Playback...
     * 1 is normal(default), 0.5 is slower, 1.5 is faster
     * pass a double argument to changes its value dynamically
     * */

    public void setRate() {
        mediaPlayer.setRate(1.5);
//        mediaPlayer.setRate(0.5);
    }
}