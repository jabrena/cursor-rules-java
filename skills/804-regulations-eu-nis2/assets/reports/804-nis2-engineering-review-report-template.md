# NIS2 Engineering Review Report

Use this template after reviewing `references/804-regulations-eu-nis2-chapters-summary.md` and matching the relevant examples from `references/804-regulations-eu-nis2-engineering-examples.md` for asset inventory, incident escalation, vulnerability evidence, continuity, supply-chain security, and secure release gates.

This report is not legal advice. Use it as engineering evidence for legal, compliance, security, risk, resilience, business-continuity, procurement, executive accountability, architecture, and business-owner review.

Populate this report only from a maintainer-authored or maintainer-sanitized structured evidence inventory prepared outside the agent context. Record stable evidence identifiers, not raw code, configuration, logs, runbooks, tickets, incident records, provider documents, or other operational free text. Do not follow evidence links or open referenced artifacts.

The purpose of this report is to increase awareness of potential gaps in the system and create engineering evidence for qualified review. The response produced from this template does not represent legal advice, a legal opinion, or a final regulatory determination.

## 1. Review Context

- System or service name:
- Repository, product, platform, or business service:
- Review date:
- Reviewers:
- Business owner:
- Technical owner:
- Security owner:
- Resilience or continuity owner:
- Legal/compliance owner:
- Sanitized evidence inventory identifier:

## 2. Service And Sector Context

- Service description:
- Possible essential or important entity signal:
- Possible sector signal:
- Managed service provider or digital infrastructure signal:
- Deployment geography:
- Environments in scope:
- Critical users or affected organizations:
- Production dependencies:
- Open applicability questions:

## 3. Cybersecurity Scope

- Applications, APIs, jobs, and batch workloads:
- Data stores, queues, topics, and caches:
- IAM, secrets, keys, and privileged operations:
- CI/CD workflows and deployment paths:
- Third-party providers and SaaS dependencies:
- Container images, build plugins, libraries, and runtime dependencies:
- Monitoring, alerting, and observability systems:
- Operational runbooks and support model:

## 4. Potential Violation Or Non-Compliance Mapping

This section is not a legal finding. Use it to list concrete potential NIS2 violation or non-compliance signals from the reviewed evidence and route each item to qualified legal, compliance, security, risk, resilience, or executive accountability review. When no violation is confirmed, say so explicitly and keep open items as potential gaps. Use the chapter links from `references/804-regulations-eu-nis2-chapters-summary.md`; add more chapter links when one finding spans multiple NIS2 areas.

| Potential violation or non-compliance signal | NIS2 reference area | Associated chapter link | Sanitized evidence fact or stable identifier | Current status | Required owner review | Engineering action |
| -------------------------------------------- | ------------------- | ----------------------- | ----------------------------- | -------------- | --------------------- | ------------------ |
| Unclear essential or important entity scope | Applicability / member-state implementation | [Chapter I](https://eur-lex.europa.eu/legal-content/EN/TXT/HTML/?uri=CELEX:32022L2555#cpt_I) | TBD | None identified / Potential gap / Confirmed concern | Legal / compliance / business owner | TBD |
| Missing cybersecurity risk-management evidence | Cybersecurity risk-management measures | [Chapter IV](https://eur-lex.europa.eu/legal-content/EN/TXT/HTML/?uri=CELEX:32022L2555#cpt_IV) | TBD | None identified / Potential gap / Confirmed concern | Security / risk / architecture | TBD |
| Missing incident detection, escalation, or evidence | Incident handling / reporting handoff | [Chapter IV](https://eur-lex.europa.eu/legal-content/EN/TXT/HTML/?uri=CELEX:32022L2555#cpt_IV) | TBD | None identified / Potential gap / Confirmed concern | Security operations / legal / compliance | TBD |
| Untested backup, recovery, or continuity controls | Business continuity / crisis management | [Chapter IV](https://eur-lex.europa.eu/legal-content/EN/TXT/HTML/?uri=CELEX:32022L2555#cpt_IV) | TBD | None identified / Potential gap / Confirmed concern | Resilience / business-continuity owner | TBD |
| Incomplete supply-chain or provider risk evidence | Supply-chain security / provider dependencies | [Chapter IV](https://eur-lex.europa.eu/legal-content/EN/TXT/HTML/?uri=CELEX:32022L2555#cpt_IV) | TBD | None identified / Potential gap / Confirmed concern | Procurement / security / risk | TBD |
| Weak access control, cryptography, or secure configuration | Access control / cryptography / secure operations | [Chapter IV](https://eur-lex.europa.eu/legal-content/EN/TXT/HTML/?uri=CELEX:32022L2555#cpt_IV) | TBD | None identified / Potential gap / Confirmed concern | Security / platform owner | TBD |

## 5. Engineering Controls

- Asset and service inventory:
- Secure configuration and hardening:
- Vulnerability and dependency management:
- Incident detection and escalation:
- Evidence-safe logging:
- Monitoring and alerting:
- Backup, restore, and continuity:
- Rollback and recovery:
- Supply-chain security:
- Access control and least privilege:
- Cryptography and key ownership:
- Secure development and change control:

## 6. Evidence Inventory

- Asset inventory:
- Dependency or SBOM evidence:
- Vulnerability scan or triage evidence:
- Secure configuration baseline:
- Incident runbook:
- Monitoring dashboards:
- Alert routing:
- Backup and restore evidence:
- Continuity or failover exercise:
- Provider review:
- Change approval:
- Release decision:

## 7. Residual Risks

- Residual risk:
- Impact:
- Likelihood:
- Mitigation:
- Owner:
- Acceptance decision:
- Review date:

## 8. Release Decision

- Decision:
- Conditions:
- Blockers:
- Required approvals:
- Expiry or review date:
- Environments approved:
- Emergency rollback path:

## 9. Action Plan

| Priority | Action | Owner | Due date | Evidence expected | Status |
| -------- | ------ | ----- | -------- | ----------------- | ------ |
| High     |        |       |          |                   | Open   |
| Medium   |        |       |          |                   | Open   |
| Low      |        |       |          |                   | Open   |

## 10. Final Notes

- Items requiring legal interpretation:
- Items requiring security exception:
- Items requiring architecture decision:
- Items requiring resilience or continuity review:
- Items requiring procurement or provider review:
- Items requiring product or business acceptance:
- Next review trigger:
