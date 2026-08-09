# Scenario 1 — Case 1 minimal functional notes

| Field | Value |
| --- | --- |
| Scenario | `scenario1` |
| Case id | `case-1-readme-only` |
| Runnable | Yes |
| Richness | Minimal — README-only functional notes |
| Product | Greek Gods API |
| Results | `benchmarks/problem2/scenario1/results/<run-id>.json` |

## Input contract

Harness-local functional requirements (only this file):

- `benchmarks/problem2/scenario1/specs/functional-requirements/README.md`

Treat that README as a sparse Greek Gods API note: `GET /api/v1/gods/greek` returns records with `id` and `name`; my-json-server `/greek` is the synchronization source (JSON array of names); upsert by natural key `name` and preserve existing generated IDs.

## Exclusions

Do **not** use as agent input:

- Companion artifacts under `specs/functional-requirements/` other than `README.md` (`greek_gods.feature`, OpenAPI contracts, schema, diagrams), even if they exist on disk
- A full agile/design functional-requirements package (user story, ADRs, plans)
- Any `technical-requirements/` / OpenSpec tree
- Other problem2 scenarios (`scenario2`–`scenario5`)
- `benchmarks/problem1/`
- `examples/openspec/`

## Purpose

Baseline: measure how agents perform with **sparse** Greek Gods API functional notes only before richer packaging in Scenario 2 and OpenSpec technical plans in Scenarios 3–5. Agents may use the same Plinth skills and tooling as other runnable scenarios.

## Artifacts

- Functional notes: [specs/functional-requirements/README.md](specs/functional-requirements/README.md)
- Acceptance: [gherkin/scenario1.feature](gherkin/scenario1.feature)
- Results guide: [results/README.md](results/README.md)
- Example result JSON: [results/example.result.json](results/example.result.json)

## Metrics and results JSON

Every completed run MUST persist one JSON file under `results/` conforming to [metrics-v2.schema.json](../metrics-v2.schema.json):

- Schema: [../metrics-v2.schema.json](../metrics-v2.schema.json)
- Example: [../metrics-v2.example.json](../metrics-v2.example.json)
- Case template: [results/example.result.json](results/example.result.json)

Canonical definitions for all scenarios: [../README.md](../README.md).

## Execution prompt

Use this prompt to run Scenario 1 against the Gherkin feature:

```bash
execute @benchmarks/problem2/scenario1/gherkin/scenario1.feature
and verify that acceptance-tests passes.
```

Run constraints are defined in the feature file.
