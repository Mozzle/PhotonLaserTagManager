import java.awt.image.BufferedImage;

/*---------------------------------------------
 * 
 *      class Sprite
 * 
 *  DESCRIPTION: Parent class for all objects
 *  that will be drawn to the screen.
 * 
 * 
 --------------------------------------------*/
public class Sprite {
    protected int x, y, w, h, screenW, screenH, animationState;
    protected boolean isDynamic;
    protected String label;
    static BufferedImage image;

    public Sprite(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Sprite(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    public int getX() {
        return this.x;
    }
    public int getY() {
        return this.y;
    }
    public int getW() {
        return this.w;
    }
    public int getH() {
        return this.h;
    }

    public String getLabel() {
        return label;
    }

    public void setX(int x) {
        this.x = x;
    }
    public void setY(int y) {
        this.y = y;
    }
    public void setW(int w) {
        this.w = w;
    }
    public void setH(int h) {
        this.h = h;
    }

    public void update() {

    }

    public void draw() { //There's no reason for this method to exist.
    //Proper heirarchical OOP would dictate that view should be getting
    //The correct data from the ArrayList of sprites contained in Model, 
    //then drawing it in the paintComponent() method in View. So that is 
    //exactly what I did.
    }

    public BufferedImage getImage() { 
        return image;
    }

    public void setAnimationState(int s) {
        animationState = s;
    }

    public int getAnimationState() {
        return animationState;
    }

    public boolean getDynamism() {
        return isDynamic;
    }

    public void kill() {

    }

    public void updateScreenSize(int screenW, int screenH) {
        this.screenW = screenW;
        this.screenH = screenH;
    }


}
