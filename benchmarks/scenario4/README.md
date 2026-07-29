# Scenario 4 — Case 4 OpenSpec technical requirements

| Field | Value |
| --- | --- |
| Scenario | `scenario4` |
| Case id | `case-4-current-openspec-problem1` |
| Runnable | Yes |
| Richness | OpenSpec technical requirements (co-located functional requirements for derivation links only) |
| Results | `benchmarks/scenario4/results/<run-id>.json` |

## Input contract

Case 4 uses **OpenSpec technical requirements** as the sole implementation input:

- `benchmarks/scenario4/specs/technical-requirements/openspec/`

Includes the current OpenSpec project (`config.yaml`, README, and `changes/add-god-analysis-api/` with proposal, design, tasks, and specs).

OpenSpec **Source and Derivation** links MUST point at the co-located functional-requirements files under `benchmarks/scenario4/specs/functional-requirements/problem1/`, not at external `examples/openspec/...` paths as the scenario input authority.

The functional-requirements tree under `specs/functional-requirements/problem1/` is **not** provided to the agent as implementation input; it exists so OpenSpec derivation links resolve within the harness.

## Exclusions

Do **not** provide:

- `examples/openspec/god-analysis-api/` as scenario input authority (use the harness-local technical-requirements tree instead)
- The functional-requirements package as agent input (OpenSpec is the implementation input for this case)

## Purpose

Measure how agents perform when implementation input is OpenSpec technical requirements that are explicitly derived from and linked to co-located functional requirements — the canonical reference run for the richest step, executing `/implement-spec` with `@plinth-tech-lead` delegation, `@plinth-java-spring-boot-coder` implementation, and the Case 4 Spring Boot skill-discovery set from `.agents/skills/` or `skills/` (starting with `@042-planning-openspec`).

Use the canonical execution prompt only; do not prepend `/create-worktree` or other commands that bypass `/implement-spec` orchestration unless the user explicitly overrides the Case 4 protocol.

## Artifacts

- Technical requirements (OpenSpec): [specs/technical-requirements/openspec/](specs/technical-requirements/openspec/)
- Co-located functional requirements (derivation links only): [specs/functional-requirements/problem1/](specs/functional-requirements/problem1/)
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
execute @benchmarks/scenario4/gherkin/scenario4.feature
and verify that acceptance-tests passes.
```

Run constraints are defined in the feature file.
