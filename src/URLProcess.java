import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.regex.*;

/**
*@author Danny Lui
*CUNY ID#: 23200057
*Project Final
*CSCI 370
*/

public class URLProcess 
{  
   //REGEX PATTERNS
   private static final String regexForName =
         "(?<=<strong class=\"fullname js-action-profile-name show-popup-with-id)(?:.*>)(.*)(?=<\\/strong>)";
   private static final String regexForHandle =
         "(?<=span>&rlm)(?:.*<b>)(.*)(?=<\\/b><\\/span>)";
   private static final String regexForTime =
         "(?<=<a href=\")(?:.*)(?:\" class=\"tweet-timestamp js-permalink js-nav js-tooltip\" title=\")(.*)(?:\\s-\\s)(.*)(?=\" ><span.*)";
   private static final String regexForTweet =
         "(?<=<p class=\"TweetTextSize  js-tweet-text tweet-text\" lang=\"en\" data-aria-label-part=\"[0-9]\">)(.*)(?=<\\/p>)";
    
   //INSTANCE VARIABLES
   private String name;
   private String handle;
   private String time;
   private String tweet;
   
   private static URL url;
   public static BufferedWriter writer = null;
   
   private static int matchCount = 0; // number of matches found from HTML
   public static int HTMLCounter = 1; // number to keep track of files
   
   public URLProcess(String initURL)
   {
      try {
         url = new URL(initURL);
         
         getNewestHTMLCounter(); // creating new file? don't overwrite existing ones
         
         saveHTMLFile(); // save HTML to local disk
         webpageReader(); // parse HTML
         
      } catch (Exception e) { System.out.println(e); }
      
   }
   
   /**
    * Before creating a new file, check if the given file name exists.
    * If exists, increment HTMLCounter until file doesn't exist 
    * and then create file. 
    */
   public static void getNewestHTMLCounter()
   {
      File myFile = new File("webpage " +HTMLCounter+ " input.txt");
      
      while (myFile.exists()) { // file exist?
         System.out.println("File Exists, Iterating Next"); 
         HTMLCounter++;
         myFile = new File("webpage " +HTMLCounter+ " input.txt");
      }
      
      HTMLCounter--;
      //System.out.println("HTMLCounter after iteration: " +HTMLCounter);

   }
   
   /**
    * Goes through several regex patterns, if it finds a match in one
    * stop searching for that one and search for next and so on until
    * the last pattern. Repeat. The twitter HTML follows a specific pattern
    * first the name, then handle, then time and finally the tweet as such 
    * the code for pattern matching is in that order.
    * 
    * @throws Exception
    */
   public void webpageReader() throws Exception
   {
      BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream())); // read URL
      String line = reader.readLine();
      
      while (line != null) {
         Pattern p = Pattern.compile(regexForName); // regex pattern for name
         Matcher m = p.matcher(line);
      
         if(m.find()) {
            
            //System.out.println(m.group(1));
             
             while(line != null) {
                Pattern p2 = Pattern.compile(regexForHandle); // regex pattern for handle
                Matcher m2 = p2.matcher(line);
                
                if(m2.find()) {
                   
                   //System.out.println(m2.group(1));
                   
                   while(line != null) {
                      Pattern p3 = Pattern.compile(regexForTime); // regex pattern for time
                      Matcher m3 = p3.matcher(line);
                      
                      if(m3.find()) {
                         
                         //System.out.println(m3.group(1)+", " + m3.group(2));
                         
                         while(line != null) {
                            Pattern p4 = Pattern.compile(regexForTweet); // regex pattern for tweet
                            Matcher m4 = p4.matcher(line);
                            
                            if(m4.find()) { // initialize instance variables after obtain parsed data and write to text then to be read and uploaded to database
                               
                               //System.out.println(m4.group());;
                               
                               name = m.group(1);
                               handle = m2.group(1);
                               time = m3.group(1)+", " + m3.group(2);
                               tweet = m4.group();
                               
                               matchCount++; // increment number of matches found
                               writeToOutput("null   "+ name + "   @" + handle + "   " + time + "   " + tweet + "   ");
                               
                               
                               break;
                            } // find tweet?
                            line = reader.readLine();
                         } // tweet while
                         break;
                      } // find time?
                      line = reader.readLine();
                   } // time while
                   break;
                } // find handle?
                line = reader.readLine();
             } // handle while
         } //find name?
         
         line = reader.readLine(); // next line
      } 
      
      System.out.println("Found: " +matchCount+ " matches\n");
      matchCount = 0;
      
   } // webpageLineReader
   
   /**
    * Saves the HTML to local disk
    * 
    * @throws MalformedURLException
    * @throws IOException
    * @throws Exception
    */
   public static void saveHTMLFile() throws MalformedURLException, IOException, Exception   
   {
      System.out.println("\nReading HTML file...");
      HTMLCounter++; 
     
      ReadableByteChannel rbc = Channels.newChannel(url.openStream());
      FileOutputStream fos = new FileOutputStream("webpage " + HTMLCounter + ".html");
      fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
      fos.close();
       
   }
   
   /**
    * Write the parsed data from HTML to text file.
    * 
    * @param result
    */
   public static void writeToOutput(String result) 
   {
      //WRITE TO OUTPUT FILE
      try {
         writer = new BufferedWriter(new FileWriter("webpage " +HTMLCounter+ " input.txt", true)); // continue to write to existing output file
         writer.write(result);
         writer.newLine();  // newline
         writer.close();
      
      } catch (Exception e) { System.out.println("Error: No Output File"); }  
   } // writeToOutput
   
   public static int getHTMLCounter() { return HTMLCounter; }
}

