package client;

/**
 * 3/23/26: added for p5 client - shared variables
 * 4/8/36: updated for p6 websocket
 */
public class ClientSession {
    public String authToken;
    public String username;
    public ServerFacade server;
    public int gameplayID;
    public static playerTypes playerType;

    public enum playerTypes {
        PLAYER_WHITE,
        PLAYER_BLACK,
        OBSERVER
    }

    public ClientSession(String serverUrl) {
        this.server = new ServerFacade(serverUrl);
    }

    public void PlayerType(ClientSession.playerTypes type) {
        playerType = type;
    }

    public ClientSession.playerTypes getPlayerType() {
        return playerType;
    }
}