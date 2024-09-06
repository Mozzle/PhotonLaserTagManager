import javax.swing.*;

import java.awt.Color;
import java.awt.event.*;

public class TextBox {
	/*------------------------------
		Enums
	------------------------------*/
	public static final int NUMERIC_TEXT_FIELD_TYPE = 0;
    public static final int ALPHA_NUMERIC_TEXT_FIELD_TYPE = 1;


	public JTextField field; //JTextField object
	Model m;

	public TextBox(String hintText, int cols, Model m, int TextFieldType) {
		/*-----------------------------------------------------------------------
		 We have an upward reaching architecture here. This class is both 
		 controlled by Model, AND can call functions in Model
		------------------------------------------------------------------------*/
		this.m = m;
		field = new JTextField(hintText, cols);

		/*-----------------------------------------------------------------------
		Text fields have to have a keyListener that is seperate from the main
		Controller class, Have not yet decided how I will handle keys that should
		be able to be pressed in any context (e.g. F1-F5)
		------------------------------------------------------------------------*/
		if (TextFieldType == NUMERIC_TEXT_FIELD_TYPE) {
			field.addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent ke) {

					switch(ke.getKeyCode()) {
						case KeyEvent.VK_TAB:
						// Check database against this field
							break;
						case KeyEvent.VK_F1:
							if (m.getSystemState() == Model.PLAYER_ENTRY_SCREEN) {
								m.clearTextBoxes();
							}
							break;
						case KeyEvent.VK_F5:
							//TODO: Implement me!
							break;
						default:
							break;
					   }
				
					if ( ( ke.getKeyChar() < '0' && ke.getKeyChar() >= ' ' )
					|| ( ke.getKeyChar() >= ':' && ke.getKeyChar() <= '~' ) ) {
						field.setEditable(false);
						m.toolTip("ID's should only be numeric (0-9)");
						System.out.println("In TextBox");
				   	} else {
					  	field.setEditable(true);
				   	}
				
				}
			 }); 
		}
		else if (TextFieldType == ALPHA_NUMERIC_TEXT_FIELD_TYPE) {
			/*
			field.addKeyListener(new KeyAdapter() {
				//TODO: Implement Me!
			});
			*/
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
