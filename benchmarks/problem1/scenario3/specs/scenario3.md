# Scenario 3 specification — Case 3 OpenSpec technical requirements

## Case identity

- **Scenario:** `scenario3`
- **Case id:** `case-3-current-openspec-problem1`

## Status

**Runnable.** Case 3 uses OpenSpec technical requirements as the sole implementation input.

## Input contract

- **Implementation input:** `benchmarks/scenario3/specs/technical-requirements/openspec/`
- **Co-located functional requirements:** `benchmarks/scenario3/specs/functional-requirements/problem1/` exists only for OpenSpec derivation links; agents MUST NOT read it directly as input.
- **Bundled skill:** `benchmarks/scenario3/.agents/skills/openspec-propose/SKILL.md`

## Exclusions

- `examples/openspec/god-analysis-api/` as scenario input authority
- Other benchmark scenarios (`scenario1`, `scenario2`, `scenario4`)
- Direct reads of the co-located functional-requirements package for requirements or technology choices

## Acceptance

- Harness: [gherkin/scenario3.feature](../gherkin/scenario3.feature) (`@acceptance-test`)
- Product happy path: OpenSpec spec at `specs/technical-requirements/openspec/changes/add-god-analysis-api/specs/god-analysis-api/spec.md`

## Results

Each completed run MUST write one JSON file under `benchmarks/scenario3/results/` conforming to [metrics-v1.schema.json](../../metrics-v1.schema.json). See [results/README.md](../results/README.md).
