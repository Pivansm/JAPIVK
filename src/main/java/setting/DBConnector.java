package main.java.setting;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnector {
    private Connection connection;
    private String url;
    private String driver;
    private String userName;
    private String userPassword;

    public DBConnector() {

    }

    public DBConnector(String driver, String inUrl, String user, String pass) throws ClassNotFoundException, SQLException {
        this.url = inUrl;
        this.userName = user;
        this.userPassword = pass;
        this.driver = driver;
        if(userName != null) {
            Class.forName(this.driver);
            connection = DriverManager.getConnection(url, userName, userPassword);
        }
        else {
            Class.forName(this.driver);
            connection = DriverManager.getConnection(url);
        }
    }

    public Connection connect() {
        try
        {
            if(userName != null) {
                Class.forName(this.driver);
                connection = DriverManager.getConnection(url, userName, userPassword);
            }
            else {
                Class.forName(this.driver);
                connection = DriverManager.getConnection(url);
            }
            return connection;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public Connection getConnection() throws SQLException {
        if(connection != null && !connection.isClosed()) {
            return connection;
        }
        connect();
        return connection;
    }
}
