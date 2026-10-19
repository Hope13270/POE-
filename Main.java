package com.iie.poe;

import java.util.Scanner;

/**
 * Main.java
 *
 * Console-only entry point for Part 2 of the PoE (QuickChat message feature).
 * No GUI / JOptionPane is used, as required by the brief.
 */
public class Main {

    private static final Scanner SCANNER = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("          Welcome to QuickChat - Part 2         ");
        System.out.println("=================================================");

        int numMessages = readNumberOfMessages();

        for (int i = 0; i < numMessages; i++) {
            System.out.println("\n--- Message " + (i + 1) + " of " + numMessages + " ---");
            captureAndProcessMessage(i);
        }

        System.out.println("\n--- Summary ---");
        System.out.println("Total number of messages sent: " + Message.returnTotalMessagessent());
        System.out.println("\nSent messages:");
        System.out.println(Message.printMessages());

        SCANNER.close();
    }

    private static int readNumberOfMessages() {
        int num = -1;
        while (num < 0) {
            System.out.print("How many messages would you like to capture? ");
            String input = SCANNER.nextLine().trim();
            try {
                num = Integer.parseInt(input);
                if (num < 0) {
                    System.out.println("Please enter a positive number.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number.");
            }
        }
        return num;
    }

    private static void captureAndProcessMessage(int messageNumber) {
        System.out.print("Enter the recipient cell number (e.g. +27718693002): ");
        String recipient = SCANNER.nextLine().trim();

        System.out.print("Enter the message (max 250 characters): ");
        String text = SCANNER.nextLine();

        Message message = new Message(recipient, text, messageNumber);

        String cellResult = message.checkRecipientCell();
        System.out.println(cellResult);

        String lengthResult = message.checkMessageLength();
        System.out.println(lengthResult);

        if (!message.checkMessageID()) {
            System.out.println("Message ID generation failed - ID too long.");
            return;
        }
        System.out.println(message.getMessageIdGeneratedText());
        System.out.println("Message Hash: " + message.createMessageHash());

        String outcome = message.sentMessage();
        System.out.println(outcome);
    }
}
