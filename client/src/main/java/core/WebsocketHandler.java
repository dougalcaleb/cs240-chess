package core;

import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.Gson;
import repl.BaseRepl;
import websocket.commands.*;
import websocket.messages.*;

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
                    ServerMessage msgBase = new Gson().fromJson(message, ServerMessage.class);
                    try
                    {
                        switch (msgBase.getServerMessageType())
                        {
                            case ServerMessage.ServerMessageType.LOAD_GAME ->
                                    WsMessageHandler.handleGameLoad(new Gson().fromJson(message, JoinedGameMessage.class));
                            case ServerMessage.ServerMessageType.GAME_MOVE ->
                                    WsMessageHandler.handleGameLoad(new Gson().fromJson(message, GameMoveMessage.class));
                            case ServerMessage.ServerMessageType.LEGAL_MOVES ->
                                    WsMessageHandler.handleLegalMoves(new Gson().fromJson(message, LegalMovesMessage.class));
                            case ServerMessage.ServerMessageType.ERROR ->
                                    WsMessageHandler.logError(new Gson().fromJson(message, ServerErrorMessage.class));
                            default ->
                                    WsMessageHandler.logMessage(new Gson().fromJson(message, ServerMessage.class));
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
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

    public void observeGame()
    {
        try
        {
            ObserveGameCommand cmd = new ObserveGameCommand(BaseRepl.authToken, BaseRepl.observingGame, BaseRepl.username);
            session.getBasicRemote().sendText(new Gson().toJson(cmd));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void leaveGame()
    {
        try
        {
            LeaveGameCommand cmd = new LeaveGameCommand(BaseRepl.authToken, BaseRepl.trueGameId, BaseRepl.username, BaseRepl.color);
            session.getBasicRemote().sendText(new Gson().toJson(cmd));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void stopObserveGame()
    {
        try
        {
            StopObserveCommand cmd = new StopObserveCommand(BaseRepl.authToken, BaseRepl.observingGame, BaseRepl.username);
            session.getBasicRemote().sendText(new Gson().toJson(cmd));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void resignGame()
    {
        try
        {
            ResignGameCommand cmd = new ResignGameCommand(BaseRepl.authToken, BaseRepl.trueGameId, BaseRepl.username, BaseRepl.color);
            session.getBasicRemote().sendText(new Gson().toJson(cmd));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void makeMove(ChessMove move)
    {
        try
        {
            MakeMoveCommand cmd = new MakeMoveCommand(BaseRepl.authToken, BaseRepl.trueGameId, BaseRepl.username, BaseRepl.color, move);
            session.getBasicRemote().sendText(new Gson().toJson(cmd));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void highlightMoves(ChessPosition position)
    {
        try
        {
            int gID = -1;
            if (BaseRepl.trueGameId != -1)
            {
                gID = BaseRepl.trueGameId;
            }
            else if (BaseRepl.observingGame != -1)
            {
                gID = BaseRepl.observingGame;
            }
            else
            {
                throw new RuntimeException("No game ID stored to identify game");
            }

            HighlightMovesCommand cmd = new HighlightMovesCommand(BaseRepl.authToken, gID, position);
            session.getBasicRemote().sendText(new Gson().toJson(cmd));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
