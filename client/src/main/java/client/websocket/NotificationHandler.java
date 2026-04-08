package client.websocket;

import client.GameplayRepl;
import websocket.messages.*;

/**
 * 4/7/26: added for p6 websocket - handle
 */
public class NotificationHandler {

    private GameplayRepl repl;

    public void handle(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case LOAD_GAME -> handleLoadGame(message);
            case NOTIFICATION -> handleNotification(message);
            case ERROR -> handleError(message);
        }
    }

    void handleLoadGame(ServerMessage message) {
        LoadGameMessage msg = (LoadGameMessage) message;

        // store game state if your REPL tracks it
        // e.g., repl.setGame(msg.getGame());
        // if (repl != null) {
        // repl.drawBoard(); // redraw with updated state
        // }
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