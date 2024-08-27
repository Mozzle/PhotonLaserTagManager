import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.File;
import java.awt.Color;
import java.awt.Font;

public class View extends JPanel {

    private Model model;
    // Variable declarations

    public View(Controller c, Model m)
    {
        c.setView(this);
        model = m;
        // Initializations
    }

    public void update(Graphics g) //Maybe change to paintComponent()?
    {
        g.setColor(new Color(0, 0, 0)); //Black Background
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
    }
}
