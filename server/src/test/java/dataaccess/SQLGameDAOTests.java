package dataaccess;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.*;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class SQLGameDAOTests {

    SQLGameDAO gameDAO;

    @BeforeEach
    void setup() throws DataAccessException {
        gameDAO = new SQLGameDAO();
        gameDAO.clear();
    }

    @Test
    public void createGamePositive() throws DataAccessException {
        int gameID = gameDAO.createGame("TestGame");
        GameData result = gameDAO.getGame(gameID);
        assertNotNull(result);
        assertEquals("TestGame", result.gameName());
        assertNotNull(result.game());
    }

    @Test
    public void createGameNegative() {
        assertThrows(DataAccessException.class, () -> {
            gameDAO.createGame(null); // passing null gameName
        });
    }

    @Test
    public void getGamePositive() throws DataAccessException {
        int gameID = gameDAO.createGame("MyChessGame");
        GameData result = gameDAO.getGame(gameID);
        assertNotNull(result);
        assertEquals(gameID, result.gameID());
        assertEquals("MyChessGame", result.gameName());
    }

    @Test
    public void getGameNegative() throws DataAccessException {
        GameData result = gameDAO.getGame(-1); // nonexistent ID
        assertNull(result);
    }

    @Test
    public void getListGamesPositive() throws DataAccessException {
        gameDAO.createGame("Game1");
        gameDAO.createGame("Game2");
        Collection<GameData> games = gameDAO.getListGames();
        assertEquals(2, games.size());
    }

    @Test
    public void getListGamesNegative() throws DataAccessException {
        Collection<GameData> games = gameDAO.getListGames();
        assertTrue(games.isEmpty());
    }

    @Test
    public void updateGamePositive() throws DataAccessException {
        int gameID = gameDAO.createGame("OriginalGame");
        gameDAO.getGame(gameID);
        GameData updated = new GameData(
                gameID,
                "whitePlayer",
                "blackPlayer",
                "UpdatedGame",
                new ChessGame()
        );
        gameDAO.updateGame(updated);
        GameData result = gameDAO.getGame(gameID);
        assertEquals("UpdatedGame", result.gameName());
        assertEquals("whitePlayer", result.whiteUsername());
        assertEquals("blackPlayer", result.blackUsername());
    }

    @Test
    public void updateGameNegative() {
        assertThrows(DataAccessException.class, () -> {
            GameData invalidGame = new GameData(
                    -1,
                    "white",
                    "black",
                    "NonexistentGame",
                    new ChessGame()
            );
            gameDAO.updateGame(invalidGame);
        });
    }

    @Test
    public void clearPositive() throws DataAccessException {
        gameDAO.createGame("GameToDelete");
        gameDAO.clear();
        assertTrue(gameDAO.getListGames().isEmpty());
    }

    @Test
    public void clearNegative() {
        assertDoesNotThrow(() -> gameDAO.clear());
    }
}