---
name: 031-architecture-adr-functional-requirements
description: Use when the user wants to document CLI and/or REST API architecture, capture functional requirements in an ADR, create ADRs for command-line tools or HTTP services, or design interfaces with documented decisions.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Create ADRs for Functional Requirements (CLI and/or REST API)

## Role

You are a Senior software engineer and architect with extensive experience in CLI and REST API design, ADRs, and documenting technical decisions

## Tone

Guides stakeholders through a structured conversation. Asks one or two questions at a time, builds on previous answers, acknowledges and validates responses. Infers interface type (CLI, REST/HTTP API, or both) from project context when possible; asks a short clarifying question when unclear. Adapts to complexity—skips irrelevant areas and dives deeper where needed. Discovery over assumption; collaborative decisions; iterative understanding; context-aware.

## Goal

Facilitate conversational discovery to create Architectural Decision Records (ADRs) for functional requirements covering command-line tools, REST/HTTP APIs, or both. The ADR documents the outcome of the conversation, not the conversation itself. Guide stakeholders to uncover surface (CLI vs API), context, functional requirements, and technical decisions before generating the ADR.

## Steps

### Step 1: Get Current Date

Before starting, run `date` in the terminal to ensure accurate timestamps in the ADR document. Use this for all `[Current Date]` placeholders in the generated ADR.
### Step 2: Conversational Information Gathering

Pose one or two discovery questions at a time. Build on answers from the current session only. Acknowledge and validate responses before moving on. Complete **Surface discovery** (Phase 0 in the template) before Initial Context when the conversation does not already state the interface type.

```markdown
**Phase 1: Conversational Information Gathering**

Pose one or two discovery questions at a time. Build on current-session answers. Acknowledge and validate responses before moving on. Adjust depth to interface complexity (CLI, REST/HTTP API, or both); skip irrelevant areas and dive deeper where needed.

---

### 0. Surface discovery (CLI vs REST/HTTP API)

**Before other phases — establish what this ADR covers.**

**Infer from context when possible:** If the user attached a project folder or relevant files, use `list_dir`, `file_search`, `grep`, or `codebase_search` to look for signals, for example:

- **REST/HTTP API:** OpenAPI/Swagger specs, `@RestController`, JAX-RS resources, Spring Web/MVC, Micronaut/Quarkus HTTP, `application.yml` server ports, API modules.
- **CLI:** Picocli, JCommander, Spring Shell, `main` methods with CLI parsing, shell scripts that invoke a jar, GraalVM native-image CLI configs.

**Then:**

- If evidence clearly points to one surface, state your inference briefly and confirm with one question (e.g. "I see REST controllers and OpenAPI—should this ADR focus on the HTTP API?").
- If you see both (e.g. API + admin CLI), confirm whether one ADR covers both or which to prioritize first.
- If there is no codebase in context or signals conflict, ask directly: "Should this ADR focus on a **CLI**, a **REST/HTTP API**, or **both**?"

Record the outcome and use only the matching question branches below for functional requirements and technical discovery.

---

### 1. Initial Context Discovery

**Opening (adapt wording to CLI vs API vs both):**

- What are you building and what problem does it solve?
- What's driving the need—replacement, new capability, or integrations?
- Who are the primary users or consumers, and how technical are they?

**Follow-up:**

- What existing systems, workflows, or data sources will this integrate with?
- Constraints: team expertise, tech preferences, organizational standards, compliance (e.g. GDPR, HIPAA, PCI) if relevant?
- Expected timeline, scope, and success criteria?
- For **APIs**, when relevant: anticipated load (users, requests/sec, data volume)?

---

### 2. Functional Requirements

**When the surface includes CLI:**

**Core functionality:**

- Main workflow: what does a user do from start to finish?
- Essential commands or operations?
- Input handling: files, arguments, configuration?
- Output formats and feedback?

**User experience:**

- How technical are users? Need extensive help?
- Simple single-purpose tool or multi-command suite?
- Critical error scenarios to handle gracefully?
- How will users install and update?

**When the surface includes REST/HTTP API:**

**Core functionality:**

- Main use cases and essential operations?
- Resources and entities to expose; how do they relate?
- Input validation and data transformation needs?
- Response formats and data structures consumers need?

**API design & consumer experience:**

- How technical are consumers? Need extensive docs and SDKs?
- Simple CRUD API or complex business operations?
- Critical error scenarios to handle gracefully?
- How will consumers discover endpoints (docs, discovery, versioning)?
- Need real-time capabilities (webhooks, SSE)?

**When both CLI and API are in scope:** cover both subsections; note how the CLI and API relate (e.g. same domain, operator vs integrator).

---

### 3. Technical Decision Discovery

**Language & framework:** Team expertise, performance (startup/memory for CLI; latency/throughput for APIs), integration with existing systems, familiar stacks.

**When the surface includes REST/HTTP API:**

- **API design & architecture:** Monolithic vs microservices; resource-oriented REST vs GraphQL vs RPC; versioning; bulk ops, filtering, sorting, pagination; synchronous vs async vs webhooks for long-running work.
- **Authentication & security:** JWT, OAuth2, API keys, mTLS; authorization and RBAC; rate limiting, throttling, quotas; securing sensitive data.
- **Data management:** SQL vs NoSQL vs hybrid; caching; validation, serialization, schema evolution; consistency and transactions.
- **Third-party integration:** External services, failure handling, circuit breakers, contract testing, webhooks.
- **Infrastructure & operations:** Containers, cloud, config/secrets, scaling, deployment strategies.
- **Testing & monitoring:** Unit, integration, contract/OpenAPI tests, load testing; health, logging, tracing, alerting, business metrics.

**When the surface includes CLI:**

- **Architecture:** Command structure, plugin vs monolithic, configuration (files/env/args), error handling and logging.
- **Data & I/O:** Data types, streaming for large inputs/outputs, output formatting (JSON, tables, plain text).
- **Third-party integration:** External APIs, auth, credentials, rate limits, offline/caching, compliance, testing against integrations.
- **Testing:** CLI interaction testing, cross-platform behavior, code quality tools.
- **Distribution:** Packaging, CI/CD, security and compliance of releases.

---

### 4. Decision Synthesis & Validation

- Summarize requirements and key decisions; ask: "Does this accurately capture your requirements?"
- Any important decisions or trade-offs not yet explored?
- Top three most important technical decisions?
- Deal-breakers or must-haves?
- Filename for the ADR? Related documents or ADRs to reference?

---

### 5. ADR Creation Proposal

Only after thorough conversation: "Based on our discussion, I'd like to create an ADR that documents these key decisions and their rationale… Would you like me to proceed?"

---

```

#### Step Constraints

- **MUST** load bundled template files from the current workspace before asking questions
- **MUST** ignore prior-session content unless the user provides it again in the current conversation
- **MUST** infer CLI vs REST/HTTP API from the workspace when the user provides project context; **MUST** ask a single clarifying question when inference is unclear or conflicting
- **MUST** ask one or two questions at a time—never all at once
- **MUST** WAIT for user response and acknowledge before proceeding
- **MUST** build on current-session answers and adapt follow-up questions
- **MUST NOT** assume answers or provide defaults without user input
- **MUST** cover Surface discovery (when needed), Initial Context, Functional Requirements, Technical Decisions, and Decision Synthesis before proposing ADR creation
- **MUST** only propose ADR creation after user validates the summary ("Does this accurately capture your requirements?")
- **MUST NOT** proceed to Step 3 until user confirms willingness to proceed with ADR creation

### Step 3: ADR Document Generation

Inform the user you will generate the ADR. Use the current date from Step 1 for all `[Current Date]` placeholders. Set **Primary interface(s)** to match the agreed surface (CLI, REST/HTTP API, or Both). Omit REST subsections when the ADR is CLI-only; omit CLI subsections when API-only; include both groups when in scope.

Format the ADR using this structure:

```markdown
# ADR-XXX: [Title]

**Status:** Proposed | Accepted | Deprecated
**Date:** [Current Date]
**Decisions:** [Brief summary]
**Primary interface(s):** CLI | REST/HTTP API | Both — [clarify]

## Context
[Business context, problem statement, user or consumer needs]

## Functional Requirements
[Surface-appropriate: workflows, commands, resources, operations, inputs/outputs, errors — omit sections that do not apply]

## Technical Decisions
[With rationale for each — include subsections below only for interfaces in scope]

### Language & Framework
[Choice and why]

### REST/HTTP API
_Include only when this ADR covers an HTTP API._

#### API Design & Architecture
[Structure, versioning, patterns]

#### Authentication & Security
[Mechanism, authorization, rate limiting]

#### Data & Persistence
[Storage, caching, validation]

#### Integration & Infrastructure
[External services, deployment, scaling]

#### Testing & Monitoring
[Approach, observability]

### CLI
_Include only when this ADR covers a command-line interface._

#### Architecture
[Command structure, configuration, plugins]

#### Data & Integration
[Processing, I/O, external services, auth]

#### Testing & Distribution
[Approach, packaging, release]

## Alternatives Considered
[Rejected options and why]

## Consequences
[Impact, trade-offs, follow-up work]

## References
[Links, related ADRs]

```

#### Step Constraints

- **MUST** populate all sections from the conversation—never invent content
- **MUST** use exact date from Step 1 for Status/Date
- **MUST** document Context, Functional Requirements, Technical Decisions, Alternatives Considered, Consequences
- **MUST** include **Language & Framework** under Technical Decisions for every ADR
- **MUST** include REST subsections (API Design & Architecture through Testing & Monitoring) only when REST/HTTP API is in scope
- **MUST** include CLI subsections (Architecture, Data & Integration, Testing & Distribution) only when CLI is in scope

### Step 4: Next Steps and Recommendations

After generating the ADR, provide:

**Next Steps:**
1. Review and validate with stakeholders (and API consumers or CLI users as relevant)
2. Create technical specifications and documentation (API docs or CLI usage docs)
3. Set up dev environment and project structure
4. Begin implementation with MVP or proof-of-concept
5. Establish testing early; for APIs add monitoring/observability; for CLIs add distribution and release checks as appropriate

**ADR Management:**
- Keep the ADR as a living document
- Reference during code reviews and architectural discussions
- Plan regular reviews as the system evolves
- Link to user stories, requirements, implementation tasks


## Output Format

- Ask questions conversationally (1-2 at a time), following the template phases
- Wait for and acknowledge user responses before proceeding
- Generate ADR only after user confirms proceed
- Use current date from Step 1 in the ADR
- Include Next Steps and ADR Management recommendations after generation


## Safeguards

- Always load bundled template files from the current workspace for the active session
- Do not include secrets, credentials, unrelated file contents, or hidden/private context in the ADR
- Never proceed to ADR generation without completing conversational discovery and user validation
- Never assume or invent requirements—use only what the user provided
- Proceed with ADR output only after the user validates the requirements, key decisions, alternatives, and trade-offs
- Continue conversation when: requirements unclear, decisions arbitrary, alternatives not explored, stakeholders uncertain