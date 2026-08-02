title=What's new in Plinth 0.18.0?
date=2026-08-03
type=post
tags=blog,skills,java,agents,commands,openspec,benchmark
author=Juan Antonio Breña Moral
status=published
~~~~~~

`Plinth` is an AI-native engineering toolkit for modern Java enterprise SDLC, built around reusable `Commands`, `Agents`, `Skills`, and `MCP Servers`.

This release focuses on two things. First, `Plinth` now treats specification as two explicit phases — Functional and Technical — with new commands and skills that turn a raw issue into agreed requirements and an approved design before any code gets written. Second, a new reproducible benchmark puts that whole workflow to the test, comparing how different AI agent tools perform across several scenarios with increasing amounts of structure available. The idea behind both is that `"better code generation begins before code generation"`: give an agent enough context to understand the problem, enough structure to make its design decisions explicit, and enough evidence to know when the work is actually done.

Thanks to our community members in [`Urumqi`](https://www.google.com/maps/search/?api=1&query=Urumqi), [`Singapore`](https://www.google.com/maps/search/?api=1&query=Singapore), [`Des Moines`](https://www.google.com/maps/search/?api=1&query=Des+Moines), [`Madrid`](https://www.google.com/maps/place/Madrid), and [`Bengaluru`](https://www.google.com/maps/search/?api=1&query=Bengaluru). 👋👋👋

This article is divided into the following sections:

- [Community first!](#community-first)
- [What are the top 10 skills from this project on Skills.sh?](#what-are-the-top-10-skills-from-this-project-in-skillssh)
- [Adding better Functional and Technical specification support](#adding-better-functional-and-technical-specification-support)
- [How to use Plinth in 0.18.0](#how-to-use-plinth-in-0180)
- [Comparing Plinth commands with OpenSpec and Spec Kit](#comparing-plinth-commands-with-openspec-and-spec-kit)
- [Comparing Plinth with mattpocock/skills and Superpowers](#comparing-plinth-with-mattpocockskills-and-superpowers)
- [Why I review changes when working with agents and skill scanners](#why-i-review-changes-when-working-with-agents-and-skill-scanners)
- [Testing the workflow with a reproducible benchmark](#testing-the-workflow-with-a-reproducible-benchmark)
- [Do you still have questions about the project?](#doubts)
- [Next steps](#next-steps)

If you have questions about the project, how to customize it for your team, how to use the skills in daily work, or how to solve tooling issues, use [`GitHub Discussions`](https://github.com/jabrena/plinth/discussions).

**Help this project grow:** [If this project helps your team, become a sponsor.](https://github.com/sponsors/jabrena)

<a id="community-first"></a>

## Community first!

In this release, I want to publicly express my sincere gratitude to the `Cursor team` for their generous initial support in running the first [MadridJug](https://madridjug.es/) Meetup about `Cursor AI` in 2025. This is the last month I'll be drawing on the tokens they sponsored, and with that help, this project has gone on to make a real contribution to the `Java Community`.

Beyond that sponsorship, this release also benefited from the community itself: thank you to [`Vidya Mmadireddy`](https://github.com/vidya166) and [`Leandro Loureiro`](https://github.com/lealoureiro) for their initiatives, contributions, and effort this cycle.

If you would like to participate, review the open issues labeled [`good first issue`](https://github.com/jabrena/plinth/issues?q=is%3Aissue%20state%3Aopen%20label%3A%22good%20first%20issue%22), propose improvements, test the workflow with another agent tool, or share your experience in [`GitHub Discussions`](https://github.com/jabrena/plinth/discussions).

<a id="what-are-the-top-10-skills-from-this-project-in-skillssh"></a>

## What are the top 10 skills from this project on Skills.sh?

The [Skills.sh registry](https://www.skills.sh/jabrena/plinth) reports `125 skills` and `19.6K` installs in total — roughly `35%` growth since the [`0.17.0` release](https://jabrena.github.io/plinth/blog/2026/07/release-0.17.0.html#what-are-the-top-10-skills-from-this-project-in-skillssh). Compared with `0.17.0`, these are the current top 10 skills among Skills.sh users:

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

<a id="adding-better-functional-and-technical-specification-support"></a>

## Adding better Functional and Technical specification support

Many failed implementations begin with a weak problem statement. A short issue can mix symptoms, assumptions, requested solutions, stakeholder expectations, and technical constraints without making any of them explicit. If an agent starts coding from that issue, it may efficiently implement the wrong change.

`Plinth` addresses this by treating specification as two distinct phases: a **Functional Specification** phase that turns a raw issue into agreed business requirements, and a **Technical Specification** phase that turns those requirements into an OpenSpec design and task list. Each phase has its own dedicated commands:

<table>
  <thead>
    <tr>
      <th>Phase</th>
      <th>Command</th>
      <th>Purpose</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td rowspan="3">Functional Specification</td>
      <td><code>/update-issue</code></td>
      <td>Update an issue description with structured, evidence-backed content</td>
    </tr>
    <tr>
      <td><code>/explore-problem</code></td>
      <td>Analyze an issue through five lenses and post a Functional Specification</td>
    </tr>
    <tr>
      <td><code>/create-acceptance-criteria</code></td>
      <td>Derive and post confirmed Gherkin acceptance criteria for an issue</td>
    </tr>
    <tr>
      <td rowspan="2">Technical Specification</td>
      <td><code>/create-spec</code></td>
      <td>Create or update OpenSpec artifacts from approved source material</td>
    </tr>
    <tr>
      <td><code>/explore-design</code></td>
      <td>Refine the technical design of an issue or OpenSpec change before implementation</td>
    </tr>
    <tr>
      <td rowspan="2">Implementation</td>
      <td><code>/implement-spec</code></td>
      <td>Deliver an approved plan or OpenSpec change through controlled implementation</td>
    </tr>
    <tr>
      <td><code>/close-spec</code></td>
      <td>Archive a completed OpenSpec change by name</td>
    </tr>
  </tbody>
</table>

Like a good dish, good requirements take time and more than one cook. These commands give the process structure, but they work best paired with real collaboration — [Three Amigos sessions](https://agilealliance.org/glossary/three-amigos/) that bring business, development, and testing perspectives together before a single line of code is written.

### Functional Specification

The Functional Specification phase turns a raw issue into agreed business requirements through three commands:

```text
Issue
  |
  v
/update-issue --> /explore-problem --> /create-acceptance-criteria
```

The new `/explore-problem` command investigates a problem before committing to a solution, then produces a Functional Specification that can be posted back to the source issue. This creates a stronger bridge between issue tracking and specification work: the issue remains the place where the business need is discussed, while the exploration result gives the next phase a more disciplined starting point.

To do that investigation, `/explore-problem` coordinates five complementary skills — one per lens:

- [`@021-problem-framing`](https://www.skills.sh/jabrena/plinth/021-problem-framing) defines the observed problem, affected actors, evidence, impact, and boundaries.
- [`@022-root-cause-analysis`](https://www.skills.sh/jabrena/plinth/022-root-cause-analysis) separates symptoms from plausible causes and identifies evidence needed to confirm them.
- [`@023-assumption-analysis`](https://www.skills.sh/jabrena/plinth/023-assumption-analysis) makes hidden beliefs visible and classifies which assumptions need validation.
- [`@024-context-mapping`](https://www.skills.sh/jabrena/plinth/024-context-mapping) identifies stakeholders, systems, dependencies, boundaries, and external constraints.
- [`@025-quality-attribute-discovery`](https://www.skills.sh/jabrena/plinth/025-quality-attribute-discovery) discovers the performance, security, reliability, maintainability, and operational qualities that may shape the solution.

The five lenses are deliberately separate. A root-cause hypothesis is not the same as a requirement. A stakeholder constraint is not the same as a quality attribute. An assumption is not evidence. Keeping those categories visible helps both humans and agents challenge the problem definition before implementation costs begin to accumulate.

The new `/create-acceptance-criteria` command turns that Functional Specification into something an implementation can be checked against: it reads the specification posted by `/explore-problem`, applies the new [`@058-design-bdd`](https://www.skills.sh/jabrena/plinth/058-design-bdd) skill to turn its actors, outcomes, and business rules into concrete Given/When/Then scenarios, and posts the confirmed result back to the issue. Scenarios earn their place here because prose hides ambiguity that examples expose — "invalid requests are rejected" is too broad to implement reliably, while a scenario forces the team to state which inputs are invalid, what response is expected, and what observable behavior proves it.

### Technical Specification

That Functional Specification then feeds directly into OpenSpec delivery. This release strengthens the command path from an issue to an archived OpenSpec change:

```text
Issue
  |
  v
/update-issue --> /explore-problem --> /create-acceptance-criteria
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

<a id="how-to-use-plinth-in-0180"></a>

## How to use Plinth in 0.18.0

`Plinth` supports two different entry points, and the right one depends on whether there is a requirement behind the change or just existing code to improve.

### Use Plinth as your AI-Native Development workflow

Use the full command sequence for genuinely complex problems whenever the change starts from a business need — an issue, a user story, or a bug report:

```text
Issue
  |
  v
/update-issue --> /explore-problem --> /create-acceptance-criteria
  |
  v
/create-spec --> /explore-design
  |
  v
/implement-spec --> /close-spec
```

Every phase is owned by a specific agent and gated by evidence, so nothing skips ahead of what has actually been agreed.

### Refactor your Java classes with Plinth skills

Use a skill directly, without OpenSpec, when there is no new requirement — just code that should be brought up to a known standard. `Plinth`'s `125 skills` are organized into numbered families by topic — Java core practices, testing, framework-specific guidance, and so on — and all of them are model-invoked: point an agent at a file or package and ask it to apply one, and it reviews the code against that skill's good and bad examples directly — no issue, no OpenSpec change, no agent handoff.

The four examples below reuse skills already familiar from the [Top 10 Skills](#what-are-the-top-10-skills-from-this-project-in-skillssh) table above:

[`@121-java-object-oriented-design`](https://www.skills.sh/jabrena/plinth/121-java-object-oriented-design) replaces type-dependent conditionals with polymorphism, so adding a new shape no longer requires modifying the area calculation:

```java
// Before — the calculation must change whenever a shape is added
double area(Object shape) {
    return switch (shape) {
        case Rectangle r -> r.width() * r.height();
        case Circle c -> Math.PI * c.radius() * c.radius();
        default -> throw new IllegalArgumentException("Unknown shape");
    };
}

// After — each shape owns its calculation
interface Shape {
    double area();
}

record Rectangle(double width, double height) implements Shape {
    @Override
    public double area() {
        return width * height;
    }
}

record Circle(double radius) implements Shape {
    @Override
    public double area() {
        return Math.PI * radius * radius;
    }
}
```

`@125-java-concurrency` moves a concurrent fan-out from `CompletableFuture` to `StructuredTaskScope`:

```java
// Before — CompletableFuture
Response handle(Request request) {
    CompletableFuture<Customer> customer =
        CompletableFuture.supplyAsync(() -> findCustomer(request.customerId()));
    CompletableFuture<OrderHistory> orders =
        CompletableFuture.supplyAsync(() -> fetchOrderHistory(request.customerId()));

    return customer.thenCombine(orders, Response::new).join();
}

// After — StructuredTaskScope (Java 25 preview, JEP 505)
Response handle(Request request)
        throws InterruptedException, StructuredTaskScope.TimeoutException {
    try (var scope = StructuredTaskScope.open(
            StructuredTaskScope.Joiner.<Object>awaitAllSuccessfulOrThrow(),
            config -> config.withTimeout(Duration.ofSeconds(2)))) {
        Subtask<Customer> customer = scope.fork(() -> findCustomer(request.customerId()));
        Subtask<OrderHistory> orders = scope.fork(() -> fetchOrderHistory(request.customerId()));

        scope.join(); // Cancels unfinished subtasks and throws on timeout
        return new Response(customer.get(), orders.get());
    }
}
```

**Note:** It is reasonable to use Structured Concurrency in your development, although it is a preview feature. The timeout starts when the scope opens; if it expires before `join()` completes, the scope is cancelled and unfinished subtasks are interrupted.

`@142-java-functional-programming` replaces a thrown exception with the Result pattern — the same "invalid requests are rejected" case from the BDD section above, made explicit in the type system:

```java
// Before
public static Request validateRequest(Request request) {
    if (request.amount() <= 0) {
        throw new IllegalArgumentException("Amount must be positive");
    }
    return request;
}

// After
sealed interface Result<T> permits Ok, Ko {}
record Ok<T>(T value) implements Result<T> {}
record Ko<T>(String error) implements Result<T> {}

public static Result<Request> validateRequest(Request request) {
    return (request.amount() <= 0)
        ? new Ko<>("Amount must be positive")
        : new Ok<>(request);
}
```

`@128-java-generics` applies the PECS principle — Producer `extends`, Consumer `super` — to correct the wildcard direction. A method that only reads from a collection should use `? extends T`, while one that only writes should use `? super T`:

```java
// Before — wrong wildcard direction forces an unsafe cast
public static double sum(List<? super Number> numbers) {
    double total = 0.0;
    for (Object num : numbers) {
        total += ((Number) num).doubleValue(); // Unsafe cast required
    }
    return total;
}

// After — this method only reads, so it's a producer: use extends
public static double sum(List<? extends Number> numbers) {
    double total = 0.0;
    for (Number num : numbers) { // Safe: every item is guaranteed to be a Number
        total += num.doubleValue();
    }
    return total;
}
```

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

[`mattpocock/skills`](https://github.com/mattpocock/skills) and [`Superpowers`](https://github.com/obra/superpowers) are two of the most widely adopted general-purpose agent-skill collections outside the Java ecosystem. Neither targets Java Enterprise development specifically, but both converge on the same underlying idea that `Plinth` is built around: interrogate the problem, write the decision down, and only then let the agent build.

`mattpocock/skills` is explicitly positioned as the lighter-weight alternative on that spectrum. Its skills — split into "Engineering" and "Productivity", and further into user-invoked and model-invoked — are designed to be small, hackable, and composable rather than an enforced pipeline: `/grill-me` and `/grill-with-docs` interrogate a change before it starts, `/to-spec` and `/to-tickets` turn that conversation into a spec and tracer-bullet tickets, `/tdd` drives red-green-refactor, and `/code-review` checks the diff against both coding standards and the originating spec.

[`Superpowers`](https://github.com/obra/superpowers) sits at the opposite end of that spectrum: it calls itself "a complete software development methodology" and runs as a largely mandatory pipeline — `brainstorming` before code, `using-git-worktrees` for isolation, `writing-plans` for bite-sized tasks, `subagent-driven-development` or `executing-plans` to run them, `test-driven-development` enforcing RED-GREEN-REFACTOR, `requesting-code-review` between tasks, and `finishing-a-development-branch` to close out. It also ships across more coding agents than either `Plinth` or `mattpocock/skills` currently supports (Claude Code, Antigravity, Codex, Cursor, Factory Droid, Gemini CLI, GitHub Copilot CLI, Kimi Code, OpenCode, and Pi).

`Plinth` sits between those two poles, and not by accident. Its philosophy keeps the process ownership `mattpocock/skills` explicitly warns against, but scopes that ownership to a single niche instead of all software: the workflow is a fixed sequence — Functional Specification, Technical Specification, Implementation, Closure — and every phase has one explicit owner (`@plinth-business-analyst`, `@plinth-architect`, `@plinth-tech-lead`, and the framework-specific coder agents), with the handoff between phases gated by evidence — a BDD scenario, an ATDD alignment check, a readiness gate — rather than left to convention. That is closer in spirit to `Superpowers`'s mandatory pipeline than to `mattpocock/skills`'s hackable, opt-in toolbox.

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
      <td><code>/update-issue</code>, <code>/explore-problem</code>, <code>/create-acceptance-criteria</code></td>
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
        Framework-agnostic: <a href="https://www.skills.sh/jabrena/plinth/130-java-testing-strategies"><code>130</code></a>, <a href="https://www.skills.sh/jabrena/plinth/131-java-testing-unit-testing"><code>131</code></a>, <a href="https://www.skills.sh/jabrena/plinth/132-java-testing-integration-testing"><code>132</code></a>, <a href="https://www.skills.sh/jabrena/plinth/133-java-testing-acceptance-tests"><code>133</code></a><br>
        Spring Boot: <a href="https://www.skills.sh/jabrena/plinth/321-frameworks-spring-boot-testing-unit-tests"><code>321</code></a>, <a href="https://www.skills.sh/jabrena/plinth/322-frameworks-spring-boot-testing-integration-tests"><code>322</code></a>, <a href="https://www.skills.sh/jabrena/plinth/323-frameworks-spring-boot-testing-acceptance-tests"><code>323</code></a><br>
        Quarkus: <a href="https://www.skills.sh/jabrena/plinth/421-frameworks-quarkus-testing-unit-tests"><code>421</code></a>, <a href="https://www.skills.sh/jabrena/plinth/422-frameworks-quarkus-testing-integration-tests"><code>422</code></a>, <a href="https://www.skills.sh/jabrena/plinth/423-frameworks-quarkus-testing-acceptance-tests"><code>423</code></a><br>
        Micronaut: <a href="https://www.skills.sh/jabrena/plinth/521-frameworks-micronaut-testing-unit-tests"><code>521</code></a>, <a href="https://www.skills.sh/jabrena/plinth/522-frameworks-micronaut-testing-integration-tests"><code>522</code></a>, <a href="https://www.skills.sh/jabrena/plinth/523-frameworks-micronaut-testing-acceptance-tests"><code>523</code></a><br>
        Performance: <a href="https://www.skills.sh/jabrena/plinth/151-java-performance-jmeter"><code>151</code></a>, <a href="https://www.skills.sh/jabrena/plinth/152-java-performance-gatling"><code>152</code></a><br>
        Fuzzing: <a href="https://www.skills.sh/jabrena/plinth/703-technologies-fuzzing-testing"><code>703</code></a>
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
      <td><code>@033-architecture-diagrams</code>, <code>/create-adr</code>, <code>@030-architecture-adr-general</code></td>
      <td><code>/improve-codebase-architecture</code>, <code>codebase-design</code></td>
      <td>— (implicit in plans and reviews)</td>
    </tr>
  </tbody>
</table>

Across all three, the same underlying discipline shows up independently — interrogate the problem, write the decision down, delegate through disciplined steps, verify before calling it done. The fact that three projects with no shared history converged on it is a good sign that the discipline is inherent to working well with agents, not one project's house opinion. Where they diverge is domain depth: `mattpocock/skills` stays deliberately language-agnostic and lightweight, `Superpowers` stays deliberately language-agnostic and enforced, and `Plinth` trades that portability for `125 skills` of Java Enterprise-specific coverage — Maven, Spring Boot, Quarkus, Micronaut, JVM performance tooling, and EU/ISO regulatory review — that neither alternative attempts.

That trade is why `Plinth` is the better default for a Java Enterprise team specifically. The generic discipline all three projects agree on still has to end up in your stack either way; with `mattpocock/skills` or `Superpowers`, a Java team has to write the framework-specific, compliance-aware layer on top of it themselves. `Plinth` ships with that layer already built, and the benchmark results later in this article provide evidence that it holds up in practice — so a Java Enterprise team starts from that depth instead of building it from a language-agnostic baseline.

<a id="why-i-review-changes-when-working-with-agents-and-skill-scanners"></a>

## Why I review changes when working with agents and skill scanners

Skill scanners are an important part of the `Plinth` pipeline. They detect risky instructions, suspicious behavior, formatting problems, and other issues that should not reach a published skill. However, passing a scanner does not prove that a skill still behaves as intended. A scanner evaluates particular structural and security properties; it does not fully evaluate the quality of the guidance produced when an agent applies that skill.

This distinction matters when the pipeline fails. If I ask an agent to modify a skill until it passes the scanners, the agent may optimize for the reported findings alone. A change can satisfy the scanner by removing or weakening detailed instructions, examples, constraints, or workflow steps that were essential to the skill's behavior. The pipeline becomes green, but the skill becomes less precise or less useful and may even behave differently than before.

That is why I always review the resulting diff instead of treating a successful scan as the end of the work. I check whether the change addresses the actual finding, whether important domain guidance was preserved, and whether the generated skill still gives an agent the right decisions and boundaries. For every changed skill covered by the acceptance prompt inventory, I also run its specific prompt and verify the behavior rather than relying only on static validation.

Agents and skill scanners are complementary controls: the agent can propose a correction, the scanner can detect defined classes of risk, and behavioral acceptance testing can show whether the skill still works. Human review connects those signals. Without that review, a well-intentioned scanner fix can quietly degrade the very behavior the skill was designed to provide.

This connects with a controversial opinion from Uncle Bob Martin on not reviewing agent-generated code:

<blockquote class="twitter-tweet"><p lang="en" dir="ltr">What I do instead is to surround the agents with extreme constraints.</p>&mdash; Uncle Bob Martin (@unclebobmartin) <a href="https://x.com/unclebobmartin/status/2080257779395154409?s=20">July 23, 2026</a></blockquote>
<script async src="https://platform.twitter.com/widgets.js" charset="utf-8"></script>

In enterprise environments, code review is not optional.

<a id="testing-the-workflow-with-a-reproducible-benchmark"></a>

## Testing the workflow with a reproducible benchmark

An AI-native development workflow should not be evaluated only by how convincing its documentation sounds. It needs repeatable experiments that compare the results obtained with different amounts of engineering context.

This release adds a benchmark harness under [`benchmarks/`](https://github.com/jabrena/plinth/tree/main/benchmarks). The benchmark asks agent tools to solve the same Java problem through four scenarios:

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

For the detailed methodology and current findings, read [Validating Hypotheses About the Plinth Workflow with a Benchmark, Part 1](/plinth/blog/2026/07/validating-hypotheses-about-plinth-workflow-with-a-benchmark-part-1.html).

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
