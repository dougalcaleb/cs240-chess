package dataaccess.struct;

import sharedmodel.GameData;

import java.util.Collection;
import java.util.Map;

public interface GameDAO {

    public void setDB(Map<Integer, GameData> value);

    public Collection<GameData> getAllAsList();

    public int setGame(GameData data);

    public boolean gameExists(String gameName);

    public boolean gameExists(int gameID);

    public GameData getGame(int gameID);

    public void setPlayerColor(int gameID, String username, String color);

    public void unsetPlayerColor(int gameID, String username, String color);

    public void updateGame(GameData data);

    public void reset();
}
