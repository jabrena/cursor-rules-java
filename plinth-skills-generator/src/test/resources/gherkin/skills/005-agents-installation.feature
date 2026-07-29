Feature: Validate changes from usage of embedded agents installation skill

Background:
  Given the skill "005-agents-installation"
  And the installation sandbox folder "examples/skills/installers" has no git changes

@acceptance-test
Scenario: Install embedded agents into the Cursor agents destination
  Given the local generated skill path ".agents/skills/005-agents-installation"
  And the installation sandbox root is "examples/skills/installers"
  And all installation paths are resolved relative to the installation sandbox root
  And the selected agent destination is ".cursor/agents"
  And the expected target directory is "examples/skills/installers/.cursor/agents"
  When the skill ".agents/skills/005-agents-installation" is applied to install embedded agents
  Then the skill asks "Where do you want to install the embedded agents?" before copying files
  And the skill presents ".cursor/agents" and ".claude/agents" as destination choices
  And the skill waits for the explicit destination answer ".cursor/agents" before writing files
  And the skill reads "references/005-agents-installation.md"
  And the skill copies embedded agent files only from "assets/agents"
  And the target directory "examples/skills/installers/.cursor/agents" is created when missing
  And no agent files are copied outside "examples/skills/installers"
  And the target directory contains exactly these agent files:
    | fileName                           |
    | plinth-business-analyst.md          |
    | plinth-architect.md                 |
    | plinth-tech-lead.md                 |
    | plinth-no-java.md                   |
    | plinth-java-performance.md          |
    | plinth-java-coder.md                |
    | plinth-java-micronaut-coder.md      |
    | plinth-java-quarkus-coder.md        |
    | plinth-java-spring-boot-coder.md    |
  And each installed agent file matches its same-named embedded asset content
  And the skill reports the selected destination, created files, overwrite actions, and an optional verification step
  And any git changes produced under "examples/skills/installers" during skill execution and verification are reset

@integration-test
Scenario: Skill follows the generator registration and local-output workflow
  Given skill content must be maintained through the generator pipeline
  When the embedded agents installation skill is implemented
  Then the source changes are made under "plinth-skills-generator/src/main/resources"
  And "plinth-skills-generator/src/main/resources/skills.xml" registers skill id "005" with the agents installation reference
  And the generated local skill output includes ".agents/skills/005-agents-installation/SKILL.md"
  And the generated local skill output includes ".agents/skills/005-agents-installation/assets/agents/plinth-business-analyst.md"
  And the generated local skill output includes ".agents/skills/005-agents-installation/assets/agents/plinth-java-spring-boot-coder.md"
  And generated references contain no unresolved include markers or broken local reference paths
  And generated release output under "skills/" is not edited directly
  And applicable XML and skill generation validations can be executed before promotion
