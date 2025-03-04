package handlers;

import com.google.gson.Gson;
import exceptions.AuthException;
import models.ErrorMessage;
import server.Server;
import spark.Request;
import spark.Response;

public abstract class BaseRequestHandler {

    protected Gson serializer;
    Request req;
    Response res;

    public BaseRequestHandler(Request request, Response response)
    {
        req = request;
        res = response;
        serializer = new Gson();
    }

    protected <T> Object deserializeRequest(Class<T> returnClass) {
        return serializer.fromJson(req.body(), returnClass);
    }

    protected String serializeResponse(Object data)
    {
        return serializer.toJson(data);
    }

    protected String getRequestHeader(String header)
    {
        return req.headers(header);
    }

    public String handleRequestAuthd()
    {
        String authToken = getRequestHeader("Authorization");

        try
        {
            Server.authAccess.verifyAuth(authToken);
        }
        catch (AuthException e)
        {
            res.status(401);
            return serializeResponse(new ErrorMessage(e.getMessage()));
        }

        return handleRequest();
    }

    public abstract String handleRequest();

}
