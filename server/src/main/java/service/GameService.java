package service;

import model.GameData;

import java.util.Collection;

public class GameService extends BaseService {

    public int createGame(String name) throws RuntimeException
    {
        if (gameAccess.gameExists(name))
        {
            throw new RuntimeException("Game with this name already exists");
        }

        return gameAccess.setGame(new GameData(name));
    }

    public Collection<GameData> getAll()
    {
        return gameAccess.getAllAsList();
    }
}
