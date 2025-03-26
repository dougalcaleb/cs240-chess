package dataaccess;

import exceptions.DataAccessException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.Scanner;

public class DatabaseManager {
    private static final String DATABASE_NAME;
    private static final String USER;
    private static final String PASSWORD;
    private static final String CONNECTION_URL;

    private static final String DATABASE_SETUP_FILE = "db-init.txt";
    private static final String DATABASE_SETUP_FILE_DROP_OLD = "db-init-drop.txt";
    private static final String SQL_FILE_DELIM = ";";

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
                DATABASE_NAME = props.getProperty("db.name");
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
            var conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
            var statement = "CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME;
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
            executeServerSetupArr(getConnection());
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

    public static void initDatabase(boolean dropExisting, boolean useExternalFile) throws RuntimeException
    {
        if (useExternalFile)
        {
            try (var conn = getConnection(false))
            {
                if (dropExisting)
                {
                    executeSqlFile(conn, DATABASE_SETUP_FILE_DROP_OLD);
                }
                else
                {
                    executeSqlFile(conn, DATABASE_SETUP_FILE);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        else
        {
            try {
                createDatabase();
            } catch (DataAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void executeServerSetupArr(Connection db)
    {
        ArrayList<String> statements = new ArrayList<>(Arrays.asList(
                """
                CREATE TABLE IF NOT EXISTS users (
                    `username` VARCHAR(256) NOT NULL,
                    `password` VARCHAR(256) NOT NULL,
                    `email` VARCHAR(256) DEFAULT NULL,
                    PRIMARY KEY (`username`)
                ) CHARSET=utf8mb4;
                """,
                """
                CREATE TABLE IF NOT EXISTS auth (
                    `username` VARCHAR(256) NOT NULL,
                    `token` VARCHAR(256) NOT NULL,
                    INDEX (`username`)
                ) CHARSET=utf8mb4;
                """,
                """
                CREATE TABLE IF NOT EXISTS games (
                    `id` INT NOT NULL AUTO_INCREMENT,
                    `name` VARCHAR(256) DEFAULT NULL,
                    `data` TEXT DEFAULT NULL,
                    `blackUser` VARCHAR(256) DEFAULT NULL,
                    `whiteUser` VARCHAR(256) DEFAULT NULL,
                    PRIMARY KEY (`id`)
                ) CHARSET=utf8mb4;
                """
        ));

        for (String st : statements)
        {
            try {
                try (PreparedStatement sqlStatement = db.prepareStatement(st))
                {
                    sqlStatement.executeUpdate();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void executeSqlFile(Connection db, String filePath) throws RuntimeException
    {
        try (Scanner scanner = new Scanner(Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath)))
        {
            scanner.useDelimiter(SQL_FILE_DELIM);

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
