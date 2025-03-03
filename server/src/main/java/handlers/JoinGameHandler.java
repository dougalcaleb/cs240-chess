package handlers;

import exceptions.DoesNotExistException;
import exceptions.GameTakenException;
import models.ErrorMessage;
import models.JoinGameRequest;
import server.Server;
import spark.Request;
import spark.Response;

public class JoinGameHandler extends BaseRequestHandler {
    public JoinGameHandler(Request req, Response res)
    {
        super(req, res);
    }

    @Override
    public String HandleRequest() {
        JoinGameRequest input = (JoinGameRequest) deserializeRequest(JoinGameRequest.class);

        if (!input.playerColor().equals("WHITE") && !input.playerColor().equals("BLACK"))
        {
            res.status(400);
            return serializeResponse(new ErrorMessage("Error: bad request (invalid player color)"));
        }

        try
        {
            Server.gameAccess.joinGame(
                input.gameID(),
                Server.authAccess.getUsername(getRequestHeader("Authorization")),
                input.playerColor()
            );
        }
        catch (GameTakenException e)
        {
            res.status(403);
            return serializeResponse(new ErrorMessage("Error: already taken"));
        }
        catch (DoesNotExistException e)
        {
            res.status(400);
            return serializeResponse(new ErrorMessage("Error: bad request (game does not exist)"));
        }
        catch (RuntimeException e)
        {
            res.status(500);
            return serializeResponse(new ErrorMessage(e.getMessage()));
        }

        res.status(200);
        return serializeResponse(new Object());
    }
}
