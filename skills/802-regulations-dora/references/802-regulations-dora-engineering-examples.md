---
name: 802-regulations-dora-engineering-examples
description: Use as Java-focused DORA engineering examples for ICT inventory, incident routing, recovery evidence, third-party ICT provider risk, resilience release gates, and Java release-policy controls.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# DORA Regulation for Java Enterprise Digital Operational Resilience

## Role

You are a senior Java enterprise architect and digital operational resilience reviewer who translates DORA themes into concrete Java engineering examples and reviewable evidence patterns

## Goal

Apply these DORA-aware examples after the regulation summary has been read, the questionnaire has been answered, and the target operational scope is understood.

These examples are not legal advice. They show engineering patterns that help Java teams create reviewable evidence for legal, compliance, security, risk, resilience, procurement, business-continuity, architecture, and business owners.

Use this examples reference together with `references/802-regulations-dora-chapters-summary.md`, `assets/questions/802-dora-engineering-review-questionnaire.md`, and the [DORA engineering review report template](../assets/reports/802-dora-engineering-review-report-template.md).

## Constraints

Translate DORA concerns into engineering controls for Java enterprise systems without replacing qualified legal, compliance, security, risk, resilience, procurement, business-continuity, architecture, or business-owner review.

- **NOT LEGAL ADVICE**: Treat the examples as engineering control patterns and escalation aids, not legal findings
- **EVIDENCE FIRST**: Prefer reviewable evidence over claims that controls exist
- **OWNER HANDOFFS**: Include business, service, operational, resilience, security, risk, procurement, provider, and compliance owners in control records
- **SECURE LOGGING**: Preserve operational evidence without exposing secrets, credentials, personal data, regulated records, or sensitive incident details unnecessarily
- **RELEASE READINESS**: Do not mark a system ready when critical resilience controls are undocumented, untested, ownerless, or dependent on unknown providers

## Examples

### Table of contents

- Example 1: Document ICT assets and operational dependencies
- Example 2: Route operational incidents with evidence
- Example 3: Verify backup, restore, and continuity controls
- Example 4: Make third-party ICT provider risk reviewable
- Example 5: Block release until resilience controls are evidenced
- Example 6: Implement a Java resilience release policy

### Example 1: Document ICT assets and operational dependencies

Title: business service, owners, data stores, providers, and recovery targets
Description: Use this pattern when a Java service supports financial operations or an important business service. The inventory creates evidence for operational owners and helps identify failure modes before resilience controls are designed.

**Good example:**

```yaml
ictServiceInventory:
  service: payment-reconciliation-api
  businessService: merchant settlement
  owner: payments-platform
  resilienceOwner: operational-resilience
  runtime:
    application: Spring Boot service
    environments: [staging, production]
    database: postgresql-settlement
    messaging: kafka.payments.reconciliation
    externalProviders:
      - name: cloud-managed-postgresql
        criticality: high
        supportContact: provider-support-id
      - name: payment-network-api
        criticality: high
        supportContact: vendor-ops-id
  recovery:
    rto: PT2H
    rpo: PT15M
    lastRestoreTest: 2026-05-18
  evidence:
    runbook: runbooks/payment-reconciliation.md
    dashboards: [grafana:settlement-latency, grafana:reconciliation-errors]
    alerts: [pagerduty:payment-reconciliation-sev2]
```

**Bad example:**

```yaml
ictServiceInventory:
  service: payment-reconciliation-api
  dependencies: cloud stuff
  recovery: should be fine
```


### Example 2: Route operational incidents with evidence

Title: severity, escalation, trace IDs, and corrective actions
Description: Use this pattern when a Java service has critical operations, external dependencies, or reporting handoffs. The goal is to make incidents detectable, reconstructable, and routed to the right owners without leaking sensitive data.

**Good example:**

```java
public IncidentDecision handleOperationalIncident(OperationalIncident incident) {
    IncidentSeverity severity = severityPolicy.classify(incident);
    EvidenceRecord evidence = evidenceRecorder.capture(
        incident.traceId(),
        incident.serviceName(),
        severity,
        incident.startedAt(),
        incident.affectedDependencyIds()
    );

    incidentWorkflow.notifyOwners(
        severity,
        List.of("service-owner", "security-operations", "resilience-owner", "risk-owner"),
        evidence.id()
    );

    correctiveActionBacklog.create(incident.id(), severity, evidence.id());
    return IncidentDecision.escalated(incident.id(), severity, evidence.id());
}
```

**Bad example:**

```java
public void handleOperationalIncident(Exception exception) {
    logger.error("Something failed", exception);
}
```


### Example 3: Verify backup, restore, and continuity controls

Title: tested recovery beats untested runbooks
Description: Use this pattern when a service owns regulated records, operational data, or critical state. DORA-aware engineering review should ask for restoration evidence and recovery target ownership, not just backup configuration.

**Good example:**

```markdown
Recovery evidence

- Service: customer-ledger-service
- Data store: ledger-postgresql
- Backup schedule: every 15 minutes
- RPO target: 15 minutes
- RTO target: 2 hours
- Last restore test: 2026-05-30
- Restore test result: passed in 47 minutes
- Rollback plan: deployment/runbooks/customer-ledger-rollback.md
- Business continuity owner: finance-operations
- Open gaps:
  - Add automated alert when backup age exceeds 20 minutes
  - Repeat restore test after schema migration v42
```

**Bad example:**

```markdown
Recovery evidence

- Backups are enabled.
- The cloud provider handles recovery.
```


### Example 4: Make third-party ICT provider risk reviewable

Title: criticality, SLAs, incident contacts, exit path, and monitoring
Description: Use this pattern when the system depends on cloud services, SaaS, managed data stores, payment processors, messaging platforms, observability providers, IAM providers, or external APIs.

**Good example:**

```yaml
thirdPartyIctProvider:
  provider: managed-kafka-platform
  servicesUsed: [transactions-topic, settlement-events-topic]
  criticality: high
  dataClassification: regulated-operational-data
  controls:
    contractOwner: procurement
    serviceOwner: platform-messaging
    slaReference: contract:kafka-platform-sla
    incidentNotification: vendor-portal-and-pagerduty
    monitoringDashboards: [grafana:kafka-lag, grafana:producer-errors]
    exitPlan: docs/resilience/kafka-provider-exit-plan.md
    lastFailoverExercise: 2026-04-22
  reviewDecision: acceptable-with-monitoring
```

**Bad example:**

```yaml
thirdPartyIctProvider:
  provider: managed-kafka-platform
  criticality: unknown
  exitPlan: none
```


### Example 5: Block release until resilience controls are evidenced

Title: controls, tests, owners, exceptions, and residual risk
Description: Use this pattern before production release or when a service becomes part of a regulated operational flow. The release decision should identify missing evidence and route residual risks to accountable owners.

**Good example:**

```yaml
resilienceReleaseGate:
  service: payment-reconciliation-api
  decision: conditional-release
  requiredBeforeGoLive:
    - restore-test-passed
    - incident-runbook-reviewed
    - provider-contacts-verified
    - sev2-alerts-enabled
  evidence:
    restoreTest: evidence:restore-test-2026-05-18
    incidentRunbook: runbooks/payment-reconciliation.md
    dashboards: [grafana:settlement-latency]
  residualRisks:
    - risk: manual provider escalation outside business hours
      owner: resilience-owner
      dueDate: 2026-07-01
```

**Bad example:**

```yaml
resilienceReleaseGate:
  service: payment-reconciliation-api
  decision: release
  reason: all tests passed locally
```


### Example 6: Implement a Java resilience release policy

Title: owner review, evidence checks, and release decision
Description: Use this pattern when Java deployment code, platform gates, or internal delivery services decide whether a financial service can move to production. The code should evaluate evidence, not hard-code a blanket approval.

**Good example:**

```java
public ReleaseDecision evaluate(ResilienceReview review) {
    List<String> blockers = new ArrayList<>();

    requirePresent(review.ictInventory(), "ICT inventory", blockers);
    requirePresent(review.incidentRunbook(), "incident runbook", blockers);
    requirePassing(review.restoreTest(), "restore test", blockers);
    requirePassing(review.providerReview(), "third-party ICT provider review", blockers);
    requirePresent(review.rollbackPlan(), "rollback plan", blockers);

    if (!blockers.isEmpty()) {
        return ReleaseDecision.blocked(
            review.serviceId(),
            blockers,
            Escalation.required("resilience", "security", "risk", "business-owner")
        );
    }

    return ReleaseDecision.approvedWithEvidence(
        review.serviceId(),
        review.evidenceReferences(),
        review.approvers()
    );
}
```

**Bad example:**

```java
public ReleaseDecision evaluate(ResilienceReview review) {
    return ReleaseDecision.approved(review.serviceId(), "tests passed");
}
```


## Output Format

- **MATCH** the relevant example patterns: ICT inventory, incident routing, recovery evidence, third-party ICT provider risk, resilience release gate, or Java resilience release policy
- **RECOMMEND** engineering controls: inventory updates, monitoring, alerting, evidence-safe logging, incident workflow, backup and restore verification, failover testing, rollback plans, provider monitoring, exit planning, and change approval
- **REPORT** conclusions and actions using `assets/reports/802-dora-engineering-review-report-template.md`, including review context, operational scope, potential violation or non-compliance signals with official-source links, engineering controls, residual risks, release decision, and prioritized action plan with owners and due dates


## Safeguards

- **GOVERNANCE ESCALATION**: Treat DORA applicability, classification, incident reporting, outsourcing, and provider criticality as governance decisions requiring qualified review
- **OPERATIONAL EVIDENCE**: Do not accept undocumented claims that monitoring, backup, recovery, continuity, or provider controls exist; ask for reviewable evidence
- **PROVIDER DEPENDENCY**: Treat cloud, SaaS, managed database, messaging, IAM, observability, and payment integrations as ICT dependencies requiring ownership and monitoring