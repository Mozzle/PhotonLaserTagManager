import javax.swing.JFrame;
import java.awt.Toolkit;

public class PhotonSystem extends JFrame {
    
    // Variable Declarations
    private Model model;
    private Controller controller;
    private View view;

    public PhotonSystem()
    {
        model = new Model();
        controller = new Controller(model);
        view = new View(controller, model);
        view.addMouseListener(controller);
        view.addMouseListener(controller);
		this.addKeyListener(controller);

        this.setTitle("Photon Manager");
		this.setSize(1000, 1000);
		this.setFocusable(true);
		this.getContentPane().add(view);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
    }

    public void run() {
        while(true) //Main program superloop
        {
            controller.update();
            view.repaint();
            Toolkit.getDefaultToolkit().sync();

            try
			{
				Thread.sleep(12);
			} catch(Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
        }
    }

    public static void main(String[] args) 
    {
        PhotonSystem system = new PhotonSystem();
        system.run();
    }
}
