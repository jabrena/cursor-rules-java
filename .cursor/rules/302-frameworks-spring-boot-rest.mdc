---
description: 
globs: 
alwaysApply: false
---
# Java REST API Design Principles

This comprehensive guide provides essential principles for designing robust, maintainable, and secure REST APIs using Spring Boot. These rules ensure your APIs follow industry best practices, maintain consistency, and provide excellent developer experience for API consumers.

## Implementing These Principles

These guidelines are built upon the following core principles:

- **Semantic Consistency**: Use HTTP methods, status codes, and URI patterns according to their intended semantics
- **Clear Communication**: Provide unambiguous API contracts through proper DTOs, error handling, and documentation
- **Security by Design**: Implement authentication, authorization, and input validation from the start
- **Evolutionary Design**: Version APIs and structure them to support future changes without breaking existing clients

## Table of contents

- Rule 1: Use HTTP Methods Correctly
- Rule 2: Design Clear and Consistent Resource URIs
- Rule 3: Use HTTP Status Codes Appropriately
- Rule 4: Implement Effective Request and Response Payloads (DTOs)
- Rule 5: Version Your APIs
- Rule 6: Handle Errors Gracefully
- Rule 7: Secure Your APIs
- Rule 8: Document Your APIs
- Rule 9: Use Controller Advice for Global Exception Handling
- Rule 10: Implement Problem Details for Error Responses

## Rule 1: Use HTTP Methods Correctly

Title: Employ HTTP Methods Semantically
Description: Use HTTP methods according to their defined semantics to ensure predictability and compliance with web standards. `GET` for retrieval, `POST` for creation, `PUT` for update/replace, `PATCH` for partial update, and `DELETE` for removal.

**Good example:**

```java
// Using Spring MVC annotations for illustration
@RestController
@RequestMapping("/users")
public class UserController {

    @GetMapping("/{id}") // GET for retrieving a user
    public ResponseEntity<UserDTO> getUser(@PathVariable String id) {
        // ... logic to fetch user ...
        return ResponseEntity.ok(new UserDTO());
    }

    @PostMapping // POST for creating a new user
    public ResponseEntity<UserDTO> createUser(@RequestBody UserCreateDTO userCreateDTO) {
        // ... logic to create user ...
        UserDTO newUser = new UserDTO(); // Assume it gets an ID after creation
        return ResponseEntity.created(URI.create("/users/" + newUser.getId())).body(newUser);
    }

    @PutMapping("/{id}") // PUT for replacing/updating a user
    public ResponseEntity<UserDTO> updateUser(@PathVariable String id, @RequestBody UserUpdateDTO userUpdateDTO) {
        // ... logic to update user ...
        return ResponseEntity.ok(new UserDTO());
    }

    @DeleteMapping("/{id}") // DELETE for removing a user
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        // ... logic to delete user ...
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/{id}") // PATCH for partial updates
    public ResponseEntity<UserDTO> partiallyUpdateUser(@PathVariable String id, @RequestBody Map<String, Object> updates) {
        // ... logic to partially update user ...
        return ResponseEntity.ok(new UserDTO());
    }
}
// Dummy DTO classes
class UserDTO { private String id; public String getId() { return id; } /* ... other fields, getters, setters ... */ }
class UserCreateDTO { /* ... fields ... */ }
class UserUpdateDTO { /* ... fields ... */ }
```

**Bad Example:**

```java
@RestController
@RequestMapping("/api")
public class BadUserController {

    // Bad: Using GET to perform a state change (e.g., delete)
    @GetMapping("/deleteUser")
    public ResponseEntity<String> deleteUserViaGet(@RequestParam String id) {
        System.out.println("Deleting user: " + id + " (Bad: GET used for delete)");
        // ... delete logic ...
        return ResponseEntity.ok("User deleted (but GET was used!)");
    }

    // Bad: Using POST for all operations, including retrieval
    @PostMapping("/getUser")
    public ResponseEntity<UserDTO> getUserViaPost(@RequestBody String idPayload) {
        System.out.println("Fetching user: " + idPayload + " (Bad: POST used for GET)");
        // ... fetch logic ...
        return ResponseEntity.ok(new UserDTO());
    }
}
```

## Rule 2: Design Clear and Consistent Resource URIs

Title: Use Nouns for Resources and Maintain URI Consistency
Description: Design URIs that are intuitive and clearly represent resources. Use nouns (e.g., `/users`, `/orders`) instead of verbs. Keep URIs consistent in style (e.g., lowercase, hyphenated or camelCase for path segments).

**Good example:**

```
GET /users                           // Get all users
GET /users/{userId}                  // Get a specific user
GET /users/{userId}/orders           // Get all orders for a specific user
GET /users/{userId}/orders/{orderId} // Get a specific order for a user
POST /users                          // Create a new user
```

**Bad Example:**

```
GET /getAllUsers
GET /fetchUserById?id={userId}
POST /createNewUser
GET /userOrders?userId={userId}  // Mixing query params and path styles inconsistently
POST /processUserOrderCreation   // URI contains verbs and is overly complex
```

## Rule 3: Use HTTP Status Codes Appropriately

Title: Return Meaningful HTTP Status Codes
Description: Utilize standard HTTP status codes to accurately reflect the outcome of API requests. This helps clients understand the result without needing to parse the response body for basic success/failure information.
- `200 OK`: General success.
- `201 Created`: Resource successfully created (often with a `Location` header pointing to the new resource).
- `204 No Content`: Success, but no content to return (e.g., after a successful `DELETE`).
- `400 Bad Request`: Client error (e.g., invalid syntax, missing parameters).
- `401 Unauthorized`: Authentication is required and has failed or has not yet been provided.
- `403 Forbidden`: Authenticated client does not have permission to access the resource.
- `404 Not Found`: Resource not found.
- `500 Internal Server Error`: A generic error message for unexpected server-side errors.

**Good example:**

```java
// (Inside a Spring @RestController method)
if (resourceNotFound) {
    return ResponseEntity.notFound().build(); // 404
}
if (!userHasPermission) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied"); // 403
}
if (validationFailed) {
    return ResponseEntity.badRequest().body("Invalid input data"); // 400
}
// For creation:
// return ResponseEntity.created(newResourceUri).body(newResource); // 201
// For successful deletion:
// return ResponseEntity.noContent().build(); // 204
```

**Bad Example:**

```java
import java.util.Objects;

public ResponseEntity<String> processSomething(String input) {
    try {
        if (Objects.isNull(input)) {
            // Client should receive a 400 Bad Request, not 200 with an error message in body.
            return ResponseEntity.ok("{\"error\":\"Input cannot be null\"}"); 
        }
        // ... process ...
        return ResponseEntity.ok("{\"data\":\"Success!\"}");
    } catch (Exception e) {
        // Client should receive a 500 Internal Server Error, not 200.
        return ResponseEntity.ok("{\"error\":\"Something went wrong on the server\"}");
    }
}
```

## Rule 4: Implement Effective Request and Response Payloads (DTOs)

Title: Use Data Transfer Objects (DTOs) for Payloads and Keep Them Lean
Description: Use dedicated DTO classes for request and response bodies instead of exposing internal domain/entity objects directly. This decouples your API contract from your internal data model. Keep DTOs focused on the data needed for the specific API operation. Use consistent naming conventions (e.g., JSON with camelCase keys).

**Good example:**

```java
// Domain Entity (internal)
class User {
    private Long id;
    private String username;
    private String passwordHash; // Internal field, should not be in API responses
    private String email;
    private java.time.LocalDateTime createdAt;
    // getters, setters
}

// DTO for API responses (exposes only necessary fields)
class UserResponseDTO {
    private Long id;
    private String username;
    private String email;
    // getters, setters
}

// DTO for creating a user
class UserCreateRequestDTO {
    private String username;
    private String password; // Received in request, then hashed internally
    private String email;
    // getters, setters
}

// In controller:
// public ResponseEntity<UserResponseDTO> getUser(@PathVariable Long id) { ... }
// public ResponseEntity<UserResponseDTO> createUser(@RequestBody UserCreateRequestDTO createDto) { ... }
```

**Bad Example:**

```java
// Bad: Exposing internal User entity directly in API, including sensitive fields like passwordHash.
@RestController
public class AnotherUserController {
    @GetMapping("/rawusers/{id}")
    public ResponseEntity<User> getRawUser(@PathVariable String id) {
        // Assume User class has passwordHash and other internal fields
        User internalUser = findUserById(id); // Method that returns the internal User entity
        return ResponseEntity.ok(internalUser); // Exposes passwordHash, createdAt, etc.
    }
    private User findUserById(String id) { return new User(); /* ... */}
}
```

## Rule 5: Version Your APIs

Title: Implement a Clear API Versioning Strategy
Description: Introduce API versioning from the beginning to manage changes and evolution without breaking existing clients. Common strategies include URI versioning (e.g., `/v1/users`), custom request header versioning (e.g., `X-API-Version: 1`), or media type versioning (e.g., `Accept: application/vnd.example.v1+json`). Choose a strategy and apply it consistently.

**Good example (URI Versioning):**

```java
@RestController
@RequestMapping("/api/v1/products") // Version in URI
public class ProductControllerV1 {
    // ... v1 endpoints ...
}

@RestController
@RequestMapping("/api/v2/products") // New version in URI
public class ProductControllerV2 {
    // ... v2 endpoints with potential breaking changes or new features ...
}
```

**Bad Example (No Versioning):**

```java
@RestController
@RequestMapping("/products") // No version information
public class UnversionedProductController {
    // If a breaking change is made here (e.g., field removed from response),
    // all existing clients might break.
    @GetMapping("/{id}")
    public ProductDTO getProduct(@PathVariable String id) {
        // ... logic ...
        return new ProductDTO();
    }
}
class ProductDTO {}
```

## Rule 6: Handle Errors Gracefully

Title: Provide Clear and Consistent Error Responses
Description: When an error occurs, return a standardized, machine-readable error response format (e.g., JSON). Include a unique error code or type, a human-readable message, and optionally, details about specific fields that caused validation errors. Do not expose sensitive internal details like stack traces in production error responses.

**Good example (Error DTO and @ControllerAdvice for Spring):**

```java
// Error Response DTO
class ErrorResponse {
    private String errorCode;
    private String message;
    private List<String> details; // Optional: for field-specific validation errors
    // Constructor, getters
    public ErrorResponse(String errorCode, String message) { this.errorCode = errorCode; this.message = message; }
    public ErrorResponse(String errorCode, String message, List<String> details) { /* ... */ }
}

@ControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleResourceNotFound(ResourceNotFoundException ex) {
        return new ErrorResponse("RESOURCE_NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class) // Example for bean validation errors
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                                .collect(Collectors.toList());
        return new ErrorResponse("VALIDATION_ERROR", "Input validation failed", errors);
    }
    
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGenericError(Exception ex) {
        // Log the full exception internally
        // logger.error("Unhandled exception:", ex);
        return new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred.");
    }
}
// Custom exception
class ResourceNotFoundException extends RuntimeException { public ResourceNotFoundException(String msg){ super(msg);}}
```

**Bad Example:**

```java
@RestController
public class BadErrorHandlingController {
    @GetMapping("/items/{id}")
    public ResponseEntity<String> getItem(@PathVariable String id) {
        if (id.equals("unknown")) {
            // Bad: Returning plain text error, or HTML, or inconsistent format.
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item not found!"); 
        }
        try {
            // ... some logic that might throw an exception ...
            if(id.equals("fail")) throw new NullPointerException("Simulated internal error");
            return ResponseEntity.ok("Item data");
        } catch (Exception e) {
            // Bad: Exposing stack trace to the client in production.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.toString());
        }
    }
}
```

## Rule 7: Secure Your APIs

Title: Implement Robust Security Measures
Description: Protect your APIs from common threats. Use HTTPS for all communication. Implement proper authentication (e.g., OAuth 2.0, JWT) and authorization (e.g., role-based access control). Validate all input data to prevent injection attacks (SQLi, XSS). Apply rate limiting and throttling to prevent abuse.

**Good example (Conceptual - actual implementation involves security frameworks like Spring Security):**

```java
// In a Spring Security configuration:
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable() // Consider CSRF protection needs
            .authorizeRequests()
                .antMatchers("/public/**").permitAll()
                .antMatchers("/admin/**").hasRole("ADMIN") // Role-based authorization
                .anyRequest().authenticated()       // All other requests need authentication
            .and()
            .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt); // Example: JWT authentication
            // .httpBasic(); // Or basic auth for simplicity in some cases
    }
    // ... user details service, password encoder, etc. ...
}

// In a controller method:
// @PreAuthorize("hasAuthority('SCOPE_read:users')") // Example: OAuth2 scope-based authorization
// @GetMapping("/users/{id}")
// public UserDTO getUser(@PathVariable String id) { ... }
```

**Bad Example:**

```java
@RestController
public class InsecureController {
    // Bad: No authentication or authorization for sensitive operations.
    @PostMapping("/admin/deleteAllData")
    public String deleteAllData() {
        System.out.println("All data deleted! (INSECURE - NO AUTH)");
        return "All data wiped.";
    }

    // Bad: Trusting user input directly in a query (conceptual SQLi vulnerability).
    @GetMapping("/products")
    public List<ProductDTO> searchProducts(@RequestParam String category) {
        // String query = "SELECT * FROM products WHERE category = '" + category + "'"; // SQL Injection risk!
        // Use PreparedStatement or an ORM to prevent SQLi.
        System.out.println("Searching for category (raw input): " + category);
        return List.of();
    }
}
```

## Rule 8: Document Your APIs

Title: Provide Clear and Comprehensive API Documentation
Description: Maintain up-to-date documentation for your API. Tools like Swagger/OpenAPI can be used to generate interactive documentation from code annotations. Documentation should cover resource URIs, HTTP methods, request/response formats (including DTO schemas), expected status codes, authentication methods, and error responses.

**Good example (Using Springdoc OpenAPI / Swagger annotations):**

```java
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
// Assume other necessary imports like Spring MVC, DTOs etc.

@RestController
@RequestMapping("/api/v1/widgets")
@Tag(name = "Widget API", description = "APIs for managing widgets")
public class WidgetController {

    @Operation(summary = "Get a widget by its ID",
               description = "Returns a single widget based on the provided ID.",
               responses = {
                   @ApiResponse(responseCode = "200", description = "Successfully retrieved widget",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = WidgetDTO.class))),
                   @ApiResponse(responseCode = "404", description = "Widget not found",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
               })
    @GetMapping("/{widgetId}")
    public ResponseEntity<WidgetDTO> getWidgetById(
            @Parameter(description = "ID of the widget to retrieve", required = true) 
            @PathVariable String widgetId) {
        // ... logic ...
        // return ResponseEntity.ok(new WidgetDTO(widgetId, "Sample Widget"));
        // For example, if not found:
        if ("unknown".equals(widgetId)) { 
            throw new ResourceNotFoundException("Widget with ID " + widgetId + " not found.");
        }
        return ResponseEntity.ok(new WidgetDTO()); // Placeholder
    }
}
class WidgetDTO { /* fields, getters, setters */ }
// ErrorResponse and ResourceNotFoundException as defined in Rule 6
```

**Bad Example (No Documentation or Incomplete):**

```java
// No API documentation tool usage, comments are sparse or missing.
@RestController
@RequestMapping("/legacy/things")
public class LegacyThingController {
    // What does this do? What are parameters? What are responses?
    @GetMapping("/{id}")
    public Object getAThing(@PathVariable String id, @RequestParam(required = false) String type) {
        // ... complex logic ...
        return new Object(); // Unclear what this object structure is.
    }
}
// Clients have to guess or read source code to understand how to use the API.
```

## Rule 9: Use Controller Advice for Global Exception Handling

Title: Implement Centralized Exception Handling with @ControllerAdvice
Description: Use `@ControllerAdvice` to create a centralized exception handling mechanism that can catch and handle both checked `Exception` and unchecked `RuntimeException` across all controllers. Use Spring Boot's built-in `ProblemDetail` class for consistent, standardized error responses that follow RFC 7807. This approach promotes DRY principles, ensures consistent error responses, and separates error handling logic from business logic.

**Good example:**

```java
@ControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleIllegalArgument(
            IllegalArgumentException ex, HttpServletRequest request) {
        logger.warn("Invalid argument: {}", ex.getMessage());
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST, ex.getMessage()
        );
        problemDetail.setType(URI.create("https://example.com/problems/invalid-argument"));
        problemDetail.setTitle("Invalid Argument");
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty("timestamp", Instant.now());
        
        return ResponseEntity.badRequest().body(problemDetail);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleEntityNotFound(
            EntityNotFoundException ex, HttpServletRequest request) {
        logger.warn("Entity not found: {}", ex.getMessage());
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND, ex.getMessage()
        );
        problemDetail.setType(URI.create("https://example.com/problems/entity-not-found"));
        problemDetail.setTitle("Entity Not Found");
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty("timestamp", Instant.now());
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ProblemDetail> handleRuntimeException(
            RuntimeException ex, HttpServletRequest request) {
        String errorId = UUID.randomUUID().toString();
        logger.error("Unexpected runtime exception with ID: {}", errorId, ex);
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR, 
            "An unexpected error occurred while processing the request"
        );
        problemDetail.setType(URI.create("https://example.com/problems/internal-error"));
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("errorId", errorId);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGenericException(
            Exception ex, HttpServletRequest request) {
        String errorId = UUID.randomUUID().toString();
        logger.error("Unexpected exception with ID: {}", errorId, ex);
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR, 
            "An unexpected error occurred while processing the request"
        );
        problemDetail.setType(URI.create("https://example.com/problems/internal-error"));
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("errorId", errorId);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.toList());
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST, "Validation failed for the provided input"
        );
        problemDetail.setType(URI.create("https://example.com/problems/validation-error"));
        problemDetail.setTitle("Validation Error");
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("violations", errors);
        
        return ResponseEntity.badRequest().body(problemDetail);
    }
}

// Custom exceptions
class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String message) { super(message); }
}
```

**Bad Example:**

```java
@RestController
public class BadUserController {
    
    // Bad: Exception handling scattered across multiple controllers
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUser(@PathVariable String id) {
        try {
            if (id.equals("invalid")) {
                throw new IllegalArgumentException("Invalid user ID");
            }
            if (id.equals("notfound")) {
                throw new EntityNotFoundException("User not found");
            }
            // ... business logic ...
            return ResponseEntity.ok(new UserDTO());
        } catch (IllegalArgumentException e) {
            // Bad: Inconsistent error format, not using ProblemDetail
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (EntityNotFoundException e) {
            // Bad: Different error format, no error details
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            // Bad: Exposing stack trace and inconsistent format
            return ResponseEntity.status(500).body("Server error: " + e.toString());
        }
    }

    // Bad: Different controller with different exception handling approach
    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody UserCreateDTO user) {
        try {
            // ... business logic ...
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            // Bad: Yet another inconsistent error format, not using ProblemDetail
            Map<String, String> error = Map.of("error", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    // Bad: Using custom error response instead of standard ProblemDetail
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        try {
            // ... business logic ...
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            // Bad: Custom error format instead of ProblemDetail
            CustomErrorResponse error = new CustomErrorResponse(
                "DELETE_ERROR", e.getMessage(), new Date()
            );
            return ResponseEntity.status(500).body(error);
        }
    }
}

// Bad: Custom error response class instead of using ProblemDetail
class CustomErrorResponse {
    private String code;
    private String message;
    private Date timestamp;
    // constructors, getters, setters...
}
```

## Rule 10: Implement Problem Details for Error Responses

Title: Use RFC 7807 Problem Details for HTTP APIs
Description: Implement standardized error responses using RFC 7807 Problem Details format for HTTP 500 (Internal Server Error) and other error responses. This provides machine-readable error information that includes a type, title, status, detail, and instance to help clients understand and handle errors consistently.

**Good example:**

```java
@ControllerAdvice
public class ProblemDetailsExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(ProblemDetailsExceptionHandler.class);

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ProblemDetail> handleRuntimeException(
            RuntimeException ex, HttpServletRequest request) {
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR, 
            "An unexpected error occurred while processing the request"
        );
        
        problemDetail.setType(URI.create("https://example.com/problems/internal-error"));
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("errorId", UUID.randomUUID().toString());
        
        // Log the actual exception for debugging (don't expose to client)
        logger.error("Internal server error with ID: {}", 
            problemDetail.getProperties().get("errorId"), ex);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleEntityNotFound(
            EntityNotFoundException ex, HttpServletRequest request) {
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND, ex.getMessage()
        );
        
        problemDetail.setType(URI.create("https://example.com/problems/entity-not-found"));
        problemDetail.setTitle("Entity Not Found");
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty("timestamp", Instant.now());
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ProblemDetail> handleValidation(
            ValidationException ex, HttpServletRequest request) {
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST, "Validation failed for the provided input"
        );
        
        problemDetail.setType(URI.create("https://example.com/problems/validation-error"));
        problemDetail.setTitle("Validation Error");
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("violations", ex.getViolations());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }
}

// Custom validation exception
class ValidationException extends RuntimeException {
    private final List<String> violations;
    
    public ValidationException(String message, List<String> violations) {
        super(message);
        this.violations = violations;
    }
    
    public List<String> getViolations() { return violations; }
}
```

**Bad Example:**

```java
@ControllerAdvice
public class BadExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleRuntimeException(RuntimeException ex) {
        // Bad: Non-standard error format, inconsistent fields
        Map<String, Object> error = new HashMap<>();
        error.put("error", true);
        error.put("msg", "Something went wrong");
        error.put("exception_type", ex.getClass().getSimpleName());
        error.put("time", new Date());
        
        // Bad: Exposing sensitive stack trace information
        error.put("stack_trace", Arrays.toString(ex.getStackTrace()));
        
        return error;
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleNotFound(EntityNotFoundException ex) {
        // Bad: Inconsistent response format (string vs JSON vs problem details)
        return ResponseEntity.status(404).body("Not found: " + ex.getMessage());
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Object> handleValidation(ValidationException ex) {
        // Bad: Yet another inconsistent format
        return ResponseEntity.badRequest().body(
            Map.of("validationErrors", ex.getViolations())
        );
    }

    // Bad: Missing proper error ID, timestamps, and structured format
    // Bad: No type URIs or standard problem details structure
    // Bad: Inconsistent error formats across different exception types
}
```
