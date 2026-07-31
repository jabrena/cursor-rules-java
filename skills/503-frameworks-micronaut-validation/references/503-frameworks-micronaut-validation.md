---
name: 503-frameworks-micronaut-validation
description: Use when you need to design, review, or improve validation in Micronaut applications — including Bean Validation on @Controller methods, @Body @Valid, query/path parameter validation, @ConfigurationProperties validation, custom constraints, nested DTO validation, and ExceptionHandler mapping for constraint violations.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Micronaut Validation Guidelines

## Role

You are a Senior software engineer with extensive experience in Micronaut and Jakarta Bean Validation

## Goal

Ensure Micronaut HTTP APIs validate inputs at boundaries, keep constraint definitions on DTOs and command models, and return stable validation error responses. Align with `@502-frameworks-micronaut-rest` for status codes and error bodies.

## Constraints

Before applying recommendations, ensure the project compiles. Compilation failure is a blocking condition.

- **MANDATORY**: Run `./mvnw compile` or `mvn compile` before applying any change
- **SAFETY**: If compilation fails, stop immediately
- **VERIFY**: Run `./mvnw clean verify` or `mvn clean verify` after applying improvements

## Examples

### Table of contents

- Example 1: Request body validation
- Example 2: Query and path parameters
- Example 3: @ConfigurationProperties validation
- Example 4: Nested and collection validation
- Example 5: ConstraintViolationException handler
- Example 6: Class-level custom constraint

### Example 1: Request body validation

Title: @Body @Valid with constrained records
Description: Always pair constrained request types with `@Valid` (or `@Validated` for groups) on `@Body` parameters so Micronaut triggers Bean Validation before the controller method runs.

**Good example:**

```java
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Controller("/users")
class UserController {
    @Post
    HttpResponse<Void> create(@Body @Valid CreateUserRequest request) {
        return HttpResponse.noContent();
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
@Post
HttpResponse<Void> create(@Body CreateUserRequest request) {
    return HttpResponse.noContent();
}
// Constraints on CreateUserRequest are not enforced without @Valid
```

### Example 2: Query and path parameters

Title: @Min, @Max, @Pattern on @QueryValue and URI variables
Description: Validate simple parameters on the controller method. Prefer typed query values with constraints instead of parsing raw strings manually.

**Good example:**

```java
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.QueryValue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

@Controller("/items")
class ItemController {
    @Get("/{id}")
    String get(@PathVariable @Pattern(regexp = "^[a-z0-9-]+$") String id) {
        return id;
    }

    @Get
    String list(@QueryValue @Min(0) int page, @QueryValue @Min(1) @Max(100) int size) {
        return page + ":" + size;
    }
}
```

**Bad example:**

```java
@Get("/{id}")
String get(String id) {
    if (id.contains("..")) throw new IllegalArgumentException();
    return id;
}
```

### Example 3: @ConfigurationProperties validation

Title: Fail-fast binding with Bean Validation constraints
Description: Put Jakarta constraints directly on `@ConfigurationProperties` values so invalid `application.yml` fails at startup. Pairs with `@501-frameworks-micronaut-core`.

**Good example:**

```java
import io.micronaut.context.annotation.ConfigurationProperties;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@ConfigurationProperties("app.api")
record ApiSettings(
    @NotBlank String baseUrl,
    @Min(1) @Max(600) int timeoutSeconds
) { }
```

**Bad example:**

```java
@ConfigurationProperties("app.api")
record ApiSettings(String baseUrl, int timeoutSeconds) { }
// No startup validation
```

### Example 4: Nested and collection validation

Title: @Valid on nested types and list elements
Description: Cascade validation with `@Valid` on nested fields and `List<@NotNull @Valid LineItem>` patterns.

**Good example:**

```java
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;

public record OrderRequest(
    @NotNull @Valid Address shipping,
    @NotEmpty List<@NotNull @Valid LineItem> lines
) { }

record Address(@NotNull @Size(max = 200) String street) { }

record LineItem(
    @NotNull @Size(min = 1, max = 64) String sku,
    @Positive int qty
) { }
```

**Bad example:**

```java
public record OrderRequest(Address shipping, List<LineItem> lines) { }
// Missing @Valid: nested Address and LineItem constraints are never evaluated
```

### Example 5: ConstraintViolationException handler

Title: Map validation failures to HTTP 400
Description: Provide an `ExceptionHandler<ConstraintViolationException>` singleton that returns RFC 7807 Problem Details aligned with `@502-frameworks-micronaut-rest`: stable `type`, `title`, `status`, `detail`, `instance`, plus a `violations` extension for field-level messages. Use `HttpResponse.badRequest(body)` for concise construction and `.toList()` (Java 16+) instead of `Collectors.toList()`.

**Good example:**

```java
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;
import jakarta.validation.ConstraintViolationException;

import java.net.URI;
import java.util.List;

record ValidationProblemBody(URI type, String title, int status, String detail, URI instance, List<String> violations) { }

@Singleton
public class ConstraintViolationHandler
        implements ExceptionHandler<ConstraintViolationException, HttpResponse<ValidationProblemBody>> {

    private static final URI VALIDATION_TYPE = URI.create("https://example.com/problems/validation-error");

    @Override
    public HttpResponse<ValidationProblemBody> handle(HttpRequest request, ConstraintViolationException ex) {
        List<String> violations = ex.getConstraintViolations().stream()
            .map(v -> v.getPropertyPath() + ": " + v.getMessage())
            .toList();
        ValidationProblemBody body = new ValidationProblemBody(
            VALIDATION_TYPE,
            "Validation Error",
            400,
            "One or more request fields failed validation.",
            request.getUri(),
            violations);
        return HttpResponse.badRequest(body);
    }
}
```

**Bad example:**

```java
return HttpResponse.badRequest(
    java.util.Map.of("error", "VALIDATION_ERROR", "details", List.of(ex.getMessage())));
// Ad hoc Map shape — not RFC 7807; unstable message; may expose internal property paths
```

### Example 6: Class-level custom constraint

Title: Reuse cross-field rules
Description: Implement `@Constraint(validatedBy = ...)` for DTO-level rules shared across endpoints.

**Good example:**

```java
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SamePasswords.Validator.class)
@interface SamePasswords {
    String message() default "passwords must match";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    class Validator implements ConstraintValidator<SamePasswords, PasswordForm> {
        @Override
        public boolean isValid(PasswordForm v, ConstraintValidatorContext ctx) {
            if (v == null) return true;
            return java.util.Objects.equals(v.password(), v.confirmPassword());
        }
    }
}

@SamePasswords
record PasswordForm(String password, String confirmPassword) { }
```

**Bad example:**

```java
if (!p1.equals(p2)) throw new IllegalArgumentException("mismatch");
```


## Output Format

- **ANALYZE** controllers, factories, and configuration beans for missing `@Valid`, missing cascades, and inconsistent 400 bodies
- **APPLY** Bean Validation at HTTP boundaries and on `@ConfigurationProperties`; centralize `ConstraintViolationException` handling
- **ALIGN** error responses with RFC 7807 Problem Details (`ValidationProblemBody` record) from `@502-frameworks-micronaut-rest`
- **VALIDATE** with `./mvnw compile` before and `./mvnw clean verify` after changes


## Safeguards

- Do not omit `@Valid` on constrained `@Body` parameters
- Do not return stack traces or raw validator messages to clients
- Verify validation behavior under compile-time HTTP clients and GraalVM native if applicable