import javax.swing.*;
import java.awt.event.*;

/**
*@author Danny Lui
*CUNY ID#: 23200057
*Project Final
*CSCI 370
*/

public class EditMenuHandler implements ActionListener 
{
   JFrame jframe;
  
   public EditMenuHandler (JFrame jf) 
   {
      jframe = jf;
   }
   
   public void actionPerformed(ActionEvent event) 
   {
      String menuName = event.getActionCommand();
      
     if (menuName.equals("Add"))
         //SET ADD TO TRUE IF USER CLICKS ADD
         Driver.addButton = true;
     if (menuName.equals("Remove"))
        //SET REMOVE TO TRUE IF USER CLICKS REMOVE
        Driver.removeButton = true;
     if (menuName.equals("Update"))
        //SET UPDATE TO TRUE IF USER CLICKS UPDATE
        Driver.updateButton = true;
     
   } //actionPerformed
   
}