package handlers;

import spark.Request;
import spark.Response;

public class LogoutHandler extends BaseRequestHandler {
    public LogoutHandler(Request req, Response res)
    {
        super(req, res);
    }

    @Override
    public String HandleRequest() {
        return null;
    }
}
