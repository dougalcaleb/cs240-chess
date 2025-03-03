package dataaccess.mem;

import dataaccess.struct.GameDAO;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MemGameDAO implements GameDAO {

    private static final Map<Integer, GameData> db = new HashMap<>();

    public Collection<GameData> getAllAsList()
    {
        return MemGameDAO.db.values();
    }

    public int setGame(GameData data)
    {
        data.gameID = MemGameDAO.db.size();
        MemGameDAO.db.put(data.gameID, data);

        return data.gameID;
    }

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
}
