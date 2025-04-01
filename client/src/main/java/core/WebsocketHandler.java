package core;

import com.google.gson.Gson;
import repl.BaseRepl;
import websocket.commands.JoinGameCommand;
import websocket.commands.ObserveGameCommand;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebsocketHandler extends Endpoint {
    public static String serverURL = "";

    private Session session = null;

    public WebsocketHandler()
    {

    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {

    }

    public void safeConnect()
    {
        if (session != null)
        {
            return;
        }

        try
        {
            URI socket = new URI("ws://" + serverURL + "/ws");
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            session = container.connectToServer(this, socket);

            session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage msg = new Gson().fromJson(message, ServerMessage.class);
                    WsMessageHandler.logMessage(msg);
                }
            });

            joinGame();
        }
        catch (URISyntaxException | DeploymentException | IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void joinGame()
    {
        try
        {
            JoinGameCommand cmd = new JoinGameCommand(BaseRepl.authToken, BaseRepl.trueGameId, BaseRepl.username, BaseRepl.color);
            session.getBasicRemote().sendText(new Gson().toJson(cmd));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void observeGame(Integer gameID)
    {
        try
        {
            ObserveGameCommand cmd = new ObserveGameCommand(BaseRepl.authToken, gameID, BaseRepl.username);
            session.getBasicRemote().sendText(new Gson().toJson(cmd));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
