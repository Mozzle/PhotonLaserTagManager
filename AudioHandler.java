import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;

import java.util.ArrayList;

    /*-----------------------------------------------------------
     * 
     *  README
     * 
     *  The main methods you will use in this class are:
     *  
     *  loadAudio(String path) - loads an audio file, will fail if audio is already playing
     *  playAudio() - plays the loaded audio file
     *  stopAudio() - stops the currently playing audio file
     * 
     *  setVolume(int n) - sets the master volume of the program
     *  getVolume() - returns the master volume of the program
     * 
    ---------------------------------------------------------- */

public class AudioHandler
{
    public static int MASTER_VOLUME;
    private Clip currentAudio;
    private boolean isPlaying;

    // Hard-coded enums for audio tracks
    public static final int gethit = 0;
    public static final int hit = 1;
    public static final int hitown = 2;
    public static final int miss = 3;
    public static final int reset = 4;

    /// Create list of hard-coded paths for audio files
    public ArrayList<String> tracks;
    public ArrayList<String> sfx;

    /*-----------------------------------------------------------
     * 
     *  AudioHandler()
     * 
     *  DESCRIPTION: AudioHandler constructor.
     * 
    ---------------------------------------------------------- */
    public AudioHandler() {
        MASTER_VOLUME = 100;
        currentAudio = null;
        isPlaying = false;

        tracks = new ArrayList<>();
        sfx = new ArrayList<>();

        // Hard-coded paths for audio files
        for (int i = 0; i < 8; i++) {
            tracks.add("/src/photon_tracks/Track0" + (i+1) + ".wav");
        }

        sfx.add("/src/sfx/gethit.wav");
        sfx.add("/src/sfx/hit.wav");
        sfx.add("/src/sfx/hitown.wav");
        sfx.add("/src/sfx/miss.wav");
        sfx.add("/src/sfx/reset.wav");
    }

    /*-----------------------------------------------------------
     * 
     *  setVolume(int n)
     * 
     *  DESCRIPTION: Sets the master volume of the program.
     *  Returns true if value was successfully set to the parameter
     *  Returns false if the constraint was violated (0-100).
     * 
    ---------------------------------------------------------- */
    public boolean setVolume(int n) {
        if (n < 0) {
            MASTER_VOLUME = 0;
        }
        if (n > 100) {
            MASTER_VOLUME = 100;
        }
        MASTER_VOLUME = n;

        if (currentAudio != null) {
            FloatControl gainControl = (FloatControl) currentAudio.getControl(FloatControl.Type.MASTER_GAIN);
            double dB = (Math.log(MASTER_VOLUME / 100.0) / Math.log(10.0) * 20.0);
            gainControl.setValue((float) dB);
        }

        return n == MASTER_VOLUME;
    }

    /*-----------------------------------------------------------
     * 
     *  getVolume()
     * 
     *  DESCRIPTION: ...it just, gets the volume.
     * 
    ---------------------------------------------------------- */
    public int getVolume() {
        return MASTER_VOLUME;
    }

    /*-----------------------------------------------------------
     * 
     *  playAudio(String path)
     * 
     *  DESCRIPTION: Loads an audio file from the given path and plays it.
     *  Returns true if audio successfully begins playing
     *  Returns false if audio fails to play.
     * 
    ---------------------------------------------------------- */
    public boolean playAudio(String path) {

        try {
            AudioInputStream audio = AudioSystem.getAudioInputStream(getClass().getResource(path));
            currentAudio = AudioSystem.getClip();
            currentAudio.addLineListener(event -> {
                if(LineEvent.Type.STOP.equals(event.getType())) {
                currentAudio.close();
                }
            });
            currentAudio.open(audio);
            currentAudio.start();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        isPlaying = true;
        System.out.println("[AudioHandler] Playing " + currentAudio.toString());
        return true;
    }

    /*-----------------------------------------------------------
     * 
     *  stopAudio(String path)
     * 
     *  DESCRIPTION: Stops any currently playing audio.
     *  Returns true if audio was successfully killed
     *  Returns false if there is no audio to stop
     * 
    ---------------------------------------------------------- */
    public boolean stopAudio() {

        if (!isPlaying) {
            return false;
        }

        if (currentAudio != null) {
            currentAudio.stop();
            isPlaying = false;
            currentAudio.close();
            System.out.println("[AudioHandler] Stopping current track...");
            return true;
        }

        return false;
    }

}