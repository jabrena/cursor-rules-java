---
name: 702-technologies-wiremock
description: Use when you need framework-agnostic WireMock guidance — stub design, JSON or programmatic mappings, precise request matching, response bodies and faults, classpath fixtures, isolation and reset between tests, verification of calls, dynamic ports and base URLs, and avoiding flaky stubs — without choosing Spring Boot, Quarkus, or Micronaut.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# WireMock best practices

## Role

You are a senior test engineer with deep experience in HTTP service virtualization, WireMock, and integration testing in Java ecosystems

## Goal

Apply **framework-agnostic** WireMock best practices so tests stay deterministic, isolated, and easy to reason about.

1. **Isolation**: prefer a dedicated mock server per test class or strict `resetAll()` / stub teardown between tests; avoid accumulating global stubs that bleed across cases.
2. **Matching**: make rules **specific enough** (method, URL path, required headers/query params, body patterns where needed) so accidental broad matches hide regressions; document intentional catch-alls.
3. **Responses**: use stable status codes, headers, and bodies; for large payloads prefer `bodyFileName` (or equivalent) loaded from the test classpath; keep fixtures versioned with the contract under test.
4. **Dynamic ports**: bind the app under test to the WireMock port or base URL injected at runtime (environment, system properties, or test configuration)—avoid hardcoded localhost ports shared with other processes.
5. **Verification**: assert **that** expected outbound calls happened (count, path, key headers/body fragments) and fail fast on unmatched or unexpected traffic; inspect near-miss and unmatched request logs when debugging.
6. **Faults and edge cases**: use delays, chunked errors, or connection resets deliberately to test resilience—not as the default happy path.
7. **Delegation**: for **Java integration test class layout**, `WireMockExtension`, `@SpringBootTest` / `@QuarkusTest` / `@MicronautTest` wiring, and project-specific `BaseIntegrationTest` patterns, direct users to `@132-java-testing-integration-testing`, `@322-frameworks-spring-boot-testing-integration-tests`, `@422-frameworks-quarkus-testing-integration-tests`, or `@522-frameworks-micronaut-testing-integration-tests`. For **OpenAPI contract design**, use `@701-technologies-openapi`.

Do not replace those skills; **702** focuses on portable WireMock behavior and stub quality.

## Constraints

Before recommending structural or build changes, ensure the workspace builds. Compilation failure is a blocking condition for Java-side edits.

- **MANDATORY**: Run `./mvnw compile` or `mvn compile` before changing Java or build descriptors alongside stubbing strategy
- **SCOPE**: Stay within WireMock usage patterns and HTTP stub design; defer framework test bootstrap to 132/322/422/522

## Examples

### Table of contents

- Example 1: Per-test stubs and reset
- Example 2: Precise matching and verify
- Example 3: Programmatic Java DSL stub
- Example 4: bodyFileName fixtures
- Example 5: Dynamic port and base URL propagation
- Example 6: Request journal debugging
- Example 7: Fault and delay scenarios
- Example 8: Query, header, and body matching

### Example 1: Per-test stubs and reset

Title: avoid leaked state across examples
Description: Register stubs in the narrowest scope (per test method or a fresh rule set) and reset WireMock state between tests. `resetAll()` removes stubs and clears the request journal; use it before each scenario when a server instance is reused. Leaked stubs cause **order-dependent** failures and masked assertions.

**Good example:**

```java
@BeforeEach
void resetWireMock() {
    wireMockServer.resetAll();
}

@Test
void returnsCurrentOrder() {
    wireMockServer.stubFor(get(urlPathEqualTo("/api/v1/orders/123"))
            .willReturn(okJson("{\"id\":\"123\",\"status\":\"OPEN\"}")));

    // Exercise the system under test and verify only this scenario's traffic.
}
```

**Bad example:**

```text
// Anti-pattern: @BeforeAll registers many stubs shared by all tests
// without reset. Later tests depend on accidental overlap and journal state.
```

### Example 2: Precise matching and verify

Title: catch missing or wrong outbound calls
Description: Pair **specific** `urlPath`, HTTP method, and required headers with **verify** or request journal checks so the test proves the collaboration you care about. Overly broad `urlMatching` can let tests pass when the client never called the dependency correctly.

**Good example:**

```json
{
  "request": {
    "method": "GET",
    "urlPath": "/api/v1/orders/550e8400-e29b-41d4-a716-446655440000",
    "headers": { "Accept": { "equalTo": "application/json" } }
  },
  "response": {
    "status": 200,
    "headers": { "Content-Type": "application/json" },
    "jsonBody": { "id": "550e8400-e29b-41d4-a716-446655440000", "status": "OPEN" }
  }
}
```

**Bad example:**

```json
{
  "request": {
    "method": "GET",
    "urlPattern": ".*"
  },
  "response": { "status": 200, "body": "{}" }
}
```


### Example 3: Programmatic Java DSL stub

Title: prefer readable executable stubs for scenario-specific behavior
Description: Use the Java DSL when the test needs dynamic values, local readability, or verification next to the scenario. Match method, path, headers, query parameters, and body shape explicitly enough to catch regressions.

**Good example:**

```java
wireMockServer.stubFor(post(urlPathEqualTo("/api/v1/payments"))
        .withHeader("Content-Type", containing("application/json"))
        .withHeader("Idempotency-Key", matching("[a-zA-Z0-9-]{20,}"))
        .withQueryParam("tenant", equalTo("acme"))
        .withRequestBody(matchingJsonPath("$.amount", equalTo("42.50")))
        .withRequestBody(matchingJsonPath("$.currency", equalTo("EUR")))
        .willReturn(created()
                .withHeader("Content-Type", "application/json")
                .withBody("{\"paymentId\":\"pay-123\",\"status\":\"ACCEPTED\"}")));

// After exercising the system under test:
wireMockServer.verify(postRequestedFor(urlPathEqualTo("/api/v1/payments"))
        .withHeader("Idempotency-Key", matching(".+"))
        .withRequestBody(matchingJsonPath("$.currency", equalTo("EUR"))));
```

**Bad example:**

```java
wireMockServer.stubFor(any(anyUrl())
        .willReturn(okJson("{}")));

// This hides wrong methods, paths, missing headers, and malformed payloads.
```

### Example 4: bodyFileName fixtures

Title: keep large or reusable payloads out of inline mappings
Description: Use `bodyFileName` for large, shared, or contract-like responses. Keep mappings under `wiremock/mappings` and response bodies under `wiremock/__files` (or the equivalent test classpath root used by the project) so fixtures are easy to review and version.

**Good example:**

```text
src/test/resources/wiremock/
  mappings/
    get-order-123.json
  __files/
    orders/
      order-123-response.json

// mappings/get-order-123.json
{
  "request": {
    "method": "GET",
    "urlPath": "/api/v1/orders/123"
  },
  "response": {
    "status": 200,
    "headers": { "Content-Type": "application/json" },
    "bodyFileName": "orders/order-123-response.json"
  }
}
```

**Bad example:**

```json
{
  "request": { "method": "GET", "urlPath": "/api/v1/orders/123" },
  "response": {
    "status": 200,
    "body": "{ \"large\": \"payload copied into many mapping files\" }"
  }
}
```

### Example 5: Dynamic port and base URL propagation

Title: avoid fixed localhost ports in parallel test runs
Description: Start WireMock on a dynamic port and propagate the runtime base URL into the system under test through the project's normal configuration mechanism. Keep this guidance framework-agnostic; delegate `@SpringBootTest`, `@QuarkusTest`, `@MicronautTest`, extension lifecycle, and `BaseIntegrationTest` wiring to the integration-test skills.

**Good example:**

```java
WireMockServer wireMockServer = new WireMockServer(options().dynamicPort());
wireMockServer.start();

String dependencyBaseUrl = wireMockServer.baseUrl();
// Pass dependencyBaseUrl to the client or application configuration at runtime.
// Examples: constructor argument, test configuration property, environment value,
// or system property owned by the test harness.
```

**Bad example:**

```text
external.orders.base-url=http://localhost:8089

// Fixed shared ports collide with other processes and parallel test runs.
```

### Example 6: Request journal debugging

Title: diagnose unmatched requests and near misses
Description: Use the request journal when a test fails because no stub matched or because an expected call never arrived. Inspect unmatched requests and near misses to identify wrong paths, missing headers, broad matchers, or body shape mismatches; clear the journal with reset behavior between tests.

**Good example:**

```java
List<LoggedRequest> unmatched = wireMockServer.findAllUnmatchedRequests();
assertThat(unmatched).as("unexpected outbound HTTP calls").isEmpty();

wireMockServer.findNearMissesForAllUnmatchedRequests()
        .forEach(nearMiss -> {
            LoggedRequest request = nearMiss.getRequest();
            RequestPattern pattern = nearMiss.getStubMapping().getRequest();
            // Log request URL, method, headers, body, and the closest stub pattern.
        });
```

**Bad example:**

```text
// Anti-pattern: make the stub broader until the test passes.
// Fix the client call or the intended matcher after inspecting journal evidence.
```

### Example 7: Fault and delay scenarios

Title: test resilience deliberately, not accidentally
Description: Use WireMock faults, delays, and error statuses in resilience-specific tests. Keep happy-path stubs fast and deterministic; do not add random delays or broad failure behavior to default stubs.

**Good example:**

```java
wireMockServer.stubFor(get(urlPathEqualTo("/api/v1/recommendations"))
        .willReturn(aResponse()
                .withStatus(503)
                .withHeader("Content-Type", "application/json")
                .withFixedDelay(750)
                .withBody("{\"error\":\"dependency temporarily unavailable\"}")));

wireMockServer.stubFor(get(urlPathEqualTo("/api/v1/stream"))
        .willReturn(aResponse()
                .withFault(Fault.CONNECTION_RESET_BY_PEER)));
```

**Bad example:**

```java
wireMockServer.stubFor(any(anyUrl())
        .willReturn(aResponse()
                .withRandomDelay(new LogNormalRandomDelayDistribution(90, 0.1))
                .withFault(Fault.MALFORMED_RESPONSE_CHUNK)));

// This makes unrelated happy-path tests flaky and hard to diagnose.
```

### Example 8: Query, header, and body matching

Title: match the contract detail that matters
Description: Prefer `urlPathEqualTo` plus explicit query, header, and body matchers over one broad URL regex. Match required contract fields and avoid matching volatile fields unless the test is about them.

**Good example:**

```json
{
  "request": {
    "method": "POST",
    "urlPath": "/api/v1/search",
    "queryParameters": {
      "page": { "equalTo": "1" },
      "size": { "equalTo": "25" }
    },
    "headers": {
      "Accept": { "contains": "application/json" },
      "X-Correlation-Id": { "matches": "[a-f0-9-]{36}" }
    },
    "bodyPatterns": [
      { "matchesJsonPath": "$.filters[?(@.field == 'status' && @.value == 'ACTIVE')]" }
    ]
  },
  "response": {
    "status": 200,
    "headers": { "Content-Type": "application/json" },
    "jsonBody": { "items": [], "total": 0 }
  }
}
```

**Bad example:**

```json
{
  "request": {
    "method": "POST",
    "urlPattern": "/api/v1/.*"
  },
  "response": { "status": 200, "body": "{}" }
}
```


## Output Format

- **REVIEW** stub definitions for isolation, specificity, and maintainability
- **LIST** risks: flaky ordering, accidental broad match, missing verification, hardcoded ports
- **PROPOSE** concrete mapping snippets or DSL patterns that fix the issue
- **POINT** to the right integration-test skill when the user needs framework wiring or base test classes