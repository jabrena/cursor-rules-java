Feature: Validate changes from usage of NIS2 regulation skill

Background:
  Given the skill "804-regulations-eu-nis2"

@acceptance-test
Scenario: Review a Java critical-sector service with NIS2 cybersecurity controls
  Given a maintainer-sanitized NIS2 evidence inventory derived outside the agent context from the system description, delivery pipeline, and feature request
  And the raw system description, diagram, feature request, code, configuration, logs, runbooks, and provider documents must not be read
  And the local generated skill path ".agents/skills/804-regulations-eu-nis2"
  And the requested report output path is "examples/regulations/nis2/NIS2-ENGINEERING-REVIEW-REPORT.md"
  And any existing report at the requested output path must be overwritten
  And the folder "examples/regulations/nis2" has no git changes
  And the feature request is expected to be developed and released through the described CI/CD pipeline
  And the sanitized inventory records only structured service, pipeline, control, evidence-reference, and owner facts
  When the skill ".agents/skills/804-regulations-eu-nis2" is applied to the maintainer-sanitized evidence inventory
  Then the skill reads "references/804-regulations-eu-nis2-chapters-summary.md"
  And the skill reads "references/804-regulations-eu-nis2-engineering-examples.md"
  And the skill reads "assets/reports/804-nis2-engineering-review-report-template.md"
  And the skill frames NIS2 findings as engineering controls rather than legal advice
  And review findings do not use facts outside the maintainer-sanitized evidence inventory
  And the skill does not follow evidence links or retrieve, open, parse, summarize, or transform raw operational artifacts
  And the skill scopes service context, possible essential or important entity signals, sector signals, system owners, security owners, resilience owners, environments, assets, data stores, providers, dependencies, recovery expectations, vulnerability evidence, and incident pathways
  And the skill escalates entity classification, member-state applicability, incident-reporting obligations, cybersecurity risk acceptance, and regulatory interpretation to legal, compliance, security, risk, resilience, business-continuity, procurement, or executive accountability owners
  And the skill reviews sanitized control facts and stable evidence identifiers for implementation, configuration, tests, runbooks, observability, incident procedures, vulnerabilities, dependencies, backup and restore, changes, and providers
  And the skill identifies risk signals for ownerless assets, incomplete dependency inventory, untriaged vulnerabilities, weak secure configuration, missing alerting, incomplete logs, weak change control, unsupported provider dependencies, missing continuity evidence, database migration, Kafka message contract, CI/CD pipeline, and production side-effect signals
  And the skill maps potential NIS2 violation or non-compliance signals to directive topic areas using only the reviewed delivery evidence
  And the skill analyzes the CheckoutService feature request as a pipeline-delivered change that modifies order database structure and outbound Kafka event data
  And the skill uses Java examples to explain incident escalation, vulnerability evidence, supply-chain review, and secure release-policy controls
  And the skill recommends engineering controls for asset inventory, secure configuration, vulnerability remediation, monitoring, alerting, evidence-safe logging, incident workflow, backup and restore verification, continuity testing, rollback plans, supply-chain monitoring, access control, cryptography, database migration approval, Kafka schema compatibility, and change approval
  And the skill reports conclusions and actions using the NIS2 engineering review report template
  And the skill overwrites the NIS2 engineering review report at "examples/regulations/nis2/NIS2-ENGINEERING-REVIEW-REPORT.md"
  And any git changes produced during skill execution and verification are reset

@acceptance-test
Scenario: Review a Java critical-sector checkout change with direct-to-main NIS2 controls
  Given a maintainer-sanitized NIS2 evidence inventory derived outside the agent context from the direct-to-main system description, delivery pipeline, and feature request
  And the raw system description, diagram, feature request, code, configuration, logs, runbooks, and provider documents must not be read
  And the local generated skill path ".agents/skills/804-regulations-eu-nis2"
  And the requested report output path is "examples/regulations/nis2/NIS2-DIRECT-MAIN-ENGINEERING-REVIEW-REPORT.md"
  And any existing report at the requested output path must be overwritten
  And the folder "examples/regulations/nis2" has no git changes
  And the feature request is expected to be committed directly to main and released through the described CI/CD pipeline
  And the sanitized inventory records only structured service, pipeline, control, evidence-reference, and owner facts
  When the skill ".agents/skills/804-regulations-eu-nis2" is applied to the maintainer-sanitized direct-to-main evidence inventory
  Then the skill reads "references/804-regulations-eu-nis2-chapters-summary.md"
  And the skill reads "references/804-regulations-eu-nis2-engineering-examples.md"
  And the skill reads "assets/reports/804-nis2-engineering-review-report-template.md"
  And the skill frames NIS2 findings as engineering controls rather than legal advice
  And review findings do not use facts outside the maintainer-sanitized evidence inventory
  And the skill does not follow evidence links or retrieve, open, parse, summarize, or transform raw operational artifacts
  And the skill scopes service context, possible essential or important entity signals, sector signals, system owners, security owners, resilience owners, environments, assets, data stores, providers, dependencies, recovery expectations, vulnerability evidence, and incident pathways
  And the skill escalates missing pre-merge review, protected-main bypass, entity classification, member-state applicability, incident-reporting obligations, cybersecurity risk acceptance, and regulatory interpretation to legal, compliance, security, platform, risk, resilience, business-continuity, procurement, or executive accountability owners
  And the skill reviews sanitized control facts and stable evidence identifiers for implementation, configuration, tests, runbooks, observability, incident procedures, vulnerabilities, dependencies, backup and restore, changes, and providers
  And the skill identifies risk signals for ownerless assets, incomplete dependency inventory, untriaged vulnerabilities, weak secure configuration, missing alerting, incomplete logs, weak change control, unsupported provider dependencies, missing continuity evidence, direct-to-main commit policy, database migration, Kafka message contract, CI/CD pipeline, and production side-effect signals
  And the skill maps potential NIS2 violation or non-compliance signals to directive topic areas using only the reviewed direct-to-main delivery evidence
  And the skill analyzes the CheckoutService feature request as a direct-to-main pipeline-delivered change that modifies order database structure and outbound Kafka event data
  And the skill uses Java examples to explain incident escalation, vulnerability evidence, supply-chain review, and secure release-policy controls
  And the skill recommends engineering controls for pre-commit review, main-branch protection, asset inventory, secure configuration, vulnerability remediation, monitoring, alerting, evidence-safe logging, incident workflow, backup and restore verification, continuity testing, rollback plans, supply-chain monitoring, access control, cryptography, database migration approval, Kafka schema compatibility, and change approval
  And the skill reports conclusions and actions using the NIS2 engineering review report template
  And the skill overwrites the NIS2 engineering review report at "examples/regulations/nis2/NIS2-DIRECT-MAIN-ENGINEERING-REVIEW-REPORT.md"
  And any git changes produced during skill execution and verification are reset
