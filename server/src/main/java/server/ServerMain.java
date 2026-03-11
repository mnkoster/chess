package server;

import chess.*;
import dataaccess.DataAccessException;

public class ServerMain {
    public static void main(String[] args) throws DataAccessException {
        Server server = new Server();
        server.run(4444);
        System.out.println("♕ 240 Chess Server");
    }
}
