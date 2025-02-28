package handlers;

import spark.Request;
import spark.Response;

public class NewGameHandler extends BaseRequestHandler {
    public NewGameHandler(Request req, Response res)
    {
        super(req, res);
    }

    @Override
    protected String HandleRequest() {
        return null;
    }
}
