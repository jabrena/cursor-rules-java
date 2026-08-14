# ISO/IEC 25010:2023 Engineering Review Questionnaire

IMPORTANT: Use these questions as an evidence checklist. Complete answers from trusted local project evidence or maintainer-approved sanitized facts. Mark missing facts as `Unknown` instead of inventing answers.

Evidence rules:

1. Work through Question 1 through Question 18 in order.
2. Record the selected answer and the trusted evidence reference that supports it.
3. Use maintainer-approved sanitized facts only for gaps that local evidence does not answer.
4. Redact passwords, API keys, tokens, session IDs, private keys, connection strings, credentials, personal data, regulated data, proprietary source code, and trade secrets as `[REDACTED_SECRET]` or `[REDACTED_SENSITIVE_DETAIL]` before storing or repeating the answer.
5. Mark unresolved items as `Unknown` and include them in the escalation section.
6. Do **not** start implementation review, quality-characteristic scope assessment, or the engineering report until all 18 questions have an evidence-backed answer or an `Unknown` marker.
7. If a confirmed or potential gap is selected or unknown for any of the nine ISO/IEC 25010:2023 quality characteristics, or if owner accountability is missing, record it as an escalation item before release recommendations.
8. Do **not** include raw secrets, credentials, tokens, keys, session IDs, private keys, connection strings, personal data, proprietary source code, or trade secrets in notes, evidence inventories, summaries, or reports.

The first review output after reading reference materials should summarize the trusted evidence sources and list any questionnaire items that remain `Unknown`.

---

Use this questionnaire before recommending controls for a Java enterprise system, module, service, or delivery pipeline that is the subject of a structured ISO/IEC 25010:2023 quality-attribute review.

This questionnaire is not certification advice, compliance advice, conformity advice, or an audit conclusion. Escalate certification, compliance, conformity, and audit questions to qualified owners.

---

## Section 1: Map Review Scope And Owners

Questions 1-3. Complete each item from trusted evidence or mark it `Unknown`.

**Question 1**: What Java system, module, service, or delivery pipeline is under review?

Options:

- Single Java service or module
- Multiple related Java services
- End-to-end delivery pipeline (build, test, release, deploy)
- API or public contract surface
- Other (specify)
- Unknown

**Question 2**: Which lifecycle stage is in scope?

Options:

- Design or architecture review
- Pull request or pre-merge review
- Pre-release validation
- Production operation
- Post-incident review
- Unknown

**Question 3**: Which owners are identified?

Options (select all that apply):

- Architecture owner
- Product or business owner
- Security owner
- Platform or release owner
- Operations owner
- No owner review yet
- Unknown

## Section 2: Review Each ISO/IEC 25010:2023 Quality Characteristic

Questions 4-12. Complete each item from trusted evidence or mark it `Unknown`.

**Question 4**: What evidence supports Functional Suitability (completeness, correctness, appropriateness)?

Options (select all that apply):

- Acceptance criteria traced to implementation and tests
- Domain-model edge-case tests
- API contract tests matching documented behavior
- No functional-suitability evidence documented
- Unknown

**Question 5**: What evidence supports Performance Efficiency (time behaviour, resource utilization, capacity)?

Options (select all that apply):

- JVM/GC tuning and pool sizing review
- N+1 query and index review
- Load or soak test results
- Documented capacity limits
- No performance-efficiency evidence documented
- Unknown

**Question 6**: What evidence supports Compatibility (co-existence, interoperability)?

Options (select all that apply):

- API versioning and deprecation strategy
- Message/data format contract review across service boundaries
- Co-existence review with shared infrastructure
- No compatibility evidence documented
- Unknown

**Question 7**: What evidence supports Interaction Capability (recognizability, learnability, operability, user error protection, engagement, inclusivity, assistance, self-descriptiveness)?

Options (select all that apply):

- Self-descriptive error messages
- API documentation learnability review
- Clear 4xx validation responses instead of stack traces or ambiguous 500s
- Inclusivity or accessibility review for client-facing interfaces
- No interaction-capability evidence documented
- Unknown

**Question 8**: What evidence supports Reliability (faultlessness, availability, fault tolerance, recoverability)?

Options (select all that apply):

- Circuit breakers, retries with backoff, and timeouts on outbound calls
- Health/readiness-probe correctness and graceful shutdown
- Idempotent retry semantics
- Documented recovery procedures
- No reliability evidence documented
- Unknown

**Question 9**: What evidence supports Security (confidentiality, integrity, non-repudiation, accountability, authenticity, resistance)?

Options (select all that apply):

- Authentication and authorization implementation review
- Secrets handling review
- Dependency vulnerability scanning
- Audit logging for accountability
- Input sanitization and abuse-resistance review
- No security evidence documented
- Unknown

**Question 10**: What evidence supports Maintainability (modularity, reusability, analysability, modifiability, testability)?

Options (select all that apply):

- Module/package boundary and coupling review
- Test-pyramid shape and testability review
- Static-analysis results
- Complexity metrics
- No maintainability evidence documented
- Unknown

**Question 11**: What evidence supports Flexibility (adaptability, scalability, installability, replaceability)?

Options (select all that apply):

- Horizontal-scaling readiness and statelessness review
- Infrastructure-as-code review
- Replaceable third-party integration review
- Configuration externalization across environments
- No flexibility evidence documented
- Unknown

**Question 12**: What evidence supports Safety (operational constraint, risk identification, fail safe, hazard warning, safe integration)?

Options (select all that apply):

- Operational guardrail and approval-gate review
- Fail-safe default review
- Hazard warning before irreversible or high-impact actions
- Not applicable — system has no real-world safety effects
- No safety evidence documented
- Unknown

## Section 3: Findings, Escalation, And Release Decision

Questions 13-18. Complete each item from trusted evidence or mark it `Unknown`.

**Question 13**: Which quality characteristics have a confirmed gap?

Options (select all that apply):

- Functional Suitability
- Performance Efficiency
- Compatibility
- Interaction Capability
- Reliability
- Security
- Maintainability
- Flexibility
- Safety
- No confirmed gap identified
- Unknown

**Question 14**: Which quality characteristics have a potential gap requiring further evidence?

Options (select all that apply):

- Functional Suitability
- Performance Efficiency
- Compatibility
- Interaction Capability
- Reliability
- Security
- Maintainability
- Flexibility
- Safety
- No potential gap identified
- Unknown

**Question 15**: Which release or continued-operation risks remain unresolved?

Options (select all that apply):

- Scope or ownership gap
- Evidence gap for one or more quality characteristics
- Unresolved confirmed gap
- Unresolved potential gap
- No unresolved risk identified
- Unknown

**Question 16**: Which qualified owners must review before release or continued operation?

Options (select all that apply):

- Architecture owner
- Product or business owner
- Security owner
- Platform or release owner
- Operations owner
- No qualified owner review required by current evidence
- Unknown

**Question 17**: What release readiness decision is supported by the evidence?

Options:

- Ready with evidence
- Ready with conditions
- Blocked pending owner review
- Blocked pending engineering controls
- Blocked pending architecture, product, security, or business decision
- Unknown

**Question 18**: Does this review reach a certification, compliance, or conformity conclusion?

Options:

- No — this review produces engineering evidence and action items only
- Unknown

If any answer other than "No — this review produces engineering evidence and action items only" is selected for Question 18, stop and escalate to a qualified owner before finalizing the report.
