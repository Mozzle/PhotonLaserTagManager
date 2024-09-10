import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public class Database {

    public static final int PARAM_ID = 0;
    public static final int PARAM_CODENAME = 1;
    public static final int PARAM_ID_AND_CODENAME = 2;

    String jdbcUrl;
    Connection connection;

    public Database() {
        jdbcUrl = "jdbc:postgresql://localhost:5432/photon";

        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(jdbcUrl, "student", "student");
            System.out.println("Connected to DB");
        }
        catch (Exception e) {
         e.printStackTrace();
         System.err.println(e.getClass().getName()+": "+e.getMessage());
        }
        
        /*
        SAMPLE QUERY: Get entire database
         
        try {
            Statement statement = connection.createStatement();
            
            ResultSet resultSet = statement.executeQuery("SELECT * FROM players");
            
            while (resultSet.next())
            {
                String columnValue = resultSet.getString("id");
                String codename = resultSet.getString("codename");
                System.out.println("ID: " + columnValue);
                System.out.println("codename: " + codename);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
        }
        */

    }

    /*-----------------------------------------------------
     * 
     *      searchDB()
     * 
     *  REQUIREMENTS:
     * 
     *  DESCRIPTION: Searches the photon database for either
     *  a given ID, a given codename, or both, depending on
     *  the Database search parameter that is given.
     *  searchParam should reflect the parameter that is
     *  given, NOT the parameter you wish searchDB to return.
     *  EXAMPLE: searchDB(Database.PARAM_ID, 3, "")
     *  Searches for ID # 3 in the ID column of the table,
     *  and returns the corresponding codename
     -----------------------------------------------------*/
    public String searchDB(int searchParam, int id, String codename) {
        String returnVal = "";

        if (searchParam == PARAM_ID) {
            try {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT codename FROM players WHERE players.id = " + id + ";");
                // Set returnVal to the returned codename
                if (resultSet.next()) {
                    returnVal = resultSet.getString("codename");
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                System.err.println(e.getClass().getName()+": "+e.getMessage());
            }
        }
        else if (searchParam == PARAM_CODENAME) {
            try {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT id FROM players WHERE players.codename = '" + codename + "';");
                // Set returnVal to the returned codename
                if (resultSet.next()) {
                    returnVal = resultSet.getString("id");
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                System.err.println(e.getClass().getName()+": "+e.getMessage());
            }
        }
        else if (searchParam == PARAM_ID_AND_CODENAME) {
            // Why are you calling this method then?
        }
        return returnVal;
    }

    /*-----------------------------------------------------
     * 
     *      insertDB()
     * 
     *  REQUIREMENTS:
     * 
     *  DESCRIPTION: Inserts a new entry in the table given
     *  an ID and/or codename. 
     *  EXAMPLE:
     * insertDB(Database.PARAM_ID_AND_CODENAME, 7, "Sparky")
     -----------------------------------------------------*/
    public boolean insertDB(int param, int id, String codename) {
        boolean returnVal = true;

        if (param == PARAM_ID_AND_CODENAME) {
            try {
                Statement statement = connection.createStatement();
                //ResultSet resultSet = statement.executeQuery("INSERT INTO players(id, codename) VALUES (" + id + ", '" + codename +"');");
                statement.executeUpdate("INSERT INTO players(id, codename) VALUES (" + id + ", '" + codename +"');");
            }
            catch (Exception e) {
                e.printStackTrace();
                System.err.println(e.getClass().getName()+": "+e.getMessage());
                returnVal = false;
            }
        }
        return returnVal;
    }

    /*-----------------------------------------------------
     * 
     *      deleteDBRow()
     * 
     *  REQUIREMENTS:
     * 
     *  DESCRIPTION: Deletes a row in players table, given
     *  either the ID or the codename of the row.
     *  EXAMPLE:
     *  deleteDBRow(Database.PARAM_ID, 7, "");
     -----------------------------------------------------*/
    public boolean deleteDBRow(int param, int id, String codename) {
        boolean returnVal = true;

        if (param == PARAM_ID) {
            try {
                Statement statement = connection.createStatement();
                //ResultSet resultSet = statement.executeQuery("DELETE FROM players WHERE players.id = " + id + ";");
                statement.executeUpdate("DELETE FROM players WHERE players.id = " + id + ";");
            }
            catch (Exception e) {
                e.printStackTrace();
                System.err.println(e.getClass().getName()+": "+e.getMessage());
                returnVal = false;
            }
        }
        else if (param == PARAM_CODENAME) {
            try {
                Statement statement = connection.createStatement();
                //ResultSet resultSet = statement.executeQuery("DELETE FROM players WHERE players.codename = '" + codename + "';");
                statement.executeUpdate("DELETE FROM players WHERE players.codename = '" + codename + "';");
            }
            catch (Exception e) {
                e.printStackTrace();
                System.err.println(e.getClass().getName()+": "+e.getMessage());
                returnVal = false;
            }
        }
        return returnVal;
    }

    /*-----------------------------------------------------
     * 
     *      getNumRows()
     * 
     *  REQUIREMENTS:
     * 
     *  DESCRIPTION: Returns number of rows in players table
     * 
     -----------------------------------------------------*/
    public int getNumRows() {
        int returnVal = -1;

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT count(*) FROM players;");
            if (resultSet.next()) {
                returnVal = resultSet.getInt("count");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
                System.err.println(e.getClass().getName()+": "+e.getMessage());
                returnVal = -1;
        }
        return returnVal;
    }
}


