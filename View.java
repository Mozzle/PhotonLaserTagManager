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
import javax.swing.JSeparator;
import javax.swing.text.AttributeSet;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JSlider;

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
import java.util.Comparator;
import java.util.Timer;
import java.util.TimerTask;

public class View extends JPanel {

    // Declare references
    private Model model;
    private Controller controller;
    public NetController netController;

    // Declare window variables
    private int windowHeight, windowWidth, PlayerEntryPanePadding;
    
    // Declare current state flags (to determine what to draw)
    private boolean inPlayerEntryScreen, inCountDownScreen, inGameScreen, inGameOverScreen;

    // Declare panes for the main JPanel
    public JPanel RedTeamTextBoxPane, GreenTeamTextBoxPane, PlayerEntryPanes;

    // Declare tooltip related vars
    public JLabel toolTipLabel;
    public int toolTipCounter, prevToolTipCounter;

    // Declare selection related vars
    public ArrayList<JLabel> rowSelectionLabel;
    public int lastSelectedRow;
    public char lastSelectedTeam;

    // Declare button components
    public JButton ClearScreenButton, StartGameButton, NewPlayerButton, SettingsButton;
    public Component currentFocus;

    // Declare timer related vars
    JLabel countDownLabel;
    public boolean CountDownVar; 
    public Timer timer;

    public boolean finishPopup;
    private int setNewMaxVolume;

    // Game Action Screen
    JPanel GameActionScreen, RedTeamScorePane, GameActionPane, GreenTeamScorePane;
    public ArrayList<JLabel> redTeamScores;
    public ArrayList<JLabel> redTeamNames, redTeamBaseLabel;
    public ArrayList<JLabel> greenTeamScores, greenTeamBaseLabel;
    public ArrayList<JLabel> greenTeamNames;
    public JLabel redTeamTotScore;
    public JLabel greenTeamTotScore;
    //View copy of the Model value
    public int vSecondsRemainingInGame;
    public JLabel TimeRemainingLabel;

    /*-------------------------------------------------
     *
     *  View()
     *
     *  DESCRIPTION: View Class Initializer
     *
     *  REQUIREMENTS: 0023
     *
    ------------------------------------------------- */

    public View(Controller c, Model m, NetController n)
    {
        // Set references to the controller and model
        c.setView(this);
        model = m;
        controller = c;
        netController = n;

        // Initialize current UI state, or flags
        inPlayerEntryScreen = false;
        inCountDownScreen = false;
        inGameScreen = false;
        inGameOverScreen = false;

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

        redTeamScores = new ArrayList<JLabel>();
        redTeamNames = new ArrayList<JLabel>();
        redTeamBaseLabel = new ArrayList<JLabel>();
        greenTeamBaseLabel = new ArrayList<JLabel>();
        greenTeamScores = new ArrayList<JLabel>();
        greenTeamNames = new ArrayList<JLabel>();

        // Init finish-popup flag
        finishPopup = false;

        //For countdown variable
        CountDownVar=true;
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
            View.this.remove(countDownLabel);
            model.system_State = Model.PLAY_ACTION_SCREEN;
            }

        };

        vSecondsRemainingInGame = 0;

    }

    /*-------------------------------------------------
     *
     *  paintComponent()
     *
     *  DESCRIPTION: Draws the images to the screen
     *
     *  REQUIREMENTS: 0024
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
     *  REQUIREMENTS: 0024,
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
     *  REQUIREMENTS: 0005,
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
     *  REQUIREMENTS: 0005, 
     -------------------------------------------------*/
    public void update() {
        model.updateScreenSize(windowWidth, windowHeight);
        
        // Block that handles the PLAYER ENTRY SCREEN
        if (model.getSystemState() == Model.PLAYER_ENTRY_SCREEN) {
             
            if (!inPlayerEntryScreen) { // If first time in Player Entry Screen
            PreviousScreenDeleter();
            inPlayerEntryScreen = true;
            inGameOverScreen = false;
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

            // Check if model is telling us to do create a New Player Popup window
            if (model.getMakePlayerPopupFlag()) {
                model.setMakePlayerPopupFlag(false);
                finishPopup = true;
                NewPlayerPopupScreen("", null, null, "Add New Player", "");
            }

            // Check if the model is telling us to create a new settings window
            if (model.getMakeSettingsPopupFlag()) {
                model.setMakeSettingsPopupFlag(false);
                // Create a new settings window
                NewSettingsScreen();
            }

            /*-----------------------------------------------------
            Update the '>>>>' row selection arrows and handle 
            valid Player/Equipment ID input detection
            -----------------------------------------------------*/
            // Check if a text field has a current focus, if so handle it
            if (KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner() != null && !finishPopup) {
                handleFocusDrawing();
            }
        }

        // Block that handles the COUNTDOWN SCREEN
        if (model.getSystemState() == Model.COUNTDOWN_SCREEN) {
        
            // If it's our first time in the COUNTDOWN SCREEN
            if (!inCountDownScreen) {
            inPlayerEntryScreen = false;
            inCountDownScreen = true; 
            inGameOverScreen = false;
            // Delete Player Entry Screen
            this.PreviousScreenDeleter();
            // Draw the Countdown Screen 
            this.drawCountDownScreen();
            // Schedule the end of the countdown screen
            }

        }

        // Block that handles the GAME SCREEN
        if (model.getSystemState() == Model.PLAY_ACTION_SCREEN && !inGameScreen) {
            inCountDownScreen = false;
            inGameScreen = true;
            drawPlayActionScreen();
            netController.transmit(String.valueOf(202));
            
            // TODO: Link a method here that handles all the sprites and objects
            // for the game screen. Or implement it here directly.
        }
        else if (model.getSystemState() == Model.PLAY_ACTION_SCREEN && inGameScreen) {
            if (vSecondsRemainingInGame != model.getGameSecondsRemaining()) {
                TimeRemainingLabel.setText(model.getGameTimeRemaining());
                vSecondsRemainingInGame = model.getGameSecondsRemaining();
            }

            if (model.getScoreUpdatedFlag() == true) {
                model.setScoreUpdatedFlag(false);
                updateScores();
            }

        }

        if (model.getSystemState() == Model.GAME_OVER) {

            if (!inGameOverScreen) {
                inGameScreen = false;
                inGameOverScreen = true;
                GameOverScreen();

            }
        }
        
    }

    public void handleFocusLogic(JTextField ID, JTextField Equip, JTextField Name) {

        // Exit if we focus is switched due to pop-up menu
        if (finishPopup) {
            return;
        }

        // If our references are invalid, exit early
        if (ID == null || Equip == null || Name == null)
            return;

        // If our integer references are empty, don't handle any logic and exit early
        if (Equip.getText().equals("") || ID.getText().equals(""))
            return;

        // Check if a player already lives in this row
        if (controller.checkRefForPlayer(ID, Equip, Name, lastSelectedRow) != null) {
            return;
        }

        // Find out what team our new player is on.
        int newPlayerTeam = -1;
        if (lastSelectedTeam == 'R') {
            newPlayerTeam = Player.RED_TEAM;
        }
        else if (lastSelectedTeam == 'G') {
            newPlayerTeam = Player.GREEN_TEAM;
        }

        // Create our new player object
        Player newPlayer = Player.createPlayer(
            Name.getText(), 
            Integer.valueOf(Equip.getText()),
            Integer.valueOf(ID.getText()),
            newPlayerTeam);

        // Add our player to the local playerlist, exit method early if failed to insert
        if (!model.addPlayer(newPlayer)) {
            // Clear reference text boxes and print a tooltip
            ID.setText("");
            Equip.setText("");
            Name.setText("");

            model.toolTip("Player ID or equipment ID already exists!", 4500);
            return;
        }

        // Update player references if successfully added
        newPlayer.setReferences(ID,Equip,Name);

        // Set row identifier for our player
        newPlayer.rowIdentifier = lastSelectedRow;

        // Query database for a codename to match the entered ID.
        String returnCode = model.database.searchDB(Database.PARAM_ID, newPlayer.getNormalID(), "");
                            
        // If our codename already exists for this ID, return a tooltip to the user
        if (!returnCode.equals("")) {

            // Update our new players name
            newPlayer.name = returnCode;

            // Update the players codename if they aren't a duplicate locally
            if (lastSelectedTeam == 'R') {
                model.getCodenameBoxAt(lastSelectedRow).setText(newPlayer.name);
            }
            else if (lastSelectedTeam == 'G') {
                model.getCodenameBoxAt(lastSelectedRow + Model.NUM_MAX_PLAYERS_PER_TEAM).setText(newPlayer.name);
            }

            model.toolTip(newPlayer.name + " added successfully!", 4500);
            // Transmit the new player to the server
            netController.transmit(String.valueOf(newPlayer.getEquipID()));
            newPlayer.verify();
        }

        // Print a tooltip and adjust playerlist if database connection fails
        else if (model.database.getdbConnectionStatus() == false && !model.getDebugMode()) {
            model.removePlayer(newPlayer);
            model.toolTip("No database connection! Game will not work!",10000);
        }

        // If ID does not exist in the database and the connection is good, add the ID to the DB
        else {
            // Create the New Player Entry Popup screen
            finishPopup = true;
            NewPlayerPopupScreen(ID.getText(), ID, Name, "Unknown Player ID entered, would", "you like to create a new Player?");
        }
    }

    public void handleFocusDrawing() {

        // Create a reference to the last object that has been focused
        Component selectedComponent = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();

        // Null check on our component (check if it exists, if it doesn't then exit early)
        if (selectedComponent == null)
            return;

        String selectedRow = selectedComponent.getName();

        // Null check on our component name (check if it exists, if it doesn't then exit early)
        if (selectedRow == null)
            return;

        // Sanitize our row substring (for some reason, "frame0" keeps getting passed to this?)
        String tempParse = selectedRow.substring(1);
        tempParse = tempParse.replaceAll("[a-zA-Z]", "");
        if (!selectedRow.matches("[a-zA-Z]\\d+"))
            return;

        // Grab two separate indexes from our selected row
        int selectedRowValue = Integer.valueOf(tempParse);
        int selectedRowIndex = model.getTextBoxIndexFromName(selectedRow);

        JTextField IDBox = null;
        JTextField EquipIDBox = null;
        JTextField CodenameBox = null;

        // Grab references to all components in our row
        if (lastSelectedRow >= 0 && lastSelectedRow <= 31) {
            int queryRow = lastSelectedRow;

            // Check and adjust our index if we are looking at green/red team
            if (lastSelectedTeam == 'G') {
                queryRow += Model.NUM_MAX_PLAYERS_PER_TEAM;
            }

            IDBox = (JTextField)model.getPlayerIDBoxAt(queryRow);
            EquipIDBox = (JTextField)model.getEquipmentIDBoxAt(queryRow);
            CodenameBox = (JTextField)model.getCodenameBoxAt(queryRow);
        }

        // Highlight selection to our user using gray background
        if (selectedComponent.getName() != null)
            selectedComponent.setBackground(Color.LIGHT_GRAY); // Indicate to the user the box is selected

        /// Make several checks to see if we can apply logic to add players
        // If we have a valid row selection and the row selection has changed
        // If the team has changed
        // If we are not currently in the process of creating a new player popup
        if ((selectedRow.length() == 2 || selectedRow.length() == 3) 
        && (lastSelectedRow != selectedRowValue)
        || (lastSelectedTeam != selectedRow.charAt(0))
        && (!model.getMakePlayerPopupFlag())) {
            model.playerUpdateFlag = false;
            handleFocusLogic(IDBox, EquipIDBox, CodenameBox);
            model.playerUpdateFlag = true;
        }

        // Update our lastSelected variables
        lastSelectedRow = selectedRowValue;
        lastSelectedTeam = selectedRow.charAt(0);
            
        // Re-draw other components to ensure we don't duplicate our grey background
        for (int i = 0; i < rowSelectionLabel.size(); i++) {
            rowSelectionLabel.get(i).setText("           ");
        }

        // Re-draw these components and use a try-catch to keep program from breaking?
        try {
        for (int i = 0; i < model.getNumPlayerIDBoxes(); i++) {
            JTextField compareBox = model.getPlayerIDBoxAt(i);
            if (compareBox != selectedComponent)
                compareBox.setBackground(Color.WHITE);
        }
        for (int i = 0; i < model.getNumEquipmentIDBoxes(); i++) {
            JTextField compareBox = model.getEquipmentIDBoxAt(i);
            if (compareBox != selectedComponent)
                compareBox.setBackground(Color.WHITE);
        }
    }
    catch (Exception e) {
        // Do nothing
    }

        // Handle drawing the row selection label
        if (selectedComponent.getParent() != null) {
            if (lastSelectedTeam == 'R') {
                rowSelectionLabel.get(lastSelectedRow).setBackground(Color.BLACK);
                rowSelectionLabel.get(lastSelectedRow).setText("  >>>>");
            }
            else if (lastSelectedTeam == 'G') {
                  rowSelectionLabel.get(lastSelectedRow + Model.NUM_MAX_PLAYERS_PER_TEAM).setText("  >>>>");
            }
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
      *     -------[PlayerEntryPanes]------------------------------------------------------------------------------------------
      *     |         |--------[RedTeamTextBoxPane]----------------||------[GreenTeamTextBoxPane]--------------------|        |
      *     |         ||----------[TextFieldsR]-------------------||||-----------[TextFieldsG]----------------------||        |
      *     |         ||                                          ||||                                              ||        |
      *     |         ||             RED TEAM                     ||||            GREEN TEAM                        ||        |
      *     |         ||  Player ID     Equipment ID    Codename  ||||  Player ID       Equipment ID     Codename   ||        |
      *     |         || ||||||||||||  ||||||||||||||  |||||||||| |||| |||||||||||||||  ||||||||||||||  ||||||||||  ||        |
      *     |         || ||||||||||||  ||||||||||||||  |||||||||| |||| |||||||||||||||  ||||||||||||||  ||||||||||  ||        |
      *     |         || ||||||||||||  ||||||||||||||  |||||||||| |||| |||||||||||||||  ||||||||||||||  ||||||||||  ||        |
      *     |         || ||||||||||||  ||||||||||||||  |||||||||| |||| |||||||||||||||  ||||||||||||||  ||||||||||  ||        |
      *     |         || ||||||||||||  ||||||||||||||  |||||||||| |||| |||||||||||||||  ||||||||||||||  ||||||||||  ||        |
      *     |         || ||||||||||||  ||||||||||||||  |||||||||| |||| |||||||||||||||  ||||||||||||||  ||||||||||  ||        |
      *     |         || ||||||||||||  ||||||||||||||  |||||||||| |||| |||||||||||||||  ||||||||||||||  ||||||||||  ||        |
      *     |         || ||||||||||||  ||||||||||||||  |||||||||| |||| |||||||||||||||  ||||||||||||||  ||||||||||  ||        |
      *     |         || ||||||||||||  ||||||||||||||  |||||||||| |||| |||||||||||||||  ||||||||||||||  ||||||||||  ||        |
      *     |         || ||||||||||||  ||||||||||||||  |||||||||| |||| |||||||||||||||  ||||||||||||||  ||||||||||  ||        |
      *     |         || ||||||||||||  ||||||||||||||  |||||||||| |||| |||||||||||||||  ||||||||||||||  ||||||||||  ||        |
      *     |         || ||||||||||||  ||||||||||||||  |||||||||| |||| |||||||||||||||  ||||||||||||||  ||||||||||  ||        |
      *     |         || ||||||||||||  ||||||||||||||  |||||||||| |||| |||||||||||||||  ||||||||||||||  ||||||||||  ||        |
      *     |         || ||||||||||||  ||||||||||||||  |||||||||| |||| |||||||||||||||  ||||||||||||||  ||||||||||  ||        |
      *     |         || ||||||||||||  ||||||||||||||  |||||||||| |||| |||||||||||||||  ||||||||||||||  ||||||||||  ||        |
      *     |         ||                                          ||||                                              ||        |
      *     |         ||------------------------------------------||||----------------------------------------------||        |
      *     |         |                                            ||                                               ||        |
      *     |         |                                            ||                                                |        |
      *     |         |--------------------------------------------||------------------------------------------------|        |
      *     ||---------------------------------------------------------------------------------------------------------------||
      *     |                                                                                                                 |
      *     |                                                                                                                 |
      *     |----------------[Buttons]----------------------------------------------------------------------------------------|
      *     ||-----------------------------[ButtonsCenter]-------------------------------------------------------------------||
      *     ||                  |----------------||----------------||----------------||----------------|                     ||
      *     ||                  |                ||                ||                ||                |                     ||
      *     ||                  |  Clear Screen  ||   Start Game   || Add New Player ||    Settings    |                     ||
      *     ||                  |[ClearScreenBtn]|| [StartGameBtn] || [NewPlayerBtn] ||[SettingsButton]|                     ||
      *     ||                  |----------------||----------------||----------------||----------------|                     ||
      *     ||---------------------------------------------------------------------------------------------------------------||
      *     |-----------------------------------------------------------------------------------------------------------------|
      */
    public void drawPlayerEntryScreen() {
        LayoutManager layout = new FlowLayout();
        JLabel tmpJLabel;
        this.setLayout(new BorderLayout());


        /*-------------
         * Buttons
        --------------*/

        // Key controller for when buttons are in focus
        KeyAdapter buttonKeys = model.getStandardKeyAdapter();

        // Buttons panel container
        JPanel Buttons = new JPanel();
        Buttons.setLayout(new BoxLayout(Buttons, BoxLayout.X_AXIS));
        Buttons.setBackground(Color.BLACK);
        Buttons.setOpaque(false);

        // Buttons Center is within Buttons, centers all buttons that get
        // put within it
        JPanel ButtonsCenter = new JPanel();
        ButtonsCenter.setBackground(Color.BLACK);
        ButtonsCenter.setLayout(new FlowLayout(FlowLayout.CENTER));

        // Clear Screen button
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
                model.clearPlayerList();
			}
        }  
        }); 
        ButtonsCenter.add(ClearScreenButton);

        // Start Game Button
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

        // New Player Button
        NewPlayerButton = new JButton("(F9) - Add New Player");
        NewPlayerButton.setPreferredSize(new Dimension(225, 60));
        NewPlayerButton.setMaximumSize(new Dimension(225, 60));
        NewPlayerButton.setBackground(new Color(0, 66, 32));
        NewPlayerButton.setForeground(Color.WHITE);
        NewPlayerButton.addKeyListener(buttonKeys);
        NewPlayerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                finishPopup = true;
                NewPlayerPopupScreen("", null, null, "Add New Player", "");
            }  
        });
        ButtonsCenter.add(NewPlayerButton);

        // Settings Button
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
        
        if (model.GameDataStatus == Model.FIRST_GAME) {
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

        
        RedTeamTextBoxPane.setVisible(true);
        GreenTeamTextBoxPane.setVisible(true);
        this.setVisible(true);
        this.repaint();
        this.revalidate();

        controller.initTextboxListener();
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
        
        Timer timer = new Timer();
        BorderFactory border1;

        ImageIcon imgIcon = new ImageIcon(this.getClass().getResource("src/timer.gif"));
        countDownLabel = new JLabel(imgIcon);
        countDownLabel.setBounds((int)((windowWidth - 256) / 2), (int)((windowHeight - 256) / 2), 256, 256); // Adjust these values to suit your layout
        add(countDownLabel);
        countDownLabel.setVisible(true);

        TimerTask task0 = new TimerTask() {
            @Override
            public void run() {
                setBorder(BorderFactory.createLineBorder(Color.RED, 50));
            }
        };
        
        
        
        //If timer scheulde is true thne run belwo function
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
            View.this.remove(countDownLabel);
            View.this.removeAll();
            model.system_State = Model.PLAY_ACTION_SCREEN; 
            
            }
        };

        TimerTask task1 = new TimerTask() {
            @Override
            public void run() {
            setBorder(BorderFactory.createLineBorder(Color.YELLOW, 50));
            }
        };

        TimerTask task2 = new TimerTask() {
            @Override
            public void run() {
            setBorder(BorderFactory.createLineBorder(Color.GREEN, 50));
            }
        };

        TimerTask task3 = new TimerTask() {
            @Override
            public void run() {
            setBorder(BorderFactory.createLineBorder(Color.BLACK, 0));
            }
        };

        TimerTask taskKill = new TimerTask() {
            @Override
            public void run(){
                View.this.remove(countDownLabel);
                View.this.removeAll();
                model.system_State = Model.PLAY_ACTION_SCREEN;
            }
        };

        //timer.schedule(task, 0);
        timer.schedule(task0, 0);
        timer.schedule(task1, 10000);
        timer.schedule(task2, 20000);
        timer.schedule(task3, 30000);
        
        // Schedule the task to run after 30 seconds (30000 milliseconds)
        
        timer.schedule(task, 30000);
        
        if(CountDownVar==false && (CountDownVar==false || model.getDebugMode())){
            task0.cancel(); 
            task1.cancel();
            task2.cancel();
            task3.cancel(); 
            View.this.remove(countDownLabel); 
            View.this.removeAll();
            model.system_State=Model.PLAY_ACTION_SCREEN;
        }
        
        
    }
    /*--------------------------------------------------
     * 
     *  Function for if countdown debug mode is active
     * 
     *  DESCRIPTION: Cancels the countdown screen for debuggin purposes
     * 
     *  REQUIREMENTS: 
     * 
     --------------------------------------------------*/
     public void CountDownDebug(){
        CountDownVar=false; 
     }

    /*--------------------------------------------------
     * 
     *  toolTipTimeout.run()
     * 
     *  DESCRIPTION: Timer Task that executes when a 
     *  tool tip has timed out on the screen. Deletes 
     *  the tool Tip
     * 
     *  REQUIREMENTS: 0026,
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
     *  PreviousScreenDeleter()
     * 
     *  DESCRIPTION: Deletes the previous screen.
     *  Used for transitioning between screens
     * 
     *  REQUIREMENTS: 0013
     * 
     --------------------------------------------------*/

    public void PreviousScreenDeleter(){
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

        // Declare new player vars
        boolean allowNewPlayer = true;
        int EquipID = -1;
        int NormalID = -1;
        String Name = "NULL";

        // Find a reference to the equipment ID field to link for our player
        int tempIDReference = -1;
        JTextField tempEquipIDBox = null;

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

        // Temp string to prevent number format exception
        String s = "";

        // If we have references to the ID and Equipment ID boxes, grab their values
        if (IDBox != null)
            tempIDReference = model.getTextBoxIndexFromName(IDBox.getName());

            s = NewPlayerID.getText();
            if (s.equals("")) {
                s = "-1";
            }

            NormalID = Integer.valueOf(s);
        if (tempIDReference != -1)
            tempEquipIDBox = model.getEquipmentIDBoxAt(tempIDReference);
        if (tempEquipIDBox != null) {
            s = tempEquipIDBox.getText();
            if (s.equals("")) {
                s = "-1";
            }
            EquipID = Integer.valueOf(s);
        }
        
        while (!closePopupFlag) {
            // Create the popup JPanel
            JPanel NewPlayerPopup = new JPanel();

            // Add elements to our JPanel
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

            // Add a "generate ID" button to our JPanel
            JButton GenerateNewIDBtn = new JButton("Generate A New Player ID");
            GenerateNewIDBtn.setPreferredSize(new Dimension(240, 25));
            GenerateNewIDBtn.setMaximumSize(new Dimension(240, 25));
            // Attach a listener to our button that selects an ID and applys it to the ID textbox
            GenerateNewIDBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e){

                    int newID = model.database.getNextAvailableID();

                    if (newID != -1) {
                        NewPlayerID.setText(String.valueOf(newID));
                        if (IDBox != null) {
                            String temp = IDBox.getText();
                            IDBox.setText(String.valueOf(newID));
                            System.out.println("[Model] Changed ID of \"" + temp + "\" to \"" + newID + "\"");
                        }
                    }
                }  
            });
            NewPlayerPopup.add(GenerateNewIDBtn);

            // Create the dialog popup with the ok/cancel buttons and wait for the window to be closed
            int result = JOptionPane.showConfirmDialog(null, NewPlayerPopup, "New Player Entry", JOptionPane.OK_CANCEL_OPTION);
            
            // Logic to run if the user selects OK
            if (result == JOptionPane.OK_OPTION) {

                // Update our player vars
                Name = NewPlayerName.getText();
                NormalID = Integer.valueOf(NewPlayerID.getText());

                // Check if the entered ID exists in the database
                String searchResult = model.database.searchDB(Database.PARAM_ID, NormalID, "");
                
                // If the entered ID doesn't exist in the DB, attempt to add the new player to the DB. 
                if (searchResult == "" 
                && NewPlayerID.getText().length() > 0
                 && NewPlayerName.getText().length() > 0) {

                    // If we are not connected to the database, print a tooltip
                    if (model.database.getdbConnectionStatus() == false && !model.getDebugMode()) {
                        model.toolTip("No database connection! Game will not work!",10000);
                        allowNewPlayer = false;
                    }

                    // If we are connected to the database, add the new user to the database
                    else if (model.database.insertDB(Database.PARAM_ID_AND_CODENAME, NormalID, Name)) {
                        model.toolTip(Name + " added successfully!", 4500);
                    }

                    else {
                        model.toolTip("Error adding " + Name + " to the database!", 4500);
                        allowNewPlayer = false;
                    }

                    // Apply changes to the players references and add to playerlist
                    if (allowNewPlayer) {
                        Player p = model.identifyPlayer(NormalID, EquipID, Name, lastSelectedRow);
                        if (p != null) {
                            NameBox.setText(Name);
                            p.setReferences(IDBox, tempEquipIDBox, NameBox);
                            p.rowIdentifier = lastSelectedRow;
                        }
                    } else {
                        if (tempEquipIDBox != null)
                            tempEquipIDBox.setText("");
                        if (IDBox != null)
                            IDBox.setText("");
                        if (NameBox != null)
                            NameBox.setText("");
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
            }

            // Logic to run if the user clicked CANCEL or CLOSED out of the JPanel
            else if (result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION) {

                // Update our name var
                Name = NewPlayerName.getText();

                // Remove the player from the local list since we cancelled the operation
                Player p = model.identifyPlayer(NormalID, EquipID, Name, lastSelectedRow);
                if (p != null) {
                    model.removePlayer(p);
                }

                closePopupFlag = true;
            }
        }

        // Inform our model that we no longer need to create a new popup
        model.setNewPopup(false);
        finishPopup = false;
        
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
     *  REQUIREMENTS: 0027
     * 
     --------------------------------------------------*/

     public boolean NewSettingsScreen() {

        // Ensure another popup isn't already open
        if (model.getNewPopup())
            return false;

        setNewMaxVolume = -1;

        // Set popup flag
        model.setNewPopup(true);

        // Text fields on popup window
        JTextField sendPort = new JTextField(5);
        JTextField receivePort = new JTextField(5);
        JTextField sendAddress = new JTextField(6);
        JCheckBox debugField = new JCheckBox("Debug Mode");
        JSlider volumeSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, AudioHandler.MASTER_VOLUME);

        // Create slider logic and details
        volumeSlider.setMajorTickSpacing(10);
        volumeSlider.setMinorTickSpacing(1);
        volumeSlider.setPaintTicks(true);
        volumeSlider.setPaintLabels(true);
        volumeSlider.setSnapToTicks(true);
        volumeSlider.setBorder(BorderFactory.createTitledBorder("Volume"));
        volumeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider changedSource = (JSlider)e.getSource();
                if (!changedSource.getValueIsAdjusting()) {
                    setNewMaxVolume = changedSource.getValue();
                }
            }
        });

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
            NewSettingsPopup.add(Box.createVerticalStrut(20));
            NewSettingsPopup.add(volumeSlider);

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

                // Check if send port has changed, if so unverify all players
                if (sChanged || aChanged) {
                    for (Player p : model.playerList) {
                        p.revoke();
                    }
                }

                // Success/fail tooltip
                if(!error)
                    model.toolTip("Settings updated successfully!", 2500);
                else if (error && !duplicatePorts)
                    model.toolTip("Error! Not all settings may have saved!", 2500);

                // If the volume has changed, update the volume
                if (setNewMaxVolume != -1) {
                    AudioHandler.MASTER_VOLUME = setNewMaxVolume;
                }

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
     *  REQUIREMENTS: 0026
     * 
     --------------------------------------------------*/
    void handleToolTipDrawing() {
        // If there is a new toolTip for us to add
        if (model.newToolTip == true) {
            // Add it
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

    /*--------------------------------------------------
     * 
     *  drawPlayActionScreen()
     * 
     *  DESCRIPTION: Creates and draws the Play Action
     *  Screen 
     * 
     *  REQUIREMENTS: 
     -------------------------------------------------*/

     public void drawPlayActionScreen() {

        model.PlayActionScreenDataInitializer();

        JLabel tmpJLabel; // Used for all static labels on the screen.

        // Set up GridBag Layout Manager
        this.setLayout(new GridBagLayout());
        GridBagConstraints constraint = new GridBagConstraints();
        constraint.fill = GridBagConstraints.BOTH;
        constraint.anchor = GridBagConstraints.NORTH;
        constraint.weightx = 1;
        constraint.weighty = 1;

        // Game Action Screen is a container for all of the components of this screen
        GameActionScreen = new JPanel(new GridBagLayout());
        GameActionScreen.setBackground(new Color (0, 0, 0, 0));
        GameActionScreen.setOpaque(false);

        // Red Team Score Pane -> Left hand side
        RedTeamScorePane = new JPanel();
        RedTeamScorePane.setBackground(new Color (136, 0, 21));
        constraint.gridx = 0;
        constraint.gridy = 0;
        constraint.gridwidth = 1;
        RedTeamScorePane.setBorder(new EmptyBorder(15, 30, 15, 30));
        RedTeamScorePane.setLayout(new GridBagLayout());
        GameActionScreen.add(RedTeamScorePane, constraint);

        // Game Action Pane -> Center Pane
        GameActionPane = new JPanel();
        GameActionPane.setBackground(new Color(20, 20, 20));
        constraint.gridx = 1;
        GameActionPane.setBorder(new EmptyBorder(0, 30, 15, 30));
        GameActionPane.setLayout(new GridBagLayout());
        GameActionScreen.add(GameActionPane, constraint);

        // Green Team Score Pane -> Right hand side
        GreenTeamScorePane = new JPanel();
        GreenTeamScorePane.setBackground(new Color (29, 156, 66));
        constraint.gridx = 2;
        GreenTeamScorePane.setBorder(new EmptyBorder(15, 30, 15, 30));
        GreenTeamScorePane.setLayout(new GridBagLayout());
        GameActionScreen.add(GreenTeamScorePane, constraint);

        // "Red/Green Team" Labels
        constraint.gridx = 0;
        constraint.gridy = 0;
        constraint.gridwidth = 3;
        constraint.ipady = 25;

        tmpJLabel = new JLabel("Red Team", SwingConstants.CENTER);
        tmpJLabel.setForeground(Color.WHITE);
        tmpJLabel.setFont(new Font("Arial", Font.BOLD, 35));
        RedTeamScorePane.add(tmpJLabel, constraint);

        tmpJLabel = new JLabel("Green Team", SwingConstants.CENTER);
        tmpJLabel.setForeground(Color.WHITE);
        tmpJLabel.setFont(new Font("Arial", Font.BOLD, 35));
        GreenTeamScorePane.add(tmpJLabel, constraint);

        // Red Team Players and their Scores
        constraint.gridwidth = 1;
        constraint.ipady = 8;
        constraint.ipadx = 10;
        redTeamBaseLabel.clear();

        for (int i = 0; i < model.getRedTeamPlayerListSize(); i++) {
            model.getPlayer(model.getRedTeamPlayerListAt(i)).setScore(0);
        }
        
        int drawingIndex = 0;

        for (int i = 0; i < model.getRedTeamPlayerListSize(); i++) {
            constraint.gridy = drawingIndex + 1;

            constraint.gridx = 0;
            redTeamBaseLabel.add(new JLabel(""));
            redTeamBaseLabel.get(i).setForeground(Color.YELLOW);
            redTeamBaseLabel.get(i).setFont(new Font("Arial", Font.BOLD, 20));
            RedTeamScorePane.add(redTeamBaseLabel.get(i), constraint); 

            constraint.gridx = 0;
            redTeamNames.add(new JLabel(model.getPlayer(model.getRedTeamPlayerListAt(i)).name));
            redTeamNames.get(i).setForeground(Color.WHITE);
            redTeamNames.get(i).setFont(new Font("Arial", Font.BOLD, 20));
            RedTeamScorePane.add(redTeamNames.get(i), constraint);            
            // Score
            constraint.gridx = 2;
            redTeamScores.add(new JLabel(String.valueOf(model.getPlayer(model.getRedTeamPlayerListAt(i)).getScore()), SwingConstants.RIGHT));
            redTeamScores.get(i).setForeground(Color.WHITE);
            redTeamScores.get(i).setFont(new Font("Arial", Font.BOLD, 20));
            RedTeamScorePane.add(redTeamScores.get(i), constraint);
            drawingIndex++;
        }

        // Fill the remaining spaces with blank rows
        while (drawingIndex < 15) {
            constraint.gridy = drawingIndex + 1;
            constraint.gridx = 0;
            tmpJLabel = new JLabel(" ");
            tmpJLabel.setForeground(Color.WHITE);
            tmpJLabel.setFont(new Font("Arial", Font.BOLD, 20));
            RedTeamScorePane.add(tmpJLabel, constraint);
            drawingIndex++;
    } 

        drawingIndex = 0;
        greenTeamBaseLabel.clear();

        for (int i = 0; i < model.getGreenTeamPlayerListSize(); i++) {
            model.getPlayer(model.getGreenTeamPlayerListAt(i)).setScore(0);
        }

        // Green Team Players and their Scores
        for (int i = 0; i < model.getGreenTeamPlayerListSize(); i++) {
                // Player
                constraint.gridy = drawingIndex + 1;

                constraint.gridx = 0;
                constraint.weightx = 0.1;
                greenTeamBaseLabel.add(new JLabel(""));
                greenTeamBaseLabel.get(i).setForeground(Color.YELLOW);
                greenTeamBaseLabel.get(i).setFont(new Font("Arial", Font.BOLD, 20));
                GreenTeamScorePane.add(greenTeamBaseLabel.get(i), constraint); 

                constraint.gridx = 0;
                constraint.weightx = 1000;
                greenTeamNames.add(new JLabel(model.getPlayer(model.getGreenTeamPlayerListAt(i)).name));
                greenTeamNames.get(i).setForeground(Color.WHITE);
                greenTeamNames.get(i).setFont(new Font("Arial", Font.BOLD, 20));
                GreenTeamScorePane.add(greenTeamNames.get(i), constraint);

                // Score
                constraint.gridx = 2;
                greenTeamScores.add(new JLabel(String.valueOf(model.getPlayer(model.getGreenTeamPlayerListAt(i)).getScore()), SwingConstants.RIGHT));
                greenTeamScores.get(i).setForeground(Color.WHITE);
                greenTeamScores.get(i).setFont(new Font("Arial", Font.BOLD, 20));
                GreenTeamScorePane.add(greenTeamScores.get(i), constraint);

                drawingIndex++;
            }
        
        // Fill the remaining spaces with blank rows
        while (drawingIndex < 15) {
                constraint.gridy = drawingIndex + 1;
                constraint.gridx = 0;
                tmpJLabel = new JLabel(" ");
                tmpJLabel.setForeground(Color.WHITE);
                tmpJLabel.setFont(new Font("Arial", Font.BOLD, 20));
                GreenTeamScorePane.add(tmpJLabel, constraint);
                drawingIndex++;
        } 

        // Red Team Total Score
        constraint.ipady = 25;
        constraint.gridx = 0;
        constraint.gridy = 17;
        constraint.gridwidth = 3;
        redTeamTotScore = new JLabel(String.valueOf(model.getRedTeamScore()), SwingConstants.CENTER);
        redTeamTotScore.setForeground(Color.WHITE);
        redTeamTotScore.setFont(new Font("Arial", Font.BOLD, 35));
        RedTeamScorePane.add(redTeamTotScore, constraint);
        // And Green Team Total Score
        greenTeamTotScore = new JLabel(String.valueOf(model.getGreenTeamScore()), SwingConstants.CENTER);
        greenTeamTotScore.setForeground(Color.WHITE);
        greenTeamTotScore.setFont(new Font("Arial", Font.BOLD, 35));
        GreenTeamScorePane.add(greenTeamTotScore, constraint);

        // Game Events Scroll
        JLabel gameEventsScroll = new JLabel();
        gameEventsScroll.setLayout(new GridLayout(16, 1, 0, 6));
        constraint.ipady = 3;
        constraint.weighty = 24;
        constraint.gridx = 0;
        constraint.gridy = 0;
        constraint.gridwidth = 1;
        constraint.gridheight = 10;
        constraint.anchor = GridBagConstraints.SOUTH;

        // Add all 16 scroll labels
        for (int i = 0; i < model.getNumGameEventQueue(); i++) {
            gameEventsScroll.add(model.getGameEventQueueAtNum(i));
        }
        

        GameActionPane.add(gameEventsScroll, constraint);

        // Time Remaining counter
        constraint.anchor = GridBagConstraints.NORTH;
        constraint.weighty = 0.5;
        constraint.ipady = 0;
        constraint.gridy = 10;
        constraint.gridheight = 1;

        // Separaty McSeparatorFace
        GameActionPane.add(new JSeparator(SwingConstants.HORIZONTAL), constraint);

        // "Time Remaining:"
        constraint.gridy = 11;
        tmpJLabel = new JLabel("Time Remaining:", SwingConstants.CENTER);
        tmpJLabel.setForeground(Color.WHITE);
        tmpJLabel.setFont(new Font("Arial", Font.BOLD, 32));
        GameActionPane.add(tmpJLabel, constraint);

        // Time.
        constraint.gridy = 12;
        constraint.ipady = 5;
        TimeRemainingLabel = new JLabel(model.getGameTimeRemaining(), SwingConstants.CENTER);
        TimeRemainingLabel.setForeground(Color.WHITE);
        TimeRemainingLabel.setFont(new Font("Arial", Font.BOLD, 35));
        GameActionPane.add(TimeRemainingLabel, constraint);

        // Add Game Action Screen to the main JFrame (The View class)
        constraint.gridx = 0;
        constraint.gridy = 0;
        this.add(GameActionScreen, constraint);
        
        // Set Everything visible and force the screen to update
        RedTeamScorePane.setVisible(true);
        GreenTeamScorePane.setVisible(true);
        GameActionPane.setVisible(true);
        GameActionScreen.setVisible(true);
        this.setVisible(true);
        this.repaint();
        this.revalidate();
        
     }

    /*--------------------------------------------------
     * 
     *  updateScores()
     * 
     *  DESCRIPTION: Handles the drawing of updated
     *  individual and total scores to the screen.
     * 
     *  REQUIREMENTS: 
     * 
     --------------------------------------------------*/
    public void updateScores() {
        int score = 0;
        ArrayList<Integer> rankedScores = new ArrayList<Integer>();
        ArrayList<Integer> rankedPlayers = new ArrayList<Integer>();

        for (int i = 0; i < model.getRedTeamPlayerListSize(); i++) {
            rankedScores.add(model.getPlayer(model.getRedTeamPlayerListAt(i)).getScore());
        }
        rankedScores.sort(Comparator.reverseOrder());

        // For the ranked scores arraylist
        for (int i = 0; i < model.getRedTeamPlayerListSize(); i++) {
            // for all players in red team
            for (int j = 0; j < model.getRedTeamPlayerListSize(); j++) {
                // If ranked score at i is equal to red team player's score at j
                if (rankedScores.get(i) == model.getPlayer(model.getRedTeamPlayerListAt(j)).getScore()) {
                    boolean alreadyRanked = false;
                    // and if red team player at j is not already in the rankedPlayers list
                     for (int element : rankedPlayers) {
                         if (element == model.getRedTeamPlayerListAt(j)) {
                             alreadyRanked = true;
                         }
                    }
                    // Add the player to their position in the list.
                    if (!alreadyRanked) {
                        rankedPlayers.add(model.getRedTeamPlayerListAt(j));
                    }
                }
            }
        }

        // Set text and score for all players
        for (int i = 0; i < model.getRedTeamPlayerListSize(); i++) {
            redTeamScores.get(i).setText(String.valueOf(model.getPlayer(rankedPlayers.get(i)).getScore()));

            if (model.getPlayer(rankedPlayers.get(i)).getHasHitBase()) {
                redTeamBaseLabel.get(i).setText("[B]");
                redTeamNames.get(i).setText("     " + model.getPlayer(rankedPlayers.get(i)).name);
            }
            else {
                redTeamBaseLabel.get(i).setText("");
                redTeamNames.get(i).setText(model.getPlayer(rankedPlayers.get(i)).name);
            }
            score += model.getPlayer(rankedPlayers.get(i)).getScore();
        }
        // Set total team score
        redTeamTotScore.setText(String.valueOf(score));

        // GREEN TEAM
        rankedScores = new ArrayList<Integer>();
        rankedPlayers = new ArrayList<Integer>();
        score = 0;

        // Add all green team scores to an arrayList
        for (int i = 0; i < model.getGreenTeamPlayerListSize(); i++) {
            rankedScores.add(model.getPlayer(model.getGreenTeamPlayerListAt(i)).getScore());
        }
        // Sort rankedScores in reverse order
        rankedScores.sort(Comparator.reverseOrder());

        // For the ranked scores arraylist
        for (int i = 0; i < model.getGreenTeamPlayerListSize(); i++) {
            // For all players in green team
            for (int j = 0; j < model.getGreenTeamPlayerListSize(); j++) {
                // If ranked score at i is equal to green team player's score at j
                if (rankedScores.get(i) == model.getPlayer(model.getGreenTeamPlayerListAt(j)).getScore()) {
                    boolean alreadyRanked = false;
                    // and if green team player at j is not already in the rankedPlayers list
                     for (int element : rankedPlayers) {
                         if (element == model.getGreenTeamPlayerListAt(j)) {
                             alreadyRanked = true;
                         }
                    }
                    // Add the player to their position in the list.
                    if (!alreadyRanked) {
                        rankedPlayers.add(model.getGreenTeamPlayerListAt(j));
                    }
                }
            }
        }

        // Set text and score for all players
        for (int i = 0; i < model.getGreenTeamPlayerListSize(); i++) {
            greenTeamScores.get(i).setText(String.valueOf(model.getPlayer(rankedPlayers.get(i)).getScore()));

            if (model.getPlayer(rankedPlayers.get(i)).getHasHitBase()) {
                greenTeamBaseLabel.get(i).setText("[B]");
                greenTeamNames.get(i).setText("     " + model.getPlayer(rankedPlayers.get(i)).name);
            }
            else {
                greenTeamBaseLabel.get(i).setText("");
                greenTeamNames.get(i).setText(model.getPlayer(rankedPlayers.get(i)).name);
            }
            score += model.getPlayer(rankedPlayers.get(i)).getScore();
        }
        // Set total team score
        greenTeamTotScore.setText(String.valueOf(score));
    }

    public boolean GameOverScreen() {

        Object[] options = { "Restart", "Back To Player Entry", "Exit Photon"};

        int choice = JOptionPane.showOptionDialog(null, "                    Game Over! Do you want to restart the game?", model.calculateGameWinner(), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        // If user selects to restart game
        if (choice == JOptionPane.YES_OPTION) {
            // Reset play action message queue
            // Reset Player Lists
            // Revalidate Equipment/Send out Revalidation codes to equpment
            model.setSystemState(Model.COUNTDOWN_SCREEN);
            model.GameDataStatus = Model.SECOND_GAME_RESET;
        }
        // If user selects to navigate to the Player Entry Screen
        else if (choice == JOptionPane.NO_OPTION) {
            model.GameDataStatus = Model.SECOND_GAME_NEW_ENTRY;
            model.setSystemState(Model.PLAYER_ENTRY_SCREEN);
        }
        // If the user selects "Exit Photon" or hits the 'X' button
        else {
            System.exit(0);
        }
        return true;
    }
}

