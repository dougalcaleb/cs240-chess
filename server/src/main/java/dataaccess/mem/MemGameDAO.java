package dataaccess.mem;

import dataaccess.struct.GameDAO;
import model.GameData;
import model.UserData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MemGameDAO implements GameDAO {

    private static final Map<Integer, GameData> DB = new HashMap<>();

    // exclusively for testing purposes
    public void setDB(Map<Integer, GameData> value)
    {
        MemGameDAO.DB.clear();
        MemGameDAO.DB.putAll(value);
    }

    @Override
    public Collection<GameData> getAllAsList()
    {
        return MemGameDAO.DB.values();
    }

    @Override
    public int setGame(GameData data)
    {
        data.gameID = MemGameDAO.DB.size() + 1;
        MemGameDAO.DB.put(data.gameID, data);

        return data.gameID;
    }

    @Override
    public boolean gameExists(String gameName)
    {
        for (GameData game : MemGameDAO.DB.values())
        {
            if (game.gameName.equals(gameName))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean gameExists(int gameID)
    {
        for (Integer existingID : MemGameDAO.DB.keySet())
        {
            if (existingID.equals(gameID))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public GameData getGame(int gameID)
    {
        return MemGameDAO.DB.get(gameID);
    }

    @Override
    public void setPlayerColor(int gameID, String username, String color)
    {
        GameData existing = MemGameDAO.DB.get(gameID);

        if (color.equals("BLACK"))
        {
            existing.blackUsername = username;
        }
        else
        {
            existing.whiteUsername = username;
        }

        // probably don't need this, but I don't want to test it and have it not work
        MemGameDAO.DB.put(gameID, existing);
    }

    public void reset()
    {
        MemGameDAO.DB.clear();
    }
}
