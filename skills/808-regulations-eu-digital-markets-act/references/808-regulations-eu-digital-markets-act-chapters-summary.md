---
name: 808-regulations-eu-digital-markets-act-chapters-summary
description: Use as a chapter-by-chapter and article-by-article summary of Regulation (EU) 2022/1925 to enrich Java enterprise platform reviews with Digital Markets Act context.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# EU Digital Markets Act Regulation for Java Enterprise Gatekeeper Platform Controls

## Role

You are a senior Java enterprise architect and platform compliance reviewer using the Digital Markets Act article structure to map gatekeeper-platform regulatory themes to engineering controls and owner handoffs

## Goal

Summarize the official chapter, article, and annex structure of Regulation (EU) 2022/1925 (Digital Markets Act) for engineering review of Java enterprise systems, core platform service tooling, marketplaces, app stores, advertising systems, ranking systems, identity services, interoperability interfaces, business-user portals, CI/CD pipelines, and operational workflows.

Source reviewed: [EUR-Lex Regulation (EU) 2022/1925 HTML text](https://eur-lex.europa.eu/legal-content/EN/TXT/HTML/?uri=CELEX:32022R1925).

This reference is not legal advice. Use it to orient engineering discovery, architecture review, compliance evidence collection, release gates, and escalation conversations with legal, compliance, platform governance, product, privacy, security, risk, competition, executive accountability, and business owners.

Use `808-regulations-eu-digital-markets-act-engineering-examples.md` for Java examples and implementation control patterns. Keep this chapters summary focused on DMA Regulation structure and article-level obligations.

Report template asset: [DMA engineering review report template](../assets/reports/808-eu-digital-markets-act-engineering-review-report-template.md).

## [Chapter I: Subject matter, scope and definitions (Articles 1-2)](https://eur-lex.europa.eu/legal-content/EN/TXT/HTML/?uri=CELEX:32022R1925#cpt_I)

Sets the internal-market purpose, scope for core platform services provided by gatekeepers to EU business users or end users, relationship with other competition rules, and definitions. For Java teams, this chapter is the starting point for identifying whether a platform service, API, data flow, ranking system, advertising workflow, or interoperability interface may require DMA-aware evidence.

Article map:
- Article 1, Subject matter and scope: establishes harmonised rules for contestable and fair digital markets where gatekeepers are present, applies to core platform services offered to EU business users or end users, and clarifies relationship with national rules, competition rules, and electronic communications rules.
- Article 2, Definitions: defines gatekeeper, core platform service categories, business user, end user, ranking, data, personal data, interoperability, profiling, consent, online advertising services, operating systems, web browsers, virtual assistants, cloud computing services, and related platform concepts.

Engineering impact:
- Record whether the Java system supports a possible core platform service category such as online intermediation, app store, online search, social network, video-sharing platform, messaging, operating system, browser, virtual assistant, cloud service, or online advertising service.
- Record business-user and end-user journeys, platform owners, data owners, product owners, privacy owners, security owners, and compliance owners.
- Treat gatekeeper designation and core platform service classification as qualified owner decisions; engineering should collect evidence, not make legal determinations.

## [Chapter II: Gatekeepers (Articles 3-4)](https://eur-lex.europa.eu/legal-content/EN/TXT/HTML/?uri=CELEX:32022R1925#cpt_II)

Defines gatekeeper designation, quantitative thresholds, notification, designation decisions, review of gatekeeper status, and the listed core platform services for which obligations apply.

Article map:
- Article 3, Designation of gatekeepers: defines significant impact, important gateway, entrenched and durable position, quantitative thresholds, notification duties, Commission designation, market-investigation designation, and the six-month period for compliance with Articles 5, 6, and 7 after a core platform service is listed.
- Article 4, Review of the status of gatekeeper: enables the Commission to reconsider, amend, repeal, and periodically review designation decisions and listed core platform services.

Engineering impact:
- Maintain service inventories that map product capabilities, core platform service candidates, business-user counts, end-user counts, active-user calculation evidence, and listed service boundaries.
- Preserve traceable evidence when a Java system is added to, removed from, or materially changes a listed core platform service.
- Escalate gatekeeper designation, threshold calculations, core platform service listings, and status-review questions to legal, compliance, platform governance, product, and competition owners.

## [Chapter III: Practices of gatekeepers that limit contestability or are unfair (Articles 5-15)](https://eur-lex.europa.eu/legal-content/EN/TXT/HTML/?uri=CELEX:32022R1925#cpt_III)

Contains the primary engineering control surface. It covers consent-dependent personal-data processing, business-user terms, steering, tying, advertising transparency, non-public data use, app uninstall and defaults, third-party app stores, ranking self-preferencing, switching, interoperability, measurement tools, data portability, business-user data access, search data access, fair access terms, messaging interoperability, compliance reporting, anti-circumvention, concentration information, and profiling audits.

Article map:
- Article 5, Obligations for gatekeepers: includes restrictions on combining and cross-using personal data without consent, parity clauses, business-user communications, externally acquired content access, complaints to authorities, required gatekeeper payment or identity services, tying with other core platform services, and advertiser or publisher information.
- Article 6, Obligations susceptible of being further specified under Article 8: includes restrictions on using non-public business-user data in competition, uninstall and default-setting controls, third-party software and app-store access, ranking self-preferencing, switching restrictions, interoperability with operating-system or hardware features, advertising measurement tools, end-user data portability, business-user data access, search data access, fair access conditions, and termination conditions.
- Article 7, Interoperability of number-independent interpersonal communications services: requires requested interoperability for basic messaging and call functions, preserving security and end-to-end encryption where applicable, publishing reference offers, responding to reasonable requests, limiting exchanged personal data, and justifying security and privacy measures.
- Article 8, Compliance with obligations for gatekeepers: requires gatekeepers to ensure and demonstrate effective compliance with Articles 5, 6, and 7 and allows the Commission to specify effective measures.
- Article 9, Suspension: allows exceptional suspension of a specific obligation where compliance would endanger economic viability in the Union.
- Article 10, Exemption for grounds of public health and public security: allows exemption for specified grounds.
- Article 11, Reporting: requires a detailed and transparent compliance report and a non-confidential summary within six months of designation, updated at least annually.
- Article 12, Updating obligations for gatekeepers: empowers delegated acts to update obligations when new practices limit contestability or are unfair.
- Article 13, Anti-circumvention: prohibits service fragmentation, behaviour that undermines compliance, making consent burdensome, degrading quality for users exercising rights, non-neutral choices, and interface designs that subvert autonomy or free choice.
- Article 14, Obligation to inform about concentrations: requires gatekeepers to inform the Commission about intended concentrations involving core platform services, digital-sector services, or data-collection capabilities.
- Article 15, Obligation of an audit: requires an independently audited description of profiling techniques applied to or across listed core platform services and a public overview.

Engineering impact:
- Treat consent flows, preference records, business-user data access APIs, advertiser and publisher reporting, data portability, search data access, app-store access, default settings, ranking pipelines, messaging interoperability, and profiling descriptions as audit-relevant engineering surfaces.
- Require evidence for specific choices, opt-in and withdrawal, one-year consent retry limits, non-neutral UI avoidance, fair and non-discriminatory access conditions, ranking fairness, data lineage, API availability, security justifications, and annual compliance-report updates.
- Escalate self-preferencing assessments, consent interpretation, access-term fairness, suspension or exemption requests, concentration reporting, and profiling audit scope to qualified owners.

## [Chapter IV: Market investigation (Articles 16-19)](https://eur-lex.europa.eu/legal-content/EN/TXT/HTML/?uri=CELEX:32022R1925#cpt_IV)

Defines market investigations for designation, systematic non-compliance, and new services or practices.

Article map:
- Article 16, Opening of a market investigation: defines opening decisions and assistance by national competent authorities.
- Article 17, Market investigation for designating gatekeepers: covers designation through market investigation and identification of listed core platform services.
- Article 18, Market investigation into systematic non-compliance: covers repeated infringements, behavioural or structural remedies, and possible temporary concentration restrictions.
- Article 19, Market investigation into new services and new practices: covers adding core platform services or new obligations when practices limit contestability or are unfair.

Engineering impact:
- Keep service boundaries, user metrics, data flows, ranking logic, API controls, release histories, incidents, business-user complaints, and remediation evidence reviewable for potential investigations.
- Design compliance evidence so it can be exported without exposing secrets, personal data, business secrets, or unrelated confidential information.
- Treat repeated findings, ineffective mitigations, and missing control evidence as material release and governance risks.

## [Chapter V: Investigative, enforcement and monitoring powers (Articles 20-43)](https://eur-lex.europa.eu/legal-content/EN/TXT/HTML/?uri=CELEX:32022R1925#cpt_V)

Defines Commission powers for proceedings, information requests, interviews, inspections, interim measures, commitments, monitoring, third-party information, compliance functions, non-compliance decisions, fines, penalty payments, rights of defence, reporting, professional secrecy, cooperation with national authorities and courts, high-level group, representative actions, and breach reporting.

Article map:
- Articles 20-23: opening of proceedings, information requests, interviews, and inspections, including access to data, algorithms, testing information, organisation, IT systems, and business practices.
- Articles 24-28: interim measures, commitments, monitoring of obligations and measures, information by third parties, and independent compliance function with sufficient authority, resources, management access, and direct reporting.
- Articles 29-34: non-compliance decisions, fines up to 10 percent and repeat fines up to 20 percent of worldwide turnover, periodic penalty payments, limitation periods, right to be heard, and access to the file.
- Articles 35-40: annual reporting, professional secrecy, cooperation with national authorities, competition authorities, national courts, and the high-level group for the Digital Markets Act.
- Articles 41-43: Member State requests for market investigations, representative actions, and reporting of breaches with protection of reporting persons.

Engineering impact:
- Preserve tamper-evident data, algorithm, ranking, testing, API, interoperability, consent, preference, access-control, observability, and release evidence that can support qualified owner responses to information requests or monitoring.
- Do not hide compliance evidence in logs that leak secrets, personal data, or business secrets. Use controlled evidence repositories, access controls, retention policies, and redaction workflows.
- Ensure compliance functions can obtain technical evidence from Java teams without bypassing normal security, privacy, and change-control safeguards.

## [Chapter VI: Final provisions (Articles 44-54)](https://eur-lex.europa.eu/legal-content/EN/TXT/HTML/?uri=CELEX:32022R1925#cpt_VI)

Covers publication of decisions, Court of Justice review, implementing provisions, guidelines, standardisation, delegated acts, committee procedure, amendments to whistleblower and representative-action directives, review, entry into force, and application dates.

Article map:
- Articles 44-45: publication of decisions and review by the Court of Justice.
- Articles 46-50: implementing provisions, guidelines, standardisation, delegated acts, and committee procedure.
- Articles 51-52: amendments to Directive (EU) 2019/1937 and Directive (EU) 2020/1828.
- Article 53, Review: requires Commission evaluations by 3 May 2026 and every three years, including possible changes to scope, obligations, and enforcement.
- Article 54, Entry into force and application: sets application dates and direct applicability in Member States.

Engineering impact:
- Track Commission implementing acts, guidelines, standardisation work, audits, and review-cycle changes that may alter technical expectations for data access, interoperability, reporting, profiling descriptions, and evidence.
- Review long-lived Java systems when product scope, listed service boundaries, ranking models, interoperability features, business-user tooling, or data-sharing capabilities change.

## Annex map for engineering review

The annex specifies methodology for identifying and calculating active end users and active business users for each core platform service.

## Annex: Methodology for active users

Provides general rules, active end-user and active business-user concepts, submission responsibilities, under-counting and over-counting controls, and specific definitions by core platform service category.

Engineering impact:
- Keep active-user metrics traceable, explainable, reproducible, and reviewed by qualified owners before they are used for gatekeeper-scope handoff.
- Avoid service fragmentation or metric manipulation that could undermine Article 3 and Article 13 evidence.
- Preserve methodology documentation for logged-in and non-logged-in environments, alternate metrics, account-level business users, and service-boundary decisions.

## Engineering review focus

When reviewing a Java enterprise system for DMA-aware controls, connect article themes to evidence:
- Applicability and definitions: Articles 1-2
- Gatekeeper designation and service listing evidence: Articles 3-4 and Annex
- Consent, data combination, tying, steering, and advertising transparency: Article 5
- Business-user data, ranking, data portability, app-store access, search data, and fair access terms: Article 6
- Messaging interoperability and security preservation: Article 7
- Demonstrating compliance and compliance reporting: Articles 8 and 11
- Anti-circumvention and neutral choice design: Article 13
- Concentration notification and profiling audit evidence: Articles 14-15
- Market investigation and evolving obligations: Articles 16-19
- Monitoring, information requests, compliance function, non-compliance, penalties, and third-party reports: Articles 20-43
- Implementing acts, guidelines, standardisation, and review cycles: Articles 44-54

## Constraints

Use this reference as Digital Markets Act Regulation context for Java engineering review. Do not provide legal advice or replace review by legal, compliance, platform governance, product, privacy, security, risk, competition, executive accountability, or business owners.

- **NOT LEGAL ADVICE**: State when an article mapping requires legal, compliance, platform governance, product, privacy, security, risk, competition, or executive accountability review instead of giving legal conclusions
- **SUMMARY ONLY**: Keep this reference focused on DMA Regulation chapters, articles, annex, and engineering implications; use the engineering examples reference for Java patterns
- **ARTICLE MAPPING**: Map findings to DMA topic areas such as gatekeeper designation, core platform service scope, obligations, interoperability, data access, consent, ranking, anti-circumvention, monitoring, enforcement, or final provisions
- **OWNER ESCALATION**: Escalate gatekeeper designation, core platform service classification, obligation applicability, consent interpretation, self-preferencing assessments, fair access terms, suspension or exemption requests, and regulatory interpretation to qualified owners
- **EVIDENCE ORIENTATION**: Use article summaries to identify what engineering evidence is missing or weak, not to certify compliance


## Output Format

- **READ** this DMA chapters summary before applying Java examples or generating the review report
- **MAP** reviewed findings to DMA topic areas and article groups using only reviewed evidence
- **ESCALATE** legal applicability, gatekeeper designation, core platform service scope, obligation applicability, consent interpretation, self-preferencing assessment, fair access terms, and regulatory interpretation to qualified owners
- **HAND OFF** implementation control patterns to `808-regulations-eu-digital-markets-act-engineering-examples.md`


## Safeguards

- **DO NOT CERTIFY COMPLIANCE**: This reference supports engineering review and owner handoff only
- **CHECK OFFICIAL GUIDANCE**: Implementing acts, guidelines, standardisation, and Commission decisions may add or clarify technical expectations beyond this summary
- **KEEP FACTS GROUNDED**: Do not infer gatekeeper designation, core platform service scope, obligation applicability, self-preferencing conclusions, or consent validity without reviewed evidence and qualified owner input