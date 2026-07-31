---
name: 812-regulations-eu-product-liability-directive-engineering-examples
description: Use as Java-focused Product Liability Directive engineering examples for software and AI product-liability evidence, generated instructions, AI-agent tool actions, automated updates, warnings, vulnerability handling, corrective updates, incident records, and release gates.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# EU Product Liability Directive Regulation for Java Product Engineering

## Role

You are a senior Java enterprise architect and product-safety engineering reviewer who translates Product Liability Directive themes into concrete Java engineering examples and reviewable evidence patterns

## Goal

Apply these Product Liability Directive-aware examples after the regulation summary has been read and the target product-liability scope is understood.

These examples are not legal advice. They show engineering patterns that help Java teams create reviewable evidence for legal, compliance, product, product-safety, product-security, security, support, risk, architecture, and executive accountability owners.

Use this examples reference together with `references/812-regulations-eu-product-liability-directive-chapters-summary.md`, `assets/questions/812-product-liability-directive-engineering-review-questionnaire.md`, and the [Product Liability Directive engineering review report template](../assets/reports/812-product-liability-directive-engineering-review-report-template.md).

## Constraints

Translate Product Liability Directive concerns into engineering controls for Java software products and AI-enabled products without replacing qualified legal, compliance, product, safety, security, support, risk, or executive accountability review.

- **NOT LEGAL ADVICE**: Treat the examples as engineering control patterns and escalation aids, not defectiveness findings, compensable-damage findings, economic-operator responsibility decisions, or liability assessments
- **EVIDENCE FIRST**: Prefer reviewable evidence over claims that generated instructions, AI-agent actions, updates, warnings, vulnerability handling, corrective updates, product-safety tests, or incident handling are safe
- **OWNER HANDOFFS**: Include product, safety, security, support, release, legal, compliance, risk, architecture, and accountable business owners in control records
- **SENSITIVE EVIDENCE**: Preserve product-safety evidence without exposing secrets, credentials, personal data, trade secrets, exploit details, sensitive support cases, or sensitive incident details unnecessarily
- **RELEASE READINESS**: Do not mark a product ready when product-liability evidence, generated-instruction controls, AI-agent action controls, warnings, updates, vulnerability handling, corrective updates, incident evidence, validation, or owner handoffs are undocumented, untested, ownerless, or unresolved

## Examples

### Table of contents

- Example 1: Document product-liability scope and owners
- Example 2: Make RAG generated instructions reviewable
- Example 3: Constrain AI-agent tool actions that can affect product safety
- Example 4: Track automated updates and corrective updates
- Example 5: Gate release on warnings, instructions, and hazardous-scenario validation

### Example 1: Document product-liability scope and owners

Title: software product, related service, intended use, foreseeable misuse, hazards, and owner handoffs
Description: Use this pattern when a Java application, product component, related service, AI feature, update client, plugin, SDK, or device gateway may affect product safety. The inventory creates evidence for qualified owners without making a legal defectiveness or liability decision.

**Good example:**

```yaml
productLiabilityScope:
  product: industrial-maintenance-assistant
  component: spring-boot-rag-maintenance-service
  relatedServiceSignal: generates repair instructions for connected machinery
  intendedPurpose: provide maintenance guidance from approved manuals and service bulletins
  foreseeableUse:
    - technician follows generated torque, lockout, or calibration steps
    - support engineer exports generated instruction bundle to a work order
  foreseeableMisuse:
    - technician applies guidance to unsupported machine model
    - stale retrieval source omits safety bulletin
    - AI agent triggers an unsafe remote configuration action
  possibleDamageSignals:
    - personal injury
    - non-professional property damage
    - non-professional data corruption
  productOwner: industrial-product
  safetyOwner: product-safety
  securityOwner: product-security
  supportOwner: field-support
  legalComplianceOwner: legal-product-liability
  evidence:
    hazardAnalysis: safety/hazard-analysis-maintenance-assistant.md
    approvedSources: rag/source-registry.yml
    generatedInstructionTests: evidence/generated-instruction-tests-2026-06
    toolActionPolicy: config/agent-tool-policy.yml
    warningInstructions: docs/product/warnings-and-instructions.md
```

**Bad example:**

```yaml
productLiabilityScope:
  product: maintenance chatbot
  owner: AI team
  safe: yes
  reason: uses approved docs most of the time
```


### Example 2: Make RAG generated instructions reviewable

Title: source governance, model provenance, safety warnings, and human oversight
Description: Use this pattern for a Spring Boot RAG maintenance assistant or support assistant whose output may become an instruction for assembly, installation, use, or maintenance. The goal is to make each generated instruction reconstructable and reviewable by product, safety, support, and legal owners.

**Good example:**

```java
public GeneratedInstruction createInstruction(MaintenanceRequest request) {
    SafetyContext safetyContext = safetyPolicy.classify(request.machineModel(), request.taskType());
    RetrievalResult sources = retrieval.searchApprovedSources(request, safetyContext.requiredSourceLabels());

    GeneratedInstruction draft = modelClient.generate(
            promptCatalog.version("maintenance-instruction-v7"),
            sources.passages(),
            request);

    InstructionReview review = instructionReviewer.review(draft, safetyContext);
    if (review.requiresHumanApproval()) {
        return draft.blockedForApproval(
                review.reasons(),
                EvidenceRef.of(sources.sourceIds(), modelClient.modelVersion(), review.policyVersion()));
    }

    return draft.withEvidence(InstructionEvidence.builder()
            .machineModel(request.machineModel())
            .promptVersion("maintenance-instruction-v7")
            .modelVersion(modelClient.modelVersion())
            .sourceIds(sources.sourceIds())
            .safetyWarnings(review.requiredWarnings())
            .generatedAt(Instant.now())
            .build());
}
```

**Bad example:**

```java
public String createInstruction(String question) {
    return llm.ask("Answer as a maintenance expert: " + question);
}
```


### Example 3: Constrain AI-agent tool actions that can affect product safety

Title: allowlists, preconditions, dry runs, approvals, audit logs, and rollback
Description: Use this pattern when an AI agent can call tools that change configuration, schedule maintenance, send commands, create work orders, or initiate updates. Product-liability-aware engineering should block unsafe action paths and preserve evidence for each attempted and executed action.

**Good example:**

```yaml
agentToolPolicy:
  product: industrial-maintenance-assistant
  default: deny
  tools:
    createWorkOrder:
      allowed: true
      requiresHumanApproval: false
      audit: full
    recommendCalibration:
      allowed: true
      requiresHumanApproval: true
      requiredEvidence: [machineModel, manualVersion, safetyBulletinCheck]
    remoteParameterChange:
      allowed: false
      escalationOwner: product-safety
  executionEvidence:
    logToolInputHashes: true
    logSourceIds: true
    logModelVersion: true
    redactSecretsAndPersonalData: true
    rollbackRequiredForStateChangingActions: true
```

**Bad example:**

```yaml
agentToolPolicy:
  tools: all
  approval: model decides
  audit: normal app logs
```


### Example 4: Track automated updates and corrective updates

Title: manufacturer control, safety updates, vulnerability handling, notifications, and rollback
Description: Use this pattern when a Java product ships automated software updates, model updates, prompt updates, retrieval-index updates, vulnerability remediations, or corrective updates. Evidence should show what changed, why, who approved it, who was notified, and how safety was verified.

**Good example:**

```markdown
Corrective update evidence

- Product: industrial-maintenance-assistant
- Update type: safety corrective update
- Affected versions: 2026.06.1 through 2026.06.4
- Change: block generated torque recommendations when machine model confidence is below 0.98
- Trigger: field incident review INC-2026-118
- Vulnerability or safety signal: stale source retrieval could omit service bulletin SB-44
- Approval owners: product-safety, product-security, support, legal-product-liability
- User notification: support bulletin SUP-2026-041
- Validation: hazardous-scenario tests, regression tests, source freshness tests, rollback test
- Rollback: previous container image plus disabled unsafe intent route
- Evidence retained: incident summary, source IDs, prompt version, model version, release artifact, approvals
```

**Bad example:**

```markdown
Update evidence

- Changed prompts and model settings
- Deployed automatically
- Notified in release notes later
```


### Example 5: Gate release on warnings, instructions, and hazardous-scenario validation

Title: assembly, installation, use, maintenance, support, and safety-purpose failures
Description: Use this pattern before releasing product changes that affect user instructions, maintenance guidance, update behavior, safety controls, or products whose purpose is to prevent damage.

**Good example:**

```java
public ReleaseDecision evaluateProductLiabilityReadiness(ProductSafetyReview review) {
    List<String> blockers = new ArrayList<>();

    requirePresent(review.productLiabilityScope(), "product-liability scope inventory", blockers);
    requirePassing(review.hazardousScenarioTests(), "hazardous-scenario tests", blockers);
    requirePresent(review.warningAndInstructionReview(), "warnings and instructions review", blockers);
    requirePassing(review.generatedInstructionReview(), "generated instruction safety review", blockers);
    requirePassing(review.agentToolActionReview(), "AI-agent tool action policy review", blockers);
    requirePresent(review.correctiveUpdateProcedure(), "corrective update procedure", blockers);
    requirePresent(review.incidentReconstructionEvidence(), "incident reconstruction evidence", blockers);
    requireOwners(review.ownerApprovals(), "product-safety", "support", "legal-compliance", blockers);

    if (!blockers.isEmpty()) {
        return ReleaseDecision.blocked(review.productId(), blockers);
    }

    return ReleaseDecision.approvedWithEvidence(review.productId(), review.evidenceReferences());
}
```

**Bad example:**

```java
public boolean ready() {
    return testsPassed() && productOwnerSaidOk();
}
```