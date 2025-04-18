package dataaccess;

import dataaccess.sql.SQLAuthDAO;
import dataaccess.sql.SQLGameDAO;
import dataaccess.sql.SQLUserDAO;
import dataaccess.struct.AuthDAO;
import dataaccess.struct.GameDAO;
import dataaccess.struct.UserDAO;
import sharedmodel.AuthData;
import sharedmodel.GameData;
import sharedmodel.UserData;
import org.junit.jupiter.api.*;
import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class DataAccessTests {

    public static UserDAO userdao;
    public static AuthDAO authdao;
    public static GameDAO gamedao;

    @BeforeAll
    public static void init()
    {
        userdao = new SQLUserDAO();
        authdao = new SQLAuthDAO();
        gamedao = new SQLGameDAO();
    }

    @BeforeEach
    public void setup()
    {

    }

    @AfterEach
    public void teardown()
    {
        userdao.reset();
        authdao.reset();
        gamedao.reset();
    }

    /**
     * =========================================================================================
     * AuthDAO
     * =========================================================================================
     */

    @Test
    public void authCreateAuthReturnsNewAuth()
    {
        AuthData returnData = authdao.createAuth(
            new UserData("user1", "userpw", "email")
        );

        Assertions.assertNotNull(returnData);
        Assertions.assertNotNull(returnData.authToken);
    }

    @Test
    public void authCreateAuthInsertsToDb()
    {
        AuthData returnData = authdao.createAuth(
            new UserData("user2", "userpw", "email2")
        );

        boolean wasInserted = authdao.tokenExists(returnData.authToken);

        Assertions.assertTrue(wasInserted);
    }

    @Test
    public void authDeleteAuthCompletes()
    {
        AuthData newAuth = authdao.createAuth(
            new UserData("user1", "userpw", "email")
        );

        boolean result = authdao.deleteAuthData(newAuth.authToken);
        boolean tokenExists = authdao.tokenExists(newAuth.authToken);

        Assertions.assertTrue(result);
        Assertions.assertFalse(tokenExists);
    }

    @Test
    public void authDeleteAuthDoesNotDeleteNonexistent()
    {
        boolean result = authdao.deleteAuthData("my-cool-nonexistent-token");
        Assertions.assertTrue(result);
        Assertions.assertDoesNotThrow(() -> authdao.deleteAuthData("my-cool-nonexistent-token"));
    }

    @Test
    public void authTokenExistsFindsToken()
    {
        AuthData newAuth = authdao.createAuth(
            new UserData("user1", "userpw", "email")
        );

        boolean tokenExists = authdao.tokenExists(newAuth.authToken);

        Assertions.assertTrue(tokenExists);
    }

    @Test
    public void authTokenExistsDoesNotFindNonexistent()
    {
        AuthData newAuth = authdao.createAuth(
            new UserData("user1", "userpw", "email")
        );

        boolean tokenExists = authdao.tokenExists("nonexistent-token");

        Assertions.assertFalse(tokenExists);
    }

    @Test
    public void authGetUsernameByTokenReturns()
    {
        AuthData newAuth1 = authdao.createAuth(
            new UserData("newuser", "userpw", "eeeemail")
        );
        AuthData newAuth2 = authdao.createAuth(
                new UserData("anotheruser", "pass", "email")
        );
        AuthData newAuth3 = authdao.createAuth(
                new UserData("cooluser", "password", "mail")
        );

        String username = authdao.getUsernameByToken(newAuth2.authToken);

        Assertions.assertEquals("anotheruser", username);
    }

    @Test
    public void authGetUsernameByTokenDoesNotFindNonexistent()
    {
        AuthData newAuth1 = authdao.createAuth(
                new UserData("newuser", "userpw", "eeeemail")
        );
        AuthData newAuth2 = authdao.createAuth(
                new UserData("anotheruser", "pass", "email")
        );

        String username = authdao.getUsernameByToken("nobody-has-this-token");

        Assertions.assertNull(username);
    }

    @Test
    public void authGetAllReturnsFullDbList()
    {
        authdao.setDB(Map.of(
            "user1", new ArrayList<>(Arrays.asList("auth-token-1")),
            "user2", new ArrayList<>(Arrays.asList("auth-token-453", "token-3462"))
        ));

        ArrayList<ArrayList<String>> expected = new ArrayList<>(Arrays.asList(
                new ArrayList<>(Arrays.asList("auth-token-1")),
                new ArrayList<>(Arrays.asList("auth-token-453", "token-3462"))
        ));

        Assertions.assertIterableEquals(expected, authdao.getAllAsList());
    }

    @Test
    public void authGetAllDoesNotReturnNull()
    {
        authdao.setDB(Map.of(
            "user1", new ArrayList<>(Arrays.asList("auth-token-1")),
            "user2", new ArrayList<>(Arrays.asList("auth-token-453", "token-3462"))
        ));

        Assertions.assertNotNull(authdao.getAllAsList());
    }

    /**
     * =========================================================================================
     * UserDAO
     * =========================================================================================
     */

    @Test
    public void userGetUserReturnsUser()
    {
        userdao.setDB(Map.of(
            "user1", new UserData("user1", "pw1", "email"),
            "user2", new UserData("user2", "pw2", "email2")
        ));

        AtomicReference<UserData> data = new AtomicReference<>();
        Assertions.assertDoesNotThrow(() -> {
            data.set(userdao.getUser("user2"));
        });

        Assertions.assertEquals(new UserData("user2", "pw2", "email2"), data.get());
    }

    @Test
    public void userGetUserDoesNotReturnNonexistent()
    {
        userdao.setDB(Map.of(
            "user1", new UserData("user1", "pw1", "email"),
            "user2", new UserData("user2", "pw2", "email2")
        ));

        Assertions.assertThrows(RuntimeException.class, () -> userdao.getUser("cooluser"));
    }

    @Test
    public void userSetUserInsertsUser()
    {
        Assertions.assertDoesNotThrow(
            () -> userdao.setUser(new UserData("user1", "pw1", "email"))
        );
        Assertions.assertDoesNotThrow(
            () -> userdao.getUser("user1")
        );
    }

    @Test
    public void userCannotInsertDuplicateUser()
    {
        Assertions.assertDoesNotThrow(
            () -> userdao.setUser(new UserData("user1", "pw1", "email"))
        );
        Assertions.assertThrows(
            RuntimeException.class,
            () -> userdao.setUser(new UserData("user1", "pw1", "email"))
        );
    }

    @Test
    public void userUserExistsReturnsUser()
    {
        userdao.setDB(Map.of(
            "user1", new UserData("user1", "pw1", "email"),
            "user2", new UserData("user2", "pw2", "email2")
        ));

        boolean result = userdao.userExists(new UserData("user1", "pw1", "email"));
        Assertions.assertTrue(result);
    }

    @Test
    public void userUserDoesNotReturnNonexistent()
    {
        userdao.setDB(Map.of(
            "user1", new UserData("user1", "pw1", "email"),
            "user2", new UserData("user2", "pw2", "email2")
        ));

        boolean result = userdao.userExists(new UserData("superuser", "pass", "email"));
        Assertions.assertFalse(result);
    }

    @Test
    public void userGetAllReturnsFullDbList()
    {
        userdao.setDB(Map.of(
            "user1", new UserData("user1", "pw1", "e@mail.com"),
            "user2", new UserData("user2", "pass", "email@internet.com")
        ));

        ArrayList<UserData> expected = new ArrayList<>(Arrays.asList(
                new UserData("user1", "pw1", "e@mail.com"),
                new UserData("user2", "pass", "email@internet.com")
        ));

        Assertions.assertIterableEquals(expected, userdao.getAllAsList());
    }

    @Test
    public void userGetAllDoesNotReturnNullOrEmpty()
    {
        userdao.setDB(Map.of(
            "user1", new UserData("user1", "pw1", "e@mail.com"),
            "user2", new UserData("user2", "pass", "email@internet.com")
        ));

        Collection<UserData> actual = userdao.getAllAsList();

        Assertions.assertNotEquals(null, actual);
        Assertions.assertEquals(2, actual.size());
    }

    @Test
    public void userMatchesUsernamePassword()
    {
        userdao.setDB(Map.of(
            "user1", new UserData("user1", BCrypt.hashpw("pw1", BCrypt.gensalt()), "e@mail.com"),
            "user2", new UserData("user2", "pass", "email@internet.com")
        ));

        AtomicBoolean result = new AtomicBoolean();
        Assertions.assertDoesNotThrow(
            () -> result.set(userdao.matchUsernamePassword(new UserData("user1", "pw1", "email")))
        );

        Assertions.assertTrue(result.get());
    }

    @Test
    public void userMatchDoesNotMatchIncorrectly()
    {
        userdao.setDB(Map.of(
                "user1", new UserData("user1", BCrypt.hashpw("password", BCrypt.gensalt()), "e@mail.com"),
                "user2", new UserData("user2", "pass", "email@internet.com")
        ));

        AtomicBoolean result = new AtomicBoolean();
        Assertions.assertDoesNotThrow(
                () -> result.set(userdao.matchUsernamePassword(new UserData("user1", "pw1", "email")))
        );

        Assertions.assertFalse(result.get());
    }


    /**
     * =========================================================================================
     * GameDAO
     * =========================================================================================
     */

    @Test
    public void gameSetGameReturnsNewGameID()
    {
        int id = gamedao.setGame(
            new GameData(999, "wUser", "bUser", "game1")
        );
        Assertions.assertEquals(1, id);
    }

    @Test
    public void gameSetGameCanInsertDuplicateGames()
    {
        int id1 = gamedao.setGame(
            new GameData(999, "wUser", "bUser", "game1")
        );
        Assertions.assertEquals(1, id1);

        int id2 = gamedao.setGame(
            new GameData(999, "wUser", "bUser", "game1")
        );
        Assertions.assertEquals(2, id2);
    }

    @Test
    public void gameGameExistsStringFindsExisting()
    {
        gamedao.setDB(Map.of(
            1, new GameData(1, "userw", "userb", "game1"),
            2, new GameData(2, "userw1", "userb1", "game2")
        ));

        boolean result = gamedao.gameExists("game1");

        Assertions.assertTrue(result);
    }

    @Test
    public void gameGameExistsGameIDFindsExisting()
    {
        gamedao.setDB(Map.of(
                1, new GameData(1, "userw", "userb", "game1"),
                2, new GameData(2, "userw1", "userb1", "game2")
        ));

        boolean result = gamedao.gameExists(2);

        Assertions.assertTrue(result);
    }

    @Test
    public void gameGameExistsStringDoesNotFindNonexistent()
    {
        gamedao.setDB(Map.of(
                1, new GameData(1, "userw", "userb", "game1"),
                2, new GameData(2, "userw1", "userb1", "game2")
        ));

        boolean result = gamedao.gameExists("coolgame");

        Assertions.assertFalse(result);
    }

    @Test
    public void gameGameExistsGameIDDoesNotFindNonexistent()
    {
        gamedao.setDB(Map.of(
                1, new GameData(1, "userw", "userb", "game1"),
                2, new GameData(2, "userw1", "userb1", "game2")
        ));

        boolean result = gamedao.gameExists(24);

        Assertions.assertFalse(result);
    }

    @Test
    public void gameGetGameFindsGame()
    {
        gamedao.setDB(Map.of(
            1, new GameData(1, "userw", "userb", "game1"),
            2, new GameData(2, "userw1", "userb1", "game2")
        ));

        GameData result = gamedao.getGame(2);

        Assertions.assertEquals(
                new GameData(2, "userw1", "userb1", "game2"),
                result
        );
    }

    @Test
    public void gameGetGameDoesNotFindNonexistent()
    {
        gamedao.setDB(Map.of(
            1, new GameData(1, "userw", "userb", "game1"),
            2, new GameData(2, "userw1", "userb1", "game2")
        ));

        GameData result = gamedao.getGame(224);

        Assertions.assertNull(result);
    }

    @Test
    public void gameSetPlayerColorCompletes()
    {
        gamedao.setDB(Map.of(
            1, new GameData(1, null, null, "game1"),
            2, new GameData(2, null, null, "game2")
        ));

        gamedao.setPlayerColor(2, "whiteuser", "WHITE");

        GameData data = gamedao.getGame(2);

        Assertions.assertEquals("whiteuser", data.whiteUsername);
        Assertions.assertNull(data.blackUsername);
    }

    @Test
    public void gameSetPlayerColorDoesNotUpdateNonexistent()
    {
        gamedao.setDB(Map.of(
            1, new GameData(1, null, null, "game1"),
            2, new GameData(2, null, null, "game2")
        ));

        gamedao.setPlayerColor(200, "whiteuser", "WHITE");

        GameData data1 = gamedao.getGame(1);
        GameData data2 = gamedao.getGame(2);

        Assertions.assertNull(data1.whiteUsername);
        Assertions.assertNull(data1.blackUsername);
        Assertions.assertNull(data2.whiteUsername);
        Assertions.assertNull(data2.blackUsername);
    }
}
