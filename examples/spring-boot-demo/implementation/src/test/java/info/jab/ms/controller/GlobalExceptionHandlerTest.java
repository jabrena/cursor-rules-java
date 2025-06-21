package info.jab.ms.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Comprehensive integration tests for GlobalExceptionHandler using @WebMvcTest.
 *
 * Following Java Unit Testing Guidelines:
 * - @WebMvcTest for web layer testing with MockMvc
 * - Given-When-Then structure with clear separation
 * - Descriptive test names using should_ExpectedBehavior_when_StateUnderTest pattern
 * - @Nested classes for logical grouping
 * - Parameterized tests for data variations
 * - AssertJ for fluent assertions
 * - Test controller to trigger exceptions
 * - Comprehensive boundary testing (CORRECT)
 * - Single responsibility per test
 *
 * These tests verify that the GlobalExceptionHandler properly handles exceptions
 * in the web layer and returns RFC 7807 compliant error responses with correct
 * status codes, error messages, and structured format.
 */
@WebMvcTest(controllers = {GlobalExceptionHandler.class, GlobalExceptionHandlerTest.TestExceptionController.class})
@Import(GlobalExceptionHandler.class)
@DisplayName("GlobalExceptionHandler Web Layer Tests")
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Test controller to trigger various exceptions for testing the GlobalExceptionHandler
     */
    @RestController
    static class TestExceptionController {

        @GetMapping("/test/runtime-exception/{message}")
        public String triggerRuntimeException(@PathVariable String message) {
            throw new RuntimeException(message);
        }

        @GetMapping("/test/generic-exception/{message}")
        public String triggerGenericException(@PathVariable String message) throws Exception {
            throw new Exception(message);
        }

        @GetMapping("/test/null-pointer-exception")
        public String triggerNullPointerException() {
            throw new NullPointerException("Null pointer error");
        }

        @GetMapping("/test/illegal-argument-exception")
        public String triggerIllegalArgumentException() {
            throw new IllegalArgumentException("Invalid argument error");
        }
    }

    @Nested
    @DisplayName("RuntimeException Handling Tests")
    class RuntimeExceptionHandlingTests {

        @Test
        @DisplayName("Should handle RuntimeException with HTTP 500 status")
        void should_handleRuntimeExceptionWithHttp500_when_runtimeExceptionThrown() throws Exception {
            // Given
            String errorMessage = "Database connection failed";

            // When & Then
            MvcResult result = mockMvc.perform(get("/test/runtime-exception/{message}", errorMessage)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.title").value("Internal Server Error"))
                .andExpect(jsonPath("$.detail").value("An unexpected error occurred while processing the request"))
                .andExpect(jsonPath("$.instance").exists()) // URL encoded, so just check existence
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errorId").exists())
                .andReturn();

            // And - Verify response structure
            String responseContent = result.getResponse().getContentAsString();
            JsonNode responseJson = objectMapper.readTree(responseContent);
            assertThat(responseJson.has("type")).isTrue();
        }

        @Test
        @DisplayName("Should include timestamp in RuntimeException response")
        void should_includeTimestampInResponse_when_runtimeExceptionHandled() throws Exception {
            // Given
            Instant beforeRequest = Instant.now().minusSeconds(1);

            // When
            MvcResult result = mockMvc.perform(get("/test/runtime-exception/test-error"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.timestamp").exists())
                .andReturn();

            // Then
            String responseContent = result.getResponse().getContentAsString();
            JsonNode responseJson = objectMapper.readTree(responseContent);
            String timestampStr = responseJson.get("timestamp").asText();
            Instant timestamp = Instant.parse(timestampStr);

            Instant afterRequest = Instant.now().plusSeconds(1);
            assertThat(timestamp)
                .as("Timestamp should be within reasonable time range")
                .isBetween(beforeRequest, afterRequest);
        }

        @Test
        @DisplayName("Should include unique error ID in RuntimeException response")
        void should_includeUniqueErrorId_when_runtimeExceptionHandled() throws Exception {
            // When
            MvcResult result = mockMvc.perform(get("/test/runtime-exception/test-error"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errorId").exists())
                .andReturn();

            // Then
            String responseContent = result.getResponse().getContentAsString();
            JsonNode responseJson = objectMapper.readTree(responseContent);
            String errorId = responseJson.get("errorId").asText();

            assertThat(errorId)
                .as("Error ID should not be empty")
                .isNotEmpty();
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "password-secret123-connection-failed",
            "api-key-sk-1234567890abcdef-invalid",
            "database-credentials-user-password-host-5432"
        })
        @DisplayName("Should not expose sensitive information in RuntimeException response detail")
        void should_notExposeSensitiveInformation_when_runtimeExceptionContainsSensitiveData(String sensitiveMessage) throws Exception {
            // When & Then
            MvcResult result = mockMvc.perform(get("/test/runtime-exception/{message}", sensitiveMessage))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.detail").value("An unexpected error occurred while processing the request"))
                .andReturn();

            // Then - Verify sensitive data is not exposed in the detail field (instance field may contain URL path)
            String responseContent = result.getResponse().getContentAsString();
            JsonNode responseJson = objectMapper.readTree(responseContent);
            String detailField = responseJson.get("detail").asText();

            assertThat(detailField)
                .as("Should not expose sensitive details in error detail field")
                .doesNotContain("password", "secret", "key", "credentials")
                .isEqualTo("An unexpected error occurred while processing the request");
        }

        @Test
        @DisplayName("Should generate unique error IDs for different RuntimeExceptions")
        void should_generateUniqueErrorIds_when_multipleRuntimeExceptionsHandled() throws Exception {
            // When
            MvcResult result1 = mockMvc.perform(get("/test/runtime-exception/first-error"))
                .andExpect(status().isInternalServerError())
                .andReturn();

            MvcResult result2 = mockMvc.perform(get("/test/runtime-exception/second-error"))
                .andExpect(status().isInternalServerError())
                .andReturn();

            // Then
            String response1 = result1.getResponse().getContentAsString();
            String response2 = result2.getResponse().getContentAsString();

            JsonNode json1 = objectMapper.readTree(response1);
            JsonNode json2 = objectMapper.readTree(response2);

            String errorId1 = json1.get("errorId").asText();
            String errorId2 = json2.get("errorId").asText();

            assertThat(errorId1)
                .as("Error IDs should be unique")
                .isNotEqualTo(errorId2);

            assertThat(errorId1).as("First error ID should not be empty").isNotEmpty();
            assertThat(errorId2).as("Second error ID should not be empty").isNotEmpty();
        }
    }

    @Nested
    @DisplayName("Generic Exception Handling Tests")
    class GenericExceptionHandlingTests {

        @Test
        @DisplayName("Should handle generic Exception with HTTP 500 status")
        void should_handleGenericExceptionWithHttp500_when_genericExceptionThrown() throws Exception {
            // Given
            String errorMessage = "Unexpected system error";

            // When & Then
            mockMvc.perform(get("/test/generic-exception/{message}", errorMessage))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.title").value("Internal Server Error"))
                .andExpect(jsonPath("$.detail").value("An unexpected error occurred while processing the request"))
                .andExpect(jsonPath("$.instance").exists()) // URL encoded, so just check existence
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errorId").exists());
        }

        @Test
        @DisplayName("Should include required properties in generic Exception response")
        void should_includeRequiredProperties_when_genericExceptionHandled() throws Exception {
            // When
            MvcResult result = mockMvc.perform(get("/test/generic-exception/system-error"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errorId").exists())
                .andReturn();

            // Then
            String responseContent = result.getResponse().getContentAsString();
            JsonNode responseJson = objectMapper.readTree(responseContent);

            assertThat(responseJson.get("timestamp").asText())
                .as("Timestamp should be valid ISO string")
                .matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.*Z");

            assertThat(responseJson.get("errorId").asText())
                .as("Error ID should be String type")
                .isNotEmpty();
        }
    }

    @Nested
    @DisplayName("Request URI Handling Tests")
    class RequestUriHandlingTests {

        @ParameterizedTest
        @ValueSource(strings = {
            "first-uri",
            "second-uri",
            "third-uri",
            "health-check",
            "actuator-info"
        })
        @DisplayName("Should handle different request URIs correctly")
        void should_handleDifferentRequestUris_when_differentEndpointsThrowExceptions(String uriPart) throws Exception {
            // When & Then
            MvcResult result = mockMvc.perform(get("/test/runtime-exception/{message}", uriPart))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.instance").exists())
                .andReturn();

            // Then - Verify instance contains expected URI path
            String responseContent = result.getResponse().getContentAsString();
            JsonNode responseJson = objectMapper.readTree(responseContent);
            String instanceField = responseJson.get("instance").asText();

            assertThat(instanceField)
                .as("Should include the request URI path")
                .contains("/test/runtime-exception/")
                .contains(uriPart);
        }

        @Test
        @DisplayName("Should handle complex URI paths")
        void should_handleComplexUriPaths_when_nestedPathsUsed() throws Exception {
            // When & Then
            MvcResult result = mockMvc.perform(get("/test/runtime-exception/complex-path-test"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.instance").exists())
                .andReturn();

            // Then - Verify instance contains expected URI path
            String responseContent = result.getResponse().getContentAsString();
            JsonNode responseJson = objectMapper.readTree(responseContent);
            String instanceField = responseJson.get("instance").asText();

            assertThat(instanceField)
                .as("Should include the complex URI path")
                .contains("/test/runtime-exception/complex-path-test");
        }
    }

    @Nested
    @DisplayName("RFC 7807 Compliance Tests")
    class Rfc7807ComplianceTests {

        @ParameterizedTest
        @MethodSource("exceptionEndpointProvider")
        @DisplayName("Should return RFC 7807 compliant structure for different exception types")
        void should_returnRfc7807CompliantStructure_when_differentExceptionTypesHandled(
                String endpoint, String expectedTitle) throws Exception {
            // When & Then
            MvcResult result = mockMvc.perform(get(endpoint))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.type").exists())
                .andExpect(jsonPath("$.title").value(expectedTitle))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.detail").exists())
                .andExpect(jsonPath("$.instance").exists())
                .andReturn();

            // And - Verify custom properties
            String responseContent = result.getResponse().getContentAsString();
            JsonNode responseJson = objectMapper.readTree(responseContent);
            assertThat(responseJson.has("timestamp")).isTrue();
            assertThat(responseJson.has("errorId")).isTrue();
        }

        private static Stream<Arguments> exceptionEndpointProvider() {
            return Stream.of(
                Arguments.of("/test/runtime-exception/test-error", "Internal Server Error"),
                Arguments.of("/test/generic-exception/test-error", "Internal Server Error"),
                Arguments.of("/test/illegal-argument-exception", "Internal Server Error"),
                Arguments.of("/test/null-pointer-exception", "Internal Server Error")
            );
        }

        @Test
        @DisplayName("Should include custom properties in RFC 7807 structure")
        void should_includeCustomProperties_when_exceptionHandled() throws Exception {
            // When
            MvcResult result = mockMvc.perform(get("/test/runtime-exception/test-error"))
                .andExpect(status().isInternalServerError())
                .andReturn();

            // Then
            String responseContent = result.getResponse().getContentAsString();
            JsonNode responseJson = objectMapper.readTree(responseContent);

            assertThat(responseJson.has("timestamp"))
                .as("Should include timestamp property")
                .isTrue();

            assertThat(responseJson.has("errorId"))
                .as("Should include errorId property")
                .isTrue();
        }

        @Test
        @DisplayName("Should maintain consistent response structure across exception types")
        void should_maintainConsistentResponseStructure_when_differentExceptionTypesHandled() throws Exception {
            // When
            MvcResult runtimeResult = mockMvc.perform(get("/test/runtime-exception/test-error"))
                .andExpect(status().isInternalServerError())
                .andReturn();

            MvcResult genericResult = mockMvc.perform(get("/test/generic-exception/test-error"))
                .andExpect(status().isInternalServerError())
                .andReturn();

            // Then
            JsonNode runtimeJson = objectMapper.readTree(runtimeResult.getResponse().getContentAsString());
            JsonNode genericJson = objectMapper.readTree(genericResult.getResponse().getContentAsString());

            assertThat(runtimeJson.get("title").asText())
                .as("Both exception types should have same error title")
                .isEqualTo(genericJson.get("title").asText());

            assertThat(runtimeJson.get("detail").asText())
                .as("Both exception types should have same error detail")
                .isEqualTo(genericJson.get("detail").asText());

            assertThat(runtimeJson.get("status").asInt())
                .as("Both exception types should have same status code")
                .isEqualTo(genericJson.get("status").asInt());
        }
    }

    @Nested
    @DisplayName("Error Response Validation Tests")
    class ErrorResponseValidationTests {

        @Test
        @DisplayName("Should validate all required fields are present")
        void should_validateAllRequiredFieldsPresent_when_exceptionHandled() throws Exception {
            // When & Then
            mockMvc.perform(get("/test/runtime-exception/validation-test"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.type").exists())
                .andExpect(jsonPath("$.title").isNotEmpty())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.detail").isNotEmpty())
                .andExpect(jsonPath("$.instance").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errorId").exists());
        }

        @Test
        @DisplayName("Should return appropriate server error status range")
        void should_returnServerErrorStatusRange_when_exceptionHandled() throws Exception {
            // When & Then
            MvcResult result = mockMvc.perform(get("/test/runtime-exception/server-error-test"))
                .andExpect(status().is5xxServerError())
                .andReturn();

            int statusCode = result.getResponse().getStatus();
            assertThat(statusCode)
                .as("Should return status in 5xx server error range")
                .isBetween(500, 599);
        }

        @Test
        @DisplayName("Should provide descriptive error message")
        void should_provideDescriptiveErrorMessage_when_exceptionHandled() throws Exception {
            // When & Then
            MvcResult result = mockMvc.perform(get("/test/runtime-exception/descriptive-message-test"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.detail").isNotEmpty())
                .andReturn();

            String responseContent = result.getResponse().getContentAsString();
            JsonNode responseJson = objectMapper.readTree(responseContent);
            String detail = responseJson.get("detail").asText();

            assertThat(detail)
                .as("Error message should be descriptive and user-friendly")
                .doesNotContain("null", "undefined")
                .contains("unexpected error", "processing", "request");
        }
    }

    @Nested
    @DisplayName("Edge Cases and Boundary Conditions Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle exception with null message")
        void should_handleExceptionWithNullMessage_when_exceptionMessageIsNull() throws Exception {
            // Note: URL encoding will convert null to "null" string, but the handler should still work
            // When & Then
            mockMvc.perform(get("/test/runtime-exception/null"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.detail").isNotEmpty())
                .andExpect(jsonPath("$.detail").value("An unexpected error occurred while processing the request"));
        }

        @Test
        @DisplayName("Should handle exception with empty message")
        void should_handleExceptionWithEmptyMessage_when_exceptionMessageIsEmpty() throws Exception {
            // When & Then
            mockMvc.perform(get("/test/runtime-exception/ ")) // Space for empty-like message
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.detail").isNotEmpty())
                .andExpect(jsonPath("$.detail").value("An unexpected error occurred while processing the request"));
        }

        @Test
        @DisplayName("Should handle very long exception messages")
        void should_handleVeryLongExceptionMessages_when_exceptionMessageIsVeryLong() throws Exception {
            // Given - Create a reasonably long message (URL has practical limits)
            String longMessage = "very-long-error-message".repeat(10);

            // When & Then
            mockMvc.perform(get("/test/runtime-exception/{message}", longMessage))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.detail").value("An unexpected error occurred while processing the request"));
        }
    }
}
