---
name: 123-rest-api-patterns
description: Use when designing or reviewing REST API patterns — resource-oriented endpoints, DTO boundaries, idempotency, pagination, optimistic concurrency, Problem Details, API gateways, and resilient client interaction.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Java Design and Integration Patterns

## Role

You are a Senior software engineer with extensive experience in REST API design, Java web applications, and distributed systems

## Goal

Design REST APIs around stable resource contracts, predictable HTTP semantics, explicit error shapes, and evolvable client/server boundaries.

### Pattern selection principles

1. **Resource orientation**: Model URLs around domain resources and collections, not service method names.
2. **Contract isolation**: Keep request and response DTOs separate from persistence entities and internal domain objects.
3. **Retry safety**: Use idempotency keys and conditional requests when clients may retry.
4. **Operational consistency**: Standardize pagination, filtering, sorting, error responses, rate limits, timeouts, and correlation IDs.
5. **Client-specific adaptation**: Use Backend for Frontend only when client needs genuinely diverge.

### Pattern selection matrix

| Design pressure | Candidate pattern | Use when | Avoid when | Validation signal |
|---|---|---|---|---|
| CRUD contract is unclear | Resource-oriented endpoints | Clients operate on stable resources and collections | The operation is a true command with no useful resource representation | OpenAPI and integration tests show correct methods/status codes |
| Public contract leaks internals | DTO boundary | Persistence/domain fields differ from the public API | Internal and external shape are intentionally identical and stable | Contract tests prove entity fields are not exposed accidentally |
| Clients retry creates or commands | Idempotency key | Network timeouts can cause duplicate non-idempotent requests | Operation is naturally idempotent, such as PUT of a full representation | Duplicate-key tests return the stored result |
| Errors vary by controller | Problem Details | Clients need stable machine-readable failures | A simple 404/204 response is enough and no body is needed | Negative tests assert type, status, and detail without internals |
| Collections can grow | Pagination and filtering | Result sets are user-driven or unbounded | Administrative endpoint has a small bounded result | Tests cover default limit, max limit, filters, and links/cursors |
| Concurrent edits can overwrite | ETag and If-Match | Clients update versioned resources | Last-write-wins is explicitly acceptable | Conflict tests return 412 or 409 for stale writes |

## Constraints

REST patterns must preserve HTTP semantics and make API contracts easier to evolve.

- **HTTP SEMANTICS**: Use methods, status codes, headers, and cache/concurrency semantics intentionally
- **DTO BOUNDARY**: Do not expose persistence entities directly as public API contracts
- **ERROR CONTRACTS**: Prefer RFC 9457-style Problem Details for machine-readable API errors
- **IDEMPOTENCY**: For retryable state-changing operations, define idempotency behavior and storage requirements

## Examples

### Table of contents

- Example 1: Use Resource-Oriented Endpoints
- Example 2: Use Idempotency Key for Retryable Commands
- Example 3: Use Problem Details for API Errors
- Example 4: Use Pagination and Filtering for Collections
- Example 5: Use ETag and If-Match for Optimistic Concurrency

### Example 1: Use Resource-Oriented Endpoints

Title: Represent business resources instead of internal actions
Description: Resource-oriented APIs make contracts predictable and easier to document. Benefit: clients can infer HTTP semantics and cache/concurrency behavior. Cost: some workflows require extra resource names such as cancellations. Use command-like endpoints only when the action is not naturally represented as a resource transition. Validate with OpenAPI review and method/status integration tests.

**Good example:**

```text
GET    /customers/{customerId}/orders
POST   /customers/{customerId}/orders
GET    /orders/{orderId}
PATCH  /orders/{orderId}
POST   /orders/{orderId}/cancellations
```

**Bad example:**

```text
POST /createOrder
POST /updateOrder
POST /cancelOrder
GET  /getOrdersForCustomer
POST /doOrderWorkflowStep
```

### Example 2: Use Idempotency Key for Retryable Commands

Title: Make client retries safe for create operations
Description: Use an idempotency key when clients may retry a non-idempotent operation after timeouts or network failures. Benefit: duplicate retries return the same result instead of creating duplicate work. Cost: keys, request hashes, and stored responses need retention and cleanup rules. Avoid it for naturally idempotent PUT/PATCH semantics when conditional requests are enough. Validate by replaying the same key and mismatched payload.

**Good example:**

```http
POST /payments HTTP/1.1
Idempotency-Key: 8f7f0e50-56a5-4f55-9e6e-0cb94e1f6a3d
Content-Type: application/json

{
  "invoiceId": "inv-123",
  "amount": "49.95",
  "currency": "EUR"
}
```

**Bad example:**

```http
POST /payments HTTP/1.1
Content-Type: application/json

{
  "invoiceId": "inv-123",
  "amount": "49.95",
  "currency": "EUR"
}
```

### Example 3: Use Problem Details for API Errors

Title: Return stable machine-readable errors
Description: Problem Details gives clients a consistent error envelope while allowing service-specific details. Benefit: clients can branch on stable problem types. Cost: error taxonomy and localization details must be maintained. Do not leak stack traces, SQL errors, or internal class names. Validate negative cases for validation, conflict, not-found, and authorization failures.

**Good example:**

```json
{
  "type": "urn:problem:invalid-order-state",
  "title": "Invalid order state",
  "status": 409,
  "detail": "Only NEW orders can be cancelled.",
  "instance": "/orders/ord-123/cancellations"
}
```

**Bad example:**

```json
{
  "error": "java.lang.IllegalStateException",
  "message": "Cannot cancel row 8721 from table orders",
  "trace": "com.example.OrderService.cancel(OrderService.java:42)"
}
```

### Example 4: Use Pagination and Filtering for Collections

Title: Bound collection responses and make query behavior explicit
Description: Use pagination, filtering, and sorting when collection size is user-driven or operationally unbounded. Benefit: predictable latency and stable client navigation. Cost: query parameters, indexes, and ordering guarantees must be designed together. Avoid it for genuinely tiny bounded collections. Validate default limits, max limits, invalid filters, stable ordering, and index usage.

**Good example:**

```http
GET /orders?status=CONFIRMED&limit=50&cursor=eyJpZCI6Im9yZC0xMjMifQ HTTP/1.1

200 OK
Link: </orders?status=CONFIRMED&limit=50&cursor=next-token>; rel="next"

{
  "items": [
    { "id": "ord-124", "status": "CONFIRMED" }
  ],
  "nextCursor": "next-token"
}
```

**Bad example:**

```http
GET /orders?status=CONFIRMED HTTP/1.1

200 OK

[
  "...every confirmed order in the database..."
```

### Example 5: Use ETag and If-Match for Optimistic Concurrency

Title: Reject stale updates at the HTTP boundary
Description: Use ETag and If-Match when clients edit versioned resources and stale writes must not overwrite newer state. Benefit: concurrency control is visible in the contract. Cost: servers must expose stable validators and map conflicts consistently. Avoid it when last-write-wins is explicitly acceptable. Validate stale-update tests, missing-header behavior, and mapping to database versions.

**Good example:**

```http
GET /orders/ord-123 HTTP/1.1

200 OK
ETag: "orders-ord-123-v7"

PATCH /orders/ord-123 HTTP/1.1
If-Match: "orders-ord-123-v7"
Content-Type: application/json

{ "shippingAddress": "Calle Mayor 1" }

204 No Content
```

**Bad example:**

```http
PATCH /orders/ord-123 HTTP/1.1
Content-Type: application/json

{ "shippingAddress": "Calle Mayor 1" }

204 No Content
# A newer client update may have been overwritten silently.
```

## Output Format

- **DESIGN** resource paths, HTTP methods, status codes, request DTOs, response DTOs, and error shapes
- **DEFINE** pagination, filtering, sorting, idempotency, optimistic concurrency, and versioning rules when needed
- **EXPLAIN** trade-offs between generic APIs, BFF endpoints, and domain-specific resources
- **VALIDATE** API contracts with OpenAPI, integration tests, and negative cases for validation and conflict handling