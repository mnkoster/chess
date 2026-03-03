package requests;

/**
 * 3/2/26: added for p3 apis (games)
 * @param gameID
 * @param playerColor
 */
public record JoinGameRequest(
        int gameID,
        String playerColor
) {}
