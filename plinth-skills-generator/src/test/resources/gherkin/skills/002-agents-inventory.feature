Feature: Validate changes from usage of embedded agents inventory skill

Background:
  Given the skill "002-agents-inventory"
  And the inventory sandbox folder "examples/skills/inventory" has no git changes

@acceptance-test
Scenario: Generate embedded agents inventory with every agent asset
  Given the local generated skill path ".agents/skills/002-agents-inventory"
  And the inventory template asset path "assets/java-agents-inventory-template.md"
  And the requested inventory output path is "examples/skills/inventory/INVENTORY-AGENTS-JAVA.md"
  And any existing report at the requested output path must be overwritten
  When the skill ".agents/skills/002-agents-inventory" is applied to generate the embedded agents inventory
  Then the skill reads "references/002-agents-inventory.md"
  And the skill reads "assets/java-agents-inventory-template.md"
  And the generated inventory file exists at "examples/skills/inventory/INVENTORY-AGENTS-JAVA.md"
  And the generated inventory file includes the heading "# Embedded Agents Inventory"
  And the generated inventory file includes the section "## Embedded agents"
  And the generated inventory file includes the section "## Installation target options"
  And the generated inventory file includes the installation targets ".cursor/agents" and ".claude/agents"
  And the generated inventory file includes exactly one row for each embedded agent asset:
    | assetFile                        | agentName                      |
    | plinth-business-analyst.md        | plinth-business-analyst         |
    | plinth-architect.md               | plinth-architect                |
    | plinth-tech-lead.md               | plinth-tech-lead                |
    | plinth-no-java.md                 | plinth-no-java                  |
    | plinth-java-performance.md        | plinth-java-performance         |
    | plinth-java-coder.md              | plinth-java-coder               |
    | plinth-java-micronaut-coder.md    | plinth-java-micronaut-coder     |
    | plinth-java-quarkus-coder.md      | plinth-java-quarkus-coder       |
    | plinth-java-spring-boot-coder.md  | plinth-java-spring-boot-coder   |
  And every agent row in the generated file corresponds to a same-named agent listed in "assets/java-agents-inventory-template.md"
  And no agent listed in "assets/java-agents-inventory-template.md" is missing from the generated inventory
  And the generated inventory file does not include agent rows outside the embedded inventory template
  And any git changes produced under "examples/skills/inventory" during skill execution and verification are reset
