title=Validating hypotheses about Plinth workflow with a Benchmark Part 2
date=2026-09-05
type=post
tags=blog,agents,skills,openspec,performance,java
author=MyRobot
status=published
~~~~~~

This is the second article in a series that tries to put a number on the value of Plinth's AI-native development workflow for Java. [Part 1](https://jabrena.github.io/plinth/blog/2026/07/validating-hypotheses-about-plinth-workflow-with-a-benchmark-part-1.html) built a [benchmark](https://github.com/jabrena/plinth/tree/main/benchmarks) that hands the same problem to several agent tools under briefs of increasing structure, logs every run as a validated JSON record, and uses those records to test three hypotheses about whether richer workflows actually pay off. The benchmark tried to validate the followed Hyphoteses:

- **Hypothesis 1:** Richer workflows reduce implementation rework.
- **Hypothesis 2:** Delegation workflows encourage autonomous use of reusable skills.
- **Hypothesis 3:** Written architectural decisions improve consistency.

Part 1 ended with one scenario clearly ahead — but it could not say *why*, because that scenario changed two things at once: the OpenSpec plan and the orchestration command that executes it. Part 2 sets out to separate them. The goals here are concrete:

- **Isolate the command from the plan.** A new `scenario5` runs the exact same OpenSpec technical plan as `scenario4`, implemented directly, with `/implement-spec` and its agents forbidden. `scenario4` vs `scenario5` is then a clean A/B: identical plan, orchestration the only variable.
- **Check whether the answer is problem-dependent.** A second problem — a persistence-and-scheduling "Greek Gods API" — joins the original fan-out-and-sum one, so every finding can be read per problem.
- **Re-test all three hypotheses on a much larger dataset.** The corpus has grown from 54 runs to 217 non-template records across two problems, five scenarios, four tools, and a spread of models, with outlier trimming applied consistently.

## What is new in Part 2

Two additions to the harness:

<table>
<thead>
<tr><th>Addition</th><th>What it is</th></tr>
</thead>
<tbody>
<tr><td><code>scenario5</code></td><td>The <strong>same</strong> OpenSpec technical plan as <code>scenario4</code> — same <code>specs/technical-requirements/openspec/</code> tree — implemented <strong>directly</strong>. <code>/implement-spec</code> is forbidden; there is no mandated agent or skill. Agents and skills are still <em>allowed</em> if the tool reaches for them on its own.</td></tr>
<tr><td><a href="https://github.com/jabrena/plinth/tree/main/benchmarks/problem2">Problem 2</a></td><td>The <strong>Greek Gods API</strong> (<a href="https://github.com/jabrena/latency-problems/blob/master/docs/problem5/README.md">latency-problems Problem 5</a>): a REST API that periodically syncs a Greek-gods catalogue from a third-party service into a relational database via Flyway. It is a persistence-and-scheduling problem, not a pure fan-out-and-sum one.</td></tr>
</tbody>
</table>

So `scenario4` (**S4**, orchestrated) versus `scenario5` (**S5**, direct) is a clean A/B: identical plan, and the only difference is whether `/implement-spec` and its agents run. Doing this across two different problems shows whether the answer is problem-dependent.

The dataset has also grown a lot — from the 54 runs in Part 1 to **217 non-template result records** across both problems, five scenarios, and tools including `cursor`, `codex`, `claude-code`, and `github-copilot` on a spread of models (Grok 4.5, GPT-5.x, Claude Sonnet 5 / Opus 5 / Fable 5, Composer).

### How long the two problems actually take

Those fences come from the full per-problem wall-clock distribution — every scenario, tool, and model pooled together. It is worth looking at that distribution on its own, because it answers the question people ask before they ask anything about workflows: *how long is one of these runs?*

<table>
<thead>
<tr><th>Problem</th><th>Positive timings</th><th>Q1</th><th>Median</th><th>Q3</th><th>IQR</th><th>Tukey upper fence</th><th>High outliers</th><th>Non-outlier range</th></tr>
</thead>
<tbody>
<tr><td><strong>Problem 1</strong> (God Analysis)</td><td>117</td><td>307 s (5:07 min)</td><td>560 s (9:20 min)</td><td>1,003 s (16:43 min)</td><td>696 s</td><td>2,047 s (34:07 min)</td><td>7</td><td><strong>51–1,908 s</strong> (0:51–31:48 min)</td></tr>
<tr><td><strong>Problem 2</strong> (Greek Gods)</td><td>98</td><td>366 s (6:06 min)</td><td>665 s (11:05 min)</td><td>1,400 s (23:20 min)</td><td>1,034 s</td><td>2,951 s (49:11 min)</td><td>2</td><td><strong>136–2,446 s</strong> (2:16–40:46 min)</td></tr>
</tbody>
</table>

Problem 2 is the harder job by every measure: its median run is about two minutes longer, its middle 50% of runs span **6 to 23 minutes** against Problem 1's 5 to 17, and its spread (IQR) is half as wide again. The persistence-plus-scheduling problem simply takes more building than the fan-out-and-sum one — a useful planning bound in its own right, independent of which workflow produced it. It also explains why the two Tukey fences land so far apart (**2,047 s for Problem 1**, **2,951 s for Problem 2**), and why a run that would be a clear outlier on Problem 1 can be an ordinary long run on Problem 2.

After trimming, the current-workflow cohort (S4 = only `plinth-*` agents recorded; S5 = no legacy `robot-*` agent) is:

<table>
<thead>
<tr><th></th><th>Raw records</th><th>Wall-time outliers</th><th>Retained</th></tr>
</thead>
<tbody>
<tr><td><strong>S4</strong> (orchestrated)</td><td>56</td><td>6</td><td><strong>50</strong></td></tr>
<tr><td><strong>S5</strong> (direct)</td><td>71</td><td>3</td><td><strong>68</strong></td></tr>
</tbody>
</table>

## Hypothesis 1 revisited: does the command reduce rework once you already have the plan?

Here is the pooled, trimmed picture:

<table>
<thead>
<tr><th>Cohort</th><th>Runs</th><th>Pass rate</th><th>Avg rework turns</th><th>Zero-rework runs</th><th>Median wall time</th></tr>
</thead>
<tbody>
<tr><td><strong>S4</strong> — <code>/implement-spec</code> + agents</td><td>50</td><td>49/50 (98%)</td><td><strong>0.76</strong></td><td>44%</td><td>15:52 min (952 s)</td></tr>
<tr><td><strong>S5</strong> — direct implementation</td><td>68</td><td>65/68 (96%)</td><td>1.21</td><td>35%</td><td><strong>10:12 min</strong> (612 s)</td></tr>
</tbody>
</table>

On the pooled numbers S4 comes out ahead on every reliability column — higher pass rate, lower average rework, more zero-rework runs — while S5 is about **36% faster by median wall time**. That is a real trade-off, not a free win: the command costs you roughly a third of your wall-clock budget to buy a lower correction rate.

But "pooled" hides an interaction, and the per-problem split is where it shows up:

<table>
<thead>
<tr><th>Problem</th><th>Cohort</th><th>Pass rate</th><th>Avg rework</th><th>Zero-rework</th><th>Median wall</th></tr>
</thead>
<tbody>
<tr><td rowspan="2">Problem 1 (God Analysis)</td><td>S4</td><td>17/18</td><td><strong>0.78</strong></td><td>39%</td><td>14:32 min (872 s)</td></tr>
<tr><td>S5</td><td>28/30</td><td>1.00</td><td><strong>50%</strong></td><td><strong>9:15 min</strong> (555 s)</td></tr>
<tr><td rowspan="2">Problem 2 (Greek Gods)</td><td>S4</td><td><strong>32/32</strong></td><td><strong>0.75</strong></td><td><strong>47%</strong></td><td>18:11 min (1,091 s)</td></tr>
<tr><td>S5</td><td>37/38</td><td>1.37</td><td>24%</td><td><strong>11:05 min</strong> (665 s)</td></tr>
</tbody>
</table>

On **Problem 2** — the database problem — S4 is cleanly better on reliability: a perfect 32/32 pass rate, roughly half the average rework, and double the zero-rework share. It also pays for it with a bigger latency premium here (median 1,091 s vs 665 s, about 64% slower).

On **Problem 1**, the orchestration advantage mostly evaporates. Pass rates are within a run of each other, S5 has *more* zero-rework runs, and S5 is materially faster. S4 keeps a slight edge on average rework and nothing else.

### It is also model-dependent

Breaking the trimmed cohort down by model family shows the rework advantage is not universal:

<table>
<thead>
<tr><th>Model family</th><th>Retained runs (S4 / S5)</th><th>What the data shows</th></tr>
</thead>
<tbody>
<tr><td>Cursor Grok 4.5</td><td>19 / 20</td><td>Every run passes. S5 has <em>lower</em> rework (0.35 vs 0.68), more zero-rework runs (70% vs 32%), and is faster. A clean counterexample to a model-independent S4 benefit.</td></tr>
<tr><td>Codex GPT-5.x</td><td>13 / 23</td><td>S4 13/13 pass at 1.15 rework; S5 22/23 at 2.04. S5 about 41% faster. Favours S4 on reliability, S5 on speed.</td></tr>
<tr><td>Claude Sonnet 5</td><td>13 / 18</td><td>S4 12/13 pass, 0.62 rework, 62% zero-rework; S5 16/18, 1.11 rework, 28% zero-rework. S4-leaning on reliability; S5 only ~11% faster.</td></tr>
</tbody>
</table>

**Hypothesis 1 holds only as a conditional trade-off.** Orchestration lowers the correction rate on Problem 2 and for Codex / Sonnet 5 / Fable — always at a latency cost. On Problem 1 and for the large Grok cohort, direct implementation matches or beats it.

## Hypothesis 2 revisited: what actually makes an agent use your skills?

Part 1 showed `scenario4` runs pulling in far more of the skill/command/agent library than the sparser scenarios — but it could not tell whether that was the OpenSpec documents or the delegation wiring doing it. `scenario5` answers it directly, because it hands the tool the exact same OpenSpec plan with the delegation wiring removed.

<table>
<thead>
<tr><th>Cohort</th><th>Runs</th><th>Avg skills used</th><th>Avg agents used</th><th>Runs with zero agents</th></tr>
</thead>
<tbody>
<tr><td><strong>S4</strong> (<code>/implement-spec</code>)</td><td>50</td><td><strong>14.2</strong></td><td><strong>2.00</strong></td><td>0 / 50</td></tr>
<tr><td><strong>S5</strong> (direct, same plan)</td><td>68</td><td>6.0</td><td>0.26</td><td>51 / 68</td></tr>
</tbody>
</table>

Same plan, an order of magnitude less delegation. Every S4 run records exactly two agents (the tech-lead plus a framework coder); three quarters of S5 runs record **no agent at all**, and the skill count roughly halves. The documents do not trigger the machinery — the command does.

Those pooled averages hide a wide spread by tool and model. Under S4 the delegation command forces the issue — every retained run logs exactly two agents whatever the tool — so the only thing left to vary is how many *skills* each run pulls in behind the command. Under S5, with nothing mandated, both numbers are the tool's own choice:

<table>
<thead>
<tr><th>Tool · model</th><th>S4 avg skills (runs)</th><th>S5 avg skills (runs)</th><th>S5 avg agents</th><th>S5 runs with ≥1 agent</th></tr>
</thead>
<tbody>
<tr><td><code>codex</code> · GPT-5.x</td><td><strong>16.8</strong> (13)</td><td>7.4 (23)</td><td>0.00</td><td>0 / 23</td></tr>
<tr><td><code>cursor</code> · Grok 4.5</td><td>13.2 (19)</td><td><strong>7.7</strong> (20)</td><td><strong>0.55</strong></td><td>11 / 20</td></tr>
<tr><td><code>claude-code</code> · Sonnet 5</td><td>14.0 (13)</td><td>3.3 (18)</td><td>0.17</td><td>2 / 18</td></tr>
</tbody>
</table>

Two things stand out. First, **self-directed skill use splits by tool, not by model family**: left alone, `cursor` (Grok 4.5) and `codex` (GPT-5.x) reach for 7–8 skills a run, while every `claude-code` model settles around 3 — more than double on the Cursor/Codex side. Second, **skills and agents are separate reflexes**: `codex` pulls plenty of skills but never once spawns a sub-agent unprompted (0 / 23), whereas `cursor` with Grok delegates in about half its runs and `claude-code` with Fable every time on a tiny sample. The one- and two-run rows (Composer, Opus 5, Fable 5) are too thin to lean on; the Grok 4.5, GPT-5.x and Sonnet 5 rows carry the weight. All six S5 "≥1 agent" counts add up to the 17 self-directed-agent runs noted below.

There is a caveat that keeps S5 from being a pure "no-agent control": **17 of 68** retained S5 runs still reached for at least one agent on their own initiative (11 on Problem 1, 6 on Problem 2). So the contrast is really *mandated* full orchestration versus *optional* self-directed tool use, not orchestration versus nothing.

**Hypothesis 2 is supported.** Autonomous skill and agent use tracks the delegation command, not the richness of the specification the agent is handed.

## Hypothesis 3 revisited: does the architecture survive without the command?

Part 1's finding was that the hexagonal scaffold in `scenario4` came from the written `design.md`, not from any tool's taste. If that is true, S5 should produce the same structure from the same plan — even with no orchestration.

It does. Of the trimmed snapshots, **an ArchUnit boundary test (`HexagonalArchitectureTest`) is present in 50/50 S4 runs and 67/68 S5 runs.** The one exception is a single Codex S5 run that never wrote the test file.

Holding the tool and commit constant (`cursor` / Grok 4.5, same Problem 2 commit), the S4 and S5 trees are near-identical — same ports-and-adapters split, same class names:

**S4 — orchestrated (`/implement-spec`):**

```text
info/jab/ms/
├── GreekGodsApplication.java
├── adapter/
│   ├── in/
│   │   ├── rest/
│   │   │   ├── GreekGodResource.java
│   │   │   ├── ProblemDetails.java
│   │   │   └── ProblemDetailsExceptionMapper.java
│   │   └── scheduler/
│   │       └── GodUpdater.java
│   └── out/
│       ├── http/
│       │   ├── GodSourceRestClient.java
│       │   └── RestGodSourceClient.java
│       └── persistence/
│           └── JdbcGreekGodRepository.java
├── application/
│   ├── GetGreekGodsUseCase.java
│   ├── SynchronizeGreekGodsUseCase.java
│   └── port/
│       ├── in/  {QueryGreekGods, SynchronizeGreekGods}
│       └── out/ {GodSourceClient, GreekGodRepository}
└── domain/
    └── GreekGod.java

test/.../architecture/HexagonalArchitectureTest.java
```

**S5 — direct, same plan:**

```text
info/jab/ms/
├── GreekGodsApplication.java
├── adapter/
│   ├── in/
│   │   ├── rest/
│   │   │   ├── GreekGodResource.java
│   │   │   └── ProblemDetailsExceptionMapper.java
│   │   └── scheduler/
│   │       └── GodUpdater.java
│   └── out/
│       ├── http/
│       │   ├── MyJsonServerApi.java
│       │   └── RestGodSourceClient.java
│       └── persistence/
│           └── JdbcGreekGodRepository.java
├── application/
│   ├── GetGreekGodsUseCase.java
│   ├── SynchronizeGreekGodsUseCase.java
│   └── port/
│       ├── in/  {QueryGreekGods, SynchronizeGreekGods}
│       └── out/ {GodSourceClient, GreekGodRepository}
└── domain/
    └── GreekGod.java

test/.../architecture/HexagonalArchitectureTest.java
```

The differences are cosmetic (a differently named HTTP client, a spare `ProblemDetails` record, where the acceptance tests are parked). The `adapter/in`, `adapter/out`, `application/port/{in,out}`, `domain` skeleton and the boundary test are identical — because they are spelled out in the OpenSpec input both scenarios receive.

### A side check: how many test classes each run leaves behind

The boundary test is prescribed; the rest of the test suite is not. Every retained run's snapshot carries the full `src/` tree, so the test files each run wrote can be counted — and because the trees arrive in several `tree`/flat formats, this counts **distinct `*Test` / `*IT` / `*AT` Java class names** rather than reconstructing the directory layout. Setting aside the `HexagonalArchitectureTest`-style boundary test above (present in nearly every run of both cohorts), the test classes the implementer chose to add break down as:

<table>
<thead>
<tr><th>Problem</th><th>S4 — median (mean), runs</th><th>S5 — median (mean), runs</th></tr>
</thead>
<tbody>
<tr><td><strong>Problem 1</strong> (God Analysis)</td><td>6 (6.4), n=18</td><td>5 (6.0), n=30</td></tr>
<tr><td><strong>Problem 2</strong> (Greek Gods)</td><td><strong>5.5 (5.1)</strong>, n=32</td><td>4 (3.8), n=38</td></tr>
<tr><td>Pooled</td><td>6 (5.6), n=50</td><td>4 (4.8), n=68</td></tr>
</tbody>
</table>

On **Problem 1** the two workflows land within half a test class of each other. On **Problem 2** the orchestrated runs carry about **one extra test class apiece** (median 5.5 vs 4, mean 5.1 vs 3.8) — the same problem where S4 also cut rework in half and hit a perfect pass rate. Counting the boundary test back in keeps the shape: 6.5 vs 5 median on Problem 2, 7 vs 6 on Problem 1. So orchestration's structural fingerprint is not in the *architecture* — that is contractual — but in a slightly thicker test layer on the harder problem.

The usual caveat applies: this is **file presence**. A `*Test.java` in the tree is not proof it compiles, runs, or asserts anything — the single acceptance flag is still the only execution signal in the dataset.

### Where orchestration does leave a fingerprint: the Maven build

Architecture is contractual, but the `pom.xml` is not fully specified, and that is where the two workflows differ — a little. Decoding and parsing every retained Problem 2 POM (all 70 are strict-Base64, well-formed XML, Java 25, and import the Quarkus BOM):

<table>
<thead>
<tr><th>POM property</th><th>S4 (32 POMs)</th><th>S5 (38 POMs)</th></tr>
</thead>
<tbody>
<tr><td>Core REST / REST-client / PostgreSQL / Flyway / scheduler stack</td><td>32/32</td><td>38/38</td></tr>
<tr><td>Complete, version-pinned four-plugin lifecycle</td><td><strong>28/32</strong></td><td>26/38</td></tr>
<tr><td><code>quarkus</code> application packaging (rest are <code>jar</code>)</td><td>18/32</td><td>21/38</td></tr>
<tr><td>SmallRye OpenAPI dependency</td><td>31/32</td><td>30/38</td></tr>
<tr><td>ArchUnit dependency</td><td>32/32</td><td>37/38</td></tr>
<tr><td>Native-build profile</td><td>15/32</td><td>18/38</td></tr>
<tr><td>Custom repositories / multi-module</td><td>0/32</td><td>0/38</td></tr>
<tr><td>Production / test / error-named Java files (means)</td><td>16.3 / 6.5 / 3.7</td><td>15.5 / 5.7 / 3.2</td></tr>
</tbody>
</table>

S4 pins the full Compiler / Surefire / Failsafe / Quarkus plugin set a bit more often (28/32 vs 26/38) and writes marginally more production and test files — a **5–15% edge**, not the large "assurance surface" gap an earlier read of the data suggested, and file presence still says nothing about whether those tests execute. On everything else the two are within a rounding error of each other. Packaging is a coin flip in *both* workflows: only about half of each cohort ships a `quarkus` application POM, the rest fall back to plain `jar`.

The bigger story in the POMs is drift that neither workflow controls. Retained Quarkus platform versions span **3.27.0 to 3.39.1**, and same-commit S4/S5 pairs sometimes pick different platform versions. A recurring set of POMs declares both `quarkus-rest` and `quarkus-rest-jackson`, or adds `quarkus-agroal` next to a JDBC driver that already pulls it in; several mix `quarkus-junit` with the older `quarkus-junit5-*` coordinate family. None of that is caused by the workflow — it is the agents choosing dependencies run to run — but it is exactly the kind of variance a benchmark has to normalize before it can compare anything downstream.

**Hypothesis 3 is supported, with a sharper conclusion than Part 1.** Project shape — framework, layering, boundary test — is not an orchestration effect at all; it is a written-decision effect, reproduced just as reliably by direct implementation because the OpenSpec input prescribes it. Whatever value `/implement-spec` adds shows up in execution discipline and correction cycles, not in project structure.

## Takeaways

Across two problems, five scenarios and 118 retained current-workflow runs, the S4-vs-S5 A/B is clean enough to say what `/implement-spec` does and does not buy once the OpenSpec plan already exists.

**Hypothesis 1 — orchestration reduces rework beyond the written plan: conditionally supported.**
The command lowers the correction rate, but only where the work is hard. On Problem 2 — persistence, scheduling, a migration — S4 is unambiguously ahead: 32/32 pass, half the average rework (0.75 vs 1.37), double the zero-rework share (47% vs 24%), and about one extra test class per run. On Problem 1, the fan-out-and-sum task, the gap closes: pass rates tie, S5 posts *more* zero-rework runs (50% vs 39%), and S5 is faster. The price is charged whether or not the benefit shows up — S5 is ~36% faster by median wall time, and S4 produced wall-time outliers at more than twice the rate (6 of 56 vs 3 of 71). It is model-shaped too: clear help for Codex, Sonnet 5 and Fable, break-even or worse for the large Cursor/Grok cohort. Reach for `/implement-spec` on integration-heavy work, not by default.

**Hypothesis 2 — the delegation command, not the specification, drives skill and agent use: supported.**
Given the identical `scenario4` plan with the orchestration stripped out, S5 runs use half the skills (6.0 vs 14.2) and almost no agents (0.26 vs 2.00 per run; 51 of 68 use none at all). Richer documents do not pull the library in — the command does. What the tools do unprompted splits by *tool*, not model family: `cursor`/Grok and `codex`/GPT-5.x self-invoke 7–8 skills a run while every `claude-code` model sits near 3, and `codex` never once spawns a sub-agent on its own. "Will my skill get used" today depends more on which tool is driving than on how the task is written.

**Hypothesis 3 — written architecture holds regardless of who executes: supported, and narrower than Part 1 implied.**
The hexagonal scaffold and the ArchUnit boundary test appear in 67 of 68 direct runs, class-for-class identical to the orchestrated ones, because the OpenSpec input spells them out. Orchestration's only structural fingerprint is a slightly fuller Maven lifecycle (28/32 vs 26/38 pinning all four plugins) and ~5–15% more source and test files — not architecture. What *neither* workflow controls is the dependency graph: Quarkus platform versions from 3.27.0 to 3.39.1, an H2 test driver in roughly half the POMs either way, redundant `quarkus-rest*` / `quarkus-agroal` declarations, mixed JUnit coordinates, and a `jar`-vs-`quarkus` packaging coin flip in both cohorts.

**The load-bearing caveat.** Every number here rests on file presence plus a single pass/fail flag. A `*Test.java` in the tree is not a test that compiles, runs, or asserts anything, and `mvn verify` output is never captured. Until that closes, "less rework" means "the acceptance check went green in fewer turns", not "the code is more correct".

## Next steps

The data points at a handful of concrete changes to `/implement-spec`, each tied to a finding above:

1. **Cut the latency — scale orchestration to the task.** The full tech-lead + framework-coder fan-out runs on every task, but the rework payoff only lands on the integration-heavy problem; on Problem 1 it buys nothing and still costs ~36% more wall time and more than twice the wall-time-outlier rate. Grade the task up front — integration count, persistence, migrations, concurrency, external services — and run the full fan-out only above a threshold. Below it, a single implementation pass plus one verification checkpoint should match it for far less time.
2. **Find and keep only the active ingredient.** An S4 run pulls ~14 skills and 2 agents; the correction-rate benefit may come from a single verification pass. Log which sub-step fired and what each one caught, then drop — or background — the steps that never move rework.

## Share your insights

Share findings or raise questions about the harness itself on [GitHub Discussions](https://github.com/jabrena/plinth/discussions).
