package service;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.InvalidMoveException;
import exceptions.DoesNotExistException;
import exceptions.GameTakenException;
import sharedmodel.GameData;

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
            ((existingGame.blackUsername == null || existingGame.blackUsername.isEmpty()) && requestedColor.equals("BLACK")) ||
            ((existingGame.whiteUsername == null || existingGame.whiteUsername.isEmpty()) && requestedColor.equals("WHITE"))
        ) {
            gameAccess.setPlayerColor(gameID, username, requestedColor);
        }
        else {
            throw new GameTakenException("Color already taken");
        }
    }

    public void leaveGame(int gameID, String username, ChessGame.TeamColor color) throws RuntimeException, DoesNotExistException
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

        gameAccess.unsetPlayerColor(gameID, username, color.name());
    }

    public ChessPiece makeMove(int gameID, ChessMove move)
    {
        GameData gameData = gameAccess.getGame(gameID);
        gameData.game.setup();
        ChessPiece pieceAffected = null;
        try {
            pieceAffected = gameData.game.makeMove(move);
        } catch (InvalidMoveException e) {
            throw new RuntimeException(e);
        }

        gameAccess.updateGame(gameData);

        return pieceAffected;
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
