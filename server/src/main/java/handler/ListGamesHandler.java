package handler;

import dataaccess.DataAccessException;
import dataaccess.UnauthorizedException;
import io.javalin.http.Context;
import java.util.Map;
import service.GameService;

/**
 * 3/2/26: added for p3 apis - list games
 * 3/10/26: updated for p4 database with DataAccessException
 */
public class ListGamesHandler {

    private final GameService gameService;

    public ListGamesHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public void handle(Context ctx) {
        try {
            String authToken = ctx.header("Authorization");
            var games = gameService.getListGames(authToken);
            ctx.status(200).json(Map.of("games", games));
        } catch (UnauthorizedException e) {
            ctx.status(401).json(new ErrorResponse("Error: unauthorized"));
        } catch (DataAccessException e) {
            ctx.status(500).json(new ErrorResponse("Error: server error"));
        }
    }
}
