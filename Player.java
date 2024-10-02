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
     *  createPlayer(String name, int equipID) -- Creates
     *  and returns a player object
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
     *  createPlayer(String NAME, int equipID)
     * 
     *  DESCRIPTION: Static method used to create a player object
     * 
    ---------------------------------------------------------- */
    public static Player createPlayer(String NAME, int equipID) {
        Player p = new Player();

        p.name = NAME;
        p.equipmentID = equipID;

        return p;
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
        System.out.println("[Player] Verified " + name + ".");
    }

    public void revoke() {
        verified = false;
        System.out.println("[Player] Revoked " + name + ".");
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