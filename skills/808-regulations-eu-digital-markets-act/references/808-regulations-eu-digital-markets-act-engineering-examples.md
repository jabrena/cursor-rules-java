---
name: 808-regulations-eu-digital-markets-act-engineering-examples
description: Use as Java-focused Digital Markets Act engineering examples for interoperability interfaces, business-user data access, consent evidence, ranking audit signals, export workflows, anti-circumvention controls, and compliance evidence handoff.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# EU Digital Markets Act Regulation for Java Enterprise Gatekeeper Platform Controls

## Role

You are a senior Java enterprise architect and platform compliance reviewer who translates Digital Markets Act themes into concrete Java engineering examples and reviewable evidence patterns

## Goal

Apply these DMA-aware examples after the regulation summary has been read and the target platform scope is understood.

These examples are not legal advice. They show engineering patterns that help Java teams create reviewable evidence for legal, compliance, platform governance, product, privacy, security, risk, competition, and executive accountability owners.

Use this examples reference together with `808-regulations-eu-digital-markets-act-chapters-summary.md` and the [DMA engineering review report template](../assets/reports/808-eu-digital-markets-act-engineering-review-report-template.md).

## Constraints

Translate Digital Markets Act concerns into engineering controls for Java enterprise systems without replacing qualified legal, compliance, platform governance, product, privacy, security, risk, competition, or executive accountability review.

- **NOT LEGAL ADVICE**: Treat the examples as engineering control patterns and escalation aids, not legal findings
- **EVIDENCE FIRST**: Prefer reviewable evidence over claims that interoperability, data access, consent, ranking fairness, export, or anti-circumvention controls exist
- **OWNER HANDOFFS**: Include platform, product, privacy, security, data, compliance, business-user, and risk owners in control records
- **SAFE EVIDENCE**: Preserve DMA evidence without exposing secrets, credentials, personal data, business secrets, confidential ranking logic, or unnecessary platform telemetry
- **RELEASE READINESS**: Do not mark a system ready when critical DMA controls are undocumented, untested, ownerless, inaccessible to business users, or dependent on unclear compliance interpretation

## Examples

### Table of contents

- Example 1: Design interoperability interfaces with reviewable safeguards
- Example 2: Expose business-user data access with consent and lineage
- Example 3: Preserve consent and preference evidence before combining data
- Example 4: Audit ranking and self-preferencing signals
- Example 5: Make business-user exports operable and observable
- Example 6: Block releases that undermine DMA controls

### Example 1: Design interoperability interfaces with reviewable safeguards

Title: API contracts, reference offer evidence, compatibility, security, and request handling
Description: Use this pattern when a Java platform exposes or consumes interoperability interfaces for messaging, operating-system features, hardware or software capabilities, app-store access, or services provided together with a core platform service. The evidence should show that interoperability is effective, secure, observable, and not degraded for third parties exercising DMA-related choices.

**Good example:**

```yaml
dmaInteroperabilityInterface:
  service: messaging-bridge-api
  corePlatformServiceSignal: number-independent interpersonal communications service
  interfaceOwner: platform-interoperability
  complianceOwner: dma-compliance
  referenceOffer: evidence:reference-offer-messaging-bridge-v3
  contract:
    openapi: contracts/messaging-bridge-v3.yaml
    supportedFunctions:
      - one-to-one text messages
      - attachments metadata
      - delivery status callbacks
    security:
      authentication: mTLS and signed provider requests
      authorization: provider-scoped OAuth2 client credentials
      personalDataMinimization: only data strictly necessary for interoperability
  requestWorkflow:
    intakeQueue: platform-interoperability-requests
    responseSla: P30D
    decisionEvidence: evidence:provider-onboarding-decisions
  tests:
    compatibility: passed
    securityRegression: passed
    degradationCheck: passed
  observability:
    metrics: [interop.requests.accepted, interop.requests.rejected, interop.latency.p95]
    auditLog: dma_interop_access_audit
```

**Bad example:**

```yaml
dmaInteroperabilityInterface:
  service: messaging-bridge-api
  access: partners can email us
  security: internal only
  tests: none
  owner: platform
```


### Example 2: Expose business-user data access with consent and lineage

Title: continuous access, export jobs, personal-data boundaries, and audit evidence
Description: Use this pattern when a marketplace, app store, advertising service, search service, or platform portal must provide business users or authorised third parties with access to data generated in the context of their use of the platform. Personal-data access requires opt-in and privacy-owner review.

**Good example:**

```java
public BusinessUserDataExport createExport(BusinessUserDataRequest request) {
    accessPolicy.requireBusinessUserScope(request.businessUserId(), request.requestedBy());
    dataCatalog.requireDocumentedLineage(request.datasetId(), "DMA business-user data access");

    if (request.includesPersonalData()) {
        consentLedger.requireOptIn(
            request.endUserId(),
            request.businessUserId(),
            ConsentPurpose.BUSINESS_USER_DATA_SHARING
        );
    }

    ExportJob job = exportJobs.scheduleContinuousExport(
        request.businessUserId(),
        request.datasetId(),
        request.format(),
        request.authorizedThirdPartyId()
    );

    auditLog.record(new DmaDataAccessAuditEvent(
        request.businessUserId(),
        request.datasetId(),
        job.id(),
        request.includesPersonalData(),
        request.requestedBy(),
        clock.instant()
    ));

    return BusinessUserDataExport.accepted(job.id(), job.statusUrl());
}
```

**Bad example:**

```java
public File exportData(String sellerId) {
    return database.dumpSellerRows(sellerId);
}
```


### Example 3: Preserve consent and preference evidence before combining data

Title: specific choice, withdrawal, retry limits, purpose binding, and neutral UI
Description: Use this pattern when a platform combines, cross-uses, signs in, or shares personal data across core platform services, advertising systems, identity services, or third-party services. Consent interpretation belongs to privacy and legal owners; engineering should preserve objective evidence.

**Good example:**

```java
public DataCombinationDecision evaluate(DataCombinationRequest request) {
    ConsentRecord consent = consentLedger.findCurrent(
        request.endUserId(),
        request.sourceService(),
        request.targetService(),
        request.purpose()
    );

    if (!consent.isSpecificOptIn() || consent.withdrawn()) {
        consentPromptPolicy.recordSuppressedPrompt(
            request.endUserId(),
            request.purpose(),
            "No valid opt-in or consent withdrawn"
        );
        return DataCombinationDecision.blocked(
            request.requestId(),
            Escalation.required("privacy", "legal", "product")
        );
    }

    if (consentPromptPolicy.promptedWithinOneYear(request.endUserId(), request.purpose())) {
        return DataCombinationDecision.blocked(
            request.requestId(),
            Escalation.required("privacy", "legal")
        );
    }

    preferenceAudit.recordDataCombination(request, consent.id(), clock.instant());
    return DataCombinationDecision.allowedWithEvidence(request.requestId(), consent.id());
}
```

**Bad example:**

```java
public boolean canCombineData(User user) {
    return user.acceptedTerms();
}
```


### Example 4: Audit ranking and self-preferencing signals

Title: ranking inputs, own-service flags, experiments, sponsored placement, and fairness checks
Description: Use this pattern when a Java service ranks search results, marketplace listings, app-store results, recommendation modules, advertising inventory, or platform-owned services. The review should make own-service treatment and third-party conditions inspectable without exposing confidential ranking internals unnecessarily.

**Good example:**

```java
public RankedResults rank(SearchRequest request) {
    RankingContext context = rankingContextFactory.create(request);
    RankedResults results = rankingModel.rank(context);

    rankingAudit.record(new RankingAuditEvent(
        request.requestId(),
        request.corePlatformService(),
        context.experimentId(),
        context.rankingPolicyVersion(),
        results.ownServiceResultCount(),
        results.thirdPartyResultCount(),
        results.sponsoredPlacementCount(),
        fairnessChecks.evaluate(results).status(),
        clock.instant()
    ));

    selfPreferencingPolicy.requireReviewWhenOwnServiceBoostDetected(
        request.corePlatformService(),
        context.rankingPolicyVersion(),
        results
    );

    return results;
}
```

**Bad example:**

```java
public RankedResults rank(SearchRequest request) {
    return rankingModel.rank(request);
}
```


### Example 5: Make business-user exports operable and observable

Title: request lifecycle, format, errors, delivery, retention, and support handoff
Description: Use this pattern when business users need effective exports or continuous access to platform data. The workflow should show request status, delivery, error handling, retention, and support escalation without relying on manual database extracts.

**Good example:**

```yaml
businessUserExportWorkflow:
  service: seller-analytics-export-api
  owner: marketplace-data-platform
  complianceOwner: dma-compliance
  supportedFormats: [jsonl, parquet, csv]
  requestLifecycle:
    created: audit event emitted
    authorized: business-user scope verified
    prepared: data lineage version attached
    delivered: signed download URL or continuous feed endpoint
    expired: export artifacts deleted after P14D
  errors:
    retryableFailures: queue with backoff and alert after 3 attempts
    authorizationFailures: audit and notify business-user support
    schemaFailures: block release until contract fixed
  observability:
    metrics: [export.created, export.completed, export.failed, export.latency.p95]
    dashboards: [grafana:seller-export-health]
  evidence:
    runbook: runbooks/dma-business-user-export.md
    contractTests: passed
```

**Bad example:**

```yaml
businessUserExportWorkflow:
  request: support ticket
  format: whatever SQL returns
  deletion: manual
  observability: none
```


### Example 6: Block releases that undermine DMA controls

Title: neutral choices, no degradation, service-boundary integrity, and evidence handoff
Description: Use this pattern before production release of changes to ranking, consent flows, platform defaults, data access APIs, interoperability interfaces, access terms, exports, or service boundaries. The release gate should identify anti-circumvention risk and route unresolved questions to qualified owners.

**Good example:**

```java
public ReleaseDecision evaluate(DmaEngineeringReview review) {
    List<String> blockers = new ArrayList<>();

    requirePresent(review.corePlatformServiceScope(), "core platform service scope evidence", blockers);
    requirePassing(review.interoperabilityContractTests(), "interoperability contract tests", blockers);
    requirePresent(review.dataAccessApiEvidence(), "business-user data access evidence", blockers);
    requirePassing(review.consentPreferenceAudit(), "consent and preference audit evidence", blockers);
    requirePassing(review.rankingFairnessReview(), "ranking and self-preferencing review", blockers);
    requirePresent(review.exportWorkflowRunbook(), "business-user export workflow", blockers);
    requirePassing(review.neutralChoiceReview(), "neutral choice and no-degradation review", blockers);
    requirePresent(review.complianceHandoff(), "qualified owner handoff", blockers);

    if (!blockers.isEmpty()) {
        return ReleaseDecision.blocked(
            review.serviceId(),
            blockers,
            Escalation.required("legal", "compliance", "platform-governance", "product", "privacy", "security", "risk")
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
public ReleaseDecision evaluate(DmaEngineeringReview review) {
    return ReleaseDecision.approved(review.serviceId(), "tests passed");
}
```


## Output Format

- **MATCH** the relevant example patterns: interoperability interface evidence, business-user data access API, consent and preference evidence, ranking audit signals, business-user export workflow, or anti-circumvention release gate
- **RECOMMEND** engineering controls: interoperability APIs, reference offers, compatibility tests, data access APIs, export workflows, consent records, neutral preference UX, ranking audit events, self-preferencing review, advertiser or publisher metrics, access control, observability, evidence retention, documentation, change approval, and compliance handoff
- **REPORT** conclusions and actions using `../assets/reports/808-eu-digital-markets-act-engineering-review-report-template.md`, including review context, platform scope, evidence reviewed, DMA risk signals, potential violation or non-compliance signals with owner handoff, engineering controls, residual risks, release decision, and prioritized action plan with owners and due dates


## Safeguards

- **DMA EVIDENCE**: Do not accept undocumented claims that interoperability, business-user access, consent, ranking fairness, export, advertising transparency, or anti-circumvention controls exist; ask for reviewable evidence
- **PLATFORM RELIANCE**: Apply stronger controls when a Java system affects marketplaces, app stores, search, advertising, messaging, identity, browser or operating-system defaults, cloud platforms, or business-user platform access
- **OWNER ESCALATION**: Route gatekeeper designation, core platform service classification, obligation applicability, consent interpretation, self-preferencing conclusions, fair access terms, and regulatory interpretation to qualified owners