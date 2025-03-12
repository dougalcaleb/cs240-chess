package dataaccess.struct;

import com.google.gson.Gson;
import dataaccess.DatabaseManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public abstract class BaseSQLDAO {
    protected Gson serializer;

    protected BaseSQLDAO()
    {
        serializer = new Gson();
    }

    protected <T> Object deseralize(String data, Class<T> returnClass) {
        return serializer.fromJson(data, returnClass);
    }

    protected String serialize(Object data)
    {
        return serializer.toJson(data);
    }

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

    protected ResultSet[] executeSQLQueryGetKeys(String statement)
    {
        try {
            try (PreparedStatement sqlStatement = DatabaseManager.getConnection().prepareStatement(statement))
            {
                ResultSet[] results = new ResultSet[2];
                results[0] = sqlStatement.executeQuery();;
                results[1] = sqlStatement.getGeneratedKeys();
                return results;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
