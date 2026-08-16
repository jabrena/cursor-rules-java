# Scenario 4 results — how to measure and store

Each completed Case 4 run MUST write one JSON file under this directory that
conforms to the canonical harness schema:

- Schema: [metrics-v2.schema.json](../../metrics-v2.schema.json)
- Example: [metrics-v2.example.json](../../metrics-v2.example.json)

```text
benchmarks/problem2/scenario4/results/<run-id>.json
```

Suggested `run-id` pattern:

```text
YYYYMMDDTHHMMSSZ-<tool>-<model-slug>
```

Example: `20260717T180530Z-cursor-gpt5.json`

Do not commit secrets. Token/cost values may be estimates when the tool only exposes approximate usage.

The schema marks all fields optional; this scenario's Gherkin feature requires
**every** group and field to be populated for completed runs. See
[scenario4.feature](../gherkin/scenario4.feature).

## Case-specific labels

| Field | Value for Case 4 |
| --- | --- |
| `protocol_labels.scenario` | `"scenario4"` |
| `protocol_labels.case_id` | `"case-4-current-openspec-problem2"` |

Set `outcome_quality.acceptance_pass` to `true` only when the product
acceptance checks and the Scenario 4 `/implement-spec` orchestration constraints
hold.

## Operator checklist (measure → store)

1. Confirm the Case 4 OpenSpec input and `/implement-spec` orchestration setup
   before starting.
2. Note `protocol_labels.commit`, `protocol_labels.tool`, `protocol_labels.model`, `protocol_labels.plinth_config`.
3. Start timer (`efficiency.wall_clock_s`).
4. Run the agent against `benchmarks/problem2/scenario4/demo/` using only the
   Case 4 allowlisted OpenSpec input.
5. Track `outcome_quality.rework_turns` and optional `protocol_labels.human_intervention_min`.
6. Stop timer when done; capture tokens, cost, and `plinth_usage` from the tool or operator tally.
7. Set `outcome_quality.acceptance_pass` from product + harness checks.
8. Capture `solution_snapshot` from `benchmarks/problem2/scenario4/demo/`
   before restore, including the tree and `pom.xml` Base64 payloads, and set
   `solution_snapshot.file_count`.
9. Write `benchmarks/problem2/scenario4/results/<run-id>.json` conforming to
   [metrics-v2.schema.json](../../metrics-v2.schema.json).
10. Restore `benchmarks/problem2/scenario4/demo/` to empty (only `.gitkeep`).
11. Rank later using [benchmarks/README.md](../../README.md) rules (`outcome_quality.acceptance_pass = true` only).

See also:

- [scenario4.feature](../gherkin/scenario4.feature)
