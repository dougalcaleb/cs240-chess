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
        return serializeResponse(registrationResult);
    }
}
