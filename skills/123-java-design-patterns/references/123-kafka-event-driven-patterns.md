---
name: 123-kafka-event-driven-patterns
description: Use when designing or reviewing Kafka and event-driven patterns — event schemas, partitioning keys, consumer groups, idempotent consumers, retry topics, dead-letter topics, outbox, CDC, sagas, CQRS projections, and event sourcing.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Java Design and Integration Patterns

## Role

You are a Senior software engineer with extensive experience in Kafka, event-driven architecture, and Java messaging systems

## Goal

Design Kafka and event-driven systems around reliable delivery, stable event contracts, explicit ordering boundaries, replay safety, and observable failure handling.

### Pattern selection principles

1. **Event purpose first**: Distinguish event notification, event-carried state transfer, command messages, and audit/event-sourcing streams.
2. **Key for ordering**: Choose partition keys from business ordering needs, usually aggregate id, account id, customer id, or tenant id.
3. **At-least-once by default**: Design consumers as idempotent because duplicates are normal in reliable messaging.
4. **Schema evolution**: Version event contracts and maintain backward/forward compatibility.
5. **Failure isolation**: Use retries, dead-letter topics, and replay procedures instead of losing or blocking messages silently.

### Pattern selection matrix

| Design pressure | Candidate pattern | Use when | Avoid when | Validation signal |
|---|---|---|---|---|
| Consumer needs local autonomy | Event-carried state transfer | Consumers need enough state to build projections without synchronous calls | Event payload would become an internal entity dump | Replay rebuilds the projection from event payloads |
| Database update and publish must agree | Transactional outbox | A local transaction changes state and must publish reliably | No local database transaction exists or losing the signal is acceptable | Crash tests leave either no state change or a publishable outbox row |
| Ordering matters per business entity | Partition key selection | Records for one aggregate/customer/account must be processed in order | Key would create a hot partition or ordering is irrelevant | Tests and metrics confirm per-key order and partition balance |
| At-least-once delivery creates duplicates | Idempotent consumer or inbox | Consumer side effects must run once per event id | Consumer is read-only and naturally idempotent | Duplicate-delivery tests do not duplicate state changes |
| Poison records block progress | Retry topic and DLT | Transient failures should retry while permanent failures are isolated | Failure can be handled inline without blocking or data loss | Tests cover retries, DLT payload, alerts, and replay procedure |
| Event contracts evolve | Schema versioning | Producers and consumers deploy independently | One service owns both ends and deploys atomically | Compatibility checks pass for old and new schemas |

## Constraints

Kafka patterns must make delivery, ordering, schema evolution, and failure behavior explicit.

- **IDEMPOTENCY REQUIRED**: Consumers that change state must handle duplicate records safely
- **KEY INTENTIONALLY**: Select partition keys based on ordering and load distribution, not convenience
- **NO HIDDEN SCHEMAS**: Events must have explicit names, versions, and compatibility rules
- **FAILURE PATHS**: Define retry, DLT, alerting, and replay behavior for poison records

## Examples

### Table of contents

- Example 1: Use Event-Carried State Transfer for Local Read Models
- Example 2: Use Transactional Outbox for Reliable Publishing
- Example 3: Choose Partition Key from Ordering Needs
- Example 4: Use Idempotent Consumer or Inbox for Side Effects
- Example 5: Use Retry Topics and DLT for Failure Isolation

### Example 1: Use Event-Carried State Transfer for Local Read Models

Title: Publish enough state for consumers to update their own views
Description: Use this pattern when consumers need autonomous local projections. Benefit: consumers can update local read models without synchronous lookups. Cost: payload design and compatibility become producer responsibilities. Avoid publishing entire internal entities or fields without consumer value. Validate replay by rebuilding a projection from retained events.

**Good example:**

```json
{
  "eventId": "evt-1001",
  "eventType": "OrderConfirmed",
  "eventVersion": 1,
  "occurredAt": "2026-06-01T10:15:30Z",
  "orderId": "ord-123",
  "customerId": "cus-456",
  "total": {
    "amount": "49.95",
    "currency": "EUR"
  }
}
```

**Bad example:**

```json
{
  "eventType": "OrderChanged",
  "payload": {
    "entityDump": "...all database columns, including internal flags..."
  }
}
```

### Example 2: Use Transactional Outbox for Reliable Publishing

Title: Persist domain state and outgoing events atomically
Description: Use outbox when a service updates a database and must publish an event without a dual-write race. Benefit: state and outgoing event are committed atomically. Cost: relay lag, duplicate publish handling, and outbox cleanup must be operated. Avoid it for pure message transformations with no local transaction. Validate crash recovery, duplicate relay sends, and unpublished row alerts.

**Good example:**

```text
Transaction:
  1. Update orders table: status = CONFIRMED
  2. Insert outbox row: event_type = OrderConfirmed, aggregate_id = ord-123
  3. Commit

Relay:
  4. Read unpublished outbox rows
  5. Publish to Kafka with key = aggregate_id
  6. Mark row published or let CDC advance the offset
```

**Bad example:**

```text
1. Update orders table and commit
2. Publish OrderConfirmed to Kafka
3. If the process crashes between steps, the database changed but no event exists
```

### Example 3: Choose Partition Key from Ordering Needs

Title: Key records by the smallest business scope that must stay ordered
Description: Use explicit partition key selection when consumers require ordered processing for an aggregate, account, customer, or tenant. Benefit: Kafka preserves order within that key. Cost: uneven keys can create hot partitions. Avoid keying by convenience values that do not express ordering. Validate per-key ordering tests and partition distribution metrics.

**Good example:**

```text
Topic: order-events
Key: orderId

ord-123 -> OrderCreated
ord-123 -> OrderConfirmed
ord-123 -> OrderShipped

Ordering guarantee: all records for ord-123 are processed in partition order.
```

**Bad example:**

```text
Topic: order-events
Key: eventType

OrderCreated -> many unrelated orders in one hot partition
OrderConfirmed -> confirmations separated from each order's creation event
```

### Example 4: Use Idempotent Consumer or Inbox for Side Effects

Title: Record processed event ids before applying non-repeatable changes
Description: Use an idempotent consumer or inbox when duplicate Kafka delivery can repeat state changes, emails, payments, or integration calls. Benefit: at-least-once delivery becomes safe. Cost: processed-message storage and retention must be managed. Avoid it for read-only consumers whose writes are naturally idempotent. Validate by delivering the same event id twice and checking one side effect.

**Good example:**

```sql
BEGIN;

INSERT INTO processed_events(event_id, consumer_name, processed_at)
VALUES (?, 'invoice-projector', CURRENT_TIMESTAMP);

UPDATE invoice_summary
   SET paid_total = paid_total + ?
 WHERE customer_id = ?;

COMMIT;

-- Duplicate event id violates the processed_events key and skips the update.
```

**Bad example:**

```sql
UPDATE invoice_summary
   SET paid_total = paid_total + ?
 WHERE customer_id = ?;

-- A duplicate event increments the total again.
```

### Example 5: Use Retry Topics and DLT for Failure Isolation

Title: Separate transient retry from poison-record investigation
Description: Use retry topics and dead-letter topics when failures should not block a partition forever or disappear silently. Benefit: transient failures get time to recover and permanent failures become inspectable. Cost: retry ordering, duplicate handling, alerts, and replay tools are required. Avoid it when the consumer can reject invalid records before Kafka processing. Validate retry count, DLT headers, alerting, and documented replay.

**Good example:**

```text
order-events
  -> order-events.retry.1m
  -> order-events.retry.10m
  -> order-events.dlt

DLT record headers:
  original-topic=order-events
  original-partition=3
  exception-class=InvalidAddressException
  correlation-id=corr-123
```

**Bad example:**

```text
while (true) {
  consume(record);
  // same poison record is retried immediately forever
  // no alert, no DLT, no replay instruction
}
```

## Output Format

- **DESIGN** event name, payload, schema version, topic, key, headers, and consumer-group boundaries
- **DEFINE** idempotency, retries, DLT, replay, ordering, and schema evolution behavior
- **APPLY** outbox, inbox, saga, projection, or event-sourcing patterns only when their reliability or audit value is needed
- **TEST** with realistic broker behavior, duplicate delivery, poison messages, and schema compatibility checks