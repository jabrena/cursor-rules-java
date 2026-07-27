title=Why Functional and Technical Specifications Matter for AI-Assisted Development
date=2026-07-27
type=post
tags=blog,skills,agents,design,software-engineering,openspec
author=MyRobot
status=published
~~~~~~

## The gap AI agents fall into

Ask an AI agent to "add rate limiting to the API" and it will produce something. It might even compile, pass a quick test, and look complete in the diff.

But what does "rate limiting" mean here? Per user, per API key, per IP? What happens when the limit is hit — a 429, a queue, a silent drop? Is this a business rule with a specific number attached to a contract, or an infrastructure concern with an operational default? Should it be configurable per tenant?

None of that is in the four-word request. An agent that jumps straight to code has to guess, and guesses compound: one guess about behavior becomes a design decision, becomes an implementation, becomes tests that lock in the guess as if it were a requirement.

This is why this project treats specification as its own phase, not a formality before "the real work" of writing code. The pipeline is:

<table>
<thead>
<tr><th>Issue tracker</th><th>Technical Specification</th><th>Implementation</th></tr>
</thead>
<tbody>
<tr>
<td>
GitHub Issues<br>
Jira<br>
Azure DevOps
</td>
<td>OpenSpec</td>
<td>Agents + Skills</td>
</tr>
</tbody>
</table>

The first stage is the issue tracker that captures the problem before OpenSpec turns it into a specification. This project uses GitHub Issues, but the same pipeline works with any tracker that can hold a problem statement and a URL an agent can fetch — Jira and Azure DevOps work items play the same role, provided there's an adapter step to fetch the issue before `/explore-problem` can evaluate it.

Two different kinds of specification sit inside that middle step, and they answer two different questions.

## Functional specification: what problem, for whom, and why

A functional specification answers *what* the system should do and *why*, from the perspective of the people affected by it. It is deliberately silent on implementation.

In this project, `/explore-problem` produces exactly this artifact. It is owned by `@robot-business-analyst` and evaluates an issue through five fixed lenses, in order:

<table>
<thead>
<tr><th>Lens</th><th>Answers</th></tr>
</thead>
<tbody>
<tr>
<td>Problem framing</td>
<td>Problem statement, current state, desired state, stakeholders, success criteria</td>
</tr>
<tr>
<td>Root cause analysis</td>
<td>Five Whys, Fishbone, Current Reality Tree, constraints</td>
</tr>
<tr>
<td>Assumption analysis</td>
<td>Assumptions, unknowns, validation plan</td>
</tr>
<tr>
<td>Context mapping</td>
<td>Existing systems, integrations, ownership, external dependencies</td>
</tr>
<tr>
<td>Quality attribute discovery</td>
<td>Which -ilities the solution must satisfy, and their priority</td>
</tr>
</tbody>
</table>

The command processes these lenses one at a time, not batched, and it asks a clarifying question whenever a lens's content is vague — then waits for the answer before writing that section. It will not invent a stakeholder, a root cause, or a quality attribute to fill a gap. If the issue doesn't say who is affected by the rate limiter or what happens under load today, the command stops and asks rather than fabricating a plausible-sounding answer.

That constraint is the point. A functional specification is only useful if it reflects what is actually known and actually agreed, not what an agent inferred to keep moving.

Before a functional specification becomes a technical one, there's a step that turns *what* and *why* into something checkable: `/create-acceptance-criteria`. It's owned by `@robot-business-analyst` — the same agent that writes the functional specification — and it runs strictly after `/explore-problem`.

Its input is narrow by design: only the confirmed Functional Specification comment already posted on the issue, never the raw issue description or unrelated discussion. It applies the `058-design-bdd` skill to that specification alone, confirming actors, outcomes, and business rules already established there, then derives main, alternative, boundary, and error examples into a self-contained Gherkin `Feature`. If a scenario would depend on a fact the functional specification never settled, the command asks one focused clarification question instead of inventing a decision — the same discipline `/explore-problem` applies to its five lenses.

The result is posted as its own new comment, headed `# Acceptance Criteria`, never as an edit to the issue description or the functional specification itself. That separation matters: anyone reviewing the issue can see the *what/why* and the *observable criteria derived from it* as two distinct, independently auditable artifacts, and `/create-spec` can then turn confirmed criteria into full Given/When/Then requirements without re-deriving behavior facts from scratch.

## Technical specification: how, precisely enough to build and verify

Once the problem is framed, a technical specification answers *how* — in enough detail that implementation and verification aren't left to interpretation.

In this project, that's OpenSpec. `/create-spec`, owned by `@robot-architect`, turns an approved issue, design, or ADR into a structured change: a proposal, a design, requirements written as Given/When/Then scenarios, and a task list. The scenario format matters more than it looks:

```text
GIVEN a user is in a Plinth repository with a reachable issue at <issue-url>
WHEN the user invokes /explore-problem <issue-url>
THEN the command drafts a Functional Specification
AND the draft contains a Problem Framing section with ...
```

Every requirement is falsifiable. "Rate limiting should work correctly" is not a technical specification; "given 101 requests in 60 seconds from one API key, the 101st request receives a 429 with a Retry-After header" is. The second version can be checked by a human reviewer, tested by an agent, and diffed against a future change to see exactly what moved.

Technical specification is also where compatibility and migration strategy get decided *before* code exists — for example, this project's own `@055-design-parallel-change` and `@056-design-avoid-breaking-changes` skills, which force an explicit expand/migrate/contract plan instead of a big-bang rename. That decision belongs in the spec, not discovered mid-implementation.

`/create-spec` produces the first draft of that structured change, but a first draft isn't always ready for implementation — some changes need their design pressure-tested before an agent starts writing code. That's what `/explore-design` is for: also owned by `@robot-architect`, run after `/create-spec` rather than in place of it. Given an issue or an OpenSpec change with an unresolved technical approach, it compares feasible alternatives and their trade-offs, recommends a direction with rationale, and works out components, boundaries, data flow, failure handling, and the testing strategy needed before implementation starts — the same discipline that produces the `055`/`056` compatibility decision above. When the OpenSpec change already has a proposal, confirmed acceptance criteria, and a task checklist, `/explore-design` finishes with `059-design-atdd`, a read-only alignment gate that checks whether those three artifacts still agree with each other and reports `ready` or `changes-requested`. Either way, the command will not report a design as approved until a human confirms it — a recommendation is not a decision.

## Why the order matters

Functional specification before technical specification isn't bureaucracy — it's what keeps the two questions from getting tangled.

If you let an agent decide *how* before anyone has agreed on *what* and *why*, the "how" quietly becomes the "what." A 429-with-backoff response gets chosen because it was easy to implement, not because a stakeholder needs graceful degradation instead of a hard cutoff. Nobody decided that; it fell out of the first draft and nobody questioned it because there was no functional specification to check it against.

Reversing the failure mode is just as real: technical detail leaking into a functional specification locks in an implementation choice before anyone has evaluated alternatives, and forecloses options a proper design phase should have compared.

Keeping the two artifacts distinct — and requiring explicit human confirmation before either is posted — means each question gets answered by someone equipped to answer it, and each can be reviewed on its own terms: a product owner can approve a functional specification without reading Given/When/Then scenarios, and an engineer can review a technical specification without re-litigating whether the problem is worth solving.

## What this costs, and what it buys back

Two review passes before any code is written is real overhead on a change that might have taken ten minutes to hack together. For a one-line config tweak, that overhead is not worth paying, and this project doesn't force it — OpenSpec is reserved for changes complex enough to need coordination.

But for anything that crosses a compatibility boundary, touches multiple stakeholders, or will still matter in six months, the cost is cheap next to the alternative: an agent-generated change that is syntactically correct and semantically wrong, merged because nobody wrote down what "correct" meant before the diff existed.

## Share your experience

If you're using `/explore-problem`, `/create-acceptance-criteria`, `/create-spec`, or an equivalent functional/technical specification split in your own AI-assisted workflow, share what's working and where the process feels too heavy.

Use [GitHub Discussions](https://github.com/jabrena/plinth/discussions) to post where the two-phase approach caught a real problem, and where you had to relax it.
