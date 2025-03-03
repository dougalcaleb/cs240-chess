package handlers;

import com.google.gson.Gson;
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

    protected boolean Authenticate()
    {
        return false;
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

    public abstract String HandleRequest();

}
