## Context

Issue #1127 defines a new skill request: apply the ISO/IEC 25010:2023 quality model to Java Enterprise systems in the same structured, repeatable review style as `813-regulations-iso-42001`, instead of the ad hoc conversational discovery already embedded in `032-architecture-adr-non-functional-requirements`. The issue body supplies the User Story, precedent shape, and file layout; comment 1 supplies a full functional specification (problem framing, root-cause analysis, assumption analysis, context mapping, quality-attribute discovery); comment 2 supplies Gherkin acceptance criteria covering build output, chapters-summary coverage, XML well-formedness, `skills.xml` wiring, local-vs-release output boundaries, and the `skill-check`/`skill-scanner` CI gates.

Existing regulation skills establish the expected shape: PML XML source, bundled official-source summary, Java-focused engineering examples, questionnaire and report assets for formal review, Gherkin acceptance scenarios, generated local skill inspection, and explicit boundaries between structured skill guidance and legal/compliance/certification conclusions. `032-architecture-adr-non-functional-requirements` already uses the ISO/IEC 25010:2023 quality model, but only conversationally, to synthesize ADRs through interactive discovery (`interactive="true"` in `plinth-skills-generator/src/main/resources/skill-indexes/032-skill.xml`) — it does not produce a fixed, evidence-based review artifact.

## Decisions

### Skill Identifier

Use `814-regulations-iso-25010` for ISO/IEC 25010:2023 quality-attribute review guidance.

The `814` prefix follows the existing regulation sequence after `813-regulations-iso-42001` (verified as the current highest `8xx-regulations-*` id in `plinth-skills-generator/src/main/resources/skills.xml:822-833`). The identifier names the standard directly, consistent with the `8xx-regulations-iso-*` sub-pattern already established by `813-regulations-iso-42001`.

### Skill Shape

The skill uses the established XML source pattern, mirroring `813-regulations-iso-42001`:

- `plinth-skills-generator/src/main/resources/skill-indexes/814-skill.xml` defines metadata, goal, scope, constraints, triggers, and workflow.
- `plinth-skills-generator/src/main/resources/skill-references/814-regulations-iso-25010-chapters-summary.xml` summarizes the ISO/IEC 25010:2023 quality model using stable public descriptions of the standard.
- `plinth-skills-generator/src/main/resources/skill-references/814-regulations-iso-25010-engineering-examples.xml` provides Java Enterprise-focused engineering examples and output guidance for each quality characteristic.
- `plinth-skills-generator/src/main/resources/skill-references/assets/questions/814-iso-25010-engineering-review-questionnaire.md` captures structured evidence questions per quality characteristic.
- `plinth-skills-generator/src/main/resources/skill-references/assets/reports/814-iso-25010-engineering-review-report-template.md` captures scope, evidence, per-characteristic findings, controls, owner handoffs, residual risks, release decision, and action plan.
- `plinth-skills-generator/src/main/resources/skills.xml` registers the skill id `814`, its two references, and its questionnaire/report resources — mirroring the `813` registration block exactly in shape.

This four-asset shape resolves the issue's own "Negotiable" tension (User Story requests the full four-asset shape; the INVEST bullet frames questionnaire/report-template inclusion as open) in favor of the full shape, because comment 2's Gherkin acceptance criteria explicitly require a chapters-summary reference and an engineering-examples reference in the generated skill, and explicitly gate the release-profile scenario on "the chapters-summary and engineering-examples assets" — establishing those two references as required, with the questionnaire/report-template pair included for shape parity with every other `8xx-regulations-*` skill and to support the "produce reviewable engineering evidence" outcome named in the User Story.

**Alternatives considered and rejected:**

- *Three-asset shape (drop the questionnaire or the report-template asset).* Rejected: `skills.xml` shows every registered `8xx-regulations-*` skill from `808-regulations-eu-digital-markets-act` through `813-regulations-iso-42001` using the identical reference-list-plus-resource-list (questionnaire + report template) shape — there is no three-asset precedent to follow instead. Dropping either asset would also fail spec.md's "Generate local ISO/IEC 25010:2023 skill" scenario, which requires both `assets/questions/814-iso-25010-engineering-review-questionnaire.md` and `assets/reports/814-iso-25010-engineering-review-report-template.md` in generated output.
- *Two-asset shape (merge chapters-summary and engineering-examples into one reference file).* Rejected: spec.md's scenarios name two separate reference ids (`814-regulations-iso-25010-chapters-summary` and `814-regulations-iso-25010-engineering-examples`), matching the `813` precedent's separation of official-source summary from Java-worked-examples content; merging them would also fail the "Skill inventory is wired correctly" scenario, which expects both reference ids registered independently in `skills.xml`.

No alternative skill shape survives both the `813` precedent on disk and the accepted spec.md scenarios, so this decision is effectively determined by prior art plus already-accepted acceptance criteria rather than a live trade-off.

### ISO/IEC 25010:2023 Scope

The skill covers all nine ISO/IEC 25010:2023 product-quality characteristics defined by the published standard, applied to Java Enterprise systems:

- Functional Suitability
- Performance Efficiency
- Compatibility
- Interaction Capability
- Reliability
- Security
- Maintainability
- Flexibility
- Safety

**Correction to issue #1127:** the issue's User Story and comment 2's Gherkin acceptance criteria name only eight characteristics and omit Interaction Capability. Cross-checked against the published ISO/IEC 25010:2023 product quality model (`https://www.iso.org/standard/78176.html`, corroborated by the IEC catalogue entry, `iso25000.com`, and independent secondary summaries), the standard defines nine characteristics — Interaction Capability replaced and expanded "Usability" from the 2011 edition. This repository's own `032-architecture-adr-non-functional-requirements` questionnaire (`plinth-skills-generator/src/main/resources/skill-references/assets/questions/adr-nfr-questions-template.md`) already lists all nine correctly, including Interaction Capability, so the omission is an issue-level oversight rather than a deliberate scope decision or a conflict with existing repository content. Corrected here, and in `proposal.md` and `specs/iso-25010-quality-model-java-skill-reference/spec.md`, at maintainer direction.

The chapters-summary reference must cover all nine characteristics (per the corrected "Chapters summary covers all named ISO/IEC 25010:2023 quality characteristics" scenario). The engineering-examples reference translates each characteristic into concrete Java Enterprise review guidance (code-level, architecture-level, and operational signals a reviewer can check), consistent with the depth and per-characteristic worked-example pattern used by `813-regulations-iso-42001`. Exact per-characteristic example depth is an implementation-time authoring decision within this established shape, not a further open design question.

The skill must not decide whether a system is certified, compliant, or contractually conformant to any external quality standard or SLA. Those determinations require qualified architecture, product, and accountable business-owner review; the skill produces engineering review evidence and action items, not certification or compliance conclusions.

**Alternative considered and rejected:** a prioritized subset of characteristics (for example, Security, Reliability, and Maintainability only, deferring Compatibility, Flexibility, and Safety). Rejected because coverage of all nine is not negotiable: full coverage is a mandatory acceptance criterion in spec.md's "Chapters summary covers all named ISO/IEC 25010:2023 quality characteristics" scenario, and the published standard itself defines exactly nine characteristics with no lower-priority subset endorsed by any source consulted. Only the per-characteristic worked-example *depth* is a genuinely open, implementation-time authoring choice (per the issue's own "Negotiable" INVEST bullet), not which characteristics are covered.

### Chapters-Summary Content Draft (authoring basis for execution)

Reviewed and approved by the maintainer during design refinement as the authoring basis for `plinth-skills-generator/src/main/resources/skill-references/814-regulations-iso-25010-chapters-summary.xml` (task 1.5). Not yet written to the generator source — XML authoring happens during implementation (`/implement-spec`), mirroring the `813-regulations-iso-42001-chapters-summary.xml` shape: `metadata`, `role`, and a `goal` `CDATA` block citing the official source, the bundled-summary/no-live-fetch boundary, cross-references to the engineering-examples/questionnaire/report assets, and per-characteristic sections ending in a cross-skill-routing list.

**Source:** [ISO/IEC 25010:2023](https://www.iso.org/standard/78176.html) — *Systems and software engineering — Systems and software Quality Requirements and Evaluation (SQuaRE) — Product quality model*. Cross-checked against the IEC catalogue entry, `iso25000.com`, and the sub-characteristic breakdown already used correctly by `032-architecture-adr-non-functional-requirements`'s questionnaire (`adr-nfr-questions-template.md`).

For each of the nine characteristics below, the chapters-summary must define the characteristic and its ISO-named sub-characteristics, then state its Java Enterprise engineering review impact:

1. **Functional Suitability** (completeness, correctness, appropriateness) — trace acceptance criteria to controller/service methods and tests; review domain-model edge cases; check API contracts against actual behavior.
2. **Performance Efficiency** (time behaviour, resource utilization, capacity) — review JVM/GC tuning, connection- and thread-pool sizing; check for N+1 query patterns and missing indexes; verify load/soak test evidence and explicit capacity limits.
3. **Compatibility** (co-existence, interoperability) — review API versioning/deprecation strategy; check message/data format contracts across service boundaries; verify safe co-existence with shared infrastructure.
4. **Interaction Capability** (appropriateness recognizability, learnability, operability, user error protection, user engagement, inclusivity, user assistance, self-descriptiveness) — review error-message self-descriptiveness and API documentation learnability; check input validation gives clear 4xx responses instead of stack traces or ambiguous 500s.
5. **Reliability** (faultlessness, availability, fault tolerance, recoverability) — review circuit breakers, retries with backoff, and timeouts on outbound calls; check health/readiness-probe correctness and graceful shutdown; verify idempotent retry semantics and documented recovery procedures.
6. **Security** (confidentiality, integrity, non-repudiation, accountability, authenticity, resistance) — review authn/authz implementation, secrets handling, and dependency vulnerability scanning; check audit logging for accountability; verify input sanitization and abuse resistance.
7. **Maintainability** (modularity, reusability, analysability, modifiability, testability) — review module/package boundaries and coupling; check test-pyramid shape and testability; verify static-analysis results and complexity metrics.
8. **Flexibility** (adaptability, scalability, installability, replaceability) — review horizontal-scaling readiness and statelessness; check infrastructure-as-code and replaceable third-party integrations; verify configuration externalization across environments.
9. **Safety** (operational constraint, risk identification, fail safe, hazard warning, safe integration) — for systems with real-world effects, review operational guardrails and approval gates; check fail-safe defaults; verify hazard warnings surface before irreversible or high-impact actions.

Close the reference with a cross-skill-routing list mirroring `813`'s pattern: `814-regulations-iso-25010` for structured, repeatable product-quality review and evidence; `032-architecture-adr-non-functional-requirements` for interactive ADR discovery; `813-regulations-iso-42001` for AI management system practices; `801-regulations-eu-ai-act`, `803-regulations-gdpr`, `805-regulations-eu-cyber-resilience-act` for their respective regulatory concerns.

The `814-regulations-iso-25010-engineering-examples.xml` reference (task 1.6) expands each bullet above into worked Java Enterprise examples, consistent with the depth of `813-regulations-iso-42001-engineering-examples.xml`.

### Relationship to `032-architecture-adr-non-functional-requirements` and Other Regulation Skills

- Use `032-architecture-adr-non-functional-requirements` when the goal is interactive, conversational discovery that produces a new Architectural Decision Record for non-functional requirements — a design-time, dialogue-driven activity that synthesizes a single ADR.
- Use `814-regulations-iso-25010` when the goal is a structured, repeatable quality-attribute review of an existing or in-progress Java Enterprise system against all nine ISO/IEC 25010:2023 characteristics, producing a fixed engineering review report as reviewable evidence — a review-time, questionnaire-driven activity independent of any live ADR conversation.
- The two skills are complementary and may be used together: `814` review findings can surface quality-attribute decisions worth capturing as an ADR, which `032` then facilitates conversationally. Neither skill supersedes or duplicates the other; `032` is unchanged by this addition.
- Use `813-regulations-iso-42001` when the primary concern is AI management system practices for GenAI development with Java.
- Use `801-regulations-eu-ai-act` when the primary concern is EU AI Act classification or governance.
- Use `803-regulations-gdpr` when the primary concern is personal-data processing.
- Use `805-regulations-eu-cyber-resilience-act` when the primary concern is product cybersecurity, vulnerability handling, or secure development conformity evidence.
- Use multiple regulation and quality-model skills together when the same Java system crosses quality, AI management, privacy, cybersecurity, product, or operational boundaries.

**Alternative considered and rejected: extend `032` with a structured/non-interactive review mode instead of adding a standalone `814` skill.** `032-architecture-adr-non-functional-requirements` declares `interactive="true"` on its skill index (`plinth-skills-generator/src/main/resources/skill-indexes/032-skill.xml:4`), and its four steps (get current date; load the reference and open with a challenge-first question; conduct consultative discovery in small batches, never all questions at once; generate the ADR only after explicit user confirmation) are built for exactly one conversational, dialogue-driven output — a single synthesized ADR. Adding a second, questionnaire-driven, non-interactive execution path to the same skill index would require either branching the workflow on an execution mode the PML skill-index schema and generator have no mechanism to express, or overloading the `interactive="true"` attribute that the generator and every other `03x-architecture-*` skill currently treat as unconditional. It would also make one skill entry produce two structurally different outputs (a synthesized ADR vs. a fixed multi-characteristic engineering review report), breaking the one-skill-one-output-shape convention every entry in `skills.xml` currently follows. Issue #1127 itself frames the work as extracting and adapting 032's quality-characteristic knowledge into a *standalone* skill, and the proposal's Change Boundary Assessment already commits to leaving `032` unchanged — so this alternative would also require reopening an issue-level and proposal-level decision that was not left open. A standalone `814-regulations-iso-25010` skill is the option consistent with both the generator's one-skill-one-shape convention and the issue's explicit instruction.

This is not a one-off judgment call: it is the same shape decision every future `8xx-regulations-*` addition sourced from an existing conversational `03x-architecture-adr-*` skill will face. See Open Questions below for whether that general principle is worth recording as its own ADR.

## Two-Step Delivery Plan

1. Make the change easy: add the OpenSpec artifacts, XML source files, reference files, questionnaire/report assets, Gherkin feature, and acceptance prompt entry without touching generated release output. (This OpenSpec change covers only the planning artifacts; XML authoring is future implementation work.)
2. Make the behavior change: run local generation and verification so `.agents/skills/814-regulations-iso-25010` is emitted and validated, then optionally run the release profile to refresh public `skills/`.

## Validation Strategy

- Validate changed XML files with `xmllint --noout`.
- Run `./mvnw clean install -pl plinth-skills-generator -am` to regenerate local skills into `.agents/skills` without refreshing public `skills/`.
- Inspect generated local `.agents/skills/814-regulations-iso-25010/SKILL.md`.
- Inspect generated local ISO/IEC 25010:2023 chapters-summary, engineering examples, questionnaire, and report template outputs.
- Confirm `skills.xml` registers skill id `814` with `skillId="814-regulations-iso-25010"` and its chapters-summary and engineering-examples reference ids.
- Confirm the local install does not modify the public `skills/` directory.
- Run `./mvnw clean install -pl plinth-skills-generator -am -P release` to confirm the release profile refreshes `skills/814-regulations-iso-25010` with the chapters-summary and engineering-examples assets.
- Run `npx skill-check@latest skills --no-security-scan --format github` against the released `skills/` output.
- Run `skill-scanner scan-all ./skills --recursive --use-behavioral --policy strict --fail-on-severity high` against the released `skills/` output.
- Execute the listed `814-regulations-iso-25010` acceptance prompt and verify it passes.
- Run `./mvnw clean verify -pl plinth-skills-generator -am`.
- Run `openspec validate --all`.

## Open Questions

No open question blocks implementation of this OpenSpec change. Issue #1127 is otherwise unusually complete: the User Story, functional-specification comment, and Gherkin acceptance-criteria comment together resolve skill identifier, four-asset shape, and validation gates without further clarification, and the alternatives considered above (three/two-asset shape, prioritized characteristic subset, extending `032`) are all rejected by prior art already on disk or by acceptance criteria already accepted in `specs/iso-25010-quality-model-java-skill-reference/spec.md`. The one exception is quality-characteristic scope, where the issue's eight-characteristic list omitted Interaction Capability against the published nine-characteristic standard; corrected above at maintainer direction, and now consistent across `proposal.md`, `spec.md`, and this document. Exact certification, compliance, or conformity conclusions for any reviewed Java system remain outside the skill and must be escalated to qualified architecture, product, and business owners.

**ADR candidate (non-blocking):** this change is the second time a structured `8xx-regulations-*` review skill has been designed as deliberately separate from an existing conversational `03x-architecture-adr-*` skill that already touches the same standard or quality model informally (here, `032`'s conversational use of ISO/IEC 25010:2023). The repository already records its own skill-catalog architecture in ADRs (`documentation/adr/ADR-004-skill-generation.md`, `documentation/adr/ADR-006-separate-local-skill-generation-from-release-publishing.md`, `documentation/adr/ADR-008-one-xml-schema-per-generated-artifact.md`), but no existing ADR states the general principle of when a quality or regulation concern gets a standalone `8xx-regulations-*` structured-review skill versus being folded into an interactive `03x-architecture-adr-*` discovery skill. Recording that principle once as a general ADR (via `030-architecture-adr-general`) would avoid re-litigating this boundary ad hoc for the next `8xx-regulations-*` addition sourced from an existing conversational skill. This does not block this change and is not created as part of this design-refinement pass; it is a recommendation for the maintainer to approve as separate follow-up work.
