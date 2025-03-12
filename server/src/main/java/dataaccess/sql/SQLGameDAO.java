package dataaccess.sql;

import dataaccess.struct.BaseSQLDAO;
import dataaccess.struct.GameDAO;
import model.GameData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class SQLGameDAO extends BaseSQLDAO implements GameDAO {
    @Override
    public void setDB(Map<Integer, GameData> value) {
        for (var entry : value.entrySet())
        {
            String gameData = entry.getValue().game == null ? "null" : "'"+serialize(entry.getValue().game)+"'";
            String bName = entry.getValue().blackUsername == null ? "null" : "'"+entry.getValue().blackUsername+"'";
            String wName = entry.getValue().whiteUsername == null ? "null" : "'"+entry.getValue().whiteUsername+"'";
            String values = "'"+entry.getValue().gameName+"', "+gameData+", "+bName+", "+wName;
            executeSQL("INSERT INTO games (name, data, blackUser, whiteUser) VALUES ("+values+");");
        }
    }

    @Override
    public Collection<GameData> getAllAsList() {
        ArrayList<GameData> data = new ArrayList<>();

        try (ResultSet entries = executeSQLQuery("SELECT * FROM games LIMIT 0, 1000;")) {
            while (entries.next())
            {
                data.add(new GameData(
                    entries.getInt(1),
                    entries.getString(5),
                    entries.getString(4),
                    entries.getString(2)
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return data;
    }

    @Override
    public int setGame(GameData data) {
        String gameData = data.game == null ? "null" : "\""+serialize(data.game)+"\"";
        String bName = data.blackUsername == null ? "null" : "\""+data.blackUsername+"\"";
        String wName = data.whiteUsername == null ? "null" : "\""+data.whiteUsername+"\"";
        String values = "\""+data.gameName+"\", "+gameData+", "+bName+", "+wName;
        ResultSet entries = executeSQLGetKeys("INSERT INTO games (name, data, blackUser, whiteUser) VALUES ("+values+");");
        try {
            entries.next();
            return entries.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean gameExists(String gameName) {
        try (ResultSet entries = executeSQLQuery("SELECT * FROM games WHERE name=\"" + gameName + "\";")) {
            if (entries.next())
            {
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    @Override
    public boolean gameExists(int gameID) {
        try (ResultSet entries = executeSQLQuery("SELECT * FROM games WHERE id='" + gameID + "';")) {
            if (entries.next())
            {
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    @Override
    public GameData getGame(int gameID) {
        try (ResultSet entries = executeSQLQuery("SELECT * FROM games WHERE id=" + gameID + ";")) {
            if (entries.next())
            {
                return new GameData(
                    entries.getInt(1),
                    entries.getString(5),
                    entries.getString(4),
                    entries.getString(2)
                );
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setPlayerColor(int gameID, String username, String color) {
        String colName = color.equals("BLACK") ? "blackUser" : "whiteUser";
        executeSQL("UPDATE games SET "+colName+"='"+username+"' WHERE id="+gameID+";");
    }

    @Override
    public void reset() {
        executeSQL("TRUNCATE TABLE games");
    }
}
