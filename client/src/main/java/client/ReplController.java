package client;

import ui.State;

/**
 * 3/22/26: added for p5 client - repl controller of 3 REPLs
 * 3/23/26: added for p5 client - session info sharing
 */
public class ReplController {

    private final ClientSession session;

    public ReplController(ClientSession session) {
        this.session = session;
    }

    public void run() {
        State state = State.LOGOUT;

        while (state != State.EXIT) {
            state = switch (state) {
                case LOGOUT -> new LogoutRepl(session).run();
                case LOGIN -> new LoginRepl(session).run();
                case GAMEPLAY -> {
                    try {
                        yield new GameplayRepl(session).run();
                    } catch (Exception e) {
                        System.out.println("Couldn't establish websocket");
                        yield State.LOGIN;
                    }
                }
                default -> State.EXIT;
            };
        }
    }
}
