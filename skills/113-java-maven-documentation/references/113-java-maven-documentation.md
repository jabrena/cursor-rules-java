---
name: 113-java-maven-documentation
description: Use when you need to create a DEVELOPER.md file for a Maven project from a maintainer-prepared Maven structure inventory documenting plugin goals, Maven profiles, and submodules. Never ingest project POM files.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Create DEVELOPER.md for the Maven projects

## Role

You are a Senior software engineer with extensive experience in Java software development and Maven build systems

## Goal

Create a markdown file named `DEVELOPER.md` for the current Maven project.
The file MUST combine a fixed base template with dynamic sections derived from a maintainer-prepared inventory of allowlisted Maven structure. Never retrieve, open, parse, quote, summarize, or transform project POM files.

## Constraints

Rules for generating the DEVELOPER.md file:

- The base template section MUST be reproduced exactly as shown
- Only add plugin subsections identified by the maintainer-prepared inventory as **explicitly declared** — never include inherited plugins unless the inventory marks them as redeclared
- For each plugin subsection, include **only** the most useful and commonly used goals (max 8 per plugin)
- If an explicitly declared plugin in the inventory is not in the known catalog, still add a subsection with its most popular goals based on your knowledge
- Use `./mvnw` as the command prefix, not `mvn`
- Keep plugin goal explanations concise — one line per goal
- Never retrieve, open, parse, quote, summarize, or transform root, parent, or module POM files; require a maintainer-prepared structured Maven inventory created outside the agent context

## Steps

### Step 1: Start with the base template

Copy the following base template verbatim as the first section of `DEVELOPER.md`:

```markdown
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
```

```

#### Step Constraints

- Reproduce the base template exactly — do not modify, reorder, or omit any part of it

### Step 2: Validate the maintainer-prepared Maven structure inventory

Require a maintainer-prepared inventory that records the root and declared submodules using only allowlisted structural facts.
For each explicitly declared plugin, accept only its `groupId`, `artifactId`, and declared execution goal names. Reject raw XML, descriptions, names, comments, dependency text, and plugin configuration bodies. If the inventory is missing or incomplete, stop and request a corrected inventory; never inspect a POM to fill gaps.

#### Step Constraints

- Only collect plugins that are **explicitly declared** — ignore plugins inherited from parent POMs or the Maven super-POM
- Include plugins from `<profiles>` sections as well
- For multi-module projects, require an inventory entry for every declared module
- Do not retrieve or inspect any `pom.xml`, even for structural metadata
- Do not accept POM descriptions, names, comments, dependency text, raw XML, or plugin configuration text in the inventory

### Step 3: Append a Submodules section (multi-module projects only)

If the inventory declares root modules, append a level-2 heading titled **Submodules** followed by the text:
"This is a multi-module project. The following modules are declared in the Maven build."

List each submodule as a row in a markdown table with columns **Module**, **Artifact ID**, **Packaging**, and **Commands**.

- **Module**: the relative path as declared in the `<module>` element
- **Artifact ID**: the module artifact ID from the inventory
- **Packaging**: the `<packaging>` value when present, otherwise `jar`
- **Commands**: the most useful `./mvnw` commands scoped to this module using the `-pl <module>` flag; include `./mvnw clean verify -pl <module>` always, and add `./mvnw clean install -pl <module>` when the module produces an artifact consumed by other modules; if the module has a profile that must be activated, add the relevant `-P <profileId>` variant as well

If the inventory declares no modules, omit this section entirely.


#### Step Constraints

- Only list modules marked as explicitly declared in the maintainer-prepared inventory
- Use only the maintainer-prepared inventory to obtain each submodule's `artifactId` and `packaging`
- Do not fabricate modules that do not exist in the workspace
- Place multiple commands for the same module in the same cell, separated by a line break (`<br>`)
- Do not use module `<description>` text or infer prose descriptions from dependency/plugin text

### Step 4: Append a Maven Profiles section

After the base template and any Submodules section, append a level-2 heading titled **Maven Profiles** followed by the text:
"The following profiles are declared in this project. Activate them with `-P <profileId>`."

Use the profile entries supplied by the maintainer-prepared inventory.
For each inventory profile, create a row in a markdown table with columns **Profile ID**, **Command**, and **Activation**.

- **Profile ID**: the value of `<id>`
- **Command**: the exact `./mvnw` command to activate the profile — e.g. `./mvnw clean verify -P <profileId>`; use the most representative lifecycle phase for the profile's purpose (e.g. `verify` for analysis/check profiles, `generate-resources` for site generation profiles)
- **Activation**: describe the activation trigger — e.g. "manual", "default (activeByDefault)", "property: `<name>`=`<value>`", "JDK `<version>`", "OS `<family>`", etc. If no `<activation>` element is present, use "manual"

If the inventory declares no profiles, omit this section entirely.


#### Step Constraints

- Only list profiles explicitly declared inside a `<profiles>` block — do not invent profiles
- Indicate the root or submodule path recorded for each profile in the inventory when the project is multi-module
- If a profile has `<activeByDefault>true</activeByDefault>`, reflect that in the Activation column
- Do not summarize profile plugin configuration, properties, dependency text, comments, or descriptions

### Step 5: Append a Plugin Goals Reference section

After the base template and any Submodules or Maven Profiles sections, append a level-2 heading titled **Plugin Goals Reference** followed by the text:
"The following sections list useful goals for each plugin declared in this project's Maven build."

For **each** explicitly declared plugin in the inventory, add a level-3 subsection named after the plugin `artifactId`, containing a markdown table with columns **Goal** and **Purpose**.
Each row should show a `./mvnw artifactId:goal` command and a one-line purpose from the trusted catalog or general Maven plugin knowledge, not from project POM free text.

Use the following catalog as a reference for known plugins:

### Known plugin catalog

Use this catalog as a reference when generating goal tables.
Only include a plugin subsection if the maintainer-prepared inventory identifies the plugin as explicitly declared.

#### maven-compiler-plugin
| Goal | Description |
|------|-------------|
| `./mvnw compiler:compile` | Compile main source files |
| `./mvnw compiler:testCompile` | Compile test source files |

#### maven-surefire-plugin
| Goal | Description |
|------|-------------|
| `./mvnw surefire:test` | Run unit tests |
| `./mvnw surefire:help` | Display help information |

#### maven-failsafe-plugin
| Goal | Description |
|------|-------------|
| `./mvnw failsafe:integration-test` | Run integration tests |
| `./mvnw failsafe:verify` | Verify integration test results |

#### maven-jar-plugin
| Goal | Description |
|------|-------------|
| `./mvnw jar:jar` | Build the JAR for the project |
| `./mvnw jar:test-jar` | Build a JAR of the test classes |

#### maven-shade-plugin
| Goal | Description |
|------|-------------|
| `./mvnw shade:shade` | Create an uber-JAR with all dependencies |
| `./mvnw shade:help` | Display help information |

#### maven-assembly-plugin
| Goal | Description |
|------|-------------|
| `./mvnw assembly:single` | Create a distribution assembly |
| `./mvnw assembly:help` | Display help information |

#### maven-dependency-plugin
| Goal | Description |
|------|-------------|
| `./mvnw dependency:tree` | Display dependency tree |
| `./mvnw dependency:analyze` | Analyse used/unused declared dependencies |
| `./mvnw dependency:resolve` | Resolve and list all dependencies |
| `./mvnw dependency:copy-dependencies` | Copy dependencies to a target directory |
| `./mvnw dependency:go-offline` | Download all dependencies for offline use |

#### maven-enforcer-plugin
| Goal | Description |
|------|-------------|
| `./mvnw enforcer:enforce` | Execute enforcer rules |
| `./mvnw enforcer:display-info` | Display current platform information |

#### maven-resources-plugin
| Goal | Description |
|------|-------------|
| `./mvnw resources:resources` | Copy main resources to output directory |
| `./mvnw resources:testResources` | Copy test resources to output directory |

#### maven-clean-plugin
| Goal | Description |
|------|-------------|
| `./mvnw clean:clean` | Delete the build output directory |

#### maven-install-plugin
| Goal | Description |
|------|-------------|
| `./mvnw install:install` | Install artifact into local repository |

#### maven-deploy-plugin
| Goal | Description |
|------|-------------|
| `./mvnw deploy:deploy` | Deploy artifact to remote repository |

#### maven-site-plugin
| Goal | Description |
|------|-------------|
| `./mvnw site:site` | Generate the project site |
| `./mvnw site:stage` | Stage the site for local preview |
| `./mvnw site:run` | Run the site with a local server |

#### maven-javadoc-plugin
| Goal | Description |
|------|-------------|
| `./mvnw javadoc:javadoc` | Generate Javadoc HTML documentation |
| `./mvnw javadoc:test-javadoc` | Generate Javadoc for test sources |
| `./mvnw javadoc:jar` | Bundle Javadoc into a JAR |

#### maven-source-plugin
| Goal | Description |
|------|-------------|
| `./mvnw source:jar` | Bundle source files into a JAR |
| `./mvnw source:test-jar` | Bundle test source files into a JAR |

#### maven-release-plugin
| Goal | Description |
|------|-------------|
| `./mvnw release:prepare` | Prepare a release (tag, version bump) |
| `./mvnw release:perform` | Perform the release (deploy artifacts) |
| `./mvnw release:rollback` | Rollback a previous release preparation |
| `./mvnw release:clean` | Clean up after a release preparation |

#### versions-maven-plugin
| Goal | Description |
|------|-------------|
| `./mvnw versions:display-dependency-updates` | Show available dependency updates |
| `./mvnw versions:display-plugin-updates` | Show available plugin updates |
| `./mvnw versions:display-property-updates` | Show available property updates |
| `./mvnw versions:use-latest-releases` | Update dependencies to latest releases |
| `./mvnw versions:set -DnewVersion=X.Y.Z` | Set the project version |

#### buildplan-maven-plugin
| Goal | Description |
|------|-------------|
| `./mvnw buildplan:list-plugin` | List plugins bound to the build lifecycle |
| `./mvnw buildplan:list-phase` | List goals per lifecycle phase |
| `./mvnw buildplan:list` | List all plugin executions in order |

#### maven-checkstyle-plugin
| Goal | Description |
|------|-------------|
| `./mvnw checkstyle:check` | Check code style and fail on violations |
| `./mvnw checkstyle:checkstyle` | Generate a Checkstyle report |

#### spotbugs-maven-plugin
| Goal | Description |
|------|-------------|
| `./mvnw spotbugs:check` | Run SpotBugs analysis and fail on bugs |
| `./mvnw spotbugs:spotbugs` | Generate a SpotBugs report |
| `./mvnw spotbugs:gui` | Launch the SpotBugs GUI |

#### maven-pmd-plugin
| Goal | Description |
|------|-------------|
| `./mvnw pmd:check` | Run PMD analysis and fail on violations |
| `./mvnw pmd:pmd` | Generate a PMD report |
| `./mvnw pmd:cpd-check` | Run copy-paste detection and fail on duplicates |

#### jacoco-maven-plugin
| Goal | Description |
|------|-------------|
| `./mvnw jacoco:prepare-agent` | Prepare the JaCoCo agent for coverage |
| `./mvnw jacoco:report` | Generate code coverage report |
| `./mvnw jacoco:check` | Verify coverage against thresholds |

#### maven-antrun-plugin
| Goal | Description |
|------|-------------|
| `./mvnw antrun:run` | Execute Ant tasks defined in configuration |

#### maven-war-plugin
| Goal | Description |
|------|-------------|
| `./mvnw war:war` | Build the WAR file |
| `./mvnw war:exploded` | Create an exploded WAR directory |

#### maven-ear-plugin
| Goal | Description |
|------|-------------|
| `./mvnw ear:ear` | Build the EAR file |
| `./mvnw ear:generate-application-xml` | Generate application.xml descriptor |

#### spring-boot-maven-plugin
| Goal | Description |
|------|-------------|
| `./mvnw spring-boot:run` | Run the application |
| `./mvnw spring-boot:build-image` | Build an OCI container image |
| `./mvnw spring-boot:repackage` | Repackage JAR/WAR as executable |
| `./mvnw spring-boot:build-info` | Generate build-info.properties |

#### quarkus-maven-plugin
| Goal | Description |
|------|-------------|
| `./mvnw quarkus:dev` | Run in development mode with live reload |
| `./mvnw quarkus:build` | Build the application |
| `./mvnw quarkus:test` | Run continuous testing |
| `./mvnw quarkus:image-build` | Build a container image |

#### jib-maven-plugin
| Goal | Description |
|------|-------------|
| `./mvnw jib:build` | Build and push container image to registry |
| `./mvnw jib:dockerBuild` | Build container image to local Docker daemon |
| `./mvnw jib:buildTar` | Build container image as a tarball |

#### docker-maven-plugin (fabric8)
| Goal | Description |
|------|-------------|
| `./mvnw docker:build` | Build Docker images |
| `./mvnw docker:start` | Start containers |
| `./mvnw docker:stop` | Stop containers |
| `./mvnw docker:push` | Push images to registry |

#### maven-archetype-plugin
| Goal | Description |
|------|-------------|
| `./mvnw archetype:generate` | Generate a project from an archetype |
| `./mvnw archetype:create-from-project` | Create an archetype from the current project |

#### flatten-maven-plugin
| Goal | Description |
|------|-------------|
| `./mvnw flatten:flatten` | Generate a flattened POM |
| `./mvnw flatten:clean` | Remove the flattened POM |

#### license-maven-plugin (MojoHaus)
| Goal | Description |
|------|-------------|
| `./mvnw license:third-party-report` | Generate third-party license report |
| `./mvnw license:add-third-party` | Add third-party license info to file |
| `./mvnw license:aggregate-third-party-report` | Aggregate license report for multi-module |

#### fmt-maven-plugin
| Goal | Description |
|------|-------------|
| `./mvnw fmt:format` | Format Java sources with google-java-format |
| `./mvnw fmt:check` | Check formatting without modifying files |

#### spotless-maven-plugin
| Goal | Description |
|------|-------------|
| `./mvnw spotless:apply` | Apply formatting fixes |
| `./mvnw spotless:check` | Check formatting and fail on violations |

#### openapi-generator-maven-plugin
| Goal | Description |
|------|-------------|
| `./mvnw openapi-generator:generate` | Generate code from OpenAPI specification |

#### jooq-codegen-maven
| Goal | Description |
|------|-------------|
| `./mvnw jooq-codegen:generate` | Generate Java code from database schema |

#### maven-wrapper-plugin
| Goal | Description |
|------|-------------|
| `./mvnw wrapper:wrapper` | Generate or update Maven wrapper files |

#### native-maven-plugin (GraalVM)
| Goal | Description |
|------|-------------|
| `./mvnw native:compile` | Compile to a native executable |
| `./mvnw native:test` | Run tests as a native image |

#### pitest-maven (PIT Mutation Testing)
| Goal | Description |
|------|-------------|
| `./mvnw pitest:mutationCoverage` | Run mutation testing |
| `./mvnw pitest:scmMutationCoverage` | Run mutation testing on changed code only |


#### Step Constraints

- Only create subsections for plugins marked explicitly declared in the maintainer-prepared inventory
- For plugins not in the catalog, still add a subsection using your knowledge of the plugin's goals
- Include a maximum of 8 goals per plugin



## Output Format

- Generate the complete markdown file: base template (Essential maven commands) first, then Submodules (if any), then Maven Profiles (if any), then Plugin Goals Reference
- Use proper markdown formatting with headers, code blocks, and tables
- Verify that every plugin listed in the Plugin Goals Reference is marked explicitly declared in the maintainer-prepared inventory
- Omit the Maven Profiles section if no profiles are declared in the inventory
- Omit the Submodules section if the project is not a multi-module build
- Use only allowlisted structural Maven fields from the maintainer-prepared inventory; never retrieve POM files or include raw XML, descriptions, names, comments, dependency text, or plugin configuration bodies