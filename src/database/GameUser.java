package database;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GameUser {
    public static int registration (String login, String password) {
        try {
            String query = "select * from game_user where login = '" + login + "';";
            ResultSet rs = Database.exeQuery(query);
            if (rs.next()) {
                return Const.LOGIN_DENIED;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Const.LOGIN_DENIED;
        }
        String query = "insert into game_user values (nextval('game_user_id_seq'), '" + login + "', '" + password + "', 0, 0);";
        Database.exeUpdate(query);
        return Const.SUCCESS_REGISTRATION;
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

    public static String getWinsAndLoses(String login) throws SQLException {
        ResultSet rs;
        String result;
        String query = "select wins, loses from game_user where login = '" + login + "';";
        rs = Database.exeQuery(query);
        rs.next();
        result = rs.getInt("wins") + ";" + rs.getInt("loses");
        return result;
    }
}
