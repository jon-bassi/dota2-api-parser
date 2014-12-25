import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.io.IOException;

public class Dota2
{
   
   public final String API_KEY;
   
   public final String MATCH_HISTORY_BASE_URL =
         "https://api.steampowered.com/IDOTA2Match_570/GetMatchHistory/V001/?key=";
   public final String MATCH_INFO_BASE_URL =
         "https://api.steampowered.com/IDOTA2Match_570/GetMatchDetails/V001/?match_id=";
   
   public ArrayList<Integer> matchIDs = new ArrayList<Integer>();
   
   public Dota2(String apiKey)
   {
      API_KEY = apiKey;
   }
   
   
   private void parseManyMatches() throws IOException
   {
      String url = MATCH_HISTORY_BASE_URL;
      url += API_KEY;
      url = addOptionsToURL(url,0,10);
      Document doc = Jsoup.connect(url).ignoreContentType(true).get();
      
      String page = convertToString(doc);
      
      int i = 0;
      while(page.contains("match_id"))
      {
         System.out.println(i++);
         page = page.substring(page.indexOf("match_id:") + 9);
         //System.out.println(page);
         matchIDs.add(Integer.parseInt(page.substring(0, 10)));
      }
   }
   
   private void parseNextManyMatches() throws IOException, InterruptedException
   {
      Thread.sleep(1500);
   }
   
   private static void parseSingleMatch(Integer matchID) throws IOException, InterruptedException
   {
      /*
      Thread.sleep(1500);
      String url = MATCH_INFO_BASE_URL;
      url += matchID;
      url += "key=" + API_KEY;
      */
   }
   
   /**
    * possible expressions:
    * match_id:
    * lobby_type:
    * start_time:
    * @param expression one of the above
    */
   private void parseStringFor(String expression) throws IOException, InterruptedException
   {
      
      
   }
   
   private String addOptionsToURL(String url,int gamemode, int minPlayers)
   {
      return url + "&game_mode=" + gamemode + "&min_players=" + minPlayers;
   }
   
   private String convertToString(Document doc)
   {
      Elements results = doc.select("body");
      String page = results.get(0).toString();
      page = page.replaceAll("[\\[\\{\"\\}\\]\n/<> ]", "");
      return page.replace("body","");
   }
   
   public static void main(String[] args) throws IOException, InterruptedException
   {
      // replace <API KEY> with your unique key
      Dota2 parser = new Dota2("<API KEY>");
      System.out.println("start");
      parser.parseManyMatches();
      System.out.println("done");
      System.out.println(parser.matchIDs);
      for (int i = 0; i < parser.matchIDs.size(); i++)
      {
         parseSingleMatch(parser.matchIDs.get(i));
      }
   }
   
}
