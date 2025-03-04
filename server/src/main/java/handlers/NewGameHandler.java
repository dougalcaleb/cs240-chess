package handlers;

import exceptions.GameTakenException;
import models.ErrorMessage;
import models.NewGameRequest;
import models.NewGameResponse;
import server.Server;
import spark.Request;
import spark.Response;

public class NewGameHandler extends BaseRequestHandler {
    public NewGameHandler(Request req, Response res)
    {
        super(req, res);
    }

    @Override
    public String handleRequest() {
        NewGameRequest input = (NewGameRequest) deserializeRequest(NewGameRequest.class);

        int gameID = -1;

        try
        {
            gameID = Server.gameAccess.createGame(input.gameName());
        }
        catch (GameTakenException e)
        {
            res.status(400);
            return serializeResponse(new ErrorMessage(e.getMessage()));
        }

        if (gameID < 0)
        {
            res.status(500);
            return serializeResponse(new ErrorMessage("Error: invalid game ID created"));
        }

        return serializeResponse(new NewGameResponse(gameID));
    }
}
