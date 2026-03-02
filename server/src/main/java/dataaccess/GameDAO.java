package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.Map;

/**
 * 3/1/26: added clear for /db
 * 3/2/26: added game functions for listGames, createGame
 */
public interface GameDAO {
    Collection<GameData> getListGames();
    int createGame(String gameName);
    void clear();
}
