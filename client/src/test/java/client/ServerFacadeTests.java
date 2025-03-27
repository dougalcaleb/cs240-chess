package client;

import clientmodel.FacadeResult;
import core.ServerFacade;
import org.junit.jupiter.api.*;
import repl.BaseRepl;
import server.Server;


public class ServerFacadeTests {

    private static Server server;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        server.drop();
        System.out.println("Started test HTTP server on " + port);
        ServerFacade.setServerURL("http://localhost:"+port);
    }

    @AfterAll
    static void stopServer() {
        BaseRepl.authToken = null;
        server.drop();
        server.stop();
    }

    @AfterEach
    public void teardown()
    {
        server.drop();
    }


    @Test
    public void facadeRegisterRegistersUserBasic()
    {
        // with just username and password
        String[] args = new String[] { "testuser1", "testuserpw1" };
        FacadeResult result = ServerFacade.register(args);

        Assertions.assertTrue(result.success());
        Assertions.assertEquals("Successfully registered and logged in as testuser1", result.message());
    }

    @Test
    public void facadeRegisterRegistersUserAdvanced()
    {
        // with username and password and email
        String[] args = new String[] { "testuser2", "testuserpw2", "test@useremail.com" };
        FacadeResult result = ServerFacade.register(args);

        Assertions.assertTrue(result.success());
        Assertions.assertEquals("Successfully registered and logged in as testuser2", result.message());
    }

    @Test
    public void facadeRegisterFailsOnInvalidArgs()
    {
        // with no args
        String[] args = new String[] {};
        Assertions.assertThrows(RuntimeException.class, () -> ServerFacade.register(args));

        // with too many args
        String[] args2 = new String[] { "username", "password", "email", "extra data"};
        Assertions.assertThrows(RuntimeException.class, () -> ServerFacade.register(args2));
    }

    @Test
    public void facadeRegisterFailsOnExistingUser()
    {
        String[] first = new String[] { "dupuser", "userpassword" };
        ServerFacade.register(first);

        String[] second = new String[] { "dupuser", "passwordforexistinguser" };
        FacadeResult result = ServerFacade.register(second);

        Assertions.assertFalse(result.success());
        Assertions.assertEquals("Username 'dupuser' is already taken.", result.message());
    }

    @Test
    public void facadeLoginLogsInExistingUser()
    {
        ServerFacade.register(new String[] { "cooluser", "userpw" });
        ServerFacade.logout();

        FacadeResult result = ServerFacade.login(new String[] { "cooluser", "userpw" });
        Assertions.assertTrue(result.success());
        Assertions.assertEquals("Successfully logged in as cooluser", result.message());
    }

    @Test
    public void facadeLoginDoesNotLoginNonexistent()
    {
        FacadeResult result = ServerFacade.login(new String[] { "heyman", "userpw" });
        Assertions.assertFalse(result.success());
        Assertions.assertEquals("Unauthorized", result.message());
    }

    @Test
    public void facadeLoginDoesNotLoginIncorrectPassword()
    {
        ServerFacade.register(new String[] { "cooluser", "userpw" });
        ServerFacade.logout();

        FacadeResult result = ServerFacade.login(new String[] { "cooluser", "mypassword" });
        Assertions.assertFalse(result.success());
        Assertions.assertEquals("Unauthorized", result.message());
    }

    @Test
    public void facadeListGetsGameList()
    {
        ServerFacade.register(new String[]{ "listuser", "listuserpassword" });
        ServerFacade.create(new String[]{ "game1" });
        ServerFacade.create(new String[]{ "game2" });

        FacadeResult result = ServerFacade.list();
        Assertions.assertTrue(result.success());
        Assertions.assertEquals("""
                [1m   Game ID Name  Players[22m
                   1       game1 White:      Black:    \s
                   2       game2 White:      Black:    \s""", result.message());
    }

    @Test
    public void facadeCreateCreatesGame()
    {
        ServerFacade.register(new String[]{ "createUser", "createUserpassword" });
        FacadeResult result = ServerFacade.create(new String[]{ "gameSingle1" });

        Assertions.assertTrue(result.success());
        Assertions.assertEquals("Successfully created game", result.message());
    }

    @Test
    public void facadeCreateCreatesMultiple()
    {
        ServerFacade.register(new String[]{ "createUserMult", "createUserpassword" });
        FacadeResult result1 = ServerFacade.create(new String[]{ "gameM1" });

        Assertions.assertTrue(result1.success());
        Assertions.assertEquals("Successfully created game", result1.message());

        FacadeResult result2 = ServerFacade.create(new String[]{ "gameM2" });

        Assertions.assertTrue(result2.success());
        Assertions.assertEquals("Successfully created game", result2.message());
    }

    @Test
    public void facadeCreateDoesNotCreateWithDupName()
    {
        ServerFacade.register(new String[]{ "createUserMult", "createUserpassword" });
        FacadeResult result1 = ServerFacade.create(new String[]{ "gameMD1" });

        Assertions.assertTrue(result1.success());
        Assertions.assertEquals("Successfully created game", result1.message());

        FacadeResult result2 = ServerFacade.create(new String[]{ "gameMD1" });

        Assertions.assertFalse(result2.success());
        Assertions.assertEquals("Server error: Game with this name already exists", result2.message());
    }

    @Test
    public void facadeLogoutLogsOutUser()
    {
        ServerFacade.register(new String[]{ "loginUser", "logoutpw" });
        FacadeResult result = ServerFacade.logout();

        Assertions.assertTrue(result.success());
        Assertions.assertEquals("Successfully logged out", result.message());
    }

    @Test
    public void facadeLogoutCannotLogoutTwice()
    {
        ServerFacade.register(new String[]{ "loginUser", "logoutpw" });
        FacadeResult result = ServerFacade.logout();

        Assertions.assertTrue(result.success());
        Assertions.assertEquals("Successfully logged out", result.message());

        FacadeResult result2 = ServerFacade.logout();
        Assertions.assertFalse(result2.success());
        Assertions.assertEquals("Unauthorized", result2.message());
    }

    @Test
    public void facadeJoinGameJoinsExisting()
    {
        ServerFacade.register(new String[]{ "joinUser", "pass" });
        ServerFacade.create(new String[]{ "game1" });

        FacadeResult result = ServerFacade.join(new String[]{ "1", "black" });
        Assertions.assertTrue(result.success());
        Assertions.assertEquals("Successfully joined game", result.message());
    }

    @Test
    public void facadeJoinGameCannotJoinNonexistent()
    {
        ServerFacade.register(new String[]{ "joinUser", "pass" });
        ServerFacade.create(new String[]{ "game1" });

        Assertions.assertThrows(RuntimeException.class, () -> ServerFacade.join(new String[]{ "6", "black" }));
    }

    @Test
    public void facadeJoinGameRequiresCorrectArgs()
    {
        ServerFacade.register(new String[]{ "joinUser", "pass" });
        ServerFacade.create(new String[]{ "game1" });

        Assertions.assertThrows(RuntimeException.class, () -> ServerFacade.join(new String[]{ "1" }));
    }

    @Test
    public void facadeJoinGameRequiresCorrectColor()
    {
        ServerFacade.register(new String[]{ "joinUser", "pass" });
        ServerFacade.create(new String[]{ "game1" });

        Assertions.assertThrows(RuntimeException.class, () -> ServerFacade.join(new String[]{ "1", "whichever" }));
        Assertions.assertThrows(RuntimeException.class, () -> ServerFacade.join(new String[]{ "1", "blaack" }));
        Assertions.assertThrows(RuntimeException.class, () -> ServerFacade.join(new String[]{ "1", " black" }));
        Assertions.assertThrows(RuntimeException.class, () -> ServerFacade.join(new String[]{ "1", " white" }));

        Assertions.assertDoesNotThrow(() -> ServerFacade.join(new String[]{ "1", "white" }));
        Assertions.assertDoesNotThrow(() -> ServerFacade.join(new String[]{ "1", "BLACK" }));
    }

}
