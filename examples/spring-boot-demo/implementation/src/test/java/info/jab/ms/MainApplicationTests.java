package info.jab.ms;

import info.jab.ms.common.PostgreSQLTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for the Demo Application.
 *
 * Following Java Unit Testing Guidelines:
 * - Given-When-Then structure
 * - Descriptive test names using should_ExpectedBehavior_when_StateUnderTest pattern
 * - AssertJ for fluent assertions
 * - Clear test documentation
 *
 * This class extends PostgreSQLTestBase to inherit the common TestContainer setup
 * with Sakila schema and test data. It provides basic smoke tests to verify
 * the Spring Boot application context loads correctly with the database.
 */
@SpringBootTest
@DisplayName("Main Application Integration Tests")
class MainApplicationTests extends PostgreSQLTestBase {

    /**
     * Basic smoke test to verify the Spring Boot application context loads
     * successfully with the PostgreSQL TestContainer.
     *
     * This test validates:
     * - Spring Boot application context loads without errors
     * - Database connection is established via @ServiceConnection
     * - All auto-configuration works properly
     * - TestContainer infrastructure is working correctly
     */
    @Test
    @DisplayName("Should load Spring context successfully with TestContainer database")
    void should_loadSpringContextSuccessfully_when_testContainerDatabaseIsAvailable() {
        // Given - TestContainer PostgreSQL is started and Spring context is loaded

        // When - We check the container status and connectivity
        var container = getPostgresContainer();

        // Then - Container should be running and properly configured
        assertThat(container.isRunning())
            .as("PostgreSQL container should be running")
            .isTrue();

        assertThat(container.getDatabaseName())
            .as("Database name should match test configuration")
            .isEqualTo("testdb");

        assertThat(container.getUsername())
            .as("Username should match test configuration")
            .isEqualTo("testuser");

        // And - Connection properties should be valid
        assertThat(container.getJdbcUrl())
            .as("JDBC URL should be properly formatted")
            .isNotBlank()
            .startsWith("jdbc:postgresql://")
            .contains("testdb");

        // And - Container should have a valid ID indicating it's running
        assertThat(container.getContainerId())
            .as("Container should have valid ID")
            .isNotBlank();

        // Context loading success implies database connectivity is working
        // The @ServiceConnection annotation automatically configures the database connection
        System.out.println("✅ Spring Boot context loaded successfully with TestContainer database");
        System.out.println("📊 Database URL: " + container.getJdbcUrl());
        System.out.println("🐘 PostgreSQL Container ID: " + container.getContainerId());
    }

    @Test
    @DisplayName("Should have database connectivity working correctly")
    void should_haveDatabaseConnectivityWorking_when_applicationStarts() {
        // Given - Application context is loaded with TestContainer

        // When - We verify basic database connectivity indicators
        var container = getPostgresContainer();

        // Then - Database should be accessible
        assertThat(container.isCreated())
            .as("Container should be created")
            .isTrue();

        assertThat(container.isRunning())
            .as("Container should be running and accessible")
            .isTrue();

        // And - Standard PostgreSQL port should be mapped
        assertThat(container.getMappedPort(5432))
            .as("PostgreSQL port should be mapped")
            .isPositive();

        // And - Container logs should not contain critical errors
        String logs = container.getLogs();
        assertThat(logs)
            .as("Container logs should indicate successful startup")
            .contains("database system is ready to accept connections");

        System.out.println("✅ Database connectivity verified");
        System.out.println("🔌 Mapped PostgreSQL port: " + container.getMappedPort(5432));
    }
}
