# Scenario 5 — Case 5 direct OpenSpec implementation

| Field | Value |
| --- | --- |
| Scenario | `scenario5` |
| Case id | `case-5-direct-openspec-problem2` |
| Runnable | Yes |
| Richness | OpenSpec technical requirements (direct implementation; no `/implement-spec`) |
| Product | Greek Gods API |
| Results | `benchmarks/problem2/scenario5/results/<run-id>.json` |

## Input contract

Case 5 uses **OpenSpec technical requirements** as the sole implementation input:

- `benchmarks/problem2/scenario5/specs/technical-requirements/openspec/`

Includes the OpenSpec project (`config.yaml`, README, and `changes/add-greek-gods-api/` with proposal, design, tasks, and specs).

OpenSpec **Source and Derivation** links MUST point at the co-located functional-requirements files under `benchmarks/problem2/scenario5/specs/functional-requirements/`, not at external `examples/openspec/...` paths as the scenario input authority.

The functional-requirements tree under `specs/functional-requirements/` is **not** provided to the agent as implementation input; it exists so OpenSpec derivation links resolve within the harness.

Treat the OpenSpec change `add-greek-gods-api` as the Greek Gods API specification: `GET /api/v1/gods/greek` returns records with `id` and `name` from PostgreSQL table `greek_god` via a local read model; my-json-server `/greek` is the synchronization source (JSON array of names); upsert by natural key `name` and preserve existing generated IDs. Hexagonal Ports and Adapters is OpenSpec/domain context only (skill `707` is optional and not mandated in Then).

## Exclusions

Do **not** use as agent input:

- `.cursor/commands/implement-spec.md` (must not be read or invoked)
- `specs/functional-requirements/` as direct requirements or technology choices
- Other problem2 scenarios (`scenario1`–`scenario4`)
- `benchmarks/problem1/`
- `examples/openspec/`

## Purpose

Compare **direct** OpenSpec implementation (no `/implement-spec` orchestration) with Scenario 4 command-driven OpenSpec. Agents and skills under `.cursor/agents/`, `.agents/skills/`, or `skills/` may be used; the model decides which are relevant and records only those actually read or invoked. Case 5 does **not** mandate any skill in `plinth_usage.skills`. Completed runs MUST record `plinth_usage.commands_count` as `0` and an empty `plinth_usage.commands` list.

## Artifacts

- Technical requirements (OpenSpec): [specs/technical-requirements/openspec/](specs/technical-requirements/openspec/)
- Co-located functional requirements (derivation links only): [specs/functional-requirements/](specs/functional-requirements/)
- Acceptance: [gherkin/scenario5.feature](gherkin/scenario5.feature)
- Results guide: [results/README.md](results/README.md)
- Example result JSON: [results/example.result.json](results/example.result.json)

## Metrics and results JSON

Every completed run MUST persist one JSON file under `results/` conforming to [metrics-v1.schema.json](../metrics-v1.schema.json):

- Schema: [../metrics-v1.schema.json](../metrics-v1.schema.json)
- Example: [../metrics-v1.example.json](../metrics-v1.example.json)
- Case template: [results/example.result.json](results/example.result.json)

Canonical definitions for all scenarios: [../README.md](../README.md).

## Execution prompt

Use this prompt to run Scenario 5 against the Gherkin feature:

```bash
execute @benchmarks/problem2/scenario5/gherkin/scenario5.feature
and verify that acceptance-tests passes.
```

Run constraints are defined in the feature file.
