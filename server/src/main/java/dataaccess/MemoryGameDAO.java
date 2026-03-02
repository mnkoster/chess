package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 3/1/26: added for p3 apis - clear first for /db
 * 3/2/26: added getGames()
 */
public class MemoryGameDAO implements GameDAO {

    private final Map<Integer, GameData> games = new HashMap<>();

    @Override
    public Collection<GameData> getListGames() {
        return games.values();
    }

    @Override
    public void clear() { games.clear(); }
}
