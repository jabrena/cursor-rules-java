---
description: 
globs: 
alwaysApply: false
---
# Spring Boot Local Testing with Docker Compose

Best practices for local testing in Spring Boot applications using `spring-boot-docker-compose` for seamless integration with external services like databases, message queues, and caches.

## Implementing These Principles

These guidelines are built upon the following core principles:

- **Seamless Integration**: Use spring-boot-docker-compose to automatically manage external service dependencies
- **Environment Parity**: Maintain consistency between local development and production environments
- **Test Isolation**: Ensure tests are independent and can run in any order without side effects
- **Performance Optimization**: Minimize container startup time and resource usage for faster development cycles
- **Configuration Management**: Use profiles and dynamic properties for flexible environment-specific configurations

## Table of contents

- Rule 1: Dependency Configuration
- Rule 2: Docker Compose Service Definition
- Rule 3: Application Profile Configuration
- Rule 4: Integration Test Setup
- Rule 5: Service Connection Management
- Rule 6: Health Check Implementation
- Rule 7: Test Data Management
- Rule 8: Performance Optimization

## Rule 1: Dependency Configuration

Title: Proper Spring Boot Docker Compose Dependency Setup
Description: Configure the spring-boot-docker-compose dependency correctly for runtime-only usage to automatically manage Docker services during application startup.

**Good example:**

```xml
<!-- Maven -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-docker-compose</artifactId>
    <scope>runtime</scope>
</dependency>

<!-- Testcontainers for integration tests -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-testcontainers</artifactId>
    <scope>test</scope>
</dependency>
```

**Bad Example:**

```xml
<!-- Don't include as compile dependency -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-docker-compose</artifactId>
    <scope>compile</scope>
</dependency>

<!-- Missing testcontainers dependency -->
```

## Rule 2: Docker Compose Service Definition

Title: Well-structured Docker Compose Configuration
Description: Define services with proper health checks, environment variables, and port mappings for reliable local testing.

**Good example:**

```yaml
# compose.yaml
services:
  postgres:
    image: 'postgres:15'
    environment:
      POSTGRES_DB: testdb
      POSTGRES_USER: testuser
      POSTGRES_PASSWORD: testpass
    ports:
      - '5432:5432'
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U testuser -d testdb"]
      interval: 10s
      timeout: 5s
      retries: 5
    volumes:
      - postgres_data:/var/lib/postgresql/data

volumes:
  postgres_data:
```

**Bad Example:**

```yaml
# compose.yaml
services:
  postgres:
    image: 'postgres'  # No version specified
    environment:
      - POSTGRES_PASSWORD=password  # Hardcoded, no DB/USER
    # Missing health checks
    # Missing volume persistence
    # Missing proper port mapping
```

## Rule 3: Application Profile Configuration

Title: Environment-specific Configuration Management
Description: Use Spring profiles to manage different configurations for local development, testing, and production environments.

**Good example:**

```yaml
# application-local.yml
spring:
  docker:
    compose:
      enabled: true
      file: compose.yaml
      lifecycle-management: start_and_stop
      readiness:
        wait: HEALTHY
        timeout: 2m
  
  datasource:
    url: jdbc:postgresql://localhost:5432/testdb
    username: testuser
    password: testpass
    
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true

logging:
  level:
    org.springframework.boot.docker: DEBUG
```

**Bad Example:**

```yaml
# application.yml
spring:
  docker:
    compose:
      enabled: true  # Always enabled, no profile separation
      lifecycle-management: none  # No lifecycle management
  
  datasource:
    url: jdbc:postgresql://localhost:5432/proddb  # Production DB in local config
    username: root  # Unsafe credentials
    password: admin
```

## Rule 4: Integration Test Setup

Title: Testcontainers Integration with Service Connections
Description: Use @ServiceConnection and @Testcontainers for clean integration test setup with automatic container management.

**Good example:**

```java
@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class UserRepositoryIntegrationTest {
    
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    void shouldSaveAndRetrieveUser() {
        User user = new User("john.doe@example.com", "John Doe");
        User saved = userRepository.save(user);
        
        assertThat(saved.getId()).isNotNull();
        assertThat(userRepository.findByEmail("john.doe@example.com"))
                .isPresent()
                .get()
                .extracting(User::getName)
                .isEqualTo("John Doe");
    }
}
```

**Bad Example:**

```java
@SpringBootTest
class UserRepositoryIntegrationTest {
    
    // No container management
    // No profile specification
    // Assumes external database is running
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    void shouldSaveAndRetrieveUser() {
        // Test may fail if database is not available
        // No cleanup between tests
        User user = new User("john.doe@example.com", "John Doe");
        userRepository.save(user);
        // No proper assertions
    }
}
```

## Rule 5: Service Connection Management

Title: Dynamic Service Configuration
Description: Use @DynamicPropertySource and @ServiceConnection for flexible service configuration in tests.

**Good example:**

```java
@TestConfiguration
public class TestContainersConfiguration {
    
    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>("postgres:15")
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test")
                .withReuse(true);  // Reuse container across tests
    }
    
    @Bean
    @ServiceConnection
    RedisContainer redisContainer() {
        return new RedisContainer("redis:7-alpine")
                .withReuse(true);
    }
}

// Alternative approach with @DynamicPropertySource
@DynamicPropertySource
static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
}
```

**Bad Example:**

```java
@TestConfiguration
public class TestConfiguration {
    
    // Hardcoded connection properties
    @Bean
    public DataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:postgresql://localhost:5432/testdb");
        dataSource.setUsername("test");
        dataSource.setPassword("test");
        return dataSource;
    }
    
    // No container lifecycle management
    // No dynamic property configuration
}
```

## Rule 6: Health Check Implementation

Title: Robust Service Health Monitoring
Description: Implement proper health checks for all external services to ensure they are ready before running tests.

**Good example:**

```yaml
# compose.yaml
services:
  postgres:
    image: postgres:15
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U testuser -d testdb"]
      interval: 10s
      timeout: 5s
      retries: 5
      start_period: 30s
      
  redis:
    image: redis:7-alpine
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s
      timeout: 3s
      retries: 3
      
  elasticsearch:
    image: docker.elastic.co/elasticsearch/elasticsearch:8.11.0
    healthcheck:
      test: ["CMD-SHELL", "curl -f http://localhost:9200/_cluster/health || exit 1"]
      interval: 30s
      timeout: 10s
      retries: 5
```

**Bad Example:**

```yaml
# compose.yaml
services:
  postgres:
    image: postgres:15
    # No health check - application may start before DB is ready
    
  redis:
    image: redis:7-alpine
    # No readiness verification
    
  elasticsearch:
    image: docker.elastic.co/elasticsearch/elasticsearch:8.11.0
    # May cause intermittent test failures
```

## Rule 7: Test Data Management

Title: Clean Test Data Handling
Description: Ensure proper test data setup and cleanup for reliable and isolated tests.

**Good example:**

```java
@SpringBootTest
@Testcontainers
@Transactional
@Rollback
class OrderServiceIntegrationTest {
    
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Test
    @Sql(scripts = "/test-data/orders.sql", executionPhase = BEFORE_TEST_METHOD)
    @Sql(scripts = "/test-data/cleanup.sql", executionPhase = AFTER_TEST_METHOD)
    void shouldProcessOrderCorrectly() {
        Order order = orderService.createOrder(createOrderRequest());
        
        entityManager.flush();
        entityManager.clear();
        
        Order retrieved = orderService.findById(order.getId());
        assertThat(retrieved.getStatus()).isEqualTo(OrderStatus.PENDING);
    }
    
    private OrderRequest createOrderRequest() {
        return OrderRequest.builder()
                .customerId(1L)
                .items(List.of(new OrderItem("product-1", 2)))
                .build();
    }
}
```

**Bad Example:**

```java
@SpringBootTest
class OrderServiceIntegrationTest {
    
    @Autowired
    private OrderService orderService;
    
    @Test
    void shouldProcessOrderCorrectly() {
        // No data cleanup - may affect other tests
        // No transaction management
        // Hardcoded data that may conflict
        Order order = new Order();
        order.setId(1L);  // Hardcoded ID
        order.setCustomerId(999L);  // May not exist
        
        orderService.save(order);
        // No proper verification
    }
}
```

## Rule 8: Performance Optimization

Title: Optimized Container and Test Execution
Description: Configure container reuse and optimize resource usage for faster development cycles.

**Good example:**

```properties
# application-test.properties
spring.docker.compose.lifecycle-management=start_only
testcontainers.reuse.enable=true

# Resource optimization
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=false
logging.level.org.hibernate.SQL=WARN
```

```java
@TestMethodOrder(OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServicePerformanceTest {
    
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withReuse(true)
            .withTmpFs(Map.of("/var/lib/postgresql/data", "rw"));
    
    @BeforeAll
    void setupTestData() {
        // Setup once for all tests
    }
    
    @AfterAll
    void cleanup() {
        // Cleanup once after all tests
    }
}
```

**Bad Example:**

```properties
# application-test.properties
spring.docker.compose.lifecycle-management=start_and_stop
# No container reuse - slow test execution

spring.jpa.show-sql=true  # Verbose logging slows down tests
logging.level.org.hibernate=DEBUG
```

```java
class UserServiceTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");
    // No reuse configuration
    // No memory optimization
    
    @BeforeEach
    void setup() {
        // Expensive setup for each test
        populateDatabase();
    }
    
    @AfterEach
    void cleanup() {
        // Expensive cleanup for each test
        clearDatabase();
    }
}