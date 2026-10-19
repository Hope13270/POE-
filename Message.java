package com.iie.poe;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Message.java
 *
 * Represents a single chat message for Part 2 of the PoE, together with the
 * validation / processing methods required by the brief.
 *
 * Design notes / assumptions (explained again in the video presentation,
 * as the brief asks that logic and flow be explained):
 *
 * 1. The brief lists every method with an EMPTY parameter list
 *    (checkMessageID(), checkRecipientCell(), createMessageHash(),
 *    SentMessage(), printMessages(), returnTotalMessagessent()). To honour
 *    that exact signature, each Message object is constructed with its own
 *    recipient number and message text, and the zero-argument methods act
 *    on those stored fields rather than taking parameters.
 *
 * 2. checkRecipientCell() and checkMessageLength() reuse the same
 *    validation approach as the Login class in Part 1, as the brief's HINT
 *    explicitly asks us to reuse the method/tests from the last task.
 *
 * 3. "printMessages()", "returnTotalMessagessent()" and "storeMessage()"
 *    need visibility across every Message that has been sent/stored during
 *    the run, not just the current object, so they are backed by static
 *    (class-level) collections shared by every Message instance - this is
 *    a common, idiomatic way to implement this well-known brief.
 *
 * Reference for JSON storage: org.json library, https://github.com/stleary/JSON-java
 */
public class Message {

    // ---- Regex patterns (reused approach from Part 1's Login class) -------
    private static final Pattern RECIPIENT_PATTERN = Pattern.compile("^\\+27\\d{1,10}$");
    private static final int MAX_MESSAGE_LENGTH = 250;
    private static final int MAX_ID_LENGTH = 10;
    private static final String STORE_FILE = "storedMessages.json";

    // ---- Class-level (shared) state ---------------------------------------
    private static final List<Message> sentMessages = new ArrayList<>();
    private static final JSONArray storedMessagesJson = new JSONArray();
    private static int totalMessagesSent = 0;

    // ---- Instance fields ----------------------------------------------------
    private final String recipientNumber;
    private final String messageText;
    private final String messageID;
    private final int messageNumber; // position of this message in the run (used in the hash)
    private String messageHash;
    private boolean sent = false;
    private boolean stored = false;
    private boolean disregarded = false;

    /**
     * @param recipientNumber the recipient's cell number, as typed by the user
     * @param messageText     the message body, as typed by the user
     * @param messageNumber   the sequence number of this message in the current run (0-based),
     *                        used when building the message hash
     */
    public Message(String recipientNumber, String messageText, int messageNumber) {
        this.recipientNumber = recipientNumber;
        this.messageText = messageText;
        this.messageNumber = messageNumber;
        this.messageID = generateMessageID();
    }

    /**
     * Package-private constructor used only by unit tests, so that the
     * Message ID can be pinned to a known value (e.g. "0012345678") instead
     * of the randomly generated one - this makes assertions like
     * assertEquals("00:0:HITONIGHT", ...) deterministic and repeatable.
     */
    Message(String recipientNumber, String messageText, int messageNumber, String fixedMessageIdForTesting) {
        this.recipientNumber = recipientNumber;
        this.messageText = messageText;
        this.messageNumber = messageNumber;
        this.messageID = fixedMessageIdForTesting;
    }

    private String generateMessageID() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < MAX_ID_LENGTH; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    // ---- Required methods ---------------------------------------------------

    /**
     * Boolean: checkMessageID()
     * This method ensures that the message ID is not more than ten characters.
     */
    public boolean checkMessageID() {
        return messageID != null && messageID.length() <= MAX_ID_LENGTH;
    }

    /**
     * String: checkRecipientCell()
     * This method ensures that the recipient cell number is no more than ten
     * characters long and starts with the international code (reuses the
     * same validation approach as Part 1's checkCellPhoneNumber()).
     */
    public String checkRecipientCell() {
        if (recipientNumber != null && RECIPIENT_PATTERN.matcher(recipientNumber).matches()) {
            return "Cell phone number successfully captured.";
        }
        return "Cell phone number is incorrectly formatted or does not contain an international code. "
                + "Please correct the number and try again.";
    }

    /**
     * Not explicitly named in the table, but required to drive the "Message
     * exceeds 250 characters..." test - checks the message body length.
     */
    public String checkMessageLength() {
        if (messageText != null && messageText.length() <= MAX_MESSAGE_LENGTH) {
            return "Message ready to send.";
        }
        int over = (messageText == null ? 0 : messageText.length() - MAX_MESSAGE_LENGTH);
        return "Message exceeds " + MAX_MESSAGE_LENGTH + " characters by " + over + "; please reduce the size.";
    }

    /**
     * String: createMessageHash()
     * Builds and returns the message hash in the format:
     *   &lt;first two digits of the Message ID&gt;:&lt;message number&gt;:&lt;FIRST WORD&gt;&lt;LAST WORD&gt;
     * e.g. "00:0:HITONIGHT" for message number 0, ID starting "00", and the
     * message "Hi Mike, can you join us for dinner tonight?".
     */
    public String createMessageHash() {
        String idPrefix = messageID.substring(0, 2);
        String[] words = messageText.trim().split("\\s+");
        String firstWord = stripToLetters(words[0]).toUpperCase();
        String lastWord = stripToLetters(words[words.length - 1]).toUpperCase();
        this.messageHash = idPrefix + ":" + messageNumber + ":" + firstWord + lastWord;
        return this.messageHash;
    }

    private String stripToLetters(String word) {
        return word.replaceAll("[^a-zA-Z]", "");
    }

    /**
     * Convenience method matching the "Message ID is created" test, which
     * expects: "Message ID generated: &lt;Message ID&gt;"
     */
    public String getMessageIdGeneratedText() {
        return "Message ID generated: " + messageID;
    }

    /**
     * String: SentMessage()
     * Prompts the user to choose whether to Send, Store, or Disregard this
     * message, and returns the corresponding outcome message.
     */
    public String sentMessage() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Would you like to: 1) Send Message  2) Store Message  3) Disregard Message");
        System.out.print("> ");
        String choice = scanner.nextLine().trim();
        return sentMessage(choice);
    }

    /**
     * Overload used by the console app and by unit tests so that the user's
     * choice can be supplied directly without needing System.in.
     * Accepts "1"/"send", "2"/"store", "3"/"disregard" (case-insensitive).
     */
    public String sentMessage(String choice) {
        if (choice == null) {
            return "Invalid selection.";
        }
        switch (choice.trim().toLowerCase()) {
            case "1":
            case "send":
            case "send message":
                this.sent = true;
                sentMessages.add(this);
                totalMessagesSent++;
                return "Message successfully sent.";
            case "2":
            case "store":
            case "store message":
                this.stored = true;
                storeMessage();
                return "Message successfully stored.";
            case "3":
            case "disregard":
            case "disregard message":
                this.disregarded = true;
                return "Press 0 to delete the message.";
            default:
                return "Invalid selection.";
        }
    }

    /**
     * String: printMessages()
     * Returns all the messages sent while the program has been running.
     */
    public static String printMessages() {
        if (sentMessages.isEmpty()) {
            return "No messages have been sent yet.";
        }
        StringBuilder sb = new StringBuilder();
        for (Message m : sentMessages) {
            sb.append("Message ID: ").append(m.messageID)
                    .append(" | Hash: ").append(m.messageHash)
                    .append(" | Recipient: ").append(m.recipientNumber)
                    .append(" | Message: ").append(m.messageText)
                    .append(System.lineSeparator());
        }
        return sb.toString().trim();
    }

    /**
     * Int: returnTotalMessagessent()
     * Returns the total number of messages sent while the program has been
     * running (name kept consistent with the brief's spelling).
     */
    public static int returnTotalMessagessent() {
        return totalMessagesSent;
    }

    /**
     * Your own defined storeMessage() method.
     * Serialises this message to a JSON object and appends it to the
     * in-memory JSON array (used by tests) and to storedMessages.json on
     * disk (used by the console app). Reference: org.json library,
     * https://github.com/stleary/JSON-java
     */
    public String storeMessage() {
        JSONObject json = new JSONObject();
        json.put("messageID", messageID);
        json.put("messageHash", messageHash);
        json.put("recipient", recipientNumber);
        json.put("message", messageText);

        storedMessagesJson.put(json);

        try (FileWriter writer = new FileWriter(STORE_FILE)) {
            writer.write(storedMessagesJson.toString(4));
        } catch (IOException e) {
            System.out.println("Warning: could not write " + STORE_FILE + " (" + e.getMessage() + ")");
        }

        return json.toString();
    }

    // ---- Getters (used by Main and the tests) --------------------------------

    public String getRecipientNumber() {
        return recipientNumber;
    }

    public String getMessageText() {
        return messageText;
    }

    public String getMessageID() {
        return messageID;
    }

    public String getMessageHash() {
        return messageHash;
    }

    public boolean isSent() {
        return sent;
    }

    public boolean isStored() {
        return stored;
    }

    public boolean isDisregarded() {
        return disregarded;
    }

    /** Resets shared static state - used between JUnit tests so each test starts clean. */
    static void resetAllMessagesForTesting() {
        sentMessages.clear();
        storedMessagesJson.clear();
        totalMessagesSent = 0;
    }
}
