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

### ISO/IEC 25010:2023 Scope

The skill covers all eight ISO/IEC 25010:2023 product-quality characteristics named in issue #1127, applied to Java Enterprise systems:

- Functional Suitability
- Performance Efficiency
- Compatibility
- Reliability
- Security
- Maintainability
- Flexibility
- Safety

The chapters-summary reference must cover all eight characteristics (per comment 2's second scenario). The engineering-examples reference translates each characteristic into concrete Java Enterprise review guidance (code-level, architecture-level, and operational signals a reviewer can check), consistent with the depth and per-characteristic worked-example pattern used by `813-regulations-iso-42001`. Exact per-characteristic example depth is an implementation-time authoring decision within this established shape, not a further open design question.

The skill must not decide whether a system is certified, compliant, or contractually conformant to any external quality standard or SLA. Those determinations require qualified architecture, product, and accountable business-owner review; the skill produces engineering review evidence and action items, not certification or compliance conclusions.

### Relationship to `032-architecture-adr-non-functional-requirements` and Other Regulation Skills

- Use `032-architecture-adr-non-functional-requirements` when the goal is interactive, conversational discovery that produces a new Architectural Decision Record for non-functional requirements — a design-time, dialogue-driven activity that synthesizes a single ADR.
- Use `814-regulations-iso-25010` when the goal is a structured, repeatable quality-attribute review of an existing or in-progress Java Enterprise system against all eight ISO/IEC 25010:2023 characteristics, producing a fixed engineering review report as reviewable evidence — a review-time, questionnaire-driven activity independent of any live ADR conversation.
- The two skills are complementary and may be used together: `814` review findings can surface quality-attribute decisions worth capturing as an ADR, which `032` then facilitates conversationally. Neither skill supersedes or duplicates the other; `032` is unchanged by this addition.
- Use `813-regulations-iso-42001` when the primary concern is AI management system practices for GenAI development with Java.
- Use `801-regulations-eu-ai-act` when the primary concern is EU AI Act classification or governance.
- Use `803-regulations-gdpr` when the primary concern is personal-data processing.
- Use `805-regulations-eu-cyber-resilience-act` when the primary concern is product cybersecurity, vulnerability handling, or secure development conformity evidence.
- Use multiple regulation and quality-model skills together when the same Java system crosses quality, AI management, privacy, cybersecurity, product, or operational boundaries.

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

None for this OpenSpec change. Issue #1127 is unusually complete: the User Story, functional-specification comment, and Gherkin acceptance-criteria comment together resolve skill identifier, four-asset shape, quality-characteristic scope, and validation gates without further clarification. Exact certification, compliance, or conformity conclusions for any reviewed Java system remain outside the skill and must be escalated to qualified architecture, product, and business owners.
