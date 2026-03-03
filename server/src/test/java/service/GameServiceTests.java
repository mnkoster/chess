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

public class GameServiceTests {

    private GameDAO gameDAO;
    private static GameService gameService;
    private static UserService userService;
    private String PLAYER1_VALID_TOKEN;

    @BeforeEach
    public void setup() {
        UserDAO userDAO = new MemoryUserDAO();
        gameDAO = new MemoryGameDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        gameService = new GameService(gameDAO, authDAO);
        userService = new UserService(userDAO, authDAO);

        String USERNAME = "player1";
        String PASSWORD = "player1password";
        String EMAIL = "player1@email.em";
        userService.register(USERNAME, PASSWORD, EMAIL);
        LoginResult login = userService.login(USERNAME, PASSWORD);
        PLAYER1_VALID_TOKEN = login.authToken();
    }

    @Test
    public void createGame_positive() {
        int gameID = gameService.createGame(PLAYER1_VALID_TOKEN, "MyGame");
        assertEquals(1000, gameID);
    }

    @Test
    public void createGame_negative() {
        assertThrows(BadRequestException.class, () -> gameService.createGame(PLAYER1_VALID_TOKEN, ""));
    }

    @Test
    public void getListGames_positive() {
        gameService.createGame(PLAYER1_VALID_TOKEN, "game1");
        gameService.createGame(PLAYER1_VALID_TOKEN, "game2");
        gameService.createGame(PLAYER1_VALID_TOKEN, "game3");

        Collection<GameData> result = gameService.getListGames(PLAYER1_VALID_TOKEN);
        assertEquals(3, result.size());
    }

    @Test
    public void getListGames_negative() {
        assertThrows(UnauthorizedException.class, () ->
                gameService.getListGames("badToken"));
    }

    @Test
    public void joinGame_positive() {
        int gameID = gameService.createGame(PLAYER1_VALID_TOKEN, "theGame");
        gameService.joinGame(PLAYER1_VALID_TOKEN, gameID, "WHITE");
        GameData testGame = gameDAO.getGame(gameID);
        assertEquals("player1", testGame.whiteUsername());
    }

    @Test
    public void joinGame_negative() {
        String USERNAME = "player2";
        String PASSWORD = "player2password";
        String EMAIL = "player2@email.em";
        userService.register(USERNAME, PASSWORD, EMAIL);
        LoginResult login = userService.login(USERNAME, PASSWORD);
        String PLAYER2_VALID_TOKEN = login.authToken();
        
        int gameID = gameService.createGame(PLAYER1_VALID_TOKEN, "theWrongGame");
        gameService.joinGame(PLAYER2_VALID_TOKEN, gameID, "WHITE");
        assertThrows(AlreadyTakenException.class, () ->
                gameService.joinGame(PLAYER1_VALID_TOKEN, gameID, "WHITE"));
    }
}
