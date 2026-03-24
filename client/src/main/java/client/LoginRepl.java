package client;

import model.AuthData;
import ui.State;
import java.util.Scanner;

public class LoginRepl {

    private final ClientSession session;
    private final Scanner scanner = new Scanner(System.in);

    public LoginRepl(ClientSession session) {
        this.session = session;
    }

    public State run() {
        System.out.println("=== Logged into Chess ===");
        printHelp();

        while (true) {
            System.out.print("\n[LOGGED IN] >>> ");
            String input = scanner.nextLine().trim();
            String[] tokens = input.split("\\s+");
            String command = tokens[0];

            switch (command) {
                case "create" -> {
                    if (tokens.length != 2) {
                        System.out.println("Invalid number of arguments. Type 'help' to see options.");
                        System.out.println("Usage: create <NAME>");
                    } else if (handleCreateGame(tokens[1])) {
                        System.out.println("Game created. You can now find it in the list and join the game.");
                        return State.LOGIN;
                    } else {
                        System.out.println("Could not observe game. Make sure ID exists.");
                    }
                }
                case "list" -> {
                    if (tokens.length != 1) {
                        System.out.println("Invalid number of arguments. Type 'help' to see options.");
                    } else if (handleListGames()) {
                        return State.LOGIN;
                    } else {
                        System.out.println("Could not list games. Try again.");
                    }
                }
                case "join" -> {
                    if (tokens.length != 3) {
                        System.out.println("Invalid number of arguments. Type 'help' to see options.");
                        System.out.println("Usage: join <ID> [WHITE|BLACK]");
                    } else if (handleJoinGame(Integer.parseInt(tokens[1]), tokens[2])) {
                        return State.GAMEPLAY;
                    } else {
                        System.out.println("Could not observe game. Make sure ID exists.");
                    }
                }
                case "observe" -> {
                    if (tokens.length != 2) {
                        System.out.println("Invalid number of arguments. Type 'help' to see options.");
                        System.out.println("Usage: observe <ID>");
                    } else if (handleObservation()) {
                        return State.GAMEPLAY;
                    } else {
                        System.out.println("Could not observe game. Make sure ID exists.");
                    }
                }
                case "logout" -> {
                    if (tokens.length != 1) {
                        System.out.println("Invalid number of arguments. Type 'help' to see options.");
                    } else if (handleLogout()) {
                        System.out.println("Logging out...");
                        return State.LOGOUT;
                    } else {
                        System.out.println("Could not log out. Try again.");
                    }
                }
                case "help" -> {
                    if (tokens.length != 1) {
                        System.out.println("Invalid number of arguments. Type 'help' to see options.");
                    } else {
                        printHelp();
                    }
                }
                default -> System.out.println("Unknown command. Type 'help' to see options.");
            }
        }
    }

    private void printHelp() {
        System.out.println("""
            Commands:
            - help                                      : Show available commands
            - create <NAME>                             : Create new game
            - list                                      : List existing games
            - join <ID> [WHITE | BLACK]                 : Join game under ID
            - observe <ID>                              : Observe game under ID
            - logout                                    : Logout (logout to quit program)
            """);
    }

    private boolean handleCreateGame(String name) {
        try {
            session.server.createGame(session.authToken, name);
            System.out.println("Successfully created game.");
            return true;
        } catch (Exception e) {
            System.out.println("Create game failed: " + e.getMessage());
            return false;
        }
    }

    private boolean handleJoinGame(int gameID, String color) {
        if (color.equals("BLACK") || color.equals("WHITE")) {
            try {
                session.server.joinGame(session.authToken, gameID, color);
                System.out.println("Joining game...");
                return true;
            } catch (Exception e) {
                System.out.println("Join game failed: " + e.getMessage());
                return false;
            }
        } else {
            System.out.println("Invalid player color argument");
            return false;
        }
    }

    private boolean handleListGames() {

    }

    private boolean handleObservation() {

    }

    private boolean handleLogout() {
        try {
            session.server.logout(session.authToken);
            return true;
        } catch (Exception e) {
            System.out.println("Failed to logout: " + e.getMessage());
        }
    }
}
