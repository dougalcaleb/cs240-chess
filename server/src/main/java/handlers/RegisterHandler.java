package handlers;

import model.RegisterResult;
import model.UserData;
import server.Server;
import spark.Request;
import spark.Response;

public class RegisterHandler extends BaseRequestHandler {
    public RegisterHandler(Request request, Response response)
    {
        super(request, response);
    }

    @Override
    public String HandleRequest() {
        UserData input = (UserData) deserializeRequest(UserData.class);
        RegisterResult registrationResult = Server.userAccess.registerUser(input);

        // probably needs different error codes for different errors
//        if (
//            registrationResult == null ||
//            !registrationResult.loginSucceeded ||
//            !registrationResult.createSucceeded ||
//            registrationResult.userExists
//        ) {
//            res.status(500);
//        }

        return serializeResponse(registrationResult);
    }
}
