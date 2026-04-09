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
    public static PlayerTypes playerType;

    public enum PlayerTypes {
        PLAYER_WHITE,
        PLAYER_BLACK,
        OBSERVER
    }

    public ClientSession(String serverUrl) {
        this.server = new ServerFacade(serverUrl);
    }

    public void playerType(ClientSession.PlayerTypes type) {
        playerType = type;
    }

    public ClientSession.PlayerTypes getPlayerType() {
        return playerType;
    }
}