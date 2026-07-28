title=Proving few hypotheses about the AI-native development workflow with a Benchmark
date=2026-07-27
type=post
tags=blog,agents,skills,openspec,performance,java
author=MyRobot
status=published
~~~~~~

## The questions behind the benchmark

During the last months, the project has evolved from a complete `Skills folder` for `Java` to provide an `AI-native development workflow` for `Java`. During this time, any inquisitive user could ask for evidences about what is the real value using this approach and this is the motivation to create the benchmark and this article share few insights.

The benchmark asks different agent tools to solve the same problem — a "God Analysis API" — four times, each time with more structure available:

<table>
<thead>
<tr><th>Scenario</th><th>What the agent gets</th></tr>
</thead>
<tbody>
<tr><td><code>scenario1</code></td><td>A minimal README only — baseline, sparsest possible brief</td></tr>
<tr><td><code>scenario2</code></td><td>A full functional-requirements package: user story, Gherkin, OpenAPI, ADRs</td></tr>
<tr><td><code>scenario3</code></td><td>An OpenSpec technical plan, plus the bundled <code>openspec-propose</code> skill</td></tr>
<tr><td><code>scenario4</code></td><td>An OpenSpec change created by <code>/create-spec</code> and enriched with <code>/explore-design</code> and finally implemented via <code>/implement-spec</code>.</td></tr>
</tbody>
</table>

Every completed run is logged as a JSON record under `scenarioN/results/`, validated against [`metrics-v1.schema.json`](https://github.com/jabrena/plinth/blob/main/benchmarks/metrics-v1.schema.json), capturing efficiency (wall clock, tokens, cost), outcome quality (pass/fail, rework turns), and how much of the Plinth skill/command/agent library got used along the way.

I pulled every result file currently checked in — 54 completed runs across four tools (`cursor`, `codex`, `claude-code`, `copilot`/`github-copilot`) and several models — and looked for patterns.

That data will try to validated the following hypotheses:

- **Hypothesis 1:** Richer workflows reduce implementation rework.
- **Hypothesis 2:** Delegation workflows encourage autonomous use of reusable skills.
- **Hypothesis 3:** Written architectural decisions improve consistency.

Let's review the different analysis.

## Hypothesis 1: Richer workflows reduce implementation rework

If this hypothesis holds, pass rate should climb and rework should fall as each scenario adds more structure. The table below breaks that down per scenario — pass rate, average rework turns, and the share of runs that needed no rework at all:

<table>
<thead>
<tr><th>Scenario</th><th>Runs</th><th>Pass rate</th><th>Avg rework turns</th><th>Zero-rework runs</th></tr>
</thead>
<tbody>
<tr><td><code>scenario1</code> (README only)</td><td>14</td><td>13/14 (93%)</td><td>1.36</td><td>29%</td></tr>
<tr><td><code>scenario2</code> (full functional spec)</td><td>8</td><td>8/8 (100%)</td><td>0.88</td><td>50%</td></tr>
<tr><td><code>scenario3</code> (OpenSpec + propose skill)</td><td>8</td><td>7/8 (88%)</td><td>2.62</td><td>25%</td></tr>
<tr><td><code>scenario4</code> (OpenSpec + agent delegation)</td><td>24</td><td>22/24 (92%)</td><td>0.71</td><td>67%</td></tr>
</tbody>
</table>

The first thing that jumps out: pass rate does **not** climb steadily with richness. It's 93%, 100%, 88%, 92% — every scenario clears at least 88%, and the dip at `scenario3` is real, not noise (more on that below). If the hypothesis was "more context monotonically improves correctness," this dataset doesn't support it.

What *does* move cleanly is rework burden and library usage. `scenario4` — the only scenario with an actual agent-delegation workflow, not just more documents to read — has both the lowest average rework (0.71 turns) and by far the highest fraction of runs that needed zero rework at all (67%, vs. 25–50% everywhere else). It also pulls in dramatically more of the skill library autonomously: an average of 5 skills, 1 command, and nearly 2 agents per run, against essentially none of that in `scenario1` and `scenario2`. That's the `@robot-tech-lead` → `@robot-java-spring-boot-coder` handoff doing its job — reading Spring Boot core/REST/validation/testing/security/OpenAPI/WireMock skills on its own initiative because the workflow is there to trigger it, not because the spec told it to.

`scenario3` is the interesting exception: nominally the same OpenSpec change as `scenario4`, but without the agent-delegation wiring — just the `openspec-propose` skill available bare. It has the *highest* average rework (2.62 turns) and the *lowest* pass rate (88%) in the whole ladder. Read together with `scenario4`, this looks like evidence that the OpenSpec artifacts alone are not what helps. Treat that reading as provisional, though: as the "Where the hexagonal shape actually comes from" section below shows, `scenario3`'s `design.md` was never taken through the `/explore-design` refinement pass that produced `scenario4`'s copy, so the two scenarios differ in design-document content, not delegation wiring alone. Some of `scenario3`'s extra rework may be agents re-deriving a design decision (hexagonal boundaries, an ArchUnit enforcement test) that `scenario4`'s agents were simply handed in writing — on top of, not instead of, the missing execution workflow.

## Hypothesis 2: Delegation workflows encourage autonomous use of reusable skills.

Hypothesis 1 asked whether richness lowers rework. Hypothesis 2 asks a narrower question: does an actual delegation workflow get agents to reach for the reusable skill/command/agent library on their own, without the spec telling them to? Pass rate and rework aren't relevant to that question, so the table below drops those columns from the previous one and keeps only what is — how many skills, commands, and agents each scenario's runs pulled in, on average:

<table>
<thead>
<tr><th>Scenario</th><th>Runs</th><th>Avg skills</th><th>Avg commands</th><th>Avg agents</th></tr>
</thead>
<tbody>
<tr><td><code>scenario1</code> (README only)</td><td>14</td><td>0.64</td><td>0.00</td><td>0.00</td></tr>
<tr><td><code>scenario2</code> (full functional spec)</td><td>8</td><td>1.00</td><td>0.00</td><td>0.00</td></tr>
<tr><td><code>scenario3</code> (OpenSpec + propose skill)</td><td>8</td><td>0.88</td><td>0.12</td><td>0.00</td></tr>
<tr><td><code>scenario4</code> (OpenSpec + agent delegation)</td><td>24</td><td>5.00</td><td>1.08</td><td>1.75</td></tr>
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

## Hiphotesis 3: Written architectural decisions improve consistency.

The pass/fail and cost numbers are only half the picture. Every run also snapshots the resulting demo project as a directory tree (`solution_snapshot.tree_b64`, base64-encoded, captured before the folder resets for the next run). Decoding every tree in the dataset turns the aggregate numbers into something you can actually look at — and it shows the same problem taking visibly different shapes at each rung.

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

- **Hypothesis 1 — richer workflows reduce implementation rework: partially supported.** Richness alone doesn't do it: `scenario3` (OpenSpec plus a skill, richer than `scenario1`/`scenario2`) has the *highest* average rework in the whole ladder (2.62 turns), not the lowest. But `scenario4` — the same OpenSpec input shape, plus a `design.md` refined by `/explore-design`, plus an actual delegation workflow — has the lowest (0.71 turns) and the highest zero-rework share (67%). Rework drops when richness is paired with delegation and with a design already refined before the coding agent sees it, not from richness by itself; this benchmark can't yet separate how much of that drop the refined design versus the delegation workflow is each responsible for.
- **Hypothesis 2 — delegation workflows encourage autonomous use of reusable skills: supported.** `scenario4` is the only scenario with real agent delegation, and it pulls in an average of 5 skills, 1 command, and nearly 2 agents per run — against essentially zero everywhere else, including `scenario3`, which has the same OpenSpec documents but no delegation wiring.
- **Hypothesis 3 — written architectural decisions improve consistency: supported.** Package-naming chaos (six schemes across 14 `scenario1` runs) collapses to one dominant scheme the moment `scenario2`'s ADR states the base package explicitly. The hexagonal scaffold in `scenario4` follows the same pattern: it's mandated in `design.md`, not an emergent agent preference, and 14 of 22 `scenario4` runs actually produced it once that decision existed in writing.

A few more patterns showed up beyond the three hypotheses:

- **Spec richness alone is not what drives pass/fail here.** Every scenario cleared at least 88%, and the roughest scenario for both aggregate rework (`scenario3`, 2.62 avg) and one individual run (`copilot`, 8 rework turns) sits in the *middle* of the ladder, not the start.
- **Richness has a real, escalating cost for at least one clean same-tool comparison.** `claude-code` / `claude-sonnet-5` went from $0.15 to $3.00 across the ladder while passing every time — the correctness outcome didn't change, but the bill did.
- **Tool choice interacts with richness rather than being dominated by it.** `codex` needed the full `scenario4` scaffolding to close a persistent gap that neither a bare README nor an OpenSpec plan alone could close; `cursor` barely needed any of it.
- **A single "worst rework" outlier can hide a strong underlying record.** `copilot`/`github-copilot` posted the dataset's single highest rework count (8, in `scenario3`) yet still passed all 9 of its checked-in runs — the highest raw pass rate of any tool alongside `cursor`. Looking at one row per scenario, as the same-tool ladder does, makes an outlier look like a trend; looking at the full cohort shows it's one run out of three at that scenario, not a pattern.

## Add your own runs

The harness is designed to grow: drop a new `metrics-v1`-shaped result into the matching `scenarioN/results/` folder and it becomes part of the next pass over this data. If you run this benchmark with a tool or model not yet represented here — or if you can fill in the token/cost gaps for `cursor` or `codex`, or close the `copilot`/`github-copilot` `scenario2` gap with a second, consistently-labeled sample — that's exactly the kind of contribution that would sharpen the same-tool ladders in the next update.

Share findings or raise questions about the harness itself on [GitHub Discussions](https://github.com/jabrena/plinth/discussions).
