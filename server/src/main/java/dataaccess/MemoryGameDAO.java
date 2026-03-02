package dataaccess;

import model.GameData;
import java.util.HashMap;
import java.util.Map;

/**
 * 3/1/26: added for p3 apis - clear first for /db
 */
public class MemoryGameDAO implements GameDAO {

    private final Map<Integer, GameData> games = new HashMap<>();

    @Override
    public void clear() {
        games.clear();
    }
}
