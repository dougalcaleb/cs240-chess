package dataaccess;

import exceptions.DataAccessException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Scanner;

public class DatabaseManager {
    private static final String DATABASE_NAME;
    private static final String USER;
    private static final String PASSWORD;
    private static final String CONNECTION_URL;

    private static final String databaseSetupFile = "db-init.txt";
    private static final String databaseSetupFileDropOld = "db-init-drop.txt";
    private static final String sqlFileDelim = ";";

    /*
     * Load the database information for the db.properties file.
     */
    static {
        try {
            try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
                if (propStream == null) {
                    throw new Exception("Unable to load db.properties");
                }
                Properties props = new Properties();
                props.load(propStream);
//                DATABASE_NAME = props.getProperty("db.name");
                DATABASE_NAME = "chess";
                USER = props.getProperty("db.user");
                PASSWORD = props.getProperty("db.password");

                var host = props.getProperty("db.host");
                var port = Integer.parseInt(props.getProperty("db.port"));
                CONNECTION_URL = String.format("jdbc:mysql://%s:%d", host, port);
            }
        } catch (Exception ex) {
            throw new RuntimeException("unable to process db.properties. " + ex.getMessage());
        }
    }

    /**
     * Creates the database if it does not already exist.
     */
    public static void createDatabase() throws DataAccessException {
        try {
            var statement = "CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME;
            var conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * Create a connection to the database and sets the catalog based upon the
     * properties specified in db.properties. Connections to the database should
     * be short-lived, and you must close the connection when you are done with it.
     * The easiest way to do that is with a try-with-resource block.
     * <br/>
     * <code>
     * try (var conn = DbInfo.getConnection(databaseName)) {
     * // execute SQL statements.
     * }
     * </code>
     */
    public static Connection getConnection(boolean dbExists) throws DataAccessException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            var conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
            if (dbExists)
            {
                conn.setCatalog(DATABASE_NAME);
            }
            return conn;
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public static Connection getConnection() throws DataAccessException
    {
        return getConnection(true);
    }

    public static void initDatabase(boolean dropExisting) throws RuntimeException
    {
        try (var conn = getConnection(false))
        {
            if (dropExisting)
            {
                executeSqlFile(conn, databaseSetupFileDropOld);
            }
            else
            {
                executeSqlFile(conn, databaseSetupFile);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void executeSqlFile(Connection db, String filePath) throws RuntimeException
    {
        try (Scanner scanner = new Scanner(Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath)))
        {
            scanner.useDelimiter(sqlFileDelim);

            while (scanner.hasNext())
            {
                String statement = scanner.next().trim();
                try {
                    try (PreparedStatement sqlStatement = db.prepareStatement(statement))
                    {
                        sqlStatement.executeUpdate();
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
