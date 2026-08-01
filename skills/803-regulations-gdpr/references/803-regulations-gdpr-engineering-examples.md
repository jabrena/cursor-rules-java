---
name: 803-regulations-gdpr-engineering-examples
description: Use as Java-focused GDPR engineering examples for personal-data inventory, DTO minimization, rights workflows, retention and deletion, transfer review, privacy-safe logging, and field-level privacy policy controls.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# GDPR Regulation for Java Enterprise Personal Data Protection

## Role

You are a senior Java enterprise architect and privacy engineering reviewer who translates GDPR themes into concrete Java engineering examples and reviewable evidence patterns

## Goal

Apply these GDPR-aware examples after the regulation summary has been read, the questionnaire has been answered, and the target personal-data scope is understood.

These examples are not legal advice. They show engineering patterns that help Java teams create reviewable evidence for legal, privacy, data protection officer, compliance, security, risk, data-governance, architecture, vendor, and business owners.

Use this examples reference together with `references/803-regulations-gdpr-chapters-summary.md`, `assets/questions/803-gdpr-engineering-review-questionnaire.md`, and the [GDPR engineering review report template](../assets/reports/803-gdpr-engineering-review-report-template.md).

## Constraints

Translate GDPR concerns into engineering controls for Java enterprise systems without replacing qualified legal, privacy, data protection officer, compliance, security, risk, data-governance, architecture, vendor, or business-owner review.

- **NOT LEGAL ADVICE**: Treat the examples as engineering control patterns and escalation aids, not legal findings
- **EVIDENCE FIRST**: Prefer reviewable evidence over claims that controls exist
- **OWNER HANDOFFS**: Include legal, privacy, DPO, security, data-governance, vendor, product, and business owners in control records
- **PRIVACY-SAFE EVIDENCE**: Preserve auditability without logging secrets, credentials, unnecessary identifiers, full payloads, or sensitive free-text personal data
- **RELEASE READINESS**: Do not mark a system ready when data inventory, minimization, rights workflows, retention, security, breach, transfer, or owner handoff controls are undocumented, untested, or ownerless

## Examples

### Table of contents

- Example 1: Document personal-data processing before implementation
- Example 2: Minimize personal data in API DTOs
- Example 3: Make data-subject rights workflows executable
- Example 4: Propagate retention and deletion beyond the primary table
- Example 5: Escalate transfer and vendor processing decisions
- Example 6: Capture breach evidence without excessive personal data
- Example 7: Apply field-level privacy policy before returning DTOs

### Example 1: Document personal-data processing before implementation

Title: categories, purposes, stores, processors, and retention
Description: Use this pattern when a Java service receives, stores, publishes, logs, exports, or deletes personal data. The inventory helps engineers separate data needed for the business purpose from excessive collection or unclear processing.

**Good example:**

```yaml
personalDataInventory:
  service: customer-profile-api
  dataSubjects: [customers]
  purposes:
    - account-management
    - support-contact
  fields:
    - name: customerId
      category: identifier
      source: identity-service
      retainedFor: P7Y
    - name: email
      category: contact
      source: registration-form
      retainedFor: account-lifetime
    - name: marketingPreference
      category: preference
      source: consent-service
      retainedFor: account-lifetime
  stores:
    primary: postgresql.customer_profile
    cache: redis.customer-profile
    logs: redacted identifiers only
  owners:
    product: customer-platform
    privacy: privacy-office
    security: appsec
```

**Bad example:**

```yaml
personalDataInventory:
  service: customer-profile-api
  data: user details
  retention: forever
```


### Example 2: Minimize personal data in API DTOs

Title: purpose-specific response shape instead of broad entity exposure
Description: Use this pattern when controllers return persistence entities or broad DTOs. GDPR-aware engineering should expose only fields needed for the specific purpose and authorization context.

**Good example:**

```java
public record CustomerSummaryResponse(
    String customerId,
    String displayName,
    String supportTier
) {
    static CustomerSummaryResponse from(CustomerProfile profile) {
        return new CustomerSummaryResponse(
            profile.customerId().value(),
            profile.displayName(),
            profile.supportTier().name()
        );
    }
}
```

**Bad example:**

```java
public CustomerProfile getCustomer(String customerId) {
    return customerRepository.findById(customerId).orElseThrow();
}
```


### Example 3: Make data-subject rights workflows executable

Title: locate, authorize, fulfill, propagate, and audit
Description: Use this pattern when a service stores data that may need access, rectification, erasure, restriction, objection, portability, or preference handling. The workflow should be auditable and avoid exposing data to unauthorized requesters.

**Good example:**

```java
public RightsRequestResult fulfillErasure(RightsRequest request) {
    authorization.verifyRequester(request.subjectId(), request.requester());

    List<DataLocation> locations = dataMap.locationsForSubject(request.subjectId());
    DeletionPlan plan = deletionPlanner.plan(request.subjectId(), locations);

    DeletionResult result = deletionExecutor.execute(plan);
    audit.recordRightsRequest(request.id(), request.subjectId(), plan.summary(), result.summary());

    downstreamNotifier.publishDeletionCompleted(request.subjectId(), result.deletedLocations());
    return RightsRequestResult.completed(request.id(), result.deletedLocations());
}
```

**Bad example:**

```java
public void deleteUser(String userId) {
    customerRepository.deleteById(userId);
}
```


### Example 4: Propagate retention and deletion beyond the primary table

Title: caches, indexes, topics, exports, and evidence
Description: Use this pattern when personal data appears in derived stores. A deletion job that only touches the primary database can leave personal data in caches, search indexes, object storage, topics, exports, or analytics datasets.

**Good example:**

```yaml
retentionDeletionPlan:
  subjectId: customer-123
  primaryStores:
    - postgresql.customer_profile
  derivedStores:
    - redis.customer-profile-cache
    - opensearch.customer-search
    - s3.customer-export-bucket
    - kafka.customer-profile-events
  actions:
    - delete-primary-record
    - evict-cache-entry
    - remove-search-document
    - expire-export-object
    - publish-deletion-tombstone
  evidence:
    deletionJobId: deletion-20260613-00042
    completedAt: 2026-06-13T10:30:00Z
    failedLocations: []
```

**Bad example:**

```yaml
retentionDeletionPlan:
  action: delete row from customer table
  caches: not checked
  exports: not checked
```


### Example 5: Escalate transfer and vendor processing decisions

Title: processors, subprocessors, regions, data categories, and review owners
Description: Use this pattern when personal data leaves the service boundary through SaaS tools, analytics, support systems, model providers, data warehouses, managed platforms, or third-country regions.

**Good example:**

```yaml
dataTransferReview:
  integration: support-ticket-saas
  dataCategories: [customerId, email, supportConversation]
  roleAssessmentOwner: privacy-office
  vendorOwner: procurement
  securityOwner: appsec
  regions: [eu-west-1]
  subprocessorsReviewed: true
  transferMechanismReview: legal-required
  decision: blocked-until-privacy-and-legal-approval
```

**Bad example:**

```yaml
dataTransferReview:
  integration: support-ticket-saas
  dataCategories: all customer data
  approval: vendor is popular
```


### Example 6: Capture breach evidence without excessive personal data

Title: traceability, redaction, containment, and notification handoff
Description: Use this pattern when reviewing logs, audit events, exception handlers, traces, or incident workflows. Engineering evidence should help security and privacy owners investigate incidents without creating a new privacy risk.

**Good example:**

```java
public void recordPrivacyEvent(PrivacyEvent event) {
    auditLogger.info(
        "privacy_event eventId={} subjectHash={} action={} outcome={} traceId={}",
        event.id(),
        subjectHasher.hash(event.subjectId()),
        event.action(),
        event.outcome(),
        event.traceId()
    );
}
```

**Bad example:**

```java
public void recordPrivacyEvent(PrivacyEvent event) {
    logger.info("privacy event payload={}", event);
}
```


### Example 7: Apply field-level privacy policy before returning DTOs

Title: purpose, authorization, minimization, and audit evidence
Description: Use this pattern when a Java API can return different personal-data fields depending on purpose and requester authority. Keep the policy outside the controller and make denied fields explicit in audit evidence.

**Good example:**

```java
public CustomerProfileResponse viewProfile(CustomerId customerId, Requester requester, Purpose purpose) {
    CustomerProfile profile = customerRepository.getRequired(customerId);
    FieldPolicyDecision decision = fieldPolicy.evaluate(requester, purpose, profile);

    audit.recordPersonalDataAccess(
        customerId,
        requester.id(),
        purpose,
        decision.allowedFields(),
        decision.deniedFields()
    );

    return CustomerProfileResponse.from(profile, decision.allowedFields());
}
```

**Bad example:**

```java
public CustomerProfile viewProfile(String customerId) {
    return customerRepository.findById(customerId).orElseThrow();
}
```


## Output Format

- **MATCH** the relevant example patterns: personal-data inventory, minimization DTO, rights workflow, deletion propagation, transfer review, privacy-safe logging, or Java field-level privacy policy
- **RECOMMEND** engineering controls: minimization, field-level authorization, pseudonymization, redaction, retention jobs, deletion propagation, rights request orchestration, transfer evidence, breach evidence, privacy-safe observability, and owner escalation
- **REPORT** conclusions and actions using `assets/reports/803-gdpr-engineering-review-report-template.md`, including review context, personal-data processing summary, potential violation or non-compliance signals with official-source links, engineering controls, residual risks, release decision, and prioritized action plan with owners and due dates


## Safeguards

- **LEGAL AND PRIVACY ESCALATION**: Treat lawful basis, controller or processor role, special-category data, transfer mechanisms, DPIA, and obligations as governance decisions requiring qualified review
- **DATA MINIMIZATION**: Prefer narrow DTOs, purpose-specific processing, selective logging, short retention, and deletion propagation over broad data collection or entity exposure
- **RIGHTS AND DELETION**: Do not accept deletion or rights workflows that only handle the primary table while ignoring logs, caches, indexes, exports, backups, or downstream systems