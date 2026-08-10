## 1. Command Version Contract

- [x] 1.1 Update `commands.xsd` with required global version metadata and extend schema tests.
- [x] 1.2 Add `0.19.0-SNAPSHOT` version metadata to every inventoried command XML source and validate the XML.

## 2. Generation and Verification

- [x] 2.1 Render nested `metadata.version` in `command-to-markdown.xsl` and extend frontmatter tests.
- [x] 2.2 Run OpenSpec validation and `./mvnw clean install -pl plinth-skills-generator -am` to verify and regenerate local skills.
- [x] 2.3 Confirm generated command assets preserve version metadata and record validation results.

## 3. Command Author Contract

- [x] 3.1 Add PML 0.9.0-compatible `authors/author` support to `commands.xsd` and schema tests.
- [x] 3.2 Add author metadata to every inventoried command XML source and validate the XML.
- [x] 3.3 Render nested `metadata.author` in `command-to-markdown.xsl` and extend frontmatter tests for one and multiple authors.
- [x] 3.4 Run OpenSpec, Markdown, focused Maven, and integrated local-skill regeneration validation.
- [x] 3.5 Confirm all generated command assets preserve author and version metadata.

## 4. Repository Version Consistency

- [x] 4.1 Add an inventory-driven command test requiring every command XML version to match the root `pom.xml` version.
- [x] 4.2 Add an inventory-driven agent test requiring every agent XML version to match the root `pom.xml` version.
- [x] 4.3 Run focused module verification, the full Maven build, OpenSpec validation, and Markdown validation.
