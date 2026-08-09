# Tasks

## 1. Implementation Checklist

- [ ] 1.1 Create the Spring Boot 4.1.0 application module using Spring MVC and base package `info.jab.ms`.
- [ ] 1.2 Configure Maven **`--enable-preview`** on compiler, Surefire, and Spring Boot run goals for **`StructuredTaskScope`** (Java 25).
- [ ] 1.2 Add configuration properties for Greek, Roman, and Nordic source URLs plus outbound connect and read timeouts.
- [ ] 1.3 Implement source-key parsing for `greek`, `roman`, and `nordic`.
- [ ] 1.4 Implement request validation for required `filter` and `sources` query parameters.
- [ ] 1.5 Implement Unicode code point filtering with case-sensitive first-code-point matching.
- [ ] 1.6 Implement decimal conversion by concatenating each code point's decimal value and summing with a large integer type.
- [ ] 1.7 Implement parallel source fetching with **`StructuredTaskScope`** on virtual threads (Java 25 preview, **`--enable-preview`**) and Spring `RestClient` with one outbound attempt per selected source.
- [ ] 1.8 Treat timed-out or failed source calls as empty source results for partial aggregation.
- [ ] 1.9 Expose `GET /api/v1/gods/stats/sum` returning JSON `{ "sum": "<decimal-string>" }`.
- [ ] 1.10 Add error handling that returns HTTP 400 with an error message for malformed requests.
- [ ] 1.11 Add structured logs for per-source success, timeout, and failure decisions.
- [ ] 1.12 Add OpenAPI documentation matching the source contract.
- [ ] 1.13 Add unit tests for conversion, filtering, source parsing, and aggregation.
- [ ] 1.14 Add controller validation tests for all error-handling scenarios.
- [ ] 1.15 Add WireMock-backed acceptance or integration tests for `filter=N` happy path expected sum `78179288397447443426`.
- [ ] 1.16 Add WireMock-backed timeout tests for `filter=N` partial result expected sum `78101109179220212216`.
- [ ] 1.17 Ensure WireMock stubs are reset between tests.
- [ ] 1.18 Verify no WebFlux, `WebClient`, Rest Assured, Resilience4j Retry, Spring Retry, or custom retry loop is introduced for US-001.
- [ ] 1.19 Run the relevant Maven test or verification command for the implementation module.
- [ ] 1.20 Run `openspec validate --all` from `benchmarks/scenario3/specs/technical-requirements`.
