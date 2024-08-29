
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import java.lang.Math;

public class Model
{

    public enum System_State {  /* Main system state of the program */
        INITIALIZE,
        SPLASH_SCREEN,
        TEAM_INPUT_SCREEN,
        // ...
        MAX_SYS_STATES
    }
    private System_State system_State;

    public ArrayList<Sprite> windowObjects; // This arrayList contains all elements that
                                            //  will be drawn to the scrren

    /*-------------------------------------------------
     *
     *      Model()
     *
     *  DESCRIPTION: Model Class Initializer
     *
     *  REQUIREMENTS:
     *
    ------------------------------------------------- */
    public Model()
    {
        system_State = System_State.INITIALIZE;
        // Initialization
        windowObjects = new ArrayList<Sprite>();

        startSplashScreen();
        system_State = System_State.SPLASH_SCREEN;
    }

    /*-------------------------------------------------
     *
     *      update()
     *
     *  DESCRIPTION: Updates all data for the program,
     *  outside of graphical elements. May be necessary
     *  to have a parameter passed into this function
     *  from where it is called in Controller
     *
     *  REQUIREMENTS:
     *
    ------------------------------------------------- */
    public void update()
    {
        for (int i = 0; i < getNumWindowObjects(); i++) {
            windowObjects.get(i).update();
        }
    }

    public int getNumWindowObjects() {
		return windowObjects.size();
	}

    public Sprite getWindowObjectAt(int i) {
		return windowObjects.get(i);
	}

    public void startSplashScreen() {
        windowObjects.add(new SplashScreen(0, 0, 900, 450));
    }

    public void updateScreenSize(int screenW, int screenH) {
        for (int i = 0; i < getNumWindowObjects(); i++) {
            windowObjects.get(i).updateScreenSize(screenW, screenH);
        }
    }
}