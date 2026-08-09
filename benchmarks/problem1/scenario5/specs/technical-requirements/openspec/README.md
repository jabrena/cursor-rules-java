# OpenSpec Project: God Analysis API

This OpenSpec project is derived from the co-located functional requirements in
[`../../functional-requirements/problem1`](../../functional-requirements/problem1/).

## Derivation

- Source direction: requirements, Gherkin, OpenAPI, and ADRs -> OpenSpec change.
- Change boundary: one atomic API capability, `god-analysis-api`.
- Pending change: `add-god-analysis-api`.
- Capability delta: `changes/add-god-analysis-api/specs/god-analysis-api/spec.md`.

The functional-requirements tree remains authoritative for this scenario. The
OpenSpec change records the planned capability and links back to those files.

## Layout

```text
technical-requirements/openspec/
├── config.yaml
├── README.md
└── changes/
    └── add-god-analysis-api/
        ├── .openspec.yaml
        ├── proposal.md
        ├── design.md
        ├── tasks.md
        └── specs/
            └── god-analysis-api/
                └── spec.md
```
