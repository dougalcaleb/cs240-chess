package dataaccess.mem;

import dataaccess.struct.AuthDAO;
import model.AuthData;
import model.UserData;

import java.util.*;

public class MemAuthDAO implements AuthDAO {

    private static final Map<String, ArrayList<String>> DB = new HashMap<>();

    // exclusively for testing purposes
    public void setDB(Map<String, ArrayList<String>> value)
    {
        MemAuthDAO.DB.clear();
        MemAuthDAO.DB.putAll(value);
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    @Override
    public AuthData createAuth(UserData data) {
        String token = generateToken();

        ArrayList<String> existingTokens = MemAuthDAO.DB.get(data.username);

        if (existingTokens == null)
        {
            existingTokens = new ArrayList<>();
        }

        existingTokens.add(token);

        MemAuthDAO.DB.put(data.username, existingTokens);

        return new AuthData(token, data.username);
    }

    @Override
    public String getUsernameByToken(String token)
    {
        for (var entry : MemAuthDAO.DB.entrySet())
        {
            for (String existingToken : entry.getValue())
            {
                if (existingToken.equals(token))
                {
                    return entry.getKey();
                }
            }
        }

        return null;
    }

    @Override
    public boolean tokenExists(String token)
    {
        for (ArrayList<String> existingTokens : MemAuthDAO.DB.values())
        {
            for (String existingToken : existingTokens)
            {
                if (existingToken.equals(token)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean deleteAuthData(String token)
    {
        for (var entry : MemAuthDAO.DB.entrySet())
        {
            for (String existingToken : entry.getValue())
            {
                if (existingToken.equals(token)) {
                    entry.getValue().remove(token);
                    break;
                }
            }
        }

        return true;
    }

    @Override
    public Collection<ArrayList<String>> getAllAsList()
    {
        return MemAuthDAO.DB.values();
    }

    @Override
    public void reset()
    {
        MemAuthDAO.DB.clear();
    }
}
