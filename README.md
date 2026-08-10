# Plinth for Java

<a href="https://trendshift.io/repositories/15013" target="_blank"><img src="https://trendshift.io/api/badge/repositories/15013" alt="jabrena%2Fcursor-rules-java | Trendshift" style="width: 250px; height: 55px;" width="250" height="55"/></a>

[![CI Builds](https://github.com/jabrena/plinth/actions/workflows/maven.yaml/badge.svg)](https://github.com/jabrena/plinth/actions/workflows/maven.yaml)

> **Languages:** [Español](./README_ES.md) · [中文](./README_ZH.md)
>
> **Help this project grow:** [Become a sponsor](https://github.com/sponsors/jabrena)

## Goal

An opinionated AI-native workflow for evolving modern Java Enterprise `SDLC` practices through reusable `Skills`, `Agents`, `Commands` & `MCP servers`.

## What is a Plinth?

> A `plinth` represents the solid foundation or platform used to support statues or artworks in art and sculpture. It served as a structural and symbolic foundation for columns, statues, and entire temple podiums. Romans inherited the idea from Greek architecture but expanded its use to emphasize monumentality, hierarchy, and imperial power.

## Project at a glance

- 13 Commands
- 9 Agents
- 125 Skills

## Latest Updates

Explore the latest published content on https://jabrena.github.io/plinth/ and follow its evolution through new skills, improvements, and fixes in the [CHANGELOG](./CHANGELOG.md).

## Start in 60 seconds

Install every skill for your preferred agent:

```bash
npx skills add jabrena/plinth --skill '*' --agent cursor -y
npx skills add jabrena/plinth --skill '*' --agent claude-code -y
npx skills add jabrena/plinth --skill '*' --agent codex -y
npx skills add jabrena/plinth --skill '*' --agent github-copilot -y
```

Install every command for your prefered agent:

```bash
install @004-commands-installation cursor
install @004-commands-installation claude-code
install @004-commands-installation codex
install @004-commands-installation github-copilot
```

Install every agent for your prefered agent:

```text
install @005-agents-installation cursor
install @005-agents-installation claude-code
install @005-agents-installation codex
install @005-agents-installation github-copilot
```

### See it in action

You can use the project in 2 ways:

- Use the AI-Native development workflow
- Refactor your code with Skills

#### Using AI-Native development workflow

Prepare the repository with `/onboarding`, then identify an issue in your `Kanban` dashboard from `Atlasian Jira`, `Github Issues` or `Azure DevOps` and apply the following workflow:

```text
/onboarding
  |
  v
Issue
  |
  v
/update-issue --> /explore-problem --> /create-acceptance-criteria
  |
  v
/create-spec --> /explore-design
  |
  v
/implement-spec --> /close-spec
```

`/onboarding` establishes root `AGENTS.md` and one unambiguous OpenSpec project before issue selection. It preserves existing prerequisites; when OpenSpec is missing, you select its result path with `documentation/openspec` as the default.

##### Analysis & Design

Turn an idea into an actionable change with user stories, GitHub Issues or Jira, ADRs, diagrams, AI plan mode, and OpenSpec.

**Functional Specification:**

<table>
  <thead>
    <tr>
      <th>Command</th>
      <th>Explanation</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><code>/onboarding</code></td>
      <td>Establish root repository guidance and one unambiguous OpenSpec project before issue work.</td>
    </tr>
    <tr>
      <td><code>/update-issue</code></td>
      <td>Update an existing GitHub or Jira issue with a structured user story, acceptance criteria, and resource content.</td>
    </tr>
    <tr>
      <td><code>/explore-problem</code></td>
      <td>Evaluate an issue from five perspectives and post a Functional Specification comment on the issue.</td>
    </tr>
    <tr>
      <td><code>/create-acceptance-criteria</code></td>
      <td>Derive Gherkin acceptance criteria from a Functional Specification and post them as a separate issue comment.</td>
    </tr>
  </tbody>
</table>

**Technical Specification:**

<table>
  <thead>
    <tr>
      <th>Command</th>
      <th>Explanation</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><code>/create-adr</code> (Optional)</td>
      <td>Record an architectural decision, its alternatives, rationale, and consequences.</td>
    </tr>
    <tr>
      <td><code>/create-diagram</code> (Optional)</td>
      <td>Create a focused architecture or design diagram from approved artifacts.</td>
    </tr>
    <tr>
      <td><code>/create-spec</code> (OpenSpec)</td>
      <td>Create or update one or more validated OpenSpec changes.</td>
    </tr>
    <tr>
      <td><code>/explore-design</code></td>
      <td>Compare technical approaches and obtain an approved design direction.</td>
    </tr>
  </tbody>
</table>

##### Build

Implement and improve Java applications with Maven, design, coding, testing, security, documentation, Spring Boot, Quarkus, Micronaut, OpenAPI, and WireMock guidance.

<table>
  <thead>
    <tr>
      <th>Command</th>
      <th>Explanation</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><code>/implement-spec</code></td>
      <td>Deliver an approved plan or validated OpenSpec task list through framework-aware delegation.</td>
    </tr>
    <tr>
      <td><code>/close-spec</code></td>
      <td>Archive an OpenSpec change by name using the OpenSpec CLI.</td>
    </tr>
  </tbody>
</table>

### Operate

Measure and improve production behavior through observability, profiling, benchmarking, and performance testing.

<table>
  <thead>
    <tr>
      <th>Command</th>
      <th>Explanation</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><code>/profile</code></td>
      <td>Coordinate Java profiling from baseline detection through verified optimization.</td>
    </tr>
    <tr>
      <td><code>/benchmark</code></td>
      <td>Select and coordinate JMeter, Gatling, or JMH performance workflows.</td>
    </tr>
  </tbody>
</table>

### Compliance (Alpha)

Review Java systems, AI models, and how GenAI tools are used across applications and delivery pipelines for regulation-aware engineering controls, evidence, and qualified owner handoffs spanning AI, data, security, product, platform, market, and governance. **<u>These skills support engineering awareness and do not provide legal advice.</u>**

| Regulation | Skill |
| --- | --- |
| [EU AI Act](https://eur-lex.europa.eu/legal-content/EN/TXT/HTML/?uri=OJ:L_202401689) | `801-regulations-eu-ai-act` |
| [DORA](https://eur-lex.europa.eu/legal-content/EN/TXT/HTML/?uri=CELEX:32022R2554) | `802-regulations-dora` |
| [GDPR](https://eur-lex.europa.eu/legal-content/EN/TXT/HTML/?uri=CELEX:32016R0679) | `803-regulations-gdpr` |
| [NIS2](https://eur-lex.europa.eu/legal-content/EN/TXT/HTML/?uri=CELEX:32022L2555) | `804-regulations-eu-nis2` |
| [Cyber Resilience Act](https://eur-lex.europa.eu/legal-content/EN/TXT/HTML/?uri=CELEX:32024R2847) | `805-regulations-eu-cyber-resilience-act` |
| [Data Act](https://eur-lex.europa.eu/legal-content/EN/TXT/HTML/?uri=CELEX:32023R2854) | `806-regulations-eu-data-act` |
| [Digital Services Act](https://eur-lex.europa.eu/legal-content/EN/TXT/HTML/?uri=CELEX:32022R2065) | `807-regulations-eu-digital-services-act` |
| [Digital Markets Act](https://eur-lex.europa.eu/legal-content/EN/TXT/HTML/?uri=CELEX:32022R1925) | `808-regulations-eu-digital-markets-act` |
| [MiFID II](https://eur-lex.europa.eu/eli/dir/2014/65/oj/eng) | `810-regulations-eu-mifid-ii` |
| [Market Abuse Regulation](https://eur-lex.europa.eu/eli/reg/2014/596/oj/eng) | `811-regulations-eu-market-abuse-regulation` |
| [Product Liability Directive](https://eur-lex.europa.eu/eli/dir/2024/2853/oj/eng) | `812-regulations-eu-product-liability-directive` |

**Note:** This set of skills could be a good complement for the future [OWASP EU Compliance MCP](https://genai.owasp.org/solution/eu-compliance-mcp/).

#### Refactor your code with Skill

Ask your agent:

```text
Use @110-java-maven-best-practices to review this Maven project located in examples/@maven/maven-demo
Explain the findings, apply the approved improvements, and validate the build.
```

The skill guides the agent through a structured Maven review while keeping you in control of proposed changes.

## 5-Minute Onboarding

Learn to use this project following the quick guide [Getting Started in 5 minutes](./documentation/guides/GETTING-STARTED-IN-5-MINUTES.md).

Explore the complete [Commands](./documentation/guides/INVENTORY-COMMANDS-JAVA.md), [Agents](./documentation/guides/INVENTORY-AGENTS-JAVA.md), [Skills](./documentation/guides/INVENTORY-SKILLS-JAVA.md), and [MCP Servers](./documentation/guides/THIRD-PARTIES.md) inventories.

## Project Components

The project generates a set of deliverables at the end of any iteration.

| Inventory     | Installation                                                                                    | Getting Started                                                                           |
| --------------- | -------------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------- |
| 1. [Commands](./documentation/guides/INVENTORY-COMMANDS-JAVA.md) | `@004-commands-installation` Install Commands in project | [`Commands`](./documentation/guides/COMMANDS.md) |
| 2. [Agents](./documentation/guides/INVENTORY-AGENTS-JAVA.md) | `@005-agents-installation` Install Agents in Cursor/Claude | [`Agents`](./documentation/guides/GETTING-STARTED-AGENTS.md)     |
| 3. [Skills](./documentation/guides/INVENTORY-SKILLS-JAVA.md) | `npx skills add jabrena/plinth --skill '*' --agent cursor -y` | [`Skills`](./documentation/guides/GETTING-STARTED-SKILLS.md)     |

### Compatibility

This project is compatible with any tool that supports `Commands`, `Agents`, `Skills`, `MCP Servers` and `AGENTS.md`.

## Skill Validations

Every push runs the following validation checks in the [Skill Scanners](./.github/workflows/skill-scanners.yml) as part of the CI Pipeline to keep documentation and generated skills correct, consistent, and secure:

| Name | Purpose |
| --- | --- |
| 1. [MarkdownValidator](./markdown-validator/src/main/java/info/jab/mv/MarkdownValidator.java) | Protects the documentation layer by catching Markdown parsing drift and remote link failures before skill-specific checks run. |
| 2. [skill-check](https://github.com/thedaviddias/skill-check) | Confirms every generated skill follows the expected packaging contract, complementing scanners that focus on behavior or security risk. |
| 3. [cisco-ai-skill-scanner](https://github.com/cisco-ai-defense/skill-scanner) by Cisco | Adds behavior-oriented security coverage by looking for risky skill flows that structural validation cannot see. |
| 4. [SkillSpector](https://github.com/NVIDIA/SkillSpector) by NVIDIA | Provides an independent static quality and security review, useful for comparing findings against the other scanners. |
| 5. [Snyk Agent Scan](https://github.com/snyk/agent-scan) by SNYK | Focuses on agent-skill supply-chain and prompt-risk signals, adding another security perspective alongside Cisco and SkillSpector. |

## Limitations

### Lack of determinism

From the outset, be aware that results from interactions with these `Skills` and agents are not deterministic because of how the models behave, but you can mitigate that with clear goals and validation checkpoints.

### Not all models behave in the same way

Some interactive skills require `Premium` models for interactive use; otherwise they follow a fixed sequence of steps.

### Limits of interactions with models

Models can generate code, but they cannot execute it against your local data. To bridge that gap, some Skills include scripts you run locally.

### Software engineers must remain in the loop

This project supports software engineering work; it does not replace engineering judgment. A software engineer must review, guide, and validate AI-generated decisions, code, and outcomes before they are used.

### Access to corporate data

Use caution when a problem involves corporate databases or other sensitive organizational data. Before granting an AI-assisted workflow access, assess authorization, privacy, data leakage, retention, and unintended modification risks. Apply least-privilege access, human review, validation, and monitoring. See [OWASP GenAI Data Security Risks & Mitigations 2026](https://genai.owasp.org/resource/owasp-genai-data-security-risks-mitigations-2026/), and the new set of [skills about EU regulation](#compliance-alpha).

## Contribute

See [CONTRIBUTING.md](./CONTRIBUTING.md) for ways to support and improve the project.

## Architecture Decision Records (ADR)

- Review the [ADR index](./documentation/adr/README.md) for the complete list.

## Java JEPs from Java 8 onward

Java uses JEPs (JDK Enhancement Proposals) to describe new language and platform features. This repository tracks which JEPs could improve the Skills and guidance here.

- [JEPs list](./documentation/jeps/All-JEPS.md)

## Further resources

Talks, articles, reference links, skill portals, and related projects live in [Project references](./documentation/guides/PROJECT-REFERENCES.md).

Developed by humans with support from [Cursor](https://www.cursor.com/) and [Codex](https://openai.com/codex/), with ❤️ from [Madrid](https://www.google.com/maps/place/Community+of+Madrid,+Madrid/@40.4983324,-6.3162283,8z/data=!3m1!4b1!4m6!3m5!1s0xd41817a40e033b9:0x10340f3be4bc880!8m2!3d40.4167088!4d-3.5812692!16zL20vMGo0eGc?entry=ttu&g_ep=EgoyMDI1MDgxOC4wIKXMDSoASAFQAw%3D%3D)
