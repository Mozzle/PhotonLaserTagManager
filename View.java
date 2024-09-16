import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

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
    public JButton ClearScreenButton, StartGameButton;
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
     *  the splash screen to the player entry screen.
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
            if (PlayerEntryPanePadding != (int)((windowWidth - 780)/ 2)) {
                PlayerEntryPanePadding = (int)((windowWidth - 780)/ 2);
                PlayerEntryPanes.setBorder(new EmptyBorder(0, PlayerEntryPanePadding, 0, PlayerEntryPanePadding));
            }
            
        }

        // Block that handles the COUNTDOWN SCREEN
        if (model.getSystemState() == Model.COUNTDOWN_SCREEN && !inCountDownScreen) {
            inPlayerEntryScreen = false;
            inCountDownScreen=true; 
            this.PlayerEntryScreenDeleter(); 
            this.drawCountDownScreen();
        }

        // Block that handles the GAME SCREEN
        if (model.getSystemState() == Model.PLAY_ACTION_SCREEN && !inGameScreen) {
            inGameScreen = true;
            // TODO: Link a method here that handles all the sprites and objects
            // for the game screen. Or implement it here directly.
        }
        
        /*-----------------------------------------------------
        If model has a new tooltip for us to add, add it
        -----------------------------------------------------*/
        if (model.newToolTip == true) {
            
            model.newToolTip = false;
            toolTipCounter++;
            toolTipLabel = model.toolTip;
            toolTipLabel.setBounds((int)((windowWidth - toolTipLabel.getWidth()) / 2), (580 + toolTipCounter * 30), 1000, 30);
            toolTipLabel.setBorder(new EmptyBorder(0, 100, 0, 100));
            PlayerEntryPanes.add(toolTipLabel, BorderLayout.SOUTH);
            toolTipLabel.setVisible(true);
            timer.schedule(new toolTipTimeout(),4500);
        }

        if (prevToolTipCounter != toolTipCounter) {
            for (int i = 2; i < PlayerEntryPanes.getComponentCount(); i++) {
                PlayerEntryPanes.getComponent(i).setBounds((int)((windowWidth - toolTipLabel.getWidth()) / 2), (580 + i * 30), 1000, 30);
            }
            prevToolTipCounter = toolTipCounter;
        }

        /*-----------------------------------------------------
        Update the '>>>>' row selection arrows
        -----------------------------------------------------*/

        // Check if a text field has a current focus, if so handle it
        if (model.getSystemState() == Model.PLAYER_ENTRY_SCREEN 
        && KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner() != null)
            handleSystemFocus();
    }

    /*--------------------------------------------------
     * 
     *  handleSystemFocus()
     * 
     *  DESCRIPTION: Cleaner method to handle when an
     *  object is given focus in the view.
     * 
     -------------------------------------------------*/

     public void handleSystemFocus() {
        // Attempt to retrieve a reference to the current focused component
        try {
            
            // Create a reference to the last object that has been focused
            Component LastFocusedComponent = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
            // Create an indication to the user that this row is selected
            LastFocusedComponent.setBackground(Color.LIGHT_GRAY);
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
                            idToCheck = Integer.valueOf(model.PlayerIDBoxes.get(lastSelectedRow).getTextFromField());
                            indexToCompare = lastSelectedRow;
                        }
                        else if (lastSelectedTeam == 'G') {
                            idToCheck = Integer.valueOf(model.PlayerIDBoxes.get(lastSelectedRow + Model.NUM_MAX_PLAYERS_PER_TEAM).getTextFromField());
                            indexToCompare = lastSelectedRow + Model.NUM_MAX_PLAYERS_PER_TEAM;
                        }
                        
                        // Query database for the entered ID.
                        String codename = model.database.searchDB(Database.PARAM_ID, idToCheck, "");
    
                        // If our ID already exists, return a tooltip to the user
                        if (codename != "") {
                            boolean notADuplicate = true;
                            for (int i = 0; i < (Model.NUM_MAX_PLAYERS_PER_TEAM * 2); i++) {
                                // Ensure that the ID doesn't exist in another textbox locally
                                if (String.valueOf(idToCheck).equals(model.PlayerIDBoxes.get(i).getTextFromField()) && (i != indexToCompare)) {
                                    model.toolTip(codename + " (ID: " + idToCheck + ") is already in this game!");
                                    notADuplicate = false;
                                }
                            }

                            // Make a tooltip to say player has been added successfully
                            if (notADuplicate)
                                model.toolTip(codename + " added successfully!");
                        }
                        // If ID does not exist in the database, add it
                        else {

                            // Create a popup window to prompt the user to add a new player
                            String popupInputID = "";
                            JTextField NewPlayerIDField = new JTextField();

                            // Grab the ID based on the selected team
                            if (lastSelectedTeam == 'R') {
                                popupInputID = model.PlayerIDBoxes.get(lastSelectedRow).getTextFromField();
                                NewPlayerIDField = model.PlayerIDBoxes.get(lastSelectedRow).getTextBox();
                            }
                            else if (lastSelectedTeam == 'G') {
                                popupInputID = model.PlayerIDBoxes.get(lastSelectedRow + Model.NUM_MAX_PLAYERS_PER_TEAM).getTextFromField();
                                NewPlayerIDField = model.PlayerIDBoxes.get(lastSelectedRow  + Model.NUM_MAX_PLAYERS_PER_TEAM).getTextBox();
                            }

                            // Create the New Player Entry Popup screen
                            NewPlayerPopupScreen(popupInputID, NewPlayerIDField);
                        }

                        String EquipmentIdToSend = model.EquipmentIDBoxes.get(indexToCompare).getTextFromField();
                        //If Equipment ID field is not empty and is not the game start/end code
                        if (!EquipmentIdToSend.equals("") && !EquipmentIdToSend.equals("202") && !EquipmentIdToSend.equals("221")) {
                            //TODO!: Check all other Equipment ID fields for this equipment ID, make sure that that equipment ID doesn't already exist in the game
                            //before sending out the UDP.
                            //Make this ^ a function in model and do some real modular programming, silly
                            boolean tmpDuplicateFlag = model.checkEquipmentIDFieldsForDupicates(indexToCompare);

                            if (tmpDuplicateFlag) {
                                model.toolTip("This Equipment ID is already in use in this game!");
                            }
                            else {
                                netController.transmit(model.EquipmentIDBoxes.get(indexToCompare).getTextFromField());
                            }
                            
                        }

                        // Send tooltip saying that the equipment ID is invalid.
                        else {
                            String tmpTeam = "";
                            if (indexToCompare < Model.NUM_MAX_PLAYERS_PER_TEAM) {
                                tmpTeam = "Red";
                            }
                            else {
                                tmpTeam = "Green";
                            }
                            // "Invalid Equipment ID for Red team Player 5"
                            model.toolTip("Invalid Equipment ID for " + tmpTeam + " Team Player " + (indexToCompare % Model.NUM_MAX_PLAYERS_PER_TEAM));
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
                ClearScreenButton.setBackground(new Color(0, 66, 32));
                StartGameButton.setBackground(new Color(0, 66, 32));

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
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
        }
     }

    /*--------------------------------------------------
     * 
     *  drawPlayerEntryScreen()
     * 
     *  DESCRIPTION: Creates and draws the Player Entry
     *  Screen, and all Jpanels and elements therein.
     * 
     *  REQUIREMENTS: 0009,
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
        JPanel Buttons = new JPanel();
        Buttons.setLayout(new BoxLayout(Buttons, BoxLayout.X_AXIS));
        Buttons.setBackground(Color.BLACK);
        Buttons.setOpaque(false);

        JPanel ButtonsCenter = new JPanel();
        ButtonsCenter.setBackground(Color.BLACK);
        ButtonsCenter.setLayout(new FlowLayout(FlowLayout.CENTER));

        ClearScreenButton = new JButton("(F1) - Clear Screen");
        ClearScreenButton.setPreferredSize(new Dimension(175, 60));
        ClearScreenButton.setMaximumSize(new Dimension(175, 60));
        ClearScreenButton.setBackground(new Color(0, 66, 32));
        ClearScreenButton.setForeground(Color.WHITE);
        ClearScreenButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e){  
            model.clearTextBoxes();
        }  
        }); 
        ButtonsCenter.add(ClearScreenButton);

        StartGameButton = new JButton("(F5) - Start Game");
        StartGameButton.setPreferredSize(new Dimension(175, 60));
        StartGameButton.setMaximumSize(new Dimension(175, 60));
        StartGameButton.setBackground(new Color(0, 66, 32));
        StartGameButton.setForeground(Color.WHITE);
        StartGameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                model.PlayerEntryScreenDeleter();
            }  
        }); 
        ButtonsCenter.add(StartGameButton);
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
        
        RedTeamTextBoxPane.setBackground(new Color(207, 0, 0));
        RedTeamTextBoxPane.setPreferredSize(new Dimension(375, 600));
        //RedTeamTextBoxPane.setBorder(new EmptyBorder(0, -100, 0, 0));
        RedTeamTextBoxPane.setLayout(layout);

        GreenTeamTextBoxPane.setBackground(new Color(10, 160, 0));
        GreenTeamTextBoxPane.setPreferredSize(new Dimension(375, 600));
        //GreenTeamTextBoxPane.setBorder(new EmptyBorder(0, 0, 0, -100));
        GreenTeamTextBoxPane.setLayout(layout);

        // Set up the 'Text Fields' panel that will be placed inside the RedTeamTextBoxPane
        JPanel TextFieldsR = new JPanel(new GridBagLayout());
        TextFieldsR.setName("RedTextFields");
        GridBagConstraints tFR = new GridBagConstraints();
        tFR.fill = GridBagConstraints.BOTH;
        tFR.anchor = GridBagConstraints.NORTH;
        tFR.ipady = 4;
        TextFieldsR.setBackground(new Color(175, 31, 0));

        // 'Red Team' label
        tmpJLabel = new JLabel("Red Team", SwingConstants.CENTER);
        tmpJLabel.setFont(new Font("Verdana", Font.BOLD, 22));
        tmpJLabel.setForeground(Color.WHITE);
        tFR.gridx = 0;
        tFR.gridy = 0;
        tFR.gridwidth = 6;
        TextFieldsR.add(tmpJLabel, tFR);

        // Set up the 'Text Fields' panel that will be placed inside the GreenTeamTextBoxPane
        JPanel TextFieldsG = new JPanel(new GridBagLayout());
        TextFieldsG.setName("GreenTextFields");
        GridBagConstraints tFG = new GridBagConstraints();
        tFG.fill = GridBagConstraints.BOTH;
        tFG.anchor = GridBagConstraints.NORTH;
        tFG.ipady = 4;
        TextFieldsG.setBackground(new Color(8, 120, 0));

        // 'Green Team' label
        tmpJLabel = new JLabel("Green Team", SwingConstants.CENTER);
        tmpJLabel.setFont(new Font("Verdana", Font.BOLD, 22));
        tmpJLabel.setForeground(Color.WHITE);
        tFG.gridx = 0;
        tFG.gridy = 0;
        tFG.gridwidth = 6;
        TextFieldsG.add(tmpJLabel, tFG);

        //Draw "Playet ID", Red Team
        tmpJLabel = new JLabel("Player ID", SwingConstants.CENTER);
        tmpJLabel.setForeground(Color.WHITE);
        tFR.weightx = 0.5;
        tFR.gridx = 2;
        tFR.gridy = 1;
        tFR.gridwidth = 2;
        TextFieldsR.add(tmpJLabel, tFR);
        
        // Draw "Player ID", Green Team
        tmpJLabel = new JLabel("Player ID", SwingConstants.CENTER);
        tmpJLabel.setForeground(Color.WHITE);
        tFG.weightx = 0.5;
        tFG.gridx = 2;
        tFG.gridy = 1;
        tFG.gridwidth = 2;
        TextFieldsG.add(tmpJLabel, tFG);

        // Draw "Equipment ID", Red Team
        tmpJLabel = new JLabel("Equipment ID", SwingConstants.CENTER);
        tmpJLabel.setForeground(Color.WHITE);
        tFR.gridx = 4;
        tFR.gridy = 1;
        tFR.gridwidth = 2;
        TextFieldsR.add(tmpJLabel, tFR);

        // Draw "Equipment ID", Green Team
        tmpJLabel = new JLabel("Equipment ID", SwingConstants.CENTER);
        tmpJLabel.setForeground(Color.WHITE);
        tFG.gridx = 4;
        tFG.gridy = 1;
        tFG.gridwidth = 2;
        TextFieldsG.add(tmpJLabel, tFG);

        if (model.getNumPlayerIDBoxes() != 0) {
            // Draw the Red Team Text Boxes
            for (int i = 0; i < Model.NUM_MAX_PLAYERS_PER_TEAM; i++) {
                tFR.weightx = 0.1;
                tFR.gridx = 0;
                tFR.gridy = i + 2;
                tFR.gridwidth = 1;
                rowSelectionLabel.add(new JLabel("           ", SwingConstants.LEFT));
                rowSelectionLabel.get(i).setForeground(Color.WHITE);
                TextFieldsR.add(rowSelectionLabel.get(i), tFR);
                tFR.weightx = 0.3;
                tFR.gridx = 1;
                tFR.gridy = i + 2;
                tFR.gridwidth = 1;
                tmpJLabel = new JLabel(String.valueOf(i), SwingConstants.CENTER);
                tmpJLabel.setForeground(Color.WHITE);
                TextFieldsR.add(tmpJLabel, tFR);
                
                tFR.weightx = 0.5;
                tFR.gridx = 2;
                tFR.gridy = i + 2;
                tFR.gridwidth = 2;
                TextFieldsR.add(model.getPlayerIDBoxAt(i), tFR);

                tFR.gridx = 4;
                tFR.gridy = i + 2;
                tFR.gridwidth = 2;
                TextFieldsR.add(model.getEquipmentIDBoxAt(i), tFR);
            }
            // Draw the Green Team Text Fields
            for (int i = Model.NUM_MAX_PLAYERS_PER_TEAM; i < (Model.NUM_MAX_PLAYERS_PER_TEAM * 2); i++) {
                tFG.weightx = 0.1;
                tFG.gridx = 0;
                tFG.gridy = i + 2;
                tFG.gridwidth = 1;
                rowSelectionLabel.add(new JLabel("           ", SwingConstants.LEFT));
                rowSelectionLabel.get(i).setForeground(Color.WHITE);
                TextFieldsG.add(rowSelectionLabel.get(i), tFG);
                tFG.weightx = 0.3;
                tFG.gridx = 1;
                tFG.gridy = i + 2;
                tFG.gridwidth = 1;
                tmpJLabel = new JLabel(String.valueOf(i - Model.NUM_MAX_PLAYERS_PER_TEAM), SwingConstants.CENTER);
                tmpJLabel.setForeground(Color.WHITE);
                TextFieldsG.add(tmpJLabel, tFG);
                
                tFG.weightx = 0.5;
                tFG.gridx = 2;
                tFG.gridy = i + 2;
                tFG.gridwidth = 2;
                TextFieldsG.add(model.getPlayerIDBoxAt(i), tFG);

                tFG.gridx = 4;
                tFG.gridy = i + 2;
                tFG.gridwidth = 2;
                TextFieldsG.add(model.getEquipmentIDBoxAt(i), tFG);
            }
            TextFieldsR.setPreferredSize(new Dimension(350, 550));
            TextFieldsG.setPreferredSize(new Dimension(350, 550));
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

    //Draws countdown screen: unfinished
    public void drawCountDownScreen(){

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
     *  REQUIREMENTS: 
     * 
     --------------------------------------------------*/

    public void PlayerEntryScreenDeleter(){
        for(int i=this.getComponentCount() - 1; i>= 0 ; i--)
            this.remove(i);        
    }

    public void NewPlayerPopupScreen(String idInput, JTextField IDBox) {

        // Text fields on popup window
        model.setNewPlayerPopup(true);
        JTextField NewPlayerName = new JTextField(10);
        JTextField NewPlayerID = new JTextField(5);

        NewPlayerID.setText(idInput);

        // Input sanitation for New Player ID Textfield
        NewPlayerID.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent ke) {
                if ( ( ke.getKeyChar() < '0' && ke.getKeyChar() >= ' ' )
                || ( ke.getKeyChar() >= ':' && ke.getKeyChar() <= '~' ) ) {
                      NewPlayerID.setEditable(false);
                   } else {
                      NewPlayerID.setEditable(true);
                   }
            }
         }); 

        String hintLine1 = "Unknown Player ID entered, would";
        String hintLine2 = "you like to create a new Player?";

        // Flag to mark the popup window ready to close
        boolean closePopupFlag = false;
        
        while (!closePopupFlag) {

            // making the popup window and adding elements to it
            JPanel NewPlayerPopup = new JPanel();
            NewPlayerPopup.setPreferredSize(new Dimension(250, 125));
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

            //Create the dialog popup with the ok/cancel buttons and wait for the window to be closed
            int result = JOptionPane.showConfirmDialog(null, NewPlayerPopup, "New Player Entry", JOptionPane.OK_CANCEL_OPTION);
            // If the user clicked the "OK" button
            if (result == JOptionPane.OK_OPTION) {
                //Check if the entered ID exists in the database
                String searchResult = model.database.searchDB(Database.PARAM_ID, Integer.valueOf(NewPlayerID.getText()), "");

                // If the entered ID doesn't exist in the DB, add the new player to the DB. 
                if (searchResult == "" && Integer.valueOf(NewPlayerID.getText()) > 0 && NewPlayerID.getText().length() >= 1 && NewPlayerName.getText().length() >= 1) {
                    model.database.insertDB(Database.PARAM_ID_AND_CODENAME, Integer.valueOf(NewPlayerID.getText()), NewPlayerName.getText());
                    IDBox.setText(NewPlayerID.getText());
                    model.toolTip(NewPlayerName.getText() + " added successfully!");
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
                IDBox.setText("");
                closePopupFlag = true;
            }
        }

        model.setNewPlayerPopup(false);
    }
}
