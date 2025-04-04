package serverwebsocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.Gson;
import exceptions.DoesNotExistException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import server.Server;
import servermodel.GameSockets;
import service.BaseService;
import sharedmodel.GameData;
import sharedmodel.MoveMadeResult;
import websocket.commands.*;
import websocket.messages.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@WebSocket
public class ServerWebsocketHandler {

    private final HashMap<Integer, GameSockets> sessions = new HashMap<>();

    public ServerWebsocketHandler()
    {

    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message)
    {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        boolean authenticated = BaseService.authAccess.tokenExists(command.getAuthToken());
        if (!authenticated)
        {
            try {
                safeSend(session, command.getGameID(), new Gson().toJson(new ServerErrorMessage("Unauthorized")));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }

        try
        {
            switch (command.getCommandType())
            {
                case UserGameCommand.CommandType.CONNECT -> addPlayer(session, new Gson().fromJson(message, JoinGameCommand.class));
                case UserGameCommand.CommandType.OBSERVE -> addObserver(session, new Gson().fromJson(message, ObserveGameCommand.class));
                case UserGameCommand.CommandType.LEAVE -> removePlayer(session, new Gson().fromJson(message, LeaveGameCommand.class));
                case UserGameCommand.CommandType.STOP_OBSERVE -> removeObserver(session, new Gson().fromJson(message, StopObserveCommand.class));
                case UserGameCommand.CommandType.RESIGN -> resignPlayer(session, new Gson().fromJson(message, ResignGameCommand.class));
                case UserGameCommand.CommandType.MAKE_MOVE -> makeMove(session, new Gson().fromJson(message, MakeMoveCommand.class));
                case UserGameCommand.CommandType.SHOW_MOVES -> getLegalMoves(session, new Gson().fromJson(message, HighlightMovesCommand.class));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void addPlayer(Session session, JoinGameCommand data) throws IOException {
        if (data.username == null)
        {
            data.username = BaseService.authAccess.getUsernameByToken(data.getAuthToken());
        }

        GameData gameData = BaseService.gameAccess.getGame(data.getGameID());
        if (data.color == null)
        {
            if (gameData == null)
            {
                safeSend(session, data.getGameID(), new Gson().toJson(new ServerErrorMessage("Game does not exist")));
            }
            if (gameData.whiteUsername.equals(data.username))
            {
                data.color = ChessGame.TeamColor.WHITE;
            }
            else
            {
                data.color = ChessGame.TeamColor.BLACK;
            }
        }

        if (
            gameData.whiteUsername != null && gameData.blackUsername != null &&
            !data.username.equals(gameData.whiteUsername) && !data.username.equals(gameData.blackUsername)
        )
        {
            addObserver(session, new ObserveGameCommand(data.getAuthToken(), data.getGameID(), data.username));
            return;
        }

        if (!sessions.containsKey(data.getGameID()))
        {
            sessions.put(data.getGameID(), new GameSockets());
        }

        sessions.get(data.getGameID()).addPlayer(session, data.color);
        notifyAllExcept(session, data.getGameID(), data.username + " joined the game as " + data.color.toString().toLowerCase());
        JoinedGameMessage msgObj = new JoinedGameMessage(null, gameData);
        safeSend(session, data.getGameID(), msgObj.serialize());
    }

    private void addObserver(Session session, ObserveGameCommand data) throws IOException {
        if (!sessions.containsKey(data.getGameID()))
        {
            sessions.put(data.getGameID(), new GameSockets());
        }

        sessions.get(data.getGameID()).addObserver(session);
        notifyAllExcept(session, data.getGameID(), data.username + " is observing the game");

        GameData gameData = BaseService.gameAccess.getGame(data.getGameID());
        JoinedGameMessage msgObj = new JoinedGameMessage(null, gameData);
        safeSend(session, data.getGameID(), msgObj.serialize());
    }

    private void removePlayer(Session session, LeaveGameCommand data) throws IOException, DoesNotExistException {
        if (data.username == null)
        {
            data.username = BaseService.authAccess.getUsernameByToken(data.getAuthToken());
        }

        if (data.color == null)
        {
            GameData gameData = BaseService.gameAccess.getGame(data.getGameID());
            if (gameData == null)
            {
                safeSend(session, data.getGameID(), new Gson().toJson(new ServerErrorMessage("Game does not exist")));
                return;
            }
            if (gameData.whiteUsername != null && gameData.whiteUsername.equals(data.username))
            {
                data.color = ChessGame.TeamColor.WHITE;
            }
            else
            {
                data.color = ChessGame.TeamColor.BLACK;
            }
        }

        notifyAllExcept(session, data.getGameID(), data.username + " left the game");
        sessions.get(data.getGameID()).removeParticipant(session);

        Server.gameAccess.leaveGame(data.getGameID(), data.username, data.color);
    }

    private void removeObserver(Session session, StopObserveCommand data) throws IOException {
        notifyAllExcept(session, data.getGameID(), data.username + " is no longer observing the game");
        sessions.get(data.getGameID()).removeObserver(session);
    }

    private void resignPlayer(Session session, ResignGameCommand data) throws IOException {
        GameData preCheck = BaseService.gameAccess.getGame(data.getGameID());

        if (data.username == null)
        {
            data.username = BaseService.authAccess.getUsernameByToken(data.getAuthToken());
        }

        if (preCheck.whiteUsername != null && preCheck.blackUsername != null && sessions.get(data.getGameID()).isEmpty())
        {
            ServerErrorMessage msgObj = new ServerErrorMessage("Game is over, cannot resign again");
            safeSend(session, data.getGameID(), new Gson().toJson(msgObj));
            return;
        }

        if (!preCheck.whiteUsername.equals(data.username) && !preCheck.blackUsername.equals(data.username))
        {
            ServerErrorMessage msgObj = new ServerErrorMessage("Cannot resign as an observer");
            safeSend(session, data.getGameID(), new Gson().toJson(msgObj));
            return;
        }

        ServerMessage textMsgObj = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, "Game over");
        safeSend(session, data.getGameID(), new Gson().toJson(textMsgObj));
        notifyAllExcept(session, data.getGameID(), data.username + " resigned from the game");
        sessions.get(data.getGameID()).removeAll();
    }

    private void makeMove(Session session, MakeMoveCommand data) throws IOException {
        MoveMadeResult moveResult = null;
        Exception error = null;

        GameData preCheck = BaseService.gameAccess.getGame(data.getGameID());

        if (preCheck.whiteUsername != null && preCheck.blackUsername != null && sessions.get(data.getGameID()).isEmpty())
        {
            ServerErrorMessage msgObj = new ServerErrorMessage("Game is over, cannot move");
            safeSend(session, data.getGameID(), new Gson().toJson(msgObj));
            return;
        }

        try {
            moveResult = Server.gameAccess.makeMove(data.getAuthToken(), data.getGameID(), data.move);
        } catch (Exception e) {
            // pieceMoved remains null
            error = e;
        }

        if (data.color == null) {
            GameData gameData = BaseService.gameAccess.getGame(data.getGameID());
            if (gameData == null) {
                safeSend(session, data.getGameID(), new Gson().toJson(new ServerErrorMessage("Game does not exist")));
                return;
            }
            if (gameData.whiteUsername != null && gameData.whiteUsername.equals(data.username)) {
                data.color = ChessGame.TeamColor.WHITE;
            } else {
                data.color = ChessGame.TeamColor.BLACK;
            }
        }

        if (moveResult != null)
        {
            GameData updated = BaseService.gameAccess.getGame(data.getGameID());

            for (Session gameSession : sessions.get(data.getGameID()).getParticipants())
            {
                if (gameSession.equals(session))
                {
                    GameMoveMessage msgObj = new GameMoveMessage(null, updated);
                    safeSend(gameSession, data.getGameID(), msgObj.serialize());
                }
                else
                {
                    GameMoveMessage updateObj = new GameMoveMessage(null, updated);
                    safeSend(gameSession, data.getGameID(), updateObj.serialize());

                    ServerMessage textMsgObj =
                            new ServerMessage(
                                    ServerMessage.ServerMessageType.NOTIFICATION,
                                    data.username + " moved " + moveResult.piece.toString() + ": " + data.move.toString()
                            );
                    safeSend(gameSession, data.getGameID(), new Gson().toJson(textMsgObj));
                }

                ChessGame.TeamColor checkedColor = data.color.equals(ChessGame.TeamColor.WHITE)
                        ? ChessGame.TeamColor.BLACK
                        : ChessGame.TeamColor.WHITE;

                if (moveResult.resultedInCheckmate) {
                    ServerMessage textMsgObj =
                            new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, checkedColor.name() + " was checkmated");
                    safeSend(gameSession, data.getGameID(), new Gson().toJson(textMsgObj));
                } else if (moveResult.resultedInCheck)
                {
                    ServerMessage textMsgObj =
                            new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, checkedColor.name() + " is in check");
                    safeSend(gameSession, data.getGameID(), new Gson().toJson(textMsgObj));
                }

            }
        }
        else
        {
            assert error != null;
            String msg = (error.getMessage() != null && !error.getMessage().isEmpty())
                ? error.getMessage()
                : "Invalid Move";
            ServerErrorMessage msgObj = new ServerErrorMessage(msg);
            safeSend(session, data.getGameID(), new Gson().toJson(msgObj));
        }
    }

    private void getLegalMoves(Session session, HighlightMovesCommand data) throws IOException {
        GameData gameData = BaseService.gameAccess.getGame(data.getGameID());
        List<ChessMove> moves = (List<ChessMove>) gameData.game.validMoves(data.pieceAtPosition);
        List<ChessPosition> moveEnds = new ArrayList<>();
        String message = null;

        if (moves == null)
        {
            moveEnds = null;
            message = "Invalid input: no piece at position " + data.pieceAtPosition;
        }
        else
        {
            for (ChessMove move : moves)
            {
                moveEnds.add(move.getEndPosition());
            }
        }

        LegalMovesMessage msgObj = new LegalMovesMessage(message, moveEnds);

        safeSend(session, data.getGameID(), new Gson().toJson(msgObj));
    }

    private void notifyAllExcept(
            Session exclude, Integer gameID, String message, ServerMessage.ServerMessageType msgType
    ) throws IOException {
        for (Session gameSession : sessions.get(gameID).getParticipants())
        {
            if (gameSession.equals(exclude)) {
                continue;
            }

            ServerMessage msgObj = new ServerMessage(msgType, message);
            safeSend(gameSession, gameID, new Gson().toJson(msgObj));
        }
    }

    private void notifyAllExcept(Session exclude, Integer gameID, String message) throws IOException {
        notifyAllExcept(exclude, gameID, message, ServerMessage.ServerMessageType.NOTIFICATION);
    }

    private void safeSend(Session session, Integer gameID, String message) throws IOException {
        if (session.isOpen())
        {
            session.getRemote().sendString(message);
        }
        else
        {
            sessions.get(gameID).removeParticipant(session);
        }
    }
}
