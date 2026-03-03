package service;

import java.util.Collection;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UnauthorizedException;
import handler.AlreadyTakenException;
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

    public void joinGame(String authToken, int gameID, String playerColor) {
        if (authToken == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }

        String username = auth.username();
        GameData game = gameDAO.getGame(gameID);
        if (game == null) {
            throw new BadRequestException("Error: bad request");
        }
        if (playerColor.equalsIgnoreCase("WHITE")) {
            if (game.whiteUsername() != null) {
                throw new AlreadyTakenException("Error: already taken");
            }
            game = new GameData(
                    game.gameID(),
                    username,
                    game.blackUsername(),
                    game.gameName(),
                    new ChessGame()
            );
        } else {
            if (game.blackUsername() != null) {
                throw new AlreadyTakenException("Error: already taken");
            }
            game = new GameData(
                    game.gameID(),
                    game.whiteUsername(),
                    username,
                    game.gameName(),
                    new ChessGame()
            );
        }
        gameDAO.updateGame(game);
    }
}
