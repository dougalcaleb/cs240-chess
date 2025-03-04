package handlers;

import model.RegisterResult;
import model.UserData;
import models.ErrorMessage;
import server.Server;
import spark.Request;
import spark.Response;

public class RegisterHandler extends BaseRequestHandler {
    public RegisterHandler(Request request, Response response)
    {
        super(request, response);
    }

    @Override
    public String handleRequest() {
        UserData input = (UserData) deserializeRequest(UserData.class);

        if (
            (input.password == null || (input.password != null && input.password.isBlank())) ||
            (input.username == null || (input.username != null && input.username.isBlank())) ||
            (input.email == null || (input.email != null && input.email.isBlank()))
        ) {
            res.status(400);
            return serializeResponse(new ErrorMessage("Error: bad request (missing information)"));
        }

        RegisterResult registrationResult = Server.userAccess.registerUser(input);

        if (registrationResult == null)
        {
            res.status(500);
            return serializeResponse(new ErrorMessage("Error: internal server error"));
        }
        else if (registrationResult.userExists)
        {
            res.status(403);
            return serializeResponse(new ErrorMessage("Error: already taken"));
        }
        else if (!registrationResult.createSucceeded)
        {
            res.status(500);
            return serializeResponse(new ErrorMessage("Error: user creation failed"));
        }
        else if (!registrationResult.loginSucceeded)
        {
            res.status(400);
            return serializeResponse(new ErrorMessage("Error: bad request"));
        }

        res.status(200);
        return serializeResponse(registrationResult);
    }
}
