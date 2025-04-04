package serverwebsocket;

import chess.ChessGame;
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
        if (!sessions.containsKey(data.getGameID()))
        {
            sessions.put(data.getGameID(), new GameSockets());
        }

        if (data.username == null)
        {
            data.username = BaseService.authAccess.getUsernameByToken(data.getAuthToken());
        }

        GameData gameData = null;
        if (data.color == null)
        {
            gameData = BaseService.gameAccess.getGame(data.getGameID());
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

        if (gameData.whiteUsername != null && gameData.blackUsername != null)
        {
            addObserver(session, new ObserveGameCommand(data.getAuthToken(), data.getGameID(), data.username));
            return;
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

        // hijack move message to send the game data to the observer before the game has been broadcast
        GameData gameData = BaseService.gameAccess.getGame(data.getGameID());
        JoinedGameMessage msgObj = new JoinedGameMessage(null, gameData);
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
            pieceMoved = Server.gameAccess.makeMove(data.getAuthToken(), data.getGameID(), data.move);
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
                    GameMoveMessage msgObj = new GameMoveMessage(null, updated);
                    safeSend(gameSession, data.getGameID(), msgObj.serialize());
                    continue;
                }
                GameMoveMessage updateObj = new GameMoveMessage(null, updated);
                safeSend(gameSession, data.getGameID(), updateObj.serialize());

                ServerMessage textMsgObj = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, data.username + " moved " + pieceMoved.toString() + ": " + data.move.toString());
                safeSend(gameSession, data.getGameID(), new Gson().toJson(textMsgObj));
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
