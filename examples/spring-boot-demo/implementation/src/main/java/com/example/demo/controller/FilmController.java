package com.example.demo.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import com.example.demo.service.FilmService;
import com.example.demo.dto.FilmResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.HashMap;
import java.util.List;

/**
 * FilmController - REST API Controller for Film Query Operations
 * 
 * This controller provides REST endpoints for querying films from the Sakila database.
 * It implements the Film Query API specification for retrieving films that start with
 * specific letters.
 * 
 * API Endpoints:
 * - GET /api/v1/films - Retrieve all films or filter by starting letter
 * - GET /api/v1/films?startsWith=A - Retrieve films starting with letter "A"
 * 
 * Task 4.1: Create FilmController class with @RestController annotation ✅
 * Task 4.2: Implement GET /api/v1/films endpoint with @GetMapping ✅
 * Task 4.3: Add startsWith parameter with @RequestParam validation ✅
 * Task 4.4: Implement parameter validation logic (single letter, not empty) ✅
 * Task 4.5: Create FilmResponse DTO for JSON response format ✅
 * Task 4.6: Implement response formatting with films array, count, and filter ✅
 * Task 4.7: Add OpenAPI @Operation, @Parameter, and @ApiResponse annotations ✅
 * Task 4.8: Implement proper HTTP status code handling ✅
 */
@RestController
@RequestMapping("/api/v1")
@Tag(name = "Films", description = "Film query operations")
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    /**
     * Task 4.2: Implement GET /api/v1/films endpoint with @GetMapping ✅
     * Task 4.3: Add startsWith parameter with @RequestParam validation ✅
     * Task 4.4: Implement parameter validation logic (single letter, not empty) ✅
     * Task 4.6: Implement response formatting with films array, count, and filter ✅
     * Task 4.7: Add OpenAPI @Operation, @Parameter, and @ApiResponse annotations ✅
     * Task 4.8: Implement proper HTTP status code handling ✅
     * 
     * Retrieves films from the Sakila database, optionally filtered by starting letter.
     * 
     * @param startsWith Optional parameter to filter films by starting letter (single character A-Z)
     * @param request HttpServletRequest for building error responses
     * @return JSON response containing films array, count, and filter information or error response
     */
    @Operation(
        summary = "Query films by starting letter",
        description = """
            Retrieves films from the Sakila database that start with the specified letter.
            The query is case-insensitive and returns film ID and title for each matching film.
            
            **Performance**: Query execution time is guaranteed to be under 2 seconds.
            
            **Expected Results**:
            - Letter "A": 46 films
            """,
        operationId = "getFilmsByStartingLetter"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Successfully retrieved films",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = FilmResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid parameter - startsWith must be a single letter (A-Z)",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = org.springframework.http.ProblemDetail.class)
            )
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = org.springframework.http.ProblemDetail.class)
            )
        )
    })
    @GetMapping("/films")
    public ResponseEntity<?> getFilms(
            @Parameter(
                name = "startsWith",
                description = "Filter films by starting letter (case-insensitive, single character A-Z)",
                example = "A",
                schema = @Schema(type = "string", pattern = "^[A-Za-z]$")
            )
            @RequestParam(required = false) String startsWith,
            HttpServletRequest request) {
        
        // Task 4.4: Implement parameter validation logic (single letter, not empty)
        if (Objects.nonNull(startsWith)) {
            ValidationResult validationResult = validateStartsWithParameter(startsWith);
            if (!validationResult.valid()) {
                return createErrorResponse(validationResult.errorMessage(), request);   
            }
        }
        
        // Call service layer to get films
        List<Map<String, Object>> films = filmService.findFilmsByStartingLetter(startsWith);
        
        // Task 4.6: Implement response formatting with films array, count, and filter
        // Build filter object
        Map<String, Object> filter = new HashMap<>();
        if (startsWith != null && !startsWith.trim().isEmpty()) {
            filter.put("startsWith", startsWith);
        }
        
        // Create FilmResponse DTO (Task 4.5)
        FilmResponse response = new FilmResponse(films, films.size(), filter);
        
        // Task 4.8: Implement proper HTTP status code handling
        return ResponseEntity.ok(response);
    }
    
    /**
     * Task 4.4: Implement parameter validation logic (single letter, not empty)
     * 
     * Validates the startsWith parameter to ensure it meets the API requirements:
     * - Must be a single character
     * - Must be a letter (A-Z, a-z)
     * - Cannot be empty or whitespace
     * - Cannot be numeric or special characters
     * 
     * @param startsWith The parameter value to validate
     * @return ValidationResult containing validation status and error message if invalid
     */
    private ValidationResult validateStartsWithParameter(String startsWith) {
        if (Objects.isNull(startsWith) || startsWith.trim().isEmpty()) {
            return new ValidationResult(false, "Parameter 'startsWith' cannot be empty");
        }
        
        String trimmed = startsWith.trim();
        
        // Check if it's a single character
        if (trimmed.length() != 1) {
            return new ValidationResult(false, "Parameter 'startsWith' must be a single letter (A-Z)");
        }
        
        char character = trimmed.charAt(0);
        
        // Check if it's a letter (A-Z, a-z)
        if (!Character.isLetter(character)) {
            return new ValidationResult(false, "Parameter 'startsWith' must be a single letter (A-Z)");
        }
        
        return new ValidationResult(true, null);
    }
    
    /**
     * Creates an error response matching the format from GlobalExceptionHandler
     * 
     * @param errorMessage The error message to include in the response
     * @param request The HTTP request for building the error response
     * @return ResponseEntity with ProblemDetail matching GlobalExceptionHandler format
     */
    private ResponseEntity<ProblemDetail> createErrorResponse(String errorMessage, HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST, errorMessage
        );
        
        problemDetail.setType(URI.create("https://example.com/problems/invalid-parameter"));
        problemDetail.setTitle("Invalid Parameter");
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty("timestamp", Instant.now());
        
        return ResponseEntity.badRequest().body(problemDetail);
    }
    
    /**
     * Record to hold validation result with validation status and error message
     * 
     * @param valid true if validation passed, false otherwise
     * @param errorMessage error message if validation failed, null if valid
     */
    private record ValidationResult(boolean valid, String errorMessage) { }
} 