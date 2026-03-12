package service;

import dataaccess.*;
import handler.AlreadyTakenException;
import handler.BadRequestException;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import results.LoginResult;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 3/2/26: added for p3 apis - unit tests
 */
public class GameServiceTests {

    private GameDAO gameDAO;
    private static GameService gameService;
    private static UserService userService;
    private String playerValidToken1;

    @BeforeEach
    public void setup() throws DataAccessException {
        UserDAO userDAO = new MemoryUserDAO();
        gameDAO = new MemoryGameDAO();
        AuthDAO authDAO = new MemoryAuthDAO();

        gameService = new GameService(gameDAO, authDAO);
        userService = new UserService(userDAO, authDAO);

        String username = "player1";
        String password = "player1password";
        String email = "player1@email.em";
        userService.register(username, password, email);
        LoginResult login = userService.login(username, password);
        playerValidToken1 = login.authToken();
    }

    @Test
    public void createGamePositive() throws DataAccessException {
        int gameID = gameService.createGame(playerValidToken1, "MyGame");
        assertEquals(1000, gameID);
    }

    @Test
    public void createGameNegative() {
        assertThrows(BadRequestException.class, () -> gameService.createGame(playerValidToken1, ""));
    }

    @Test
    public void getListGamesPositive() throws DataAccessException {
        gameService.createGame(playerValidToken1, "game1");
        gameService.createGame(playerValidToken1, "game2");
        gameService.createGame(playerValidToken1, "game3");

        Collection<GameData> result = gameService.getListGames(playerValidToken1);
        assertEquals(3, result.size());
    }

    @Test
    public void getListGamesNegative() {
        assertThrows(UnauthorizedException.class, () ->
                gameService.getListGames("badToken"));
    }

    @Test
    public void joinGamePositive() throws DataAccessException {
        int gameID = gameService.createGame(playerValidToken1, "theGame");
        gameService.joinGame(playerValidToken1, gameID, "WHITE");

        GameData testGame = gameDAO.getGame(gameID);
        assertEquals("player1", testGame.whiteUsername());
    }

    @Test
    public void joinGameNegative() throws DataAccessException {
        String username = "player2";
        String password = "player2password";
        String email = "player2@email.em";

        userService.register(username, password, email);
        LoginResult login = userService.login(username, password);
        String playerValidToken2 = login.authToken();
        int gameID = gameService.createGame(playerValidToken1, "theWrongGame");

        gameService.joinGame(playerValidToken2, gameID, "WHITE");
        assertThrows(AlreadyTakenException.class, () ->
                gameService.joinGame(playerValidToken1, gameID, "WHITE"));
    }
}
