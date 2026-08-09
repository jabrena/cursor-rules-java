# Plinth project benchmark harness (problem2)

Reproducible **project effectiveness** benchmark for comparing agent outcomes on the **Greek Gods API** problem as **information richness increases** from Scenario 1 through Scenario 5 (issue [#1012](https://github.com/jabrena/plinth/issues/1012)).

This harness is documentation and campaign protocol only (Markdown + Gherkin). It is **not** a Maven module and is **not** part of the JVM `/benchmark` command.

Sibling harness: [problem1](../problem1/) (God Analysis API). Do not mix inputs across problems.

## Scenarios (Cases 1–5)

Each runnable scenario adds structured inputs so agents have more to build from than the previous case. Scenarios are columns; each row explains one dimension of the information-richness ladder.

| | `scenario1` | `scenario2` | `scenario3` | `scenario4` | `scenario5` |
| --- | --- | --- | --- | --- | --- |
| **Richness level** | Minimal | Full functional package | OpenSpec technical requirements | OpenSpec technical requirements | OpenSpec technical requirements |
| **Case id** | `case-1-readme-only` | `case-2-all-functional-requirements` | `case-3-current-openspec-problem2` | `case-4-current-openspec-problem2` | `case-5-direct-openspec-problem2` |
| **Functional input** | `specs/functional-requirements/README.md` only | `specs/functional-requirements/agile/` + `design/` (user story, Gherkin, OpenAPI, ADRs, schema, diagrams) | — | — | — |
| **Implementation input** | — | — | `specs/technical-requirements/openspec/` | `specs/technical-requirements/openspec/` | `specs/technical-requirements/openspec/` |
| **Bundled Plinth skills** | — | — | `openspec-propose` under `.agents/skills/` | `@042-planning-openspec/` under `.agents/skills/` or `skills/` | — (discovery only; no mandated skill in Then) |
| **Plinth agents** | — | — | — | `@plinth-tech-lead` → `@plinth-java-quarkus-coder` via `/implement-spec` | Agents/skills discovery allowed; `/implement-spec` forbidden |
| **Runnable** | Yes | Yes | Yes | Yes | Yes |
| **Delta vs previous scenario** | Baseline: sparse functional notes only | Adds full `agile/` + `design/` inventory; still no OpenSpec | Adds OpenSpec implementation input **plus** bundled propose skill | Same OpenSpec input shape as Case 3; command-driven via `/implement-spec` | Same OpenSpec input; **direct** implementation (no `/implement-spec`) |
| **Intent** | Measure baseline with minimal notes | Measure rich FR without OpenSpec | Measure implementation from OpenSpec with propose-workflow support | Measure implementation from pre-linked OpenSpec via Plinth command/agents | Measure direct OpenSpec implementation vs Case 4 orchestration |

Campaigns SHOULD compare cost/tokens/quality across this ladder for the same product acceptance outcome.

## Layout

```text
benchmarks/problem2/
├── README.md
├── metrics-v1.schema.json    # canonical run metrics JSON Schema
├── metrics-v1.example.json   # fully populated example; scenarios require all fields
├── acceptance-tests-prompts.md
├── scenario1/                # minimal functional notes
│   ├── README.md
│   ├── specs/functional-requirements/README.md
│   └── gherkin/scenario1.feature
├── scenario2/                # full functional requirements
│   ├── README.md
│   ├── specs/functional-requirements/agile/
│   ├── specs/functional-requirements/design/
│   └── gherkin/scenario2.feature
├── scenario3/                # OpenSpec + bundled openspec-propose
│   ├── README.md
│   ├── .agents/skills/openspec-propose/
│   ├── specs/
│   │   ├── functional-requirements/   # derivation links only (not agent input)
│   │   └── technical-requirements/openspec/
│   └── gherkin/scenario3.feature
├── scenario4/                # OpenSpec via /implement-spec
│   ├── README.md
│   ├── specs/
│   │   ├── functional-requirements/   # derivation links only (not agent input)
│   │   └── technical-requirements/openspec/
│   └── gherkin/scenario4.feature
└── scenario5/                # OpenSpec direct implementation
    ├── README.md
    ├── specs/
    │   ├── functional-requirements/   # derivation links only (not agent input)
    │   └── technical-requirements/openspec/
    └── gherkin/scenario5.feature
```

Each scenario folder owns its input contract (`README.md`), requirements under `specs/`, acceptance criteria (`gherkin/` with exactly one `@acceptance-test` scenario), and run records under `results/` (JSON per completed run conforming to [metrics-v1.schema.json](metrics-v1.schema.json)).

Input paths above are relative to each scenario folder (for example `benchmarks/problem2/scenario2/specs/functional-requirements/agile/`). OpenSpec changes use `add-greek-gods-api`. Runnable scenario authority is the harness-local `specs/` trees. Do not use `benchmarks/problem1/` or rewrite upstream example trees as part of a campaign run.

## Metrics scorecard

Canonical field definitions and JSON Schema for every scenario execution record:

- Schema: [metrics-v1.schema.json](metrics-v1.schema.json)
- Example: [metrics-v1.example.json](metrics-v1.example.json)

Each run record is a JSON object with up to five top-level groups (`efficiency`, `outcome_quality`, `protocol_labels`, `plinth_usage`, `solution_snapshot`). **All groups and fields are optional in the schema.** Each scenario Gherkin feature (`scenarioN/gherkin/scenarioN.feature`) defines which fields MUST be populated for completed runs in that case (currently: all fields in every scenario).

### `efficiency`

| Field | Meaning |
| --- | --- |
| `efficiency.wall_clock_s` | Elapsed wall-clock seconds for the run |
| `efficiency.active_agent_s` | Seconds the agent was actively working (excludes idle wait where measurable) |
| `efficiency.tokens_in` / `efficiency.tokens_out` | Prompt/completion token breakdown |
| `efficiency.tokens_total` | Total tokens for the run |
| `efficiency.cost_usd` | Estimated or billed USD cost for the run |

### `outcome_quality`

| Field | Meaning |
| --- | --- |
| `outcome_quality.acceptance_pass` | Boolean: whether the run met the scenario `@acceptance-test` outcome |
| `outcome_quality.acceptance_coverage` | Share of acceptance criteria satisfied |
| `outcome_quality.rework_turns` | Number of human or agent rework turns after the first attempt |
| `outcome_quality.artifact_completeness` | Whether required deliverables for the case were produced |

### `protocol_labels`

| Field | Meaning |
| --- | --- |
| `protocol_labels.scenario` | Folder id: `scenario1` … `scenario5` |
| `protocol_labels.case_id` | Stable case id (for example `case-1-readme-only`) |
| `protocol_labels.tool` | Agent tool used for the run |
| `protocol_labels.model` | Model identifier used for the run |
| `protocol_labels.plinth_config` | Plinth configuration level |
| `protocol_labels.commit` | Git commit SHA of the workspace under test |
| `protocol_labels.retry_count` | Number of retries for this run |
| `protocol_labels.human_intervention_min` | Minutes of human intervention |

### `plinth_usage`

| Field | Meaning |
| --- | --- |
| `plinth_usage.skills_count` | Number of distinct skills read or invoked (must equal the length of `plinth_usage.skills` when both are present) |
| `plinth_usage.commands_count` | Number of distinct Plinth commands read or invoked from `.cursor/commands/` (must equal the length of `plinth_usage.commands` when both are present) |
| `plinth_usage.agents_count` | Number of distinct Plinth agents invoked from `.cursor/agents/` (must equal the length of `plinth_usage.agents` when both are present) |
| `plinth_usage.skills` | Skill ids used during the run |
| `plinth_usage.commands` | Plinth command ids from `.cursor/commands/` used during the run (for example `implement-spec`) |
| `plinth_usage.agents` | Plinth agent ids from `.cursor/agents/` invoked during the run (for example `plinth-tech-lead`); not the host tool model |

### `solution_snapshot`

| Field | Meaning |
| --- | --- |
| `solution_snapshot.demo_root` | Repository-relative demo path snapshotted (for example `benchmarks/problem2/scenario1/demo/`) |
| `solution_snapshot.tree_format` | Format of the decoded tree payload (currently `unix-tree`) |
| `solution_snapshot.tree_encoding` | Encoding of the tree payload (currently `base64`) |
| `solution_snapshot.tree_b64` | Base64-encoded directory tree of `demo_root` at run completion; capture before restoring `demo/` |
| `solution_snapshot.file_count` | Number of files under `demo_root` excluding `.gitkeep` when the snapshot was taken |

### Schema vs scenario requirements

- **Schema** ([metrics-v1.schema.json](metrics-v1.schema.json)): defines allowed shape and types only; nothing is required at validation time.
- **Scenario Gherkin**: defines mandatory population for completed runs. Cases 1–5 currently require every group and every field listed above.

See the `@acceptance-test` scenario in each `scenarioN/gherkin/scenarioN.feature` for the authoritative population checklist.

### Ranking rules

1. Rank by `efficiency.cost_usd` and/or `efficiency.tokens_total` only among runs where `outcome_quality.acceptance_pass = true`.
2. Keep `outcome_quality.acceptance_pass = false` runs in a separate failure cohort; do not mix them into the cost/token leaderboard.
3. Prefer same-tool/same-model cells when comparing Scenario 1 → 2 → 3 → 4 → 5 richness steps.
