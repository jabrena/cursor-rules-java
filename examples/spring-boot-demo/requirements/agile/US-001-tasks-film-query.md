# Task List: Film Query Starting with Letter A

## Artifact Sources

- **User Story:** `examples/spring-boot-demo/requirements/agile/US-001-user-story-film-query.md`
- **Acceptance Criteria:** `examples/spring-boot-demo/requirements/agile/US-001-film-query.feature`
- **UML Sequence Diagram:** `examples/spring-boot-demo/requirements/design/US-001-film-query-sequence.puml`
- **C4 Model Diagrams:** 
  - `examples/spring-boot-demo/requirements/design/c4/C4_FilmQuery_Context.puml`
  - `examples/spring-boot-demo/requirements/design/c4/C4_FilmQuery_Container.puml`
  - `examples/spring-boot-demo/requirements/design/c4/C4_FilmQuery_Component.puml`
- **ADR Functional Requirements:** `[NOT YET CREATED - Required for detailed sub-tasks]`
- **ADR Acceptance testing Strategy:** `[NOT YET CREATED - Required for detailed sub-tasks]`
- **ADR Non Functional Requirements:** `[NOT YET CREATED - Required for detailed sub-tasks]`

## Tasks

### Phase 1: High-Level Tasks

- [x] 1.0 **Project Setup and Database Configuration**
  - [x] 1.1 Create Spring Boot project with Maven
  - [x] 1.2 Add Spring Boot dependencies (Web, Data JDBC, Test)
  - [x] 1.3 Add PostgreSQL driver dependency
  - [x] 1.4 Add TestContainers dependencies (PostgreSQL, JUnit)
  - [x] 1.5 Add OpenAPI/SpringDoc dependency
  - [x] 1.6 Add JaCoCo Maven plugin for code coverage
  - [x] 1.7 Configure application.yaml for PostgreSQL connection
  - [x] 1.8 Set up Docker Compose with Sakila PostgreSQL database
  - [x] 1.9 Verify database connection and Sakila data availability

- [x] 2.0 **Acceptance Tests Implementation (TestRestTemplate-Based)**
  - [x] 2.1 Set up Spring Boot Test with @SpringBootTest and TestRestTemplate
  - [x] 2.2 Configure TestContainers for PostgreSQL in acceptance tests
  - [x] 2.3 Create acceptance test for "Successfully retrieve films starting with A" scenario (46 films expected)
  - [x] 2.4 Create acceptance test for "API endpoint responds correctly" scenario (HTTP 200, JSON format)
  - [x] 2.5 Create acceptance test for "Database query performance" scenario (< 2 seconds)
  - [x] 2.6 Create acceptance test for "Handle empty results gracefully" scenario
  - [x] 2.7 Create acceptance test for "Query films by different starting letters" scenario (parameterized)
  - [x] 2.8 Create acceptance test for "Invalid query parameter handling" scenario (HTTP 400)
  - [x] 2.9 **Verify all acceptance tests FAIL** (Red phase - Outside-in TDD strategy)

- [x] 3.0 **REST API Unit Tests Creation**
  - [x] 3.1 Create unit test for GET /api/v1/films endpoint without parameters
  - [x] 3.2 Create unit test for GET /api/v1/films?startsWith=A endpoint
  - [x] 3.3 Create unit tests for parameter validation (valid single letters)
  - [x] 3.4 Create unit tests for invalid parameter scenarios (empty, multiple chars, special chars)
  - [x] 3.5 Create unit tests for response format validation (JSON structure)
  - [x] 3.6 Create unit tests for HTTP status codes (200, 400)
  - [x] 3.7 Create unit tests for controller error handling integration
  - [x] 3.8 Create unit tests for OpenAPI annotations validation
  - [x] 3.9 **Verify REST API unit tests FAIL** (Red phase - TDD strategy)

- [x] 4.0 **REST API Layer Implementation**
  - [x] 4.1 Create FilmController class with @RestController annotation
  - [x] 4.2 Implement GET /api/v1/films endpoint with @GetMapping
  - [x] 4.3 Add startsWith parameter with @RequestParam validation
  - [x] 4.4 Implement parameter validation logic (single letter, not empty)
  - [x] 4.5 Create FilmResponse DTO for JSON response format
  - [x] 4.6 Implement response formatting with films array, count, and filter
  - [x] 4.7 Add OpenAPI @Operation, @Parameter, and @ApiResponse annotations
  - [x] 4.8 Implement proper HTTP status code handling
  - [x] 4.9 **Verify REST API unit tests PASS** (Green phase - TDD strategy)
  - [x] 4.10 **Verify acceptance tests PASS** (Outside-in TDD validation)
  - [x] 4.11 **Test locally** - Ensure application starts and endpoints are accessible

- [x] 5.0 **Business Logic Unit Tests Creation**
  - [x] 5.1 Create unit tests for FilmService.findFilmsByStartingLetter() method
  - [x] 5.2 Create unit tests for film filtering logic (case insensitive matching)
  - [x] 5.3 Create unit tests for DTO transformation (Entity to Response DTO)
  - [x] 5.4 Create unit tests for business validation (letter parameter validation)
  - [x] 5.5 Create unit tests for empty result handling logic
  - [x] 5.6 Create unit tests for error scenarios (invalid input, null handling)
  - [x] 5.7 Create unit tests for business rules (46 films for "A", etc.)
  - [x] 5.8 **Verify business logic unit tests FAIL** (Red phase - TDD strategy)

- [x] 6.0 **Business Logic Layer Implementation**
  - [x] 6.1 Create FilmService class with @Service annotation
  - [x] 6.2 Implement findFilmsByStartingLetter(String letter) method
  - [x] 6.3 Add business validation for letter parameter (not null, single character)
  - [x] 6.4 Implement film filtering logic (case insensitive LIKE query)
  - [x] 6.5 Create Film entity class with proper annotations
  - [x] 6.6 Create FilmDTO for data transfer
  - [x] 6.7 Implement entity to DTO transformation logic
  - [x] 6.8 Add empty result handling with appropriate messaging
  - [x] 6.9 **Verify business logic unit tests PASS** (Green phase - TDD strategy)
  - [x] 6.10 **Verify acceptance tests PASS** (Outside-in TDD validation)
  - [x] 6.11 **Test locally** - Ensure application works end-to-end with business logic

- [ ] 7.0 **Data Access Integration Tests Creation**
  - [ ] 7.1 Set up TestContainers PostgreSQL configuration for integration tests
  - [ ] 7.2 Create integration tests for FilmRepository.findByTitleStartingWith() method
  - [ ] 7.3 Create integration tests for exact count validation (46 films for "A")
  - [ ] 7.4 Create integration tests for different starting letters (B, C, etc.)
  - [ ] 7.5 Create integration tests for empty results (letters with no films)
  - [ ] 7.6 Create integration tests for database performance (< 2 seconds)
  - [ ] 7.7 Create integration tests for database error scenarios
  - [ ] 7.8 Create integration tests for Spring Data JDBC configuration
  - [ ] 7.9 **Verify data access integration tests FAIL** (Red phase - TDD strategy)

- [ ] 8.0 **Data Access Layer Implementation**
  - [ ] 8.1 Create Film entity class with @Table annotation
  - [ ] 8.2 Add entity fields (filmId, title) with proper annotations
  - [ ] 8.3 Create FilmRepository interface extending CrudRepository
  - [ ] 8.4 Implement findByTitleStartingWith(String prefix) method
  - [ ] 8.5 Configure Spring Data JDBC with PostgreSQL
  - [ ] 8.6 Set up database connection properties for Sakila DB
  - [ ] 8.7 Add database query optimization (ensure proper indexing)
  - [ ] 8.8 Implement repository error handling
  - [ ] 8.9 **Verify data access integration tests PASS** (Green phase - TDD strategy)
  - [ ] 8.10 **Verify acceptance tests PASS** (Outside-in TDD validation)
  - [ ] 8.11 **Test locally** - Ensure application works end-to-end with database integration (follow Local Testing Approach in Notes)

- [ ] 9.0 **Error Handling Unit Tests Creation**
  - [ ] 9.1 Create unit tests for GlobalExceptionHandler class
  - [ ] 9.2 Create unit tests for IllegalArgumentException handling (400 Bad Request)
  - [ ] 9.3 Create unit tests for RFC 7807 ProblemDetail response format
  - [ ] 9.4 Create unit tests for invalid parameter error messages
  - [ ] 9.5 Create unit tests for HTTP status code mapping
  - [ ] 9.6 Create unit tests for error response JSON structure
  - [ ] 9.7 **Verify error handling unit tests FAIL** (Red phase - TDD strategy)

- [ ] 10.0 **Error Handling and Global Exception Management**
  - [ ] 10.1 Create GlobalExceptionHandler class with @ControllerAdvice
  - [ ] 10.2 Implement handleIllegalArgumentException method
  - [ ] 10.3 Implement RFC 7807 ProblemDetail response format
  - [ ] 10.4 Add proper HTTP status code mapping (400, 500)
  - [ ] 10.5 Implement descriptive error messages for invalid parameters
  - [ ] 10.6 Add request validation error handling
  - [ ] 10.7 **Verify error handling unit tests PASS** (Green phase - TDD strategy)
  - [ ] 10.8 **Verify acceptance tests PASS** (Outside-in TDD validation)
  - [ ] 10.9 **Test locally** - Ensure application handles errors gracefully end-to-end (follow Local Testing Approach in Notes)

- [ ] 11.0 **Integration Testing Implementation**
  - [ ] 11.1 Set up end-to-end integration test suite
  - [ ] 11.2 Implement full-stack integration tests with TestContainers
  - [ ] 11.3 Create performance tests for query execution time (< 2 seconds)
  - [ ] 11.4 Validate complete request-response cycle
  - [ ] 11.5 Configure JaCoCo Maven plugin in pom.xml
  - [ ] 11.6 Set up 80% minimum code coverage threshold
  - [ ] 11.7 Configure coverage exclusions for configuration classes
  - [ ] 11.8 Generate HTML and XML coverage reports
  - [ ] 11.9 Verify coverage threshold enforcement in build
  - [ ] 11.10 **Validate 80% code coverage achieved**

- [ ] 12.0 **API Documentation and Validation**
  - [ ] 12.1 Complete OpenAPI documentation with proper descriptions
  - [ ] 12.2 Add API examples and response schemas
  - [ ] 12.3 Document all error responses and status codes
  - [ ] 12.4 Validate API documentation accuracy with Swagger UI
  - [ ] 12.5 Run final acceptance criteria validation
  - [ ] 12.6 Execute performance validation (< 2 seconds response time)
  - [ ] 12.7 Validate 46 films returned for letter "A"
  - [ ] 12.8 **Final end-to-end testing and sign-off** (follow Local Testing Approach in Notes)

## Acceptance Criteria Mapping

Based on the Gherkin scenarios from `US-001-film-query.feature`:

- [ ] AC1: Successfully retrieve films starting with "A" (46 films) → Tasks 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0
- [ ] AC2: API endpoint responds correctly with HTTP 200 and JSON format → Tasks 2.0, 3.0, 4.0, 9.0, 10.0  
- [ ] AC3: Database query performance under 2 seconds → Tasks 2.0, 7.0, 8.0, 11.0, 12.0
- [ ] AC4: Handle empty results gracefully → Tasks 2.0, 5.0, 6.0, 9.0, 10.0
- [ ] AC5: Query films by different starting letters → Tasks 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0
- [ ] AC6: Invalid query parameter handling with HTTP 400 → Tasks 2.0, 3.0, 4.0, 9.0, 10.0

## Relevant Files

- `pom.xml` - Maven project configuration with Spring Boot, Data JDBC, TestContainers, JaCoCo, and OpenAPI dependencies
- `src/main/java/info/jab/ms/FilmQueryApplication.java` - Spring Boot main application class
- `src/main/java/info/jab/ms/controller/FilmController.java` - REST Controller for /api/v1/films endpoint with OpenAPI annotations
- `src/main/java/com/example/demo/service/FilmService.java` - Business logic layer for film query operations with @Service annotation, parameter validation, filtering logic, and DTO transformation
- `src/main/java/com/example/demo/repository/FilmRepository.java` - Data access layer with Spring Data JDBC
- `src/main/java/com/example/demo/entity/Film.java` - Film entity class with proper Spring Data JDBC annotations (@Table, @Id, @Column)
- `src/main/java/com/example/demo/dto/FilmDTO.java` - Data transfer object for film data with entity conversion methods
- `src/main/java/com/example/demo/dto/FilmResponse.java` - Response DTO for API responses
- `src/main/java/info/jab/ms/controller/GlobalExceptionHandler.java` - Global error handling with RFC 7807 ProblemDetail
- `src/test/java/com/example/demo/controller/FilmControllerTest.java` - REST Controller unit tests with tasks 3.1, 3.2, and 3.3 implementation
- `src/test/java/info/jab/ms/controller/GlobalExceptionHandlerTest.java` - Exception handler unit tests
- `src/test/java/com/example/demo/service/FilmServiceTest.java` - Complete unit test suite for FilmService.findFilmEntitiesByStartingLetter() with comprehensive coverage: method testing, case insensitive matching, DTO transformation, business validation, empty result handling, error scenarios, and business rules (46 films for "A")
- `src/test/java/info/jab/ms/repository/FilmRepositoryTest.java` - Data access integration tests
- `src/test/java/info/jab/ms/integration/FilmQueryIntegrationTest.java` - End-to-end integration tests
- `src/test/java/info/jab/ms/acceptance/FilmQueryAcceptanceIT.java` - TestRestTemplate-based acceptance tests
- `application.yaml` - Application configuration for database connection
- `docker-compose.yml` - Docker Compose configuration for Sakila PostgreSQL database
- `openapi-film-query.yaml` - OpenAPI 3.0 specification for the Film Query API

## Notes

- **Test implementation follows Outside-in TDD strategy** with acceptance tests driving development
- **Component structure aligns with C4 model architecture** (Controller → Service → Repository)
- **Technical flow respects sequence diagram interactions** from UML design
- **JaCoCo enforces 80% minimum code coverage** as quality gate
- **TestContainers provide isolated testing environment** for database integration tests
- **Database:** Using Sakila PostgreSQL database with Docker: `docker run -e POSTGRES_PASSWORD=sakila --rm --name sakiladb -p 5432:5432 -d "sakiladb/postgres"`
- **Build command:** `./mvnw clean verify` to run all tests with coverage validation
- **Local Testing Approach:** 
  1. Start application: `./mvnw spring-boot:run -Dspring-boot.run.profiles=local` (run in background)
  2. Wait 20 seconds for startup
  3. Test endpoints with curl:
     - `curl -s "http://localhost:8080/api/v1/films?startsWith=A" | jq .`
     - `curl -s "http://localhost:8080/api/v1/films?startsWith=B" | jq '{count: .count, filter: .filter}'`
     - `curl -s "http://localhost:8080/api/v1/films?startsWith=ABC" | jq .` (test error handling)
  4. Clean up: `pkill -f "spring-boot:run" && docker stop sakiladb-test && docker rm sakiladb-test`

---

**Status:** Phase 2 Complete - Detailed sub-tasks generated based on comprehensive agile artifacts analysis.

**Implementation Ready:** All tasks are now actionable with specific sub-tasks that follow Outside-in TDD methodology. 