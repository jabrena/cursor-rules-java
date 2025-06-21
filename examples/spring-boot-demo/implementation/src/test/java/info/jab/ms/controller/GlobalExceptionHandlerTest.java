package info.jab.ms.controller;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Comprehensive unit tests for GlobalExceptionHandler class.
 *
 * Following Java Unit Testing Guidelines:
 * - Given-When-Then structure with clear separation
 * - Descriptive test names using should_ExpectedBehavior_when_StateUnderTest pattern
 * - @Nested classes for logical grouping
 * - Parameterized tests for data variations
 * - AssertJ for fluent assertions
 * - Mockito for dependency isolation
 * - Comprehensive boundary testing (CORRECT)
 * - Single responsibility per test
 *
 * These tests verify that the GlobalExceptionHandler properly handles exceptions
 * and returns RFC 7807 compliant error responses with correct status codes,
 * error messages, and structured format.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("GlobalExceptionHandler Unit Tests")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private HttpServletRequest mockRequest;

    @BeforeEach
    void setUp() {
        // Given - Fresh instance for each test
        globalExceptionHandler = new GlobalExceptionHandler();

        // And - Mock request URI for all tests
        when(mockRequest.getRequestURI()).thenReturn("/api/v1/films");
    }

    @Nested
    @DisplayName("RuntimeException Handling Tests")
    class RuntimeExceptionHandlingTests {

        @Test
        @DisplayName("Should handle RuntimeException with HTTP 500 status")
        void should_handleRuntimeExceptionWithHttp500_when_runtimeExceptionThrown() {
            // Given
            RuntimeException exception = new RuntimeException("Database connection failed");

            // When
            ResponseEntity<ProblemDetail> response = globalExceptionHandler.handleRuntimeException(exception, mockRequest);

            // Then
            assertThat(response.getStatusCode())
                .as("Should return HTTP 500 Internal Server Error")
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

            ProblemDetail problemDetail = response.getBody();
            assertThat(problemDetail)
                .as("Response body should contain ProblemDetail")
                .isNotNull();

            assertThat(problemDetail.getStatus())
                .as("Should have correct status code in problem detail")
                .isEqualTo(500);

            assertThat(problemDetail.getTitle())
                .as("Should have correct error title")
                .isEqualTo("Internal Server Error");

            assertThat(problemDetail.getDetail())
                .as("Should have generic error message")
                .isEqualTo("An unexpected error occurred while processing the request");

            assertThat(problemDetail.getInstance().toString())
                .as("Should include request URI")
                .isEqualTo("/api/v1/films");
        }

        @Test
        @DisplayName("Should include timestamp in RuntimeException response")
        void should_includeTimestampInResponse_when_runtimeExceptionHandled() {
            // Given
            RuntimeException exception = new RuntimeException("Test error");

            // When
            ResponseEntity<ProblemDetail> response = globalExceptionHandler.handleRuntimeException(exception, mockRequest);

            // Then
            ProblemDetail problemDetail = response.getBody();
            assertThat(problemDetail.getProperties())
                .as("Should contain timestamp property")
                .containsKey("timestamp");

            Object timestamp = problemDetail.getProperties().get("timestamp");
            assertThat(timestamp)
                .as("Timestamp should be Instant type")
                .isInstanceOf(Instant.class);
        }

        @Test
        @DisplayName("Should include unique error ID in RuntimeException response")
        void should_includeUniqueErrorId_when_runtimeExceptionHandled() {
            // Given
            RuntimeException exception = new RuntimeException("Test error");

            // When
            ResponseEntity<ProblemDetail> response = globalExceptionHandler.handleRuntimeException(exception, mockRequest);

            // Then
            ProblemDetail problemDetail = response.getBody();
            assertThat(problemDetail.getProperties())
                .as("Should contain errorId property")
                .containsKey("errorId");

            Object errorId = problemDetail.getProperties().get("errorId");
            assertThat(errorId)
                .as("Error ID should be String type")
                .isInstanceOf(String.class);

            assertThat(errorId.toString())
                .as("Error ID should not be empty")
                .isNotEmpty();
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "SQL password: secret123, connection failed",
            "API key: sk-1234567890abcdef, invalid",
            "Database credentials: user:password@host:5432"
        })
        @DisplayName("Should not expose sensitive information in RuntimeException response")
        void should_notExposeSensitiveInformation_when_runtimeExceptionContainsSensitiveData(String sensitiveMessage) {
            // Given
            RuntimeException exception = new RuntimeException(sensitiveMessage);

            // When
            ResponseEntity<ProblemDetail> response = globalExceptionHandler.handleRuntimeException(exception, mockRequest);

            // Then
            ProblemDetail problemDetail = response.getBody();
            assertThat(problemDetail.getDetail())
                .as("Should not expose sensitive details in response")
                .doesNotContain("password", "secret", "key", "credentials")
                .isEqualTo("An unexpected error occurred while processing the request");
        }

        @Test
        @DisplayName("Should generate unique error IDs for different RuntimeExceptions")
        void should_generateUniqueErrorIds_when_multipleRuntimeExceptionsHandled() {
            // Given
            RuntimeException exception1 = new RuntimeException("First error");
            RuntimeException exception2 = new RuntimeException("Second error");

            // When
            ResponseEntity<ProblemDetail> response1 = globalExceptionHandler.handleRuntimeException(exception1, mockRequest);
            ResponseEntity<ProblemDetail> response2 = globalExceptionHandler.handleRuntimeException(exception2, mockRequest);

            // Then
            String errorId1 = response1.getBody().getProperties().get("errorId").toString();
            String errorId2 = response2.getBody().getProperties().get("errorId").toString();

            assertThat(errorId1)
                .as("Error IDs should be unique")
                .isNotEqualTo(errorId2);

            assertThat(errorId1)
                .as("First error ID should not be empty")
                .isNotEmpty();

            assertThat(errorId2)
                .as("Second error ID should not be empty")
                .isNotEmpty();
        }
    }

    @Nested
    @DisplayName("Generic Exception Handling Tests")
    class GenericExceptionHandlingTests {

        @Test
        @DisplayName("Should handle generic Exception with HTTP 500 status")
        void should_handleGenericExceptionWithHttp500_when_genericExceptionThrown() {
            // Given
            Exception exception = new Exception("Unexpected system error");

            // When
            ResponseEntity<ProblemDetail> response = globalExceptionHandler.handleGenericException(exception, mockRequest);

            // Then
            assertThat(response.getStatusCode())
                .as("Should return HTTP 500 Internal Server Error")
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

            ProblemDetail problemDetail = response.getBody();
            assertThat(problemDetail)
                .as("Response body should contain ProblemDetail")
                .isNotNull();

            assertThat(problemDetail.getStatus())
                .as("Should have correct status code")
                .isEqualTo(500);

            assertThat(problemDetail.getTitle())
                .as("Should have correct error title")
                .isEqualTo("Internal Server Error");

            assertThat(problemDetail.getDetail())
                .as("Should have generic error message")
                .isEqualTo("An unexpected error occurred while processing the request");

            assertThat(problemDetail.getInstance().toString())
                .as("Should include request URI")
                .isEqualTo("/api/v1/films");
        }

        @Test
        @DisplayName("Should include required properties in generic Exception response")
        void should_includeRequiredProperties_when_genericExceptionHandled() {
            // Given
            Exception exception = new Exception("System error");

            // When
            ResponseEntity<ProblemDetail> response = globalExceptionHandler.handleGenericException(exception, mockRequest);

            // Then
            ProblemDetail problemDetail = response.getBody();
            assertThat(problemDetail.getProperties())
                .as("Should contain both timestamp and errorId")
                .containsKeys("timestamp", "errorId");

            assertThat(problemDetail.getProperties().get("timestamp"))
                .as("Timestamp should be Instant type")
                .isInstanceOf(Instant.class);

            assertThat(problemDetail.getProperties().get("errorId"))
                .as("Error ID should be String type")
                .isInstanceOf(String.class);
        }
    }

    @Nested
    @DisplayName("Request URI Handling Tests")
    class RequestUriHandlingTests {

        @ParameterizedTest
        @ValueSource(strings = {
            "/api/v1/films",
            "/api/v1/users",
            "/api/v2/orders",
            "/health",
            "/actuator/info"
        })
        @DisplayName("Should handle different request URIs correctly")
        void should_handleDifferentRequestUris_when_differentEndpointsThrowExceptions(String requestUri) {
            // Given
            when(mockRequest.getRequestURI()).thenReturn(requestUri);
            RuntimeException exception = new RuntimeException("Test error");

            // When
            ResponseEntity<ProblemDetail> response = globalExceptionHandler.handleRuntimeException(exception, mockRequest);

            // Then
            ProblemDetail problemDetail = response.getBody();
            assertThat(problemDetail.getInstance().toString())
                .as("Should include the correct request URI in instance field")
                .isEqualTo(requestUri);
        }

        @Test
        @DisplayName("Should handle null request URI gracefully")
        void should_handleNullRequestUri_when_requestUriIsNull() {
            // Given
            when(mockRequest.getRequestURI()).thenReturn(null);
            RuntimeException exception = new RuntimeException("Test error");

            // When
            ResponseEntity<ProblemDetail> response = globalExceptionHandler.handleRuntimeException(exception, mockRequest);

            // Then
            ProblemDetail problemDetail = response.getBody();
            assertThat(problemDetail.getInstance())
                .as("Should handle null URI gracefully")
                .isNotNull();

            assertThat(problemDetail.getInstance().toString())
                .as("Should provide default URI when request URI is null")
                .isEqualTo("/unknown");
        }
    }

    @Nested
    @DisplayName("RFC 7807 Compliance Tests")
    class Rfc7807ComplianceTests {

        @ParameterizedTest
        @MethodSource("exceptionTypeProvider")
        @DisplayName("Should return RFC 7807 compliant structure for different exception types")
        void should_returnRfc7807CompliantStructure_when_differentExceptionTypesHandled(
                Exception exception, String expectedTitle) {
            // Given - Exception provided by parameterized test

            // When
            ResponseEntity<ProblemDetail> response = exception instanceof RuntimeException
                ? globalExceptionHandler.handleRuntimeException((RuntimeException) exception, mockRequest)
                : globalExceptionHandler.handleGenericException(exception, mockRequest);

            // Then
            ProblemDetail problemDetail = response.getBody();
            assertThat(problemDetail)
                .as("Should have all required RFC 7807 fields")
                .satisfies(pd -> {
                    assertThat(pd.getType()).isNotNull();
                    assertThat(pd.getTitle()).isEqualTo(expectedTitle);
                    assertThat(pd.getStatus()).isEqualTo(500);
                    assertThat(pd.getDetail()).isNotNull();
                    assertThat(pd.getInstance()).isNotNull();
                });
        }

        private static Stream<Arguments> exceptionTypeProvider() {
            return Stream.of(
                Arguments.of(new RuntimeException("Runtime error"), "Internal Server Error"),
                Arguments.of(new Exception("Generic error"), "Internal Server Error"),
                Arguments.of(new IllegalArgumentException("Invalid argument"), "Internal Server Error"),
                Arguments.of(new NullPointerException("Null pointer"), "Internal Server Error")
            );
        }

        @Test
        @DisplayName("Should include custom properties in RFC 7807 structure")
        void should_includeCustomProperties_when_exceptionHandled() {
            // Given
            RuntimeException exception = new RuntimeException("Test error");

            // When
            ResponseEntity<ProblemDetail> response = globalExceptionHandler.handleRuntimeException(exception, mockRequest);

            // Then
            ProblemDetail problemDetail = response.getBody();
            assertThat(problemDetail.getProperties())
                .as("Should include custom properties beyond standard RFC 7807 fields")
                .isNotEmpty()
                .containsKeys("timestamp", "errorId");
        }

        @Test
        @DisplayName("Should maintain consistent response structure across exception types")
        void should_maintainConsistentResponseStructure_when_differentExceptionTypesHandled() {
            // Given
            RuntimeException runtimeException = new RuntimeException("Runtime error");
            Exception genericException = new Exception("Generic error");

            // When
            ResponseEntity<ProblemDetail> runtimeResponse = globalExceptionHandler.handleRuntimeException(runtimeException, mockRequest);
            ResponseEntity<ProblemDetail> genericResponse = globalExceptionHandler.handleGenericException(genericException, mockRequest);

            // Then
            assertThat(runtimeResponse.getStatusCode())
                .as("Both exception types should return same status code")
                .isEqualTo(genericResponse.getStatusCode());

            assertThat(runtimeResponse.getBody().getTitle())
                .as("Both exception types should have same error title")
                .isEqualTo(genericResponse.getBody().getTitle());

            assertThat(runtimeResponse.getBody().getDetail())
                .as("Both exception types should have same error detail")
                .isEqualTo(genericResponse.getBody().getDetail());

            assertThat(runtimeResponse.getBody().getProperties().keySet())
                .as("Both exception types should have same custom properties")
                .isEqualTo(genericResponse.getBody().getProperties().keySet());
        }
    }

    @Nested
    @DisplayName("Error Response Validation Tests")
    class ErrorResponseValidationTests {

        @Test
        @DisplayName("Should validate all required fields are present")
        void should_validateAllRequiredFieldsPresent_when_exceptionHandled() {
            // Given
            RuntimeException exception = new RuntimeException("Validation test");

            // When
            ResponseEntity<ProblemDetail> response = globalExceptionHandler.handleRuntimeException(exception, mockRequest);

            // Then
            ProblemDetail problemDetail = response.getBody();
            assertThat(problemDetail.getType())
                .as("Type field should be present")
                .isNotNull();

            assertThat(problemDetail.getTitle())
                .as("Title field should be present and not empty")
                .isNotNull()
                .isNotEmpty();

            assertThat(problemDetail.getStatus())
                .as("Status field should be present and valid")
                .isNotNull()
                .isEqualTo(500);

            assertThat(problemDetail.getDetail())
                .as("Detail field should be present and not empty")
                .isNotNull()
                .isNotEmpty();

            assertThat(problemDetail.getInstance())
                .as("Instance field should be present")
                .isNotNull();
        }

        @Test
        @DisplayName("Should maintain status code consistency between ResponseEntity and ProblemDetail")
        void should_maintainStatusCodeConsistency_when_exceptionHandled() {
            // Given
            RuntimeException exception = new RuntimeException("Consistency test");

            // When
            ResponseEntity<ProblemDetail> response = globalExceptionHandler.handleRuntimeException(exception, mockRequest);

            // Then
            assertThat(response.getStatusCode().value())
                .as("ResponseEntity status should match ProblemDetail status")
                .isEqualTo(response.getBody().getStatus());
        }

        @Test
        @DisplayName("Should return appropriate server error status range")
        void should_returnServerErrorStatusRange_when_exceptionHandled() {
            // Given
            RuntimeException exception = new RuntimeException("Server error test");

            // When
            ResponseEntity<ProblemDetail> response = globalExceptionHandler.handleRuntimeException(exception, mockRequest);

            // Then
            assertThat(response.getStatusCode())
                .as("Should return status in 5xx server error range")
                .satisfies(status -> {
                    assertThat(status.is5xxServerError()).isTrue();
                    assertThat(status.value()).isBetween(500, 599);
                });
        }

        @Test
        @DisplayName("Should generate valid timestamp")
        void should_generateValidTimestamp_when_exceptionHandled() {
            // Given
            RuntimeException exception = new RuntimeException("Timestamp test");
            Instant beforeHandling = Instant.now();

            // When
            ResponseEntity<ProblemDetail> response = globalExceptionHandler.handleRuntimeException(exception, mockRequest);
            Instant afterHandling = Instant.now();

            // Then
            Instant timestamp = (Instant) response.getBody().getProperties().get("timestamp");
            assertThat(timestamp)
                .as("Timestamp should be within reasonable time range")
                .isBetween(beforeHandling.minusSeconds(1), afterHandling.plusSeconds(1));
        }

        @Test
        @DisplayName("Should provide descriptive error message")
        void should_provideDescriptiveErrorMessage_when_exceptionHandled() {
            // Given
            RuntimeException exception = new RuntimeException("Descriptive message test");

            // When
            ResponseEntity<ProblemDetail> response = globalExceptionHandler.handleRuntimeException(exception, mockRequest);

            // Then
            ProblemDetail problemDetail = response.getBody();
            assertThat(problemDetail.getDetail())
                .as("Error message should be descriptive and user-friendly")
                .isNotEmpty()
                .doesNotContain("null", "undefined")
                .contains("unexpected error", "processing", "request");
        }
    }

    @Nested
    @DisplayName("Edge Cases and Boundary Conditions Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle exception with null message")
        void should_handleExceptionWithNullMessage_when_exceptionMessageIsNull() {
            // Given
            RuntimeException exception = new RuntimeException((String) null);

            // When
            ResponseEntity<ProblemDetail> response = globalExceptionHandler.handleRuntimeException(exception, mockRequest);

            // Then
            assertThat(response.getStatusCode())
                .as("Should handle null message gracefully")
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

            assertThat(response.getBody().getDetail())
                .as("Should provide generic message when exception message is null")
                .isNotNull()
                .isNotEmpty();
        }

        @Test
        @DisplayName("Should handle exception with empty message")
        void should_handleExceptionWithEmptyMessage_when_exceptionMessageIsEmpty() {
            // Given
            RuntimeException exception = new RuntimeException("");

            // When
            ResponseEntity<ProblemDetail> response = globalExceptionHandler.handleRuntimeException(exception, mockRequest);

            // Then
            assertThat(response.getStatusCode())
                .as("Should handle empty message gracefully")
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

            assertThat(response.getBody().getDetail())
                .as("Should provide generic message when exception message is empty")
                .isNotNull()
                .isNotEmpty();
        }

        @Test
        @DisplayName("Should handle very long exception messages")
        void should_handleVeryLongExceptionMessages_when_exceptionMessageIsVeryLong() {
            // Given
            String longMessage = "a".repeat(10000); // Very long message
            RuntimeException exception = new RuntimeException(longMessage);

            // When
            ResponseEntity<ProblemDetail> response = globalExceptionHandler.handleRuntimeException(exception, mockRequest);

            // Then
            assertThat(response.getStatusCode())
                .as("Should handle very long messages gracefully")
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

            assertThat(response.getBody().getDetail())
                .as("Should provide standard message regardless of original message length")
                .isEqualTo("An unexpected error occurred while processing the request");
        }
    }
}
