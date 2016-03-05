import javax.swing.*;
import java.awt.*;

/**
*@author Danny Lui
*CUNY ID#: 23200057
*Project Final
*CSCI 370
*/

public class DBGUI extends JFrame
{
   private JMenu editMenu;
   private JMenu viewMenu;
   private JTable table;
   
   JTextArea myTextArea;
   
   public DBGUI() {
      createAndShowGUI();
   } // constructor
 
   /**
    * Setup JFrame with dimensions 
    * and a JMenubar.
    */
   public void createAndShowGUI() {
      //CREATE AND SET UP WINDOW
      setTitle("Tweeter Table");
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setLocation(200,200);                                 // (x, y)
      setSize(300,200);                                     // (width, height)
  
      //CREATE MENUBAR
      JMenuBar menuBar = new JMenuBar();  
      
      //MENUBAR ITEMS
      createFileMenu(menuBar);
      createEditMenu(menuBar);
      createViewMenu(menuBar);

      //DISPLAY WINDOW
      //pack(); 
      setVisible(true);   
   }
   
   /**
    * Retrieve information from database and populate data to JTable. 
    * Add JTable to JScrollPane then to JPanel and finally JPanel to JFrame. 
    * Also add JTextArea to JPanel for displaying database search information.
    * @param dbc
    */
   public void populateDBTableToJTable(DBConnect dbc)
   {
      //CREATE PANEL
      JPanel twoPanels = new JPanel();
      twoPanels.setLayout(new GridLayout(1,2));
      add(twoPanels, BorderLayout.SOUTH);
      
      //GET DATABASE TABLE INFORMATION TO ADD TO JTABLE, PUT JTABLE IN JSCROLLPANE THEN ADD TO PANEL
      table = new JTable(dbc.getVectData(), dbc.getVectColumn());
      JScrollPane scrollPane = new JScrollPane(table);
      twoPanels.add(scrollPane);
      
      //ADD JTEXTAREA TO ANOTHER JSCROLLPANE THEN ADD TO PANEL
      myTextArea = new JTextArea();
      myTextArea.setLineWrap(true);
      myTextArea.setFont(new Font("Monospaced", Font.PLAIN,14));
      myTextArea.setEditable(false);
      JScrollPane scrollPane2 = new JScrollPane(myTextArea);
      twoPanels.add(scrollPane2);
      
      //TEXT HEADER
      myTextArea.append("DB SEARCH INFORMATION:");
      
      pack();
   }
   
   /**
    * Retrieve database information for one row
    * and output data to JTextArea.
    * @param dbc
    */
   public void appendTextArea(DBConnect dbc) {
      myTextArea.append("\n\n");
      myTextArea.append("Tweeter: " + dbc.getName() + dbc.getHandle());
      myTextArea.append("\nAt " + dbc.getTime());
      myTextArea.append("\n\nTweeted:\n" + dbc.getTweet());
   }
   
   /**
    * JMenu for file
    * @param menuBar
    */
   private void createFileMenu(JMenuBar menuBar)  {
      JMenuItem item;
    
      JMenu fileMenu = new JMenu("File");
      FileMenuHandler fmh  = new FileMenuHandler(this);

      item = new JMenuItem("New"); // New
      item.addActionListener( fmh );
      fileMenu.add( item );
      
      item = new JMenuItem("Load"); // Load
      item.addActionListener( fmh );
      fileMenu.add( item );

      fileMenu.addSeparator(); // add a horizontal separator line
    
      item = new JMenuItem("Quit"); // Quit
      item.addActionListener( fmh );
      fileMenu.add( item );

      setJMenuBar(menuBar);
      menuBar.add(fileMenu);
   }
   
   /**
    * JMenu for edit
    * @param menuBar
    */
   private void createEditMenu(JMenuBar menuBar)  {
      JMenuItem item;
      
      editMenu = new JMenu("Edit");
      editMenu.setEnabled(false);
      EditMenuHandler emh  = new EditMenuHandler(this);

      item = new JMenuItem("Add"); // Add
      item.addActionListener( emh );
      editMenu.add( item );

      item = new JMenuItem("Remove"); // Remove
      item.addActionListener( emh );
      editMenu.add( item );
      
      item = new JMenuItem("Update"); // Update
      item.addActionListener( emh );
      editMenu.add( item );
      
      setJMenuBar(menuBar);
      menuBar.add(editMenu);
   }

   /**
    * JMenu for view
    * @param menuBar
    */
   private void createViewMenu(JMenuBar menuBar)  {
      JMenuItem item;
      
      viewMenu = new JMenu("View");
      viewMenu.setEnabled(false);
      ViewMenuHandler vmh  = new ViewMenuHandler(this);

      item = new JMenuItem("Search by ID"); // Search
      item.addActionListener( vmh );
      viewMenu.add( item );
      
      setJMenuBar(menuBar);
      menuBar.add(viewMenu);
   }
   
   //ACCESSOR METHODS
   public JMenu getEditMenu() { return editMenu; }
   public JMenu getViewMenu() { return viewMenu; }
}
