title=What's new in Plinth 0.18.0?
date=2026-08-03
type=post
tags=blog,skills,java,agents,commands,openspec,benchmark
author=Juan Antonio Breña Moral
status=published
~~~~~~

`Plinth` is an AI-native engineering toolkit for modern Java enterprise SDLC, built around reusable `Commands`, `Agents`, `Skills`, and `MCP Servers`.

This release adds a reproducible benchmark, five problem-exploration lenses, BDD and ATDD skills, stronger OpenSpec delivery gates, clearer agent responsibilities, and dedicated generators for commands and agents.

The central idea is simple: `"better code generation begins before code generation"`. An agent needs enough context to understand the problem, enough structure to make design decisions explicit, and enough verification evidence to know when the implementation is ready.

Thanks to our community members in [`Urumqi`](https://www.google.com/maps/search/?api=1&query=Urumqi), [`Singapore`](https://www.google.com/maps/search/?api=1&query=Singapore), [`Des Moines`](https://www.google.com/maps/search/?api=1&query=Des+Moines), [`Madrid`](https://www.google.com/maps/place/Madrid) & [`Bengaluru`](https://www.google.com/maps/search/?api=1&query=Bengaluru). 👋👋👋

This article reviews the main changes:

- [Community first!](#community-first)
- [What are the Top 10 Skills from this project in Skills.sh?](#what-are-the-top-10-skills-from-this-project-in-skillssh)
- [Testing the workflow with a reproducible benchmark](#testing-the-workflow-with-a-reproducible-benchmark)
- [Exploring the problem through five lenses](#exploring-the-problem-through-five-lenses)
- [Connecting problem exploration to OpenSpec delivery](#connecting-problem-exploration-to-openspec-delivery)
- [Using BDD and ATDD to keep intent and delivery aligned](#using-bdd-and-atdd-to-keep-intent-and-delivery-aligned)
- [Clarifying responsibilities across agents](#clarifying-responsibilities-across-agents)
- [Making commands and agents first-class generated products](#making-commands-and-agents-first-class-generated-products)
- [Simplifying the design workflow](#simplifying-the-design-workflow)
- [Next steps](#next-steps)

If you have questions about the project, how to customize it for your team, how to use the skills in daily work, or how to solve tooling issues, use [`GitHub Discussions`](https://github.com/jabrena/plinth/discussions).

**Help this project grow:** [If this project helps your team, become a sponsor.](https://github.com/sponsors/jabrena)

<a id="community-first"></a>

## Community first!

Community participation makes `Plinth` more useful beyond the context where it was created. Issues, pull requests, benchmark runs, installation feedback, and conversations about real workflows provide evidence that documentation alone cannot produce.

Many thanks to [Leandro Loureiro](https://github.com/lealoureiro) for contributing to this release. In [`#1068`](https://github.com/jabrena/plinth/pull/1068), Leandro expanded the benchmark with additional samples generated with GitHub Copilot, Claude Sonnet 4.5, and Opus. These samples strengthen the comparison between tools and models and help the project avoid drawing conclusions from a single agent environment. Leandro also continued participating in the [`Project Benchmark Harness Part 1` issue](https://github.com/jabrena/plinth/issues/1012), sharing his intention to complete more scenarios.

In [`#1073`](https://github.com/jabrena/plinth/pull/1073), Leandro added missing agent installation instructions for `Codex` and `GitHub Copilot`. This improves the path from discovering Plinth to using its agents in different development environments.

The human committers represented in the `0.18.0` development range are [Juan Antonio Breña Moral](https://github.com/jabrena) and [Leandro Loureiro](https://github.com/lealoureiro). Dependabot also contributed automated dependency updates during the release cycle.

If you would like to participate, review the open issues labeled [`good first issue`](https://github.com/jabrena/plinth/issues?q=is%3Aissue%20state%3Aopen%20label%3A%22good%20first%20issue%22), propose improvements, test the workflow with another agent tool, or share your experience in [`GitHub Discussions`](https://github.com/jabrena/plinth/discussions).

<a id="what-are-the-top-10-skills-from-this-project-in-skillssh"></a>

## What are the Top 10 Skills from this project in Skills.sh?

After the repository rename, Skills.sh maintains statistics under both [`jabrena/cursor-rules-java`](https://www.skills.sh/jabrena/cursor-rules-java) and [`jabrena/plinth`](https://www.skills.sh/jabrena/plinth). To preserve the complete installation history, the ranking below adds the installs reported for each skill on both registry pages. All skill links point to the current `jabrena/plinth` location.

The values were recalculated on `2026-07-30`. Skills.sh statistics change continuously, so this table should be treated as a snapshot. The movement shown beside each `Plinth rank` compares the combined ranking with the Top 10 published in the [`0.17.0` release article](https://jabrena.github.io/plinth/blog/2026/07/release-0.17.0.html#what-are-the-top-10-skills-from-this-project-in-skillssh), in the same way that article compared its ranking with `0.16.0`. `New` means that the skill was outside the previous article's Top 10.

The `Skills.sh Search rank` column shows the current position of the legacy registry entry in the linked Skills.sh search category. The legacy entry carries the installation history used in the combined calculation, while the `Skill` column links to the current `jabrena/plinth` location.

<table>
  <thead>
    <tr>
      <th>Plinth rank&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</th>
      <th>Skills.sh Search</th>
      <th>Skills.sh Search rank</th>
      <th>Skill</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><code>#1</code> ➡️ <code>=</code></td>
      <td><a href="https://www.skills.sh/search?q=maven">Maven</a></td>
      <td><code>#3</code></td>
      <td><a href="https://www.skills.sh/jabrena/plinth/110-java-maven-best-practices"><code>110-java-maven-best-practices</code></a></td>
    </tr>
    <tr>
      <td><code>#2</code> ➡️ <code>=</code></td>
      <td><a href="https://www.skills.sh/search?q=java%20object%20oriented">Java object oriented</a></td>
      <td><code>#1</code></td>
      <td><a href="https://www.skills.sh/jabrena/plinth/121-java-object-oriented-design"><code>121-java-object-oriented-design</code></a></td>
    </tr>
    <tr>
      <td><code>#3</code> ➡️ <code>=</code></td>
      <td><a href="https://www.skills.sh/search?q=java%20security">Java security</a></td>
      <td><code>#10</code></td>
      <td><a href="https://www.skills.sh/jabrena/plinth/124-java-secure-coding"><code>124-java-secure-coding</code></a></td>
    </tr>
    <tr>
      <td><code>#4</code> ➡️ <code>=</code></td>
      <td><a href="https://www.skills.sh/search?q=java%20unit%20testing">Java unit testing</a></td>
      <td><code>#5</code></td>
      <td><a href="https://www.skills.sh/jabrena/plinth/131-java-testing-unit-testing"><code>131-java-testing-unit-testing</code></a></td>
    </tr>
    <tr>
      <td><code>#5</code> ➡️ <code>=</code></td>
      <td><a href="https://www.skills.sh/search?q=java%20refactoring">Java refactoring</a></td>
      <td><code>#4</code></td>
      <td><a href="https://www.skills.sh/jabrena/plinth/141-java-refactoring-with-modern-features"><code>141-java-refactoring-with-modern-features</code></a></td>
    </tr>
    <tr>
      <td><code>#6</code> ↗️ <code>+2</code></td>
      <td><a href="https://www.skills.sh/search?q=java%20concurrency">Java concurrency</a></td>
      <td><code>#4</code></td>
      <td><a href="https://www.skills.sh/jabrena/plinth/125-java-concurrency"><code>125-java-concurrency</code></a></td>
    </tr>
    <tr>
      <td><code>#7</code> ➡️ <code>=</code></td>
      <td><a href="https://www.skills.sh/search?q=java%20functional%20programming">Java functional programming</a></td>
      <td><code>#2</code></td>
      <td><a href="https://www.skills.sh/jabrena/plinth/142-java-functional-programming"><code>142-java-functional-programming</code></a></td>
    </tr>
    <tr>
      <td><code>#8</code> ↘️ <code>-2</code></td>
      <td><a href="https://www.skills.sh/search?q=maven">Maven</a></td>
      <td><code>#5</code></td>
      <td><a href="https://www.skills.sh/jabrena/plinth/111-java-maven-dependencies"><code>111-java-maven-dependencies</code></a></td>
    </tr>
    <tr>
      <td><code>#9</code> 🆕</td>
      <td><a href="https://www.skills.sh/search?q=spring%20boot">Spring Boot</a></td>
      <td><code>#31</code></td>
      <td><a href="https://www.skills.sh/jabrena/plinth/302-frameworks-spring-boot-rest"><code>302-frameworks-spring-boot-rest</code></a></td>
    </tr>
    <tr>
      <td><code>#10</code> ↘️ <code>-1</code></td>
      <td><a href="https://www.skills.sh/search?q=java%20generics">Java generics</a></td>
      <td><code>#3</code></td>
      <td><a href="https://www.skills.sh/jabrena/plinth/128-java-generics"><code>128-java-generics</code></a></td>
    </tr>
  </tbody>
</table>

The combined ranking keeps the first five positions unchanged from `0.17.0`. Java concurrency rises two places to number six, while Maven dependencies moves from number six to number eight. Spring Boot REST enters the Top 10 at number nine, and Java generics moves down one place to number ten.

The same combined view for framework-specific skills shows the current Top 5 project skills for `Spring Boot`, `Quarkus`, and `Micronaut`. When combined install totals are tied, the linked Skills.sh search order determines their relative position.

**Spring Boot**

<table>
  <thead>
    <tr>
      <th>Plinth rank</th>
      <th>Skills.sh Search</th>
      <th>Skills.sh Search rank</th>
      <th>Skill</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><code>#1</code></td>
      <td><a href="https://www.skills.sh/search?q=spring%20boot">Spring Boot</a></td>
      <td><code>#31</code></td>
      <td><a href="https://www.skills.sh/jabrena/plinth/302-frameworks-spring-boot-rest"><code>302-frameworks-spring-boot-rest</code></a></td>
    </tr>
    <tr>
      <td><code>#2</code></td>
      <td><a href="https://www.skills.sh/search?q=spring%20boot">Spring Boot</a></td>
      <td><code>#32</code></td>
      <td><a href="https://www.skills.sh/jabrena/plinth/301-frameworks-spring-boot-core"><code>301-frameworks-spring-boot-core</code></a></td>
    </tr>
    <tr>
      <td><code>#3</code></td>
      <td><a href="https://www.skills.sh/search?q=spring%20boot">Spring Boot</a></td>
      <td><code>#34</code></td>
      <td><a href="https://www.skills.sh/jabrena/plinth/321-frameworks-spring-boot-testing-unit-tests"><code>321-frameworks-spring-boot-testing-unit-tests</code></a></td>
    </tr>
    <tr>
      <td><code>#4</code></td>
      <td><a href="https://www.skills.sh/search?q=spring%20boot">Spring Boot</a></td>
      <td><code>#38</code></td>
      <td><a href="https://www.skills.sh/jabrena/plinth/322-frameworks-spring-boot-testing-integration-tests"><code>322-frameworks-spring-boot-testing-integration-tests</code></a></td>
    </tr>
    <tr>
      <td><code>#5</code></td>
      <td><a href="https://www.skills.sh/search?q=spring%20boot">Spring Boot</a></td>
      <td><code>#39</code></td>
      <td><a href="https://www.skills.sh/jabrena/plinth/323-frameworks-spring-boot-testing-acceptance-tests"><code>323-frameworks-spring-boot-testing-acceptance-tests</code></a></td>
    </tr>
  </tbody>
</table>

**Quarkus**

<table>
  <thead>
    <tr>
      <th>Plinth rank</th>
      <th>Skills.sh Search</th>
      <th>Skills.sh Search rank</th>
      <th>Skill</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><code>#1</code></td>
      <td><a href="https://www.skills.sh/search?q=quarkus">Quarkus</a></td>
      <td><code>#12</code></td>
      <td><a href="https://www.skills.sh/jabrena/plinth/412-frameworks-quarkus-panache"><code>412-frameworks-quarkus-panache</code></a></td>
    </tr>
    <tr>
      <td><code>#2</code></td>
      <td><a href="https://www.skills.sh/search?q=quarkus">Quarkus</a></td>
      <td><code>#13</code></td>
      <td><a href="https://www.skills.sh/jabrena/plinth/402-frameworks-quarkus-rest"><code>402-frameworks-quarkus-rest</code></a></td>
    </tr>
    <tr>
      <td><code>#3</code></td>
      <td><a href="https://www.skills.sh/search?q=quarkus">Quarkus</a></td>
      <td><code>#14</code></td>
      <td><a href="https://www.skills.sh/jabrena/plinth/401-frameworks-quarkus-core"><code>401-frameworks-quarkus-core</code></a></td>
    </tr>
    <tr>
      <td><code>#4</code></td>
      <td><a href="https://www.skills.sh/search?q=quarkus">Quarkus</a></td>
      <td><code>#15</code></td>
      <td><a href="https://www.skills.sh/jabrena/plinth/411-frameworks-quarkus-jdbc"><code>411-frameworks-quarkus-jdbc</code></a></td>
    </tr>
    <tr>
      <td><code>#5</code></td>
      <td><a href="https://www.skills.sh/search?q=quarkus">Quarkus</a></td>
      <td><code>#16</code></td>
      <td><a href="https://www.skills.sh/jabrena/plinth/421-frameworks-quarkus-testing-unit-tests"><code>421-frameworks-quarkus-testing-unit-tests</code></a></td>
    </tr>
  </tbody>
</table>

**Micronaut**

<table>
  <thead>
    <tr>
      <th>Plinth rank</th>
      <th>Skills.sh Search</th>
      <th>Skills.sh Search rank</th>
      <th>Skill</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><code>#1</code></td>
      <td><a href="https://www.skills.sh/search?q=micronaut">Micronaut</a></td>
      <td><code>#6</code></td>
      <td><a href="https://www.skills.sh/jabrena/plinth/521-frameworks-micronaut-testing-unit-tests"><code>521-frameworks-micronaut-testing-unit-tests</code></a></td>
    </tr>
    <tr>
      <td><code>#2</code></td>
      <td><a href="https://www.skills.sh/search?q=micronaut">Micronaut</a></td>
      <td><code>#7</code></td>
      <td><a href="https://www.skills.sh/jabrena/plinth/512-frameworks-micronaut-data"><code>512-frameworks-micronaut-data</code></a></td>
    </tr>
    <tr>
      <td><code>#3</code></td>
      <td><a href="https://www.skills.sh/search?q=micronaut">Micronaut</a></td>
      <td><code>#8</code></td>
      <td><a href="https://www.skills.sh/jabrena/plinth/522-frameworks-micronaut-testing-integration-tests"><code>522-frameworks-micronaut-testing-integration-tests</code></a></td>
    </tr>
    <tr>
      <td><code>#4</code></td>
      <td><a href="https://www.skills.sh/search?q=micronaut">Micronaut</a></td>
      <td><code>#9</code></td>
      <td><a href="https://www.skills.sh/jabrena/plinth/501-frameworks-micronaut-core"><code>501-frameworks-micronaut-core</code></a></td>
    </tr>
    <tr>
      <td><code>#5</code></td>
      <td><a href="https://www.skills.sh/search?q=micronaut">Micronaut</a></td>
      <td><code>#10</code></td>
      <td><a href="https://www.skills.sh/jabrena/plinth/502-frameworks-micronaut-rest"><code>502-frameworks-micronaut-rest</code></a></td>
    </tr>
  </tbody>
</table>

<a id="testing-the-workflow-with-a-reproducible-benchmark"></a>

## Testing the workflow with a reproducible benchmark

An AI-native development workflow should not be evaluated only by how convincing its documentation sounds. It needs repeatable experiments that compare the results obtained with different amounts of engineering context.

`0.18.0` adds a benchmark harness under [`benchmarks/`](https://github.com/jabrena/plinth/tree/main/benchmarks). The benchmark asks agent tools to solve the same Java problem through four scenarios:

<table>
  <thead>
    <tr>
      <th>Scenario</th>
      <th>Context available to the agent</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><code>scenario1</code></td>
      <td>A minimal README.</td>
    </tr>
    <tr>
      <td><code>scenario2</code></td>
      <td>A functional-requirements package with a user story, Gherkin scenarios, OpenAPI, and ADRs.</td>
    </tr>
    <tr>
      <td><code>scenario3</code></td>
      <td>An OpenSpec change created from the same functional requirements.</td>
    </tr>
    <tr>
      <td><code>scenario4</code></td>
      <td>An OpenSpec change created, designed, and implemented through the Plinth workflow.</td>
    </tr>
  </tbody>
</table>

Each run records efficiency, outcome quality, rework, and the commands, agents, and skills used during delivery. The repository includes reproducible scorecards and result samples generated with GitHub Copilot and Claude models.

The first results suggest an important distinction: more documents do not automatically produce a better result. The strongest signal comes when structured artifacts are combined with commands that turn those artifacts into an executed workflow. In the current samples, the complete Plinth scenario required less average rework and encouraged more autonomous use of reusable skills and specialized agents.

The benchmark is not intended to declare a universal winner between agent tools or models. Its purpose is to make workflow hypotheses testable, expose where the process adds value, and identify where more evidence is needed.

For the detailed methodology and current findings, read [Validating hypotheses about Plinth workflow with a Benchmark Part 1](/plinth/blog/2026/07/validating-hypotheses-about-plinth-workflow-with-a-benchmark-part-1.html).

<a id="exploring-the-problem-through-five-lenses"></a>

## Exploring the problem through five lenses

Many failed implementations begin with a weak problem statement. A short issue can mix symptoms, assumptions, requested solutions, stakeholder expectations, and technical constraints without making any of them explicit. If an agent starts coding from that issue, it may efficiently implement the wrong change.

This release adds five complementary skills for investigating a problem before committing to a solution:

- [`@021-problem-framing`](https://www.skills.sh/jabrena/plinth/021-problem-framing) defines the observed problem, affected actors, evidence, impact, and boundaries.
- [`@022-root-cause-analysis`](https://www.skills.sh/jabrena/plinth/022-root-cause-analysis) separates symptoms from plausible causes and identifies evidence needed to confirm them.
- [`@023-assumption-analysis`](https://www.skills.sh/jabrena/plinth/023-assumption-analysis) makes hidden beliefs visible and classifies which assumptions need validation.
- [`@024-context-mapping`](https://www.skills.sh/jabrena/plinth/024-context-mapping) identifies stakeholders, systems, dependencies, boundaries, and external constraints.
- [`@025-quality-attribute-discovery`](https://www.skills.sh/jabrena/plinth/025-quality-attribute-discovery) discovers the performance, security, reliability, maintainability, and operational qualities that may shape the solution.

The new `/explore-problem` command coordinates those five lenses and produces a Functional Specification that can be posted back to the source issue. This creates a stronger bridge between issue tracking and specification work: the issue remains the place where the business need is discussed, while the exploration result gives the next phase a more disciplined starting point.

The five lenses are deliberately separate. A root-cause hypothesis is not the same as a requirement. A stakeholder constraint is not the same as a quality attribute. An assumption is not evidence. Keeping those categories visible helps both humans and agents challenge the problem definition before implementation cost begins to accumulate.

<a id="connecting-problem-exploration-to-openspec-delivery"></a>

## Connecting problem exploration to OpenSpec delivery

`0.18.0` strengthens the command path from an issue to an archived OpenSpec change:

```text
Issue
  |
  v
/explore-problem
  |
  v
/create-spec --> /explore-design --> /implement-spec --> /close-spec
```

The commands now provide clearer transitions and gates:

- `/create-spec` reads the complete issue context before drafting an OpenSpec change. Comments and follow-up discussion can contain decisions that are absent from the issue description, so partial issue reads are not enough.
- `/explore-design` runs after specification creation and enriches the change with design decisions instead of acting as a substitute for the specification.
- `/implement-spec` checks readiness before delivery begins. Implementation should not start when required artifacts are missing, unresolved decisions would materially change the solution, or the task list is not actionable.
- `/close-spec` archives a completed OpenSpec change after implementation and verification are finished.

This sequence makes the lifecycle easier to explain and audit. Problem exploration discovers what needs attention. Specification turns that understanding into requirements and scenarios. Design decides how the change should be delivered. Implementation executes an approved task list. Closure archives the completed change and its evidence.

Generated YAML frontmatter is now included for all commands, giving command assets consistent metadata for discovery and tooling.

<a id="using-bdd-and-atdd-to-keep-intent-and-delivery-aligned"></a>

## Using BDD and ATDD to keep intent and delivery aligned

The design skill family introduced in `0.17.0` focused on how to change software safely. This release adds two skills that strengthen the connection between expected behavior and implementation work:

- [`@058-design-bdd`](https://www.skills.sh/jabrena/plinth/058-design-bdd) helps teams discover behavior collaboratively and express it through concrete examples and Given/When/Then scenarios.
- [`@059-design-atdd`](https://www.skills.sh/jabrena/plinth/059-design-atdd) reviews alignment between OpenSpec goals, acceptance criteria, and implementation and verification tasks.

BDD is useful before and during specification because examples expose ambiguity. A phrase such as "invalid requests are rejected" is too broad for reliable implementation. Concrete scenarios force the team to identify which inputs are invalid, which response is expected, and which observable behavior proves the requirement.

ATDD adds a complementary review. It asks whether every important goal is represented by acceptance criteria and whether the delivery plan includes work that will implement and verify those criteria. The result is an alignment report, not an automatic claim that the system is correct.

Together, these skills help preserve traceability:

```text
Business goal -> Example -> Acceptance criterion -> Implementation task -> Verification evidence
```

This is especially valuable when agents participate across multiple phases. Without explicit traceability, an agent can produce a technically clean implementation while silently dropping an important behavior from the original issue.

<a id="clarifying-responsibilities-across-agents"></a>

## Clarifying responsibilities across agents

As the workflow grows, agent specialization needs clear boundaries. `0.18.0` moves planning ownership from the Tech Lead to the Architect and separates design exploration from implementation coordination:

- `@plinth-business-analyst` owns issue quality, requirements traceability, problem exploration, and alignment review.
- `@plinth-architect` owns design exploration, architecture decisions, diagrams, plans, and OpenSpec preparation.
- `@plinth-tech-lead` coordinates delivery from an approved implementation plan or OpenSpec task list.
- Specialized Java and framework coder agents implement the delegated tasks.

The active embedded agent identifiers have also moved from the `robot-` prefix to the `plinth-` prefix. The new names connect the agents clearly to the project and avoid presenting them as generic autonomous robots.

These boundaries are more than naming. They create useful stopping conditions. The Tech Lead should not invent a missing plan while implementation is already under way. The Architect should not quietly become the implementation coordinator. The Business Analyst should not make architecture decisions while reviewing requirements. When responsibilities are explicit, handoffs and unresolved decisions become easier to see.

<a id="making-commands-and-agents-first-class-generated-products"></a>

## Making commands and agents first-class generated products

The project now has three focused generator modules:

- `plinth-commands-generator`
- `plinth-agents-generator`
- `plinth-skills-generator`

Commands and agents were extracted into standalone modules that mirror the existing skills generator. Each module owns its inventory, embedded assets, schema, manifest, and focused tests. Commands and agents also receive dedicated `commands.xsd` and `agents.xsd` schemas.

This modularization matters because commands, agents, and skills evolve at different rates and have different validation needs. A dedicated module makes ownership clearer, reduces accidental coupling, and gives each generated product a place for schema validation and propagation tests.

The parent Maven artifact has also been renamed to `plinth`, completing another part of the project identity introduced in `0.17.0`.

<a id="simplifying-the-design-workflow"></a>

## Simplifying the design workflow

This release removes `@034-architecture-design-exploration`. Its responsibilities have not disappeared; the necessary exploration steps now live directly in `/explore-design`.

The removal reflects the new command sequence. When design exploration was an early standalone activity, a separate skill helped initiate that work. Now `/explore-design` runs after `/create-spec`, with an OpenSpec change already available as its input. Keeping a second orchestration layer would duplicate responsibilities and make the workflow harder to understand.

This is a healthy kind of removal: as commands become better at coordinating complete phases, some intermediate skills can be absorbed into the workflow and the public model becomes smaller.

<a id="next-steps"></a>

## Next steps

The benchmark and the new workflow gates create a foundation for the next iterations. Future work can use benchmark evidence to improve skill discovery, compare outcomes across agent tools, and identify which specification and design artifacts have the greatest effect on delivery quality.

The broader direction remains the same: help Java teams move from isolated code generation to an engineering workflow where problems are explored, decisions are recorded, changes are implemented through explicit responsibilities, and results are verified before closure.
