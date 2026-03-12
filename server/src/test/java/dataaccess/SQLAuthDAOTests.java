package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class SQLAuthDAOTests {

    SQLAuthDAO authDAO;

    @BeforeEach
    void setup() throws DataAccessException {
        authDAO = new SQLAuthDAO();
        authDAO.clear();
    }

    @Test
    public void createAuthPositive() throws DataAccessException {
        AuthData auth = new AuthData("token1", "user1");
        authDAO.createAuth(auth);
        AuthData result = authDAO.getAuth("token1");
        assertNotNull(result);
        assertEquals("token1", result.authToken());
        assertEquals("user1", result.username());
    }

    @Test
    public void createAuthNegative() {
        assertThrows(DataAccessException.class, () -> authDAO.createAuth(null));
    }

    @Test
    public void getAuthPositive() throws DataAccessException {
        AuthData auth = new AuthData("token2", "user2");
        authDAO.createAuth(auth);
        AuthData result = authDAO.getAuth("token2");
        assertNotNull(result);
        assertEquals("token2", result.authToken());
        assertEquals("user2", result.username());
    }

    @Test
    public void getAuthNegative() throws DataAccessException {
        AuthData result = authDAO.getAuth("nonexistentToken");
        assertNull(result);
    }

    @Test
    public void deleteAuthPositive() throws DataAccessException {
        AuthData auth = new AuthData("tokenDel", "userDel");
        authDAO.createAuth(auth);
        authDAO.deleteAuth("tokenDel");
        AuthData result = authDAO.getAuth("tokenDel");
        assertNull(result);
    }

    @Test
    public void deleteAuthNegative() {
        assertDoesNotThrow(() -> {
            authDAO.deleteAuth("nonexistentToken"); // deleting missing token shouldn't crash
        });
    }

    @Test
    public void clearPositive() throws DataAccessException {
        authDAO.createAuth(new AuthData("t1", "u1"));
        authDAO.createAuth(new AuthData("t2", "u2"));
        authDAO.clear();
        assertNull(authDAO.getAuth("t1"));
        assertNull(authDAO.getAuth("t2"));
    }

    @Test
    public void clearNegative() {
        assertDoesNotThrow(() -> authDAO.clear());
    }
}