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
import java.awt.LayoutManager;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.File;
import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

public class View extends JPanel {

    private Model model;
    private int windowHeight, windowWidth;
    private boolean inPlayerEntryScreen;
    public JPanel RedTeamTextBoxPane, GreenTeamTextBoxPane;
    public JLabel RedTeamPlayerIDLabel;
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
        RedTeamTextBoxPane = new JPanel();
        RedTeamTextBoxPane.setVisible(false);
        GreenTeamTextBoxPane = new JPanel();
        GreenTeamTextBoxPane.setVisible(false);
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
     *  DESCRIPTION: Currently used for communicating 
     *  screen size data to model() and the sprites 
     *  therein.
     * 
     *  REQUIREMENTS: 
     -------------------------------------------------*/
    public void update() {
        //RedTeamTextBoxPane.setSize((int)(windowWidth * 0.2), (int)(windowHeight * 0.8));
        //GreenTeamTextBoxPane.setSize((int)(windowWidth * 0.2), (int)(windowHeight * 0.8));
        model.updateScreenSize(windowWidth, windowHeight);
        
        if (model.getSystemState() == 2 && !inPlayerEntryScreen) { //PLAYER ENTRY SCREEN
            inPlayerEntryScreen = true;
            this.drawPlayerEntryScreen();
        }
    }

    public void drawPlayerEntryScreen() {
        LayoutManager layout = new FlowLayout();
        JLabel tmpJLabel;

        
        RedTeamTextBoxPane.setVisible(true);
        RedTeamTextBoxPane.setBackground(new Color(207, 0, 0));
        RedTeamTextBoxPane.setPreferredSize(new Dimension(375, 700));
        RedTeamTextBoxPane.setLayout(layout);

        GreenTeamTextBoxPane.setVisible(true);
        GreenTeamTextBoxPane.setBackground(new Color(10, 160, 0));
        GreenTeamTextBoxPane.setPreferredSize(new Dimension(375, 700));
        GreenTeamTextBoxPane.setLayout(layout);

        
        //RedTeamTextBoxPane.add(tmpJLabel);

        tmpJLabel = new JLabel("Green Team", SwingConstants.CENTER);
        tmpJLabel.setFont(new Font("Verdana", Font.BOLD, 22));
        tmpJLabel.setForeground(Color.WHITE);
        GreenTeamTextBoxPane.add(tmpJLabel);

        JPanel TextFieldsR = new JPanel(new GridBagLayout());
        GridBagConstraints tFR = new GridBagConstraints();
        tFR.fill = GridBagConstraints.HORIZONTAL;
        tFR.anchor = GridBagConstraints.PAGE_START;
        tFR.ipady = 4;
        TextFieldsR.setBackground(new Color(175, 31, 0));

        tmpJLabel = new JLabel("Red Team", SwingConstants.CENTER);
        tmpJLabel.setFont(new Font("Verdana", Font.BOLD, 22));
        tmpJLabel.setForeground(Color.WHITE);
        tFR.gridx = 0;
        tFR.gridy = 0;
        tFR.gridwidth = 6;
        TextFieldsR.add(tmpJLabel, tFR);

        JPanel TextFieldsG = new JPanel(new GridLayout(21, 2, 5, 5));
        TextFieldsG.setBackground(new Color(8, 120, 0));

        tmpJLabel = new JLabel("Player ID", SwingConstants.CENTER);
        tmpJLabel.setForeground(Color.WHITE);
        tFR.gridx = 2;
        tFR.gridy = 1;
        tFR.gridwidth = 2;
        TextFieldsR.add(tmpJLabel, tFR);
        
        tmpJLabel = new JLabel("Player ID", SwingConstants.CENTER);
        tmpJLabel.setForeground(Color.WHITE);
        TextFieldsG.add(tmpJLabel);

        tmpJLabel = new JLabel("Equipment ID", SwingConstants.CENTER);
        tmpJLabel.setForeground(Color.WHITE);
        tFR.gridx = 4;
        tFR.gridy = 1;
        tFR.gridwidth = 2;
        TextFieldsR.add(tmpJLabel, tFR);

        tmpJLabel = new JLabel("Equipment ID", SwingConstants.CENTER);
        tmpJLabel.setForeground(Color.WHITE);
        TextFieldsG.add(tmpJLabel);

        if (model.getNumPlayerIDBoxes() != 0) {
            for (int i = 0; i < 20; i++) {
                tFR.gridx = 0;
                tFR.gridy = i + 2;
                tFR.gridwidth = 1;
                TextFieldsR.add(new JLabel("           ", SwingConstants.LEFT), tFR);
                tFR.gridx = 1;
                tFR.gridy = i + 2;
                tFR.gridwidth = 1;
                TextFieldsR.add(new JLabel(String.valueOf(i), SwingConstants.CENTER), tFR);
                
                tFR.gridx = 2;
                tFR.gridy = i + 2;
                tFR.gridwidth = 2;
                TextFieldsR.add(model.getPlayerIDBoxAt(i), tFR);

                tFR.gridx = 4;
                tFR.gridy = i + 2;
                tFR.gridwidth = 2;
                TextFieldsR.add(model.getEquipmentIDBoxAt(i), tFR);
            }
            for (int i = 20; i < 40; i++) {
                TextFieldsG.add(model.getPlayerIDBoxAt(i), BorderLayout.CENTER);
                TextFieldsG.add(model.getEquipmentIDBoxAt(i), BorderLayout.CENTER);
            }
            RedTeamTextBoxPane.add(TextFieldsR, BorderLayout.SOUTH);
            GreenTeamTextBoxPane.add(TextFieldsG, BorderLayout.CENTER);
            this.add(RedTeamTextBoxPane, BorderLayout.WEST);
            this.add(GreenTeamTextBoxPane, BorderLayout.EAST);
        }
        
    }
}
