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
        throw new RuntimeException("Not supported in SQL database mode.");
    }

    @Override
    public Collection<GameData> getAllAsList() {
        ArrayList<GameData> data = new ArrayList<>();

        try (ResultSet entries = executeSQLQuery("SELECT * FROM games LIMIT 0, 1000;")) {
            while (entries.next())
            {
                data.add(new GameData(
                    entries.getInt(0),
                    entries.getString(4),
                    entries.getString(3),
                    entries.getString(1)
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return data;
    }

    @Override
    public int setGame(GameData data) {
        String values = data.gameName+", "+serialize(data.game)+", "+data.blackUsername+", "+data.whiteUsername;
        ResultSet[] results = executeSQLQueryGetKeys("INSERT INTO games (name, data, blackUser, whiteUser) VALUES ("+values+");");
        try {
            results[1].next();
            return results[1].getInt(0);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean gameExists(String gameName) {
        try (ResultSet entries = executeSQLQuery("SELECT * FROM games WHERE name='" + gameName + "';")) {
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
        try (ResultSet entries = executeSQLQuery("SELECT * FROM games WHERE name='" + gameID + "';")) {
            entries.next();

            return new GameData(
                entries.getInt(0),
                entries.getString(4),
                entries.getString(3),
                entries.getString(1)
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setPlayerColor(int gameID, String username, String color) {
        String colName = color.equals("BLACK") ? "blackUser" : "whiteUser";
        executeSQL("UPDATE games SET "+colName+"="+username+" WHERE id="+gameID+";");
    }

    @Override
    public void reset() {
        executeSQL("TRUNCATE TABLE games");
    }
}
