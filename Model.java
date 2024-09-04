
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import java.lang.Math;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.*;
import java.awt.event.*;

public class Model
{
    //enums in Java are utterly useless, so here is my C-like enum:
    public static final int INITIALIZE = 0;
    public static final int SPLASH_SCREEN = 1;
    public static final int PLAYER_ENTRY_SCREEN = 2;
    //...
    public static final int NUM_SCREENS = 4;

    public int system_State;

    public ArrayList<Sprite> windowObjects; // This arrayList contains all elements that
                                            //  will be drawn to the scrren

    public ArrayList<TextBox> PlayerIDBoxes;    // For player entry screen
    public ArrayList<TextBox> EquipmentIDBoxes; // For player entry screen

    public Timer timer;
    public TimerTask splashScreenTimeoutTask;

    /*-----------------------------------------------------
     * 
     *      SplashScreenTimeout
     * 
     *  DESCRIPTION: This class is used as a callback for
     *  a timer that controls how long the splash screen
     *  is displayed on screen. When the timer ends,
     *  the Player Entry Screen is initialized, then it is
     *  drawn in the View.update() function.
     * 
     ----------------------------------------------------*/
    public class SplashScreenTimeout extends TimerTask
    {
        // Ran when the splash screen timer ends.
        public void run()
        {
            windowObjects.remove(0);
            for (int i = 0; i < 20; i++) {
                PlayerIDBoxes.add(new TextBox("", 12, Model.this, TextBox.NUMERIC_TEXT_FIELD_TYPE));
                EquipmentIDBoxes.add(new TextBox("", 8, Model.this,TextBox.NUMERIC_TEXT_FIELD_TYPE));
            }
            for (int i = 0; i < 20; i++) {
                PlayerIDBoxes.add(new TextBox("", 12, Model.this,TextBox.NUMERIC_TEXT_FIELD_TYPE));
                EquipmentIDBoxes.add(new TextBox("", 8, Model.this,TextBox.NUMERIC_TEXT_FIELD_TYPE));
            }
            system_State = PLAYER_ENTRY_SCREEN;
        }
    }



    /*-------------------------------------------------
     *
     *      Model()
     *
     *  DESCRIPTION: Model Class Initializer
     *
     *  REQUIREMENTS:
     *
    ------------------------------------------------- */
    public Model()
    {
        system_State = INITIALIZE;
        // Initialization
        windowObjects = new ArrayList<Sprite>();
        PlayerIDBoxes = new ArrayList<TextBox>();
        EquipmentIDBoxes = new ArrayList<TextBox>();

        // Start a timer, go to the splash screen, wait for 3.2 seconds,
        // then go to SplashScreenTimeout.run() to go to the player entry
        // screen
        timer = new Timer();
        splashScreenTimeoutTask = new SplashScreenTimeout();
        startSplashScreen();
        system_State = SPLASH_SCREEN;
        timer.schedule(splashScreenTimeoutTask, 3200);
        
    }

    /*-------------------------------------------------
     *
     *      update()
     *
     *  DESCRIPTION: Updates all data for the program,
     *  outside of graphical elements. May be necessary
     *  to have a parameter passed into this function
     *  from where it is called in Controller
     *
     *  REQUIREMENTS:
     *
    ------------------------------------------------- */
    public void update()
    {
        for (int i = 0; i < getNumWindowObjects(); i++) {
            windowObjects.get(i).update();
        }

        switch(system_State) {
            case SPLASH_SCREEN:

                break;
            case PLAYER_ENTRY_SCREEN:

                break;

            default:
                break;
        }

    }

    public int getNumWindowObjects() {
		return windowObjects.size();
	}

    public Sprite getWindowObjectAt(int i) {
		return windowObjects.get(i);
	}

    public void startSplashScreen() {
        windowObjects.add(new SplashScreen(0, 0, 900, 450));
    }

    public void updateScreenSize(int screenW, int screenH) {
        for (int i = 0; i < getNumWindowObjects(); i++) {
            windowObjects.get(i).updateScreenSize(screenW, screenH);
        }

        for (int i = 0; i < getNumPlayerIDBoxes(); i++) {
            PlayerIDBoxes.get(i).updateScreenSize(screenW, screenH);
        }
    }

    public int getNumPlayerIDBoxes() {
        return PlayerIDBoxes.size();
    }

    public JTextField getPlayerIDBoxAt(int i) {
        return PlayerIDBoxes.get(i).getTextBox();
    }

    public int getNumEquipmentIDBoxes() {
        return EquipmentIDBoxes.size();
    }

    public JTextField getEquipmentIDBoxAt(int i) {
        return EquipmentIDBoxes.get(i).getTextBox();
    }


    public int getSystemState() {
        return system_State;
    }

    public void clearTextBoxes() {
        if (getSystemState() == PLAYER_ENTRY_SCREEN) {
            for (int i = 0; i < getNumPlayerIDBoxes(); i++) {
                getPlayerIDBoxAt(i).setText("");
            }
            for (int i = 0; i < getNumEquipmentIDBoxes(); i++) {
                getEquipmentIDBoxAt(i).setText("");
            }
            System.out.println("Hello?");
        }
    }
    
    
}