package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 3/1/26: added for p3 apis - clear first for /db
 * 3/2/26: added getGames(), createGame(), getGame(), updateGame()
 */
public class MemoryGameDAO implements GameDAO {

    private final Map<Integer, GameData> games = new HashMap<>();
    private int nextGameID = 1;

    @Override
    public Collection<GameData> getListGames() {
        return games.values();
    }

    @Override
    public int createGame(String gameName) {
        int id = nextGameID;
        nextGameID++;
        GameData game = new GameData(id, null, null, gameName, new ChessGame());
        games.put(id, game);
        return id;
    }

    @Override
    public GameData getGame(int gameID) {
        return games.get(gameID);
    }

    @Override
    public void updateGame(GameData game) {
        games.put(game.gameID(), game);
    }

    @Override
    public void clear() { games.clear(); }
}
