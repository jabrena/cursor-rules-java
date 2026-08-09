## Context

The service must expose Greek god data through a local REST API while keeping
that data synchronized from a third-party JSON service. The external service
returns names only (`["Zeus", "Hera"]`); the internal API returns database
records with generated identifiers (`[{ "id": 1, "name": "Zeus" }, ...]`).

Source ADRs select Quarkus 3.x with REST, JDBC, PostgreSQL, and a scheduler.
Reads MUST NOT proxy the external JSON server. Synchronization uses `name` as
the natural key so existing rows keep stable IDs.

Architecture guidance follows Plinth skill
`707-technologies-hexagonal-architecture` (Ports and Adapters): domain and
application core stay framework-agnostic; driving/driven adapters sit at the
edge; dependency direction is core ← adapters.

Functional authority lives in
`../../../../functional-requirements/`; this design records implementation
decisions for the OpenSpec change only. Demo target is greenfield
`benchmarks/problem2/scenario4/demo/` (currently empty except `.gitkeep`).

## Goals / Non-Goals

**Goals:**

- Expose `GET /api/v1/gods/greek` returning a JSON array of `{ "id", "name" }`.
- Persist data in PostgreSQL table `greek_god` matching the source schema.
- Synchronize names periodically from my-json-server `/greek` with upsert-by-name.
- Keep API reads independent from external service availability.
- Structure the Quarkus demo with Hexagonal ports/adapters packages and
  dependency-direction checks (ArchUnit).
- Provide deterministic Quarkus tests (REST Assured for HTTP; stubbed upstream for sync).

**Non-Goals:**

- Authentication, rate limiting, caching, or circuit breakers.
- Returning a bare array of strings from the public API.
- Proxying the external JSON server on read requests.
- Switching to Micronaut or Spring Boot for this scenario.
- Feature-toggle / canary rollout machinery for this benchmark demo.
- Expand/migrate/contract parallel schema change (greenfield V1 only).
- Deep domain modeling beyond `{ id, name }` and name-based upsert policy.

## Decisions

### REST API Boundary

Expose `GET /api/v1/gods/greek` under path-based API versioning. Successful
responses return `200` with `application/json` and a JSON array of Greek god
records. Unexpected server failures return `500` with `problemDetails`
(`application/problem+json`). No request authentication is required for this
endpoint.

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

Access the table through a driven JDBC adapter implementing an outbound
persistence port. Apply schema via Flyway V1 matching
`../../../../functional-requirements/design/schema.sql`. Read path uses
`SELECT id, name FROM greek_god`. Upsert path inserts only when `name` is
absent; never rewrite an existing row's `id`.

Greenfield migration note (`055-design-parallel-change`): a single additive V1
migration is sufficient. No expand/migrate/contract window is required because
there is no prior schema or coexisting application version.

### Background Synchronization

A scheduled driving adapter triggers a synchronize use case that:

1. Calls outbound HTTP port →
   `GET https://my-json-server.typicode.com/jabrena/latency-problems/greek`.
2. Accepts a JSON array of strings.
3. Upserts via the persistence outbound port by unique `name`.
4. Inserts new names with generated IDs; retains existing IDs for known names.

The external OpenAPI contract is
`../../../../functional-requirements/design/my-json-server-oas.yaml`.
Sequence detail is in
`../../../../functional-requirements/design/greek_gods_api_sequence_diagram.puml`.

### Runtime Stack

Adopt Quarkus 3.x as documented in ADR-003; wire adapters at the edge only:

| Concern | Choice |
| --- | --- |
| Runtime | Quarkus 3.x (Java 25, Maven) |
| Architecture | Hexagonal (Ports and Adapters) per `707-technologies-hexagonal-architecture` |
| Public API | Quarkus REST driving adapter |
| Persistence | Quarkus JDBC driven adapter + PostgreSQL |
| Schema migration | Flyway (`quarkus-flyway`); V1 matches `schema.sql` |
| Sync trigger | Quarkus scheduler driving adapter |
| Sync cadence | Every `5m` by default; disable or override under `%test` so sync tests drive the updater explicitly |
| Outbound sync HTTP | Quarkus REST Client driven adapter (injectable, stubbable in tests) |
| Architecture tests | ArchUnit Hexagonal boundary rules |
| HTTP acceptance tests | REST Assured |
| Sync upstream in tests | Stubbed / fake external service |

**Design approval (maintainer):** Hexagonal Quarkus layout (domain + application
ports/use cases; driving REST/scheduler adapters; driven JDBC/HTTP adapters;
`GreekGod` in domain) and the three hamburger slices (read API → sync →
contract/errors/architecture) are approved for implementation.

Feature toggles (`057-design-feature-toggles`): not selected. Rollout risk for
this benchmark is handled by module-local configuration (datasource URL,
scheduler cron/interval, external base URL) and by disabling or stubbing the
upstream in tests. No release/experiment toggle lifecycle is required.

### Hexagonal Component Boundaries

Follow skill `707` package scaffolding (small, honest, no empty layers).
Suggested root package `info.jab.ms` (adjust only if demo conventions require):

```text
src/main/java/info/jab/ms/
  GreekGodsApplication.java          # thin Quarkus bootstrap only
  domain/
    GreekGod.java                    # { id, name }; no framework/JDBC/HTTP APIs
  application/
    GetGreekGodsUseCase.java         # implements inbound query port
    SynchronizeGreekGodsUseCase.java # implements inbound sync port
    port/
      in/
        QueryGreekGods.java          # inbound: List<GreekGod> findAll()
        SynchronizeGreekGods.java    # inbound: void synchronize()
      out/
        GreekGodRepository.java      # outbound: findAll / existsByName / insert
        GodSourceClient.java         # outbound: List<String> fetchNames()
  adapter/
    in/
      rest/
        GreekGodResource.java        # driving: HTTP → QueryGreekGods
        ProblemDetailsExceptionMapper.java
      scheduler/
        GodUpdater.java              # driving: @Scheduled → SynchronizeGreekGods
    out/
      persistence/
        JdbcGreekGodRepository.java  # driven: implements GreekGodRepository
      http/
        RestGodSourceClient.java     # driven: implements GodSourceClient
```

| Layer | Responsibility | Must not |
| --- | --- | --- |
| `domain` | `GreekGod` and any name/upsert policy that is pure Java | Import Quarkus, JDBC, REST, HTTP client, or adapter packages |
| `application` | Use cases orchestrate ports; declare inbound/outbound ports | Depend on concrete adapters or framework web/persistence APIs |
| `adapter.in.rest` | Map HTTP ↔ inbound query port; `problemDetails` for unexpected failures | Contain SQL; call outbound HTTP; trigger sync |
| `adapter.in.scheduler` | Trigger inbound sync port on schedule | Serve HTTP; own upsert SQL |
| `adapter.out.persistence` | JDBC + Flyway-backed table access implementing repository port | Know REST paths or scheduler cadence |
| `adapter.out.http` | Fetch upstream `List<String>` implementing source port | Write to the database; expose public REST |

Dependency direction (`707`):

- Driving adapters → inbound ports / use cases.
- Driven adapters → implement outbound ports.
- `domain` and `application` must not depend on `adapter..`.
- `domain` must not depend on `application`.
- Driving adapters (`adapter.in..`) must not depend on driven adapters
  (`adapter.out..`) and vice versa.
- Runtime composition (CDI) wires concrete adapters at the edge.

Recommended collaboration patterns (`121` / `122` / `123` + `707`):

- **Constructor injection** of ports into use cases and of use cases into
  driving adapters.
- **Outbound ports** for persistence and upstream HTTP (not concrete JDBC/REST
  Client types in application code).
- **Record** for `GreekGod` with `long`/`Long` `id` (OpenAPI `int64`) and
  non-blank `name` as a plain `String`.
- **Anti-corruption at the HTTP driven adapter**: map upstream `List<String>`
  into domain names before the sync use case persists them.
- Keep the port set minimal: two inbound + two outbound ports for this scope
  (Beck: fewest elements after intention and testability).

### Data Flow

**Read path (request time) — REST driving adapter uses local read model only:**

1. Client → `adapter.in.rest.GreekGodResource` `GET /api/v1/gods/greek`
2. Resource → inbound `QueryGreekGods` / `GetGreekGodsUseCase`
3. Use case → outbound `GreekGodRepository.findAll()`
4. `adapter.out.persistence` → `SELECT id, name FROM greek_god`
5. JSON array of `{ "id", "name" }` returned; no call to `GodSourceClient`

Non-proxy on GET is an architectural boundary (driving REST → query use case →
persistence port), not a separate network-assertion acceptance task.

**Sync path (background):**

1. `adapter.in.scheduler.GodUpdater` fires
2. Adapter → inbound `SynchronizeGreekGods` / `SynchronizeGreekGodsUseCase`
3. Use case → `GodSourceClient.fetchNames()` → driven HTTP adapter
4. Use case → `GreekGodRepository` insert-if-absent by `name`
5. Unique constraint on `name` is the last line of defense against duplicates

### Failure Handling

| Failure | Expected behavior |
| --- | --- |
| Unexpected exception on read (DB down, unchecked failure) | HTTP `500` with `problemDetails` (`application/problem+json`, status `500`) via REST driving adapter mapping |
| Upstream sync HTTP error / timeout / non-array payload | Log and fail the sync run without affecting in-flight read responses; do not mutate partial identity of existing rows |
| Duplicate `name` insert race | Database unique constraint rejects duplicate; upsert logic should prefer existence-check / insert-if-absent so retries stay idempotent by name |
| Empty upstream array | No inserts; existing rows remain |

Reads remain available from local PostgreSQL even when sync fails.

### Testing Strategy

Authority: ADR-002 plus OpenSpec scenarios in `specs/greek-gods-api/spec.md`.
Architecture verification follows `707` (optional ArchUnit selected for this demo).

| Layer | Focus | Strategy notes (`054` / `130` / `707`) |
| --- | --- | --- |
| Acceptance (HTTP) | Happy path + Zeus data-quality | REST Assured; assert `200`, `Content-Type` `application/json`, array of objects with integer/long `id` and string `name` |
| Sync | String-array upstream, insert-new, stable IDs, natural key | Stub/fake outbound `GodSourceClient` or WireMock at HTTP adapter; assert DB state; never call live my-json-server |
| Persistence | Schema `greek_god`, unique `name` | Flyway in Quarkus tests (Dev Services or testcontainers-equivalent) |
| Error | Unexpected read failure → `500` + `problemDetails` | Failing outbound persistence collaborator (or failing use-case double) behind the inbound port |
| Architecture | Hexagonal dependency direction + package shape | ArchUnit: core ↛ adapters; domain ↛ application; `adapter.in` ↛ `adapter.out` and reverse; core free of Quarkus/JDBC/HTTP client APIs where practical |

RIGHT-BICEP emphasis: right results (payload shape), boundaries (empty DB vs seeded Zeus), error conditions (500, upstream failure isolation), cross-check (DB rows vs HTTP body after sync).

A-TRIP: automatic Maven module tests; repeatable via stubs; independent scenarios with fresh seed data; no live network dependency.

CORRECT boundaries: existence (missing name inserted), cardinality (no duplicate Zeus), conformance (array of strings upstream vs objects downstream), reference (reads use local table only).

### Alternatives Considered

Evaluated with Beck simple design rules in order: passes the tests → reveals
intention → no duplication → fewest elements — and with `707` dependency
direction constraints.

| Option | Assessment | Beck / `707` rules |
| --- | --- | --- |
| Hexagonal ports/adapters + Quarkus edge adapters | **Selected.** Matches ADR-003 stack while keeping core independent; REST and scheduler are driving adapters; JDBC and REST Client are driven adapters. | Passes planned tests; package names reveal intention; shared ports avoid duplicating contracts; minimal port set (2 in / 2 out). |
| Thin Resource → Service → Repository without ports | **Rejected.** Couples application orchestration to concrete adapters; harder to fake persistence/HTTP at the use-case boundary. | Weaker intention for replaceable adapters; conflicts with maintainer direction to apply `707`. |
| Micronaut or Spring Boot stack | **Rejected.** Conflicts with ADR-003 Quarkus choice. | Would fail stack/contract tests for this scenario. |
| Direct external proxy on read | **Rejected.** Violates independence and OpenSpec “no proxy on read”. | Fails acceptance/architecture criteria. |
| Public API returns only strings | **Rejected.** Conflicts with OpenAPI / Gherkin / US-001. | Fails contract tests. |
| Hibernate ORM / Panache entity model | **Rejected for default.** ADR-003 specifies JDBC; ORM adds elements without improving the two-column upsert story. | Fewer elements + clearer JDBC intention in the driven adapter. |
| Sync-on-read (lazy fetch) | **Rejected.** Couples latency/availability to GET; conflicts with background updater model. | Fails independence goal / tests. |

### Compatibility Surfaces (`056-design-avoid-breaking-changes`)

| Surface | Classification | Note |
| --- | --- | --- |
| Public HTTP contract `GET /api/v1/gods/greek` | Intentional new contract | Align with `greekController-oas.yaml`; array of `{id,name}` |
| Upstream `/greek` string array | External dependency | Do not change; adapt at HTTP driven adapter |
| PostgreSQL `greek_god` | New schema | V1 create only; unique `name` is part of the contract |
| Functional-requirements sources | Non-breaking | OpenSpec derives from them; do not rewrite those files in this change |
| Plinth commands/skills/generated outputs | Non-breaking | Demo module is isolated under `benchmarks/problem2/scenario4/demo/` |

## Risks / Trade-offs

- [External freshness depends on scheduler] → Document sync cadence in
  configuration; acceptance focuses on upsert rules, not live freshness SLAs.
- [Live my-json-server flakiness in tests] → Stub the upstream for sync tests.
- [Duplicate names from upstream] → Unique constraint on `name` prevents duplicates;
  upsert matches existing rows.
- [500 path hard to provoke] → Prefer a failing outbound persistence collaborator
  behind the inbound query port rather than breaking production configuration.
- [Scheduler fires during tests] → Disable or lengthen schedule in test profile;
  invoke sync use case / scheduler adapter explicitly in sync tests.
- [Hexagonal ceremony for a small demo] → Keep only the ports this feature needs;
  do not add unused layers or generic “common” packages.

## Delivery Plan (Two-Step + Vertical Slices)

Intended behavior changes (in order):

1. Clients can retrieve local `{id,name}` records over HTTP.
2. Background sync upserts upstream names by `name` without changing existing IDs.

### Hamburger layers and first-slice choices

| Layer | Simplest useful option selected for slice 1 | Deferred |
| --- | --- | --- |
| API surface | `GET /api/v1/gods/greek` only | Extra endpoints, auth |
| Application core | `QueryGreekGods` + `GetGreekGodsUseCase` + `GreekGod` | Sync use case |
| Read model | Seeded/migrated `greek_god` + JDBC driven adapter | Projection/caching |
| Sync trigger | None in slice 1 (manual/seeded data) | Scheduler driving adapter |
| External integration | None in slice 1 | HTTP driven adapter |
| Quality | REST Assured happy-path + Zeus | Sync suite, ArchUnit, OpenAPI polish |

**Slice 1 — Read API vertical slice (observable value: HTTP contract)**

1. **Prep (behavior-preserving / scaffolding):** Quarkus module, dependencies,
   Flyway V1, Hexagonal packages, inbound query port + use case, persistence
   outbound port + JDBC adapter, REST driving adapter wired but not yet meeting
   acceptance assertions.
2. **Behavior:** Implement read path so acceptance tests pass with seeded data
   (including Zeus `id=1` when required).
3. **Verify:** Module build + HTTP acceptance tests (`tasks.md` §2).

**Slice 2 — Sync vertical slice (observable value: upsert identity rules)**

1. **Prep:** Add inbound sync port/use case, outbound `GodSourceClient`, HTTP
   driven adapter seam; test profile stubs upstream; keep scheduler from calling
   live network.
2. **Behavior:** Implement synchronize use case upsert-by-name (insert new;
   retain IDs); scheduler driving adapter calls the inbound sync port.
3. **Verify:** Sync tests with stubbed string-array payload (`tasks.md` §3).

**Slice 3 — Contract, errors, and architecture fitness**

1. **Prep:** Confirm OpenAPI alignment approach (generated vs static doc);
   add ArchUnit test skeleton matching packages.
2. **Behavior:** Ensure unexpected read failures surface as HTTP `500` +
   `problemDetails`; enforce Hexagonal dependency-direction rules.
3. **Verify:** Gherkin-mapped coverage review, ArchUnit green, module verify,
   `openspec validate` (`tasks.md` §4).

TDD sequencing inside slices: maintain a test list; write failing acceptance/sync
test first; implement only enough production code to pass; refactor while green.

## Migration Plan

Greenfield demo module under `benchmarks/problem2/scenario4/demo/`. Apply Flyway
V1 creating `greek_god`, scaffold Hexagonal packages, then enable scheduler
(default every `5m`) and REST driving adapter. Rollback is removing the demo
implementation and restoring `demo/.gitkeep` for the benchmark harness.

## Open Questions

None remaining. Maintainer resolutions:

1. **Migration** — Flyway (`quarkus-flyway`); V1 matches `schema.sql`.
2. **Outbound client** — Quarkus REST Client (driven adapter).
3. **Scheduler cadence** — every `5m` by default; `%test` disables or overrides
   so tests invoke sync explicitly (no freshness SLA in functional sources).
4. **Observable `500` verification** — `tasks.md` 4.2 + `spec.md` require
   `problemDetails` (`application/problem+json`, status `500`).
5. **Read-path non-proxy** — design-only (REST driving adapter → query use case →
   persistence port); no network spy task.
6. **OpenAPI** — docs-only via tasks 4.1/4.3; no OpenAPI scenario in `spec.md`.
7. **Design direction** — approved Hexagonal Quarkus split + three slices
   (updated to apply `707-technologies-hexagonal-architecture`).

No new functional ADR is proposed: ADR-001/002/003 already record contract,
testing, and stack. Flyway, REST Client, and Hexagonal package boundaries are
recorded here as implementation decisions for this change.
