# ADR-002: Acceptance Testing Strategy for Greek Gods API

**Date:** December 19, 2024  
**Status:** Accepted  
**Deciders:** Development Team, QA Team  
**Technical Story:** [US-001](../agile/US-001_API_Greek_Gods_Data_Retrieval.md)

---

## Context and Problem Statement

The Greek Gods API must prove that it returns the public contract described by
[ADR-001](./ADR-001_REST_API_Functional_Requirements.md): a JSON array of
records, each containing `id` and `name`. Acceptance tests should exercise the
HTTP endpoint the way API consumers use it and should cover synchronization
rules where the external service returns an array of names.

---

## Decision Drivers

- Validate the documented API shape, not implementation internals.
- Keep Gherkin acceptance criteria aligned with automated tests.
- Verify stable database IDs across synchronization.
- Avoid depending on the live external JSON server in repeatable tests.

---

## Decision Outcome

Use Quarkus integration tests for the HTTP API and synchronization behavior.
REST Assured is suitable for black-box HTTP assertions in Quarkus tests. Stub
or fake the external JSON server when validating synchronization so tests remain
deterministic.

---

## Test Categories and Tags

Tags align with
[US-001_api_greek_gods_data_retrieval.feature](../agile/US-001_api_greek_gods_data_retrieval.feature):

```java
@Tag("smoke")
@Tag("happy-path")
@Tag("data-quality")
```

---

## Acceptance Layer

HTTP-level tests must validate:

- `GET /api/v1/gods/greek` returns `200 OK`.
- The response `Content-Type` is JSON.
- The response body is an array.
- Each array item contains `id` as an integer and `name` as a string.
- A known synchronized record can be found, for example `{ "id": 1, "name": "Zeus" }`.

---

## Synchronization Layer

Synchronization tests must validate:

- The external service contract is a JSON array of strings.
- New external names are inserted into `greek_god`.
- Existing names are matched by `name`.
- Existing rows keep their IDs after a sync run.
- Duplicate names are not inserted.

---

## Test Data Management

- Use database setup or migrations that match [schema.sql](./schema.sql).
- Seed only the records needed by a scenario.
- Prefer a stubbed upstream response for sync tests.
- Keep assertions focused on the public contract and identity rules.

---

## Success Metrics

- Every scenario in the Gherkin feature has automated coverage.
- Public API tests assert record objects with `id` and `name`.
- Sync tests assert `name`-based upsert behavior and stable IDs.
- OpenAPI validation, if present, uses [greekController-oas.yaml](./greekController-oas.yaml).

---

## Related Decisions

- [ADR-001: REST API Functional Requirements](./ADR-001_REST_API_Functional_Requirements.md)
- [ADR-003: Technology Stack](./ADR-003-Greek-Gods-API-Technology-Stack.md)

---

**Last Updated:** August 9, 2026  
**Review Trigger:** API contract, persistence, or synchronization behavior changes
