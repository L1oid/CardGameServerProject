package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Card {
    public static ArrayList<String> getCard() throws SQLException {
        ArrayList<String> cards = new ArrayList<>();
        ResultSet rs;
        String result;
        String query = "select * from card;";
        rs = Database.exeQuery(query);
        while (rs.next()) {
            result = rs.getInt("id") + ";" + rs.getString("name") + ";" +
                    rs.getString("description") + ";" + rs.getInt("color_id") + ";" +
                    rs.getInt("price") + ";" + rs.getInt("action_id") + ";" +
                    rs.getInt("action_type");
            cards.add(result);
        }
        return cards;
    }
}
