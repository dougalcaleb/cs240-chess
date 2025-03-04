package handlers;

import exceptions.DataAccessException;
import model.AuthData;
import model.UserData;
import models.ErrorMessage;
import server.Server;
import spark.Request;
import spark.Response;

public class LoginHandler extends BaseRequestHandler {
    public LoginHandler(Request req, Response res)
    {
        super(req, res);
    }

    @Override
    public String handleRequest() {
        UserData input = (UserData) deserializeRequest(UserData.class);
        AuthData loginData = null;

        try
        {
            loginData = Server.userAccess.loginUser(input);
            if (loginData == null)
            {
                res.status(401);
                return serializeResponse(new ErrorMessage("Error: unauthorized (invalid credentials)"));
            }
        }
        catch (DataAccessException e)
        {
            res.status(401);
            return serializeResponse(new ErrorMessage("Error: unauthorized (user does not exist)"));
        }

        res.status(200);
        return serializeResponse(loginData);
    }
}
