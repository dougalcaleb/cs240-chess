package service;

import dataaccess.AuthException;

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

    public Collection<String> getAll()
    {
        return authAccess.getAllAsList();
    }
}
