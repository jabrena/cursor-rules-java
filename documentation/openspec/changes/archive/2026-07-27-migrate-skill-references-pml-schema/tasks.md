schema: spec-driven
created: 2026-07-27
---
## 1. Baseline verification

- [x] 1.1 Fetch and diff `https://jabrena.github.io/pml/schemas/0.8.0/pml.xsd` against `https://jabrena.github.io/pml/schemas/0.9.0/pml.xsd` and confirm the only differences are the removal of the deprecated `<xs:element ref="author" minOccurs="0"/>` reference and the `<xs:element ref="triggers" minOccurs="0"/>` reference plus its `triggers`/`trigger-list`/`trigger` definitions.
- [x] 1.2 List the complete set of in-scope files: all XML files under `plinth-skills-generator/src/main/resources/skill-references/` that declare `xsi:noNamespaceSchemaLocation` for PML 0.8.0 (196 expected), excluding `skill-references/assets/questions/*.xml` (no schema location declared).
- [x] 1.3 Within that set, list which files already declare `<authors>` (2 expected: `045-planning-azure-devops.xml`, `057-design-feature-toggles.xml`) versus which still declare a bare `<author>` (194 expected).

## 2. Migrate skill-reference schema references and metadata (atomic per file)

- [x] 2.1 For each of the 194 bare-`<author>` files, in one edit: change `xsi:noNamespaceSchemaLocation` to the PML 0.9.0 `pml.xsd` URL, and wrap the existing `<author>Name</author>` element in `<authors>...</authors>`, preserving the original author name exactly.
- [x] 2.2 For each of the 2 already-`<authors>`-shaped files, change only `xsi:noNamespaceSchemaLocation` to the PML 0.9.0 `pml.xsd` URL; leave the existing `<authors>`/`<author>` structure untouched.
- [x] 2.3 Confirm no file under `skill-references/` still references the PML 0.8.0 URL or declares a bare `<author>` element directly under `<metadata>` after migration.
- [x] 2.4 Confirm `skill-references/assets/questions/*.xml` files are unchanged (no schema location added).

## 3. Scope boundary verification

- [x] 3.1 Confirm `plinth-skills-generator/src/main/resources/skills.xsd` is byte-for-byte unchanged after this change.
- [x] 3.2 Confirm every `plinth-skills-generator/src/main/resources/skill-indexes/*.xml` file still references the local `skills.xsd` and still uses its pre-existing `<author>`/`<authors>`/`<triggers>` shape, unmodified by this change.
- [x] 3.3 For each of the 2 already-`<authors>`-shaped files (`045-planning-azure-devops.xml`, `057-design-feature-toggles.xml`), diff the file before and after migration and confirm the only changed line is the `xsi:noNamespaceSchemaLocation` value — the `<authors>` element and every `<author>` child (content, order, and whitespace) are byte-identical to their pre-migration state.

## 4. Test and documentation currency

- [x] 4.1 Update `REMOTE_XSD` in `plinth-skills-generator/src/test/java/info/jab/pml/RemoteSchemaValidationTest.java` from the PML 0.8.0 URL to the PML 0.9.0 URL.
- [x] 4.2 Update the PML schema URL referenced in `AGENTS.md` from 0.8.0 to 0.9.0, and confirm the update is visible through the `CLAUDE.md` symlink.

## 5. Validation

- [x] 5.1 Run `xmllint --noout` (or `--schema` against the PML 0.9.0 `pml.xsd`) for every migrated file under `skill-references/` and resolve any reported error.
- [x] 5.2 Run `./mvnw clean verify -pl plinth-skills-generator -am` and confirm `RemoteSchemaValidationTest` passes for all 196 files against the bumped `REMOTE_XSD`.
- [x] 5.3 Generate skill-reference Markdown output before and after the migration (`./mvnw clean install -pl plinth-skills-generator -am`) and diff the author-related frontmatter/content for at least the 194 rewritten files, confirming no unintended output change.
- [x] 5.4 Run `openspec validate --all` from `documentation/` and resolve every validation error before implementation promotion.

## 6. Duplicate-author content check (new test, schema cannot express this)

- [x] 6.1 Confirm that the PML 0.9.0 schema does **not** reject two `<author>` children with an identical value inside the same `<authors>` element — `xmllint --noout --schema` against `examples/xml/invalid-duplicate-authors-example.xml` (this change) reports the file as valid, because `pml.xsd` places no uniqueness constraint on sibling `author` text. Record this as the reason a repository-owned test, not schema validation, is required for task 6.2.
- [x] 6.2 Add a new JUnit test (e.g. `SkillReferenceAuthorsUniquenessTest` in `plinth-skills-generator/src/test/java/info/jab/pml/`), parameterized over `SkillReferences.xmlFilenames()` like `RemoteSchemaValidationTest`, that parses each file's `<metadata>/<authors>/<author>` values and asserts no two `<author>` children within the same file share an identical (normalized/trimmed) value.
- [x] 6.3 Confirm the new test passes for all 196 migrated skill-reference sources (including the 2 already-`<authors>`-shaped files, which already have distinct author names today).
- [x] 6.4 Confirm the new test fails, with a clear assertion message identifying the offending file and duplicated value, when pointed at `examples/xml/invalid-duplicate-authors-example.xml` (this change) as a fixture.

## 7. Follow-up (deferred — not required to close this change)

- [ ] 7.1 (Follow-up, not gating) Consider a shared schema-version constant/config so `RemoteSchemaValidationTest` and `AGENTS.md` cannot drift independently on a future PML version bump — recorded as a candidate in `design.md`'s Alternative Analysis, not implemented by this change.
