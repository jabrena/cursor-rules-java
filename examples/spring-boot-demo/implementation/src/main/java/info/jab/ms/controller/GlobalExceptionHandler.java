package info.jab.ms.controller;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.time.Instant;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * GlobalExceptionHandler - Centralized exception handling for Film Query API
 *
 * This class provides centralized exception handling following RFC 7807 Problem Details
 * for HTTP APIs. It ensures consistent error responses across all endpoints.
 *
 * Error Response Format (RFC 7807):
 * {
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

        problemDetail.setTitle("Internal Server Error");

        // Handle null request URI gracefully
        String requestUri = request.getRequestURI();
        problemDetail.setInstance(URI.create(requestUri != null ? requestUri : "/unknown"));

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

        problemDetail.setTitle("Internal Server Error");

        // Handle null request URI gracefully
        String requestUri = request.getRequestURI();
        problemDetail.setInstance(URI.create(requestUri != null ? requestUri : "/unknown"));

        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("errorId", errorId);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }
}
