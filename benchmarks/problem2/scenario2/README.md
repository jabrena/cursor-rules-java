# Scenario 2 — Case 2 full functional requirements

| Field | Value |
| --- | --- |
| Scenario | `scenario2` |
| Case id | `case-2-all-functional-requirements` |
| Runnable | Yes |
| Richness | Full functional requirements package (no OpenSpec) |
| Product | Greek Gods API |
| Results | `benchmarks/problem2/scenario2/results/<run-id>.json` |

## Input contract

Harness-local functional requirements roots:

- `benchmarks/problem2/scenario2/specs/functional-requirements/agile/`
- `benchmarks/problem2/scenario2/specs/functional-requirements/design/`

### Full file inventory (MUST provide all)

- `benchmarks/problem2/scenario2/specs/functional-requirements/agile/US-001_API_Greek_Gods_Data_Retrieval.md`
- `benchmarks/problem2/scenario2/specs/functional-requirements/agile/US-001_api_greek_gods_data_retrieval.feature`
- `benchmarks/problem2/scenario2/specs/functional-requirements/agile/PLAN-US-001_Implementation.plan.md`
- `benchmarks/problem2/scenario2/specs/functional-requirements/design/ADR-001_REST_API_Functional_Requirements.md`
- `benchmarks/problem2/scenario2/specs/functional-requirements/design/ADR-002-Acceptance-Testing-Strategy.md`
- `benchmarks/problem2/scenario2/specs/functional-requirements/design/ADR-003-Greek-Gods-API-Technology-Stack.md`
- `benchmarks/problem2/scenario2/specs/functional-requirements/design/greekController-oas.yaml`
- `benchmarks/problem2/scenario2/specs/functional-requirements/design/my-json-server-oas.yaml`
- `benchmarks/problem2/scenario2/specs/functional-requirements/design/schema.sql`
- `benchmarks/problem2/scenario2/specs/functional-requirements/design/greek_gods_api_sequence_diagram.puml`
- `benchmarks/problem2/scenario2/specs/functional-requirements/design/greek_gods_api_sequence_diagram.png`

Treat that agile/ + design/ package as a Greek Gods API specification: `GET /api/v1/gods/greek` returns records with `id` and `name`; my-json-server `/greek` is the synchronization source (JSON array of names).

## Exclusions

Do **not** use as agent input:

- Other trees under `specs/functional-requirements/` (for example `requirements/` or `requirements3/`), even if they exist on disk
- Any `technical-requirements/` / OpenSpec tree
- Other problem2 scenarios (`scenario1`, `scenario3`–`scenario5`)
- `benchmarks/problem1/`
- `examples/openspec/`

## Purpose

Next richness step after Scenario 1: measure how agents perform with a **complete functional requirements package** (user story, Gherkin, OpenAPI, ADRs, schema, diagrams) before adding OpenSpec technical requirements in Scenarios 3–5.

## Artifacts

- Functional requirements (agile): [specs/functional-requirements/agile/](specs/functional-requirements/agile/)
- Functional requirements (design): [specs/functional-requirements/design/](specs/functional-requirements/design/)
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
execute @benchmarks/problem2/scenario2/gherkin/scenario2.feature
and verify that acceptance-tests passes.
```

Run constraints are defined in the feature file.
