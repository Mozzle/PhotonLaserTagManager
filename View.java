import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.KeyboardFocusManager;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.event.ActionListener;
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
    private int windowHeight, windowWidth;
    private boolean inPlayerEntryScreen, inCountDownScreen;
    public JPanel RedTeamTextBoxPane, GreenTeamTextBoxPane, PlayerEntryPanes;
    public JLabel toolTipLabel;
    public ArrayList<JLabel> rowSelectionLabel;
    public Timer timer;
    public int toolTipCounter;
    public int lastSelectedRow;
    public String lastSelectedTeam;
    JButton ClearScreenButton, StartGameButton;

    // Variable declarations

    /*-------------------------------------------------
     *
     *      View()
     *
     *  DESCRIPTION: View Class Initializer
     *
     *  REQUIREMENTS:
     *
    ------------------------------------------------- */

    public View(Controller c, Model m)
    {
        c.setView(this);
        model = m;
        inPlayerEntryScreen = false;
        inCountDownScreen = false;
        RedTeamTextBoxPane = new JPanel();
        RedTeamTextBoxPane.setVisible(false);
        GreenTeamTextBoxPane = new JPanel();
        GreenTeamTextBoxPane.setVisible(false);
        timer = new Timer();
        toolTipCounter = 0;
        rowSelectionLabel = new ArrayList<JLabel>();
        lastSelectedRow = 0;
        lastSelectedTeam = "";
        // Initializations
    }

    /*-------------------------------------------------
     *
     *      paintComponent()
     *
     *  DESCRIPTION: Draws the images to the screen
     *
     *  REQUIREMENTS:
     *
    ------------------------------------------------- */
    public void paintComponent(Graphics g)
    {
        g.setColor(new Color(0, 0, 0)); // Black Background
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
     *      loadImage()
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
     *      setScreenSize()
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
     *      update()
     * 
     *  DESCRIPTION: This function is used to update
     *  model and is the entry point for changing from
     *  the splash screen to the player entry screen.
     * 
     *  REQUIREMENTS: 
     -------------------------------------------------*/
    public void update() {
        model.updateScreenSize(windowWidth, windowHeight);
        
        if (model.getSystemState() == Model.PLAYER_ENTRY_SCREEN && !inPlayerEntryScreen) { // PLAYER ENTRY SCREEN
            inPlayerEntryScreen = true;
            this.drawPlayerEntryScreen();
        }

        if(model.getSystemState()==Model.COUNTDOWN_SCREEN && !inCountDownScreen){
            inPlayerEntryScreen = false;
            inCountDownScreen=true; 
            this.PlayerEntryScreenDeleter(); 
            this.drawCountDownScreen();
        }
        
        /*-----------------------------------------------------
        If model has a new tooltip for us to add, add it
        -----------------------------------------------------*/
        if (model.newToolTip == true) {
            model.newToolTip = false;
            toolTipCounter++;
            toolTipLabel = model.toolTip;
            toolTipLabel.setBounds(10, (580 + toolTipCounter * 30), 1000, 30);
            PlayerEntryPanes.add(toolTipLabel, BorderLayout.SOUTH);
            toolTipLabel.setVisible(true);
            timer.schedule(new toolTipTimeout(), 3000);
            System.out.println("In View");
        }

        /*-----------------------------------------------------
        Update the '>>>>' row selection arrows
        -----------------------------------------------------*/
        if (model.getSystemState() == Model.PLAYER_ENTRY_SCREEN 
        && KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner() != null) {
            
            try {
                KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner().setBackground(Color.LIGHT_GRAY);

                int selectedY = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner().getY();

                if (lastSelectedRow != ((int)((selectedY - 121)/24))
                || (lastSelectedTeam != KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner().getParent().getName())) { // If we have selected a different Row

                    lastSelectedRow = ((int)((selectedY - 121)/24));
                    lastSelectedTeam = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner().getParent().getName();
            
                
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

                    if (KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner().getParent() != null) {
                        if (lastSelectedTeam == "RedTextFields") {
                            rowSelectionLabel.get(lastSelectedRow).setBackground(Color.BLACK);
                            rowSelectionLabel.get(lastSelectedRow).setText("  >>>>");
                        }
                        else if (lastSelectedTeam == "GreenTextFields") {
                            rowSelectionLabel.get(lastSelectedRow + Model.NUM_MAX_PLAYERS_PER_TEAM).setText("  >>>>");
                        }
                    }
                }
                
            }
            catch (Exception e) {
                // getFocusOwner() is not reliable, so we need to pass and do nothing if it 
                // returns null
            }
        }
    }

    /*--------------------------------------------------
     * 
     *      drawPlayerEntryScreen()
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
        ClearScreenButton.setPreferredSize(new Dimension(160, 60));
        ClearScreenButton.setMaximumSize(new Dimension(160, 60));
        ClearScreenButton.setBackground(new Color(0, 66, 32));
        ClearScreenButton.setForeground(Color.WHITE);
        ClearScreenButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e){  
            model.clearTextBoxes();
        }  
        }); 
        ButtonsCenter.add(ClearScreenButton);

        StartGameButton = new JButton("(F5) - Start Game");
        StartGameButton.setPreferredSize(new Dimension(160, 60));
        StartGameButton.setMaximumSize(new Dimension(160, 60));
        StartGameButton.setBackground(new Color(0, 66, 32));
        StartGameButton.setForeground(Color.WHITE);
        StartGameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){  
                model.startGameButtonHit();
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

        
        RedTeamTextBoxPane.setBackground(new Color(207, 0, 0));
        RedTeamTextBoxPane.setPreferredSize(new Dimension(375, 600));
        RedTeamTextBoxPane.setLayout(layout);

        GreenTeamTextBoxPane.setBackground(new Color(10, 160, 0));
        GreenTeamTextBoxPane.setPreferredSize(new Dimension(375, 600));
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

    public class toolTipTimeout extends TimerTask
    {
        public void run()
        {
            toolTipLabel.setVisible(false);

            int tempIndex = View.this.PlayerEntryPanes.getComponentCount() - 1;
            //Ensuring this index is >=2 prevents us from somehow deleting the player
            //Entry panels
            if (tempIndex >=2) {
                View.this.PlayerEntryPanes.remove(tempIndex);
                toolTipCounter--;
            }
        }
    }

    public void PlayerEntryScreenDeleter(){
        for(int i=0; i<this.getComponentCount(); i++){
            this.remove(i);
        }
        
    }
}
