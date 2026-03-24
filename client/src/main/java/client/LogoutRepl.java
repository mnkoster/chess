package client;

import ui.State;
import java.util.Scanner;

public class LogoutRepl {

    private final ClientSession session;
    private final Scanner scanner = new Scanner(System.in);

    public LogoutRepl(ClientSession session) {
        this.session = session;
    }

    public State run() {
        System.out.println("=== Welcome to Chess ===");
        printHelp();

        while (true) {
            System.out.print("\n[LOGGED OUT] >>> ");
            String input = scanner.nextLine().trim().toLowerCase();

            switch (input) {
                case "help" -> printHelp();
                case "quit" -> {
                    System.out.println("Goodbye!");
                    return State.EXIT;
                }
                case "login" -> {
                    if (handleLogin()) {
                        return State.LOGIN;
                    }
                }
                case "register" -> {
                    if (handleRegister()) {
                        return State.LOGIN;
                    }
                }
                default -> System.out.println("Unknown command. Type 'help' to see options.");
            }
        }
    }

    private void printHelp() {
        System.out.println("""
            Commands:
            - help     : Show available commands
            - login    : Login with your username and password
            - register : Create a new account
            - quit     : Exit the program
            """);
    }

    private boolean handleLogin() {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        boolean success = session.server.login(username, password);
        if (success) {
            System.out.println("Login successful!");
            return true;
        } else {
            System.out.println("Login failed.");
            return false;
        }
    }

    private boolean handleRegister() {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();

        boolean success = session.server.register(username, password, email);
        if (success) {
            System.out.println("Registration successful! You are now logged in.");
            return true;
        } else {
            System.out.println("Registration failed.");
            return false;
        }
    }
}
