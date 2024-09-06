import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.KeyboardFocusManager;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.management.AttributeList;

import java.io.IOException;
import java.lang.reflect.Array;
import java.io.File;
import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
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
    public JPanel RedTeamTextBoxPane, GreenTeamTextBoxPane;
    public JLabel toolTipLabel;
    public ArrayList<JLabel> rowSelectionLabel;
    public Timer timer;
    public int toolTipCounter;
    public int lastSelectedRow;

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
        g.setColor(new Color(0, 0, 0)); //Black Background
        g.fillRect(0, 0, this.getWidth(), this.getHeight());

        if (model.getNumWindowObjects() != 0) {
            try
		    {
		    	for(int i = 0; i < model.getNumWindowObjects(); i++) //Iterate through all sprites, drawing them on the screen
		    	{											   //And accounting for the scroll position
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
    static BufferedImage loadImage(String filename) {	//Static image loading 
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
        
        if (model.getSystemState() == Model.PLAYER_ENTRY_SCREEN && !inPlayerEntryScreen) { //PLAYER ENTRY SCREEN
            inPlayerEntryScreen = true;
            this.drawPlayerEntryScreen();
        }

        if(model.getSystemState()==Model.COUNTDOWN_SCREEN && !inCountDownScreen){
            inPlayerEntryScreen = false;
            inCountDownScreen=true; 
            this.PlayerEntryScreenDeleter(); 
            this.drawCountDownScreen();
        }
        
        // If model has a new tooltip for us to add
        if (model.newToolTip == true) {
            model.newToolTip = false;
            toolTipCounter++;
            toolTipLabel = model.toolTip;
            toolTipLabel.setBounds(10, (580 + toolTipCounter * 30), 1000, 30);
            this.add(toolTipLabel, BorderLayout.SOUTH);
            toolTipLabel.setVisible(true);
            timer.schedule(new toolTipTimeout(), 3000);
            System.out.println("In View");
        }

        if (model.getSystemState() == Model.PLAYER_ENTRY_SCREEN
        && KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner() != null) {
            int selectedY = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner().getY();

            if (lastSelectedRow != ((int)((selectedY - 121)/24))) {
                lastSelectedRow = ((int)((selectedY - 121)/24));
            
                try {

                    for (int i = 0; i < rowSelectionLabel.size(); i++) {
                        rowSelectionLabel.get(i).setText("           ");
                    }

                    if (KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner().getParent() != null) {
                        if (KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner().getParent().getName() == "RedTextFields") {
                            rowSelectionLabel.get(lastSelectedRow).setText("  >>>>");
                        }
                        else if (KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner().getParent().getName() == "GreenTextFields") {
                            rowSelectionLabel.get(lastSelectedRow + Model.NUM_MAX_PLAYERS_PER_TEAM).setText("  >>>>");
                        }
                    }
                }
                catch (Exception e) {
                    // getFocusOwner() is not reliable, so we need to pass and do nothing if it 
                    // returns null
                }
            }
            //rowSelectionLabel.get((int)((selectedY - 121)/24))
            //tmp.setText("    >>>>   ");
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
    public void drawPlayerEntryScreen() {
        LayoutManager layout = new FlowLayout();
        JLabel tmpJLabel;

        
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
            this.add(RedTeamTextBoxPane, BorderLayout.WEST);
            this.add(GreenTeamTextBoxPane, BorderLayout.EAST);
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
            View.this.remove(View.this.getComponentCount() - 1);
            toolTipCounter--;
            System.out.println("In View timeout");
        }
    }

    public void PlayerEntryScreenDeleter(){
        for(int i=0; i<this.getComponentCount(); i++){
            this.remove(i);
        }
        
    }
}
