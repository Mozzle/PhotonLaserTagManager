import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.PlainDocument;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.*;

public class TextBox {
	/*------------------------------
		Enums
	------------------------------*/
	public static final int NUMERIC_TEXT_FIELD_TYPE = 0;
    	public static final int ALPHA_NUMERIC_TEXT_FIELD_TYPE = 1;
	public static final int DISPLAY_ONLY_NO_TYPE = 2;


	public JTextField field; //JTextField object
	Model m;
	private View view;

	/*-------------------------------------------------
     *
     *  TextBox()
     *
     *  DESCRIPTION: TextBox class constructor. Creates
     *  a JTextField object, provides some methods to
     *  access it, and provides some default settings
     *  for the text field.
     *
     *  REQUIREMENTS: 0006, 0009, 0010, 0011, 0023
     *
    ------------------------------------------------- */

	public TextBox(String name, int cols, Model m, int TextFieldType) {
		/*-----------------------------------------------------------------------
		 We have an upward reaching architecture here. This class is both 
		 controlled by Model, AND can call functions in Model
		------------------------------------------------------------------------*/
		this.m = m;
		field = new JTextField("", cols);
		field.setName(name);

		

		/*-----------------------------------------------------------------------
		Text fields have to have a keyListener that is seperate from the main
		Controller class, Have not yet decided how I will handle keys that should
		be able to be pressed in any context (e.g. F1-F5)
		------------------------------------------------------------------------*/
		if (TextFieldType == NUMERIC_TEXT_FIELD_TYPE) {

			// Input sanitation -- use a plain document as the method to push input, we can sanitize from here
		field.setDocument(new PlainDocument() {
			@Override
			public void insertString(int offset, String s, AttributeSet a) throws javax.swing.text.BadLocationException {
				// Insert nothing if there is no string
				if (s == null)
					return;
				// Only insert if our string contains the values from 0-9
				if (s.matches("[0-9]+"))
					super.insertString(offset, s, a);
				else
					m.toolTip("ID's should only be numeric (0-9)", 4500);
			}
		});

			field.addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent ke) {

					switch(ke.getKeyCode()) {
						case KeyEvent.VK_TAB:
						// Check database against this field
							break;
						case KeyEvent.VK_F1:
							if (m.getSystemState() == Model.PLAYER_ENTRY_SCREEN 
							&& m.getNewPopup() == false) {
								m.clearPlayerList();
								m.clearTextBoxes();
							}
							break;
						case KeyEvent.VK_F5:
							if (m.getSystemState() == Model.PLAYER_ENTRY_SCREEN
							&& m.getNewPopup() == false) {
		   
						   		if (m.checkStartGameConditions()) {
							   		m.PlayerEntryScreenDeleter();
						   		}
							}
							break;
						// Case F9 -- Open player popup
						case KeyEvent.VK_F9:
							if (m.getSystemState() == Model.PLAYER_ENTRY_SCREEN
							 && m.getNewPopup() == false) {
									m.setMakePlayerPopupFlag(true);
							}
							break;
						// Case F12 -- Open settings popup
						case KeyEvent.VK_F12:
							if (m.getSystemState() == Model.PLAYER_ENTRY_SCREEN 
							&& m.getNewPopup() == false) {
								m.setMakeSettingsPopupFlag(true);
							}

							break;

						case KeyEvent.VK_ENTER:
							
							try {
								int i = m.getTextBoxIndexFromName(field.getName());

								if (i >= 0 && i < (Model.NUM_MAX_PLAYERS_PER_TEAM * 2)) {
									m.PlayerIDBoxes.get(i + 1).getTextBox().requestFocus();
								}

							}
							catch (Exception e) {
								//Do nothing
							}

						case KeyEvent.VK_F2:
							if(m.getSystemState()==Model.PLAY_ACTION_SCREEN){
								view.CountDownDebug();
								System.out.println("Countdown Skip Button Pressed Textbox");
							}
						default:
							break;
					   }
				}
			 }); 
		}
		else if (TextFieldType == ALPHA_NUMERIC_TEXT_FIELD_TYPE) {

		}
		else if (TextFieldType == DISPLAY_ONLY_NO_TYPE) {
			field.setEditable(false);
			field.setBackground(new Color(135, 135, 135));
			if (field.getName().charAt(0) == 'R') {
				field.setForeground(new Color(91, 0, 14));
			}
			else {
				field.setForeground(new Color(0, 50, 0));
			}
			field.setFont(new Font("Arial", Font.BOLD, 12));
			field.setFocusable(false);
			field.setFocusTraversalKeysEnabled(false);
			field.setHorizontalAlignment(JTextField.CENTER);
		}
	}

	public JTextField getTextBox() {
		return this.field;
	}

	public String getTextFromField() {
		return field.getText();
	}

	public void update() {

	}

	public void updateScreenSize(int screenW, int screenH) {

	}

}
