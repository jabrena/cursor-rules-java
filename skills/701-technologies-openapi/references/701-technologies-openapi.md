---
name: 701-technologies-openapi
description: Use when you need framework-agnostic OpenAPI 3.x guidance — spec structure, metadata and versioning, paths and operations, reusable schemas, security schemes, examples, documentation quality, contract validation (e.g. Spectral), breaking-change awareness, and handoffs to codegen — without choosing Spring Boot, Quarkus, or Micronaut.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# OpenAPI 3.x best practices

## Role

You are a senior API architect with deep experience in OpenAPI 3.x, HTTP APIs, and contract-first delivery in Java ecosystems

## Goal

Apply **framework-agnostic** OpenAPI best practices so a published contract is accurate, evolvable, and safe for consumers and generators.

1. **Document and version** the API surface: `openapi`, `info` (title, version, contact, license), `servers`, tags, and consistent operation grouping.
2. **Model data** with reusable `components.schemas`, explicit formats (`format`, `pattern`, enums), nullability, and examples that match real payloads.
3. **Describe HTTP** faithfully: stable paths, correct verbs, explicit status codes and error bodies, `operationId`, parameters (path, query, header, cookie), and request/response `content` types.
4. **Security**: declare `securitySchemes` and default/global `security` requirements; avoid silent optional auth where the runtime always enforces it.
5. **Quality gates**: recommend spec linting (e.g. Spectral rulesets), CI validation before codegen, and changelog discipline for breaking vs additive changes.

Do not replace those framework skills; complement them with contract-level guidance only.

## Constraints

Before recommending structural or build changes, ensure the workspace builds. Compilation failure is a blocking condition for Java-side edits.

- **MANDATORY**: Run `./mvnw compile` or `mvn compile` before changing Java or build descriptors alongside the spec
- **SCOPE**: Stay within OpenAPI artifacts and cross-cutting contract hygiene; defer framework wiring to 302/402/502

## Examples

### Table of contents

- Example 1: Strong API metadata
- Example 2: Explicit operations and errors
- Example 3: Reusable schema design
- Example 4: Parameter design
- Example 5: Problem Details error contract
- Example 6: Security schemes
- Example 7: Realistic examples and content types
- Example 8: Breaking-change and validation gates

### Example 1: Strong API metadata

Title: title, version, description, servers, tags
Description: Consumers and tooling rely on `info` and `servers`. Version the *API product* in `info.version`; use URIs or server variables for environments. Use `tags` to group operations as they appear in documentation.

**Good example:**

```yaml
openapi: 3.0.3
info:
  title: Orders API
  version: 2.1.0
  description: |
    Manages customer orders. Breaking changes are documented in CHANGELOG.md.
  license:
    name: Apache-2.0
    url: https://www.apache.org/licenses/LICENSE-2.0.html
servers:
  - url: /v2
    description: Relative API base path resolved against the trusted deployment host
tags:
  - name: Orders
    description: Create and query orders
```

**Bad example:**

```yaml
openapi: 3.0.3
info:
  title: API
  version: "1"
# Missing servers, license, and tags; vague title/version
```

### Example 2: Explicit operations and errors

Title: operationId, responses, problem-style errors
Description: Every operation should have a unique `operationId` (stable for codegen). Document **all** realistic HTTP outcomes, including validation errors and conflict cases, referencing shared error schemas where possible.

**Good example:**

```yaml
paths:
  /orders/{orderId}:
    get:
      operationId: getOrderById
      tags: [Orders]
      parameters:
        - name: orderId
          in: path
          required: true
          schema:
            type: string
            format: uuid
      responses:
        "200":
          description: Order found
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Order'
        "404":
          description: Order not found
          content:
            application/problem+json:
              schema:
                $ref: '#/components/schemas/ProblemDetails'
```

**Bad example:**

```yaml
paths:
  /orders/{orderId}:
    get:
      # missing operationId; only 200 documented; no 404
      responses:
        "200":
          description: OK
```

### Example 3: Reusable schema design

Title: required fields, formats, enums, optionality, examples
Description: Put shared models under `components.schemas` and make the data contract explicit. Use `required` for fields consumers can rely on, `format` or `pattern` for constrained strings, enum values for closed sets, and realistic examples that match the declared schema. In OpenAPI 3.0, represent nullable values with `nullable: true`; in OpenAPI 3.1, prefer JSON Schema unions such as `type: [string, "null"]`.

**Good example:**

```yaml
components:
  schemas:
    Order:
      type: object
      required: [id, status, currency, totalAmount, createdAt, items]
      properties:
        id:
          type: string
          format: uuid
          example: "6f0a6a4b-36b8-4dc9-bc67-8f4a3bfb2d61"
        status:
          type: string
          enum: [PENDING, CONFIRMED, CANCELLED]
          example: CONFIRMED
        currency:
          type: string
          pattern: "^[A-Z]{3}$"
          example: EUR
        totalAmount:
          type: string
          pattern: "^\\d+\\.\\d{2}$"
          description: Decimal amount encoded as a string to preserve precision.
          example: "42.90"
        customerNote:
          type: string
          nullable: true
          maxLength: 500
          example: "Leave at reception"
        createdAt:
          type: string
          format: date-time
          example: "2026-03-18T10:15:30Z"
        items:
          type: array
          minItems: 1
          items:
            $ref: '#/components/schemas/OrderItem'
    OrderItem:
      type: object
      required: [sku, quantity]
      properties:
        sku:
          type: string
          pattern: "^[A-Z0-9-]{3,40}$"
          example: "BOOK-123"
        quantity:
          type: integer
          minimum: 1
          example: 2
```

**Bad example:**

```yaml
components:
  schemas:
    Order:
      type: object
      properties:
        id:
          type: string
        status:
          type: string
        totalAmount:
          type: number
# No required fields, no enum, no examples, and money precision is ambiguous.
```

### Example 4: Parameter design

Title: path, query, header, pagination, filtering, sorting
Description: Define reusable parameters when multiple operations share pagination, filtering, sorting, correlation, or tenant context. Path parameters must be `required: true`; query and header parameters should document defaults, bounds, allowed values, and examples so generated clients and tests behave predictably.

**Good example:**

```yaml
components:
  parameters:
    OrderId:
      name: orderId
      in: path
      required: true
      schema:
        type: string
        format: uuid
      example: "6f0a6a4b-36b8-4dc9-bc67-8f4a3bfb2d61"
    Page:
      name: page
      in: query
      required: false
      schema:
        type: integer
        minimum: 0
        default: 0
      example: 0
    Size:
      name: size
      in: query
      required: false
      schema:
        type: integer
        minimum: 1
        maximum: 100
        default: 25
      example: 25
    OrderStatusFilter:
      name: status
      in: query
      required: false
      schema:
        type: array
        items:
          type: string
          enum: [PENDING, CONFIRMED, CANCELLED]
      style: form
      explode: false
      example: [CONFIRMED, CANCELLED]
    Sort:
      name: sort
      in: query
      required: false
      schema:
        type: string
        pattern: "^[a-zA-Z]+,(asc|desc)$"
        default: "createdAt,desc"
      example: "createdAt,desc"
    CorrelationId:
      name: X-Correlation-Id
      in: header
      required: false
      schema:
        type: string
        format: uuid
      example: "b39bfc20-3f6d-4f72-b9d7-7c9f8b5d64ac"
paths:
  /orders:
    get:
      operationId: listOrders
      parameters:
        - $ref: '#/components/parameters/Page'
        - $ref: '#/components/parameters/Size'
        - $ref: '#/components/parameters/OrderStatusFilter'
        - $ref: '#/components/parameters/Sort'
        - $ref: '#/components/parameters/CorrelationId'
```

**Bad example:**

```yaml
paths:
  /orders:
    get:
      parameters:
        - name: page
          in: query
        - name: sort
          in: query
        - name: X-Correlation-Id
          in: header
# Missing schemas, bounds, defaults, examples, and sorting format.
```

### Example 5: Problem Details error contract

Title: reusable errors, validation details, conflict, not found
Description: Use one reusable error model across operations. RFC 9457 Problem Details (`application/problem+json`) is a strong default for HTTP APIs; add extension fields for validation errors only when they are stable contract fields. Reference shared responses for common failures such as validation, not found, conflict, unauthorized, and forbidden.

**Good example:**

```yaml
components:
  schemas:
    ProblemDetails:
      type: object
      required: [type, title, status, detail, instance]
      properties:
        type:
          type: string
          format: uri
          example: "https://api.example.com/problems/validation-error"
        title:
          type: string
          example: "Validation failed"
        status:
          type: integer
          minimum: 400
          maximum: 599
          example: 400
        detail:
          type: string
          example: "Request body contains invalid fields."
        instance:
          type: string
          example: "/orders"
        errors:
          type: array
          description: Field-level validation errors for validation problems.
          items:
            $ref: '#/components/schemas/FieldError'
    FieldError:
      type: object
      required: [field, message]
      properties:
        field:
          type: string
          example: "items[0].quantity"
        message:
          type: string
          example: "must be greater than or equal to 1"
  responses:
    ValidationProblem:
      description: Request validation failed
      content:
        application/problem+json:
          schema:
            $ref: '#/components/schemas/ProblemDetails'
    OrderNotFound:
      description: Order not found
      content:
        application/problem+json:
          schema:
            $ref: '#/components/schemas/ProblemDetails'
    OrderConflict:
      description: Order state conflict
      content:
        application/problem+json:
          schema:
            $ref: '#/components/schemas/ProblemDetails'
paths:
  /orders/{orderId}:
    patch:
      operationId: updateOrder
      responses:
        "400":
          $ref: '#/components/responses/ValidationProblem'
        "404":
          $ref: '#/components/responses/OrderNotFound'
        "409":
          $ref: '#/components/responses/OrderConflict'
```

**Bad example:**

```yaml
responses:
  "400":
    description: Bad Request
  "404":
    description: Not Found
  "409":
    description: Conflict
# No reusable body shape, no content type, and no field-level validation contract.
```

### Example 6: Security schemes

Title: bearer, OAuth2, API key, global vs operation security
Description: Declare security schemes under `components.securitySchemes`, then set the default `security` requirement at the root when most operations require authentication. Override per operation only for real exceptions, such as public health checks or stricter OAuth scopes. Do not document authentication as optional when the runtime enforces it.

**Good example:**

```yaml
components:
  securitySchemes:
    bearerAuth:
      type: http
      scheme: bearer
      bearerFormat: JWT
    orderOAuth:
      type: oauth2
      flows:
        authorizationCode:
          authorizationUrl: https://identity.example.com/oauth2/authorize
          tokenUrl: https://identity.example.com/oauth2/token
          scopes:
            orders:read: Read orders
            orders:write: Create and update orders
    apiKeyAuth:
      type: apiKey
      in: header
      name: X-API-Key
security:
  - bearerAuth: []
paths:
  /orders:
    get:
      operationId: listOrders
      security:
        - orderOAuth: [orders:read]
      responses:
        "200":
          description: Orders listed
    post:
      operationId: createOrder
      security:
        - orderOAuth: [orders:write]
      responses:
        "201":
          description: Order created
  /health:
    get:
      operationId: getHealth
      security: []
      responses:
        "200":
          description: Service health is public
```

**Bad example:**

```yaml
paths:
  /orders:
    get:
      responses:
        "200":
          description: Orders listed
# Runtime requires JWTs, but the contract declares no security scheme.
```

### Example 7: Realistic examples and content types

Title: request and response examples, media types, payload fidelity
Description: Document each supported media type explicitly and provide examples that consumers can copy into tests. Examples should include realistic identifiers, timestamps, enum values, and nested payloads that satisfy required fields. Avoid documenting content types the operation does not actually consume or produce.

**Good example:**

```yaml
paths:
  /orders:
    post:
      operationId: createOrder
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/CreateOrderRequest'
            examples:
              bookstoreOrder:
                summary: Two books shipped to a registered customer
                value:
                  customerId: "c7d5fb84-fb68-4b3d-8c32-9dc7f0a1c700"
                  currency: EUR
                  items:
                    - sku: "BOOK-123"
                      quantity: 2
      responses:
        "201":
          description: Order created
          headers:
            Location:
              description: URL of the created order
              schema:
                type: string
                format: uri-reference
              example: "/v2/orders/6f0a6a4b-36b8-4dc9-bc67-8f4a3bfb2d61"
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Order'
              examples:
                created:
                  value:
                    id: "6f0a6a4b-36b8-4dc9-bc67-8f4a3bfb2d61"
                    status: PENDING
                    currency: EUR
                    totalAmount: "42.90"
                    createdAt: "2026-03-18T10:15:30Z"
                    items:
                      - sku: "BOOK-123"
                        quantity: 2
```

**Bad example:**

```yaml
requestBody:
  content:
    '*/*':
      schema:
        type: object
responses:
  "200":
    description: OK
    content:
      application/json:
        example:
          foo: bar
# Wildcard request content and toy examples make generated clients unreliable.
```

### Example 8: Breaking-change and validation gates

Title: additive changes, incompatible changes, linting, pre-codegen checks
Description: Treat the OpenAPI document as a versioned consumer contract. Additive changes, such as new optional fields or new response examples, are usually safer than removed paths, changed status semantics, newly required fields, incompatible enum changes, or schema type changes. Validate the contract in CI with a project-approved linter or validator, compare contract diffs before release, and gate code generation on a valid spec.

**Good example:**

```yaml
# Safer additive evolution
components:
  schemas:
    Order:
      type: object
      required: [id, status, currency, totalAmount, createdAt, items]
      properties:
        id:
          type: string
          format: uuid
        status:
          type: string
          enum: [PENDING, CONFIRMED, CANCELLED]
        trackingNumber:
          type: string
          nullable: true
          description: Optional carrier tracking number, absent until shipment.

# CI gate examples to adapt to the project:
# - validate OpenAPI syntax and schema references
# - run Spectral-style lint rules for operationId, tags, examples, and errors
# - compare against the last released contract for breaking changes
# - run code generation only after validation succeeds
```

**Bad example:**

```yaml
# Breaking evolution unless released as a new major contract
components:
  schemas:
    Order:
      type: object
      required: [id, status, totalAmount, createdAt, items, trackingNumber]
      properties:
        id:
          type: integer
        status:
          type: string
          enum: [CONFIRMED, CANCELLED]
        trackingNumber:
          type: string
paths:
  /orders/{orderId}: {}
# Changed id type, removed PENDING enum value, made trackingNumber required,
# and removed operation details from an existing path.
```


## Output Format

- **REVIEW** the spec (or diff) against structure, completeness, and consumer clarity
- **LIST** concrete issues: missing schemas, vague examples, inconsistent error models, under-specified parameters, security gaps
- **PROPOSE** YAML/JSON snippets or edit lists that fix problems without hand-waving
- **CALL OUT** breaking vs non-breaking changes when evolving paths, schemas, or required fields
- **POINT** to the right framework skill when the user needs server stubs, controller binding, or runtime OpenAPI exposure