## ADDED Requirements

### Requirement: Gods stats sum HTTP endpoint

The system MUST expose `GET /api/v1/gods/stats/sum` with required query
parameters `filter` and `sources`.

#### Scenario: Happy path returns the documented aggregate

- **GIVEN** the God Analysis API is available at `/api/v1`
- **AND** Greek, Roman, and Nordic source APIs return their documented god-name data
- **WHEN** a client sends `GET /gods/stats/sum?filter=N&sources=greek,roman,nordic`
- **THEN** the response status is `200`
- **AND** the response body contains JSON field `sum`
- **AND** `sum` is `78179288397447443426`

#### Scenario: Successful response uses decimal string

- **WHEN** a valid request is processed
- **THEN** the response body is a JSON object with a required `sum` field
- **AND** `sum` is a non-negative decimal string matching `^[0-9]+$`

### Requirement: Query parameter validation

The system MUST reject malformed requests with HTTP `400` and an error message
when required query parameters are missing, empty, or invalid.

#### Scenario: Missing filter is rejected

- **WHEN** a client sends `GET /gods/stats/sum?sources=greek,roman,nordic`
- **THEN** the response status is `400`
- **AND** the response body contains an error message

#### Scenario: Missing sources is rejected

- **WHEN** a client sends `GET /gods/stats/sum?filter=N`
- **THEN** the response status is `400`
- **AND** the response body contains an error message

#### Scenario: Empty filter is rejected

- **WHEN** a client sends a request with `filter` empty and valid `sources`
- **THEN** the response status is `400`
- **AND** the response body contains an error message

#### Scenario: Multi-character filter is rejected

- **WHEN** a client sends a request with `filter=abc` and valid `sources`
- **THEN** the response status is `400`
- **AND** the response body contains an error message

#### Scenario: Invalid sources are rejected

- **WHEN** a client sends a request with `sources=invalid,unknown`
- **THEN** the response status is `400`
- **AND** the response body contains an error message

#### Scenario: Empty sources are rejected

- **WHEN** a client sends a request with `sources` empty and valid `filter`
- **THEN** the response status is `400`
- **AND** the response body contains an error message

### Requirement: Source selection and integration

The system MUST support source keys `greek`, `roman`, and `nordic`, fetch only
the selected sources, and consume each source as a JSON list of god names from
configurable HTTP URLs.

#### Scenario: Selected sources are fetched

- **WHEN** a valid request includes `sources=greek,roman,nordic`
- **THEN** the system issues one GET request to each selected source URL
- **AND** the returned names participate in filtering and aggregation

### Requirement: Unicode filtering and decimal aggregation

The system MUST include only names whose first Unicode code point exactly
matches `filter`, MUST perform matching case-sensitively, MUST convert each
included name by concatenating decimal Unicode code point values, and MUST sum
the converted values using a large integer representation. The documented source
data uses uppercase initial letters, so `filter=N` is the meaningful filter for
the documented aggregate examples. Lowercase `filter=n` is valid but returns no
matches for the current documented data.

#### Scenario: Uppercase filter matches documented source data

- **WHEN** the request uses `filter=N`
- **THEN** only names starting with uppercase `N` contribute to `sum`
- **AND** names starting with lowercase `n` do not contribute

#### Scenario: Lowercase filter is valid but has no documented matches

- **WHEN** the request uses `filter=n`
- **AND** the documented source data contains no names starting with lowercase `n`
- **THEN** the response status is `200`
- **AND** `sum` is `0`

#### Scenario: Name contribution uses concatenated code point values

- **GIVEN** an included name `Zeus`
- **WHEN** the system computes the name contribution
- **THEN** the contribution is the numeric value of `90101117115`

#### Scenario: No matches return zero

- **WHEN** no retrieved name starts with the requested `filter`
- **THEN** the response status is `200`
- **AND** `sum` is `0`

### Requirement: Timeout and partial-result behavior

The system MUST configure Spring `RestClient` connect and read timeouts, MUST
perform exactly one outbound attempt per selected source per request, and MUST
continue aggregation with successful sources when one or more sources time out
or fail.

#### Scenario: Multiple source timeouts return partial sum

- **GIVEN** the Nordic API is configured to respond after the timeout threshold
- **AND** the Roman API is configured to respond after the timeout threshold
- **WHEN** a client sends `GET /gods/stats/sum?filter=N&sources=greek,roman,nordic`
- **THEN** the response status is `200`
- **AND** the response body contains JSON field `sum`
- **AND** `sum` is `78101109179220212216`

#### Scenario: Timed-out source is not retried

- **WHEN** a selected source times out
- **THEN** that source contributes no names to the current aggregation
- **AND** no second outbound attempt is made for that source during the same API request

### Requirement: Technology constraints

The implementation MUST use Spring Boot with Spring MVC, Spring `RestClient`,
base package `info.jab.ms`, and WireMock-backed deterministic tests for source
fixtures and timeout behavior.

#### Scenario: Servlet stack is used

- **WHEN** dependencies are inspected
- **THEN** WebFlux, `WebClient`, reactive streams, Rest Assured, Resilience4j Retry, and Spring Retry are absent for US-001

#### Scenario: WireMock tests are isolated

- **WHEN** acceptance or integration tests simulate timeout behavior
- **THEN** WireMock stubs are reset between tests
- **AND** timeout scenarios do not depend on test execution order
