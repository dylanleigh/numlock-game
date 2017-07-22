// main program entry point
// vim:set shiftwidth=3 tabstop=3 expandtab:

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.*;

class Numlock extends JFrame implements WindowListener
{
   public Numlock()
   {
      super(UI.APP_TITLE);
      Container parent;
      
      // Set frame size and operation
      this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      this.setPreferredSize(UI.APP_SIZE);
      this.setResizable(false);

      parent = this.getContentPane();
      parent.setLayout(new CardLayout());

      parent.add(new UIServerMenu(parent), UI.SERVER_MENU);
      parent.add(new UIGameMenu(parent), UI.GAME_MENU);
      parent.add(new UIGameBoard(parent), UI.GAME_BOARD);

      this.pack();
      this.setVisible(true);

      // Center in viewable area
      this.setLocationRelativeTo(null);
      // Add self as window listener for program closure
      this.addWindowListener(this);
   }
        
   public static void main(String args[])
   {
      //command line args
      for(int i = 0; i < args.length; i++)
      {
         if (args[i].indexOf("-debug") != -1)
            UI.debugMode = true;
      }

      javax.swing.SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            JFrame.setDefaultLookAndFeelDecorated(true);
            JDialog.setDefaultLookAndFeelDecorated(true);
            Numlock application = new Numlock();
         }
      });
   }

   /** Called when application is closed by clicking on the "X" 
    */
   public void windowClosing(WindowEvent we)
   {
      System.exit(0);
   }

   // interface WindowListener forces us to implement functions for
   // many window events, but we don't actually need to do anything
   public void windowClosed(WindowEvent we) {}

   public void windowOpened(WindowEvent we) {}

   public void windowActivated(WindowEvent we) {}

   public void windowDeactivated(WindowEvent we) {}

   public void windowIconified(WindowEvent we) {}

   public void windowDeiconified(WindowEvent we) {}
}
