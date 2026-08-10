Feature: Validate changes from usage of ISO/IEC 25010 quality model skill

Background:
  Given the skill "814-regulations-iso-25010"

@acceptance-test
Scenario: Review a Java change through pull-request delivery with ISO/IEC 25010:2023 quality characteristics
  Given the system description file "examples/diagrams/deployment/system-example-cicd-pr-model.md"
  And the deployment and delivery pipeline diagram file "examples/diagrams/deployment/expected-system-deployment.puml"
  And the simulated feature request file "examples/diagrams/deployment/checkout-service-feature-request.md"
  And the local generated skill path ".agents/skills/814-regulations-iso-25010"
  And the requested report output path is "examples/regulations/iso-25010/ISO-25010-ENGINEERING-REVIEW-REPORT.md"
  And any existing report at the requested output path must be overwritten
  And the folder "examples/regulations/iso-25010" has no git changes
  And the feature request is expected to be developed and released through the described CI/CD pipeline
  And the ISO/IEC 25010:2023 review facts are based only on information present in "examples/diagrams/deployment/system-example-cicd-pr-model.md" and "examples/diagrams/deployment/checkout-service-feature-request.md"
  And the reviewed scenario includes Java implementation changes, a database schema migration, Kafka message contract changes, and checkout business logic
  When the skill ".agents/skills/814-regulations-iso-25010" is applied to the system description, diagram, and feature request files
  Then the skill reads "references/814-regulations-iso-25010-chapters-summary.md"
  And the skill reads "references/814-regulations-iso-25010-engineering-examples.md"
  And the skill reads "assets/questions/814-iso-25010-engineering-review-questionnaire.md"
  And the skill reads "assets/reports/814-iso-25010-engineering-review-report-template.md"
  And the skill frames ISO/IEC 25010:2023 findings as engineering review evidence and action items rather than certification, compliance, or conformity conclusions
  And review findings do not use facts outside "examples/diagrams/deployment/system-example-cicd-pr-model.md" and "examples/diagrams/deployment/checkout-service-feature-request.md"
  And the skill covers all nine ISO/IEC 25010:2023 quality characteristics: Functional Suitability, Performance Efficiency, Compatibility, Interaction Capability, Reliability, Security, Maintainability, Flexibility, and Safety
  And the skill scopes the Java system, module, or delivery pipeline under review, its lifecycle stage, and the architecture, product, security, platform, and operations owners
  And the skill identifies quality-attribute findings for the database schema migration, the Kafka message contract change, and the checkout business logic against each of the nine quality characteristics
  And each finding is connected to actionable Java engineering practices and qualified owner escalation where needed
  And the skill analyzes the CheckoutService feature request as a structured ISO/IEC 25010:2023 quality-attribute engineering review scope because a backward-compatible schema migration and a versioned Kafka contract change can affect functional suitability, compatibility, reliability, and maintainability
  And the skill uses Java examples to explain functional-suitability traceability, performance-efficiency capacity evidence, compatibility versioning, interaction-capability self-descriptive errors, reliability resilience patterns, security controls, maintainability module boundaries, flexibility configuration externalization, and safety fail-safe guardrails
  And the skill recommends engineering controls for acceptance-criteria traceability, load/capacity test evidence, API and message contract versioning, self-descriptive error handling, resilience patterns, authorization and secrets management, module-boundary and test-pyramid review, configuration externalization, and fail-safe or hazard-warning guardrails where applicable
  And the skill reports conclusions and actions using the ISO/IEC 25010:2023 engineering review report template
  And the skill overwrites the ISO/IEC 25010:2023 engineering review report at "examples/regulations/iso-25010/ISO-25010-ENGINEERING-REVIEW-REPORT.md"
  And any git changes produced during skill execution and verification are reset

@integration-test
Scenario: Skill follows the generator registration and local-output workflow
  Given skill content must be maintained through the generator pipeline
  When the ISO/IEC 25010:2023 skill is implemented
  Then the source changes are made under "plinth-skills-generator/src/main/resources"
  And "plinth-skills-generator/src/main/resources/skills.xml" registers skill id "814" with skillId "814-regulations-iso-25010"
  And the generated local skill output includes ".agents/skills/814-regulations-iso-25010/SKILL.md"
  And the generated local skill output includes ".agents/skills/814-regulations-iso-25010/assets/questions/814-iso-25010-engineering-review-questionnaire.md"
  And the generated local skill output includes ".agents/skills/814-regulations-iso-25010/assets/reports/814-iso-25010-engineering-review-report-template.md"
  And generated references contain no unresolved include markers or broken local reference paths
  And generated release output under "skills/" is not edited directly
  And the skill's guidance disambiguates it from "032-architecture-adr-non-functional-requirements" as a structured, repeatable review rather than interactive, conversational ADR discovery, without modifying "032-architecture-adr-non-functional-requirements" itself
  And applicable XML and skill generation validations can be executed before promotion
