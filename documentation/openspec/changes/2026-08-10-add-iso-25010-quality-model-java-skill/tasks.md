# Tasks

## 1. Implementation Checklist

- [ ] 1.1 Review issue #1127 and identify ISO/IEC 25010:2023 as scoped skill `814`.
- [ ] 1.2 Record source artifacts, derivation direction, scope boundary, and validation expectations.
- [ ] 1.3 Compare the existing `813-regulations-iso-42001` skill and current `8xx-regulations-*` pattern, and the conversational `032-architecture-adr-non-functional-requirements` skill, before implementing the new skill.
- [ ] 1.4 Add `plinth-skills-generator/src/main/resources/skill-indexes/814-skill.xml`.
- [ ] 1.5 Add `plinth-skills-generator/src/main/resources/skill-references/814-regulations-iso-25010-chapters-summary.xml` covering Functional Suitability, Performance Efficiency, Compatibility, Reliability, Security, Maintainability, Flexibility, and Safety.
- [ ] 1.6 Add `plinth-skills-generator/src/main/resources/skill-references/814-regulations-iso-25010-engineering-examples.xml` with Java Enterprise engineering examples and output guidance per quality characteristic.
- [ ] 1.7 Include engineering review guidance for each of the eight quality characteristics named in issue #1127, connected to concrete Java Enterprise engineering practices.
- [ ] 1.8 Add `plinth-skills-generator/src/main/resources/skill-references/assets/questions/814-iso-25010-engineering-review-questionnaire.md`.
- [ ] 1.9 Add `plinth-skills-generator/src/main/resources/skill-references/assets/reports/814-iso-25010-engineering-review-report-template.md`.
- [ ] 1.10 Ensure the skill workflow reads the ISO/IEC 25010:2023 chapters-summary, engineering examples, questionnaire, and report template before implementation review.
- [ ] 1.11 Ensure the skill clearly states that it provides structured engineering review guidance, not certification, compliance, or conformity decisions.
- [ ] 1.12 Ensure the skill's guidance and workflow steps disambiguate it from `032-architecture-adr-non-functional-requirements` (structured, repeatable review vs. interactive, conversational ADR discovery) without modifying `032` itself.
- [ ] 1.13 Register skill id `814` with explicit `skillId="814-regulations-iso-25010"`, references, questionnaire, and report template in `plinth-skills-generator/src/main/resources/skills.xml`.
- [ ] 1.14 Add `plinth-skills-generator/src/test/resources/gherkin/skills/814-regulations-iso-25010.feature` with acceptance and integration scenarios modeled after existing regulation skills and the issue's Gherkin acceptance criteria.
- [ ] 1.15 Ensure the Gherkin scenarios require reading bundled references and assets, covering all eight quality characteristics, producing an engineering review report, and avoiding certification, compliance, or conformity conclusions.
- [ ] 1.16 Add `814-regulations-iso-25010` to `plinth-skills-generator/src/test/resources/gherkin/skills/acceptance-tests-prompts-skills.md`.
- [ ] 1.17 Validate changed XML files with `xmllint --noout`.
- [ ] 1.18 Run `./mvnw clean install -pl plinth-skills-generator -am`.
- [ ] 1.19 Inspect generated local `.agents/skills/814-regulations-iso-25010/SKILL.md`.
- [ ] 1.20 Inspect generated local ISO/IEC 25010:2023 chapters-summary, engineering examples, questionnaire, and report template outputs.
- [ ] 1.21 Confirm the local install did not modify the public `skills/` directory.
- [ ] 1.22 Run `./mvnw clean install -pl plinth-skills-generator -am -P release` and confirm `skills/814-regulations-iso-25010` is refreshed with the chapters-summary and engineering-examples assets.
- [ ] 1.23 Run `npx skill-check@latest skills --no-security-scan --format github` and confirm no errors for `814-regulations-iso-25010`.
- [ ] 1.24 Run `skill-scanner scan-all ./skills --recursive --use-behavioral --policy strict --fail-on-severity high` and confirm no high-severity findings for `814-regulations-iso-25010`.
- [ ] 1.25 Execute the listed `814-regulations-iso-25010` acceptance prompt and verify it passes.
- [ ] 1.26 Run `./mvnw clean verify -pl plinth-skills-generator -am`.
- [ ] 1.27 Run `openspec validate --all`.
