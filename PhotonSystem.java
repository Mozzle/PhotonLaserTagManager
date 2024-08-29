
import javax.swing.JFrame;
import java.awt.Toolkit;

public class PhotonSystem extends JFrame {
    
    // Variable Declarations
    private Model model;
    private Controller controller;
    private View view;

    /*-----------------------------------------------------------
     * 
     *      PhotonSystem()
     * 
     *  DESCRIPTION: Highest level program initializer
     * 
     *  REQUIREMENTS: 5, 
     * 
    ---------------------------------------------------------- */

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

    /*-----------------------------------------------------------
     * 
     *      run()
     * 
     *  DESCRIPTION: The main program superloop. Executes for
     *  entire duration of program.
     * 
     *  REQUIREMENTS: 4, 
     * 
    ---------------------------------------------------------- */

    public void run() {
        while(true) //Main program superloop
        {
            controller.update();
            view.setScreenSize(this.getWidth(), this.getHeight());
            view.update();
            view.repaint();
            Toolkit.getDefaultToolkit().sync();


            // This probably needs to change to a non-blocking timer
            try
			{
				Thread.sleep(12);
			} catch(Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
        }
    }


    /*-----------------------------------------------------------
     * 
     *      main()
     * 
     *  DESCRIPTION: Entry point of all Java program
     * 
     *  REQUIREMENTS:
     * 
    ---------------------------------------------------------- */

    public static void main(String[] args) 
    {
        PhotonSystem system = new PhotonSystem();
        system.run();
    }
}
