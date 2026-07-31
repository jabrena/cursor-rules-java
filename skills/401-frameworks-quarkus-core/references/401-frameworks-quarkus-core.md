---
name: 401-frameworks-quarkus-core
description: Use when you need to review, improve, or build Quarkus applications — including mandatory @QuarkusMain entry points with static main methods, CDI scopes (@ApplicationScoped, @Singleton, @Dependent), constructor injection, @ConfigMapping and SmallRye Config, profiles (%dev, %test, %prod), build-time vs runtime configuration, lifecycle (@Startup, @PreDestroy), metrics integration patterns, and test-friendly bean design.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Quarkus Core Guidelines

## Role

You are a Senior software engineer with extensive experience in Quarkus, CDI, and Java enterprise development

## Goal

Quarkus core development favors explicit CDI beans, narrow scopes, type-safe configuration, and profile-aware behavior without a giant Spring-style component graph. The runtime is optimized for fast startup and low memory; align with that by avoiding unnecessary eager singletons, keeping beans `@ApplicationScoped` by default for services, and using `@ConfigMapping` for structured settings. Prefer constructor injection, validate configuration at startup, and separate imperative bootstrap (`@QuarkusMain`) from HTTP or messaging entry points defined by extensions.

## Constraints

Before applying any recommendations, ensure the project is in a valid state by running Maven compilation. Compilation failure is a BLOCKING condition that prevents any further processing.

- **MANDATORY**: Run `./mvnw compile` or `mvn compile` before applying any change
- **PREREQUISITE**: Project must compile successfully before any Quarkus refactoring
- **CRITICAL SAFETY**: If compilation fails, IMMEDIATELY STOP and DO NOT CONTINUE with any recommendations
- **VERIFY**: Run `./mvnw clean verify` or `mvn clean verify` after applying improvements
- **JAKARTA NAMESPACE**: Use `jakarta.*` for CDI, validation, and inject APIs; never mix `javax.inject` or `javax.annotation` with their Jakarta equivalents
- **NATIVE IMAGE SAFETY**: When adding libraries that use reflection, register required classes with `@RegisterForReflection` or a `ReflectionRegistrar` before building native

## Examples

### Table of contents

- Example 1: Simple application entry point
- Example 2: Advanced application entry point
- Example 3: Service beans
- Example 4: Type-safe configuration
- Example 5: Profiles in configuration
- Example 6: Startup hooks
- Example 7: CDI bean disambiguation
- Example 8: CDI events and lifecycle observers
- Example 9: Resource cleanup with @PreDestroy
- Example 10: Configuration validation
- Example 11: Programmatic injection with Instance<T>
- Example 12: Graceful shutdown
- Example 13: Scheduled tasks
- Example 14: Virtual threads (Java 21+)
- Example 15: javax vs jakarta consistency

### Example 1: Simple application entry point

Title: @QuarkusMain with static main method
Description: Every Quarkus application should have a `@QuarkusMain` class with a static `main` method. For simple cases, use `Quarkus.run(args)` directly. Keep `main` thin — no business logic.

**Good example:**

```java
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

/**
 * Main application class for the service.
 */
@QuarkusMain
public class MyApplication {

    public static void main(String... args) {
        Quarkus.run(args);
    }
}
```

**Bad example:**

```java
@QuarkusMain
public class BloatedMain {
    public static void main(String... args) {
        // Bad: orchestrating use cases directly in main
        new OrderService().processBacklog();
        Quarkus.run(args);
    }
}
```

### Example 2: Advanced application entry point

Title: QuarkusApplication interface for lifecycle control
Description: When you need more control over the application lifecycle or custom shutdown logic, implement `QuarkusApplication`. Still keep business logic out of the main class.

**Good example:**

```java
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain
public class CustomMain implements QuarkusApplication {

    @Override
    public int run(String... args) {
        Quarkus.waitForExit();
        return 0;
    }
}
```

**Bad example:**

```java
@QuarkusMain
public class BloatedMain implements QuarkusApplication {
    @Override
    public int run(String... args) {
        // Bad: orchestrating use cases directly in main
        new OrderService().processBacklog();
        Quarkus.waitForExit();
        return 0;
    }
}
```

### Example 3: Service beans

Title: @ApplicationScoped with constructor injection
Description: Application services should be `@ApplicationScoped`, immutable-friendly, and receive collaborators via constructor. Avoid static holders and service locators.

**Good example:**

```java
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class OrderService {

    private final OrderRepository orders;

    @Inject
    public OrderService(OrderRepository orders) {
        this.orders = orders;
    }

    public void place(Order order) {
        orders.persist(order);
    }
}
```

**Bad example:**

```java
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class OrderService {

    @Inject
    OrderRepository orders; // field injection — harder to test and reason about

    public void place(Order order) {
        orders.persist(order);
    }
}
```

### Example 4: Type-safe configuration

Title: @ConfigMapping for grouped settings
Description: Use `@ConfigMapping` interfaces to bind prefixes in `application.properties`. Keeps magic strings out of business code and documents required keys.

**Good example:**

```java
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithName;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ConfigMapping(prefix = "app.orders")
public interface OrderConfig {

    @WithName("max-batch")
    int maxBatch();
}

@ApplicationScoped
public class BatchOrderProcessor {

    private final OrderConfig config;

    @Inject
    public BatchOrderProcessor(OrderConfig config) {
        this.config = config;
    }

    public int limit() {
        return config.maxBatch();
    }
}
```

**Bad example:**

```java
import org.eclipse.microprofile.config.inject.ConfigProperty;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class BatchOrderProcessor {

    @Inject
    @ConfigProperty(name = "app.orders.max-batch", defaultValue = "10")
    int maxBatch; // scattered keys — prefer @ConfigMapping for groups
}
```

### Example 5: Profiles in configuration

Title: %dev, %test, %prod property overrides
Description: Use Quarkus profile prefixes in `application.properties` instead of branching in Java for environment differences.

**Good example:**

```properties
app.feature.verbose=false
%dev.app.feature.verbose=true
%test.app.feature.verbose=false
%prod.app.feature.verbose=false
```

**Bad example:**

```java
// Bad: environment checks in domain code
if (System.getenv("ENV") != null && System.getenv("ENV").equals("dev")) {
    log.debug("verbose");
}
```

### Example 6: Startup hooks

Title: @Startup for eager initialization
Description: Use `@Startup` on a bean method to run work after the Quarkus application is ready. Keep work short or offload to async executors.

**Good example:**

```java
import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

@ApplicationScoped
public class CacheWarmup {

    private static final Logger LOG = Logger.getLogger(CacheWarmup.class);

    @Startup
    void warmCaches() {
        LOG.info("Warming read-only reference data");
        // load small reference data — keep fast
    }
}
```

**Bad example:**

```java
import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CacheWarmupBad {

    @Startup
    void onStart() throws Exception {
        // Bad: blocking work during startup — delays readiness
        Thread.sleep(60_000);
    }
}
```

### Example 7: CDI bean disambiguation

Title: @Default, @Alternative + @Priority, and @Named to resolve ambiguous types
Description: When two or more CDI beans implement the same type, injection points become ambiguous unless you disambiguate. Use `@Default` on the standard implementation, `@Alternative` + `@Priority` to promote an override (e.g. for tests), or a custom qualifier / `@Named` to select by name. `Instance<T>` can also iterate or select programmatically.

**Good example:**

```java
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

public interface NotificationSender {
    void send(String message);
}

@Default
@ApplicationScoped
public class EmailNotificationSender implements NotificationSender {
    @Override
    public void send(String message) { /* email */ }
}

@Alternative
@Priority(100)
@ApplicationScoped
public class SmsNotificationSender implements NotificationSender {
    @Override
    public void send(String message) { /* sms */ }
}

@ApplicationScoped
public class AlertService {

    private final NotificationSender sender; // resolves to @Default or highest-priority @Alternative

    @Inject
    public AlertService(NotificationSender sender) {
        this.sender = sender;
    }

    public void alert(String msg) {
        sender.send(msg);
    }
}

// Programmatic selection via Instance<T>
@ApplicationScoped
public class MultiChannelService {

    private final Instance<NotificationSender> senders;

    @Inject
    public MultiChannelService(Instance<NotificationSender> senders) {
        this.senders = senders;
    }

    public void broadcast(String message) {
        senders.forEach(s -> s.send(message));
    }
}
```

**Bad example:**

```java
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

public interface NotificationSender {
    void send(String message);
}

@ApplicationScoped
public class EmailNotificationSender implements NotificationSender {
    @Override
    public void send(String message) { }
}

@ApplicationScoped
public class SmsNotificationSender implements NotificationSender {
    @Override
    public void send(String message) { }
}

@ApplicationScoped
public class AlertService {

    private final NotificationSender sender;

    @Inject
    public AlertService(NotificationSender sender) {
        // Bad: two @ApplicationScoped beans — CDI cannot choose — DeploymentException at startup
        this.sender = sender;
    }
}
```

### Example 8: CDI events and lifecycle observers

Title: @Observes StartupEvent / ShutdownEvent instead of blocking in @Startup
Description: Prefer `@Observes StartupEvent` and `@Observes ShutdownEvent` for application lifecycle hooks. These fire after CDI beans are ready (startup) and before the context is torn down (shutdown). Use `@ObservesAsync` for non-blocking event handling. Avoid long blocking I/O in `@Startup` methods when an event observer decouples the concern better.

**Good example:**

```java
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.ObservesAsync;
import org.jboss.logging.Logger;

@ApplicationScoped
public class AppLifecycleBean {

    private static final Logger LOG = Logger.getLogger(AppLifecycleBean.class);

    void onStart(@Observes StartupEvent ev) {
        LOG.info("Application starting — warming reference data");
        // load small read-only reference data; keep fast
    }

    void onStop(@Observes ShutdownEvent ev) {
        LOG.info("Application stopping — flushing buffers");
        // release resources, flush async buffers
    }
}

@ApplicationScoped
public class AuditEventHandler {

    void onAuditEvent(@ObservesAsync AuditEvent event) {
        // non-blocking: runs on a worker thread, does not delay the caller
        persistAuditRecord(event);
    }

    private void persistAuditRecord(AuditEvent event) { }
}

public record AuditEvent(String action, String userId) { }
```

**Bad example:**

```java
import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BadLifecycleBean {

    @Startup
    void init() {
        // Bad: blocking remote call during startup — delays readiness probe
        callRemoteService();
        // Bad: no shutdown hook — resources are never released
    }

    private void callRemoteService() {
        try { Thread.sleep(5_000); } catch (InterruptedException ignored) { }
    }
}
```

### Example 9: Resource cleanup with @PreDestroy

Title: Release executors, connections, and I/O handles on bean destruction
Description: Annotate a no-arg method with `@PreDestroy` to run cleanup logic when the CDI container destroys the bean. Use it to close external connections, stop background threads, or release file handles. Pair with `@Observes ShutdownEvent` for cleanup that must happen before the CDI context is torn down.

**Good example:**

```java
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.jboss.logging.Logger;

@ApplicationScoped
public class BackgroundWorker {

    private static final Logger LOG = Logger.getLogger(BackgroundWorker.class);

    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    public void submit(Runnable task) {
        executor.submit(task);
    }

    @PreDestroy
    void shutdown() {
        LOG.info("Shutting down background executor");
        executor.shutdown();
        try {
            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
```

**Bad example:**

```java
import jakarta.enterprise.context.ApplicationScoped;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ApplicationScoped
public class LeakyWorker {

    // Bad: executor is never shut down — threads leak on every application restart
    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    public void submit(Runnable task) {
        executor.submit(task);
    }
}
```

### Example 10: Configuration validation

Title: Bean Validation constraints on @ConfigMapping fail fast at startup
Description: Apply Jakarta Bean Validation constraints (`@NotBlank`, `@Min`, `@Max`, `@Pattern`) on `@ConfigMapping` interface methods. Quarkus validates annotated config interfaces at startup; invalid or missing values produce a clear error before any request is served.

**Good example:**

```java
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@ConfigMapping(prefix = "app.api")
public interface ApiClientConfig {

    @NotBlank
    String baseUrl();

    @Min(1) @Max(600)
    @WithDefault("30")
    int connectTimeoutSeconds();

    @Positive
    @WithDefault("3")
    int maxRetries();
}

@ApplicationScoped
public class ApiClient {

    private final ApiClientConfig config;

    @Inject
    public ApiClient(ApiClientConfig config) {
        this.config = config;
    }

    public String baseUrl() {
        return config.baseUrl(); // guaranteed non-blank at this point
    }
}
```

**Bad example:**

```java
import io.smallrye.config.ConfigMapping;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ConfigMapping(prefix = "app.api")
public interface ApiClientConfig {
    String baseUrl(); // no @NotBlank — empty string is silently accepted
    int connectTimeoutSeconds(); // no range check — negative timeout is allowed
}

@ApplicationScoped
public class ApiClient {

    private final ApiClientConfig config;

    @Inject
    public ApiClient(ApiClientConfig config) {
        this.config = config;
    }

    public void call() {
        // Bad: discovering misconfiguration only when the HTTP call fails at runtime
        if (config.baseUrl().isBlank()) {
            throw new IllegalStateException("baseUrl not configured");
        }
    }
}
```

### Example 11: Programmatic injection with Instance<T>

Title: Optional, multiple, and dynamically-selected beans via CDI Instance
Description: `jakarta.enterprise.inject.Instance<T>` gives programmatic access to CDI beans: check presence with `isUnsatisfied()` / `isAmbiguous()`, iterate all matching implementations, or select by qualifier. Use it when a dependency is optional, when you need to enumerate all implementations, or when the choice depends on runtime state.

**Good example:**

```java
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

public interface MetricsExporter {
    void export(String metric, double value);
}

@ApplicationScoped
public class MetricsService {

    private final Instance<MetricsExporter> exporters;

    @Inject
    public MetricsService(Instance<MetricsExporter> exporters) {
        this.exporters = exporters;
    }

    public void record(String metric, double value) {
        if (exporters.isUnsatisfied()) {
            return; // optional dependency — no exporter registered, skip silently
        }
        exporters.forEach(e -> e.export(metric, value));
    }
}
```

**Bad example:**

```java
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

public interface MetricsExporter {
    void export(String metric, double value);
}

@ApplicationScoped
public class MetricsService {

    @Inject
    MetricsExporter exporter; // Bad: fails with UnsatisfiedResolutionException if no bean is registered;
                               // field injection cannot express "optional" without Instance<T>

    public void record(String metric, double value) {
        exporter.export(metric, value);
    }
}
```

### Example 12: Graceful shutdown

Title: Wait for in-flight HTTP work with `quarkus.shutdown.timeout`
Description: Set `quarkus.shutdown.timeout` so Quarkus waits for running HTTP requests to finish (up to that limit) instead of stopping immediately. Optional: enable `quarkus.shutdown.delay-enabled` and `quarkus.shutdown.delay` so readiness fails first while the instance still drains traffic — useful behind Kubernetes. See the lifecycle guide for details.

**Good example:**

```properties
# application.properties — graceful shutdown for HTTP (requires quarkus-vertx-http or equivalent)
quarkus.shutdown.timeout=30s
```

**Bad example:**

```properties
# Bad: timeout too short for longest in-flight HTTP work — requests may be aborted mid-flight
quarkus.shutdown.timeout=1s
```

### Example 13: Scheduled tasks

Title: `io.quarkus.scheduler.Scheduled` with the Quarkus Scheduler extension
Description: Use the Scheduler extension (`quarkus-scheduler`) for periodic work. Keep tasks short, catch errors, and avoid blocking remote calls without `@RunOnVirtualThread` or worker offload where appropriate.

**Good example:**

```java
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

@ApplicationScoped
public class OutboxFlushJob {

    private static final Logger LOG = Logger.getLogger(OutboxFlushJob.class);

    private final OutboxRepository outbox;

    public OutboxFlushJob(OutboxRepository outbox) {
        this.outbox = outbox;
    }

    @Scheduled(every = "30s")
    void flush() {
        try {
            outbox.dispatchBatch();
        } catch (Exception e) {
            LOG.warn("outbox flush failed", e);
        }
    }
}

interface OutboxRepository {
    void dispatchBatch();
}
```

**Bad example:**

```java
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SilentJob {

    @Scheduled(every = "5s")
    void tick() {
        throw new IllegalStateException(); // Bad: unhandled — failures should be logged or handled
    }
}
```

### Example 14: Virtual threads (Java 21+)

Title: Enable platform virtual threads, then use `@RunOnVirtualThread` on blocking endpoints
Description: Add the **`quarkus-virtual-threads`** extension on **Java 21+**. `VirtualThreadsConfig` maps to `quarkus.virtual-threads.*` (e.g. `enabled` defaults to `true`; set `enabled=false` to force `@RunOnVirtualThread` work onto the worker pool when pinning hurts). For blocking JAX-RS methods, annotate with `@RunOnVirtualThread` so execution moves off the Vert.x event loop. Ideal for blocking I/O; avoid for CPU-bound work. Profile before enabling broadly.

**Good example:**

```properties
# Java 21+; add io.quarkus:quarkus-virtual-threads — tune naming/shutdown if needed
quarkus.virtual-threads.enabled=true
quarkus.virtual-threads.name-prefix=orders-vt-
```

**Bad example:**

```java
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("/orders")
public class OrderResource {

    private final OrderRepository repository;

    @Inject
    public OrderResource(OrderRepository repository) {
        this.repository = repository;
    }

    @GET
    @Path("/{id}")
    // Bad: blocking JDBC call on the Vert.x event-loop thread — starves
    // other requests and causes latency spikes under load; add @RunOnVirtualThread
    public Order getOrder(@PathParam("id") long id) {
        return repository.findById(id);
    }
}

interface OrderRepository {
    Order findById(long id);
}

record Order(long id, String status) { }
```

### Example 15: javax vs jakarta consistency

Title: Use jakarta.* for CDI, validation, and JAX-RS; keep javax.sql for JDBC
Description: Quarkus targets **Jakarta EE 9+** namespaces. Use `jakarta.enterprise.context.*`, `jakarta.inject.*`, `jakarta.validation.constraints.*`, `jakarta.ws.rs.*`, and `jakarta.annotation.*` as appropriate. **Do not** mix legacy `javax.inject`, `javax.annotation`, or `javax.validation` with Jakarta equivalents. **Exception:** JDK types such as `javax.sql.DataSource` remain under `javax.sql`.

**Good example:**

```java
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotBlank;
import javax.sql.DataSource;

@ApplicationScoped
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
// Mixing legacy javax.* EE imports with Quarkus — wrong API on the classpath
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

- **ENSURE** every Quarkus project has a `@QuarkusMain` class with a static `main` method using `Quarkus.run(args)` — create one if missing
- **ANALYZE** the Quarkus project for CDI scope misuse, configuration sprawl, profile handling, lifecycle blocking, injection style, graceful shutdown settings, scheduler error handling, virtual-thread usage, and jakarta vs legacy javax imports
- **CATEGORIZE** findings by impact (startup, memory, maintainability) and layer (config, beans, lifecycle)
- **APPLY** improvements: constructor injection, `@ConfigMapping`, profile-based properties, appropriate scopes, lean startup observers, graceful shutdown timeouts, safe scheduler jobs, and virtual-thread usage aligned with the Virtual Threads extension
- **VALIDATE** with `./mvnw compile` before and `./mvnw clean verify` after substantive edits
- **DETECT** CDI scope ambiguity (multiple unqualified implementations of the same type) and missing bean disambiguation (`@Default`, `@Alternative`, `@Named`)
- **EXPLAIN** what changed and why: improved startup safety, better testability, narrower scopes, or cleaner cross-cutting separation via interceptors and events


## Safeguards

- **BLOCKING SAFETY CHECK**: Run `./mvnw compile` before ANY Quarkus refactoring
- **CRITICAL VALIDATION**: Run `./mvnw clean verify` after changes
- **NATIVE IMAGE**: Changes that require reflection must be registered for native builds — verify with `quarkus build` when native is in scope
- **CDI SCOPE CHANGE**: Changing `@ApplicationScoped` to `@Singleton` removes the client proxy — beans injected into narrower scopes may break; verify before refactoring
- **CONFIG VALIDATION**: Adding Bean Validation to `@ConfigMapping` will fail startup on invalid or missing properties — align `%test` profile values before enabling
- **DEV SERVICES**: Dev Services only activate in `%dev` and `%test` profiles; never rely on them in `%prod`; ensure production configuration is always explicit
- **VIRTUAL THREADS**: Requires Java 21+; re-test for thread-pinning and third-party library compatibility after enabling `@RunOnVirtualThread`
- **JAKARTA NAMESPACE**: Mixing `javax.inject` with `jakarta.inject` on the same classpath causes silent CDI resolution failures — use `jakarta.*` throughout
- **INCREMENTAL SAFETY**: Apply CDI scope, configuration, and lifecycle changes in small steps with `./mvnw compile` verification between steps