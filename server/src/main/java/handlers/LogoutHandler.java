package handlers;

import exceptions.AuthException;
import servermodel.ErrorMessage;
import server.Server;
import spark.Request;
import spark.Response;

public class LogoutHandler extends BaseRequestHandler {
    public LogoutHandler(Request req, Response res)
    {
        super(req, res);
    }

    @Override
    public String handleRequest() {
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

        res.status(200);
        return serializeResponse(new Object());
    }
}
