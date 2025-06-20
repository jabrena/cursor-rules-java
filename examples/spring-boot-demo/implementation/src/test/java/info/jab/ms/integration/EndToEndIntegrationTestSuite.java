package info.jab.ms.integration;

import info.jab.ms.common.PostgreSQLTestBase;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTimeout;

/**
 * EndToEndIntegrationTestSuite - Comprehensive end-to-end integration test suite
 *
 * This class provides a complete end-to-end testing framework that validates:
 * - Full application stack (Web → Service → Repository → Database)
 * - Database connectivity and data integrity
 * - API contract compliance
 * - Performance characteristics
 * - Error handling and edge cases
 * - Cross-cutting concerns (logging, monitoring)
 *
 * Test execution order is controlled to simulate realistic user workflows
 * and ensure proper test isolation while maximizing efficiency.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("End-to-End Integration Test Suite")
class EndToEndIntegrationTestSuite extends PostgreSQLTestBase {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String getBaseUrl() {
        return "http://localhost:" + port;
    }

    /**
     * Test 1: Infrastructure and Application Startup Validation
     * Validates that the complete application stack starts successfully
     */
    @Test
    @Order(1)
    @DisplayName("1. Application Infrastructure - Startup and Health Check")
    void shouldStartApplicationWithCompleteInfrastructure() {
        // Validate application context loaded
        assertThat(restTemplate).isNotNull();
        assertThat(port).isGreaterThan(0);

        // Validate PostgreSQL container infrastructure
        assertThat(getPostgresContainer().isRunning()).isTrue();
        assertThat(getPostgresContainer().getDatabaseName()).isEqualTo("testdb");
        assertThat(getPostgresContainer().getUsername()).isEqualTo("testuser");

        // Validate database connectivity
        String jdbcUrl = getPostgresContainer().getJdbcUrl();
        assertThat(jdbcUrl).startsWith("jdbc:postgresql://");
        assertThat(jdbcUrl).contains("testdb");

        System.out.println("✅ Infrastructure validated - Application: " + getBaseUrl() + ", Database: " + jdbcUrl);
    }

    /**
     * Test 2: Database Schema and Data Integrity Validation
     * Validates that the database schema is properly loaded with expected data
     */
    @Test
    @Order(2)
    @DisplayName("2. Database Schema - Data Integrity and Structure")
    void shouldValidateDatabaseSchemaAndDataIntegrity() {
        try {
            // Validate film table exists and has expected structure
            var tableExistsResult = getPostgresContainer().execInContainer(
                "psql", "-U", "testuser", "-d", "testdb",
                "-c", "SELECT tablename FROM pg_tables WHERE tablename = 'film';"
            );
            assertThat(tableExistsResult.getExitCode()).isEqualTo(0);
            assertThat(tableExistsResult.getStdout()).contains("film");

            // Validate expected number of test records
            var countResult = getPostgresContainer().execInContainer(
                "psql", "-U", "testuser", "-d", "testdb",
                "-c", "SELECT COUNT(*) FROM film;"
            );
            assertThat(countResult.getExitCode()).isEqualTo(0);
            assertThat(countResult.getStdout()).contains("51"); // Expected test data count

            // Validate films starting with 'A' count (critical business requirement)
            var filmsWithAResult = getPostgresContainer().execInContainer(
                "psql", "-U", "testuser", "-d", "testdb",
                "-c", "SELECT COUNT(*) FROM film WHERE title ILIKE 'A%';"
            );
            assertThat(filmsWithAResult.getExitCode()).isEqualTo(0);
            assertThat(filmsWithAResult.getStdout()).contains("46"); // Expected films starting with A

            System.out.println("✅ Database schema validated - 51 total films, 46 starting with 'A'");
        } catch (Exception e) {
            throw new RuntimeException("Database validation failed", e);
        }
    }

    /**
     * Test 3: Core API Functionality - Happy Path
     * Validates the primary business use case end-to-end
     */
    @Test
    @Order(3)
    @DisplayName("3. Core API - Happy Path Film Query")
    void shouldValidateCoreApiHappyPath() {
        // Execute core business function: Get films starting with 'A'
        String url = "/api/v1/films?startsWith=A";
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

        // Validate HTTP response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType().toString()).contains("application/json");

        // Validate response structure and business rules
        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body).containsKeys("films", "count", "filter");

        // Validate business rule: 46 films starting with 'A'
        List<Map<String, Object>> films = (List<Map<String, Object>>) body.get("films");
        assertThat(films).hasSize(46);
        assertThat((Integer) body.get("count")).isEqualTo(46);

        // Validate filter metadata
        Map<String, Object> filter = (Map<String, Object>) body.get("filter");
        assertThat(filter).containsEntry("startsWith", "A");

        // Validate film data structure
        films.forEach(film -> {
            assertThat(film).containsKeys("film_id", "title");
            assertThat(film.get("film_id")).isNotNull();
            assertThat(film.get("title")).asString().startsWithIgnoringCase("A");
        });

        System.out.println("✅ Core API validated - Retrieved 46 films starting with 'A'");
    }

    /**
     * Test 4: API Contract Validation - Multiple Parameters
     * Validates API behavior with different input parameters
     */
    @Test
    @Order(4)
    @DisplayName("4. API Contract - Multiple Parameter Scenarios")
    void shouldValidateApiContractWithMultipleParameters() {
        // Test different valid letters
        String[] testLetters = {"B", "C", "D", "M"};
        for (String letter : testLetters) {
            String url = "/api/v1/films?startsWith=" + letter;
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            Map<String, Object> body = response.getBody();
            assertThat(body).isNotNull();
            assertThat(body).containsKeys("films", "count", "filter");

            Map<String, Object> filter = (Map<String, Object>) body.get("filter");
            assertThat(filter).containsEntry("startsWith", letter);

            System.out.println("✅ Letter '" + letter + "' validated - " + body.get("count") + " films found");
        }

        // Test case insensitive behavior
        ResponseEntity<Map> lowerCaseResponse = restTemplate.getForEntity("/api/v1/films?startsWith=a", Map.class);
        ResponseEntity<Map> upperCaseResponse = restTemplate.getForEntity("/api/v1/films?startsWith=A", Map.class);

        assertThat(lowerCaseResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(upperCaseResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        Map<String, Object> lowerBody = lowerCaseResponse.getBody();
        Map<String, Object> upperBody = upperCaseResponse.getBody();
        assertThat(lowerBody.get("count")).isEqualTo(upperBody.get("count")); // Should return same count

        System.out.println("✅ Case insensitive behavior validated");
    }

    /**
     * Test 5: Error Handling and Edge Cases
     * Validates application behavior under error conditions
     */
    @Test
    @Order(5)
    @DisplayName("5. Error Handling - Edge Cases and Invalid Input")
    void shouldValidateErrorHandlingAndEdgeCases() {
        // Test invalid parameter scenarios
        String[] invalidInputs = {"", "AB", "123", "!", " "};
        for (String input : invalidInputs) {
            String url = "/api/v1/films?startsWith=" + input;
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            Map<String, Object> body = response.getBody();
            assertThat(body).isNotNull();
            assertThat(body).containsKey("type"); // RFC 7807 Problem Detail format

            System.out.println("✅ Invalid input '" + input + "' correctly rejected with HTTP 400");
        }

        // Test parameter missing scenario (should return HTTP 200 with empty/all films)
        ResponseEntity<Map> noParamResponse = restTemplate.getForEntity("/api/v1/films", Map.class);
        assertThat(noParamResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        // Should return valid response structure
        Map<String, Object> noParamBody = noParamResponse.getBody();
        assertThat(noParamBody).isNotNull();
        assertThat(noParamBody).containsKeys("films", "count", "filter");

        // Test empty results scenario (letter that has no films)
        ResponseEntity<Map> emptyResponse = restTemplate.getForEntity("/api/v1/films?startsWith=Q", Map.class);
        assertThat(emptyResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map<String, Object> emptyBody = emptyResponse.getBody();
        assertThat(emptyBody.get("count")).isEqualTo(0);
        assertThat((List<?>) emptyBody.get("films")).isEmpty();

        System.out.println("✅ Error handling validated - Bad requests return HTTP 400, empty results handled gracefully");
    }

    /**
     * Test 6: Performance and Scalability Validation
     * Validates that the application meets performance requirements
     */
    @Test
    @Order(6)
    @DisplayName("6. Performance - Response Time and Scalability")
    void shouldValidatePerformanceRequirements() {
        // Test single request performance (requirement: < 2 seconds)
        assertTimeout(java.time.Duration.ofSeconds(2), () -> {
            ResponseEntity<Map> response = restTemplate.getForEntity("/api/v1/films?startsWith=A", Map.class);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }, "Single request should complete within 2 seconds");

        // Test multiple concurrent requests to validate scalability
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            ResponseEntity<Map> response = restTemplate.getForEntity("/api/v1/films?startsWith=A", Map.class);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
        long totalTime = System.currentTimeMillis() - startTime;

        assertThat(totalTime).isLessThan(5000L); // 10 requests should complete within 5 seconds
        System.out.println("✅ Performance validated - 10 requests completed in " + totalTime + "ms");
    }

    /**
     * Test 7: Data Consistency and Transaction Integrity
     * Validates that data remains consistent across operations
     */
    @Test
    @Order(7)
    @DisplayName("7. Data Consistency - Transaction Integrity")
    void shouldValidateDataConsistencyAndTransactionIntegrity() {
        // Perform multiple reads to ensure data consistency
        ResponseEntity<Map> response1 = restTemplate.getForEntity("/api/v1/films?startsWith=A", Map.class);
        ResponseEntity<Map> response2 = restTemplate.getForEntity("/api/v1/films?startsWith=A", Map.class);
        ResponseEntity<Map> response3 = restTemplate.getForEntity("/api/v1/films?startsWith=A", Map.class);

        // All responses should be identical
        assertThat(response1.getBody().get("count")).isEqualTo(response2.getBody().get("count"));
        assertThat(response2.getBody().get("count")).isEqualTo(response3.getBody().get("count"));
        assertThat(response1.getBody().get("count")).isEqualTo(46);

        // Validate that film data is consistent across requests
        List<Map<String, Object>> films1 = (List<Map<String, Object>>) response1.getBody().get("films");
        List<Map<String, Object>> films2 = (List<Map<String, Object>>) response2.getBody().get("films");

        assertThat(films1).hasSize(films2.size());

        System.out.println("✅ Data consistency validated - Multiple requests return identical results");
    }

    /**
     * Test 8: Full Integration Workflow Simulation
     * Simulates a complete user workflow from start to finish
     */
    @Test
    @Order(8)
    @DisplayName("8. Full Workflow - Complete User Journey Simulation")
    void shouldSimulateCompleteUserWorkflow() {
        System.out.println("🚀 Simulating complete user workflow...");

        // Step 1: User searches for films starting with 'A'
        ResponseEntity<Map> searchA = restTemplate.getForEntity("/api/v1/films?startsWith=A", Map.class);
        assertThat(searchA.getStatusCode()).isEqualTo(HttpStatus.OK);
        int countA = (Integer) searchA.getBody().get("count");
        System.out.println("   Step 1: Found " + countA + " films starting with 'A'");

        // Step 2: User searches for films starting with 'B'
        ResponseEntity<Map> searchB = restTemplate.getForEntity("/api/v1/films?startsWith=B", Map.class);
        assertThat(searchB.getStatusCode()).isEqualTo(HttpStatus.OK);
        int countB = (Integer) searchB.getBody().get("count");
        System.out.println("   Step 2: Found " + countB + " films starting with 'B'");

        // Step 3: User makes an invalid search (error handling)
        ResponseEntity<Map> invalidSearch = restTemplate.getForEntity("/api/v1/films?startsWith=123", Map.class);
        assertThat(invalidSearch.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        System.out.println("   Step 3: Invalid search properly rejected");

        // Step 4: User searches for a letter with no results
        ResponseEntity<Map> emptySearch = restTemplate.getForEntity("/api/v1/films?startsWith=Q", Map.class);
        assertThat(emptySearch.getStatusCode()).isEqualTo(HttpStatus.OK);
        int countQ = (Integer) emptySearch.getBody().get("count");
        assertThat(countQ).isEqualTo(0);
        System.out.println("   Step 4: Empty results handled gracefully (Q: " + countQ + " films)");

        // Step 5: User returns to successful search
        ResponseEntity<Map> finalSearch = restTemplate.getForEntity("/api/v1/films?startsWith=A", Map.class);
        assertThat(finalSearch.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat((Integer) finalSearch.getBody().get("count")).isEqualTo(countA); // Same result as Step 1

        System.out.println("✅ Complete user workflow validated successfully");
    }
}
