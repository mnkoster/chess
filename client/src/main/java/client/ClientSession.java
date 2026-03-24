package client;

import server.ServerFacade;

/**
 * 3/23/26: added for p5 client - shared variables
 */
public class ClientSession {
    public String authToken;
    public String username;
    public ServerFacade server;

    public ClientSession(String serverUrl) {
        this.server = new ServerFacade(serverUrl);
    }
}