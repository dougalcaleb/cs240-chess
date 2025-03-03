package service;

import dataaccess.DataAccessException;

import java.util.Collection;

public class AuthService extends BaseService {

    public void logoutAuth(String token) throws DataAccessException
    {
        if (!authAccess.tokenExists(token))
        {
            throw new DataAccessException("Error: unauthorized");
        }

        authAccess.deleteAuthData(token);
    }

    public Collection<String> getAll()
    {
        return authAccess.getAllAsList();
    }
}
