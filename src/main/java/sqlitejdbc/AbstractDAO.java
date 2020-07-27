package main.java.sqlitejdbc;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class AbstractDAO {
    protected Connection connection;
    public AbstractDAO(Connection connection) {
        this.connection = connection;
    }

    public abstract void runnerToQuery(String query);

    public void closeConnection() {
        if(connection != null) {
            try
            {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
}
