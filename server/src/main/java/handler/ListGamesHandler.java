package handler;

import io.javalin.http.Context;
import java.util.Map;
import service.GameService;

/**
 * 3/2/26: added for p3 apis (list games)
 */
public class ListGamesHandler {

    private final GameService gameService;

    public ListGamesHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public void handle(Context ctx) {
        String authToken = ctx.header("Authorization");
        var games = gameService.getListGames(authToken);
        ctx.status(200).json(Map.of("games", games));
    }
}
