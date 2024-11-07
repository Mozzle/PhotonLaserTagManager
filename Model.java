
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import java.lang.Math;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.*;
import javax.xml.crypto.Data;
import java.util.Iterator;

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
    public static final int GAME_OVER = 5;
    public static final int NUM_SCREENS = 6;

    // GameDataStatus
    public static final int FIRST_GAME = 0;
    public static final int SECOND_GAME_RESET = 1;
    public static final int SECOND_GAME_NEW_ENTRY = 2;

    // Max players per team
    public static final int NUM_MAX_PLAYERS_PER_TEAM = 15;

    Database database;
    public int system_State;                        // Controls the state of the program.
                                                    // See the 'System_States' enums above
    public int GameDataStatus;

    public ArrayList<Player> playerList;            // Local player list for the game
    public ArrayList<Integer> redTeamPlayerList;    // List of playerList indicies that belong to Red team members.
    public ArrayList<Integer> greenTeamPlayerList;  // List of playerList indicies that belong to Red team members.
    public ArrayList<Player> removePlayerQueue;     // Queue for players to be removed from the game
    public ArrayList<JTextField> setTBQueue;        // Queue for textboxes to be set with text
    public ArrayList<String> dataTBQueue;           // Queue of text to be set with a textbox

    public ArrayList<Sprite> windowObjects;         // This arrayList contains all elements that
                                                    //  will be drawn to the scrren

    public ArrayList<TextBox> PlayerIDBoxes;        // For player entry screen
    public ArrayList<TextBox> EquipmentIDBoxes;     // For player entry screen
    public ArrayList<TextBox> CodenameBoxes;        // For player entry screen

    public Timer timer;
    public TimerTask splashScreenTimeoutTask;

    public JLabel toolTip;                          // Container for the newest tooltip in program
    public int toolTip_ms;                          // On-screen time for the tool tip in ms
    public boolean newToolTip;                      // Flag for View class to indicate if Model has
                                                    // a screen element that needs updated

    public boolean PlayerEntryScreenNewPopup;       // Flag to disable F1, F5 commands while
                                                    // New Player Popup screen is active

    public boolean makePlayerPopup;                 // Flag to View class to create a new New Player Popup.
    public boolean makeSettingsPopup;               // Flag to View class to create a new Settings Popup.

    public boolean debugMode;                       // Flag to enable debug mode

    public boolean playerUpdateFlag;                // Flag to allow player updates in model.update() to avoid concurrentexception
    
    public boolean scoreUpdatedFlag;                // Flag to View to update scores
    public int greenTeamScore;                      // For the Play Action Screen
    public int redTeamScore;                        // For the Play Action Screen
    public int secondsRemainingInGame;              // For the Play Action Screen
    public Timer gameCountdownTimer;
    public TimerTask gameCountdownTask;

    public ArrayList<JLabel> gameEventsQueue;       // For Game Action Screen
    
    private NetController netController;            // Network controller for the program
    private AudioHandler audioHandler;              // Audio handler for the program

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
     *  REQUIREMENTS: 0024
     * 
     ----------------------------------------------------*/
    public class SplashScreenTimeout extends TimerTask
    {
        // Ran when the splash screen timer ends.
        public void run()
        {
            // removes the splashscreen picture
            windowObjects.remove(0);

            PlayerIDBoxes.clear();
            EquipmentIDBoxes.clear();
            CodenameBoxes.clear();
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
            audioHandler.stopAudio();
        }
    }

    /*-------------------------------------------------
     *
     *      Model()
     *
     *  DESCRIPTION: Model Class Initializer
     *
     *  REQUIREMENTS: 0023
     *
    ------------------------------------------------- */
    public Model()
    {   
        database = new Database();
        netController = null;
        audioHandler = new AudioHandler();

        system_State = INITIALIZE;
        GameDataStatus = FIRST_GAME;
        // Initialization
        windowObjects = new ArrayList<Sprite>();
        PlayerIDBoxes = new ArrayList<TextBox>();
        EquipmentIDBoxes = new ArrayList<TextBox>();
        CodenameBoxes = new ArrayList<TextBox>();
        gameEventsQueue = new ArrayList<JLabel>();
        playerList = new ArrayList<Player>();
        redTeamPlayerList = new ArrayList<Integer>();
        greenTeamPlayerList = new ArrayList<Integer>();

        // Init our remove and set queues
        removePlayerQueue = new ArrayList<Player>();
        setTBQueue = new ArrayList<JTextField>();
        dataTBQueue = new ArrayList<String>();

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
        playerUpdateFlag = true;

        scoreUpdatedFlag = false;
        redTeamScore = 0;
        greenTeamScore = 0;
        secondsRemainingInGame = 0;
        
        // If we are not connected to the database, make a tooltip to warn the user.
        if (database.getdbConnectionStatus() == false) {
            this.toolTip("No database connection! Game will not work!",10000);
        }

    }

    /*-------------------------------------------------
     *
     *  update()
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
            audioHandler.loadAudio(audioHandler.sfx.get(audioHandler.reset));
            audioHandler.playAudio();
                break;

            case PLAYER_ENTRY_SCREEN:

            // Update playerlist
           Iterator<Player> it = playerList.iterator();
              while (it.hasNext()) {
                Player p = it.next();
                if (p != null)
                    p.update();
                
                if (!p.getStatus()) {
                    // Retransmit if the player is not verified
                    netController.transmit(String.valueOf(p.getEquipID()));
                    p.verify();
                }
              }
            
            // Remove any needed players
            for(Player p : removePlayerQueue) {
                if (p != null)
                    removePlayer(p);
            }

            // Clear any needed textboxes
            for (int i = 0; i < setTBQueue.size(); i++) {
                setTBQueue.get(i).setText(dataTBQueue.get(i));
            }

            removePlayerQueue.clear();
            setTBQueue.clear();
            dataTBQueue.clear();

                break;

            // Do nothing for countdown screen
            case COUNTDOWN_SCREEN:
                
                break;

            // Handle received network data for player action screen
            case PLAY_ACTION_SCREEN:
                // Handle incoming UDP data
                if (netController.pingFlag) {
                    // Update our ping flag so we can continue receiving
                    netController.pingFlag = false;

                    String data = netController.pop();
                    Integer[] receivedPlayers = new Integer[2];
                    receivedPlayers = processNetworkData(data);

                    Player firstIdentifiedPlayer = identifyPlayer(-1, receivedPlayers[0], "NULL", -1);
                    Player secondIdentifiedPlayer = identifyPlayer(-1, receivedPlayers[1], "NULL", -1);

                    // If we have received a shot, update the player scores
                    if (firstIdentifiedPlayer != null) {
                            // If firstIdentifiedPlayer DID NOT hit the base
                            if (receivedPlayers[1] != 53 && receivedPlayers[1] != 43 && secondIdentifiedPlayer != null) {
                                // If firstIdentifiedPlayer hit a player on the opposite team
                                if (firstIdentifiedPlayer.getTeam() != secondIdentifiedPlayer.getTeam()) {
                                    firstIdentifiedPlayer.setScore(firstIdentifiedPlayer.getScore() + 10);
                                    scoreUpdatedFlag = true;
                                    netController.transmit(String.valueOf(secondIdentifiedPlayer.getEquipID()));
                                    updateGameEventsQueue(firstIdentifiedPlayer.name, secondIdentifiedPlayer.name);
                                }
                                // If firstIdentifiedPlayer hit a player on the SAME team
                                else  {
                                    firstIdentifiedPlayer.setScore(firstIdentifiedPlayer.getScore() - 10);
                                    scoreUpdatedFlag = true;
                                    netController.transmit(String.valueOf(firstIdentifiedPlayer.getEquipID()));
                                    updateGameEventsQueue(firstIdentifiedPlayer.name, secondIdentifiedPlayer.name);
                                }
                            }
                            // If firstIdentifiedPlayer DID hit the base
                            else if (receivedPlayers[1] == 53 || receivedPlayers[1] == 43) {
                                // If Red Team player scored the green team base.
                                if (firstIdentifiedPlayer.getTeam() == Player.RED_TEAM && receivedPlayers[1] == 43) {
                                    firstIdentifiedPlayer.setScore(firstIdentifiedPlayer.getScore() + 100);
                                    firstIdentifiedPlayer.setHasHitBase(true);
                                    scoreUpdatedFlag = true;
                                    // Verify with Mr. Strother that we are supposed to transmit the value of the Base!
                                    netController.transmit(String.valueOf(receivedPlayers[1]));
                                    updateGameEventsQueue(firstIdentifiedPlayer.name, "Green Team Base");
                                }
                                else if (firstIdentifiedPlayer.getTeam() == Player.GREEN_TEAM && receivedPlayers[1] == 53) {
                                    firstIdentifiedPlayer.setScore(firstIdentifiedPlayer.getScore() + 100);
                                    firstIdentifiedPlayer.setHasHitBase(true);
                                    scoreUpdatedFlag = true;
                                    // Verify with Mr. Strother that we are supposed to transmit the value of the Base!
                                    netController.transmit(String.valueOf(receivedPlayers[1]));
                                    updateGameEventsQueue(firstIdentifiedPlayer.name, "Red Team Base");
                                }
                                else {
                                    // Do we want to have some action for player hitting their own base?
                                }
                            }
                        }

                    
                }



                break;

            case GAME_OVER:

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

    // Runs when game is started: transition from Player Entry Screen
    // to the Countdown screen
    public void PlayerEntryScreenDeleter()
    {
        //removes the tables for the player entry screen
        for (int i = 0; i < getNumPlayerIDBoxes(); i++) {
            //PlayerIDBoxes.remove(i);
            //EquipmentIDBoxes.remove(i);
            //CodenameBoxes.remove(i);
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
     *  REQUIREMENTS: 0024,
     *
    ------------------------------------------------- */

    public void startSplashScreen() {
        windowObjects.add(new SplashScreen(0, 0, 900, 450));
    }

    public void BackGroundCountDown(){
        windowObjects.add(new BackGroundCountDown(0,0,800,800));
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
     *  getCodenameBoxAt()
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

    public void setSystemState(int state) {
        if (state < NUM_SCREENS) {
            system_State = state;
        }
        
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
     *  REQUIREMENTS: 0026
     *
    ------------------------------------------------- */
    public boolean toolTip(String tipText, int ms) {
        boolean success = true;
        // Some error handling
        if (ms > 0) {
            toolTip_ms = ms;
        } else {
            toolTip_ms = 4500;
        }
        // Make the new toolTip, then set the newToolTip flag
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
     *  the player entry screen to determine if we are
     *  able to start the game. If so, we will
     *  transition to the countdown screen. Otherwise,
     *  tooltips will appear telling the user what is
     *  preventing the game from starting.
     *
     *  REQUIREMENTS: 0025,
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
     *  checkEquipmentIDFieldsForDupicates()
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

        // Check that the equipment ID at indexToCompare is unique (assumes the equipment ID box isn't empty)
        else {
            // Check our playerlist, if we find a duplicate return true
            int compareID = Integer.parseInt(EquipmentIDBoxes.get(indexToCompare).getTextFromField());
            if (identifyPlayer(compareID, -1, "NULL", -1) != null)
                result = true;

            // Check all other text boxes for duplicates
            for (int i = 0; (i < Model.NUM_MAX_PLAYERS_PER_TEAM * 2); i++) {
                if ( EquipmentIDBoxes.get(i).getTextFromField().equals(EquipmentIDBoxes.get(indexToCompare).getTextFromField() )
                && ( i != indexToCompare)) {
                    result = true;
                }
            }
        }

        return result;
    }


    /*-------------------------------------------------
     *
     *  getTextBoxIndexFromName()
     *
     *  DESCRIPTION: The Player ID boxes, Equipment ID 
     *  boxes, and Codename boxes on the player entry 
     *  screen are assigned names of the following 
     *  format when they are created: 'R4', 'G11', 'G7',
     *  etc. Given one of these objects, this method
     *  will return its index in its respective ID Boxes
     *  ArrayList (EquipmentIDBoxes, for example). 
     *
     *  REQUIREMENTS:
     *
    ------------------------------------------------- */

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


    /*-------------------------------------------------
     *
     *  getMakePlayerPopupFlag()
     *
     *  DESCRIPTION: Gets the status of the flag that
     *  controls whether or not a New Player Entry
     *  popup window should appear
     *
     *  REQUIREMENTS:
     *
    ------------------------------------------------- */

    public boolean getMakePlayerPopupFlag() {
        return makePlayerPopup;
    }

    /*-------------------------------------------------
     *
     *  setMakePlayerPopupFlag()
     *
     *  DESCRIPTION: Sets the status of the flag that
     *  controls whether or not a New Player Entry
     *  popup window should appear
     *
     *  REQUIREMENTS:
     *
    ------------------------------------------------- */

    public void setMakePlayerPopupFlag(boolean set) {
        makePlayerPopup = set;
    }


    /*-------------------------------------------------
     *
     *  getMakeSettingsPopupFlag()
     *
     *  DESCRIPTION: Gets the status of the flag that
     *  controls whether or not a Settings popup window
     *  should appear
     *
     *  REQUIREMENTS:
     *
    ------------------------------------------------- */

    public boolean getMakeSettingsPopupFlag() {
        return makeSettingsPopup;
    }


    /*-------------------------------------------------
     *
     *  setMakeSettingsPopupFlag()
     *
     *  DESCRIPTION: Sets the status of the flag that
     *  controls whether or not a Settings popup window
     *  should appear
     *
     *  REQUIREMENTS:
     *
    ------------------------------------------------- */

    public void setMakeSettingsPopupFlag(boolean set) {
        makeSettingsPopup = set;
    }


    /*-------------------------------------------------
     *
     *  getDebugMode()
     *
     *  DESCRIPTION: Gets the boolean debugMode status.
     *  debugMode controls whether database connection
     *  and other conditions need to be met for a game
     *  to start.
     *
     *  REQUIREMENTS:
     *
    ------------------------------------------------- */

    public boolean getDebugMode() {
        return debugMode;
    }


    /*-------------------------------------------------
     *
     *  setDebugMode()
     *
     *  DESCRIPTION: Sets the boolean debugMode status.
     *  debugMode controls whether database connection
     *  and other conditions need to be met for a game
     *  to start.
     *
     *  REQUIREMENTS:
     *
    ------------------------------------------------- */

    public void setDebugMode(boolean set) {
        debugMode = set;
    }

    /*-------------------------------------------------
     *
     *  getStandardKeyAdapter()
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

    public void PlayActionScreenDataInitializer() {

        redTeamScore = 0;
        greenTeamScore = 0;
        secondsRemainingInGame = 360;
        gameCountdownTimer = new Timer();
        gameCountdownTask = new TimerTask() {
            public void run() {
                secondsRemainingInGame--;

                if (secondsRemainingInGame < 0) {
                    // TODO: END GAME
                    // IDEA: A POPUP SCREEN LIKE THE SETTINGS/NEW PLAYER
                    // ENTRY POPUP SCREENS THAT PROMPTS THE USER TO RESTART or
                    // Go back to the player entry screen
                    netController.transmit("221");
                    netController.transmit("221");
                    netController.transmit("221");
                    system_State = GAME_OVER;
                    gameCountdownTimer.cancel();
                }
            }
        };
        gameCountdownTimer.schedule(gameCountdownTask, 1000, 1000);

        gameEventsQueue.clear();

        for (int i = 0; i < 16; i++) {
            gameEventsQueue.add(new JLabel(""));
            gameEventsQueue.get(i).setForeground(Color.WHITE);
            gameEventsQueue.get(i).setFont(new Font("Arial", Font.BOLD, 20));
        }

        if (GameDataStatus == FIRST_GAME) {
            for (int i = 0; i < getPlayerListSize(); i++) {
                // Iterate through playerList to find the next Green Team Member.
                if (getPlayer(i).getTeam() == Player.RED_TEAM) {
                    redTeamPlayerList.add(i);
                }
                else if (getPlayer(i).getTeam() == Player.GREEN_TEAM) {
                    greenTeamPlayerList.add(i);
                }
                else {
                    //Throw error.
                }
            }
        }
        else if (GameDataStatus == SECOND_GAME_NEW_ENTRY) {
            redTeamPlayerList.clear();
            greenTeamPlayerList.clear();
            for (int i = 0; i < getPlayerListSize(); i++) {
                // Iterate through playerList to find the next Green Team Member.
                if (getPlayer(i).getTeam() == Player.RED_TEAM) {
                    redTeamPlayerList.add(i);
                }
                else if (getPlayer(i).getTeam() == Player.GREEN_TEAM) {
                    greenTeamPlayerList.add(i);
                }
                else {
                    //Throw error.
                }
            }
        }
        // If replaying a game, turn all of the equipment back on, and reset scores
        else {
            for (int i = 0; i < getPlayerListSize(); i++) {
                netController.transmit(String.valueOf(getPlayer(i).getEquipID()));
                getPlayer(i).verify();
                getPlayer(i).setScore(0);
            }
        }

    }

    /*-------------------------------------------------
     *
     *  getRedTeamPlayerListAt(int i) and
     *  getGreenTeamPlayerListAt(int i)
     *
     *  DESCRIPTION: Returns the playerList index stored
     *  at the given red/greenTeamPlayerList index.
     *  The red/greenTeamPlayerList ArrayLists only
     *  store the indexes of Players stored in playerList,
     *  sorted by team.
     *
    ------------------------------------------------- */

    public int getRedTeamPlayerListAt(int i) {
        return redTeamPlayerList.get(i);
    }

    public int getGreenTeamPlayerListAt(int i) {
        return greenTeamPlayerList.get(i);
    }

    /*-------------------------------------------------
     *
     *  getRedTeamPlayerListSize() and
     *  getGreenTeamPlayerListSize()
     *
     *  DESCRIPTION: Returns size of the lists
     *
    ------------------------------------------------- */

    public int getRedTeamPlayerListSize() {
        return redTeamPlayerList.size();
    }

    public int getGreenTeamPlayerListSize() {
        return greenTeamPlayerList.size();
    }

    public JLabel getGameEventQueueAtNum(int i) {
        return gameEventsQueue.get(i);
    }

    public int getNumGameEventQueue() {
        return gameEventsQueue.size();
    }

    public int getRedTeamScore() {
        return redTeamScore;
    }

    public int getGreenTeamScore() {
        return greenTeamScore;
    }

    public boolean getScoreUpdatedFlag() {
        return scoreUpdatedFlag;
    }

    public void setScoreUpdatedFlag(boolean val) {
        scoreUpdatedFlag = val;
    }

    public void updateGameEventsQueue(String playerNameShooting, String playerNameHit) {
        // Move all the text up the screen
        for (int i = 1; i < 16; i++) {
            gameEventsQueue.get(i-1).setText(gameEventsQueue.get(i).getText());
        }

        gameEventsQueue.get(15).setText(playerNameShooting + " hit " + playerNameHit);

    }

    public String getGameTimeRemaining() {
        
        int minutes = secondsRemainingInGame / 60;
        int seconds = secondsRemainingInGame % 60;
        if (secondsRemainingInGame < 0) {
            minutes = 0;
            seconds = 0;
        }
        return (String.valueOf(minutes) + ":" + String.format("%02d", seconds));
    }

    public int getGameSecondsRemaining() {
        return secondsRemainingInGame;
    }

    public int returnNextAvailableBox(boolean team) {
        // Return the index of the next empty and available textbox based on the given team
        int index = -1;

        for (int i = 0; i < Model.NUM_MAX_PLAYERS_PER_TEAM; i++) {
            // Check for next available green team box
            if (team) {
                if (PlayerIDBoxes.get(i).getTextFromField().equals("")) {
                    index = i;
                    break;
                }
            }
            else {
            // Check for next available red team box
                if (PlayerIDBoxes.get(i + Model.NUM_MAX_PLAYERS_PER_TEAM).getTextFromField().equals("")) {
                    index = i + Model.NUM_MAX_PLAYERS_PER_TEAM;
                    break;
                }
            }
        }

        return index;
    }
 
    /*-------------------------------------------------
     *
     *  clearPlayerList()
     *
     *  DESCRIPTION: Clears the player list
     *
    ------------------------------------------------- */
    public void clearPlayerList() {
        playerList.clear();
        System.out.println("[Model] Cleared the playerlist.");
    }

    /*-------------------------------------------------
     *
     *  getPlayer(int index)
     *
     *  DESCRIPTION: Returns a player object at an index
     *
    ------------------------------------------------- */
    public Player getPlayer(int index) {
        return playerList.get(index);
    }

    /*-------------------------------------------------
     *
     *  addPlayer(Player p)
     *
     *  DESCRIPTION: Adds a player to the player list
     *  Returns true if successful, false if not
     * 
     *  Will add a player to the player list only if
     *  the player's ID and equipment ID is unique and valid
     *
    ------------------------------------------------- */
    public boolean addPlayer(Player p) {
        // Check to ensure we aren't adding a duplicate equipment ID
        for (Player player : playerList) {
            if (player.getEquipID() == p.getEquipID() || player.getNormalID() == p.getNormalID()) {
                System.out.println("[Model] Failed to add " + p.getNormalID() + ", duplicate ID or invalid name found.");
                return false;
            }
        }

        // Ensure we don't add an invalid player (invalid IDs)
        if (p.getEquipID() == -1 || p.getNormalID() == -1)
            return false;

        playerList.add(p);
        System.out.println("[Model] Added player \"" + p.getNormalID() + "\" to playerlist.");
        return true;
    }

    /*-------------------------------------------------
     *
     *  getPlayerListSize()
     *
     *  DESCRIPTION: Returns size of playerList
     *  arrayList, used for looping purposes.
     *
    ------------------------------------------------- */

    public int getPlayerListSize() {
        return playerList.size();
    }

    /*-------------------------------------------------
     *
     *  removePlayer(Player p)
     *
     *  DESCRIPTION: Removes a player from the playerlist
     *  and clears their reference text boxes
     *  Returns true if successful, false if not
     * 
     *
    ------------------------------------------------- */
    public boolean removePlayer(Player p) {

        if (p == null) {
            return false;
        }

        // Clear reference text boxes for given player
        JTextField j[] = p.getReferences();
        if (j[0] != null)
            j[0].setText("");
        if (j[1] != null)
            j[1].setText("");
        if (j[2] != null)
            j[2].setText("");

        // Attempt to find & remove player from list -- return result as boolean
        boolean result = playerList.remove(p);

        if (result)
            System.out.println("[Model] Removed player that contained or now contains ID reference " + p.getNormalID() + " from playerlist.");
        else
            System.out.println("[Model] Could not find/remove " + p.getNormalID() + ".");

        return result;
    }

    /*-------------------------------------------------
     *
     *  identifyPlayer(int normID, int equipID, String name, int lastSelectedRow)
     *
     *  DESCRIPTION: Searches the playerlist for a player
     *  that matches the given criteria
     * 
     *  A player is located with the following order
     *  of importance: Name -> Normal ID -> Equipment ID
     * 
     *  If multiple players are found, the first player that matches the
     *  lastSelectedRow criteria is returned
     * 
     *  in question is returned
     *  Returns null if no player is found
     *
    ------------------------------------------------- */
    public Player identifyPlayer(int normID, int equipID, String name, int lastSelectedRow) {

        ArrayList <Player> possiblePlayers = new ArrayList<Player>();

        // Attempt to locate all possible players by name, then normal id, then equip ID
        for (Player player : playerList) {

            if (player.name.equals(name) && !name.equals("NULL")) {
                possiblePlayers.add(player);
            }
            else if (player.getNormalID() == normID && normID != -1) {
                possiblePlayers.add(player);
            }
            else if (player.getEquipID() == equipID && equipID != -1) {
                possiblePlayers.add(player);
            }
        }

        // Return the first player that matches the lastSelectedRow criteria
        for (Player player : possiblePlayers) {

            if (player.rowIdentifier == lastSelectedRow && player.rowIdentifier != -1) {
                return player;
            }
        }

        // Return the first found match if we can't find the previous criteria
        if (possiblePlayers.size() > 0) {
            System.out.println("Couldn't match " + lastSelectedRow + " row, returning " + possiblePlayers.get(0).name + " at " + possiblePlayers.get(0).getNormalID());
            return possiblePlayers.get(0);
        }

        // If no match is found on either list, then return null
        System.out.println("No player match found " + normID);
        return null;
    }

    /*-------------------------------------------------
     *
     *  addComponentToRemoveQueue(Player p, JTextField f, String s)
     *
     *  DESCRIPTION: Adds both input components to the
     *  removal list to safely remove them from the screen
     *  without causing a ConcurrentModificationException
     *
    ------------------------------------------------- */
    public void addComponentToRemoveQueue(Player p, JTextField f, String s) {

        // Check that the queue doesn't contain the same object already
        if (removePlayerQueue.contains(p) || setTBQueue.contains(f) || dataTBQueue.contains(s))
            return;

        removePlayerQueue.add(p);
        setTBQueue.add(f);
        dataTBQueue.add(s);
    }

    public void setNet(NetController n) {
        netController = n;
    }

    /*-------------------------------------------------
     *
     *  Player[] processNetworkData(String data)
     *
     *  DESCRIPTION: Parses a received network code
     * 
     *  Returns an array with two integers, those received
     *  in the received packet
     * 
     *  Will return an empty array with -1 entries
     *  if error occurs
     *
    ------------------------------------------------- */
    private Integer[] processNetworkData(String data) {
        int playerIdentifierParam1 = -1;
        int playerIdentifierParam2 = -1;
        Integer[] returnPlayers = new Integer[2];

        // Splice the string for the number:number format if possible, and return to two integers
        String[] tempSplitString = data.split(":");

        // Parse the first part of the received data
        try {
            playerIdentifierParam1 = Integer.parseInt(tempSplitString[0]);
        } catch (Exception e) {
            System.out.println("[Model] Error parsing player 1 network data. '" + tempSplitString[0] + "'");
        }

        // Parse the second part of the received data
        try {
            tempSplitString[1] = tempSplitString[1].replaceAll("\0", "");
            playerIdentifierParam2 = Integer.parseInt(tempSplitString[1]);
        } catch (Exception e) {
            System.out.println("[Model] Error parsing player 2 network data. '" + tempSplitString[1] + "'");
            System.out.println(tempSplitString[1].length());
        }

        //Player firstIdentifiedPlayer = identifyPlayer(playerIdentifierParam1, -1, "NULL", -1);
        //Player secondIdentifiedPlayer = identifyPlayer(playerIdentifierParam2, -1, "NULL", -1);
        returnPlayers[0] = playerIdentifierParam1;
        returnPlayers[1] = playerIdentifierParam2;

        return returnPlayers;
    }

    public String calculateGameWinner() {
        int rScore = 0, gScore = 0;

        for (int i = 0; i < getRedTeamPlayerListSize(); i++) {
            rScore += getPlayer(getRedTeamPlayerListAt(i)).getScore();
        }

        for (int i = 0; i < getGreenTeamPlayerListSize(); i++) {
            gScore += getPlayer(getGreenTeamPlayerListAt(i)).getScore();
        }

        if (rScore > gScore) {
            return "RED TEAM WINS!!!";
        }
        else if (gScore > rScore) {
            return "GREEN TEAM WINS!!!";
        }
        else {
            return "TIE GAME! NO WINNERS!";
        }
    }

    public void gameRestartDataReset() {

    }
}