package database;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Card {
    int id;
    String name;
    String description;
    int color_id;
    int price;
    int action_id;

    public Card(int id_p, String name_p, String description_p, int color_id_p, int price_p, int action_id_p) {
        id = id_p;
        name = name_p;
        description = description_p;
        color_id = color_id_p;
        price = price_p;
        action_id = action_id_p;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getColorId() {
        return color_id;
    }

    public int getPrice() {
        return price;
    }

    public int getActionId() {
        return action_id;
    }

    public void setId(int id_p) {
        id = id_p;
    }

    public void setName(String name_p) {
        name = name_p;
    }

    public void setDescription(String description_p) {
        description = description_p;
    }

    public void setColorId(int color_id_p) {
        color_id = color_id_p;
    }

    public void setPrice(int price_p) {
        price = price_p;
    }

    public void setActionId(int action_id_p) {
        action_id = action_id_p;
    }

    public static Card getCard(String id_p) throws SQLException {
        ResultSet rs;
        String query = "select * from card where id = " + id_p + ";";
        rs = Database.exeQuery(query);
        return new Card(rs.getInt("id"), rs.getString("name"),
                        rs.getString("description"), rs.getInt("color_id"),
                        rs.getInt("price"), rs.getInt("action_id"));
    }

}
