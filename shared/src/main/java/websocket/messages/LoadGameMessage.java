package websocket.messages;

import chess.ChessGame;

/**
 * 4/7/26: added for p6 websocket - messages
 */
public class LoadGameMessage extends ServerMessage {

    private final ChessGame game;

    public LoadGameMessage(ChessGame game) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }

    public ChessGame getGame() {
        return game;
    }
}