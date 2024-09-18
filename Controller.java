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
     *  Controller()
     * 
     *  DESCRIPTION: Controller class initializer
     * 
    ---------------------------------------------------------- */

    public Controller(Model m)
    {
        // Initialization
        model = m;
    }

    /*-----------------------------------------------------------
     * 
     *  update()
     * 
     *  DESCRIPTION: May be necessary to pass some parameter into
     *  model.update() depending on some state of the controller.
     * 
    ---------------------------------------------------------- */

    public void update()
    {
        model.update();
    }

    /*-----------------------------------------------------------
     * 
     *  setView(View v)
     * 
     *  DESCRIPTION: Set's the models reference to the view.
     * 
    ---------------------------------------------------------- */

    void setView(View v)
	{
        view = v;
	}

    /*-----------------------------------------------------------
     * 
     *  actionPerformed()
     * 
     *  DESCRIPTION: Implementation of actionListener function.
     *  Executes logic specific to the type of action passed in.
     * 
    ---------------------------------------------------------- */
    public void actionPerformed(ActionEvent e)
	{

	}

    /*-----------------------------------------------------------
     * 
     *  mousePressed()
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

	}

    /*-----------------------------------------------------------
     * 
     *  mouseReleased(), mouseEntered(), mouseExited(), mouseClicked()
     * 
     *  DESCRIPTION: Implementation of mouseListener function,
     *  this function is called by interrupt when a mouse click 
     *  is released. This function should be used for most normal
     *  mouse clicking events.
     * 
    ---------------------------------------------------------- */

    // Use e.getX() and e.getY() to get mouse coordinates
    public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) {}

    /*-----------------------------------------------------------
     * 
     *  keyPressed()
     * 
     *  DESCRIPTION: Implementation of keyListener function,
     *  this function is called by interrupt when a key is 
     *  pressed down. 
     * 
    ---------------------------------------------------------- */

	public void keyPressed(KeyEvent e)
	{
	}
    
    /*-----------------------------------------------------------
     * 
     *  keyReleased()
     * 
     *  DESCRIPTION: Implementation of keyListener function,
     *  this function is called by interrupt when a key is 
     *  released. Either this function or keyTyped() should be
     *  used for most user typing events. Testing is required 
     *  to determine which is better.
     * 
     *  For the Player Entry Screen, the key listeners for the 
     *  TextBox objects actually do the majority of handling
     *  for the input controls. For the vast majority of 
     *  the time in that screen, TextBoxes have the 'focus' 
     *  for keyboard inputs. This class' key listener is 
     *  attatched to the frame. The frame has a collection of 
     *  objects inside it, including the TextBoxes. The 
     *  keyListeners of objects inside the fram take precedence 
     *  over the keyListener of the frame. In case the focus is 
     *  not on any 'focusable' object, we set the keyListener 
     *  here to accept F1 and F5 commands.
     * 
     *  REQUIREMENTS: 0010, 0011,
     * 
    ---------------------------------------------------------- */
	public void keyReleased(KeyEvent e)
	{
		switch(e.getKeyCode()) {
            
            /// TODO: Ensure for case F1, when the text boxes are cleared any updates needed
            /// are made accordingly with the DB. If the DB is not needed to be updated 
            /// on clearing, then ignore this and remove this comment please.

            // Case F1 -- Clear all player entries
			case KeyEvent.VK_F1:
				if (model.getSystemState() == Model.PLAYER_ENTRY_SCREEN
                 && model.getNewPlayerPopupStatus() == false) {
                    model.clearTextBoxes();
                 }
                    
				break;

            // Case F2 -- No logic
			case KeyEvent.VK_F2:
				break;

            // Case F3 -- No logic
			case KeyEvent.VK_F3:
				break;

            // Case F4 -- No logic
			case KeyEvent.VK_F4:
				break;

            // Case F5 -- Start the game
            case KeyEvent.VK_F5:
                if (model.getSystemState() == Model.PLAYER_ENTRY_SCREEN
                 && model.getNewPlayerPopupStatus() == false) {

                    // Delete the player entry screen
                    if (model.checkStartGameConditions()) {
                        model.PlayerEntryScreenDeleter();
                    }
                }
                break;
            case KeyEvent.VK_F9:
                if (model.getSystemState() == Model.PLAYER_ENTRY_SCREEN
                 && model.getNewPlayerPopupStatus() == false) {
                        model.setMakeNewPlayerPopupFlag(true);
                }
                break;

            

            // Default case -- Do nothing
			default:
				break;
		}
	}

    /*-----------------------------------------------------------
     * 
     *  keyTyped()
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
		// If model.getMode() == team input screen, send key typed
		// to model to add text to a field.
	}

}
