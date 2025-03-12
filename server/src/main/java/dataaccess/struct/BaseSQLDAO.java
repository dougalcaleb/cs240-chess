package dataaccess.struct;

import com.google.gson.Gson;
import dataaccess.DatabaseManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class BaseSQLDAO {
    protected Gson serializer;
    PreparedStatement activeStatement;

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
        closeStatement();
        try {
            activeStatement = DatabaseManager.getConnection().prepareStatement(statement);
            activeStatement.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected ResultSet executeSQLQuery(String statement)
    {
        closeStatement();
        try {
            activeStatement = DatabaseManager.getConnection().prepareStatement(statement);
            return activeStatement.executeQuery();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected ResultSet executeSqlQueryWithStrReplacement(String statement, String value)
    {
        closeStatement();
        try {
            activeStatement = DatabaseManager.getConnection().prepareStatement(statement);
            activeStatement.setString(1, value);
            return activeStatement.executeQuery();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    protected ResultSet executeSQLGetKeys(String statement)
    {
        closeStatement();
        try {
            activeStatement = DatabaseManager.getConnection().prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);
            activeStatement.executeUpdate();
            return activeStatement.getGeneratedKeys();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void closeStatement() {
        if (activeStatement != null) {
            try {
                activeStatement.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
