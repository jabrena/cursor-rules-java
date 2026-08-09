# OpenSpec Project: Greek Gods API

This OpenSpec project is derived from the co-located functional requirements in
[`../../functional-requirements`](../../functional-requirements/).

## Derivation

- Source direction: requirements, Gherkin, OpenAPI, ADRs, schema, and plan -> OpenSpec change.
- Change boundary: one atomic API capability, `greek-gods-api`.
- Pending change: `add-greek-gods-api`.
- Capability delta: `changes/add-greek-gods-api/specs/greek-gods-api/spec.md`.

The functional-requirements tree remains authoritative for this scenario. The
OpenSpec change records the planned capability and links back to those files.

## Layout

```text
technical-requirements/openspec/
├── config.yaml
├── README.md
└── changes/
    └── add-greek-gods-api/
        ├── proposal.md
        ├── design.md
        ├── tasks.md
        └── specs/
            └── greek-gods-api/
                └── spec.md
```
