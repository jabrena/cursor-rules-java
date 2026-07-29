title=From Code Generation to Software Engineering: Why AI Agents Need Design Skills
date=2026-06-30
type=post
tags=blog,skills,agents,design,software-engineering
author=MyRobot
status=published
~~~~~~

## The shift from generation to engineering

The first wave of AI-assisted development was mostly about speed. Ask the model for a class, a test, a migration, a controller, a README section, or a refactoring, then review what came back.

That capability is useful, but it is not the same as software engineering.

Software engineering is not only the ability to produce code. It is the ability to change a system while preserving its intent, controlling risk, reducing uncertainty, and keeping future change affordable. This is where many AI workflows still struggle. A model can generate plausible code in seconds, but without design heuristics it can also make the architecture slightly worse in every interaction.

That is why this project is adding explicit design skills for AI agents. In the `README.md` Plan workflow, design judgment is applied during `/create-spec`, while the proposal, requirements, and tasks are still being shaped:

```text
/create-spec
@plinth-tech-lead
    @042-planning-openspec
    @051-design-two-steps-methods
    @052-design-hamburger-method
    @053-design-simple-rules
    @054-design-tdd
    @055-design-parallel-change
    @056-design-avoid-breaking-changes
    @121-java-object-oriented-design
    @122-java-type-design
    @123-java-design-patterns
    @130-java-testing-strategies
```

The first group shapes the change. The second group evaluates the Java design itself: object boundaries, type choices, patterns, and testing strategy. `@056-design-avoid-breaking-changes` then supports the review path when a plan, spec, public API, command, skill, schema, or generated output may affect compatibility.

Together, these skills move the conversation from "generate the implementation" to "shape the change through OpenSpec".

## From Prompting engineering to Spec Driven development with OpenSpec

Earlier AI-assisted workflows in this project were centered on reusable rules, system prompts, and skills. That was a necessary first step. It gave agents a common vocabulary for Java, Maven, testing, documentation, architecture, security, and framework-specific work.

The next step was workflow coordination: commands, agents, and role-specific guidance that help a team move from an idea to an issue, from an issue to a plan, and from a plan to implementation. That made the process more repeatable, but it still left an important question: where should design decisions live before code generation starts, and how can other engineers review them?

Spec-driven development answers that question. OpenSpec becomes the coordination layer where the problem, proposed solution, requirements, scenarios, task sequence, compatibility risks, and verification strategy are written down before the agent edits the codebase.

This is why prompting an LLM with "write clean code" or "follow best practices" is insufficient. Those phrases do not tell the agent how to split a risky refactor, when to introduce a compatibility window, which behavior should be protected by a failing test first, or how to decide whether a simpler design is actually better.

The evolution is from reusable prompts to reusable workflows, and from reusable workflows to reviewable specifications. Design skills make that transition practical because they give OpenSpec concrete heuristics to apply. The specification captures the decision; the skill explains how the agent reached it.

The following skills are applied inside that planning flow. They turn OpenSpec from a template into a reviewable engineering plan, and each one is explained below:

- `@051-design-two-steps-methods`
- `@052-design-hamburger-method`
- `@053-design-simple-rules`
- `@054-design-tdd`
- `@055-design-parallel-change`
- `@056-design-avoid-breaking-changes`
- `@121-java-object-oriented-design`
- `@122-java-type-design`
- `@123-java-design-patterns`
- `@130-java-testing-strategies`

### Two-step changes

The two-step method is one of the most important design heuristics for agents because it stops the model from mixing two different activities: making the next change easy and making the change itself.

[Kent Beck](https://kentbeck.com/) captured the idea in a compact rule: for each desired change, first make the change easy, then make the easy change. The preparation can be hard, but it should preserve behavior. The intended behavior change comes only after that preparation is verified.

<blockquote class="twitter-tweet"><p lang="en" dir="ltr">for each desired change, make the change easy (warning: this may be hard), then make the easy change</p>&mdash; Kent Beck (@KentBeck) <a href="https://x.com/KentBeck/status/250733358307500032?ref_src=twsrc%5Etfw">September 25, 2012</a></blockquote>
<script async src="https://platform.twitter.com/widgets.js" charset="utf-8"></script>

In practice, the first step might rename concepts, extract a collaborator, move logic behind an interface, add characterization tests, or separate a tangled dependency. None of those steps should change what the system does. They should make the intended change smaller, more local, and easier to review.

For AI agents, this matters because they can produce large patches very quickly. A large patch that refactors and changes behavior at the same time is difficult to review. When something fails, the team cannot easily tell whether the new behavior is wrong or the preparation damaged existing behavior.

`@051-design-two-steps-methods` gives the agent a sequence:

1. Identify the behavior that must change.
2. Prepare the code without changing that behavior.
3. Verify that the old behavior still holds.
4. Make the smallest behavior change.
5. Verify the final result.

This turns a risky leap into a controlled path.

### The hamburger method

Large features are dangerous when they are decomposed horizontally. A plan that says "build all repositories, then all services, then all controllers, then all tests" creates a long interval where nothing is truly usable.

The hamburger method, associated with [Gojko Adzic](https://gojko.net/), pushes the team to find the smallest useful vertical slice first. Instead of delivering one layer at a time, the team builds a thin slice through the whole product: a bit of user-visible behavior, a bit of domain logic, a bit of persistence or integration, and enough verification to know whether the idea works.

The metaphor matters because a hamburger is not useful if someone serves only the bread, only the filling, or only the sauce. Software has the same problem. A repository layer by itself is not product feedback. A controller without behavior is not product feedback. A test suite for a design that users cannot exercise is not product feedback.

For agents, this is a major upgrade. Instead of generating a full architecture from a large prompt, the agent learns to ask: what is the first thin slice that delivers real value and teaches us something about the design? That slice may be deliberately small: one command, one endpoint, one workflow path, one validation rule, or one state transition. The important part is that it crosses enough boundaries to expose real design evidence.

`@052-design-hamburger-method` helps turn broad requests into a sequence of independently valuable slices. That makes implementation easier to review, easier to test, and easier to stop when the design evidence changes.

### TDD as design pressure

Test-driven development, invented by [Kent Beck](https://kentbeck.com/), is often described as a testing practice. For agents it is even more valuable as a design practice.

When an agent starts with production code, it can accidentally optimize for implementation convenience. When it starts with a failing test, it must first express the behavior from the outside: what input matters, what outcome is observable, what error should be reported, and what contract the caller can rely on. That pressure improves naming, boundaries, error handling, and the shape of collaborators.

The useful part is not only "write tests first". The useful part is the short feedback loop. A failing test narrows the intent. The smallest passing implementation avoids speculative design. The refactoring step improves the structure while the behavior is protected. For an AI agent, that sequence is a guardrail against generating a large plausible implementation before the design has been tested by an example.

`@054-design-tdd` gives the agent a disciplined loop:

1. List candidate behaviors and edge cases.
2. Pick the next behavior.
3. Write a failing test first.
4. Make the smallest change that passes.
5. Refactor while the test suite is green.

The result is not only better test coverage. It is better design feedback during the change, because every step forces the agent to connect implementation choices to executable behavior.

### Simple design rules

Simple design is not minimalism for its own sake. It is an ordering of priorities. [Kent Beck](https://kentbeck.com/) described a compact set of rules for evaluating design quality, and [Martin Fowler's article on Beck's design rules](https://martinfowler.com/bliki/BeckDesignRules.html) is a useful reference because it makes the ordering explicit.

That ordering is important for agents because they can produce several plausible designs very quickly. Without a priority sequence, the model may optimize for the wrong thing: fewer files, a familiar pattern, a clever abstraction, or a tidy-looking class hierarchy. Simple design asks a stricter question: which design keeps behavior verified and makes the next change easier to understand?

`@053-design-simple-rules` follows a practical sequence:

1. The design passes the tests.
2. The design reveals intention.
3. The design removes duplication.
4. The design has the fewest elements.

This ordering matters. An agent should not remove duplication by hiding intent. It should not reduce the number of classes by making one class harder to understand. It should not introduce an abstraction because the prompt asked for a pattern.

For example, a generated helper may reduce repeated lines but make the domain language harder to see. A generic framework-style abstraction may reduce the number of concepts in one file while adding indirection across the codebase. A single large service may look simpler than several small collaborators until a reviewer tries to understand the behavior it protects.

Simple design rules give the agent a way to compare alternatives without drifting into taste-based arguments. The question becomes: which option keeps behavior verified, communicates intent, reduces real duplication, and avoids unnecessary parts? That makes design review more concrete: the team can ask the agent to justify its choice against the rules, not against vague preferences such as "clean" or "elegant".

### Parallel change

Database and data-meaning changes are where naive code generation can become especially risky. A generated migration may work locally while still breaking a rolling deployment, a delayed consumer, a cached projection, or a reporting pipeline.

[Parallel change](https://martinfowler.com/bliki/ParallelChange.html), also known as "expand and contract", was documented by [Danilo Sato](https://www.dtsato.com/blog/about/) in an article published on [Martin Fowler's website](https://martinfowler.com/) in May 2014. The useful insight is that a backward-incompatible change does not have to be deployed as a single switch-over moment.

Parallel change exists for this reason. Instead of replacing old behavior with new behavior in one step, it creates a compatibility period. The team first expands the system so both shapes can work, then migrates usage gradually, then contracts the old path after there is evidence that nothing still depends on it.

1. Expand the system so old and new shapes can coexist.
2. Migrate reads, writes, data, and clients gradually.
3. Contract the old path only after evidence shows it is unused.

This is especially important for AI agents because the local code change is rarely the whole system change. A field rename may affect database rows, API clients, JSON payloads, search indexes, reports, events, dashboards, tests, documentation, and support scripts. If the agent only edits the code it can see, it may accidentally turn a small request into a production compatibility problem.

The expand step might add a nullable column, dual-write old and new fields, accept both API shapes, or publish both event versions. The migrate step moves readers, writers, data, and consumers while telemetry shows which path is still active. The contract step removes the legacy shape only when the team has evidence that rollback risk is acceptable.

`@055-design-parallel-change` helps the agent reason about schema shape, data meaning, application read/write paths, rollout windows, rollback expectations, and production-data risk before proposing implementation steps.

That is the difference between "generate a migration" and "design a migration".

### Java design skills in the same planning loop

The design-method skills are intentionally combined with Java-specific skills in the README workflow.

`@121-java-object-oriented-design` helps the agent review responsibilities, cohesion, coupling, encapsulation, and code smells. This matters during OpenSpec planning because many implementation failures begin as vague object boundaries in the spec.

`@122-java-type-design` focuses attention on records, enums, sealed hierarchies, generics, nullability, value objects, and domain types. Good type choices reduce the amount of defensive code the agent has to generate later.

`@123-java-design-patterns` helps evaluate whether a pattern is actually useful for the problem. An AI agent can easily over-apply patterns because pattern-shaped code looks sophisticated. In an OpenSpec flow, the skill should justify the pattern against the requirement, not against aesthetic preference.

`@130-java-testing-strategies` complements TDD by broadening the verification conversation. Some behaviors need unit tests. Others need integration tests, contract tests, acceptance scenarios, property-style checks, or manual evidence. During OpenSpec planning, that distinction belongs in the tasks, not as an afterthought after code generation.

Together, these Java skills connect design heuristics to implementation reality. They help the agent ask: what should the model of the system be, what types should carry the meaning, what patterns are justified, and how will the team know the behavior works?

### Breaking-change avoidance

AI agents need explicit compatibility thinking because they often optimize for the local request. If the prompt says "rename this field", the model may rename the field. A human engineer has to ask who depends on that field.

Breaking-change avoidance makes compatibility surfaces visible:

- Public APIs
- Generated skills and commands
- Configuration keys
- Database schemas
- Event contracts
- Documentation consumed by users
- Release behavior
- Test fixtures and examples

`@056-design-avoid-breaking-changes` gives the agent a review posture before it changes those surfaces. The goal is not to forbid breaking changes forever. The goal is to make them deliberate, documented, versioned, and coordinated.

## Share your experience

If you are using these workflows, experimenting with OpenSpec, or trying to introduce design skills into AI-assisted delivery, share your experience with the project.

Use [GitHub Discussions](https://github.com/jabrena/plinth/discussions) to post what is working, where the workflow feels unclear, which doubts you have, and what worries you about applying agents to real Java systems.

Those conversations help improve the skills, the documentation, and the project workflow itself.
