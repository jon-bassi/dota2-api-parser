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
   // your unique API key
   private final String API_KEY;
   
   // base url for looking at recent matches
   private final String MATCH_HISTORY_BASE_URL =
         "https://api.steampowered.com/IDOTA2Match_570/GetMatchHistory/V001/?key=";
   // base url for looking at one specific match
   private final String MATCH_INFO_BASE_URL =
         "https://api.steampowered.com/IDOTA2Match_570/GetMatchDetails/V001/?match_id=";
   // parse options
   private int gamemode = -1;
   private int minPlayers = -1;
   private int startAtMatch = -1;
   
   // stores match IDs of parsed matches
   // note: change this to hash set later
   public ArrayList<Integer> matchIDs = new ArrayList<Integer>();
   
   /**
    * 
    * @param apiKey
    * @param gameMode 0-8 for different game modes, 9 if all
    * @param minPlayers 0-10 for minimum number of players per game
    */
   public Dota2(String apiKey)
   {
      API_KEY = apiKey;
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
   
   /**
    * modifies our url to parse for specific types of matches
    * @param url base url (match history)
    * @return modified url for parsing specific types of matches
    */
   private String addOptionsToURL(String url)
   {
      switch (gamemode)
      {
      case -1 :
         break;
      default : url += "&game_mode=" + gamemode;
      }
      switch (minPlayers)
      {
      case -1 :
         break;
      default : url += "&min_players=" + minPlayers;
      }
      
      switch (startAtMatch)
      {
      case -1:
         break;
      default : url += "&start_at_match_id=" + startAtMatch;
      }
      return url;
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
   
   /**
    * sets the game mode to parse for, set to -1 to parse all modes
    * (note: default is set to parse all game modes)
    * @param type the game mode to parse for
    */
   public void setGameMode(int gamemode)
   {
      this.gamemode = gamemode;
   }
   
   /**
    * sets minimum number of players per game to parse for, set to -1
    * to parse for any number of players
    * (note: default is no preference)
    * @param minPlayers minimum number of players required per game
    */
   public void setMinPlayers(int minPlayers)
   {
      this.minPlayers = minPlayers;
   }
   
   /**
    * sets the match to start at
    * @param startAtMatch match ID of the match to start parsing at
    */
   public void setStartMatch(int startAtMatch)
   {
      this.startAtMatch = startAtMatch;
   }
   
   /**
    * gets the IDs of 100 matches, default method
    * @param matches the number of matches to parse
    * @throws IOException
    */
   public void getMatchIDs() throws IOException, InterruptedException
   {
      Thread.sleep(1500);
      String url = MATCH_HISTORY_BASE_URL;
      url += API_KEY;
      url = addOptionsToURL(url);
      Document doc = Jsoup.connect(url).ignoreContentType(true).get();
      
      String page = convertToString(doc);
      parseStringFor(page,"match_id:");
   }
   
   /**
    * gets the IDs of n matches, max is 500
    * @param matches the number of matches to parse
    * @throws IOException
    */
   public void getMatchIDs(int matches) throws IOException, InterruptedException
   {
      String url = MATCH_HISTORY_BASE_URL;
      url += API_KEY;
      for (int i = 0; i < matches/100; i++)
      {
         System.out.println(i+ 1);
         url = addOptionsToURL(url);
         Document doc = Jsoup.connect(url).ignoreContentType(true).get();
         String page = convertToString(doc);
         parseStringFor(page,"match_id:");
         setStartMatch(matchIDs.get(matchIDs.size()-1));
         Thread.sleep(1500);
      }
   }
   
   public void parseSingleMatch(int matchID) throws IOException, InterruptedException
   {
      /*
      Thread.sleep(1500);
      String url = MATCH_INFO_BASE_URL;
      url += matchID;
      url += "key=" + API_KEY;
      */
   }
}
