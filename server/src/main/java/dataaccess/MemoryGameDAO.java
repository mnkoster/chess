package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.*;

/**
 * 3/1/26: added for p3 apis - clear() first for /db
 * 3/2/26: added for p3 apis - getGames(), createGame(), getGame(), updateGame()
 * 3/24/26: updated for p5 client - list is now List
 */
public class MemoryGameDAO implements GameDAO {

    private final List<GameData> games = new ArrayList<>();
    private int nextGameID = 1;

    @Override
    public List<GameData> getListGames() {
        return games;
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        int id = nextGameID;
        nextGameID++;
        GameData game = new GameData(id, null, null, gameName, new ChessGame(), false);
        games.add(game);
        return id;
    }

    @Override
    public GameData getGame(int gameID) {
        for (GameData game : games) {
            if (game.gameID() == gameID) {
                return game;
            }
        }
        return null;
    }

    @Override
    public void updateGame(GameData updatedGame) {
        for (int i = 0; i < games.size(); i++) {
            if (games.get(i).gameID() == updatedGame.gameID()) {
                games.set(i, updatedGame);
                return;
            }
        }
    }

    @Override
    public void clear() throws DataAccessException {
        games.clear();
    }
}
