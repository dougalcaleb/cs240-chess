package handlers;

import spark.Request;
import spark.Response;

public class LoginHandler extends BaseRequestHandler {
    public LoginHandler(Request req, Response res)
    {
        super(req, res);
    }

    @Override
    protected String HandleRequest() {
        return null;
    }
}
