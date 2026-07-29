title=Validating hypotheses about Plinth workflow with a Benchmark
date=2026-07-28
type=post
tags=blog,agents,skills,openspec,performance,java
author=MyRobot
status=published
~~~~~~

## The questions behind the benchmark

During the last months, the project has evolved from a complete `Skills folder` for `Java` to provide an `AI-native development workflow` for `Java`, more or less this project lives in the Third generation (`Systemp prompts` -> `Skills` -> `AI-Native development Workflow`). During this time, any inquisitive user could ask for evidences about what is the real value using this development approach and this is the motivation to create the benchmark and this article share few insights.

[The benchmark](https://github.com/jabrena/plinth/tree/main/benchmarks) has been designed to asks different agent tools to solve the same problem — a "God Analysis API", the first project from [Latency problems](https://github.com/jabrena/latency-problems) — four times, each time with more structure available:

<table>
<thead>
<tr><th>Scenario</th><th>What the agent gets</th></tr>
</thead>
<tbody>
<tr><td><code>scenario1</code></td><td>A minimal README only — baseline, sparsest possible brief</td></tr>
<tr><td><code>scenario2</code></td><td>A full functional-requirements package: user story, Gherkin, OpenAPI, ADRs</td></tr>
<tr><td><code>scenario3</code></td><td>An OpenSpec change created by the official <a href="https://www.skills.sh/fission-ai/openspec/openspec-propose"><code>openspec-propose</code></a> skill using as input the same functional requirements from scenario 2.</td></tr>
<tr><td><code>scenario4</code></td><td>An OpenSpec change created by the Plinth skill <code>/create-spec</code> and enriched with <code>/explore-design</code> and finally implemented via <code>/implement-spec</code>.</td></tr>
</tbody>
</table>

Every completed run is logged as a JSON record under `scenarioN/results/`, validated against [`metrics-v1.schema.json`](https://github.com/jabrena/plinth/blob/main/benchmarks/metrics-v1.schema.json), capturing efficiency (wall clock, tokens, cost), outcome quality (pass/fail, rework turns), and how much of the Plinth skill/command/agent library got used along the way.

I pulled every result file currently checked in — 54 completed runs across four tools (`cursor`, `codex`, `claude-code`, `copilot`/`github-copilot`) and several models — and looked for patterns.

With the data gathered, lets review the following hypotheses:

- **Hypothesis 1:** Richer workflows reduce implementation rework.
- **Hypothesis 2:** Delegation workflows encourage autonomous use of reusable skills.
- **Hypothesis 3:** Written architectural decisions improve consistency.

## Hypothesis 1: Richer workflows reduce implementation rework

If this hypothesis holds, pass rate should climb and rework should fall as each scenario adds more structure. The table below breaks that down per scenario — pass rate, average rework turns, and the share of runs that needed no rework at all:

<table>
<thead>
<tr><th>Scenario</th><th>Inputs</th><th>Runs</th><th>Pass rate</th><th>Avg rework turns</th><th>Zero-rework runs</th></tr>
</thead>
<tbody>
<tr><td><code>scenario1</code></td><td>README only</td><td>14</td><td>13/14 (93%)</td><td>1.36</td><td>29%</td></tr>
<tr><td><code>scenario2</code></td><td>Full functional spec</td><td>8</td><td>8/8 (100%)</td><td>0.88</td><td>50%</td></tr>
<tr><td><code>scenario3</code></td><td>Full functional spec + Pure OpenSpec</td><td>8</td><td>7/8 (88%)</td><td>2.62</td><td>25%</td></tr>
<tr><td><code>scenario4</code></td><td>Full functional spec + Plinth OpenSpec + Plinth implementation</td><td>24</td><td>22/24 (92%)</td><td>0.71</td><td>67%</td></tr>
</tbody>
</table>

Pass rate alone doesn't climb steadily with richness — it's 93%, 100%, 88%, 92% across the four scenarios, so more input material by itself isn't the story here.

The real signal is `scenario4`, the only scenario that runs the full Plinth workflow — `/create-spec` and `/explore-design` producing a refined plan, then `/implement-spec` delegating to `@plinth-tech-lead` and framework-specific coder agents. It has the lowest average rework (0.71 turns), the highest zero-rework share (67%, vs. 25–50% everywhere else), and pulls in far more of the skill library on its own initiative (5 skills, 1 command, nearly 2 agents per run, against essentially none in `scenario1` and `scenario2`).

`scenario3` makes the contrast concrete: the same OpenSpec change as `scenario4`, but produced with just the `openspec-propose` skill and none of Plinth's delegation commands. It's the *worst* performer in the ladder — highest average rework (2.62 turns), lowest pass rate (88%). The documents alone aren't what helps; it's Plinth's commands turning those documents into an executed, delegated workflow.

## Hypothesis 2: Delegation workflows encourage autonomous use of reusable skills.

Are you sure your agent tools are actually using the skills you wrote for them? Hypothesis 2 puts that question to the data: does a delegation workflow get agents to reach for the skill/command/agent library on their own, without being told to? The table below tracks just that — how many skills, commands, and agents each scenario's runs pulled in, on average:

<table>
<thead>
<tr><th>Scenario</th><th>Inputs</th><th>Runs</th><th>Avg skills</th><th>Avg commands</th><th>Avg agents</th></tr>
</thead>
<tbody>
<tr><td><code>scenario1</code></td><td>README only</td><td>14</td><td>0.64</td><td>0.00</td><td>0.00</td></tr>
<tr><td><code>scenario2</code></td><td>Full functional spec</td><td>8</td><td>1.00</td><td>0.00</td><td>0.00</td></tr>
<tr><td><code>scenario3</code></td><td>Full functional spec + Pure OpenSpec</td><td>8</td><td>0.88</td><td>0.12</td><td>0.00</td></tr>
<tr><td><code>scenario4</code></td><td>Full functional spec + Plinth OpenSpec + Plinth implementation</td><td>24</td><td>5.00</td><td>1.08</td><td>1.75</td></tr>
</tbody>
</table>

`scenario4` is the only scenario with an actual agent-delegation workflow, and it shows: 5 skills, 1 command, and nearly 2 agents per run on average, against close to nothing everywhere else. But that aggregate hides a real question — is this a property of the workflow, or of one tool that happens to make up half of `scenario4`'s 24-run sample? Breaking `scenario4`'s skill discovery down by tool, and comparing it against each tool's own `scenario1`–`scenario3` baseline, answers that:

<table>
<thead>
<tr><th>Tool</th><th><code>scenario4</code> runs</th><th>Avg skills used (<code>scenario4</code>)</th><th>Avg skills used (<code>scenario1</code>–<code>3</code>)</th></tr>
</thead>
<tbody>
<tr><td><code>codex</code></td><td>6</td><td>10.83</td><td>1.50</td></tr>
<tr><td><code>claude-code</code></td><td>4</td><td>7.00</td><td>1.67</td></tr>
<tr><td><code>cursor</code></td><td>12</td><td>2.17</td><td>0.36</td></tr>
<tr><td><code>github-copilot</code></td><td>2</td><td>0.50</td><td>0.14</td></tr>
</tbody>
</table>

The direction holds for every tool: each one's own `scenario4` average beats its own `scenario1`–`3` average, so this isn't one tool's habit skewing the aggregate. The magnitude doesn't hold, though. `codex` and `claude-code` lean hard on the library (10.83 and 7.00 skills per run), `cursor` picks up a more modest amount (2.17), and `copilot` barely engages with it at all — 0.50 average, and only 1 of its 2 checked-in `scenario4` runs touched a skill or agent. That `copilot` cell is too thin (2 runs) to say whether that's the tool or just the sample. Hypothesis 2 is supported directionally across the board, but the size of the effect is very tool-dependent.

**Note:** In coming releases, we will review if exist ways to do a Skill discovery at OpenSpec level, not only at Implementation phase.

## Hiphotesis 3: Written architectural decisions improve consistency.

The pass/fail and cost numbers are only half the picture. Every run also snapshots the resulting demo project as a directory tree (`solution_snapshot.tree_b64`, base64-encoded, captured before the folder resets for the next run). Decoding every tree in the dataset turns the aggregate numbers into something you can actually look at — and it shows the same problem taking visibly different shapes at each rung.

### Claude-code results

Holding the tool constant (`claude-code` / `claude-sonnet-5`) and pruning to source files only:

**`scenario1`** — flat, single package, no layering at all:

```text
info/jab/ms/
├── GodAnalysisApplication.java
├── GodSource.java
├── GodSourceClient.java
├── GodSourceClientConfig.java
├── GodStatsController.java
├── GodStatsService.java
└── GodStatsSumResponse.java
```

**`scenario2`** — still one flat package, but the same category of exception-handling scaffolding `codex` adds shows up here too (`ErrorResponse`, `GlobalExceptionHandler`, `InvalidRequestException`), plus a `RestClientConfig` — just without splitting a `config/` subpackage out of it:

```text
info/jab/ms/
├── ErrorResponse.java
├── GlobalExceptionHandler.java
├── GodAnalysisApiApplication.java
├── GodSourceProperties.java
├── GodStatsService.java
├── GodStatsSumResponse.java
├── GodsController.java
├── InvalidRequestException.java
└── RestClientConfig.java
```

**`scenario3`** — a `gods` subpackage appears, and the same OpenAPI contract file `codex` produces shows up here too, pulled straight from the OpenSpec input:

```text
info/jab/ms/
├── GodAnalysisApplication.java
└── gods/
    ├── ApiExceptionHandler.java
    ├── BadRequestException.java
    ├── GodNameConverter.java
    ├── GodSourceProperties.java
    ├── GodStatsController.java
    ├── GodStatsResponse.java
    ├── GodStatsService.java
    └── Source.java
resources/openapi/god-analysis-api.yaml
```

**`scenario4`** — full ports-and-adapters, with a dedicated architecture-boundary test:

```text
info/jab/ms/
├── GodAnalysisApplication.java
├── adapter/
│   ├── in/rest/
│   │   ├── GlobalExceptionHandler.java
│   │   ├── GodStatsController.java
│   │   └── GodStatsResponse.java
│   └── out/http/
│       ├── GodAnalysisProperties.java
│       ├── RestClientConfig.java
│       └── RestGodSourceClient.java
├── application/
│   ├── GodSourceFetchException.java
│   ├── GodStatsUseCase.java
│   └── port/
│       ├── in/QueryGodStats.java
│       └── out/GodSourceClient.java
└── domain/
    ├── GodNameFilter.java
    ├── GodStatsAggregator.java
    ├── PantheonSource.java
    └── UnicodeNameConverter.java

test/.../architecture/HexagonalArchitectureTest.java
```

Same story, different tool: flat at `scenario1`, a light exception/config layer at `scenario2`, an OpenAPI-informed flat package at `scenario3`, and the full hexagonal split with an ArchUnit boundary test at `scenario4`. The `domain` and `adapter/out/http` packages here match `codex`'s `scenario4` tree below file-for-file, same class names included — direct evidence that the shape traces back to the written design decision, not to either tool's own architectural taste.

### Codex results

Holding the tool constant (`codex` / `gpt-5`) and pruning to source files only:

**`scenario1`** — flat, single package, no layering at all:

```text
info/jab/gods/
├── GodAnalysisApplication.java
├── GodSource.java
├── GodSourceClient.java
├── GodStatsController.java
├── GodStatsService.java
└── HttpGodSourceClient.java
```

**`scenario2`** — a `config` package appears, and explicit exception types show up now that Gherkin scenarios spell out error cases:

```text
info/jab/ms/
├── GodAnalysisApplication.java
├── config/
│   ├── GodSourceConfig.java
│   └── GodSourceProperties.java
└── gods/
    ├── ApiExceptionHandler.java
    ├── BadRequestException.java
    ├── GodSourceClient.java
    ├── GodStatsController.java
    ├── GodStatsResponse.java
    ├── GodStatsService.java
    └── Source.java
```

**`scenario3`** — flat again (the `config` split collapses back into one `gods` package), but an OpenAPI contract file shows up for the first time, pulled straight from the OpenSpec input:

```text
info/jab/ms/
├── GodAnalysisApplication.java
└── gods/
    ├── GodAnalysisProperties.java
    ├── GodSourceClient.java
    ├── GodStatsController.java
    ├── GodStatsExceptionHandler.java
    ├── GodStatsService.java
    ├── InvalidGodStatsRequestException.java
    ├── RestClientConfig.java
    ├── RestGodSourceClient.java
    ├── SourceKey.java
    └── SumResponse.java
resources/openapi/god-analysis-api.yaml
```

**`scenario4`** — full ports-and-adapters, with a dedicated architecture-boundary test:

```text
info/jab/ms/
├── GodAnalysisApplication.java
├── adapter/
│   ├── in/rest/
│   │   ├── GlobalExceptionHandler.java
│   │   └── GodStatsController.java
│   └── out/http/
│       ├── GodAnalysisProperties.java
│       ├── RestClientConfig.java
│       └── RestGodSourceClient.java
├── application/
│   ├── GodStatsUseCase.java
│   └── port/
│       ├── in/QueryGodStats.java
│       └── out/GodSourceClient.java
└── domain/
    ├── GodNameFilter.java
    ├── GodStatsAggregator.java
    ├── PantheonSource.java
    └── UnicodeNameConverter.java

test/.../architecture/HexagonalArchitectureTest.java
```

A driving REST adapter, a driven HTTP adapter, an application layer with explicit inbound/outbound ports, a framework-free domain package, and a `HexagonalArchitectureTest` — an ArchUnit-style boundary test that fails the build if `domain` or `application` ever import an adapter type.

This isn't one lucky tool. Grepping every decoded tree in the dataset for hexagonal markers (`adapter/`, `port/`, `domain/` as sibling packages) turns up **zero hits across all 14 `scenario1` runs, all 7 `scenario2` runs, and all 7 `scenario3` runs** — and **14 of 22 `scenario4` runs** (roughly two-thirds), spread across `cursor`, `codex`, `claude-code`, and `copilot` alike. `scenario3` runs stay flat or add a shallow `config`/`controller`/`service` split at most — never the driving/driven port distinction.

There's a second, quieter form of "mess" underneath this: base package naming. `scenario1` alone produces six different naming schemes across 14 runs for the identical problem — `info.jab.ms`, `info.jab.gods`, `com.example.gods`, `info.jab.benchmark.godanalysis`, `info.jab.benchmarks.godanalysis`, and one run with no package at all. From `scenario2` onward that collapses to essentially two schemes (`info.jab.ms` dominant, plus one tool's own habitual `info.jab.benchmark...`), because `scenario2`'s functional-requirements package includes an ADR (`ADR-003-God-Analysis-API-Technology-Stack.md`) that states outright: "Use `info.jab.ms` as the base package." Once that decision exists anywhere in the input, almost every tool follows it — the naming chaos in `scenario1` is a direct, traceable consequence of nobody having written the decision down.

## What this suggests

Putting the per-scenario view and the same-tool ladders together, here's how the three hypotheses from the top of this post held up against the data:

- **Hypothesis 1 — richer workflows reduce implementation rework: partially supported.** Richness by itself doesn't help: `scenario3` — the same OpenSpec change as `scenario4`, but produced with the bare `openspec-propose` skill and none of Plinth's delegation commands — has the *highest* average rework in the whole ladder (2.62 turns), not the lowest. `scenario4` — same OpenSpec input, plus Plinth's `/create-spec` → `/explore-design` → `/implement-spec` workflow — has the lowest (0.71 turns) and the highest zero-rework share (67%). Rework only drops when richness is paired with Plinth's delegated execution, not from richer documents by themselves.
- **Hypothesis 2 — delegation workflows encourage autonomous use of reusable skills: supported, but tool-dependent.** `scenario4` is the only scenario with real agent delegation, and it pulls in an average of 5 skills, 1 command, and nearly 2 agents per run — against essentially zero everywhere else, including `scenario3`, which has the same OpenSpec documents but no delegation wiring. Every tool's own `scenario4` average beats its own `scenario1`–`3` baseline, but the size of the effect varies widely: `codex` and `claude-code` pull in 7–11 skills per run, `cursor` around 2, and `copilot` barely engages (0.50, on a thin 2-run sample). 

> This analysis is very relevant because if you don´t ask the AI-Agent tool to review your skills, models will solve the issues in their way in many cases.

- **Hypothesis 3 — written architectural decisions improve consistency: supported.** Package-naming chaos (six different schemes across 14 `scenario1` runs) collapses to one dominant scheme the moment `scenario2`'s ADR states the base package explicitly. The hexagonal scaffold in `scenario4` follows the same pattern: it's mandated in `design.md`, not an emergent agent preference — 14 of 22 `scenario4` runs produced it once that decision existed in writing, versus zero hits across every `scenario1`–`scenario3` run.

## Add your own runs

The harness is designed to grow: drop a new `metrics-v1`-shaped result into the matching `scenarioN/results/` folder and it becomes part of the next pass over this data. If you run this benchmark with a tool or model not yet represented here — or if you can fill in the token/cost gaps for `cursor` or `codex`, or close the `copilot`/`github-copilot` `scenario2` gap with a second, consistently-labeled sample — that's exactly the kind of contribution that would sharpen the same-tool ladders in the next update.

Share findings or raise questions about the harness itself on [GitHub Discussions](https://github.com/jabrena/plinth/discussions).
