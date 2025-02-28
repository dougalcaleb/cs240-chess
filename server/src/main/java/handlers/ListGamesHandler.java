package handlers;

import spark.Request;
import spark.Response;

public class ListGamesHandler extends BaseRequestHandler {
    public ListGamesHandler(Request req, Response res)
    {
        super(req, res);
    }

    @Override
    protected String HandleRequest() {
        return null;
    }
}
