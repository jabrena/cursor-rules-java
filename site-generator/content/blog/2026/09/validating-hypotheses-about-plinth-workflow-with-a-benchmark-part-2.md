title=Validating hypotheses about Plinth workflow with a Benchmark Part 2
date=2026-09-05
type=post
tags=blog,agents,skills,openspec,performance,java
author=MyRobot
status=published
~~~~~~

This is the second article in a series that tries to put a number on the value of Plinth's AI-native development workflow for Java. The [Part 1](https://jabrena.github.io/plinth/blog/2026/07/validating-hypotheses-about-plinth-workflow-with-a-benchmark-part-1.html) article showed the [benchmark](https://github.com/jabrena/plinth/tree/main/benchmarks) results across 4 scenarios:

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

The benchmark tried to validate the following hypotheses:

- **Hypothesis 1:** Richer workflows reduce implementation rework.
- **Hypothesis 2:** Delegation workflows encourage autonomous use of reusable skills.
- **Hypothesis 3:** Written architectural decisions improve consistency.

Part 1 ended with a clear result — implementing a problem from a written specification pays off — and one scenario clearly ahead of the rest. But it could not say *why* that scenario won, because it changed two things at once: the OpenSpec plan and the orchestration command that executes it. Part 2 sets out to separate them. The goals here are concrete:

- **Isolate the command from the plan.** A new `scenario5` runs the exact same OpenSpec technical plan as `scenario4`, implemented directly, with `/implement-spec` and its agents forbidden. `scenario4` vs `scenario5` is then a clean A/B: identical plan, orchestration the only variable.
- **Check whether the answer is problem-dependent.** A second problem — a persistence-and-scheduling "Greek Gods API" — joins the original fan-out-and-sum one, so every finding can be read per problem.
- **Re-test all three hypotheses on a much larger dataset.** The corpus has grown from 54 runs to 217 non-template records across two problems, five scenarios, four tools, and a spread of models, with outlier trimming applied consistently.

## What is new in Part 2

Two additions to the harness: a new scenario, and a second problem.

**The two scenarios under comparison:**

<table>
<thead>
<tr><th>Scenario</th><th>How the plan is implemented</th></tr>
</thead>
<tbody>
<tr><td><code>scenario4</code> (<strong>S4</strong>, orchestrated)</td><td>The OpenSpec technical plan from <code>/create-spec</code> + <code>/explore-design</code>, implemented through <code>/implement-spec</code> — which delegates to <code>@plinth-tech-lead</code> and a framework-specific coder agent. Full orchestration is <strong>mandated</strong>.</td></tr>
<tr><td><code>scenario5</code> (<strong>S5</strong>, direct)</td><td>The <strong>same</strong> OpenSpec technical plan as <code>scenario4</code> — same <code>specs/technical-requirements/openspec/</code> tree — implemented <strong>directly</strong>. <code>/implement-spec</code> is forbidden; there is no mandated agent or skill. Agents and skills are still <em>allowed</em> if the tool reaches for them on its own.</td></tr>
</tbody>
</table>

**The two problems the A/B runs on:**

<table>
<thead>
<tr><th>Problem</th><th>What it is</th></tr>
</thead>
<tbody>
<tr><td><a href="https://github.com/jabrena/plinth/tree/main/benchmarks/problem1">Problem 1</a> — God Analysis API</td><td>The original benchmark problem (<a href="https://github.com/jabrena/latency-problems/blob/master/docs/problem1/README.md">latency-problems Problem 1</a>): a <strong>Spring Boot</strong> REST API that fans out to a third-party service and aggregates the results. A pure <strong>fan-out-and-sum</strong> problem, carried over from Part 1.</td></tr>
<tr><td><a href="https://github.com/jabrena/plinth/tree/main/benchmarks/problem2">Problem 2</a> — Greek Gods API</td><td>New in Part 2 (<a href="https://github.com/jabrena/latency-problems/blob/master/docs/problem5/README.md">latency-problems Problem 5</a>): a <strong>Quarkus</strong> REST API that periodically syncs a Greek-gods catalogue from a third-party service into a relational database via Flyway. A <strong>persistence-and-scheduling</strong> problem, not a pure fan-out-and-sum one.</td></tr>
</tbody>
</table>

So `scenario4` (**S4**, orchestrated) versus `scenario5` (**S5**, direct) is a clean A/B: identical plan, and the only difference is whether `/implement-spec` and its agents run. Running that A/B across both problems shows whether the answer is problem-dependent.

Part 2 runs the benchmark on **Plinth v0.18.0**; Part 1 used **Plinth v0.17.0**.

### How long the two problems actually take

The Tukey fences below come from the full per-problem wall-clock distribution — every scenario, tool, and model pooled together. It is worth looking at that distribution on its own, because it answers the question people ask before they ask anything about workflows: *how long is one of these runs?*

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

### Which skills get pulled in, per problem

The pooled "6.0 vs 14.2" gap has a specific shape once it is split by problem and the skills are lined up by how often they appear. Each table counts **retained runs that pulled the skill in at least once** — the JSON records skills as a per-run set, so this is run frequency, not invocation count — sorted by the combined S4 + S5 total. The cohorts are the same trimmed ones used everywhere else in this section.

**Problem 1 — God Analysis API (Spring Boot).** S4 `n = 18`, S5 `n = 30`.

<table>
<thead>
<tr><th>Skill</th><th>S4 runs</th><th>S5 runs</th><th>Total</th></tr>
</thead>
<tbody>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/300-frameworks-spring-boot-create-project"><code>300-frameworks-spring-boot-create-project</code></a></td><td>17</td><td>23</td><td>40</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/302-frameworks-spring-boot-rest"><code>302-frameworks-spring-boot-rest</code></a></td><td>17</td><td>20</td><td>37</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/707-technologies-hexagonal-architecture"><code>707-technologies-hexagonal-architecture</code></a></td><td>13</td><td>24</td><td>37</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/323-frameworks-spring-boot-testing-acceptance-tests"><code>323-frameworks-spring-boot-testing-acceptance-tests</code></a></td><td>16</td><td>19</td><td>35</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/322-frameworks-spring-boot-testing-integration-tests"><code>322-frameworks-spring-boot-testing-integration-tests</code></a></td><td>15</td><td>17</td><td>32</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/702-technologies-wiremock"><code>702-technologies-wiremock</code></a></td><td>16</td><td>15</td><td>31</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/301-frameworks-spring-boot-core"><code>301-frameworks-spring-boot-core</code></a></td><td>16</td><td>13</td><td>29</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/321-frameworks-spring-boot-testing-unit-tests"><code>321-frameworks-spring-boot-testing-unit-tests</code></a></td><td>15</td><td>12</td><td>27</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/042-planning-openspec"><code>042-planning-openspec</code></a></td><td><strong>18</strong></td><td>6</td><td>24</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/125-java-concurrency"><code>125-java-concurrency</code></a></td><td>9</td><td>10</td><td>19</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/701-technologies-openapi"><code>701-technologies-openapi</code></a></td><td><strong>16</strong></td><td>3</td><td>19</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/303-frameworks-spring-boot-validation"><code>303-frameworks-spring-boot-validation</code></a></td><td>12</td><td>6</td><td>18</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/124-java-secure-coding"><code>124-java-secure-coding</code></a></td><td><strong>14</strong></td><td>1</td><td>15</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/126-java-exception-handling"><code>126-java-exception-handling</code></a></td><td>10</td><td>1</td><td>11</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/111-java-maven-dependencies"><code>111-java-maven-dependencies</code></a></td><td>3</td><td><strong>7</strong></td><td>10</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/122-java-type-design"><code>122-java-type-design</code></a></td><td>8</td><td>1</td><td>9</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/051-design-two-steps-methods"><code>051-design-two-steps-methods</code></a></td><td>4</td><td>3</td><td>7</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/110-java-maven-best-practices"><code>110-java-maven-best-practices</code></a></td><td>4</td><td>3</td><td>7</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/181-java-observability-logging"><code>181-java-observability-logging</code></a></td><td>7</td><td>0</td><td>7</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/054-design-tdd"><code>054-design-tdd</code></a></td><td>4</td><td>2</td><td>6</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/121-java-object-oriented-design"><code>121-java-object-oriented-design</code></a></td><td>6</td><td>0</td><td>6</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/130-java-testing-strategies"><code>130-java-testing-strategies</code></a></td><td>5</td><td>1</td><td>6</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/133-java-testing-acceptance-tests"><code>133-java-testing-acceptance-tests</code></a></td><td>0</td><td>6</td><td>6</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/112-java-maven-plugins"><code>112-java-maven-plugins</code></a></td><td>2</td><td>1</td><td>3</td></tr>
<tr><td colspan="4"><em>tail (total ≤ 2): <a href="https://www.skills.sh/jabrena/plinth/143-java-functional-exception-handling"><code>143-java-functional-exception-handling</code></a> (2/0), <a href="https://www.skills.sh/jabrena/plinth/142-java-functional-programming"><code>142-java-functional-programming</code></a> (1/0), <a href="https://www.skills.sh/jabrena/plinth/052-design-hamburger-method"><code>052-design-hamburger-method</code></a> (1/0), <a href="https://www.skills.sh/jabrena/plinth/131-java-testing-unit-testing"><code>131-java-testing-unit-testing</code></a> (0/1), <a href="https://www.skills.sh/jabrena/plinth/132-java-testing-integration-testing"><code>132-java-testing-integration-testing</code></a> (0/1)</em></td></tr>
</tbody>
</table>

The framework scaffold sits at the top and is used heavily by *both* cohorts — `create-project` (17/18 vs 23/30), `-rest`, the three `-testing-` skills, `wiremock` — because the OpenSpec plan names those technologies directly, so a direct implementer walks into them anyway. `707-…-hexagonal-architecture` is actually *more* common under S5 (24/30) than S4 (13/18): it is written into `design.md`, and the S4 tech-lead does not always re-log it.

The divergence is entirely in the cross-cutting discipline skills. `042-planning-openspec` goes 18/18 → 6/30; `701-…-openapi` 16 → 3, `124-java-secure-coding` 14 → 1, `126-java-exception-handling` 10 → 1, `122-java-type-design` 8 → 1, `181-java-observability-logging` 7 → 0, `121-java-object-oriented-design` 6 → 0. The only skill S5 reaches for *more* than S4 is `111-java-maven-dependencies` (3 → 7) — implementing directly, the tool edits the POM itself instead of delegating it — and S5 grabs the generic `133-java-testing-acceptance-tests` (0 → 6) where S4 uses the Spring-specific `323-…`.

**Problem 2 — Greek Gods API (Quarkus, persistence + scheduling).** S4 `n = 32`, S5 `n = 38`.

<table>
<thead>
<tr><th>Skill</th><th>S4 runs</th><th>S5 runs</th><th>Total</th></tr>
</thead>
<tbody>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/707-technologies-hexagonal-architecture"><code>707-technologies-hexagonal-architecture</code></a></td><td>32</td><td>36</td><td>68</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/400-frameworks-quarkus-create-project"><code>400-frameworks-quarkus-create-project</code></a></td><td>30</td><td>29</td><td>59</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/423-frameworks-quarkus-testing-acceptance-tests"><code>423-frameworks-quarkus-testing-acceptance-tests</code></a></td><td>31</td><td>23</td><td>54</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/413-frameworks-quarkus-db-migrations-flyway"><code>413-frameworks-quarkus-db-migrations-flyway</code></a></td><td><strong>32</strong></td><td>21</td><td>53</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/411-frameworks-quarkus-jdbc"><code>411-frameworks-quarkus-jdbc</code></a></td><td>31</td><td>21</td><td>52</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/402-frameworks-quarkus-rest"><code>402-frameworks-quarkus-rest</code></a></td><td>31</td><td>19</td><td>50</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/401-frameworks-quarkus-core"><code>401-frameworks-quarkus-core</code></a></td><td>31</td><td>15</td><td>46</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/421-frameworks-quarkus-testing-unit-tests"><code>421-frameworks-quarkus-testing-unit-tests</code></a></td><td>31</td><td>10</td><td>41</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/042-planning-openspec"><code>042-planning-openspec</code></a></td><td><strong>32</strong></td><td>2</td><td>34</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/701-technologies-openapi"><code>701-technologies-openapi</code></a></td><td><strong>30</strong></td><td>3</td><td>33</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/704-technologies-sql"><code>704-technologies-sql</code></a></td><td><strong>26</strong></td><td>1</td><td>27</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/422-frameworks-quarkus-testing-integration-tests"><code>422-frameworks-quarkus-testing-integration-tests</code></a></td><td>19</td><td>7</td><td>26</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/126-java-exception-handling"><code>126-java-exception-handling</code></a></td><td><strong>20</strong></td><td>1</td><td>21</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/124-java-secure-coding"><code>124-java-secure-coding</code></a></td><td><strong>19</strong></td><td>0</td><td>19</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/111-java-maven-dependencies"><code>111-java-maven-dependencies</code></a></td><td>4</td><td><strong>13</strong></td><td>17</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/054-design-tdd"><code>054-design-tdd</code></a></td><td>13</td><td>0</td><td>13</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/133-java-testing-acceptance-tests"><code>133-java-testing-acceptance-tests</code></a></td><td>3</td><td>10</td><td>13</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/122-java-type-design"><code>122-java-type-design</code></a></td><td>11</td><td>0</td><td>11</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/121-java-object-oriented-design"><code>121-java-object-oriented-design</code></a></td><td>8</td><td>0</td><td>8</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/130-java-testing-strategies"><code>130-java-testing-strategies</code></a></td><td>8</td><td>0</td><td>8</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/123-java-design-patterns"><code>123-java-design-patterns</code></a></td><td>5</td><td>0</td><td>5</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/143-java-functional-exception-handling"><code>143-java-functional-exception-handling</code></a></td><td>4</td><td>0</td><td>4</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/110-java-maven-best-practices"><code>110-java-maven-best-practices</code></a></td><td>3</td><td>0</td><td>3</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/181-java-observability-logging"><code>181-java-observability-logging</code></a></td><td>3</td><td>0</td><td>3</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/702-technologies-wiremock"><code>702-technologies-wiremock</code></a></td><td>3</td><td>0</td><td>3</td></tr>
<tr><td colspan="4"><em>tail (total ≤ 2): <a href="https://www.skills.sh/jabrena/plinth/052-design-hamburger-method"><code>052-design-hamburger-method</code></a> (1/0), <a href="https://www.skills.sh/jabrena/plinth/112-java-maven-plugins"><code>112-java-maven-plugins</code></a> (0/1)</em></td></tr>
</tbody>
</table>

Same pattern, sharper. `707-…-hexagonal-architecture` is near-universal in both (32/32, 36/38) — contractual. The Quarkus scaffold and the persistence skills are used by both cohorts, but the gap is wider than on Problem 1: `413-…-flyway` 32/32 → 21/38, `423-…-acceptance-tests` 31 → 23, `421-…-unit-tests` 31 → 10. The pure-knowledge skills collapse outright: `704-technologies-sql` 26 → 1, `124-java-secure-coding` 19 → 0, `126-java-exception-handling` 20 → 1, `054-design-tdd` 13 → 0, `122-java-type-design` 11 → 0, and the `121`/`130`/`123` design skills 5–8 → 0. As on Problem 1, `111-java-maven-dependencies` is the lone skill S5 leans on harder (4 → 13). Self-directed agent use is rarer here too — 32 of 38 S5 runs used no agent at all, against 19 of 30 on Problem 1 — so on the harder problem the tools fall back to plain implementation even more.

**Across both problems the split is the same.** Skills that name a *technology the plan already specifies* — framework create / rest / test, hexagonal architecture, Flyway, JDBC, WireMock — get used with or without the command. Skills that encode *cross-cutting practice* — `openspec` planning, `openapi`, secure-coding, exception-handling, type-design, TDD, OO / design-pattern, observability — are pulled in almost only when `/implement-spec` drives. That difference is what the "14.2 vs 6.0" is made of.

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

**Which testing skills each harness reaches for.** The per-problem skill tables earlier in this section pool all three tools. Splitting the **same trimmed S4/S5 cohorts** by harness — and keeping only the skills whose name marks them as test work (`*-testing-*`, `054-design-tdd`, `702-technologies-wiremock`) — shows each tool has its own testing-skill reflex once `/implement-spec` is out of the picture. The `claude-code` rows are restricted to **Sonnet 5** samples (the Opus 5 / Fable 5 runs are 1–5 apiece, too thin to read); `codex` is all GPT-5.x and `cursor` is Grok 4.5 plus a little Composer. Every cell counts **retained runs that pulled the skill in at least once**, sorted by the combined S4 + S5 total.

#### `claude-code` (Sonnet 5)

**Problem 1 — God Analysis API (Spring Boot).** S4 `n = 3`, S5 `n = 8`.

<table>
<thead>
<tr><th>Testing skill</th><th>S4 runs</th><th>S5 runs</th><th>Total</th></tr>
</thead>
<tbody>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/323-frameworks-spring-boot-testing-acceptance-tests"><code>323-frameworks-spring-boot-testing-acceptance-tests</code></a></td><td>3</td><td>4</td><td>7</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/702-technologies-wiremock"><code>702-technologies-wiremock</code></a></td><td>3</td><td>4</td><td>7</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/321-frameworks-spring-boot-testing-unit-tests"><code>321-frameworks-spring-boot-testing-unit-tests</code></a></td><td>3</td><td>3</td><td>6</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/322-frameworks-spring-boot-testing-integration-tests"><code>322-frameworks-spring-boot-testing-integration-tests</code></a></td><td>3</td><td>3</td><td>6</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/130-java-testing-strategies"><code>130-java-testing-strategies</code></a></td><td>1</td><td>0</td><td>1</td></tr>
</tbody>
</table>

**Problem 2 — Greek Gods API (Quarkus).** S4 `n = 10`, S5 `n = 10`.

<table>
<thead>
<tr><th>Testing skill</th><th>S4 runs</th><th>S5 runs</th><th>Total</th></tr>
</thead>
<tbody>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/423-frameworks-quarkus-testing-acceptance-tests"><code>423-frameworks-quarkus-testing-acceptance-tests</code></a></td><td>10</td><td>2</td><td>12</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/421-frameworks-quarkus-testing-unit-tests"><code>421-frameworks-quarkus-testing-unit-tests</code></a></td><td>10</td><td>0</td><td>10</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/130-java-testing-strategies"><code>130-java-testing-strategies</code></a></td><td>2</td><td>0</td><td>2</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/054-design-tdd"><code>054-design-tdd</code></a></td><td>1</td><td>0</td><td>1</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/422-frameworks-quarkus-testing-integration-tests"><code>422-frameworks-quarkus-testing-integration-tests</code></a></td><td>1</td><td>0</td><td>1</td></tr>
</tbody>
</table>

Under orchestration Sonnet 5 logs the framework testing trio (unit / integration / acceptance) in every run; strip the command and almost all of it disappears. On Problem 2 the only testing skill any direct Sonnet 5 run records is `423-…-acceptance-tests`, in 2 of 10 — the sparse self-directed profile seen everywhere else for this tool.

#### `codex` (GPT-5.x)

**Problem 1 — God Analysis API (Spring Boot).** S4 `n = 3`, S5 `n = 9`.

<table>
<thead>
<tr><th>Testing skill</th><th>S4 runs</th><th>S5 runs</th><th>Total</th></tr>
</thead>
<tbody>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/322-frameworks-spring-boot-testing-integration-tests"><code>322-frameworks-spring-boot-testing-integration-tests</code></a></td><td>3</td><td>5</td><td>8</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/323-frameworks-spring-boot-testing-acceptance-tests"><code>323-frameworks-spring-boot-testing-acceptance-tests</code></a></td><td>3</td><td>5</td><td>8</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/133-java-testing-acceptance-tests"><code>133-java-testing-acceptance-tests</code></a></td><td>0</td><td>5</td><td>5</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/321-frameworks-spring-boot-testing-unit-tests"><code>321-frameworks-spring-boot-testing-unit-tests</code></a></td><td>3</td><td>1</td><td>4</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/702-technologies-wiremock"><code>702-technologies-wiremock</code></a></td><td>3</td><td>0</td><td>3</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/130-java-testing-strategies"><code>130-java-testing-strategies</code></a></td><td>2</td><td>0</td><td>2</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/054-design-tdd"><code>054-design-tdd</code></a></td><td>0</td><td>1</td><td>1</td></tr>
</tbody>
</table>

**Problem 2 — Greek Gods API (Quarkus).** S4 `n = 10`, S5 `n = 14`.

<table>
<thead>
<tr><th>Testing skill</th><th>S4 runs</th><th>S5 runs</th><th>Total</th></tr>
</thead>
<tbody>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/423-frameworks-quarkus-testing-acceptance-tests"><code>423-frameworks-quarkus-testing-acceptance-tests</code></a></td><td>9</td><td>11</td><td>20</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/422-frameworks-quarkus-testing-integration-tests"><code>422-frameworks-quarkus-testing-integration-tests</code></a></td><td>9</td><td>6</td><td>15</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/421-frameworks-quarkus-testing-unit-tests"><code>421-frameworks-quarkus-testing-unit-tests</code></a></td><td>9</td><td>5</td><td>14</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/133-java-testing-acceptance-tests"><code>133-java-testing-acceptance-tests</code></a></td><td>3</td><td>10</td><td>13</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/130-java-testing-strategies"><code>130-java-testing-strategies</code></a></td><td>4</td><td>0</td><td>4</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/054-design-tdd"><code>054-design-tdd</code></a></td><td>3</td><td>0</td><td>3</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/702-technologies-wiremock"><code>702-technologies-wiremock</code></a></td><td>2</td><td>0</td><td>2</td></tr>
</tbody>
</table>

`codex` keeps reaching for testing skills without the command — but it drifts from the framework-specific `323`/`423` toward the generic `133-java-testing-acceptance-tests` (0 → 5 on Problem 1, 3 → 10 on Problem 2). Unit- and integration-testing skills survive into S5 at a reduced rate, while the practice skills (`130`, `054-design-tdd`, `702-technologies-wiremock`) fall to zero.

#### `cursor` (Grok 4.5 / Composer)

**Problem 1 — God Analysis API (Spring Boot).** S4 `n = 9`, S5 `n = 11`.

<table>
<thead>
<tr><th>Testing skill</th><th>S4 runs</th><th>S5 runs</th><th>Total</th></tr>
</thead>
<tbody>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/702-technologies-wiremock"><code>702-technologies-wiremock</code></a></td><td>8</td><td>10</td><td>18</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/323-frameworks-spring-boot-testing-acceptance-tests"><code>323-frameworks-spring-boot-testing-acceptance-tests</code></a></td><td>8</td><td>9</td><td>17</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/322-frameworks-spring-boot-testing-integration-tests"><code>322-frameworks-spring-boot-testing-integration-tests</code></a></td><td>7</td><td>9</td><td>16</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/321-frameworks-spring-boot-testing-unit-tests"><code>321-frameworks-spring-boot-testing-unit-tests</code></a></td><td>7</td><td>8</td><td>15</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/054-design-tdd"><code>054-design-tdd</code></a></td><td>4</td><td>1</td><td>5</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/130-java-testing-strategies"><code>130-java-testing-strategies</code></a></td><td>1</td><td>1</td><td>2</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/131-java-testing-unit-testing"><code>131-java-testing-unit-testing</code></a></td><td>0</td><td>1</td><td>1</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/132-java-testing-integration-testing"><code>132-java-testing-integration-testing</code></a></td><td>0</td><td>1</td><td>1</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/133-java-testing-acceptance-tests"><code>133-java-testing-acceptance-tests</code></a></td><td>0</td><td>1</td><td>1</td></tr>
</tbody>
</table>

**Problem 2 — Greek Gods API (Quarkus).** S4 `n = 11`, S5 `n = 11`.

<table>
<thead>
<tr><th>Testing skill</th><th>S4 runs</th><th>S5 runs</th><th>Total</th></tr>
</thead>
<tbody>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/423-frameworks-quarkus-testing-acceptance-tests"><code>423-frameworks-quarkus-testing-acceptance-tests</code></a></td><td>11</td><td>9</td><td>20</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/421-frameworks-quarkus-testing-unit-tests"><code>421-frameworks-quarkus-testing-unit-tests</code></a></td><td>11</td><td>5</td><td>16</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/422-frameworks-quarkus-testing-integration-tests"><code>422-frameworks-quarkus-testing-integration-tests</code></a></td><td>8</td><td>1</td><td>9</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/054-design-tdd"><code>054-design-tdd</code></a></td><td>8</td><td>0</td><td>8</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/130-java-testing-strategies"><code>130-java-testing-strategies</code></a></td><td>2</td><td>0</td><td>2</td></tr>
<tr><td><a href="https://www.skills.sh/jabrena/plinth/702-technologies-wiremock"><code>702-technologies-wiremock</code></a></td><td>1</td><td>0</td><td>1</td></tr>
</tbody>
</table>

`cursor` is the steadiest of the three. On Problem 1 the Spring testing trio plus `702-technologies-wiremock` appear at near-identical rates with and without the command. On Problem 2 direct runs still hold onto acceptance and unit testing, but `054-design-tdd` (8 → 0) and integration testing (8 → 1) are the ones that only survive under orchestration.

### Where orchestration does leave a fingerprint: the Maven build

Architecture is contractual, but the `pom.xml` is not fully specified, and that is where the two workflows differ — a little. Decoding and parsing every retained Problem 2 POM (all 70 are strict-Base64, well-formed XML, Java 25, and import the Quarkus BOM) gives:

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
<tr><td>Custom repositories / multi-module</td><td>0/32</td><td>0/38</td></tr>
<tr><td>Production Java files (mean per run)</td><td>16.3</td><td>15.5</td></tr>
<tr><td>Test Java files (mean per run)</td><td>6.5</td><td>5.7</td></tr>
<tr><td>Error-handling classes — name contains <code>Error</code>/<code>Exception</code> (mean per run)</td><td>3.7</td><td>3.2</td></tr>
</tbody>
</table>

S4 pins the full Compiler / Surefire / Failsafe / Quarkus plugin set a bit more often (28/32 vs 26/38) and writes marginally more production and test files — a **5–15% edge**, not the large "assurance surface" gap an earlier read of the data suggested, and file presence still says nothing about whether those tests execute. On everything else the two are within a rounding error of each other. Packaging is a coin flip in *both* workflows: only about half of each cohort ships a `quarkus` application POM; the rest fall back to plain `jar`.

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

1. **Cut the latency.** Running the full agent fan-out on every job makes S4 about 36% slower than plain implementation (roughly 16 vs 10 minutes), and that extra time only earns its keep on hard, integration-heavy work. For simple problems, skip the fan-out and do one implementation pass plus one review — same result, much faster.

## Share your insights

Share findings or raise questions about the harness itself on [GitHub Discussions](https://github.com/jabrena/plinth/discussions).
