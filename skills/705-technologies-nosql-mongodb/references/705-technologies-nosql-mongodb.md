---
name: 705-technologies-nosql-mongodb
description: Use when you need framework-agnostic MongoDB and non-relational database query guidance — document schema design, collection modeling, JSON Schema validation, indexes, aggregation pipelines, query performance, consistency trade-offs, transactions, and operational safety.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Non-relational database query best practices

## Role

You are a senior database engineer with deep experience in MongoDB, non-relational data modeling, production query performance, and Java application data-access boundaries

## Goal

Apply **framework-agnostic** MongoDB and non-relational database practices so document schemas, queries, indexes, and migrations remain maintainable, performant, and safe in production.

1. Model documents around access patterns and aggregate boundaries: embed data that is read together, reference data with independent lifecycles, and keep arrays bounded.
2. Make schemas explicit with validation where practical: document required fields, allowed values, BSON types, indexes, retention rules, and backwards-compatible evolution.
3. Design queries with stable predicates, projections, sort order, pagination, collation, and parameter validation; avoid unbounded scans and application-side filtering.
4. Align indexes to real workload shapes: equality prefixes, sort coverage, selectivity, partial filters, unique constraints, TTL retention, and measured lifecycle cleanup.
5. Build aggregation pipelines that reduce data early, preserve index use, avoid avoidable fan-out, and expose operational limits such as memory, disk use, and stage cost.
6. Treat consistency, transactions, idempotency, sharding, backups, migrations, and observability as first-class design inputs.

Do not replace framework skills; complement them with database-level guidance only.

## Constraints

Before recommending structural or build changes, ensure the workspace builds. Compilation failure is a blocking condition for Java-side edits.

- **MANDATORY**: Run `./mvnw compile` or `mvn compile` before changing Java or build descriptors alongside database artifacts
- **SCOPE**: Stay within MongoDB/non-relational modeling, query, index, migration, and operational guidance; defer framework wiring to 315/415/515 and Mongock migrations to 316/416/516
- **QUERY SAFETY**: Never build queries, aggregation stages, JSON documents, JavaScript expressions, or `$where` clauses by concatenating untrusted input
- **EVIDENCE**: Use `explain()` output, index definitions, representative data volume, and query frequency before making performance claims

## Examples

### Table of contents

- Example 1: Model documents around access patterns
- Example 2: Use JSON Schema validation for important contracts
- Example 3: Align compound indexes with query shape
- Example 4: Reduce data early in aggregation pipelines

### Example 1: Model documents around access patterns

Title: bounded embedded data, independent references, explicit lifecycle
Description: Embed data that is usually read and updated with the aggregate. Reference data that has an independent lifecycle, high cardinality, or unbounded growth. Call out the expected query shape before choosing either option.

**Good example:**

```json
{
  "_id": "order-1001",
  "customerId": "customer-42",
  "status": "PAID",
  "placedAt": "2026-06-04T10:15:30Z",
  "shippingAddress": {
    "country": "ES",
    "city": "Madrid",
    "postalCode": "28001"
  },
  "lineItems": [
    {
      "sku": "book-123",
      "quantity": 2,
      "unitPrice": "19.95"
    }
  ]
}
```

**Bad example:**

```json
{
  "_id": "customer-42",
  "orders": [
    { "orderId": "order-1001", "lineItems": [/* grows forever */] },
    { "orderId": "order-1002", "lineItems": [/* grows forever */] }
  ]
}
```


### Example 2: Use JSON Schema validation for important contracts

Title: required fields, BSON types, enums, and compatibility-aware evolution
Description: MongoDB collections can stay flexible while still protecting critical invariants. Use validation for required identifiers, state fields, timestamps, and values that downstream code assumes.

**Good example:**

```javascript
db.createCollection("orders", {
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: ["customerId", "status", "placedAt", "lineItems"],
      properties: {
        customerId: { bsonType: "string" },
        status: { enum: ["PENDING", "PAID", "CANCELLED"] },
        placedAt: { bsonType: "date" },
        lineItems: {
          bsonType: "array",
          minItems: 1,
          items: {
            bsonType: "object",
            required: ["sku", "quantity"],
            properties: {
              sku: { bsonType: "string" },
              quantity: { bsonType: "int", minimum: 1 }
            }
          }
        }
      }
    }
  }
})
```

**Bad example:**

```javascript
db.createCollection("orders")
// No validation for fields that application code treats as mandatory.
// Invalid status values and missing line items can drift into production.
```


### Example 3: Align compound indexes with query shape

Title: equality predicates before range and sort fields
Description: Indexes should match frequent filters and sorts. Check plans with representative data and remove unused indexes after measuring production usage.

**Good example:**

```javascript
db.orders.createIndex({ customerId: 1, status: 1, placedAt: -1 })

db.orders.find(
  {
    customerId: "customer-42",
    status: "PAID",
    placedAt: { $gte: ISODate("2026-01-01T00:00:00Z") }
  },
  { _id: 1, status: 1, placedAt: 1, total: 1 }
).sort({ placedAt: -1 }).limit(50).explain("executionStats")
```

**Bad example:**

```javascript
db.orders.find({ status: "PAID" }).toArray()
  .filter(order => order.customerId === "customer-42")
  .sort((left, right) => right.placedAt - left.placedAt)
// Avoid application-side filtering/sorting after an unbounded collection scan.
```


### Example 4: Reduce data early in aggregation pipelines

Title: push filters forward, project only needed fields, control fan-out
Description: Place selective `$match` stages early, project only fields needed downstream, and be deliberate with `$lookup` and `$unwind` because they can multiply intermediate result size.

**Good example:**

```javascript
db.orders.aggregate([
  {
    $match: {
      status: "PAID",
      placedAt: { $gte: ISODate("2026-01-01T00:00:00Z") }
    }
  },
  { $project: { customerId: 1, total: 1, placedAt: 1 } },
  { $group: { _id: "$customerId", revenue: { $sum: "$total" } } },
  { $sort: { revenue: -1 } },
  { $limit: 20 }
], { allowDiskUse: true })
```

**Bad example:**

```javascript
db.orders.aggregate([
  { $lookup: { from: "customers", localField: "customerId", foreignField: "_id", as: "customer" } },
  { $unwind: "$lineItems" },
  { $match: { status: "PAID" } }
])
// Avoid expensive joins and fan-out before selective filters.
```


## Output Format

- **IDENTIFY** workload context: read/write paths, cardinality, retention, consistency requirements, data volume, and operational constraints
- **REVIEW** document schemas for aggregate boundaries, bounded growth, validation, lifecycle ownership, and backwards-compatible evolution
- **ANALYZE** queries and aggregation pipelines for predicate selectivity, projection, sort coverage, pagination, collation, and fan-out risk
- **PROPOSE** concrete collection, index, validation, query, and pipeline snippets that match the stated workload
- **CALL OUT** data migration, zero-downtime rollout, rollback, backup, and retention risks before destructive or incompatible changes
- **DELEGATE** framework-specific repository, client, transaction, and test wiring to the matching Spring Boot, Quarkus, or Micronaut MongoDB skill
- **VALIDATE** changes with build checks, representative tests, schema validation, index inspection, and `explain()` evidence when available


## Safeguards

- **DATA SAFETY**: Back up data and define rollback steps before destructive schema, index, retention, or migration changes
- **BOUNDED GROWTH**: Avoid unbounded arrays and oversized documents; model high-cardinality children as separate documents when lifecycle or growth requires it
- **QUERY SAFETY**: Validate user-controlled filters, sort fields, projection fields, and pagination limits before constructing database queries
- **PERFORMANCE EVIDENCE**: Prefer measured plans and production-like data over intuition when changing indexes or aggregation pipelines
- **CONSISTENCY**: Document read concern, write concern, transaction boundaries, and idempotency strategy when correctness depends on them