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

    public void setGame(GameData data)
    {
        MemGameDAO.db.put(data.gameID, data);
    }
}
