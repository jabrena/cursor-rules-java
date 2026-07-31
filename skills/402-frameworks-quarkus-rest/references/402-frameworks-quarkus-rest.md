---
name: 402-frameworks-quarkus-rest
description: Use when you need to design, review, or improve REST APIs with Quarkus REST (Jakarta REST) — including resource classes, HTTP methods, status codes, request/response DTOs, ISO-8601 instants in DTOs, Bean Validation, exception mappers, optional runtime OpenAPI exposure (SmallRye), contract-first generation from OpenAPI (Quarkus OpenAPI Generator or OpenAPI Generator `jaxrs-spec`), content negotiation, pagination, and security-aware boundaries.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Quarkus REST API Guidelines

## Role

You are a Senior software engineer with extensive experience in REST API design and Quarkus

## Goal

Quarkus REST builds on Jakarta REST (`jakarta.ws.rs`). Resources should express clear HTTP semantics, validate input at the boundary, return appropriate status codes, and map failures to stable JSON (or Problem Details) without leaking stack traces. Use CDI-scoped resource classes (`@Path` + `@ApplicationScoped` or `@RequestScoped` as appropriate), inject services, and (if you need a live `/openapi` endpoint) expose SmallRye OpenAPI — for **API-first** work, the checked-in OpenAPI file is the contract, not annotation-heavy descriptions in code. Generate Jakarta REST API interfaces and models from the OpenAPI document using **Quarkus OpenAPI Generator** (`io.quarkiverse.openapi.generator:quarkus-openapi-generator`) or the **OpenAPI Generator** `jaxrs-spec` target with `interfaceOnly`, then implement those interfaces in CDI resource classes. Align with the same REST design principles as Spring (`@302-frameworks-spring-boot-rest`) while using JAX-RS annotations and Quarkus extensions.

## Constraints

Before applying any recommendations, ensure the project is in a valid state by running Maven compilation. Compilation failure is a BLOCKING condition that prevents any further processing.

- **MANDATORY**: Run `./mvnw compile` or `mvn compile` before applying any change
- **PREREQUISITE**: Project must compile successfully before any REST refactoring
- **CRITICAL SAFETY**: If compilation fails, IMMEDIATELY STOP and DO NOT CONTINUE
- **VERIFY**: Run `./mvnw clean verify` or `mvn clean verify` after applying improvements
- **BLOCKING CONDITION**: Compilation errors must be resolved by the user before proceeding with REST improvements
- **NO EXCEPTIONS**: Under no circumstances should REST API recommendations be applied to a project that fails to compile

## Examples

### Table of contents

- Example 1: CRUD resource
- Example 2: Request validation
- Example 3: Exception mapping
- Example 4: Resource URIs and naming
- Example 5: HTTP status codes
- Example 6: DTOs for requests and responses
- Example 7: Time and locale in contracts
- Example 8: API versioning
- Example 9: Structured error responses
- Example 10: Pagination, sorting, and filtering
- Example 11: Idempotency and safe retries
- Example 12: Optimistic concurrency with ETag
- Example 13: Caching semantics
- Example 14: Deprecation and sunset
- Example 15: Security annotations
- Example 16: API-first with OpenAPI Generator (Quarkus / Jakarta REST)

### Example 1: CRUD resource

Title: Jakarta REST with correct status codes
Description: Map operations to HTTP verbs and return `Response` when you need status codes and headers such as `Location`.

**Good example:**

```java
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

@Path("/items")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ItemResource {

    private final ItemService items;

    @Inject
    public ItemResource(ItemService items) {
        this.items = items;
    }

    @GET
    public List<ItemResponse> list() {
        return items.findAll();
    }

    @GET
    @Path("{id}")
    public ItemResponse get(@PathParam("id") long id) {
        return items.findById(id);
    }

    @POST
    public Response create(@Valid CreateItemRequest body) {
        ItemResponse created = items.create(body);
        return Response.created(URI.create("/items/" + created.id())).entity(created).build();
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") long id) {
        items.delete(id);
        return Response.noContent().build();
    }
}
```

**Bad example:**

```java
@Path("/api")
public class BadApi {

    @GET
    @Path("/deleteItem")
    public String deleteViaGet(@QueryParam("id") String id) {
        return "deleted"; // unsafe semantics — GET must not mutate
    }
}
```

### Example 2: Request validation

Title: @Valid on request bodies
Description: Apply Bean Validation annotations on DTOs and `@Valid` on resource parameters so Quarkus returns **400** with violation details.

**Good example:**

```java
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

public record RegisterRequest(
    @NotBlank String name,
    @NotBlank @Email String email
) { }

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @POST
    public void register(@Valid RegisterRequest body) {
        // persist
    }
}
```

**Bad example:**

```java
@POST
public void register(RegisterRequest body) {
    if (body.email() == null || !body.email().contains("@")) {
        throw new IllegalArgumentException("bad email"); // ad-hoc validation — inconsistent 400 bodies
    }
}
```

### Example 3: Exception mapping

Title: Consistent error JSON
Description: Register `ExceptionMapper` beans to translate domain exceptions into HTTP responses without duplicating try/catch in every resource method.

**Good example:**

```java
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.Map;

@Provider
public class NotFoundMapper implements ExceptionMapper<NotFoundException> {

    @Override
    public Response toResponse(NotFoundException ex) {
        return Response.status(404)
            .entity(Map.of("error", ex.getMessage()))
            .build();
    }
}
```

**Bad example:**

```java
@GET
@Path("{id}")
public ItemResponse get(@PathParam("id") long id) {
    try {
        return service.find(id);
    } catch (NotFoundException e) {
        return null; // wrong — yields 200 with empty body or NPE
    }
}
```

### Example 4: Resource URIs and naming

Title: Noun-based paths and hierarchy; no verbs in URIs
Description: Model resources as nouns with stable hierarchical paths. The HTTP method communicates the action; the path names the resource. Avoid RPC-style verbs (`/getAllOrders`, `/createOrder`) that duplicate what the HTTP verb already expresses.

**Good example:**

```text
GET    /api/v1/orders
GET    /api/v1/orders/{orderId}
GET    /api/v1/orders/{orderId}/items
POST   /api/v1/orders
PUT    /api/v1/orders/{orderId}
PATCH  /api/v1/orders/{orderId}/status
```

**Bad example:**

```text
GET  /getAllOrders
GET  /fetchOrderById?id={orderId}
POST /createNewOrder
GET  /orderItems?orderId={orderId}
POST /processOrderCancellation
```

### Example 5: HTTP status codes

Title: 201 + Location for creates, 204 for deletes, 400/404/409 for errors
Description: Use status codes that match the real outcome: `201 Created` + `Location` for creates; `204 No Content` for successful deletes; `400` for input errors; `404` for missing resources; `409 Conflict` for state collisions. Never stuff errors into a `200 OK` body.

**Good example:**

```java
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;

@Path("/api/v1/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResource {

    private final OrderService service;

    @Inject
    public OrderResource(OrderService service) {
        this.service = service;
    }

    @POST
    public Response create(@Valid CreateOrderRequest body) {
        OrderResponse created = service.create(body);
        return Response.created(URI.create("/api/v1/orders/" + created.id()))
            .entity(created).build(); // 201 Created + Location
    }

    @GET
    @Path("{id}")
    public OrderResponse get(@PathParam("id") long id) {
        return service.findById(id); // 200 OK; 404 handled via ExceptionMapper
    }

    @DELETE
    @Path("{id}")
    public Response cancel(@PathParam("id") long id) {
        service.cancel(id);
        return Response.noContent().build(); // 204 No Content
    }
}
```

**Bad example:**

```java
@Path("/orders")
public class BadOrderResource {

    @POST
    public String createOrder(CreateOrderRequest body) {
        return "Order created"; // Bad: 200 instead of 201; no Location header
    }

    @GET
    @Path("{id}")
    public String get(@PathParam("id") long id) {
        if (id == 0) {
            return "{\"error\": \"not found\"}"; // Bad: stuffs error into 200 OK body
        }
        return "{}";
    }
}
```

### Example 6: DTOs for requests and responses

Title: Decouple API contracts from persistence entities
Description: Expose records or DTOs from resources — never persistence entities — so serialization matches what clients should see. Returning entities directly leaks internal fields (password hashes, audit columns) and couples the API contract to the database schema.

**Good example:**

```java
import java.time.Instant;

// Persistence entity — never exposed directly to API consumers
class UserEntity {
    long id;
    String username;
    String passwordHash; // internal — must never reach the client
    String email;
    Instant createdAt;
}

// Lean response DTO: no password hash, no internal audit fields
record UserResponse(long id, String username, String email, Instant createdAt) { }

// Inbound DTO: password accepted once, never echoed back
record CreateUserRequest(
    @jakarta.validation.constraints.NotBlank String username,
    @jakarta.validation.constraints.Email String email,
    @jakarta.validation.constraints.Size(min = 8) String password) { }
```

**Bad example:**

```java
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    @GET
    @Path("{id}")
    public UserEntity get(@PathParam("id") long id) {
        return findById(id); // Bad: exposes passwordHash, audit columns, and all internal state
    }

    private UserEntity findById(long id) { return new UserEntity(); }
}

class UserEntity {
    public long id;
    public String username;
    public String passwordHash; // leaked to every API consumer
}
```

### Example 7: Time and locale in contracts

Title: ISO-8601 with offset; optional `Accept-Language` for errors
Description: Model timestamps as `Instant` or `OffsetDateTime` (ISO-8601 with offset) so clients interpret wall-clock unambiguously—avoid legacy `Date` and ambiguous zone-less `LocalDateTime` in public JSON unless the domain is strictly calendar-local. For localized problem/error text (RFC 7807-style bodies), honor `Accept-Language` only if you keep stable problem `type` URIs/codes identical across locales and avoid leaking sensitive details through translated messages.

**Good example:**

```java
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Locale;
import java.util.ResourceBundle;

@Path("/api/v1/events")
@Produces(MediaType.APPLICATION_JSON)
public class EventResource {

    @GET
    @Path("{id}")
    public Response get(@PathParam("id") String id) {
        return Response.ok(new EventDTO(
            Instant.parse("2026-03-23T12:00:00Z"),
            OffsetDateTime.of(2026, 3, 23, 13, 0, 0, 0, ZoneOffset.ofHours(1)))).build();
    }
}

record EventDTO(Instant occurredAt, OffsetDateTime windowStart) { }

// Same Problem Detail field shape as in structured error examples — title/detail from bundles, never raw ex.getMessage()
record ProblemDetail(String type, String title, int status, String detail, String errorId) { }

@Provider
public class IllegalArgumentExceptionMapper implements ExceptionMapper<IllegalArgumentException> {

    @Context
    HttpHeaders headers;

    @Override
    public Response toResponse(IllegalArgumentException ex) {
        Locale locale = headers.getAcceptableLanguages().isEmpty()
            ? Locale.ENGLISH
            : headers.getAcceptableLanguages().getFirst();
        ResourceBundle bundle = ResourceBundle.getBundle("ProblemMessages", locale);
        String detail = bundle.getString("problem.invalid_argument.detail");
        String title = bundle.getString("problem.invalid_argument.title");
        return Response.status(400)
            .type(MediaType.APPLICATION_JSON)
            .entity(new ProblemDetail(
                "https://example.com/problems/invalid-argument",
                title,
                400,
                detail,
                null))
            .build();
    }
}
```

**Bad example:**

```java
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Date;

@Path("/events")
@Produces(MediaType.APPLICATION_JSON)
public class AmbiguousTimeResource {

    @GET
    @Path("{id}")
    public Response get(@PathParam("id") String id) {
        return Response.ok(new EventDTO(new Date())).build();
    }
}

class EventDTO {
    private Date createdAt;
    EventDTO(Date createdAt) { this.createdAt = createdAt; }
}
```


### Example 8: API versioning

Title: URI path, custom header, or vendor media type — pick one and apply uniformly
Description: Introduce versioning early so breaking changes do not silently affect existing clients. Pick one strategy and apply it **uniformly** across every resource. Common options: **URI path** (`/api/v1/…`) — easiest to browse, cache, and test; visible in logs. **Custom header** (`X-API-Version: 2`) — clean URIs; version not in browser or CDN cache keys. **Vendor media type** (`Accept: application/vnd.example.order+json;version=2`) — RESTful negotiation; higher client complexity.

**Good example:**

```java
// ── Strategy 1: URI path versioning ──────────────────────────────────────────
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/api/v1/orders")
@Produces(MediaType.APPLICATION_JSON)
public class OrderResourceV1 {

    @GET
    @Path("{id}")
    public OrderDTOv1 get(@PathParam("id") long id) {
        return new OrderDTOv1(id, "PENDING");
    }
}

// V2 route — breaking shape change isolated behind a new path
@Path("/api/v2/orders")
@Produces(MediaType.APPLICATION_JSON)
public class OrderResourceV2 {

    @GET
    @Path("{id}")
    public OrderDTOv2 get(@PathParam("id") long id) {
        return new OrderDTOv2(id, "PENDING", java.time.Instant.now());
    }
}

record OrderDTOv1(long id, String status) { }
record OrderDTOv2(long id, String status, java.time.Instant createdAt) { }
```

**Bad example:**

```java
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

// No versioning — any breaking change silently breaks all clients
@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
public class UnversionedOrderResource {

    @GET
    @Path("{id}")
    public OrderDTO get(@PathParam("id") long id) {
        return new OrderDTO(id);
    }
}

record OrderDTO(long id) { }
```

### Example 9: Structured error responses

Title: ExceptionMapper with RFC 7807 Problem Detail shape; no stack traces to clients
Description: Register `ExceptionMapper` beans to centralize error handling. Return a consistent JSON body with stable fields (type, title, status, detail) aligned with RFC 7807 Problem Details. Log full exceptions server-side with a correlation ID; never return stack traces or raw exception messages to API clients in production.

**Good example:**

```java
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.UUID;
import org.jboss.logging.Logger;

public record ProblemDetail(String type, String title, int status, String detail, String errorId) { }

// Domain exception hierarchy — one mapper handles the family
public abstract class DomainException extends RuntimeException {
    protected DomainException(String message) { super(message); }
    public abstract int httpStatus();
    public abstract String typeSlug();
    public abstract String title();
}

public class NotFoundException extends DomainException {
    public NotFoundException(String message) { super(message); }
    public int httpStatus() { return 404; }
    public String typeSlug() { return "not-found"; }
    public String title() { return "Resource Not Found"; }
}

@Provider
public class DomainExceptionMapper implements ExceptionMapper<DomainException> {

    @Override
    public Response toResponse(DomainException ex) {
        return Response.status(ex.httpStatus())
            .type(MediaType.APPLICATION_JSON)
            .entity(new ProblemDetail(
                "https://example.com/problems/" + ex.typeSlug(),
                ex.title(),
                ex.httpStatus(),
                ex.getMessage(),
                null))
            .build();
    }
}

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {

    private static final Logger LOG = Logger.getLogger(GlobalExceptionMapper.class);

    @Override
    public Response toResponse(Exception ex) {
        String errorId = UUID.randomUUID().toString();
        LOG.errorf(ex, "Unhandled exception [%s]", errorId);
        return Response.serverError()
            .type(MediaType.APPLICATION_JSON)
            .entity(new ProblemDetail(
                "https://example.com/problems/internal-error",
                "Internal Server Error",
                500,
                "An unexpected error occurred.",
                errorId))
            .build();
    }
}
```

**Bad example:**

```java
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
public class OrderResource {

    @GET
    @Path("{id}")
    public Response get(@PathParam("id") long id) {
        try {
            return Response.ok(findOrder(id)).build();
        } catch (Exception e) {
            // Bad: leaks stack trace and exception type to clients
            return Response.serverError()
                .entity(e.toString()) // internal details exposed
                .build();
        }
    }

    private Object findOrder(long id) { throw new RuntimeException("DB unavailable"); }
}
```

### Example 10: Pagination, sorting, and filtering

Title: Bounded pages, sort whitelist, optional Link headers (RFC 8288)
Description: Returning unbounded lists does not scale. Use explicit `page`/`size` query params with server-enforced maximums, whitelist sortable field names, and optionally emit `Link` headers (`rel="next"` / `rel="prev"`) for discoverable navigation (RFC 8288).

**Good example:**

```java
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Set;

@Path("/api/v1/orders")
@Produces(MediaType.APPLICATION_JSON)
public class OrderListResource {

    private static final int MAX_PAGE_SIZE = 100;
    private static final Set<String> ALLOWED_SORT = Set.of("createdAt", "status", "total");

    private final OrderService service;

    @Inject
    public OrderListResource(OrderService service) {
        this.service = service;
    }

    @GET
    public Response list(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size,
            @QueryParam("sort") @DefaultValue("createdAt") String sort) {
        if (size > MAX_PAGE_SIZE) {
            throw new BadRequestException("size exceeds maximum of " + MAX_PAGE_SIZE);
        }
        if (!ALLOWED_SORT.contains(sort)) {
            throw new BadRequestException("sort by '" + sort + "' is not allowed");
        }
        List<OrderResponse> orders = service.findPage(page, size, sort);
        String nextLink = "</api/v1/orders?page=" + (page + 1) + "&size=" + size + ">; rel=\"next\"";
        return Response.ok(orders).header("Link", nextLink).build();
    }
}

record OrderResponse(long id, String status) { }
interface OrderService { List<OrderResponse> findPage(int page, int size, String sort); }
```

**Bad example:**

```java
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
public class UnboundedOrderResource {

    @GET
    public List<OrderResponse> listAll(@QueryParam("sort") String sort) {
        // Bad: no result cap — dumps entire table; sort field not validated
        return loadAllOrders(sort);
    }

    private List<OrderResponse> loadAllOrders(String sort) { return List.of(); }
}

record OrderResponse(long id, String status) { }
```

### Example 11: Idempotency and safe retries

Title: Idempotency-Key header, deduplication, 409 Conflict
Description: Network clients retry `POST`; without idempotency, creates can duplicate. Accept an `Idempotency-Key` header and return the same result when the key replays a completed operation. Return **409 Conflict** when a request collides with current resource state (e.g. duplicate business key).

**Good example:**

```java
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;

@Path("/api/v1/payments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PaymentResource {

    private final Map<String, PaymentResponse> completed = new ConcurrentHashMap<>();

    @Operation(summary = "Create payment",
               description = "Idempotent: repeating the same Idempotency-Key returns the original result without a double charge.")
    @POST
    public Response create(
            @Parameter(description = "Client-generated deduplication key", required = true)
            @HeaderParam("Idempotency-Key") String idempotencyKey,
            @Valid CreatePaymentRequest body) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new BadRequestException("Idempotency-Key header is required");
        }
        PaymentResponse prior = completed.get(idempotencyKey);
        if (prior != null) {
            return Response.ok(prior).build(); // replay — no double charge
        }
        PaymentResponse created = new PaymentResponse("pay-" + System.currentTimeMillis());
        completed.put(idempotencyKey, created);
        return Response.created(URI.create("/api/v1/payments/" + created.id()))
            .entity(created).build();
    }
}

record CreatePaymentRequest(String accountId, int amountCents) { }
record PaymentResponse(String id) { }
```

**Bad example:**

```java
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/payments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class NonIdempotentPaymentResource {

    @POST
    public Response charge(CreatePaymentRequest body) {
        // Bad: no Idempotency-Key — every retry causes a duplicate charge
        return Response.ok(new PaymentResponse("pay-new")).build();
    }
}

record CreatePaymentRequest(String accountId, int amountCents) { }
record PaymentResponse(String id) { }
```

### Example 12: Optimistic concurrency with ETag

Title: If-Match / If-None-Match, 412 Precondition Failed, 304 Not Modified
Description: Prevent lost updates by generating an `ETag` on reads and requiring `If-Match` on writes. Use `jakarta.ws.rs.core.Request.evaluatePreconditions()` to check `If-None-Match` (yields **304**) or `If-Match` (yields **412**) without manual boilerplate.

**Good example:**

```java
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

@Path("/api/v1/items")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ItemResource {

    private final ItemService service;

    @Inject
    public ItemResource(ItemService service) {
        this.service = service;
    }

    @GET
    @Path("{id}")
    public Response get(@PathParam("id") long id, @Context Request request) {
        ItemResponse item = service.findById(id);
        EntityTag etag = new EntityTag(Integer.toHexString(item.version()));
        Response.ResponseBuilder notModified = request.evaluatePreconditions(etag);
        if (notModified != null) {
            return notModified.build(); // 304 Not Modified
        }
        return Response.ok(item).tag(etag).build();
    }

    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") long id,
                           @HeaderParam("If-Match") String ifMatch,
                           @Valid UpdateItemRequest body) {
        ItemResponse current = service.findById(id);
        EntityTag etag = new EntityTag(Integer.toHexString(current.version()));
        if (!etag.toString().equals(ifMatch)) {
            return Response.status(Response.Status.PRECONDITION_FAILED).build(); // 412
        }
        ItemResponse updated = service.update(id, body);
        return Response.ok(updated)
            .tag(new EntityTag(Integer.toHexString(updated.version()))).build();
    }
}

record ItemResponse(long id, String name, int version) { }
record UpdateItemRequest(String name) { }
interface ItemService {
    ItemResponse findById(long id);
    ItemResponse update(long id, UpdateItemRequest req);
}
```

**Bad example:**

```java
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/items")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BlindOverwriteResource {

    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") long id, UpdateItemRequest body) {
        // Bad: no ETag / version check — concurrent updates silently overwrite each other
        return Response.ok(new ItemResponse(id, body.name(), 0)).build();
    }
}

record UpdateItemRequest(String name) { }
record ItemResponse(long id, String name, int version) { }
```

### Example 13: Caching semantics

Title: Cache-Control: public for catalog data, no-store for authenticated responses
Description: Set `Cache-Control` deliberately: `public, max-age=N` for anonymous catalog data so CDNs and browsers can cache it; `no-store` for authenticated or personalized payloads to prevent cross-user data leaks via shared caches.

**Good example:**

```java
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.CacheControl;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/v1/catalog")
@Produces(MediaType.APPLICATION_JSON)
public class CatalogResource {

    @GET
    @Path("{id}")
    public Response getPublic(@PathParam("id") long id) {
        CacheControl cc = new CacheControl();
        cc.setMaxAge(300);
        return Response.ok(new ProductResponse(id, "Widget"))
            .cacheControl(cc)
            .header("Cache-Control", "public, max-age=300") // mark as publicly cacheable
            .build();
    }
}

@Path("/api/v1/me")
@Produces(MediaType.APPLICATION_JSON)
public class ProfileResource {

    @GET
    public Response getProfile() {
        CacheControl noStore = new CacheControl();
        noStore.setNoStore(true);
        return Response.ok(new UserProfile("alice@example.com"))
            .cacheControl(noStore)
            .build(); // no-store: never served from a shared cache
    }
}

record ProductResponse(long id, String name) { }
record UserProfile(String email) { }
```

**Bad example:**

```java
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/me")
@Produces(MediaType.APPLICATION_JSON)
public class BadProfileResource {

    @GET
    public Response getProfile() {
        // Bad: authenticated user profile marked as publicly cacheable
        // A CDN will serve user A's data to user B on the next request
        return Response.ok(new UserProfile("alice@example.com"))
            .header("Cache-Control", "public, max-age=3600")
            .build();
    }
}

record UserProfile(String email) { }
```

### Example 14: Deprecation and sunset

Title: Deprecation, Sunset, and Link headers for API lifecycle signalling
Description: When an endpoint or version is headed for removal, advertise it with RFC-style headers: `Deprecation` (`true` or an HTTP-date timestamp), `Sunset` with the expected removal date, and `Link` (`rel="successor-version"`) pointing to the replacement. Pair with your versioning strategy and document the timeline in OpenAPI.

**Good example:**

```java
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/v1/orders")
@Produces(MediaType.APPLICATION_JSON)
public class DeprecatedOrderResourceV1 {

    @GET
    @Path("{id}")
    public Response get(@PathParam("id") long id) {
        return Response.ok(new OrderDTOv1(id, "PENDING"))
            .header("Deprecation", "true")
            .header("Sunset", "Sat, 31 Dec 2026 23:59:59 GMT")
            .header("Link", "</api/v2/orders/" + id + ">; rel=\"successor-version\"")
            .build();
        // Production tip: emit Deprecation/Sunset from a ContainerResponseFilter
        // so all deprecated resources are flagged uniformly without per-method repetition.
    }
}

record OrderDTOv1(long id, String status) { }
```

**Bad example:**

```java
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

// No Deprecation / Sunset headers — clients have no way to know this endpoint is being removed
@Path("/api/v1/orders")
@Produces(MediaType.APPLICATION_JSON)
public class SilentBreakResource {

    @GET
    @Path("{id}")
    public Response get(@PathParam("id") long id) {
        return Response.ok(new OrderDTOv1(id, "PENDING")).build();
    }
}

record OrderDTOv1(long id, String status) { }
```

### Example 15: Security annotations

Title: @RolesAllowed, @Authenticated, and @PermitAll for declarative access control
Description: Use Jakarta Security and Quarkus Security annotations to declare access control at the resource or method level. Prefer `@RolesAllowed` for role-based access, `@io.quarkus.security.Authenticated` for any authenticated user, and `@PermitAll` for public endpoints. Avoid ad-hoc security checks inside business logic.

**Good example:**

```java
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/api/v1/orders")
@Produces(MediaType.APPLICATION_JSON)
@Authenticated // all methods require authentication unless overridden
public class SecureOrderResource {

    @GET
    @PermitAll // public catalog read — no auth required
    public List<OrderResponse> listPublic() {
        return List.of();
    }

    @GET
    @Path("{id}")
    // @Authenticated inherited — any authenticated user can read
    public OrderResponse get(@PathParam("id") long id) {
        return new OrderResponse(id, "PENDING");
    }

    @DELETE
    @Path("{id}")
    @RolesAllowed("admin") // only users with the admin role can delete
    public Response delete(@PathParam("id") long id) {
        return Response.noContent().build();
    }
}

record OrderResponse(long id, String status) { }
```

**Bad example:**

```java
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
public class AdHocSecurityResource {

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") long id, @Context HttpHeaders headers) {
        // Bad: manual token inspection instead of declarative security annotations
        String token = headers.getHeaderString("X-Admin-Token");
        if (!"secret".equals(token)) {
            return Response.status(403).build();
        }
        return Response.noContent().build();
    }
}
```

### Example 16: API-first with OpenAPI Generator (Quarkus / Jakarta REST)

Title: Generate JAX-RS API interfaces from `openapi.yaml`; implement as `@Path` resources
Description: Store the contract under `src/main/resources/openapi` (or similar) and generate **Jakarta REST** API interfaces plus models in the build. Prefer **Quarkus OpenAPI Generator** (`quarkus-openapi-generator`) for Quarkus-native integration, or **`openapi-generator-maven-plugin`** with `generatorName` `jaxrs-spec`, `interfaceOnly=true`, and `useJakartaEe=true`. The generated `*Api` types declare `@Path`, `@GET`/`@POST`, `@Consumes`/`@Produces`; your CDI resource class **implements** the interface and delegates to application services. Register `ExceptionMapper` as needed. Keep the OpenAPI spec authoritative in CI (lint before merge); generated interfaces carry the contract — avoid duplicating it with inline MicroProfile OpenAPI annotations unless you intentionally maintain runtime OpenAPI exposure separately.

**Good example:**

```text
<!-- Option A: Quarkus extension — see Quarkus OpenAPI Generator guide for full config -->
<!--
<dependency>
    <groupId>io.quarkiverse.openapi.generator</groupId>
    <artifactId>quarkus-openapi-generator-server</artifactId>
</dependency>
-->
<!-- Option B: openapi-generator-maven-plugin (jaxrs-spec) -->
<plugin>
    <groupId>org.openapitools</groupId>
    <artifactId>openapi-generator-maven-plugin</artifactId>
    <version>${openapi-generator.version}</version>
    <executions>
        <execution>
            <goals><goal>generate</goal></goals>
            <configuration>
                <inputSpec>${project.basedir}/src/main/resources/openapi/api.yaml</inputSpec>
                <generatorName>jaxrs-spec</generatorName>
                <apiPackage>com.example.generated.api</apiPackage>
                <modelPackage>com.example.generated.model</modelPackage>
                <configOptions>
                    <interfaceOnly>true</interfaceOnly>
                    <useJakartaEe>true</useJakartaEe>
                    <supportAsync>true</supportAsync>
                </configOptions>
            </configuration>
        </execution>
    </executions>
</plugin>

// --- Implement generated UsersApi as a CDI resource ---

import com.example.generated.api.UsersApi;
import com.example.generated.model.UserDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;

@ApplicationScoped
@Path("/api/v1")
public class UserResource implements UsersApi {

    private final UserService users;

    @Inject
    public UserResource(UserService users) {
        this.users = users;
    }

    @Override
    public UserDto getUserById(String userId) {
        return users.require(userId);
    }
}

interface UserService {
    UserDto require(String id);
}
```

**Bad example:**

```java
// Anti-pattern: resource paths and payloads edited only in Java while openapi.yaml
// is stale — contract tests and client SDKs disagree with runtime behavior.
@jakarta.ws.rs.Path("/api/v1")
public class DriftResource {
    @jakarta.ws.rs.GET
    @jakarta.ws.rs.Path("users/{id}")
    public Object get(String id) {
        return id;
    }
}
```


## Output Format

- **ANALYZE** resources for HTTP misuse, URI shape, status codes, DTO boundaries, versioning, deprecation/sunset/Link headers, `@Produces`/`@Consumes` declarations, ISO-8601 time fields, pagination bounds, Bean Validation on request DTOs, idempotency, ETag/concurrency, Cache-Control correctness, ExceptionMapper coverage, security annotations, and (when API-first) consistency between `openapi.yaml` and generated Jakarta REST API interfaces vs resource implementations
- **CATEGORIZE** findings by impact (CRITICAL for security/semantic violations, MAINTAINABILITY for DTO/versioning/docs, CONSISTENCY for errors) and by area (routing, responses, errors, security, caching, docs)
- **APPLY** improvements: fix unsafe GETs, normalize paths, return correct status codes, introduce DTO boundaries, use `OffsetDateTime`/`Instant` in API DTOs for ISO-8601 time fields, add versioning, emit deprecation/sunset/Link when phasing out endpoints, tighten `@Produces`/`@Consumes`, bound list endpoints with `page`/`size` caps, add `@Valid` on request bodies, add idempotency and 409 Conflict patterns, add ETag/`If-Match` checks for updates, set `Cache-Control`, centralize errors with `ExceptionMapper` and Problem Detail shape, add declarative security annotations, and (API-first) align CDI resources with generated `*Api` interfaces from the checked-in `openapi.yaml`
- **IMPLEMENT** incrementally: preserve public API contracts where possible; use deprecation and versioning for breaking changes; keep error shapes backward compatible unless versioning allows a break
- **EXPLAIN** trade-offs (e.g., URI vs header versioning, blocking vs reactive patterns, `@Blocking` vs `@RunOnVirtualThread`) when multiple valid options exist
- **VALIDATE** with `./mvnw compile` before and `./mvnw clean verify` after substantive edits; exercise critical endpoints in integration tests where available


## Safeguards

- **BLOCKING SAFETY CHECK**: ALWAYS run `./mvnw compile` or `mvn compile` before ANY REST refactoring — compilation failure is a HARD STOP
- **CRITICAL VALIDATION**: Run `./mvnw clean verify` after changes that touch resources, exception mappers, security, or DTOs
- **SECURITY**: Never log or return secrets, tokens, or passwords in API responses; verify that error payloads do not include stack traces in production
- **COMPATIBILITY**: Changing status codes, DTO field names, or error JSON shapes can break clients — coordinate versioning or deprecation before making breaking changes
- **REACTIVE THREADS**: Long blocking JDBC or HTTP client calls on the Vert.x event loop will stall other requests — annotate with `@Blocking` or use `@RunOnVirtualThread`
- **INCREMENTAL SAFETY**: Prefer small, reviewable resource and mapper changes over large sweeping rewrites; verify compile and tests between steps