# Agile Development Guide

Use the following step-by-step process to implement a complete agile development workflow using Cursor Rules.

## Process Overview

### Phase 1: Requirements Analysis & Agile Artifacts

This phase transforms initial requirements into structured agile artifacts, starting with high-level epics and progressively breaking them down into actionable features and user stories.
The goal is to establish a clear understanding of what needs to be built through epics, features, and detailed user stories with acceptance criteria in Gherkin format.

| Activity | Done | Prompt | Notes |
|----------|------|--------|-------|
| Create an Epic about the development | [ ] | `Create an agile epic based the initial documentation received and use the cursor rule @2001-agile-create-an-epic` | Attach the initial free format text/markdown document describing the problem to solve |
| Create a Feature about the development | [ ] | `Create a feature based on the epic and use the cursor rule @2002-agile-create-features-from-epics` | Attach the EPIC created previously. Review if the rule generates several features and maybe it is possible to merge into a single one. If you prefer to have only one feature, ask it. |
| Create User Story and Acceptance Criteria in Gherkin format | [ ] | `Create a user story based on the feature and the acceptance criteria using the cursor rule @2003-agile-create-user-stories` | Attach the EPIC and the Feature created previously |

### Phase 2: Technical Design & Architecture

This phase creates visual representations of the solution architecture using UML sequence diagrams to model interactions and C4 diagrams for system structure.
The focus is on translating agile requirements into technical designs that clearly communicate system behavior and architectural components.

| Activity | Done | Prompt | Notes |
|----------|------|--------|-------|
| Create UML Sequence diagram about functional requirements | [ ] | `Create the UML sequence diagram based in plantuml format using the information provided with the cursor rule @2004-uml-sequence-diagram-from-agile-artifacts` | Attach the EPIC, Feature, User Story & Gherkin created previously. You can use `jbang --fresh puml-to-png@jabrena --watch YOUR_REQS_DIRECTORY` to generate PNG. Ask for simplified version if needed: `Can you create the diagram again with less detail` |
| Create C4 Model diagrams based on requirements | [ ] | `Create the C4 Model diagrams from the requirements in plantuml format using the cursor rule @2005-c4-diagrams-about-solution` | Attach the EPIC, Feature, User Story, Gherkin & UML Sequence diagram created previously. Review the diagrams, sometimes it is necessary to simplify the models. Review for incoherences and check previous documents (Epic, Feature or User story) if issues exist |

### Phase 3: Architecture Decision Records (ADRs)

This phase documents critical architectural decisions including functional requirements approach (CLI or REST API), acceptance testing strategies, and non-functional requirements.
ADRs provide structured documentation that captures the rationale behind key technical choices to guide implementation and future maintenance.

| Activity | Done | Prompt | Notes |
|----------|------|--------|-------|
| Create ADR about functional requirements | [ ] | **CLI Development:** `Create the ADR about functional requirements using the cursor rule @2006-adr-create-functional-requirements-for-cli-development` <br> **REST API Development:** `Create the ADR about the functional requirements using the cursor rule @2006-adr-create-functional-requirements-for-rest-api-development` | Attach the EPIC, Feature, User Story, Gherkin, UML Sequence diagram & C4 Model diagrams created previously. Choose between CLI or REST API development approach |
| Create ADR about acceptance testing strategy | [ ] | `Create the ADR about the acceptance testing strategy using the cursor rule @2007-adr-create-acceptance-testing-strategy` | Attach User Story & Gherkin created previously |
| Create ADR about non-functional requirements based on ISO/IEC 25010:2023 quality model | [ ] | `Create the ADR about the non functional requirements using the information provided using the cursor rule @2008-adr-create-non-functional-requirements-decisions` | Attach the EPIC, Feature, User Story, Gherkin, UML Sequence diagram & C4 Model diagrams created previously |

### Phase 4: Solution planning

This phase converts all previous analysis and design work into actionable implementation plans through detailed task lists and Definition of Done criteria.
The goal is to establish clear execution roadmaps with quality gates that ensure systematic development following Outside-in TDD practices.

| Activity | Done | Prompt | Notes |
|----------|------|--------|-------|
| Create task list based on Agile analysis & Technical design | [ ] | `create task list with @2100-create-task-list.md using documents @agile @design` | Review the high level design if you agree and later continue with the process for the sublist typing "Go". Review if the planning task is oriented to implement Outside-in-TDD London. Review that all blocks from the first completed ATDD cycle include a task to verify that everything works. |
| Create Definition of Done (DoD) | [ ] | `Create the Definition of Done using the cursor rule @2102-agile-create-dor.mdc` | Attach User Story, Gherkin scenarios, and related ADRs. Ensures all acceptance criteria, technical requirements, and quality standards are met before story completion |
| Start implementation with task list management | [ ] | `Start with task 0.1 using the cursor rule @2101-implement-task-list.mdc` | Begin executing the task list created in the previous step |

### Phase 5: Solution Review & Refactoring

Perform a comprehensive review of the implemented solution to assess stability and completeness

| Activity | Done | Prompt | Notes |
|----------|------|--------|-------|
| Create UML class diagram | [ ] | `Create the UML diagram based on @example/implementation/src/main/java using the cursor rule @2200-uml-class-diagram.mdc` | Once you have a stable solution, review design aspects and identify potential improvements |
| Refactor the initial stable solution | [ ] | No recipe, this is the added value of a good SSE. `¯\_(ツ)_/¯` | Apply engineering expertise to improve code quality, design patterns, and architecture based on the UML analysis |

---

## Available Cursor Rules Reference

| Rule ID | Purpose | When to Use |
|---------|---------|-------------|
| @2000-agile-checklist | Create a Checklist with all agile steps | Starting any agile development process |
| @2001-agile-create-an-epic | Create agile epics | Start of project with initial requirements |
| @2002-agile-create-features-from-epics | Create agile features from an epic | After epic is created and approved |
| @2003-agile-create-user-stories | Create Agile User stories with Gherkin | After features are defined |
| @2004-uml-sequence-diagram-from-agile-artifacts | Create UML Sequence Diagrams | After user stories are complete |
| @2005-c4-diagrams-about-solution | Create C4 Diagrams | For architectural overview |
| @2006-adr-create-functional-requirements-for-cli-development | Create ADR for CLI Development | For command-line applications |
| @2006-adr-create-functional-requirements-for-rest-api-development | Create ADR for REST API Implementation | For REST API development |
| @2007-adr-create-acceptance-testing-strategy | Create ADR for Acceptance Testing Strategy | After user stories with Gherkin |
| @2008-adr-create-non-functional-requirements-decisions | Create ADR for Non-Functional Requirements | After technical design phase |
| @2100-create-task-list | Generate detailed task lists from agile artifacts | After completing Phase 3 (ADRs) |
| @2101-implement-task-list | Task List Management | When working with task lists to track implementation progress |
| @2102-agile-create-dor | Agile Definition of Done (DoD) Creation | When creating completion criteria and quality gates for user stories |
| @2200-uml-class-diagram | Create UML Class Diagram for Java Projects | For reviewing Java solution architecture |
| @2300-adr-conversational-assistant | ADR Conversational Assistant | For interactive ADR creation and refinement |

## Tips for Success

### Best Practices
- **Always attach the required documents** mentioned in each step's note section
- **Follow the sequence** - each step builds on the previous ones
- **Review and refine** - Don't hesitate to ask for simplifications or modifications
- **Keep artifacts updated** - Maintain consistency across all documents

### Common Pitfalls to Avoid
- Skipping the attachment of previous artifacts when required
- Not reviewing generated features for potential consolidation
- Creating overly complex C4 diagrams (simplify when needed)
- Forgetting to choose between CLI or REST API ADR templates
