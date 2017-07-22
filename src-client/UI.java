// Parent class for all 3 UI panes.
// vim:set shiftwidth=3 tabstop=3 expandtab:

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;

/**
 * Parent class for the UI panes.
 */
abstract public class UI extends JPanel
{
   //constants
   final static String APP_TITLE = "NumLock";
   final static int APP_WIDTH = 640;
   final static int APP_HEIGHT = 480;
   final static int BUTTON_WIDTH = 95;
   final static int BUTTON_HEIGHT = 20;
   final static Dimension APP_SIZE = new Dimension(APP_WIDTH,APP_HEIGHT);

   /* names for card layout manager */
   final static String SERVER_MENU = "SERVER_MENU";
   final static String GAME_MENU = "GAME_MENU";
   final static String GAME_BOARD = "GAME_BOARD";

   /** Parent pane that holds the entire app */
   protected Container parentPane;

   /* Static protected stuff used by all UI panels */
   static public boolean debugMode = false;
   static protected Socket socket = null;
   static protected BufferedReader in = null;
   static protected BufferedWriter out= null;

   static protected void closeSocket()
   {
      //stuff necessary to do when disconnecting here...
      try
      {
         socket.close();
      }
      catch (Exception e)
      {

      }
   }

   static void write(String output) throws IOException
   {
      out.write(output + "\n");
      if (debugMode)
         System.out.println("Client: " + output);
      out.flush();
   }

   static String read() throws IOException
   {
      String input = in.readLine();
      if (debugMode)
         System.out.println("Server: " + input);
      if (input == null)
         return "";
      return input;
   }

   /** Constructor - note that there is no default
    *
    * @param  parent  Parent pane that holds the entire app
    */
   protected UI(Container parent)
   {
      super();
      this.setLayout(null);
      parentPane = parent;
   }
}
