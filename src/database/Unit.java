package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Unit {
    public static ArrayList<String> getUnit() throws SQLException {
        ArrayList<String> units = new ArrayList<>();
        ResultSet rs;
        String result;
        String query = "select * from unit;";
        rs = Database.exeQuery(query);
        while (rs.next()) {
            result = rs.getInt("id") + ";" + rs.getInt("type_id") + ";" +
                    rs.getInt("color_id");
            units.add(result);
        }
        return units;
    }
}    
