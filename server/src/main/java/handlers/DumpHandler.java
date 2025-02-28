package handlers;

import model.UserData;
import models.DumpRecord;
import server.Server;
import spark.Request;
import spark.Response;

import java.util.Collection;

public class DumpHandler extends BaseRequestHandler  {
    public DumpHandler(Request req, Response res)
    {
        super(req, res);
    }

    @Override
    public String HandleRequest() {
        Collection<UserData> userData = Server.userAccess.getAll();
        Collection<String> authData = Server.authAccess.getAll();
//        ArrayList<GameData> gameData = Server.gameAccess;
        DumpRecord dump = new DumpRecord(userData, authData);

        String dumpJson = serializer.toJson(dump);

        res.type("application/json");
        res.status(200);
        return dumpJson;
    }
}
