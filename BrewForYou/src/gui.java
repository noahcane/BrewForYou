import javax.swing.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;

/**
 * JAVA SWING HELP FOUND HERE
 * https://www.javatpoint.com/java-swing
 */
public class gui {

    public static String[] keywords = {"bitter", "smooth", "citrus", "tropical", "hop", "light",
            "dark", "balanced", "dank", "refreshing", "crisp", "fruit", "thick"};

    public ArrayList<beer> action(JSlider[] sliders) throws Exception{
        int[] values = new int[keywords.length];
        for(int i = 0; i < values.length; i++){
            values[i] = sliders[i].getValue();
        }
        algorithm match = new algorithm(values);
        int[] keys = match.find();
        String driverName = "com.mysql.cj.jdbc.Driver";
        Class.forName(driverName);

        System.out.println("Connecting to a selected database...");

        String serverName = "localhost";
        String mydatabase = "beers";
        String url = "jdbc:mysql://" + serverName + "/" + mydatabase;

        String username = "root";
        String password = "C7H34002KTVF74";
        Connection connection= DriverManager.getConnection(url, username, password);

        System.out.println("Connected database successfully...");

        Statement stmt = connection.createStatement();

        String sql = "SELECT id, name, link, brewery, location FROM attributes";
        ResultSet rs = stmt.executeQuery(sql);

        ArrayList<beer> beers = new ArrayList<beer>();

        while(rs.next()){
            int id  = rs.getInt("id");
            if(keys[0] == id || keys[1] == id ||keys[2] == id ||keys[3] == id ||keys[4] == id){
                String name = rs.getString("name");
                String link = rs.getString("link");
                String brewery = rs.getString("brewery");
                String location = rs.getString("location");
                beer b = new beer(name, link);
                b.setBrewery(brewery);
                b.setLocation(location);
                beers.add(b);
            }
        }
        rs.close();
        return beers;
    }

    public static void main(String[] args) throws Exception{

        JFrame frame1 = new JFrame();

        JSlider[] sliders = new JSlider[keywords.length];

        for(int i = 0; i < sliders.length; i++){
            int difference = 40 * i;
            JLabel text = new JLabel(keywords[i]);
            sliders[i] = new JSlider(0, 100, 0);
            text.setBounds(25, 15 + difference, 100, 25);
            sliders[i].setBounds(115, 15 + difference, 250, 25);
            sliders[i].setMinorTickSpacing(10);
            sliders[i].setPaintTicks(true);
            frame1.add(text);
            frame1.add(sliders[i]);
        }

        JButton submit = new JButton("SUBMIT");
        submit.setBounds(50,575,85, 25);
        submit.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                gui t = new gui();
                ArrayList<beer> beers = new ArrayList<beer>();
                try {
                    beers = t.action(sliders);
                } catch (Exception exception) {
                    System.out.println("OIAJDWOI");
                    exception.printStackTrace();
                }
                frame1.setVisible(false);
                JFrame frame2 = new JFrame();
                JLabel[] labels = new JLabel[beers.size()];
                for(int i = 0; i < beers.size(); i++){
                    String text1 = "" + (i + 1) + ": " + beers.get(i).getName();
                    String text2 = "brewery: " + beers.get(i).getBrewery();
                    String text3 = "location: " + beers.get(i).getLocation();
                    String text4 = "link: " + beers.get(i).getLink();
                    String finalText = "<html>" + text1 + "<br>" + text2 + "<br>" + text3 + "<br>" + text4 + "<html>";
                    labels[i] = new JLabel(finalText);
                    labels[i].setBounds(25,25 + (i * 100), 350,75);
                    frame2.add(labels[i]);
                }
                frame2.setSize(400,600);
                frame2.setLayout(null);
                frame2.setVisible(true);
            }
        });

        frame1.add(submit);

        frame1.setSize(500,650);
        frame1.setLayout(null);
        frame1.setVisible(true);

    }
}
