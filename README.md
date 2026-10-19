# PROG5121 PoE — Part 2: QuickChat Message Feature

A console-only Java application (no GUI / no JOptionPane) implementing the
Message class and chat workflow specified in Part 2 of the PoE.

## Project structure

```
poe-part2/
├── pom.xml
├── .github/workflows/maven.yml   # GitHub Actions: runs `mvn test` on every push
├── src/
│   ├── main/java/com/iie/poe/
│   │   ├── Message.java   # validation, hashing, sending/storing logic
│   │   └── Main.java      # console menu driving the whole flow
│   └── test/java/com/iie/poe/
│       └── MessageTest.java   # JUnit 5 tests using the brief's exact test data
```

## Requirements covered

- **checkMessageID()** — true if the (randomly generated) Message ID is no
  more than 10 characters.
- **checkRecipientCell()** — reuses the same `+27` international-code
  validation approach as Part 1's `checkCellPhoneNumber()`, as the brief's
  hint asks.
- **checkMessageLength()** — "Message ready to send." if ≤250 characters,
  otherwise "Message exceeds 250 characters by X; please reduce the size."
- **createMessageHash()** — `<first 2 digits of ID>:<message number>:<FIRST
  WORD><LAST WORD>`, e.g. `00:0:HITONIGHT` for the brief's Test Case 1.
- **sentMessage()** — lets the user choose Send / Store / Disregard and
  returns the matching outcome message.
- **printMessages()** / **returnTotalMessagessent()** — report on every
  message *sent* while the program has been running (class-level/static,
  since they need to see every `Message` instance, not just one).
- **storeMessage()** — serialises the message to JSON (via the `org.json`
  library) and writes it to `storedMessages.json`.

## Assumptions (explained again in the video presentation)

- The brief lists every method with **no parameters**. To honour that while
  still needing data to validate, each `Message` is constructed with its
  recipient/text/sequence-number up front, and the zero-arg methods act on
  those stored fields.
- `printMessages()`, `returnTotalMessagessent()` and the stored-JSON array
  are kept as **static/class-level** state shared by all `Message`
  instances, since the brief asks them to report across the whole run, not
  just a single message.
- A package-private test-only constructor lets unit tests pin the Message ID
  (e.g. `"0012345678"`) instead of the randomly generated one, so hash/ID
  assertions are deterministic and match the brief's exact expected values.

## How to run

**Build & run the console app:**
```
mvn compile exec:java
```

**Run the unit tests:**
```
mvn test
```

## References

- JSON serialisation: org.json library — https://github.com/stleary/JSON-java
- Cell phone / recipient validation approach reused from Part 1 (see that
  project's README for its own references).
