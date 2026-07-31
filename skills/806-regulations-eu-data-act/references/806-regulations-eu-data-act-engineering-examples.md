---
name: 806-regulations-eu-data-act-engineering-examples
description: Use as Java-focused EU Data Act engineering examples for data inventory, access authorization, portability APIs, export formats, metadata, audit logs, cloud switching, non-personal data safeguards, trade-secret handoffs, and data-sharing request workflows.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# EU Data Act Regulation for Java Enterprise Data Access and Portability Engineering

## Role

You are a senior Java enterprise architect and data-governance engineering reviewer who translates EU Data Act themes into concrete Java engineering examples and reviewable evidence patterns

## Goal

Apply these EU Data Act-aware examples after the regulation summary has been read and the target system scope is understood.

These examples are not legal advice. They show engineering patterns that help Java teams create reviewable evidence for legal, compliance, privacy, data governance, security, product, procurement, cloud, risk, provider, and business owners.

Use this examples reference together with `references/806-regulations-eu-data-act-chapters-summary.md` and the [EU Data Act engineering review report template](../assets/reports/806-eu-data-act-engineering-review-report-template.md).

## Constraints

Translate EU Data Act concerns into engineering controls for Java enterprise systems without replacing qualified legal, compliance, privacy, data governance, security, product, procurement, cloud, risk, provider, or business review.

- **NOT LEGAL ADVICE**: Treat the examples as engineering control patterns and escalation aids, not legal findings
- **EVIDENCE FIRST**: Prefer reviewable evidence over claims that access, portability, interoperability, or switching controls exist
- **OWNER HANDOFFS**: Include data, product, privacy, security, legal, compliance, platform, cloud, provider, and business owners in control records
- **SECURE LOGGING**: Preserve access-request and portability evidence without exposing secrets, credentials, personal data, trade secrets, commercially sensitive records, or sensitive public-sector request details unnecessarily
- **RELEASE READINESS**: Do not mark a system ready when critical data inventory, access authorization, export, interoperability, cloud-switching, trade-secret, or owner handoff controls are undocumented, untested, ownerless, or dependent on unknown providers

## Examples

### Table of contents

- Example 1: Document data inventory and Data Act role signals
- Example 2: Authorize data access requests with minimal verification
- Example 3: Expose portable, machine-readable data exports
- Example 4: Make interoperability and metadata reviewable
- Example 5: Prepare cloud switching and exportable data evidence
- Example 6: Safeguard non-personal data during access and transfer
- Example 7: Route trade-secret and sensitive-data decisions to owners
- Example 8: Block release until Data Act engineering controls are evidenced

### Example 1: Document data inventory and Data Act role signals

Title: datasets, metadata, products, services, recipients, owners, and handoffs
Description: Use this pattern when a Java system exposes or generates data through APIs, Kafka topics, exports, data spaces, SaaS features, connected-product integrations, or cloud services. The inventory creates evidence for Data Act scoping without making legal role determinations.

**Good example:**

```yaml
dataActInventory:
  service: connected-fleet-telemetry-api
  possibleRoleSignals:
    dataHolder: requires legal confirmation
    user: fleet-customer
    dataRecipient: repair-network-provider
    dataProcessingServiceProvider: cloud-platform-team
  datasets:
    - id: vehicle-usage-events
      dataTypes: [product-data, related-service-data, metadata]
      containsPersonalData: possible
      containsNonPersonalData: yes
      containsTradeSecrets: possible
      owner: fleet-data-governance
      api: /v1/data-access/vehicle-usage
      exportFormats: [application/json, text/csv]
      metadataReference: catalog:data-products/vehicle-usage-events
  owners:
    productOwner: fleet-product
    dataOwner: fleet-data-governance
    privacyOwner: privacy-office
    securityOwner: security-architecture
    legalOwner: legal-data-sharing
  openHandoffs:
    - confirm data holder status
    - confirm user entitlement model
    - classify trade secret fields
    - approve third-party recipient conditions
```

**Bad example:**

```yaml
dataActInventory:
  service: telemetry-api
  data: lots of events
  owner: platform
  compliance: handled by terms
```


### Example 2: Authorize data access requests with minimal verification

Title: entitlement, purpose, recipient role, refusal, suspension, and audit evidence
Description: Use this pattern when users or user-authorised third parties can request product data, related service data, exportable data, or metadata. The workflow records the engineering facts needed for qualified owner review without deciding legal entitlement in code.

**Good example:**

```java
public DataAccessDecision decide(DataAccessRequest request) {
    AccessSubject subject = subjectVerifier.verifyMinimumNecessary(request.subject());
    DatasetScope scope = catalog.resolve(request.datasetId());
    EntitlementResult entitlement = entitlementPolicy.evaluate(subject, scope, request.requestedPurpose());

    if (entitlement.requiresOwnerReview()) {
        return DataAccessDecision.holdForReview(
            request.id(),
            Escalation.required("legal", "data-governance", "privacy", "security"),
            entitlement.reason()
        );
    }

    if (!entitlement.allowed()) {
        auditLog.recordRefusal(request.id(), subject.id(), scope.id(), entitlement.reason());
        return DataAccessDecision.refused(request.id(), entitlement.reason());
    }

    auditLog.recordApproval(
        request.id(),
        subject.id(),
        scope.id(),
        request.requestedPurpose(),
        request.recipientRole()
    );
    return DataAccessDecision.approved(request.id(), scope.exportProfile());
}
```

**Bad example:**

```java
public boolean canExport(String userId) {
    return userRepository.exists(userId);
}
```


### Example 3: Expose portable, machine-readable data exports

Title: format, metadata, schema version, bulk export, and API quality of service
Description: Use this pattern when a Java service must provide user access, third-party sharing, or cloud-customer export workflows. Export behavior should be explicit, testable, documented, and aligned with metadata and schema records.

**Good example:**

```java
public DataExportPackage exportData(ApprovedDataAccess access) {
    ExportProfile profile = access.exportProfile();
    SchemaDescriptor schema = schemaRegistry.current(profile.schemaId());

    DataExportPackage export = exporter.create(
        access.datasetId(),
        profile.format(),
        schema.version(),
        ExportMetadata.of(
            schema.uri(),
            profile.qualityOfService(),
            profile.retentionPolicy(),
            profile.useRestrictions()
        )
    );

    auditLog.recordExportCreated(access.requestId(), export.id(), export.format(), schema.version());
    return export;
}
```

**Bad example:**

```java
public byte[] exportData(String datasetId) {
    return database.dump(datasetId).getBytes();
}
```


### Example 4: Make interoperability and metadata reviewable

Title: schemas, vocabularies, API terms, quality of service, and data-space readiness
Description: Use this pattern when data is shared through REST APIs, Kafka topics, object storage, data spaces, or smart-contract-enabled workflows. Interoperability evidence should describe how recipients discover, access, interpret, and use data.

**Good example:**

```yaml
interoperabilityEvidence:
  dataset: vehicle-usage-events
  contentDescription: connected-product usage telemetry
  useRestrictions: contract:vehicle-data-sharing-v3
  license: data-access-agreement-v3
  collectionMethodology: edge telemetry events normalized by ingestion-service
  dataQuality: dashboard:data-quality/vehicle-usage-events
  structures:
    schema: schema-registry:vehicle-usage-events:4
    vocabulary: catalog:vocabularies/fleet-telemetry
    taxonomy: catalog:taxonomies/product-events
  access:
    api: /v1/data-access/vehicle-usage
    bulkDownload: supported
    realtimeAccess: supported-where-technically-feasible
    qos: api-qos:data-access-standard
  metadataFormat: application/json
  audit: audit:data-access-export-log
```

**Bad example:**

```yaml
interoperabilityEvidence:
  dataset: telemetry
  format: json
  docs: ask the platform team
```


### Example 5: Prepare cloud switching and exportable data evidence

Title: exit strategy, export register, continuity, secure transfer, and erasure
Description: Use this pattern when a Java platform provides or consumes data processing services, cloud-hosted SaaS capabilities, managed databases, object storage, event brokers, or platform services. The goal is to make switching behavior testable and owner-approved before a customer asks for it.

**Good example:**

```yaml
cloudSwitchingPlan:
  service: analytics-saas-platform
  sourceProviderOwner: cloud-platform
  exportableData:
    - customer-events
    - dashboard-configurations
    - user-defined-alert-rules
    - api-client-metadata
  excludedData:
    - provider-internal-observability-rules
    - proprietary-routing-heuristics
  exportFormats:
    customer-events: parquet
    dashboard-configurations: application/json
    alert-rules: application/json
  switchingSupport:
    documentation: runbooks/customer-exit.md
    maximumNoticePeriod: P2M
    defaultTransitionPeriod: P30D
    retrievalPeriod: P30D
    secureTransfer: mTLS plus customer-managed destination
    continuityControls: dual-write validation during transition
    erasureEvidence: audit:customer-export-erasure
  openHandoffs:
    - legal review of contract terms
    - cloud owner review of exportable data exclusions
    - security review of transfer controls
```

**Bad example:**

```yaml
cloudSwitchingPlan:
  service: analytics
  exports: database backup
  switching: professional services will help
```


### Example 6: Safeguard non-personal data during access and transfer

Title: classification, jurisdiction, minimum data, customer notification, and legal handoff
Description: Use this pattern for cloud services, data processing services, and non-personal datasets held in the Union. Engineering systems should make international access requests visible and bounded while routing legal interpretation to qualified owners.

**Good example:**

```java
public InternationalAccessDecision evaluate(InternationalAccessRequest request) {
    DatasetClassification classification = classifier.classify(request.datasetId());
    if (!classification.containsNonPersonalDataHeldInUnion()) {
        return InternationalAccessDecision.notInScope(request.id());
    }

    LegalReviewTicket ticket = legalWorkflow.openReview(
        request.id(),
        classification.datasetId(),
        request.requestingAuthority(),
        request.requestedFields()
    );

    auditLog.recordInternationalAccessReview(request.id(), ticket.id(), classification.residency());
    return InternationalAccessDecision.pendingQualifiedReview(
        request.id(),
        ticket.id(),
        Escalation.required("legal", "compliance", "security", "cloud-owner")
    );
}
```

**Bad example:**

```java
public byte[] respondToGovernmentRequest(String datasetId) {
    return objectStore.read(datasetId);
}
```


### Example 7: Route trade-secret and sensitive-data decisions to owners

Title: field classification, confidentiality measures, refusal evidence, and suspension workflow
Description: Use this pattern when requested data may include trade secrets, commercially sensitive data, intellectual-property-sensitive material, or mixed personal and non-personal data. Engineering should identify and preserve evidence, not make the disclosure decision alone.

**Good example:**

```yaml
tradeSecretHandoff:
  requestId: dar-2026-0614-0042
  dataset: vehicle-diagnostics-events
  potentiallyProtectedFields:
    - predictive-maintenance-score
    - proprietary-fault-classification
  metadataTags:
    confidentiality: trade-secret-review-required
    personalData: possible
  proposedMeasures:
    - confidentiality agreement
    - field-level masking until approval
    - recipient access protocol
    - encryption in transit and at rest
    - audit logging and deletion confirmation
  decisionOwners:
    - legal-data-sharing
    - product-owner
    - data-governance
    - security-architecture
  engineeringStatus: hold-for-qualified-review
```

**Bad example:**

```yaml
tradeSecretHandoff:
  dataset: diagnostics
  decision: do not share because it looks sensitive
```


### Example 8: Block release until Data Act engineering controls are evidenced

Title: inventory, authorization, export, metadata, audit, switching, and owner approvals
Description: Use this pattern before production release of Java changes that introduce data access APIs, new export fields, third-party sharing, cloud customer exports, public-sector request handling, or interoperability changes.

**Good example:**

```java
public ReleaseDecision evaluate(DataActEngineeringReview review) {
    List<String> blockers = new ArrayList<>();

    requirePresent(review.dataInventory(), "data inventory and owner map", blockers);
    requirePassing(review.accessAuthorizationTests(), "access authorization tests", blockers);
    requirePresent(review.exportFormatEvidence(), "machine-readable export format evidence", blockers);
    requirePresent(review.metadataCatalogEntry(), "metadata and schema evidence", blockers);
    requirePassing(review.auditLogVerification(), "data access audit-log verification", blockers);
    requirePresent(review.tradeSecretHandoff(), "trade-secret and sensitive-data handoff", blockers);
    requirePresent(review.cloudSwitchingPlan(), "cloud-switching or non-applicability evidence", blockers);

    if (!blockers.isEmpty()) {
        return ReleaseDecision.blocked(
            review.serviceId(),
            blockers,
            Escalation.required("legal", "data-governance", "privacy", "security", "cloud-owner")
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
public ReleaseDecision evaluate(DataActEngineeringReview review) {
    return ReleaseDecision.approved(review.serviceId(), "API tests passed");
}
```


## Output Format

- **MATCH** the relevant example patterns: data inventory, access authorization, portability API, export format, interoperability metadata, cloud switching, non-personal data safeguards, trade-secret handoff, request workflow, or release gate
- **RECOMMEND** engineering controls: data inventory updates, role and entitlement checks, request intake workflow, machine-readable export formats, metadata catalog entries, API quality-of-service evidence, audit logs, evidence-safe support operations, interoperability contracts, cloud-switching runbooks, secure transfer, erasure verification, non-personal data safeguards, and qualified owner handoffs
- **REPORT** conclusions and actions using `assets/reports/806-eu-data-act-engineering-review-report-template.md`, including review context, data access scope, evidence reviewed, Data Act risk signals, potential violation or non-compliance signals with official-source links, engineering controls, residual risks, release decision, and prioritized action plan with owners and due dates


## Safeguards

- **DATA ACCESS EVIDENCE**: Do not accept undocumented claims that user access, third-party sharing, export, portability, interoperability, cloud switching, request handling, or erasure controls exist; ask for reviewable evidence
- **QUALIFIED ROLE DECISIONS**: Escalate data holder status, user entitlement, data recipient role, public-sector exceptional need, contract interpretation, trade-secret boundaries, cloud-switching obligations, and regulatory interpretation
- **MIXED DATASETS**: Coordinate with privacy and GDPR controls when personal data and non-personal data are mixed or inseparable