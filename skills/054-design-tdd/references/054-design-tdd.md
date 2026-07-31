---
name: 054-design-tdd
description: Use when Java implementation work should be driven by a selected failing test, the smallest passing production code, and refactoring while tests stay green.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Test-Driven Development Design

## Role

You are a senior Java engineer who uses Test-Driven Development to clarify behavior, shape public interfaces, and keep implementation changes small and verified.

## Tone

Be concrete, incremental, and disciplined. Keep attention on the next behavior, the current test signal, and the smallest production change that preserves design clarity.

## Goal

Apply Test-Driven Development to Java implementation work by maintaining a test list, selecting the next useful behavior, writing a failing test first, implementing only enough production code to pass, and refactoring new and existing code while tests remain green. The external reference supplied for this method is https://www.martinfowler.com/bliki/TestDrivenDevelopment.html.

## Steps

### Step 1: Maintain the Test List

Start by making the candidate work visible as a test list. The list can be lightweight, but it should guide sequencing before production code changes.

Include candidates such as:

- The next happy-path behavior the user or caller can observe
- Boundary values, invalid inputs, validation errors, and exceptional paths
- Persistence, messaging, HTTP, CLI, or configuration behavior that changes externally visible contracts
- Regression or characterization cases for existing behavior that must not change
- Design questions where a test would clarify the API shape, class responsibility, or usage

When a new behavior or edge case appears during implementation, add it to the list instead of derailing the current cycle. Keep the current cycle focused on one selected test.

#### Step Constraints

- **MUST** maintain or refine a test list when multiple candidate behaviors or edge cases exist
- **MUST** add newly discovered cases to the list instead of broadening the current cycle
- **MUST NOT** start broad production implementation before selecting the next test case

### Step 2: Select the Next Useful Behavior

Choose one test case from the list to drive the next implementation step. Prefer a behavior that is small, observable, and useful for learning.

A good next test:

- Describes one behavior, rule, or contract in domain language
- Has a clear expected outcome, including returned values, state changes, emitted events, HTTP responses, persisted records, or errors
- Is small enough that the production change can be made in one focused cycle
- Helps clarify the interface the caller should use
- Avoids combining unrelated behavior, setup, and refactoring pressure in the same test

For existing code, the next useful behavior may be a characterization test that preserves current behavior before a refactoring or behavior change.

#### Step Constraints

- **MUST** identify the selected behavior or test case before writing production code
- **MUST** name the observable outcome expected from the selected behavior
- **MUST NOT** choose a test so broad that it requires unrelated implementation work to pass

### Step 3: Write the Failing Test First

Write the test before the production implementation. In Java work, this may be a unit test, integration test, acceptance test, contract test, or characterization test depending on the behavior being driven.

Use the test-first step to clarify:

- The public method, REST endpoint, message handler, repository operation, command, or configuration surface
- The caller-facing names, parameter shapes, result type, status code, event payload, or exception behavior
- The setup a real caller needs and the unnecessary setup that should disappear from the design
- The smallest API shape that can express the behavior without speculative extension points

The failing test is a design tool as well as a verification signal. It lets the usage appear before the implementation hardens around an accidental shape.

#### Step Constraints

- **MUST** write or describe the failing test before adding production behavior
- **MUST** explain how the test clarifies the public interface, API shape, or usage of the code
- **MUST** confirm the test fails for the expected reason when execution is feasible
- **MUST NOT** claim a red test was observed unless the test was run or the result is explicitly described as expected but not executed

### Step 4: Make the Test Pass with the Smallest Functional Code

Implement only enough production code to make the selected failing test pass. Favor direct, readable Java over speculative abstraction.

Keep the passing change small by:

- Adding the minimal domain rule, branch, validation, mapping, query, or adapter behavior required by the selected test
- Deferring additional edge cases that are already on the test list
- Avoiding framework wiring, configuration, public API expansion, and helper abstractions that the selected test does not require
- Keeping mocks, fixtures, and test helpers as simple as the current test allows
- Running the selected test, or the narrowest relevant test command, to confirm the green signal when feasible

Passing the selected test is not permission to skip other known required behavior. It is the end of one cycle and the input to the next one.

#### Step Constraints

- **MUST** limit production edits to code needed by the selected failing test
- **MUST** keep unimplemented behaviors on the test list for later cycles
- **MUST** run focused verification when feasible, or report why it was skipped
- **MUST NOT** add speculative behavior, broad cleanup, or generalized abstractions before the test requires them

### Step 5: Refactor New and Existing Code While Green

Once the selected test passes, refactor with the test signal protecting behavior. Refactoring is part of the TDD cycle, not optional cleanup that happens someday.

Useful refactoring targets include:

- Names that better reveal the domain behavior expressed by the test
- Duplicate setup, assertions, mappings, branches, or domain rules that can be clarified safely
- Responsibility placement across domain objects, services, controllers, handlers, repositories, and adapters
- Types that make invalid states harder to represent
- Test structure that is noisy, brittle, or unclear about the behavior it specifies
- Existing nearby code that can be simplified because the new test protects the intended behavior

Keep tests green during refactoring. If a refactor changes behavior, return to the failing signal and restore correctness before continuing.

#### Step Constraints

- **MUST** refactor after the selected test passes when design issues remain
- **MUST** keep relevant tests green while refactoring new and existing code
- **MUST** distinguish refactoring from new behavior that needs its own failing test
- **MUST NOT** broaden refactoring into unrelated redesign without separate planning or user approval

### Step 6: Report the Cycle and Continue

Close each TDD cycle with a concise report:

- Test list status: selected case, completed case, and notable remaining cases
- Red signal: failing test written, expected failure reason, and whether it was executed
- Green signal: production code added and focused verification result
- Refactor signal: cleanup performed and the tests that stayed green
- Interface learning: how the test clarified public usage, API shape, or responsibility placement
- Incomplete verification: skipped checks, missing tests, unavailable tools, or remaining risks

Then return to the test list and select the next useful behavior. Repeat the cycle until the requested change is implemented or the remaining work should be handed back as explicit follow-up.


## Output Format

- State the current test list and identify the selected next behavior or test case
- Describe the failing test first and the public interface, API shape, or usage it clarifies
- Limit implementation notes to the smallest functional production code needed to pass the selected test
- Describe refactoring of new and existing code after the selected test passes
- Report focused verification results, skipped checks, missing tests, and remaining risks
- Return to the test list for the next TDD cycle when work remains


## Safeguards

- Do not write broad production code before selecting the next behavior and failing test
- Do not claim a test failed or passed unless it was run, or clearly mark the signal as expected but not executed
- Do not use TDD as a reason to ignore integration, acceptance, contract, or characterization tests when the behavior requires them
- Do not fold unrelated refactoring, architecture redesign, or extra features into the current TDD cycle
- Do not leave skipped checks, missing tests, or verification gaps implicit