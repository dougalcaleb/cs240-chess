package server;

import dataaccess.DatabaseManager;
import handlers.*;
import serverwebsocket.ServerWebsocketHandler;
import service.AuthService;
import service.GameService;
import service.UserService;
import spark.Spark;

public class Server {

    public static UserService userAccess;
    public static AuthService authAccess;
    public static GameService gameAccess;
    public static ServerWebsocketHandler wsHandler;

    public Server()
    {
        userAccess = new UserService();
        authAccess = new AuthService();
        gameAccess = new GameService();
        wsHandler = new ServerWebsocketHandler();
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.webSocket("/ws", Server.wsHandler);

        // Endpoints
        Spark.get("/db", (req, res) -> new DumpHandler(req, res).handleRequest() );

        Spark.post("/user", (req, res) -> new RegisterHandler(req, res).handleRequest() );

        Spark.post("/session", (req, res) -> new LoginHandler(req, res).handleRequest() );

        Spark.delete("/session", (req, res) -> new LogoutHandler(req, res).handleRequest() );

        Spark.get("/game", (req, res) -> new ListGamesHandler(req, res).handleRequestAuthd() );

        Spark.post("/game", (req, res) -> new NewGameHandler(req, res).handleRequestAuthd() );

        Spark.put("/game", (req, res) -> new JoinGameHandler(req, res).handleRequestAuthd() );

        Spark.delete("/db", (req, res) -> new ResetDatabaseHandler(req, res).handleRequest() );

        Spark.awaitInitialization();

        // Database
        DatabaseManager.initDatabase(false, false);

        return Spark.port();
    }

    public void drop()
    {
        Server.gameAccess.reset();
        Server.userAccess.reset();
        Server.authAccess.reset();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
