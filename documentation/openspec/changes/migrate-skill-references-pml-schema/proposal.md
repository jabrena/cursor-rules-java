## Why

GitHub issue [#1022](https://github.com/jabrena/plinth/issues/1022) requests that skill-reference XML sources track the newly published PML 0.9.0 schema. Today, of the 196 in-scope XML files under `plinth-skills-generator/src/main/resources/skill-references/`, every one declares `xsi:noNamespaceSchemaLocation` for the PML 0.8.0 `pml.xsd`, and 194 of them still use the bare, deprecated `<author>` element directly under `<metadata>` instead of the `<authors>` wrapper (2 files — `045-planning-azure-devops.xml`, `057-design-feature-toggles.xml` — already use `<authors>`). `RemoteSchemaValidationTest.REMOTE_XSD` (`plinth-skills-generator/src/test/java/info/jab/pml/RemoteSchemaValidationTest.java:21`) and the contributor instructions in `AGENTS.md`/`CLAUDE.md` (`CLAUDE.md` is a symlink to `AGENTS.md`) both still point at the 0.8.0 URL. Diffing the published 0.8.0 and 0.9.0 `pml.xsd` files directly confirms 0.9.0 removes exactly two things: the deprecated `<xs:element ref="author" minOccurs="0"/>` reference inside `<metadata>`, and the `<xs:element ref="triggers" minOccurs="0"/>` reference (plus the `triggers`/`trigger-list`/`trigger` definitions) inside `<prompt>`. Per [ADR-008](../../adr/ADR-008-one-xml-schema-per-generated-artifact.md), skill-reference XML intentionally continues to reference the remote, shared `pml.xsd` (it is the schema's original abstraction, per [ADR-004](../../adr/ADR-004-skill-generation.md)), while `skill-indexes/*.xml` validates against the locally forked `skills.xsd`, which is a deliberate, independently-evolving snapshot fork — not required to track upstream `pml.xsd` version bumps. `skill-indexes/*.xml` actively uses `<triggers>` in all 125 files, so this change must not touch `skills.xsd` or `skill-indexes/*.xml`.

## What Changes

- Update `xsi:noNamespaceSchemaLocation` from the PML 0.8.0 `pml.xsd` URL to the PML 0.9.0 `pml.xsd` URL in all 196 in-scope XML files under `plinth-skills-generator/src/main/resources/skill-references/` (the 3 files under `skill-references/assets/questions/` declare no schema location and are unaffected).
- Rewrite the 194 files that still declare a bare `<author>Name</author>` element under `<metadata>` to use `<authors><author>Name</author></authors>`, matching the shape already used by `045-planning-azure-devops.xml` and `057-design-feature-toggles.xml`.
- Update the 2 already-`<authors>`-shaped files' schema location only; their existing `<authors>`/`<author>` structure is unchanged.
- Update `RemoteSchemaValidationTest.REMOTE_XSD` (`plinth-skills-generator/src/test/java/info/jab/pml/RemoteSchemaValidationTest.java:21`) from the 0.8.0 URL to the 0.9.0 URL.
- Update the PML schema URL referenced in `AGENTS.md` (and its `CLAUDE.md` symlink) from 0.8.0 to 0.9.0.
- Explicitly leave `plinth-skills-generator/src/main/resources/skills.xsd` and every `skill-indexes/*.xml` file unchanged.
- Add a new JUnit test (e.g. `SkillReferenceAuthorsUniquenessTest`) that rejects any skill-reference source whose `<authors>` element contains two `<author>` children with an identical value — a content-integrity rule the PML schema itself cannot express (no uniqueness constraint on sibling `author` text), and which this repository does not own upstream to add there.

## Capabilities

### New Capabilities

- `pml-skill-references-schema`: Defines that skill-reference XML sources, `RemoteSchemaValidationTest`, and contributor documentation track the same, current published remote PML schema version, that the deprecated bare `<author>` element is replaced by `<authors>`/`<author>`, and that this scope is explicitly bounded away from `skills.xsd`/`skill-indexes/*.xml`.

### Modified Capabilities

None. `pml-skills-schema` (the local `skills.xsd` baseline for `skill-indexes/`) is unaffected — it already states in its "Schema scope is limited to skill-indexes" scenario that `skill-references/*.xml` is unmigrated and out of its scope; this change does not alter that boundary, it only acts within the boundary already reserved for skill-references.

## Impact

- **Migrated sources:** all 196 in-scope files under `plinth-skills-generator/src/main/resources/skill-references/` (schema-location bump; 194 also gain the `<authors>` wrapper).
- **Test:** `RemoteSchemaValidationTest.REMOTE_XSD` constant, bumped to 0.9.0; new `SkillReferenceAuthorsUniquenessTest` added, parameterized over `SkillReferences.xmlFilenames()`, asserting no skill-reference source declares two identical `<author>` values in the same `<authors>` element.
- **Docs:** `AGENTS.md` (and its `CLAUDE.md` symlink), schema URL bumped to 0.9.0.
- **Generator output:** `skill-index-to-markdown.xsl` / `skill-reference-to-markdown.xsl` already render the `<authors>` shape (confirmed by the 2 files already using it and by the prior `authors` rendering work in commit `89c0b001`), so no XSLT change is required; generated Markdown for the 194 rewritten files must remain textually equivalent (same author name(s), same rendering), verified before promoting this change.
- **Not in scope:** `plinth-skills-generator/src/main/resources/skills.xsd`, all `skill-indexes/*.xml` files, `skill-references/assets/questions/*.xml` (no schema location today), `commands.xsd`/`agents.xsd` and their generators, and upstream `pml.xsd` itself (already published at 0.9.0; not authored or edited by this repository).

## Source Artifacts and Derivation

| Source | Authority | Derivation |
|---|---|---|
| [Issue #1022](https://github.com/jabrena/plinth/issues/1022) (title, description) | Problem statement, scope, acceptance criteria | Issue to OpenSpec |
| [Issue #1022 Functional Specification comment](https://github.com/jabrena/plinth/issues/1022#issuecomment-5083323522) | Root cause analysis, verified assumptions (fetched and diffed 0.8.0 vs 0.9.0 `pml.xsd` directly, confirmed HTTP reachability, confirmed no upstream skills-specific schema exists), scope boundary against `skills.xsd`/`skill-indexes/*.xml` per ADR-008 | Comment to OpenSpec |
| [Issue #1022 Acceptance Criteria comment](https://github.com/jabrena/plinth/issues/1022#issuecomment-5090741509) | Six Gherkin scenarios: fresh migration, already-migrated-file preservation, test version currency, doc version currency, completeness sweep, skill-index/skills.xsd boundary | Comment to OpenSpec |
| `plinth-skills-generator/src/main/resources/skill-references/045-planning-azure-devops.xml` and `057-design-feature-toggles.xml` (representative) | Current `<authors>` target shape | Grounds the rewrite pattern for the 194 bare-`<author>` files |
| `plinth-skills-generator/src/test/java/info/jab/pml/RemoteSchemaValidationTest.java` | Confirms the exact constant and line to update, and that it enumerates `SkillReferences.xmlFilenames()` only | Grounds the test-currency requirement |
| `AGENTS.md` / `CLAUDE.md` | Confirms the exact contributor-facing schema URL to update, and that `CLAUDE.md` is a symlink (one file) | Grounds the doc-currency requirement |
| [ADR-008](../../adr/ADR-008-one-xml-schema-per-generated-artifact.md) | Establishes `skill-references/*.xml` stays on the remote PML schema while `skills.xsd`/`skill-indexes/*.xml` is a decoupled, independently-evolving fork | Grounds the explicit scope boundary excluding `skills.xsd`/`skill-indexes/*.xml` |
| `documentation/openspec/specs/pml-skills-schema/spec.md` (existing capability) | Confirms `skill-references/*.xml` is already documented as out of `pml-skills-schema`'s scope | Confirms no conflicting requirement exists; this change fills that reserved-but-undefined scope |
| Live fetch of `https://jabrena.github.io/pml/schemas/0.8.0/pml.xsd` and `.../0.9.0/pml.xsd` | Confirms the exact diff between versions (deprecated `author` ref and `triggers` removed, nothing else) | Grounds the "what changed upstream" statement in the Why section |

**Derivation direction:** Issue #1022 (description + Functional Specification + Acceptance Criteria comments) → this OpenSpec change (`migrate-skill-references-pml-schema`) → migrated `skill-references/*.xml` sources + `RemoteSchemaValidationTest.REMOTE_XSD` + `AGENTS.md`/`CLAUDE.md`. This is one-way derivation; this change does not modify the source issue or its comments, and does not touch `skills.xsd` or `skill-indexes/*.xml`.

## Design Decisions

| Question | Decision | Status |
|---|---|---|
| Is this one reviewable change, or should it split? | One change. Schema-location bump, metadata rewrite, test-constant update, and doc update all share one rollback boundary (revert the schema-location string and the `authors` wrapper) and one compatibility guarantee (generated Markdown stays textually equivalent). No independent business value, release timing, ownership, or risk boundary separates them. | **Resolved** |
| Does this change touch `skills.xsd` or `skill-indexes/*.xml`? | No. Per ADR-008, `skills.xsd` is a deliberate, independently-evolving snapshot fork of PML 0.8.0, not required to track `pml.xsd`'s future releases; all 125 `skill-indexes/*.xml` files actively depend on `<triggers>`, which 0.9.0 removes from `pml.xsd`. Migrating them is explicitly out of scope and would require its own, separately-approved change (and likely an ADR-008 amendment). | **Resolved** |
| Target metadata shape for the 194 bare-`<author>` files | `<authors><author>Name</author></authors>`, matching the 2 already-migrated files; one `<author>` child per file since none of the 194 files currently declare more than one author | **Resolved** |
| Does generated Markdown output change? | No, confirmed by a design verification spike on a representative file (`014-agile-user-story.xml`, temporarily rewritten and reverted with `git checkout --`): rebuilt Markdown was a zero-byte diff against the pre-change output, consistent with the `authors` XSLT template's single-author loop behavior. Full-set confirmation across all 194 rewritten files remains an implementation task (`tasks.md` 5.3). | **Resolved** |
| Are all 196 rewritten files valid against the real PML 0.9.0 schema? | Yes, confirmed for the representative file with `xmllint --noout --schema` against the actual published 0.9.0 `pml.xsd`. Full-set confirmation across all 196 files remains an implementation task (`tasks.md` 5.1). | **Resolved** |
| Is `xmllint`/schema validity sufficient evidence that the 2 already-`<authors>`-shaped files were left untouched? | No — schema validity proves the result is well-formed, not that it is unchanged. Added `tasks.md` 3.3 (per-file diff confirming only the schema-location line changed) and a matching `spec.md` scenario. Raised during the `059-design-atdd` alignment gate (finding F-002). | **Resolved** |
| Can the PML schema reject two `<author>` children with an identical value in the same `<authors>` element? | No — confirmed empirically: `examples/xml/invalid-duplicate-authors-example.xml` (this change) validates cleanly against the real PML 0.9.0 `pml.xsd`, since `pml.xsd` places no uniqueness constraint on sibling `author` text, and this repository does not own upstream `pml.xsd` to add one. A new repository-owned JUnit test (`SkillReferenceAuthorsUniquenessTest`) enforces this instead (`tasks.md` section 6). | **Resolved** |

See [`design.md`](design.md) for the migration mechanics, validation strategy, and risk analysis.

## Handoff

After OpenSpec docs and `openspec validate --all` pass, `@robot-tech-lead` can coordinate implementation from `tasks.md`: the mechanical schema-location and metadata rewrite across 196 files, the `RemoteSchemaValidationTest` and `AGENTS.md`/`CLAUDE.md` updates, and the compatibility verification (generated Markdown equivalence, `xmllint` validation, and `RemoteSchemaValidationTest` passing).
