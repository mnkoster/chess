package service;

import dataaccess.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import results.LoginResult;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 3/2/26: added for p3 apis - unit tests
 */
public class ClearServiceTest {

    private GameDAO gameDAO;
    private static GameService gameService;
    private static UserService userService;
    private static ClearService clearService;
    private static String playerValidToken1;

    @BeforeEach
    public void setup() throws DataAccessException {
        UserDAO userDAO = new MemoryUserDAO();
        gameDAO = new MemoryGameDAO();
        AuthDAO authDAO = new MemoryAuthDAO();

        gameService = new GameService(gameDAO, authDAO);
        userService = new UserService(userDAO, authDAO);
        clearService = new ClearService(userDAO, authDAO, gameDAO);

        String username = "player1";
        String password = "player1password";
        String email = "player1@email.em";
        userService.register(username, password, email);
        LoginResult login = userService.login(username, password);
        playerValidToken1 = login.authToken();

        gameService.createGame(playerValidToken1, "game1");
        gameService.createGame(playerValidToken1, "game2");
        int gameID = gameService.createGame(playerValidToken1, "game3");

        gameService.joinGame(playerValidToken1, gameID, "WHITE");
    }

    @Test
    public void clearPositive() throws DataAccessException {
        clearService.clear();

        assertTrue(gameDAO.getListGames().isEmpty(), "Games should be empty");
        assertThrows(UnauthorizedException.class, () -> userService.login("player1", "player1password"));
        assertThrows(UnauthorizedException.class, () -> gameService.getListGames(playerValidToken1));
    }
}
