package service;

import exceptions.AuthException;

import java.util.Collection;

public class AuthService extends BaseService {

    public void logoutAuth(String token) throws AuthException
    {
        if (!authAccess.tokenExists(token))
        {
            throw new AuthException("Error: unauthorized");
        }

        authAccess.deleteAuthData(token);
    }

    public void verifyAuth(String token) throws AuthException
    {
        if (!authAccess.tokenExists(token))
        {
            throw new AuthException();
        }
    }

    public String getUsername(String token)
    {
        return authAccess.getUsernameByToken(token);
    }

    public Collection<String> getAll()
    {
        return authAccess.getAllAsList();
    }

    public void reset()
    {
        authAccess.reset();
    }
}
