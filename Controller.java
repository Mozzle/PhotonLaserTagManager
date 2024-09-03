import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.RenderingHints.Key;
import java.awt.event.ActionEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

public class Controller implements ActionListener, MouseListener, KeyListener
{
    // Class variables
    private View view;
    private Model model;

    /*-----------------------------------------------------------
     * 
     *      Controller()
     * 
     *  DESCRIPTION: Controller class initializer
     * 
     *  REQUIREMENTS: 
     * 
    ---------------------------------------------------------- */
    public Controller(Model m)
    {
        // Initialization
        model = m;
    }

    /*-----------------------------------------------------------
     * 
     *      update()
     * 
     *  DESCRIPTION: May be necessary to pass some parameter into
     *  model.update() depending on some state of the controller.
     * 
     *  REQUIREMENTS: 
     * 
    ---------------------------------------------------------- */

    public void update()
    {
        model.update();
    }

    void setView(View v)
	{
    view = v;
	}

    /*-----------------------------------------------------------
     * 
     *      actionPerformed()
     * 
     *  DESCRIPTION: Implementation of actionListener function
     * 
     *  REQUIREMENTS: 
     * 
    ---------------------------------------------------------- */
    public void actionPerformed(ActionEvent e)
	{
		;
	}

    /*-----------------------------------------------------------
     * 
     *      mousePressed()
     * 
     *  DESCRIPTION: Implementation of mouseListener function,
     *  this function is called by interrupt when the mouse is
     *  first clicked down
     * 
     *  REQUIREMENTS: 
     * 
    ---------------------------------------------------------- */
    public void mousePressed(MouseEvent e)
	{
	/*	//If in Edit mode and adding walls and not adding any other sprites
		if (model.getEditModeState() && model.getAddRemoveWallsState() && !model.getAddGhostState() && !model.getAddPelletState() && !model.getAddFruitState()) {			//If in editmode and add wall mode
			model.setNewWallOrigin(e.getX(), e.getY() + view.getScrollPos());		//make first corner
		} // If in edit mode and removing walls and not adding any other sprites
		else if (model.getEditModeState() && !model.getAddRemoveWallsState() && !model.getAddGhostState() && !model.getAddPelletState() && !model.getAddFruitState()) {	//if in edit mode and remove wall mode
			model.removeWallOnClick(e.getX(), e.getY() + view.getScrollPos());				//remove wall if clicking on one
		} // If in edit mode and adding ghosts
		else if (model.getEditModeState() && model.getAddGhostState()) {
			model.addNewGhost(e.getX(), e.getY() + view.getScrollPos());
		} // If in edit mode and adding pellets
		else if (model.getEditModeState() && model.getAddPelletState()) {
			model.addNewPellet(e.getX(), e.getY() + view.getScrollPos());
		} // If in edit mode and adding fruit
		else if (model.getEditModeState() && model.getAddFruitState()) {
			model.addNewFruit(e.getX(), e.getY() + view.getScrollPos());
		}
		*/
        
	}

    /*-----------------------------------------------------------
     * 
     *      mouseReleased()
     * 
     *  DESCRIPTION: Implementation of mouseListener function,
     *  this function is called by interrupt when a mouse click 
     *  is released. This function should be used for most normal
     *  mouse clicking events.
     * 
     *  REQUIREMENTS: 
     * 
    ---------------------------------------------------------- */
    public void mouseReleased(MouseEvent e) { 
		//You can use e.getX() and e.getY() to get mouse position.
      }
	public void mouseEntered(MouseEvent e) {    }
	public void mouseExited(MouseEvent e) {    }
	public void mouseClicked(MouseEvent e) {    }

    /*-----------------------------------------------------------
     * 
     *      keyPressed()
     * 
     *  DESCRIPTION: Implementation of keyListener function,
     *  this function is called by interrupt when a key is 
     *  pressed down.
     * 
     *  REQUIREMENTS: 
     * 
    ---------------------------------------------------------- */
	public void keyPressed(KeyEvent e)
	{
	}
    
    /*-----------------------------------------------------------
     * 
     *      keyReleased()
     * 
     *  DESCRIPTION: Implementation of keyListener function,
     *  this function is called by interrupt when a key is 
     *  released. Either this function or keyTyped() should be
     *  used for most user typing events. Testing is required 
     *  to determine which is better.
     * 
     *  REQUIREMENTS: 
     * 
    ---------------------------------------------------------- */
	public void keyReleased(KeyEvent e)
	{
		switch(e.getKeyCode()) {
			case KeyEvent.VK_F1:
				if (model.getSystemState() == 2) {
                    for (int i = 0; i < model.getNumPlayerIDBoxes(); i++) {
                        model.getPlayerIDBoxAt(i).setText("");
                    }
                    for (int i = 0; i < model.getNumEquipmentIDBoxes(); i++) {
                        model.getEquipmentIDBoxAt(i).setText("");
                    }
                    System.out.println("Hello?");
                }
				break;
			case KeyEvent.VK_F2:

				break;
			case KeyEvent.VK_F3:
				
				break;
			case KeyEvent.VK_F4:

				break;
            case KeyEvent.VK_A:
                System.out.println("Hello");
                break;

            case KeyEvent.VK_F5:
                break;
		
			default:
				break;
		}
	}

    /*-----------------------------------------------------------
     * 
     *      keyTyped()
     * 
     *  DESCRIPTION: Implementation of keyListener function,
     *  this function is called by interrupt when a key is 
     *  detected to have been typed Either this function or 
     *  keyReleased() should be used for most user typing events.
     *  Testing is required to determine which is better.
     * 
     *  REQUIREMENTS: 0006,
     * 
    ---------------------------------------------------------- */
	public void keyTyped(KeyEvent e)
	{
		//If model.getMode() == team input screen, send key typed
		// to model to add text to a field.
	}

}
