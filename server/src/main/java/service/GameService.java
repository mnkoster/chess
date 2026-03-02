package service;

import java.util.Collection;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UnauthorizedException;
import handler.BadRequestException;
import model.AuthData;
import model.GameData;

/**
 * 3/2/26: added for p3 apis (games)
 */
public class GameService {

    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public Collection<GameData> getListGames(String authToken) {
        if (authToken == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }

        return gameDAO.getListGames();
    }

    public int createGame(String authToken, String gameName) {
        if (authToken == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }

        if (gameName == null || gameName.isBlank()) {
            throw new BadRequestException("Error: bad request");
        }

        return gameDAO.createGame(gameName);
    }
}
