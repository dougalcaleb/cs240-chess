package dataaccess.struct;

import dataaccess.DatabaseManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public abstract class BaseSQLDAO {
    protected void executeSQL(String statement)
    {
        try {
            try (PreparedStatement sqlStatement = DatabaseManager.getConnection().prepareStatement(statement)) {
                sqlStatement.executeUpdate();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected ResultSet executeSQLQuery(String statement)
    {
        try {
            try (PreparedStatement sqlStatement = DatabaseManager.getConnection().prepareStatement(statement))
            {
                return sqlStatement.executeQuery();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
