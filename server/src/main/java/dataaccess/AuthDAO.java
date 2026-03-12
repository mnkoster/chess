package dataaccess;

import model.AuthData;

/**
 * 3/1/26: added createAuth() for p3 apis
 * 3/10/26: added DataAccessException for p4 database
 */
public interface AuthDAO {
    void createAuth(AuthData authData) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
    void clear() throws DataAccessException;
}
