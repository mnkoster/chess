package client;

import model.AuthData;
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
                case "register" -> {
                    if (handleRegister()) {
                        return State.LOGIN;
                    }
                }
                case "login" -> {
                    if (handleLogin()) {
                        return State.LOGIN;
                    }
                }
                case "quit" -> {
                    System.out.println("Goodbye!");
                    return State.EXIT;
                }
                default -> System.out.println("Unknown command. Type 'help' to see options.");
            }
        }
    }

    private void printHelp() {
        System.out.println("""
            Commands:
            - help                                      : Show available commands
            - login <USERNAME> <PASSWORD>               : Login with your username and password
            - register <USERNAME> <PASSWORD> <EMAIL>    : Create a new account
            - quit                                      : Exit the program
            """);
    }

    private boolean handleLogin() {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        try {
            AuthData auth = session.server.login(username, password);
            session.authToken = auth.authToken();
            session.username = auth.username();

            System.out.println("Login successful!");
            return true;
        } catch (Exception e) {
            System.out.println("Login failed: " + e.getMessage());
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

        try {
            AuthData auth = session.server.register(username, password, email);
            session.authToken = auth.authToken();
            session.username = auth.username();

            System.out.println("Registration successful! You are now logged in.");
            return true;
        } catch (Exception e) {
            System.out.println("Registration failed: " + e.getMessage());
            return false;
        }
    }
}
