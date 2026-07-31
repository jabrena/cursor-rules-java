---
name: 807-regulations-eu-digital-services-act-engineering-examples
description: Use as Java-focused Digital Services Act engineering examples for content decision audit logs, moderation workflow state, notice tracking, recommender explanation, ad transparency, user controls, appeals, systemic-risk evidence, auditor and researcher access, incident escalation, and privacy-safe observability.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# EU Digital Services Act Regulation for Java Online Platform Engineering

## Role

You are a senior Java enterprise architect and online platform governance reviewer who translates Digital Services Act themes into concrete Java engineering examples and reviewable evidence patterns

## Goal

Apply these Digital Services Act-aware examples after the regulation summary has been read and the target system scope is understood.

These examples are not legal advice. They show engineering patterns that help Java teams create reviewable evidence for legal, compliance, trust-and-safety, privacy, security, product, risk, audit, research-access, and executive accountability owners.

Use this examples reference together with `references/807-regulations-eu-digital-services-act-chapters-summary.md` and the [Digital Services Act engineering review report template](../assets/reports/807-eu-digital-services-act-engineering-review-report-template.md).

## Constraints

Translate Digital Services Act concerns into engineering controls for Java enterprise systems without replacing qualified legal, compliance, trust-and-safety, privacy, security, product, risk, audit, research-access, or executive accountability review.

- **NOT LEGAL ADVICE**: Treat the examples as engineering control patterns and escalation aids, not legal findings
- **EVIDENCE FIRST**: Prefer reviewable evidence over claims that platform controls exist
- **OWNER HANDOFFS**: Include platform, product, legal, compliance, trust-and-safety, privacy, security, audit, research-access, and risk owners in control records
- **PRIVACY-SAFE LOGGING**: Preserve moderation, ranking, advertising, complaint, audit, and incident evidence without exposing secrets, credentials, personal data, illegal-content details, sensitive reports, researcher datasets, trade secrets, or confidential model details unnecessarily
- **RELEASE READINESS**: Do not mark a system ready when critical DSA scope, moderation, notice, transparency, recommender, advertising, complaint, appeal, risk, audit, researcher access, or owner handoff controls are undocumented, untested, ownerless, or dependent on unknown providers

## Examples

### Table of contents

- Example 1: Document DSA service scope and owners
- Example 2: Record content decisions with moderation workflow state
- Example 3: Track notice intake and response end to end
- Example 4: Explain recommender and ranking behavior with user controls
- Example 5: Attach advertising transparency metadata
- Example 6: Preserve complaint and appeal workflow state
- Example 7: Prepare VLOP or VLOSE systemic-risk, audit, and researcher-access evidence
- Example 8: Keep observability useful and privacy-safe

### Example 1: Document DSA service scope and owners

Title: intermediary, hosting, platform, marketplace, recommender, ad, and VLOP/VLOSE signals
Description: Use this pattern when a Java system stores or disseminates recipient-provided information, ranks platform content, supports marketplace transactions, delivers ads, handles complaints, or produces transparency evidence. The inventory does not decide legal classification; it creates evidence for qualified owner review.

**Good example:**

```yaml
dsaServiceInventory:
  service: marketplace-content-api
  businessCapability: user listings, product search, checkout, and seller support
  possibleScopeSignals:
    intermediaryService: true
    hostingService: true
    onlinePlatform: owner-review-required
    onlineMarketplace: owner-review-required
    onlineSearchEngine: false
    recommenderSystem: product-ranking-v2
    advertisingWorkflow: promoted-listings
    vlopOrVlose: active-recipient-count-review-required
  owners:
    productOwner: marketplace-product
    technicalOwner: marketplace-platform
    legalOwner: legal-platform-governance
    trustSafetyOwner: trust-and-safety
    privacyOwner: privacy-engineering
    securityOwner: security-operations
  evidence:
    termsVersion: terms-2026-06
    moderationPolicy: policy/content-v7
    transparencyReportJob: jobs/dsa-transparency-report
    recommenderExplanation: docs/ranking-explanation-v2
    adMetadataSchema: schemas/ad-transparency-v1
```

**Bad example:**

```yaml
dsaServiceInventory:
  service: marketplace-content-api
  scope: probably just ecommerce
  owner: platform
  evidence: none
```


### Example 2: Record content decisions with moderation workflow state

Title: policy, authority order, statement of reasons, user notice, appeal, and retention
Description: Use this pattern when a Java service removes, disables, demotes, demonetises, suspends, or otherwise restricts content, listings, accounts, ads, or marketplace offers. The audit record should be reconstructable without storing unnecessary illegal-content details.

**Good example:**

```java
ContentDecision decision = contentDecisionRecorder.record(ContentDecisionRequest.builder()
    .contentId(content.id())
    .recipientId(content.ownerId())
    .decisionType(DecisionType.VISIBILITY_RESTRICTED)
    .source(DecisionSource.NOTICE_AND_ACTION)
    .policyReference("marketplace-terms-v7:counterfeit-goods")
    .officialOrderReference(orderReference.orElse(null))
    .statementOfReasonsId(statementOfReasons.id())
    .moderationWorkflowState("AWAITING_USER_APPEAL_WINDOW")
    .appealAvailable(true)
    .notifiedRecipientAt(clock.instant())
    .evidenceReference(evidenceVault.storeRedacted(content, notice))
    .owner("trust-and-safety")
    .build());
```

**Bad example:**

```java
moderationRepository.save(new ModerationEvent(contentId, "removed"));
```


### Example 3: Track notice intake and response end to end

Title: validation, triage, trusted flagger handling, response, misuse protection, and deadlines
Description: Use this pattern for hosting or platform services with user or authority notices. The workflow should show who submitted the notice, what was actionable, how it was assessed, what decision was taken, and how the notifier and affected recipient were informed.

**Good example:**

```java
NoticeCase noticeCase = noticeWorkflow.open(NoticeIntake.builder()
    .noticeId(command.noticeId())
    .submitterType(command.trustedFlagger() ? SubmitterType.TRUSTED_FLAGGER : SubmitterType.USER)
    .contentLocator(command.contentUrl())
    .legalOrPolicyBasis(command.claimedBasis())
    .contactChannel(command.contactEmail())
    .receivedAt(clock.instant())
    .build());

noticeWorkflow.route(noticeCase.id(), Route.builder()
    .priority(command.trustedFlagger() ? Priority.HIGH : Priority.NORMAL)
    .reviewQueue("trust-safety-marketplace")
    .deduplicationKey(noticeCase.deduplicationKey())
    .misuseCheckRequired(true)
    .build());
```

**Bad example:**

```java
emailSender.forward("support@example.com", request.body());
```


### Example 4: Explain recommender and ranking behavior with user controls

Title: main parameters, prominence factors, alternatives, and evidence snapshots
Description: Use this pattern when Java services rank feeds, search results, marketplace listings, offers, content, or ads. The evidence should help product and legal owners review user-facing explanations and control behavior without exposing confidential implementation details.

**Good example:**

```yaml
rankingExplanation:
  recommenderId: product-ranking-v2
  surface: storefront-search-results
  termsReference: terms-2026-06#ranking
  mainParameters:
    - text relevance
    - product availability
    - delivery promise
    - price and promotion fit
    - shopper-selected sort option
  userControls:
    sortOptions: [relevance, price_low_to_high, newest]
    personalizationToggle: available
    profilingFreeAlternative: owner-review-required-if-vlop-or-vlose
  evidence:
    explanationSnapshot: evidence:ranking-explanation-2026-06-14
    experimentId: ranker-exp-421
    owner: search-platform
```

**Bad example:**

```yaml
rankingExplanation:
  recommenderId: product-ranking-v2
  explanation: algorithm chooses best products
```


### Example 5: Attach advertising transparency metadata

Title: ad labels, payer, beneficiary, targeting parameters, repository evidence, and sensitive-profiling guardrails
Description: Use this pattern for promoted listings, sponsored search results, display ads, or commercial communications on online platforms. The metadata should be available to render user-facing labels and support transparency reporting.

**Good example:**

```yaml
adTransparency:
  adId: ad-2026-0614-8821
  surface: product-search-results
  label: Sponsored
  advertiser: Example Seller Ltd
  payer: Example Seller Ltd
  beneficiary: Example Seller Ltd
  mainTargetingParameters:
    - search query category
    - delivery region
    - product category
  prohibitedProfilingCheck:
    specialCategoryProfiling: not-used
    minorProfiling: not-used
    evidence: evidence:ad-policy-check-2026-06-14
  repositoryRecord: owner-review-required-if-vlop-or-vlose
  retentionPolicy: dsa-ad-transparency-retention-v1
```

**Bad example:**

```yaml
adTransparency:
  adId: ad-2026-0614-8821
  label: paid placement
  targeting: optimized
```


### Example 6: Preserve complaint and appeal workflow state

Title: redress, reversals, reinstatement, dispute settlement, and notifications
Description: Use this pattern when recipients can challenge moderation decisions or marketplace restrictions. The workflow should record each decision, user submission, reviewer action, outcome, and restoration step.

**Good example:**

```java
AppealDecision appealDecision = appealWorkflow.decide(AppealReview.builder()
    .appealId(appeal.id())
    .contentDecisionId(appeal.contentDecisionId())
    .reviewerGroup("trust-and-safety-appeals")
    .outcome(AppealOutcome.RESTRICTION_REVERSED)
    .reasonCode("POLICY_EXCEPTION_CONFIRMED")
    .restorationAction(RestorationAction.RESTORE_VISIBILITY)
    .recipientNotificationTemplate("appeal-reversed-v2")
    .outOfCourtDisputeInfoIncluded(true)
    .decidedAt(clock.instant())
    .build());
```

**Bad example:**

```java
appeal.status = "done";
appealRepository.save(appeal);
```


### Example 7: Prepare VLOP or VLOSE systemic-risk, audit, and researcher-access evidence

Title: risk assessment, mitigation, independent audit, data access, and confidentiality controls
Description: Use this pattern only where VLOP or VLOSE scope may apply or qualified owners request readiness evidence. The system should make risk, audit, and researcher-access data available through governed interfaces, not ad hoc production database access.

**Good example:**

```yaml
systemicRiskEvidence:
  service: marketplace-platform
  activeRecipientMeasurement: evidence:monthly-active-recipients-2026-06
  vlopOrVloseStatus: owner-review-required
  riskAssessment:
    assessmentId: dsa-risk-2026-h1
    assessedRisks:
      - dissemination of illegal products
      - recommender amplification of harmful listings
      - consumer protection and minor protection impacts
    mitigations:
      - trusted flagger priority workflow
      - ranking safety demotion rules
      - ad sensitive-profiling guardrail
  audit:
    independentAuditPlan: evidence:audit-plan-2026
    implementationEvidence: evidence:mitigation-tracker
  dataAccess:
    authorityExportApi: governed-readonly-api
    vettedResearcherAccess: privacy-preserving-dataset-workspace
    controls: [approval, minimization, pseudonymization, rate-limit, access-log, revocation]
```

**Bad example:**

```yaml
systemicRiskEvidence:
  status: big platform maybe
  dataAccess: ask DBA for a database dump
```


### Example 8: Keep observability useful and privacy-safe

Title: moderation evidence, incident escalation, redaction, owner routing, and reconstructable traces
Description: Use this pattern when moderation, advertising, ranking, complaints, or transparency pipelines need operational monitoring. Evidence should support incident response and governance review without spreading sensitive content or personal data through logs.

**Good example:**

```java
PlatformIncident incident = incidentEscalation.raise(PlatformIncidentRequest.builder()
    .incidentType(PlatformIncidentType.MODERATION_PIPELINE_FAILURE)
    .severity(severityPolicy.classify(alert))
    .affectedWorkflow("notice-and-action")
    .evidenceIds(List.of(alert.evidenceId(), workflowMetrics.snapshotId()))
    .redactionPolicy("dsa-observability-redaction-v1")
    .ownerGroups(List.of("trust-and-safety", "security-operations", "legal-platform-governance"))
    .build());

logger.info("DSA workflow incident escalated incidentId={} workflow={} severity={}",
    incident.id(),
    incident.affectedWorkflow(),
    incident.severity());
```

**Bad example:**

```java
logger.error("Moderation failed for content {}", fullUserContent);
```


## Output Format

- **MATCH** the relevant example patterns: DSA scope inventory, content decision audit log, notice tracking, recommender explanation, ad transparency, complaint and appeal workflow, systemic-risk evidence, auditor or researcher access, incident escalation, or privacy-safe observability
- **RECOMMEND** engineering controls: scope inventory, moderation workflow state, statement-of-reasons records, notice intake and response tracking, trusted flagger routing, misuse protection, recommender and ranking explanation evidence, user controls, ad transparency metadata, complaint and appeal workflows, trader traceability, transparency reporting, minor-protection controls, privacy-safe observability, incident escalation, risk assessment, independent audit, and governed data access for auditors or researchers where applicable
- **REPORT** conclusions and actions using `assets/reports/807-eu-digital-services-act-engineering-review-report-template.md`, including review context, platform scope, evidence reviewed, Digital Services Act risk signals, potential violation or non-compliance signals with official-source links, engineering controls, residual risks, release decision, and prioritized action plan with owners and due dates


## Safeguards

- **PLATFORM EVIDENCE**: Do not accept undocumented claims that moderation, notice handling, recommender explanations, advertising transparency, complaint handling, transparency reporting, audit, researcher access, or systemic-risk controls exist; ask for reviewable evidence
- **CLASSIFICATION HANDOFF**: Do not decide intermediary service, online platform, marketplace, online search engine, VLOP, VLOSE, illegal-content, systemic-risk, advertising, or recommender legal interpretation; route those decisions to qualified owners
- **SENSITIVE CONTENT**: Do not copy illegal content, personal data, secrets, credentials, private reports, confidential datasets, or trade-secret model details into findings; reference redacted evidence IDs instead