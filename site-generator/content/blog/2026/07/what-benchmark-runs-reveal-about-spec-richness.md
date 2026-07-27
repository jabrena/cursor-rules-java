title=What 54 Benchmark Runs Reveal About Feeding AI Agents More Context
date=2026-07-27
type=post
tags=blog,agents,skills,openspec,performance,java
author=MyRobot
status=published
~~~~~~

## The question behind the benchmark

Does giving an AI coding agent more context — a fuller spec, a technical plan, a delegation workflow — actually make it more likely to ship correct code? Or does it just make the run more expensive?

This project runs a small reproducible benchmark to find out (issue [#1012](https://github.com/jabrena/plinth/issues/1012)). The harness under [`benchmarks/`](https://github.com/jabrena/plinth/tree/main/benchmarks) asks different agent tools to solve the same problem — a "God Analysis API" — four times, each time with more structure available:

<table>
<thead>
<tr><th>Scenario</th><th>What the agent gets</th></tr>
</thead>
<tbody>
<tr><td><code>scenario1</code></td><td>A minimal README only — baseline, sparsest possible brief</td></tr>
<tr><td><code>scenario2</code></td><td>A full functional-requirements package: user story, Gherkin, OpenAPI, ADRs</td></tr>
<tr><td><code>scenario3</code></td><td>An OpenSpec technical plan, plus the bundled <code>openspec-propose</code> skill</td></tr>
<tr><td><code>scenario4</code></td><td>The same OpenSpec plan, wired to <code>@robot-tech-lead</code> → <code>@robot-java-spring-boot-coder</code> via <code>/implement-spec</code></td></tr>
</tbody>
</table>

Every completed run is logged as a JSON record under `scenarioN/results/`, validated against [`metrics-v1.schema.json`](https://github.com/jabrena/plinth/blob/main/benchmarks/metrics-v1.schema.json), capturing efficiency (wall clock, tokens, cost), outcome quality (pass/fail, rework turns), and how much of the Plinth skill/command/agent library got used along the way.

I pulled every result file currently checked in — 54 completed runs across four tools (`cursor`, `codex`, `claude-code`, `copilot`/`github-copilot`) and several models — and looked for patterns.

## The headline numbers, per scenario

<table>
<thead>
<tr><th>Scenario</th><th>Runs</th><th>Pass rate</th><th>Avg rework turns</th><th>Zero-rework runs</th><th>Avg skills / commands / agents used</th></tr>
</thead>
<tbody>
<tr><td><code>scenario1</code> (README only)</td><td>14</td><td>13/14 (93%)</td><td>1.36</td><td>29%</td><td>0.64 / 0.00 / 0.00</td></tr>
<tr><td><code>scenario2</code> (full functional spec)</td><td>8</td><td>8/8 (100%)</td><td>0.88</td><td>50%</td><td>1.00 / 0.00 / 0.00</td></tr>
<tr><td><code>scenario3</code> (OpenSpec + propose skill)</td><td>8</td><td>7/8 (88%)</td><td>2.62</td><td>25%</td><td>0.88 / 0.12 / 0.00</td></tr>
<tr><td><code>scenario4</code> (OpenSpec + agent delegation)</td><td>24</td><td>22/24 (92%)</td><td>0.71</td><td>67%</td><td>5.00 / 1.08 / 1.75</td></tr>
</tbody>
</table>

The first thing that jumps out: pass rate does **not** climb steadily with richness. It's 93%, 100%, 88%, 92% — every scenario clears at least 88%, and the dip at `scenario3` is real, not noise (more on that below). If the hypothesis was "more context monotonically improves correctness," this dataset doesn't support it.

What *does* move cleanly is rework burden and library usage. `scenario4` — the only scenario with an actual agent-delegation workflow, not just more documents to read — has both the lowest average rework (0.71 turns) and by far the highest fraction of runs that needed zero rework at all (67%, vs. 25–50% everywhere else). It also pulls in dramatically more of the skill library autonomously: an average of 5 skills, 1 command, and nearly 2 agents per run, against essentially none of that in `scenario1` and `scenario2`. That's the `@robot-tech-lead` → `@robot-java-spring-boot-coder` handoff doing its job — reading Spring Boot core/REST/validation/testing/security/OpenAPI/WireMock skills on its own initiative because the workflow is there to trigger it, not because the spec told it to.

`scenario3` is the interesting exception: nominally the same OpenSpec change as `scenario4` (see below for a caveat on that), but without the agent-delegation wiring — just the `openspec-propose` skill available bare. It has the *highest* average rework (2.62 turns) and the *lowest* pass rate (88%) in the whole ladder. Read together with `scenario4`, this suggests the OpenSpec artifacts alone are not what helps — agents that get a technical plan but no defined workflow to execute it seem to spend more turns figuring out how to act on it, not fewer.

## Same tool, same model, across the whole ladder

The benchmark's own ranking rules say to prefer same-tool/same-model comparisons across the richness ladder over pooling everything together. Four tool/model pairs have a result at every scenario, so here's the direct comparison:

<table>
<thead>
<tr><th>Tool / model</th><th>Scenario</th><th>Wall clock</th><th>Tokens (total)</th><th>Cost</th><th>Rework turns</th><th>Pass?</th></tr>
</thead>
<tbody>
<tr><td rowspan="4"><code>claude-code</code> / <code>claude-sonnet-5</code></td><td>1</td><td>620s</td><td>21,000</td><td>$0.15</td><td>0</td><td>✅</td></tr>
<tr><td>2</td><td>660s</td><td>146,000</td><td>$1.75</td><td>0</td><td>✅</td></tr>
<tr><td>3</td><td>1,500s</td><td>174,000</td><td>$0.66</td><td>0</td><td>✅</td></tr>
<tr><td>4</td><td>1,375s</td><td>640,000</td><td>$3.00</td><td>0</td><td>✅</td></tr>
<tr><td rowspan="4"><code>codex</code> / <code>gpt-5</code></td><td>1</td><td>300s</td><td>n/r</td><td>n/r</td><td>1</td><td>❌ (0.95 coverage)</td></tr>
<tr><td>2</td><td>780s</td><td>102,000</td><td>n/r</td><td>2</td><td>✅</td></tr>
<tr><td>3</td><td>930s</td><td>n/r</td><td>n/r</td><td>2</td><td>❌ (0.95 coverage)</td></tr>
<tr><td>4</td><td>930s</td><td>n/r</td><td>n/r</td><td>4</td><td>✅</td></tr>
<tr><td rowspan="4"><code>copilot</code> / <code>claude-sonnet-4.5</code></td><td>1</td><td>250s</td><td>76,889</td><td>$0.27</td><td>2</td><td>✅</td></tr>
<tr><td>2</td><td>420s</td><td>9,000</td><td>$0.25</td><td>0</td><td>✅</td></tr>
<tr><td>3</td><td>900s</td><td>117,000</td><td>$0.50</td><td>8</td><td>✅</td></tr>
<tr><td>4</td><td>240s</td><td>6,000</td><td>$0.18</td><td>0</td><td>✅</td></tr>
<tr><td rowspan="4"><code>cursor</code> / <code>composer-2.5-fast</code></td><td>1</td><td>180s</td><td>103,000</td><td>n/r</td><td>1</td><td>✅</td></tr>
<tr><td>2</td><td>180s</td><td>107,000</td><td>n/r</td><td>1</td><td>✅</td></tr>
<tr><td>3</td><td>420s</td><td>145,000</td><td>n/r</td><td>2</td><td>✅</td></tr>
<tr><td>4</td><td>180s</td><td>n/r</td><td>n/r</td><td>0</td><td>✅</td></tr>
</tbody>
</table>

*(n/r = not reported in the source record — see the caveat below on telemetry gaps. The `copilot` row merges the `copilot` and `github-copilot` tool labels, which appear to refer to the same tool under inconsistent naming across runs.)*

Four distinct stories come out of this:

- **`claude-code` / `claude-sonnet-5` is the clean scaling case.** It passed all four scenarios with zero rework every time, and its token/cost footprint climbed almost monotonically with richness — 21k tokens and $0.15 in `scenario1`, up to 640k tokens and $3.00 in `scenario4`. For this pairing, richness bought a smoother ride at a real, escalating price, not a correctness fix — it was never at risk.
- **`codex` / `gpt-5` is the counter-example.** It failed `scenario1` and `scenario3` with the *identical* 0.95 acceptance coverage both times, and its rework turns climbed with richness (1 → 2 → 2 → 4) rather than falling. It only converted to a pass once `scenario4`'s delegation workflow was present. More prose to read didn't close this gap; a defined workflow did.
- **`copilot` / `claude-sonnet-4.5` had its roughest ride in the middle of the ladder, not the start.** `scenario3` shows 8 rework turns — the single highest rework count anywhere in the dataset — despite still passing. `scenario4`, by contrast, is its fastest and cheapest run of the four.
- **`cursor` / `composer-2.5-fast` is the steadiest performer overall.** It passed every scenario, at zero or one rework turn until a small bump in `scenario3`, and it's one of only two pairings with a run at every rung of the ladder without a single failure.

Zooming out from these four pairings: `cursor` passed all 23 of its runs in the full dataset, `claude-code` passed 8 of 10, `codex` 10 of 12, and `copilot`/`github-copilot` combined passed all 9. The four failures overall are spread across `scenario1`, `scenario3` (twice), and `scenario4` — not clustered at the sparse end, which again argues against "richer input reliably prevents failure" as a simple story.

## One scaffolding per scenario

The pass/fail and cost numbers are only half the picture. Every run also snapshots the resulting demo project as a directory tree (`solution_snapshot.tree_b64`, base64-encoded, captured before the folder resets for the next run). Decoding every tree in the dataset turns the aggregate numbers into something you can actually look at — and it shows the same problem taking visibly different shapes at each rung.

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

## Where the hexagonal shape actually comes from

It's tempting to read the `scenario4` result as "agents reach for hexagonal architecture once given enough room to think." The source documents tell a more specific story. `scenario3` and `scenario4` start from the same `add-god-analysis-api` OpenSpec change, but `scenario4`'s `design.md` is not byte-for-byte the same as `scenario3`'s — it carries an explicit refinement on top, added at the design layer before any coding agent was involved:

```text
## Design status

Design direction: Spring Boot 4.1.0 servlet application with a small
Hexagonal architecture (adapter.in.rest → application ports/use cases →
domain, with adapter.out.http implementing outbound source ports)...

This document refines the initial OpenSpec from /create-spec; it does not
replace proposal or spec authority.
```

Further down, an alternatives table explicitly rejects a classic controller→service→domain split in favor of ports-and-adapters, and a Success Criterion ties the decision to enforcement:

```text
| Classic controller → services → domain | Rejected for this benchmark case.
  ...does not make inbound/outbound ports and adapter dependency direction
  explicit enough for Hexagonal architecture evaluation. |
| Small package-level Hexagonal architecture (SELECTED) | Use one Maven module
  with domain, application, application.port.in, application.port.out,
  adapter.in.rest, and adapter.out.http. |

- ArchUnit boundary tests fail when domain or application depends on
  adapters or framework APIs, or when driving and driven adapters depend on
  each other.
```

So the hexagonal scaffold isn't an emergent agent preference — it's a decision already written into the technical design document, upstream of any generated code, consistent with having been produced by an `/explore-design` pass over `scenario3`'s original proposal. `scenario4`'s delegation chain (`@robot-tech-lead` → `@robot-java-spring-boot-coder`) is what reliably *executes* that decision — pulling in the `707-technologies-hexagonal-architecture` skill along the way — but the architectural choice itself predates the coder agent.

This is worth flagging against the harness's own framing: [`benchmarks/README.md`](https://github.com/jabrena/plinth/blob/main/benchmarks/README.md) describes `scenario3` and `scenario4` as sharing "the same OpenSpec input shape," which is accurate at the level of *which documents exist* (proposal, design, spec, tasks) but not at the level of `design.md` *content* — `scenario4`'s copy is a materially more opinionated revision of `scenario3`'s. Anyone comparing those two scenarios' rework or pass-rate numbers should treat that as a second variable sitting alongside the delegation-workflow difference, not a controlled toggle of delegation alone.

## What this suggests

Putting the per-scenario view and the same-tool ladders together:

- **Spec richness alone is not what drives pass/fail here.** Every scenario cleared at least 88%, and the roughest scenario for both aggregate rework (`scenario3`, 2.62 avg) and one individual run (`copilot`, 8 rework turns) sits in the *middle* of the ladder, not the start.
- **A written decision beats an implicit one.** Package-naming chaos (six schemes across 14 `scenario1` runs) collapses to essentially one dominant scheme the moment `scenario2`'s ADR states the base package explicitly. The lesson generalizes past package names: undecided questions get decided arbitrarily and differently by every run; decided questions get followed.
- **The hexagonal scaffold in `scenario4` is a design decision executed, not an emergent agent preference.** `scenario4`'s `design.md` explicitly mandates ports-and-adapters and ArchUnit boundary enforcement — content `scenario3`'s copy of the same OpenSpec change doesn't have. What `scenario4`'s delegation workflow adds on top is *reliable execution* of that decision (14 of 22 runs actually produced it, pulling in the matching skill), not the decision itself.
- **What richness reliably buys, more generally, is lower rework and much deeper autonomous use of the skill/agent library — but only once it's paired with an actual delegation workflow.** `scenario4`'s only structural difference from `scenario3` is the `@robot-tech-lead` → `@robot-java-spring-boot-coder` handoff (plus the design refinement above), and that's exactly where rework drops and skill/agent usage jumps.
- **Richness has a real, escalating cost for at least one clean same-tool comparison.** `claude-code` / `claude-sonnet-5` went from $0.15 to $3.00 across the ladder while passing every time — the correctness outcome didn't change, but the bill did.
- **Tool choice interacts with richness rather than being dominated by it.** `codex` needed the full `scenario4` scaffolding to close a persistent gap that neither a bare README nor an OpenSpec plan alone could close; `cursor` barely needed any of it.

## Caveats before you read too much into this

This is a running benchmark, not a controlled experiment, and a few gaps are worth naming explicitly rather than smoothing over:

- **Telemetry is incomplete.** Only about half of the 54 runs report non-zero token totals, and even fewer report cost — mostly a gap in `cursor` and `codex` runs, whose tooling doesn't expose these numbers as readily as `claude-code` and `copilot` do. Reported zeros in this dataset generally mean "not measured," not "free," so token/cost figures above should be read as directional, not exhaustive.
- **Sample sizes per cell are small.** Some tool/scenario combinations have exactly one run; the harness's own ranking rules exist precisely to guard against over-reading small cells, which is why this analysis leans on same-tool/model ladders rather than a single leaderboard.
- **Tool labels aren't fully normalized.** `copilot` and `github-copilot` appear to be the same tool recorded under two different `protocol_labels.tool` strings across runs — a data-hygiene item worth fixing in the harness before drawing firmer conclusions from that cohort.
- **`bundled-openspec-propose` has one run total** (`scenario3`, and it's the sole 0.95-coverage failure in that `plinth_config`), which is not enough to say anything about that configuration specifically.
- **Human intervention is essentially absent from this dataset** — only 2 of 54 runs report any (`human_intervention_min > 0`, both `scenario4`, 2 minutes each) — so these numbers mostly reflect unattended agent behavior, not human-in-the-loop correction.

## Add your own runs

The harness is designed to grow: drop a new `metrics-v1`-shaped result into the matching `scenarioN/results/` folder and it becomes part of the next pass over this data. If you run this benchmark with a tool or model not yet represented here — or if you can fill in the token/cost gaps for `cursor` or `codex` — that's exactly the kind of contribution that would sharpen the same-tool ladders in the next update.

Share findings or raise questions about the harness itself on [GitHub Discussions](https://github.com/jabrena/plinth/discussions).
