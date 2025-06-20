package info.jab.ms.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Objects;

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
        
        // And: Should include proper type URI
        assertThat(problemDetail.getType().toString()).isEqualTo("https://example.com/problems/internal-error");
        
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
        
        // And: Should include proper type URI
        assertThat(problemDetail.getType().toString()).isEqualTo("https://example.com/problems/internal-error");
        
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
} 