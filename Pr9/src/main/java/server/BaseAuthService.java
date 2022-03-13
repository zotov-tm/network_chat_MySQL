package server;

import javax.naming.Name;
import java.sql.*;

public class BaseAuthService {

    private static Connection connection;
    private static Statement statement;
    private static PreparedStatement preparedStatement;
    private static ResultSet rs;




    public static void connect() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:Base.db");
        statement = connection.createStatement();

    }

    public  String getNicknameByUsernameAndPassword(String l, String p) throws SQLException {
        rs = statement.executeQuery(String.format("SELECT Name FROM Users WHERE Login='%s' AND Pass='%s'", l, p));
        if (rs.next()) {
            return rs.getString("Name");
        }
        return null;

    }


    public  void addUserTable(String nick, String login, String pass) throws SQLException {
        preparedStatement = connection.prepareStatement("INSERT INTO Users (Name,login,Pass)\n" +
                "VALUES(?,?,?)");
        preparedStatement.setString(1,nick);
        preparedStatement.setString(2,login);
        preparedStatement.setString(3,pass);
        preparedStatement.addBatch();
        preparedStatement.executeBatch();
    }

    public static void disConnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
