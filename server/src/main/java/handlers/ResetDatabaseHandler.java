package handlers;

import servermodel.ErrorMessage;
import server.Server;
import spark.Request;
import spark.Response;

public class ResetDatabaseHandler extends BaseRequestHandler {
    public ResetDatabaseHandler(Request req, Response res)
    {
        super(req, res);
    }

    @Override
    public String handleRequest() {

        try
        {
            Server.gameAccess.reset();
            Server.userAccess.reset();
            Server.authAccess.reset();
        }
        catch (Exception e)
        {
            res.status(500);
            return serializeResponse(new ErrorMessage("Server error"));
        }

        return serializeResponse(new Object());
    }
}
