/// Developed by Ben Kensington
import javax.swing.JTextField;

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
     *  setReferences(JTextField IDRef, JTextField EquipIDRef, JTextField NameRef)
     *  -- Sets the references that holds the players information
     * 
     *  getReferences() -- Returns an array of JTextField components,
     *  ref[0] -- reference text box containing ID
     *  ref[1] -- reference text box containing equipment ID
     *  ref[2] -- reference text box containing name
     * 
     *  setEquipID() -- Set the players equipment ID
     * 
     *  getEquipID() -- Get the players equipment ID
     * 
     *  setNormalID() -- Set the players normal ID
     * 
     *  getNormalID() -- Get the players normal ID
     * 
    ---------------------------------------------------------- */

public class Player
{
    /// Define public vars (why not include a name)
    public String name;

    /// Define private flags and vars
    private boolean verified;
    private int equipmentID;
    private int normalID;

    /// Reference flags for player entry screen
    private JTextField refID;
    private JTextField refEquipID;
    private JTextField refName;

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
        normalID = -1;
    }

    /*-----------------------------------------------------------
     * 
     *  createPlayer(String NAME, int equipID, int normID)
     * 
     *  DESCRIPTION: Static method used to create a player object
     * 
    ---------------------------------------------------------- */
    public static Player createPlayer(String NAME, int equipID, int normID) {
        Player p = new Player();

        p.name = NAME;
        p.equipmentID = equipID;
        p.normalID = normID;

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

    /*-----------------------------------------------------------
     * 
     *  setNormalID(int ID) and getNormalID()
     * 
     *  DESCRIPTION: Getter and setter for normal ID
     * 
    ---------------------------------------------------------- */
    public boolean setNormalID(int ID) {
        if (ID >= 0)
            normalID = ID;        

        return normalID == ID;
    }

    public int getNormalID() {
        return normalID;
    }

    /*-----------------------------------------------------------
     * 
     *  setReferences(JTextField IDRef, JTextField EquipIDRef, JTextField NameRef)
     * 
     *  DESCRIPTION: Sets references for textboxes containing player info
     * 
    ---------------------------------------------------------- */
    public boolean setReferences(JTextField IDRef, JTextField EquipIDRef, JTextField NameRef) {
        refID = IDRef;
        refEquipID = EquipIDRef;
        refName = NameRef;

        return (refID == IDRef && refEquipID == EquipIDRef && refName == NameRef);
    }

    /*-----------------------------------------------------------
     * 
     *  getReferences()
     * 
     *  DESCRIPTION: Returns an array of size 3 containing all JTextField references
     * 
    ---------------------------------------------------------- */
    public JTextField[] getReferences() {
        JTextField[] ref = new JTextField[3];

        ref[0] = refID;
        ref[1] = refEquipID;
        ref[2] = refName;

        return ref;
    }

    /*-----------------------------------------------------------
     * 
     *  update())
     * 
     *  DESCRIPTION: Update method for player object, syncs references
     *  to currently held values
     * 
    ---------------------------------------------------------- */
    public void update() {

        // If our references are null, exit early
        if (refID == null || refEquipID == null)
            return;
        if (refEquipID.getText().equals("") || refID.getText().equals(""))
            return;

        // Apply changes
        normalID = Integer.valueOf(refID.getText());
        equipmentID = Integer.valueOf(refEquipID.getText());
        if (refName != null)
            name = refName.getText();
    }

    /*-----------------------------------------------------------
     * 
     *  equals(Player p)
     * 
     *  DESCRIPTION: Override equal method to compare two player objects
     * 
    ---------------------------------------------------------- */
    public boolean equals(Player p) {
        if (p == null)
            return false;
        return (p.name.equals(name) && p.equipmentID == equipmentID && p.normalID == normalID);
    }
}