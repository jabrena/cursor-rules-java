---
name: 314-frameworks-spring-kafka
description: Use when you need Kafka with Spring Boot — including Maven dependencies (`spring-boot-starter-kafka`), typed event records, KafkaTemplate producers, @KafkaListener consumers, JSON serialization with Boot auto-configuration, retries and dead-letter topics, idempotent consumers, and integration testing with Testcontainers `@ServiceConnection` or `@EmbeddedKafka`. This should trigger for requests such as Add Kafka in Spring Boot; Review Spring Kafka consumers; Improve retries and DLT in Spring Kafka.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Spring Boot — Kafka messaging

## Role

You are a Senior software engineer with extensive experience in Spring Boot and Apache Kafka

## Goal

Design and implement reliable Kafka messaging in Spring Boot using `spring-boot-starter-kafka` and Spring Kafka auto-configuration. Prefer typed event records, keyed producers for ordered processing, and declarative error handling with dead-letter topics over silent exception swallowing. Keep listeners thin — delegate business logic to services. Guard consumers against poison messages and replay with idempotency on the eventId.

**What is covered in this Skill?**

- Maven `spring-boot-starter-kafka` dependency aligned with the Spring Boot BOM (raw `spring-kafka` only for non-Boot library modules)
- JSON event serialization via `DefaultKafkaProducerFactoryCustomizer` / `DefaultKafkaConsumerFactoryCustomizer` — do not duplicate manual `ProducerFactory` / `ConsumerFactory` beans when using `@ServiceConnection`
- Versioned event schemas as Java records with explicit `eventId`, `schemaVersion`, and aggregate key
- Topic naming conventions (`domain.entity.operation.v{N}`)
- `KafkaTemplate<K, V>` typed producer with explicit key strategy
- `@KafkaListener` consumer with explicit `groupId`, `topics`, and typed payload
- `ConcurrentKafkaListenerContainerFactory` for concurrency and batch vs. single-record modes
- `DefaultErrorHandler` with `FixedBackOff` / `ExponentialBackOff` and `DeadLetterPublishingRecoverer`
- Idempotent consumer pattern using `eventId` de-duplication store
- Kafka transactions for exactly-once producer semantics
- Integration testing with Testcontainers `@ServiceConnection` (full-stack `*IT`) and `@EmbeddedKafka` (in-process broker tests)
- `@TestConfiguration` with `NewTopic` beans imported only by integration tests

**Scope:** Apply recommendations based on the reference rules and good/bad code examples. For HTTP + Testcontainers wiring in `*IT` classes, cross-reference `@322-frameworks-spring-boot-testing-integration-tests`.

## Constraints

Before applying any Kafka changes, ensure the project compiles. Compilation failure is a BLOCKING condition.

- **MANDATORY**: Run `./mvnw compile` or `mvn compile` before applying any change
- **SAFETY**: If compilation fails, stop immediately
- **VERIFY**: Run `./mvnw clean verify` or `mvn clean verify` after applying improvements
- **INJECTION**: Never build topic names or Kafka header values from untrusted user input
- **BEFORE APPLYING**: Read the reference for detailed rules and good/bad patterns
- **EDGE CASE**: If the user goal is ambiguous, stop and ask a clarifying question before editing files
- **EDGE CASE**: If required context, files, or tools are missing, report the blocker explicitly

## Examples

### Table of contents

- Example 1: Maven dependency
- Example 2: Event schema design
- Example 3: Producer implementation
- Example 4: Consumer implementation
- Example 5: Error handling and dead-letter topic
- Example 6: Idempotent consumer
- Example 7: Integration testing with @EmbeddedKafka
- Example 8: JSON serialization with Boot auto-configuration
- Example 9: Full-stack integration test with Testcontainers

### Example 1: Maven dependency

Title: Use spring-boot-starter-kafka in Boot apps; raw spring-kafka only in library modules
Description: Spring Boot manages Kafka versions via its BOM. In Spring Boot applications, prefer `spring-boot-starter-kafka` so `KafkaConnectionDetails`, `@ServiceConnection`, and factory customizers integrate correctly. Declaring an explicit version pins the library and can cause classpath conflicts. Use the raw `spring-kafka` artifact only in library modules that do not depend on Spring Boot auto-configuration.

**Good example:**

```xml
<!-- pom.xml — Spring Boot application: use the starter -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-kafka</artifactId>
</dependency>
<!-- Optional for JSON event records at compile time -->
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
</dependency>
```

**Bad example:**

```xml
<!-- Bad in a Boot app: raw artifact skips Boot Kafka auto-configuration;
     explicit version fights the BOM -->
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
    <version>3.1.0</version>
</dependency>
```

### Example 2: Event schema design

Title: Versioned immutable records with eventId and aggregate key
Description: Define each Kafka event as a Java record. Include a unique `eventId` (UUID) for consumer de-duplication, a `schemaVersion` string for forward compatibility, and the aggregate's natural key (e.g. `orderId`). Use the aggregate key as the Kafka message key so all events for the same aggregate land in the same partition, preserving ordering. Topic names follow the pattern `domain.entity.operation.v{N}` to allow parallel consumers on older and newer versions.

**Good example:**

```java
import java.time.Instant;

// Immutable event schema — one record per event type
public record OrderCreatedEvent(
    String eventId,       // UUID; used for consumer-side de-duplication
    String schemaVersion, // "v1"; bump when non-backward-compatible fields change
    String orderId,       // aggregate key → use as Kafka message key
    String customerId,
    Instant occurredAt
) {
    public static OrderCreatedEvent of(String orderId, String customerId) {
        return new OrderCreatedEvent(
            java.util.UUID.randomUUID().toString(),
            "v1",
            orderId,
            customerId,
            Instant.now()
        );
    }
}
// topic: orders.created.v1  key: orderId
```

**Bad example:**

```java
// Bad: generic map payload — no schema, no version, no stable key
Map<String, Object> event = Map.of(
    "type", "orderCreated",
    "data", "..."
);
// topic: events   key: null (random partition assignment)
```

### Example 3: Producer implementation

Title: Typed KafkaTemplate with explicit key; handle send failure
Description: Inject a typed `KafkaTemplate&lt;String, OrderCreatedEvent&gt;` so the compiler enforces the key/value contract. Always pass the aggregate's key as the Kafka record key. Return the `CompletableFuture` to the caller (or await it at a command boundary) so broker-side send failures are observable. Do not throw from a `whenComplete` callback and assume the caller will see it.

**Good example:**

```java
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Service
class OrderEventPublisher {

    private static final String TOPIC = "orders.created.v1";

    private final KafkaTemplate<String, OrderCreatedEvent> template;

    OrderEventPublisher(KafkaTemplate<String, OrderCreatedEvent> template) {
        this.template = template;
    }

    CompletableFuture<SendResult<String, OrderCreatedEvent>> publish(OrderCreatedEvent event) {
        return template.send(TOPIC, event.orderId(), event)
            .exceptionally(ex -> {
                throw new CompletionException("Kafka send failed for order: " + event.orderId(), ex);
            });
    }
}
```

**Bad example:**

```java
// Bad: raw KafkaTemplate<String, String> loses type safety;
// null key loses partition ordering; ignored CompletableFuture drops send errors
@Service
class OrderEventPublisher {
    @Autowired
    KafkaTemplate<String, String> template;

    void publish(OrderCreatedEvent event) {
        template.send("events", null, event.toString()); // no key, serialized via toString()
        // CompletableFuture discarded — broker errors silently lost
    }
}
```

### Example 4: Consumer implementation

Title: @KafkaListener with typed payload and thin handler
Description: Annotate the listener class with `@Component`. Declare `topics` and `groupId` on the `@KafkaListener` so the consumer group is explicit and testable. Accept the typed event record as the method parameter. Delegate all business logic to an application service — the listener method should only translate the Kafka message into a service call.

**Good example:**

```java
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
class BillingEventListener {

    private final BillingService billingService;

    BillingEventListener(BillingService billingService) {
        this.billingService = billingService;
    }

    @KafkaListener(topics = "orders.created.v1", groupId = "billing-service-v1")
    void onOrderCreated(OrderCreatedEvent event) {
        billingService.createInvoice(event.orderId(), event.customerId());
    }
}
```

**Bad example:**

```java
// Bad: no groupId means a random group on each restart causing re-read from the beginning;
// String payload requires manual parsing; business logic mixed directly in listener
@KafkaListener(topics = "orders.created.v1")
void onMessage(String rawJson) {
    var orderId = rawJson.split(",")[0]; // fragile parsing
    // 50 lines of business logic inline ...
}
```

### Example 5: Error handling and dead-letter topic

Title: DefaultErrorHandler with backoff and DeadLetterPublishingRecoverer
Description: Configure a `DefaultErrorHandler` bean that retries with exponential back-off and then routes unrecoverable messages to a dead-letter topic via `DeadLetterPublishingRecoverer`. The recoverer automatically targets `{topic}.DLT` by default. Register the handler on the container factory so it applies to all listeners. Do not swallow exceptions inside the listener — let them propagate so the container's error handler can decide.

**Good example:**

```java
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.ExponentialBackOff;

@Configuration
class KafkaErrorConfig {

    @Bean
    DefaultErrorHandler errorHandler(KafkaTemplate<Object, Object> template) {
        var recoverer = new DeadLetterPublishingRecoverer(template,
            (record, ex) -> new TopicPartition(record.topic() + ".DLT", -1));

        var backOff = new ExponentialBackOff(500L, 2.0);
        backOff.setMaxElapsedTime(30_000L); // stop retrying after 30 s

        return new DefaultErrorHandler(recoverer, backOff);
    }
}
```

**Bad example:**

```java
// Bad: swallowing the exception causes acknowledgement of poison messages;
// they are never retried and never routed to a DLT
@KafkaListener(topics = "orders.created.v1", groupId = "billing-service-v1")
void onOrderCreated(OrderCreatedEvent event) {
    try {
        billingService.createInvoice(event.orderId(), event.customerId());
    } catch (Exception ignored) {
        // silent: message acknowledged, data loss guaranteed
    }
}
```

### Example 6: Idempotent consumer

Title: De-duplicate on eventId to survive retries and rebalances
Description: Kafka at-least-once delivery means a consumer can receive the same message more than once — on broker retries, after a rebalance, or when consuming from a DLT. Check the `eventId` against a de-duplication store (an in-memory set in dev/test; a database table or Redis in production) before processing. Persist the `eventId` inside the same transaction as the business side effect.

**Good example:**

```java
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
class IdempotentBillingListener {

    private final Set<String> processedEventIds = ConcurrentHashMap.newKeySet();
    private final BillingService billingService;

    IdempotentBillingListener(BillingService billingService) {
        this.billingService = billingService;
    }

    @KafkaListener(topics = "orders.created.v1", groupId = "billing-service-v1")
    @Transactional
    void onOrderCreated(OrderCreatedEvent event) {
        if (!processedEventIds.add(event.eventId())) {
            return; // already processed — skip without error
        }
        billingService.createInvoice(event.orderId(), event.customerId());
    }
}
```

**Bad example:**

```java
@KafkaListener(topics = "orders.created.v1", groupId = "billing-service-v1")
void onOrderCreated(OrderCreatedEvent event) {
    // Bad: no idempotency guard — rebalance or retry doubles billing
    billingService.createInvoice(event.orderId(), event.customerId());
}
```

### Example 7: Integration testing with @EmbeddedKafka

Title: In-process broker for producer/consumer serialization tests
Description: Use `@EmbeddedKafka` when you need an in-process broker to verify serialization and topic routing without Docker. For full-stack integration tests that boot the application with HTTP and real infrastructure, prefer Testcontainers `@ServiceConnection` (see the next example). Do not mock `KafkaTemplate` in tests meant to verify messaging behaviour. In Spring Boot 4.x use `@MockitoBean`, not `@MockBean`.

**Good example:**

```java
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EmbeddedKafka(topics = {"orders.created.v1"}, partitions = 1)
class OrderEventPublisherIT {

    @Autowired
    OrderEventPublisher publisher;

    @Autowired
    EmbeddedKafkaBroker broker;

    @Autowired
    ConsumerFactory<String, OrderCreatedEvent> consumerFactory;

    @Test
    void publish_sendsEventToTopic() {
        OrderCreatedEvent event = OrderCreatedEvent.of("order-1", "cust-1");
        publisher.publish(event);

        try (Consumer<String, OrderCreatedEvent> consumer = consumerFactory.createConsumer("test-group", "")) {
            broker.consumeFromAnEmbeddedTopic(consumer, "orders.created.v1");
            ConsumerRecords<String, OrderCreatedEvent> records = KafkaTestUtils.getRecords(consumer);
            assertThat(records.count()).isEqualTo(1);
            assertThat(records.iterator().next().value().orderId()).isEqualTo("order-1");
        }
    }
}
```

**Bad example:**

```java
// Bad: mocking KafkaTemplate proves nothing — does not test serialization or topic routing
@SpringBootTest
class OrderEventPublisherTest {

    @MockitoBean
    KafkaTemplate<String, OrderCreatedEvent> template;

    @Autowired
    OrderEventPublisher publisher;

    @Test
    void publish_callsTemplate() {
        publisher.publish(OrderCreatedEvent.of("o1", "c1"));
        verify(template).send(any(), any(), any()); // verifies Mockito glue only
    }
}
```

### Example 8: JSON serialization with Boot auto-configuration

Title: DefaultKafkaProducerFactoryCustomizer / DefaultKafkaConsumerFactoryCustomizer — not manual factories
Description: When publishing typed Java records, configure JSON serializers on Boot's auto-configured Kafka client factories. Use `DefaultKafkaProducerFactoryCustomizer` and `DefaultKafkaConsumerFactoryCustomizer` with `updateConfigs()` so bootstrap servers from `@ServiceConnection` or `spring.kafka.bootstrap-servers` stay intact. Do **not** declare separate `@Bean ProducerFactory` / `@Bean ConsumerFactory` built from `KafkaProperties.buildProducerProperties()` — those often capture `localhost:9092` while Testcontainers runs on a dynamic port, and `@ServiceConnection` may still default producers to `StringSerializer`, causing `SerializationException` on typed events. In Spring Boot 4.x, `KafkaProperties.buildProducerProperties()` and `buildConsumerProperties()` take no arguments.

**Good example:**

```java
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.kafka.autoconfigure.DefaultKafkaConsumerFactoryCustomizer;
import org.springframework.boot.kafka.autoconfigure.DefaultKafkaProducerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
class KafkaConfig {

    @Bean
    DefaultKafkaProducerFactoryCustomizer jsonProducerFactoryCustomizer() {
        return producerFactory -> producerFactory.updateConfigs(Map.of(
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class));
    }

    @Bean
    DefaultKafkaConsumerFactoryCustomizer jsonConsumerFactoryCustomizer() {
        return consumerFactory -> consumerFactory.updateConfigs(Map.of(
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class,
            JsonDeserializer.TRUSTED_PACKAGES, "com.example.events",
            JsonDeserializer.VALUE_DEFAULT_TYPE, OrderCreatedEvent.class.getName()));
    }
}
```

**Bad example:**

```java
// Bad: manual factories bypass @ServiceConnection bootstrap wiring and often stick to localhost:9092
@Configuration
class KafkaConfig {

    @Bean
    ProducerFactory<String, OrderCreatedEvent> producerFactory(KafkaProperties props) {
        Map<String, Object> config = props.buildProducerProperties();
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config); // wrong broker in *IT tests
    }

    @Bean
    KafkaTemplate<String, OrderCreatedEvent> template(ProducerFactory<String, OrderCreatedEvent> pf) {
        return new KafkaTemplate<>(pf);
    }
}
```

### Example 9: Full-stack integration test with Testcontainers

Title: @ServiceConnection on KafkaContainer; NewTopic in @TestConfiguration
Description: For `*IT` classes that POST to HTTP and assert a `@KafkaListener` received an event, start a `KafkaContainer` with Testcontainers and wire it via `@ServiceConnection` (Spring Boot 4.x). Do not set `spring.kafka.bootstrap-servers=localhost:9092` in shared `src/test/resources/application.properties` — that overrides the container port. Declare `NewTopic` in a `@TestConfiguration` imported only by the IT class. Enable listeners in the IT (`spring.kafka.listener.auto-startup=true`) while keeping them disabled in unit tests. Use Maven Failsafe for `*IT` classes. Testcontainers 2.x Maven artifacts are `testcontainers-kafka` and `testcontainers-junit-jupiter`. Cross-reference `@322-frameworks-spring-boot-testing-integration-tests` for HTTP client and container lifecycle details. Resolve the Kafka container image from trusted project or CI configuration instead of hard-coding a public registry image in reusable guidance. Prefer organization-approved images pinned by digest.

**Good example:**

```java
import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.config.TopicBuilder;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@Import(OrderKafkaTestConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = "spring.kafka.listener.auto-startup=true")
class OrderCreatedIT {

    @Container
    @ServiceConnection
    static KafkaContainer kafka = new KafkaContainer(approvedKafkaImage());

    private static DockerImageName approvedKafkaImage() {
        return DockerImageName.parse(System.getProperty("test.kafka.image"));
    }

    @Test
    void createOrder_publishesOrderCreatedEvent() {
        // POST via TestRestTemplate; await listener-side assertion
    }
}

@TestConfiguration
class OrderKafkaTestConfiguration {

    @Bean
    NewTopic orderCreatedTopic() {
        return TopicBuilder.name("orders.created.v1").partitions(1).replicas(1).build();
    }
}
```

**Bad example:**

```java
# src/test/resources/application.properties — Bad with @ServiceConnection ITs:
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.listener.auto-startup=false

// Forces producer/consumer to localhost:9092 while Testcontainers exposes a random port.
// Also disables the listener in full-stack IT unless every class overrides properties.
```


## Output Format

- **ANALYZE** Kafka code: event schema versioning, producer key strategy, listener group isolation, error handler registration, idempotency guards, and test coverage
- **CATEGORIZE** issues by impact (RELIABILITY for missing retries/DLT, CORRECTNESS for missing idempotency, MAINTAINABILITY for untyped schemas, SECURITY for untrusted topic name injection)
- **APPLY** Spring Kafka–aligned fixes: type the templates, key producers on the aggregate id, register DefaultErrorHandler with backoff and DLT, add eventId de-duplication
- **IMPLEMENT** changes so topic configs, serializers, and tests stay consistent
- **EXPLAIN** trade-offs (at-least-once vs exactly-once, fixed vs exponential backoff, in-process vs Testcontainers Kafka)
- **TEST** with Testcontainers `@ServiceConnection` for full-stack `*IT` tests; use `@EmbeddedKafka` for in-process serialization tests; `@MockitoBean KafkaTemplate` only for context-load tests without a broker
- **VALIDATE** with `./mvnw compile` before and `./mvnw clean verify` after changes


## Safeguards

- **BLOCKING SAFETY CHECK**: Run `./mvnw compile` before ANY Kafka refactoring — compilation failure is a HARD STOP
- **CRITICAL VALIDATION**: Run `./mvnw clean verify` after changes; exercise listener integration tests before promoting
- **INJECTION SAFETY**: Never construct topic names or message headers from untrusted user input
- **ERROR HANDLING**: Never swallow exceptions inside a `@KafkaListener` — propagate so the container's error handler can retry or route to DLT
- **IDEMPOTENCY**: Always de-duplicate on `eventId` — Kafka's at-least-once guarantee means consumers must tolerate duplicates
- **KEY STRATEGY**: Always set the Kafka message key to the aggregate's natural key; null keys disable partition-level ordering
- **DLT MONITORING**: Set up alerting on the DLT topic — messages landing there indicate systematic processing failures
- **SERVICE CONNECTION**: Never hand-wire `spring.kafka.bootstrap-servers` when `@ServiceConnection` on `KafkaContainer` already applies; never duplicate manual `ProducerFactory` / `ConsumerFactory` beans unless you also wire `KafkaConnectionDetails`
- **JSON SERDE**: For typed event records with `@ServiceConnection`, prefer factory customizers with `updateConfigs()` over `application.properties` alone — defaults may still use `StringSerializer`
- **INCREMENTAL SAFETY**: Change one producer/consumer surface at a time; verify with `@EmbeddedKafka` or Testcontainers `*IT` tests between steps