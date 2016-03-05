import javax.swing.*;
import java.awt.event.*;

/**
*@author Danny Lui
*CUNY ID#: 23200057
*Project Final
*CSCI 370
*/

public class FileMenuHandler implements ActionListener 
{
   JFrame jframe;
  
   public FileMenuHandler (JFrame jf) 
   {
      jframe = jf;
   }
   
   public void actionPerformed(ActionEvent event) 
   {
      String menuName = event.getActionCommand();
      
     if (menuName.equals("New"))
         //SET NEW TO TRUE IF USER CLICKS NEW
         Driver.newButton = true;
     if (menuName.equals("Load"))
        //SET LOAD TO TRUE IF USER CLICKS LOAD
        Driver.loadButton = true;
     if (menuName.equals("Quit")) 
         //EXIT PROGRAM
         System.exit(0);
      
   } //actionPerformed
   
}