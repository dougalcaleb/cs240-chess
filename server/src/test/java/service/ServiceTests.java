package service;


import exceptions.GameTakenException;
import sharedmodel.GameData;
import sharedmodel.RegisterResult;
import sharedmodel.UserData;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceTests {

    private static AuthService auth;
    private static UserService user;
    private static GameService game;

    @BeforeAll
    public static void init()
    {
        auth = new AuthService();
        user = new UserService();
        game = new GameService();
    }

    @BeforeEach
    public void setup()
    {

    }

    @AfterEach
    public void teardown()
    {
        auth.reset();
        user.reset();
        game.reset();
    }


    /**
     * =========================================================================================
     * AuthService
     * =========================================================================================
     */

    @Test
    public void authLogoutAuthRoutesCorrectly()
    {
        BaseService.authAccess.setDB(Map.of(
            "user1", new ArrayList<>(Arrays.asList("auth-token-1"))
        ));

        boolean errorThrown = false;

        try
        {
            auth.logoutAuth("auth-token-1");
        }
        catch (Exception e)
        {
            errorThrown = true;
        }

        Assertions.assertFalse(errorThrown);
    }

    @Test
    public void authLogoutAuthThrows()
    {
        BaseService.authAccess.setDB(Map.of(
            "user1", new ArrayList<>(Arrays.asList("auth-token-1"))
        ));

        boolean errorThrown = false;

        try
        {
            auth.logoutAuth("auth-token-78");
        }
        catch (Exception e)
        {
            errorThrown = true;
        }

        Assertions.assertTrue(errorThrown);
    }

    @Test
    public void authVerifyAuthPasses()
    {
        BaseService.authAccess.setDB(Map.of(
            "user1", new ArrayList<>(Arrays.asList("auth-token-1"))
        ));

        boolean errorThrown = false;

        try
        {
            auth.verifyAuth("auth-token-1");
        }
        catch (Exception e)
        {
            errorThrown = true;
        }

        Assertions.assertFalse(errorThrown);
    }

    @Test
    public void authVerifyAuthThrows()
    {
        BaseService.authAccess.setDB(Map.of(
            "user1", new ArrayList<>(Arrays.asList("auth-token-1"))
        ));

        boolean errorThrown = false;

        try
        {
            auth.verifyAuth("auth-token-234");
        }
        catch (Exception e)
        {
            errorThrown = true;
        }

        Assertions.assertTrue(errorThrown);
    }

    @Test
    public void authGetUsernameGetsUsername()
    {
        BaseService.authAccess.setDB(Map.of(
            "user1", new ArrayList<>(Arrays.asList("auth-token-1"))
        ));

        Assertions.assertEquals("user1", auth.getUsername("auth-token-1"));
    }

    @Test
    public void authGetUsernameFailsOnNonexistentToken()
    {
        BaseService.authAccess.setDB(Map.of(
            "user1", new ArrayList<>(Arrays.asList("auth-token-1"))
        ));

        Assertions.assertEquals(null, auth.getUsername("auth-token-0"));
    }

    @Test
    public void authGetAllReturnsFullDbList()
    {
        BaseService.authAccess.setDB(Map.of(
            "user1", new ArrayList<>(Arrays.asList("auth-token-1")),
            "user2", new ArrayList<>(Arrays.asList("auth-token-453", "token-3462"))
        ));

        ArrayList<ArrayList<String>> expected = new ArrayList<>(Arrays.asList(
                new ArrayList<>(Arrays.asList("auth-token-1")),
                new ArrayList<>(Arrays.asList("auth-token-453", "token-3462"))
        ));

        Assertions.assertIterableEquals(expected, auth.getAll());
    }

    @Test
    public void authGetAllDoesNotReturnNull()
    {
        BaseService.authAccess.setDB(Map.of(
                "user1", new ArrayList<>(Arrays.asList("auth-token-1")),
                "user2", new ArrayList<>(Arrays.asList("auth-token-453", "token-3462"))
        ));

        Assertions.assertNotEquals(null, auth.getAll());
    }

    @Test
    public void authResetGoesThrough()
    {
        Assertions.assertDoesNotThrow(() -> auth.reset());
    }


    /**
     * =========================================================================================
     * UserService
     * =========================================================================================
     */

    @Test
    public void userRegisterCompletes()
    {
        RegisterResult actual = user.registerUser(new UserData("user1", "pw1", "e@mail.com"));

        RegisterResult expected = new RegisterResult(true, true, false, "any", "user1");

        Assertions.assertEquals(expected.createSucceeded, actual.createSucceeded);
        Assertions.assertEquals(expected.loginSucceeded, actual.loginSucceeded);
        Assertions.assertEquals(expected.userExists, actual.userExists);
        Assertions.assertEquals(expected.username, actual.username);
    }

    @Test
    public void userRegisterFailsDouble()
    {
        RegisterResult actual1 = user.registerUser(new UserData("user1", "pw1", "e@mail.com"));
        RegisterResult actual2 = user.registerUser(new UserData("user1", "pw1", "e@mail.com"));

        RegisterResult expected = new RegisterResult(true, true, false, "any", "user1");

        Assertions.assertEquals(expected.createSucceeded, actual1.createSucceeded);
        Assertions.assertEquals(expected.loginSucceeded, actual1.loginSucceeded);
        Assertions.assertEquals(expected.userExists, actual1.userExists);
        Assertions.assertEquals(expected.username, actual1.username);

        Assertions.assertTrue(actual2.userExists);
    }

    @Test
    public void userLoginUserCompletes()
    {
        UserData uData = new UserData("user1", "pw1", "e@mail.com");
        user.registerUser(uData);

        boolean errorThrown = false;

        try {
            user.loginUser(uData);
        }
        catch (Exception e)
        {
            errorThrown = true;
        }

        Assertions.assertFalse(errorThrown);
    }

    @Test
    public void userLoginUserDoesNotLoginNonexistentUser()
    {
        UserData uData = new UserData("user1", "pw1", "e@mail.com");
        user.registerUser(uData);

        boolean errorThrown = false;

        try {
            user.loginUser(new UserData("user2", "pw1", "e@mail.com"));
        }
        catch (Exception e)
        {
            errorThrown = true;
        }

        Assertions.assertTrue(errorThrown);
    }

    @Test
    public void userGetAllReturnsFullDbList()
    {
        BaseService.userAccess.setDB(Map.of(
            "user1", new UserData("user1", "pw1", "e@mail.com"),
            "user2", new UserData("user2", "pass", "email@internet.com")
        ));

        ArrayList<UserData> expected = new ArrayList<>(Arrays.asList(
                new UserData("user1", "pw1", "e@mail.com"),
                new UserData("user2", "pass", "email@internet.com")
        ));

        Assertions.assertIterableEquals(expected, user.getAll());
    }

    @Test
    public void userGetAllDoesNotReturnNullOrEmpty()
    {
        BaseService.userAccess.setDB(Map.of(
                "user1", new UserData("user1", "pw1", "e@mail.com"),
                "user2", new UserData("user2", "pass", "email@internet.com")
        ));

        Collection<UserData> actual = user.getAll();

        Assertions.assertNotEquals(null, actual);
        Assertions.assertEquals(2, actual.size());
    }

    @Test
    public void userResetGoesThrough()
    {
        Assertions.assertDoesNotThrow(() -> user.reset());
    }

    /**
     * =========================================================================================
     * GameService
     * =========================================================================================
     */

    @Test
    public void gameCreateGameCompletes()
    {
        Assertions.assertDoesNotThrow(() -> game.createGame("coolgame"));
    }

    @Test
    public void gameCreateGameThrows()
    {
        Assertions.assertDoesNotThrow(() -> game.createGame("coolgame"));
        Assertions.assertThrows(GameTakenException.class, () -> game.createGame("coolgame"));
    }

    @Test
    public void gameJoinGameCompletes()
    {
        try {
            game.createGame("coolgame");
        } catch (Exception e) {}

        Assertions.assertDoesNotThrow(() -> game.joinGame(1, "user1", "BLACK"));
    }

    @Test
    public void gameJoinGameThrows()
    {
        try {
            game.createGame("coolgame");
        } catch (Exception e) {}

        Assertions.assertDoesNotThrow(() -> game.joinGame(1, "user1", "BLACK"));
        Assertions.assertThrows(GameTakenException.class, () -> game.joinGame(1, "user2", "BLACK"));
    }

    @Test
    public void gameGetAllReturnsFullDbList()
    {
        BaseService.gameAccess.setDB(Map.of(
            1, new GameData(1, "userw", "userb", "game1"),
            2, new GameData(2, "userw1", "userb1", "game2")
        ));

        ArrayList<GameData> expected = new ArrayList<>(Arrays.asList(
            new GameData(1, "userw", "userb", "game1"),
            new GameData(2, "userw1", "userb1", "game2")
        ));

        Assertions.assertIterableEquals(expected, game.getAll());
    }

    @Test
    public void gameGetAllDoesNotReturnNullOrEmpty()
    {
        BaseService.gameAccess.setDB(Map.of(
                2345, new GameData(2345, "userw", "userb", "game1"),
                3456, new GameData(3456, "userw1", "userb1", "game2")
        ));

        Collection<GameData> actual = game.getAll();

        Assertions.assertNotEquals(null, actual);
        Assertions.assertEquals(2, actual.size());
    }
}
