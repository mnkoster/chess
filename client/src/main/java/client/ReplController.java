package client;

import ui.State;

/**
 * 3/22/26: added for p5 client - repl controller of 3 REPLs
 */
public class ReplController {
    public void run() {
        State state = State.LOGOUT;

        while (state != State.EXIT) {
            state = switch (state) {
                case LOGOUT -> new LoggedOutRepl().run();
                case LOGIN -> new LoggedInRepl().run();
                case GAMEPLAY -> new GameplayRepl().run();
                default -> State.EXIT;
            };
        }
    }
}
