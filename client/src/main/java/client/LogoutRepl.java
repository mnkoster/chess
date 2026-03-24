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
            String input = scanner.nextLine().trim();
            String[] tokens = input.split("\\s+");
            String command = tokens[0];

            switch (command) {
                case "help" -> {
                    if (tokens.length != 1) {
                        System.out.println("Invalid number of arguments. Type 'help' to see options.");
                    } else {
                        printHelp();
                    }
                }
                case "register" -> {
                    if (tokens.length != 4) {
                        System.out.println("Invalid number of arguments.");
                        System.out.println("Usage: register <USERNAME> <PASSWORD> <EMAIL>");
                    } else if (handleRegister(tokens[1], tokens[2], tokens[3])) {
                        return State.LOGIN;
                    }
                }
                case "login" -> {
                    if (tokens.length != 3) {
                        System.out.println("Invalid number of arguments. Type 'help' to see options.");
                        System.out.println("Usage: login <USERNAME> <PASSWORD>");
                    } else if (handleLogin(tokens[1], tokens[2])) {
                        return State.LOGIN;
                    }
                }
                case "quit" -> {
                    if (tokens.length != 1) {
                        System.out.println("Invalid number of arguments. Type 'help' to see options.");
                        return State.LOGOUT;
                    }
                    System.out.println("Exiting program...");
                    return State.EXIT;
                }
                default -> System.out.println("Invalid command. Type 'help' to see options.");
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

    private boolean handleLogin(String username, String password) {
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

    private boolean handleRegister(String username, String password, String email) {
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
