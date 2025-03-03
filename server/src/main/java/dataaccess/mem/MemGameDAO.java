package dataaccess.mem;

import dataaccess.struct.GameDAO;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MemGameDAO implements GameDAO {

    private static final Map<Integer, GameData> db = new HashMap<>();

    @Override
    public Collection<GameData> getAllAsList()
    {
        return MemGameDAO.db.values();
    }

    @Override
    public int setGame(GameData data)
    {
        data.gameID = MemGameDAO.db.size();
        MemGameDAO.db.put(data.gameID, data);

        return data.gameID;
    }

    @Override
    public boolean gameExists(String gameName)
    {
        for (GameData game : MemGameDAO.db.values())
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
        for (Integer existingID : MemGameDAO.db.keySet())
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
        return MemGameDAO.db.get(gameID);
    }

    @Override
    public void setPlayerColor(int gameID, String username, String color)
    {
        GameData existing = MemGameDAO.db.get(gameID);

        if (color.equals("BLACK"))
        {
            existing.blackUsername = username;
        }
        else
        {
            existing.whiteUsername = username;
        }

        // probably don't need this, but I don't want to test it and have it not work
        MemGameDAO.db.put(gameID, existing);
    }

    public void reset()
    {
        MemGameDAO.db.clear();
    }
}
