import javax.swing.*;
import java.awt.event.*;

/**
*@author Danny Lui
*CUNY ID#: 23200057
*Project Final
*CSCI 370
*/

public class ViewMenuHandler implements ActionListener 
{
   JFrame jframe;
  
   public ViewMenuHandler (JFrame jf) 
   {
      jframe = jf;
   }
   
   public void actionPerformed(ActionEvent event) 
   {
      String menuName = event.getActionCommand();
      
      if (menuName.equals("Search by ID"))
         //SET SEARCHBYIDBUTTON TO TRUE IF USER CLICKS SEARCH BY ID
         Driver.searchByIDButton = true;
     
   } //actionPerformed
   
}