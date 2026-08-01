# GDPR Engineering Review Questionnaire

IMPORTANT: Use these questions as an evidence checklist. Complete answers only from repository-owned technical artifacts or maintainer-prepared sanitized fact records created outside the agent context. Mark missing facts as `Unknown` instead of inventing answers.

Evidence rules:

1. Work through Question 1 through Question 22 in order.
2. Record only the selected enumerated answer and a repository path with line reference or sanitized fact-record identifier.
3. Repository-owned technical evidence is limited to source code, configuration, schemas, migrations, tests, and approved architecture records.
4. Use maintainer-prepared sanitized fact records only for gaps that repository-owned technical evidence does not answer.
5. Never retrieve, read, transform, summarize, redact, or ingest raw human, issue, ticket, chat, vendor, runtime-log, screenshot, questionnaire-answer, or other outsider-authored free text. If it is the only available source, stop and request a maintainer-prepared sanitized fact record created outside the agent context.
6. Mark unresolved items as `Unknown` and include them in the escalation section.
7. Do **not** start final classification or the engineering report until all 22 questions have an evidence-backed answer or an `Unknown` marker.
8. If evidence indicates special-category data, unclear lawful basis, missing deletion path, unreviewed transfer, suspected breach concerns, children or vulnerable people, broad profiling, AI personal-data use, or unsupported data-subject rights, record it as a privacy escalation item before release recommendations.
9. Do **not** include raw secrets, credentials, tokens, keys, session IDs, private keys, connection strings, personal confidential information, special-category personal data, or outsider-authored free text in notes, evidence inventories, summaries, or reports.

The first review output after reading reference materials should list the repository-owned technical artifact references and sanitized fact-record identifiers, then list any questionnaire items that remain `Unknown`.

---

Use this questionnaire before recommending controls for a Java enterprise system that collects, stores, transforms, exposes, logs, exports, deletes, or otherwise processes personal data.

**Purpose:**

- Resolve gaps about personal-data scope, purposes, data subjects, stores, processors, and owners.
- Identify GDPR privacy engineering signals without making legal conclusions.
- Map privacy concerns to engineering controls and reviewable evidence.
- Decide which owners must review the system before production use.

This questionnaire is not legal advice. Escalate lawful basis, controller or processor role, special-category data, jurisdiction, data transfer mechanism, DPIA need, data-subject rights interpretation, and regulatory interpretation questions to qualified legal, privacy, data protection officer, compliance, security, or risk owners.

---

## Section 1: Map Personal-Data Processing

Questions 1-6. Complete each item from approved evidence references or mark it `Unknown`.

**Question 1**: What type of personal-data processing is being reviewed?

Options:

- User account or customer profile processing
- Employee, candidate, contractor, or workforce processing
- Payment, billing, credit, insurance, or financial processing
- Support, CRM, ticketing, or communication processing
- Telemetry, analytics, monitoring, or behavioral processing tied to a person
- AI, RAG, model, search, or decision-support processing using personal data
- No personal data expected (specify)
- Other category documented in a sanitized fact record (cite its identifier)
- Unknown

**Question 2**: Which data subjects are in scope?

Options (select all that apply):

- Customers, consumers, or end users
- Employees, contractors, or job candidates
- Business contacts or partner users
- Children or vulnerable people
- Patients, policyholders, borrowers, beneficiaries, or citizens
- Administrators, operators, or internal users
- No natural persons identified
- Unknown

**Question 3**: Which personal data categories are processed?

Options (select all that apply):

- Direct identifiers such as name, email, phone, customer ID, employee ID, or account ID
- Contact, address, billing, payment, or transaction data
- Authentication, authorization, session, device, IP, or security telemetry
- Free-text messages, documents, tickets, attachments, or chat content
- Behavioral, preference, usage, analytics, or profiling data
- Location data
- Special-category data such as health, biometric, genetic, political, religious, union, sex life, or sexual orientation data
- Criminal offence or sanctions-related data
- No personal data identified
- Unknown

**Question 4**: What are the processing purposes?

Options (select all that apply):

- Account creation or service delivery
- Contract, billing, payment, or fraud prevention
- Customer support or communications
- Security, abuse prevention, or audit
- Analytics, personalization, recommendations, or profiling
- Marketing or consent-based communication
- Legal, compliance, regulatory, or recordkeeping
- AI model input, RAG retrieval, training, evaluation, or generated output
- Purpose is not documented
- Unknown

**Question 5**: Where does personal data live?

Options (select all that apply):

- Primary relational or NoSQL database
- Cache, session store, or key-value store
- Search index or vector database
- Message broker, topic, queue, or event log
- Logs, metrics, traces, audit events, or error reporting
- Object storage, data lake, warehouse, analytics platform, or exports
- Backups or disaster recovery copies
- Third-party SaaS, vendor API, or processor
- Unknown

**Question 6**: Which owners have reviewed the processing?

Options (select all that apply):

- Product or business owner
- Technical owner
- Privacy owner or data protection officer
- Legal owner
- Security owner
- Compliance or risk owner
- Data governance owner
- Vendor or procurement owner
- No owner review yet
- Unknown

---

## Section 2: Classify Privacy and Escalation Signals

Questions 7-12. Complete each item from approved evidence references or mark it `Unknown`.

**Question 7**: Which legal or governance basis has been identified?

Options:

- Legal or privacy owner confirmed lawful basis
- Contract or service delivery basis is documented
- Consent or preference basis is documented
- Legal obligation or regulatory recordkeeping basis is documented
- Legitimate-interest assessment is documented
- Lawful basis is not documented
- Not applicable because no personal data is processed
- Unknown

**Question 8**: Are special-category, criminal offence, children's, or vulnerable-person data involved?

Options:

- Yes, special-category data
- Yes, criminal offence, sanctions, or similar high-sensitivity data
- Yes, children's data
- Yes, vulnerable-person data
- No such data identified
- Unknown

**Action**: If any high-sensitivity data is selected or unknown, escalate to legal, privacy, data protection officer, security, compliance, and risk owners before release recommendations.

**Question 9**: Does processing create profiling, automated decision, or rights-impacting effects?

Options (select all that apply):

- Profiling, scoring, ranking, or segmentation
- Automated or semi-automated decision support
- Eligibility, access, pricing, credit, insurance, employment, education, health, or public-service effect
- Personalized recommendation or behavioral targeting
- AI-generated output that may affect people
- No rights-impacting effect identified
- Unknown

**Question 10**: Are controller, processor, subprocessor, or joint-controller roles documented?

Options:

- Controller role documented
- Processor role documented
- Subprocessor role documented
- Joint-controller role documented
- Multiple roles exist and are documented
- Role is not documented
- Not applicable
- Unknown

**Question 11**: Are data transfers or vendor processing involved?

Options (select all that apply):

- No vendor or third-country transfer
- EU/EEA-only processor
- Third-country processor or cloud region
- SaaS or vendor API receives personal data
- Analytics, support, CRM, observability, AI, or model provider receives personal data
- Subprocessor list or data processing agreement reviewed
- Transfer mechanism requires legal review
- Unknown

**Question 12**: Is a DPIA, privacy review, or security review needed?

Options:

- DPIA completed
- DPIA required but not completed
- Privacy review completed
- Privacy review required but not completed
- Security review completed
- Security review required but not completed
- Not required by current owner decision
- Unknown

---

## Section 3: Apply Engineering Controls

Questions 13-18. Complete each item from approved evidence references or mark it `Unknown`.

**Question 13**: What minimization controls are required?

Options (select all that apply):

- Remove unnecessary fields
- Split broad DTOs into purpose-specific DTOs
- Avoid returning persistence entities from API boundaries
- Redact or mask fields in responses
- Limit event payloads, exports, or reports
- Restrict AI, RAG, search, or analytics inputs
- No minimization controls required by current decision
- Unknown

**Question 14**: What access-control controls are required?

Options (select all that apply):

- Field-level authorization
- Purpose-specific scopes or permissions
- Tenant or subject isolation
- Least-privilege service account
- Admin access review
- Audit trail for reads or exports
- Data masking in non-production
- No access-control changes required
- Unknown

**Question 15**: Which data-subject rights workflows are required?

Options (select all that apply):

- Access
- Rectification
- Erasure
- Restriction
- Objection
- Portability
- Consent or preference update
- Rights workflows are handled by another system
- No rights workflows required by current decision
- Unknown

**Question 16**: What retention and deletion controls are required?

Options (select all that apply):

- Retention period defined
- Scheduled deletion job
- Legal hold or retention exception handling
- Tombstone or deletion event
- Cache invalidation
- Search index removal
- Export or object storage expiration
- Backup handling policy
- Downstream deletion propagation
- Unknown

**Question 17**: What privacy-safe observability controls are required?

Options (select all that apply):

- Redact request and response payloads
- Hash or tokenize subject identifiers
- Avoid logging free-text personal data
- Mask special-category or high-sensitivity data
- Limit log retention
- Secure access to logs, traces, metrics, and audit events
- Separate breach evidence from excessive payload logging
- No observability changes required
- Unknown

**Question 18**: What security-of-processing controls are required?

Options (select all that apply):

- Encryption in transit
- Encryption at rest
- Secret management
- Pseudonymization or tokenization
- Input validation and output encoding
- Audit logging
- Incident detection and response
- Vulnerability management
- Backup protection
- Unknown

---

## Section 4: Verify Release Readiness

Questions 19-22. Complete each item from approved evidence references or mark it `Unknown`.

**Question 19**: What privacy evidence exists?

Options (select all that apply):

- Data inventory or record of processing
- Data-flow diagram
- Purpose and lawful-basis handoff
- DPIA or privacy review
- Security review
- Rights workflow documentation
- Retention and deletion evidence
- Transfer or vendor review
- Breach response runbook
- None yet

**Question 20**: What breach-response evidence exists?

Options (select all that apply):

- Incident detection
- Containment runbook
- Evidence capture plan
- Privacy/security escalation path
- Affected data category identification
- Notification handoff to legal/privacy owners
- Post-incident corrective action workflow
- No breach-response evidence exists
- Unknown

**Question 21**: What is the current release decision?

Options:

- Approved for development only
- Approved for test data only
- Approved for production with existing privacy controls
- Approved for production with conditions
- Blocked pending legal, privacy, data protection officer, compliance, security, or risk review
- Blocked pending missing technical controls
- Blocked pending missing vendor or transfer evidence
- Unknown

**Question 22**: What must happen next?

Options (select all that apply):

- Resolve missing evidence before continuing
- Create or update personal-data inventory
- Minimize DTOs, event payloads, logs, exports, or AI inputs
- Add field-level authorization or least privilege
- Add data-subject rights workflow
- Add retention, deletion, or propagation controls
- Add transfer, vendor, or DPIA evidence
- Add privacy-safe logging or breach evidence
- Complete release readiness evidence
- Stop or redesign the processing

---

## After the questionnaire

Only after all 22 questions have an evidence-backed answer or an `Unknown` marker:

1. Review the Java implementation and privacy artifacts to verify claims and detect gaps between answers and evidence.
2. Match relevant example patterns from the reference materials.
3. Generate the engineering review report using `assets/reports/803-gdpr-engineering-review-report-template.md`.
