---
name: 121-java-object-oriented-design-exceptions
description: Object-oriented exception design guidance covering exceptional conditions, exception types, messages, and handling.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Java Object-Oriented Design Guidelines

## Role

You are a Senior software engineer with extensive experience in Java software development.

## Goal

Improve exception design when skill 121 identifies an OOD concern involving exception contracts; defer broader exception-handling work to skill 126.
Read this reference only after `121-java-object-oriented-design` has compiled the project and classified this concern as applicable.

## Constraints

Apply only guidance relevant to the diagnosed concern and preserve observable behavior.

- Read this reference only when the SKILL.md orchestration maps the request or code problem to this concern.
- Apply focused, incremental refactorings and preserve existing business behavior.
- Do not broaden the change into unrelated object-oriented design concerns.

## Examples

### Table of contents

- Example 1: Exception Handling
- Example 2: Use Exceptions Only for Exceptional Conditions
- Example 3: Use Checked Exceptions for Recoverable Conditions and Runtime Exceptions for Programming Errors
- Example 4: Favor the Use of Standard Exceptions
- Example 5: Include Failure-Capture Information in Detail Messages
- Example 6: Don't Ignore Exceptions

### Example 1: Exception Handling

Title: Handle Exceptions Effectively and Appropriately
Description: Proper exception handling makes code more robust and easier to debug. These practices ensure exceptions are used correctly and provide meaningful information.

### Example 2: Use Exceptions Only for Exceptional Conditions

Title: Don't use exceptions for ordinary control flow
Description: Exceptions should be used for exceptional conditions, not for normal program flow. They are expensive and make code harder to understand.

**Good example:**

```java
public class NumberProcessor {
    public void processNumbers(int[] numbers) {
        for (int number : numbers) {  // Normal iteration
            if (isValid(number)) {    // Normal condition checking
                process(number);
            } else {
                System.out.println("Skipping invalid number: " + number);
            }
        }
    }

    private boolean isValid(int number) {
        return number >= 0 && number <= 1000;
    }

    private void process(int number) {
        // Process the number
        System.out.println("Processing: " + number);
    }
}
```

**Bad example:**

```java
public class NumberProcessor {
    public void processNumbers(int[] numbers) {
        try {
            int i = 0;
            while (true) {  // Using exception for loop termination
                process(numbers[i++]);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            // Using exception for normal control flow - bad!
        }
    }

    private void process(int number) {
        System.out.println("Processing: " + number);
    }
}
```

### Example 3: Use Checked Exceptions for Recoverable Conditions and Runtime Exceptions for Programming Errors

Title: Choose the right type of exception for the situation
Description: Checked exceptions force the caller to handle recoverable conditions, while runtime exceptions indicate programming errors.

**Good example:**

```java
public class FileProcessor {
    /**
     * Processes a file. Throws checked exception for recoverable I/O issues.
     */
    public void processFile(String filename) throws FileProcessingException {
        try {
            // File operations that might fail due to external factors
            Files.readAllLines(Paths.get(filename));
        } catch (IOException e) {
            // Wrap in domain-specific checked exception
            throw new FileProcessingException("Failed to process file: " + filename, e);
        }
    }

    /**
     * Validates input parameters. Throws runtime exception for programming errors.
     */
    public void validateInput(String input) {
        if (input == null) {
            // Programming error - should never happen in correct code
            throw new IllegalArgumentException("Input cannot be null");
        }
        if (input.trim().isEmpty()) {
            // Programming error - caller should validate before calling
            throw new IllegalArgumentException("Input cannot be empty");
        }
    }
}

// Custom checked exception for recoverable conditions
class FileProcessingException extends Exception {
    public FileProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

**Bad example:**

```java
public class FileProcessor {
    // Bad: Using checked exception for programming error
    public void validateInput(String input) throws ValidationException {
        if (input == null) {
            throw new ValidationException("Input cannot be null");  // Should be RuntimeException
        }
    }

    // Bad: Using runtime exception for recoverable condition
    public void processFile(String filename) {
        try {
            Files.readAllLines(Paths.get(filename));
        } catch (IOException e) {
            // Should be checked exception so caller can handle
            throw new RuntimeException("File processing failed", e);
        }
    }
}
```

### Example 4: Favor the Use of Standard Exceptions

Title: Use standard Java exceptions when appropriate
Description: Standard exceptions are familiar to developers and have clear semantics. Don't reinvent the wheel.

**Good example:**

```java
public class Calculator {
    public double divide(double dividend, double divisor) {
        if (divisor == 0.0) {
            throw new ArithmeticException("Division by zero");  // Standard exception
        }
        return dividend / divisor;
    }

    public int getElement(List<Integer> list, int index) {
        Objects.requireNonNull(list, "list");  // Standard NullPointerException
        if (index < 0 || index >= list.size()) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + list.size());
        }
        return list.get(index);
    }

    public void processPositiveNumber(int number) {
        if (number <= 0) {
            throw new IllegalArgumentException("Number must be positive: " + number);
        }
        // Process the number
    }
}
```

**Bad example:**

```java
public class Calculator {
    public double divide(double dividend, double divisor) {
        if (divisor == 0.0) {
            throw new DivisionByZeroException("Cannot divide by zero");  // Custom exception when standard would do
        }
        return dividend / divisor;
    }

    public int getElement(List<Integer> list, int index) {
        if (list == null) {
            throw new ListIsNullException("List cannot be null");  // Custom exception when standard would do
        }
        if (index < 0 || index >= list.size()) {
            throw new InvalidIndexException("Bad index: " + index);  // Custom exception when standard would do
        }
        return list.get(index);
    }
}

// Unnecessary custom exceptions
class DivisionByZeroException extends RuntimeException {
    public DivisionByZeroException(String message) {
        super(message);
    }
}
class ListIsNullException extends RuntimeException {
    public ListIsNullException(String message) {
        super(message);
    }
}
class InvalidIndexException extends RuntimeException {
    public InvalidIndexException(String message) {
        super(message);
    }
}
```

### Example 5: Include Failure-Capture Information in Detail Messages

Title: Provide detailed, actionable information in exception messages
Description: Exception messages should contain all information needed to diagnose the failure.

**Good example:**

```java
public class BankAccount {
    private double balance;
    private final String accountNumber;

    public BankAccount(String accountNumber, double initialBalance) {
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
    }

    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException(
                String.format("Withdrawal amount must be positive. Account: %s, Amount: %.2f",
                             accountNumber, amount));
        }
        if (amount > balance) {
            throw new InsufficientFundsException(
                String.format("Insufficient funds. Account: %s, Balance: %.2f, Requested: %.2f",
                             accountNumber, balance, amount));
        }
        balance -= amount;
    }

    public void transfer(BankAccount toAccount, double amount) {
        if (toAccount == null) {
            throw new IllegalArgumentException(
                String.format("Destination account cannot be null. Source account: %s, Amount: %.2f",
                             accountNumber, amount));
        }
        if (toAccount.accountNumber.equals(this.accountNumber)) {
            throw new IllegalArgumentException(
                String.format("Cannot transfer to same account. Account: %s, Amount: %.2f",
                             accountNumber, amount));
        }
        withdraw(amount);
        toAccount.deposit(amount);
    }

    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException(
                String.format("Deposit amount must be positive. Account: %s, Amount: %.2f",
                             accountNumber, amount));
        }
        balance += amount;
    }
}

class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(String message) {
        super(message);
    }
}
```

**Bad example:**

```java
public class BankAccount {
    private double balance;
    private final String accountNumber;

    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Invalid amount");  // Too vague
        }
        if (amount > balance) {
            throw new InsufficientFundsException("Not enough money");  // No specific details
        }
        balance -= amount;
    }

    public void transfer(BankAccount toAccount, double amount) {
        if (toAccount == null) {
            throw new IllegalArgumentException("Bad account");  // No context
        }
        if (toAccount.accountNumber.equals(this.accountNumber)) {
            throw new IllegalArgumentException("Error");  // Completely unhelpful
        }
        withdraw(amount);
        toAccount.deposit(amount);
    }
}
```

### Example 6: Don't Ignore Exceptions

Title: Always handle exceptions appropriately, never ignore them silently
Description: Ignoring exceptions can hide bugs and make debugging extremely difficult.

**Good example:**

```java
public class FileManager {
    private static final Logger logger = LoggerFactory.getLogger(FileManager.class);

    public Optional<String> readFileContent(String filename) {
        try {
            return Optional.of(Files.readString(Paths.get(filename)));
        } catch (IOException e) {
            // Log the exception with context
            logger.warn("Failed to read file: {}", filename, e);
            return Optional.empty();  // Return meaningful result
        }
    }

    public void saveToFile(String filename, String content) throws FileOperationException {
        try {
            Files.writeString(Paths.get(filename), content);
            logger.info("Successfully saved content to file: {}", filename);
        } catch (IOException e) {
            // Re-throw as domain-specific exception with context
            throw new FileOperationException("Failed to save content to file: " + filename, e);
        }
    }

    public void cleanupTempFiles(List<String> tempFiles) {
        for (String tempFile : tempFiles) {
            try {
                Files.deleteIfExists(Paths.get(tempFile));
            } catch (IOException e) {
                // Log but continue with other files
                logger.warn("Failed to delete temporary file: {}", tempFile, e);
            }
        }
    }
}

class FileOperationException extends Exception {
    public FileOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

**Bad example:**

```java
public class FileManager {
    public String readFileContent(String filename) {
        try {
            return Files.readString(Paths.get(filename));
        } catch (IOException e) {
            // Silently ignoring exception - very bad!
        }
        return null;  // Caller has no idea what went wrong
    }

    public void saveToFile(String filename, String content) {
        try {
            Files.writeString(Paths.get(filename), content);
        } catch (IOException e) {
            // Empty catch block - hiding the problem
        }
    }

    public void processFiles(List<String> files) {
        for (String file : files) {
            try {
                // Some file processing
                Files.readString(Paths.get(file));
            } catch (Exception e) {
                // Catching Exception is too broad, and then ignoring it
            }
        }
    }
}
```