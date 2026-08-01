Feature: Validate changes from usage of NIS2 regulation skill

Background:
  Given the skill "804-regulations-eu-nis2"

@acceptance-test
Scenario: Review a Java critical-sector service with NIS2 cybersecurity controls
  Given the maintainer-sanitized NIS2 evidence inventory prepared outside the agent context is:
    | category       | item                         | sanitized fact or stable evidence identifier                                      |
    | service        | service-id                   | CheckoutService                                                                    |
    | service        | business-service             | food distribution ordering and checkout                                            |
    | scope          | sector-signal                | Annex II food production, processing, and distribution                             |
    | scope          | entity-signal                | possible important entity; qualified classification pending                        |
    | scope          | member-state                 | Spain                                                                               |
    | environment    | deployment-environments      | staging and production                                                             |
    | owner          | business-owner               | food-commerce                                                                      |
    | owner          | technical-owner              | checkout-platform                                                                  |
    | owner          | security-owner               | security-operations                                                                |
    | owner          | resilience-owner             | business-continuity                                                                |
    | owner          | legal-compliance-owner       | legal-compliance                                                                   |
    | owner          | procurement-owner            | supplier-risk                                                                      |
    | asset          | application-api              | CheckoutService and POST /api/checkout; evidence:asset-checkout-api                 |
    | asset          | database                     | order-postgresql; evidence:asset-order-db                                           |
    | asset          | messaging                    | kafka.checkout-events; evidence:asset-checkout-topic                                |
    | asset          | identity-and-secrets         | workload identity and managed secrets; evidence:iam-checkout                        |
    | asset-gap      | ownerless-asset              | legacy settlement retry job has no assigned owner                                  |
    | asset-gap      | network-path                 | production egress path is absent from evidence:asset-inventory-2026-08              |
    | dependency-gap | dependency-inventory         | evidence:sbom-checkout-2026-08 excludes CI actions and Maven build plugins          |
    | vulnerability  | untriaged-finding            | evidence:vulnerability-scan-2026-08 contains one untriaged high finding             |
    | configuration  | secure-baseline              | no stable secure-configuration baseline identifier                                 |
    | observability  | monitoring                   | evidence:checkout-service-dashboard                                                |
    | observability  | alerting-gap                 | no alert routes failed checkout event publication                                  |
    | observability  | logging-gap                  | Kafka administrative actions and privileged database changes are not evidenced     |
    | incident       | incident-pathway             | evidence:incident-checkout-v2 routes service, security, legal, and resilience owners |
    | incident       | reporting-obligation         | qualification and notification timing decision pending legal and compliance review |
    | continuity     | recovery-targets             | RTO PT2H and RPO PT15M                                                             |
    | continuity-gap | restore-test                 | no restore-test evidence after the planned database migration                      |
    | continuity-gap | failover-and-rollback        | no continuity exercise or verified rollback evidence                              |
    | provider-gap   | unsupported-provider         | payment-risk-api support status and provider owner are unknown                     |
    | access-gap     | least-privilege              | privileged production database access review is not evidenced                     |
    | cryptography   | transport                    | TLS required; key ownership and rotation evidence are missing                      |
    | pipeline       | delivery-mode                | pull request through CI/CD                                                         |
    | pipeline       | pipeline-evidence            | evidence:checkout-ci-2026-08                                                       |
    | change-gap     | approval                     | pull-request review exists; migration and Kafka compatibility approvals are absent |
    | change         | database-migration           | add risk_decision column to the order database                                    |
    | change         | kafka-contract               | add riskDecision to outbound CheckoutCompleted events                              |
    | side-effect    | production-impact            | writes order state and publishes an outbound checkout event                        |
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
  Given the maintainer-sanitized NIS2 evidence inventory prepared outside the agent context is:
    | category       | item                         | sanitized fact or stable evidence identifier                                      |
    | service        | service-id                   | CheckoutService                                                                    |
    | service        | business-service             | food distribution ordering and checkout                                            |
    | scope          | sector-signal                | Annex II food production, processing, and distribution                             |
    | scope          | entity-signal                | possible important entity; qualified classification pending                        |
    | scope          | member-state                 | Spain                                                                               |
    | environment    | deployment-environments      | staging and production                                                             |
    | owner          | business-owner               | food-commerce                                                                      |
    | owner          | technical-owner              | checkout-platform                                                                  |
    | owner          | security-owner               | security-operations                                                                |
    | owner          | resilience-owner             | business-continuity                                                                |
    | owner          | legal-compliance-owner       | legal-compliance                                                                   |
    | owner          | platform-owner               | platform-engineering                                                               |
    | owner          | procurement-owner            | supplier-risk                                                                      |
    | asset          | application-api              | CheckoutService and POST /api/checkout; evidence:asset-checkout-api                 |
    | asset          | database                     | order-postgresql; evidence:asset-order-db                                           |
    | asset          | messaging                    | kafka.checkout-events; evidence:asset-checkout-topic                                |
    | asset          | identity-and-secrets         | workload identity and managed secrets; evidence:iam-checkout                        |
    | asset-gap      | ownerless-asset              | legacy settlement retry job has no assigned owner                                  |
    | asset-gap      | network-path                 | production egress path is absent from evidence:asset-inventory-2026-08              |
    | dependency-gap | dependency-inventory         | evidence:sbom-checkout-2026-08 excludes CI actions and Maven build plugins          |
    | vulnerability  | untriaged-finding            | evidence:vulnerability-scan-2026-08 contains one untriaged high finding             |
    | configuration  | secure-baseline              | no stable secure-configuration baseline identifier                                 |
    | observability  | monitoring                   | evidence:checkout-service-dashboard                                                |
    | observability  | alerting-gap                 | no alert routes failed checkout event publication                                  |
    | observability  | logging-gap                  | Kafka administrative actions and privileged database changes are not evidenced     |
    | incident       | incident-pathway             | evidence:incident-checkout-v2 routes service, security, legal, and resilience owners |
    | incident       | reporting-obligation         | qualification and notification timing decision pending legal and compliance review |
    | continuity     | recovery-targets             | RTO PT2H and RPO PT15M                                                             |
    | continuity-gap | restore-test                 | no restore-test evidence after the planned database migration                      |
    | continuity-gap | failover-and-rollback        | no continuity exercise or verified rollback evidence                              |
    | provider-gap   | unsupported-provider         | payment-risk-api support status and provider owner are unknown                     |
    | access-gap     | least-privilege              | privileged production database access review is not evidenced                     |
    | cryptography   | transport                    | TLS required; key ownership and rotation evidence are missing                      |
    | pipeline       | delivery-mode                | direct commit to main followed by CI/CD                                             |
    | pipeline-gap   | pre-merge-review             | missing                                                                            |
    | pipeline-gap   | protected-main               | bypassed                                                                           |
    | pipeline       | pipeline-evidence            | evidence:checkout-direct-main-ci-2026-08                                           |
    | change-gap     | approval                     | migration, Kafka compatibility, and cybersecurity approvals are absent             |
    | change         | database-migration           | add risk_decision column to the order database                                    |
    | change         | kafka-contract               | add riskDecision to outbound CheckoutCompleted events                              |
    | side-effect    | production-impact            | writes order state and publishes an outbound checkout event                        |
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
