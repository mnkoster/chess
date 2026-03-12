package dataaccess;

import model.UserData;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 3/11/26: added for p4 database - unit tests
 */
public class SQLUserDAOTests {

    SQLUserDAO userDAO;

    @BeforeEach
    void setup() throws DataAccessException {
        userDAO = new SQLUserDAO();
        userDAO.clear();
    }

    @Test
    public void addUserPositive() throws DataAccessException {
        UserData user = new UserData("testUser", "password123", "test@email.com");
        userDAO.addUser(user);
        UserData result = userDAO.getUser("testUser");

        assertNotNull(result);
        assertEquals("testUser", result.username());
        assertEquals("password123", result.password());
        assertEquals("test@email.com", result.email());
    }

    @Test
    public void addUserNegative() {
        assertThrows(DataAccessException.class, () -> userDAO.addUser(null));
    }

    @Test
    public void getUserPositive() throws DataAccessException {
        UserData user = new UserData("bob", "pass", "bob@email.com");
        userDAO.addUser(user);
        UserData result = userDAO.getUser("bob");

        assertNotNull(result);
        assertEquals("bob", result.username());
        assertEquals("pass", result.password());
        assertEquals("bob@email.com", result.email());
    }

    @Test
    public void getUserNegative() throws DataAccessException {
        UserData result = userDAO.getUser("doesNotExist");

        assertNull(result);
    }

    @Test
    public void clearPositive() throws DataAccessException {
        UserData user = new UserData("alice", "pass", "alice@email.com");
        userDAO.addUser(user);
        userDAO.clear();
        UserData result = userDAO.getUser("alice");

        assertNull(result);
    }

    @Test
    public void clearNegative() {
        assertDoesNotThrow(() -> userDAO.clear());
    }
}
