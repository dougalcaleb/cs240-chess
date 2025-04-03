package serverwebsocket;

import chess.ChessPiece;
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
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.HashMap;

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
        notifyAll(data.getGameID(), data.username + " joined the game as " + data.color.toString().toLowerCase());
    }

    private void addObserver(Session session, ObserveGameCommand data) throws IOException {
        if (!sessions.containsKey(data.getGameID()))
        {
            sessions.put(data.getGameID(), new GameSockets());
        }

        sessions.get(data.getGameID()).addObserver(session);
        notifyAll(data.getGameID(), data.username + " is observing the game");
    }

    private void removePlayer(Session session, LeaveGameCommand data) throws IOException, DoesNotExistException {
        notifyAll(data.getGameID(), data.username + " left the game");
        sessions.get(data.getGameID()).removePlayer(data.color);

        Server.gameAccess.leaveGame(data.getGameID(), data.username, data.color);
    }

    private void removeObserver(Session session, StopObserveCommand data) throws IOException {
        notifyAll(data.getGameID(), data.username + " is no longer observing the game");
        sessions.get(data.getGameID()).removeObserver(session);
    }

    private void resignPlayer(Session session, ResignGameCommand data) throws IOException {
        notifyAll(data.getGameID(), data.username + " resigned from the game");
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
//            notifyAll(data.getGameID(), data.username + " moved " + pieceMoved.toString() + ": " + data.move.toString(), ServerMessage.ServerMessageType.GAME_MOVE);
            GameData updated = BaseService.gameAccess.getGame(data.getGameID());

            for (Session gameSession : sessions.get(data.getGameID()).getParticipants())
            {
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

    private void notifyAll(Integer gameID, String message, ServerMessage.ServerMessageType msgType) throws IOException {
        for (Session gameSession : sessions.get(gameID).getParticipants())
        {
            ServerMessage msgObj = new ServerMessage(msgType, message);
            safeSend(gameSession, gameID, new Gson().toJson(msgObj));
        }
    }

    private void notifyAll(Integer gameID, String message) throws IOException {
        notifyAll(gameID, message, ServerMessage.ServerMessageType.NOTIFICATION);
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
