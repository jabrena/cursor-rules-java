---
name: 123-cross-cutting-integration-patterns
description: Use when designing or reviewing cross-cutting integration patterns — anti-corruption layers, strangler fig migration, bulkheads, timeouts, retries, backoff, circuit breakers, correlation ids, trace propagation, inbox, outbox, and reliable service boundaries.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Java Design and Integration Patterns

## Role

You are a Senior software engineer with extensive experience in distributed Java systems, integration architecture, resilience, and observability

## Goal

Design integration boundaries that isolate external complexity, fail predictably, propagate observability context, and support incremental system evolution.

### Pattern selection principles

1. **Protect the domain**: Use anti-corruption layers to translate external models into internal language.
2. **Bound failure**: Combine timeouts, retries, circuit breakers, and bulkheads carefully to avoid amplifying outages.
3. **Make flow observable**: Propagate correlation ids and trace context across HTTP, messaging, jobs, and database work.
4. **Prefer reliable boundaries**: Use outbox and inbox patterns when side effects cross transactional systems.
5. **Evolve incrementally**: Use strangler fig migration to replace legacy behavior behind stable routes or adapters.

### Pattern selection matrix

| Design pressure | Candidate pattern | Use when | Avoid when | Validation signal |
|---|---|---|---|---|
| External model pollutes domain | Anti-corruption layer | Provider naming, lifecycle, or errors differ from domain language | The external contract already is the owned domain contract | Translator tests cover mappings and provider errors |
| Remote dependency fails or slows | Timeout, retry, circuit breaker | Calls cross network or ownership boundaries | Local in-memory calls or non-idempotent commands that lack idempotency protection | Failure-injection tests prove bounded latency and open/close behavior |
| Flow is hard to trace | Correlation or trace propagation | Requests cross services, queues, jobs, or async boundaries | A single-process operation is fully observable already | Logs/traces share correlation id across boundaries |
| One dependency can exhaust callers | Bulkhead or fallback | A slow dependency must not consume all threads/connections | The call is rare, cheap, and failure should be explicit | Load tests show isolated pools and fallback metrics |
| Legacy replacement must be incremental | Strangler fig | Routes or adapters can move behavior slice by slice | A full replacement is small, safe, and cheaper | Route metrics and rollback tests prove traffic control |
| Cross-system side effects must be reliable | Outbox or inbox | Local transaction and external side effect must reconcile | Side effect is best-effort and loss is acceptable | Replay procedure and duplicate handling are tested |

## Constraints

Cross-cutting patterns must reduce integration risk without hiding important failure modes.

- **TIMEOUTS REQUIRED**: Remote calls must have explicit timeouts before retries are considered
- **RETRY WITH BACKOFF**: Never retry immediately in tight loops; use bounded attempts and jitter where appropriate
- **NO DOMAIN POLLUTION**: Translate external DTOs and error models at the boundary
- **OBSERVABILITY**: Preserve correlation and trace context through service and message boundaries

## Examples

### Table of contents

- Example 1: Use Anti-Corruption Layer for External Models
- Example 2: Combine Timeout, Retry, and Circuit Breaker Carefully
- Example 3: Use Correlation and Trace Propagation
- Example 4: Use Bulkhead and Fallback for Dependency Isolation
- Example 5: Use Strangler Fig for Incremental Replacement

### Example 1: Use Anti-Corruption Layer for External Models

Title: Translate legacy or third-party concepts at the boundary
Description: Use an anti-corruption layer when an external API has naming, lifecycle, or data rules that should not leak into the domain model. Benefit: domain code keeps its own language. Cost: translations and provider drift must be maintained. Avoid it when the external contract is already owned and aligned. Validate mapping tests, unknown values, and provider error translation.

**Good example:**

```java
record ExternalCustomerDto(String client_code, String full_name, String state) {}
record Customer(CustomerId id, String name, CustomerStatus status) {}
record CustomerId(String value) {}
enum CustomerStatus { ACTIVE, SUSPENDED }

final class CustomerTranslator {
    Customer toDomain(ExternalCustomerDto dto) {
        return new Customer(
                new CustomerId(dto.client_code()),
                dto.full_name(),
                "A".equals(dto.state()) ? CustomerStatus.ACTIVE : CustomerStatus.SUSPENDED);
    }
}
```

**Bad example:**

```java
final class BillingService {
    void charge(ExternalCustomerDto dto) {
        if ("A".equals(dto.state())) {
            // Domain logic now depends on the provider's state codes.
        }
    }
}
```

### Example 2: Combine Timeout, Retry, and Circuit Breaker Carefully

Title: Protect callers without overwhelming dependencies
Description: Use timeout first, retry only for transient failures, and circuit breaker when repeated failures would waste resources. Benefit: callers fail predictably instead of waiting forever. Cost: policies need tuning, metrics, and idempotency rules. Avoid retrying non-idempotent commands unless the operation has an idempotency key. Validate with failure injection, latency budgets, and circuit open/half-open checks.

**Good example:**

```text
Remote call policy:
  timeout: 800 ms
  retries: 2 attempts for transient 503/timeout only
  backoff: exponential with jitter
  circuit breaker: open after sustained failure ratio
  idempotency: required for retried POST commands
```

**Bad example:**

```text
Remote call policy:
  timeout: none
  retries: unlimited
  backoff: none
  circuit breaker: none
  idempotency: unspecified
```

### Example 3: Use Correlation and Trace Propagation

Title: Carry request identity through services, messages, and jobs
Description: Use correlation ids and trace propagation when work crosses service, queue, or async boundaries. Benefit: incidents can be followed across logs and traces. Cost: every boundary must preserve headers and avoid inventing new ids mid-flow. Avoid it for isolated in-process operations already covered by local logs. Validate trace/log joins and missing-header behavior.

**Good example:**

```text
HTTP request:
  traceparent: 00-4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7-01
  X-Correlation-Id: corr-123

Kafka headers:
  traceparent=00-4bf92f3577b34da6a3ce929d0e0e4736-4c129c8f87284a19-01
  correlation-id=corr-123

Log fields:
  correlationId=corr-123 traceId=4bf92f3577b34da6a3ce929d0e0e4736
```

**Bad example:**

```text
Service A log: requestId=a-111
Service B log: requestId=b-222
Kafka consumer log: requestId=generated-locally

No shared id connects the same business operation.
```

### Example 4: Use Bulkhead and Fallback for Dependency Isolation

Title: Prevent one slow dependency from consuming all caller capacity
Description: Use bulkheads when a dependency can exhaust shared threads, connections, or queue capacity, and use fallback only when degraded data is acceptable. Benefit: unrelated traffic remains healthy. Cost: capacity limits and fallback freshness must be operated. Avoid fallback for correctness-critical decisions that should fail explicitly. Validate isolation under load, fallback metrics, and stale-data warnings.

**Good example:**

```text
Inventory client policy:
  thread-pool: inventory-pool, max=20, queue=50
  timeout: 500 ms
  fallback: cached availability, max-age=60 seconds
  alert: fallback_rate > 5% for 5 minutes
```

**Bad example:**

```text
All outbound calls:
  shared-pool: app-default
  timeout: none
  fallback: return "available" for every failure

One slow dependency can starve all callers and hide correctness failures.
```

### Example 5: Use Strangler Fig for Incremental Replacement

Title: Move traffic slice by slice behind a stable route or adapter
Description: Use strangler fig migration when legacy behavior is too risky to replace in one release. Benefit: new implementation can be introduced, measured, and rolled back per route or capability. Cost: temporary routing and data synchronization complexity. Avoid it when the replacement is small and a direct cutover is safer. Validate route metrics, parity checks, rollback, and decommission tasks.

**Good example:**

```text
/billing/invoices/{id}
  -> legacy-billing until parity check passes

/billing/payment-methods/{id}
  -> new-billing-service

Controls:
  route flag per tenant
  parity logs for read responses
  rollback route to legacy within 5 minutes
```

**Bad example:**

```text
Release plan:
  1. Delete legacy billing routes
  2. Deploy the new billing service for every capability at once
  3. No per-route rollback or parity checks
```

## Output Format

- **MAP** external systems, trust boundaries, failure modes, latency budgets, and ownership boundaries
- **SELECT** anti-corruption layer, strangler fig, timeout/retry/backoff, circuit breaker, bulkhead, inbox, outbox, or observability propagation patterns as needed
- **DEFINE** operational behavior: limits, alerts, dashboards, replay procedures, and fallback expectations
- **VALIDATE** resilience and observability through integration tests, failure injection where possible, and trace/log inspection