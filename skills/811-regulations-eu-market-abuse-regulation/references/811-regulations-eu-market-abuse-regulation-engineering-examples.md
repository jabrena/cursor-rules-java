---
name: 811-regulations-eu-market-abuse-regulation-engineering-examples
description: Use as Java-focused Market Abuse Regulation engineering examples for suspicious order and transaction monitoring, market-data lineage, insider-list workflows, disclosure workflows, alert explainability, model and rule provenance, reviewer decisions, false-positive handling, and compliance evidence handoff.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# EU Market Abuse Regulation for Java Enterprise Market Surveillance Controls

## Role

You are a senior Java enterprise architect and market-surveillance compliance reviewer who translates Market Abuse Regulation themes into concrete Java engineering examples and reviewable evidence patterns

## Goal

Apply these MAR-aware examples after the regulation summary has been read and the target trading, surveillance, disclosure, or investigation scope is understood.

These examples are not legal advice. They show engineering patterns that help Java teams create reviewable evidence for legal, compliance, market-surveillance, product, risk, operations, data, security, audit, and executive accountability owners.

Use this examples reference together with `811-regulations-eu-market-abuse-regulation-chapters-summary.md` and the [MAR engineering review report template](../assets/reports/811-market-abuse-regulation-engineering-review-report-template.md).

## Constraints

Translate Market Abuse Regulation concerns into engineering controls for Java enterprise systems without replacing qualified legal, compliance, market-surveillance, risk, product, operations, data, security, audit, or executive accountability review.

- **NOT LEGAL ADVICE**: Treat the examples as engineering control patterns and escalation aids, not legal findings
- **EVIDENCE FIRST**: Prefer reviewable evidence over claims that suspicious order or transaction monitoring, insider-list controls, disclosure controls, alert explainability, or model governance exists
- **OWNER HANDOFFS**: Include compliance, market-surveillance, legal, product, risk, operations, data, security, audit, and business owners in control records
- **SAFE EVIDENCE**: Preserve MAR evidence without exposing secrets, credentials, inside information, client data, personal data, business secrets, confidential trading strategies, model internals, or investigation-sensitive records
- **RELEASE READINESS**: Do not mark a system ready when critical surveillance, disclosure, insider-list, model provenance, reviewer-decision, or owner-handoff controls are undocumented, untested, ownerless, or dependent on unclear compliance interpretation

## Examples

### Table of contents

- Example 1: Design suspicious order and transaction monitoring with explainable alerts
- Example 2: Version surveillance rules and AI models before production use
- Example 3: Capture reviewer decisions and false-positive rationale
- Example 4: Maintain insider-list workflows with access evidence
- Example 5: Preserve inside-information disclosure and delay evidence
- Example 6: Protect market-data lineage for surveillance decisions

### Example 1: Design suspicious order and transaction monitoring with explainable alerts

Title: coverage, scenario definitions, rule versions, alert reasons, and STOR handoff
Description: Use this pattern when a Java service consumes orders, cancellations, amendments, executions, quotes, or market-data signals and creates surveillance alerts for potential insider dealing, unlawful disclosure, or market manipulation review. Engineering should provide traceable evidence for Article 16 arrangements without deciding whether a STOR is legally required.

**Good example:**

```java
public SurveillanceAlert evaluate(OrderEvent order, MarketSnapshot market) {
    ScenarioEvaluation evaluation = scenarios.evaluate(order, market);

    AlertExplanation explanation = AlertExplanation.builder()
        .scenarioId(evaluation.scenarioId())
        .scenarioVersion(evaluation.scenarioVersion())
        .ruleVersion(ruleRegistry.activeVersion())
        .marketDataLineage(market.lineageId())
        .triggeredFactors(evaluation.triggeredFactors())
        .thresholds(evaluation.thresholds())
        .build();

    SurveillanceAlert alert = alertRepository.save(SurveillanceAlert.open(
        order.orderId(),
        order.instrumentId(),
        order.tradingVenue(),
        explanation,
        clock.instant()
    ));

    auditLog.record(MarAuditEvent.alertCreated(alert.id(), explanation, order.correlationId()));
    return alert;
}
```

**Bad example:**

```java
public boolean suspicious(OrderEvent order) {
    return order.quantity() > 10000;
}
```


### Example 2: Version surveillance rules and AI models before production use

Title: training data lineage, approvals, rollback, thresholds, and reviewer-facing explanations
Description: Use this pattern when suspicious order or transaction monitoring uses rule engines, scoring models, GenAI summarization, or ML-assisted market-abuse detection. Evidence should make rule and model behavior reproducible for reviewers while protecting confidential model internals and sensitive trading data.

**Good example:**

```yaml
marSurveillanceModel:
  modelId: layering-spoofing-detector
  modelVersion: 2026.06.15
  purpose: detect potential layering and spoofing signals for analyst review
  trainingDataLineage: evidence:market-data-lineage-2026-q2
  featureSetVersion: features-surveillance-v8
  ruleFallbackVersion: rules-layering-v12
  explainability:
    reviewerSummary: enabled
    topFactors: [orderToTradeRatio, cancellationBurst, priceDistance, venueSpread]
  approvals:
    modelRisk: approved:evidence/model-risk-811-2026-06
    marketSurveillance: approved:evidence/surveillance-approval-811-2026-06
    compliance: approved:evidence/compliance-approval-811-2026-06
  releaseGate:
    shadowMode: passed
    falsePositiveReview: completed
    rollbackPlan: runbooks/rollback-layering-spoofing-detector.md
```

**Bad example:**

```yaml
marSurveillanceModel:
  modelId: abuse-detector
  version: latest
  trainingData: production
  approval: ask compliance if needed
```


### Example 3: Capture reviewer decisions and false-positive rationale

Title: triage state, escalation, STOR handoff, auditability, and retention
Description: Use this pattern when market-surveillance analysts review alerts. The system should preserve who reviewed what, what evidence was considered, why an alert was escalated or closed, and which owner accepted the decision. It should not force engineers to determine legal reportability.

**Good example:**

```java
public AlertDecision closeAsFalsePositive(CloseAlertCommand command) {
    accessPolicy.requireSurveillanceReviewer(command.reviewerId());
    SurveillanceAlert alert = alertRepository.requireOpen(command.alertId());

    FalsePositiveReason reason = falsePositiveCatalog.requireKnownReason(command.reasonCode());
    AlertDecision decision = AlertDecision.falsePositive(
        alert.id(),
        command.reviewerId(),
        reason,
        command.evidenceReferences(),
        clock.instant()
    );

    decisionRepository.save(decision);
    auditLog.record(MarAuditEvent.reviewerDecision(
        alert.id(),
        decision.id(),
        decision.status(),
        decision.evidenceReferences()
    ));

    return decision;
}
```

**Bad example:**

```java
public void closeAlert(String alertId) {
    jdbc.update("update alerts set status = 'CLOSED' where id = ?", alertId);
}
```


### Example 4: Maintain insider-list workflows with access evidence

Title: identity, reason, access timestamps, acknowledgement, update timing, and retention
Description: Use this pattern when a Java workflow grants people access to inside information or supports issuer insider lists. Engineering should preserve structured evidence aligned with Article 18 themes and escalate list scope, identity rules, and acknowledgement wording to qualified owners.

**Good example:**

```java
public InsiderListEntry addInsider(AddInsiderCommand command) {
    accessPolicy.requireInsideInformationOwner(command.requestedBy(), command.projectCode());
    InsideInformationProject project = projects.requireActive(command.projectCode());

    InsiderListEntry entry = InsiderListEntry.create(
        command.personId(),
        project.code(),
        command.reasonForAccess(),
        command.accessStartedAt(),
        clock.instant()
    );

    acknowledgements.requireSignedDutiesNotice(command.personId(), project.code());
    insiderLists.save(entry);
    auditLog.record(MarAuditEvent.insiderAdded(entry.id(), project.code(), command.requestedBy()));
    retentionPolicy.retain(entry.id(), RetentionPeriod.years(5));
    return entry;
}
```

**Bad example:**

```java
void grantDealRoom(String user) {
    permissions.add(user, "deal-room");
}
```


### Example 5: Preserve inside-information disclosure and delay evidence

Title: publication decisions, delay rationale, approvals, timestamps, and public access
Description: Use this pattern when Java systems support issuer disclosures, delayed disclosure records, publication workflows, or compliance evidence for inside information. Legal and compliance owners decide disclosure and delay questions; engineering preserves objective evidence.

**Good example:**

```yaml
insideInformationDisclosure:
  projectCode: acquisition-alpha
  disclosureOwner: issuer-disclosure-team
  legalOwner: legal-markets
  complianceOwner: mar-compliance
  decision:
    status: delayed-disclosure-approved
    decidedAt: 2026-06-27T08:30:00Z
    evidenceRef: evidence:disclosure-delay-decision-alpha
  publication:
    channel: regulated-information-service
    plannedAt: 2026-06-28T06:00:00Z
    auditEvent: mar_disclosure_publication_scheduled
  controls:
    insiderListLinked: true
    accessReviewCompleted: true
    publicationRollbackRunbook: runbooks/disclosure-publication-rollback.md
```

**Bad example:**

```yaml
disclosure:
  project: acquisition-alpha
  status: wait
  owner: team inbox
```


### Example 6: Protect market-data lineage for surveillance decisions

Title: feed provenance, transformations, replayability, schema compatibility, and gaps
Description: Use this pattern when order, transaction, quote, benchmark, or reference data feeds drive surveillance alerts or investigation evidence. Reviewers need to understand which data was available at decision time and whether gaps, corrections, or late events affected alert outcomes.

**Good example:**

```java
public MarketSnapshot loadSnapshot(AlertContext context) {
    MarketDataWindow window = marketDataRepository.window(
        context.instrumentId(),
        context.tradingVenue(),
        context.eventTime().minus(context.lookback()),
        context.eventTime()
    );

    lineageRepository.save(MarketDataLineage.record(
        context.correlationId(),
        window.feedVersions(),
        window.schemaVersions(),
        window.correctionsApplied(),
        window.missingIntervals(),
        clock.instant()
    ));

    return MarketSnapshot.from(window);
}
```

**Bad example:**

```java
MarketSnapshot snapshot(String symbol) {
    return cache.get(symbol);
}
```


## Output Format

- **EXPLAIN** which MAR engineering pattern applies and which owner must review legal or compliance questions
- **MAP** Java code, data, schema, event, model, rule, and release evidence to MAR topic areas using the chapters summary
- **RECOMMEND** concrete controls for suspicious order and transaction monitoring, insider-list workflows, disclosure workflows, alert explainability, model and rule provenance, reviewer decisions, false-positive handling, market-data lineage, observability, and owner handoff
- **AVOID** declaring insider dealing, unlawful disclosure, market manipulation, STOR reportability, or compliance status