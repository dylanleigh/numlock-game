//Game menu
// vim:set shiftwidth=3 tabstop=3 expandtab:

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;

public class UIGameMenu extends UI implements ActionListener
{
   private JButton discB;
   private JButton startB;
   private JRadioButton diff1RB;
   private JRadioButton diff2RB;
   private JRadioButton diff3RB;
   private JTextArea hiScoreTA;
   private JScrollPane scrollPane;
   private ButtonGroup rbgroup;

   public UIGameMenu(Container parent)
   {
      super(parent);

      discB = new JButton("Back");
      startB = new JButton("Start");
      diff1RB = new JRadioButton("Easy", true);
      diff2RB = new JRadioButton("Medium", false);
      diff3RB = new JRadioButton("Hard", false);
      //diff3RB.setEnabled(false);

      rbgroup = new ButtonGroup();
      rbgroup.add(diff1RB);
      rbgroup.add(diff2RB);
      rbgroup.add(diff3RB);

      hiScoreTA = new JTextArea();
      hiScoreTA.setEditable(false);
      scrollPane = new JScrollPane(hiScoreTA,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

      this.add(discB);
      this.add(startB);
      this.add(diff1RB);
      this.add(diff2RB);
      this.add(diff3RB);
      this.add(scrollPane);

      discB.addActionListener(this);
      startB.addActionListener(this);

      diff1RB.setBounds(200,40,100,20);
      diff2RB.setBounds(200,70,100,20);
      diff3RB.setBounds(200,100,100,20);

      startB.setBounds(340,40,UI.BUTTON_WIDTH,UI.BUTTON_HEIGHT);
      discB.setBounds(340,80,UI.BUTTON_WIDTH,UI.BUTTON_HEIGHT);

      scrollPane.setBounds(100,150,440,280);
   }

   public void setVisible(boolean b)
   {
      super.setVisible(b);
      
      if (b)
      {
         //fetch highscores
         try
         {
            String input = read();
            if (input.indexOf("High Scores follow:") == -1)
               throw new Exception("Garbled response from server.");

            hiScoreTA.setText("\n\t\tHigh Scores:\n");
            input = read();
            while (input.indexOf("End High Scores.") == -1)
            {
               hiScoreTA.setText(hiScoreTA.getText() + input + "\n");
               input = read();
            }
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
   }

   public void actionPerformed(ActionEvent event)
   {
      Object source = event.getSource();
      CardLayout cl = (CardLayout) this.parentPane.getLayout();

      if (source.equals(discB))
      {
         cl.show(parentPane, UI.SERVER_MENU);
      }
      else if (source.equals(startB))
      {
         try
         {
            if (diff1RB.isSelected())
               write("Newgame difficulty: 1");
            else if (diff2RB.isSelected())
               write("Newgame difficulty: 2");
            else
               write("Newgame difficulty: 3");
            cl.show(parentPane, UI.GAME_BOARD);
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
}
