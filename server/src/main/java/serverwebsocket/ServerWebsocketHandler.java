package serverwebsocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import servermodel.GameSockets;
import websocket.commands.JoinGameCommand;
import websocket.commands.ObserveGameCommand;
import websocket.commands.UserGameCommand;
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

        for (Session gameSession : sessions.get(data.getGameID()).getParticipants())
        {
            String joinMsgStr = data.username + " joined the game as " + data.color.toString().toLowerCase();
            ServerMessage joinMsgObj = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, joinMsgStr);
            if (gameSession.isOpen())
            {
                gameSession.getRemote().sendString(new Gson().toJson(joinMsgObj));
            }
        }
    }

    private void addObserver(Session session, ObserveGameCommand data) throws IOException {
        if (!sessions.containsKey(data.getGameID()))
        {
            sessions.put(data.getGameID(), new GameSockets());
        }

        sessions.get(data.getGameID()).addObserver(session);

        for (Session gameSession : sessions.get(data.getGameID()).getParticipants())
        {
            String joinMsgStr = data.username + " is observing the game";
            ServerMessage joinMsgObj = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, joinMsgStr);
            gameSession.getRemote().sendString(new Gson().toJson(joinMsgObj));
        }
    }
}
