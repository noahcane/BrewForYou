/**
 * THIS CLASS GETS THE TOP 250 BEERS AND CREATES A BIG LIST OF THE BEERS + ATTRIBUTES
 */

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.List;
import java.util.ArrayList;
import java.lang.Thread;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.net.ssl.*;

import java.sql.*;

public class beeradvocate{

    public static String[] keywords = {"bitter", "smooth", "citrus", "tropical", "hop", "light",
    "dark", "balanced", "dank", "refreshing", "crisp", "fruit", "thick"};
    // KEY WORDS INCLUDE OTHER VERSIONS OF WORDS, LIKE "FRUITY", "HOPPY", "CITRUSY"
    // TODO: Use Hashmap to map these keys to a value, which we increment every time the
    //  word is spotted in a review.

    /*
    SOURCE USED TO BYPASS SSL RESTRICTION
    https://nanashi07.blogspot.com/2014/06/enable-ssl-connection-for-jsoup.html

    THANK YOU SO MUCH!!!
     */
    public static void enableSSLSocket() throws KeyManagementException, NoSuchAlgorithmException {
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });

        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, new X509TrustManager[]{new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        }}, new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
    }

    public void parseReview(beer b, String text){
        text = text.toLowerCase();
        text = text.replaceAll("[,.:;]", "");
        List<String> words = Arrays.asList(keywords);
        String[] allWords = text.split(" ");
        for(int i = 0; i < allWords.length; i++){
            String word = allWords[i];
            if(word.equals("bitterness")){
                word = "bitter";
            }else if(word.equals("smoothness")){
                word = "smooth";
            }else if(word.equals("citrusy") || word.equals("grapefruit") || word.equals("lemon") ||
                    word.equals("tangerine") || word.equals("lime")){
                word = "citrus";
            }else if(word.equals("hoppy") || word.equals("hops")){
                word = "hop";
            }else if(word.equals("lightness")){
                word = "light";
            }else if(word.equals("balance")){
                word = "balanced";
            }else if(word.equals("dankness")){
                word = "dank";
            }else if(word.equals("crispness") || word.equals("crispiness")){
                word = "crisp";
            }else if(word.equals("fruity") || word.equals("mango") || word.equals("raspberry") ||
                    word.equals("melon") || word.equals("cherry") || word.equals("berry") ||
                    word.equals("strawberry") || word.equals("peach") || word.equals("mango") ||
                    word.equals("nectarine")){
                word = "fruit";
            }else if(word.equals("thickness")){
                word = "thick";
            }
            if(words.contains(word)){
                b.increment(word);
            }
        }
    }


    public ArrayList<beer> getBeers() throws Exception{ //returns list of college names

        ArrayList<beer> beers = new ArrayList<beer>();

        Document doc = Jsoup.connect("https://www.beeradvocate.com/beer/top-rated/").get();

        Element table = doc.select("table").get(0);

        Elements rows = table.select("tr");

        for(int i = 1; i < 251; i++){
            Element elt = rows.get(i).select("a").get(0);
            String link = elt.attr("abs:href");
            String name = elt.select("b").get(0).text();
            beers.add(new beer(name, link));
        }
        return beers;
    }

    public void getInfo(beer b) throws Exception {

        enableSSLSocket();

        Document doc = Jsoup.connect(b.getLink()).get();

        Element info_box = doc.getElementById("info_box");
        Element beerstats = info_box.select("dl").get(0);

        String before = beerstats.select("dd").get(4).text();
        before = before.replaceAll(",", "");
        int reviewNum = Integer.parseInt(before);

        Element breweryElement = beerstats.select("dd").get(6);
        b.setBrewery(breweryElement.select("a").get(0).text());

        Element locationElement = beerstats.select("dd").get(7);
        b.setLocation(locationElement.select("a").get(0).text());

        Element reviewBox = doc.getElementById("rating_fullview");

        Elements reviews = reviewBox.getElementsByClass("user-comment");

        if(reviewNum >= 25) {
            reviewNum = 25;
        }

        for(int i = 0; i < reviewNum; i++) {
            Element review = reviews.get(i);
            String reviewText = review.getElementById("rating_fullview_content_2").text();
            parseReview(b, reviewText);
        }
    }

    public static void main(String args[]) throws Exception{

        beeradvocate fetch = new beeradvocate();
        ArrayList<beer> beers = fetch.getBeers();

        for(int i = 0; i < beers.size(); i++) {
            fetch.getInfo(beers.get(i));
            System.out.println(beers.get(i).getName());
            Thread.sleep(3000);
        }

        System.out.println("Fetched beers");

        /**
         * LINKS USED FOR HELP WITH MYSQL CONNECTION
         * https://stackoverflow.com/questions/4436146/how-to-connect-to-local-host-using-jdbc
         * https://www.tutorialspoint.com/jdbc/jdbc-insert-records.htm
         */

        String driverName = "com.mysql.cj.jdbc.Driver";
        Class.forName(driverName);

        System.out.println("Connecting to a selected database...");

        String serverName = "localhost";
        String mydatabase = "beers";
        String url = "jdbc:mysql://" + serverName + "/" + mydatabase;

        String username = "root";
        String password = "C7H34002KTVF74";
        Connection connection = DriverManager.getConnection(url, username, password);

        System.out.println("Connected database successfully...");

        System.out.println("Inserting data into table...");

        Statement insert = connection.createStatement();

        for(int i = 0; i < beers.size(); i++) {
            String myStatement = "INSERT INTO attributes(name,link,brewery,location,bitter," +
                    "smooth,citrus,tropical,hop,light,dark,balanced,dank,refreshing," +
                    "crisp,fruit,thick) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement statement = connection.prepareStatement(myStatement);
            statement.setString(1, beers.get(i).getName());
            statement.setString(2, beers.get(i).getLink());
            statement.setString(3, beers.get(i).getBrewery());
            statement.setString(4, beers.get(i).getLocation());
            for(int j = 0; j < keywords.length; j++) {
                statement.setInt(5 + j, beers.get(i).getKeyValue(keywords[j]));
            }
            statement.executeUpdate();
        }

        System.out.println("Successfully inserted data into table...");

    }
}
