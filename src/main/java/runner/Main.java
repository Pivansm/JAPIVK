package main.java.runner;

import main.java.setting.DBConnector;
import main.java.sqlitejdbc.SQLiteDAO;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Main {
    public static final Logger LOGGER;

    static {
        LOGGER = Logger.getLogger("VK API Groups");
        try {
            File logDir = new File("./logs/");
            if(!logDir.exists())
                logDir.mkdir();
            String logPattern = String.format("%s%clog%s.log", logDir.getAbsolutePath(), File.separatorChar, LocalDate.now());
            FileHandler fileHandler = new FileHandler(logPattern, true);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
            LOGGER.addHandler(fileHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static SQLiteDAO sqLiteDAO;
    private static DBConnector connectorSqlite;
    private static final String CONN_IRL = "jdbc:sqlite:dbsqlrb.sqlite";
    private static final String CONN_DRIVER = "org.sqlite.JDBC";

    public static void main(String[] args) {
        System.out.println("========Start========");
        File currSqlite = new File("dbsqlrb.sqlite");
        if(!currSqlite.exists()) {
            System.out.println("Нет БД dbsqlrb.sqlite");
            try
            {
                connectorSqlite = new DBConnector(CONN_DRIVER, CONN_IRL, null, null);
                sqLiteDAO = new SQLiteDAO(connectorSqlite.getConnection());
                sqLiteDAO.createTable();
                sqLiteDAO.closeConnection();

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        else {
            System.out.println("БД dbsqlrb.sqlite OK!");
        }


        try {

            MainLaunch mainLaunch = new MainLaunch();
            //mainLaunch.postToFileTxt();
            mainLaunch.processingFoundation();


        } catch (Exception e) {
            e.printStackTrace();
        }

     }

}
