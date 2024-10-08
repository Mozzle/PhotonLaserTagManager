import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JTextField;
import javax.swing.event.DocumentListener;

import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.RenderingHints.Key;
import java.awt.event.ActionEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

public class Controller implements ActionListener, MouseListener, KeyListener
{
    // Class variables
    private View view;
    private Model model;
    
    // Reference to all textboxes in the player entry screen
    ArrayList<TextBox> masterTextboxList;

    /*-----------------------------------------------------------
     * 
     *  Controller()
     * 
     *  DESCRIPTION: Controller class initializer
     * 
     *  REQUIREMENTS: 0023,
     * 
    ---------------------------------------------------------- */

    public Controller(Model m)
    {
        // Initialization
        model = m;
    }

    /*-----------------------------------------------------------
     * 
     *  initTextboxListener()
     * 
     *  DESCRIPTION: Attachs a key listener to all textboxes in the player entry screen.
     * 
    ---------------------------------------------------------- */
    public void initTextboxListener() {
        // Create a list of all textboxes in the player entry screen to attach key listeners to
        masterTextboxList = new ArrayList<TextBox>();
        masterTextboxList.addAll(model.PlayerIDBoxes);
        masterTextboxList.addAll(model.EquipmentIDBoxes);

        // Attach a key listener to every box on our list
        for (int i = 0; i < masterTextboxList.size(); i++) {
            masterTextboxList.get(i).getTextBox().getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void changedUpdate(DocumentEvent e) {  } // Do nothing

                @Override
                public void insertUpdate(DocumentEvent e) { handleDocEvent(e); }

                @Override
                public void removeUpdate(DocumentEvent e) { handleDocEvent(e); }
            });
        }
    }

    /*-----------------------------------------------------------
     * 
     *  handleDocEvent(DocumentEvent e)
     * 
     *  DESCRIPTION: Handles a change in a document, or in other words a key change
     *  in a textbox. This function is called when a change is detected in a textbox.
     * 
    ---------------------------------------------------------- */
    public void handleDocEvent(DocumentEvent e) {
        // Grab the reference document to identify which row we are working with
        Document compareDoc = e.getDocument();
        JTextField referenceRow = null;
        for (int j = 0; j < masterTextboxList.size(); j++) {
            if (masterTextboxList.get(j).getTextBox().getDocument() == compareDoc) {
                referenceRow = masterTextboxList.get(j).getTextBox();
            }
        }

        // If we find no match, exit early
        if (referenceRow == null)
            return;

        // Find the row we are working with
        int rowIndex = model.getTextBoxIndexFromName(referenceRow.getName());

        // Grab references for this row
        JTextField IDRef = model.PlayerIDBoxes.get(rowIndex).getTextBox();
        JTextField EquipIDRef = model.EquipmentIDBoxes.get(rowIndex).getTextBox();
        JTextField NameRef = model.CodenameBoxes.get(rowIndex).getTextBox();

        String savedNormID = IDRef.getText();
        String savedEquipID = EquipIDRef.getText();

        // Check if the row contains a valid player
        Player checkPlayer = checkRefForPlayer(IDRef, EquipIDRef, NameRef, rowIndex);

        // Check if our reference textboxes contain info belonging to another player
        if (checkPlayer != null) {
            // If so, add our player to the remove queue (since a modification has been made)
            // and reset the textboxes to contain the previously entered data
            model.addComponentToRemoveQueue(checkPlayer, IDRef, savedNormID);
            model.addComponentToRemoveQueue(null, EquipIDRef, savedEquipID);
        }
    }

    /*-----------------------------------------------------------
     * 
     *  checkRefForPlayer(JTextField NormalIDBox, JTextField EquipIDBox, 
     *  JTextField NameBox, int lastSelectedRow)
     * 
     *  DESCRIPTION: Checks if the given references already exist in another player object.
     *  Returns the offending player object if found, otherwise returns null.
     * 
    ---------------------------------------------------------- */
    public Player checkRefForPlayer(JTextField NormalIDBox, JTextField EquipIDBox, JTextField NameBox, int lastSelectedRow) {
        // Check if our references are null, if so exit early
        if (NormalIDBox == null || EquipIDBox == null || NameBox == null)
            return null;

        // Check if a player has populated the row by looking for a name
        if (NameBox.getText().equals(""))
            return null;

        // Adjust strings in case they are empty, -1 will return no search
        String tmpNormID = NormalIDBox.getText();
        String tmpEquipID = EquipIDBox.getText();
        if (tmpNormID.equals("")) {
            tmpNormID = "-1";
        }
        if (tmpEquipID.equals("")) {
            tmpEquipID = "-1";
        }
        
        // Create player search parameters
        int normalIDSearched = Integer.valueOf(tmpNormID);
        int equipIDSearched = Integer.valueOf(tmpEquipID);
        String nameSearched = NameBox.getText();

        // Identify the player exists on the list
        Player checkPlayer = model.identifyPlayer(normalIDSearched, equipIDSearched, nameSearched, lastSelectedRow);

        // Identify if our player already exists, if so then remove the player from the list
        if (checkPlayer != null) {
            return checkPlayer;
        }

        return null;
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
     *  updateView()
     * 
     *  DESCRIPTION: Updates the view
     * 
    ---------------------------------------------------------- */
    public void updateView()
    {
        view.update();
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
                 && model.getNewPopup() == false) {
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
                 && model.getNewPopup() == false) {

                    // Delete the player entry screen
                    if (model.checkStartGameConditions()) {
                        model.PlayerEntryScreenDeleter();
                    }
                }
                break;
            
            // Case F9 -- Open player popup
            case KeyEvent.VK_F9:
                if (model.getSystemState() == Model.PLAYER_ENTRY_SCREEN
                 && model.getMakePlayerPopupFlag() == false) {
                        model.setMakePlayerPopupFlag(true);
                }
                break;

            // Case F12 -- Open settings popup
            case KeyEvent.VK_F12:
                if (model.getSystemState() == Model.PLAYER_ENTRY_SCREEN
                 && model.getMakeSettingsPopupFlag() == false) {
                        model.setMakeSettingsPopupFlag(true);
                }
                break;

            case KeyEvent.VK_D:
                if(model.getSystemState()== Model.PLAYER_ENTRY_SCREEN){
                    view.CountDownDebug();
                    System.out.println("Button Pressed");
                }

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
