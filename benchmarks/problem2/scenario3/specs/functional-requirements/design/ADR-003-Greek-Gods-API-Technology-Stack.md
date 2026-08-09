# ADR-003: Greek Gods API Technology Stack

**Status:** Accepted  
**Date:** August 9, 2026  
**Deciders:** Development Team  
**Traceability:** [US-001](../agile/US-001_API_Greek_Gods_Data_Retrieval.md)

---

## Decision

Adopt a Quarkus 3.x stack for the Greek Gods API:

- **Runtime:** Quarkus 3.x.
- **Public API:** `GET /api/v1/gods/greek`, returning a JSON array of Greek god
  records with `id` and `name`.
- **Persistence:** PostgreSQL table `greek_god`, accessed through JDBC.
- **Schema Management:** SQL migration matching [schema.sql](./schema.sql).
- **Synchronization:** Scheduled background updater calls the external
  my-json-server `/greek` endpoint and upserts records by `name`.
- **API Documentation:** OpenAPI 3.0.3 aligned with
  [greekController-oas.yaml](./greekController-oas.yaml).
- **Testing:** JUnit 5, Quarkus test support, REST Assured for HTTP acceptance
  tests, and a stubbed external service for deterministic sync tests.

---

## Context

The scenario 1 functional specification describes a microservice with a REST
API, Greek God service, Quarkus JDBC repository, PostgreSQL database, and a God
Updater. Scenario 2 requirements must preserve that contract instead of
switching to another example stack or returning a different payload shape.

---

## Decision Drivers

- Match the sequence diagram and scenario 1 source artifacts.
- Keep the database schema and API payload aligned.
- Use the external JSON service only as a synchronization source.
- Keep tests close to the HTTP boundary while allowing deterministic upstream
  behavior.

---

## Considered Options

| Option | Assessment |
|--------|------------|
| Quarkus + JDBC + PostgreSQL | Selected. Matches the scenario source diagram and benchmark intent. |
| Micronaut stack | Rejected for this scenario because it came from a different example and conflicts with the scenario 1 Quarkus repository model. |
| Direct external proxy | Rejected because read requests must use synchronized local data. |
| Public API returns only strings | Rejected because the public contract requires records with `id` and `name`. |

---

## Consequences

### Positive

- Scenario 2 requirements now align with scenario 1 artifacts.
- The implementation has a clear boundary between read API and sync source.
- Stable IDs are preserved for API consumers.

### Negative

- A local database and synchronization job are required.
- Upstream data freshness depends on the scheduler.

---

## References

- [ADR-001: REST API Functional Requirements](./ADR-001_REST_API_Functional_Requirements.md)
- [ADR-002: Acceptance Testing Strategy](./ADR-002-Acceptance-Testing-Strategy.md)
- [US-001: API Greek Gods Data Retrieval](../agile/US-001_API_Greek_Gods_Data_Retrieval.md)
- [US-001 acceptance criteria](../agile/US-001_api_greek_gods_data_retrieval.feature)
- [Public API OpenAPI](./greekController-oas.yaml)
- [External JSON Server OpenAPI](./my-json-server-oas.yaml)
