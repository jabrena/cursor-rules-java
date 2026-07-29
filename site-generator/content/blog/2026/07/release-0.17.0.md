title=What's new in Plinth 0.17.0?
date=2026-07-13
type=post
tags=blog,skills,java,agents,design,regulations,validation
author=Juan Antonio Breña Moral
status=published
~~~~~~

`Plinth` is an AI-native engineering toolkit for modern Java enterprise SDLC, built around reusable `Commands`, `Agents`, `Skills`, and `MCP Servers`.

This release brings several improvements to the development workflow, but the most visible change is the new project identity: the repository moves from `cursor-rules-java` to `plinth`.

Thanks to our community members in [`Urumqi`](https://www.google.com/maps/search/?api=1&query=Urumqi), [`Singapore`](https://www.google.com/maps/search/?api=1&query=Singapore), [`Des Moines`](https://www.google.com/maps/search/?api=1&query=Des+Moines), [`Bengaluru`](https://www.google.com/maps/search/?api=1&query=Bengaluru), and [`San Jose`](https://www.google.com/maps/place/San+Jose,+CA,+USA). 👋👋👋

## Why Plinth?

In civil architecture, a `Plinth` is the base that supports what people actually see. It is not the column, the arch, the road, or the aqueduct, but without a good plinth, the visible structure loses alignment, load-bearing capacity, and long-term stability. That image fits the project better than the original name.

The project is no longer only a collection of `Cursor rules for Java`. Today, it includes `Commands`, `Agents`, `Skills`, generated inventories, OpenSpec workflows, validation pipelines, regulation review aids, framework-specific guidance, and release assets. The new name, `Plinth`, gives the project a broader identity without losing its Java roots.

The metaphor comes from classical Roman architecture. In the Roman Empire, durable engineering depended on foundations, proportions, materials, load paths, maintenance, and repeatable methods. [Marcus Vitruvius Pollio](https://en.wikipedia.org/wiki/Vitruvius) described architecture around the enduring qualities often summarized as `firmitas`, `utilitas`, and `venustas`.

[![](/plinth/images/2026/7/firmitas-utilitas-venustas.png)](https://en.wikipedia.org/wiki/Firmness,_commodity,_and_delight)

That is a useful analogy for modern software engineering with AI.

An AI agent can generate code quickly, but speed is not enough. A team still needs a stable base: requirements, design constraints, compatibility strategy, tests, security checks, architecture boundaries, operational evidence, and human review. Without that base, the generated work may look impressive while resting on weak assumptions.

Now that the repository name change has been explained, let's continue by reviewing the new features included in this release:

- [Community first!](#community-first)
- [What are the Top 10 Skills from this project in Skills.sh?](#what-are-the-top-10-skills-from-this-project-in-skillssh)
- [Design skills for safer delivery](#design-skills-for-safer-delivery)
- [Comparing Plinth commands with OpenSpec and Spec Kit](#comparing-plinth-commands-with-openspec-and-spec-kit)
- [Improving migration safety with Flyway, Mongock, and Parallel Change](#improving-migration-safety-with-flyway-mongock-and-parallel-change)
- [Making architecture boundaries visible with Hexagonal Architecture](#making-architecture-boundaries-visible-with-hexagonal-architecture)
- [Modeling the domain with stronger Java types](#adding-more-types-possibilities)
- [Extending EU regulations and ISO engineering review skills](#extending-eu-regulations-and-iso-engineering-review-skills)
- [Improving security gates in the pipeline with VirusTotal](#improving-security-gates-in-the-pipeline-with-virustotal)
- [Recommended Books and Talks for this summer](#recommended-books-talks-for-this-summer)
- [Next steps](#next-steps)

If you have questions about the project, how to customize it for your team, how to use the skills in daily work, or how to solve tooling issues, use [`GitHub Discussions`](https://github.com/jabrena/plinth/discussions).

**Help this project grow:** [If this project helps your team, become a sponsor.](https://github.com/sponsors/jabrena)

<a id="community-first"></a>

## Community first!

As `Plinth` grows from a technical repository into an OSS product that helps make everyday engineering work easier, the community becomes part of the product itself. Feedback, issue reports, pull requests, and real project stories help keep the guidance practical, reviewable, and useful beyond my own context. This release is a good reminder of that. Many thanks to [Leandro Loureiro](https://github.com/lealoureiro) and [Sangwon Park](https://github.com/wipheg) for your contributions.

[![](/plinth/images/2026/7/the-first-pr-new.png)](https://github.com/jabrena/plinth/pull/966)

[Leandro](https://github.com/lealoureiro) expanded the planning workflow with the new `@045-planning-azure-devops` skill, making it possible to capture what the business tentatively wants to build from issues registered in [`Azure DevOps`](https://learn.microsoft.com/en-us/azure/devops/boards/boards/kanban-overview?view=azure-devops). This brings Azure DevOps into the same planning family as `@043-planning-github-issues` and `@044-planning-jira`, helping teams start from the tools where their work is already tracked.

[Sangwon](https://github.com/wipheg) strengthened the design toolset with the new `@057-design-feature-toggles` skill. This contribution helps teams introduce feature toggles with clearer design guidance, so they can evolve systems incrementally, reduce release risk, and keep unfinished work under control.

If you use the project, you can participate as an [individual contributor](https://github.com/jabrena/plinth/issues?q=is%3Aissue%20state%3Aopen%20label%3A%22good%20first%20issue%22): the [`0.18.0` backlog](https://github.com/jabrena/plinth/milestone/12) already includes new issues labeled with `good first issue`. You can also share your experience in [`GitHub Discussions`](https://github.com/jabrena/plinth/discussions).

<a id="what-are-the-top-10-skills-from-this-project-in-skillssh"></a>

## What are the Top 10 Skills from this project in Skills.sh?

The [Skills.sh registry](https://www.skills.sh/jabrena/plinth) reports `119 skills` and `14.5K` installs in total. Compared with the [`0.16.0` release article](https://jabrena.github.io/plinth/blog/2026/06/release-0.16.0.html#what-are-the-top-10-skills-from-this-project-in-skillssh), these are the latest top 10 skills used by Skills.sh users:

The `Search rank` column shows the skill's position inside that `Skills.sh` search category when results are sorted by install count.

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
      <td><code>#5</code></td>
      <td><a href="https://www.skills.sh/jabrena/plinth/121-java-object-oriented-design"><code>121-java-object-oriented-design</code></a></td>
    </tr>
    <tr>
      <td><code>#3</code> ➡️ <code>=</code></td>
      <td><a href="https://www.skills.sh/search?q=java%20security">Java security</a></td>
      <td><code>#5</code></td>
      <td><a href="https://www.skills.sh/jabrena/plinth/124-java-secure-coding"><code>124-java-secure-coding</code></a></td>
    </tr>
    <tr>
      <td><code>#4</code> ➡️ <code>=</code></td>
      <td><a href="https://www.skills.sh/search?q=java%20unit%20testing">Java unit testing</a></td>
      <td><code>#5</code></td>
      <td><a href="https://www.skills.sh/jabrena/plinth/131-java-testing-unit-testing"><code>131-java-testing-unit-testing</code></a></td>
    </tr>
    <tr>
      <td><code>#5</code> ↗️ <code>+3</code></td>
      <td><a href="https://www.skills.sh/?q=java+refactoring">Java refactoring</a></td>
      <td><code>#8</code></td>
      <td><a href="https://www.skills.sh/jabrena/plinth/141-java-refactoring-with-modern-features"><code>141-java-refactoring-with-modern-features</code></a></td>
    </tr>
    <tr>
      <td><code>#6</code> ↗️ <code>+1</code></td>
      <td><a href="https://www.skills.sh/search?q=maven">Maven</a></td>
      <td><code>#5</code></td>
      <td><a href="https://www.skills.sh/jabrena/plinth/111-java-maven-dependencies"><code>111-java-maven-dependencies</code></a></td>
    </tr>
    <tr>
      <td><code>#7</code> ↘️ <code>-2</code></td>
      <td><a href="https://www.skills.sh/search?q=java%20functional%20programming">Java functional programming</a></td>
      <td><code>#4</code></td>
      <td><a href="https://www.skills.sh/jabrena/plinth/142-java-functional-programming"><code>142-java-functional-programming</code></a></td>
    </tr>
    <tr>
      <td><code>#8</code> ↗️ <code>+1</code></td>
      <td><a href="https://www.skills.sh/search?q=java%20concurrency">Java concurrency</a></td>
      <td><code>#7</code></td>
      <td><a href="https://www.skills.sh/jabrena/plinth/125-java-concurrency"><code>125-java-concurrency</code></a></td>
    </tr>
    <tr>
      <td><code>#9</code> ↘️ <code>-3</code></td>
      <td><a href="https://www.skills.sh/search?q=java%20generics">Java generics</a></td>
      <td><code>#6</code></td>
      <td><a href="https://www.skills.sh/jabrena/plinth/128-java-generics"><code>128-java-generics</code></a></td>
    </tr>
    <tr>
      <td><code>#10</code> 🆕</td>
      <td><a href="https://www.skills.sh/?q=java+type+design">Java type design</a></td>
      <td><code>#9</code></td>
      <td><a href="https://www.skills.sh/jabrena/plinth/122-java-type-design"><code>122-java-type-design</code></a></td>
    </tr>
  </tbody>
</table>

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
      <td><code>#42</code></td>
      <td><a href="https://www.skills.sh/jabrena/plinth/301-frameworks-spring-boot-core"><code>301-frameworks-spring-boot-core</code></a></td>
    </tr>
    <tr>
      <td><code>#2</code></td>
      <td><a href="https://www.skills.sh/search?q=spring%20boot">Spring Boot</a></td>
      <td><code>#43</code></td>
      <td><a href="https://www.skills.sh/jabrena/plinth/302-frameworks-spring-boot-rest"><code>302-frameworks-spring-boot-rest</code></a></td>
    </tr>
    <tr>
      <td><code>#3</code></td>
      <td><a href="https://www.skills.sh/search?q=spring%20boot">Spring Boot</a></td>
      <td><code>#44</code></td>
      <td><a href="https://www.skills.sh/jabrena/plinth/321-frameworks-spring-boot-testing-unit-tests"><code>321-frameworks-spring-boot-testing-unit-tests</code></a></td>
    </tr>
    <tr>
      <td><code>#4</code></td>
      <td><a href="https://www.skills.sh/search?q=spring%20boot">Spring Boot</a></td>
      <td><code>#45</code></td>
      <td><a href="https://www.skills.sh/jabrena/plinth/322-frameworks-spring-boot-testing-integration-tests"><code>322-frameworks-spring-boot-testing-integration-tests</code></a></td>
    </tr>
    <tr>
      <td><code>#5</code></td>
      <td><a href="https://www.skills.sh/search?q=spring%20boot">Spring Boot</a></td>
      <td><code>#46</code></td>
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
      <td><code>#2</code></td>
      <td><a href="https://www.skills.sh/jabrena/plinth/521-frameworks-micronaut-testing-unit-tests"><code>521-frameworks-micronaut-testing-unit-tests</code></a></td>
    </tr>
    <tr>
      <td><code>#2</code></td>
      <td><a href="https://www.skills.sh/search?q=micronaut">Micronaut</a></td>
      <td><code>#3</code></td>
      <td><a href="https://www.skills.sh/jabrena/plinth/512-frameworks-micronaut-data"><code>512-frameworks-micronaut-data</code></a></td>
    </tr>
    <tr>
      <td><code>#3</code></td>
      <td><a href="https://www.skills.sh/search?q=micronaut">Micronaut</a></td>
      <td><code>#4</code></td>
      <td><a href="https://www.skills.sh/jabrena/plinth/522-frameworks-micronaut-testing-integration-tests"><code>522-frameworks-micronaut-testing-integration-tests</code></a></td>
    </tr>
    <tr>
      <td><code>#4</code></td>
      <td><a href="https://www.skills.sh/search?q=micronaut">Micronaut</a></td>
      <td><code>#5</code></td>
      <td><a href="https://www.skills.sh/jabrena/plinth/502-frameworks-micronaut-rest"><code>502-frameworks-micronaut-rest</code></a></td>
    </tr>
    <tr>
      <td><code>#5</code></td>
      <td><a href="https://www.skills.sh/search?q=micronaut">Micronaut</a></td>
      <td><code>#6</code></td>
      <td><a href="https://www.skills.sh/jabrena/plinth/523-frameworks-micronaut-testing-acceptance-tests"><code>523-frameworks-micronaut-testing-acceptance-tests</code></a></td>
    </tr>
  </tbody>
</table>

**Note:** The project is currently consolidating the `Skills.sh` data from `cursor-rules-java` to `plinth`: https://www.skills.sh/jabrena. There is an open ticket for this work: https://github.com/jabrena/plinth/issues/975

If you use the project but you have doubts about any Skill, you could share your feedback in [`GitHub Discussions`](https://github.com/jabrena/plinth/discussions).

<a id="design-skills-for-safer-delivery"></a>

## Design skills for safer delivery

AI coding tools are very good at producing code quickly. That is useful, but speed alone is not the same as engineering discipline. Real delivery work also needs design sequencing, compatibility analysis, test strategy, small slices, and reviewable evidence.

This release adds a new family of design skills to be used in the workflow:

- [`@051-design-two-steps-methods`](https://www.skills.sh/jabrena/plinth/051-design-two-steps-methods)
- [`@052-design-hamburger-method`](https://www.skills.sh/jabrena/plinth/052-design-hamburger-method)
- [`@053-design-simple-rules`](https://www.skills.sh/jabrena/plinth/053-design-simple-rules)
- [`@054-design-tdd`](https://www.skills.sh/jabrena/plinth/054-design-tdd)
- [`@055-design-parallel-change`](https://www.skills.sh/jabrena/plinth/055-design-parallel-change)
- [`@056-design-avoid-breaking-changes`](https://www.skills.sh/jabrena/plinth/056-design-avoid-breaking-changes)

> Repeat with me:
> “SDD is not Aladdin’s lamp—it won’t grant every software wish”.

Once the OpenSpec change is created, the next step is to refine it with design skills before implementation begins. The spec describes the intent, but the design skills help shape the change into a safer engineering path: smaller slices, clearer compatibility decisions, stronger test strategy, and explicit review points.

> "There is no favorable wind for the sailor who doesn’t know where to go”
> ― Seneca

**Invest quality time in gathering and refining the requirements that belong in the spec.** The design phase should move deliberately at `1x`; once the spec is clear, implementation can safely accelerate toward `10x` because the intent, constraints, and verification path are already understood.

[![](/plinth/images/2026/7/kitchen-timer-new.png)](https://www.amazon.es/dp/B0893242P4?ref=nb_sb_ss_w_as-reorder_k2_1_13&amp=&crid=3MILANAXX5K8P&sprefix=kitchen%2Bclock&th=1)

Continue running [`Three Amigos sessions`](https://agilealliance.org/glossary/three-amigos/) to combine business, technology, and quality perspectives before implementation starts.

If you are interested in this new set of design skills, I recommend reading [From code generation to software engineering](/cursor-rules-java/blog/2026/06/from-code-generation-to-software-engineering.html).

<a id="comparing-plinth-commands-with-openspec-and-spec-kit"></a>

## Comparing Plinth commands with OpenSpec and Spec Kit

`Plinth commands` make software engineers' lives easier by hiding the tool-specific details of the SDD workflow. The comparison below shows how those commands map to `OpenSpec` and `Spec Kit` phases, from intake to specification, design refinement, task planning, and implementation.

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
      <td><code>/create-spec</code><br><code>/review-alignment</code></td>
      <td>Proposal<br><code>openspec new change &lt;change-name&gt;</code></td>
      <td><code>/speckit.specify</code> and <code>/speckit.clarify</code></td>
    </tr>
    <tr>
      <td><code>/create-spec</code></td>
      <td>Specification<br><code>openspec show &lt;change-name&gt;</code></td>
      <td><code>/speckit.specify</code> plus <code>/speckit.checklist</code></td>
    </tr>
    <tr>
      <td><code>/create-spec</code><br><code>/explore-design</code></td>
      <td>Task planning<br><code>openspec validate --all</code></td>
      <td><code>/speckit.plan</code> and <code>/speckit.tasks</code></td>
    </tr>
    <tr>
      <td><code>/implement-spec</code></td>
      <td>Implementation<br><code>openspec show &lt;change-name&gt;</code></td>
      <td><code>/speckit.implement</code></td>
    </tr>
    <tr>
      <td></td>
      <td>Review and closure<br><code>openspec archive &lt;change-name&gt;</code></td>
      <td><code>/speckit.analyze</code> and <code>/speckit.converge</code></td>
    </tr>
  </tbody>
</table>

Looking deeper into the command execution details, we can see the relationship between `Plinth commands` and `Plinth agents`:

<table>
  <thead>
    <tr>
      <th>Plinth command</th>
      <th>Owning agent</th>
      <th>Purpose</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><code>/update-issue</code></td>
      <td><code>@plinth-business-analyst</code></td>
      <td>Refines issue content across GitHub, Jira, or Azure DevOps inputs.</td>
    </tr>
    <tr>
      <td><code>/create-adr</code></td>
      <td><code>@plinth-architect</code></td>
      <td>Records architecture decisions, alternatives, and consequences.</td>
    </tr>
    <tr>
      <td><code>/create-diagram</code></td>
      <td><code>@plinth-architect</code></td>
      <td>Creates architecture or design diagrams from trusted source artifacts.</td>
    </tr>
    <tr>
      <td><code>/create-spec</code></td>
      <td><code>@plinth-tech-lead</code></td>
      <td>Applies planning, design, compatibility, and testing skills before tasking.</td>
    </tr>
    <tr>
      <td><code>/review-alignment</code></td>
      <td><code>@plinth-business-analyst</code></td>
      <td>Reviews traceability, consistency, gaps, and implementation readiness.</td>
    </tr>
    <tr>
      <td><code>/explore-design</code></td>
      <td><code>@plinth-architect</code></td>
      <td>Compares design options and recommends an approved technical direction.</td>
    </tr>
    <tr>
      <td><code>/implement-spec</code></td>
      <td><code>@plinth-tech-lead</code></td>
      <td>Delegates implementation to <code>@plinth-java-coder</code>, <code>@plinth-java-spring-boot-coder</code>, <code>@plinth-java-quarkus-coder</code>, <code>@plinth-java-micronaut-coder</code>, or <code>@plinth-no-java</code>.</td>
    </tr>
    <tr>
      <td><code>/profile</code></td>
      <td><code>@plinth-java-performance</code></td>
      <td>Coordinates profiling evidence and delegates application-code changes to the appropriate Java or framework coder.</td>
    </tr>
    <tr>
      <td><code>/benchmark</code></td>
      <td><code>@plinth-java-performance</code></td>
      <td>Coordinates JMeter, Gatling, or JMH performance work with reproducible thresholds and artifacts.</td>
    </tr>
  </tbody>
</table>

In future releases, the agents related to `Analysis & Design` will become more independent to increase decoupling between phases. For example, `/create-spec` currently maps to `@plinth-tech-lead`. Starting in release `0.18.0`, `@plinth-tech-lead` will participate only in implementation phases. This change will make the project more modular and easier to customize for your organization.

<a id="improving-migration-safety-with-flyway-mongock-and-parallel-change"></a>

## Improving migration safety with Flyway, Mongock, and Parallel Change

Database migrations are one of the easiest places for an AI agent to produce a clean-looking but unsafe patch. Bad migrations can damage a database in ways that are hard to recover from:

- A column is renamed in place while old application instances are still running, so writes start failing during a rolling deployment.
- A column is dropped because the new code no longer uses it, but a delayed worker, report, export, or integration still reads it.
- A backfill updates every row with the wrong default, overwriting meaningful historical data with a value that cannot be reconstructed.
- A migration changes money, time-zone, status, or identifier semantics without preserving the original business meaning.
- A `NOT NULL`, unique, or foreign-key constraint is added before dirty production data has been measured and cleaned.
- A long-running DDL statement locks a hot table and turns a small schema change into an availability incident.
- A MongoDB document migration rewrites nested fields without handling old variants, partial documents, or unknown enum values.
- A rollback restores the previous application version, but the destructive migration has already removed the shape that version needs.

This release improves the migration guidance for:

- [`@313-frameworks-spring-db-migrations-flyway`](https://www.skills.sh/jabrena/plinth/313-frameworks-spring-db-migrations-flyway)
- [`@413-frameworks-quarkus-db-migrations-flyway`](https://www.skills.sh/jabrena/plinth/413-frameworks-quarkus-db-migrations-flyway)
- [`@513-frameworks-micronaut-db-migrations-flyway`](https://www.skills.sh/jabrena/plinth/513-frameworks-micronaut-db-migrations-flyway)
- [`@316-frameworks-spring-mongodb-migrations-mongock`](https://www.skills.sh/jabrena/plinth/316-frameworks-spring-mongodb-migrations-mongock)
- [`@416-frameworks-quarkus-mongodb-migrations-mongock`](https://www.skills.sh/jabrena/plinth/416-frameworks-quarkus-mongodb-migrations-mongock)
- [`@516-frameworks-micronaut-mongodb-migrations-mongock`](https://www.skills.sh/jabrena/plinth/516-frameworks-micronaut-mongodb-migrations-mongock)

The new guidance emphasizes migration safety, antipatterns, and `Parallel Change`.

<table>
  <thead>
    <tr>
      <th>Phase</th>
      <th>Goal</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>Expand</td>
      <td>Add the new schema or document shape without removing the old one.</td>
    </tr>
    <tr>
      <td>Migrate</td>
      <td>Backfill, dual-write, compare, and observe.</td>
    </tr>
    <tr>
      <td>Contract</td>
      <td>Remove the old shape only after rollout evidence exists.</td>
    </tr>
  </tbody>
</table>

This is why `@055-design-parallel-change` belongs in the design workflow before framework-specific Flyway or Mongock implementation guidance. The agent should understand the transition before it writes the migration.

> **⚠️ AVOID RUNNING DATABASE MIGRATION TASKS WITHOUT HITL.**
Human-in-the-loop review is necessary because migrations can destroy or reinterpret production data, block critical tables, break rollback paths, and affect systems that are not visible in the local codebase. An agent can draft the plan, checks, and scripts, but a qualified human owner should review the migration intent, data impact, rollback strategy, monitoring evidence, and execution window before it reaches production.

**Note:** If your domain or subdomain owns microservices with associated databases and you are adopting GenAI tools, you should review the current architecture to mitigate potential production database issues.

If you want to go deeper into this topic, I recommend reading: [Why Do I Need to Use the Parallel Change Pattern?](/cursor-rules-java/blog/2026/07/why-do-i-need-to-use-the-parallel-change-pattern.html)

<a id="making-architecture-boundaries-visible-with-hexagonal-architecture"></a>

## Making architecture boundaries visible with Hexagonal Architecture

This release adds [`@707-technologies-hexagonal-architecture`](https://www.skills.sh/jabrena/plinth/707-technologies-hexagonal-architecture), a framework-agnostic skill for implementing Hexagonal Architecture in your applications.

As [Alistair Cockburn](https://alistair.cockburn.us/hexagonal-architecture) defines it in the original article:

> The hexagonal architecture, or ports and adapters architecture, is an architectural style used in software design. It aims at creating loosely coupled application components that can be easily connected to their software environment by means of ports and adapters.

![](/plinth/images/2026/7/hexagonal-architecture-new.png)

The skill helps engineers and agents inspect whether dependency direction and responsibility placement are consistent with Hexagonal Architecture:

- Domain code should not depend on framework adapters.
- Application services should coordinate use cases without becoming infrastructure code.
- Adapters should translate between external systems and the application boundary.
- Framework-specific details should stay outside the core model unless the team has made an explicit tradeoff.

The skill also includes `ArchUnit`-aware verification guidance without forcing every project to add a new dependency automatically. That is important because architecture tests should support the existing project context, not become cargo-cult configuration.

**Example:**

```java
@ArchTest
static final ArchRule should_keep_application_core_independent_from_adapters = noClasses()
        .that()
        .resideInAnyPackage("info.jab.domain..", "info.jab.mv.application..")
        .should()
        .dependOnClassesThat()
        .resideInAPackage("info.jab.mv.adapter..");

@ArchTest
static final ArchRule should_keep_domain_independent_from_application_services = noClasses()
        .that()
        .resideInAPackage("info.jab.mv.domain..")
        .should()
        .dependOnClassesThat()
        .resideInAPackage("info.jab.mv.application..");

@ArchTest
static final ArchRule should_keep_driving_adapters_independent_from_driven_adapters = noClasses()
        .that()
        .resideInAPackage("info.jab.mv.adapter.in..")
        .should()
        .dependOnClassesThat()
        .resideInAPackage("info.jab.mv.adapter.out..");

@ArchTest
static final ArchRule should_keep_driven_adapters_independent_from_driving_adapters = noClasses()
        .that()
        .resideInAPackage("info.jab.mv.adapter.out..")
        .should()
        .dependOnClassesThat()
        .resideInAPackage("info.jab.mv.adapter.in..");
```

For curious users, take a look at the `ArchUnit` documentation here: https://www.archunit.org/userguide/html/000_Index.html

For framework agents, the skill is useful before making changes in `Spring Boot`, `Quarkus`, or `Micronaut` applications. It gives the agent a boundary review language before it starts moving packages, introducing adapters, or changing service responsibilities.

<a id="adding-more-types-possibilities"></a>

## Modeling the domain with stronger Java types

Java is a strongly typed programming language, and that is not only a compiler detail. It is one of the main design tools available to a Java team. Good types make business rules visible, prevent accidental misuse, improve method signatures, and help agents reason about the code before they change it.

This matters especially when a domain concept has more meaning than its storage representation. A `String` can hold an email, an order id, a country code, or a currency code, but those values are not interchangeable. A `BigDecimal` can hold a numeric amount, but it does not explain whether the amount is euros, dollars, tax, discount, revenue, balance, or an exchange rate. When everything is modeled with generic types, invalid combinations become easier to write and harder to review.

For that reason, [`@111-java-maven-dependencies`](https://www.skills.sh/jabrena/plinth/111-java-maven-dependencies) now includes `JavaMoney` guidance. The skill helps agents recognize when a project should use Money and Currency API support, including [`JSR 354`](https://jcp.org/en/jsr/detail?id=354) and [`Moneta`](https://javamoney.github.io/api.html), instead of treating monetary values as isolated numbers.

This update combines well with [`@122-java-type-design`](https://www.skills.sh/jabrena/plinth/122-java-type-design), which focuses on type-safe wrappers, domain-specific value objects, and reducing primitive obsession in Java code.

Use this guidance when a project needs to model money, currencies, amounts, exchange-rate concerns, pricing, billing, financial calculations, or financial domain values where `BigDecimal` alone is too weak to describe the business meaning. The goal is not to add dependencies by default. The goal is to choose types that make the model harder to misuse and easier to evolve.

<a id="extending-eu-regulations-and-iso-engineering-review-skills"></a>

## Extending EU regulations and ISO standard engineering review skills

`0.16.0` introduced the first family of EU regulation engineering review skills. `0.17.0` extends that family with financial-market, market-integrity, product-liability, and AI management-system review support:

- [`@810-regulations-eu-mifid-ii`](https://www.skills.sh/jabrena/plinth/810-regulations-eu-mifid-ii)
- [`@811-regulations-eu-market-abuse-regulation`](https://www.skills.sh/jabrena/plinth/811-regulations-eu-market-abuse-regulation)
- [`@812-regulations-eu-product-liability-directive`](https://www.skills.sh/jabrena/plinth/812-regulations-eu-product-liability-directive)
- [`@813-regulations-iso-42001`](https://www.skills.sh/jabrena/plinth/813-regulations-iso-42001)

These skills include questionnaires, report templates, examples, and acceptance prompts.

The common workflow is:

1. Classify whether the system, product, workflow, or AI capability is in scope.
2. Ask for concrete engineering evidence.
3. Map regulatory or standard concerns to controls, logs, tests, approvals, documentation, and release gates.
4. Produce a report for qualified owner review.

These skills are engineering review aids. **They do not provide legal advice and they do not replace qualified legal, compliance, privacy, security, risk, product, audit, or governance owners.**

For fintech and enterprise teams, the practical value is early evidence. An AI agent working on trading workflows, issuer disclosure, surveillance alerts, product updates, model governance, RAG systems, or AI-assisted delivery should know which engineering questions may need owner review before release.

The current regulation and standard review coverage is:

<table>
  <thead>
    <tr>
      <th>Review skill</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><a href="https://www.skills.sh/jabrena/plinth/801-regulations-eu-ai-act"><code>@801-regulations-eu-ai-act</code></a><br>Reviews AI-system scope, risk classification, transparency, human oversight, evidence, and owner handoff.</td>
    </tr>
    <tr>
      <td><a href="https://www.skills.sh/jabrena/plinth/802-regulations-dora"><code>@802-regulations-dora</code></a><br>Reviews digital operational resilience for financial entities and ICT-dependent business services.</td>
    </tr>
    <tr>
      <td><a href="https://www.skills.sh/jabrena/plinth/803-regulations-gdpr"><code>@803-regulations-gdpr</code></a><br>Reviews privacy engineering concerns around personal data collection, processing, logging, sharing, and deletion.</td>
    </tr>
    <tr>
      <td><a href="https://www.skills.sh/jabrena/plinth/804-regulations-eu-nis2"><code>@804-regulations-eu-nis2</code></a><br>Reviews cybersecurity and resilience evidence for essential, important, and critical-sector services.</td>
    </tr>
    <tr>
      <td><a href="https://www.skills.sh/jabrena/plinth/805-regulations-eu-cyber-resilience-act"><code>@805-regulations-eu-cyber-resilience-act</code></a><br>Reviews product-security evidence for software and products with digital elements.</td>
    </tr>
    <tr>
      <td><a href="https://www.skills.sh/jabrena/plinth/806-regulations-eu-data-act"><code>@806-regulations-eu-data-act</code></a><br>Reviews connected-product, related-service, data-sharing, portability, and cloud-switching concerns.</td>
    </tr>
    <tr>
      <td><a href="https://www.skills.sh/jabrena/plinth/807-regulations-eu-digital-services-act"><code>@807-regulations-eu-digital-services-act</code></a><br>Reviews due-diligence and transparency evidence for intermediaries, hosting services, and platforms.</td>
    </tr>
    <tr>
      <td><a href="https://www.skills.sh/jabrena/plinth/808-regulations-eu-digital-markets-act"><code>@808-regulations-eu-digital-markets-act</code></a><br>Reviews gatekeeper-platform concerns around core platform services, interoperability, and fair access.</td>
    </tr>
    <tr>
      <td><a href="https://www.skills.sh/jabrena/plinth/810-regulations-eu-mifid-ii"><code>@810-regulations-eu-mifid-ii</code></a><br>Reviews investment-services, trading, investor-protection, transparency, and record-keeping evidence.</td>
    </tr>
    <tr>
      <td><a href="https://www.skills.sh/jabrena/plinth/811-regulations-eu-market-abuse-regulation"><code>@811-regulations-eu-market-abuse-regulation</code></a><br>Reviews market-integrity evidence for issuer disclosures, insider information, surveillance, and alerts.</td>
    </tr>
    <tr>
      <td><a href="https://www.skills.sh/jabrena/plinth/812-regulations-eu-product-liability-directive"><code>@812-regulations-eu-product-liability-directive</code></a><br>Reviews software, AI-enabled product, component, update, and post-release safety evidence.</td>
    </tr>
    <tr>
      <td><a href="https://www.skills.sh/jabrena/plinth/813-regulations-iso-42001"><code>@813-regulations-iso-42001</code></a><br>Reviews AI management-system evidence for organizational AI governance and lifecycle controls.</td>
    </tr>
  </tbody>
</table>

If you are interested in this topic, I recommend reading both articles: [Introduction to EU regulations Part I](/cursor-rules-java/blog/2026/06/introduction-to-eu-regulations-part-i.html) and [Introduction to EU regulations Part II](/cursor-rules-java/blog/2026/07/introduction-to-eu-regulations-part-ii.html)

<a id="improving-security-gates-in-the-pipeline-with-virustotal"></a>

## Improving security gates in the pipeline with VirusTotal

This release also strengthens the validation path around generated content, release artifacts, and documentation.

The pipeline now includes `VirusTotal` checks before generated artifacts are promoted. In the latest scan, `VirusTotal` evaluated the artifact with `75` security applications and engines. This is not a replacement for the build, tests, or human review, but it gives maintainers one more piece of evidence when deciding whether a generated artifact is ready to publish.

The scan included the following applications and engines: `ALYac`, `APEX`, `AVG`, `Acronis`, `AhnLab-V3`, `Alibaba`, `Antiy-AVL`, `Arcabit`, `Avast`, `Avast-Mobile`, `Avira`, `BitDefender`, `BitDefenderFalx`, `Bkav`, `CAT-QuickHeal`, `CMC`, `CTX`, `ClamAV`, `CrowdStrike`, `Cylance`, `Cynet`, `DeepInstinct`, `DrWeb`, `ESET-NOD32`, `Elastic`, `Emsisoft`, `F-Secure`, `Fortinet`, `GData`, `Google`, `Gridinsoft`, `Ikarus`, `Jiangmin`, `K7AntiVirus`, `K7GW`, `Kaspersky`, `Kingsoft`, `Lionic`, `Malwarebytes`, `MaxSecure`, `McAfeeD`, `MicroWorld-eScan`, `Microsoft`, `NANO-Antivirus`, `Paloalto`, `Panda`, `Rising`, `SUPERAntiSpyware`, `Sangfor`, `SentinelOne`, `Skyhigh`, `Sophos`, `Symantec`, `SymantecMobileInsight`, `TACHYON`, `Tencent`, `Trapmine`, `TrellixENS`, `TrendMicro`, `TrendMicro-HouseCall`, `Trustlook`, `VBA32`, `VIPRE`, `Varist`, `ViRobot`, `VirIT`, `Webroot`, `Xcitium`, `Yandex`, `Zillya`, `ZoneAlarm`, `Zoner`, `alibabacloud`, `huorong`, `tehtris`.

<a id="recommended-books-talks-for-this-summer"></a>

## Recommended Books and Talks for this summer

**Specifications:**

[Specification by Example](https://www.manning.com/books/specification-by-example), by [Gojko Adzic](https://gojko.net/), is a fantastic book for learning how to turn real examples into clear, testable specifications that business and engineering teams can share.

For a software engineer, the value of this book is very practical. It teaches how to move from vague requirements to concrete examples that expose ambiguity before code is written. That habit improves conversations with product owners, helps developers discover edge cases earlier, and gives testers a stronger basis for acceptance criteria.

It is also a useful book for teams adopting AI coding tools. Agents work better when the expected behavior is described with precise examples instead of broad intentions. A good example can become a shared requirement, a test case, a review checklist, and a prompt input. That makes the delivery workflow more explicit and reduces the risk of generating code that looks correct but solves the wrong problem.

[![](/plinth/images/2026/7/specification-by-example-new.png)](https://www.manning.com/books/specification-by-example)

**Spec-Driven Development:**

I recommend watching one of the recent [talks](https://www.youtube.com/watch?v=35dH6q18UtI) from [Simon Martinelli](https://martinelli.ch/) about Spec-Driven Development. I appreciate the common sense in his ideas.

The useful message for engineers is that a specification is not paperwork created after the real work is done. A good spec describes the use cases, business rules, examples, constraints, and acceptance expectations that should guide implementation. When the use cases are explicit, developers can reason about behavior before choosing frameworks, persistence details, or API shapes.

**Empirical Software Design:**

For daily design discipline, I recommend [`Tidy First?`](https://www.oreilly.com/library/view/tidy-first/9781098151232/) by [Kent Beck](https://www.kentbeck.com/). The book is short, practical, and focused on small structural improvements that make the next behavior change easier.

Its value for software engineers is the distinction between tidying and changing behavior. Before adding a feature, fixing a bug, or asking an agent to modify code, sometimes the best first move is a tiny design improvement: clarify a name, separate a responsibility, remove a little duplication, or make a dependency direction easier to see. Those small moves reduce risk without turning every change into a refactoring project.

[![](/plinth/images/2026/7/tidy-first-new.jpg)](https://www.oreilly.com/library/view/tidy-first/9781098151232/)

**LEAN Development:**

If you are interested in improving your LEAN skills, [Eduardo Ferro](https://www.eferro.net/) has published an excellent book:

[`Menos software, más impacto`](https://menos-software.eferro.net/) is especially useful for engineers who feel their team is running faster but delivering less real value. The book frames software as something that must be cultivated, not merely built once and forgotten. Every feature, integration, dependency, and line of code creates a permanent maintenance cost, even when nobody is actively changing it.

The practical lesson is close to the spirit of this release: good engineering is not only about generating more code. It is also about protecting team capacity through active simplicity, reducing work in progress, removing unused functionality, building quality from the beginning, and choosing what not to build. That mindset is healthy for human teams and even more important when agents can produce large amounts of code very quickly.

[![](/plinth/images/2026/7/menos-software-mas-impacto-new.jpg)](https://menos-software.eferro.net/)

**Hexagonal Architecture:**

If you are interested in Hexagonal Architecture, I recommend [Alistair Cockburn's book](https://alistair.cockburn.us/hexagonal-architecture):

Hexagonal Architecture, also known as `Ports and Adapters`, is valuable because it protects the application core from accidental dependency on external technologies. The business behavior should not be trapped inside a web controller, a database repository, a message broker consumer, or a framework callback. Instead, the application exposes ports that describe purposeful conversations, and adapters translate between those ports and the outside world.

That separation gives engineers several practical benefits: use cases can be tested without a real UI or database, adapters can be replaced when technology changes, and architecture reviews can focus on whether business logic is leaking across the boundary. For teams using AI agents, this is especially important. A clear inside/outside boundary gives the agent a better map of where domain behavior belongs and where framework-specific code should stay.

[![](/plinth/images/2026/7/hexagonal-architecture-explained-new.jpg)](https://www.amazon.com/Hexagonal-Architecture-Explained-Alistair-Cockburn/dp/173751978X)

If you use the project and have nice books to share that fit this engineering direction, share your recommendations in [`GitHub Discussions`](https://github.com/jabrena/plinth/discussions).

<a id="next-steps"></a>

## Next steps

For the next release, we plan to work on a few topics:

- Add `Katas` that help users learn `Plinth` incrementally.
- Improve behavior across the `Design phase`.
- Decouple agents for design and implementation.
- Make the project more modular so it is easier to integrate into organizations.
- Update the `Spring Boot` support for `4.1.0`.
- Add a skill about `JVM Flags`.
- Go deeper into the EU regulation ecosystem for `GenAI`.
