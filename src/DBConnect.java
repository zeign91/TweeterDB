import java.sql.*;
import java.util.*;

/**
*@author Danny Lui
*CUNY ID#: 23200057
*Project Final
*CSCI 370
*/

public class DBConnect 
{
   //DATABASE CONNECTION INFO
   static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
   static final String DB_URL = "jdbc:mysql://localhost/";
   
   //DATABASE USER/PASS
   static final String USER = "root";
   static final String PASS = "root";
   
   //DATABASE QUERY INFO
   private Connection con;
   private Statement st;
   private ResultSet rs;
   
   //INSTANCE VARIABLES
   private Vector column = null;
   private Vector data = null;
   
   private String tweID = null;
   private String tweName = null;
   private String tweHandle = null;
   private String tweTime = null;
   private String tweTweet = null;

   public DBConnect() 
   { 
      try {
         //REGISTER JDBC DRIVER
         Class.forName(JDBC_DRIVER);

         //OPEN A CONNECTION
         System.out.println("Connecting to mysql database...");
         con = DriverManager.getConnection(DB_URL, USER, PASS);
         
         //CREATE DATABASE TWEETER_DB
         System.out.println("Creating database tweeter_db...");
         st = con.createStatement();
         st.executeUpdate("CREATE DATABASE IF NOT EXISTS tweeter_db");
         
         //CONNECT TO TWEETER_DB
         System.out.println("Connecting to tweeter_db...");
         con = DriverManager.getConnection("jdbc:mysql://localhost/tweeter_db", USER, PASS);
         
         //CREATE TABLE TWEETER
         createTable();
         
         st = con.createStatement();
         
         System.out.println("Setup Successful!\n");
         
      } catch (Exception e) {  System.out.println("Error: " + e); }
   } //constructor
   
   /**
    * Create the table tweeter in database.
    */
   public void createTable() {
      try {
         System.out.println("Creating table tweeter...");
         st = con.createStatement();
         st.executeUpdate("CREATE TABLE IF NOT EXISTS tweeter (TWEETER_ID INT NOT NULL AUTO_INCREMENT, NAME VARCHAR(50) NOT NULL, HANDLE VARCHAR(50) NOT NULL, TIME_OF_TWEET VARCHAR(30) NOT NULL, TWEET VARCHAR(3000) NOT NULL, PRIMARY KEY (TWEETER_ID))");
      
      }catch (Exception e) { System.out.println(e); }
   }
   
   /**
    * Drop the table tweeter from database.
    */
   public void dropTable() {
      try {
         System.out.println("Dropping table tweeter...");
         st = con.createStatement();
         st.executeUpdate("DROP TABLE tweeter_db.tweeter");
         
      }catch (Exception e) { System.out.println("Error: " + e); }
   }
   
   /**
    * Obtain table information from database 
    * for use in JTable.
    */
   public void retrieveDBTableInfo() {
      try {
         String s = "SELECT * from tweeter";    
         rs = st.executeQuery(s);
         ResultSetMetaData rmst = rs.getMetaData();
         int c = rmst.getColumnCount();
         column = new Vector(c);
         for (int i = 1; i <= c; i++) {
            column.add(rmst.getColumnName(i));
         }
         
         data = new Vector();
         Vector row = new Vector();
         while (rs.next()) {
            row = new Vector(c);
            for (int i = 1; i <= c; i++) {
               row.add(rs.getString(i));
            }
            data.add(row);
         }
      } catch (Exception e) {  System.out.println("Error: " + e); }
   }
   
   
   /** 
    * This method takes the text file and
    * reads and writes the data to database
    */
   public void initializeTable(Scanner file) 
   {
      System.out.println("Populating Database...");
      
      file.useDelimiter("\\s\\s\\s"); // tokens in text file separated by 3 spaces
      
      while(file.hasNext()) {   // while file is not empty
         String id = file.next();
         //System.out.println("ID: " +id);
         
         String name = file.next();
         //System.out.println("NAME: "+name);
         
         String handle = file.next();
         //System.out.println("HANDLE: " +handle);
         
         String time = file.next();
         //System.out.println("TIME: " +time);
         
         String tweet = file.next();
         //System.out.println("TWEET: "+tweet);

         file.nextLine();
         if (id.equals("null")) // initial reads from url returns null IDs, so send them to auto increment primary key ID code
            addData(-1, name, handle, time, tweet);
         else // subsequent reads with numeric IDs
            addData(Integer.parseInt(id), name, handle, time, tweet);
      }
      System.out.println("Database Populated!\n");
   }
   
   /** 
    * This method retrieves data from the database using select all query
    * at given ID input and initializes instance variables.
    */
   public void getData(int input) {
      try {
         String query = "SELECT * "
                      + "FROM tweeter_db.tweeter "
                      + "WHERE tweeter_id =" +input;   // select all from input ID
         rs = st.executeQuery(query);

         while (rs.next()) { // initialize instance variables
            tweID = rs.getString("TWEETER_ID");
            tweName = rs.getString("NAME");
            tweHandle = rs.getString("HANDLE");
            tweTime = rs.getString("TIME_OF_TWEET");
            tweTweet = rs.getString("TWEET");
         }
      } catch (Exception e) {  System.out.println(e); }
   }
   
   /**
    * Retrieve data from database using select all query 
    * and enter it into inputted text file. This is to keep
    * persistent data in text file when doing changes such as 
    * add, update, and delete. Files can be loaded from last changes.
    * 
    * @param input
    */
   public void getDataForTXT(String input) {
      try {
         String query = "SELECT * "
                      + "FROM tweeter_db.tweeter ";  
         rs = st.executeQuery(query);

         while (rs.next()) {
            tweID = rs.getString("TWEETER_ID");
            tweName = rs.getString("NAME");
            tweHandle = rs.getString("HANDLE");
            tweTime = rs.getString("TIME_OF_TWEET");
            tweTweet = rs.getString("TWEET");
            
            //WRITE TO TEXT FILE GIVEN FROM INPUT
            FileOverwriter.enterNewTXTFile(input, tweID, tweName, tweHandle, tweTime, tweTweet);
         }
      
      } catch (Exception e) {  System.out.println(e); }
   }
   
   /** 
    * Add row to database using insert into query.
    */ 
   public void addData(int id, String name, String handle, String time, String tweet) 
   {
      try {
         String query;
         if(id != -1)   // adding data with known primary keys or IDs
            query = "INSERT INTO tweeter_db.tweeter (TWEETER_ID, NAME, HANDLE, TIME_OF_TWEET, TWEET) "
                  + "VALUES ("+id+", '"+name+"', '"+handle+"', '"+time+"', '"+tweet+"')";
         else   // using mysql's auto increment without primary key
            query = "INSERT INTO tweeter_db.tweeter (TWEETER_ID, NAME, HANDLE, TIME_OF_TWEET, TWEET) "
                  + "VALUES (null, '"+name+"', '"+handle+"', '"+time+"', '"+tweet+"')";
         
         st.executeUpdate(query); 
         //System.out.println("inserted row");
      } catch (Exception e) {  System.out.println(e); }
   }
   
   /** 
    * Remove row from database at given ID using delete query.
    */ 
   public void removeData(int id) 
   {
      try {
         String query = "DELETE FROM tweeter_db.tweeter "
                      + "WHERE tweeter_id="+id;    //delete from primary key
         
         st.executeUpdate(query); 
      
      } catch (Exception e) {  System.out.println(e); }
   } 
   
   /**
    * Update/Change a row from database at given ID using update query.
    * 
    * @param input
    * @param newID
    * @param newName
    * @param newHandle
    * @param newTime
    * @param newTweet
    */
   public void updateData(int input, int newID, String newName, String newHandle, String newTime, String newTweet) 
   {
      try {
         String query = "UPDATE tweeter_db.tweeter "
                         + "SET TWEETER_ID = " +newID+ ", "
                         + "NAME = '" +newName+ "', "
                         + "HANDLE = '" +newHandle+ "', "
                         + "TIME_OF_TWEET = '" +newTime+ "', "
                         + "TWEET = '" +newTweet+ "' "
                         + "WHERE TWEETER_ID = " +input; 
         
         st.executeUpdate(query); 
      
      } catch (Exception e) {  System.out.println(e); }
   } 
   
   //ACCESSOR METHODS
   public String getID() { return tweID; }
   public String getName() { return tweName; }
   public String getHandle() { return tweHandle; }
   public String getTime() { return tweTime; }
   public String getTweet() { return tweTweet; }
   public Vector getVectColumn() { return column; }
   public Vector getVectData() { return data; }
   
   //MUTATOR METHOD
   public void setAllNull() {
      tweID = null;
      tweName = null;
      tweHandle = null;
      tweTime = null;
      tweTweet = null;
   } 
}
