---
name: 057-design-feature-toggles
description: Examples for designing, implementing, testing, operating, and cleaning up feature toggles in Java enterprise systems without creating avoidable release, rollback, security, or maintenance risk.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral, Sangwon Park
  version: 0.18.0
---
# Feature Toggles Design

## Role

You are a senior Java Enterprise engineer who provides concrete examples for safe runtime behavior controls.

## Tone

Be practical and example-driven. Prefer small Java, configuration, test, and cleanup examples that make toggle ownership, defaults, rollback, and removal visible.

## Goal

Provide concrete examples that support the Feature Toggles skill workflow. Use these examples to show how Java teams can keep feature toggles typed, owned, observable, reversible, and removable.

## Constraints

Use these examples as implementation references, not as a second workflow.

- **EXAMPLES FIRST**: Prefer concrete Java, configuration, testing, operations, and cleanup examples over another sequence of process steps
- **TYPED BOUNDARY**: Keep provider-specific flag keys behind a typed decision API
- **SAFE DEFAULTS**: Show the default and failure behavior explicitly in code or configuration
- **ROLLBACK PATH**: Keep the disabled path testable until cleanup removes the temporary toggle
- **NO SENSITIVE RULES**: Do not put secrets, personal data, internal cohort logic, or authorization bypasses in toggle names, rules, logs, or metrics

## Examples

### Table of contents

- Example 1: Toggle contract record
- Example 2: Pure Java configuration-backed decision
- Example 3: Spring Boot configuration-backed decision
- Example 4: Quarkus configuration-backed decision
- Example 5: Micronaut configuration-backed decision
- Example 6: Strategy selector boundary
- Example 7: Request consistency
- Example 8: Unit tests for both paths
- Example 9: Operational kill switch
- Example 10: Cleanup after rollout

### Example 1: Toggle contract record

Title: Document the owner, default, rollback, and cleanup trigger before coding
Description: A release toggle should have enough metadata for reviewers and operators to understand the runtime contract. The contract can live in an issue, OpenSpec task, ADR, release checklist, or code-adjacent documentation.

**Good example:**

```markdown
Toggle: checkoutRiskScoringEnabled
Type: release toggle
Owner: Checkout Platform
Default: false
Disabled behavior: legacy manual review remains the production path
Enabled behavior: risk scoring service evaluates eligible checkout orders
Rollback: set false in the feature service; propagation expected under 60 seconds
Observability: checkout.risk_scoring.decision and checkout.risk_scoring.errors
Cleanup trigger: 100 percent rollout stable for 14 days with no rollback incidents
Cleanup task: remove legacyReviewService branch, old config key, and disabled-path tests
```

**Bad example:**

```markdown
Toggle: newCheckout
Owner: TBD
Default: probably off
Rollback: turn it off
Cleanup: later
```


### Example 2: Pure Java configuration-backed decision

Title: Parse configuration at the edge and expose a typed decision method
Description: Framework-free Java can still keep toggle evaluation centralized. Parse raw properties once near startup or an adapter boundary, keep the safe default visible, and let business code depend on a named decision method.

**Good example:**

```java
import java.util.Properties;

public record CheckoutFeatureProperties(boolean riskScoringEnabled) {

    public static CheckoutFeatureProperties from(Properties properties) {
        boolean enabled = Boolean.parseBoolean(
            properties.getProperty("checkout.features.risk-scoring-enabled", "false")
        );
        return new CheckoutFeatureProperties(enabled);
    }
}

public final class CheckoutFeatureDecisions {

    private final CheckoutFeatureProperties properties;

    public CheckoutFeatureDecisions(CheckoutFeatureProperties properties) {
        this.properties = properties;
    }

    public boolean useRiskScoring(CheckoutContext context) {
        return properties.riskScoringEnabled() && context.isEligibleForRiskScoring();
    }
}
```

**Bad example:**

```java
if (Boolean.getBoolean("new.impl")) {
    riskScoringService.score(order);
}
```


### Example 3: Spring Boot configuration-backed decision

Title: Bind configuration once and expose a typed decision method
Description: Configuration binding keeps the default visible and provider-independent. Business code depends on a domain decision instead of reading raw property names.

**Good example:**

```java
@ConfigurationProperties(prefix = "checkout.features")
public record CheckoutFeatureProperties(boolean riskScoringEnabled) {

    public CheckoutFeatureProperties {
        // Default remains false when configuration is absent.
    }
}

@Component
public final class CheckoutFeatureDecisions {

    private final CheckoutFeatureProperties properties;

    public CheckoutFeatureDecisions(CheckoutFeatureProperties properties) {
        this.properties = properties;
    }

    public boolean useRiskScoring(CheckoutContext context) {
        return properties.riskScoringEnabled() && context.isEligibleForRiskScoring();
    }
}
```

**Bad example:**

```java
if (environment.getProperty("new.impl", Boolean.class, false)) {
    riskScoringService.score(order);
}
```


### Example 4: Quarkus configuration-backed decision

Title: Bind SmallRye Config once and expose a typed decision method
Description: Quarkus applications can use `@ConfigMapping` to keep feature keys grouped, typed, and defaulted. Business code still depends on a checkout decision instead of provider-specific property names.

**Good example:**

```java
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import jakarta.enterprise.context.ApplicationScoped;

@ConfigMapping(prefix = "checkout.features")
public interface CheckoutFeatureProperties {

    @WithDefault("false")
    boolean riskScoringEnabled();
}

@ApplicationScoped
public final class CheckoutFeatureDecisions {

    private final CheckoutFeatureProperties properties;

    public CheckoutFeatureDecisions(CheckoutFeatureProperties properties) {
        this.properties = properties;
    }

    public boolean useRiskScoring(CheckoutContext context) {
        return properties.riskScoringEnabled() && context.isEligibleForRiskScoring();
    }
}
```

**Bad example:**

```java
boolean enabled = ConfigProvider.getConfig()
    .getOptionalValue("new.impl", Boolean.class)
    .orElse(false);

if (enabled) {
    riskScoringService.score(order);
}
```


### Example 5: Micronaut configuration-backed decision

Title: Bind Micronaut configuration once and expose a typed decision method
Description: Micronaut applications can use `@ConfigurationProperties` to group feature settings with explicit defaults. Application services receive a typed decision object instead of reading raw `@Value` keys.

**Good example:**

```java
import io.micronaut.context.annotation.ConfigurationProperties;
import jakarta.inject.Singleton;

@ConfigurationProperties("checkout.features")
public class CheckoutFeatureProperties {

    private boolean riskScoringEnabled = false;

    public boolean isRiskScoringEnabled() {
        return riskScoringEnabled;
    }

    public void setRiskScoringEnabled(boolean riskScoringEnabled) {
        this.riskScoringEnabled = riskScoringEnabled;
    }
}

@Singleton
public final class CheckoutFeatureDecisions {

    private final CheckoutFeatureProperties properties;

    public CheckoutFeatureDecisions(CheckoutFeatureProperties properties) {
        this.properties = properties;
    }

    public boolean useRiskScoring(CheckoutContext context) {
        return properties.isRiskScoringEnabled() && context.isEligibleForRiskScoring();
    }
}
```

**Bad example:**

```java
@Value("${new.impl:false}")
boolean enabled;

if (enabled) {
    riskScoringService.score(order);
}
```


### Example 6: Strategy selector boundary

Title: Choose one implementation path near the application boundary
Description: When the behavior is substantial, selecting a strategy keeps branch logic out of domain objects and makes both paths easier to test.

**Good example:**

```java
public final class CheckoutService {

    private final CheckoutFeatureDecisions featureDecisions;
    private final CheckoutRiskScoring riskScoring;
    private final LegacyCheckoutReview legacyReview;

    public Receipt submit(CheckoutCommand command) {
        CheckoutContext context = CheckoutContext.from(command);
        if (featureDecisions.useRiskScoring(context)) {
            return riskScoring.submit(command);
        }
        return legacyReview.submit(command);
    }
}
```

**Bad example:**

```java
public final class Order {

    public void submit(Environment environment) {
        if (environment.getProperty("checkout.features.risk-scoring-enabled", Boolean.class)) {
            // new path
        }
        // repeated checks in domain code
    }
}
```


### Example 7: Request consistency

Title: Evaluate once when mid-flow changes would be unsafe
Description: For transactions, messages, batch items, billing, authorization-adjacent behavior, or external calls, carry the decision value through the flow so one request cannot mix old and new behavior.

**Good example:**

```java
public Receipt submit(CheckoutCommand command) {
    CheckoutContext context = CheckoutContext.from(command);
    boolean useRiskScoring = featureDecisions.useRiskScoring(context);

    CheckoutDecision decision = new CheckoutDecision(command, useRiskScoring);
    return checkoutProcessor.process(decision);
}
```

**Bad example:**

```java
public Receipt submit(CheckoutCommand command) {
    fraudGateway.reserve(command);
    if (featureClient.isEnabled("checkoutRiskScoringEnabled")) {
        riskScoringService.score(command);
    }
    if (featureClient.isEnabled("checkoutRiskScoringEnabled")) {
        receiptPublisher.publishNewReceipt(command);
    }
}
```


### Example 8: Unit tests for both paths

Title: Keep disabled, enabled, and fallback behavior covered
Description: The disabled path is the rollback path. Keep it tested until the cleanup change removes the old behavior.

**Good example:**

```java
@Test
void usesLegacyReviewWhenRiskScoringIsDisabled() {
    CheckoutFeatureDecisions decisions = decisions(false);

    checkoutService.submit(command, decisions);

    verify(legacyReview).submit(command);
    verifyNoInteractions(riskScoring);
}

@Test
void usesRiskScoringWhenRiskScoringIsEnabledAndContextIsEligible() {
    CheckoutFeatureDecisions decisions = decisions(true);

    checkoutService.submit(command, decisions);

    verify(riskScoring).submit(command);
    verifyNoInteractions(legacyReview);
}

@Test
void fallsBackToDisabledBehaviorWhenProviderValueIsMissing() {
    CheckoutFeatureDecisions decisions = decisionsWithMissingValue();

    assertThat(decisions.useRiskScoring(context)).isFalse();
}
```

**Bad example:**

```java
@Test
void newCheckoutWorks() {
    enable("new.impl");
    checkoutService.submit(command);
    verify(riskScoring).submit(command);
}
```


### Example 9: Operational kill switch

Title: Make emergency disablement observable and reversible
Description: A kill switch can be long-lived, but it still needs an owner, safe default, metric, and runbook entry. It should disable risky optional work without breaking the core contract.

**Good example:**

```java
public void publishRecommendationEvents(List<RecommendationEvent> events) {
    if (featureDecisions.disableRecommendationPublishing()) {
        meterRegistry.counter("recommendations.publisher.skipped", "reason", "kill_switch").increment(events.size());
        logger.warn("Recommendation publishing skipped by operational kill switch");
        return;
    }

    recommendationPublisher.publish(events);
}
```

**Bad example:**

```java
if (featureClient.isEnabled("disableEverything")) {
    return;
}
```


### Example 10: Cleanup after rollout

Title: Remove temporary toggle artifacts after stability evidence is complete
Description: Cleanup is broader than one conditional branch. Retire the old behavior path, decision method, provider key, stale tests, dashboards, alerts, and documentation that existed only for the temporary rollout.

**Good example:**

```markdown
Cleanup checklist for checkoutRiskScoringEnabled:
- Confirm 100 percent rollout was stable for 14 days
- Retire legacyReviewService branch from CheckoutService
- Retire CheckoutFeatureDecisions#useRiskScoring when no other rollout uses it
- Retire checkout.features.risk-scoring-enabled from configuration templates
- Retire disabled-path tests and keep released behavior tests
- Retire rollout dashboard panels and provider entry
- Close cleanup task with release note reference
```

**Bad example:**

```markdown
Cleanup:
- Leave the flag in case we need it later
- Keep both branches because they still compile
- Remove only the dashboard
```


## Output Format

- Use examples that name the toggle, owner, default, disabled behavior, enabled behavior, rollback mechanism, observability, and cleanup trigger
- Prefer Java snippets with typed decision APIs over raw provider string lookups
- Include tests for disabled, enabled, fallback, rollback, and cleanup behavior when the example touches code
- Show operational examples with metrics, logs, alerts, or runbook signals when the toggle controls production risk
- Call out anti-examples that hide ownership, use vague names, scatter checks, skip rollback validation, or defer cleanup indefinitely


## Safeguards

- Do not duplicate the Feature Toggles skill workflow inside this reference; keep this file focused on examples
- Do not recommend raw feature-provider lookups throughout controllers, domain objects, repositories, jobs, or clients
- Do not show toggle examples that bypass authentication, authorization, audit, compliance, rate limiting, or data retention controls
- Do not present rollback as safe unless the disabled path and provider failure behavior are tested
- Do not leave temporary release toggles without owner, expiry condition, cleanup task, and validation evidence