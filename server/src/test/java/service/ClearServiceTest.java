package service;

import dataaccess.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import results.LoginResult;

import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTest {

    private GameDAO gameDAO;
    private static GameService gameService;
    private static UserService userService;
    private static ClearService clearService;
    private static String PLAYER1_VALID_TOKEN;

    @BeforeEach
    public void setup() {
        UserDAO userDAO = new MemoryUserDAO();
        gameDAO = new MemoryGameDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        gameService = new GameService(gameDAO, authDAO);
        userService = new UserService(userDAO, authDAO);
        clearService = new ClearService(userDAO, authDAO, gameDAO);

        String USERNAME = "player1";
        String PASSWORD = "player1password";
        String EMAIL = "player1@email.em";
        userService.register(USERNAME, PASSWORD, EMAIL);
        LoginResult login = userService.login(USERNAME, PASSWORD);
        PLAYER1_VALID_TOKEN = login.authToken();

        gameService.createGame(PLAYER1_VALID_TOKEN, "game1");
        gameService.createGame(PLAYER1_VALID_TOKEN, "game2");
        int gameID = gameService.createGame(PLAYER1_VALID_TOKEN, "game3");

        gameService.joinGame(PLAYER1_VALID_TOKEN, gameID, "WHITE");
    }

    @Test
    public void clearPositive() {
        clearService.clear();

        assertTrue(gameDAO.getListGames().isEmpty(), "Games should be empty");
        assertThrows(UnauthorizedException.class, () -> userService.login("player1", "player1password"));
        assertThrows(UnauthorizedException.class, () -> gameService.getListGames(PLAYER1_VALID_TOKEN));
    }
}
