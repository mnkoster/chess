package service;

import dataaccess.*;
import handler.AlreadyTakenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import results.LoginResult;
import results.RegisterResult;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTests {

    private UserDAO userDAO;
    private AuthDAO authDAO;
    private static UserService userService;
    private String playerValidToken1;
    private String username;
    private String password;
    private String email;

    @BeforeEach
    public void setup() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        userService = new UserService(userDAO, authDAO);
        username = "player1";
        password = "player1password";
        email = "player1@email.em";
    }

    @Test
    public void registerPositive() {
        RegisterResult result = userService.register(username, password, email);
        playerValidToken1 = result.authToken();

        assertEquals(username, authDAO.getAuth(playerValidToken1).username());
    }

    @Test
    public void registerNegative() {
        String password2 = "password2";
        userService.register(username, password, email);
        assertThrows(AlreadyTakenException.class, () ->
                userService.register(username, password, email));
    }

    @Test
    public void loginPositive() {

    }

    @Test
    public void loginNegative() {

    }

    @Test
    public void logoutPositive() {

    }

    @Test
    public void logoutNegative() {

    }
}
