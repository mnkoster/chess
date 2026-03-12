package dataaccess;

import model.GameData;

import java.util.Collection;

/**
 * 3/1/26: added clear for /db
 * 3/2/26: added game functions for p3 apis
 * 3/11/26: added for p4 database
 */
public interface GameDAO {
    Collection<GameData> getListGames() throws DataAccessException;
    int createGame(String gameName) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    void updateGame(GameData game) throws DataAccessException;
    void clear() throws DataAccessException;
}
