package client;

import model.GameData;
import ui.State;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LoginRepl {

    private final ClientSession clientSession;
    private final Scanner scanner = new Scanner(System.in);
    private List<GameData> currentGames = new ArrayList<>();

    public LoginRepl(ClientSession clientSession) {
        this.clientSession = clientSession;
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
                    } else {
                        System.out.println("Could not observe game. Make sure ID exists.");
                    }
                }
                case "list" -> {
                    if (tokens.length != 1) {
                        System.out.println("Invalid number of arguments. Type 'help' to see options.");
                    } else if (!handleListGames()) {
                        System.out.println("Could not list games. Try again.");
                    }
                }
                case "join" -> {
                    if (tokens.length != 3) {
                        System.out.println("Invalid number of arguments. Type 'help' to see options.");
                        System.out.println("Usage: join <ID> [WHITE|BLACK]");
                    } else if (handleJoinGame(tokens[1], tokens[2])) {
                        return State.GAMEPLAY;
                    } else {
                        System.out.println("Could not join game. Usage: join <ID> [WHITE|BLACK].");
                    }
                }
                case "observe" -> {
                    if (tokens.length != 2) {
                        System.out.println("Invalid number of arguments. Type 'help' to see options.");
                        System.out.println("Usage: observe <ID>");
                    } else if (handleObservation(tokens[1])) {
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

    private void updateCurrentGames() throws Exception {
        var result = clientSession.server.listGames(clientSession.authToken);
        currentGames = result.getGames();
    }

    private void printException(Exception e) {
        String raw = e.getMessage();
        String cleaned = raw.replaceAll(".*\"message\":\"", "")
                .replaceAll("\".*", "").replaceFirst("^Error:\\s*", "");
        System.out.println("Action failed: " + cleaned);
    }

    private boolean handleCreateGame(String name) {
        try {
            updateCurrentGames();
            clientSession.server.createGame(clientSession.authToken, name);
            System.out.println("Creating game...");
            return true;
        } catch (Exception e) {
            printException(e);
            return false;
        }
    }

    private boolean handleJoinGame(String gameNumber, String color) {
        if (!color.equals("BLACK") && !color.equals("WHITE")) {
            System.out.println("Invalid player color argument");
            return false;
        }
        int choice;
        try {
            choice = Integer.parseInt(gameNumber);
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID. Must be a number.");
            return false;
        }
        try {
            updateCurrentGames();
        } catch (Exception e) {
            printException(e);
        }
        if (choice < 1 || choice > currentGames.size()) {
            System.out.println("Invalid game selection. Run 'list' again.");
            return false;
        }

        var selectedGame = currentGames.get(choice - 1);
        int realGameID = selectedGame.gameID();

        try {
            clientSession.server.joinGame(clientSession.authToken, realGameID, color);
            System.out.println("Joining game...");
            clientSession.gameplayID = selectedGame.gameID();
            clientSession.playerWhite = color.equals("WHITE");
            return true;
        } catch (Exception e) {
            printException(e);
            return false;
        }
    }

    private boolean handleListGames() {
        try {
            updateCurrentGames();

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
            printException(e);
            return false;
        }
    }

    private boolean handleObservation(String gameNumber) {
        int choice;
        try {
            choice = Integer.parseInt(gameNumber);
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID. Must be a number.");
            return false;
        }
        try {
            updateCurrentGames();
        } catch (Exception e) {
            printException(e);
        }
        if (choice < 1 || choice > currentGames.size()) {
            System.out.println("Invalid game selection. Run 'list' first.");
            return false;
        }

        var selectedGame = currentGames.get(choice - 1);
        clientSession.gameplayID = selectedGame.gameID();
        clientSession.playerWhite = true;
        System.out.println("Observing game...");
        return true;
    }

    private boolean handleLogout() {
        try {
            clientSession.server.logout(clientSession.authToken);
            return true;
        } catch (Exception e) {
            System.out.println("Failed to logout. Try again.");
            return false;
        }
    }
}
