package client;

import chess.*;

/**
 * 3/24/26: updated for p5 client - added repl entry and session shared variables
 */
public class ClientMain {
    public static void main(String[] args) {
        System.out.println("♕ 240 Chess Client");

        String serverUrl = "http://localhost:64373";
        ClientSession session = new ClientSession(serverUrl);

        ReplController repl = new ReplController(session);
        repl.run();
    }
}
