package dataaccess.mem;

import dataaccess.DataAccessException;
import dataaccess.struct.UserDAO;
import model.UserData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MemUserDAO implements UserDAO {

    private static final Map<String, UserData> db = new HashMap<>();

    @Override
    public UserData getUser(String username) throws DataAccessException
    {
        UserData data = MemUserDAO.db.get(username);

        if (data == null)
        {
            throw new DataAccessException("Could not get user - user does not exist.");
        }

        return data;
    }

    @Override
    public boolean userExists(UserData data) {
        return MemUserDAO.db.containsKey(data.username);
    }

    @Override
    public void setUser(UserData data) throws DataAccessException
    {
        if (MemUserDAO.db.get(data.username) != null)
        {
            throw new DataAccessException("Could not set user - user already exists");
        }

        MemUserDAO.db.put(data.username, data);
    }

    @Override
    public void deleteUser(UserData data) throws DataAccessException
    {
        if (MemUserDAO.db.get(data.username) == null)
        {
            throw new DataAccessException("Could not delete user - user does not exist.");
        }

        MemUserDAO.db.remove(data.username);
    }

    public Collection<UserData> getAllAsList()
    {
        return MemUserDAO.db.values();
    }
}
