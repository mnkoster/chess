package dataaccess;

import model.AuthData;

/**
 * 3/1/26: added createAuth() for p3 apis
 */
public interface AuthDAO {
    void createAuth(AuthData authData);
    AuthData getAuth(String authToken);
    void deleteAuth(String authToken);
    void clear();
}
