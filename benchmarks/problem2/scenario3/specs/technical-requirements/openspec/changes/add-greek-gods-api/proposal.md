## Why

API consumers need a stable local REST endpoint that returns Greek god records
with generated identifiers, synchronized from a third-party JSON service that
exposes names only. Reads must use PostgreSQL so clients are not coupled to
external latency or availability at request time.

## What Changes

- Add `GET /api/v1/gods/greek` returning a JSON array of `{ "id", "name" }` records.
- Persist synchronized data in PostgreSQL table `greek_god` (`id` serial PK, unique `name`).
- Add a scheduled background updater that fetches
  `GET https://my-json-server.typicode.com/jabrena/latency-problems/greek`
  (JSON array of strings) and upserts by natural key `name`.
- Preserve existing row IDs across synchronization; insert only new names.
- Align OpenAPI documentation with the public contract.
- Add Quarkus acceptance and synchronization tests covering the Gherkin scenarios.

## Capabilities

### New Capabilities

- `greek-gods-api`: Public Greek gods read API, PostgreSQL persistence,
  background name-based synchronization, and acceptance coverage for the
  documented happy-path and data-quality scenarios.

### Modified Capabilities

None.

## Source and Derivation

- Source artifact: `../../../../functional-requirements/agile/US-001_API_Greek_Gods_Data_Retrieval.md`.
- Acceptance source: `../../../../functional-requirements/agile/US-001_api_greek_gods_data_retrieval.feature`.
- Implementation plan source: `../../../../functional-requirements/agile/PLAN-US-001_Implementation.plan.md`.
- HTTP contract source: `../../../../functional-requirements/design/greekController-oas.yaml`.
- External sync contract source: `../../../../functional-requirements/design/my-json-server-oas.yaml`.
- Functional decision source: `../../../../functional-requirements/design/ADR-001_REST_API_Functional_Requirements.md`.
- Testing decision source: `../../../../functional-requirements/design/ADR-002-Acceptance-Testing-Strategy.md`.
- Technology decision source: `../../../../functional-requirements/design/ADR-003-Greek-Gods-API-Technology-Stack.md`.
- Schema source: `../../../../functional-requirements/design/schema.sql`.
- Sequence diagram source: `../../../../functional-requirements/design/greek_gods_api_sequence_diagram.puml`.
- Derivation direction: co-located functional requirements -> OpenSpec change
  artifacts -> implementation and tests.

## Change Boundary Assessment

This is one OpenSpec change because the source artifacts describe one
independently reviewable API capability with one public endpoint, one
persistence model, and one synchronization worker behind the same acceptance
boundary.

## Impact

Implementation affects the Quarkus demo module under
`benchmarks/problem2/scenario3/demo/`: Maven dependencies (REST, JDBC,
PostgreSQL, scheduler, test stack), schema migration, repository/service/REST
resource, scheduled updater and outbound HTTP client, application configuration,
OpenAPI alignment, and unit/integration/acceptance tests. The OpenSpec change
records the capability without rewriting the source functional requirements.
