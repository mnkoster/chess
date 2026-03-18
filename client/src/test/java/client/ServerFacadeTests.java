package client;

import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

/**
 * 3/17/26: added for p5 pre-gameplay
 */
public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(0);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

//    @BeforeEach
//    static void clearDatabase {
//        // use http request to clear database before each test
//    }

    /**
     * ADD ONE POSITIVE AND NEGATIVE PER FUNCTION IN SERVERFACADE
     */
    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

}
