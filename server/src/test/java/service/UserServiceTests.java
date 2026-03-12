package service;

import dataaccess.*;
import handler.AlreadyTakenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import results.LoginResult;
import results.RegisterResult;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 3/2/26: added for p3 apis - unit tests
 */
public class UserServiceTests {

    private AuthDAO authDAO;
    private static UserService userService;
    private String playerValidToken1;
    private String username;
    private String password;
    private String email;

    @BeforeEach
    public void setup() {
        UserDAO userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        userService = new UserService(userDAO, authDAO);
        username = "player1";
        password = "player1password";
        email = "player1@email.em";
    }

    @Test
    public void registerPositive() throws DataAccessException {
        RegisterResult result = userService.register(username, password, email);

        playerValidToken1 = result.authToken();
        assertEquals(username, authDAO.getAuth(playerValidToken1).username());
    }

    @Test
    public void registerNegative() throws DataAccessException {
        String password2 = "password2";

        userService.register(username, password, email);
        assertThrows(AlreadyTakenException.class, () ->
                userService.register(username, password2, email));
    }

    @Test
    public void loginPositive() throws DataAccessException {
        RegisterResult result = userService.register(username, password, email);
        playerValidToken1 = result.authToken();
        LoginResult login = userService.login(username, password);

        playerValidToken1 = login.authToken();
        assertEquals(playerValidToken1, authDAO.getAuth(playerValidToken1).authToken());
    }

    @Test
    public void loginNegative() throws DataAccessException {
        RegisterResult result = userService.register(username, password, email);

        playerValidToken1 = result.authToken();
        assertThrows(UnauthorizedException.class, () ->
                userService.login(username, "wrongPassword"));
    }

    @Test
    public void logoutPositive() throws DataAccessException {
        RegisterResult result = userService.register(username, password, email);
        playerValidToken1 = result.authToken();
        LoginResult login = userService.login(username, password);
        playerValidToken1 = login.authToken();

        userService.logout(playerValidToken1);
        assertNull(authDAO.getAuth(playerValidToken1));
    }

    @Test
    public void logoutNegative() throws DataAccessException {
        RegisterResult result = userService.register(username, password, email);
        playerValidToken1 = result.authToken();
        LoginResult login = userService.login(username, password);

        playerValidToken1 = login.authToken();
        assertThrows(UnauthorizedException.class, () ->
                userService.logout("notAValidToken"));
    }
}
