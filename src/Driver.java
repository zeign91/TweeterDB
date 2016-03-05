import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.net.URL;
import java.util.Scanner;

import javax.swing.*;

/**
*@author Danny Lui
*CUNY ID#: 23200057
*Project Final
*CSCI 370
*/

public class Driver 
{
   //FILE BUTTONS
   public static boolean newButton = false;
   public static boolean loadButton = false;
   
   //FILE EXIST?
   public static boolean validLoad = false;
   
   //EDIT BUTTONS
   public static boolean addButton = false;
   public static boolean removeButton = false;
   public static boolean updateButton = false;
   
   //GRAYING OUT EDIT/VIEW BUTTONS
   public static boolean graySwitch = false;
   
   //VIEW BUTTONS
   public static boolean searchByIDButton = false;

   //FILE READERS
   public static Scanner fileScanner = null;
   public static BufferedReader reader = null;
  
   //DATABASE CONNECTION AND GUI
   public static DBConnect connect;
   public static DBGUI gui;
   
   //URL PROCESSING
   public static URL url = null;
   public static URLProcess urlP = null;
   public static boolean validURL = false;
   
   //PROGRAM LOOPS
   public static boolean runningProgram = true;
   public static boolean inSession = false;
   
   //USER INPUT
   private static String input;
   
   public static void main (String[] args) {
      connect = new DBConnect(); // connect to database
      runProgram(); // run program
   } // main
   
   /**
    * Program loop, exit when window is closed
    * or when user clicks quit.
    */
   public static void runProgram()
   {
      gui = new DBGUI(); // initialize gui
      
      //LOOP PROGRAM
      while (runningProgram) 
      {
         //NEW SESSION START WHEN USER ENTERS NEW URL OR LOADS FROM EXISTING DATA
         if (!inSession) 
         {
            if (newButton) { // clicked new
               
               while (true) { // handle user input
                  input = JOptionPane.showInputDialog(null,"Please enter URL: ");
                  
                  if (input == null) { // exit or cancel
                     
                     if (!graySwitch) { // default false if no valid url inputted, hide edit/view
                        
                        gui.getEditMenu().setEnabled(false);
                        gui.getViewMenu().setEnabled(false);
                     }
                     break;
                  }
                  else { // verify correct input
                     graySwitch = true; // keep edit/view visible
                     
                     try { // valid url?
                        url = new URL(input);
                        validURL = true;
                     } catch (Exception e) { JOptionPane.showMessageDialog(gui, "Invalid URL, Try Again"); }
                     
                     if (validURL) { // process valid url
                        System.out.println("\nNew Session\n");
                        
                        urlP = new URLProcess(input);
                        loadFile("webpage "+URLProcess.getHTMLCounter()+" input.txt");
               
                        // set edit/view visible
                        gui.getEditMenu().setEnabled(true);
                        gui.getViewMenu().setEnabled(true);
                        
                        validURL = false;
                        
                        break; // exit request user input loop
                     }
                  }
               } // new button while
                  
               newButton = false;
            }
            inSession = true;
         }
         
         //ENTER SESSION AFTER INPUTTED URL OR FROM LOADING
         while (inSession)
         {
            //BUTTON OPTIONS
            if (newButton) { newButtonOption(); }
            if (loadButton) { loadButtonOption(); }
            if (addButton) { addButtonOption(); }
            if (removeButton) { removeButtonOption(); }
            if (updateButton) { updateButtonOption(); }
            if (searchByIDButton) { searchByIDOption(); }
            
         }// while inSession
      }// while runningProgram   
   }// runProgram
   
   /**
    * Code for new button is covered mostly in the above
    * due to user being able to click new before being in session. 
    * This simple snippet simply exits current session loop when 
    * new is clicked.
    */
   public static void newButtonOption() {
      System.out.println("NEW CLICKED");

      inSession = false;
   }
  
   /**
    * Code for load button. Ask user for a file to load from
    * and if file exist process the file else request file input
    * until cancel or exit. Similar code to new button except no url
    * is processed.
    */
   public static void loadButtonOption() {
      System.out.println("LOAD CLICKED");
      
      while (true) { // handle user input
         input = JOptionPane.showInputDialog(null,"Enter file to load: ");
         
         if (input == null)  // cancel or exit
            break;
         
         else { // verify correct input
            File myFile = new File(input);
            if (myFile.exists()) // does input file exist? 
               validLoad = true;
             else
                JOptionPane.showMessageDialog(gui, "File Not Found, Try Again"); 
            
            if (validLoad) // file exists proceed to load
            {
               System.out.println("\nNew Session\n");
               
               loadFile(input); // load data from text into database
               URLProcess.HTMLCounter = Character.getNumericValue(input.charAt(8)); // file's are saved as "webpage # input.txt", # is obtained from HTMLCounter to keep persistent data
               graySwitch = true; // visible edit/view
               
               validLoad = false;
               break;
            }
         }
      } // exit request user input loop
      loadButton = false;
   }
   
   /**
    * Code for add button. Deceivingly short but most of the code
    * is handled in the showAddInput method. This code adds a new row
    * to the table. Removes all current content in the gui and presents 
    * a freshly added row with the past content to user. 
    */
   public static void addButtonOption()
   {
      System.out.println("ADD CLICKED");
      
      showAddInput(); // gui to handle multiple field inputs
      
      gui.getContentPane().removeAll(); // remove old content
      connect.retrieveDBTableInfo(); // retrieve database info after newly added row
      gui.populateDBTableToJTable(connect); // re-populate data from database to JTable
      
      addButton = false;
   }
   
   /**
    * Code for remove button. Removes a row from database. Ask user for 
    * the unique ID to remove. It can handle non-existing IDs and prompts 
    * user for an input again.
    */
   public static void removeButtonOption()
   {
      System.out.println("REMOVE CLICKED");
      
      while (true) { // handle user input
         input = JOptionPane.showInputDialog(null,"Enter Tweeter ID to remove: ");
         
         if (input == null) // cancel or exit
            break;
         
         else {
            try { // ID is number?, if number does it exist
               connect.getData(Integer.parseInt(input)); // get the row info requested by user from database
               
               if (connect.getID() == null) // get ID from database
                  JOptionPane.showMessageDialog(gui, "ID does not exist, Try again");
               
               else {
                  connect.removeData(Integer.parseInt(input)); // remove row from database
                  
                  System.out.println("Removed ID: " +connect.getID()+ " | " 
                                                    +connect.getName()+ " | " 
                                                    +connect.getHandle()+ " | "  
                                                    +connect.getTime()+ " | " 
                                                    +connect.getTweet()+ "\n");
                  
                  //EVERY ADD/UPDATE/REMOVE UPDATE TEXT FILES OF RESPECTED DATA TO KEEP PERSISTENCY
                  FileOverwriter.clearTXTFile("webpage " +URLProcess.getHTMLCounter()+ " input.txt"); // clear current text file 
                  connect.getDataForTXT("webpage " +URLProcess.getHTMLCounter()+ " input.txt"); // write new data to current text file
                  
                  //UPDATE GUI
                  gui.getContentPane().removeAll();
                  connect.retrieveDBTableInfo();
                  gui.populateDBTableToJTable(connect);
                  
                  connect.setAllNull(); // reset current instance variables from connect.getData
                  break;
               }
            }catch (Exception e) {JOptionPane.showMessageDialog(gui, "Ivalid ID, Try Again"); }
         }
      } // while end 
      removeButton = false;
   }
   
   /**
    * Code for update button. Similar to add button and remove button combined.
    * Ask user for ID to update. Present multiple fields to change. 
    */
   public static void updateButtonOption()
   {
      System.out.println("UPDATE CLICKED");
      
      while (true) { // handle user input
         input = JOptionPane.showInputDialog(null,"Enter Tweeter ID to update: ");
         
         if (input == null) // cancel or exit
            break;
         
         else { 
            try{ // ID is number?, if number does it exist
               connect.getData(Integer.parseInt(input));
               
               if (connect.getID() == null)
                  JOptionPane.showMessageDialog(gui, "ID does not exist, Try again");
               
               else {
                  showUpdateInput(input); // gui to handle multiple field changes
                  
                  //UPDATE GUI
                  gui.getContentPane().removeAll();
                  connect.retrieveDBTableInfo();
                  gui.populateDBTableToJTable(connect);
                  
                  connect.setAllNull(); // reset current instance variables from connect.getData
                  break;
               }
            }catch (Exception e) {JOptionPane.showMessageDialog(gui, "Invalid ID, Try Again");}
         }   
      }// while end 
      updateButton = false;
   }
   
   /**
    * Code for search by ID button. Ask user for ID, and show information about
    * tweeter on the right panel. 
    */
   public static void searchByIDOption()
   {
      System.out.println("SEARCH CLICKED");
      
      while (true) { // handle user input
         input = JOptionPane.showInputDialog(null,"Enter Tweeter ID to view: ");
         
         if (input == null) // cancel or exit
            break;
         
         else {
            try { // ID is number?, if number does it exist 
               connect.getData(Integer.parseInt(input));
               
               if (connect.getID() == null)
                  JOptionPane.showMessageDialog(gui, "ID does not exist, Try again");
               
               else {
                  //UPDATE GUI
                  gui.getContentPane().removeAll();
                  connect.retrieveDBTableInfo();
                  gui.populateDBTableToJTable(connect);
                  gui.appendTextArea(connect); // append information to JTextArea
                  
                  connect.setAllNull(); // reset current instance variables from connect.getData
                  break;
               }
            }catch (Exception e) {JOptionPane.showMessageDialog(gui, "Invalid ID"); }
         }
      } // while end
      searchByIDButton = false;
   }
   
   /**
    * Code used for add button. 
    * Provide gui for multiple fields to enter.
    */
   public static void showAddInput()
   {
      JPanel p = new JPanel(new BorderLayout(5,5));
      
      //LEFT PANEL
      JPanel labels = new JPanel(new GridLayout(0,1,2,2));
      labels.add(new JLabel("Tweeter ID", SwingConstants.RIGHT));
      labels.add(new JLabel("Name", SwingConstants.RIGHT));
      labels.add(new JLabel("Handle", SwingConstants.RIGHT));
      labels.add(new JLabel("Time", SwingConstants.RIGHT));
      labels.add(new JLabel("Tweet", SwingConstants.RIGHT));
      p.add(labels, BorderLayout.WEST);

      //FIELDS ON THE RIGHT FOR USER INPUT
      JPanel controls = new JPanel(new GridLayout(0,1,2,2));
      JTextField tweeter_id = new JTextField();
      controls.add(tweeter_id);
      JTextField name = new JTextField();
      controls.add(name);
      JTextField handle = new JTextField();
      controls.add(handle);
      JTextField time = new JTextField();
      controls.add(time);
      JTextField tweet = new JTextField();
      controls.add(tweet);
      p.add(controls, BorderLayout.CENTER);

      int result = JOptionPane.showConfirmDialog(
            gui, p, "Add Row", JOptionPane.OK_CANCEL_OPTION);
      
      if (result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION) { 
          // cancel or exit, do nothing 
      }
      else {
         try { // add row
             connect.addData(Integer.parseInt(tweeter_id.getText()),
                          name.getText(),
                          handle.getText(),
                          time.getText(),
                          tweet.getText());
          
             System.out.println("Added: " +tweeter_id.getText()+ " | " 
                   +name.getText()+ " | " 
                   +handle.getText()+ " | "  
                   +time.getText()+ " | " 
                   +tweet.getText()+ "\n");
             
             //EVERY ADD/UPDATE/REMOVE UPDATE TEXT FILES OF RESPECTED DATA TO KEEP PERSISTENCY
             FileOverwriter.clearTXTFile("webpage " +URLProcess.getHTMLCounter()+ " input.txt"); // clear current text file 
             connect.getDataForTXT("webpage " +URLProcess.getHTMLCounter()+ " input.txt"); // write new data to current text file
          
         } catch (Exception ex) { JOptionPane.showMessageDialog(gui, "Invalid ID"); }
      }
   } // showAddInput
   
   /**
    * Code used for update button. Update/Change a row 
    * at ID requested by user.
    * @param input
    */
   public static void showUpdateInput(String input)
   {
      connect.getData(Integer.parseInt(input)); // get the row info from ID requested by user from database
      
      JPanel p = new JPanel(new BorderLayout(5,5));

      //LEFT PANEL
      JPanel labels = new JPanel(new GridLayout(0,1,2,2));
      labels.add(new JLabel("Tweeter ID", SwingConstants.RIGHT));
      labels.add(new JLabel("Name", SwingConstants.RIGHT));
      labels.add(new JLabel("Handle", SwingConstants.RIGHT));
      labels.add(new JLabel("Time", SwingConstants.RIGHT));
      labels.add(new JLabel("Tweet", SwingConstants.RIGHT));
      p.add(labels, BorderLayout.WEST);

      //FIELDS ON THE RIGHT FOR USER INPUT WITH CURRENT DATA
      JPanel controls = new JPanel(new GridLayout(0,1,2,2));
      controls.setPreferredSize(new Dimension(300,120));
      JTextField tweeter_id = new JTextField(connect.getID());
      controls.add(tweeter_id);
      JTextField name = new JTextField(connect.getName());
      controls.add(name);
      JTextField handle = new JTextField(connect.getHandle());
      controls.add(handle);
      JTextField time = new JTextField(connect.getTime());
      controls.add(time);
      JTextField tweet = new JTextField(connect.getTweet());
      controls.add(tweet);
      p.add(controls, BorderLayout.CENTER);

      int result = JOptionPane.showConfirmDialog(
            gui, p, "Update Row", JOptionPane.OK_CANCEL_OPTION);
      
      if (result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION) {
          // cancel or exit, do nothing
      }
      else {
         try { // update row
            connect.updateData(Integer.parseInt(input),
                                Integer.parseInt(tweeter_id.getText()),
                                name.getText(),
                                handle.getText(),
                                time.getText(),
                                tweet.getText());
          
            System.out.println("Updated: " +tweeter_id.getText()+ " | " 
                  +name.getText()+ " | " 
                  +handle.getText()+ " | "  
                  +time.getText()+ " | " 
                  +tweet.getText()+ "\n");
            
            //EVERY ADD/UPDATE/REMOVE UPDATE TEXT FILES OF RESPECTED DATA TO KEEP PERSISTENCY
            FileOverwriter.clearTXTFile("webpage " +URLProcess.getHTMLCounter()+ " input.txt"); // clear current text file 
            connect.getDataForTXT("webpage " +URLProcess.getHTMLCounter()+ " input.txt"); // write new data to current text file
        
         } catch (Exception ex) {System.out.println(ex);}
      }
   }

   /**
    * Code for loadFile, used in new button and load button.
    * Every url read, the program does not read data into the database
    * immediately, instead it reads the data to a text file and saves it first.
    * Then it reads from the text file and loads from it into the database.
    * It saves a backup to local disk first. 
    * 
    * The load file button is straightforward in that it reads from a text file.
    * 
    * This code reads a text file and loads into a database.
    * 
    * @param input
    */
   public static void loadFile(String input)
   {
      try { 
         //SCAN TEXT FILE
         File inputFile = new File(input);
         fileScanner = new Scanner(inputFile);
         
         //DROP AND CREATE DATABASE TABLE OF REQUESTED TEXT FILE
         connect.dropTable();
         connect.createTable();
         connect.initializeTable(fileScanner);
         
         //SHOW GUI OF PRESENT TABLE
         gui.getContentPane().removeAll();
         connect.retrieveDBTableInfo();
         gui.populateDBTableToJTable(connect);
         
         //MAKE VISIBLE EDIT/VIEW
         gui.getEditMenu().setEnabled(true);
         gui.getViewMenu().setEnabled(true);
      } catch (Exception e) { System.out.println(e); }
   }
}


