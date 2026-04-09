package client.websocket;

import client.GameplayRepl;
import websocket.messages.*;

/**
 * 4/7/26: added for p6 websocket - handle
 */
public class NotificationHandler {

    private GameplayRepl repl;

    void handleLoadGame(ServerMessage message) {
        LoadGameMessage msg = (LoadGameMessage) message;
        repl.setGame(msg.getGame());
        if (repl != null) {
            repl.drawBoard(null, null); // redraw with updated state
            System.out.print("[GAME] >>> ");
        }
    }

    void handleNotification(ServerMessage message) {
        NotificationMessage msg = (NotificationMessage) message;
        System.out.println("\n[NOTIFICATION] " + msg.getMessage());
        System.out.print("[GAME] >>> ");
    }

    void handleError(ServerMessage message) {
        ServerErrorMessage msg = (ServerErrorMessage) message;
        System.out.println("\n[ERROR] " + msg.getErrorMessage());
        System.out.print("[GAME] >>> ");
    }

    public void setGameplayRepl(GameplayRepl repl) {
        this.repl = repl;
    }
}