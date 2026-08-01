### Known plugin catalog

Use this catalog as a reference when generating goal tables.
Only include a plugin subsection if the plugin appears in the project `pom.xml`.

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
