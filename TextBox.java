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
		//field.setBounds(x,y,w,30);
	}

	public JTextField getTextBox() {
		return this.field;
	}

	public void update() {

	}

	public void updateScreenSize(int screenW, int screenH) {
		//field.setLocation((int)((float)(originalX / 1000.0f) * screenW), originalY);
		//field.setSize(originalW, 30);
	}

}
