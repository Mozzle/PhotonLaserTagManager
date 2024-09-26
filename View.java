import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.text.PlainDocument;
import javax.swing.JComponent;
import javax.swing.text.AttributeSet;

import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.KeyboardFocusManager;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Component;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.management.AttributeList;

import java.io.IOException;
import java.lang.reflect.Array;
import java.io.File;
import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import java.util.Timer;
import java.util.TimerTask;

public class View extends JPanel {

    private Model model;
    public NetController netController;
    private int windowHeight, windowWidth, PlayerEntryPanePadding;
    private boolean inPlayerEntryScreen, inCountDownScreen, inGameScreen;
    public JPanel RedTeamTextBoxPane, GreenTeamTextBoxPane, PlayerEntryPanes;
    public JLabel toolTipLabel;
    public ArrayList<JLabel> rowSelectionLabel;
    public Timer timer;
    public int toolTipCounter, prevToolTipCounter;
    public int lastSelectedRow;
    public char lastSelectedTeam;
    public JButton ClearScreenButton, StartGameButton, NewPlayerButton, SettingsButton;
    public Component currentFocus;


    // Variable declarations

    /*-------------------------------------------------
     *
     *  View()
     *
     *  DESCRIPTION: View Class Initializer
     *
     *  REQUIREMENTS:
     *
    ------------------------------------------------- */

    public View(Controller c, Model m, NetController n)
    {
        // Set references to the controller and model
        c.setView(this);
        model = m;
        netController = n;

        // Initialize current UI state, or flags
        inPlayerEntryScreen = false;
        inCountDownScreen = false;
        inGameScreen = false;

        // Create the red and green team panes, set them to visible
        RedTeamTextBoxPane = new JPanel();
        RedTeamTextBoxPane.setVisible(false);
        GreenTeamTextBoxPane = new JPanel();
        GreenTeamTextBoxPane.setVisible(false);

        // Create the timer for the tooltips
        timer = new Timer();
        toolTipCounter = 0;
        prevToolTipCounter = 0;
        rowSelectionLabel = new ArrayList<JLabel>();
        lastSelectedRow = 99;
        lastSelectedTeam = 'R';
        PlayerEntryPanePadding = 0;
    }

    /*-------------------------------------------------
     *
     *  paintComponent()
     *
     *  DESCRIPTION: Draws the images to the screen
     *
     *  REQUIREMENTS:
     *
    ------------------------------------------------- */
    public void paintComponent(Graphics g)
    {
        // Set background color to black
        g.setColor(new Color(0, 0, 0));
        g.fillRect(0, 0, this.getWidth(), this.getHeight());

        if (model.getNumWindowObjects() != 0) {
            try
		    {
		    	for(int i = 0; i < model.getNumWindowObjects(); i++) // Iterate through all sprites, drawing them on the screen
		    	{											   // And accounting for the scroll position
		    		Sprite sprite = model.getWindowObjectAt(i);
		    		g.drawImage(sprite.getImage(), sprite.getX(), sprite.getY(), sprite.getW(), sprite.getH(), null);
		    	}
		    }
		    catch(Exception e) 
		    {
		    	System.out.println("Error drawing to screen. Please ensure textures are correctly loaded.");
 		    	e.printStackTrace(System.err);
    	    	System.exit(1);
		    }
        }
    }

    /*-------------------------------------------------
     *
     *  loadImage()
     *
     *  DESCRIPTION: loads image for a Sprite object,
     *  given a file link.
     *
     *  REQUIREMENTS:
     *
    ------------------------------------------------- */
    static BufferedImage loadImage(String filename) {	// Static image loading 
		BufferedImage img = null;
		try {
		img = ImageIO.read(new File(filename));
		} catch (Exception e) {
			System.out.println("Error loading " + filename);
 			e.printStackTrace(System.err);
    		System.exit(1);
		}
		return img;
	}

    /*-------------------------------------------------
     *
     *  setScreenSize()
     *
     *  DESCRIPTION: PhotonSystem.java gives view.java 
     *  the current window height for proper sizing action
     *
     *  REQUIREMENTS:
     *
    ------------------------------------------------- */
    public void setScreenSize(int w, int h) { 
		windowWidth = w;
        windowHeight = h;
	}

    /*--------------------------------------------------
     * 
     *  update()
     * 
     *  DESCRIPTION: This function is used to update
     *  model and is the entry point for changing from
     *  the one screen to another.
     * 
     *  REQUIREMENTS: 
     -------------------------------------------------*/
    public void update() {
        model.updateScreenSize(windowWidth, windowHeight);
        
        // Block that handles the PLAYER ENTRY SCREEN
        if (model.getSystemState() == Model.PLAYER_ENTRY_SCREEN) {
             
            if (!inPlayerEntryScreen) { // If first time in Player Entry Screen
            inPlayerEntryScreen = true;
            this.drawPlayerEntryScreen();
            }
            
            // Adjust padding on the outsides of the red and green panes
            // This ensures that both the red team and green team panes will be horizontally
            // aligned with one another no matter the window sizing.
            if (PlayerEntryPanePadding != (int)((windowWidth - 880)/ 2)) {
                PlayerEntryPanePadding = (int)((windowWidth - 880)/ 2);
                PlayerEntryPanes.setBorder(new EmptyBorder(0, PlayerEntryPanePadding, 0, PlayerEntryPanePadding));
            }

            /*-----------------------------------------------------
            If model has a new tooltip for us to add, add it
            and update any existing tooltips
            -----------------------------------------------------*/
            handleToolTipDrawing();

            /*-----------------------------------------------------
            Update the '>>>>' row selection arrows and handle 
            valid Player/Equipment ID input detection
            -----------------------------------------------------*/
            // Check if a text field has a current focus, if so handle it
            if (KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner() != null) {
                handleSystemFocus();
            }
            
            // Check if model is telling us to do create a New Player Popup window
            if (model.getMakePlayerPopupFlag()) {
                model.setMakePlayerPopupFlag(false);
                NewPlayerPopupScreen("", null, null, "Enter new Player information", "");
            }

            // Check if the model is telling us to create a new settings window
            if (model.getMakeSettingsPopupFlag()) {
                model.setMakeSettingsPopupFlag(false);
                // Create a new settings window
                NewSettingsScreen();
            }
        }

        // Block that handles the COUNTDOWN SCREEN
        if (model.getSystemState() == Model.COUNTDOWN_SCREEN) {
        
            // If it's our first time in the COUNTDOWN SCREEN
            if (!inCountDownScreen) {
            inPlayerEntryScreen = false;
            inCountDownScreen=true; 
            this.PlayerEntryScreenDeleter(); 
            this.drawCountDownScreen();
            }

        }

        // Block that handles the GAME SCREEN
        if (model.getSystemState() == Model.PLAY_ACTION_SCREEN && !inGameScreen) {
            inCountDownScreen = false;
            inGameScreen = true;
            // TODO: Link a method here that handles all the sprites and objects
            // for the game screen. Or implement it here directly.
        }
        
    }

    /*--------------------------------------------------
     * 
     *  handleSystemFocus()
     * 
     *  DESCRIPTION: Cleaner method to handle when an
     *  object is given focus in the view. Called from 
     *  the player entry screen to do the following:
     *  
     *  Update the shaded backgrounds of text fields to 
     *  show they are selected.
     * 
     *  Update the '>>>>' selection arrows
     * 
     *  Validate proper entry of Player ID and Equipment
     *  ID fields and warn user of invalid inputs.
     * 
     *  REQUIREMENTS: 0021
     *  
     -------------------------------------------------*/

     public void handleSystemFocus() {
        // Attempt to retrieve a reference to the current focused component
        try {
            // Create a reference to the last object that has been focused
            Component LastFocusedComponent = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
            if (LastFocusedComponent.getName() != null) {
                // Create an indication to the user that this row is selected
                LastFocusedComponent.setBackground(Color.LIGHT_GRAY);
            }
            String selectedRow = LastFocusedComponent.getName();
        
            // Check that the selected row is different from the last selected row
            if ((selectedRow.length() == 2 || selectedRow.length() == 3) && (lastSelectedRow != Integer.valueOf(selectedRow.substring(1))
            || (lastSelectedTeam != selectedRow.charAt(0)))) {
                
                // If we have selected a different row, then focus has changed.
                // Check the ID of the previously selected row against the database.
                if (lastSelectedRow <= model.getNumPlayerIDBoxes()) {

                    // Wrap this in a try/catch block to prevent errors from crashing the program
                    try {
                        // Declare temporary variables to hold ID and index
                        int idToCheck = -1;
                        int indexToCompare = -1;

                        // Grab the ID and Database Index based on the selected team
                        if (lastSelectedTeam == 'R') {
                            // If the Player ID field is not empty, get the int value of the field
                            if (!model.PlayerIDBoxes.get(lastSelectedRow).getTextFromField().equals("")) {
                                idToCheck = Integer.valueOf(model.PlayerIDBoxes.get(lastSelectedRow).getTextFromField());
                            }
                            indexToCompare = lastSelectedRow;
                        }
                        else if (lastSelectedTeam == 'G') {
                            // If the Player ID field is not empty, get the int value of the field
                            if (!model.PlayerIDBoxes.get(lastSelectedRow + Model.NUM_MAX_PLAYERS_PER_TEAM).getTextFromField().equals("")) {
                                idToCheck = Integer.valueOf(model.PlayerIDBoxes.get(lastSelectedRow + Model.NUM_MAX_PLAYERS_PER_TEAM).getTextFromField());
                            }
                            indexToCompare = lastSelectedRow + Model.NUM_MAX_PLAYERS_PER_TEAM;
                        }

                        // If the Player ID Field was empty
                        if (idToCheck == -1) {
                            //AND the Equipment ID field was not empty, print a tool tip
                            if (lastSelectedTeam == 'R' && !model.EquipmentIDBoxes.get(lastSelectedRow).getTextFromField().equals("")) {
                                model.toolTip("Invalid Player ID for Red Team Player " + lastSelectedRow, 4500);
                            }
                            else if (lastSelectedTeam == 'G' && !model.EquipmentIDBoxes.get(lastSelectedRow + Model.NUM_MAX_PLAYERS_PER_TEAM).getTextFromField().equals("")) {
                                model.toolTip("Invalid Player ID for Green Team Player " + lastSelectedRow, 4500);
                            }
                        }
                        // If the Player ID Field was not empty
                        else {
                            // Query database for a codename to match the entered ID.
                            String codename = model.database.searchDB(Database.PARAM_ID, idToCheck, "");
                            
                            // If our codename already exists for this ID, return a tooltip to the user
                            if (codename != "") {
                                boolean notADuplicate = true;
                                for (int i = 0; i < (Model.NUM_MAX_PLAYERS_PER_TEAM * 2); i++) {
                                    // Ensure that the ID doesn't exist in another textbox locally
                                    if (String.valueOf(idToCheck).equals(model.PlayerIDBoxes.get(i).getTextFromField()) && (i != indexToCompare)) {
                                        // TODO: Add logic here that removes the offending/duplicate ID from the local text field.
                                        model.toolTip(codename + " (ID: " + idToCheck + ") is already in this game!", 4500);
                                        notADuplicate = false;
                                    }
                                }

                                // We have added a player to the game and they are not a duplicate
                                if (notADuplicate) {
                                    if (lastSelectedTeam == 'R') {
                                        model.getCodenameBoxAt(lastSelectedRow).setText(codename);
                                    }
                                    else if (lastSelectedTeam == 'G') {
                                        model.getCodenameBoxAt(lastSelectedRow + Model.NUM_MAX_PLAYERS_PER_TEAM).setText(codename);
                                    }
                                    model.toolTip(codename + " added successfully!", 4500);
                                }
                            }
                            // If we are not connected to the database.
                            else if (model.database.getdbConnectionStatus() == false && !model.getDebugMode()) {
                                model.toolTip("No database connection! Game will not work!",10000);
                            }
                            // If ID does not exist in the database and the connection is good, add the ID to the DB
                            else {
                                // Create a popup window to prompt the user to add a new player
                                String popupInputID = "";
                                JTextField NewPlayerIDField = new JTextField();
                                JTextField PlayerCodenameField = new JTextField();

                                // Grab the ID based on the selected team
                                if (lastSelectedTeam == 'R') {
                                    popupInputID = model.PlayerIDBoxes.get(lastSelectedRow).getTextFromField();
                                    NewPlayerIDField = model.PlayerIDBoxes.get(lastSelectedRow).getTextBox();
                                    PlayerCodenameField = model.CodenameBoxes.get(lastSelectedRow).getTextBox();
                                }
                                else if (lastSelectedTeam == 'G') {
                                    popupInputID = model.PlayerIDBoxes.get(lastSelectedRow + Model.NUM_MAX_PLAYERS_PER_TEAM).getTextFromField();
                                    NewPlayerIDField = model.PlayerIDBoxes.get(lastSelectedRow + Model.NUM_MAX_PLAYERS_PER_TEAM).getTextBox();
                                    PlayerCodenameField = model.CodenameBoxes.get(lastSelectedRow + Model.NUM_MAX_PLAYERS_PER_TEAM).getTextBox();
                                }

                                // Create the New Player Entry Popup screen
                                NewPlayerPopupScreen(popupInputID, NewPlayerIDField, PlayerCodenameField, "Unknown Player ID entered, would", "you like to create a new Player?");
                            }
                        }

                        String EquipmentIdToSend = model.EquipmentIDBoxes.get(indexToCompare).getTextFromField();
                        //If Equipment ID field is not empty and is not the game start/end code
                        if (!EquipmentIdToSend.equals("") && !EquipmentIdToSend.equals("202") && !EquipmentIdToSend.equals("221")) {
                            //TODO!: Check all other Equipment ID fields for this equipment ID, make sure that that equipment ID doesn't already exist in the game
                            //before sending out the UDP.
                            //Make this ^ a function in model and do some real modular programming, silly
                            boolean tmpDuplicateFlag = model.checkEquipmentIDFieldsForDupicates(indexToCompare);

                            if (tmpDuplicateFlag) {
                                model.toolTip("This Equipment ID is already in use in this game!", 4500);
                            }
                            else {
                                netController.transmit(model.EquipmentIDBoxes.get(indexToCompare).getTextFromField());
                            }
                            
                        }

                        // Else if there IS a player ID entered, and an invalid Equipment ID entered,
                        // Print a tooltip stating that the Equipment ID is invalid
                        else if (idToCheck != -1) {
                            String tmpTeam = "";
                            if (indexToCompare < Model.NUM_MAX_PLAYERS_PER_TEAM) {
                                tmpTeam = "Red";
                            }
                            else {
                                tmpTeam = "Green";
                            }
                            // "Invalid Equipment ID for Red team Player 5"
                            model.toolTip("Invalid Equipment ID for " + tmpTeam + " Team Player " + (indexToCompare % Model.NUM_MAX_PLAYERS_PER_TEAM), 4500);
                        }
                    }
                    catch (Exception e) {
                       // Do nothing
                    }
                }
                

                lastSelectedRow = Integer.valueOf(selectedRow.substring(1));
                lastSelectedTeam = selectedRow.charAt(0);
            
                for (int i = 0; i < rowSelectionLabel.size(); i++) {    // Clear out all other rows
                    rowSelectionLabel.get(i).setText("           ");
                }
                for (int i = 0; i < model.getNumPlayerIDBoxes(); i++) {
                    model.getPlayerIDBoxAt(i).setBackground(Color.WHITE);
                }
                for (int i = 0; i < model.getNumEquipmentIDBoxes(); i++) {
                    model.getEquipmentIDBoxAt(i).setBackground(Color.WHITE);
                }

                if (LastFocusedComponent.getParent() != null) {
                    if (lastSelectedTeam == 'R') {
                        rowSelectionLabel.get(lastSelectedRow).setBackground(Color.BLACK);
                        rowSelectionLabel.get(lastSelectedRow).setText("  >>>>");
                    }
                    else if (lastSelectedTeam == 'G') {
                        rowSelectionLabel.get(lastSelectedRow + Model.NUM_MAX_PLAYERS_PER_TEAM).setText("  >>>>");
                    }
                }
            }
            
        }
        catch (Exception e) {
            //e.printStackTrace();
            //System.err.println(e.getClass().getName()+": "+e.getMessage());
        }

     }


    /*--------------------------------------------------
     * 
     *  drawPlayerEntryScreen()
     * 
     *  DESCRIPTION: Creates and draws the Player Entry
     *  Screen, and all Jpanels and elements therein.
     * 
     *  REQUIREMENTS: 0009, 0021
     -------------------------------------------------*/

     /*                     PLAYER ENTRY PAGE LAYOUT
      *
      *     -------[PlayerEntryPanes]----------------------------------------------------------------------
      *     |         |--------[RedTeamTextBoxPane]--------||------[GreenTeamTextBoxPane]--------|        |
      *     |         ||----------[TextFieldsR]-----------||||-----------[TextFieldsG]----------||        |
      *     |         ||                                  ||||                                  ||        |
      *     |         ||             RED TEAM             ||||            GREEN TEAM            ||        |
      *     |         ||  Player ID       Equipment ID    ||||  Player ID       Equipment ID    ||        |
      *     |         || |||||||||||||||  ||||||||||||||  |||| |||||||||||||||  ||||||||||||||  ||        |
      *     |         || |||||||||||||||  ||||||||||||||  |||| |||||||||||||||  ||||||||||||||  ||        |
      *     |         || |||||||||||||||  ||||||||||||||  |||| |||||||||||||||  ||||||||||||||  ||        |
      *     |         || |||||||||||||||  ||||||||||||||  |||| |||||||||||||||  ||||||||||||||  ||        |
      *     |         || |||||||||||||||  ||||||||||||||  |||| |||||||||||||||  ||||||||||||||  ||        |
      *     |         || |||||||||||||||  ||||||||||||||  |||| |||||||||||||||  ||||||||||||||  ||        |
      *     |         || |||||||||||||||  ||||||||||||||  |||| |||||||||||||||  ||||||||||||||  ||        |
      *     |         || |||||||||||||||  ||||||||||||||  |||| |||||||||||||||  ||||||||||||||  ||        |
      *     |         || |||||||||||||||  ||||||||||||||  |||| |||||||||||||||  ||||||||||||||  ||        |
      *     |         || |||||||||||||||  ||||||||||||||  |||| |||||||||||||||  ||||||||||||||  ||        |
      *     |         || |||||||||||||||  ||||||||||||||  |||| |||||||||||||||  ||||||||||||||  ||        |
      *     |         || |||||||||||||||  ||||||||||||||  |||| |||||||||||||||  ||||||||||||||  ||        |
      *     |         || |||||||||||||||  ||||||||||||||  |||| |||||||||||||||  ||||||||||||||  ||        |
      *     |         || |||||||||||||||  ||||||||||||||  |||| |||||||||||||||  ||||||||||||||  ||        |
      *     |         || |||||||||||||||  ||||||||||||||  |||| |||||||||||||||  ||||||||||||||  ||        |
      *     |         ||                                  ||||                                  ||        |
      *     |         ||----------------------------------||||----------------------------------||        |
      *     |         |                                    ||                                   ||        |
      *     |         |                                    ||                                    |        |
      *     |         |------------------------------------||------------------------------------|        |
      *     ||-------------------------------------------------------------------------------------------||
      *     |                                                                                             |
      *     |                                                                                             |
      *     |----------------[Buttons]--------------------------------------------------------------------|
      *     ||-----------------------------[ButtonsCenter]-----------------------------------------------||
      *     ||                            |----------------||----------------|                           ||
      *     ||                            |                ||                |                           ||
      *     ||                            |  Clear Screen  ||   Start Game   |                           ||
      *     ||                            |[ClearScreenBtn]|| [StartGameBtn  |                           ||
      *     ||                            |----------------||----------------|                           ||
      *     ||-------------------------------------------------------------------------------------------||
      *     |----------------------------------------------------------------------------------------------
      */
    public void drawPlayerEntryScreen() {
        LayoutManager layout = new FlowLayout();
        JLabel tmpJLabel;
        this.setLayout(new BorderLayout());


        /*-------------
         * Buttons
        --------------*/

        KeyAdapter buttonKeys = model.getStandardKeyAdapter();

        JPanel Buttons = new JPanel();
        Buttons.setLayout(new BoxLayout(Buttons, BoxLayout.X_AXIS));
        Buttons.setBackground(Color.BLACK);
        Buttons.setOpaque(false);

        JPanel ButtonsCenter = new JPanel();
        ButtonsCenter.setBackground(Color.BLACK);
        ButtonsCenter.setLayout(new FlowLayout(FlowLayout.CENTER));

        ClearScreenButton = new JButton("(F1) - Clear Screen");
        ClearScreenButton.setPreferredSize(new Dimension(225, 60));
        ClearScreenButton.setMaximumSize(new Dimension(225, 60));
        ClearScreenButton.setBackground(new Color(0, 66, 32));
        ClearScreenButton.setForeground(Color.WHITE);
        ClearScreenButton.addKeyListener(buttonKeys);
        ClearScreenButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e){  
            if (model.getNewPopup() == false) {
				model.clearTextBoxes();
			}
        }  
        }); 
        ButtonsCenter.add(ClearScreenButton);

        StartGameButton = new JButton("(F5) - Start Game");
        StartGameButton.setPreferredSize(new Dimension(225, 60));
        StartGameButton.setMaximumSize(new Dimension(225, 60));
        StartGameButton.setBackground(new Color(0, 66, 32));
        StartGameButton.setForeground(Color.WHITE);
        StartGameButton.addKeyListener(buttonKeys);
        StartGameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                if (model.getNewPopup() == false) {
				    if (model.checkStartGameConditions()) {
				    	model.PlayerEntryScreenDeleter();
				    }
				}
            }  
        }); 
        ButtonsCenter.add(StartGameButton);

        NewPlayerButton = new JButton("(F9) - Add New Player");
        NewPlayerButton.setPreferredSize(new Dimension(225, 60));
        NewPlayerButton.setMaximumSize(new Dimension(225, 60));
        NewPlayerButton.setBackground(new Color(0, 66, 32));
        NewPlayerButton.setForeground(Color.WHITE);
        NewPlayerButton.addKeyListener(buttonKeys);
        NewPlayerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                NewPlayerPopupScreen("", null, null, "Enter new Player information", "");
            }  
        });
        ButtonsCenter.add(NewPlayerButton);

        SettingsButton = new JButton("(F12) - Settings");
        SettingsButton.setPreferredSize(new Dimension(225, 60));
        SettingsButton.setMaximumSize(new Dimension(225, 60));
        SettingsButton.setBackground(new Color(0, 66, 32));
        SettingsButton.setForeground(Color.WHITE);
        SettingsButton.addKeyListener(buttonKeys);
        SettingsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                NewSettingsScreen();
            }  
        });
        ButtonsCenter.add(SettingsButton);

        ButtonsCenter.setOpaque(false);

        Buttons.add(ButtonsCenter);
        this.add(Buttons, BorderLayout.SOUTH);
        Buttons.setVisible(true);


        /*------------------------------------
         * Main Container for Text Entry Box
         * Panes
        -------------------------------------*/
        PlayerEntryPanes = new JPanel();
        PlayerEntryPanes.setBackground(new Color (0, 0, 0, 255));
        PlayerEntryPanes.setPreferredSize(new Dimension(900, 900));
        PlayerEntryPanes.setBorder(new EmptyBorder(0, 100, 0, 100));
        PlayerEntryPanes.setOpaque(false);
        
        RedTeamTextBoxPane.setBackground(new Color(207, 0, 0));
        RedTeamTextBoxPane.setPreferredSize(new Dimension(425, 600));
        //RedTeamTextBoxPane.setBorder(new EmptyBorder(0, -100, 0, 0));
        RedTeamTextBoxPane.setLayout(layout);

        GreenTeamTextBoxPane.setBackground(new Color(10, 160, 0));
        GreenTeamTextBoxPane.setPreferredSize(new Dimension(425, 600));
        //GreenTeamTextBoxPane.setBorder(new EmptyBorder(0, 0, 0, -100));
        GreenTeamTextBoxPane.setLayout(layout);

        // Set up the 'Text Fields' panel that will be placed inside the RedTeamTextBoxPane
        JPanel TextFieldsR = new JPanel(new GridBagLayout());
        TextFieldsR.setName("RedTextFields");
        GridBagConstraints tFR = new GridBagConstraints();
        tFR.fill = GridBagConstraints.BOTH;
        tFR.anchor = GridBagConstraints.NORTH;
        tFR.ipady = 25;
        TextFieldsR.setBackground(new Color(175, 31, 0));

        // 'Red Team' label
        tmpJLabel = new JLabel("Red Team", SwingConstants.CENTER);
        tmpJLabel.setFont(new Font("Verdana", Font.BOLD, 22));
        tmpJLabel.setForeground(Color.WHITE);
        tFR.gridx = 0;
        tFR.gridy = 0;
        tFR.gridwidth = 5;
        TextFieldsR.add(tmpJLabel, tFR);
        tFR.ipady = 15;

        // Set up the 'Text Fields' panel that will be placed inside the GreenTeamTextBoxPane
        JPanel TextFieldsG = new JPanel(new GridBagLayout());
        TextFieldsG.setName("GreenTextFields");
        GridBagConstraints tFG = new GridBagConstraints();
        tFG.fill = GridBagConstraints.BOTH;
        tFG.anchor = GridBagConstraints.NORTH;
        tFG.ipady = 25;
        TextFieldsG.setBackground(new Color(8, 120, 0));

        // 'Green Team' label
        tmpJLabel = new JLabel("Green Team", SwingConstants.CENTER);
        tmpJLabel.setFont(new Font("Verdana", Font.BOLD, 22));
        tmpJLabel.setForeground(Color.WHITE);
        tFG.gridx = 0;
        tFG.gridy = 0;
        tFG.gridwidth = 5;
        TextFieldsG.add(tmpJLabel, tFG);
        tFG.ipady = 15;

        //Draw "Player ID", Red Team
        tmpJLabel = new JLabel("Player ID", SwingConstants.CENTER);
        tmpJLabel.setForeground(Color.WHITE);
        tFR.weightx = 0.5;
        tFR.gridx = 2;
        tFR.gridy = 1;
        tFR.gridwidth = 1;
        TextFieldsR.add(tmpJLabel, tFR);
        
        // Draw "Player ID", Green Team
        tmpJLabel = new JLabel("Player ID", SwingConstants.CENTER);
        tmpJLabel.setForeground(Color.WHITE);
        tFG.weightx = 0.5;
        tFG.gridx = 2;
        tFG.gridy = 1;
        tFG.gridwidth = 1;
        TextFieldsG.add(tmpJLabel, tFG);

        // Draw "Equipment ID", Red Team
        tmpJLabel = new JLabel("Equipment ID", SwingConstants.CENTER);
        tmpJLabel.setForeground(Color.WHITE);
        tFR.gridx = 3;
        tFR.gridy = 1;
        tFR.gridwidth = 1;
        TextFieldsR.add(tmpJLabel, tFR);

        // Draw "Equipment ID", Green Team
        tmpJLabel = new JLabel("Equipment ID", SwingConstants.CENTER);
        tmpJLabel.setForeground(Color.WHITE);
        tFG.gridx = 3;
        tFG.gridy = 1;
        tFG.gridwidth = 1;
        TextFieldsG.add(tmpJLabel, tFG);

        // Draw "Codeame", Red Team
        tmpJLabel = new JLabel("       Codename       ", SwingConstants.CENTER);
        tmpJLabel.setForeground(Color.WHITE);
        tFR.gridx = 4;
        tFR.gridy = 1;
        tFR.gridwidth = 1;
        TextFieldsR.add(tmpJLabel, tFR);

        // Draw "Codeame", Green Team
        tmpJLabel = new JLabel("       Codename       ", SwingConstants.CENTER);
        tmpJLabel.setForeground(Color.WHITE);
        tFG.gridx = 4;
        tFG.gridy = 1;
        tFG.gridwidth = 1;
        TextFieldsG.add(tmpJLabel, tFG);

        tFR.ipady = 5;
        tFG.ipady = 5;

        if (model.getNumPlayerIDBoxes() != 0) {
            // Draw the Red Team Text Boxes row by row
            for (int i = 0; i < Model.NUM_MAX_PLAYERS_PER_TEAM; i++) {
                // ">>>>>" Row selection labels
                tFR.weightx = 0.1;
                tFR.gridx = 0;
                tFR.gridy = i + 2;
                tFR.gridwidth = 1;
                rowSelectionLabel.add(new JLabel("           ", SwingConstants.LEFT));
                rowSelectionLabel.get(i).setForeground(Color.WHITE);
                TextFieldsR.add(rowSelectionLabel.get(i), tFR);

                // Row number 0 - 14
                tFR.weightx = 0.3;
                tFR.gridx = 1;
                tFR.gridy = i + 2;
                tFR.gridwidth = 1;
                tmpJLabel = new JLabel(String.valueOf(i), SwingConstants.CENTER);
                //tmpJLabel.setFont(new Font("Arial", Font.BOLD, 14));
                tmpJLabel.setForeground(Color.WHITE);
                TextFieldsR.add(tmpJLabel, tFR);
                
                // Player ID Box
                tFR.weightx = 0.5;
                tFR.gridx = 2;
                tFR.gridy = i + 2;
                tFR.gridwidth = 1;
                TextFieldsR.add(model.getPlayerIDBoxAt(i), tFR);
                
                // Equipment ID Box
                tFR.gridx = 3;
                tFR.gridy = i + 2;
                tFR.gridwidth = 1;
                TextFieldsR.add(model.getEquipmentIDBoxAt(i), tFR);

                // Codename Box
                tFR.gridx = 4;
                tFR.gridy = i + 2;
                tFR.gridwidth = 1;
                TextFieldsR.add(model.getCodenameBoxAt(i), tFR);
            }
            // Draw the Green Team Text Fields row by row
            for (int i = Model.NUM_MAX_PLAYERS_PER_TEAM; i < (Model.NUM_MAX_PLAYERS_PER_TEAM * 2); i++) {
                // ">>>>" Row selection labels
                tFG.weightx = 0.1;
                tFG.gridx = 0;
                tFG.gridy = i + 2;
                tFG.gridwidth = 1;
                rowSelectionLabel.add(new JLabel("           ", SwingConstants.LEFT));
                rowSelectionLabel.get(i).setForeground(Color.WHITE);
                TextFieldsG.add(rowSelectionLabel.get(i), tFG);

                // Row number 0-14
                tFG.weightx = 0.3;
                tFG.gridx = 1;
                tFG.gridy = i + 2;
                tFG.gridwidth = 1;
                tmpJLabel = new JLabel(String.valueOf(i - Model.NUM_MAX_PLAYERS_PER_TEAM), SwingConstants.CENTER);
                tmpJLabel.setForeground(Color.WHITE);
                TextFieldsG.add(tmpJLabel, tFG);

                // Player ID box
                tFG.weightx = 0.5;
                tFG.gridx = 2;
                tFG.gridy = i + 2;
                tFG.gridwidth = 1;
                TextFieldsG.add(model.getPlayerIDBoxAt(i), tFG);

                // Equipment ID Box
                tFG.gridx = 3;
                tFG.gridy = i + 2;
                tFG.gridwidth = 1;
                TextFieldsG.add(model.getEquipmentIDBoxAt(i), tFG);

                // Codename Box
                tFG.gridx = 4;
                tFG.gridy = i + 2;
                tFG.gridwidth = 1;
                TextFieldsG.add(model.getCodenameBoxAt(i), tFG);
            }
            TextFieldsR.setPreferredSize(new Dimension(415, 587));
            TextFieldsG.setPreferredSize(new Dimension(415, 587));
            RedTeamTextBoxPane.add(TextFieldsR, tFR);
            GreenTeamTextBoxPane.add(TextFieldsG, tFG);
            PlayerEntryPanes.add(RedTeamTextBoxPane);
            PlayerEntryPanes.add(GreenTeamTextBoxPane);
            //this.add(RedTeamTextBoxPane, BorderLayout.CENTER);
            //this.add(GreenTeamTextBoxPane, BorderLayout.CENTER);
            this.add(PlayerEntryPanes, BorderLayout.NORTH);

        }
        RedTeamTextBoxPane.setVisible(true);
        GreenTeamTextBoxPane.setVisible(true);

    }


    /*--------------------------------------------------
     * 
     *  drawCountDownScreen()
     * 
     *  DESCRIPTION: Creates and draws the Countdown 
     *  Screen prior to the start of a game.
     * 
     *  REQUIREMENTS: 0013,
     -------------------------------------------------*/

    public void drawCountDownScreen(){
        //LayoutManager layout = new FlowLayout();
        //JLabel tmpJLabel;
       // ImageIcon Icon; 
        //JFrame frame = new JFrame("CountDown");

        //this.setLayout(new BorderLayout());

        
        //system.threadcount(30000);
        
        Timer timer = new Timer();
        
        ImageIcon imgIcon = new ImageIcon(this.getClass().getResource("Timer.gif"));
        JLabel label = new JLabel(imgIcon);
        label.setBounds(200, 200, 256, 256); // Adjust these values to suit your layout
        add(label);
        setVisible(true);

        
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
            setVisible(false);
            }
        };
        

        // Schedule the task to run every 30 seconds (30000 milliseconds)
        timer.schedule(task, 30000, 30000);
        
    }

    /*--------------------------------------------------
     * 
     *  toolTipTimeout.run()
     * 
     *  DESCRIPTION: Timer Task that executes when a 
     *  tool tip has timed out on the screen. Deletes 
     *  the tool Tip
     * 
     *  REQUIREMENTS: 
     * 
     --------------------------------------------------*/

    public class toolTipTimeout extends TimerTask
    {
        public void run()
        {
            if (toolTipCounter > 0) {
                View.this.PlayerEntryPanes.remove(View.this.PlayerEntryPanes.getComponentCount() - toolTipCounter);
                toolTipCounter--;
            }
        }
    }

    /*--------------------------------------------------
     * 
     *  PlayerEntryScreenDeleter()
     * 
     *  DESCRIPTION: Deletes the player entry screen.
     *  Used for transitioning into the Countdown 
     *  screen.
     * 
     *  REQUIREMENTS: 0013
     * 
     --------------------------------------------------*/

    public void PlayerEntryScreenDeleter(){
        for(int i=this.getComponentCount() - 1; i>= 0 ; i--)
            this.remove(i);        
    }

    /*--------------------------------------------------
     * 
     *  NewPlayerPopupScreen()
     * 
     *  DESCRIPTION: Creates a new Popup window that
     *  allows players to input information (Codename, ID)
     *  for a new Player.
     * 
     *  REQUIREMENTS: 0015
     * 
     --------------------------------------------------*/

    public boolean NewPlayerPopupScreen(String idInput, JTextField IDBox, JTextField NameBox, String hint1, String hint2) {

         // Ensure another popup isn't already open
         if (model.getNewPopup())
            return false;

        // Set popup flag
        model.setNewPopup(true);

        // Text fields on popup window
        JTextField NewPlayerName = new JTextField(10);
        JTextField NewPlayerID = new JTextField(5);

        // Input sanitation -- use a plain document as the method to push input, we can sanitize from here
        NewPlayerID.setDocument(new PlainDocument() {
            @Override
            public void insertString(int offset, String s, AttributeSet a) throws javax.swing.text.BadLocationException {
                // Insert nothing if there is no string
                if (s == null)
                    return;
                // Only insert if our string contains the values from 0-9
                if (s.matches("[0-9]+"))
                    super.insertString(offset, s, a);
            }
        });
        
        // Input sanitation -- use a plain document as the method to push input, we can sanitize from here
        NewPlayerName.setDocument(new PlainDocument() {
            @Override
            public void insertString(int offset, String s, AttributeSet a) throws javax.swing.text.BadLocationException {
                // Insert nothing if there is no string
                if (s == null)
                    return;
                if (NewPlayerName.getText().length() + s.length() <= 16) {
                // Only insert if our string contains the values from 0-9
                    if (s.matches("^[a-zA-Z0-9 ]*$"))
                        super.insertString(offset, s, a);
                }
            }
        });

        // Set the text field to the input ID
        NewPlayerID.setText(idInput);

        // Set the hint lines for the popup window
        String hintLine1 = hint1;
        String hintLine2 = hint2;

        // Flag to mark the popup window ready to close
        boolean closePopupFlag = false;
        
        while (!closePopupFlag) {
            // making the popup window and adding elements to it
            JPanel NewPlayerPopup = new JPanel();
            NewPlayerPopup.setPreferredSize(new Dimension(250, 150));
            NewPlayerPopup.add(new JLabel(hintLine1));
            NewPlayerPopup.add(Box.createVerticalStrut(15));
            NewPlayerPopup.add(new JLabel(hintLine2));
            NewPlayerPopup.add(Box.createVerticalStrut(15));
            NewPlayerPopup.add(Box.createVerticalStrut(35));
            NewPlayerPopup.add(new JLabel("New Player ID                  "));
            NewPlayerPopup.add(NewPlayerID, BorderLayout.EAST);
            NewPlayerPopup.add(Box.createVerticalStrut(15));
            NewPlayerPopup.add(new JLabel("New Player Name"));
            NewPlayerPopup.add(NewPlayerName,BorderLayout.EAST);
            NewPlayerPopup.add(Box.createVerticalStrut(25));

            JButton GenerateNewIDBtn = new JButton("Generate A New Player ID");
            GenerateNewIDBtn.setPreferredSize(new Dimension(240, 25));
            GenerateNewIDBtn.setMaximumSize(new Dimension(240, 25));
            GenerateNewIDBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e){
                    int newID = model.database.getNextAvailableID();

                    if (newID != -1) {
                        NewPlayerID.setText(String.valueOf(newID));
                    }
                }  
            });
            NewPlayerPopup.add(GenerateNewIDBtn);

            //Create the dialog popup with the ok/cancel buttons and wait for the window to be closed
            int result = JOptionPane.showConfirmDialog(null, NewPlayerPopup, "New Player Entry", JOptionPane.OK_CANCEL_OPTION);
            // If the user clicked the "OK" button
            if (result == JOptionPane.OK_OPTION) {

                int newPlayerIDVal = -1;
                // If the Player ID field String is not empty, get its value as an integer
                if (!NewPlayerID.getText().equals("")) {
                    newPlayerIDVal = Integer.valueOf(NewPlayerID.getText());
                }
                //Check if the entered ID exists in the database
                String searchResult = model.database.searchDB(Database.PARAM_ID, newPlayerIDVal, "");
                
                // If the entered ID doesn't exist in the DB, attempt to add the new player to the DB. 
                if (searchResult == "" && newPlayerIDVal > 0 && NewPlayerID.getText().length() >= 1 && NewPlayerName.getText().length() >= 1) {
                    if (IDBox != null) {
                        IDBox.setText(NewPlayerID.getText());
                    }
                    // If we are not connected to the database, print a tooltip
                    if (model.database.getdbConnectionStatus() == false && !model.getDebugMode()) {
                        model.toolTip("No database connection! Game will not work!",10000);
                    }
                    // If we are connected to the database, add the new user to the database
                    else if (model.database.insertDB(Database.PARAM_ID_AND_CODENAME, newPlayerIDVal, NewPlayerName.getText())) {
                        model.toolTip(NewPlayerName.getText() + " added successfully!", 4500);
                        if (NameBox != null) {
                            NameBox.setText(NewPlayerName.getText());
                        }
                    }

                    else {
                        model.toolTip("Error adding " + NewPlayerName.getText() + " to the database!", 4500);
                    }
                    closePopupFlag = true;
                }

                // If the entered Player ID is not a new ID
                else if (searchResult != "") {
                    hintLine1 = "The Player ID you entered already";
                    hintLine2 = "exists, please try again";
                }
                else if (NewPlayerID.getText().length() < 1) {
                    hintLine1 = "A Player ID must be entered.";
                    hintLine2 = " ";
                }
                else if (NewPlayerName.getText().length() < 1) {
                    hintLine1 = "A Player Name must be entered.";
                    hintLine2 = " ";
                }
                else {
                    hintLine1 = "You did something really weird,";
                    hintLine2 = "something went wrong.";
                }
            
            // If the user hit cancel or close on the window
            }
            else if (result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION) {
                if (IDBox != null) {
                    IDBox.setText("");
                }
                closePopupFlag = true;
            }
        }

        model.setNewPopup(false);
        return true;
    }

    /*--------------------------------------------------
     * 
     *  NewSettingsScreen()
     * 
     *  DESCRIPTION: Creates a new popup window that allows
     *  players to manage the settings, such as ip address,
     *  ports, debug mode.
     * 
     *  REQUIREMENTS: 0015? I think.
     * 
     --------------------------------------------------*/

     public boolean NewSettingsScreen() {

        // Ensure another popup isn't already open
        if (model.getNewPopup())
            return false;

        // Set popup flag
        model.setNewPopup(true);

        // Text fields on popup window
        JTextField sendPort = new JTextField(5);
        JTextField receivePort = new JTextField(5);
        JTextField sendAddress = new JTextField(9);
        JCheckBox debugField = new JCheckBox("Debug Mode");

        // Input sanitation -- use a plain document as the method to push input, we can sanitize from here
        sendPort.setDocument(new PlainDocument() {
            @Override
            public void insertString(int offset, String s, AttributeSet a) throws javax.swing.text.BadLocationException {
                // Insert nothing if there is no string
                if (s == null)
                    return;
                // Only insert if our string contains the values from 0-9
                if (s.matches("[0-9]+"))
                    super.insertString(offset, s, a);
            }
        });

        // Input sanitation for receive port as well.
        receivePort.setDocument(new PlainDocument() {
            @Override
            public void insertString(int offset, String s, AttributeSet a) throws javax.swing.text.BadLocationException {
                // Insert nothing if there is no string
                if (s == null)
                    return;
                // Only insert if our string contains the values from 0-9
                if (s.matches("[0-9]+"))
                    super.insertString(offset, s, a);
            }
        });

        // Input sanitation for sending an address as well.
        sendAddress.setDocument(new PlainDocument() {
            @Override
            public void insertString(int offset, String s, AttributeSet a) throws javax.swing.text.BadLocationException {
                // Insert nothing if there is no string
                if (s == null)
                    return;

                 if (s.matches("[0-9.]+"))
                    super.insertString(offset, s, a);
            }
        });

        // Initialize the text fields with the current settings
        sendPort.setText(Integer.toString(netController.getSendPort()));
        receivePort.setText(Integer.toString(netController.getReceivePort()));
        sendAddress.setText(netController.getSendAddress());
        debugField.setSelected(model.getDebugMode());

        // Declare error flag
        boolean error = false;

        // Flag to mark the popup window ready to close
        boolean closePopupFlag = false;
        
        while (!closePopupFlag) {
            // Create the JPanel Window and add elements
            JPanel NewSettingsPopup = new JPanel();
            NewSettingsPopup.setPreferredSize(new Dimension(280, 150));
            NewSettingsPopup.add(new JLabel("Outbound port:         "));
            NewSettingsPopup.add(sendPort,BorderLayout.EAST);
            NewSettingsPopup.add(Box.createVerticalStrut(15));
            //NewSettingsPopup.add(Box.createHorizontalStrut(40));
            NewSettingsPopup.add(new JLabel("Inbound port:            "));
            NewSettingsPopup.add(receivePort,BorderLayout.EAST);
            NewSettingsPopup.add(Box.createVerticalStrut(15));
            //NewSettingsPopup.add(Box.createHorizontalStrut(45));
            NewSettingsPopup.add(new JLabel("Destination Address (IPv4):"));
            //NewSettingsPopup.add(Box.createVerticalStrut(15));
            NewSettingsPopup.add(sendAddress,BorderLayout.EAST);
            NewSettingsPopup.add(Box.createVerticalStrut(20));
            NewSettingsPopup.add(debugField);

            // Create the dialog popup with the ok/cancel buttons and wait for the window to be closed
            int result = JOptionPane.showConfirmDialog(null, NewSettingsPopup, "Settings", JOptionPane.OK_CANCEL_OPTION);

            // If the user clicked the "OK" button, update the settings
            if (result == JOptionPane.OK_OPTION) {
                // Declare vars to determine if a change occurred
                boolean sChanged = (Integer.parseInt(sendPort.getText()) != netController.getSendPort());
                boolean rChanged = (Integer.parseInt(receivePort.getText()) != netController.getReceivePort());
                boolean aChanged = (!sendAddress.getText().equals(netController.getSendAddress()));
                boolean dChanged = (debugField.isSelected() != model.getDebugMode());

                // Declare vars to hold the current settings
                int sPort = 0;
                int rPort = 0;
                try {
                    sPort = Integer.parseInt(sendPort.getText());
                }
                catch (Exception exception) { } // Do nothing
                try {
                    rPort = Integer.parseInt(receivePort.getText());
                }
                catch (Exception exception) { } // Do nothing
                String sAddr = sendAddress.getText();

                // Declare flag for duplicate ports
                boolean duplicatePorts = false;

                // Error handling for duplicate ports.
                if (sPort == rPort) {
                    model.toolTip("Transmission and receival ports cannot be the same!", 2500);
                    error = true;
                    duplicatePorts = true;
                }

                // Save all changed settings
                if (sChanged && !duplicatePorts)
                    if(!netController.setSendPort(sPort))
                        error = true;
                if (rChanged && !duplicatePorts)
                    if(!netController.setReceivePort(rPort, true))
                        error = true;
                if (aChanged)
                    if(!netController.setSendAddress(sAddr))
                        error = true;
                if (dChanged)
                    model.setDebugMode(debugField.isSelected());

                // Success/fail tooltip
                if(!error)
                    model.toolTip("Settings updated successfully!", 2500);
                else if (error && !duplicatePorts)
                    model.toolTip("Error! Not all settings may have saved!", 2500);

                closePopupFlag = true;
            }

            // If the user hit cancel or close on the window
            if (result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION) {
                model.toolTip("No changes made.", 2500);
                closePopupFlag = true;
            }
        }

        model.setNewPopup(false);
        return true;
    }

    /*--------------------------------------------------
     * 
     *  handleToolTipDrawing()
     * 
     *  DESCRIPTION: Handles the updating and display of 
     *  tool tips
     * 
     *  REQUIREMENTS: 
     * 
     --------------------------------------------------*/
    void handleToolTipDrawing() {
        if (model.newToolTip == true) {

            model.newToolTip = false;
            toolTipCounter++;
            toolTipLabel = model.toolTip;
            toolTipLabel.setBounds((int)((windowWidth - toolTipLabel.getWidth()) / 2), (580 + toolTipCounter * 30), 1000, 30);
            toolTipLabel.setPreferredSize(new Dimension(1000, 30));
            toolTipLabel.setLocation((int)((windowWidth - toolTipLabel.getWidth()) / 2), (580 + toolTipCounter * 30));
            toolTipLabel.setBorder(new EmptyBorder(0, 150, 0, 150));
            PlayerEntryPanes.add(toolTipLabel);
            toolTipLabel.setVisible(true);
            timer.schedule(new toolTipTimeout(),model.toolTip_ms);
        }

        // If a tool tip has timed out and been deleted, update the position of all the tool tips
        if (prevToolTipCounter != toolTipCounter) {
            for (int i = 2; i < PlayerEntryPanes.getComponentCount(); i++) {
                PlayerEntryPanes.getComponent(i).setBounds((int)((windowWidth - toolTipLabel.getWidth()) / 2), (580 + i * 30), 1000, 30);
            }
            prevToolTipCounter = toolTipCounter;
        }
    }
}

