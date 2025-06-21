package info.jab.ms.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI Configuration for Film Query Service
 *
 * This configuration provides comprehensive API documentation including:
 * - API information, version, and description
 * - Contact information and license
 * - Server configurations for different environments
 * - Global tags and enhanced documentation structure
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI filmQueryOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Film Query API")
                        .description("""
                                ## Sakila Film Database Query Service

                                This REST API provides access to film information from the PostgreSQL Sakila sample database.
                                The service allows querying films by their starting letter with high performance guarantees.

                                ### Key Features
                                - **Case-insensitive search**: Query films starting with any letter (A-Z)
                                - **High performance**: Guaranteed response time under 2 seconds
                                - **Comprehensive validation**: Input parameter validation with detailed error responses
                                - **RFC 7807 compliance**: Standardized error response format

                                ### Expected Results
                                The API returns accurate counts based on the Sakila database:
                                - Films starting with "A": 46 films
                                - Films starting with "B": 37 films
                                - Films starting with "C": 57 films

                                ### Performance Guarantees
                                - Query execution time: < 2 seconds
                                - Database connection timeout: 30 seconds
                                - Maximum response payload: Optimized for all result sets

                                ### Error Handling
                                All errors follow RFC 7807 Problem Details standard with:
                                - Structured error responses
                                - Unique error identifiers
                                - Detailed error descriptions
                                - HTTP status code compliance
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Film Query API Team")
                                .email("api-support@example.com")
                                .url("https://github.com/your-org/film-query-service"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local development server")))
                .tags(List.of(
                        new Tag()
                                .name("Films")
                                .description("Film query operations for retrieving films from the Sakila database by starting letter"),
                        new Tag()
                                .name("Health")
                                .description("Service health and monitoring endpoints")));
    }
}
