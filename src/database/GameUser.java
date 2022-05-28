package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
public class GameUser {
    public static void registration (String login, String password) {
        String query = "insert into game_user values (nextval('game_user_id_seq'), '" + login + "', '" + password + "', 0, 0);";
        Database.exeUpdate(query);
    }

    public static int login (String login, String password) {
        try {
            String query = "select * from game_user where login = '" + login + "';";
            ResultSet rs = Database.exeQuery(query);
            if (rs.next()) {
                if (password.equals(rs.getString("password"))) {
                    return Const.SUCCESS_LOGIN;
                }
                return Const.WRONG_PASSWORD;
            }
            return Const.INVALID_LOGIN;
        } catch (Exception e) {
            e.printStackTrace();
            return Const.INVALID_LOGIN;
        }
    }
}
