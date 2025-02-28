package service;

import java.util.Collection;

public class AuthService extends BaseService {

    public Collection<String> getAll()
    {
        return authAccess.getAllAsList();
    }
}
