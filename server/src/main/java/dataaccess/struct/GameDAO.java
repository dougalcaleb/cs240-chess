package dataaccess.struct;

import model.GameData;

import java.util.Collection;

public interface GameDAO {

    public Collection<GameData> getAllAsList();

    public int setGame(GameData data);

    public boolean gameExists(String gameName);

    public boolean gameExists(int gameID);

    public GameData getGame(int gameID);

    public void setPlayerColor(int gameID, String username, String color);

    public void reset();
}
