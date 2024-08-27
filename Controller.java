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
     *  DESCRIPTION: May not be necessary to implement, since 
     *  Controller uses interrupt functions like mousePressed()
     * 
     *  REQUIREMENTS: 
     * 
    ---------------------------------------------------------- */

    public void update()
    {
        
    }

    void setView(View v)
	{
    view = v;
	}

    public void actionPerformed(ActionEvent e)
	{
		;
	}

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

    public void mouseReleased(MouseEvent e) { 

      }
	public void mouseEntered(MouseEvent e) {    }
	public void mouseExited(MouseEvent e) {    }
	public void mouseClicked(MouseEvent e) {    }

	public void keyPressed(KeyEvent e)
	{
	}

	public void keyReleased(KeyEvent e)
	{
	}

	public void keyTyped(KeyEvent e)
	{
	}

}
