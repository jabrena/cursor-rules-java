# Problem 1

## User Story Statement

- **As an** API consumer / data analyst
- **I want to** consume God APIs (Greek, Roman & Nordic), filter gods whose names start with a requested letter, convert each filtered god name into a decimal representation, and return the sum of those values
- **So that** I can perform cross-pantheon analysis and aggregate mythology data for research, reporting, or educational applications.

**Notes:**

- Decimal conversion: For each god name, each character is converted to its Unicode integer value and those integers are concatenated as strings (for example, `Zeus` -> `90101117115`). The final result is the numeric sum of all per-name string representations.
- Case sensitivity: The `filter` parameter accepts exactly one Unicode code point and matching is case-sensitive. The documented source data returns god names with uppercase initial letters, such as `Nike`, `Nemesis`, `Neptun`, and `Njord`, so `filter=N` is the meaningful value for the documented aggregate examples. A lowercase `filter=n` is valid but returns no matches for the current documented data.
- HTTP timeouts: Outbound source calls use bounded connect and read timeouts with one attempt per source and no automatic retries; aggregation continues with the sources that return in time. When every selected source times out or fails, the response is HTTP 200 with `sum` `"0"`.
- Data sources:
  - Greek API: https://my-json-server.typicode.com/jabrena/latency-problems/greek
  - Roman API: https://my-json-server.typicode.com/jabrena/latency-problems/roman
  - Nordic API: https://my-json-server.typicode.com/jabrena/latency-problems/nordic

## Gherkin file

```gherkin
Feature: God Analysis API
# REST API: GET /api/v1/gods/stats/sum
# Notes:
# - Decimal Conversion Rule: For each name, convert each char to its Unicode int value,
#   then concatenate those ints as strings (e.g., "Zeus" -> Z(90)e(101)u(117)s(115) -> "90101117115").
# - If a selected source times out or fails on the single outbound attempt, aggregation continues with the remaining sources (no retries).
# - If every selected source times out or fails, the response is still HTTP 200 with sum "0" (no contributing names).
# - Filtering is case-sensitive. The documented source data uses uppercase initials, so "N" matches
#   Nike, Nemesis, Neptun, and Njord; lowercase "n" is valid but returns no matches for that data.
# - Greek API: https://my-json-server.typicode.com/jabrena/latency-problems/greek
# - Roman API: https://my-json-server.typicode.com/jabrena/latency-problems/roman
# - Nordic API: https://my-json-server.typicode.com/jabrena/latency-problems/nordic

Background:
    Given the God Analysis API is available at "/api/v1"
    And the system is configured with bounded HTTP connect and read timeouts for outbound source calls

Scenario: Happy path - Get sum with explicit sources
    When the client sends a GET request to "/gods/stats/sum" with query parameters "filter" = "N" and "sources" = "greek,roman,nordic"
    Then the response status code should be 200
    And the response body should contain a JSON object with a "sum" field
    And the value of "sum" should be "78179288397447443426"
```
