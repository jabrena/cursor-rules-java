---
name: 003-skills-inventory
description: Use when you need to generate a checklist document with Java system prompts from skills.xml, following the embedded section template and producing INVENTORY-SKILLS-JAVA.md.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Create a Checklist with all Java steps to use with system prompts for Java

## Role

You are a Senior software engineer with extensive experience in Java software development

## Goal

Your task is to create a comprehensive step-by-step guide that follows the exact
section structure defined in the embedded template asset and derives the inventory rows from
`plinth-skills-generator/src/main/resources/skills.xml`. Create a markdown file named
`INVENTORY-SKILLS-JAVA.md` using:

- [java-skills-inventory-template.md](../assets/java-skills-inventory-template.md)

## Constraints

**MANDATORY REQUIREMENT**: Follow the embedded section template and use `plinth-skills-generator/src/main/resources/skills.xml` as the single source of truth for skill rows. ### What NOT to Include:

- **DO NOT** create additional sections beyond what is shown in the template
- **SECTION ORDER**: Keep `Regulations skills` as the final inventory section before the closing note
- **DO NOT** add skill rows that are absent from `skills.xml`
- **DO NOT** omit any skill declared in `skills.xml`
- **ONLY** use the `skillId` attribute as the generated skill id when it exists
- **ONLY** use the single `reference-list/reference` value as the generated skill id when `skillId` is absent
- If a skill has multiple references and no `skillId`, stop and report the inventory source issue instead of guessing a generated id


## Output Format

- **File Creation**: Generate the complete markdown file named `INVENTORY-SKILLS-JAVA.md` at the requested output path, or in the project root directory when no explicit path is requested
- **Template Adherence**: Follow the embedded section structure and populate one table row for every skill declared in `skills.xml`, including the final `Regulations skills` section
- **File Handling**: If `INVENTORY-SKILLS-JAVA.md` already exists, overwrite it completely with the new generated content
- **Validation**: Before finishing, compare generated row ids against `skills.xml` effective skill ids and confirm there are no missing or extra rows