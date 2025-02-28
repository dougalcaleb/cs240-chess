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
    public boolean deleteAuthData(AuthData data) {
        MemAuthDAO.db.remove(data.username);

        return true;
    }

    @Override
    public Collection<String> getAllAsList()
    {
        return MemAuthDAO.db.values();
    }
}
