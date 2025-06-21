package info.jab.ms.controller;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit tests for GlobalExceptionHandler class
 *
 * Task 9.1: Create unit tests for GlobalExceptionHandler class
 *
 * These tests verify that the GlobalExceptionHandler properly handles exceptions
 * and returns RFC 7807 compliant error responses with correct status codes,
 * error messages, and structured format.
 *
 * Following TDD methodology - these tests should FAIL initially (Red phase)
 * as they test the specific behavior expected from the exception handler.
 */
@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private HttpServletRequest mockRequest;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();

        // Mock request URI for all tests
        when(mockRequest.getRequestURI()).thenReturn("/api/v1/films");
    }

    // ========================================================================
    // Task 9.1: Test RuntimeException handling
    // ========================================================================

    @Test
    void shouldHandleRuntimeExceptionWithHttp500() {
        // Given: A RuntimeException is thrown
        RuntimeException exception = new RuntimeException("Database connection failed");

        // When: The exception handler processes the exception
        ResponseEntity<ProblemDetail> response = globalExceptionHandler.handleRuntimeException(exception, mockRequest);

        // Then: Should return HTTP 500 Internal Server Error
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

        // And: Response body should contain ProblemDetail
        ProblemDetail problemDetail = response.getBody();
        assertThat(problemDetail).isNotNull();

        // And: Should follow RFC 7807 Problem Details format
        assertThat(problemDetail.getStatus()).isEqualTo(500);
        assertThat(problemDetail.getTitle()).isEqualTo("Internal Server Error");
        assertThat(problemDetail.getDetail()).isEqualTo("An unexpected error occurred while processing the request");

        // And: Should include request instance
        assertThat(problemDetail.getInstance().toString()).isEqualTo("/api/v1/films");

        // And: Should include timestamp
        assertThat(problemDetail.getProperties()).containsKey("timestamp");
        Object timestamp = problemDetail.getProperties().get("timestamp");
        assertThat(timestamp).isInstanceOf(Instant.class);

        // And: Should include error ID for tracking
        assertThat(problemDetail.getProperties()).containsKey("errorId");
        Object errorId = problemDetail.getProperties().get("errorId");
        assertThat(errorId).isInstanceOf(String.class);
        assertThat(errorId.toString()).isNotEmpty();
    }

    @Test
    void shouldNotExposeSensitiveRuntimeExceptionDetails() {
        // Given: A RuntimeException with sensitive information
        RuntimeException exception = new RuntimeException("SQL password: secret123, connection failed");

        // When: The exception handler processes the exception
        ResponseEntity<ProblemDetail> response = globalExceptionHandler.handleRuntimeException(exception, mockRequest);

        // Then: Should not expose sensitive details in the response
        ProblemDetail problemDetail = response.getBody();
        assertThat(problemDetail).isNotNull();
        assertThat(problemDetail.getDetail()).doesNotContain("password");
        assertThat(problemDetail.getDetail()).doesNotContain("secret123");
        assertThat(problemDetail.getDetail()).isEqualTo("An unexpected error occurred while processing the request");
    }

    // ========================================================================
    // Task 9.1: Test generic Exception handling
    // ========================================================================

    @Test
    void shouldHandleGenericExceptionWithHttp500() {
        // Given: A generic Exception is thrown
        Exception exception = new Exception("Unexpected system error");

        // When: The exception handler processes the exception
        ResponseEntity<ProblemDetail> response = globalExceptionHandler.handleGenericException(exception, mockRequest);

        // Then: Should return HTTP 500 Internal Server Error
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

        // And: Response body should contain ProblemDetail
        ProblemDetail problemDetail = response.getBody();
        assertThat(problemDetail).isNotNull();

        // And: Should follow RFC 7807 Problem Details format
        assertThat(problemDetail.getStatus()).isEqualTo(500);
        assertThat(problemDetail.getTitle()).isEqualTo("Internal Server Error");
        assertThat(problemDetail.getDetail()).isEqualTo("An unexpected error occurred while processing the request");

        // And: Should include request instance
        assertThat(problemDetail.getInstance().toString()).isEqualTo("/api/v1/films");

        // And: Should include timestamp
        assertThat(problemDetail.getProperties()).containsKey("timestamp");
        Object timestamp = problemDetail.getProperties().get("timestamp");
        assertThat(timestamp).isInstanceOf(Instant.class);

        // And: Should include error ID for tracking
        assertThat(problemDetail.getProperties()).containsKey("errorId");
        Object errorId = problemDetail.getProperties().get("errorId");
        assertThat(errorId).isInstanceOf(String.class);
        assertThat(errorId.toString()).isNotEmpty();
    }

    @Test
    void shouldGenerateUniqueErrorIdsForDifferentExceptions() {
        // Given: Multiple RuntimeExceptions
        RuntimeException exception1 = new RuntimeException("First error");
        RuntimeException exception2 = new RuntimeException("Second error");

        // When: The exception handler processes both exceptions
        ResponseEntity<ProblemDetail> response1 = globalExceptionHandler.handleRuntimeException(exception1, mockRequest);
        ResponseEntity<ProblemDetail> response2 = globalExceptionHandler.handleRuntimeException(exception2, mockRequest);

        // Then: Each response should have a unique error ID
        String errorId1 = response1.getBody().getProperties().get("errorId").toString();
        String errorId2 = response2.getBody().getProperties().get("errorId").toString();

        assertThat(errorId1).isNotEqualTo(errorId2);
        assertThat(errorId1).isNotEmpty();
        assertThat(errorId2).isNotEmpty();
    }

    // ========================================================================
    // Task 9.1: Test proper request URI handling
    // ========================================================================

    @Test
    void shouldHandleDifferentRequestUris() {
        // Given: Different request URIs
        when(mockRequest.getRequestURI()).thenReturn("/api/v1/different-endpoint");
        RuntimeException exception = new RuntimeException("Test error");

        // When: The exception handler processes the exception
        ResponseEntity<ProblemDetail> response = globalExceptionHandler.handleRuntimeException(exception, mockRequest);

        // Then: Should include the correct request URI in the instance field
        ProblemDetail problemDetail = response.getBody();
        assertThat(problemDetail).isNotNull();
        assertThat(problemDetail.getInstance().toString()).isEqualTo("/api/v1/different-endpoint");
    }

    // ========================================================================
    // Task 9.1: Test error response consistency
    // ========================================================================

    @Test
    void shouldReturnConsistentErrorResponseStructure() {
        // Given: A RuntimeException
        RuntimeException exception = new RuntimeException("Test error");

        // When: The exception handler processes the exception
        ResponseEntity<ProblemDetail> response = globalExceptionHandler.handleRuntimeException(exception, mockRequest);

        // Then: Should return consistent response structure
        ProblemDetail problemDetail = response.getBody();
        assertThat(problemDetail).isNotNull();

        // Verify all required RFC 7807 fields are present
        assertThat(problemDetail.getType()).isNotNull();
        assertThat(problemDetail.getTitle()).isNotNull();
        assertThat(problemDetail.getStatus()).isNotNull();
        assertThat(problemDetail.getDetail()).isNotNull();
        assertThat(problemDetail.getInstance()).isNotNull();

        // Verify custom properties are present
        assertThat(problemDetail.getProperties()).isNotNull();
        assertThat(problemDetail.getProperties()).containsKey("timestamp");
        assertThat(problemDetail.getProperties()).containsKey("errorId");
    }

    // ========================================================================
    // Task 9.2: Test HTTP status code mapping
    // ========================================================================

    @Test
    void shouldMapRuntimeExceptionToHttp500StatusCode() {
        // Given: A RuntimeException
        RuntimeException exception = new RuntimeException("Service unavailable");

        // When: The exception handler processes the exception
        ResponseEntity<ProblemDetail> response = globalExceptionHandler.handleRuntimeException(exception, mockRequest);

        // Then: Should map to HTTP 500 Internal Server Error
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getStatusCode().value()).isEqualTo(500);

        // And: ProblemDetail status should match ResponseEntity status
        ProblemDetail problemDetail = response.getBody();
        assertThat(problemDetail).isNotNull();
        assertThat(problemDetail.getStatus()).isEqualTo(500);
    }

    @Test
    void shouldMapGenericExceptionToHttp500StatusCode() {
        // Given: A generic Exception
        Exception exception = new Exception("Unexpected failure");

        // When: The exception handler processes the exception
        ResponseEntity<ProblemDetail> response = globalExceptionHandler.handleGenericException(exception, mockRequest);

        // Then: Should map to HTTP 500 Internal Server Error
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getStatusCode().value()).isEqualTo(500);

        // And: ProblemDetail status should match ResponseEntity status
        ProblemDetail problemDetail = response.getBody();
        assertThat(problemDetail).isNotNull();
        assertThat(problemDetail.getStatus()).isEqualTo(500);
    }

    @Test
    void shouldReturnConsistentStatusCodeMappingAcrossExceptionTypes() {
        // Given: Different exception types that should map to the same status code
        RuntimeException runtimeException = new RuntimeException("Runtime error");
        Exception genericException = new Exception("Generic error");

        // When: Both exceptions are processed
        ResponseEntity<ProblemDetail> runtimeResponse = globalExceptionHandler.handleRuntimeException(runtimeException, mockRequest);
        ResponseEntity<ProblemDetail> genericResponse = globalExceptionHandler.handleGenericException(genericException, mockRequest);

        // Then: Both should map to the same HTTP status code
        assertThat(runtimeResponse.getStatusCode()).isEqualTo(genericResponse.getStatusCode());
        assertThat(runtimeResponse.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

        // And: ProblemDetail status should be consistent
        assertThat(runtimeResponse.getBody().getStatus()).isEqualTo(genericResponse.getBody().getStatus());
        assertThat(runtimeResponse.getBody().getStatus()).isEqualTo(500);
    }

    @Test
    void shouldValidateStatusCodeIsNotNull() {
        // Given: A RuntimeException
        RuntimeException exception = new RuntimeException("Test error");

        // When: The exception handler processes the exception
        ResponseEntity<ProblemDetail> response = globalExceptionHandler.handleRuntimeException(exception, mockRequest);

        // Then: Status code should never be null
        assertThat(response.getStatusCode()).isNotNull();
        assertThat(response.getBody().getStatus()).isNotNull();

        // And: Status code should be a valid HTTP status code
        int statusCode = response.getStatusCode().value();
        assertThat(statusCode).isBetween(100, 599);
    }

    @Test
    void shouldEnsureStatusCodeConsistencyBetweenResponseEntityAndProblemDetail() {
        // Given: Different types of exceptions
        RuntimeException runtimeException = new RuntimeException("Runtime error");
        Exception genericException = new Exception("Generic error");

        // When: Both exceptions are processed
        ResponseEntity<ProblemDetail> runtimeResponse = globalExceptionHandler.handleRuntimeException(runtimeException, mockRequest);
        ResponseEntity<ProblemDetail> genericResponse = globalExceptionHandler.handleGenericException(genericException, mockRequest);

        // Then: ResponseEntity status code should match ProblemDetail status
        assertThat(runtimeResponse.getStatusCode().value()).isEqualTo(runtimeResponse.getBody().getStatus());
        assertThat(genericResponse.getStatusCode().value()).isEqualTo(genericResponse.getBody().getStatus());

        // And: Both should be HTTP 500
        assertThat(runtimeResponse.getStatusCode().value()).isEqualTo(500);
        assertThat(genericResponse.getStatusCode().value()).isEqualTo(500);
    }

    @Test
    void shouldMapToAppropriateServerErrorStatusRange() {
        // Given: Server-side exceptions
        RuntimeException runtimeException = new RuntimeException("Server error");
        Exception genericException = new Exception("Server failure");

        // When: Both exceptions are processed
        ResponseEntity<ProblemDetail> runtimeResponse = globalExceptionHandler.handleRuntimeException(runtimeException, mockRequest);
        ResponseEntity<ProblemDetail> genericResponse = globalExceptionHandler.handleGenericException(genericException, mockRequest);

        // Then: Should map to 5xx server error status range
        int runtimeStatusCode = runtimeResponse.getStatusCode().value();
        int genericStatusCode = genericResponse.getStatusCode().value();

        assertThat(runtimeStatusCode).isBetween(500, 599);
        assertThat(genericStatusCode).isBetween(500, 599);

        // And: Should not map to client error 4xx range
        assertThat(runtimeStatusCode).isGreaterThanOrEqualTo(500);
        assertThat(genericStatusCode).isGreaterThanOrEqualTo(500);
    }

    // ========================================================================
    // Task 9.3: Test error response JSON structure
    // ========================================================================

    @Test
    void shouldReturnRfc7807CompliantJsonStructure() {
        // Given: A RuntimeException
        RuntimeException exception = new RuntimeException("Test error");

        // When: The exception handler processes the exception
        ResponseEntity<ProblemDetail> response = globalExceptionHandler.handleRuntimeException(exception, mockRequest);

        // Then: Should return RFC 7807 compliant JSON structure
        ProblemDetail problemDetail = response.getBody();
        assertThat(problemDetail).isNotNull();

        // RFC 7807 mandatory fields
        assertThat(problemDetail.getTitle()).isNotNull();
        assertThat(problemDetail.getStatus()).isNotNull();
        assertThat(problemDetail.getDetail()).isNotNull();
        assertThat(problemDetail.getInstance()).isNotNull();

        // Field value validation
        assertThat(problemDetail.getTitle()).isEqualTo("Internal Server Error");
        assertThat(problemDetail.getStatus()).isEqualTo(500);
        assertThat(problemDetail.getDetail()).isEqualTo("An unexpected error occurred while processing the request");
        assertThat(problemDetail.getInstance().toString()).isEqualTo("/api/v1/films");
    }

    @Test
    void shouldIncludeCustomPropertiesInJsonStructure() {
        // Given: A RuntimeException
        RuntimeException exception = new RuntimeException("Test error");

        // When: The exception handler processes the exception
        ResponseEntity<ProblemDetail> response = globalExceptionHandler.handleRuntimeException(exception, mockRequest);

        // Then: Should include custom properties in JSON structure
        ProblemDetail problemDetail = response.getBody();
        assertThat(problemDetail).isNotNull();
        assertThat(problemDetail.getProperties()).isNotNull();

        // Custom properties validation
        assertThat(problemDetail.getProperties()).containsKey("timestamp");
        assertThat(problemDetail.getProperties()).containsKey("errorId");

        // Timestamp validation
        Object timestamp = problemDetail.getProperties().get("timestamp");
        assertThat(timestamp).isInstanceOf(Instant.class);
        assertThat(timestamp).isNotNull();

        // Error ID validation
        Object errorId = problemDetail.getProperties().get("errorId");
        assertThat(errorId).isInstanceOf(String.class);
        assertThat(errorId.toString()).matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"); // UUID format
        assertThat(errorId.toString()).isNotEmpty();
    }

    @Test
    void shouldReturnValidJsonFieldTypes() {
        // Given: A RuntimeException
        RuntimeException exception = new RuntimeException("Test error");

        // When: The exception handler processes the exception
        ResponseEntity<ProblemDetail> response = globalExceptionHandler.handleRuntimeException(exception, mockRequest);

        // Then: Should return valid JSON field types
        ProblemDetail problemDetail = response.getBody();
        assertThat(problemDetail).isNotNull();

        // Type field should be URI
        //assertThat(problemDetail.getType()).isInstanceOf(URI.class);

        // Title field should be String
        assertThat(problemDetail.getTitle()).isInstanceOf(String.class);

        // Status field should be Integer
        assertThat(problemDetail.getStatus()).isInstanceOf(Integer.class);

        // Detail field should be String
        assertThat(problemDetail.getDetail()).isInstanceOf(String.class);

        // Instance field should be URI
        assertThat(problemDetail.getInstance()).isInstanceOf(URI.class);

        // Properties should be Map
        assertThat(problemDetail.getProperties()).isInstanceOf(java.util.Map.class);
    }

    @Test
    void shouldMaintainJsonStructureConsistencyAcrossExceptionTypes() {
        // Given: Different exception types
        RuntimeException runtimeException = new RuntimeException("Runtime error");
        Exception genericException = new Exception("Generic error");

        // When: Both exceptions are processed
        ResponseEntity<ProblemDetail> runtimeResponse = globalExceptionHandler.handleRuntimeException(runtimeException, mockRequest);
        ResponseEntity<ProblemDetail> genericResponse = globalExceptionHandler.handleGenericException(genericException, mockRequest);

        // Then: Should maintain consistent JSON structure across exception types
        ProblemDetail runtimeProblem = runtimeResponse.getBody();
        ProblemDetail genericProblem = genericResponse.getBody();

        assertThat(runtimeProblem).isNotNull();
        assertThat(genericProblem).isNotNull();

        // Structure consistency validation
        assertThat(runtimeProblem.getTitle()).isEqualTo(genericProblem.getTitle());
        assertThat(runtimeProblem.getStatus()).isEqualTo(genericProblem.getStatus());
        assertThat(runtimeProblem.getDetail()).isEqualTo(genericProblem.getDetail());

        // Properties consistency validation
        assertThat(runtimeProblem.getProperties().keySet()).isEqualTo(genericProblem.getProperties().keySet());
        assertThat(runtimeProblem.getProperties()).containsKeys("timestamp", "errorId");
        assertThat(genericProblem.getProperties()).containsKeys("timestamp", "errorId");
    }

    @Test
    void shouldIncludeAllRequiredRfc7807Fields() {
        // Given: A generic Exception
        Exception exception = new Exception("System error");

        // When: The exception handler processes the exception
        ResponseEntity<ProblemDetail> response = globalExceptionHandler.handleGenericException(exception, mockRequest);

        // Then: Should include all required RFC 7807 fields
        ProblemDetail problemDetail = response.getBody();
        assertThat(problemDetail).isNotNull();

        assertThat(problemDetail.getTitle())
            .as("RFC 7807 'title' field is required")
            .isNotNull()
            .isNotEmpty();

        assertThat(problemDetail.getStatus())
            .as("RFC 7807 'status' field is required")
            .isNotNull()
            .isPositive();

        assertThat(problemDetail.getDetail())
            .as("RFC 7807 'detail' field is required")
            .isNotNull()
            .isNotEmpty();

        assertThat(problemDetail.getInstance())
            .as("RFC 7807 'instance' field is required")
            .isNotNull();
    }

    @Test
    void shouldReturnValidUriFieldsInJsonStructure() {
        // Given: A RuntimeException
        RuntimeException exception = new RuntimeException("URI validation test");

        // When: The exception handler processes the exception
        ResponseEntity<ProblemDetail> response = globalExceptionHandler.handleRuntimeException(exception, mockRequest);

        // Then: Should return valid URI fields in JSON structure
        ProblemDetail problemDetail = response.getBody();
        assertThat(problemDetail).isNotNull();

        // Instance URI validation
        URI instanceUri = problemDetail.getInstance();
        assertThat(instanceUri).isNotNull();
        assertThat(instanceUri.toString()).startsWith("/");
        assertThat(instanceUri.getPath()).isNotNull();
    }

    @Test
    void shouldProvideDescriptiveErrorMessageInJsonStructure() {
        // Given: A RuntimeException with specific message
        RuntimeException exception = new RuntimeException("Database connection timeout");

        // When: The exception handler processes the exception
        ResponseEntity<ProblemDetail> response = globalExceptionHandler.handleRuntimeException(exception, mockRequest);

        // Then: Should provide descriptive error message without exposing sensitive details
        ProblemDetail problemDetail = response.getBody();
        assertThat(problemDetail).isNotNull();

        // Generic error message validation (should not expose internal details)
        assertThat(problemDetail.getDetail())
            .isEqualTo("An unexpected error occurred while processing the request")
            .doesNotContain("Database")
            .doesNotContain("timeout")
            .doesNotContain("connection");

        // Title should be descriptive but generic
        assertThat(problemDetail.getTitle())
            .isEqualTo("Internal Server Error")
            .isNotEmpty();
    }

    @Test
    void shouldGenerateValidTimestampInJsonStructure() {
        // Given: A RuntimeException
        RuntimeException exception = new RuntimeException("Timestamp test");
        Instant beforeRequest = Instant.now();

        // When: The exception handler processes the exception
        ResponseEntity<ProblemDetail> response = globalExceptionHandler.handleRuntimeException(exception, mockRequest);
        Instant afterRequest = Instant.now();

        // Then: Should generate valid timestamp in JSON structure
        ProblemDetail problemDetail = response.getBody();
        assertThat(problemDetail).isNotNull();

        Object timestampObj = problemDetail.getProperties().get("timestamp");
        assertThat(timestampObj).isInstanceOf(Instant.class);

        Instant timestamp = (Instant) timestampObj;
        assertThat(timestamp)
            .isAfterOrEqualTo(beforeRequest)
            .isBeforeOrEqualTo(afterRequest);

        // Timestamp should be in ISO-8601 format when serialized
        assertThat(timestamp.toString()).matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(\\.\\d+)?Z");
    }
}
