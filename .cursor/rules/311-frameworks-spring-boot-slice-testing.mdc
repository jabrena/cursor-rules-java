---
description: 
globs: 
alwaysApply: false
---
# Spring Boot Slice Testing

Spring Boot slice testing allows you to test specific layers or "slices" of your application in isolation, providing faster and more focused tests than full integration tests. This approach helps maintain test clarity, reduces test execution time, and improves maintainability.

## Implementing These Principles

These guidelines are built upon the following core principles:

- **Layer Isolation**: Test each application layer independently without loading the entire Spring context
- **Focused Testing**: Use appropriate slice annotations to load only the components needed for specific functionality
- **Mock Dependencies**: Mock external dependencies and other layers to achieve true unit testing at the slice level
- **Fast Execution**: Minimize Spring context loading to achieve rapid test feedback cycles

## Table of contents

- Rule 1: Use @WebMvcTest for Web Layer Testing
- Rule 2: Use @JdbcTest for Repository Layer Testing
- Rule 3: Use @JsonTest for JSON Serialization Testing
- Rule 4: Use @MockBean for Mocking Dependencies
- Rule 5: Configure Test Profiles Appropriately
- Rule 6: Use @TestConfiguration for Custom Test Setup

## Rule 1: Use @WebMvcTest for Web Layer Testing

Title: Test Controllers in Isolation with @WebMvcTest
Description: Use @WebMvcTest to test only the web layer (controllers) without loading the full application context. This annotation configures Spring MVC infrastructure and auto-configures MockMvc for testing HTTP requests and responses.

**Good example:**

```java
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UserService userService;
    
    @Test
    void shouldReturnUserWhenValidId() throws Exception {
        // Given
        User user = new User(1L, "John Doe", "john@example.com");
        when(userService.findById(1L)).thenReturn(user);
        
        // When & Then
        mockMvc.perform(get("/api/users/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("John Doe"))
            .andExpect(jsonPath("$.email").value("john@example.com"));
    }
}
```

**Bad Example:**

```java
@SpringBootTest
@AutoConfigureTestDatabase
class UserControllerTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void shouldReturnUser() {
        // This loads the entire application context unnecessarily
        // and requires database setup for a simple controller test
        ResponseEntity<User> response = restTemplate.getForEntity("/api/users/1", User.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
```

## Rule 2: Use @JdbcTest for Repository Layer Testing

Title: Test JDBC Repositories with @JdbcTest
Description: Use @JdbcTest to test Spring Data JDBC repositories in isolation. This annotation configures an in-memory database, auto-configures JdbcTemplate and NamedParameterJdbcTemplate, and loads Spring Data JDBC repositories without loading the full application context.

**Good example:**

```java
@JdbcTest
class UserRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    void shouldFindUserByEmail() {
        // Given
        User user = new User(null, "John Doe", "john@example.com");
        User saved = userRepository.save(user);
        
        // When
        Optional<User> found = userRepository.findByEmail("john@example.com");
        
        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("John Doe");
        assertThat(found.get().getEmail()).isEqualTo("john@example.com");
    }
    
    @Test
    void shouldReturnEmptyWhenUserNotFound() {
        // When
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");
        
        // Then
        assertThat(found).isEmpty();
    }
    
    @Test
    void shouldUseJdbcTemplateForCustomQueries() {
        // Given
        jdbcTemplate.update(
            "INSERT INTO users (name, email) VALUES (?, ?)", 
            "Jane Smith", "jane@example.com"
        );
        
        // When
        Long count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM users WHERE email LIKE '%@example.com'", 
            Long.class
        );
        
        // Then
        assertThat(count).isEqualTo(1L);
    }
}
```

**Bad Example:**

```java
@SpringBootTest
class UserRepositoryTest {
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    void shouldFindUserByEmail() {
        // This loads the entire application context and all beans
        // unnecessarily for a simple repository test
        User user = new User(null, "John Doe", "john@example.com");
        userRepository.save(user);
        
        Optional<User> found = userRepository.findByEmail("john@example.com");
        assertThat(found).isPresent();
    }
}
```

## Rule 3: Use @JsonTest for JSON Serialization Testing

Title: Test JSON Serialization/Deserialization with @JsonTest
Description: Use @JsonTest to test JSON serialization and deserialization logic in isolation. This annotation auto-configures Jackson ObjectMapper and provides JacksonTester helper for testing JSON operations.

**Good example:**

```java
@JsonTest
class UserJsonTest {

    @Autowired
    private JacksonTester<User> json;
    
    @Test
    void shouldSerializeUser() throws Exception {
        // Given
        User user = new User(1L, "John Doe", "john@example.com");
        
        // When & Then
        assertThat(json.write(user))
            .hasJsonPathNumberValue("$.id", 1)
            .hasJsonPathStringValue("$.name", "John Doe")
            .hasJsonPathStringValue("$.email", "john@example.com");
    }
    
    @Test
    void shouldDeserializeUser() throws Exception {
        // Given
        String content = """
            {
                "id": 1,
                "name": "John Doe", 
                "email": "john@example.com"
            }
            """;
        
        // When & Then
        assertThat(json.parse(content))
            .usingRecursiveComparison()
            .isEqualTo(new User(1L, "John Doe", "john@example.com"));
    }
}
```

**Bad Example:**

```java
@SpringBootTest
class UserJsonTest {
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    void shouldSerializeUser() throws Exception {
        // Loading full application context just for JSON testing
        // is overkill and slow
        User user = new User(1L, "John Doe", "john@example.com");
        String json = objectMapper.writeValueAsString(user);
        
        assertThat(json).contains("John Doe");
    }
}
```

## Rule 4: Use @MockBean for Mocking Dependencies

Title: Mock External Dependencies with @MockBean
Description: Use @MockBean to mock Spring beans that are dependencies of the component under test. This replaces the bean in the Spring context with a Mockito mock, allowing you to control its behavior during tests.

**Good example:**

```java
@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private OrderService orderService;
    
    @MockBean 
    private PaymentService paymentService;
    
    @Test
    void shouldCreateOrder() throws Exception {
        // Given
        CreateOrderRequest request = new CreateOrderRequest("Product A", 2);
        Order order = new Order(1L, "Product A", 2, BigDecimal.valueOf(100.00));
        
        when(orderService.createOrder(any(CreateOrderRequest.class))).thenReturn(order);
        
        // When & Then
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "productName": "Product A",
                        "quantity": 2
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.productName").value("Product A"));
    }
}
```

**Bad Example:**

```java
@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    // Missing @MockBean - this will cause the test to fail
    // because OrderService is not available in the context
    
    @Test
    void shouldCreateOrder() throws Exception {
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isCreated());
        // This test will fail due to missing dependencies
    }
}
```

## Rule 5: Configure Test Profiles Appropriately

Title: Use Test Profiles for Environment-Specific Configuration
Description: Configure specific test profiles to override application properties for testing scenarios. Use @ActiveProfiles to activate test-specific configurations that differ from production settings.

**Good example:**

```java
@JdbcTest
@ActiveProfiles("test")
class UserRepositoryIntegrationTest {

    @Autowired
    private UserRepository userRepository;
    
    @Test
    void shouldUseTestDatabaseConfiguration() {
        // Test will use application-test.yml configuration
        // which might specify H2 in-memory database
        User user = new User(null, "Test User", "test@example.com");
        User saved = userRepository.save(user);
        
        assertThat(saved.getId()).isNotNull();
    }
}
```

**Bad Example:**

```java
@JdbcTest
class UserRepositoryIntegrationTest {

    // No @ActiveProfiles annotation
    // This might use production database configuration
    // leading to unreliable or slow tests
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    void shouldSaveUser() {
        User user = new User(null, "Test User", "test@example.com");
        userRepository.save(user);
    }
}
```

## Rule 6: Use @TestConfiguration for Custom Test Setup

Title: Create Custom Test Configuration with @TestConfiguration
Description: Use @TestConfiguration to define test-specific bean configurations that override or supplement the main application configuration during testing.

**Good example:**

```java
@WebMvcTest(UserController.class)
class UserControllerTest {

    @TestConfiguration
    static class TestConfig {
        
        @Bean
        @Primary
        public Clock testClock() {
            return Clock.fixed(
                Instant.parse("2023-12-01T10:00:00Z"), 
                ZoneOffset.UTC
            );
        }
    }
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UserService userService;
    
    @Test
    void shouldUseFixedTimeForTesting() throws Exception {
        // Test with predictable time for consistent results
        mockMvc.perform(get("/api/users/current-time"))
            .andExpect(status().isOk())
            .andExpect(content().string("2023-12-01T10:00:00Z"));
    }
}
```

**Bad Example:**

```java
@WebMvcTest(UserController.class)
class UserControllerTest {

    // No test configuration for time-dependent tests
    // This makes tests unreliable and hard to reproduce
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UserService userService;
    
    @Test
    void shouldReturnCurrentTime() throws Exception {
        mockMvc.perform(get("/api/users/current-time"))
            .andExpect(status().isOk());
        // Cannot assert exact time value due to system clock dependency
    }
}
```

### Additional Slice Testing Annotations

**@WebFluxTest**: For testing Spring WebFlux reactive web applications
**@RestClientTest**: For testing REST clients and @RestTemplate configurations  
**@AutoConfigureTestDatabase**: For configuring test databases in slice tests
**@TestPropertySource**: For overriding specific properties in test scenarios
**@DataJdbcTest**: Alternative to @JdbcTest that focuses specifically on Spring Data JDBC repositories
**@Sql**: For executing SQL scripts before test execution in JDBC tests