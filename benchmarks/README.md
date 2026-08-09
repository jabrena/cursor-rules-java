# Plinth benchmarks

Reproducible **project-effectiveness** harnesses for comparing how coding agents (and Plinth skills, commands, and agents) perform on the same product outcome as **information richness increases**.

Each problem folder is documentation and campaign protocol only (Markdown + Gherkin + metrics schema). It is **not** a Maven module and is **not** part of the JVM `/benchmark` command.

## Idea

Coding agents are sensitive to how much structured context they receive: a sparse README, a full functional-requirements package, or an OpenSpec technical plan with Plinth orchestration.

These benchmarks keep the **acceptance outcome fixed** and vary the **input richness** across scenarios (typically Scenario 1 → Scenario 5). Campaigns record efficiency and quality metrics per run so results can be compared across tools, models, and Plinth configurations.

Typical questions the harness answers:

- Does richer functional packaging improve acceptance pass rate or reduce cost/tokens?
- Does OpenSpec (with or without `/implement-spec` and Plinth agents) outperform README-only or FR-only runs?
- How do different agent tools and models compare on the same scenario ladder?

## Upstream problem statements

The product problems are adapted from [jabrena/latency-problems](https://github.com/jabrena/latency-problems) — a set of asynchronous, non-blocking, and parallelism challenges that add latency as part of the exercise.

| Harness | Product | Original statement |
| --- | --- | --- |
| [problem1](problem1/) | God Analysis API (filter, Unicode decimal conversion, multi-source sum) | [Problem 1](https://github.com/jabrena/latency-problems/blob/master/docs/problem1/README.md) |
| [problem2](problem2/) | Greek Gods API (periodic sync from a third-party service) | [Problem 5](https://github.com/jabrena/latency-problems/blob/master/docs/problem5/README.md) |

Do **not** mix inputs across problems. Runnable scenario authority is always the harness-local `specs/` trees under each scenario folder.

## Layout

```text
benchmarks/
├── README.md                 # this file
├── problem1/                 # God Analysis API (latency-problems Problem 1)
│   ├── README.md             # scenario ladder, metrics, ranking rules
│   ├── acceptance-tests-prompts.md
│   ├── metrics-v1.schema.json
│   └── scenario1/ … scenario5/
└── problem2/                 # Greek Gods API (latency-problems Problem 5)
    ├── README.md
    ├── acceptance-tests-prompts.md
    ├── metrics-v1.schema.json
    └── scenario1/ … scenario5/
```

Each problem README documents its scenario ladder, metrics scorecard, and ranking rules. Start there before running a campaign.
