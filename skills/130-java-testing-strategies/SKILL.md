---
name: 130-java-testing-strategies
description: Use when you need to apply testing strategies for Java code — RIGHT-BICEP to guide test creation, A-TRIP for test quality characteristics, or CORRECT for verifying boundary conditions. This should trigger for requests such as Review Java code for testing strategies; Apply RIGHT-BICEP testing strategies in Java code; Apply A-TRIP testing strategies in Java code; Apply CORRECT boundary condition verification in Java code. Part of Plinth Toolkit
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Java testing strategies

Apply proven testing strategies (RIGHT-BICEP, A-TRIP, CORRECT) to design and verify Java unit tests.

**What is covered in this Skill?**

- **RIGHT-BICEP**: Key questions to guide test creation — Right results, Boundary conditions, Inverse relationships, Cross-checks, Error conditions, Performance
- **A-TRIP**: Characteristics of good tests — Automatic, Thorough, Repeatable, Independent, Professional
- **CORRECT**: Boundary condition verification — Conformance, Ordering, Range, Reference, Existence, Cardinality, Time


## Constraints

Before applying any test strategy changes, ensure the project compiles. If compilation fails, stop immediately — do not proceed until resolved. After applying improvements, run full verification.

- **MANDATORY**: Run `./mvnw compile` or `mvn compile` before applying any change
- **SAFETY**: If compilation fails, stop immediately and do not proceed — compilation failure is a blocking condition
- **VERIFY**: Run `./mvnw clean verify` or `mvn clean verify` after applying improvements
- **BEFORE APPLYING**: Read only the testing-strategy references selected by the user's concern before changing tests
- **FOCUSED ROUTING**: For missing behavior coverage or RIGHT-BICEP requests, read `references/130-java-testing-strategies-right-bicep.md` by default
- **FOCUSED ROUTING**: For flaky, brittle, manual, order-dependent, shared-state, or maintainability requests, read `references/130-java-testing-strategies-a-trip.md` by default
- **FOCUSED ROUTING**: For boundary-condition or CORRECT requests, read `references/130-java-testing-strategies-correct.md` by default
- **BROAD REVIEW**: For broad test-strategy reviews, combine RIGHT-BICEP, A-TRIP, and CORRECT references and categorize findings by technique
- **EDGE CASE**: If the user goal is ambiguous, stop and ask a clarifying question before editing files or running project-wide commands
- **EDGE CASE**: If required context, files, credentials, or tools are missing, report the blocker explicitly and ask whether to proceed with setup or fallback guidance
- **EDGE CASE**: If requested changes conflict with project constraints or safety boundaries, explain the conflict and ask for user confirmation on the preferred trade-off

## When to use this skill

- Review Java code for testing strategies
- Find missing Java test cases or behavior coverage gaps
- Apply RIGHT-BICEP testing strategies in Java code
- Review flaky, brittle, order-dependent, or hard-to-maintain Java tests
- Apply A-TRIP testing strategies in Java code
- Review Java boundary condition tests
- Apply CORRECT boundary condition verification in Java code

## Workflow

1. **Compile project before test-strategy changes**

Run `./mvnw compile` or `mvn compile` and stop immediately if compilation fails.

2. **Select focused testing-strategy references**


Route the request before reading references:

- Read `references/130-java-testing-strategies-right-bicep.md` when the request asks what to test, which behavior is missing, whether assertions prove the right result, how to force errors, how to cross-check, or how to add performance guardrails.
- Read `references/130-java-testing-strategies-a-trip.md` when the request asks why tests are flaky, brittle, manual, slow to run locally, order-dependent, shared-state dependent, unclear, or hard to maintain.
- Read `references/130-java-testing-strategies-correct.md` when the request asks for boundary-condition review, CORRECT analysis, invalid input handling, conformance, ordering, range, external reference, existence, cardinality, or time cases.
- For broad test-strategy reviews, read all three focused references and organize findings by RIGHT-BICEP, A-TRIP, and CORRECT.

Do not require unrelated references for a narrow request unless the user's concern crosses technique boundaries.


3. **Apply strategy-driven test improvements**

Improve or add tests using the selected technique: produce a RIGHT-BICEP gap matrix for missing behavior coverage, an A-TRIP quality finding list for test reliability and maintainability issues, a CORRECT boundary checklist for boundary reviews, or all three sections for broad reviews.

4. **Verify with full build**

Run `./mvnw clean verify` or `mvn clean verify` after applying improvements.

## Reference

For detailed guidance, examples, and constraints, see:

- [references/130-java-testing-strategies-right-bicep.md](references/130-java-testing-strategies-right-bicep.md)
- [references/130-java-testing-strategies-a-trip.md](references/130-java-testing-strategies-a-trip.md)
- [references/130-java-testing-strategies-correct.md](references/130-java-testing-strategies-correct.md)
