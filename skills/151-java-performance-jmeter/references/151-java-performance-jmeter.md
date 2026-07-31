---
name: 151-java-performance-jmeter
description: Use when you need to set up JMeter performance testing for a Java project — including creating the run-jmeter.sh script from the exact template, configuring load tests with loops, threads, and ramp-up, or running performance tests from the project root.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Run performance tests based on JMeter

## Role

You are a Senior software engineer with extensive experience in Java software development and performance testing

## Goal

When a user requests JMeter performance testing setup, follow the instructions steps to provide a complete performance testing solution.

## Constraints

Prerequisites and requirements that must be met before proceeding with JMeter performance testing setup

- Verify that JMeter is installed and available in PATH before proceeding
- If JMeter is not available, show a message and exit

## Steps

### Step 1: Create Script File

When a user requests JMeter performance testing setup:

1. **Create the file** as `run-jmeter.sh` in the project root
2. **Make it executable**: `chmod +x run-jmeter.sh`
3. **Verify permissions**: Ensure the script has execute permissions

**Required Project Structure:**
```
project-root/
├── run-jmeter.sh                          # The performance script
├── src/test/resources/jmeter/
│   └── load-test.jmx                      # JMeter test plan
└── target/                                # Generated reports (auto-created)
├── jmeter-results.jtl                 # Raw results
├── jmeter-report/                     # HTML dashboard
│   └── index.html                     # Main report
└── jmeter.log                         # Execution log
```

### Step 2: Copy Template Content

Copy the entire script from the packaged resource:
[run-jmeter.sh](../scripts/run-jmeter.sh).

#### Step Constraints

- **DO NOT** create your own version of the performance script
- **DO NOT** modify the template logic, structure, or features
- **DO NOT** add enhancements or interpretations
- **ONLY** use the exact template provided
- **ONLY** copy the script verbatim without any changes

### Step 3: Provide Usage Information

**Basic Usage:**
```bash
# Run with default settings (1000 loops, 1 thread, 1s ramp-up)
./run-jmeter.sh

# Custom configuration
./run-jmeter.sh -l 500 -t 10 -r 30

# Open JMeter GUI
./run-jmeter.sh -g
```

**Available Options:**
- `-l, --loops LOOPS`: Number of loops per thread (default: 1000)
- `-t, --threads THREADS`: Number of concurrent threads (default: 1)
- `-r, --ramp-up SECONDS`: Ramp-up period in seconds (default: 1)
- `-g, --gui`: Open JMeter GUI instead of running in non-GUI mode
- `-h, --help`: Show detailed help message

**Environment Variables:**
```bash
# Override defaults using environment variables
JMETER_LOOPS=500 JMETER_THREADS=5 ./run-jmeter.sh

# Or export them
export JMETER_LOOPS=1000
export JMETER_THREADS=10
export JMETER_RAMP_UP=30
./run-jmeter.sh
```

**Example Scenarios:**
```bash
# Light load test
./run-jmeter.sh -l 100 -t 1 -r 1

# Medium load test
./run-jmeter.sh -l 500 -t 5 -r 10

# Heavy load test
./run-jmeter.sh -l 1000 -t 20 -r 60

# Quick GUI setup
./run-jmeter.sh -g
```


## Output Format

- Copy the performance script template exactly as specified
- Create `run-jmeter.sh` file in the project root
- Make the script executable with `chmod +x run-jmeter.sh`
- **VERIFY**: Final script contains ONLY what appears in the template


## Safeguards

- **PREREQUISITE CHECK**: Verify JMeter is installed and accessible via `jmeter --version` before creating the script
- **SCRIPT CREATION SAFETY**: Ensure `run-jmeter.sh` script is created with exactly the template content without modifications
- **PERMISSION VALIDATION**: Verify the script is executable with `chmod +x run-jmeter.sh` and confirm permissions with `ls -la run-jmeter.sh`
- **FUNCTIONALITY TEST**: Verify the script is working correctly with basic usage test: `./run-jmeter.sh -h`
- **PROJECT STRUCTURE VERIFICATION**: Confirm the expected directory structure exists (`src/test/resources/jmeter/`) or will be created by the script
- **TEMPLATE INTEGRITY**: Ensure the copied template content matches the packaged `scripts/run-jmeter.sh` resource without any alterations
- **DEPENDENCY VALIDATION**: Verify that all required directories and paths referenced in the script will be accessible and writable
- **EXECUTION SAFETY**: Test script execution in a safe environment before providing it to the user for production use