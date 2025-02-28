package handlers;

import spark.Request;
import spark.Response;

public class ResetDatabaseHandler extends BaseRequestHandler {
    public ResetDatabaseHandler(Request req, Response res)
    {
        super(req, res);
    }

    @Override
    protected String HandleRequest() {
        return null;
    }
}
