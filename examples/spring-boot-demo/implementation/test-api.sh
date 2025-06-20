#!/bin/bash

# Spring Boot API Test Script
# This script starts a Spring Boot application and runs comprehensive API tests
# Usage: ./test-api.sh [profile] [port]
# Example: ./test-api.sh local 8080

set -e  # Exit on any error

# Configuration
PROFILE=${1:-local}
PORT=${2:-8080}
MAX_STARTUP_WAIT=120  # 2 minutes
LOG_FILE="app-test.log"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Utility functions
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Cleanup function
cleanup() {
    log_info "Cleaning up..."
    if [ ! -z "$APP_PID" ] && kill -0 $APP_PID 2>/dev/null; then
        log_info "Stopping application (PID: $APP_PID)..."
        kill $APP_PID 2>/dev/null || true
        wait $APP_PID 2>/dev/null || true
        log_success "Application stopped"
    fi
    
    # Remove log file if requested
    if [ "$CLEANUP_LOGS" = "true" ] && [ -f "$LOG_FILE" ]; then
        rm -f "$LOG_FILE"
    fi
}

# Set trap for cleanup
trap cleanup EXIT INT TERM

# Check prerequisites
check_prerequisites() {
    log_info "Checking prerequisites..."
    
    # Check if curl is available
    if ! command -v curl &> /dev/null; then
        log_error "curl is required but not installed"
        exit 1
    fi
    
    # Check if jq is available
    if ! command -v jq &> /dev/null; then
        log_warning "jq not found, JSON validation will be skipped"
        SKIP_JSON_VALIDATION=true
    fi
    
    # Check if Maven wrapper exists
    if [ ! -f "./mvnw" ]; then
        log_error "Maven wrapper (mvnw) not found in current directory"
        exit 1
    fi
    
    log_success "Prerequisites check passed"
}

# Start Spring Boot application
start_application() {
    log_info "Starting Spring Boot application with profile: $PROFILE"
    
    # Start the application in background
    ./mvnw clean spring-boot:run -Dspring-boot.run.profiles=$PROFILE > "$LOG_FILE" 2>&1 &
    APP_PID=$!
    
    log_info "Application started with PID: $APP_PID"
    
    # Wait for application to be ready
    log_info "Waiting for application to start (max ${MAX_STARTUP_WAIT}s)..."
    
    local retry_count=0
    local max_retries=$((MAX_STARTUP_WAIT / 2))
    
    while [ $retry_count -lt $max_retries ]; do
        # Check if actuator health endpoint is available
        if curl -f -s "http://localhost:$PORT/actuator/health" > /dev/null 2>&1; then
            log_success "Application is healthy and ready!"
            return 0
        fi
        
        # Fallback: check if films API endpoint is available
        if curl -f -s "http://localhost:$PORT/api/v1/films?startsWith=A" > /dev/null 2>&1; then
            log_success "Application is ready (films API responding)!"
            return 0
        fi
        
        # Check if process is still running
        if ! kill -0 $APP_PID 2>/dev/null; then
            log_error "Application process died unexpectedly!"
            log_error "Application logs:"
            cat "$LOG_FILE"
            return 1
        fi
        
        retry_count=$((retry_count + 1))
        log_info "Attempt $retry_count/$max_retries - waiting for application..."
        sleep 2
    done
    
    log_error "Application failed to start within ${MAX_STARTUP_WAIT} seconds"
    log_error "Application logs:"
    cat "$LOG_FILE"
    return 1
}

# Validate JSON response
validate_json() {
    local response="$1"
    
    if [ "$SKIP_JSON_VALIDATION" = "true" ]; then
        return 0
    fi
    
    echo "$response" | jq . > /dev/null 2>&1
}

# Execute API test
run_api_test() {
    local test_name="$1"
    local url="$2"
    local expected_status="$3"
    local description="$4"
    
    log_info "Test: $test_name - $description"
    
    # Make the request
    local response=$(curl -s -w "HTTPSTATUS:%{http_code}" "http://localhost:$PORT$url")
    local http_status=$(echo $response | tr -d '\n' | sed -e 's/.*HTTPSTATUS://')
    local response_body=$(echo $response | sed -e 's/HTTPSTATUS:.*//g')
    
    # Check HTTP status
    if [ "$http_status" != "$expected_status" ]; then
        log_error "❌ $test_name Failed: Expected HTTP $expected_status, got $http_status"
        log_error "Response: $response_body"
        return 1
    fi
    
    # Validate JSON for successful responses
    if [ "$expected_status" = "200" ]; then
        if ! validate_json "$response_body"; then
            log_error "❌ $test_name Failed: Response is not valid JSON"
            log_error "Response: $response_body"
            return 1
        fi
        
        # Check for required fields in successful responses
        if [ "$SKIP_JSON_VALIDATION" != "true" ]; then
            local count=$(echo "$response_body" | jq -r '.count // empty')
            if [ -z "$count" ] || [ "$count" = "null" ]; then
                log_error "❌ $test_name Failed: Missing 'count' field in response"
                log_error "Response: $response_body"
                return 1
            fi
            
            # Return the count for further validation
            echo "$count"
        fi
    fi
    
    log_success "✅ $test_name Passed: HTTP $http_status"
    return 0
}

# Run performance test
run_performance_test() {
    local url="$1"
    local max_duration_ms="$2"
    local test_name="Performance Test"
    
    log_info "$test_name: Request should complete within ${max_duration_ms}ms"
    
    # Measure request time
    local start_time=$(date +%s%N)
    local response=$(curl -s -w "HTTPSTATUS:%{http_code}" "http://localhost:$PORT$url")
    local end_time=$(date +%s%N)
    
    local http_status=$(echo $response | tr -d '\n' | sed -e 's/.*HTTPSTATUS://')
    local duration_ms=$(( (end_time - start_time) / 1000000 ))
    
    # Check HTTP status
    if [ "$http_status" != "200" ]; then
        log_error "❌ $test_name Failed: Expected HTTP 200, got $http_status"
        return 1
    fi
    
    # Check performance
    if [ $duration_ms -gt $max_duration_ms ]; then
        log_error "❌ $test_name Failed: Request took ${duration_ms}ms, expected < ${max_duration_ms}ms"
        return 1
    fi
    
    log_success "✅ $test_name Passed: Request completed in ${duration_ms}ms"
    return 0
}

# Main test execution
run_api_tests() {
    log_info "Starting API tests..."
    
    local failed_tests=0
    
    # Test 1: Get films starting with 'A'
    if film_count=$(run_api_test "Test 1" "/api/v1/films?startsWith=A" "200" "Get films starting with 'A'"); then
        if [ "$SKIP_JSON_VALIDATION" != "true" ]; then
            log_info "Found $film_count films starting with 'A'"
        fi
    else
        failed_tests=$((failed_tests + 1))
    fi
    
    # Test 2: Test error handling with invalid parameter
    if ! run_api_test "Test 2" "/api/v1/films?startsWith=123" "400" "Invalid parameter handling" > /dev/null; then
        failed_tests=$((failed_tests + 1))
    fi
    
    # Test 3: Test empty results
    if empty_count=$(run_api_test "Test 3" "/api/v1/films?startsWith=X" "200" "Empty results handling"); then
        if [ "$SKIP_JSON_VALIDATION" != "true" ] && [ "$empty_count" != "0" ]; then
            log_error "❌ Test 3 Failed: Expected count=0, got $empty_count"
            failed_tests=$((failed_tests + 1))
        elif [ "$SKIP_JSON_VALIDATION" != "true" ]; then
            log_info "Empty results handled correctly (count=0)"
        fi
    else
        failed_tests=$((failed_tests + 1))
    fi
    
    # Test 4: Performance test
    if ! run_performance_test "/api/v1/films?startsWith=A" 2000; then
        failed_tests=$((failed_tests + 1))
    fi
    
    return $failed_tests
}

# Print summary
print_summary() {
    local failed_tests=$1
    
    echo ""
    if [ $failed_tests -eq 0 ]; then
        log_success "🎉 All API tests passed successfully!"
        log_success "✅ Application startup: OK"
        log_success "✅ Health check: OK"
        log_success "✅ Films API: OK"
        log_success "✅ Error handling: OK"
        log_success "✅ Empty results: OK"
        log_success "✅ Performance: OK"
    else
        log_error "❌ $failed_tests test(s) failed"
        return 1
    fi
}

# Print usage
usage() {
    echo "Usage: $0 [profile] [port]"
    echo ""
    echo "Arguments:"
    echo "  profile    Spring Boot profile to use (default: local)"
    echo "  port       Port to test on (default: 8080)"
    echo ""
    echo "Environment variables:"
    echo "  CLEANUP_LOGS=true    Remove log files after execution"
    echo ""
    echo "Examples:"
    echo "  $0                    # Use defaults (local profile, port 8080)"
    echo "  $0 test 8081         # Use test profile on port 8081"
    echo "  CLEANUP_LOGS=true $0 # Clean up log files after execution"
}

# Main execution
main() {
    log_info "Spring Boot API Test Script"
    log_info "Profile: $PROFILE, Port: $PORT"
    echo ""
    
    # Show usage if help requested
    if [ "$1" = "-h" ] || [ "$1" = "--help" ]; then
        usage
        exit 0
    fi
    
    # Run the test sequence
    check_prerequisites
    start_application
    
    # Run API tests
    failed_tests=0
    if ! run_api_tests; then
        failed_tests=$?
    fi
    
    # Print summary and exit
    if print_summary $failed_tests; then
        exit 0
    else
        exit 1
    fi
}

# Execute main function with all arguments
main "$@" 