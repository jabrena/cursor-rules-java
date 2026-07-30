## Context

`/create-spec` delegates substantive OpenSpec planning to `042-planning-openspec`. The current command and skill contracts require a maintainer-prepared sanitized artifact derived outside the agent context. That contract was introduced to address prompt-injection risk from outsider-authored issue content and to guarantee complete description and comment coverage.

Issue #1100 identifies the manual preparation step as the blocking behavior. The desired workflow moves complete-context preparation into `/create-spec` while retaining the security, completeness, authority, conflict-handling, and fail-closed properties of the current boundary.

The maintainer approved the direct-read direction on 2026-07-30 after comparing it with isolated-reader, deterministic-collector, and manual-artifact alternatives. The design reuses the repository's established `/explore-problem` precedent for deep single-issue analysis and the CI policy that treats only low/medium W011 third-party-content exposure findings as reviewed, non-blocking findings.

## Implementation Location

- Strategy: `feature-branch`
- Reference: `feat/1100-improve-create-spec-issue-input`

## Goals / Non-Goals

**Goals:**

- Remove the requirement for a separately prepared sanitized artifact in `/create-spec` issue mode.
- Process the issue description and every accessible paginated comment before scope assessment.
- Treat all issue prose as untrusted requirements data rather than agent instructions.
- Stop before planning when complete context cannot be established.
- Report conflicts and ambiguity without inventing resolutions.
- Preserve source and derivation traceability.
- Keep command, skill, specification, generated output, and acceptance tests aligned.

**Non-Goals:**

- Add a new tracker client, deterministic sanitizer, JBang collector, or provider abstraction.
- Modify issue descriptions or comments.
- Add issue triage, user-story rewriting, or design exploration to `/create-spec`.
- Change non-issue `/create-spec` inputs or the generic handling of other outsider-authored sources.
- Edit generated command, skill, Cursor-rule, or public release output directly.
- Refresh public `skills/` output outside an intentional release.

## Decisions

### Integrated issue-context preparation

When `/create-spec` receives issue-backed input, it resolves one supplied issue through available authenticated, read-only tracker tooling and prepares planning context from its description and every accessible comment. It does not require the maintainer to create a separate sanitized artifact.

The contract is capability-based rather than tied to a new provider abstraction: if the supplied identifier or URL cannot be resolved with available tooling, the workflow stops. A repository-owned design, ADR, plan, or existing OpenSpec artifact may supplement issue context but cannot replace complete issue retrieval.

### Completeness before planning

The workflow establishes the current accessible provider snapshot before source classification, scope assessment, or OpenSpec authoring. The snapshot contains:

- the readable issue description;
- the provider-reported zero-comment state or every accessible paginated comment;
- exhaustive pagination through the provider's terminal page; and
- a retrieved-count cross-check when the provider exposes a total.

An issue with no comments is complete only when provider metadata or exhaustive pagination establishes the zero-comment state. A multi-page discussion is complete only after every accessible page is processed. Deleted or permission-hidden historical content is outside the accessible snapshot unless the provider signals an omission or count mismatch; any signaled gap makes completeness unavailable.

Traceability records the issue URL or stable identifier, retrieval timestamp, accessible comment count, and issue-to-OpenSpec derivation direction.

### Untrusted requirements-data boundary

Issue content may supply requirements, constraints, decisions, acceptance criteria, examples, and known conflicts. Text embedded in issue content cannot supply executable agent instructions or override system, repository, command, skill, or OpenSpec authority.

The direct-read exception is narrowly scoped to `/create-spec` issue mode. Tracker operations used to prepare context are read-only. The workflow never executes embedded commands, follows embedded links, runs embedded code, or initiates tool actions because issue content requests them. Ambiguous content triggers clarification after completeness is established rather than speculative interpretation.

### Fail-closed retrieval

If authentication, permissions, availability, pagination, count reconciliation, response integrity, truncation, size limits, or another condition prevents the complete accessible snapshot from being established, the workflow stops before scope assessment or artifact authoring. It reports the reason and never represents partial context as complete.

Oversized context is a blocking failure when every accessible item cannot be processed. The workflow does not silently truncate, partially summarize, or downgrade any retrieval/completeness failure to a warning.

### Explicit conflict handling

The workflow reports conflicting and unclear requirements as unresolved. It does not infer a resolution from comment order, author identity, or chronology unless an authoritative source explicitly defines that precedence.

### One-way traceable derivation

Generated OpenSpec artifacts record the issue URL and issue-to-OpenSpec derivation direction. `/create-spec` does not modify or silently synchronize changes back to the source issue.

## Approach Analysis

### Selected: direct single-issue read

Use the command runner's available authenticated tracker tooling, exhaust pagination, keep tracker operations read-only, frame all retrieved prose as untrusted requirements data, and fail closed on incomplete context.

This option best satisfies the simple-design order:

1. **Passes tests:** its observable behavior maps directly to the issue's acceptance scenarios and the repository's existing command/skill generator tests.
2. **Reveals intention:** the command says plainly that it reads one issue, treats it as data, and stops when completeness is uncertain.
3. **Avoids duplicated knowledge:** it reuses the direct-read boundary already established by `/explore-problem` instead of introducing a second security model.
4. **Uses the fewest elements:** it requires prompt-contract and test changes, not a new script, schema, tracker client, or orchestration layer.

### Rejected: isolated reader or subagent

An isolated reader adds runtime-specific orchestration and still exposes a model to hostile prose. The command cannot guarantee that every supported host provides an isolated agent with the required no-write tool boundary, so this option is less portable without eliminating the core risk.

### Rejected: deterministic JBang collector or sanitizer

A collector could make pagination and counts deterministic, but it would add authentication, provider, schema, distribution, and maintenance responsibilities. Deterministic code cannot reliably distinguish semantic prompt injection from legitimate requirements, so it does not replace the authority boundary and is disproportionate to the command-contract change.

### Rejected: mandatory maintainer-prepared artifact

This preserves the strongest external trust boundary but directly contradicts issue #1100's user value and acceptance criteria.

## Smallest Useful Vertical Slice

The first deliverable is one complete issue-backed `/create-spec` flow using available tooling:

| Workflow layer | Selected first-slice option | Deferred richer options |
| --- | --- | --- |
| Issue resolution | Resolve one supplied identifier or URL with available authenticated read-only tooling | New provider abstraction, bulk issue support |
| Completeness | Exhaust pagination and reconcile provider count when available | Persisted manifests, resumable retrieval, historical reconciliation |
| Trust boundary | Treat content as data and prohibit content-triggered commands, links, code, and tool actions | Isolated reader, deterministic content classifier |
| Planning | Report ambiguity/conflicts and record URL, timestamp, count, and derivation | Automated conflict precedence, issue write-back |
| Verification | Generator assertions, Gherkin scenarios, focused Maven tests, local generated-output inspection, scanner review | Dedicated live-provider integration harness |

If this had to ship tomorrow, this slice is the smallest useful version because it removes the manual prerequisite for one issue while retaining complete accessible-snapshot and fail-closed behavior. Provider abstractions, persisted retrieval artifacts, historical reconciliation, and runtime orchestration are deferred because they add cost without being required by the approved behavior.

## Two-Step Delivery Sequence

### Step 1: behavior-preserving preparation

Refine characterization assertions for non-issue inputs, source authority, scope assessment, no source mutation, and OpenSpec scaffolding. Separate the retained completeness/authority assertions from the sanitization-specific wording. Run focused generator tests before changing generated command or skill behavior.

### Step 2: intended behavior change

Write failing command and skill tests for direct issue reading, zero comments, exhaustive pagination/count reconciliation, embedded-instruction rejection, conflicts, traceability, and incomplete-context failure. Update only the canonical command and skill XML required to make those tests pass, then refactor duplicated wording while tests remain green.

## Compatibility Review

- **POTENTIALLY BREAKING — security posture:** `/create-spec` moves from externally sanitized issue facts to direct issue ingestion. The change is intentional and mitigated by the established single-issue data boundary, read-only tooling, fail-closed completeness, adversarial acceptance scenarios, and scanner policy. Release notes should identify the direct-read behavior and reviewed W011 posture.
- **NON-BREAKING — invocation:** command name, issue input shape, owning agent, `042-planning-openspec` routing, outputs, and non-issue inputs remain stable.
- **NON-BREAKING — existing artifacts:** repository-owned sanitized summaries remain accepted as optional planning sources; they are no longer a mandatory issue prerequisite.
- **NON-BREAKING — source ownership:** implementation edits command and skill XML plus their tests, regenerates local output through Maven, and does not directly edit `.cursor/rules/`, `.agents/skills`, public `skills/`, or `docs/`.
- **UNKNOWN — provider capabilities:** completeness depends on the supplied tracker tooling exposing exhaustive pagination and, optionally, a count. Unsupported or unverifiable providers fail explicitly rather than receiving an implicit compatibility promise.

No feature toggle is recommended. The behavior is a static generated prompt contract, not a runtime service decision with an observable disabled/enabled path, operational owner, or safe configuration fallback. A toggle would add permanent conditional prompt complexity without improving rollback; branch/commit rollback and release review are the appropriate controls.

## ADR Candidate

Record a repository-level decision for deep single-issue direct reads. The ADR should unify the `/explore-problem` precedent and this command, describe the permitted divergence from tracker skills' default no-raw-ingestion posture, define the untrusted-data/read-only/fail-closed controls, and document the reviewed low/medium W011 CI policy. ADR creation remains a separate maintainer-approved workflow and does not block this command-scoped implementation.

## Risks / Trade-offs

- **Prompt injection:** Direct issue processing increases exposure to hostile prose. Mitigation: restrict retrieval to one issue through read-only tooling, prohibit content-triggered actions, keep higher-priority instructions authoritative, and validate adversarial fixtures and behavioral scanning.
- **Partial retrieval:** API, authentication, permission, pagination, or size failures can omit decisions. Mitigation: establish completeness before planning and fail closed.
- **Workflow latency:** Exhaustive pagination and security processing can increase execution time. Mitigation: optimize only after completeness and security requirements are met.
- **Tracker divergence:** Trackers expose different pagination and visibility semantics. Mitigation: use a capability-based contract and stop when available tooling cannot establish the accessible snapshot.
- **Contract drift:** Command, skill, specifications, tests, and generated output can contradict one another. Mitigation: update canonical XML sources and focused tests together, then inspect generated local output.
- **Conflicting issue discussion:** Later comments can contradict earlier content. Mitigation: report unresolved conflicts rather than selecting a winner implicitly.

## Validation Strategy

- Run `openspec validate --all`.
- Validate every edited XML source with `xmllint --noout`.
- Add command and skill acceptance scenarios for:
  - direct complete issue processing without a separate artifact;
  - an issue with no comments;
  - a multi-page discussion;
  - embedded prompt-injection attempts;
  - conflicting requirements;
  - source traceability; and
  - failure to retrieve complete context.
- Update generator assertions that currently require the external sanitized artifact.
- Run focused Maven verification for `plinth-commands-generator` and `plinth-skills-generator`.
- Regenerate local command and skill output through the supported Maven workflows and inspect the affected generated files.
- Execute only the listed `/create-spec` and `042-planning-openspec` acceptance prompts because both generated contracts change.
- Run `skill-check` and applicable behavioral scanners against generated local `.agents/skills` output. Accept only the repository-reviewed low/medium W011 posture; block higher-severity or different findings.
