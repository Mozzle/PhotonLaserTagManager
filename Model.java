
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import java.lang.Math;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.*;
import javax.xml.crypto.Data;

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

    Database database;
    public int system_State;

    public ArrayList<Sprite> windowObjects; // This arrayList contains all elements that
                                            //  will be drawn to the scrren

    public ArrayList<TextBox> PlayerIDBoxes;    // For player entry screen
    public ArrayList<TextBox> EquipmentIDBoxes; // For player entry screen
    public ArrayList<TextBox> CodenameBoxes; // For player entry screen

    public Timer timer;
    public TimerTask splashScreenTimeoutTask;

    public JLabel toolTip;
    public int toolTip_ms;                   // On-screen time for the tool tip in ms
    public boolean newToolTip;               // Flag for View class to indicate if Model has
                                             // a screen element that needs updated

    public boolean PlayerEntryScreenNewPopup;     // Flag to disable F1, F5 commands while
                                                        // New Player Popup screen is active

    public boolean makePlayerPopup;      // Flag to View class to create a new New Player Popup.
    public boolean makeSettingsPopup;    // Flag to View class to create a new Settings Popup.

    public boolean debugMode;            // Flag to enable debug mode

    /*-----------------------------------------------------
     * 
     *  SplashScreenTimeout
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
            // removes the splashscreen picture
            windowObjects.remove(0);
            // creates the tables for the player entry screen
            for (int i = 0; i < NUM_MAX_PLAYERS_PER_TEAM; i++) {
                // Red Team
                PlayerIDBoxes.add(new TextBox("R"+i, 12, Model.this, TextBox.NUMERIC_TEXT_FIELD_TYPE));
                EquipmentIDBoxes.add(new TextBox("R"+i, 8, Model.this,TextBox.NUMERIC_TEXT_FIELD_TYPE));
                CodenameBoxes.add(new TextBox("R"+i, 8, Model.this, TextBox.DISPLAY_ONLY_NO_TYPE));
            }
            for (int i = 0; i < NUM_MAX_PLAYERS_PER_TEAM; i++) {
                // Green Team
                PlayerIDBoxes.add(new TextBox("G"+i, 12, Model.this,TextBox.NUMERIC_TEXT_FIELD_TYPE));
                EquipmentIDBoxes.add(new TextBox("G"+i, 8, Model.this,TextBox.NUMERIC_TEXT_FIELD_TYPE));
                CodenameBoxes.add(new TextBox("G"+i, 8, Model.this, TextBox.DISPLAY_ONLY_NO_TYPE));
            }
            system_State = PLAYER_ENTRY_SCREEN;

            /*  DATABASE TEST CODE
            System.out.println(database.getNumRows());
            database.insertDB(Database.PARAM_ID_AND_CODENAME, 2, "Gerald");
            System.out.println(database.searchDB(Database.PARAM_ID, 2, ""));
            database.deleteDBRow(Database.PARAM_ID, 2, "");
            */
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
        database = new Database();

        system_State = INITIALIZE;
        // Initialization
        windowObjects = new ArrayList<Sprite>();
        PlayerIDBoxes = new ArrayList<TextBox>();
        EquipmentIDBoxes = new ArrayList<TextBox>();
        CodenameBoxes = new ArrayList<TextBox>();

        // Start a timer, go to the splash screen, wait for 3.2 seconds,
        // then go to SplashScreenTimeout.run() to go to the player entry
        // screen
        timer = new Timer();
        splashScreenTimeoutTask = new SplashScreenTimeout();
        startSplashScreen();
        system_State = SPLASH_SCREEN;
        timer.schedule(splashScreenTimeoutTask, 3200);
        newToolTip = false;
        PlayerEntryScreenNewPopup = false;
        makePlayerPopup = false;
        makeSettingsPopup = false;
        debugMode = false;
        
        // If we are not connected to the database, make a tooltip to warn the user.
        if (database.getdbConnectionStatus() == false) {
            this.toolTip("No database connection! Game will not work!",10000);
        }

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

    /*-----------------------------------------------------
     * 
     *  PlayerEntryScreenDeleter
     * 
     *  DESCRIPTION: This class is used as a deleter for
     *  the Player Entry screen table.
     * 
     ----------------------------------------------------*/

    // Runs when F5 is pushed.
    public void PlayerEntryScreenDeleter()
    {
        //creates the tables for the player entry screen
        for (int i = 0; i < getNumPlayerIDBoxes(); i++) {
            PlayerIDBoxes.remove(i);
            EquipmentIDBoxes.remove(i);
            CodenameBoxes.remove(i);
        }
        system_State = COUNTDOWN_SCREEN;
    }
    
    /*--------------------------------------------------
     * 
     *  CountDownScreenDeleter()
     * 
     *  DESCRIPTION: This function is used to update
     *  model and is the entry point for changing from
     *  the countdown screen to the play action screen.
     * 
     -------------------------------------------------*/

     public void CountDownScreenDeleter() 
     {
        // TODO: Implement this function after the countdown screen
        // is completed. Ensure it transitions to the play action screen
         system_State = PLAY_ACTION_SCREEN;
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
     *      getNumCodenameBoxes()
     *
     *  DESCRIPTION: Returns the size of the
     *  CodenameBoxes ArrayList
     *
     *  REQUIREMENTS:
     *
    ------------------------------------------------- */

    public int getNumCodenameBoxes() {
        return CodenameBoxes.size();
    }

    /*-------------------------------------------------
     *
     *      getCodenameBoxAt()
     *
     *  DESCRIPTION: Returns the CodenameBox TextField
     *  at index i
     *
     *  REQUIREMENTS:
     *
    ------------------------------------------------- */

    public JTextField getCodenameBoxAt(int i) {
        return CodenameBoxes.get(i).getTextBox();
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
            for (int i = 0; i < getNumCodenameBoxes(); i++) {
                getCodenameBoxAt(i).setText("");
            }
        }
    }

    /*-------------------------------------------------
     *
     *      toolTip()
     *
     *  DESCRIPTION: Creates and displays a new JLabel "toolTip"
     * which is used in other functions
     *
     *  REQUIREMENTS:
     *
    ------------------------------------------------- */
    public boolean toolTip(String tipText, int ms) {
        boolean success = true;
        if (ms > 0) {
            toolTip_ms = ms;
        } else {
            toolTip_ms = 4500;
        }
        toolTip = new JLabel(tipText, SwingConstants.CENTER);
        toolTip.setBackground(new Color(104, 110, 58));
        toolTip.setForeground(Color.WHITE);
        toolTip.setFont(new Font("Verdana", Font.BOLD, 22));
        newToolTip = true;

        return success;
    }

    /*-------------------------------------------------
     *
     *      checkStartGameConditions()
     *
     *  DESCRIPTION: Checks a series of conditions in 
     *  the entry screen to determine if we are
     *  able to start the game. If so, we will
     *  transition to the countdown screen. Otherwise,
     *  tooltips will appear telling the user what is
     *  preventing the game from starting.
     *
     *  REQUIREMENTS:
     *
    ------------------------------------------------- */

    public boolean checkStartGameConditions() {
        // Conditions:
        // At least 1 player on each team
        // No duplicate Player IDs
        // No duplicate Equipment IDs
        // Every non-empty player field has a corresponding filled equipment ID field
        // Every non-empty Equipment field has a corresponding filled player ID field
        // All non-empty Player ID's are valid
        // All non-empty Equipment IDs are valid

        boolean gameConditionsMet = true;

        int tmpRedTeamCnt = 0;
        int tmpGreenTeamCnt = 0;

        // For all the fields
        for (int i = 0; i < Model.NUM_MAX_PLAYERS_PER_TEAM * 2; i++) {

            // If Player ID field is not empty
            if ( !PlayerIDBoxes.get(i).getTextFromField().equals("") ) {

                // AND Equipment field IS Empty or IS invalid
                if ( EquipmentIDBoxes.get(i).getTextFromField().equals("") 
                || ( EquipmentIDBoxes.get(i).getTextFromField().equals("202") )
                || ( EquipmentIDBoxes.get(i).getTextFromField().equals("221") ) ) {
                    gameConditionsMet = false;
                    if (i < Model.NUM_MAX_PLAYERS_PER_TEAM) {
                        toolTip("Invalid Equipment ID for Red Team Player " + i + "!", 4500);
                    }
                    else {
                        toolTip("Invalid Equipment ID for Green Team Player " + (i - Model.NUM_MAX_PLAYERS_PER_TEAM) + "!", 4500);
                    }
                }

                //Search the database for this Player ID
                String tmpString = database.searchDB(Database.PARAM_ID, Integer.valueOf(PlayerIDBoxes.get(i).getTextFromField()), "");

                // If the database query Does NOT find the Player ID:
                if (tmpString.equals("")) {
                    PlayerIDBoxes.get(i).getTextBox().requestFocus();
                    gameConditionsMet = false;
                    toolTip("Consider making a new Player for this ID",4500);
                    if (i < Model.NUM_MAX_PLAYERS_PER_TEAM) {
                        toolTip("Invalid Player ID for Red Team Player " + i + "!",4500);
                    }
                    else {
                        toolTip("Invalid Player ID for Green Team Player " + (i - Model.NUM_MAX_PLAYERS_PER_TEAM) + "!",4500);
                    }
                }

                //Increment Team Counters
                if (i < Model.NUM_MAX_PLAYERS_PER_TEAM) {
                    tmpRedTeamCnt++;
                }
                else {
                    tmpGreenTeamCnt++;
                }

            }

            // If equipment ID is NOT Empty
            if ( !EquipmentIDBoxes.get(i).getTextFromField().equals("") ) {

                // And Player ID field IS empty
                if ( PlayerIDBoxes.get(i).getTextFromField().equals("") ) {
                    gameConditionsMet = false;
                    if (i < Model.NUM_MAX_PLAYERS_PER_TEAM) {
                        toolTip("Invalid Player ID for Red Team Player " + i + "!",4500);
                    }
                    else {
                        toolTip("Invalid Player ID for Green Team Player " + (i - Model.NUM_MAX_PLAYERS_PER_TEAM) + "!",4500);
                    }
                }
            
            }
        }

        // If no players on one of the teams
        if (tmpRedTeamCnt == 0 || tmpGreenTeamCnt == 0) {
            gameConditionsMet = false;
            if (tmpRedTeamCnt == 0) {
                toolTip("Red Team must have at least 1 player!",4500);
            }
            else {
                toolTip("Green Team must have at least 1 player!",4500);
            }
        }

        // For every text field
        for (int i = 0; i < (Model.NUM_MAX_PLAYERS_PER_TEAM * 2); i++) {

            //Get IDs
            String tmpPlayerID = PlayerIDBoxes.get(i).getTextFromField();
            String tmpEquipmentId = EquipmentIDBoxes.get(i).getTextFromField();

            // If the field is not empty
            if (!tmpPlayerID.equals("") || !tmpEquipmentId.equals("")) {

                // Compare to the text of every other field to check for duplicate IDs
                for (int j = 0; j < (Model.NUM_MAX_PLAYERS_PER_TEAM * 2); j++) {
                    // Make sure the text field isn't comparing to itself
                    if (i != j) {
                        // If a Player ID duplicate is found
                        if (tmpPlayerID.equals(PlayerIDBoxes.get(j).getTextFromField())) {
                            gameConditionsMet = false;
                            toolTip("Player ID " + tmpPlayerID + " is used more than once in this game!",4500);
                        }
                         // If an Equipment ID duplicate is found
                        if (tmpEquipmentId.equals(EquipmentIDBoxes.get(j).getTextFromField())) {
                            gameConditionsMet = false;
                            toolTip("Equipment ID " + tmpEquipmentId + " is used more than once in this game!",4500);
                        }
                    }
                }
            }
        }
        // Return whether or not the conditions for the game to start have been met
        return gameConditionsMet || debugMode;
    } 
    
    public void setNewPopup(boolean isTrue) {
        PlayerEntryScreenNewPopup = isTrue;
    }

    public boolean getNewPopup() {
        return PlayerEntryScreenNewPopup;
    }

    /*-------------------------------------------------
     *
     *      checkEquipmentIDFieldsForDupicates()
     *
     *  DESCRIPTION: If given parameter of '-1', compare
     *  all non-blank Equipment ID's against one another
     *  to check for duplicates. Otherwise, parameter is
     *  an index of a single Equipment ID box to compare
     *  against all other Equip. ID boxes for duplicates.
     *  Returns false if no duplicates found, true if 
     *  duplicates found.
     *
     *  REQUIREMENTS:
     *
    ------------------------------------------------- */

    public boolean checkEquipmentIDFieldsForDupicates(int indexToCompare) {
        boolean result = false;

        if (indexToCompare == -1) {
            // Check all equipment IDs against all other IDs
        }
        // Else check that the equipment ID at indexToCompare is unique. assumes that given equipment ID box
        // is not blank.
        else {
            for (int i = 0; (i < Model.NUM_MAX_PLAYERS_PER_TEAM * 2); i++) {
                if ( EquipmentIDBoxes.get(i).getTextFromField().equals(EquipmentIDBoxes.get(indexToCompare).getTextFromField() )
                && ( i != indexToCompare)) {
                    result = true;
                }
            }
        }

        return result;
    }

    public int getTextBoxIndexFromName(String name) {
        char team = name.charAt(0);
        int index = -1;
        if (team == 'R') {
            index = Integer.valueOf(name.substring(1));
        }
        else if (team == 'G') {
            index = Integer.valueOf(name.substring(1)) + Model.NUM_MAX_PLAYERS_PER_TEAM;
        }

        return index;
    }

    public boolean getMakePlayerPopupFlag() {
        return makePlayerPopup;
    }

    public void setMakePlayerPopupFlag(boolean set) {
        makePlayerPopup = set;
    }

    public boolean getMakeSettingsPopupFlag() {
        return makeSettingsPopup;
    }

    public void setMakeSettingsPopupFlag(boolean set) {
        makeSettingsPopup = set;
    }

    public boolean getDebugMode() {
        return debugMode;
    }

    public void setDebugMode(boolean set) {
        debugMode = set;
    }

    /*-------------------------------------------------
     *
     *      getStandardKeyAdapter()
     *
     *  DESCRIPTION: Returns the standard Key Listener
     *  for the Player Entry Screen. Used when the user
     *  has focus on the buttons, so that their keyboard
     *  shortcuts still work
     *
     *  REQUIREMENTS:
     *
    ------------------------------------------------- */
    public KeyAdapter getStandardKeyAdapter() {
        return new KeyAdapter() {
            public void keyPressed(KeyEvent ke) {

                switch(ke.getKeyCode()) {
                    case KeyEvent.VK_TAB:
                    // Check database against this field
                        break;
                    case KeyEvent.VK_F1:
                        if (getSystemState() == Model.PLAYER_ENTRY_SCREEN 
                        && getNewPopup() == false) {
                            clearTextBoxes();
                        }
                        break;
                    case KeyEvent.VK_F5:
                        if (getSystemState() == Model.PLAYER_ENTRY_SCREEN
                        && getNewPopup() == false) {
       
                               if (checkStartGameConditions()) {
                                   PlayerEntryScreenDeleter();
                               }
                        }
                        break;
                    // Case F9 -- Open player popup
                    case KeyEvent.VK_F9:
                        if (getSystemState() == Model.PLAYER_ENTRY_SCREEN
                         && getNewPopup() == false) {
                                setMakePlayerPopupFlag(true);
                        }
                        break;
                    // Case F12 -- Open settings popup
                    case KeyEvent.VK_F12:
                        if (getSystemState() == Model.PLAYER_ENTRY_SCREEN 
                        && getNewPopup() == false) {
                            setMakeSettingsPopupFlag(true);
                        }
                        break;
                }
            }
        };
    }
    
}