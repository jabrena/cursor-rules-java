title=What's new in Plinth 0.18.0?
date=2026-08-03
type=post
tags=blog,skills,java,agents,commands,openspec,benchmark
author=Juan Antonio Breña Moral
status=published
~~~~~~

`Plinth` is an AI-native engineering toolkit for modern Java enterprise SDLC, built around reusable `Commands`, `Agents`, `Skills`, and `MCP Servers`.

This release adds a reproducible benchmark, five problem-exploration lenses, BDD and ATDD skills, and stronger OpenSpec delivery gates. The central idea is simple: `"better code generation begins before code generation"`. An agent needs enough context to understand the problem, enough structure to make design decisions explicit, and enough verification evidence to know when the implementation is ready.

Thanks to our community members in [`Urumqi`](https://www.google.com/maps/search/?api=1&query=Urumqi), [`Singapore`](https://www.google.com/maps/search/?api=1&query=Singapore), [`Des Moines`](https://www.google.com/maps/search/?api=1&query=Des+Moines), [`Madrid`](https://www.google.com/maps/place/Madrid) & [`Bengaluru`](https://www.google.com/maps/search/?api=1&query=Bengaluru). 👋👋👋

This article reviews the main changes:

- [Community first!](#community-first)
- [What are the Top 10 Skills from this project in Skills.sh?](#what-are-the-top-10-skills-from-this-project-in-skillssh)
- [Adding better Functional and Technical specification support](#adding-better-functional-and-technical-specification-support)
- [Testing the workflow with a reproducible benchmark](#testing-the-workflow-with-a-reproducible-benchmark)
- [Next steps](#next-steps)

If you have questions about the project, how to customize it for your team, how to use the skills in daily work, or how to solve tooling issues, use [`GitHub Discussions`](https://github.com/jabrena/plinth/discussions).

**Help this project grow:** [If this project helps your team, become a sponsor.](https://github.com/sponsors/jabrena)

<a id="community-first"></a>

## Community first!

In this release, we want to share our sincere gratitude to the `Cursor team` for their initial support in running the first [MadridJug](https://madridjug.es/) Meetup about [`Cursor AI`](https://www.youtube.com/watch?v=LAshLMBb8Bc) in 2025. They provided tokens that I have been managing ever since and that will be fully consumed by August 2026. That indirect support from Cursor helped this project get off the ground, and I believe it has gone on to generate a positive impact in the Java Community worldwide.

PENDING TO MENTION CONTRIBUTIONS FROM COMMUNITY.

If you would like to participate, review the open issues labeled [`good first issue`](https://github.com/jabrena/plinth/issues?q=is%3Aissue%20state%3Aopen%20label%3A%22good%20first%20issue%22), propose improvements, test the workflow with another agent tool, or share your experience in [`GitHub Discussions`](https://github.com/jabrena/plinth/discussions).

<a id="what-are-the-top-10-skills-from-this-project-in-skillssh"></a>

## What are the Top 10 Skills from this project in Skills.sh?

The [Skills.sh registry](https://www.skills.sh/jabrena/plinth) reports `125 skills` and `19.3K` installs in total — roughly `33%` growth since the [`0.17.0` release](https://jabrena.github.io/plinth/blog/2026/07/release-0.17.0.html#what-are-the-top-10-skills-from-this-project-in-skillssh). Compared with `0.17.0`, these are the latest top 10 skills used by Skills.sh users:

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

**Note:** The `Search rank` column shows the skill's position inside that `Skills.sh` search category when results are sorted by install count.

The same view for framework-specific skills shows the top 5 project skills for `Spring Boot`, `Quarkus`, and `Micronaut` searches:

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

<a id="adding-better-functional-and-technical-specification-support"></a>

## Adding better Functional and Technical specification support

Many failed implementations begin with a weak problem statement. A short issue can mix symptoms, assumptions, requested solutions, stakeholder expectations, and technical constraints without making any of them explicit. If an agent starts coding from that issue, it may efficiently implement the wrong change.

`Plinth` addresses this by treating specification as two distinct phases: a **Functional Specification** phase that turns a raw issue into agreed business requirements, and a **Technical Specification** phase that turns those requirements into an OpenSpec design and task list. Each phase has its own dedicated commands:

```text
Functional Specification
  /update-issue                  Update an issue description with structured, evidence-backed content
  /explore-problem               Analyze an issue through five lenses and post a Functional Specification
  /create-acceptance-criteria    Derive and post confirmed Gherkin acceptance criteria for an issue

Technical Specification
  /create-spec       Create or update OpenSpec artifacts from approved source material
  /explore-design    Refine the technical design of an issue or OpenSpec change before implementation
```

### Functional Specification

`0.18.0` focuses on strengthening the Functional Specification phase, adding five complementary skills for investigating a problem before committing to a solution:

- [`@021-problem-framing`](https://www.skills.sh/jabrena/plinth/021-problem-framing) defines the observed problem, affected actors, evidence, impact, and boundaries.
- [`@022-root-cause-analysis`](https://www.skills.sh/jabrena/plinth/022-root-cause-analysis) separates symptoms from plausible causes and identifies evidence needed to confirm them.
- [`@023-assumption-analysis`](https://www.skills.sh/jabrena/plinth/023-assumption-analysis) makes hidden beliefs visible and classifies which assumptions need validation.
- [`@024-context-mapping`](https://www.skills.sh/jabrena/plinth/024-context-mapping) identifies stakeholders, systems, dependencies, boundaries, and external constraints.
- [`@025-quality-attribute-discovery`](https://www.skills.sh/jabrena/plinth/025-quality-attribute-discovery) discovers the performance, security, reliability, maintainability, and operational qualities that may shape the solution.

The new `/explore-problem` command coordinates those five lenses and produces a Functional Specification that can be posted back to the source issue. This creates a stronger bridge between issue tracking and specification work: the issue remains the place where the business need is discussed, while the exploration result gives the next phase a more disciplined starting point.

The five lenses are deliberately separate. A root-cause hypothesis is not the same as a requirement. A stakeholder constraint is not the same as a quality attribute. An assumption is not evidence. Keeping those categories visible helps both humans and agents challenge the problem definition before implementation cost begins to accumulate.

Turning that Functional Specification into something an implementation can be checked against is the job of the new `/create-acceptance-criteria` command. It reads the Functional Specification comment posted by `/explore-problem` and derives observable Gherkin acceptance criteria, which it posts back to the issue as a separate, confirmed comment.

This is where the new [`@058-design-bdd`](https://www.skills.sh/jabrena/plinth/058-design-bdd) skill does its work: it applies to the Functional Specification as the behavior source, confirms the actors, outcomes, and business rules already established there, and develops them into concrete main, alternative, boundary, and error examples expressed as externally observable Given/When/Then scenarios. BDD earns its place here because prose hides ambiguity that examples expose: a phrase such as "invalid requests are rejected" is too broad for reliable implementation, while a concrete scenario forces the team to state which inputs are invalid, which response is expected, and which observable behavior proves the requirement.

### Technical Specification

That Functional Specification then feeds directly into OpenSpec delivery. `0.18.0` strengthens the command path from an issue to an archived OpenSpec change:

```text
Issue
  |
  v
/explore-problem
  |
  v
/create-acceptance-criteria
  |
  v
/create-spec --> /explore-design
```

The commands now provide clearer transitions and gates:

- `/create-spec` reads the complete issue context before drafting an OpenSpec change. Comments and follow-up discussion can contain decisions that are absent from the issue description, so partial issue reads are not enough.
- `/explore-design` runs after specification creation and enriches the change with design decisions instead of acting as a substitute for the specification.

Reaching that design-approved state now passes through the new [`@059-design-atdd`](https://www.skills.sh/jabrena/plinth/059-design-atdd) skill: once an OpenSpec change has an execution goal, acceptance criteria, and a task checklist, `/explore-design` invokes it as a final alignment gate and reports an evidence-backed `ready` or `changes-requested` outcome, refusing approval while findings stay unresolved. `/implement-spec` enforces that same alignment again as its own readiness gate before delivery begins, so a change that never reached genuine alignment during design cannot slip into implementation. `/close-spec` then archives the completed OpenSpec change once implementation and verification are finished.

This sequence makes the lifecycle easier to explain and audit. Problem exploration discovers what needs attention. Specification turns that understanding into requirements and scenarios. Design decides how the change should be delivered. Implementation executes an approved task list. Closure archives the completed change and its evidence.

For more on why this specification phase deserves its own place in the workflow, read [Why Functional and Technical Specifications Matter for AI-Assisted Development](https://jabrena.github.io/plinth/blog/2026/07/why-functional-and-technical-specifications-matter.html).

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

<a id="next-steps"></a>

## Next steps

The benchmark and the new workflow gates create a foundation for the next iterations. Future work can use benchmark evidence to improve skill discovery, compare outcomes across agent tools, and identify which specification and design artifacts have the greatest effect on delivery quality.

The broader direction remains the same: help Java teams move from isolated code generation to an engineering workflow where problems are explored, decisions are recorded, changes are implemented through explicit responsibilities, and results are verified before closure.
