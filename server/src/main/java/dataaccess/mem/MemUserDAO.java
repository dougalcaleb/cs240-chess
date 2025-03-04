package dataaccess.mem;

import dataaccess.struct.UserDAO;
import exceptions.DataAccessException;
import model.UserData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MemUserDAO implements UserDAO {

    private static final Map<String, UserData> DB = new HashMap<>();

    @Override
    public UserData getUser(String username) throws DataAccessException
    {
        UserData data = MemUserDAO.DB.get(username);

        if (data == null)
        {
            throw new DataAccessException("Could not get user - user does not exist.");
        }

        return data;
    }

    @Override
    public boolean matchUsernamePassword(UserData data) throws DataAccessException
    {
        UserData retrieved = MemUserDAO.DB.get(data.username);

        if (retrieved == null)
        {
            throw new DataAccessException("Could not get user - user does not exist");
        }

        return retrieved.password.equals(data.password);
    }

    @Override
    public boolean userExists(UserData data) {
        return MemUserDAO.DB.containsKey(data.username);
    }

    @Override
    public void setUser(UserData data) throws DataAccessException
    {
        if (MemUserDAO.DB.get(data.username) != null)
        {
            throw new DataAccessException("Could not set user - user already exists");
        }

        MemUserDAO.DB.put(data.username, data);
    }

    @Override
    public Collection<UserData> getAllAsList()
    {
        return MemUserDAO.DB.values();
    }

    @Override
    public void reset()
    {
        MemUserDAO.DB.clear();
    }
}
