package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.GameData;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 3/11/26: added for p4 database
 * 3/24/26: updated for p5 client - list is now List
 */
public class SQLGameDAO implements GameDAO {

    private final Gson gson;

    public SQLGameDAO() throws DataAccessException {
        this.gson = new GsonBuilder().serializeNulls().create();
        configureDatabase();
    }

    // Configure the database
    private void configureDatabase() throws DataAccessException {
        var createGamesTable = """
                CREATE TABLE IF NOT EXISTS games(
                    gameID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                    whiteUsername VARCHAR(255) NULL,
                    blackUsername VARCHAR(255) NULL,
                    gameName VARCHAR (255) NOT NULL,
                    game TEXT NOT NULL
                )
                """;
        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(createGamesTable)) {
            statement.executeUpdate();
        } catch (Exception ex) {
            throw new DataAccessException("unable to configure database", ex);
        }
    }

    // Interface function - added 3/11/26
    @Override
    public List<GameData> getListGames() throws DataAccessException {
        var listGames = "SELECT * FROM games";
        List<GameData> gamesList = new ArrayList<>();

        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(listGames);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                int gameID = rs.getInt("gameID");
                String gameName = rs.getString("gameName");
                String whitePlayer = rs.getString("whiteUsername");
                String blackPlayer = rs.getString("blackUsername");
                String gameJson = rs.getString("game");
                ChessGame game = gson.fromJson(gameJson, ChessGame.class);

                gamesList.add(new GameData(gameID, whitePlayer, blackPlayer, gameName, game));
            }
        } catch (Exception ex) {
            throw new DataAccessException("unable to get list of games", ex);
        }

        return gamesList;
    }

    // Interface function - added 3/11/26
    @Override
    public int createGame(String gameName) throws DataAccessException {
        var createGame = """
                INSERT INTO games(whiteUsername, blackUsername, gameName, game)
                VALUES (NULL, NULL, ?, ?)
                """;
        try (var conn = DatabaseManager.getConnection();
            var statement = conn.prepareStatement(createGame, Statement.RETURN_GENERATED_KEYS)) {

            ChessGame newGame = new ChessGame();
            String gameJson = gson.toJson(newGame);

            statement.setString(1, gameName);
            statement.setString(2, gameJson);
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                } else {
                    throw new DataAccessException("unable to get generated gameID");
                }
            }
        } catch (Exception ex) {
            throw new DataAccessException("unable to create game", ex);
        }
    }

    // Interface function - added 3/11/26
    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        var getGame = """
                SELECT gameID, whiteUsername, blackUsername, gameName, game
                FROM games
                WHERE gameID = ?
                """;
        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(getGame)) {
            statement.setInt(1, gameID);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    String gameNameDB = rs.getString("gameName");
                    String white = rs.getString("whiteUsername");
                    String black = rs.getString("blackUsername");
                    String gameJson = rs.getString("game");
                    ChessGame game = gson.fromJson(gameJson, ChessGame.class);

                    return new GameData(gameID, white, black, gameNameDB, game);
                }
            }
        } catch (Exception ex) {
            throw new DataAccessException("unable to get game", ex);
        }
        return null;
    }

    // Interface function - added 3/11/26
    @Override
    public void updateGame(GameData game) throws DataAccessException {
        String updateGame = """
            UPDATE games SET whiteUsername = ?, blackUsername = ?, gameName = ?, game = ?
            WHERE gameID = ?
            """;
        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(updateGame)) {

            statement.setString(1, game.whiteUsername());
            statement.setString(2, game.blackUsername());
            statement.setString(3, game.gameName());
            statement.setString(4, gson.toJson(game.game()));
            statement.setInt(5, game.gameID());
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new DataAccessException("Game with ID " + game.gameID() + " does not exist");
            }
        } catch (Exception ex) {
            throw new DataAccessException("unable to update game", ex);
        }
    }

    // Interface function - added 3/11/26
    @Override
    public void clear() throws DataAccessException {
        var clearGames = "DELETE FROM games";
        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(clearGames)) {
            statement.executeUpdate();
        } catch (Exception ex) {
            throw new DataAccessException("unable to clear games", ex);
        }
    }
}
