package client;

/**
 * 3/23/26: added for p5 client - shared variables
 */
public class ClientSession {
    public String authToken;
    public String username;
    public ServerFacade server;
    public int gameplayID;
    public boolean playerWhite;

    public ClientSession(String serverUrl) {
        this.server = new ServerFacade(serverUrl);
    }
}