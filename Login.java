package com.iie.poe;

import java.util.regex.Pattern;

/**
 * Login.java
 *
 * Handles user registration and login for the chat application (Part 1 of the PoE).
 *
 * NB - assumption: the task text only explicitly lists username, password and
 * cell phone number as registration inputs, but the required login-success
 * message ("Welcome <first name>, <last name> ...") needs a first and last
 * name to exist somewhere. This class therefore also captures firstName and
 * lastName at registration so that the welcome message can be produced. This
 * assumption is called out again in the video presentation / README.
 *
 * Regex references (attribution, as requested by the brief):
 * - Password complexity lookaheads pattern style: Baeldung, "Java Regex Password Validation",
 *   https://www.baeldung.com/java-regex-password-validation
 * - South African cell phone / international code validation approach adapted from:
 *   https://www.regular-expressions.info/  and  https://quickblox.com/blog/beginners-guide-to-chat-app-architecture/
 */
public class Login {

    // ---- Regex patterns -----------------------------------------------
    // Username: must contain an underscore and be no more than 5 characters long in total.
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^(?=.{1,5}$).*_.*$");

    // Password: at least 8 characters, at least one capital letter, one digit, one special character.
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$");

    // Cell phone: starts with the South African international code "+27" followed by
    // the subscriber number, which must be no more than 10 characters long in total (digits after +27).
    private static final Pattern CELLPHONE_PATTERN = Pattern.compile("^\\+27\\d{1,10}$");

    // ---- Stored user data (in-memory "database" of one registered user) ----
    private String username;
    private String password;
    private String cellPhoneNumber;
    private String firstName;
    private String lastName;

    private boolean userRegistered = false;
    private boolean lastLoginSuccessful = false;

    // ---- Validation methods ---------------------------------------------

    /**
     * This method ensures that any username contains an underscore (_) and
     * is no more than five characters in length.
     */
    public boolean checkUserName(String username) {
        if (username == null) {
            return false;
        }
        return USERNAME_PATTERN.matcher(username).matches();
    }

    /**
     * This method ensures that passwords meet the following password
     * complexity rules: at least eight characters long, contains a capital
     * letter, contains a number and contains a special character.
     */
    public boolean checkPasswordComplexity(String password) {
        if (password == null) {
            return false;
        }
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    /**
     * This method ensures that the cell phone number is the correct length
     * and contains the international country code (+27).
     */
    public boolean checkCellPhoneNumber(String cellPhoneNumber) {
        if (cellPhoneNumber == null) {
            return false;
        }
        return CELLPHONE_PATTERN.matcher(cellPhoneNumber).matches();
    }

    // ---- Registration -----------------------------------------------------

    /**
     * This method returns the necessary registration messaging indicating if:
     * - the username is incorrectly formatted
     * - the password does not meet the complexity requirements
     * - the cell phone number is incorrectly formatted
     * - all conditions have been met and the user has been registered successfully.
     */
    public String registerUser(String username, String password, String cellPhoneNumber,
                                String firstName, String lastName) {

        boolean usernameOk = checkUserName(username);
        boolean passwordOk = checkPasswordComplexity(password);
        boolean cellOk = checkCellPhoneNumber(cellPhoneNumber);

        StringBuilder message = new StringBuilder();

        if (!usernameOk) {
            message.append("Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.\n");
        } else {
            message.append("Username successfully captured.\n");
        }

        if (!passwordOk) {
            message.append("Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.\n");
        } else {
            message.append("Password successfully captured.\n");
        }

        if (!cellOk) {
            message.append("Cell phone number incorrectly formatted or does not contain international code; please correct the number and try again.\n");
        } else {
            message.append("Cell number successfully captured.\n");
        }

        if (usernameOk && passwordOk && cellOk) {
            this.username = username;
            this.password = password;
            this.cellPhoneNumber = cellPhoneNumber;
            this.firstName = firstName;
            this.lastName = lastName;
            this.userRegistered = true;
            message.append("User successfully registered.");
        } else {
            message.append("User registration unsuccessful.");
        }

        return message.toString();
    }

    // ---- Login -----------------------------------------------------------

    /**
     * This method verifies that the login details entered match the login
     * details stored when the user registers.
     */
    public boolean loginUser(String username, String password) {
        boolean success = userRegistered
                && this.username != null
                && this.username.equals(username)
                && this.password != null
                && this.password.equals(password);
        this.lastLoginSuccessful = success;
        return success;
    }

    /**
     * This method returns the necessary messaging for a successful login or
     * a failed login, based on the outcome of the most recent loginUser() call.
     */
    public String returnLoginStatus() {
        if (lastLoginSuccessful) {
            return "Welcome " + firstName + ", " + lastName + ", it is great to see you again.";
        } else {
            return "Username or password incorrect, please try again.";
        }
    }

    // ---- Simple getters used by the console app / tests -------------------

    public boolean isUserRegistered() {
        return userRegistered;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
