/**
 * This class is the beer class, basically holds the beer name and its attributes and stuff
 */

import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.*;
import java.util.List;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class beer {

    public static String[] keywords = {"bitter", "smooth", "citrus", "tropical", "hop", "light",
            "dark", "balanced", "dank", "refreshing", "crisp", "fruit", "thick"};

    // ATTRIBUTES FOR EVERY BEER IN THE DATABASE (ADD MORE LATER)
    String name;
    String link;
    String brewery;
    String location;
    HashMap<String, Integer> keyValues;

    public beer(String n, String l){
        name = n;
        link = l;
        brewery = null;
        location = null;
        keyValues = new HashMap<String, Integer>();
        for(int i = 0; i < keywords.length; i++){
            keyValues.put(keywords[i], 0);
        }
    }

    public void setName(String n){
        name = n;
    }

    public void setLink(String l){
        link = l;
    }

    public void setBrewery(String b){ brewery = b; }

    public void setLocation(String l){ location = l; }

    public void increment(String s){ keyValues.replace(s, keyValues.get(s) + 1); }

    public String getName(){
        return name;
    }

    public String getLink(){ return link; }

    public String getBrewery(){ return brewery; }

    public String getLocation(){ return location; }

    public int getKeyValue(String s){
        return keyValues.get(s);
    }

}
