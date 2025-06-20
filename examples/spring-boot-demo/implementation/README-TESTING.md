# Spring Boot API Testing Guide

This guide explains how to use the automated testing script for the Spring Boot Film Query API.

## Test Script Overview

The `test-api.sh` script provides comprehensive testing of the Spring Boot application including:

- **Application Startup**: Verifies the application starts successfully
- **Health Checks**: Tests both actuator health endpoint and API availability
- **API Functionality**: Tests the Films API with various scenarios
- **Error Handling**: Validates proper HTTP error responses
- **Performance**: Ensures API response times meet requirements
- **Cleanup**: Properly stops the application and cleans up resources

## Usage

### Basic Usage

```bash
# Run with default settings (local profile, port 8080)
./test-api.sh

# Run with specific profile and port
./test-api.sh test 8081

# Run with log cleanup
CLEANUP_LOGS=true ./test-api.sh
```

### Parameters

- `profile` (optional): Spring Boot profile to use (default: `local`)
- `port` (optional): Port to test on (default: `8080`)

### Environment Variables

- `CLEANUP_LOGS=true`: Remove log files after execution

## Test Cases

The script runs the following test cases:

### Test 1: Basic API Functionality
- **URL**: `GET /api/v1/films?startsWith=A`
- **Expected**: HTTP 200 with JSON response containing films
- **Validation**: Checks JSON structure and required fields

### Test 2: Error Handling
- **URL**: `GET /api/v1/films?startsWith=123`
- **Expected**: HTTP 400 for invalid parameter
- **Validation**: Ensures proper error responses

### Test 3: Empty Results
- **URL**: `GET /api/v1/films?startsWith=X`
- **Expected**: HTTP 200 with empty results (count=0)
- **Validation**: Verifies graceful handling of no matches

### Test 4: Performance
- **URL**: `GET /api/v1/films?startsWith=A`
- **Expected**: Response time < 2000ms
- **Validation**: Measures and validates response time

## Prerequisites

The script automatically checks for required tools:

- **curl**: Required for HTTP requests
- **jq**: Optional for JSON validation (script continues without it)
- **mvnw**: Maven wrapper must be present in the current directory

## Local Development

```bash
# Navigate to the implementation directory
cd examples/spring-boot-demo/implementation

# Run the test script
./test-api.sh

# Run with test profile
./test-api.sh test

# Run with custom port
./test-api.sh local 8081
```

## CI/CD Integration

The script is designed to work in CI/CD environments:

```yaml
# GitHub Actions example
- name: Spring Boot API Test
  run: |
    cd examples/spring-boot-demo/implementation
    chmod +x test-api.sh
    CLEANUP_LOGS=true ./test-api.sh local 8080
```

## Output

The script provides colored output with clear indicators:

- 🔵 **[INFO]**: Informational messages
- 🟢 **[SUCCESS]**: Successful operations
- 🟡 **[WARNING]**: Warnings (non-fatal)
- 🔴 **[ERROR]**: Error messages

### Success Output Example

```
[INFO] Spring Boot API Test Script
[INFO] Profile: local, Port: 8080

[SUCCESS] Prerequisites check passed
[INFO] Starting Spring Boot application with profile: local
[INFO] Application started with PID: 12345
[SUCCESS] Application is healthy and ready!
[INFO] Starting API tests...
[SUCCESS] ✅ Test 1 Passed: HTTP 200
[SUCCESS] ✅ Test 2 Passed: HTTP 400
[SUCCESS] ✅ Test 3 Passed: HTTP 200
[SUCCESS] ✅ Performance Test Passed: Request completed in 150ms

🎉 All API tests passed successfully!
✅ Application startup: OK
✅ Health check: OK
✅ Films API: OK
✅ Error handling: OK
✅ Empty results: OK
✅ Performance: OK
```

## Troubleshooting

### Common Issues

1. **Port Already in Use**
   ```bash
   # Use a different port
   ./test-api.sh local 8081
   ```

2. **Application Fails to Start**
   - Check application logs in `app-test.log`
   - Verify database is available (if using external database)
   - Ensure all dependencies are available

3. **Tests Fail Due to Missing Data**
   - Verify test data is properly loaded
   - Check database schema is correct
   - Ensure application profile is correctly configured

4. **Performance Tests Fail**
   - System might be under load
   - Database might be slow
   - Consider adjusting performance thresholds

### Debug Mode

For detailed debugging, you can:

1. Check application logs: `cat app-test.log`
2. Run individual curl commands manually
3. Use the Spring Boot actuator endpoints for diagnostics

## Integration with Common Test Base

The script can be used alongside the `PostgreSQLTestBase` class for consistent testing:

```bash
# The script will automatically work with TestContainers
# when the application is configured with the test profile
./test-api.sh test
```

## Best Practices

1. **Always run tests before committing code**
2. **Use the test profile for integration testing**
3. **Clean up logs in CI/CD environments** (`CLEANUP_LOGS=true`)
4. **Verify both positive and negative test cases**
5. **Monitor performance metrics** over time

## Contributing

When modifying the test script:

1. Maintain backward compatibility
2. Add new test cases for new API endpoints
3. Update documentation for new parameters
4. Test in both local and CI environments
5. Follow the existing error handling patterns 