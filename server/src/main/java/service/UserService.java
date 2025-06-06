package service;

import exceptions.DataAccessException;
import sharedmodel.AuthData;
import sharedmodel.RegisterResult;
import sharedmodel.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Collection;

public class UserService extends BaseService {

    public RegisterResult registerUser(UserData data)
    {
        RegisterResult result = new RegisterResult();

        if (userAccess.userExists(data))
        {
            result.userExists = true;
            return result;
        }

        try
        {
            data.password = BCrypt.hashpw(data.password, BCrypt.gensalt());
            userAccess.setUser(data);
            result.createSucceeded = true;
        }
        catch (DataAccessException e)
        {
            result.createSucceeded = false;
            return result;
        }

        try
        {
            result.authToken = authAccess.createAuth(data).authToken;
            result.loginSucceeded = true;
        }
        catch (Exception e)
        {
            result.loginSucceeded = false;
            return result;
        }

        result.username = data.username;

        return result;
    }

    public AuthData loginUser(UserData data) throws DataAccessException, RuntimeException
    {
        try
        {
            if (!userAccess.matchUsernamePassword(data))
            {
                return null;
            }
        }
        catch (DataAccessException e)
        {
            throw new DataAccessException("User does not exist");
        }

        try
        {
            return authAccess.createAuth(data);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public Collection<UserData> getAll()
    {
        return userAccess.getAllAsList();
    }

    public void reset()
    {
        userAccess.reset();
    }
}
