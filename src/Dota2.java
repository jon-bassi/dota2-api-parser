import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.io.IOException;

/**
 * note: all requests to the web api must be at least one second apart, 
 * I've set it to 1.5 seconds to in this program as to not risk a 503 error
 * @author jon-bassi
 *
 */
public class Dota2
{
   
   private final String API_KEY;
   
   private final String MATCH_HISTORY_BASE_URL =
         "https://api.steampowered.com/IDOTA2Match_570/GetMatchHistory/V001/?key=";
   private final String MATCH_INFO_BASE_URL =
         "https://api.steampowered.com/IDOTA2Match_570/GetMatchDetails/V001/?match_id=";
   // parse options
   private int gamemode, minPlayers;
   
   public ArrayList<Integer> matchIDs = new ArrayList<Integer>();
   
   /**
    * 
    * @param apiKey
    * @param gameMode 0-8 for different game modes, 9 if all
    * @param minPlayers 0-10 for minimum number of players per game
    */
   public Dota2(String apiKey, int gamemode, int minPlayers)
   {
      API_KEY = apiKey;
      this.gamemode = gamemode;
      this.minPlayers = minPlayers;
   }
   
   /**
    * possible expressions:
    *   match_id:
    *   lobby_type:
    *   start_time:
    * @param pageSource source of page formatted as a String
    * @param expression one of the above
    */
   private void parseStringFor(String pageSource, String expression)
   {
      int sizeOfData;
      switch (expression)
      {
         case "match_id:" : sizeOfData = 10;
            break;
         default : sizeOfData = 0;
            break;
      }
      while(pageSource.contains(expression))
      {
         pageSource = pageSource.substring(pageSource.indexOf(expression) + 9);
         matchIDs.add(Integer.parseInt(pageSource.substring(0, sizeOfData)));
      }
   }
   
   private String addOptionsToURL(String url,int gamemode, int minPlayers)
   {
      return url + "&game_mode=" + gamemode + "&min_players=" + minPlayers;
   }
   
   /**
    * Converts webpage from document to a string representation, removes 
    * unnecessary delimiters
    * @param doc webpage to convert
    * @return String representation of webpage
    */
   private String convertToString(Document doc)
   {
      Elements results = doc.select("body");
      String page = results.get(0).toString();
      page = page.replaceAll("[\\[\\{\"\\}\\]\n/<> ]", "");
      return page.replace("body","");
   }
   
   public void parseManyMatches() throws IOException
   {
      String url = MATCH_HISTORY_BASE_URL;
      url += API_KEY;
      url = addOptionsToURL(url,gamemode,minPlayers);
      Document doc = Jsoup.connect(url).ignoreContentType(true).get();
      
      String page = convertToString(doc);
      parseStringFor(page,"match_id:");
   }
   
   public void parseNextManyMatches() throws IOException, InterruptedException
   {
      Thread.sleep(1500);
   }
   
   public static void parseSingleMatch(Integer matchID) throws IOException, InterruptedException
   {
      /*
      Thread.sleep(1500);
      String url = MATCH_INFO_BASE_URL;
      url += matchID;
      url += "key=" + API_KEY;
      */
   }
}
