---
name: 130-java-testing-strategies-correct
description: Focused CORRECT guidance for Java boundary-condition analysis.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Java testing strategies

## Role

You are a Senior software engineer with extensive experience in Java software development and boundary-condition test design.

## Goal

Use CORRECT to find boundary-condition gaps in Java tests: Conformance, Ordering, Range, Reference, Existence, Cardinality, and Time.

## Constraints

Apply this reference when the user asks for boundary-condition testing, invalid input handling, CORRECT analysis, or missing edge cases around values, collections, dependencies, or time.

- Keep this reference focused on boundary categories; use RIGHT-BICEP for broader coverage strategy and A-TRIP for test quality problems such as flakiness.
- Use controllable time sources such as `Clock` for time cases.
- For unordered results, assert explicitly unordered expectations instead of depending on incidental iteration order.

## Examples

### Table of contents

- Example 1: Conformance
- Example 2: Ordering
- Example 3: Range
- Example 4: Reference
- Example 5: Existence
- Example 6: Cardinality
- Example 7: Time

### Example 1: Conformance

Title: Verify values conform to format, schema, type, or validation rules.
Description: Conformance boundaries appear in emails, identifiers, JSON fields, enum names, ISO dates, request DTOs, database codes, and domain-specific formats. Validation signals: - Valid and invalid examples are both present. - The test covers malformed shape, unsupported type, and policy-specific rule violations. - Assertions check the domain error, not only a generic failure.

**Good example:**

```java
class EmailAddressTest {

    @ParameterizedTest
    @ValueSource(strings = {"ada@example.com", "first.last@sub.example.org"})
    void parse_validEmail_acceptsValue(String value) {
        assertThat(EmailAddress.parse(value).value()).isEqualTo(value);
    }

    @ParameterizedTest
    @ValueSource(strings = {"ada", "ada@", "@example.com", "ada example@example.com"})
    void parse_invalidEmail_rejectsValue(String value) {
        assertThatThrownBy(() -> EmailAddress.parse(value))
            .isInstanceOf(InvalidEmailAddressException.class);
    }
}
```

### Example 2: Ordering

Title: Distinguish sorted, stable, and explicitly unordered expectations.
Description: Ordering tests should match the contract. Some APIs promise sorted output, some preserve insertion order or stable tie order, and some are explicitly unordered. Validation signals: - Sorted results use exact order assertions. - Stable order tests cover equal sort keys. - Unordered results use order-insensitive assertions.

**Good example:**

```java
class SearchResultsTest {

    @Test
    void search_sortsByScoreDescending() {
        List<Result> results = search.find("java");

        assertThat(results).extracting(Result::score)
            .containsExactly(99, 75, 42);
    }

    @Test
    void permissions_returnsUnorderedSet() {
        Set<String> permissions = roles.permissionsFor("admin");

        assertThat(permissions).containsExactlyInAnyOrder("read", "write", "delete");
    }
}
```

### Example 3: Range

Title: Check lower bound, upper bound, just-inside, and just-outside values.
Description: Range boundaries appear in amounts, ages, percentages, page sizes, retries, ports, lengths, and numeric limits. Validation signals: - Lower and upper bounds are tested directly. - Just-below and just-above values are tested. - The expected inclusivity or exclusivity is visible in the test data.

**Good example:**

```java
class PageRequestTest {

    @ParameterizedTest
    @CsvSource({
        "1, true",
        "100, true",
        "0, false",
        "101, false"
    })
    void pageSize_mustBeBetweenOneAndOneHundred(int pageSize, boolean valid) {
        ValidationResult result = PageRequest.validate(0, pageSize);

        assertThat(result.isValid()).isEqualTo(valid);
    }
}
```

### Example 4: Reference

Title: Cover external dependencies and references outside direct control.
Description: Reference boundaries include missing records, unavailable services, denied permissions, invalid filesystem paths, stale identifiers, and downstream timeouts. Validation signals: - Missing referenced entity is tested. - Unavailable external dependency is tested with a controlled fake or stub. - Permission and filesystem failures are represented when the code touches protected resources.

**Good example:**

```java
class DocumentServiceTest {

    @Test
    void download_missingDocument_returnsNotFound() {
        DocumentRepository documents = new InMemoryDocumentRepository();
        DocumentService service = new DocumentService(documents, storage);

        assertThatThrownBy(() -> service.download(DocumentId.of("missing")))
            .isInstanceOf(DocumentNotFoundException.class);
    }

    @Test
    void export_unwritablePath_reportsStorageFailure() {
        Storage storage = path -> { throw new AccessDeniedException(path.toString()); };
        DocumentService service = new DocumentService(documentsWithOneRecord(), storage);

        assertThatThrownBy(() -> service.export(DocumentId.of("D-1"), Path.of("/root/report.pdf")))
            .isInstanceOf(DocumentStorageException.class);
    }
}
```

### Example 5: Existence

Title: Cover null, blank, empty, missing optional, and absent repository values.
Description: Existence boundaries appear when values may be null, blank, empty, absent from a collection, missing from a repository, or omitted from an optional request field. Validation signals: - Null, blank, and empty are treated according to their distinct domain meanings. - `Optional.empty()` and missing repository records are explicitly covered. - The code fails clearly or defaults intentionally.

**Good example:**

```java
class DisplayNameTest {

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    void create_missingDisplayName_rejectsValue(String value) {
        assertThatThrownBy(() -> DisplayName.of(value))
            .isInstanceOf(InvalidDisplayNameException.class);
    }

    @Test
    void preferredName_missingOptional_fallsBackToLegalName() {
        Customer customer = new Customer("Ada Lovelace", Optional.empty());

        assertThat(customer.preferredName()).isEqualTo("Ada Lovelace");
    }
}
```

### Example 6: Cardinality

Title: Cover zero, one, many, exact count, maximum count, and duplicates.
Description: Cardinality boundaries appear in collections, pages, batches, validation errors, role assignments, child records, retries, and aggregate members. Validation signals: - Zero, one, and many cases are present where collection size changes behavior. - Exact count and maximum count are asserted where policy requires them. - Duplicate handling is explicitly accepted, rejected, or collapsed.

**Good example:**

```java
class TeamMembershipTest {

    @Test
    void create_withoutMembers_rejectsTeam() {
        assertThatThrownBy(() -> Team.create("platform", List.of()))
            .isInstanceOf(InvalidTeamException.class);
    }

    @Test
    void create_withMaximumMembers_acceptsTeam() {
        List<Member> members = memberList(20);

        Team team = Team.create("platform", members);

        assertThat(team.members()).hasSize(20);
    }

    @Test
    void create_withDuplicateMembers_rejectsTeam() {
        Member ada = member("ada");

        assertThatThrownBy(() -> Team.create("platform", List.of(ada, ada)))
            .isInstanceOf(DuplicateMemberException.class);
    }
}
```

### Example 7: Time

Title: Use controllable time to test order, deadlines, expiration, and windows.
Description: Time boundaries appear in expiration, retries, scheduling, event ordering, grace periods, cut-off dates, and daylight-saving or timezone behavior. Validation signals: - Tests use `Clock` or an equivalent controllable time source. - Boundary instants before, at, and after the cut-off are covered. - Time zone and ordering semantics are explicit when relevant.

**Good example:**

```java
class AccessTokenTest {

    private static final Instant ISSUED_AT = Instant.parse("2026-06-27T10:00:00Z");
    private static final ZoneId UTC = ZoneOffset.UTC;

    @Test
    void isExpired_atExpiryInstant_returnsTrue() {
        Clock clock = Clock.fixed(ISSUED_AT.plusSeconds(3600), UTC);
        AccessToken token = AccessToken.issuedAt(ISSUED_AT, Duration.ofHours(1), clock);

        assertThat(token.isExpired()).isTrue();
    }

    @Test
    void isExpired_justBeforeExpiry_returnsFalse() {
        Clock clock = Clock.fixed(ISSUED_AT.plusSeconds(3599), UTC);
        AccessToken token = AccessToken.issuedAt(ISSUED_AT, Duration.ofHours(1), clock);

        assertThat(token.isExpired()).isFalse();
    }
}
```

**Bad example:**

```java
@Test
void tokenEventuallyExpires() throws InterruptedException {
    AccessToken token = AccessToken.issueFor(Duration.ofMillis(50));

    Thread.sleep(75);

    assertThat(token.isExpired()).isTrue();
}
```

## Output Format

- **CHECK** CORRECT categories: Conformance, Ordering, Range, Reference, Existence, Cardinality, Time
- **LIST** missing boundary cases with concrete candidate inputs and expected outcomes
- **DISTINGUISH** sorted, stable, and unordered result contracts when reviewing ordering
- **VALIDATE** compile and verification commands before and after code changes when implementation is requested