package com.iie.poe;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * MessageTest.java
 *
 * Unit tests for Message.java, using the exact test data supplied in the PoE
 * brief (Test Case 1: "Hi Mike, can you join us for dinner tonight?" / Test
 * Case 2: "Hi Keegan, did you receive the payment?").
 */
public class MessageTest {

    @BeforeEach
    public void setUp() {
        Message.resetAllMessagesForTesting();
    }

    // ---- Message length (assertEquals, success and failure) ----------------

    @Test
    public void testMessageLength_Success() {
        Message message = new Message("+27718693002", "Hi Mike, can you join us for dinner tonight?", 0);
        assertEquals("Message ready to send.", message.checkMessageLength());
    }

    @Test
    public void testMessageLength_Failure() {
        String longMessage = "A".repeat(260);
        Message message = new Message("+27718693002", longMessage, 0);
        assertEquals("Message exceeds 250 characters by 10; please reduce the size.",
                message.checkMessageLength());
    }

    // ---- Recipient cell number (reuses Part 1 approach) ---------------------

    @Test
    public void testRecipientCell_CorrectlyFormatted() {
        Message message = new Message("+27718693002", "Hi Mike, can you join us for dinner tonight?", 0);
        assertEquals("Cell phone number successfully captured.", message.checkRecipientCell());
    }

    @Test
    public void testRecipientCell_IncorrectlyFormatted() {
        Message message = new Message("08575975889", "Hi Keegan, did you receive the payment?", 1);
        assertEquals("Cell phone number is incorrectly formatted or does not contain an international code. "
                + "Please correct the number and try again.", message.checkRecipientCell());
    }

    // ---- Message hash --------------------------------------------------------

    @Test
    public void testMessageHash_Correct() {
        Message message = new Message("+27718693002", "Hi Mike, can you join us for dinner tonight?", 0, "0012345678");
        assertEquals("00:0:HITONIGHT", message.createMessageHash());
    }

    @Test
    public void testMessageHash_LoopOverMultipleMessages() {
        String[][] data = {
                {"0012345678", "Hi Mike, can you join us for dinner tonight?"},
                {"0198765432", "Hi Keegan, did you receive the payment?"}
        };
        String[] expectedHashes = {"00:0:HITONIGHT", "01:1:HIPAYMENT"};

        for (int i = 0; i < data.length; i++) {
            Message message = new Message("+27718693002", data[i][1], i, data[i][0]);
            assertEquals(expectedHashes[i], message.createMessageHash());
        }
    }

    // ---- Message ID ------------------------------------------------------------

    @Test
    public void testMessageID_NotMoreThanTenCharacters() {
        Message message = new Message("+27718693002", "Hi Mike, can you join us for dinner tonight?", 0);
        assertTrue(message.checkMessageID());
    }

    @Test
    public void testMessageID_GeneratedTextFormat() {
        Message message = new Message("+27718693002", "Hi Mike, can you join us for dinner tonight?", 0, "0012345678");
        assertEquals("Message ID generated: 0012345678", message.getMessageIdGeneratedText());
    }

    // ---- SentMessage (assertEquals) ---------------------------------------------

    @Test
    public void testSentMessage_Send() {
        Message message = new Message("+27718693002", "Hi Mike, can you join us for dinner tonight?", 0);
        assertEquals("Message successfully sent.", message.sentMessage("send"));
    }

    @Test
    public void testSentMessage_Disregard() {
        Message message = new Message("08575975889", "Hi Keegan, did you receive the payment?", 1);
        assertEquals("Press 0 to delete the message.", message.sentMessage("disregard"));
    }

    @Test
    public void testSentMessage_Store() {
        Message message = new Message("+27718693002", "Hi Mike, can you join us for dinner tonight?", 0);
        message.createMessageHash();
        assertEquals("Message successfully stored.", message.sentMessage("store"));
    }

    // ---- Total messages sent / printMessages (using the brief's two test messages) --

    @Test
    public void testReturnTotalMessagesSent() {
        Message message1 = new Message("+27718693002", "Hi Mike, can you join us for dinner tonight?", 0);
        message1.createMessageHash();
        message1.sentMessage("send");

        Message message2 = new Message("08575975889", "Hi Keegan, did you receive the payment?", 1);
        message2.createMessageHash();
        message2.sentMessage("disregard");

        assertEquals(1, Message.returnTotalMessagessent());
    }

    @Test
    public void testPrintMessages_ContainsSentMessageOnly() {
        Message message1 = new Message("+27718693002", "Hi Mike, can you join us for dinner tonight?", 0);
        message1.createMessageHash();
        message1.sentMessage("send");

        Message message2 = new Message("08575975889", "Hi Keegan, did you receive the payment?", 1);
        message2.createMessageHash();
        message2.sentMessage("disregard");

        String printed = Message.printMessages();
        assertTrue(printed.contains("Hi Mike, can you join us for dinner tonight?"));
        assertFalse(printed.contains("Hi Keegan, did you receive the payment?"));
    }
}
