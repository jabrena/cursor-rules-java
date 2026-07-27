## Context

`plinth-skills-generator/src/main/resources/skill-references/` contains 199 XML files. 196 of them declare `xsi:noNamespaceSchemaLocation` for the remote PML `pml.xsd` (the remaining 3, under `skill-references/assets/questions/`, declare no schema location and are unrelated question-asset fragments, not `<prompt>` documents). Of those 196, 194 still use the pre-0.9.0 shape — a bare `<author>Name</author>` element directly under `<metadata>` — and 2 already use the `<authors><author>Name</author>...</authors>` wrapper. `RemoteSchemaValidationTest` parameterizes over `SkillReferences.xmlFilenames()` and validates every one of those 196 files against a single hardcoded `REMOTE_XSD` constant.

Live inspection of both published schema versions confirms the exact upstream diff:

```
16d15
<                 <xs:element ref="triggers" minOccurs="0" />
40d38
<                 <xs:element ref="author" minOccurs="0"/> <!-- DEPRECATED-->
152,170d149
<     <!-- Triggers section -->
<     <xs:element name="triggers"> ... </xs:element>
<     <xs:element name="trigger-list"> ... </xs:element>
<     <xs:element name="trigger" type="xs:string" />
```

0.9.0 removes the deprecated `author` reference from `<metadata>` and removes `<triggers>` (and its children) entirely from `<prompt>`. Nothing else changed. Because `<metadata>` no longer accepts a bare `<author>` under 0.9.0, every one of the 194 unmigrated files would fail schema validation the moment the schema-location string is bumped, unless the `<authors>` rewrite happens in the same pass per file.

Per ADR-008, `skill-references/*.xml` deliberately stays on the remote, shared `pml.xsd` (it is that schema's original abstraction), while `skill-indexes/*.xml` validates against a locally-forked, independently-evolving `skills.xsd` that still needs `<triggers>` (all 125 files use it). This design does not revisit that boundary; it only migrates the side of it this issue targets.

## Goals / Non-Goals

**Goals**
- Every in-scope `skill-references/*.xml` file references PML 0.9.0 and uses `<authors>`/`<author>`.
- `RemoteSchemaValidationTest` and `AGENTS.md`/`CLAUDE.md` reference the same 0.9.0 URL the migrated sources use.
- Generated skill-reference Markdown output is unchanged in substance (same author name(s), same rendering) after the rewrite.
- No skill-reference source's `<authors>` element contains two `<author>` children with an identical value, enforced by a new repository-owned test since the schema itself cannot express this constraint.

**Non-Goals**
- Migrating `skill-indexes/*.xml` or `skills.xsd` (explicitly out of scope; see Context).
- Authoring or editing upstream `pml.xsd` (already published at 0.9.0 by the `jabrena/pml` repository).
- Changing `skill-index-to-markdown.xsl` / `skill-reference-to-markdown.xsl` rendering logic (the `<authors>` shape is already rendered correctly, evidenced by the 2 files already using it).
- Adding multiple `<author>` entries to any file; none of the 194 unmigrated files currently declare more than one author, so the rewrite is a 1:1 wrap, not a data-modeling decision.

## Change Boundary Assessment

One change. Every edit (schema-location bump, `authors` rewrite, test constant, doc string) shares the same rollback boundary — reverting the schema-location string per file and the `RemoteSchemaValidationTest`/doc constants restores 0.8.0 behavior exactly — and the same compatibility guarantee (Markdown output equivalence). Splitting by file type (XML vs. test vs. docs) would create artificial, non-independently-deployable slices of one mechanical migration.

## Decisions

### Migration unit: per-file, atomic across the whole set

Each of the 196 files gets both edits (schema location + `authors` wrapper where needed) in the same pass, not as two separate passes across the file set. Bumping the schema location alone, before the `authors` rewrite, would make the file invalid against 0.9.0 (bare `<author>` is no longer permitted under `<metadata>`); doing the rewrite alone, before the schema bump, leaves the file pointed at a schema version that still deprecates-but-permits `<author>`, which is harmless but leaves the file's own declared schema location wrong for its content's origin. Since both edits target the same file and same `<metadata>` element, doing them together avoids an intermediate invalid or inconsistent state across commits.

### Target authors shape: single-author wrap, no multi-author invention

`<authors><author>Name</author></authors>` for each of the 194 files, using the exact name currently in the bare `<author>` element. No file's author list is invented, split, or reordered.

### Scope boundary enforcement: exclude by directory, verify by search

`skill-indexes/*.xml` and `skills.xsd` are excluded by only ever touching paths under `skill-references/` (not `skill-indexes/`) and never touching `skills.xsd`. After migration, a repo-wide search for the 0.8.0 URL must show zero hits under `skill-references/` and continue to show hits under `skill-indexes/*.xml`/`skills.xsd` (proving the boundary was respected, not accidentally widened or narrowed).

### Test and doc currency: single-source values, no abstraction added

`RemoteSchemaValidationTest.REMOTE_XSD` and the `AGENTS.md` schema URL are each updated as plain string edits. No shared constant or configuration file is introduced to keep the two in sync automatically — the existing precedent (commit `67f4e0f5`, a doc-only follow-up to a prior schema bump) already treats these as two independent, low-frequency manual edits, and introducing a shared source of truth is a larger change than this issue's scope justifies.

### Preserved-file verification: diff, not just validity

Task 2.2/3.2's "leave the existing `<authors>`/`<author>` structure untouched" for the 2 already-`<authors>`-shaped files was, before this refinement, only checked by `xmllint` (schema validity). Validity does not prove the content is unchanged: the same migration tooling that wraps a bare `<author>` in `<authors>` for the other 194 files could, if not explicitly excluded for these 2, reorder `<author>` children or alter whitespace while still producing schema-valid output. This was raised as finding F-002 during the `059-design-atdd` alignment gate. `tasks.md` 3.3 now requires a per-file diff confirming the *only* changed line is the schema-location value, matched by a dedicated `spec.md` scenario.

### Duplicate-author detection: repository-owned test, not schema

`pml.xsd`'s `authors` complex type (`<xs:element ref="author" minOccurs="1" maxOccurs="unbounded"/>`) places no uniqueness constraint on sibling `author` text — two `<author>` children with the identical value are schema-valid. This was confirmed empirically: `examples/xml/invalid-duplicate-authors-example.xml` (this change) validates cleanly against the real, published PML 0.9.0 `pml.xsd`. Because this repository does not own upstream `pml.xsd` (per ADR-008, its ownership is headed toward the external `pml` repository), adding an `xs:unique` constraint there is not this change's decision to make. Instead, a new JUnit test (`SkillReferenceAuthorsUniquenessTest`), parameterized over `SkillReferences.xmlFilenames()` the same way `RemoteSchemaValidationTest` is, parses each file's `<authors>/<author>` values and asserts no duplicates — a repository-owned content-integrity check layered on top of, not replacing, schema validation. This also guards against a mechanical migration script accidentally duplicating an author entry across the 196-file rewrite, not only against the representative fixture case.

### Design verification spike (representative file, non-destructive)

To resolve the two Open Questions carried from the Functional Specification/Acceptance Criteria before implementation, `014-agile-user-story.xml` (a bare-`<author>` file) was temporarily rewritten to the target shape (schema location bumped to 0.9.0, `<author>` wrapped in `<authors>`), validated, rebuilt, and then reverted with `git checkout --` (the file is git-tracked, so the spike left no residual change):

- `xmllint --noout --schema <fetched pml-0.9.0.xsd> 014-agile-user-story.xml` → **validates**, confirming the rewritten shape is accepted by the real published 0.9.0 schema, not just the local diff analysis.
- `./mvnw clean install -pl plinth-skills-generator -am` regenerated `.agents/skills/014-agile-user-story/references/014-agile-user-story.md`; diffed against a pre-change copy of the same file → **zero-byte diff**. This is also explained analytically: `skill-reference-to-markdown.xsl`'s `authors` named template (lines 73–87) already for-each's `metadata/authors/author`, and for exactly one `<author>` child that loop emits the same string as the `metadata/author` fallback branch, with no separator to omit or add.
- `./mvnw -pl plinth-skills-generator test -Dtest=RemoteSchemaValidationTest` still passed with the file in its rewritten state, confirming `pml.xsd` 0.8.0 (the test's current, not-yet-bumped `REMOTE_XSD`) also accepts the `<authors>` shape — so the per-file migration order (schema bump + authors rewrite together, per file, across the full 196-file set) does not need to be sequenced against the `REMOTE_XSD` bump in `RemoteSchemaValidationTest`.

This is one representative file, not all 196 — `tasks.md` (5.1–5.3) still requires running `xmllint`, the full Maven verification, and the Markdown diff across the complete migrated set during implementation. The spike raises confidence that the recommended design (atomic per-file rewrite, no XSLT change, no shared schema-version constant) will hold at full scale, since every unmigrated file shares the same `<metadata><author>...</author>...</metadata>` shape and the same XSLT template governs all of them; it does not substitute for that full-set verification.

## Alternative Analysis

**Two-pass migration (schema bump commit, then authors-rewrite commit).** Rejected: the intermediate state after a schema-only bump is invalid XML per file (bare `<author>` fails 0.9.0), so `RemoteSchemaValidationTest` would fail for the entire duration between the two passes, including in CI on the intermediate commit if bisected or reviewed mid-way.

**Introduce a shared constant/config file for the schema version (e.g. a Maven property or a single `.properties` resource consumed by both `RemoteSchemaValidationTest` and a doc-generation step).** Rejected for this change: valuable in principle (addresses the Functional Specification's "Maintainability" quality attribute about duplicated version strings), but it is a tooling change independent of this migration's outcome, would touch the build and doc-generation path, and was not requested by the issue's acceptance criteria. Recorded as a candidate follow-up, not implemented here.

## Component Boundaries

- `plinth-skills-generator/src/main/resources/skill-references/*.xml` — data files; edited in place, no new files.
- `plinth-skills-generator/src/test/java/info/jab/pml/RemoteSchemaValidationTest.java` — one constant string edit; no test logic change.
- `plinth-skills-generator/src/test/java/info/jab/pml/SkillReferenceAuthorsUniquenessTest.java` — new test class; reuses `SkillReferences.xmlFilenames()`, adds no new production (`src/main`) code.
- `AGENTS.md` (`CLAUDE.md` symlink) — one URL string edit.
- `plinth-skills-generator/src/main/resources/skills.xsd`, `plinth-skills-generator/src/main/resources/skill-indexes/*.xml` — untouched; asserted as a boundary, not merely assumed.

## Data Flow

Unchanged. `skill-references/*.xml` still flows through the same XInclude → XSLT → Markdown pipeline (`skill-reference-to-markdown.xsl`); only the source XML's schema-location attribute and `<metadata>` author shape change, not the pipeline itself.

## Failure Handling

If `xmllint` or `RemoteSchemaValidationTest` reports a validation failure for a migrated file, the fix is local to that file's `<metadata>` block (most likely a malformed `<authors>` wrap or a missed schema-location string) — no pipeline or schema-file change is implicated, since `pml.xsd` 0.9.0 is fixed, external, and already verified.

## Risks / Trade-offs

- **Risk:** a mechanical rewrite across 196 files could silently drop or corrupt an author name during the `<author>` → `<authors><author>` wrap. **Mitigation:** verify the rewritten author name(s) still match the original bare `<author>` content per file, and confirm generated Markdown author output is unchanged.
- **Risk:** widening the edit accidentally into `skill-indexes/*.xml` (e.g. a repo-wide find/replace on the 0.8.0 URL string that isn't scoped to `skill-references/`). **Mitigation:** scope every edit command to the `skill-references/` path explicitly, and verify post-migration that `skill-indexes/*.xml` and `skills.xsd` still reference 0.8.0/local `skills.xsd` unchanged.
- **Trade-off:** not introducing a shared schema-version constant (see Alternative Analysis) leaves `RemoteSchemaValidationTest` and `AGENTS.md` as two independently-maintained strings; accepted because it matches existing project precedent and keeps this change's scope aligned with the issue's acceptance criteria.

## Validation Strategy

- `xmllint --noout <file>` (or `--schema https://jabrena.github.io/pml/schemas/0.9.0/pml.xsd <file>`) for each migrated file.
- `./mvnw clean verify -pl plinth-skills-generator -am`, exercising `RemoteSchemaValidationTest` across all 196 files against the bumped `REMOTE_XSD`.
- Generate skill-reference Markdown before and after the rewrite (`./mvnw clean install -pl plinth-skills-generator -am`) and diff the author-related frontmatter/content lines for at least the 194 rewritten files, confirming no unintended output change.
- Repo-wide search confirming zero remaining `schemas/0.8.0` references under `skill-references/`, and confirming `skill-indexes/*.xml`/`skills.xsd` are unchanged (still 0.8.0-derived).
- Per-file diff of the 2 already-`<authors>`-shaped files confirming only the schema-location line changed.
- New `SkillReferenceAuthorsUniquenessTest` run against all 196 migrated sources (expected: pass) and against `examples/xml/invalid-duplicate-authors-example.xml` (expected: fail, with a diagnostic naming the file and duplicated value).
- `openspec validate --all` from `documentation/`.

## ADR Candidates

None. This change applies an existing, already-recorded policy (ADR-008's remote-vs-local schema boundary); it does not introduce a new architectural decision.

## Compatibility Review (`056`)

- **Generated output:** skill-reference Markdown must remain equivalent (same author name(s) rendered, same frontmatter/content shape). Confirmed on a representative file by the design verification spike above (zero-byte diff); full-set confirmation across all 194 rewritten files remains a `tasks.md` implementation step, not assumed complete from the spike alone.
- **Consumers:** `skill-check@latest` and `cisco-ai-skill-scanner` operate on generated `skills/` output, not on `skill-references/` XML directly, and skill-reference generation is a separate pipeline from the skill-index-to-`SKILL.md` pipeline those tools scan; this change has no expected effect on them, but is not itself gated on running them (they scan `skills/`, produced from `skill-indexes/`, which this change does not touch).
- **Backwards compatibility:** no consumer depends on the deprecated bare `<author>` shape continuing to validate; the schema itself marks it `<!-- DEPRECATED-->` in 0.8.0 and removes it in 0.9.0.

## Resolved Design Questions

| Question | Resolution |
|---|---|
| One change or split by file type? | One change (see Change Boundary Assessment) |
| Touch `skills.xsd`/`skill-indexes/*.xml`? | No (see Context, Decisions) |
| Target `<authors>` shape? | Single-`<author>` wrap per file, no invented multi-author data |
| Two-pass or atomic-per-file migration? | Atomic per file (see Decisions, Alternative Analysis) |
| Introduce a shared schema-version constant? | No, out of scope for this change (see Alternative Analysis) |
| Does generated skill-reference Markdown output change in substance after the rewrite? | No, for the representative file tested (zero-byte diff, explained by the XSLT `authors` template logic) — see Design verification spike. Full-set confirmation is a `tasks.md` implementation step (5.3), not an open design question. |
| Does the rewritten shape validate against the real, published PML 0.9.0 schema? | Yes, for the representative file tested (`xmllint --noout --schema`) — see Design verification spike. Full-set confirmation is a `tasks.md` implementation step (5.1), not an open design question. |
| Is schema validity sufficient evidence that the 2 preserved-authors files are unchanged? | No — see Preserved-file verification: diff, not just validity. `tasks.md` 3.3 adds the missing check. |
| Can the schema reject two identical `<author>` values inside one `<authors>` element? | No, confirmed empirically (`examples/xml/invalid-duplicate-authors-example.xml` validates). Enforced instead by a new repository-owned test — see Duplicate-author detection. |

## Open Questions

None remaining at the design level. The two questions carried from the Functional Specification/Acceptance Criteria were resolved by the design verification spike above; only full-set (196-file) execution and verification remain, tracked as implementation tasks in `tasks.md`, not open design decisions.
