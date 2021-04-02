import java.sql.*;
import java.util.*;

public class algorithm {

    public static String[] keywords = {"bitter", "smooth", "citrus", "tropical", "hop", "light",
            "dark", "balanced", "dank", "refreshing", "crisp", "fruit", "thick"};

    public int[] input;

    public class beerKey{

        public int key;
        public double total;

        public beerKey(int k, double t){
            key = k;
            total = t;
        }

        public int getKey(){
            return key;
        }

        public double getTotal(){
            return total;
        }

    }

    public algorithm(int[] keys){
        input = keys;
    }

    public beerKey findMax(ArrayList<beerKey> beers) {
        beerKey max = beers.get(0);
        for(int i = 1; i < beers.size(); i++){
            if(beers.get(i).getTotal() > max.getTotal()){
                max = beers.get(i);
            }
        }
        return max;
    }

    public int[] find() throws Exception{

        ArrayList<beerKey> beers = new ArrayList<beerKey>();

        int[] keys = new int[5];

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

        /**
         * GO THROUGH EVERY BEER AND ADD UP ALL THE NUMBERS OF THE KEY VALUES
         * ADD THESE VALUES TO AN ARRAYLIST
         * SORT THE ARRAYLIST
         * GET THE TOP 5 HIGHEST NUMBER TOTALS AND RETURN THOSE BEERS
         */

        Statement stmt = connection.createStatement();

        String sql = "SELECT id";

        for(int i = 0; i < keywords.length; i++){
            sql += ", " + keywords[i];
        }
        sql += " FROM attributes";

        ResultSet rs = stmt.executeQuery(sql);

        while(rs.next()){
            int key = rs.getInt("id");
            double total = 0;
            for(int i = 0; i < keywords.length; i++){
                total += rs.getInt(keywords[i]) * input[i];
            }
            beers.add(new beerKey(key, total));
        }

        for(int i = 0; i < keys.length; i++){
            keys[i] = findMax(beers).getKey();
            beers.remove(findMax(beers));
        }

        return keys;
    }

    /**
     *
     * BELOW IS BASICALLY EXCLUSIVELY FOR TESTING ALGORITHM
     *
    public static void main(String[] args) throws Exception {

        int[] userinput = {23, 0, 0, 45, 10, 33, 0, 50, 45, 80, 0, 0, 25};

        algorithm test1 = new algorithm(userinput);

        int[] keys = test1.find();

        for(int i = 0; i < keys.length; i++){
            System.out.print(keys[i] + " ");
        }

    }
    */
}
