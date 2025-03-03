package dataaccess.struct;

import model.GameData;

import java.util.Collection;

public interface GameDAO {

    public Collection<GameData> getAllAsList();

    public int setGame(GameData data);

    public boolean gameExists(String gameName);
}
