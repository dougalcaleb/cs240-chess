package dataaccess.struct;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dataaccess.DatabaseManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class BaseSQLDAO {
    protected Gson serializer;
    protected Gson gameSerializer;
    PreparedStatement activeStatement;

    protected BaseSQLDAO()
    {
        serializer = new Gson();
        gameSerializer = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    }

    protected String serialize(Object data)
    {
        return serializer.toJson(data);
    }

    protected String serializeGame(ChessGame game)
    {
        return gameSerializer.toJson(game);
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
