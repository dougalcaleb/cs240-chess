package server;

import handlers.DumpHandler;
import service.AuthService;
import service.GameService;
import service.UserService;
import spark.Spark;

public class Server {

    public static UserService userAccess;
    public static AuthService authAccess;
    public static GameService gameAccess;

    public Server()
    {
        userAccess = new UserService();
        authAccess = new AuthService();
        gameAccess = new GameService();
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        //This line initializes the server and can be removed once you have a functioning endpoint 
//        Spark.init();

//        Spark.post("/user", (req, res) -> new RegisterHandler(req, res) );

        Spark.get("/dump", (req, res) -> new DumpHandler(req, res).HandleRequest() );

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
