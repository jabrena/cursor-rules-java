# ADR-001: God Analysis API Functional Requirements

**Status:** Accepted
**Date:** Fri Mar 20 20:23:52 CET 2026
**Decisions:** REST API for cross-pantheon mythology data analysis with Unicode-based name conversion and aggregation
**Primary interface(s):** REST/HTTP API

## Context

A customer has requested a new capability for cross-pantheon mythology data analysis to support educational and research platforms. The system needs to consume multiple external god APIs (Greek, Roman, Nordic), filter gods by name criteria, perform mathematical transformations on the names using Unicode values, and return aggregated results. This addresses the need for reliable, programmatic access to processed mythology data for academic research and educational applications.

The primary consumers are educational and research platforms with moderately technical users who require consistent, well-documented API access to mythology data for analysis and reporting purposes.

## Functional Requirements

### Core API Operations
- **Primary endpoint:** `GET /api/v1/gods/stats/sum`
- **Query parameters:**
  - `filter`: Case-sensitive name filtering by first Unicode code point. The documented source data uses uppercase initial letters, so `filter=N` matches `Nike`, `Nemesis`, `Neptun`, and `Njord`; lowercase `filter=n` is valid but returns no matches for the current documented data.
  - `sources`: Comma-separated pantheon selection (e.g., `sources=greek,roman,nordic`)
- **Response format:** JSON object containing a `sum` field with the calculated aggregate value

### Data Processing Workflow
1. **Source Integration:** Consume data concurrently from each **selected** external god API (parallel fetch; see [ADR-002-God-Analysis-API-Non-Functional-Requirements.md](ADR-002-God-Analysis-API-Non-Functional-Requirements.md)):
   - Greek API: `https://my-json-server.typicode.com/jabrena/latency-problems/greek`
   - Roman API: `https://my-json-server.typicode.com/jabrena/latency-problems/roman`
   - Nordic API: `https://my-json-server.typicode.com/jabrena/latency-problems/nordic`

2. **Name Filtering:** Apply case-sensitive filtering based on the first Unicode code point of god names

3. **Decimal Conversion:** Transform each filtered god name using Unicode-based algorithm:
   - Convert each character to its Unicode integer value
   - Concatenate the integers as strings (e.g., "Zeus" → Z(90)e(101)u(117)s(115) → "90101117115")
   - Sum all converted name values to produce final result

### Error Handling and Resilience
- **Timeout Management:** Outbound calls use bounded connect/read timeouts with **one attempt per source** and **no automatic retry policy** (see [ADR-002-God-Analysis-API-Non-Functional-Requirements.md](ADR-002-God-Analysis-API-Non-Functional-Requirements.md))
- **Graceful Degradation:** Continue processing with partial results when individual sources timeout or fail on the single attempt
- **Invalid Parameters:** Return HTTP 400 with error messages for malformed requests (missing parameters, invalid filter length, invalid source names)
- **Source Unavailability:** Process available sources when others are unreachable or time out
- **All sources unavailable:** When every selected source times out or fails on the single attempt, return HTTP 200 with `sum` `"0"` (no contributing names; see ADR-002 and the feature file)

## Scope and Product Decisions

- **Service shape:** Monolithic REST service with a single focused endpoint (`GET /api/v1/gods/stats/sum`)
- **Access model:** Public API with no authentication or rate limiting for educational and research use
- **Data freshness:** No response caching from external god APIs; each request fetches current upstream data subject to timeout and partial-aggregation rules in ADR-002
- **Integration:** Direct HTTP consumption of the three documented external god APIs

Implementation technology (framework, HTTP client, configuration layout, and test stack) is recorded in [ADR-003-God-Analysis-API-Technology-Stack.md](ADR-003-God-Analysis-API-Technology-Stack.md).

## Alternatives Considered

### Caching Strategy
**Rejected:** Implementing response caching from external god APIs
**Reason:** Adds complexity without clear benefit for educational use case. Direct API calls ensure data freshness and simplify error handling.

### Authentication Mechanisms
**Rejected:** API key or OAuth-based authentication
**Reason:** Educational and research context benefits from open access. No commercial or sensitive data concerns requiring access controls.

### Microservices Architecture
**Rejected:** Separate services for each pantheon or processing step
**Reason:** Single focused use case doesn't justify distributed system complexity. Monolithic approach provides simpler deployment and maintenance.

## Consequences

### Positive Impacts
- **Simplicity:** Focused functional scope reduces ambiguity for implementers and consumers
- **Accessibility:** Public API enables easy integration for educational platforms
- **Resilience:** Partial aggregation when upstream sources fail keeps the service usable
- **Testability:** Acceptance criteria in the feature file cover happy path, validation, and timeout behavior

### Trade-offs
- **Performance:** No caching may result in slower responses and higher external API load
- **Security:** Public access provides no usage tracking or abuse prevention
- **Scalability:** Monolithic design may require refactoring for high-volume usage

### Follow-up Work
- Implement comprehensive error logging for external API failures
- Monitor external API reliability and consider fallback strategies if needed
- Evaluate need for basic usage metrics collection

## References

- [ADR-002-God-Analysis-API-Non-Functional-Requirements.md](ADR-002-God-Analysis-API-Non-Functional-Requirements.md) - Reliability, timeouts, and partial results
- [ADR-003-God-Analysis-API-Technology-Stack.md](ADR-003-God-Analysis-API-Technology-Stack.md) - Framework, HTTP client, configuration, and test stack
- [US-001_God_Analysis_API.md](US-001_God_Analysis_API.md) - User story and requirements
- [US-001_god_analysis_api.feature](US-001_god_analysis_api.feature) - Acceptance criteria and test scenarios
- External God APIs:
  - Greek: https://my-json-server.typicode.com/jabrena/latency-problems/greek
  - Roman: https://my-json-server.typicode.com/jabrena/latency-problems/roman
  - Nordic: https://my-json-server.typicode.com/jabrena/latency-problems/nordic
