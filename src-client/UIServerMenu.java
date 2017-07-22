// Server Menu
// vim:set shiftwidth=3 tabstop=3 expandtab:

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;

public class UIServerMenu extends UI implements ActionListener
{
   private JButton quitB;
   private JButton connectB;
   private JComboBox serverCB;
   private JLabel topL;
   private JLabel botL;
   private JLabel playerL;
   private JTextField playerTF;

   public UIServerMenu(Container parent)
   {
      super(parent);

      quitB = new JButton("Quit");
      connectB = new JButton("Connect");
      serverCB = new JComboBox();
      topL = new JLabel("NumLock version 1.0");
      botL = new JLabel("Server:");
      playerL = new JLabel("Enter Name:");
      playerTF = new JTextField();

      serverCB.addItem("localhost:7331");
      serverCB.addItem("dleigh.ath.cx:7331");
      serverCB.addItem("synapse.gotdns.org:7331");
      serverCB.setEditable(true);

      this.add(quitB);
      this.add(connectB);
      this.add(serverCB);
      this.add(topL);
      this.add(botL);
      this.add(playerL);
      this.add(playerTF);

      quitB.addActionListener(this);
      connectB.addActionListener(this);

      serverCB.setBounds(220, 235, 200, 20);
      quitB.setBounds(220, 265, UI.BUTTON_WIDTH, UI.BUTTON_HEIGHT);
      connectB.setBounds(325, 265, UI.BUTTON_WIDTH, UI.BUTTON_HEIGHT);
      topL.setBounds(250,100,170,20);
      botL.setBounds(290,210,60,20);
      playerL.setBounds(220, 185, 80, 20);
      playerTF.setBounds(320, 185, 100, 20);
   }

   public void actionPerformed(ActionEvent event)
   {
      Object source = event.getSource();
      CardLayout cl = (CardLayout) this.parentPane.getLayout();

      if (source.equals(quitB))
      {
         System.exit(0);
      }
      else if (source.equals(connectB))
      {
         if ((playerTF.getText().trim()).length() < 1)
         {
            JOptionPane.showMessageDialog(null, "You must enter a player"
                  + " name to connect to a server.",
               "No player name", JOptionPane.ERROR_MESSAGE);
            return;
         }
         try
         {
            //get server
            String s = (String)serverCB.getSelectedItem();
            if (s == null)
            {
               JOptionPane.showMessageDialog(null, "You must enter a"
                  + " server to connect to.",
                  "No server given", JOptionPane.ERROR_MESSAGE);
               return;
            }
            if (s.length() < 1)
            {
               JOptionPane.showMessageDialog(null, "You must enter a"
                  + " server to connect to.",
                  "No server given", JOptionPane.ERROR_MESSAGE);
               return;
            }


            //get port
            int index = s.indexOf(":");
            if (index == -1)
            {
               JOptionPane.showMessageDialog(null, "You must enter a"
                  + " port after the server name, seperating them with a"
                  + " colon ( \":\" )",
                  "No port specified", JOptionPane.ERROR_MESSAGE);
               return;
            }

            int port = 0;
            try
            {
               port = Integer.parseInt(s.substring(index+1));
            }
            catch (Exception e)
            {
               JOptionPane.showMessageDialog(null, "You must enter a"
                  + " number for the port to connect to.",
                  "Invalid port specified", JOptionPane.ERROR_MESSAGE);
               return;
            }

            //connect
            socket = new Socket(s.substring(0,index), port);

            //make reader + writer
            in = new BufferedReader(new
                  InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new
               OutputStreamWriter(socket.getOutputStream()));

            //init and transfer to game menu
            String input = read();
            if (input.indexOf("NumLock server- protocol version") == -1)
            {
               //not a NumLock server
               JOptionPane.showMessageDialog(null, "This server does not"
                  + " appear to be a valid NumLock server. If it is, make"
                  + " sure you are connecting on the right port.",
                  "Protocol Error", JOptionPane.ERROR_MESSAGE);
               closeSocket();
               return;
            }
            write("NumLock client- protocol version 1.0");
            write("Player Name: " + playerTF.getText().trim());
            cl.show(this.parentPane, GAME_MENU);
         }
         catch (Exception e)
         {
            JOptionPane.showMessageDialog(null, "Unable to connect to"
                  + " the server. Make sure the"
                  + " name and port are correct.",
               "Unable to connect", JOptionPane.ERROR_MESSAGE);
            if (debugMode)
               System.out.println(e.getMessage());
         }
      }
      /*
      else if (source.equals(serverCB))
      {
         //don't have to do anything here really...
      }*/
   }

   public void setVisible(boolean b)
   {
      super.setVisible(b);
      if (b)
         closeSocket();
   }
}
