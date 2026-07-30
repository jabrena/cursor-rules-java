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
- [Comparing Plinth commands with OpenSpec and Spec Kit](#comparing-plinth-commands-with-openspec-and-spec-kit)
- [Comparing Plinth with mattpocock/skills and Superpowers](#comparing-plinth-with-mattpocockskills-and-superpowers)
- [Testing the workflow with a reproducible benchmark](#testing-the-workflow-with-a-reproducible-benchmark)
- [Do you still have questions about the project?](#doubts)
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

<a id="comparing-plinth-commands-with-openspec-and-spec-kit"></a>

## Comparing Plinth commands with OpenSpec and Spec Kit

The [`0.17.0` article](https://jabrena.github.io/plinth/blog/2026/07/release-0.17.0.html#comparing-plinth-commands-with-openspec-and-spec-kit) compared `Plinth commands` against `OpenSpec` and `Spec Kit` phases. `0.18.0`'s new Functional Specification commands and the agent-ownership changes reshape that mapping, so here is the updated comparison:

<table>
  <thead>
    <tr>
      <th>Plinth Command</th>
      <th>OpenSpec phase</th>
      <th>Spec Kit phase</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><code>/update-issue</code></td>
      <td>Issue intake<br><code>openspec list</code></td>
      <td><code>/speckit.specify</code> input</td>
    </tr>
    <tr>
      <td><code>/explore-problem</code></td>
      <td>— (precedes OpenSpec)</td>
      <td><code>/speckit.specify</code> input, <code>/speckit.clarify</code></td>
    </tr>
    <tr>
      <td><code>/create-acceptance-criteria</code></td>
      <td>— (precedes OpenSpec)</td>
      <td><code>/speckit.clarify</code>, <code>/speckit.checklist</code></td>
    </tr>
    <tr>
      <td><code>/create-spec</code></td>
      <td>Proposal and Specification<br><code>openspec new change &lt;change-name&gt;</code>, <code>openspec show &lt;change-name&gt;</code></td>
      <td><code>/speckit.specify</code> plus <code>/speckit.checklist</code></td>
    </tr>
    <tr>
      <td><code>/explore-design</code></td>
      <td>Task planning and alignment review<br><code>openspec validate --all</code></td>
      <td><code>/speckit.plan</code> and <code>/speckit.tasks</code></td>
    </tr>
    <tr>
      <td><code>/implement-spec</code></td>
      <td>Implementation<br><code>openspec show &lt;change-name&gt;</code></td>
      <td><code>/speckit.implement</code></td>
    </tr>
    <tr>
      <td><code>/close-spec</code></td>
      <td>Review and closure<br><code>openspec archive &lt;change-name&gt;</code></td>
      <td><code>/speckit.analyze</code> and <code>/speckit.converge</code></td>
    </tr>
  </tbody>
</table>

`/explore-problem` and `/create-acceptance-criteria` sit before either `OpenSpec` or `Spec Kit` phase begins, because neither tool separates a business-facing Functional Specification from the technical spec — that separation, covered in the section above, is specific to `Plinth`.

<a id="comparing-plinth-with-mattpocockskills-and-superpowers"></a>

## Comparing Plinth with mattpocock/skills and Superpowers

[`mattpocock/skills`](https://github.com/mattpocock/skills) and [`Superpowers`](https://github.com/obra/superpowers) are two of the most widely adopted general-purpose agent-skill collections outside the Java ecosystem. Neither targets Java Enterprise development specifically, but both converge on the same underlying idea `Plinth` is built around: interrogate the problem, write the decision down, and only then let the agent build.

`mattpocock/skills` is explicitly positioned as the lighter-weight alternative on that spectrum. "Its skills — split into "Engineering" and "Productivity", and further into user-invoked and model-invoked — are designed to be small, hackable, and composable rather than an enforced pipeline: `/grill-me` and `/grill-with-docs` interrogate a change before it starts, `/to-spec` and `/to-tickets` turn that conversation into a spec and tracer-bullet tickets, `/tdd` drives red-green-refactor, and `/code-review` checks the diff against both coding standards and the originating spec.

[`Superpowers`](https://github.com/obra/superpowers) sits at the opposite end of that spectrum: it calls itself "a complete software development methodology" and runs as a largely mandatory pipeline — `brainstorming` before code, `using-git-worktrees` for isolation, `writing-plans` for bite-sized tasks, `subagent-driven-development` or `executing-plans` to run them, `test-driven-development` enforcing RED-GREEN-REFACTOR, `requesting-code-review` between tasks, and `finishing-a-development-branch` to close out. It also ships across more coding agents than either `Plinth` or `mattpocock/skills` currently supports (Claude Code, Antigravity, Codex, Cursor, Factory Droid, Gemini CLI, GitHub Copilot CLI, Kimi Code, OpenCode, and Pi).

`Plinth` sits between those two poles, and not by accident. Its philosophy keeps the process ownership `mattpocock/skills` explicitly warns against, but scopes that ownership to a single niche instead of all software: the workflow is a fixed sequence — Functional Specification, Technical Specification, Implementation, Closure — and every phase has one explicit owner (`@plinth-business-analyst`, `@plinth-architect`, `@plinth-tech-lead`, and the framework-specific coder agents), with the handoff between phases gated by evidence — a BDD scenario, an ATDD alignment check, a readiness gate — rather than left to convention. That reads closer in spirit to `Superpowers`'s mandatory pipeline than to `mattpocock/skills`'s hackable, opt-in toolbox.

But `Plinth` narrows its ambition in the other direction: it does not try to be a general-purpose software engineering methodology the way `Superpowers` does, and it is not framework-agnostic the way `mattpocock/skills` is. It is deliberately, permanently Java Enterprise only. Everything below the shared shape of the three projects — Maven, Spring Boot, Quarkus, Micronaut, JVM performance tooling, EU and ISO regulatory review — is where that narrower scope earns its keep, because none of it fits inside a framework-agnostic skill collection or a generic methodology without losing the precision that makes it useful.

Putting numbers on that overlap and that gap, here is how the three projects map concern by concern:

<table>
  <thead>
    <tr>
      <th>Concern</th>
      <th>Plinth</th>
      <th>mattpocock/skills</th>
      <th>Superpowers</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>Interrogate before building</td>
      <td><code>/explore-problem</code>, <code>/update-issue</code>, <code>/create-acceptance-criteria</code></td>
      <td><code>/grill-me</code>, <code>/grill-with-docs</code></td>
      <td><code>brainstorming</code></td>
    </tr>
    <tr>
      <td>Turn the conversation into a spec</td>
      <td><code>/create-spec</code></td>
      <td><code>/to-spec</code>, <code>/to-tickets</code></td>
      <td><code>writing-plans</code></td>
    </tr>
    <tr>
      <td>TDD discipline</td>
      <td><code>@054-design-tdd</code></td>
      <td><code>/tdd</code></td>
      <td><code>test-driven-development</code></td>
    </tr>
    <tr>
      <td>Test type coverage (unit, integration, acceptance, performance, fuzzing)</td>
      <td>
        Framework-agnostic: <code>@130-java-testing-strategies</code>, <code>@131-java-testing-unit-testing</code>, <code>@132-java-testing-integration-testing</code>, <code>@133-java-testing-acceptance-tests</code><br>
        Spring Boot: <code>@321-frameworks-spring-boot-testing-unit-tests</code>, <code>@322-frameworks-spring-boot-testing-integration-tests</code>, <code>@323-frameworks-spring-boot-testing-acceptance-tests</code><br>
        Quarkus: <code>@421-frameworks-quarkus-testing-unit-tests</code>, <code>@422-frameworks-quarkus-testing-integration-tests</code>, <code>@423-frameworks-quarkus-testing-acceptance-tests</code><br>
        Micronaut: <code>@521-frameworks-micronaut-testing-unit-tests</code>, <code>@522-frameworks-micronaut-testing-integration-tests</code>, <code>@523-frameworks-micronaut-testing-acceptance-tests</code><br>
        Performance: <code>@151-java-performance-jmeter</code>, <code>@152-java-performance-gatling</code><br>
        Fuzzing: <code>@703-technologies-fuzzing-testing</code>
      </td>
      <td>—</td>
      <td>—</td>
    </tr>
    <tr>
      <td>Delegated implementation</td>
      <td><code>/implement-spec</code> → framework coder agents</td>
      <td><code>/implement</code> (single agent)</td>
      <td><code>subagent-driven-development</code>, <code>dispatching-parallel-agents</code></td>
    </tr>
    <tr>
      <td>Alignment / code review</td>
      <td><code>@059-design-atdd</code>, readiness gates</td>
      <td><code>/code-review</code> (Standards + Spec)</td>
      <td><code>requesting-code-review</code>, <code>receiving-code-review</code></td>
    </tr>
    <tr>
      <td>Git isolation</td>
      <td><code>/create-worktree</code>, <code>/create-feature-branch</code></td>
      <td>—</td>
      <td><code>using-git-worktrees</code></td>
    </tr>
    <tr>
      <td>Architecture health</td>
      <td><code>@033-architecture-diagrams</code>, <code>/create-adr</code> <code>@030-architecture-adr-general</code></td>
      <td><code>/improve-codebase-architecture</code>, <code>codebase-design</code></td>
      <td>— (implicit in plans and reviews)</td>
    </tr>
  </tbody>
</table>

Across all three, the same underlying discipline shows up independently — interrogate the problem, write the decision down, delegate through disciplined steps, verify before calling it done. That three projects with no shared history converged on it is a good sign the discipline is inherent to working well with agents, not one project's house opinion. Where they diverge is domain depth: `mattpocock/skills` stays deliberately language-agnostic and lightweight, `Superpowers` stays deliberately language-agnostic and enforced, and `Plinth` trades that portability for `125 skills` of Java Enterprise-specific coverage — Maven, Spring Boot, Quarkus, Micronaut, JVM performance tooling, and EU/ISO regulatory review — that neither alternative attempts.

That trade is why `Plinth` is the better default for a Java Enterprise team specifically. The generic discipline all three projects agree on still has to end up in your stack either way; with `mattpocock/skills` or `Superpowers`, a Java team has to write the framework-specific, compliance-aware layer on top of it themselves. `Plinth` ships with that layer already built, and the benchmark results earlier in this article are evidence it holds up in practice — so a Java Enterprise team starts from that depth instead of building it from a language-agnostic baseline.

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

With that data, the benchmark tests three hypotheses about the Plinth workflow:

- Richer workflows reduce implementation rework.
- Delegation workflows encourage autonomous use of reusable skills.
- Written architectural decisions improve consistency.

The first results are more nuanced than a straight line from "more structure" to "better outcome." Richness alone does not reduce rework: the scenario that pairs the same OpenSpec change with no delegation wiring has the *highest* average rework in the whole ladder, while the scenario that adds Plinth's `/create-spec` → `/explore-design` → `/implement-spec` delegation on top of that same input has the lowest average rework and the highest share of zero-rework runs. Rework only drops when structure is paired with delegated execution, not from richer documents by themselves — so the first hypothesis is only partially supported.

The second hypothesis fares better: the only scenario with real agent delegation pulls in several skills, a command, and close to two agents per run on average, against close to none everywhere else, and every tool's own delegated run beats its own non-delegated baseline — though the size of that effect varies a lot by tool.

The third hypothesis is the clearest of the three: package-naming chaos across the baseline runs collapses to one dominant scheme the moment a written ADR states the base package explicitly, and the same pattern holds for architectural shape — a hexagonal package layout with a dedicated boundary test shows up only once that decision exists in writing, never when it doesn't.

The benchmark is not intended to declare a universal winner between agent tools or models. Its purpose is to demonstrate the value `Plinth` brings to the Java community, and these first results validate that: across every tool tested, the full Plinth workflow produced the lowest rework, the most autonomous use of the skill and agent library, and the most consistent architecture. The full breakdown — per-scenario tables, per-tool comparisons, and decoded project trees — is in the article linked below.

For the detailed methodology and current findings, read [Validating hypotheses about Plinth workflow with a Benchmark Part 1](/plinth/blog/2026/07/validating-hypotheses-about-plinth-workflow-with-a-benchmark-part-1.html).

<a id="doubts"></a>

## Do you still have questions about the project?

If you feel stuck using this project or have questions, you can attend the following workshop at [`JCConf 2026`](https://jcconf.tw/2026/):

[![](/plinth/images/2026/7/jcconf-2026.png)](https://jcconf.tw/2026/)

<a id="next-steps"></a>

## Next steps

For the next release, we plan to work on a few topics:

- Add `Katas` that help users learn `Plinth` incrementally.
- Add a skill about `JVM Flags`.
- Go deeper into the EU regulation ecosystem for `GenAI`.

