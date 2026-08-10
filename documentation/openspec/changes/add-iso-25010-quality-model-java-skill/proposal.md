## Why

GitHub issue [#1127](https://github.com/jabrena/plinth/issues/1127) asks for a dedicated `814-regulations-iso-25010` skill that applies the ISO/IEC 25010:2023 quality model to Java Enterprise systems, mirroring the reference/questionnaire/report-template shape of the existing `813-regulations-iso-42001` skill.

Java architects, tech leads, and reviewers using the Plinth toolkit currently have no structured, repeatable way to run an ISO/IEC 25010:2023 quality-attribute review. The quality model is only available conversationally, embedded inside `032-architecture-adr-non-functional-requirements`, which facilitates ADR-authoring dialogue rather than producing reviewable, evidence-producing engineering output. The new skill must give reviewers a standalone, structured review path covering all eight ISO/IEC 25010:2023 quality characteristics (Functional Suitability, Performance Efficiency, Compatibility, Reliability, Security, Maintainability, Flexibility, Safety) without duplicating or replacing the conversational ADR-discovery workflow that `032` already owns.

## What Changes

- Add an ISO/IEC 25010:2023 skill for structured, repeatable Java Enterprise quality-attribute review.
- Model the skill after the established `8xx-regulations-*` four-asset pattern most recently used by `813-regulations-iso-42001`: XML skill index, bundled chapters-summary reference, bundled engineering-examples reference, engineering review questionnaire asset, engineering review report template asset.
- Cover all eight ISO/IEC 25010:2023 quality characteristics named in issue #1127: Functional Suitability, Performance Efficiency, Compatibility, Reliability, Security, Maintainability, Flexibility, Safety.
- Register the skill in the generator inventory so local skill generation emits `.agents/skills/814-regulations-iso-25010`.
- Keep generated public `skills/` output out of scope unless a release profile is intentionally run later.
- Leave `032-architecture-adr-non-functional-requirements` unchanged; the new skill is additive and complementary, not a replacement for its conversational ADR-discovery workflow.

## Capabilities

### New Capabilities

- `iso-25010-quality-model-java-skill-reference`: Adds ISO/IEC 25010:2023 quality-attribute review guidance for Java Enterprise systems.

### Modified Capabilities

None.

## Source and Derivation

- Source artifact: GitHub issue [#1127](https://github.com/jabrena/plinth/issues/1127), retrieved 2026-08-10 via `gh issue view 1127 --repo jabrena/plinth --json number,title,body,comments,url,state,labels`, with completeness cross-checked via `gh issue view 1127 --repo jabrena/plinth --json comments -q '.comments | length'` (2 comments, both accessible and retrieved in full).
- Issue components used:
  - Issue body: User Story, Notes (source request, `813` precedent shape, XML-authoring boundary), INVEST validation.
  - Comment 1 (functional specification, jabrena/OWNER): Problem Framing, Root Cause Analysis, Assumption Analysis, Context Mapping, Quality Attribute Discovery.
  - Comment 2 (acceptance criteria, jabrena/OWNER): Gherkin `Feature: ISO/IEC 25010:2023 quality-attribute review skill (814-regulations-iso-25010)` with 8 scenarios.
- Existing implementation model: `813-regulations-iso-42001` (verified in `plinth-skills-generator/src/main/resources/skills.xml:822-833` as the current highest `8xx-regulations-*` id, confirming `814` is the correct next id) and the archived OpenSpec change `documentation/openspec/changes/archive/2026-06-27-add-iso-42001-genai-java-skill/` that delivered it.
- Existing conversational precedent: `032-architecture-adr-non-functional-requirements` (`plinth-skills-generator/src/main/resources/skill-indexes/032-skill.xml`), which already uses the ISO/IEC 25010:2023 quality model conversationally to produce ADRs; this change extracts and adapts that quality-characteristic knowledge into a standalone, structured `8xx-regulations-*` skill without modifying `032` itself.
- Derivation direction: issue #1127 (body + functional-specification comment + acceptance-criteria comment) plus the `813-regulations-iso-42001` precedent plus the `032` conversational quality-model reference -> OpenSpec change artifacts -> future XML skill source implementation -> local generated skill validation.
- Unresolved questions or conflicts: none. Issue #1127 is unusually complete — it already contains a full functional specification and Gherkin acceptance criteria as comments, and repository verification (no existing OpenSpec change or skill-source match for "25010" or "1127") confirms this is net-new, non-conflicting work.

## Change Boundary Assessment

This is one OpenSpec change for one new ISO/IEC 25010:2023 skill. The implementation has a single review, validation, and rollback boundary: adding the generator source, references, questionnaire/report assets, `skills.xml` wiring, acceptance tests, prompt inventory entry, and generated local skill verification for that skill. It does not require changes to `032-architecture-adr-non-functional-requirements`, any other `8xx-regulations-*` skill, public release output, website output, or legacy Cursor rules.

## Impact

XML skill indexes, XML skill references, questionnaire/report assets, `skills.xml`, Gherkin acceptance tests, acceptance prompt inventory, local generated skill output, and OpenSpec artifacts are affected by the future implementation. Generated `.cursor/rules/`, public `skills/`, and `docs/` must not be edited directly. Public `skills/` should only change through the documented release profile when release output is intentionally in scope. This OpenSpec change itself authors only planning artifacts; no `plinth-skills-generator` XML sources, `skills.xml`, or Gherkin `.feature` files are created or modified as part of this change.
