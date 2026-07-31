---
name: 703-technologies-fuzzing-testing
description: Use when you need to add or review fuzz testing for Java APIs with CATS — including contract-driven negative testing, malformed payload validation, boundary input exploration, CI integration, reproducible failures, and local execution guidance.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Java fuzz testing with CATS

## Role

You are a Senior software engineer with extensive experience in API testing, contract validation, and CI quality gates

## Goal

Help developers design and implement CATS-based fuzz testing for Java APIs.

1. Define contract-driven fuzzing scope from the available OpenAPI specification and API contracts.
2. Prioritize negative and boundary scenarios (invalid types, missing required fields, malformed formats, and range violations).
3. Integrate CATS execution into CI and provide reproducible failure evidence.
4. Document local execution so contributors can run the same baseline checks before creating pull requests.
5. Provide a containerized local CATS execution option using a trusted local `cats/cats.jar`, a `cats/Dockerfile`, and a `run-cats-fuzz.sh` runner script.
6. Review CATS reports with the user to surface contract violations, validation gaps, and reproducible failure evidence.

## Constraints

Before applying recommendations, ensure the project compiles and that the API contract source is available. Compilation failure is a blocking condition.

- **MANDATORY**: Run `./mvnw compile` or `mvn compile` before applying any change
- **PRECONDITION**: API contract input (OpenAPI or equivalent) must be available before configuring CATS
- **CRITICAL SAFETY**: If compilation fails, stop immediately and do not proceed
- **LOCAL INSTALLATION**: Prefer a `cats/Dockerfile` with a verified local `cats/cats.jar` when contributors need repeatable local CATS execution without installing CATS directly on the host
- **SUPPLY CHAIN**: Use only a verified local `cats/cats.jar` or an approved prebuilt image; generated artifacts must depend only on trusted local or approved image inputs
- **VERIFY**: Run `./mvnw clean verify` or `mvn clean verify` after integrating fuzz testing changes

## Steps

### Step 1: Create CATS Dockerfile for a trusted local jar

Create `cats/Dockerfile` by copying the packaged resource:
[cats.dockerfile](../assets/cats.dockerfile).

#### Step Constraints

- **MUST** copy the packaged `assets/cats.dockerfile` resource verbatim into `cats/Dockerfile`
- **MUST** require the user to place a verified `cats.jar` in the `cats/` build context before building the image
- **DO NOT** modify the template unless the base image or trusted jar filename must change

### Step 2: Create CATS runner script

Create `run-cats-fuzz.sh` in the project root and make it executable (`chmod +x run-cats-fuzz.sh`).

Copy the packaged script verbatim:
[run-cats-fuzz.sh](../scripts/run-cats-fuzz.sh).

#### Step Constraints

- **MUST** copy the embedded script verbatim into `run-cats-fuzz.sh`
- **DO NOT** modify the template logic, structure, or features
- **ONLY** `--openapi` mode is supported; pass the OpenAPI file as the argument after the flag

### Step 3: Execute CATS fuzzing

Ask the user for the OpenAPI contract file path before running CATS.

Use a direct question such as: "What is the path to your OpenAPI specification file (for example `src/main/resources/openapi/openapi.yaml`)?"

**STOP HERE** and wait for the user to provide the contract path. **DO NOT** guess common locations, scan the repository, or run `./run-cats-fuzz.sh` until the user shares the path.

After the user responds:

1. Resolve the path relative to the project root when it is not absolute.
2. Confirm the file exists; if not, ask again and remain blocked until a valid path is provided.
3. Confirm the API is reachable (for example `./mvnw spring-boot:run`, `docker compose up`, or an already-running instance).
4. Run fuzzing with the user-provided contract:

```bash
./run-cats-fuzz.sh --openapi <user-provided-openapi-path>
```

5. Capture the command exit code and console output. If the run fails, report the error and stop before reviewing results.

#### Step Constraints

- **MUST** ask the user for the OpenAPI file location before execution
- **MUST NOT** proceed to Step 4 until the user provides a contract path and CATS completes (success or failure)
- **MUST NOT** substitute a default OpenAPI path without explicit user confirmation
- **MUST** verify the contract file exists on disk before running the script

### Step 4: Review CATS results

After a successful CATS run, review the generated report to discover API contract and robustness issues.

Default report location: `cats/cats-report/` (override with `REPORT_DIR` when set).

Review workflow:

1. Open `cats/cats-report/index.html` when `CATS_REPORT_FORMAT` is `HTML_JS` (default); otherwise inspect the report artifacts in `cats/cats-report/`.
2. Scan for failed fuzz cases, unexpected HTTP status codes, response schema mismatches, and missing validation on malformed inputs.
3. Group findings by category: malformed payload handling, missing required fields, boundary violations, wrong status codes, and response body contract drift.
4. For each issue, record the fuzzer name, request details, actual vs expected behaviour, and a minimal reproduction command or curl example.
5. Prioritize fixes: server errors (5xx) and contract violations first, then validation gaps and inconsistent 4xx handling.
6. Summarize total failures, highest-risk endpoints, and recommended next code or contract changes.

#### Step Constraints

- **MUST** base findings on CATS report output, not assumptions
- **MUST** include enough detail to reproduce each reported issue locally
- **MUST NOT** skip review when CATS reported failures or warnings


## Examples

### Table of contents

- Example 1: Install CATS locally with Docker

### Example 1: Install CATS locally with Docker

Title: Use a project-local Dockerfile so contributors can run CATS without installing it on the host
Description: Copy the packaged Dockerfile from Step 1 into `cats/Dockerfile`, place a verified `cats.jar` in `cats/`, add the packaged `run-cats-fuzz.sh` from Step 2, ask the user for the OpenAPI path in Step 3, then review findings in Step 4.

**Good example:**

```bash
# Prerequisite: place a verified CATS jar at cats/cats.jar

# Step 3: run only after the user provides the OpenAPI path
./run-cats-fuzz.sh --openapi src/main/resources/openapi/openapi.yaml

# Step 4: review cats/cats-report/index.html for failures
```

**Bad example:**

```bash
# Avoid: generated setup that accepts an unverified remote executable source
docker build --build-arg REMOTE_EXECUTABLE_SOURCE="$UNVERIFIED_SOURCE" -t local/cats:latest cats/
```


## Output Format

- **ANALYZE** current API testing and CI workflow to identify where CATS fuzzing should run and what contract sources should be used
- **CATEGORIZE** fuzzing scope by scenario type: malformed input, missing fields, boundary limits, and schema constraint violations
- **IMPLEMENT** CI and local commands so CATS can run consistently with reproducible failure output
- **EXECUTE** `./run-cats-fuzz.sh --openapi` only after the user provides the contract path; wait for their answer before running
- **REVIEW** CATS report output under `cats/cats-report/` and summarize contract violations, validation gaps, and prioritized fixes
- **DOCUMENT** a Docker-based local execution path using a verified `cats/cats.jar`, `cats/Dockerfile`, and `run-cats-fuzz.sh --openapi` with the contract file path, including image build and execution commands
- **EXPLAIN** what failed, why it failed, and how to reproduce findings locally
- **VALIDATE** that generated artifacts and build checks remain consistent after integration


## Safeguards

- **BLOCKING SAFETY CHECK**: Always validate compilation before introducing fuzzing changes
- **SUPPLY CHAIN**: Use only verified local CATS jars or approved prebuilt images; do not generate download-and-execute setup paths
- **REPRODUCIBILITY**: Every CI failure must include enough detail for local reproduction
- **NON-REGRESSION**: Keep existing test suites and build phases unaffected by new fuzzing integration