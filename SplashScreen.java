
    /*-------------------------------------------------
     *
     *  SplashScreen
     *
     *  DESCRIPTION: Establishes the variable "animationState"
     *  and loads the initial Photon logo to be displayed at the beginning of the program
     * 
     *  REQUIREMENTS: 0023,
     *
    ------------------------------------------------- */
public class SplashScreen extends Sprite {
    public int animationState;

    public SplashScreen(int x, int y, int w, int h) {
        super(x, y, w, h);

        image = View.loadImage("src/logo.jpg");
    }

    public void update() {
        
    }

    /*-------------------------------------------------
     *
     *  updateScreenSize
     *
     *  DESCRIPTION: Updates the screen's current size and width,
     * updates the current x and y values
     *
    ------------------------------------------------- */
    public void updateScreenSize(int screenW, int screenH) {
        this.screenW = screenW;
        this.screenH = screenH;
        this.x = (int)((this.screenW / 2) - (this.w / 2));
        this.y = (int)((this.screenH / 2) - (this.h / 2));
    }
}
