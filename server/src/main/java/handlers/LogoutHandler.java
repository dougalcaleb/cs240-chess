package handlers;

import spark.Request;
import spark.Response;

public class LogoutHandler extends BaseRequestHandler {
    public LogoutHandler(Request req, Response res)
    {
        super(req, res);
    }

    @Override
    protected String HandleRequest() {
        return null;
    }
}
