---
name: 130-java-testing-strategies-right-bicep
description: Focused RIGHT-BICEP guidance for deciding what behavior and risks Java tests should cover.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Java testing strategies

## Role

You are a Senior software engineer with extensive experience in Java software development and test design.

## Goal

Use RIGHT-BICEP to decide what Java tests should exist. RIGHT-BICEP is the strategy for coverage design: Right results, Boundary conditions, Inverse relationships, Cross-checks, Error conditions, and Performance guardrails.

## Constraints

Apply this reference when the user asks what behavior is missing, which tests should be added, whether assertions prove the right result, or how to cover risks before writing implementation-level unit test mechanics.

- Keep this reference focused on coverage strategy; use `131-java-testing-unit-testing` for detailed JUnit 5, AssertJ, Mockito, and parameterized-test mechanics.
- For boundary-only requests, prefer `references/130-java-testing-strategies-correct.md` unless RIGHT-BICEP coverage design is also requested.
- Frame performance checks as stable smoke or guardrail tests, not fragile timing microbenchmarks.

## Examples

### Table of contents

- Example 1: Right Results and Oracle Clarity
- Example 2: Boundary Conditions as a Bridge to CORRECT
- Example 3: Inverse Relationships
- Example 4: Cross-Checks
- Example 5: Error Conditions and Exception Contracts
- Example 6: Performance Guardrails

### Example 1: Right Results and Oracle Clarity

Title: Prove behavior with a clear expected result, not a vague assertion.
Description: Use Right results when reviewing the main behavior of a method, service, parser, mapper, or domain rule. Validation signals: - The expected value is explicit and independently understandable. - The assertion checks the observable behavior, not private implementation details. - The test name states the scenario and expected outcome.

**Good example:**

```java
class InvoiceTotalTest {

    @Test
    void total_includesLineItemsAndTax() {
        Invoice invoice = new Invoice(List.of(
            new LineItem("book", new BigDecimal("20.00")),
            new LineItem("pen", new BigDecimal("5.00"))
        ), new BigDecimal("0.10"));

        Money total = invoice.total();

        assertThat(total).isEqualTo(Money.usd("27.50"));
    }
}
```

**Bad example:**

```java
class InvoiceTotalTest {

    @Test
    void total_works() {
        Money total = sampleInvoice().total();

        assertThat(total).isNotNull();
    }
}
```

### Example 2: Boundary Conditions as a Bridge to CORRECT

Title: Use boundary thinking to reveal missing coverage, then use CORRECT for detailed boundary categories.
Description: RIGHT-BICEP asks whether boundaries are covered. If boundary analysis becomes the main task, switch to CORRECT for conformance, ordering, range, reference, existence, cardinality, and time. Validation signals: - Tests include just-inside and just-outside values. - The review names which boundary is business-critical. - Missing boundary cases are recorded as explicit test gaps.

**Good example:**

```java
class DiscountPolicyTest {

    @ParameterizedTest
    @CsvSource({
        "99.99, false",
        "100.00, true",
        "100.01, true"
    })
    void qualifiesForDiscount_aroundMinimumOrderAmount(BigDecimal amount, boolean expected) {
        DiscountPolicy policy = new DiscountPolicy(new BigDecimal("100.00"));

        assertThat(policy.qualifiesForDiscount(amount)).isEqualTo(expected);
    }
}
```

### Example 3: Inverse Relationships

Title: Verify reversible behavior such as serialize/deserialize, encode/decode, add/remove, or write/read.
Description: Use inverse tests when two operations should undo or preserve each other. They are especially useful for codecs, persistence adapters, collection operations, and mapping layers. Validation signals: - The test executes both directions of the relationship. - The round trip checks meaningful equality, not only non-null output. - The test includes representative values and at least one edge case.

**Good example:**

```java
class CustomerJsonCodecTest {

    @Test
    void encodeAndDecode_preservesCustomer() {
        Customer original = new Customer("C-42", "Ada", List.of("admin", "billing"));

        String json = codec.encode(original);
        Customer decoded = codec.decode(json);

        assertThat(decoded).isEqualTo(original);
    }

    @Test
    void addAndRemove_returnsCartToOriginalState() {
        Cart cart = new Cart();

        cart.add(ProductId.of("P-1"));
        cart.remove(ProductId.of("P-1"));

        assertThat(cart.items()).isEmpty();
    }
}
```

### Example 4: Cross-Checks

Title: Compare a result with an independent calculation, fixture, alternate implementation, or property.
Description: Use cross-checks when a single expected value is easy to compute incorrectly or when the algorithm is complex. Validation signals: - The expected result comes from a source independent of the implementation under test. - The cross-check explains the property or fixture used as the oracle. - The test avoids reimplementing the same production algorithm with the same defect.

**Good example:**

```java
class PriceBreakdownTest {

    @Test
    void subtotal_matchesIndependentLineItemSum() {
        List<LineItem> items = List.of(
            new LineItem("A", Money.usd("10.00"), 2),
            new LineItem("B", Money.usd("3.50"), 4)
        );

        Money subtotal = calculator.subtotal(items);

        Money expected = Money.usd("34.00"); // independent fixture calculation
        assertThat(subtotal).isEqualTo(expected);
    }

    @Test
    void sortedByScore_preservesAllOriginalIds() {
        List<Result> sorted = ranking.sortByScore(results);

        assertThat(sorted).extracting(Result::id)
            .containsExactlyInAnyOrderElementsOf(results.stream().map(Result::id).toList());
    }
}
```

### Example 5: Error Conditions and Exception Contracts

Title: Force failures deliberately and verify the public error contract.
Description: Use error-condition tests for invalid input, illegal state, overflow, missing resources, denied operations, and downstream failures. Validation signals: - The test forces the error directly instead of hoping it happens incidentally. - The assertion checks exception type and useful message or error code when that is part of the contract. - The test verifies no partial state change remains after the failure when rollback matters.

**Good example:**

```java
class AccountTransferTest {

    @Test
    void transfer_moreThanBalance_throwsAndLeavesBalancesUnchanged() {
        Account source = Account.withBalance(Money.usd("50.00"));
        Account target = Account.withBalance(Money.usd("10.00"));

        assertThatThrownBy(() -> transferService.transfer(source, target, Money.usd("75.00")))
            .isInstanceOf(InsufficientFundsException.class)
            .hasMessageContaining("50.00");

        assertThat(source.balance()).isEqualTo(Money.usd("50.00"));
        assertThat(target.balance()).isEqualTo(Money.usd("10.00"));
    }
}
```

### Example 6: Performance Guardrails

Title: Protect obvious performance expectations with deterministic guardrails.
Description: Use performance checks only when performance is part of the behavior contract or a known risk. Prefer stable guardrails such as avoiding N+1 calls, bounding page size, or verifying linear behavior with fake collaborators. Validation signals: - The test avoids wall-clock thresholds unless the project already has a reliable performance-test harness. - The guardrail checks a deterministic proxy, such as query count, batch count, allocation category, or algorithmic call count. - The assertion leaves room for normal machine variability.

**Good example:**

```java
class CustomerReportTest {

    @Test
    void buildReport_loadsOrdersInOneBatch() {
        CountingOrderRepository orders = new CountingOrderRepository();
        CustomerReportService service = new CustomerReportService(orders);

        service.buildReport(List.of(customer("C-1"), customer("C-2"), customer("C-3")));

        assertThat(orders.batchLoadCount()).isEqualTo(1);
    }
}
```

**Bad example:**

```java
@Test
void buildReport_finishesWithinTenMilliseconds() {
    long start = System.currentTimeMillis();

    service.buildReport(customers);

    assertThat(System.currentTimeMillis() - start).isLessThan(10);
}
```

## Output Format

- **MAP** findings in a RIGHT-BICEP gap matrix with columns for dimension, current evidence, missing test, and risk
- **PRIORITIZE** gaps by user-visible behavior, business risk, and defect likelihood
- **SEPARATE** coverage strategy from framework mechanics; hand off detailed JUnit or Mockito patterns to `131-java-testing-unit-testing` when needed
- **VALIDATE** compile and verification commands before and after code changes when implementation is requested