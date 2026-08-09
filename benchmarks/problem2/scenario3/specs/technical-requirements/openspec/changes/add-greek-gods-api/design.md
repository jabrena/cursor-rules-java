## Context

The service must expose Greek god data through a local REST API while keeping
that data synchronized from a third-party JSON service. The external service
returns names only (`["Zeus", "Hera"]`); the internal API returns database
records with generated identifiers (`[{ "id": 1, "name": "Zeus" }, ...]`).

Source ADRs select Quarkus 3.x with REST, JDBC, PostgreSQL, and a scheduler.
Reads MUST NOT proxy the external JSON server. Synchronization uses `name` as
the natural key so existing rows keep stable IDs.

Functional authority lives in
`../../../../functional-requirements/`; this design records implementation
decisions for the OpenSpec change only.

## Goals / Non-Goals

**Goals:**

- Expose `GET /api/v1/gods/greek` returning a JSON array of `{ "id", "name" }`.
- Persist data in PostgreSQL table `greek_god` matching the source schema.
- Synchronize names periodically from my-json-server `/greek` with upsert-by-name.
- Keep API reads independent from external service availability.
- Provide deterministic Quarkus tests (REST Assured for HTTP; stubbed upstream for sync).

**Non-Goals:**

- Authentication, rate limiting, caching, or circuit breakers.
- Returning a bare array of strings from the public API.
- Proxying the external JSON server on read requests.
- Switching to Micronaut or Spring Boot for this scenario.

## Decisions

### REST API Boundary

Expose `GET /api/v1/gods/greek` under path-based API versioning. Successful
responses return `200` with `application/json` and a JSON array of Greek god
records. Unexpected server failures return `500`. No request authentication is
required for this endpoint.

The public OpenAPI contract is
`../../../../functional-requirements/design/greekController-oas.yaml`.

### Persistence Model

Use PostgreSQL table `greek_god`:

```sql
CREATE TABLE IF NOT EXISTS greek_god (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);
```

Access the table through Quarkus JDBC. Apply schema via SQL migration matching
`../../../../functional-requirements/design/schema.sql`. Repository queries for
reads use `SELECT id, name FROM greek_god`.

### Background Synchronization

A scheduled God Updater:

1. Calls `GET https://my-json-server.typicode.com/jabrena/latency-problems/greek`.
2. Parses a JSON array of strings.
3. Upserts into `greek_god` by unique `name`.
4. Inserts new names with generated IDs; retains existing IDs for known names.

The external OpenAPI contract is
`../../../../functional-requirements/design/my-json-server-oas.yaml`.
Sequence detail is in
`../../../../functional-requirements/design/greek_gods_api_sequence_diagram.puml`.

### Runtime Stack

Adopt Quarkus 3.x as documented in ADR-003:

| Concern | Choice |
| --- | --- |
| Runtime | Quarkus 3.x |
| Public API | Quarkus REST |
| Persistence | Quarkus JDBC + PostgreSQL |
| Sync trigger | Quarkus scheduler |
| HTTP acceptance tests | REST Assured |
| Sync upstream in tests | Stubbed / fake external service |

### Testing Strategy

- Acceptance tests map to
  `../../../../functional-requirements/agile/US-001_api_greek_gods_data_retrieval.feature`.
- HTTP tests assert `200`, JSON content type, array shape, and integer `id` /
  string `name` on each item, including a known Zeus record when seeded.
- Sync tests assert string-array upstream payload, name-based upsert, and
  stable IDs without depending on the live external server.

### Alternatives Considered

| Option | Assessment |
| --- | --- |
| Quarkus + JDBC + PostgreSQL | Selected; matches sequence diagram and ADR-003. |
| Micronaut stack | Rejected; conflicts with the Quarkus repository model in the source artifacts. |
| Direct external proxy on read | Rejected; reads must use synchronized local data. |
| Public API returns only strings | Rejected; contract and acceptance criteria require `id` and `name`. |

## Risks / Trade-offs

- [External freshness depends on scheduler] → Document sync cadence in
  configuration; acceptance focuses on upsert rules, not live freshness SLAs.
- [Live my-json-server flakiness in tests] → Stub the upstream for sync tests.
- [Duplicate names from upstream] → Unique constraint on `name` prevents duplicates;
  upsert matches existing rows.

## Migration Plan

Greenfield demo module under `benchmarks/problem2/scenario3/demo/`. Apply V1
migration creating `greek_god`, then enable scheduler and REST resource. Rollback
is removing the demo implementation and restoring `demo/.gitkeep` for the
benchmark harness.

## Open Questions

None. Source ADRs and the implementation plan resolve stack, contract, schema,
and test approach for this change.
