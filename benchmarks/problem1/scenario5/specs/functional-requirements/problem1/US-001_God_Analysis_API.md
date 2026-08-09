# User Story: US-001 God Analysis API

**As a** API consumer / data analyst
**I want to** consume God APIs (Greek, Roman & Nordic), filter gods whose names start with a requested letter, convert each filtered god name into its decimal representation, and return the sum of those values
**So that** I can perform cross-pantheon analysis and aggregate mythology data for research, reporting, or educational applications

## Acceptance Criteria

See: [US-001_god_analysis_api.feature](US-001_god_analysis_api.feature)

## Notes

- **Decimal Conversion Rule:** For each god name, each character is converted to its Unicode integer value and those integers are concatenated as strings (e.g., "Zeus" → Z(90)e(101)u(117)s(115) → "90101117115"). The final result is the numeric sum of all per-name string representations.
- **Case sensitivity:** The `filter` parameter accepts exactly one Unicode code point and matching is case-sensitive. The documented source data returns god names with uppercase initial letters, such as `Nike`, `Nemesis`, `Neptun`, and `Njord`, so `filter=N` is the meaningful value for the documented aggregate examples. A lowercase `filter=n` is valid but returns no matches for the current documented data.
- **HTTP timeouts:** Outbound source calls use bounded connect and read timeouts with **one attempt per source** and **no automatic retries**; aggregation continues with the sources that return in time. When every selected source times out or fails, the response is HTTP 200 with `sum` `"0"`. See [ADR-002-God-Analysis-API-Non-Functional-Requirements.md](ADR-002-God-Analysis-API-Non-Functional-Requirements.md) and [US-001_god_analysis_api.feature](US-001_god_analysis_api.feature).
- **Operational configuration** (timeout values, source URLs): see [ADR-003-God-Analysis-API-Technology-Stack.md](ADR-003-God-Analysis-API-Technology-Stack.md).
- **Data sources:**
  - Greek API: https://my-json-server.typicode.com/jabrena/latency-problems/greek
  - Roman API: https://my-json-server.typicode.com/jabrena/latency-problems/roman
  - Nordic API: https://my-json-server.typicode.com/jabrena/latency-problems/nordic
