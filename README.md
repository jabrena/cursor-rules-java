# Commands, Agents & Skills for Java

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
# Cursor
npx skills add jabrena/plinth --skill '*' --agent cursor -y

# Claude Code
npx skills add jabrena/plinth --skill '*' --agent claude-code -y

# Codex
npx skills add jabrena/plinth --skill '*' --agent codex -y

# GitHub Copilot
npx skills add jabrena/plinth --skill '*' --agent github-copilot -y
```

### See it in action

Ask your agent:

```text
Use @110-java-maven-best-practices to review this Maven project located in examples/@maven/maven-demo
Explain the findings, apply the approved improvements, and validate the build.
```

![](documentation/images/herdr-example.png)

The skill guides the agent through a structured Maven review while keeping you in control of proposed changes.

## 5-Minute Onboarding

Learn to use this project following the quick guide [Getting Started in 5 minutes](./documentation/guides/GETTING-STARTED-IN-5-MINUTES.md).

### Migrating from legacy rules

Current `System prompts/rules` are deprecated and will be removed in `v0.18.0`. If you still use them, review the [release 0.14.0 article](https://jabrena.github.io/plinth/blog/2026/04/release-0.14.0.html).

## Choose your path

Commands compose the workflow by routing work to the right agent and skill set:

### Analysis & Design

Turn an idea into an actionable change with user stories, GitHub Issues or Jira, ADRs, diagrams, AI plan mode, and OpenSpec.

**Functional Specification:**

```text
/update-issue
@robot-business-analyst
    @043-planning-github-issues
    @044-planning-jira
    @045-planning-azure-devops
    @014-agile-user-story

/explore-problem
@robot-business-analyst
    @021-problem-framing
    @022-root-cause-analysis
    @023-assumption-analysis
    @024-context-mapping
    @025-quality-attribute-discovery

/create-acceptance-criteria
@robot-business-analyst
    @058-design-bdd
```

**Technical Specification:**

```
/create-adr
@robot-architect
    @030-architecture-adr-general

/create-diagram
@robot-architect
    @033-architecture-diagrams

/create-spec
@robot-architect
    @042-planning-openspec

/explore-design
@robot-architect
    @051-design-two-steps-methods
    @052-design-hamburger-method
    @053-design-simple-rules
    @054-design-tdd
    @055-design-parallel-change
    @056-design-avoid-breaking-changes
    @057-design-feature-toggles
    @059-design-atdd
    @121-java-object-oriented-design
    @122-java-type-design
    @123-java-design-patterns
    @130-java-testing-strategies

MCP Servers
    Jbang-Quarkus-JDBC
    MongoDB
    JavaDocs
    Serena-LSP
    Grafana
```

### Build

Implement and improve Java applications with Maven, design, coding, testing, security, documentation, Spring Boot, Quarkus, Micronaut, OpenAPI, and WireMock guidance.

```text
/implement-spec
@robot-tech-lead
    /create-feature-branch
    /create-worktree
    @robot-java-coder
    @robot-java-spring-boot-coder
    @robot-java-quarkus-coder
    @robot-java-micronaut-coder
    @robot-no-java

MCP Servers
    Jbang-Quarkus-JDBC
    MongoDB
    JavaDocs
    Serena-LSP
```

### Operate

Measure and improve production behavior through observability, profiling, benchmarking, and performance testing.

```text
/profile
@robot-java-performance
    @161-java-profiling-detect
    @162-java-profiling-analyze
    @163-java-profiling-refactor
    @164-java-profiling-verify
/benchmark
@robot-java-performance
    @151-java-performance-jmeter
    @152-java-performance-gatling

MCP Servers
    Jbang-Quarkus-JDBC
    MongoDB
    Serena-LSP
    Grafana
```

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

Every push runs the following validation checks in [CI Builds](./.github/workflows/maven.yaml) to keep documentation and generated skills correct, consistent, and secure:

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

- Follow the [5-minute guide](./documentation/guides/GETTING-STARTED-IN-5-MINUTES.md) and tell us where the experience can improve.
- [Browse the skill inventory](./documentation/guides/INVENTORY-SKILLS-JAVA.md) and propose a missing Java workflow.
- [Open an issue](https://github.com/jabrena/plinth/issues) to report a problem or suggest an enhancement.
- Read [CONTRIBUTING.md](./CONTRIBUTING.md) to improve a skill, agent, command, or project guide.
- Star the repository if these workflows help your Java projects.

## Architecture Decision Records (ADR)

- Review the [ADR index](./documentation/adr/README.md) for the complete list.

## Java JEPs from Java 8 onward

Java uses JEPs (JDK Enhancement Proposals) to describe new language and platform features. This repository tracks which JEPs could improve the Skills and guidance here.

- [JEPs list](./documentation/jeps/All-JEPS.md)

## Further resources

Talks, articles, reference links, skill portals, and related projects live in [Project references](./documentation/guides/PROJECT-REFERENCES.md).

Developed by humans with support from [Cursor](https://www.cursor.com/) and [Codex](https://openai.com/codex/), with ❤️ from [Madrid](https://www.google.com/maps/place/Community+of+Madrid,+Madrid/@40.4983324,-6.3162283,8z/data=!3m1!4b1!4m6!3m5!1s0xd41817a40e033b9:0x10340f3be4bc880!8m2!3d40.4167088!4d-3.5812692!16zL20vMGo0eGc?entry=ttu&g_ep=EgoyMDI1MDgxOC4wIKXMDSoASAFQAw%3D%3D)
