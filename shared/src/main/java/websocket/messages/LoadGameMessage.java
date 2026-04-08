package websocket.messages;

import model.GameData;

/**
 * 4/7/26: added for p6 websocket - messages
 */
public class LoadGameMessage extends ServerMessage {

    private final GameData game;

    public LoadGameMessage(GameData game) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }

    public GameData getGame() {
        return game;
    }
}