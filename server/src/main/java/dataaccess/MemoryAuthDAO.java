package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.Map;

/**
 * 3/1/26: added for p3 apis
 */
public class MemoryAuthDAO implements AuthDAO {

    private final Map<String, AuthData> authTokens = new HashMap<>();

    @Override
    public void createAuth(AuthData authData) {
        authTokens.put(authData.authToken(), authData);
    }

    @Override
    public AuthData getAuth(String authToken) {
        return authTokens.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) {
        authTokens.remove(authToken);
    }

    @Override
    public String getUsername(String authToken) throws DataAccessException {
        AuthData auth = authTokens.get(authToken);
        if (auth == null) {
            throw new DataAccessException("Invalid auth token");
        }
        return auth.username();
    }

    @Override
    public void clear() {
        authTokens.clear();
    }
}