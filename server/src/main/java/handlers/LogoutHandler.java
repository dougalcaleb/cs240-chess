package handlers;

import dataaccess.DataAccessException;
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
        catch (DataAccessException e)
        {
            res.status(401);
            return serializeResponse(new ErrorMessage("Error: unauthorized"));
        }

        return serializeResponse(new Object());
    }
}
