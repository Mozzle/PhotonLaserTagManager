
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import java.lang.Math;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.*;
import java.awt.event.*;
import java.awt.Color;
import java.awt.Font;

public class Model
{
    //enums in Java are utterly useless, so here is my C-like enums:

    // System_States
    public static final int INITIALIZE = 0;
    public static final int SPLASH_SCREEN = 1;
    public static final int PLAYER_ENTRY_SCREEN = 2;
    public static final int COUNTDOWN_SCREEN = 3;
    public static final int PLAY_ACTION_SCREEN = 4;
    public static final int NUM_SCREENS = 5;

    // Max players per team
    public static final int NUM_MAX_PLAYERS_PER_TEAM = 15;

    public int system_State;

    public ArrayList<Sprite> windowObjects; // This arrayList contains all elements that
                                            //  will be drawn to the scrren

    public ArrayList<TextBox> PlayerIDBoxes;    // For player entry screen
    public ArrayList<TextBox> EquipmentIDBoxes; // For player entry screen

    public Timer timer;
    public TimerTask splashScreenTimeoutTask;

    public JLabel toolTip;
    public boolean newToolTip;               // Flag for View class to indicate if Model has
                                                // a screen element that needs updated

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
            //removes the splashscreen picture
            windowObjects.remove(0);
            //creates the tables for the player entry screen
            for (int i = 0; i < NUM_MAX_PLAYERS_PER_TEAM; i++) {
                PlayerIDBoxes.add(new TextBox("", 12, Model.this, TextBox.NUMERIC_TEXT_FIELD_TYPE));
                EquipmentIDBoxes.add(new TextBox("", 8, Model.this,TextBox.NUMERIC_TEXT_FIELD_TYPE));
            }
            for (int i = 0; i < NUM_MAX_PLAYERS_PER_TEAM; i++) {
                PlayerIDBoxes.add(new TextBox("", 12, Model.this,TextBox.NUMERIC_TEXT_FIELD_TYPE));
                EquipmentIDBoxes.add(new TextBox("", 8, Model.this,TextBox.NUMERIC_TEXT_FIELD_TYPE));
            }
            system_State = PLAYER_ENTRY_SCREEN;
        }
    }


     /*-----------------------------------------------------
     * 
     *      PlayerEntryScreenDeleter
     * 
     *  DESCRIPTION: This class is used as a deleter for
     *  the Player Entry screen table.
     * 
     ----------------------------------------------------*/

    // Ran when F% is pushed.
    public void PlayerEntryScreenDeleter()
    {
        //creates the tables for the player entry screen
        for (int i = 0; i < getNumPlayerIDBoxes(); i++) {
            PlayerIDBoxes.remove(i);
            EquipmentIDBoxes.remove(i);
        }
        system_State = COUNTDOWN_SCREEN;
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
        newToolTip = false;
        
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

            case COUNTDOWN_SCREEN:
                break;

            default:
                break;
        }

    }

    /*-------------------------------------------------
     *
     *      getNumWindowObjects()
     *
     *  DESCRIPTION: Returns size of windowObjects
     *  ArrayList
     *
     *  REQUIREMENTS:
     *
    ------------------------------------------------- */

    public int getNumWindowObjects() {
		return windowObjects.size();
	}

    /*-------------------------------------------------
     *
     *      getWindowObjectAt()
     *
     *  DESCRIPTION: Returns the windowObject Sprite at
     *  index i
     *
     *  REQUIREMENTS:
     *
    ------------------------------------------------- */
    public Sprite getWindowObjectAt(int i) {
		return windowObjects.get(i);
	}

    /*-------------------------------------------------
     *
     *      startSplashScreen()
     *
     *  DESCRIPTION: Creates the splash screen, called
     *  at program initialization
     *
     *  REQUIREMENTS:
     *
    ------------------------------------------------- */

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

    /*-------------------------------------------------
     *
     *      getNumPlayerIDBoxes()
     *
     *  DESCRIPTION: Returnes size of PlayerIDBoxes
     *  TextField ArrayList
     *
     *  REQUIREMENTS:
     *
    ------------------------------------------------- */

    public int getNumPlayerIDBoxes() {
        return PlayerIDBoxes.size();
    }

    /*-------------------------------------------------
     *
     *      getPlayerIDBoxAt()
     *
     *  DESCRIPTION: Returns the PlayerIDBox TextField
     *  at index i
     *
     *  REQUIREMENTS:
     *
    ------------------------------------------------- */

    public JTextField getPlayerIDBoxAt(int i) {
        return PlayerIDBoxes.get(i).getTextBox();
    }

    /*-------------------------------------------------
     *
     *      getNumEquipmentIDBoxes()
     *
     *  DESCRIPTION: Returns the size of the
     *  EquipmentIDBoxes ArrayList
     *
     *  REQUIREMENTS:
     *
    ------------------------------------------------- */

    public int getNumEquipmentIDBoxes() {
        return EquipmentIDBoxes.size();
    }

    /*-------------------------------------------------
     *
     *      getEquipmentIDBoxAt()
     *
     *  DESCRIPTION: Returns the EquipmentIDBox TextField
     *  at index i
     *
     *  REQUIREMENTS:
     *
    ------------------------------------------------- */

    public JTextField getEquipmentIDBoxAt(int i) {
        return EquipmentIDBoxes.get(i).getTextBox();
    }

    /*-------------------------------------------------
     *
     *      getSystemState()
     *
     *  DESCRIPTION: Returns the system_State enum value
     *
     *  REQUIREMENTS:
     *
    ------------------------------------------------- */

    public int getSystemState() {
        return system_State;
    }

    /*-------------------------------------------------
     *
     *      clearTextBoxes()
     *
     *  DESCRIPTION: If we're in the player entry screen,
     *  clear out all of the text boxes
     *
     *  REQUIREMENTS:
     *
    ------------------------------------------------- */

    public void clearTextBoxes() {
        if (getSystemState() == PLAYER_ENTRY_SCREEN) {
            for (int i = 0; i < getNumPlayerIDBoxes(); i++) {
                getPlayerIDBoxAt(i).setText("");
            }
            for (int i = 0; i < getNumEquipmentIDBoxes(); i++) {
                getEquipmentIDBoxAt(i).setText("");
            }
        }
    }

    public boolean toolTip(String tipText) {
        boolean success = true;
        toolTip = new JLabel(tipText, SwingConstants.CENTER);
        toolTip.setBackground(new Color(104, 110, 58));
        toolTip.setForeground(Color.WHITE);
        toolTip.setFont(new Font("Verdana", Font.BOLD, 22));
        newToolTip = true;
        System.out.println("In Model");

        return success;
    }
    
    
}