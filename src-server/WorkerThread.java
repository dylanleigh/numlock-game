import java.io.*;
import java.net.*;

public class WorkerThread extends Thread
{
   Socket socket = null;
   boolean debugMode = false;

   public static final int BOARD_HEIGHT = 10;
   public static final int BOARD_WIDTH = 10;
   HighScores hiscores;

   public WorkerThread(Socket sock, boolean debug, HighScores hs)
   {
      socket = sock;
      debugMode = debug;
      hiscores = hs;
   }

   void write(BufferedWriter out, String output) throws IOException
   {
      out.write(output + "\n");
      if (debugMode)
         System.out.println("Server: " + output);
      out.flush();
   }

   String read(BufferedReader in) throws IOException
   {
      String input = in.readLine();
      if (debugMode)
         System.out.println("Client: " + input);
      if (input == null)
         return "";
      return input;
   }

   void onconnect() throws Exception
   {
      BufferedReader in = new BufferedReader(new
         InputStreamReader(socket.getInputStream()));
      BufferedWriter out = new BufferedWriter(new
         OutputStreamWriter(socket.getOutputStream()));

      String input;
      String player;

      //hello message send
      write(out, "NumLock server- protocol version 1.0");

      //hello message recieve, and player name
      input = read(in);
      if (input.indexOf("NumLock client- protocol version") == -1)
      {
         //not a NumLock client
         if (debugMode)
            System.out.println("Garbled Response Received.");
         socket.close();
         return;
      }
      input = read(in);
      if (input.indexOf("Player Name: ") == -1)
      {
         if (debugMode)
            System.out.println("Garbled Response Received.");
         socket.close();
         return;
      }
      player = input.substring(12); //get player name

      //MENU LOOP
      while(socket.isConnected())
      {
         //high score send
         write(out, "High Scores follow:");
         write(out, hiscores.print());
         write(out, "End High Scores. Transmit game parameters");

         //gameparams recieve
         input = read(in);
         if (input.indexOf("Newgame difficulty: ") == -1)
         {
            if (debugMode)
               System.out.println("Garbled Response Received.");
            socket.close();
            return;
         }
         int difficulty = (Integer.parseInt(input.substring(20)) - 1);
         if (difficulty < 0 || difficulty >= hiscores.DIFFLEVELS.length)
         {
            if (debugMode)
               System.out.println("Bad Difficulty Level Received.");
            socket.close();
            return;
         }

         //gamestate send
         write(out, "Initial Game State follows:");

         //create the gameboard
         int[][] gameboard = new int[BOARD_WIDTH][BOARD_HEIGHT];
         for (int y = 0; y < BOARD_HEIGHT; y++)
            for (int x = 0; x < BOARD_WIDTH; x++)
               gameboard[x][y] = ((int)(java.lang.Math.random()*9)+1);

         for (int y = 0; y < BOARD_HEIGHT; y++)
         {
            String output = new String();
            for (int x = 0; x < BOARD_WIDTH; x++)
               output = output + " " + gameboard[x][y];
            write(out, output);
         }

         int cy;
         int pscore = 0;
         int cscore = 0;
         //player or cpu move first...
         if (java.lang.Math.random() > 0.5)
         {
            cy = (int) java.lang.Math.random()*BOARD_HEIGHT;
            //player first
            write(out, "Player move row " + cy);
         }
         else
         {
            int x = (int)java.lang.Math.random()*BOARD_WIDTH;
            cy = (int)java.lang.Math.random()*BOARD_HEIGHT;
         
            //cpu first
            write(out, "CPU move " + x + "," + cy);
            cscore += gameboard[x][cy];
            gameboard[x][cy] = 0;
            write(out, "Scores: Player " + pscore + ", CPU " + cscore);
         }

         //GAME LOOP
         while(true)
         {
            //get player move
            input = read(in);
            if (input.indexOf("Move ") == -1)
            {
               if (debugMode)
                  System.out.println("Garbled Response Received.");
               socket.close();
               return;
            }
            int pmovex = Integer.parseInt(input.substring(5, 6));
            int pmovey = Integer.parseInt(input.substring(7, 8));

            //validate player y
            if (cy != pmovey)
            {
               if (debugMode)
                  System.out.println("Illegal row jumping.");
               socket.close();
               return;
            }

            //generate new player score
            if (gameboard[pmovex][pmovey]==0)
            {
               if (debugMode)
                  System.out.println("Invalid Position.");
               socket.close();
               return;
            }
            pscore += gameboard[pmovex][pmovey];
            gameboard[pmovex][pmovey] = 0;
            write(out, "Scores: Player " + pscore + ", CPU " + cscore);

            //see if cpu can move, if not endgame
            for (cy = 0; cy < BOARD_HEIGHT; cy++)
            {
               if(gameboard[pmovex][cy] != 0)
                  if (difficulty == 0)
                  {
                     //computer move diff 0
                     cscore += gameboard[pmovex][cy];
                     gameboard [pmovex][cy] = 0;
                     write(out, "CPU Move " + pmovex + "," + cy);
                     break;
                  }
                  else
                     break;
            }
            if (cy == BOARD_HEIGHT) 
               //endgame - print HIghScore! if highscore
            {
               if (hiscores.insert(difficulty, pscore-cscore, player))
               {
                     write(out, "Endgame - Highscore");
                     hiscores.save(NumlockServer.HIGHSCORE_FILE);
               }
               else
                  write(out, "Endgame");

               write(out, "Scores: Player " + pscore + ", CPU " + cscore);
               break;
            }

            //make cpu move if not diff easy
            if (difficulty == 1) //medium
            {
               cy = (int)(java.lang.Math.random()*BOARD_WIDTH);
               while (gameboard[pmovex][cy] == 0)
               {
                  //find a valid random slot
                  cy = (int)(java.lang.Math.random()*BOARD_WIDTH);
                  if (debugMode)
                     System.out.println(cy);
               }
               cscore += gameboard[pmovex][cy];
               gameboard [pmovex][cy] = 0;
               write(out, "CPU Move " + pmovex + "," + cy);
            }
            else if (difficulty == 2) //hard
            {
               //find the highest value 
               int besty = 0;
               for (cy = 1; cy < BOARD_HEIGHT; cy++)
                  if (gameboard[pmovex][cy] > gameboard[pmovex][besty])
                  {
                     besty=cy;
                     if (debugMode)
                        System.out.println(cy);
                  }

               cy=besty;
               cscore += gameboard[pmovex][cy];
               gameboard [pmovex][cy] = 0;
               write(out, "CPU Move " + pmovex + "," + cy);
            }
            //see if player can move, if not endgame
            int px;
            for (px = 0; px < BOARD_WIDTH; px++)
            {
               if(gameboard[px][cy] != 0)
                  break;
            }
            if (px == BOARD_WIDTH) //endgame
            {
               if (hiscores.insert(difficulty, pscore-cscore, player))
               {
                     write(out, "Endgame - Highscore");
                     hiscores.save(NumlockServer.HIGHSCORE_FILE);
               }
               else
                  write(out, "Endgame");

               write(out, "Scores: Player " + pscore + ", CPU " + cscore);
               break;
            }

            write(out, "Scores: Player " + pscore + ", CPU " + cscore);

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
         }
      }
   }

   public void run()
   {
      if (socket == null)
         return;

      if (debugMode)
         System.out.println("Accepted connection from " +
            socket.getInetAddress());

      try
      {
         onconnect();
      }
      catch (NumberFormatException e)
      {
         if (debugMode)
            System.out.println("NumberFormatException: " + e.getMessage());
      }
      catch (SocketException e)
      {
         if (debugMode)
            System.out.println("SocketException: " + e.getMessage());
      }
      catch (IndexOutOfBoundsException e)
      {
         if (debugMode)
            System.out.println("IndexOutOfBoundsException: " +
               e.getMessage());
      }
      catch (IOException e)
      {
         if (debugMode)
            System.out.println("IOException: " + e.getMessage());
      }
      catch (Exception e)
      {
         if (debugMode)
            System.out.println("Error: " + e.getMessage());
      }

      if (debugMode)
         System.out.println("Closing connection from " +
            socket.getInetAddress());
      if (!socket.isClosed())
      {
         try
         {
            socket.close();
         }
         catch (Exception e)
         {
         }
      }
   }
}

