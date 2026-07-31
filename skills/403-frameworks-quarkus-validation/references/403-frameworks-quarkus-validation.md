---
name: 403-frameworks-quarkus-validation
description: Use when you need to design, review, or improve validation in Quarkus applications — including Bean Validation on JAX-RS resources, @Valid on parameters and CDI beans, constraint groups, @ConfigMapping validation, custom constraints, nested DTO validation, and RFC 7807 validation errors via quarkus-http-problem.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Quarkus Validation Guidelines

## Role

You are a Senior software engineer with extensive experience in Quarkus, Jakarta REST, and Bean Validation

## Goal

Ensure Quarkus services validate inputs at boundaries, keep validation rules declarative and testable, and return consistent client-safe validation error responses. Prefer `quarkus-http-problem` (Quarkiverse) for RFC 7807 / RFC 9457 Problem Details on validation failures instead of hand-rolled response records. Align with `@402-frameworks-quarkus-rest` for HTTP semantics.

## Constraints

Before applying recommendations, ensure the project compiles. Compilation failure is a blocking condition.

- **MANDATORY**: Run `./mvnw compile` or `mvn compile` before applying any change
- **SAFETY**: If compilation fails, stop immediately
- **VERIFY**: Run `./mvnw clean verify` or `mvn clean verify` after applying improvements

## Examples

### Table of contents

- Example 1: JAX-RS request body with @Valid
- Example 2: Path and query parameter constraints
- Example 3: @ConfigMapping with validation
- Example 4: Nested and list validation
- Example 5: RFC 7807 validation errors with quarkus-http-problem
- Example 6: Class-level custom constraint

### Example 1: JAX-RS request body with @Valid

Title: Bean Validation on resource method parameters
Description: Apply `@Valid` to the request body parameter so Hibernate Validator runs constraints before your method executes.

**Good example:**

```java
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {
    @POST
    public void create(@Valid CreateUserRequest request) { }
}

record CreateUserRequest(
    @NotBlank String username,
    @NotBlank @Email String email,
    @NotBlank @Size(min = 8, max = 128) String password
) { }
```

**Bad example:**

```java
@POST
public void create(CreateUserRequest request) {
    if (request.email() == null) throw new IllegalArgumentException("bad");
}
```

### Example 2: Path and query parameter constraints

Title: @PathParam / @QueryParam with @Min, @Pattern
Description: Validate primitive and String parameters directly on resource methods. Quarkus validates these when the `quarkus-hibernate-validator` extension is present and validation is enabled for the resource.

**Good example:**

```java
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.ws.rs.*;

@Path("/items")
public class ItemResource {
    @GET
    @Path("{id}")
    public String get(@PathParam("id") @Pattern(regexp = "^[a-z0-9-]+$") String id) {
        return id;
    }

    @GET
    public String list(@QueryParam("page") @Min(0) int page,
                         @QueryParam("size") @Min(1) @Max(100) int size) {
        return page + ":" + size;
    }
}
```

**Bad example:**

```java
@GET
@Path("{id}")
public String get(@PathParam("id") String id) {
    return id;
}
// No format or bounds validation
```

### Example 3: @ConfigMapping with validation

Title: Fail-fast configuration with Bean Validation constraints
Description: Use SmallRye Config `@ConfigMapping` with Bean Validation constraints on mapping methods so invalid `application.properties` fails at startup. Pairs with `@401-frameworks-quarkus-core`.

**Good example:**

```java
import io.smallrye.config.ConfigMapping;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@ConfigMapping(prefix = "app.api")
interface ApiConfig {
    @NotBlank
    String baseUrl();

    @Min(1)
    @Max(600)
    int timeoutSeconds();
}
```

**Bad example:**

```java
@ConfigMapping(prefix = "app.api")
interface ApiConfig {
    String baseUrl();
    int timeoutSeconds();
}
// Negative timeout accepted until first HTTP call
```

### Example 4: Nested and list validation

Title: @Valid on nested types and container elements
Description: Cascade validation to nested records and to each list element using `@Valid` and `List<@NotNull @Valid Item>`.

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
// Missing @Valid: nested constraints ignored
```

### Example 5: RFC 7807 validation errors with quarkus-http-problem

Title: Built-in ConstraintViolationException mapping to application/problem+json
Description: Quarkus has no built-in RFC 7807 type like Spring's `ProblemDetail`. Add the Quarkiverse extension `io.quarkiverse.httpproblem:quarkus-http-problem` (successor to legacy `quarkus-resteasy-problem`) so `ConstraintViolationException` from `@Valid` request bodies is mapped automatically to `HttpValidationProblem` with `application/problem+json`. Customize status/title via `quarkus.http-problem.constraint-violation.*` when needed; add a custom `@Provider` `ExceptionMapper` only when the built-in shape is insufficient.

**Good example:**

```text
<!-- pom.xml -->
<dependency>
    <groupId>io.quarkiverse.httpproblem</groupId>
    <artifactId>quarkus-http-problem</artifactId>
    <version>3.33.1</version>
</dependency>

// SumController.java
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;

@Path("/api/v1")
@Consumes(MediaType.APPLICATION_JSON)
public class SumController {
    @POST
    @Path("/sum")
    public SumResponse sum(@Valid SumRequest request) {
        return new SumResponse(request.param1() + request.param2());
    }
}

record SumRequest(
    @NotNull(message = "must not be null") Integer param1,
    @NotNull(message = "must not be null") Integer param2) { }

# application.properties (optional)
quarkus.http-problem.constraint-violation.status=400
quarkus.http-problem.constraint-violation.title=Bad Request
```

**Bad example:**

```java
record ValidationProblemDetail(String type, String title, int status, String detail, List<String> violations) { }

@Provider
public class ConstraintViolationMapper implements ExceptionMapper<ConstraintViolationException> {
    // Reimplements what quarkus-http-problem already provides
}
```

### Example 6: Class-level custom constraint

Title: Cross-field rules with @Constraint and ConstraintValidator
Description: Reuse cross-field rules (date ranges, matching passwords) as a single annotation on a type.

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
if (!a.equals(b)) throw new IllegalArgumentException("mismatch");
// Duplicated across resources
```


## Output Format

- **ANALYZE** JAX-RS resources and config mappings for missing `@Valid`, missing cascades, and inconsistent 400 bodies
- **APPLY** Bean Validation at REST boundaries; add `quarkus-http-problem` for RFC 7807 validation responses
- **ALIGN** validation errors with `HttpValidationProblem` / `application/problem+json` from `@402-frameworks-quarkus-rest`
- **VALIDATE** with `./mvnw compile` before and `./mvnw clean verify` after changes


## Safeguards

- Do not skip `@Valid` on constrained request bodies
- Do not hand-roll RFC 7807 response records when `quarkus-http-problem` is on the classpath
- Do not return stack traces or raw Hibernate Validator messages that expose internals
- Test reactive and classic REST stacks if both exist in the repo