// Numlock server (console app)
// vim:set shiftwidth=3 tabstop=3 expandtab:

import java.net.*;
import java.io.*;

public class NumlockServer
{

   static ServerSocket sSocket;
   public static final String HIGHSCORE_FILE = "hiscores.dat";
   static HighScores hiscores;

   static int NLport = 7331;
   static boolean debugMode = false;

   static Thread[] threads;
   public static final int MAXTHREADS = 5;

   static void serve()
   {
      Socket socket = null;
      while(true)
      {
         try
         {
            int t = 0;

            if (debugMode)
               System.out.println("Waiting for connection");
            socket = sSocket.accept();

            for(t = 0; t < MAXTHREADS; t++)
            {
               if (threads[t] == null)
               {
                  threads[t] = new WorkerThread(socket, debugMode, hiscores);
                  threads[t].start();
                  break;
               }
               else if (!threads[t].isAlive())
               {
                  threads[t] = new WorkerThread(socket, debugMode, hiscores);
                  threads[t].start();
                  break;
               }
            }
            if (t >= MAXTHREADS) //too many connections
            {
               socket.close();
            }
         }
         catch (Exception e)
         {
            if (debugMode)
               System.out.println("Error in server:" + e.getMessage());
         }
      }
   }

   public static void main(String[] args)
   {
      //INIT
      //command line args
      for(int i = 0; i < args.length; i++)
      {
         if (args[i].indexOf("-debug") != -1)
            debugMode = true;
         else if (args[i].indexOf("-port") != -1)
            NLport = Integer.parseInt(args[i].substring(5));
      }

      //Server socket...
      try
      {
         sSocket = new ServerSocket(NLport);
      }
      catch (Exception e)
      {
         System.out.println("Error spawning ServerSocket : " +
            e.getMessage());
         return;
      }

      //highscores
      hiscores = new HighScores();
      try
      {
         hiscores.load(HIGHSCORE_FILE);
      }
      catch (Exception e)
      {
         System.out.println("Error reading High Scores : "
            + e.getMessage());
         hiscores = new HighScores();
      }

      //threads
      threads = new Thread[MAXTHREADS];

      //MAIN LOOP
      serve();
   }
}
