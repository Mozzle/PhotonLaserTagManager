
import javax.swing.JFrame;
import java.awt.*;

public class PhotonSystem extends JFrame {
    
    // Variable Declarations
    private Model model;
    private Controller controller;
    private View view;
    private NetController netController;

    /*-----------------------------------------------------------
     * 
     *  PhotonSystem()
     * 
     *  DESCRIPTION: Highest level program initializer
     * 
     *  REQUIREMENTS: 0005, 0023
     * 
    ---------------------------------------------------------- */

    public PhotonSystem()
    {
        /// REFACTOR!!!

        // Our controller should only have access to Model, View and NetController
        // Our view should only have access to Model

        model = new Model();
        controller = new Controller(model);
        netController = new NetController();
        view = new View(controller, model, netController);
        view.addMouseListener(controller);
		this.addKeyListener(controller);

        this.setTitle("Photon Manager");
		this.setSize(1000, 800);
		this.setFocusable(true);
		this.getContentPane().add(view);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
        model.setNet(netController);
        netController.startListener();
    }

    
    /*-----------------------------------------------------------
     * 
     *      run()
     * 
     *  DESCRIPTION: The main program superloop. Executes for
     *  entire duration of program.
     * 
     *  REQUIREMENTS: 0004, 
     * 
    ---------------------------------------------------------- */

    public void run() {
        while(true) //Main program superloop
        {
            controller.update();
            view.setScreenSize(this.getWidth(), this.getHeight());
            controller.updateView();
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
     *  main()
     * 
     *  DESCRIPTION: Entry point of the Java program
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
