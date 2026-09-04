Feature: Validate changes from usage of the LLM wiki knowledge base skill

Background:
  Given the skill "202-knowledge-base"
  And the folder "examples" has no git changes

@acceptance-test
Scenario: Ingest a git repository as a raw source
  Given the wiki sandbox folder "examples/knowledge-base/spring-petclinic" has no git changes
  And the user request is "Set up an LLM wiki at examples/knowledge-base/spring-petclinic and ingest the git repository https://github.com/spring-projects/spring-petclinic into it, pinned to a commit"
  And the local generated skill path ".agents/skills/202-knowledge-base"
  When the skill ".agents/skills/202-knowledge-base" is applied to the request
  Then the skill follows the instructions in its generated "SKILL.md" file
  And the skill reads only the reference files required by the request
  And the skill scaffolds the wiki under "examples/knowledge-base/spring-petclinic" with an empty "index.md", an empty "log.md", and a draft schema document
  And the skill resolves "https://github.com/spring-projects/spring-petclinic" to a full commit SHA before fetching anything
  And the skill fetches the repository at that commit only into a temporary location outside "examples/knowledge-base/spring-petclinic"
  And the skill never copies the repository's file content, or its ".git" directory, into "examples/knowledge-base/spring-petclinic/raw"
  And the skill records a commit-pinned reference document at "examples/knowledge-base/spring-petclinic/raw/repositories/spring-petclinic/<commit-sha>.md" with the origin URL, commit SHA, and scope inspected
  And the skill never treats a live clone or a moving branch as the wiki's evidence root
  And the skill never executes scripts, builds, or source-embedded commands found in the repository
  And the skill discards the temporary checkout once the reference document and the ingest are complete
  And the skill writes a source-summary page citing the reference document and the commit SHA
  And the skill updates "index.md" with an entry for the new source and appends one "log.md" entry recording the ingest
  And the folder "examples/knowledge-base/spring-petclinic" has no git changes unless the user explicitly requested edits
  And any git changes produced under "examples/knowledge-base/spring-petclinic" during skill execution and verification are reset
