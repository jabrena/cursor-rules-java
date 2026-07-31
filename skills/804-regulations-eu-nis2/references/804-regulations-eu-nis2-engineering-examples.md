---
name: 804-regulations-eu-nis2-engineering-examples
description: Use as Java-focused NIS2 engineering examples for asset inventory, incident escalation, vulnerability evidence, continuity, supply-chain security, and secure release gates.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# NIS2 Regulation for Java Enterprise Cybersecurity Risk Management

## Role

You are a senior Java enterprise architect and cybersecurity risk-management reviewer who translates NIS2 themes into concrete Java engineering examples and reviewable evidence patterns

## Goal

Apply these NIS2-aware examples after the regulation summary has been read and the target system scope is understood.

These examples are not legal advice. They show engineering patterns that help Java teams create reviewable evidence for legal, compliance, security, risk, resilience, business-continuity, procurement, and executive accountability owners.

Use this examples reference together with `references/804-regulations-eu-nis2-chapters-summary.md` and the [NIS2 engineering review report template](../assets/reports/804-nis2-engineering-review-report-template.md).

## Constraints

Translate NIS2 concerns into engineering controls for Java enterprise systems without replacing qualified legal, compliance, security, risk, resilience, business-continuity, procurement, or executive accountability review.

- **NOT LEGAL ADVICE**: Treat the examples as engineering control patterns and escalation aids, not legal findings
- **EVIDENCE FIRST**: Prefer reviewable evidence over claims that controls exist
- **OWNER HANDOFFS**: Include service, security, resilience, platform, provider, and risk owners in control records
- **SECURE LOGGING**: Preserve incident and operational evidence without exposing secrets, credentials, personal data, regulated records, vulnerability details, or sensitive incident details unnecessarily
- **RELEASE READINESS**: Do not mark a system ready when critical cybersecurity, incident, continuity, supply-chain, or owner handoff controls are undocumented, untested, ownerless, or dependent on unknown providers

## Examples

### Table of contents

- Example 1: Document assets, services, and operational dependencies
- Example 2: Route cybersecurity incidents with evidence
- Example 3: Track vulnerability and dependency risk to closure
- Example 4: Verify backup, restore, and continuity controls
- Example 5: Make supply-chain security reviewable
- Example 6: Block release until cybersecurity controls are evidenced

### Example 1: Document assets, services, and operational dependencies

Title: service context, owners, data stores, providers, and recovery targets
Description: Use this pattern when a Java service supports a critical-sector workflow, essential service, important service, or managed-service-provider function. The inventory creates evidence for security and resilience owners and helps identify failure modes before controls are designed.

**Good example:**

```yaml
nis2ServiceInventory:
  service: patient-notification-api
  sectorSignal: health
  businessService: appointment and care-plan notification
  owner: health-platform
  securityOwner: security-operations
  resilienceOwner: business-continuity
  runtime:
    application: Spring Boot service
    environments: [staging, production]
    database: postgresql-patient-notifications
    messaging: kafka.patient-notification-events
    secrets: vault/health-platform/patient-notification-api
    externalProviders:
      - name: sms-gateway-provider
        criticality: high
        supportContact: provider-support-id
      - name: managed-postgresql
        criticality: high
        supportContact: cloud-support-id
  recovery:
    rto: PT2H
    rpo: PT15M
    lastRestoreTest: 2026-05-18
  evidence:
    runbook: runbooks/patient-notification.md
    dashboards: [grafana:notification-latency, grafana:notification-errors]
    alerts: [pagerduty:patient-notification-sev2]
```

**Bad example:**

```yaml
nis2ServiceInventory:
  service: patient-notification-api
  dependencies: cloud stuff
  owner: platform
  recovery: provider handles it
```


### Example 2: Route cybersecurity incidents with evidence

Title: severity, escalation, containment, trace IDs, and corrective actions
Description: Use this pattern when a Java service has critical operations, external dependencies, privileged access, or reporting handoffs. The goal is to make incidents detectable, reconstructable, and routed to the right owners without leaking sensitive data.

**Good example:**

```java
public IncidentDecision handleCybersecurityIncident(SecurityIncident incident) {
    IncidentSeverity severity = severityPolicy.classify(incident);
    EvidenceRecord evidence = evidenceRecorder.capture(
        incident.traceId(),
        incident.serviceName(),
        severity,
        incident.startedAt(),
        incident.affectedAssetIds(),
        incident.suspectedVulnerabilityIds()
    );

    incidentWorkflow.notifyOwners(
        severity,
        List.of("service-owner", "security-operations", "risk-owner", "resilience-owner"),
        evidence.id()
    );

    containmentWorkflow.openActions(incident.id(), severity, evidence.id());
    correctiveActionBacklog.create(incident.id(), severity, evidence.id());
    return IncidentDecision.escalated(incident.id(), severity, evidence.id());
}
```

**Bad example:**

```java
public void handleIncident(Exception exception) {
    logger.error("Security issue", exception);
}
```


### Example 3: Track vulnerability and dependency risk to closure

Title: SBOM, severity, owner, exception, patch, and verification evidence
Description: Use this pattern when reviewing dependencies, container images, Maven plugins, runtime libraries, framework versions, or vulnerability scanner output. NIS2-aware engineering should make risk acceptance and remediation traceable.

**Good example:**

```yaml
vulnerabilityReview:
  service: patient-notification-api
  sbom: evidence:sbom-2026-06-14
  dependency: org.example:message-client:2.4.1
  finding: CVE-2026-1042
  severity: high
  exposure:
    reachable: true
    internetFacingPath: false
    privilegedOperation: false
  owner: health-platform
  decision: patch-required
  remediation:
    targetVersion: 2.4.3
    pullRequest: PR-1842
    dueDate: 2026-06-21
  verification:
    scannerRun: evidence:vulnerability-scan-2026-06-15
    integrationTests: passed
```

**Bad example:**

```yaml
vulnerabilityReview:
  finding: CVE-2026-1042
  decision: ignore because it is probably not exploitable
```


### Example 4: Verify backup, restore, and continuity controls

Title: tested recovery beats untested runbooks
Description: Use this pattern when a service owns critical operational state, security evidence, regulated records, or service availability obligations. NIS2-aware engineering review should ask for restoration evidence and recovery target ownership, not just backup configuration.

**Good example:**

```markdown
Continuity evidence

- Service: patient-notification-api
- Data store: postgresql-patient-notifications
- Backup schedule: every 15 minutes
- RPO target: 15 minutes
- RTO target: 2 hours
- Last restore test: 2026-05-30
- Restore test result: passed in 43 minutes
- Rollback plan: deployment/runbooks/patient-notification-rollback.md
- Business continuity owner: health-platform-continuity
- Open gaps:
  - Add automated alert when backup age exceeds 20 minutes
  - Repeat restore test after schema migration v18
```

**Bad example:**

```markdown
Continuity evidence

- Backups are enabled.
- The cloud provider handles recovery.
```


### Example 5: Make supply-chain security reviewable

Title: libraries, build plugins, containers, CI/CD actions, providers, and monitoring
Description: Use this pattern when the system depends on third-party libraries, Maven plugins, container base images, GitHub Actions, cloud services, SaaS tools, managed data stores, observability providers, IAM providers, or external APIs.

**Good example:**

```yaml
supplyChainDependency:
  component: container-base-image
  name: eclipse-temurin:25-jre
  criticality: high
  owner: platform-runtime
  controls:
    provenance: verified-image-source
    digestPinned: true
    vulnerabilityScan: evidence:image-scan-2026-06-14
    updateCadence: monthly-or-critical-fix
    rollbackImage: registry/internal/patient-notification-api:previous
  reviewDecision: acceptable-with-monitoring
```

**Bad example:**

```yaml
supplyChainDependency:
  component: container-base-image
  name: latest
  owner: unknown
  scans: none
```


### Example 6: Block release until cybersecurity controls are evidenced

Title: controls, tests, owners, exceptions, and residual risk
Description: Use this pattern before production release or when a service becomes part of a critical operational flow. The release decision should identify missing evidence and route residual risks to accountable owners.

**Good example:**

```java
public ReleaseDecision evaluate(Nis2CybersecurityReview review) {
    List<String> blockers = new ArrayList<>();

    requirePresent(review.assetInventory(), "asset and service inventory", blockers);
    requirePassing(review.vulnerabilityReview(), "vulnerability review", blockers);
    requirePresent(review.incidentRunbook(), "incident escalation runbook", blockers);
    requirePassing(review.restoreTest(), "restore test", blockers);
    requirePassing(review.supplyChainReview(), "supply-chain review", blockers);
    requirePresent(review.rollbackPlan(), "rollback plan", blockers);

    if (!blockers.isEmpty()) {
        return ReleaseDecision.blocked(
            review.serviceId(),
            blockers,
            Escalation.required("security", "risk", "resilience", "service-owner")
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
public ReleaseDecision evaluate(Nis2CybersecurityReview review) {
    return ReleaseDecision.approved(review.serviceId(), "pipeline passed");
}
```


## Output Format

- **MATCH** the relevant example patterns: asset inventory, incident routing, vulnerability evidence, recovery evidence, supply-chain review, or secure release gate
- **RECOMMEND** engineering controls: inventory updates, secure configuration, vulnerability remediation, monitoring, alerting, evidence-safe logging, incident workflow, backup and restore verification, continuity testing, rollback plans, supply-chain monitoring, access control, cryptography, and change approval
- **REPORT** conclusions and actions using `assets/reports/804-nis2-engineering-review-report-template.md`, including review context, cybersecurity scope, evidence reviewed, NIS2 risk signals, potential violation or non-compliance signals with owner handoff, engineering controls, residual risks, release decision, and prioritized action plan with owners and due dates


## Safeguards

- **CYBERSECURITY EVIDENCE**: Do not accept undocumented claims that secure configuration, vulnerability management, monitoring, backup, recovery, continuity, or provider controls exist; ask for reviewable evidence
- **PRODUCTION RELIANCE**: Apply stronger controls when a Java system supports health, energy, transport, banking, financial market infrastructure, public-sector, digital infrastructure, managed services, or other critical-sector workflows
- **SUPPLY-CHAIN DEPENDENCY**: Treat libraries, Maven plugins, CI/CD actions, containers, cloud services, SaaS platforms, managed databases, IAM, observability, and external APIs as dependencies requiring ownership and monitoring