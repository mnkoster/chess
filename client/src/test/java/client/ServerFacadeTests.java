package client;

import model.AuthData;
import org.junit.jupiter.api.*;
import server.Server;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 3/17/26: added for p5 pre-gameplay
 * 3/24/26: updated with full facade tests
 */
public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void clearDatabase() throws Exception {
        facade.clear();
    }

    // register positive
    @Test
    public void registerPositive() throws Exception {
        AuthData auth = facade.register("user1", "pass", "email@test.com");
        assertNotNull(auth);
    }

    // register negative
    @Test
    public void registerNegative() throws Exception {
        facade.register("user1", "pass", "email@test.com");
        assertThrows(Exception.class, () ->
                facade.register("user1", "pass", "email@test.com"));
    }

    // login positive
    @Test
    public void loginPositive() throws Exception {
        facade.register("user2", "pass", "email@test.com");
        AuthData auth = facade.login("user2", "pass");
        assertNotNull(auth);
    }

    // login negative
    @Test
    public void loginNegative() {
        assertThrows(Exception.class, () ->
                facade.login("badUser", "badPass"));
    }

    // logout positive
    @Test
    public void logoutPositive() throws Exception {
        AuthData auth = facade.register("user3", "pass", "email@test.com");
        assertDoesNotThrow(() -> facade.logout(auth.authToken()));
    }

    // logout negative
    @Test
    public void logoutNegative() {
        assertThrows(Exception.class, () ->
                facade.logout("badToken"));
    }

    // listGames positive
    @Test
    public void listGamesPositive() throws Exception {
        AuthData auth = facade.register("user4", "pass", "email@test.com");
        assertNotNull(facade.listGames(auth.authToken()));
    }

    // listGames negative
    @Test
    public void listGamesNegative() {
        assertThrows(Exception.class, () ->
                facade.listGames("badToken"));
    }

    // createGame positive
    @Test
    public void createGamePositive() throws Exception {
        AuthData auth = facade.register("user5", "pass", "email@test.com");
        assertDoesNotThrow(() ->
                facade.createGame(auth.authToken(), "game1"));
    }

    // createGame negative
    @Test
    public void createGameNegative() {
        assertThrows(Exception.class, () ->
                facade.createGame("badToken", "game1"));
    }

    // joinGame positive
    @Test
    public void joinGamePositive() throws Exception {
        AuthData auth = facade.register("user6", "pass", "email@test.com");
        facade.createGame(auth.authToken(), "game1");
        int gameID = facade.listGames(auth.authToken()).getGames().getFirst().gameID();
        assertDoesNotThrow(() ->
                facade.joinGame(auth.authToken(), gameID, "WHITE"));
    }

    // joinGame negative
    @Test
    public void joinGameNegative() {
        assertThrows(Exception.class, () ->
                facade.joinGame("badToken", 999, "WHITE"));
    }

    // clear positive
    @Test
    public void clearPositive() {
        assertDoesNotThrow(() -> facade.clear());
    }

    // clear negative
    @Test
    public void clearNegative() {
        assertDoesNotThrow(() -> facade.clear());
    }
}