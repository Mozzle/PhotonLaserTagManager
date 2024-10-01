/// Developed by Ben Kensington

    /*-----------------------------------------------------------
     * 
     *  README
     * 
     *  The main methods you will use in this class are:
     * 
     *  verify() -- Used to shift the players flag to verified
     * 
     *  revoke() -- Used to shift the players flag to non-verified
     * 
     *  getStatus() -- Returns the players current flag
     * 
     *  setEquipID() -- Set the players equipment ID
     * 
     *  getEquipID() -- Get the players equipment ID
     * 
    ---------------------------------------------------------- */

public class Player
{
    /// Define public vars (why not include a name)
    public String name;

    /// Define private flags and vars
    private boolean verified;
    private int equipmentID;

    /*-----------------------------------------------------------
     * 
     *  Player()
     * 
     *  DESCRIPTION: Player constructor
     * 
    ---------------------------------------------------------- */
    public Player() {
        name = "NULL";
        verified = false;
        equipmentID = -1;
    }

    /*-----------------------------------------------------------
     * 
     *  verify() and revoke()
     * 
     *  DESCRIPTION: Two methods that shift the player flag to T/F
     * 
    ---------------------------------------------------------- */
    public void verify() {
        verified = true;
    }

    public void revoke() {
        verified = false;
    }

    /*-----------------------------------------------------------
     * 
     *  getStatus()
     * 
     *  DESCRIPTION: Returns the players flag as a boolean
     * 
    ---------------------------------------------------------- */
    public boolean getStatus() {
        return verified;
    }

    /*-----------------------------------------------------------
     * 
     *  setEquipID(int ID) and getEquipID()
     * 
     *  DESCRIPTION: Getter and setter for equipment ID
     * 
    ---------------------------------------------------------- */
    public boolean setEquipID(int ID) {
        if (ID >= 0)
            equipmentID = ID;        

        return equipmentID == ID;
    }

    public int getEquipID() {
        return equipmentID;
    }
}