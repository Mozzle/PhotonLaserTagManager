import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.File;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;

public class View extends JPanel {

    private Model model;
    private int windowHeight, windowWidth;
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
        model.updateScreenSize(windowWidth, windowHeight);
    }
}
