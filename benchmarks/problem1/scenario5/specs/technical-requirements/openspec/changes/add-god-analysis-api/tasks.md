# Tasks

Implementation follows **two-step method** scaffolding then **vertical slices S1–S6** from `design.md`. Prefer TDD order within each slice. The architecture baseline is package-level Hexagonal architecture using `domain`, `application`, `application.port.in`, `application.port.out`, `adapter.in.rest`, and `adapter.out.http`.

## 1. Implementation Checklist

### Step 1 — Hexagonal scaffolding (make the change easy)

- [ ] 1.1 Create the Spring Boot 4.1.0 application module under `benchmarks/scenario4/demo/` with packages `info.jab.ms.adapter.in.rest`, `info.jab.ms.adapter.out.http`, `info.jab.ms.application`, `info.jab.ms.application.port.in`, `info.jab.ms.application.port.out`, and `info.jab.ms.domain` plus `info.jab.ms.GodAnalysisApplication` (design D4).
- [ ] 1.2 Configure Maven **`--enable-preview`** on compiler, Surefire, and Spring Boot run goals for **`StructuredTaskScope`** (Java 25).
- [ ] 1.3 Add Maven property `archunit.version` and test-scoped dependency `com.tngtech.archunit:archunit-junit5` for executable architecture rules (design Architecture verification dependency).
- [ ] 1.4 Add `GodAnalysisProperties` in **adapter.out.http** for Greek, Roman, and Nordic source URLs plus outbound connect and read timeouts (defaults in `application.yml`).
- [ ] 1.5 Register a `RestClient` bean at the adapter/bootstrap edge (`RestClientConfig`) with `JdkClientHttpRequestFactory` connect/read timeouts from properties.
- [ ] 1.6 Introduce `QueryGodStats` in **application.port.in**, `GodSourceClient` in **application.port.out**, and `PantheonSource` in **domain** with parsing for `greek`, `roman`, and `nordic` (reject unknown keys; dedupe duplicate keys per design D2).
- [ ] 1.7 Implement **domain** types: `GodNameFilter`, `UnicodeNameConverter`, `GodStatsAggregator` (no HTTP, Spring, application, or adapter dependencies).
- [ ] 1.8 Add **domain** unit tests for conversion (`Zeus` → `90101117115`), case-sensitive filter, empty-input sum `0`, and invalid source parsing.

### Step 2 — Fill layers (make the easy change)

- [ ] 1.9 Implement **adapter.out.http** `RestGodSourceClient` as the outbound `GodSourceClient` adapter — deserialize `String[]` from each source URL; on timeout/failure return empty list and log outcome.
- [ ] 1.10 Implement **application** `GodStatsUseCase` as the inbound `QueryGodStats` port implementation — parallel fetch via `StructuredTaskScope` (Java 25 preview, `--enable-preview`); merge names; delegate to domain aggregator; depend only on domain types and outbound ports.
- [ ] 1.11 Expose **adapter.in.rest** `GET /api/v1/gods/stats/sum` in `GodStatsController` returning JSON `{ "sum": "<decimal-string>" }` by calling the inbound `QueryGodStats` port.
- [ ] 1.12 Add **adapter.in.rest** validation and `@ControllerAdvice` error handling — HTTP 400 with Spring **`ProblemDetail`** (`detail` field) for all Gherkin `@error-handling` scenarios (design D1).
- [ ] 1.13 Add structured logs for per-source success, timeout, and failure decisions.
- [ ] 1.14 Add OpenAPI documentation matching `US-001-god-analysis-api.openapi.yaml`.

### Testing slices

- [ ] 1.15 Add MockMvc / `@WebMvcTest` tests for all query-parameter validation scenarios.
- [ ] 1.16 Add WireMock test support — dynamic ports, `@BeforeEach` stub reset, fixtures under `src/test/resources/__files/`.
- [ ] 1.17 Add WireMock-backed acceptance test (`@Tag("acceptance-test")`) — `filter=N`, all sources, expected sum `78179288397447443426` using Spring `RestClient` against `RANDOM_PORT`.
- [ ] 1.18 Add WireMock-backed integration test (`@Tag("integration-test")`) — Roman+Nordic delayed beyond read timeout, expected partial sum `78101109179220212216`.
- [ ] 1.19 Add WireMock-backed integration test (`@Tag("integration-test")`) — all three sources delayed beyond read timeout, expected sum `0` (slice S5b).
- [ ] 1.20 Verify WireMock stubs reset between tests and timeout scenarios do not depend on execution order.
- [ ] 1.21 Add `HexagonalArchitectureTest` using ArchUnit to verify: `domain` and `application` do not depend on `adapter..`; `domain` does not depend on `application..`; core packages do not depend on Spring MVC, Spring `RestClient`, servlet APIs, or JDK HTTP client APIs; `adapter.in..` and `adapter.out..` do not depend on each other; only `adapter`, `application`, and `domain` exist as top-level implementation packages under `info.jab.ms`.

### Guardrails and verification

- [ ] 1.22 Verify no WebFlux, `WebClient`, Rest Assured, Resilience4j Retry, Spring Retry, or custom retry loop is introduced for US-001.
- [ ] 1.23 Run focused architecture verification from the demo module: `./mvnw test -Dtest="*ArchitectureTest"` when a Maven wrapper exists, otherwise `mvn test -Dtest="*ArchitectureTest"`.
- [ ] 1.24 Run `./mvnw clean verify` from the demo module.
- [ ] 1.25 Run `openspec validate --all` from `benchmarks/scenario4/specs/technical-requirements`.
