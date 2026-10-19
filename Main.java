package com.iie.poe;

import java.util.Scanner;

/**
 * Main.java
 *
 * Console-only entry point for Part 1 of the PoE (Registration and Login feature).
 * No GUI / JOptionPane is used, as required by the brief.
 */
public class Main {

    private static final Scanner SCANNER = new Scanner(System.in);
    private static final Login LOGIN = new Login();

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println(" Welcome to the Chat App - Registration & Login ");
        System.out.println("=================================================");

        boolean running = true;
        while (running) {
            printMenu();
            String choice = SCANNER.nextLine().trim();

            switch (choice) {
                case "1":
                    registerMenu();
                    break;
                case "2":
                    loginMenu();
                    break;
                case "3":
                    running = false;
                    System.out.println("Goodbye!");
                    break;
                default:
                    System.out.println("Invalid option, please choose 1, 2 or 3.\n");
            }
        }
        SCANNER.close();
    }

    private static void printMenu() {
        System.out.println("\nPlease choose an option:");
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("3. Exit");
        System.out.print("> ");
    }

    private static void registerMenu() {
        System.out.println("\n--- Registration ---");

        System.out.print("Enter a first name: ");
        String firstName = SCANNER.nextLine().trim();

        System.out.print("Enter a last name: ");
        String lastName = SCANNER.nextLine().trim();

        System.out.print("Enter a username (must contain an underscore, max 5 characters): ");
        String username = SCANNER.nextLine().trim();

        System.out.print("Enter a password (min 8 characters, a capital letter, a number, a special character): ");
        String password = SCANNER.nextLine().trim();

        System.out.print("Enter a South African cell phone number (e.g. +27838968976): ");
        String cellPhoneNumber = SCANNER.nextLine().trim();

        String result = LOGIN.registerUser(username, password, cellPhoneNumber, firstName, lastName);
        System.out.println("\n" + result);
    }

    private static void loginMenu() {
        System.out.println("\n--- Login ---");

        if (!LOGIN.isUserRegistered()) {
            System.out.println("No user has been registered yet. Please register first.");
            return;
        }

        System.out.print("Enter your username: ");
        String username = SCANNER.nextLine().trim();

        System.out.print("Enter your password: ");
        String password = SCANNER.nextLine().trim();

        LOGIN.loginUser(username, password);
        System.out.println("\n" + LOGIN.returnLoginStatus());
    }
}
