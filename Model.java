
import java.util.ArrayList;

import java.awt.image.BufferedImage;
import java.lang.Math;

public class Model
{

    public enum System_State {  /* Main system state of the program */
        INITIALIZE,
        WELCOME_SCREEN,
        TEAM_INPUT_SCREEN,
    
        MAX_SYS_STATES
    }
    private System_State system_State;

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

    }
}