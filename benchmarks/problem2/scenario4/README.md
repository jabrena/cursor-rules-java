# Scenario 4 — Case 4 OpenSpec technical requirements

| Field | Value |
| --- | --- |
| Scenario | `scenario4` |
| Case id | `case-4-current-openspec-problem2` |
| Runnable | Yes |
| Richness | OpenSpec technical requirements via `/implement-spec` |
| Product | Greek Gods API |
| Results | `benchmarks/problem2/scenario4/results/<run-id>.json` |

## Input contract

Case 4 uses **OpenSpec technical requirements** as the sole implementation input:

- `benchmarks/problem2/scenario4/specs/technical-requirements/openspec/`

Includes the OpenSpec project (`config.yaml`, README, and `changes/add-greek-gods-api/` with proposal, design, tasks, and specs).

OpenSpec **Source and Derivation** links MUST point at the co-located functional-requirements files under `benchmarks/problem2/scenario4/specs/functional-requirements/`, not at external `examples/openspec/...` paths as the scenario input authority.

The functional-requirements tree under `specs/functional-requirements/` is **not** provided to the agent as implementation input; it exists so OpenSpec derivation links resolve within the harness.

Treat the OpenSpec change `add-greek-gods-api` as the Greek Gods API specification: `GET /api/v1/gods/greek` returns records with `id` and `name` from PostgreSQL table `greek_god` via a local read model; my-json-server `/greek` is the synchronization source (JSON array of names); upsert by natural key `name` and preserve existing generated IDs. Hexagonal Ports and Adapters is OpenSpec/domain context only (skill `707` is optional and not mandated in Then).

## Exclusions

Do **not** use as agent input:

- `specs/functional-requirements/` as direct requirements or technology choices
- Other problem2 scenarios (`scenario1`–`scenario3`, `scenario5`)
- `benchmarks/problem1/`
- `examples/openspec/`

## Purpose

Measure how agents perform when implementation input is OpenSpec technical requirements executed through `/implement-spec` with `@plinth-tech-lead` delegation, `@plinth-java-quarkus-coder` implementation, and `@042-planning-openspec` orchestration support from `.agents/skills/` or `skills/`.

Use the canonical execution prompt only; do not prepend `/create-worktree` or other commands that bypass `/implement-spec` orchestration unless the user explicitly overrides the Case 4 protocol.

## Artifacts

- Technical requirements (OpenSpec): [specs/technical-requirements/openspec/](specs/technical-requirements/openspec/)
- Co-located functional requirements (derivation links only): [specs/functional-requirements/](specs/functional-requirements/)
- Acceptance: [gherkin/scenario4.feature](gherkin/scenario4.feature)
- Results guide: [results/README.md](results/README.md)
- Example result JSON: [results/example.result.json](results/example.result.json)

## Metrics and results JSON

Every completed run MUST persist one JSON file under `results/` conforming to [metrics-v1.schema.json](../metrics-v1.schema.json):

- Schema: [../metrics-v1.schema.json](../metrics-v1.schema.json)
- Example: [../metrics-v1.example.json](../metrics-v1.example.json)
- Case template: [results/example.result.json](results/example.result.json)

Canonical definitions for all scenarios: [../README.md](../README.md).

## Execution prompt

Use this prompt to run Scenario 4 against the Gherkin feature:

```bash
execute @benchmarks/problem2/scenario4/gherkin/scenario4.feature
and verify that acceptance-tests passes.
```

Run constraints are defined in the feature file.
