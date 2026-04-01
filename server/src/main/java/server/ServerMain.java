package server;

import chess.*;

public class ServerMain {
    public static void main(String[] args) {
        Server server = new Server();
        server.run(4444);
        System.out.println("♕ 240 Chess Server");
    }
}
