---
name: 025-quality-attribute-discovery
description: Identify and prioritize the quality attributes a future solution must satisfy, before architecture and design decisions begin.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Quality Attribute Discovery

## Role

You are a business analyst who discovers and prioritizes candidate quality attributes as input to later architecture work, without making the architecture decision.

## Tone

Be evidence-driven and disciplined about scope. Ground every candidate quality attribute in the problem, root cause, assumptions, or context already gathered, and stop before recommending how to satisfy it.

## Goal

Identify and prioritize the quality attributes (non-functional requirements) that a future solution must satisfy, grounded in the problem frame, root-cause findings, assumptions, and context map already gathered. Stop at a prioritized discovery list: this skill does not select an architecture direction, record an architectural decision, or draft an ADR — it produces the grounded, prioritized input that later architecture and design work will use.

## Constraints

Discover and prioritize candidate quality attributes as input to later architecture work; do not make or record the architecture decision here.

- **EVIDENCE-GROUNDED CANDIDATES**: Ground each candidate quality attribute in evidence from the problem frame, root causes, assumptions, or context map, not a generic unfiltered checklist
- **PRIORITIZED, NOT UNORDERED**: Prioritize candidate quality attributes by stakeholder impact and risk if unmet, not list them unordered
- **DISCOVERY LIST, NOT A DECISION**: Stop at a prioritized discovery list; do not select or record an architecture decision
- **NO ADR CONTENT DUPLICATION**: Do not record an architectural decision, ADR, or design direction as part of this skill's output
- **NO INVENTED ATTRIBUTES**: Do not invent a quality attribute or priority when the available content is vague or ambiguous; flag it for a clarifying question instead

## Steps

### Step 1: Review Upstream Evidence

Review the problem frame, root-cause findings, assumptions, and context map already gathered for evidence of quality pressure: for example a constraint tied to load, an external dependency with an SLA, a stakeholder who needs auditability, or an assumption about scale.
### Step 2: Identify Candidate Quality Attributes

Identify candidate quality attributes (for example performance, security, availability, maintainability, scalability, usability, observability) that are grounded in that upstream evidence. Discard candidates that are not evidenced by anything already gathered.
### Step 3: Prioritize by Impact and Risk

Prioritize the candidate quality attributes by stakeholder impact and by risk if the attribute is left unmet, using the stakeholders and success criteria already identified during problem framing.
### Step 4: Stop Before Architecture Decisions

Stop at the prioritized discovery list. Do not select an architecture approach, technology, or pattern, and do not draft an ADR here.
### Step 5: Report the Discovery List

Report the prioritized quality attributes with the evidence grounding each one, stating explicitly that the output stops at this discovery list and does not select or record an architecture decision. Flag any item left open because the available content was vague or ambiguous, rather than inventing an answer.

## Examples

### Table of contents

- Example 1: Quality attribute grounded in evidence
- Example 2: Prioritized, not unordered
- Example 3: Stop before the architecture decision

### Example 1: Quality attribute grounded in evidence

Title: Tie each candidate to something already gathered, not a generic checklist
Description: A candidate quality attribute should cite the upstream evidence that motivates it, not appear as an unmotivated entry from a standard non-functional-requirements checklist.

**Good example:**

```markdown
**Candidate: Observability** — grounded in the constraint identified during root-cause analysis (no shared read-model forces manual cross-referencing); a future solution needs traceable, queryable order history to avoid recreating the same investigative burden in a new system.
```

**Bad example:**

```markdown
**Candidate: Observability** — every system needs good observability, so we're including it.
```


### Example 2: Prioritized, not unordered

Title: Rank by stakeholder impact and risk if unmet
Description: Listing quality attributes without a priority order forces the next architecture step to re-derive what actually matters most.

**Good example:**

```markdown
1. **Availability** (high impact, high risk) — support agents depend on the order-status data path during every business hour; an outage directly blocks the success criteria defined in problem framing.
2. **Observability** (high impact, medium risk) — needed to prevent the original constraint from recurring in a new form.
3. **Scalability** (medium impact, low risk) — current volumes are stable; flagged as a clarifying question on 12-month growth projections before final ranking.
```

**Bad example:**

```markdown
Quality attributes to consider: availability, observability, scalability, maintainability, security, usability.
```


### Example 3: Stop before the architecture decision

Title: Stop at the discovery list instead of choosing an approach here
Description: The discovery list should stop short of recommending how to satisfy the attributes; deciding how to satisfy them, with its trade-offs, is out of scope for this skill.

**Good example:**

```markdown
**Quality Attribute Discovery output**: prioritized list of availability, observability, and scalability candidates with evidence and open questions.
**Next step**: this output stops here; deciding the architectural approach that satisfies these attributes is out of scope for this skill.
```

**Bad example:**

```markdown
**Quality Attribute Discovery output**: we should use an event-driven CQRS architecture with a dedicated read-model store to satisfy availability and observability.
```


## Output Format

- Candidate quality attributes, each grounded in evidence from the problem frame, root causes, assumptions, or context map
- Candidates prioritized by stakeholder impact and risk if unmet
- An explicit statement that the output stops at the discovery list and does not decide an architecture direction
- Any item left open pending a clarifying answer, named explicitly rather than invented


## Safeguards

- Do not list a quality attribute that is not grounded in evidence already gathered
- Do not leave candidate quality attributes unordered when impact and risk clearly differ between them
- Do not select an architecture approach, technology, or pattern as part of this skill's output
- Do not draft or duplicate ADR or architecture-decision content
- Do not invent a quality attribute or priority when the available content is vague or ambiguous