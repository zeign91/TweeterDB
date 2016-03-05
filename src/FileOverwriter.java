import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
*@author Danny Lui
*CUNY ID#: 23200057
*Project Final
*CSCI 370
*/

public class FileOverwriter 
{
   public static BufferedWriter writer = null;
   
   public FileOverwriter() {
      // empty
   }// constructor
   
   /**
    * Erase all content from given text file.
    * 
    * @param input
    */
   public static void clearTXTFile(String input)
   {
      try {
         PrintWriter writer = new PrintWriter(input);
         writer.print("");
         writer.close();
      } catch (Exception e) { System.out.println("Error: No Output File"); }
   }
   
   /**
    * Write new data to given text file.
    * 
    * @param input
    * @param id
    * @param newName
    * @param newHandle
    * @param newTime
    * @param newTweet
    */
   public static void enterNewTXTFile(String input, String id, String newName, String newHandle, String newTime, String newTweet)
   {
      try {
         String str = Integer.parseInt(id)+ "   " +newName+ "   " +newHandle+ "   " +newTime+ "   " +newTweet+ "   ";
         writer = new BufferedWriter(new FileWriter(input, true)); // continue to write to existing output file
         writer.write(str);
         writer.newLine();
         writer.close();
         
      } catch (Exception e) { System.out.println(e); }
   }
}
