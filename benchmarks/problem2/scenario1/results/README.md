# Scenario 1 results — how to measure and store

Each completed Case 1 run MUST write one JSON file under this directory that conforms to the canonical harness schema:

- Schema: [metrics-v1.schema.json](../../metrics-v1.schema.json)
- Example: [metrics-v1.example.json](../../metrics-v1.example.json)

```text
benchmarks/scenario1/results/<run-id>.json
```

Suggested `run-id` pattern:

```text
YYYYMMDDTHHMMSSZ-<tool>-<model-slug>
```

Example: `20260717T180530Z-cursor-gpt5.json`

Do not commit secrets. Token/cost values may be estimates when the tool only exposes approximate usage.

The schema marks all fields optional; this scenario’s Gherkin feature requires **every** group and field to be populated for completed runs. See [scenario1.feature](../gherkin/scenario1.feature).

## Case-specific labels

| Field | Value for Case 1 |
| --- | --- |
| `protocol_labels.scenario` | `"scenario1"` |
| `protocol_labels.case_id` | `"case-1-readme-only"` |

Set `outcome_quality.acceptance_pass` to `true` only when the product happy path in `specs/functional-requirements/README.md` passes and this scenario’s setup constraints hold.

## Operator checklist (measure → store)

1. Confirm Case 1 setup (README-only FR) before starting.
2. Note `protocol_labels.commit`, `protocol_labels.tool`, `protocol_labels.model`, `protocol_labels.plinth_config`.
3. Start timer (`efficiency.wall_clock_s`).
4. Run the agent against `benchmarks/scenario1/demo/` using only the Case 1 README input.
5. Track `outcome_quality.rework_turns` and optional `protocol_labels.human_intervention_min`.
6. Stop timer when done; capture tokens, cost, and `plinth_usage` from the tool or operator tally.
7. Set `outcome_quality.acceptance_pass` from product + harness checks.
8. Capture `solution_snapshot` from `benchmarks/scenario1/demo/` before restore (for example `tree -a -I '.git' benchmarks/scenario1/demo/ | base64`) and set `solution_snapshot.file_count`.
9. Write `benchmarks/scenario1/results/<run-id>.json` conforming to [metrics-v1.schema.json](../../metrics-v1.schema.json).
10. Restore `benchmarks/scenario1/demo/` to empty (only `.gitkeep`).
11. Rank later using [benchmarks/README.md](../../README.md) rules (`outcome_quality.acceptance_pass = true` only).

See also: [scenario1.feature](../gherkin/scenario1.feature).
