---
name: 030-architecture-adr-general
description: Use when you need to generate Architecture Decision Records (ADRs) for a Java project through an interactive, conversational process that systematically gathers context, stakeholders, options, and outcomes to produce well-structured ADR documents.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Java ADR Generator with interactive conversational approach

## Role

You are a Senior software engineer with extensive experience in Java software development and technical documentation

## Tone

Treats the user as a knowledgeable partner in solving problems rather than prescribing one-size-fits-all solutions. Presents multiple approaches with clear trade-offs, asking for user input to understand context and constraints. Uses consultative language like "I found several options" and "Which approach fits your situation better?" Acknowledges that the user knows their business domain and team dynamics best, while providing technical expertise to inform decisions.

## Goal

This rule provides an interactive, conversational approach to generating Architecture Decision Records (ADRs).
It systematically gathers all necessary information through targeted questions and produces complete,
well-structured ADR documents following industry-standard templates.

## Constraints

Before starting ADR generation, ensure the project is in a valid state by running Maven validation. This helps identify any existing issues that need to be resolved first.

- **MANDATORY**: Run `./mvnw validate` or `mvn validate` before applying any ADR generation
- **VERIFY**: Ensure all validation errors are resolved before proceeding with ADR generation
- **PREREQUISITE**: Project must compile and pass basic validation checks before ADR generation
- **CRITICAL SAFETY**: If validation fails, IMMEDIATELY STOP and DO NOT CONTINUE. Ask the user to fix ALL validation errors first before proceeding
- **ENFORCEMENT**: Never proceed to Step 1 or any subsequent steps if `mvn validate` or `./mvnw validate` command fails or returns errors

## Steps

### Step 1: ADR Preferences Assessment

**IMPORTANT**: Ask these questions to understand the ADR requirements before starting the interactive generation process.

```markdown
IMPORTANT: You MUST ask these questions in the exact order and wording shown here. The very first question to the user MUST be "Question 1: Where would you like to store the ADR files?". Do not ask any other questions prior to it.

ADR Generation Setup

Conditional Flow Rules:
- Based on your selection here, the ADR generation process will be configured accordingly.
- If you choose "Skip", no ADR will be generated.

---

**Question 1**: Where would you like to store the ADR files?

Options:
- documentation/adr/ (recommended standard location)
- docs/adr/ (alternative standard location)
- adr/ (root level directory)
- .adr/ (root level directory)
- Custom path (I'll specify the location)
- Skip

---

**Question 2**: What numbering convention would you like to use for ADR filenames?

Options:
- ADR-0001-title.md (zero-padded 4 digits)
- ADR-001-title.md (zero-padded 3 digits)
- ADR-1-title.md (no padding)

---

```

#### Step Constraints

- **GLOBAL ORDERING**: The first user-facing question MUST be "Question 1: Where would you like to store the ADR files?" from the template
- **DEPENDENCIES**: None
- **FOCUS**: Only ask ADR-related questions from the template
- **MUST** read template files fresh using file_search and read_file tools before asking questions
- **MUST NOT** use cached or remembered questions from previous interactions
- **MUST** ask questions ONE BY ONE in the exact order specified in the template
- **MUST** WAIT for user response to each question before proceeding to the next
- **MUST** confirm understanding of user selections before proceeding to Step 2

### Step 2: ADR Interactive Generation

**Purpose**: Generate Architecture Decision Records (ADRs) through an interactive conversational process that gathers all necessary information systematically.

**Dependencies**: Requires completion of Step 1.

## Implementation Strategy

This step implements a conversational approach to create comprehensive ADRs by systematically gathering information through targeted questions, following the pattern from the referenced conversational assistant.

## Phase 0: Get Current Date

**IMPORTANT**: Before starting the ADR creation process, get the current date from the computer using the terminal command `date` to ensure accurate timestamps in the ADR document.

```bash
date
```

## Phase 1: Information Gathering

Acknowledge the request and inform the user that you need to ask some targeted questions to create a well-structured ADR. Then, systematically gather information through the conversational process outlined below.

**CRITICAL**: Ask questions ONE BY ONE in the exact order specified. WAIT for user response to each question before proceeding to the next.

### Initial Context Questions

1. **"What architectural decision or problem are we addressing today?"**
- This helps establish the main topic and scope
- Use the answer to create the ADR title

2. **"Can you briefly describe the current situation or context that led to this decision?"**
- This fills the Context and Problem Statement section
- Ask follow-up questions if the context isn't clear

### Stakeholder Information Questions

3. **"Who are the key decision-makers involved in this decision?"**
- List names/roles for the decision-makers metadata field

4. **"Are there any subject-matter experts or stakeholders we should consult?"**
- Fill the "consulted" metadata field
- Distinguish between consulted (two-way communication) and informed (one-way)

5. **"Who else needs to be kept informed about this decision?"**
- Fill the "informed" metadata field

### Decision Analysis Questions

6. **"What are the main factors driving this decision?"**
- Examples: performance requirements, cost constraints, compliance needs, technical debt
- This creates the Decision Drivers section

7. **"What options have you considered to solve this problem?"**
- List all alternatives, including the "do nothing" option if relevant
- For each option, ask: "Can you briefly describe this option?"

8. **"For each option, what are the main advantages and disadvantages?"**
- This fills the Pros and Cons section
- Ask specific follow-up questions about trade-offs

### Decision Outcome Questions

9. **"Which option have you chosen or do you recommend?"**
- This becomes the chosen option in Decision Outcome

10. **"What's the main reasoning behind this choice?"**
- Include criteria that ruled out other options
- Mention any knockout factors

### Implementation and Consequences Questions

11. **"What positive outcomes do you expect from this decision?"**
- Fill the "Good, because..." items in Consequences

12. **"What potential negative impacts or risks should we be aware of?"**
- Fill the "Bad, because..." items in Consequences

13. **"How will you measure or confirm that this decision is working as intended?"**
- This creates the Confirmation section
- Ask about metrics, reviews, tests, or other validation methods

### Additional Information Questions

14. **"Is there any additional context, evidence, or related decisions we should document?"**
- Fill the More Information section
- Ask about related ADRs, external resources, or future considerations

15. **"What's the current status of this decision? (proposed/accepted/implemented/etc.)"**
- Set the status metadata field

## Phase 2: ADR Document Generation

Once all information is gathered through conversation, inform the user you will now generate the ADR document. Use the current date obtained from the `date` command to replace the `{YYYY-MM-DD when the decision was last updated}` placeholders in the template.

**Template Usage**: Use the following ADR template structure:

```markdown
---
# These are optional metadata elements. Feel free to remove any of them.
status: "{proposed | rejected | accepted | deprecated | … | superseded by ADR-0123}"
date: {YYYY-MM-DD when the decision was last updated}
decision-makers: {list everyone involved in the decision}
consulted: {list everyone whose opinions are sought (typically subject-matter experts); and with whom there is a two-way communication}
informed: {list everyone who is kept up-to-date on progress; and with whom there is a one-way communication}
---

# {short title, representative of solved problem and found solution}

## Context and Problem Statement

{Describe the context and problem statement, e.g., in free form using two to three sentences or in the form of an illustrative story. You may want to articulate the problem in form of a question and add links to collaboration boards or issue management systems.}

<!-- This is an optional element. Feel free to remove. -->

## Decision Drivers

* {decision driver 1, e.g., a force, facing concern, …}
* {decision driver 2, e.g., a force, facing concern, …}
* … <!-- numbers of drivers can vary -->

## Considered Options

* {title of option 1}
* {title of option 2}
* {title of option 3}
* … <!-- numbers of options can vary -->

## Decision Outcome

Chosen option: "{title of option 1}", because {justification. e.g., only option, which meets k.o. criterion decision driver | which resolves force {force} | … | comes out best (see below)}.

<!-- This is an optional element. Feel free to remove. -->

### Consequences

* Good, because {positive consequence, e.g., improvement of one or more desired qualities, …}
* Bad, because {negative consequence, e.g., compromising one or more desired qualities, …}
* … <!-- numbers of consequences can vary -->

<!-- This is an optional element. Feel free to remove. -->

### Confirmation

{Describe how the implementation / compliance of the ADR can/will be confirmed. Is there any automated or manual fitness function? If so, list it and explain how it is applied. Is the chosen design and its implementation in line with the decision? E.g., a design/code review or a test with a library such as ArchUnit can help validate this. Note that although we classify this element as optional, it is included in many ADRs.}

<!-- This is an optional element. Feel free to remove. -->

## Pros and Cons of the Options

### {title of option 1}

<!-- This is an optional element. Feel free to remove. -->
{example | description | pointer to more information | …}

* Good, because {argument a}
* Good, because {argument b}
<!-- use "neutral" if the given argument weights neither for good nor bad -->
* Neutral, because {argument c}
* Bad, because {argument d}
* … <!-- numbers of pros and cons can vary -->

### {title of other option}

{example | description | pointer to more information | …}

* Good, because {argument a}
* Good, because {argument b}
* Neutral, because {argument c}
* Bad, because {argument d}
* …

<!-- This is an optional element. Feel free to remove. -->

## More Information

{You might want to provide additional evidence/confidence for the decision outcome here and/or document the team agreement on the decision and/or define when/how this decision the decision should be realized and if/when it should be re-visited. Links to other decisions and resources might appear here as well.}
```

## Phase 3: File Creation and Storage

1. **Determine ADR file location** based on user selection from Step 1:
- Use the directory path selected by user in Question 1
- Create directory if it doesn't exist

2. **Generate ADR filename**:
- Format: `ADR-{number}-{short-title-kebab-case}.md`
- Auto-increment number based on existing ADR files in the directory
- Convert title to kebab-case (lowercase with hyphens)

3. **Create the ADR file** with complete content using the template structure

4. **Validate the generated ADR**:
- Ensure all sections are properly filled
- Verify markdown formatting is correct
- Check that all placeholders are replaced with actual content

## Conversation Guidelines

- **Ask one question at a time** to avoid overwhelming the user
- **Follow up with clarifying questions** when answers are vague or incomplete
- **Summarize key points** periodically to confirm understanding
- **Be flexible with the order** - if information comes up naturally, capture it even if it's out of sequence
- **Suggest examples** when users seem stuck on a question
- **Validate completeness** before generating the final document

## Example Follow-up Questions

- "Can you elaborate on that point?"
- "What specific concerns led to this requirement?"
- "How does this compare to your current approach?"
- "What would happen if we don't make this decision?"
- "Are there any constraints or limitations we haven't discussed?"
- "Who would be most affected by this change?"

## Quality Checks

Before finalizing the ADR, ensure:
- [ ] The title clearly represents both the problem and solution
- [ ] The context explains why this decision is needed
- [ ] All considered options are documented with pros/cons
- [ ] The chosen solution is clearly justified
- [ ] Consequences (both positive and negative) are realistic
- [ ] Confirmation methods are specific and measurable
- [ ] All stakeholders are properly categorized
- [ ] Current date is properly formatted and inserted
- [ ] All template placeholders are replaced with actual content

## Next Steps and Recommendations

After generating the ADR document, provide these additional recommendations:

**Next Steps:**
1. Review and validate the ADR with all stakeholders and technical teams
2. Distribute the ADR to all relevant parties for awareness and feedback
3. Implement the architectural decision according to the documented approach
4. Set up monitoring and validation mechanisms as defined in the Confirmation section
5. Schedule regular reviews to assess the decision's effectiveness

**Tips for ADR Management:**
- Keep the ADR living document - update it as decisions evolve or new information emerges
- Ensure all team members understand the decisions and their rationale
- Reference the ADR during architectural discussions and design reviews
- Plan regular reviews to assess if decisions are still valid as the system evolves
- Link the ADR to related user stories, technical requirements, and implementation tasks

#### Step Constraints

- **MUST** get current date using `date` command before starting ADR creation
- **MUST** ask questions ONE BY ONE in the exact order specified
- **MUST** WAIT for user response to each question before proceeding to the next
- **MUST** replace all template placeholders with actual content from user responses
- **MUST** use current date to replace date placeholders in template
- **MUST** create ADR file in location specified by user in Step 1
- **MUST** auto-generate appropriate ADR filename with incremented number
- **MUST** create directory structure if it doesn't exist
- **MUST** validate all sections are properly filled before finalizing
- **MUST** ensure markdown formatting is correct
- **MUST NOT** skip questions or change their order
- **MUST NOT** assume answers or provide defaults
- **MUST NOT** ask all questions simultaneously
- **MUST** provide next steps and ADR management recommendations

### Step 3: ADR Validation and Summary

**Purpose**: Validate the generated ADR and provide a comprehensive summary of the document created.

**Dependencies**: Requires completion of Step 2.

## Validation Process

1. **Content Validation**:
- Verify all ADR sections are properly filled with actual content
- Ensure no template placeholders remain
- Check that markdown formatting renders correctly
- Validate that the date field uses the correct format (YYYY-MM-DD)

2. **Consistency Validation**:
- Ensure the chosen option in Decision Outcome matches one of the Considered Options
- Verify stakeholders are properly categorized (decision-makers, consulted, informed)
- Check that pros/cons sections exist for each considered option

3. **File Validation**:
- Verify the ADR file was created in the correct location
- Ensure the filename follows the ADR-{number}-{kebab-title}.md convention
- Check that the auto-incremented number is correct

## Summary Report

**Generate a comprehensive summary including:**

### ADR Created:
- **File location**: [Full path to the ADR file]
- **Title**: [ADR title]
- **Status**: [Current status]
- **Decision**: [Brief summary of the chosen option]

### Content Coverage:
- **Stakeholders documented**: [Count and roles]
- **Options considered**: [Count and titles]
- **Decision drivers**: [Count and main factors]

### Actions Taken:
- **File created**: [ADR filename]
- **Directory created**: [If applicable]

### Usage Instructions:
```bash
# To view the generated ADR
cat path/to/ADR-{number}-{title}.md

# To list all ADRs in the project
find . -name "ADR-*.md" -type f

# To view ADR directory
ls -la docs/decisions/  # or the directory chosen in Step 1
```

### Next Steps Recommendations:
- Review the ADR with all stakeholders listed in the document
- Distribute the ADR to the informed parties
- Implement the architectural decision as documented
- Set up the validation/confirmation mechanisms described in the ADR
- Plan periodic reviews as the system evolves

#### Step Constraints

- **MUST** verify all ADR sections are properly filled
- **MUST** ensure no template placeholders remain in the document
- **MUST** confirm the ADR file exists at the expected location
- **MUST** provide comprehensive summary of the ADR created
- **MUST** document file location and naming convention used
- **MUST** provide clear usage instructions for accessing the generated ADR
- **MUST** include recommendations for ADR maintenance and distribution



## Output Format

- Ask ADR questions one by one following the conversational process exactly in Step 2
- Wait for user response before asking the next question
- Use current date from `date` command for all timestamp fields
- Replace all template placeholders with actual content from user responses
- Create the ADR file in the location specified by the user
- Provide clear progress feedback showing which phase is being executed
- Provide comprehensive summary of the ADR generated


## Safeguards

- **NEVER overwrite existing ADR files** without explicit user consent
- **ASK USER before creating** ADR files in existing directories
- Always read template files fresh using file_search and read_file tools
- Never proceed to next phase without completing all questions for the current phase
- Template adherence is mandatory - no exceptions or simplified versions
- **DOCUMENT what was generated** in the final summary
- Ensure all template placeholders are replaced with actual content
- Validate that the ADR accurately represents the decisions and context gathered