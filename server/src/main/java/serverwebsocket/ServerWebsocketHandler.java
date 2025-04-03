package serverwebsocket;

import chess.ChessMove;
import chess.ChessPiece;
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
import websocket.commands.*;
import websocket.messages.GameMoveMessage;
import websocket.messages.LegalMovesMessage;
import websocket.messages.ServerMessage;

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
        if (!sessions.containsKey(data.getGameID()))
        {
            sessions.put(data.getGameID(), new GameSockets());
        }

        sessions.get(data.getGameID()).addPlayer(session, data.color);
        notifyAllExcept(session, data.getGameID(), data.username + " joined the game as " + data.color.toString().toLowerCase());
    }

    private void addObserver(Session session, ObserveGameCommand data) throws IOException {
        if (!sessions.containsKey(data.getGameID()))
        {
            sessions.put(data.getGameID(), new GameSockets());
        }

        sessions.get(data.getGameID()).addObserver(session);
        notifyAllExcept(session, data.getGameID(), data.username + " is observing the game");

        // hijack move message to send the game data to the observer before the game has been broadcast
        GameData gameData = BaseService.gameAccess.getGame(data.getGameID());
        GameMoveMessage msgObj = new GameMoveMessage(null, gameData);
        safeSend(session, data.getGameID(), msgObj.serialize());
    }

    private void removePlayer(Session session, LeaveGameCommand data) throws IOException, DoesNotExistException {
        notifyAllExcept(session, data.getGameID(), data.username + " left the game");
        sessions.get(data.getGameID()).removePlayer(data.color);

        Server.gameAccess.leaveGame(data.getGameID(), data.username, data.color);
    }

    private void removeObserver(Session session, StopObserveCommand data) throws IOException {
        notifyAllExcept(session, data.getGameID(), data.username + " is no longer observing the game");
        sessions.get(data.getGameID()).removeObserver(session);
    }

    private void resignPlayer(Session session, ResignGameCommand data) throws IOException {
        notifyAllExcept(session, data.getGameID(), data.username + " resigned from the game");
        sessions.get(data.getGameID()).removePlayer(data.color);
    }

    private void makeMove(Session session, MakeMoveCommand data) throws IOException {
        ChessPiece pieceMoved = null;
        Exception error = null;
        try {
            pieceMoved = Server.gameAccess.makeMove(data.getGameID(), data.move);
        } catch (Exception e) {
            // pieceMoved remains null
            error = e;
        }

        if (pieceMoved != null)
        {
            GameData updated = BaseService.gameAccess.getGame(data.getGameID());

            for (Session gameSession : sessions.get(data.getGameID()).getParticipants())
            {
                if (gameSession.equals(session))
                {
                    continue;
                }
                GameMoveMessage msgObj = new GameMoveMessage(data.username + " moved " + pieceMoved.toString() + ": " + data.move.toString(), updated);
                safeSend(gameSession, data.getGameID(), msgObj.serialize());
            }
        }
        else
        {
            assert error != null;
            ServerMessage msgObj = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, error.getMessage());
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

    private void notifyAllExcept(Session exclude, Integer gameID, String message, ServerMessage.ServerMessageType msgType) throws IOException {
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
