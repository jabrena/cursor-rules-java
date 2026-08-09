## Context

The API must combine data from three external mythology sources while keeping
client-facing behavior predictable when one or more sources are slow or
unavailable. The source ADRs select a Spring MVC servlet application with
Spring `RestClient`, no reactive stack, no automatic retries, and deterministic
WireMock-backed tests for timeout behavior.

## Goals / Non-Goals

**Goals:**

- Expose one REST endpoint that aggregates filtered god names across selected pantheons.
- Keep partial-result behavior predictable when individual sources time out or fail.
- Align implementation with Spring Boot 4.1.0, Spring MVC, and `RestClient`.
- Provide deterministic WireMock-backed tests for happy path and timeout scenarios.

**Non-Goals:**

- Authentication, rate limiting, caching, circuit breakers, or automatic HTTP retries.
- Reactive stack (`WebClient`, WebFlux, Project Reactor).
- Rest Assured for HTTP-level acceptance tests.

## Decisions

### REST API Boundary

Expose `GET /api/v1/gods/stats/sum` under base path `/api/v1`. The endpoint
accepts:

- `filter`: required, exactly one Unicode code point.
- `sources`: required, comma-separated source keys from `greek`, `roman`, and
  `nordic`.

Successful responses return `200` with `{ "sum": "<decimal-string>" }`.
Malformed requests return `400` with an error message.

### Aggregation Algorithm

For each selected source, retrieve a list of god names. Include only names whose
first Unicode code point exactly matches the requested `filter`. Matching is
case-sensitive.

The documented source data returns god names with uppercase initial letters,
including `Nike`, `Nemesis`, `Neptun`, and `Njord`. Therefore `filter=N` is the
meaningful filter for the documented aggregate examples. Lowercase `filter=n`
is a valid request, but it returns no matches for the current documented data.

For each included name, convert every Unicode code point to its decimal integer
string, concatenate those strings, parse the result as a large integer, and add
it to the total. Return the total as a decimal string to avoid JSON numeric
precision issues.

### Outbound Source Integration

Use Spring `RestClient` for outbound HTTP calls. Configure connect and read
timeouts once in application configuration, with environment variable overrides
for source URLs and timeout values.

Each selected source receives exactly one outbound GET attempt per API request.
Automatic retries through Resilience4j, Spring Retry, or custom retry loops are
out of scope.

### Resilience Behavior

Fetch selected sources in parallel using **`StructuredTaskScope`** on virtual
threads (**Java 25** preview; enable **`--enable-preview`** on compile, test,
and run). If a source times out or fails, treat that source as empty for the
current aggregation and continue with successful sources. Do not fail the whole
request solely because one selected source failed.

### Technology Stack

Use Spring Boot 4.1.0 with Spring MVC, base package `info.jab.ms`, Spring `RestClient`
for both production outbound calls and HTTP-level acceptance tests, **`StructuredTaskScope`**
for parallel source fetches (Java 25 preview with **`--enable-preview`**), and WireMock
for deterministic source fixtures and timeout simulation. Do not add WebFlux,
`WebClient`, reactive streams, Rest Assured, or retry libraries for this user
story.

## Risks / Trade-offs

- **Partial results** → Consumers must tolerate variable logical completeness while the JSON contract stays stable.
- **No retries** → Transient upstream blips increase partial-result rate compared with a retry-enabled design.
- **Large integer sums** → Return `sum` as a decimal string to avoid JSON numeric precision loss.

## Validation Strategy

- Unit-test the Unicode conversion, filtering, source selection, and aggregation
  behavior.
- Test controller validation for missing, empty, multi-character, and invalid
  query parameters.
- Use WireMock-backed integration or acceptance tests for happy path and partial
  timeout behavior.
- Reset WireMock stubs between tests so timeout scenarios are deterministic.

## Open Questions

None for this change. Future work such as caching, authentication, rate
limiting, circuit breakers, or retry policies should be captured in separate
changes if product scope requires them.
