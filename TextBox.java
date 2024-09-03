import javax.swing.*;
import java.awt.event.*;

public class TextBox {

	public JTextField field; //JTextField object

	int originalX, originalY; // the original x/y placement of the textfield. Used for dynamic
	// response to window adjustment.
	int originalW;

	public TextBox(String hintText, int cols, int x, int y, int w) {
		//this.originalX = x;
		//this.originalY = y;
		//this.originalW = w;
		field = new JTextField(hintText, cols);
	/*	field.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent ke) {
			   if (ke.getKeyChar() < '0' && ke.getKeyChar() >= ' ') {
				  	field.setEditable(false);
				  	//label.setText("");
			   } else if (ke.getKeyChar() >= ':' && ke.getKeyChar() <= '~') {
					field.setEditable(false);
			   } else {
				  	field.setEditable(true);
				  	//label.setText("* Enter only numeric digits(0-9)");
			   }

			   if (ke.getKeyChar() == KeyEvent.VK_TAB) {
				// Check database against this field
			   }
			}
		 }); */
		//field.setBounds(x,y,w,30);
	}

	public JTextField getTextBox() {
		return this.field;
	}

	public void update() {
	// Heres how to get text
	//	if (field.getText() != "") {
	//		System.out.println(field.getText());
	//	}
		if (field.getText().length() > 0) {
			char lastChar = field.getText().charAt(field.getText().length() - 1);
			//System.out.println(lastChar <= '/');
			if ( lastChar <= '/' || lastChar >= ':') {
				field.setText(field.getText().substring(0, field.getText().length() - 1));
				field.setCaretPosition(field.getText().length());
			}
		}
	}

	public void updateScreenSize(int screenW, int screenH) {
		//field.setLocation((int)((float)(originalX / 1000.0f) * screenW), originalY);
		//field.setSize(originalW, 30);
	}

}
