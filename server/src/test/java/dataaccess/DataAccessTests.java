package dataaccess;

import dataaccess.sql.SQLAuthDAO;
import dataaccess.sql.SQLGameDAO;
import dataaccess.sql.SQLUserDAO;
import dataaccess.struct.AuthDAO;
import dataaccess.struct.GameDAO;
import dataaccess.struct.UserDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

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

    /**
     * =========================================================================================
     * GameDAO
     * =========================================================================================
     */
}
