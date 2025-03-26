package handlers;

import servermodel.GameList;
import server.Server;
import spark.Request;
import spark.Response;

public class ListGamesHandler extends BaseRequestHandler {
    public ListGamesHandler(Request req, Response res)
    {
        super(req, res);
    }

    @Override
    public String handleRequest() {
        res.status(200);
        return serializeResponse(new GameList(Server.gameAccess.getAll()));
    }
}
