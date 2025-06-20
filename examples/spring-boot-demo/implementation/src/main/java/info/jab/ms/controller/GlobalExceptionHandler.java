package info.jab.ms.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.time.Instant;
import java.util.UUID;

/**
 * GlobalExceptionHandler - Centralized exception handling for Film Query API
 * 
 * Task 4.8: Implement proper HTTP status code handling
 * Task 10.1: Create GlobalExceptionHandler class with @ControllerAdvice
 * Task 10.2: Implement handleIllegalArgumentException method  
 * Task 10.3: Implement RFC 7807 ProblemDetail response format
 * Task 10.4: Add proper HTTP status code mapping (400, 500)
 * Task 10.5: Implement descriptive error messages for invalid parameters
 * 
 * This class provides centralized exception handling following RFC 7807 Problem Details
 * for HTTP APIs. It ensures consistent error responses across all endpoints.
 * 
 * Error Response Format (RFC 7807):
 * {
 *   "type": "https://example.com/problems/invalid-parameter",
 *   "title": "Invalid Parameter",
 *   "status": 400,
 *   "detail": "Parameter 'startsWith' must be a single letter (A-Z)",
 *   "instance": "/api/v1/films",
 *   "timestamp": "2024-01-15T10:30:00Z"
 * }
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
        
    /**
     * Handle RuntimeException for unexpected errors.
     * Returns HTTP 500 Internal Server Error with RFC 7807 Problem Details format.
     * 
     * Does not expose sensitive internal error details to clients.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ProblemDetail> handleRuntimeException(
            RuntimeException ex, HttpServletRequest request) {
        
        String errorId = UUID.randomUUID().toString();
        logger.error("Unexpected runtime exception with ID: {}", errorId, ex);
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR, 
            "An unexpected error occurred while processing the request"
        );
        
        problemDetail.setType(URI.create("https://example.com/problems/internal-error"));
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("errorId", errorId);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }
    
    /**
     * Handle generic Exception for any unexpected errors not caught by specific handlers.
     * Returns HTTP 500 Internal Server Error with RFC 7807 Problem Details format.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGenericException(
            Exception ex, HttpServletRequest request) {
        
        String errorId = UUID.randomUUID().toString();
        logger.error("Unexpected exception with ID: {}", errorId, ex);
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR, 
            "An unexpected error occurred while processing the request"
        );
        
        problemDetail.setType(URI.create("https://example.com/problems/internal-error"));
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("errorId", errorId);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }
} 