package dataaccess.mem;

import dataaccess.struct.AuthDAO;
import model.AuthData;
import model.UserData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MemAuthDAO implements AuthDAO {

    private static final Map<String, String> db = new HashMap<>();

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    @Override
    public AuthData createAuth(UserData data) {
        String token = generateToken();
        MemAuthDAO.db.put(data.username, token);

        return new AuthData(token, data.username);
    }

    @Override
    public AuthData getAuthData(String username) {
        String retrieved = MemAuthDAO.db.get(username);

        if (retrieved != null)
        {
            return new AuthData(retrieved, username);
        }

        return null;
    }

    @Override
    public String getUsernameByToken(String token)
    {
        for (var entry : MemAuthDAO.db.entrySet())
        {
            if (entry.getValue().equals(token))
            {
                return entry.getKey();
            }
        }

        return null;
    }

    @Override
    public boolean tokenExists(String token)
    {
        for (String existingToken : MemAuthDAO.db.values())
        {
            if (existingToken.equals(token))
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean deleteAuthData(AuthData data) {
        MemAuthDAO.db.remove(data.username);

        return true;
    }

    @Override
    public boolean deleteAuthData(String token)
    {
        // funky one-liner removes all matching values (which theoretically there should never be more than one
        // of a given auth token, but just in case)
        while (MemAuthDAO.db.values().remove(token));

        return true;
    }

    @Override
    public Collection<String> getAllAsList()
    {
        return MemAuthDAO.db.values();
    }

    public void reset()
    {
        MemAuthDAO.db.clear();
    }
}
