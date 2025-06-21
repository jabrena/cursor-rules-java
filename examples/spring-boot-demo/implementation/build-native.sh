#!/bin/bash

# Spring Boot Native Compilation Build Script
# This script demonstrates the various native compilation options available

set -e

echo "🚀 Spring Boot Native Compilation Build Script"
echo "================================================"

# Function to print colored output
print_step() {
    echo -e "\n\033[1;34m$1\033[0m"
}

print_success() {
    echo -e "\033[1;32m✅ $1\033[0m"
}

print_error() {
    echo -e "\033[1;31m❌ $1\033[0m"
}

# Check if GraalVM is available
check_graalvm() {
    print_step "Checking GraalVM installation..."
    if command -v native-image &> /dev/null; then
        print_success "GraalVM native-image found: $(native-image --version | head -n1)"
    else
        print_error "GraalVM native-image not found. Please install GraalVM."
        echo "Install GraalVM: https://www.graalvm.org/downloads/"
        echo "Or use SDKMAN: sdk install java 21.0.1-graal"
        exit 1
    fi
}

# Clean previous builds
clean_build() {
    print_step "Cleaning previous builds..."
    ./mvnw clean
    print_success "Clean completed"
}

# Run tests first
run_tests() {
    print_step "Running tests to ensure application works..."
    ./mvnw test
    print_success "Tests passed"
}

# Compile native executable
compile_native() {
    print_step "Compiling native executable with AOT processing..."
    echo "This may take several minutes..."
    
    # Set SPRING_PROFILES_ACTIVE for native compilation
    export SPRING_PROFILES_ACTIVE=native
    
    ./mvnw -Pnative clean native:compile
    print_success "Native executable compiled successfully"
    
    if [ -f target/sakila-api ]; then
        echo "📁 Native executable location: target/sakila-api"
        echo "📊 Executable size: $(du -h target/sakila-api | cut -f1)"
    fi
}

# Test native executable
test_native() {
    print_step "Testing native executable..."
    if [ -f target/sakila-api ]; then
        echo "Starting native executable for quick test..."
        timeout 10s ./target/sakila-api --server.port=8081 --spring.profiles.active=native,test > /dev/null 2>&1 &
        PID=$!
        sleep 5
        
        if kill -0 $PID 2>/dev/null; then
            print_success "Native executable started successfully"
            kill $PID 2>/dev/null || true
        else
            print_error "Native executable failed to start"
        fi
    else
        print_error "Native executable not found"
    fi
}

# Build container image with native binary
build_container() {
    print_step "Building container image with native binary..."
    echo "This will create a lightweight container with the native executable..."
    
    ./mvnw spring-boot:build-image -Pnative
    print_success "Container image built successfully"
    
    echo "🐳 Container image: sakila-api:0.0.1-SNAPSHOT"
    echo "Run with: docker run -p 8080:8080 sakila-api:0.0.1-SNAPSHOT"
}

# Run native tests (if available)
run_native_tests() {
    print_step "Running native tests..."
    ./mvnw -Pnative test
    print_success "Native tests completed"
}

# Display performance comparison
show_performance_info() {
    print_step "Performance Characteristics:"
    echo "
🚀 Startup Time:    10-100x faster than JVM
💾 Memory Usage:    Reduced footprint (especially for small apps)
⚡ Build Time:      Longer compilation (2-10 minutes)
📦 Image Size:      Larger executable, but no JVM needed
🎯 Runtime Perf:    Different characteristics vs JIT compilation

📋 Commands Summary:
  ./mvnw -Pnative clean native:compile           # Build native executable
  ./mvnw -Pnative test                           # Run native tests  
  ./mvnw spring-boot:build-image -Pnative        # Build container image
  ./target/sakila-api                            # Run native executable
"
}

# Main execution
main() {
    case "${1:-help}" in
        "check")
            check_graalvm
            ;;
        "clean")
            clean_build
            ;;
        "test")
            run_tests
            ;;
        "compile")
            check_graalvm
            clean_build
            compile_native
            test_native
            ;;
        "container")
            check_graalvm
            build_container
            ;;
        "native-test")
            check_graalvm
            run_native_tests
            ;;
        "full")
            check_graalvm
            clean_build
            run_tests
            compile_native
            test_native
            show_performance_info
            ;;
        "help"|*)
            echo "Usage: $0 [command]"
            echo ""
            echo "Commands:"
            echo "  check         Check GraalVM installation"
            echo "  clean         Clean previous builds"
            echo "  test          Run application tests"
            echo "  compile       Compile native executable"
            echo "  container     Build container with native image"
            echo "  native-test   Run native-specific tests"
            echo "  full          Complete native build process"
            echo "  help          Show this help message"
            echo ""
            show_performance_info
            ;;
    esac
}

main "$@" 