# Scenario 2 — Case 2 full functional requirements

| Field | Value |
| --- | --- |
| Scenario | `scenario2` |
| Case id | `case-2-all-problem1-requirements` |
| Runnable | Yes |
| Richness | Full functional requirements package (no OpenSpec) |
| Results | `benchmarks/scenario2/results/<run-id>.json` |

## Input contract

Harness-local functional requirements root:

- `benchmarks/scenario2/specs/functional-requirements/problem1/`

### Full file inventory (MUST provide all)

- `benchmarks/scenario2/specs/functional-requirements/problem1/README.md`
- `benchmarks/scenario2/specs/functional-requirements/problem1/US-001_God_Analysis_API.md`
- `benchmarks/scenario2/specs/functional-requirements/problem1/US-001_god_analysis_api.feature`
- `benchmarks/scenario2/specs/functional-requirements/problem1/US-001-god-analysis-api.openapi.yaml`
- `benchmarks/scenario2/specs/functional-requirements/problem1/my-json-server-oas.yaml`
- `benchmarks/scenario2/specs/functional-requirements/problem1/ADR-001-God-Analysis-API-Functional-Requirements.md`
- `benchmarks/scenario2/specs/functional-requirements/problem1/ADR-002-God-Analysis-API-Non-Functional-Requirements.md`
- `benchmarks/scenario2/specs/functional-requirements/problem1/ADR-003-God-Analysis-API-Technology-Stack.md`

## Exclusions

Do **not** provide:

- `benchmarks/scenario2/specs/technical-requirements/` (no technical OpenSpec in Case 2)
- `examples/openspec/god-analysis-api/openspec/changes/` as scenario input

## Purpose

Next richness step after Scenario 1: measure how agents perform with a **complete functional requirements package** (user story, Gherkin, OpenAPI, ADRs) before adding OpenSpec technical requirements in Scenario 4.

## Artifacts

- Functional requirements: [specs/functional-requirements/problem1/](specs/functional-requirements/problem1/)
- Acceptance: [gherkin/scenario2.feature](gherkin/scenario2.feature)
- Results guide: [results/README.md](results/README.md)
- Example result JSON: [results/example.result.json](results/example.result.json)

## Metrics and results JSON

Every completed run MUST persist one JSON file under `results/` conforming to [metrics-v1.schema.json](../metrics-v1.schema.json):

- Schema: [../metrics-v1.schema.json](../metrics-v1.schema.json)
- Example: [../metrics-v1.example.json](../metrics-v1.example.json)
- Case template: [results/example.result.json](results/example.result.json)

Canonical definitions for all scenarios: [../README.md](../README.md).

## Execution prompt

Use this prompt to run Scenario 2 against the Gherkin feature:

```bash
execute @benchmarks/scenario2/gherkin/scenario2.feature
and verify that acceptance-tests passes.
```

Run constraints are defined in the feature file.
