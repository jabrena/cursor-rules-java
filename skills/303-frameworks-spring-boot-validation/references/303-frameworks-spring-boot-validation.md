---
name: 303-frameworks-spring-boot-validation
description: Use when you need to design, review, or improve validation in Spring Boot applications — including Bean Validation on request DTOs, @Valid/@Validated at API boundaries, constraint groups, custom constraints, @ConfigurationProperties validation, nested DTO validation, and consistent validation error handling.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Spring Boot Validation Guidelines

## Role

You are a Senior software engineer with extensive experience in Spring Boot and Jakarta Bean Validation

## Goal

Enforce validation at the boundaries of Spring Boot applications so invalid input is rejected early, errors are predictable for clients, and domain/service layers receive valid data. Prefer declarative Bean Validation annotations and centralized error handling over ad-hoc if/else checks in controllers. Align with `@302-frameworks-spring-boot-rest` for HTTP status codes and problem bodies on validation failures.

## Constraints

Before applying recommendations, ensure the project compiles. Compilation failure is a blocking condition.

- **MANDATORY**: Run `./mvnw compile` or `mvn compile` before applying any change
- **SAFETY**: If compilation fails, stop immediately
- **VERIFY**: Run `./mvnw clean verify` or `mvn clean verify` after applying improvements

## Examples

### Table of contents

- Example 1: Request body validation with @Valid
- Example 2: Constraint groups with @Validated
- Example 3: Path and query parameter validation
- Example 4: Nested and collection validation
- Example 5: @ConfigurationProperties with @Validated
- Example 6: Custom constraint
- Example 7: Centralized validation error mapping

### Example 1: Request body validation with @Valid

Title: Bean Validation on DTOs; trigger validation on @RequestBody
Description: Annotate inbound DTOs with Jakarta constraints and add `@Valid` on the `@RequestBody` parameter. Without `@Valid`, Bean Validation does not run for that parameter.

**Good example:**

```java
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
class UserController {
    @PostMapping
    ResponseEntity<Void> create(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.noContent().build();
    }
}

record CreateUserRequest(
    @NotBlank String username,
    @NotBlank @Email String email,
    @NotBlank @Size(min = 8, max = 128) String password
) { }
```

**Bad example:**

```java
@RestController
class BadUserController {
    @PostMapping("/users")
    void create(@RequestBody CreateUserRequest request) {
        if (request.email() == null || !request.email().contains("@")) {
            throw new IllegalArgumentException("bad email");
        }
    }
}
record CreateUserRequest(String username, String email, String password) { }
```

### Example 2: Constraint groups with @Validated

Title: Separate create vs update validation on the same DTO
Description: Use `@Validated` on the controller class and pass constraint groups to `@Validated({Group.class})` on methods so POST and PUT can validate different subsets of the same record/class.

**Good example:**

```java
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import jakarta.validation.groups.Default;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

interface ValidationGroups {
    interface OnCreate { }
    interface OnUpdate { }
}

@RestController
@RequestMapping("/items")
@Validated
class ItemController {

    @PostMapping
    ResponseEntity<Void> create(
            @Validated({ValidationGroups.OnCreate.class, Default.class}) @RequestBody ItemRequest request) {
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    ResponseEntity<Void> update(
            @Validated({ValidationGroups.OnUpdate.class, Default.class}) @RequestBody ItemRequest request) {
        return ResponseEntity.noContent().build();
    }
}

record ItemRequest(
    @Null(groups = ValidationGroups.OnCreate.class)
    @NotBlank(groups = ValidationGroups.OnUpdate.class)
    String id,
    @NotBlank String name
) { }
```

**Bad example:**

```java
@RestController
class BadItemController {
    @PutMapping("/items/{id}")
    void update(@PathVariable String id, @RequestBody ItemRequest body) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id required");
        }
    }
}
record ItemRequest(String id, String name) { }
```

### Example 3: Path and query parameter validation

Title: @Min, @Max, @Pattern on @PathVariable and @RequestParam
Description: For simple handler parameters on Spring Boot 4+, put Jakarta constraints directly on method parameters and let Spring MVC method validation raise `HandlerMethodValidationException`. Avoid class-level `@Validated` on controllers unless you intentionally need the older AOP-based validation path.

**Good example:**

```java
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
class ProductController {

    @GetMapping("/{id}")
    String get(@PathVariable @Pattern(regexp = "^[a-z0-9-]+$") String id) {
        return id;
    }

    @GetMapping
    String list(@RequestParam @Min(0) int page, @RequestParam @Min(1) @Max(100) int size) {
        return page + ":" + size;
    }
}
```

**Bad example:**

```java
@RestController
class BadProductController {
    @GetMapping("/products/{id}")
    String get(@PathVariable String id) {
        if (id.contains("..")) {
            throw new IllegalArgumentException("invalid");
        }
        return id;
    }
}
```

### Example 4: Nested and collection validation

Title: @Valid on nested records and list elements
Description: Cascade validation with `@Valid` on nested objects and on collection-typed fields where each element should be validated.

**Good example:**

```java
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
class OrderController {
    @PostMapping("/orders")
    void create(@Valid @RequestBody OrderRequest request) { }
}

record OrderRequest(
    @NotNull @Valid Address shipping,
    @NotEmpty @Valid List<@NotNull @Valid LineItem> lines
) { }

record Address(@NotNull @Size(max = 200) String street) { }

record LineItem(@NotNull @Size(min = 1, max = 64) String sku, @Positive int qty) { }
```

**Bad example:**

```java
record OrderRequest(Address shipping, List<LineItem> lines) { }
// Missing @Valid: nested Address and LineItem constraints are never evaluated
```

### Example 5: @ConfigurationProperties with @Validated

Title: Fail-fast binding validation at startup
Description: Bind external configuration to a type-safe bean and validate it with `@Validated` and Bean Validation annotations so misconfiguration fails application startup instead of failing at runtime in random code paths. Pairs with `@301-frameworks-spring-boot-core`.

**Good example:**

```java
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "app.api")
@Validated
record ApiProperties(
    @NotBlank String baseUrl,
    @Min(1) @Max(600) int timeoutSeconds
) { }
```

**Bad example:**

```java
@ConfigurationProperties(prefix = "app.api")
record ApiProperties(String baseUrl, int timeoutSeconds) { }
// No validation: negative timeout or blank URL only fails deep in HTTP client code
```

### Example 6: Custom constraint

Title: @Constraint + ConstraintValidator for cross-field rules
Description: Encapsulate reusable rules (e.g. password confirmation, date ranges) in a custom annotation and validator class registered for dependency injection when needed.

**Good example:**

```java
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SamePasswords.Validator.class)
@interface SamePasswords {
    String message() default "password and confirmation must match";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    class Validator implements ConstraintValidator<SamePasswords, PasswordForm> {
        @Override
        public boolean isValid(PasswordForm form, ConstraintValidatorContext ctx) {
            if (form == null) return true;
            return java.util.Objects.equals(form.password(), form.confirmPassword());
        }
    }
}

@SamePasswords
record PasswordForm(
    String password,
    String confirmPassword
) { }
```

**Bad example:**

```java
void register(String password, String confirm) {
    if (!password.equals(confirm)) {
        throw new IllegalStateException("mismatch");
    }
}
// Duplicated in every controller instead of a reusable constraint
```

### Example 7: Centralized validation error mapping

Title: @ControllerAdvice for MethodArgumentNotValidException, ConstraintViolationException, and HandlerMethodValidationException
Description: Map all validation failure types to HTTP 400 with RFC 7807 `ProblemDetail` bodies aligned with `@302-frameworks-spring-boot-rest`. `MethodArgumentNotValidException` fires for `@Valid @RequestBody`; `HandlerMethodValidationException` (Spring Boot 4+) covers MVC handler method-parameter validation; `ConstraintViolationException` still appears in older AOP-based `@Validated` flows and non-MVC validation paths. Populate `type`, `title`, `status`, `detail`, and `instance`; attach field-level messages as a `violations` extension property. Never return raw `BindingResult` stack traces, ad hoc `Map` shapes, or exception messages that leak internals.

**Good example:**

```java
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.net.URI;
import java.util.List;
import java.util.stream.StreamSupport;

@ControllerAdvice
class ValidationExceptionHandler {

    private ProblemDetail buildValidationProblem(HttpServletRequest request, List<String> violations) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST, "One or more request fields failed validation.");
        problem.setType(URI.create("https://example.com/problems/validation-error"));
        problem.setTitle("Validation Error");
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("violations", violations);
        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ProblemDetail> handleBody(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<String> violations = ex.getBindingResult().getFieldErrors().stream()
            .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
            .toList();
        return ResponseEntity.badRequest().body(buildValidationProblem(request, violations));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<ProblemDetail> handleConstraints(ConstraintViolationException ex, HttpServletRequest request) {
        List<String> violations = ex.getConstraintViolations().stream()
            .map(v -> v.getPropertyPath() + ": " + v.getMessage())
            .toList();
        return ResponseEntity.badRequest().body(buildValidationProblem(request, violations));
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    ResponseEntity<ProblemDetail> handleMethodValidation(HandlerMethodValidationException ex, HttpServletRequest request) {
        List<String> violations = ex.getParameterValidationResults().stream()
            .flatMap(result -> StreamSupport.stream(result.getResolvableErrors().spliterator(), false)
                .map(error -> result.getMethodParameter().getParameterName() + ": " + error.getDefaultMessage()))
            .toList();
        return ResponseEntity.badRequest().body(buildValidationProblem(request, violations));
    }
}
```

**Bad example:**

```java
@ExceptionHandler(MethodArgumentNotValidException.class)
ResponseEntity<Map<String, Object>> bad(MethodArgumentNotValidException ex) {
    return ResponseEntity.badRequest()
        .body(Map.of("error", "VALIDATION_ERROR", "details", List.of(ex.getMessage())));
}
// Ad hoc Map shape — not RFC 7807; leaks framework wording; only covers one exception type
```


## Output Format

- **ANALYZE** controllers and configuration beans for missing `@Valid`, missing `@Validated`, wrong groups, and uncascaded nested DTOs
- **CATEGORIZE** findings: boundary (HTTP), configuration startup, custom constraints, error response consistency
- **APPLY** declarative Bean Validation; add `@ControllerAdvice` mapping for `MethodArgumentNotValidException`, `ConstraintViolationException`, and `HandlerMethodValidationException` (Spring Boot 4+) as appropriate
- **STANDARDIZE** validation error payloads as RFC 7807 `ProblemDetail` (Spring Boot) with a `violations` extension property, aligned with `@302-frameworks-spring-boot-rest`
- **VALIDATE** with `./mvnw compile` before and `./mvnw clean verify` after changes


## Safeguards

- Do not omit `@Valid` on request bodies that carry constraints
- Do not leak stack traces, SQL, or internal field names beyond what the API contract documents
- Prefer incremental refactors; add tests for representative invalid payloads