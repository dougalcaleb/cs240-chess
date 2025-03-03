package service;

import dataaccess.DataAccessException;
import model.AuthData;
import model.RegisterResult;
import model.UserData;

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
            result.token = authAccess.createAuth(data);
            result.loginSucceeded = true;
        }
        catch (Exception e)
        {
            result.loginSucceeded = false;
            return result;
        }

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

        AuthData existing = authAccess.getAuthData(data.username);
        if (existing != null)
        {
            throw new RuntimeException("User is already logged in");
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
}
