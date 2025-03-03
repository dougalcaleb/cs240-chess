package handlers;

import server.Server;
import spark.Request;
import spark.Response;

public class ListGamesHandler extends BaseRequestHandler {
    public ListGamesHandler(Request req, Response res)
    {
        super(req, res);
    }

    @Override
    public String HandleRequest() {
        res.status(200);
        return serializeResponse(Server.gameAccess.getAll());
    }
}
