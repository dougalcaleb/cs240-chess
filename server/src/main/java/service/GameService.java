package service;

import model.GameData;

import java.util.Collection;

public class GameService extends BaseService {

    public Collection<GameData> getAll()
    {
        return gameAccess.getAllAsList();
    }
}
