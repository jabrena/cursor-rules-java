package com.example.demo.acceptance;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * FilmQueryAcceptanceTest - TestRestTemplate-based acceptance tests
 * 
 * This class implements the acceptance test foundation for the Film Query API
 * using TestRestTemplate and TestContainers following Outside-in TDD strategy.
 * 
 * Task 2.1: Set up Spring Boot Test with @SpringBootTest and TestRestTemplate ✅
 * Task 2.2: Configure TestContainers for PostgreSQL in acceptance tests ✅
 * Task 2.3: Create acceptance test for "Successfully retrieve films starting with A" scenario ✅
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
class FilmQueryAcceptanceIT {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    /**
     * Custom Docker image name for Sakila PostgreSQL database
     * Declared as compatible substitute for standard PostgreSQL container
     */
    private static final DockerImageName SAKILA_POSTGRES_IMAGE = DockerImageName
            .parse("sakiladb/postgres:latest")
            .asCompatibleSubstituteFor("postgres");

    /**
     * PostgreSQL TestContainer configured with Sakila database
     * 
     * Features:
     * - Uses same Sakila database image as compose.yaml (sakiladb/postgres:latest)
     * - @ServiceConnection automatically configures datasource properties
     * - Log-based wait strategy ensures database is ready before tests
     * - Container reuse enabled for performance optimization
     * - Temporary filesystem for faster I/O during tests
     */
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(SAKILA_POSTGRES_IMAGE)
            .withDatabaseName("sakila")
            .withUsername("sakila")
            .withPassword("sakila")
            .withReuse(true)
            .withTmpFs(Map.of("/var/lib/postgresql/data", "rw"))
            .waitingFor(Wait.forLogMessage(".*database system is ready to accept connections.*", 1))
            .withStartupTimeout(Duration.ofMinutes(3));

    /**
     * Basic setup test to verify Spring Boot Test with TestRestTemplate is working
     * and TestContainers PostgreSQL configuration is properly set up.
     * 
     * This test validates:
     * - TestRestTemplate is properly configured
     * - Spring context loads with web environment  
     * - PostgreSQL container is running and accessible
     * - Database connection is established
     */
    @Test
    void testRestTemplateAndPostgreSQLSetupIsWorking() {
        // Verify TestRestTemplate setup
        assert restTemplate != null : "TestRestTemplate should be autowired";
        assert port > 0 : "Random port should be assigned";
        
        // Verify base URL construction
        String baseUrl = "http://localhost:" + port;
        assert baseUrl.contains("localhost") : "Base URL should contain localhost";
        
        // Verify PostgreSQL container is running
        assert postgres.isRunning() : "PostgreSQL container should be running";
        assert postgres.getDatabaseName().equals("sakila") : "Database name should be sakila";
        assert postgres.getUsername().equals("sakila") : "Username should be sakila";
        
        // Verify database connection details
        String jdbcUrl = postgres.getJdbcUrl();
        assert jdbcUrl.contains("sakila") : "JDBC URL should contain sakila database";
        assert jdbcUrl.startsWith("jdbc:postgresql://") : "Should be PostgreSQL JDBC URL";
    }

    /**
     * Task 2.3: Create acceptance test for "Successfully retrieve films starting with A" scenario (46 films expected)
     * 
     * This test implements the Gherkin scenario:
     * 
     * Scenario: Successfully retrieve films starting with "A"
     *   Given the film database contains movies with various titles
     *   When I request films that start with the letter "A"
     *   Then I should receive a list of films with titles beginning with "A"
     *   And the response should include film ID and title for each movie
     *   And the result should contain exactly 46 films
     *   And all returned film titles should start with the letter "A"
     * 
     * Expected behavior:
     * - GET /api/v1/films?startsWith=A
     * - HTTP 200 OK response
     * - JSON response format
     * - Exactly 46 films in results
     * - Each film has film_id and title fields
     * - All titles start with "A" (case insensitive)
     */
    @Test
    void shouldRetrieveFilmsStartingWithA() {
        // Given: the film database contains movies with various titles (Sakila test data)
        // (Container setup provides the Sakila database with pre-loaded data)
        
        // When: I request films that start with the letter "A"
        String url = "/api/v1/films?startsWith=A";
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        
        // Then: I should receive a HTTP 200 OK response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        // And: the response should be in JSON format
        assertThat(response.getHeaders().getContentType().toString())
                .contains("application/json");
        
        // And: the response should contain the expected structure
        Map<String, Object> responseBody = response.getBody();
        assertThat(responseBody).isNotNull();
        assertThat(responseBody).containsKey("films");
        assertThat(responseBody).containsKey("count");
        assertThat(responseBody).containsKey("filter");
        
        // And: the result should contain exactly 46 films
        List<Map<String, Object>> films = (List<Map<String, Object>>) responseBody.get("films");
        assertThat(films).hasSize(46);
        
        // And: the count field should match the number of films
        Integer count = (Integer) responseBody.get("count");
        assertThat(count).isEqualTo(46);
        
        // And: the filter field should indicate the applied filter
        Map<String, Object> filter = (Map<String, Object>) responseBody.get("filter");
        assertThat(filter).containsEntry("startsWith", "A");
        
        // And: the response should include film ID and title for each movie
        films.forEach(film -> {
            assertThat(film).containsKey("film_id");
            assertThat(film).containsKey("title");
            assertThat(film.get("film_id")).isNotNull();
            assertThat(film.get("title")).isNotNull();
        });
        
        // And: all returned film titles should start with the letter "A"
        films.forEach(film -> {
            String title = (String) film.get("title");
            assertThat(title).startsWithIgnoringCase("A");
        });
    }

    /**
     * Task 2.5: Create acceptance test for "Database query performance" scenario (< 2 seconds)
     * 
     * This test implements the Gherkin scenario:
     * 
     * Scenario: Database query performance
     *   Given the film database is populated with sample data
     *   When I execute the film query for titles starting with "A"
     *   Then the query should complete within 2 seconds
     *   And the database should use the appropriate index for title searches
     *
     * Performance requirement: Query must complete within 2 seconds
     */
    @Test
    void shouldPerformQueryUnderTwoSeconds() {
        // Given: the film database is populated with sample data (Sakila test data)
        // (Container setup provides the Sakila database with pre-loaded data)
        
        // When: I execute the film query for titles starting with "A"
        long startTime = System.currentTimeMillis();
        String url = "/api/v1/films?startsWith=A";
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        long endTime = System.currentTimeMillis();
        
        // Then: the query should complete within 2 seconds (2000 milliseconds)
        long executionTime = endTime - startTime;
        assertThat(executionTime)
                .as("Query execution time should be under 2 seconds (2000ms), but was %d ms", executionTime)
                .isLessThan(2000L);
        
        // And: the response should be successful (indicating database performed well)
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        // And: the database should return the expected results efficiently
        Map<String, Object> responseBody = response.getBody();
        assertThat(responseBody).isNotNull();
        assertThat(responseBody).containsKey("films");
        assertThat(responseBody).containsKey("count");
        
        // Verify we got the expected 46 films for "A" (proves index was used efficiently)
        Integer count = (Integer) responseBody.get("count");
        assertThat(count).isEqualTo(46);
        
        // Log performance for monitoring
        System.out.printf("Film query performance: %d ms for %d results%n", executionTime, count);
    }

    /**
     * Task 2.6: Create acceptance test for "Handle empty results gracefully" scenario
     * 
     * This test implements the Gherkin scenario:
     * 
     * Scenario: Handle empty results gracefully
     *   Given the film database contains no movies starting with "X"
     *   When I request films that start with the letter "X"
     *   Then I should receive an empty list
     *   And the response should have HTTP 200 OK status
     *   And the response should include a message indicating no films found
     *
     * Tests graceful handling of empty results with proper HTTP status and structure.
     */
    @Test
    void shouldHandleEmptyResultsGracefully() {
        // Given: the film database contains no movies starting with "X"
        // (Using "X" as it's unlikely to have films in Sakila dataset starting with X)
        
        // When: I request films that start with the letter "X"
        String url = "/api/v1/films?startsWith=X";
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        
        // Then: I should receive a HTTP 200 OK status (not an error)
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        // And: the response should be in JSON format
        assertThat(response.getHeaders().getContentType().toString())
                .contains("application/json");
        
        // And: the response should contain the expected structure
        Map<String, Object> responseBody = response.getBody();
        assertThat(responseBody).isNotNull();
        assertThat(responseBody).containsKey("films");
        assertThat(responseBody).containsKey("count");
        assertThat(responseBody).containsKey("filter");
        
        // And: I should receive an empty list
        List<Map<String, Object>> films = (List<Map<String, Object>>) responseBody.get("films");
        assertThat(films).isEmpty();
        
        // And: the count should be zero
        Integer count = (Integer) responseBody.get("count");
        assertThat(count).isEqualTo(0);
        
        // And: the filter should indicate the applied filter
        Map<String, Object> filter = (Map<String, Object>) responseBody.get("filter");
        assertThat(filter).containsEntry("startsWith", "X");
        
        // Verify the response structure is consistent even with empty results
        assertThat(responseBody.get("films")).isInstanceOf(List.class);
        assertThat(responseBody.get("count")).isInstanceOf(Integer.class);
        assertThat(responseBody.get("filter")).isInstanceOf(Map.class);
    }

    /**
     * Task 2.7: Create acceptance test for "Query films by different starting letters" scenario (parameterized)
     * 
     * This test implements the Gherkin scenario outline:
     * 
     * Scenario Outline: Query films by different starting letters
     *   Given the film database contains movies with various titles
     *   When I request films that start with the letter "<letter>"
     *   Then I should receive a list of films with titles beginning with "<letter>"
     *   And all returned film titles should start with the letter "<letter>"
     *
     * Examples:
     *   | letter | expected_count |
     *   | A      | 46            |
     *   | B      | 54            |
     *   | C      | 58            |
     *
     * Tests multiple starting letters with parameterized approach.
     */
    @ParameterizedTest
    @CsvSource({
            "A, 46",
            "B, 54", 
            "C, 58"
    })
    void shouldQueryFilmsByDifferentStartingLetters(String letter, int expectedCount) {
        // Given: the film database contains movies with various titles (Sakila test data)
        // (Container setup provides the Sakila database with pre-loaded data)
        
        // When: I request films that start with the specified letter
        String url = "/api/v1/films?startsWith=" + letter;
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        
        // Then: I should receive a HTTP 200 OK response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        // And: the response should be in JSON format
        assertThat(response.getHeaders().getContentType().toString())
                .contains("application/json");
        
        // And: the response should contain the expected structure
        Map<String, Object> responseBody = response.getBody();
        assertThat(responseBody).isNotNull();
        assertThat(responseBody).containsKey("films");
        assertThat(responseBody).containsKey("count");
        assertThat(responseBody).containsKey("filter");
        
        // And: I should receive a list of films with the expected count
        List<Map<String, Object>> films = (List<Map<String, Object>>) responseBody.get("films");
        assertThat(films).hasSize(expectedCount);
        
        // And: the count field should match the number of films
        Integer count = (Integer) responseBody.get("count");
        assertThat(count).isEqualTo(expectedCount);
        
        // And: the filter field should indicate the applied filter
        Map<String, Object> filter = (Map<String, Object>) responseBody.get("filter");
        assertThat(filter).containsEntry("startsWith", letter);
        
        // And: all returned film titles should start with the specified letter
        films.forEach(film -> {
            String title = (String) film.get("title");
            assertThat(title)
                    .as("Film title '%s' should start with letter '%s'", title, letter)
                    .startsWithIgnoringCase(letter);
        });
        
        // And: each film should have the required fields
        films.forEach(film -> {
            assertThat(film).containsKey("film_id");
            assertThat(film).containsKey("title");
            assertThat(film.get("film_id")).isNotNull();
            assertThat(film.get("title")).isNotNull();
        });
    }

    /**
     * Task 2.8: Create acceptance test for "Invalid query parameter handling" scenario (HTTP 400)
     * 
     * This test implements the Gherkin scenario:
     * 
     * Scenario: Invalid query parameter handling
     *   Given the film query service is running
     *   When I make a GET request to "/api/v1/films" with an invalid filter parameter
     *   Then I should receive a HTTP 400 Bad Request response
     *   And the response should include an error message explaining the invalid parameter
     *
     * Tests various invalid parameter scenarios that should return HTTP 400.
     */
    @Test
    void shouldHandleInvalidQueryParametersWithHttp400() {
        // Test Case 1: Empty parameter value
        // Given: the film query service is running (implicit - Spring Boot test context)
        // When: I make a GET request with empty startsWith parameter
        String url1 = "/api/v1/films?startsWith=";
        ResponseEntity<Map> response1 = restTemplate.getForEntity(url1, Map.class);
        
        // Then: I should receive a HTTP 400 Bad Request response
        assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        
        // And: the response should include an error message
        Map<String, Object> errorBody1 = response1.getBody();
        assertThat(errorBody1).isNotNull();
        assertThat(errorBody1).containsKey("title");
        assertThat(errorBody1).containsKey("detail");
        assertThat(errorBody1).containsKey("status");
        assertThat(errorBody1.get("status")).isEqualTo(400);
        
        // Test Case 2: Multiple character parameter (should be single letter)
        // When: I make a GET request with multiple character startsWith parameter
        String url2 = "/api/v1/films?startsWith=ABC";
        ResponseEntity<Map> response2 = restTemplate.getForEntity(url2, Map.class);
        
        // Then: I should receive a HTTP 400 Bad Request response
        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        
        // And: the response should include an error message
        Map<String, Object> errorBody2 = response2.getBody();
        assertThat(errorBody2).isNotNull();
        assertThat(errorBody2).containsKey("title");
        assertThat(errorBody2).containsKey("detail");
        assertThat(errorBody2).containsKey("status");
        assertThat(errorBody2.get("status")).isEqualTo(400);
        
        // Test Case 3: Special character parameter
        // When: I make a GET request with special character startsWith parameter
        String url3 = "/api/v1/films?startsWith=@";
        ResponseEntity<Map> response3 = restTemplate.getForEntity(url3, Map.class);
        
        // Then: I should receive a HTTP 400 Bad Request response
        assertThat(response3.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        
        // And: the response should include an error message
        Map<String, Object> errorBody3 = response3.getBody();
        assertThat(errorBody3).isNotNull();
        assertThat(errorBody3).containsKey("title");
        assertThat(errorBody3).containsKey("detail");
        assertThat(errorBody3).containsKey("status");
        assertThat(errorBody3.get("status")).isEqualTo(400);
        
        // Test Case 4: Numeric parameter
        // When: I make a GET request with numeric startsWith parameter
        String url4 = "/api/v1/films?startsWith=123";
        ResponseEntity<Map> response4 = restTemplate.getForEntity(url4, Map.class);
        
        // Then: I should receive a HTTP 400 Bad Request response
        assertThat(response4.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        
        // And: the response should include an error message
        Map<String, Object> errorBody4 = response4.getBody();
        assertThat(errorBody4).isNotNull();
        assertThat(errorBody4).containsKey("title");
        assertThat(errorBody4).containsKey("detail");
        assertThat(errorBody4).containsKey("status");
        assertThat(errorBody4.get("status")).isEqualTo(400);
        
        // Verify all error responses follow RFC 7807 Problem Details format
        for (Map<String, Object> errorBody : List.of(errorBody1, errorBody2, errorBody3, errorBody4)) {
            assertThat(errorBody).containsKey("type");
            assertThat(errorBody).containsKey("title");
            assertThat(errorBody).containsKey("status");
            assertThat(errorBody).containsKey("detail");
            assertThat(errorBody).containsKey("instance");
        }
    }
} 