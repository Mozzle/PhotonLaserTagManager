
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import java.lang.Math;
import java.util.Timer;
import java.util.TimerTask;

public class Model
{
    //enums in Java are utterly useless, so here is my C-like enum:
    public static final int INITIALIZE = 0;
    public static final int SPLASH_SCREEN = 1;
    public static final int PLAYER_ENTRY_SCREEN = 2;
    //...
    public static final int NUM_SCREENS = 3;

    public int system_State;

    public ArrayList<Sprite> windowObjects; // This arrayList contains all elements that
                                            //  will be drawn to the scrren

    public Timer timer;
    public TimerTask splashScreenTimeoutTask;

    public class SplashScreenTimeout extends TimerTask
    {
        public void run()
        {
            windowObjects.remove(0);
            system_State = PLAYER_ENTRY_SCREEN;
        }
    }

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
        system_State = INITIALIZE;
        // Initialization
        windowObjects = new ArrayList<Sprite>();

        timer = new Timer();
        splashScreenTimeoutTask = new SplashScreenTimeout();
        startSplashScreen();
        system_State = SPLASH_SCREEN;
        timer.schedule(splashScreenTimeoutTask, 3200);
        
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

        switch(system_State) {
            case SPLASH_SCREEN:

                break;
            case PLAYER_ENTRY_SCREEN:
                break;
            default:
                break;
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