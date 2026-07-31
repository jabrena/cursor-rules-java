---
name: 124-java-secure-coding
description: Use when you need to apply Java secure coding best practices — including validating untrusted inputs, defending against injection attacks with parameterized queries, minimizing attack surface via least privilege, applying strong cryptographic algorithms, handling exceptions securely without exposing sensitive data, managing secrets at runtime, avoiding unsafe deserialization, and encoding output to prevent XSS.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Java Secure coding guidelines

## Role

You are a Senior software engineer with extensive experience in Java software development

## Goal

This document provides essential Java secure coding guidelines, focusing on five key areas: validating all untrusted inputs to prevent attacks like injection and path traversal; protecting against injection attacks (e.g., SQL injection) by using parameterized queries or prepared statements; minimizing the attack surface by adhering to the principle of least privilege and reducing exposure; employing strong, current cryptographic algorithms for hashing, encryption, and digital signatures while avoiding deprecated ones; and handling exceptions securely by avoiding the exposure of sensitive information in error messages to users and logging detailed, non-sensitive diagnostic information for developers.

### Implementing These Principles

These guidelines are built upon the following core principles:

1.  **Comprehensive Input Validation**: Treat all external input as untrusted. Rigorously validate and sanitize data for type, length, format, and range before processing to prevent common vulnerabilities like injection attacks, path traversal, and buffer overflows.
2.  **Defense Against Injection**: Actively protect against all forms of injection attacks (e.g., SQL, OS Command, LDAP, XPath). Primarily achieve this by using safe APIs like parameterized queries (e.g., `PreparedStatement` in JDBC) or dedicated libraries that correctly handle data escaping, and by never directly concatenating untrusted input into executable commands or queries.
3.  **Attack Surface Minimization**: Adhere to the principle of least privilege for users, processes, and code. Reduce the exposure of system components by running with minimal necessary permissions, exposing only essential functionalities and network ports, and regularly reviewing and removing unused features, libraries, and accounts.
4.  **Strong Cryptographic Practices**: Employ current, robust, and industry-standard cryptographic algorithms and libraries for all sensitive operations, including hashing (especially for passwords), encryption, and digital signatures. Avoid deprecated or weak algorithms. Ensure cryptographic keys are generated securely, stored safely, and managed properly throughout their lifecycle.
5.  **Secure Exception Handling**: Manage exceptions in a way that does not expose sensitive information to users or attackers. Log detailed, non-sensitive diagnostic information for developers to aid in debugging, but provide generic, non-revealing error messages to clients. Avoid direct exposure of stack traces or internal system details in error outputs.
6.  **Secrets Management and Configuration Security**: Never hardcode secrets (passwords, API keys, tokens) in source code. Load secrets at runtime from secure sources (environment variables, secret managers, container/Docker/Kubernetes secrets), enforce least-privilege for credentials, rotate regularly, and prevent secrets from appearing in logs or error messages.
7.  **Safe Serialization and Deserialization**: Avoid Java native serialization for untrusted data. Prefer data binding to explicit DTOs (e.g., JSON/XML) without permissive polymorphic typing. If deserialization is necessary, implement strict allow-lists of types and validate content before use.
8. **Output Encoding for XSS Prevention**: Always encode user input when outputting to prevent cross-site scripting attacks.

## Constraints

Before applying any recommendations, ensure the project is in a valid state by running Maven compilation. Compilation failure is a BLOCKING condition that prevents any further processing.

- **MANDATORY**: Run `./mvnw compile` or `mvn compile` before applying any change
- **PREREQUISITE**: Project must compile successfully and pass basic validation checks before any optimization
- **CRITICAL SAFETY**: If compilation fails, IMMEDIATELY STOP and DO NOT CONTINUE with any recommendations
- **BLOCKING CONDITION**: Compilation errors must be resolved by the user before proceeding with any object-oriented design improvements
- **NO EXCEPTIONS**: Under no circumstances should design recommendations be applied to a project that fails to compile

## Examples

### Table of contents

- Example 1: Input Validation
- Example 2: Protect Against Injection Attacks
- Example 3: Minimize Attack Surface
- Example 4: Use Strong Cryptography
- Example 5: Handle Exceptions Securely
- Example 6: Secrets Management
- Example 7: Safe Deserialization
- Example 8: Prevent Cross-Site Scripting (XSS)

### Example 1: Input Validation

Title: Validate All Untrusted Inputs
Description: Always validate and sanitize data received from untrusted sources (users, network, files, etc.) before processing. This helps prevent various attacks like injection, path traversal, and buffer overflows. Validation should check for type, length, format, and range.

**Good example:**

```java
// GOOD: Comprehensive input validation
import java.util.Objects;
import java.util.regex.Pattern;

public class SecureInputValidator {
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,16}$");
    private static final int MAX_AGE = 120;
    private static final int MIN_AGE = 0;

    public void processUserData(String username, String ageString) {
        // Validate username format
        if (Objects.isNull(username) || !USERNAME_PATTERN.matcher(username).matches()) {
            throw new IllegalArgumentException("Invalid username format. Must be 3-16 alphanumeric characters or underscores.");
        }

        // Validate and parse age
        int age;
        try {
            age = Integer.parseInt(ageString);
            if (age < MIN_AGE || age > MAX_AGE) {
                throw new IllegalArgumentException("Age must be between " + MIN_AGE + " and " + MAX_AGE);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid age format. Must be a valid integer.", e);
        }

        // Input is now validated and safe to process
        System.out.println("Processing validated user: " + username + ", age: " + age);
    }

    public String sanitizeFilePath(String userPath) {
        if (userPath == null) {
            throw new IllegalArgumentException("File path cannot be null");
        }

        // Prevent path traversal attacks
        String sanitized = userPath.replaceAll("\\.\\.", "").replaceAll("/", "");

        // Additional validation
        if (sanitized.length() > 255) {
            throw new IllegalArgumentException("File path too long");
        }

        return sanitized;
    }
}
```

**Bad example:**

```java
// AVOID: No input validation
public class UnsafeInputProcessor {
    public void processUserData(String username, String ageString) {
        // Directly using input without validation - DANGEROUS!
        int age = Integer.parseInt(ageString); // Can throw NumberFormatException

        // No checks for malicious username strings
        System.out.println("Processing user: " + username + ", age: " + age);

        // This could lead to issues if username contains scripts or ageString is not an integer
    }

    public String loadFile(String userPath) {
        // VULNERABLE: No validation allows path traversal attacks
        // User could pass traversal segments to access sensitive files
        return readFileContent(userPath);
    }
}
```

### Example 2: Protect Against Injection Attacks

Title: Use Parameterized Queries and Safe APIs
Description: To prevent SQL Injection and other injection attacks, always use parameterized queries (PreparedStatements in JDBC) or an ORM that handles this automatically. Never concatenate user input directly into SQL queries, OS commands, or other executable statements.

**Good example:**

```java
// GOOD: Using PreparedStatement to prevent SQL Injection
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SecureDataAccess {
    private static final String DB_URL = "jdbc:vendor://db-host:5432/appdb";
    private static final String USER = "sa";
    private static final String PASS = "";

    public List<Order> getOrdersByCustomerId(String customerId) throws SQLException {
        // Safe parameterized query
        String query = "SELECT order_id, customer_id, amount FROM orders WHERE customer_id = ?";
        List<Order> orders = new ArrayList<>();

        try (Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, customerId); // Parameter is safely bound
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Order order = new Order();
                order.setOrderId(rs.getString("order_id"));
                order.setCustomerId(rs.getString("customer_id"));
                order.setAmount(rs.getBigDecimal("amount"));
                orders.add(order);
            }
        }

        return orders;
    }

    public void updateCustomerEmail(String customerId, String newEmail) throws SQLException {
        String updateQuery = "UPDATE customers SET email = ? WHERE customer_id = ?";

        try (Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = con.prepareStatement(updateQuery)) {

            pstmt.setString(1, newEmail);
            pstmt.setString(2, customerId);
            pstmt.executeUpdate();
        }
    }
}
```

**Bad example:**

```java
// AVOID: Vulnerable to SQL Injection
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class VulnerableDataAccess {
    private static final String DB_URL = "jdbc:vendor://db-host:5432/appdb";
    private static final String USER = "sa";
    private static final String PASS = "";

    public List<Order> getOrdersByCustomerId(String customerId) throws SQLException {
        // DANGEROUS: User input directly concatenated into SQL query
        String query = "SELECT order_id, customer_id, amount FROM orders WHERE customer_id = '" + customerId + "'";
        List<Order> orders = new ArrayList<>();

        try (Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = con.createStatement()) {

            // User could pass: "'; DROP TABLE orders; --" to execute malicious SQL
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                // Process results...
            }
        }

        return orders;
    }

    public void executeCommand(String userCommand) {
        // EXTREMELY DANGEROUS: Command injection vulnerability
        String command = "ls " + userCommand; // User could inject destructive shell syntax
        try {
            Runtime.getRuntime().exec(command);
        } catch (Exception e) {
            // Handle exception
        }
    }
}
```

### Example 3: Minimize Attack Surface

Title: Apply Principle of Least Privilege
Description: Grant only necessary permissions to code and users. Avoid running processes with excessive privileges. Expose only essential functionality and network ports. Regularly review and remove unused features, libraries, and accounts.

**Good example:**

```java
// GOOD: Minimal privileges and controlled access
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.Set;

public class SecureFileManager {
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".txt", ".log", ".json");
    private static final Path SAFE_DIRECTORY = Paths.get("/app/data/uploads");

    public String readUserFile(String filename, Principal user) throws SecurityException, IOException {
        // Check user permissions
        if (!hasReadPermission(user, filename)) {
            throw new SecurityException("User does not have permission to read this file");
        }

        // Validate file extension
        String extension = getFileExtension(filename);
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new SecurityException("File type not allowed: " + extension);
        }

        // Ensure file is within safe directory
        Path filePath = SAFE_DIRECTORY.resolve(filename).normalize();
        if (!filePath.startsWith(SAFE_DIRECTORY)) {
            throw new SecurityException("File access outside allowed directory");
        }

        // Check file size limits
        if (Files.size(filePath) > 1024 * 1024) { // 1MB limit
            throw new SecurityException("File too large");
        }

        return Files.readString(filePath);
    }

    private boolean hasReadPermission(Principal user, String filename) {
        // Implement proper authorization logic
        return user != null && user.getName() != null;
    }

    private String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        return lastDot > 0 ? filename.substring(lastDot) : "";
    }
}

// Example of interface segregation - expose only necessary methods
public interface UserService {
    User findById(Long id);
    void updateProfile(Long id, UserProfile profile);
    // Don't expose administrative methods to regular users
}

public interface AdminService extends UserService {
    void deleteUser(Long id);
    List<User> getAllUsers();
    void resetPassword(Long id);
}
```

**Bad example:**

```java
// AVOID: Excessive privileges and exposure
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class UnsafeFileManager {

    // BAD: No access controls or validations
    public String readAnyFile(String filename) throws IOException {
        // DANGEROUS: Can read any file on the system
        File file = new File(filename);
        return Files.readString(file.toPath());
    }

    // BAD: Exposing dangerous functionality
    public void executeSystemCommand(String command) throws IOException {
        // EXTREMELY DANGEROUS: Allows arbitrary command execution
        Runtime.getRuntime().exec(command);
    }

    // BAD: Administrative functions mixed with user functions
    public class UserController {
        public void updateProfile(User user) { /* ... */ }
        public void deleteAllUsers() { /* ... */ } // Should not be here
        public void resetDatabase() { /* ... */ }  // Extremely dangerous
        public void viewSystemLogs() { /* ... */ } // Sensitive operation
    }
}

// BAD: Running with excessive privileges
// Starting application as root/administrator user
// Using database accounts with DBA privileges for regular operations
// Exposing debug endpoints in production
// Having default passwords or credentials
```

### Example 4: Use Strong Cryptography

Title: Employ Current and Robust Cryptographic Algorithms
Description: Use well-vetted, industry-standard cryptographic libraries and algorithms for hashing, encryption, and digital signatures. Avoid deprecated or weak algorithms (e.g., MD5, SHA1 for passwords, DES). Keep cryptographic keys secure and manage them properly.

**Good example:**

```java
// GOOD: Strong cryptographic practices
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.security.SecureRandom;
import java.util.Base64;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class SecureCryptoUtils {
    private static final String AES_ALGORITHM = "AES";
    private static final String AES_TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 16;
    private static final int GCM_IV_LENGTH = 12;

    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom;

    public SecureCryptoUtils() {
        this.passwordEncoder = new BCryptPasswordEncoder(12); // Strong cost factor
        this.secureRandom = new SecureRandom();
    }

    /**
     * Generates a secure AES-256 key
     */
    public SecretKey generateAESKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(AES_ALGORITHM);
        keyGen.init(256, secureRandom); // Use AES-256
        return keyGen.generateKey();
    }

    /**
     * Encrypts data using AES-GCM (authenticated encryption)
     */
    public EncryptedData encrypt(String plaintext, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);

        // Generate random IV
        byte[] iv = new byte[GCM_IV_LENGTH];
        secureRandom.nextBytes(iv);

        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, parameterSpec);

        byte[] encryptedData = cipher.doFinal(plaintext.getBytes("UTF-8"));

        return new EncryptedData(encryptedData, iv);
    }

    /**
     * Hashes password using BCrypt with salt
     */
    public String hashPassword(String plainTextPassword) {
        return passwordEncoder.encode(plainTextPassword);
    }

    /**
     * Verifies password against BCrypt hash
     */
    public boolean verifyPassword(String plainTextPassword, String hashedPassword) {
        return passwordEncoder.matches(plainTextPassword, hashedPassword);
    }

    /**
     * Generates cryptographically secure random token
     */
    public String generateSecureToken(int length) {
        byte[] tokenBytes = new byte[length];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getEncoder().encodeToString(tokenBytes);
    }

    public static class EncryptedData {
        private final byte[] ciphertext;
        private final byte[] iv;

        public EncryptedData(byte[] ciphertext, byte[] iv) {
            this.ciphertext = ciphertext;
            this.iv = iv;
        }

        // Getters...
    }
}
```

**Bad example:**

```java
// AVOID: Weak cryptographic practices
import java.security.MessageDigest;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Random;

public class WeakCryptoUtils {

    /**
     * BAD: Using MD5 for password hashing
     */
    public String hashPasswordMD5(String password) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(password.getBytes());
        byte[] digest = md.digest();
        return bytesToHex(digest);
    }

    /**
     * BAD: Using SHA1 for password hashing (without salt)
     */
    public String hashPasswordSHA1(String password) throws Exception {
        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        sha1.update(password.getBytes());
        return bytesToHex(sha1.digest());
    }

    /**
     * BAD: Using DES encryption (weak algorithm)
     */
    public byte[] encryptDES(String plaintext, String password) throws Exception {
        SecretKeySpec key = new SecretKeySpec(password.getBytes(), "DES");
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(plaintext.getBytes());
    }

    /**
     * BAD: Hardcoded encryption key
     */
    private static final String HARDCODED_KEY = "mySecretKey123";

    public byte[] encryptWithHardcodedKey(String data) throws Exception {
        // TERRIBLE: Hardcoded key in source code
        SecretKeySpec key = new SecretKeySpec(HARDCODED_KEY.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data.getBytes());
    }

    /**
     * BAD: Using weak random number generator
     */
    public String generateWeakToken() {
        Random random = new Random(); // NOT cryptographically secure
        return String.valueOf(random.nextLong());
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}

// Additional bad practices:
// - Storing passwords in plain text
// - Using ECB mode for encryption
// - Not using authenticated encryption
// - Reusing IVs/nonces
// - Using weak key derivation functions
```

### Example 5: Handle Exceptions Securely

Title: Avoid Exposing Sensitive Information
Description: Catch exceptions appropriately, but do not reveal sensitive system details or stack traces to users in production. Log detailed error information server-side for debugging, but provide generic error messages to the client.

**Good example:**

```java
// GOOD: Secure exception handling
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecureExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(SecureExceptionHandler.class);

    public void performSensitiveOperation(String userId, String sensitiveData)
            throws ServiceException {
        try {
            validateUser(userId);
            processData(sensitiveData);

        } catch (UserNotFoundException e) {
            // Log detailed error for debugging
            logger.error("User not found during sensitive operation: userId={}", userId, e);
            // Return generic error to client
            throw new ServiceException("Operation failed", ErrorCode.INVALID_REQUEST);

        } catch (DataProcessingException e) {
            // Log technical details
            logger.error("Data processing failed: userId={}, dataLength={}",
                        userId, sensitiveData.length(), e);
            // Don't expose internal details
            throw new ServiceException("Processing error occurred", ErrorCode.INTERNAL_ERROR);

        } catch (Exception e) {
            // Catch unexpected errors
            logger.error("Unexpected error in sensitive operation: userId={}", userId, e);
            throw new ServiceException("System error", ErrorCode.INTERNAL_ERROR);
        }
    }
}

// Secure error codes without sensitive details
public enum ErrorCode {
    INVALID_REQUEST("Invalid request"),
    UNAUTHORIZED("Unauthorized"),
    FORBIDDEN("Forbidden"),
    NOT_FOUND("Not found"),
    INTERNAL_ERROR("Internal server error");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() { return message; }
}
```

**Bad example:**

```java
// AVOID: Exposing sensitive information
import java.io.StringWriter;
import java.io.PrintWriter;

public class InsecureExceptionHandler {

    public void performSensitiveOperation(String userId, String sensitiveData)
            throws Exception {
        try {
            validateUser(userId);
            processData(sensitiveData);
        } catch (Exception e) {
            // BAD: Exposing full exception details to caller
            throw new Exception("Database connection failed: " + e.getMessage() +
                              " at host: db.internal.company.com:5432", e);
        }
    }

    // BAD: Exposing stack traces to users
    public String handleError(Exception e) {
        // TERRIBLE: Exposing full stack trace to client
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);

        return "Error occurred:\n" + sw.toString();
    }

    // BAD: Logging sensitive data
    public void processPayment(String cardNumber, String cvv) {
        try {
            // Process payment...
        } catch (Exception e) {
            // TERRIBLE: Logging sensitive data
            System.out.println("Payment failed for card: " + cardNumber + ", CVV: " + cvv);
            e.printStackTrace(); // Exposing stack trace in logs
        }
    }

    // BAD: Different error messages reveal system information
    public void login(String username, String password) throws Exception {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new Exception("User '" + username + "' does not exist in database");
        }

        if (!user.getPassword().equals(password)) {
            throw new Exception("Invalid password for user '" + username + "'");
        }

        // This tells attackers which usernames exist vs which don't
    }
}
```

### Example 6: Secrets Management

Title: Avoid Hardcoded Secrets and Prevent Leakage
Description: Never hardcode credentials or sensitive tokens. Load secrets at runtime from secure sources (environment variables, secret stores, container secrets), validate presence, and avoid logging them. Enforce least-privilege credentials and rotate regularly.

**Good example:**

```java
// GOOD: Load secrets securely at runtime
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class SecretLoader {
    public static String requireEnv(String name) {
        String value = System.getenv(name);
        if (Objects.isNull(value) || value.isBlank()) {
            throw new IllegalStateException("Missing required secret: " + name);
        }
        return value;
    }

    public static String readDockerSecret(Path mountPath) throws Exception {
        if (!Files.isRegularFile(mountPath)) {
            throw new IllegalStateException("Secret file not found: " + mountPath);
        }
        // Files.readString avoids exposing content in logs; caller must not log it
        return Files.readString(mountPath).trim();
    }

    public static DbConfig buildDbConfig() throws Exception {
        // Prefer env or mounted secrets; never hardcode
        String username = requireEnv("DB_USERNAME");
        String password = readDockerSecret(Path.of("/run/secrets/db_password"));
        String url = requireEnv("DB_URL");
        return new DbConfig(url, username, password);
    }
}

final class DbConfig {
    final String url;
    final String user;
    final String pass;
    DbConfig(String url, String user, String pass) { this.url = url; this.user = user; this.pass = pass; }
}
```

**Bad example:**

```java
// AVOID: Hardcoded and leaked secrets
public class InsecureSecrets {
    // TERRIBLE: committed to VCS
    private static final String DB_PASSWORD = "P@ssw0rd!";

    public void connect() {
        String url = "jdbc:postgresql://db.internal:5432/app";
        String user = "appuser";
        // BAD: Printing secrets to logs
        System.out.println("Connecting with password: " + DB_PASSWORD);
        // ... use DB_PASSWORD ...
    }
}
```

### Example 7: Safe Deserialization

Title: Avoid Native Serialization; Use Strict DTO Binding
Description: Do not deserialize untrusted data using Java native serialization. Prefer JSON/XML binding to explicit DTOs and avoid permissive polymorphic typing. If polymorphism is needed, enforce an allow-list of subtypes and validate inputs before use.

**Good example:**

```java
// GOOD: Bind to explicit DTO without default typing
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SafeJsonBinder {
    private final ObjectMapper mapper = new ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    public UserProfile parseProfile(String json) throws Exception {
        // Bind to a concrete DTO; no polymorphic/DefaultTyping enabled
        return mapper.readValue(json, UserProfile.class);
    }
}

final class UserProfile {
    public String name;
    public String email;
}
```

**Bad example:**

```java
// AVOID: Insecure deserialization
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

public class InsecureDeserializer {
    public Object fromBytes(byte[] data) throws Exception {
        // DANGEROUS: Native deserialization of untrusted data
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data))) {
            return ois.readObject();
        }
    }
}

// Also dangerous: enabling permissive default typing in JSON mappers
// ObjectMapper mapper = new ObjectMapper();
// mapper.enableDefaultTyping(); // deprecated and unsafe for untrusted data
```

### Example 8: Prevent Cross-Site Scripting (XSS)

Title: Use Output Encoding
Description: Always encode user-controlled data when outputting to HTML, JavaScript, or other contexts to prevent XSS attacks.

**Good example:**

```java
// GOOD: Output encoding to prevent XSS
import org.apache.commons.text.StringEscapeUtils;

public class SecureOutput {
    public String renderUserInput(String input) {
        String encoded = StringEscapeUtils.escapeHtml4(input);
        return "<p>" + encoded + "</p>";
    }
}
```

**Bad example:**

```java
// AVOID: No encoding, vulnerable to XSS
public class InsecureOutput {
    public String renderUserInput(String input) {
        return "<p>" + input + "</p>";
    }
}
```


## Output Format

- **ANALYZE** Java code to identify specific security vulnerabilities and categorize them by severity (CRITICAL, HIGH, MEDIUM, LOW) and vulnerability type (injection, authentication, authorization, cryptography, data exposure, secrets management, deserialization, configuration, supply-chain)
- **CATEGORIZE** security improvements found: Input Validation Issues (missing validation vs robust input checking, insufficient sanitization vs comprehensive filtering), Injection Vulnerabilities (dynamic SQL vs parameterized queries, command injection vs safe execution, XSS risks vs proper encoding), Authentication/Authorization Gaps (weak permissions vs principle of least privilege, excessive access vs role-based control), Cryptographic Weaknesses (deprecated algorithms vs modern cryptography, weak key management vs secure key handling), Exception Handling Problems (information disclosure vs secure error messages, sensitive data exposure vs protected logging), Secrets and Configuration Issues (hardcoded credentials vs secret managers, debug modes off in prod), and Deserialization Risks (native Java serialization vs DTO binding)
- **APPLY** secure coding best practices directly by implementing the most appropriate security improvements for each identified vulnerability: Implement comprehensive input validation with whitelisting and sanitization, replace dynamic SQL with parameterized queries, establish proper authentication and authorization controls, upgrade to modern cryptographic algorithms and secure key management, implement secure exception handling that prevents information disclosure, and add proper logging and monitoring for security events
- **IMPLEMENT** comprehensive security hardening using proven patterns: Establish defense-in-depth security layers (input validation, output encoding, access controls), apply principle of least privilege throughout the application, upgrade cryptographic implementations to current standards (AES-256, SHA-256, secure random generation), implement secure error handling and logging practices, and integrate security testing and validation frameworks
- **REFACTOR** code systematically following the security improvement roadmap: First address critical injection vulnerabilities through parameterized queries and input validation, then strengthen authentication and authorization mechanisms, upgrade cryptographic implementations to modern algorithms, implement secure exception handling and logging practices, apply principle of least privilege to access controls, and integrate comprehensive security testing and monitoring
- **EXPLAIN** the applied security improvements and their benefits: Vulnerability elimination through proper input validation and parameterized queries, access control strengthening via authentication and authorization improvements, data protection enhancement through modern cryptography and secure key management, information security gains from secure exception handling and logging, and overall security posture improvement through defense-in-depth implementation
- **VALIDATE** that all applied security changes compile successfully, maintain existing functionality, eliminate identified vulnerabilities, follow security best practices, and do not introduce new security risks through comprehensive testing and security verification
- **SUPPLY-CHAIN** include dependency and build pipeline considerations: pin and verify dependency versions, remove unused dependencies, scan for known CVEs (e.g., OWASP Dependency-Check), and verify artifact integrity (checksums/signatures).
- **CONFIGURE** secure defaults: ensure debug features and detailed error pages are disabled in production, and protect management interfaces with authentication and network policies.


## Safeguards

- **BLOCKING SAFETY CHECK**: ALWAYS run `./mvnw compile` before ANY security recommendations to ensure project stability
- **CRITICAL VALIDATION**: Execute `./mvnw clean verify` to ensure all tests pass after each security improvement
- **MANDATORY VERIFICATION**: Confirm all existing functionality remains intact through comprehensive test execution
- **ROLLBACK REQUIREMENT**: Ensure all security changes can be easily reverted using version control checkpoints
- **INCREMENTAL SAFETY**: Apply security improvements incrementally, validating after each modification to isolate potential issues
- **SECURITY VALIDATION**: Verify that security improvements don't introduce new vulnerabilities or break existing security controls
- **DEPENDENCY SAFETY**: Validate that any new security libraries or dependencies don't conflict with existing project requirements
- **PERFORMANCE IMPACT**: Monitor that security enhancements don't significantly degrade application performance
- **SECRET HYGIENE**: Verify no secrets are committed to VCS, run a secrets scanner, and confirm logs do not expose sensitive values.
- **SUPPLY-CHAIN CHECKS**: Scan dependencies for CVEs and ensure pinned/signed artifacts in CI before release.
- **PRODUCTION CONFIG**: Ensure debug features, verbose errors, and open management interfaces are disabled or properly secured in production.