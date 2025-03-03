package handlers;

import exceptions.AuthException;
import models.ErrorMessage;
import server.Server;
import spark.Request;
import spark.Response;

public class LogoutHandler extends BaseRequestHandler {
    public LogoutHandler(Request req, Response res)
    {
        super(req, res);
    }

    @Override
    public String HandleRequest() {
        String logoutToken = getRequestHeader("Authorization");

        try
        {
            Server.authAccess.logoutAuth(logoutToken);
        }
        catch (AuthException e)
        {
            res.status(401);
            return serializeResponse(new ErrorMessage(e.getMessage()));
        }

        return serializeResponse(new Object());
    }
}
