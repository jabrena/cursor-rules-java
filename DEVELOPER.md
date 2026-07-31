# Developer commands

## Essential maven commands

```bash
# Analyze dependencies
./mvnw dependency:tree
./mvnw dependency:analyze
./mvnw dependency:resolve

./mvnw clean validate -U
./mvnw buildplan:list-plugin
./mvnw buildplan:list-phase
./mvnw help:all-profiles
./mvnw help:active-profiles
./mvnw license:third-party-report

# Clean the project
./mvnw clean

# Run unit tests
./mvnw clean test

# Run integration tests
./mvnw clean verify

# Clean and package in one command
./mvnw clean package

# Check for dependency updates
./mvnw versions:display-property-updates
./mvnw versions:display-dependency-updates
./mvnw versions:display-plugin-updates

# Generate project reports
./mvnw site
jwebserver -p 8005 -d "$(pwd)/target/site/"

# Run blog in local
./mvnw clean generate-resources jbake:inline -pl site-generator -P local-preview

# Promote blog changes to Prod
./mvnw clean generate-resources -pl site-generator -P site-update
```

## Submodules

This is a multi-module project. The following modules are declared in the root `pom.xml`.

| Module | Artifact ID | Commands | Description |
|--------|-------------|----------|-------------|
| markdown-validator | markdown-validator | `./mvnw clean verify -pl markdown-validator`<br>`jbang markdown-validator/src/main/java/info/jab/mv/MarkdownValidator.java .` | Maven-owned Markdown validator used by contributors and CI. The JBang-compatible entrypoint lives in this module. |
| plinth-commands-generator | plinth-commands-generator | `./mvnw clean verify -pl plinth-commands-generator` | Owns embedded command inventory (`commands.xml`), command markdown assets, loader code, and command-focused tests. |
| plinth-agents-generator | plinth-agents-generator | `./mvnw clean verify -pl plinth-agents-generator` | Owns embedded agent inventory (`agents.xml`), agent markdown assets, loader code, and agent-focused tests. |
| plinth-skills-generator | plinth-skills-generator | `./mvnw clean verify -pl plinth-skills-generator -am`<br>`./mvnw clean install -pl plinth-skills-generator -am`<br>`./mvnw clean install -pl plinth-skills-generator -am -P release` | Unified XML → skills generator: bridges command assets from `plinth-commands-generator` and agent assets from `plinth-agents-generator`, produces agent skills into `plinth-skills-generator/target/skills`, copies local output into `.agents/skills`, and refreshes `skills/` only through the explicit `release` profile. |
| site-generator | site | `./mvnw clean verify -pl site-generator`<br>`./mvnw clean generate-resources jbake:inline -pl site-generator -P local-preview`<br>`./mvnw clean generate-resources -pl site-generator -P site-update` | JBake-based static site generator for documentation and GitHub Pages. |

## Maven Profiles

The following profiles are declared in this project. Activate them with `-P <profileId>`.

| Profile ID | Command | Activation | Description |
|------------|---------|------------|-------------|
| snyk | `SNYK_TOKEN=<token> ./mvnw clean verify -P snyk` | manual | Runs the Snyk Maven plugin once at the reactor root to scan all module dependencies for vulnerabilities. |
| security | `./mvnw clean verify -P security` | manual | Runs OWASP dependency-check-maven to scan for known vulnerabilities; fails on CVSS ≥ 7. |
| find-bugs | `./mvnw clean verify -P find-bugs` | manual | Runs PMD and SpotBugs static analysis with max effort and low threshold. |
| release | `./mvnw clean install -pl plinth-skills-generator -P release` | manual | Cleans and refreshes the public `skills/` release output instead of copying generated skills to `.agents/skills`. |
| local-preview | `./mvnw clean generate-resources jbake:inline -pl site-generator -P local-preview` | manual | Generates site to `target/docs-local` and serves it locally (site-generator). |
| site-update | `./mvnw clean generate-resources -pl site-generator -P site-update` | manual | Regenerates the static site into `docs/` for GitHub Pages (site-generator). |

## Markdown Validator Notes

The `markdown-validator` module intentionally enables Java 25 preview features
because its validation runner uses structured concurrency. Keep the Maven
compiler, Surefire, and JBang `--enable-preview` flags aligned when changing
that module entrypoint.

The module follows a small Onion architecture package layout:
`domain` holds validation records, `application` holds use cases and policies,
`application.port` defines outbound contracts, and `adapter.in` / `adapter.out`
contain CLI, filesystem, and HTTP details.

## Plugin Goals Reference

The following sections list useful goals for each plugin configured in this project's pom.xml.

### maven-compiler-plugin

| Goal | Description |
|------|-------------|
| `./mvnw compiler:compile` | Compile main source files |
| `./mvnw compiler:testCompile` | Compile test source files |

### maven-surefire-plugin

| Goal | Description |
|------|-------------|
| `./mvnw surefire:test` | Run unit tests |
| `./mvnw surefire:help` | Display help information |

### maven-resources-plugin

| Goal | Description |
|------|-------------|
| `./mvnw resources:resources` | Copy main resources to output directory |
| `./mvnw resources:testResources` | Copy test resources to output directory |
| `./mvnw resources:copy-resources@copy-skills -pl plinth-skills-generator` | Copy already generated skills from `plinth-skills-generator/target/skills` into `.agents/skills` for local agent use |

### maven-antrun-plugin

The `plinth-skills-generator` module uses this plugin inside the `release` profile to
clean `skills/` before the install-phase `copy-skills` execution refreshes the
public release output.

### maven-clean-plugin

| Goal | Description |
|------|-------------|
| `./mvnw clean:clean` | Delete the build output directory |

### jbake-maven-plugin

| Goal | Description |
|------|-------------|
| `./mvnw jbake:generate` | Generate static site with JBake |
| `./mvnw jbake:inline` | Generate and serve site locally |

### maven-enforcer-plugin

| Goal | Description |
|------|-------------|
| `./mvnw enforcer:enforce` | Execute enforcer rules |
| `./mvnw enforcer:display-info` | Display current platform information |

### spotless-maven-plugin

| Goal | Description |
|------|-------------|
| `./mvnw spotless:apply` | Apply formatting fixes |
| `./mvnw spotless:check` | Check formatting and fail on violations |

### versions-maven-plugin

| Goal | Description |
|------|-------------|
| `./mvnw versions:display-dependency-updates` | Show available dependency updates |
| `./mvnw versions:display-plugin-updates` | Show available plugin updates |
| `./mvnw versions:display-property-updates` | Show available property updates |
| `./mvnw versions:use-latest-releases` | Update dependencies to latest releases |
| `./mvnw versions:set -DnewVersion=X.Y.Z` | Set the project version |

### spotbugs-maven-plugin

| Goal | Description |
|------|-------------|
| `./mvnw spotbugs:check` | Run SpotBugs analysis and fail on bugs |
| `./mvnw spotbugs:spotbugs` | Generate a SpotBugs report |
| `./mvnw spotbugs:gui` | Launch the SpotBugs GUI |

### maven-pmd-plugin

| Goal | Description |
|------|-------------|
| `./mvnw pmd:check` | Run PMD analysis and fail on violations |
| `./mvnw pmd:pmd` | Generate a PMD report |
| `./mvnw pmd:cpd-check` | Run copy-paste detection and fail on duplicates |

### dependency-check-maven

| Goal | Description |
|------|-------------|
| `./mvnw dependency-check:check` | Scan dependencies for known vulnerabilities |
| `./mvnw dependency-check:aggregate` | Aggregate scan for multi-module projects |

### snyk-maven-plugin

| Goal | Description |
|------|-------------|
| `SNYK_TOKEN=<token> ./mvnw snyk:test -P snyk` | Scan all module dependencies for known vulnerabilities |
