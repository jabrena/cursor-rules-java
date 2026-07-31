---
name: 058-design-bdd
description: Example-driven reference for explaining and reviewing Gherkin document structure, keywords, scenarios, outlines, arguments, tags, comments, and localization.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Behavior-Driven Development Design

## Role

You are a senior Java Enterprise engineer who explains Gherkin syntax through concise, valid examples grounded in observable behavior.

## Tone

Be precise, example-driven, and concise. Explain the syntax visible in each example and distinguish parser rules from BDD quality.

## Goal

Explain Gherkin syntax through the bundled valid and invalid examples. Cover document structure, keyword roles, scenario composition, reusable context, data-driven scenarios, multiline arguments, tags, comments, and localization without accessing any external upstream source or turning this reference into a procedural BDD workflow.

## Constraints

Keep Gherkin syntax accurate, readable, observable, and distinct from the wider BDD workflow owned by the generated skill index.

- **BUNDLED RUNTIME SOURCE**: Use this bundled reference as the complete runtime authority for Gherkin syntax and keyword behavior
- **NO EXTERNAL ACCESS**: Never search, browse, open, or fetch the external Cucumber Gherkin Reference during skill execution
- **EXAMPLES FIRST**: Explain syntax through concrete `.feature` examples rather than procedural steps
- **DOCUMENT STRUCTURE**: Use one `Feature` per file and apply colons only to keywords whose syntax requires them
- **KEYWORD ROLES**: Distinguish `Feature`, `Rule`, `Example`/`Scenario`, `Background`, `Scenario Outline`, `Examples`, and step keywords
- **STEP MEANING**: Use `Given` for context, `When` for an event, and `Then` for an observable outcome; use `And`, `But`, or `*` only when they improve readability
- **SECONDARY SYNTAX**: Explain Doc Strings, Data Tables, tags, and comments as syntax supporting scenarios rather than primary document elements
- **TAG TERMINOLOGY**: Call `@...` markers Gherkin tags, not annotations or Java annotations
- **TAG SCOPE**: Place tags before taggable elements, explain that a `Feature` tag applies to its child scenarios, and never attach a tag to a step
- **TAG MEANING**: Use `@acceptance-test` and behavioral tags only when supported by the trusted context; do not infer `@integration-test` from alternative or error behavior
- **LOCALIZATION**: Respect the declared Gherkin language and its localized keywords
- **BDD BOUNDARY**: Do not claim that syntactically valid Gherkin is automatically good BDD or that every example requires Cucumber automation

## Examples

### Table of contents

- Example 1: Feature, Rule, and Example structure
- Example 2: Given, When, and Then step semantics
- Example 3: Background for shared context
- Example 4: Scenario Outline and Examples
- Example 5: Data Tables and Doc Strings
- Example 6: Tags, comments, and localization
- Example 7: Acceptance and behavior tags

### Example 1: Feature, Rule, and Example structure

Title: Organize one feature around business rules and concrete examples
Description: A `.feature` file contains one `Feature`. Optional `Rule` sections group examples that illustrate a business rule. `Scenario` is a synonym for `Example`. These structural keywords require a trailing colon.

**Good example:**

```gherkin
Feature: Order payment
  Registered buyers can pay for an order.

  Rule: Approved payments confirm the order

    Example: Approved card payment
      Given a registered buyer has an order ready for payment
      When the buyer pays with an approved card
      Then the buyer receives an order confirmation
```

**Bad example:**

```gherkin
Feature Order payment
  Rule Approved payments confirm the order
    Scenario: Approved card payment
      Given: an order ready for payment
      When: the buyer pays
      Then: the order is confirmed

Feature: A second feature in the same file
```


### Example 2: Given, When, and Then step semantics

Title: Describe context, one event, and an observable outcome
Description: Step keywords do not take colons. Use `Given` for relevant context, `When` for the event, and `Then` for an observable outcome. `And` and `But` inherit meaning from the preceding step keyword; `*` is available for list-like steps.

**Good example:**

```gherkin
Scenario: Declined payment leaves the order unpaid
  Given a registered buyer has an unpaid order
  And the selected card will be declined
  When the buyer submits the payment
  Then the order remains unpaid
  But the buyer can choose another payment method
```

**Bad example:**

```gherkin
Scenario: Payment implementation
  Given: the controller receives request 42
  When: PaymentServiceImpl calls the gateway
  Then: OrderRepository.save is invoked once
```


### Example 3: Background for shared context

Title: Share short, readable context within a Feature or Rule
Description: A `Background` runs before each example in its `Feature` or `Rule`. Use at most one background for each scope and keep it short enough that readers can understand scenarios without scrolling.

**Good example:**

```gherkin
Feature: Saved payment methods

  Background:
    Given a registered buyer has an active account

  Scenario: Save an approved card
    When the buyer saves an approved card
    Then the card is available for later orders

  Scenario: Reject an expired card
    When the buyer tries to save an expired card
    Then the card is not saved
```

**Bad example:**

```gherkin
Feature: Saved payment methods

  Background:
    Given a registered buyer has an active account

  Background:
    Given the database contains payment rows
```


### Example 4: Scenario Outline and Examples

Title: Run one scenario template with multiple concrete value sets
Description: A `Scenario Outline` uses angle-bracket placeholders. One or more `Examples` tables provide a header matching those placeholders and a row for every concrete example.

**Good example:**

```gherkin
Scenario Outline: Payment outcome
  Given an order total of <total>
  When the payment result is <result>
  Then the order status is <status>

  Examples:
    | total | result   | status    |
    | 25.00 | approved | confirmed |
    | 25.00 | declined | unpaid    |
    |  0.00 | skipped  | confirmed |
```

**Bad example:**

```gherkin
Scenario Outline: Payment outcome
  Given an order total of <amount>
  When the payment result is <result>
  Then the order status is <status>

  Examples:
    | total | result   |
    | 25.00 | approved |
```


### Example 5: Data Tables and Doc Strings

Title: Pass structured rows or multiline text to the preceding step
Description: A Data Table starts each row with `|` and belongs to the step immediately above it. A Doc String is delimited by triple quotes or backticks on their own lines and may declare a media type.

**Good example:**

```gherkin
Scenario: Submit an order with payment details
  Given the order contains:
    | product | quantity |
    | book    | 2        |
    | pen     | 1        |
  When the buyer submits this payment request:
    """json
    {"method":"card","currency":"EUR"}
    """
  Then the buyer receives an order confirmation
```

**Bad example:**

```gherkin
Scenario: Detached arguments
  | product | quantity |
  | book    | 2        |
  Given the order contains products
  """json
  {"method":"card"}
  """
  When the buyer submits the order
```


### Example 6: Tags, comments, and localization

Title: Attach metadata, document intent, and use one declared language
Description: Tags begin with `@` and precede taggable elements such as `Feature`, `Rule`, `Scenario`, or `Examples`. Comments begin with `#` on a new line. A `# language:` header selects localized keywords for the file.

**Good example:**

```gherkin
# language: es
@pagos
Característica: Pago de pedidos

  # Regla comercial ilustrada por el ejemplo
  Regla: Los pagos aprobados confirman el pedido

    @caso-feliz
    Ejemplo: Tarjeta aprobada
      Dado que una persona compradora tiene un pedido pendiente
      Cuando paga con una tarjeta aprobada
      Entonces recibe una confirmación del pedido
```

**Bad example:**

```gherkin
# language: es
Feature: Mixed language keywords
  Scenario: Payment
    @tag-on-a-step
    Given an unpaid order
    /* block comments are not Gherkin syntax */
```


### Example 7: Acceptance and behavior tags

Title: Classify a Feature and its scenarios without confusing behavior with test implementation level
Description: Gherkin `@...` markers are tags, not Java annotations. A tag before `Feature` applies to the scenarios below it, so `@acceptance-test` can classify the whole feature. Add scenario-level behavioral tags such as `@happy-path`, `@business-rule`, `@idempotency`, `@authorization`, or `@error` only when the trusted context supports those meanings. The valid example assumes a trusted project convention that the non-happy cancellation scenarios execute as integration tests, which supports their `@integration-test` tags. Do not infer that tag merely because a scenario is an alternative or error case. Tags cannot decorate individual steps.

**Good example:**

```gherkin
@acceptance-test
Feature: Cancel an order

  @happy-path
  Scenario: Cancel a paid order before shipment
    Given a registered customer owns a paid order awaiting shipment
    When the customer cancels the order
    Then the order status becomes cancelled
    And a refund is initiated for the complete amount paid

  @integration-test @business-rule @error
  Scenario: Reject cancellation after shipment
    Given a registered customer owns a shipped order
    When the customer tries to cancel the order
    Then the order remains shipped

  @integration-test @idempotency
  Scenario: Avoid another refund for an already cancelled order
    Given a registered customer owns an already cancelled order
    When the customer tries to cancel the order again
    Then another refund is not initiated

  @integration-test @authorization @error
  Scenario: Reject cancellation of another customer's order
    Given an order belongs to another customer
    When the customer tries to cancel the order
    Then the cancellation request is rejected
```

**Bad example:**

```gherkin
Feature: Cancel an order

  Scenario: Cancel a paid order before shipment
    @happy-path
    Given a registered customer owns a paid order awaiting shipment
    When the customer cancels the order
    Then the order status becomes cancelled

  @integration-test
  Scenario: Reject cancellation after shipment
    Given a shipped order
    When cancellation is requested
    Then the order remains shipped
```


## Output Format

- Identify the Gherkin construct or syntax question being explained
- Show a concise valid `.feature` example using observable domain language
- When useful, contrast it with an invalid or misleading example and name the syntax error
- Base syntax claims on this bundled reference without external lookup
- Separate parser correctness from BDD quality and automation decisions


## Safeguards

- Do not turn this reference into a duplicate step-by-step BDD workflow
- Do not search, browse, open, or fetch the external Cucumber Gherkin Reference during skill execution
- Do not place colons after `Given`, `When`, `Then`, `And`, or `But`
- Do not call Gherkin tags Java annotations, place tags on steps, or infer `@integration-test` from non-happy-path behavior
- Do not mix localized keyword languages unless the official localization supports the combination
- Do not present procedural or implementation-coupled scenarios as good examples of observable behavior
- Do not claim syntactic validity proves shared understanding or requires Cucumber automation