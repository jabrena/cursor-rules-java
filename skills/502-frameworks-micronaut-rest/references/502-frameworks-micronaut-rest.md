---
name: 502-frameworks-micronaut-rest
description: Use when you need to design, review, or improve REST APIs with Micronaut — including HTTP methods, resource URIs, status codes, DTOs, versioning, deprecation and sunset headers, content negotiation (JSON and vendor media types), ISO-8601 instants in DTOs, pagination/sorting/filtering, Bean Validation at the boundary, idempotency, ETag concurrency, HTTP caching, error handling with `ExceptionHandler`, security annotations, contract-first OpenAPI (OpenAPI Generator `micronaut` server), optional runtime OpenAPI via `micronaut-openapi`, and RFC 7807-style problem details for errors.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Micronaut REST API Guidelines

## Role

You are a Senior software engineer with extensive experience in REST API design and Micronaut

## Goal

Micronaut REST endpoints should honor HTTP semantics predictably: resources as nouns, methods for actions, status codes for outcomes, and stable contracts via DTOs and versioning. Production APIs centralize errors (structured JSON or RFC 7807 Problem Details via `ExceptionHandler` or the `micronaut-problem` module), expose a clear contract for consumers (for API-first, the OpenAPI file is that contract), and apply authentication, authorization, and input validation by default. For **API-first** work, generate Micronaut-compatible API interfaces and DTOs from the OpenAPI document using **OpenAPI Generator** (`openapi-generator-maven-plugin`, `micronaut` generator), then implement the generated controller interface in a `@Controller` bean that delegates to domain services. Align with the same REST design principles as Spring (`@302-frameworks-spring-boot-rest`) while using Micronaut `@Controller` routes, `HttpResponse`, and HTTP server filters where appropriate.

## Constraints

Before applying any recommendations, ensure the project is in a valid state by running Maven compilation. Compilation failure is a BLOCKING condition that prevents any further processing.

- **MANDATORY**: Run `./mvnw compile` or `mvn compile` before applying any change
- **PREREQUISITE**: Project must compile successfully and pass basic validation checks before any REST API refactoring
- **CRITICAL SAFETY**: If compilation fails, IMMEDIATELY STOP and DO NOT CONTINUE with any recommendations
- **BLOCKING CONDITION**: Compilation errors must be resolved by the user before proceeding with REST API improvements
- **NO EXCEPTIONS**: Under no circumstances should REST API recommendations be applied to a project that fails to compile
- **VERIFY**: Run `./mvnw clean verify` or `mvn clean verify` after applying improvements

## Examples

### Table of contents

- Example 1: Use HTTP methods semantically
- Example 2: Resource URIs and naming
- Example 3: HTTP status codes
- Example 4: DTOs for requests and responses
- Example 5: API versioning
- Example 6: Graceful, structured error responses
- Example 7: RFC 7807 Problem Details quality
- Example 8: Pagination, sorting, and filtering
- Example 9: Validation at the boundary
- Example 10: Idempotency and safe retries
- Example 11: Concurrency control
- Example 12: Caching semantics
- Example 13: Deprecation and sunset
- Example 14: Content negotiation
- Example 15: Time and locale in contracts
- Example 16: API-first with OpenAPI Generator (Micronaut)

### Example 1: Use HTTP methods semantically

Title: GET for reads, POST for creates, PUT/PATCH for updates, DELETE for removal
Description: Map operations to the correct verb: `GET` must not change server state; `POST` typically creates subordinate resources; `PUT` replaces; `PATCH` partially updates; `DELETE` removes. Use Micronaut `@Get`, `@Post`, `@Put`, `@Patch`, `@Delete` on `@Controller` types. Return `HttpResponse` when you need status codes and headers such as `Location`. Avoid RPC-style GET/POST misuse.

**Good example:**

```java
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import java.net.URI;

@Controller("/users")
public class UserController {

    private final UserService users;

    public UserController(UserService users) {
        this.users = users;
    }

    @Get("/{id}")
    public HttpResponse<UserDto> getUser(String id) {
        return HttpResponse.ok(new UserDto(id));
    }

    @Post
    public HttpResponse<UserDto> createUser(@Body UserCreateDto body) {
        UserDto created = users.create(body);
        return HttpResponse.created(URI.create("/users/" + created.id())).body(created);
    }

    @Put("/{id}")
    public HttpResponse<UserDto> updateUser(String id, @Body UserUpdateDto body) {
        return HttpResponse.ok(new UserDto(id));
    }

    @Patch("/{id}")
    public HttpResponse<UserDto> patchUser(String id, @Body UserPatchDto patch) {
        return HttpResponse.ok(new UserDto(id));
    }

    @Delete("/{id}")
    public HttpResponse<Void> deleteUser(String id) {
        users.delete(id);
        return HttpResponse.noContent();
    }
}

record UserDto(String id) { }
record UserCreateDto(String username, String email) { }
record UserUpdateDto(String username, String email) { }
record UserPatchDto(String username, String email) { }
```

**Bad example:**

```java
import io.micronaut.http.annotation.*;

@Controller("/api")
public class BadUserController {

    @Get("/deleteUser")
    public String deleteUserViaGet(String id) {
        return "User deleted via GET";
    }

    @Post("/getUser")
    public UserDto getUserViaPost(@Body String idPayload) {
        return new UserDto("x");
    }
}
record UserDto(String id) { }
```

### Example 2: Resource URIs and naming

Title: Prefer noun-based paths and hierarchical resources
Description: Model resources as nouns with stable paths (`/users`, `/users/{userId}/orders`). Avoid verbs in paths (`/getAllUsers`) and inconsistent mixing of query-only versus path-based identification without reason.

**Good example:**

```text
GET    /users
GET    /users/{userId}
GET    /users/{userId}/orders
GET    /users/{userId}/orders/{orderId}
POST   /users
```

**Bad example:**

```text
GET  /getAllUsers
GET  /fetchUserById?id={userId}
POST /createNewUser
GET  /userOrders?userId={userId}
POST /processUserOrderCreation
```

### Example 3: HTTP status codes

Title: Reflect outcomes with standard codes, not 200 for every case
Description: Use `200` for successful bodies, `201` + `Location` for creation, `204` for success with no body, `400` for validation/syntax, `401`/`403` for authz failures, `404` for missing resources, and `500` only for unexpected server faults—without stuffing errors into a 200 OK body. Map domain failures to `HttpResponse` status in the controller or via `ExceptionHandler`.

**Good example:**

```java
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import java.net.URI;

@Controller("/api/v1/orders")
public class OrderController {

    @Post
    public HttpResponse<OrderDto> create(@Body OrderCreateDto body) {
        OrderDto created = new OrderDto("order-123");
        return HttpResponse.created(URI.create("/api/v1/orders/" + created.id())).body(created);
    }

    @Get("/{id}")
    public HttpResponse<OrderDto> get(String id) {
        return HttpResponse.ok(new OrderDto(id));
    }

    @Delete("/{id}")
    public HttpResponse<Void> cancel(String id) {
        return HttpResponse.noContent();
    }
}

record OrderDto(String id) { }
record OrderCreateDto(String productId, int quantity) { }
```

**Bad example:**

```java
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;

@Controller("/api/orders")
public class BadStatusController {

    @Post
    public HttpResponse<String> createOrder(@Body OrderCreateDto body) {
        return HttpResponse.ok("Order created: order-123");
    }

    @Get("/{id}")
    public HttpResponse<String> getOrder(String id) {
        if ("unknown".equals(id)) {
            return HttpResponse.ok("{\"error\":\"Order not found\"}");
        }
        return HttpResponse.ok("{\"id\":\"" + id + "\"}");
    }
}
record OrderCreateDto(String productId, int quantity) { }
```

### Example 4: DTOs for requests and responses

Title: Decouple API contracts from persistence entities
Description: Use dedicated request/response types so serialization matches what clients should see. Do not return persistence entities with internal fields (password hashes, audit columns) directly from controllers.

**Good example:**

```java
// Domain entity — internal state never exposed directly
class User {
    Long id;
    String username;
    String passwordHash;
    String email;
}

record UserResponseDto(Long id, String username, String email) { }
record UserCreateRequestDto(String username, String password, String email) { }

// Controller returns UserResponseDto / accepts UserCreateRequestDto — not User entity
```

**Bad example:**

```java
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;

@Controller("/rawusers")
public class AnotherUserController {

    @Get("/{id}")
    public HttpResponse<User> getRawUser(String id) {
        return HttpResponse.ok(findUserById(id));
    }

    private User findUserById(String id) {
        return new User();
    }
}

class User {
    private String passwordHash;
}
```

### Example 5: API versioning

Title: Version explicitly — URI path, custom request header, or vendor media type
Description: Introduce versioning early so breaking changes do not silently break existing clients. Pick one strategy and apply it **uniformly** across controllers; mixing strategies creates confusion. Micronaut matches routes by path, optional headers, and `produces`/`consumes` for media-type versioning. Common options: **URI path** (`/api/v1/…`) — easiest to browse, cache, and test. **Custom header** (`X-API-Version: 2`) — clean URIs; weaker CDN cache keys unless `Vary` is set. **Vendor media type** (`Accept: application/vnd.example.product+json;version=2`) — REST-aligned; higher client complexity.

**Good example:**

```java
// Strategy 1: URI path — separate controllers per major version
import io.micronaut.http.annotation.*;

@Controller("/api/v1/products")
public class ProductControllerV1 {

    @Get("/{id}")
    public ProductDtoV1 get(String id) {
        return new ProductDtoV1("widget", 9.99);
    }
}

@Controller("/api/v2/products")
public class ProductControllerV2 {

    @Get("/{id}")
    public ProductDtoV2 get(String id) {
        return new ProductDtoV2("widget", new Money(999, "USD"));
    }
}

record ProductDtoV1(String name, double price) { }
record ProductDtoV2(String name, Money price) { }
record Money(int amountCents, String currency) { }
```

**Bad example:**

```java
import io.micronaut.http.annotation.*;

@Controller("/products")
public class UnversionedProductController {

    @Get("/{id}")
    public ProductDto getProduct(String id) {
        return new ProductDto();
    }
}
class ProductDto { }
```

### Example 6: Graceful, structured error responses

Title: Machine-readable errors; no stack traces in production
Description: Return JSON with stable fields: code/type, message, and optional field-level details for validation. Implement `ExceptionHandler` beans to map domain and infrastructure exceptions to HTTP status and body. Log full exceptions server-side; do not return stack traces to API clients in production.

**Good example:**

```java
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

record ErrorResponse(String errorCode, String message, List<String> details) {
    ErrorResponse(String errorCode, String message) {
        this(errorCode, message, List.of());
    }
}

@Singleton
public class ResourceNotFoundHandler implements ExceptionHandler<ResourceNotFoundException, HttpResponse<ErrorResponse>> {

    @Override
    public HttpResponse<ErrorResponse> handle(HttpRequest request, ResourceNotFoundException ex) {
        return HttpResponse.notFound(new ErrorResponse("RESOURCE_NOT_FOUND", ex.getMessage()));
    }
}

@Singleton
public class CatchAllHandler implements ExceptionHandler<Exception, HttpResponse<ErrorResponse>> {

    private static final Logger log = LoggerFactory.getLogger(CatchAllHandler.class);

    @Override
    public HttpResponse<ErrorResponse> handle(HttpRequest request, Exception ex) {
        log.error("Unhandled exception", ex);
        return HttpResponse.serverError(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred."));
    }
}

class ResourceNotFoundException extends RuntimeException {
    ResourceNotFoundException(String m) { super(m); }
}
```

**Bad example:**

```java
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;

@Controller("/items")
public class LeakyErrorController {

    @Get("/{id}")
    public HttpResponse<String> get(String id) {
        try {
            throw new RuntimeException("db down");
        } catch (RuntimeException e) {
            return HttpResponse.serverError(e.toString());
        }
    }
}
```

### Example 7: RFC 7807 Problem Details quality

Title: Consistent problem bodies; never leak stack traces to clients
Description: When returning Problem Details, populate `type`, `title`, `status`, `detail`, and `instance` predictably (use a dedicated record or DTO). Log exceptions with correlation IDs server-side; do not attach stack traces or ad hoc `Map` shapes that change per endpoint. The `micronaut-problem` module can align responses with RFC 7807 when enabled.

**Good example:**

```java
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URI;
import java.util.List;
import java.util.UUID;

record ProblemBody(URI type, String title, int status, String detail, URI instance) { }

@Singleton
public class ProblemExceptionHandler implements ExceptionHandler<RuntimeException, HttpResponse<ProblemBody>> {

    private static final Logger log = LoggerFactory.getLogger(ProblemExceptionHandler.class);

    @Override
    public HttpResponse<ProblemBody> handle(HttpRequest request, RuntimeException ex) {
        String errorId = UUID.randomUUID().toString();
        URI instance = URI.create(request.getUri().toString());
        ProblemBody body = new ProblemBody(
            URI.create("https://example.com/problems/internal-error"),
            "Internal Server Error",
            500,
            "An unexpected error occurred.",
            instance);
        log.error("Internal error {}", errorId, ex);
        return HttpResponse.serverError(body);
    }
}

@Singleton
public class ValidationProblemHandler implements ExceptionHandler<ValidationException, HttpResponse<ProblemBody>> {

    @Override
    public HttpResponse<ProblemBody> handle(HttpRequest request, ValidationException ex) {
        ProblemBody body = new ProblemBody(
            URI.create("https://example.com/problems/validation-error"),
            "Validation Error",
            400,
            ex.getMessage(),
            URI.create(request.getUri().toString()));
        return HttpResponse.badRequest(body);
    }
}

class ValidationException extends RuntimeException {
    private final List<String> violations;
    ValidationException(String message, List<String> violations) {
        super(message);
        this.violations = violations;
    }
    List<String> getViolations() { return violations; }
}
```

**Bad example:**

```java
import io.micronaut.http.HttpResponse;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;
import java.util.*;

@Singleton
public class BadProblemHandler implements ExceptionHandler<RuntimeException, HttpResponse<Map<String, Object>>> {

    @Override
    public HttpResponse<Map<String, Object>> handle(io.micronaut.http.HttpRequest request, RuntimeException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("msg", "Something went wrong");
        error.put("stack_trace", Arrays.toString(ex.getStackTrace()));
        return HttpResponse.serverError(error);
    }
}
```

### Example 8: Pagination, sorting, and filtering

Title: Bounded pages, stable sorts, optional Link headers (RFC 8288)
Description: Exposing all rows as a single JSON array does not scale. Prefer `io.micronaut.data.model.Page` / `Pageable` with **server-enforced maximum** page size, or explicit cursor parameters. Whitelist sort properties to avoid sort-by-arbitrary-field injection. Optionally emit `Link` headers (`rel="next"` / `rel="prev"`) for discoverable navigation.

**Good example:**

```java
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import jakarta.validation.Valid;
import java.util.Optional;
import java.util.Set;

@Controller("/api/v1/products")
public class ProductQueryController {

    private static final int MAX_PAGE_SIZE = 100;
    private static final Set<String> ALLOWED_SORT = Set.of("createdAt", "name", "price");

    private final ProductService products;

    public ProductQueryController(ProductService products) {
        this.products = products;
    }

    @Get
    public HttpResponse<Page<ProductDto>> list(
            @QueryValue Optional<String> category,
            @Valid Pageable pageable) {
        if (pageable.getSize() > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException("page size exceeds maximum of " + MAX_PAGE_SIZE);
        }
        for (var order : pageable.getSort()) {
            if (!ALLOWED_SORT.contains(order.getProperty())) {
                throw new IllegalArgumentException("sort by '" + order.getProperty() + "' is not allowed");
            }
        }
        Page<ProductDto> page = products.search(category, pageable);
        return HttpResponse.ok(page)
            .header(HttpHeaders.LINK, "</api/v1/products?page=1&size=20>; rel=\"next\"");
    }
}
class ProductService {
    Page<ProductDto> search(Optional<String> category, Pageable p) { return Page.empty(); }
}
record ProductDto() { }
```

**Bad example:**

```java
import io.micronaut.http.annotation.*;
import java.util.List;

@Controller("/products")
public class UnboundedProductController {

    @Get
    public List<ProductDto> listAll(@QueryValue Optional<String> sort) {
        return List.of();
    }
}
class ProductDto { }
```

### Example 9: Validation at the boundary

Title: `@Valid` on `@Body`; Bean Validation on DTOs
Description: Validate request bodies at the controller boundary with Jakarta Bean Validation annotations on DTOs and `@Valid` on parameters. Pair with an `ExceptionHandler` for `ConstraintViolationException` (or framework validation wrapper) so clients receive **400** with field-level violations instead of inconsistent ad hoc strings.

**Good example:**

```java
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.net.URI;

@Controller("/api/v1/users")
public class UserRegistrationController {

    private final UserService users;

    public UserRegistrationController(UserService users) {
        this.users = users;
    }

    @Post
    public HttpResponse<UserDto> register(@Body @Valid UserCreateRequest body) {
        UserDto created = users.register(body);
        return HttpResponse.created(URI.create("/api/v1/users/" + created.id())).body(created);
    }
}

record UserCreateRequest(
    @NotBlank String username,
    @NotBlank @Email String email,
    @Size(min = 8, max = 128) String password) { }

record UserDto(String id) { }
```

**Bad example:**

```java
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;

@Controller("/users")
public class WeakValidationController {

    @Post
    public HttpResponse<UserDto> register(@Body UserCreateRequest body) {
        if (body.email() == null || !body.email().contains("@")) {
            return HttpResponse.badRequest();
        }
        return HttpResponse.ok(new UserDto("x"));
    }
}
record UserCreateRequest(String email) {
    String email() { return email; }
}
record UserDto(String id) {}
```

### Example 10: Idempotency and safe retries

Title: `Idempotency-Key`, duplicate detection, **409 Conflict**, OpenAPI clarity
Description: Network clients retry `POST`; without idempotency keys or server-side deduplication, creates can duplicate. Accept an `Idempotency-Key` header and return the same response when the key replays a completed operation. Use **409 Conflict** when the request conflicts with current resource state. Document idempotent behavior in OpenAPI.

**Good example:**

```java
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller("/api/v1/payments")
public class PaymentController {

    private final Map<String, PaymentDto> completed = new ConcurrentHashMap<>();

    @Post
    @Operation(
        summary = "Create payment",
        description = "Idempotent: same Idempotency-Key returns the original outcome.")
    public HttpResponse<PaymentDto> create(
            @Parameter(description = "Client deduplication key", required = true)
            @Header("Idempotency-Key") String idempotencyKey,
            @Body @Valid PaymentCreateRequest body) {
        PaymentDto prior = completed.get(idempotencyKey);
        if (prior != null) {
            return HttpResponse.ok(prior);
        }
        PaymentDto created = new PaymentDto("pay-" + idempotencyKey.hashCode());
        completed.put(idempotencyKey, created);
        return HttpResponse.created(URI.create("/api/v1/payments/" + created.id())).body(created);
    }
}

record PaymentCreateRequest(String accountId, int amountCents) { }
record PaymentDto(String id) { }
```

**Bad example:**

```java
import io.micronaut.http.annotation.*;

@Controller("/payments")
public class NonIdempotentPaymentController {

    @Post
    public PaymentDto charge(@Body PaymentCreateRequest body) {
        return new PaymentDto("x");
    }
}
record PaymentCreateRequest() {}
record PaymentDto(String id) {}
```

### Example 11: Concurrency control

Title: ETag, If-Match / If-None-Match, **412** / **304**
Description: Prevent lost updates with optimistic concurrency: expose `ETag` on reads; require `If-Match` on mutating writes and return **412 Precondition Failed** when the version does not match. For reads, `If-None-Match` can yield **304 Not Modified** when you short-circuit in the controller or filter.

**Good example:**

```java
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.*;
import java.util.Optional;

@Controller("/api/v1/items")
public class ItemConcurrencyController {

    private final ItemService items;

    public ItemConcurrencyController(ItemService items) {
        this.items = items;
    }

    @Get("/{id}")
    public HttpResponse<ItemDto> get(String id, @Header(HttpHeaders.IF_NONE_MATCH) Optional<String> ifNoneMatch) {
        ItemDto dto = items.load(id);
        String etag = "\"v3-" + id + "\"";
        if (ifNoneMatch.isPresent() && ifNoneMatch.get().equals(etag)) {
            return HttpResponse.notModified();
        }
        return HttpResponse.ok(dto).header(HttpHeaders.ETAG, etag);
    }

    @Put("/{id}")
    public HttpResponse<Void> put(
            String id,
            @Header(HttpHeaders.IF_MATCH) String ifMatch,
            @Body ItemDto body) {
        if (!ifMatch.equals("\"v3-" + id + "\"")) {
            return HttpResponse.status(HttpStatus.PRECONDITION_FAILED);
        }
        items.save(id, body);
        return HttpResponse.noContent().header(HttpHeaders.ETAG, "\"v4-" + id + "\"");
    }
}
record ItemDto() { }
class ItemService {
    ItemDto load(String id) { return new ItemDto(); }
    void save(String id, ItemDto b) { }
}
```

**Bad example:**

```java
import io.micronaut.http.annotation.*;

@Controller("/items")
public class BlindOverwriteController {

    @Put("/{id}")
    public ItemDto put(String id, @Body ItemDto body) {
        return body;
    }
}
class ItemDto {}
```

### Example 12: Caching semantics

Title: `Cache-Control`, ETag/Last-Modified, private vs public data
Description: Set `Cache-Control` deliberately: public `max-age` for anonymous catalog data; `no-store` / `private` for tokens, sessions, or personalized `/me` payloads. Mis-caching authenticated responses can leak data across users.

**Good example:**

```java
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.*;

@Controller("/api/v1/public/products")
public class PublicCatalogController {

    @Get("/{id}")
    public MutableHttpResponse<ProductDto> get(String id) {
        return HttpResponse.ok(new ProductDto())
            .header("Cache-Control", "public, max-age=300");
    }
}

@Controller
public class MeController {

    @Get("/api/me")
    public MutableHttpResponse<UserDto> me() {
        return HttpResponse.ok(new UserDto())
            .header("Cache-Control", "private, no-store");
    }
}
record ProductDto() {}
record UserDto() {}
```

**Bad example:**

```java
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;

@Controller
public class RiskyCachingController {

    @Get("/api/me")
    public HttpResponse<UserDto> profile() {
        return HttpResponse.ok(new UserDto())
            .header("Cache-Control", "public, max-age=3600");
    }
}
class UserDto {}
```

### Example 13: Deprecation and sunset

Title: `Deprecation`, `Sunset`, `Link` (`rel="successor-version"`)
Description: When an endpoint or version is headed for removal, advertise it with RFC-style headers: `Deprecation` (`true` or a timestamp), `Sunset` with an HTTP-date for expected shutdown, and `Link` to the replacement (`rel="successor-version"`). Document the timeline in OpenAPI. Prefer emitting these headers from an HTTP server filter or advice so deprecated routes stay consistent without repeating headers on every method.

**Good example:**

```java
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.*;

@Controller("/api/v1/legacy-products")
public class LegacyProductController {

    @Get("/{id}")
    public MutableHttpResponse<ProductDto> get(String id) {
        return HttpResponse.ok(new ProductDto())
            .header("Deprecation", "true")
            .header("Sunset", "Sat, 31 Dec 2026 23:59:59 GMT")
            .header(HttpHeaders.LINK, "</api/v2/products>; rel=\"successor-version\"");
    }
}
record ProductDto() {}
```

**Bad example:**

```java
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;

@Controller("/api/v1/products")
public class SilentBreakController {

    @Get("/{id}")
    public HttpResponse<ProductDto> get(String id) {
        return HttpResponse.ok(new ProductDto());
    }
}
class ProductDto {}
```

### Example 14: Content negotiation

Title: `@Produces` / `@Consumes`, JSON default, vendor media types
Description: Declare what controllers emit and accept: default to `application/json` unless you truly need more. Vendor media types can align with versioning—keep `produces`/`consumes` consistent. Avoid ambiguous multi-representation routes without tests or a clear client contract.

**Good example:**

```java
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;

@Controller("/api/widgets")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WidgetJsonController {

    @Get("/{id}")
    public WidgetDto get(String id) {
        return new WidgetDto();
    }

    @Post
    public WidgetDto create(@Body WidgetDto body) {
        return body;
    }
}

@Controller("/api/products")
public class ProductVersionedMediaController {

    static final String V1 = "application/vnd.example.product+json;version=1";

    @Get(value = "/{id}", produces = V1)
    public ProductDto getV1(String id) {
        return new ProductDto();
    }
}
record WidgetDto() {}
record ProductDto() {}
```

**Bad example:**

```java
import io.micronaut.http.annotation.*;

@Controller
public class LooseContentController {

    @Get(value = "/data", produces = {"*/*"})
    public String get() {
        return "maybe json maybe not";
    }
}
```

### Example 15: Time and locale in contracts

Title: ISO-8601 with offset; optional `Accept-Language` for errors
Description: Model timestamps as `Instant` or `OffsetDateTime` (ISO-8601 with offset) so clients interpret wall-clock unambiguously—avoid legacy `Date` and ambiguous zone-less `LocalDateTime` in public JSON unless the domain is strictly calendar-local. For localized problem/error text (RFC 7807-style bodies), honor `Accept-Language` only if you keep stable problem `type` URIs/codes identical across locales and avoid leaking sensitive details through translated messages.

**Good example:**

```java
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;
import java.net.URI;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Locale;
import java.util.ResourceBundle;

@Controller("/api/v1/events")
public class EventController {

    @Get("/{id}")
    public EventDto get(String id) {
        return new EventDto(
            Instant.parse("2026-03-23T12:00:00Z"),
            OffsetDateTime.of(2026, 3, 23, 13, 0, 0, 0, ZoneOffset.ofHours(1)));
    }
}

record EventDto(Instant occurredAt, OffsetDateTime windowStart) { }

record ProblemBody(URI type, String title, int status, String detail) { }

@Singleton
public class IllegalArgumentExceptionHandler implements ExceptionHandler<IllegalArgumentException, HttpResponse<ProblemBody>> {

    @Override
    public HttpResponse<ProblemBody> handle(HttpRequest request, IllegalArgumentException ex) {
        Locale locale = request.getLocale().orElse(Locale.ENGLISH);
        ResourceBundle bundle = ResourceBundle.getBundle("ProblemMessages", locale);
        String detail = bundle.getString("problem.invalid_argument.detail");
        String title = bundle.getString("problem.invalid_argument.title");
        ProblemBody body = new ProblemBody(
            URI.create("https://example.com/problems/invalid-argument"),
            title,
            400,
            detail);
        return HttpResponse.badRequest(body);
    }
}
```

**Bad example:**

```java
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import java.util.Date;

@Controller("/events")
public class AmbiguousTimeController {

    @Get("/{id}")
    public HttpResponse<EventDto> get(String id) {
        return HttpResponse.ok(new EventDto(new Date()));
    }
}

class EventDto {
    private Date createdAt;
    EventDto(Date createdAt) { this.createdAt = createdAt; }
}
```

### Example 16: API-first with OpenAPI Generator (Micronaut)

Title: Generate controller API interfaces from `openapi.yaml`; implement in `@Controller`
Description: Version the OpenAPI spec in `src/main/resources` and run **OpenAPI Generator** with `generatorName` `micronaut` so generated interfaces and models match the contract. Configure `apiPackage` / `modelPackage`, Micronaut/Jakarta options, and bind the `generate` goal to `generate-sources`. Your `@Controller` class **implements** the generated API interface (often named after tags or resource groups) and returns `HttpResponse` from service calls. Optionally keep runtime OpenAPI via `micronaut-openapi` for discovery, but the checked-in spec remains the source of truth for API-first. Validate the spec in CI before codegen.

**Good example:**

```text
<plugin>
    <groupId>org.openapitools</groupId>
    <artifactId>openapi-generator-maven-plugin</artifactId>
    <version>${openapi-generator.version}</version>
    <executions>
        <execution>
            <goals><goal>generate</goal></goals>
            <configuration>
                <inputSpec>${project.basedir}/src/main/resources/openapi/api.yaml</inputSpec>
                <generatorName>micronaut</generatorName>
                <apiPackage>com.example.generated.api</apiPackage>
                <modelPackage>com.example.generated.model</modelPackage>
                <configOptions>
                    <generateControllerAsAbstract>false</generateControllerAsAbstract>
                    <useJakartaEe>true</useJakartaEe>
                </configOptions>
            </configuration>
        </execution>
    </executions>
</plugin>

// --- Implement generated UsersApi in a Micronaut controller ---

import com.example.generated.api.UsersApi;
import com.example.generated.model.UserDto;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;

@Controller
public class UserController implements UsersApi {

    private final UserService users;

    public UserController(UserService users) {
        this.users = users;
    }

    @Override
    public HttpResponse<UserDto> getUserById(String userId) {
        return HttpResponse.ok(users.require(userId));
    }
}

interface UserService {
    UserDto require(String id);
}
```

**Bad example:**

```java
// Anti-pattern: Micronaut routes and return types edited ad hoc while openapi.yaml
// is not regenerated — the published contract and the server diverge.
@Controller("/manual")
public class ManualOnlyController {
    @io.micronaut.http.annotation.Get("/users/{id}")
    public Object get(String id) {
        return id;
    }
}
```


## Output Format

- **ANALYZE** controllers and supporting types for REST semantics: HTTP verbs, URI shape, status codes, DTO boundaries, versioning, deprecation/sunset/`Link` headers, `produces`/`consumes` and vendor media types, ISO-8601 time fields, pagination/sort/filter bounds, Bean Validation on request DTOs, idempotency headers and conflict behavior, ETag/preconditions, cache headers for public vs private data, error handling with `ExceptionHandler`, security annotations/config, and (when API-first) alignment between `openapi.yaml` and generated Micronaut API interfaces vs controller implementations
- **CATEGORIZE** findings by impact (CRITICAL for security/semantic violations, MAINTAINABILITY for DTO/versioning/docs, CONSISTENCY for errors) and by area (routing, responses, errors, security, docs)
- **APPLY** changes that align with these principles: fix unsafe GETs, normalize paths, return appropriate status codes, introduce or narrow DTOs, add versioning, emit deprecation/sunset/`Link` when phasing out endpoints, tighten `produces`/`consumes` (JSON default, vendor types only when versioning warrants it), use `OffsetDateTime`/`Instant` in API DTOs, bound list endpoints (`Page`/`Pageable` or capped cursors), add `@Valid` on request bodies with Bean Validation, add idempotency and conflict (**409**) patterns where writes retry, add ETag/`If-Match` or version checks for updates, set `Cache-Control` appropriately, centralize exception handling with Problem Details shape where suitable, tighten Micronaut Security configuration, and (API-first) implement generated Micronaut API interfaces so controllers follow the checked-in `openapi.yaml`
- **IMPLEMENT** incrementally: preserve public API contracts when possible; use deprecation and versioning for breaking changes; keep error shapes backward compatible unless versioning allows a break
- **EXPLAIN** trade-offs (e.g., URI vs header versioning, custom `ErrorResponse` vs RFC 7807 problem bodies, blocking I/O on the Netty event loop vs `@ExecuteOn`) when multiple valid options exist
- **VALIDATE** with `./mvnw compile` before and `./mvnw clean verify` after substantive edits; exercise critical endpoints in integration tests where available


## Safeguards

- **BLOCKING SAFETY CHECK**: ALWAYS run `./mvnw compile` or `mvn compile` before ANY REST API refactoring — compilation failure is a HARD STOP
- **CRITICAL VALIDATION**: Run `./mvnw clean verify` after changes that touch controllers, exception handlers, security, or DTOs
- **SECURITY**: Never log or return secrets, tokens, or passwords in API responses; verify that error payloads do not include stack traces in production
- **COMPATIBILITY**: Changing status codes, DTO field names, or error JSON shapes can break clients — coordinate versioning or deprecation
- **NETTY / BLOCKING**: Long blocking JDBC or blocking HTTP client calls on the Netty event loop can stall other requests — use `@ExecuteOn`, virtual threads, or reactive clients where appropriate
- **INCREMENTAL SAFETY**: Prefer small, reviewable controller and exception-handler changes over large sweeping rewrites; verify compile and tests between steps