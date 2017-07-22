//Game board panel
// vim:set shiftwidth=3 tabstop=3 expandtab:

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;

public class UIGameBoard extends UI implements ActionListener
{
   public final static int BOARD_HEIGHT=10;
   public final static int BOARD_WIDTH=10;
   
   private JLabel playerL;
   //private JLabel compL;
   //private JLabel cscoreL;
   private int[][] gameboard;
   private JButton[][] buttons;
   private int row; //hold the row enabled

   public UIGameBoard(Container parent)
   {
      super(parent);

      playerL = new JLabel("Scores: Player 0, CPU 0");
      //compL = new JLabel("Server");
      //cscoreL = new JLabel("Score 0");

      this.add(playerL);
      //this.add(compL);
      //this.add(cscoreL);

      gameboard = new int[BOARD_WIDTH][BOARD_HEIGHT];
      buttons = new JButton[BOARD_WIDTH][BOARD_HEIGHT];
      for (int y = 0; y < BOARD_HEIGHT; y++)
         for (int x = 0; x < BOARD_WIDTH; x++)
         {
            buttons[x][y] = new JButton();
            this.add(buttons[x][y]);
            buttons[x][y].setBounds((x*50)+120,(y*30)+100,45,25);
            buttons[x][y].setEnabled(false);
            buttons[x][y].addActionListener(this);
         }

      playerL.setBounds(170,50,300,20);
      //playerL.setBounds(270,50,100,20);
      //compL.setBounds(50,218,80,20);
      //cscoreL.setBounds(40,242,100,20);
   }

   public void setVisible(boolean b)
   {
      super.setVisible(b);

      if (b)
      {
         playerL.setText("Scores: Player 0, CPU 0");
         try
         {
            /*test = new JButton("test");
            this.add(test);
            test.setBounds(5,5,100,20);*/
            
            String input = read();
            //receive game state
            if (input.indexOf("Initial Game State follows:") == -1)
               throw new Exception("Garbled response from server.");

            for (int y = 0; y < BOARD_HEIGHT; y++)
            {
               input = read();
               for(int x = 0; x < BOARD_WIDTH; x++)
               {
                  gameboard[x][y] = Integer.parseInt(
                     input.substring((2*x)+1, (2*x)+2));
                  buttons[x][y].setText(String.valueOf(gameboard[x][y]));
                  buttons[x][y].setEnabled(false);
                  buttons[x][y].setVisible(true);
               }
            }

            //DEBUG : print gameboard
            if (debugMode)
            {
               for (int y = 0; y < BOARD_HEIGHT; y++)
               {
                  String output = new String();
                  for (int x = 0; x < BOARD_WIDTH; x++)
                     output = output + " " + gameboard[x][y];
                  System.out.println(output);
               }
            }

            //receive intiial moves etc...
            input = read();
            if (input.indexOf("Player move row") != -1)
            {
               //player move
               row = Integer.parseInt(input.substring(16,17));
               if (row < 0 || row > 9)
                  throw new Exception("Garbled response from server.");
               for (int x = 0; x < BOARD_WIDTH; x++)
                  buttons[x][row].setEnabled(true);
            }
            else if (input.indexOf("CPU move ") != -1)
            {
               int cx = Integer.parseInt(input.substring(9,10));
               row = Integer.parseInt(input.substring(11,12));

               flash(cx,row);
               setScores();
               for (int x = 0; x < BOARD_WIDTH; x++)
                  buttons[x][row].setEnabled(true);
            }
            else
            {
              //error
              throw new Exception("Garbled response from server.");
            }
         }
         catch (NumberFormatException e)
         {
            JOptionPane.showMessageDialog(null, "The server sent an"
               + " unexpected or invalid response. Disconnecting.",
               "Protocol Error", JOptionPane.ERROR_MESSAGE);
            if (debugMode)
               System.out.println("NumberFormatException: " + e.getMessage());
            ((CardLayout)this.parentPane.getLayout()).show(parentPane,
               UI.SERVER_MENU);
            return;
         }
         catch (Exception e)
         {
            JOptionPane.showMessageDialog(null, "The server sent an"
               + " unexpected or invalid response. Disconnecting.",
               "Protocol Error", JOptionPane.ERROR_MESSAGE);
            if (debugMode)
               System.out.println(e.getMessage());
            ((CardLayout)this.parentPane.getLayout()).show(parentPane,
               UI.SERVER_MENU);
            return;
         }
      }
      else //not visible
      {
         //buttons?
      }
   }

   void setScores() throws Exception
   {
      String input = read();
      playerL.setText(input);
   }
   
   void flash(int x, int y)
   {
      try
      {
         buttons[x][y].setVisible(false);
         buttons[x][y].setVisible(true);
         buttons[x][y].setVisible(false);
         buttons[x][y].setVisible(true);
         buttons[x][y].setVisible(false);
         buttons[x][y].setVisible(true);
         buttons[x][y].setVisible(false);
         buttons[x][y].setVisible(true);
         buttons[x][y].setVisible(false);
         buttons[x][y].setVisible(true);
         Thread.sleep(10);
      }
      catch (InterruptedException e)
      {
      }
      buttons[x][y].setVisible(false);
   }
   
   public void actionPerformed(ActionEvent event)
   {
      Object source = event.getSource();
      CardLayout cl = (CardLayout) this.parentPane.getLayout();

      for (int x = 0; x < BOARD_WIDTH; x++)
         buttons[x][row].setEnabled(false);

      for (int x = 0; x < BOARD_WIDTH; x++)
      {
         if (source == buttons[x][row])
         {
            try
            {
               flash(x,row);
               //transmit move
               write("Move " + String.valueOf(x) + "," + String.valueOf(row));
               setScores();
               
               //recieve game update
               String input = read();
               if (input.indexOf("Endgame - Highscore") != -1)
               {
                  //end game with high score
                  input = read();
                  playerL.setText(input);
                  JOptionPane.showMessageDialog(null,
                        "Congratulations!\nFinal "
                     + playerL.getText(),
                     "You got a high score!", JOptionPane.PLAIN_MESSAGE);
                  cl.show(parentPane, UI.GAME_MENU);
                  return;
               }
               else if (input.indexOf("Endgame") != -1)
               {
                  //end game
                  input = read();
                  playerL.setText(input);
                  JOptionPane.showMessageDialog(null, "Final "
                     + playerL.getText(),
                     "The game has ended", JOptionPane.PLAIN_MESSAGE);
                  cl.show(parentPane, UI.GAME_MENU);
                  return;
               }
               else if (input.indexOf("CPU Move ") != -1)
               {
                  int cx = Integer.parseInt(input.substring(9,10));
                  row = Integer.parseInt(input.substring(11,12));

                  flash(cx,row);
                  for (int xx = 0; xx < BOARD_WIDTH; xx++)
                     buttons[xx][row].setEnabled(true);
               }
               else
               {
                 //error
                 throw new Exception("Garbled update from server.");
               }
               
               //another endgame check
               input = read();
               if (input.indexOf("Endgame - Highscore") != -1)
               {
                  //end game with high score
                  input = read();
                  playerL.setText(input);
                  JOptionPane.showMessageDialog(null,
                        "Congratulations!\nFinal "
                     + playerL.getText(),
                     "You got a high score!", JOptionPane.PLAIN_MESSAGE);
                  cl.show(parentPane, UI.GAME_MENU);
                  return;
               }
               else if (input.indexOf("Endgame") != -1)
               {
                  //end game
                  input = read();
                  playerL.setText(input);
                  JOptionPane.showMessageDialog(null, "Final "
                     + playerL.getText(),
                     "The game has ended", JOptionPane.PLAIN_MESSAGE);
                  cl.show(parentPane, UI.GAME_MENU);
                  return;
               }
               else if (input.indexOf("Scores: Player ") != -1)
               {
                 playerL.setText(input);
               }
               else
               {
                 //error
                 throw new Exception("Garbled update from server.");
               }
            }
            catch (NumberFormatException e)
            {
               JOptionPane.showMessageDialog(null, "The server sent an"
                  + " unexpected or invalid response. Disconnecting.",
                  "Protocol Error", JOptionPane.ERROR_MESSAGE);
               if (debugMode)
                  System.out.println("NumberFormatException: " +
                     e.getMessage());
               cl.show(parentPane, UI.SERVER_MENU);
               return;
            }
            catch (Exception e)
            {
               JOptionPane.showMessageDialog(null, "The server sent an"
                  + " unexpected or invalid response. Disconnecting.",
                  "Protocol Error", JOptionPane.ERROR_MESSAGE);
               if (debugMode)
                  System.out.println(e.getMessage());
               cl.show(parentPane, UI.SERVER_MENU);
               return;
            }
         }
      }

      /*
      for (int y = 0; y < BOARD_HEIGHT; y++)
         for (int x = 0; x < BOARD_WIDTH; x++)
         {
         }*/
      /*
      if (source.equals(null)) 
      {
         //Need this if there is a button added.
      }*/
      
   }
}
