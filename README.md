# PROG5121 PoE — Part 1: Registration and Login Feature

A console-only Java application (no GUI / no JOptionPane) implementing user
registration and login for the chat app, as specified in Part 1 of the PoE.

## Project structure

```
poe-part1/
├── pom.xml
├── .github/workflows/maven.yml   # GitHub Actions: runs `mvn test` on every push
├── src/
│   ├── main/java/com/iie/poe/
│   │   ├── Login.java   # validation, registration and login logic
│   │   └── Main.java    # console menu (Register / Login / Exit)
│   └── test/java/com/iie/poe/
│       └── LoginTest.java   # JUnit 5 tests using the brief's exact test data
```

## Requirements covered

- **Username check**: must contain an underscore `_` and be no more than 5
  characters long (test data: `kyl_1` → valid, `kyle!!!!!!` → invalid).
- **Password complexity check**: at least 8 characters, a capital letter, a
  number and a special character (test data: `Ch&&sec@ke99!` → valid,
  `password` → invalid).
- **Cell phone check**: must start with the South African international
  code `+27` followed by no more than 10 further characters (test data:
  `+27838968976` → valid, `08966553` → invalid).
- **registerUser()** returns a combined message describing which
  conditions passed/failed, and only marks the user as registered once
  all three checks pass.
- **loginUser()** compares the entered username/password against the
  stored registration details and returns `true`/`false`.
- **returnLoginStatus()** returns the welcome message
  (`"Welcome <first name>, <last name>, it is great to see you again."`)
  on success, or an error message on failure.

> **Assumption/note:** the brief's registration step only explicitly lists
> username, password and cell phone number, but the required login success
> message needs a first and last name. `first name` / `last name` are
> therefore also captured at registration so the welcome message can be
> produced — this is explained in the video presentation as required.

## How to run

**Build & run the console app:**
```
mvn compile exec:java
```

**Run the unit tests:**
```
mvn test
```

Pushing to GitHub automatically runs `mvn test` via the included GitHub
Actions workflow (`.github/workflows/maven.yml`).

## References

- Password complexity regex approach adapted from Baeldung,
  *"Java Regex Password Validation"*: https://www.baeldung.com/java-regex-password-validation
- Chat app payload/architecture background: https://quickblox.com/blog/beginners-guide-to-chat-app-architecture/
