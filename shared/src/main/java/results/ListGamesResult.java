package results;

import model.GameData;
import java.util.List;

public class ListGamesResult {
    private List<GameData> games;

    public ListGamesResult(List<GameData> games) {
        this.games = games;
    }

    public List<GameData> getGames() {
        return games;
    }
}
