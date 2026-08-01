---
name: 501-frameworks-micronaut-core
description: Use when you need to review, improve, or build Micronaut applications — including application bootstrap with Micronaut.run, @Singleton/@Prototype scopes, @Factory bean producers, constructor injection with jakarta.inject, @ConfigurationProperties and @Property, environments and @Requires, @Controller vs application services, AOP interceptors, @Scheduled tasks, graceful shutdown, virtual-thread friendly execution, health endpoints, and test-oriented bean design.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Micronaut Core Guidelines

## Role

You are a Senior software engineer with extensive experience in Micronaut, dependency injection, and Java enterprise development

## Goal

Micronaut favors compile-time dependency injection, fast startup, and explicit configuration. Effective applications keep `main` thin, use `@Singleton` for stateless services, declare beans with `@Factory` when construction is non-trivial, bind settings with `@ConfigurationProperties`, branch environments with Micronaut environments and `@Requires` instead of scattered `if` checks, and use Jakarta APIs consistently. Operational concerns include Netty graceful shutdown, scheduling with bounded error handling, and optional `@ExecuteOn` / virtual threads for blocking work off the event loop.

## Constraints

Before applying any recommendations, ensure the project is in a valid state by running Maven compilation. Compilation failure is a BLOCKING condition that prevents any further processing.

- **MANDATORY**: Run `./mvnw compile` or `mvn compile` before applying any change
- **PREREQUISITE**: Project must compile successfully and pass basic validation checks before any Micronaut refactoring
- **CRITICAL SAFETY**: If compilation fails, IMMEDIATELY STOP and DO NOT CONTINUE with any recommendations
- **BLOCKING CONDITION**: Compilation errors must be resolved by the user before proceeding with Micronaut core improvements
- **NO EXCEPTIONS**: Under no circumstances should Micronaut recommendations be applied to a project that fails to compile
- **VERIFY**: Run `./mvnw clean verify` or `mvn clean verify` after applying improvements

## Examples

### Table of contents

- Example 1: Application bootstrap
- Example 2: Application services
- Example 3: Bean factories
- Example 4: Type-safe configuration
- Example 5: Environments and conditional beans
- Example 6: HTTP controllers
- Example 7: Scheduled tasks
- Example 8: Blocking work and the event loop
- Example 9: Multiple beans of the same type — @Primary and @Named
- Example 10: Graceful shutdown
- Example 11: Virtual-thread-friendly executors (Java 21+)
- Example 12: CDI-style interceptors
- Example 13: javax vs jakarta consistency

### Example 1: Application bootstrap

Title: Thin main with Micronaut.run
Description: Start the embedded Netty server and application context with `Micronaut.run`. Do not embed use-case orchestration or long-running business logic in `main`.

**Good example:**

```java
import io.micronaut.runtime.Micronaut;

public final class Application {

    public static void main(String[] args) {
        Micronaut.run(Application.class, args);
    }
}
```

**Bad example:**

```java
import io.micronaut.runtime.Micronaut;

public final class Application {

    public static void main(String[] args) {
        // Bad: business work in main — untestable and couples startup to domain
        new OrderBacklogProcessor().drainAllQueues();
        Micronaut.run(Application.class, args);
    }
}
```

### Example 2: Application services

Title: @Singleton with constructor injection
Description: Model application services as `@Singleton` types with final dependencies injected via constructor. Keeps beans immutable-friendly and easy to unit test without the full HTTP stack.

**Good example:**

```java
import io.micronaut.context.annotation.Singleton;
import jakarta.inject.Inject;

@Singleton
public class OrderService {

    private final OrderRepository orders;
    private final PaymentClient payments;

    @Inject
    public OrderService(OrderRepository orders, PaymentClient payments) {
        this.orders = orders;
        this.payments = payments;
    }

    public Order place(OrderRequest request) {
        payments.authorize(request.amount());
        return orders.save(Order.from(request));
    }
}
```

**Bad example:**

```java
import io.micronaut.context.annotation.Singleton;
import jakarta.inject.Inject;

@Singleton
public class OrderService {

    @Inject
    OrderRepository orders; // field injection — harder to test and reason about

    @Inject
    PaymentClient payments;

    public Order place(OrderRequest request) {
        return orders.save(Order.from(request));
    }
}
```

### Example 3: Bean factories

Title: @Factory for non-trivial construction
Description: Use `@Factory` when you integrate third-party types or need explicit setup. Annotate producer methods with `@Singleton` (or another scope) and keep them focused.

**Good example:**

```java
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Singleton;
import java.net.http.HttpClient;
import java.time.Duration;

@Factory
public class HttpClientFactory {

    @Bean
    @Singleton
    HttpClient httpClient() {
        return HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    }
}
```

**Bad example:**

```java
import io.micronaut.context.annotation.Factory;

@Factory
public class GodFactory {
    // Bad: one factory owns unrelated beans — split by integration boundary
    @io.micronaut.context.annotation.Bean
    @io.micronaut.context.annotation.Singleton
    Object kafka() { return null; }

    @io.micronaut.context.annotation.Bean
    @io.micronaut.context.annotation.Singleton
    Object redis() { return null; }
}
```

### Example 4: Type-safe configuration

Title: @ConfigurationProperties with records or beans
Description: Group related keys under a prefix with `@ConfigurationProperties`. Prefer records or immutable-friendly types; validate with Bean Validation when invalid config must fail fast at startup.

**Good example:**

```java
import io.micronaut.context.annotation.ConfigurationProperties;
import jakarta.validation.constraints.Positive;

@ConfigurationProperties("app.batch")
public class BatchSettings {

    @Positive
    private int maxItems = 10;

    public int getMaxItems() {
        return maxItems;
    }

    public void setMaxItems(int maxItems) {
        this.maxItems = maxItems;
    }
}
```

**Bad example:**

```java
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;

@Singleton
public class BatchProcessor {

    @Value("${app.batch.max-items:10}")
    int maxItems; // scattered magic keys — prefer @ConfigurationProperties grouping
}
```

### Example 5: Environments and conditional beans

Title: @Requires and micronaut.environments
Description: Encode environment differences with Micronaut environments (`micronaut.environments`) in `application.yml` / launch config, and `@Requires` on beans. Avoid `System.getenv` branching inside domain services.

**Good example:**

```java
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.annotation.Singleton;

@Singleton
@Requires(env = "dev")
public class DevDiagnosticsPublisher {
    public void emit(String message) { /* noop or console in dev */ }
}
```

**Bad example:**

```java
import io.micronaut.context.annotation.Singleton;

@Singleton
public class FeatureService {

    public boolean enabled() {
        // Bad: environment logic in domain — untestable without env hacks
        return "prod".equalsIgnoreCase(System.getenv("ENV"));
    }
}
```

### Example 6: HTTP controllers

Title: Delegate to services; keep controllers thin
Description: `@Controller` types should map HTTP to DTOs and call application services. Avoid embedding persistence or complex rules directly in controllers.

**Good example:**

```java
import io.micronaut.http.annotation.*;
import io.micronaut.http.HttpResponse;
import jakarta.inject.Inject;

@Controller("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @Inject
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Post
    public HttpResponse<OrderDto> create(@Body OrderCreateDto body) {
        OrderDto created = orderService.create(body);
        return HttpResponse.created(java.net.URI.create("/api/orders/" + created.id())).body(created);
    }
}
```

**Bad example:**

```java
import io.micronaut.http.annotation.*;
import jakarta.inject.Inject;

@Controller("/api/orders")
public class FatOrderController {

    @Inject
    OrderRepository repo; // Bad: controller talks to persistence directly

    @Post
    public OrderDto create(@Body OrderCreateDto body) {
        return repo.save(new Order(null, body.productId(), body.qty())).toDto();
    }
}
```

### Example 7: Scheduled tasks

Title: @Scheduled on @Singleton beans
Description: Use `@Scheduled` with explicit fixed delay or cron strings. Log or metric failures so silent breakage is visible.

**Good example:**

```java
import io.micronaut.context.annotation.Singleton;
import io.micronaut.scheduling.annotation.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.inject.Inject;

@Singleton
public class OutboxDispatcher {

    private static final Logger LOG = LoggerFactory.getLogger(OutboxDispatcher.class);
    private final OutboxRepository outbox;

    @Inject
    OutboxDispatcher(OutboxRepository outbox) {
        this.outbox = outbox;
    }

    @Scheduled(fixedDelay = "30s")
    void flush() {
        try {
            outbox.dispatchBatch();
        } catch (Exception ex) {
            LOG.warn("outbox flush failed", ex);
        }
    }
}
```

**Bad example:**

```java
import io.micronaut.context.annotation.Singleton;
import io.micronaut.scheduling.annotation.Scheduled;

@Singleton
public class SilentJob {

    @Scheduled(fixedDelay = "5s")
    void tick() {
        throw new IllegalStateException(); // Bad: unhandled — may suppress further runs without visibility
    }
}
```

### Example 8: Blocking work and the event loop

Title: @ExecuteOn for blocking I/O
Description: Long-running blocking calls on the default Netty thread reduce throughput. Offload with `@ExecuteOn` (e.g. `TaskExecutors.BLOCKING`) or configure virtual-thread executors when your stack supports them.

**Good example:**

```java
import io.micronaut.context.annotation.Singleton;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.http.annotation.*;

@Controller("/reports")
@Singleton
public class ReportController {

    private final ReportGenerator generator;

    public ReportController(ReportGenerator generator) {
        this.generator = generator;
    }

    @Get("/{id}")
    @ExecuteOn(TaskExecutors.BLOCKING)
    public byte[] download(String id) {
        return generator.buildPdfBlocking(id);
    }
}
```

**Bad example:**

```java
import io.micronaut.http.annotation.*;

@Controller("/reports")
public class BlockingOnEventLoopController {

    @Get("/{id}")
    public byte[] download(String id) {
        // Bad: heavy blocking PDF generation on event-loop thread
        return ReportGenerator.buildPdfBlocking(id);
    }
}
```

### Example 9: Multiple beans of the same type — @Primary and @Named

Title: Disambiguate implementations when several beans share an injection type
Description: When you register more than one bean of the same type, mark the default with `@Primary` or inject a specific one with `@Named("qualifier")` (or a custom `@Qualifier`). Prefer constructor parameters with explicit names over field injection. Avoid multiple candidates with no `@Primary` and no qualifier — the context fails to resolve the dependency.

**Good example:**

```java
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Primary;
import io.micronaut.context.annotation.Singleton;
import jakarta.inject.Inject;
import jakarta.inject.Named;

public interface NotificationSender {
    void send(String message);
}

@Factory
public class NotificationFactory {

    @Bean
    @Singleton
    @Primary
    NotificationSender loggingSender() {
        return message -> { /* logging sink */ };
    }

    @Bean
    @Singleton
    @Named("metrics")
    NotificationSender metricsSender() {
        return message -> { /* metrics sink */ };
    }
}

@Singleton
public class AlertService {

    private final NotificationSender defaultSender;
    private final NotificationSender metricsSender;

    @Inject
    public AlertService(
            NotificationSender defaultSender,
            @Named("metrics") NotificationSender metricsSender) {
        this.defaultSender = defaultSender;
        this.metricsSender = metricsSender;
    }
}
```

**Bad example:**

```java
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Singleton;
import jakarta.inject.Inject;

public interface NotificationSender {
    void send(String message);
}

@Factory
public class NotificationFactory {

    @Bean
    @Singleton
    NotificationSender emailSender() {
        return m -> { };
    }

    @Bean
    @Singleton
    NotificationSender smsSender() {
        return m -> { };
    }
}

@Singleton
public class AlertService {

    private final NotificationSender sender;

    @Inject
    public AlertService(NotificationSender sender) {
        // Bad: two NotificationSender beans — ambiguous injection at compile/runtime
        this.sender = sender;
    }
}
```

### Example 10: Graceful shutdown

Title: Netty server shutdown timeouts
Description: Configure graceful shutdown so active requests can finish. Tune parent/quiet period properties for your SLA; document values for operators.

**Good example:**

```yaml
micronaut:
  server:
    netty:
      graceful-shutdown-quiet-period: 2s
      graceful-shutdown-timeout: 20s
```

**Bad example:**

```yaml
# Bad: no graceful settings — in-flight requests may be cut on SIGTERM in K8s
micronaut:
  server:
    port: 8080
```

### Example 11: Virtual-thread-friendly executors (Java 21+)

Title: Prefer virtual-thread or thread-per-task IO executors over extra platform pools
Description: On **Java 21+**, Micronaut can route work through virtual-thread-friendly executors (see `micronaut.executors` and your Micronaut version docs). Use `@ExecuteOn` for blocking controller methods; avoid stacking a large fixed **platform** thread pool on top when the framework already schedules work on virtual threads — that recreates a pool-within-pool bottleneck.

**Good example:**

```yaml
# Example: thread-per-task style IO executor (verify keys against your Micronaut version)
micronaut:
  executors:
    io:
      type: THREAD_PER_TASK
```

**Bad example:**

```java
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Singleton;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

// Bad: extra fixed platform pool on top of virtual-thread-friendly dispatch —
// work piles up waiting for scarce platform threads
@Factory
public class RedundantPoolFactory {

    @Bean
    @Singleton
    ExecutorService requestExecutor() {
        return Executors.newFixedThreadPool(200);
    }
}
```

### Example 12: CDI-style interceptors

Title: @InterceptorBinding for cross-cutting behavior
Description: Attach logging, auditing, or timing with Micronaut AOP interceptors instead of duplicating boilerplate in every service method.

**Good example:**

```java
import io.micronaut.aop.Around;
import io.micronaut.aop.InterceptorBean;
import io.micronaut.aop.MethodInterceptor;
import io.micronaut.aop.MethodInvocationContext;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@InterceptorBean(Timed.class)
public class TimedInterceptor implements MethodInterceptor<Object, Object> {

    private static final Logger LOG = LoggerFactory.getLogger(TimedInterceptor.class);

    @Override
    public Object intercept(MethodInvocationContext<Object, Object> context) {
        long t0 = System.nanoTime();
        try {
            return context.proceed();
        } finally {
            LOG.debug("{} took {} ms", context.getMethodName(),
                (System.nanoTime() - t0) / 1_000_000);
        }
    }
}
```

**Bad example:**

```java
import io.micronaut.context.annotation.Singleton;

@Singleton
public class OrderService {

    public Order place(OrderRequest r) {
        long t0 = System.nanoTime();
        try {
            return doPlace(r);
        } finally {
            // Bad: duplicated timing in every method instead of an interceptor
            System.out.println("place took " + (System.nanoTime() - t0));
        }
    }

    private Order doPlace(OrderRequest r) { return null; }
}
```

### Example 13: javax vs jakarta consistency

Title: Use jakarta.* for CDI, validation, and lifecycle; keep javax.sql for JDBC
Description: Micronaut 4.x aligns with **Jakarta EE 9+** namespaces. Use `jakarta.inject.Inject`, `jakarta.annotation.PostConstruct` / `PreDestroy`, and `jakarta.validation.constraints.*` as appropriate. **Do not** mix legacy `javax.inject`, `javax.annotation`, or `javax.validation` with Jakarta equivalents on the same classpath. **Exception:** JDK types such as `javax.sql.DataSource` remain under `javax.sql`.

**Good example:**

```java
import io.micronaut.context.annotation.Singleton;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotBlank;
import javax.sql.DataSource;

@Singleton
public class ExampleBean {

    private final DataSource dataSource;

    @Inject
    public ExampleBean(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PreDestroy
    void cleanup() {
        // release resources
    }
}

record ValidatedInput(@NotBlank String value) { }
```

**Bad example:**

```java
// Mixing legacy javax.* EE imports with Micronaut 4 — wrong API on the classpath
import javax.inject.Inject;
import javax.validation.constraints.NotBlank;
import javax.sql.DataSource;

public class BrokenBean {

    private final DataSource dataSource;

    @Inject
    public BrokenBean(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
```


## Output Format

- **ANALYZE** the Micronaut codebase for bootstrap, bean scopes, factory usage, configuration binding, environment and `@Requires` usage, controller thickness, scheduling, event-loop blocking risk, shutdown settings, and Jakarta vs legacy javax imports
- **CATEGORIZE** findings by impact (startup, throughput, maintainability) and layer (config, beans, HTTP, scheduling)
- **APPLY** improvements: thin `main`, constructor injection, grouped `@ConfigurationProperties`, conditional beans with `@Requires`, delegate controllers to services, add `@ExecuteOn` for blocking paths, tune graceful shutdown, extract cross-cutting code to interceptors
- **IMPLEMENT** incrementally with compiling steps between edits
- **EXPLAIN** trade-offs (e.g., prototype vs singleton, blocking executor choice) when multiple valid options exist
- **VALIDATE** with `./mvnw compile` before and `./mvnw clean verify` after substantive edits


## Safeguards

- **BLOCKING SAFETY CHECK**: Run `./mvnw compile` before ANY Micronaut refactoring
- **CRITICAL VALIDATION**: Run `./mvnw clean verify` after changes
- **THREADING**: Misplaced blocking I/O causes latency under load — verify hot paths after adding `@ExecuteOn` or executor changes
- **CONFIG**: Strict Bean Validation on configuration types fails startup when keys are missing — align all environments
- **JAKARTA NAMESPACE**: Mixing `javax.*` and `jakarta.*` injection or validation annotations breaks wiring — standardize on `jakarta.*`
- **INCREMENTAL SAFETY**: Apply scope and factory changes in small steps with compile verification between steps