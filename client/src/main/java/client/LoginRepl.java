package client;

import model.GameData;
import ui.State;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LoginRepl {

    private final ClientSession session;
    private final Scanner scanner = new Scanner(System.in);
    private List<GameData> currentGames = new ArrayList<>();

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
                    } else if (handleObservation(Integer.parseInt(tokens[1]))) {
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

    private boolean handleJoinGame(int choice, String color) {
        if (!color.equals("BLACK") && !color.equals("WHITE")) {
            System.out.println("Invalid player color argument");
            return false;
        }
        if (choice < 1 || choice > currentGames.size()) {
            System.out.println("Invalid game selection. Run 'list' again.");
            return false;
        }

        var selectedGame = currentGames.get(choice - 1);
        int realGameID = selectedGame.gameID();
        session.gameplayID = selectedGame.gameID();

        try {
            session.server.joinGame(session.authToken, realGameID, color);
            System.out.println("Joining game...");
            return true;
        } catch (Exception e) {
            System.out.println("Join game failed: " + e.getMessage());
            return false;
        }
    }

    private boolean handleListGames() {
        try {
            var result = session.server.listGames(session.authToken);
            currentGames = result.get("games");

            if (currentGames == null || currentGames.isEmpty()) {
                System.out.println("No games available.");
                return true;
            }

            int index = 1;
            for (var game : currentGames) {
                String name = game.gameName();
                String white = game.whiteUsername();
                String black = game.blackUsername();
                System.out.println(index + " - " + name +
                        " | White: " + white +
                        " | Black: " + black);
                index++;
            }
            return true;
        } catch (Exception e) {
            System.out.println("List games failed: " + e.getMessage());
            return false;
        }
    }

    private boolean handleObservation(int choice) {
        if (choice < 1 || choice > currentGames.size()) {
            System.out.println("Invalid game selection. Run 'list' first.");
            return false;
        }
        var selectedGame = currentGames.get(choice - 1);
        session.gameplayID = selectedGame.gameID();
        System.out.println("Observing game...");
        return true;
    }

    private boolean handleLogout() {
        try {
            session.server.logout(session.authToken);
            return true;
        } catch (Exception e) {
            System.out.println("Failed to logout: " + e.getMessage());
            return false;
        }
    }
}
