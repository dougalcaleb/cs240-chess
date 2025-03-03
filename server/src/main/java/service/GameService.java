package service;

import exceptions.DoesNotExistException;
import exceptions.GameTakenException;
import model.GameData;

import java.util.Collection;

public class GameService extends BaseService {

    public int createGame(String name) throws GameTakenException
    {
        if (gameAccess.gameExists(name))
        {
            throw new GameTakenException("Game with this name already exists");
        }

        return gameAccess.setGame(new GameData(name));
    }

    public void joinGame(int gameID, String username, String requestedColor) throws GameTakenException, RuntimeException, DoesNotExistException
    {
        if (!gameAccess.gameExists(gameID))
        {
            throw new DoesNotExistException("Game does not exist");
        }

        GameData existingGame = gameAccess.getGame(gameID);

        if (existingGame == null)
        {
            throw new RuntimeException("Error getting game");
        }

        if (
            (requestedColor.equals("BLACK") && existingGame.whiteUsername != null && existingGame.whiteUsername.equals(username)) ||
            (requestedColor.equals("WHITE") && existingGame.blackUsername != null && existingGame.blackUsername.equals(username))
        ) {
            throw new RuntimeException("Already joined game");
        }

        if (
            ((existingGame.blackUsername == null || existingGame.blackUsername.isEmpty()) && requestedColor.equals("BLACK")) ||
            ((existingGame.whiteUsername == null || existingGame.whiteUsername.isEmpty()) && requestedColor.equals("WHITE"))
        ) {
            gameAccess.setPlayerColor(gameID, username, requestedColor);
        }
        else {
            throw new GameTakenException("Color already taken");
        }
    }

    public Collection<GameData> getAll()
    {
        return gameAccess.getAllAsList();
    }

    public void reset()
    {
        gameAccess.reset();
    }
}
