# ADR-001: REST API Functional Requirements for Greek Gods Data Synchronization

**Date:** June 3, 2025  
**Status:** Proposed  
**Deciders:** Juan Antonio Breña Moral, Development Team  
**Technical Story:** [US-001](../agile/US-001_API_Greek_Gods_Data_Retrieval.md)

---

## Context and Problem Statement

The service must expose Greek god data through a local REST API while keeping
that data synchronized from a third-party JSON service. The external service
returns names only:

```json
["Zeus", "Hera"]
```

The internal API returns database records with generated identifiers:

```json
[
  { "id": 1, "name": "Zeus" },
  { "id": 2, "name": "Hera" }
]
```

Use `name` as the natural key during synchronization. Existing rows keep their
current `id`; new names are inserted with generated IDs.

---

## Decision Drivers

- Keep API reads independent from external service availability.
- Preserve stable internal identifiers for already synchronized gods.
- Keep the public REST contract simple and documented with OpenAPI 3.0.3.
- Keep the database schema minimal: `id` plus unique `name`.

---

## Functional Requirements

### 1. Greek Gods Data Retrieval

**Primary Endpoint:** `GET /api/v1/gods/greek`

**Functional Specification:**

- **Purpose:** Retrieve all synchronized Greek gods.
- **Response Format:** JSON array of objects.
- **Record Shape:** Each object has `id` as an integer and `name` as a string.
- **Data Source:** PostgreSQL table `greek_god`.

**Response Structure:**

```json
[
  { "id": 1, "name": "Zeus" },
  { "id": 2, "name": "Hera" }
]
```

### 2. Error Handling and Response Standards

**Success Response:**

- **HTTP Status:** 200 OK
- **Content-Type:** `application/json`
- **Body:** JSON array of Greek god records

**Error Response:**

- **HTTP Status:** 500 Internal Server Error for unexpected server failures

### 3. Data Persistence

- **Storage:** PostgreSQL database.
- **Table:** `greek_god`.
- **Columns:** `id` generated primary key, `name` unique and required.
- **Integrity Rule:** No duplicate names.

### 4. Data Synchronization

- **Source:** `https://my-json-server.typicode.com/jabrena/latency-problems/greek`
- **Source Payload:** JSON array of strings.
- **Method:** Periodic background synchronization.
- **Upsert Key:** `name`.
- **Identifier Stability:** Existing rows retain IDs; inserted rows receive generated IDs.

---

## Considered Alternatives

### Alternative 1: Return Only an Array of Names

**Decision:** Rejected. The internal database contract maps names to stable
records with generated identifiers, and scenario acceptance criteria verify
both `id` and `name`.

### Alternative 2: Direct External API Proxy

**Decision:** Rejected. Reads must use the synchronized local database so the
public API is not dependent on external service latency or availability.

---

## Consequences

### Positive Consequences

- Consumers receive stable IDs and names.
- Synchronization can add new names without changing existing record identity.
- The public API contract matches the database schema and acceptance tests.

### Negative Consequences

- The service must maintain a database and synchronization process.
- Data changes in the external source are visible only after synchronization.

---

## Related Decisions

| ADR | File | Purpose |
|-----|------|---------|
| ADR-002 | [ADR-002-Acceptance-Testing-Strategy.md](./ADR-002-Acceptance-Testing-Strategy.md) | Acceptance and integration testing strategy. |
| ADR-003 | [ADR-003-Greek-Gods-API-Technology-Stack.md](./ADR-003-Greek-Gods-API-Technology-Stack.md) | Runtime stack and technology choices. |

### Referenced Artifacts

- **User Story:** [US-001 API Greek Gods Data Retrieval](../agile/US-001_API_Greek_Gods_Data_Retrieval.md)
- **Acceptance Criteria:** [US-001_api_greek_gods_data_retrieval.feature](../agile/US-001_api_greek_gods_data_retrieval.feature)
- **Public API OpenAPI:** [greekController-oas.yaml](./greekController-oas.yaml)
- **External JSON Server OpenAPI:** [my-json-server-oas.yaml](./my-json-server-oas.yaml)
- **Database Schema:** [schema.sql](./schema.sql)
- **Sequence Diagram:** [greek_gods_api_sequence_diagram.puml](./greek_gods_api_sequence_diagram.puml)

---

**Last Updated:** August 9, 2026  
**Status:** Ready for Technical Review
