package client;

import ui.State;
import java.util.Scanner;

public class LoginRepl {

    private final ClientSession session;
    private final Scanner scanner = new Scanner(System.in);

    public LoginRepl(ClientSession session) {
        this.session = session;
    }

    public State run() {
        System.out.println("=== Welcome to Chess ===");
        printHelp();

        while (true) {
            System.out.print("\n[LOGGED IN] >>> ");
            String input = scanner.nextLine().trim().toLowerCase();

            switch (input) {
                case "create" -> {
                    if (handleCreateGame()) {
                        return State.GAMEPLAY;
                    }
                }
                case "list" -> {
                    if (handleListGames()) {
                        return State.LOGIN;
                    }
                }
                case "join" -> {
                    if (handleJoinGame()) {
                        return State.OBSERVE; // change to GAMEPLAY in p6
                    }
                }
                case "observe" -> {
                    if (handleObservation()) {
                        return State.OBSERVE;
                    }
                }
                case "logout" -> {
                    if (handleLogout()) {
                        System.out.println("Logging out...");
                        return State.LOGOUT;
                    }
                }
                case "help" -> printHelp();
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
            - join <ID>                                 : Join game under ID
            - observe <ID>                              : Observe game under ID
            - logout                                    : Logout (logout to quit program)
            """);
    }
}
