import java.io.*;

public class HighScores
{
   public static final String[] DIFFLEVELS = {"Easy", "Normal", "Hard"};
   public static final int MAXHIGHSCORES = 5; //per difficulty level
   //the pvp difficulty level is not implemented yet.

   private int[][] scores; // (DIFFLEVELS.length, MAXHIGHSCORES)
   private String[][] names; // (DIFFLEVELS.length, MAXHIGHSCORES)

   public HighScores()   // generate the score list
   {
      scores = new int[DIFFLEVELS.length][MAXHIGHSCORES];
      names = new String[DIFFLEVELS.length][MAXHIGHSCORES];

      for (int x = 0; x<DIFFLEVELS.length; x++)
         for (int y = 0; y<MAXHIGHSCORES; y++)
         {
            scores[x][y] = MAXHIGHSCORES - y;
            names[x][y] = "Nobody";
         }
   }

   public synchronized void load(String filename) throws Exception
   {
      BufferedReader infile = new BufferedReader(new
         FileReader(filename));

      for(int diff = 0; diff < DIFFLEVELS.length; diff++)
      {
         infile.readLine(); //TODO score title - ignore atm, should check
         for (int i = 0; i < MAXHIGHSCORES; i++)
         {
            String s = infile.readLine();
            int index = s.indexOf(" ");
            scores[diff][i]= Integer.parseInt(s.substring(0,index));
            names[diff][i]= (s.substring(index+1));
         }
      }
   }

   public synchronized void save(String filename) throws Exception
   {
      BufferedWriter outfile = new
      BufferedWriter(new FileWriter(filename));
      for(int diff = 0; diff < DIFFLEVELS.length; diff++)
      {
         outfile.write(DIFFLEVELS[diff] + "\n");
         for (int k = 0; k < MAXHIGHSCORES; k++)
            outfile.write(String.valueOf(
               scores[diff][k]) + " " +
               names[diff][k] + "\n");
      }
      outfile.close();
   }

   /**
    * Returns true if the score was in fact a high score.
    */
   public synchronized boolean insert(int diff, int score, String player)
   {
      for(int i = 0; i < MAXHIGHSCORES; i++)
      {
         if (score > scores[diff][i])
         {
            //it's a high score
            //move others down...
            for (int j = MAXHIGHSCORES -1; j > i; j--)
            {
               scores[diff][j]=scores[diff][j-1];
               names[diff][j]=names[diff][j-1];
            }
            //save this score to file
            scores[diff][i]=score;
            names[diff][i]=player;
            return true;
         }
      }

      return false; //dummy
   }

   public synchronized String print()
   {
      String ret = "";
      for(int diff = 0; diff < DIFFLEVELS.length; diff++)
      {
         ret += "\n" + DIFFLEVELS[diff] + "\n";
         for (int i = 0; i < MAXHIGHSCORES; i++)
         {
            ret += String.valueOf(scores[diff][i])
                  + "\t\t" + names[diff][i] + "\n";
         }
      }
      return ret;
   }
}
