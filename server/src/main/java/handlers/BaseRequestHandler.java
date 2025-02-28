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

    protected Object serializeRequest(Object returnClass) {
        return serializer.fromJson(req.body(), returnClass.getClass());
    }

    protected abstract String HandleRequest();

}
