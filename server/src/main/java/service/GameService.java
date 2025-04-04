package service;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.InvalidMoveException;
import exceptions.DoesNotExistException;
import exceptions.GameTakenException;
import sharedmodel.GameData;
import sharedmodel.MoveMadeResult;

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

    public MoveMadeResult makeMove(String userToken, int gameID, ChessMove move)
    {
        GameData gameData = gameAccess.getGame(gameID);
        gameData.game.setup();
        ChessPiece pieceAffected = null;

        String username = BaseService.authAccess.getUsernameByToken(userToken);
        ChessGame.TeamColor pieceColor = null;

        if (gameData.game.getBoard().getPiece(move.getStartPosition()) == null)
        {
            throw new RuntimeException("Invalid move: no piece at position " + move.getStartPosition());
        }
        else
        {
            pieceColor = gameData.game.getBoard().getPiece(move.getStartPosition()).getTeamColor();
        }

        if (
            (gameData.blackUsername.equals(username) && pieceColor != ChessGame.TeamColor.BLACK) ||
            (gameData.whiteUsername.equals(username) && pieceColor != ChessGame.TeamColor.WHITE)
        ) {
            throw new RuntimeException("Invalid move: cannot move opponent's piece");
        }

        if (!username.equals(gameData.blackUsername) && !username.equals(gameData.whiteUsername))
        {
            throw new RuntimeException("Cannot move pieces as an observer");
        }

        try {
            pieceAffected = gameData.game.makeMove(move);
        } catch (InvalidMoveException e) {
            throw new RuntimeException(e.getMessage());
        }

        gameAccess.updateGame(gameData);

        boolean didCheck = gameData.game.isInCheck(ChessGame.TeamColor.WHITE) ||
                gameData.game.isInCheck(ChessGame.TeamColor.BLACK);
        boolean didCheckmate = gameData.game.isInCheckmate(ChessGame.TeamColor.WHITE) ||
                gameData.game.isInCheckmate(ChessGame.TeamColor.BLACK);

        return new MoveMadeResult(pieceAffected, didCheck, didCheckmate);
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
