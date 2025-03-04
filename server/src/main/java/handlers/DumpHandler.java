package handlers;

import model.GameData;
import model.UserData;
import models.DumpRecord;
import server.Server;
import spark.Request;
import spark.Response;

import java.util.ArrayList;
import java.util.Collection;

public class DumpHandler extends BaseRequestHandler  {
    public DumpHandler(Request req, Response res)
    {
        super(req, res);
    }

    @Override
    public String handleRequest() {
        Collection<UserData> userData = Server.userAccess.getAll();
        Collection<ArrayList<String>> authData = Server.authAccess.getAll();
        Collection<GameData> gameData = Server.gameAccess.getAll();
        DumpRecord dump = new DumpRecord(userData, authData, gameData);

        String dumpJson = serializer.toJson(dump);

        res.type("application/json");
        res.status(200);
        return dumpJson;
    }
}
