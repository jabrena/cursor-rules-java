# Java Maven Plugins

This template provides Maven plugin configurations that should be added conditionally based on user selections from the main Maven dependencies and plugins rule.

**Usage**: Reference this template from the main rule rather than duplicating configurations.

## Core Plugins

### Add maven-plugin-enforcer

Nature: General
Category: Build

Update the pom.xml with this new plugin:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-enforcer-plugin</artifactId>
    <version>${maven-plugin-enforcer.version}</version>
    <dependencies>
        <dependency>
            <groupId>org.codehaus.mojo</groupId>
            <artifactId>extra-enforcer-rules</artifactId>
            <version>${extra-enforcer-rules.version}</version>
        </dependency>
    </dependencies>
    <executions>
        <execution>
            <id>enforce</id>
            <configuration>
                <rules>
                    <banCircularDependencies/>
                    <dependencyConvergence />
                    <banDuplicatePomDependencyVersions />
                    <requireMavenVersion>
                        <version>${maven.version}</version>
                    </requireMavenVersion>
                    <requireJavaVersion>
                        <version>${java.version}</version>
                    </requireJavaVersion>
                    <bannedDependencies>
                        <excludes>
                            <exclude>org.projectlombok:lombok</exclude>
                        </excludes>
                    </bannedDependencies>
                </rules>
                <fail>true</fail>
            </configuration>
            <goals>
                <goal>enforce</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

### Add maven-plugin-compiler (Enhanced)

**When to use**: Always included, but ask about enhancement level.
**User question**: "Do you want enhanced code analysis with Error Prone and NullAway? (y/n)"

Nature: General
Category: Build

**If enhanced analysis selected**, create/update the file $(pwd)/.mvn/jvm.config with the following content:

```txt
--add-exports jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED
--add-exports jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED
--add-exports jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED
--add-exports jdk.compiler/com.sun.tools.javac.model=ALL-UNNAMED
--add-exports jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED
--add-exports jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED
--add-exports jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED
--add-exports jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED
--add-opens jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED
--add-opens jdk.compiler/com.sun.tools.javac.comp=ALL-UNNAMED
```

Update the pom.xml with this new plugin:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>${maven-plugin-compiler.version}</version>
    <configuration>
        <release>${java.version}</release>
        <compilerArgs>
            <arg>-Xlint:all</arg>
            <arg>-Werror</arg>
            <!-- Error prone settings-->
            <arg>-XDcompilePolicy=simple</arg>
            <arg>--should-stop=ifError=FLOW</arg>
            <arg>-Xplugin:ErrorProne \
                -Xep:NullAway:ERROR \
                -XepOpt:NullAway:JSpecifyMode=true \
                -XepOpt:NullAway:TreatGeneratedAsUnannotated=true \
                -XepOpt:NullAway:CheckOptionalEmptiness=true \
                -XepOpt:NullAway:HandleTestAssertionLibraries=true \
                -XepOpt:NullAway:AssertsEnabled=true \
                -XepOpt:NullAway:AnnotatedPackages=info.jab.cli
            </arg>
        </compilerArgs>
        <annotationProcessorPaths>
            <path>
                <groupId>com.google.errorprone</groupId>
                <artifactId>error_prone_core</artifactId>
                <version>${error-prone.version}</version>
            </path>
            <path>
                <groupId>com.uber.nullaway</groupId>
                <artifactId>nullaway</artifactId>
                <version>${nullaway.version}</version>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
```

## Optional Build Enhancement Plugins

### Add spotless-maven-plugin

Nature: General
Category: Build

Update the pom.xml with this plugin:

```xml
<plugin>
    <groupId>com.diffplug.spotless</groupId>
    <artifactId>spotless-maven-plugin</artifactId>
    <version>${maven-plugin-spotless.version}</version>
    <configuration>
        <encoding>UTF-8</encoding>
        <java>
            <removeUnusedImports />
            <importOrder>
                <order>,\#</order>
            </importOrder>
            <endWithNewline />
            <trimTrailingWhitespace />
            <indent>
                <spaces>true</spaces>
                <spacesPerTab>4</spacesPerTab>
            </indent>
        </java>
    </configuration>
    <executions>
        <execution>
            <goals>
                <goal>check</goal>
            </goals>
            <phase>process-sources</phase>
        </execution>
    </executions>
</plugin>
```

### Add flatten-maven-plugin

**When to use**: For library projects that will be published to Maven repositories.
**User question**: "Are you building a library that will be published to Maven repositories? (y/n)"

Nature: Libraries
Category: Build

Update the pom.xml with this plugin:

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>flatten-maven-plugin</artifactId>
    <version>${maven-plugin-flatten.version}</version>
    <configuration>
    </configuration>
    <executions>
        <execution>
            <id>flatten</id>
            <phase>process-resources</phase>
            <goals>
                <goal>flatten</goal>
            </goals>
        </execution>
        <execution>
            <id>flatten.clean</id>
            <phase>clean</phase>
            <goals>
                <goal>clean</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

### Add versions-maven-plugin

**When to use**: For projects that want to manage dependency versions systematically.
**User question**: "Do you want tools to help manage and update dependency versions? (y/n)"

Nature: General
Category: Build

Update the pom.xml with this plugin:

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>versions-maven-plugin</artifactId>
    <version>${maven-plugin-versions.version}</version>
    <configuration>
        <allowSnapshots>false</allowSnapshots>
    </configuration>
</plugin>
```

### Add git-commit-id-plugin

**When to use**: For applications that need build information (version, commit, etc.) at runtime.
**User question**: "Do you want to include Git commit information in your build? (y/n)"

Nature: General
Category: Build

Update the pom.xml with this plugin:

```xml
<plugin>
    <groupId>pl.project13.maven</groupId>
    <artifactId>git-commit-id-plugin</artifactId>
    <version>${maven-plugin-git-commit-id.version}</version>
    <executions>
        <execution>
            <id>get-the-git-infos</id>
            <goals>
                <goal>revision</goal>
            </goals>
            <phase>initialize</phase>
        </execution>
    </executions>
    <configuration>
        <generateGitPropertiesFile>true</generateGitPropertiesFile>
        <generateGitPropertiesFilename>${project.build.outputDirectory}/git.properties</generateGitPropertiesFilename>
        <commitIdGenerationMode>full</commitIdGenerationMode>
    </configuration>
</plugin>
```

## Testing Plugins

### Add maven-plugin-surefire

**When to use**: Always included for unit testing.
**User question**: Automatically included with testing framework selection.

Nature: General
Category: Testing

Update the pom.xml with this plugin:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>${maven-plugin-surefire.version}</version>
    <configuration>
        <skipAfterFailureCount>1</skipAfterFailureCount>
        <includes>
            <include>**/*Test.java</include>
        </includes>
        <excludes>
            <exclude>**/*IT.java</exclude>
        </excludes>
    </configuration>
</plugin>
```

### Add maven-failsafe-plugin

**When to use**: Only if integration testing is selected.
**User question**: "Do you want to set up integration testing? (y/n)"

Nature: General
Category: Testing

Update the pom.xml with this plugin:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-failsafe-plugin</artifactId>
    <version>${maven-plugin-failsafe.version}</version>
    <configuration>
        <includes>
            <include>**/*IT.java</include>
        </includes>
        <excludes>
            <exclude>**/*Test.java</exclude>
        </excludes>
    </configuration>
    <executions>
        <execution>
            <goals>
                <goal>integration-test</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

### Add test reports

**When to use**: If user wants HTML test reports.
**User question**: "Do you want HTML test reports generated? (y/n)"

Nature: General
Category: Testing

Update the pom.xml with this reporting section:

```xml
<reporting>
    <plugins>
        <!-- Generates HTML test reports -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-report-plugin</artifactId>
            <version>${maven-plugin-surefire.version}</version>
            <configuration>
                <outputName>junit-report</outputName>
                <showSuccess>true</showSuccess>
            </configuration>
        </plugin>

        <!-- Adds links to source code in reports -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-jxr-plugin</artifactId>
            <version>${maven-plugin-jxr.version}</version>
        </plugin>
    </plugins>
</reporting>
```

## Quality Analysis Profiles

### Add jacoco-maven-plugin

**When to use**: Only if code coverage analysis is selected.
**User question**: "Do you want code coverage analysis? (y/n)"

Nature: General
Category: Code coverage

Update the pom.xml with this profile to run jacoco:

```xml
<profiles>
    <profile>
        <id>jacoco</id>
        <activation>
            <activeByDefault>false</activeByDefault>
        </activation>
        <build>
            <plugins>
                <plugin>
                    <groupId>org.jacoco</groupId>
                    <artifactId>jacoco-maven-plugin</artifactId>
                    <version>${maven-plugin-jacoco.version}</version>
                    <executions>
                        <execution>
                            <id>prepare-agent</id>
                            <goals>
                                <goal>prepare-agent</goal>
                            </goals>
                        </execution>
                        <execution>
                            <id>report</id>
                            <phase>test</phase>
                            <goals>
                                <goal>report</goal>
                            </goals>
                        </execution>
                        <execution>
                            <id>check</id>
                            <phase>verify</phase>
                            <goals>
                                <goal>check</goal>
                            </goals>
                            <configuration>
                                <rules>
                                    <rule>
                                        <element>BUNDLE</element>
                                        <limits>
                                            <limit>
                                                <counter>LINE</counter>
                                                <value>COVEREDRATIO</value>
                                                <minimum>${coverage.level}%</minimum>
                                            </limit>
                                            <limit>
                                                <counter>BRANCH</counter>
                                                <value>COVEREDRATIO</value>
                                                <minimum>${coverage.level}%</minimum>
                                            </limit>
                                        </limits>
                                    </rule>
                                </rules>
                            </configuration>
                        </execution>
                    </executions>
                </plugin>
            </plugins>
        </build>
    </profile>
</profiles>
```

### Add Pitest profile 

**When to use**: Only if mutation testing is selected.
**User question**: "Do you want mutation testing to analyze test quality? (y/n)"

Nature: General
Category: Mutation Testing

Update the pom.xml with this profile to execute Mutation testing in order to analyze the quality of your asserts

```xml
<profile>
    <id>pitest</id>
    <activation>
        <activeByDefault>false</activeByDefault>
    </activation>
    <build>
        <plugins>
            <plugin>
                <groupId>org.pitest</groupId>
                <artifactId>pitest-maven</artifactId>
                <version>${maven-plugin-pitest.version}</version>
                <configuration>
                    <targetClasses>
                        <param>info.jab.cli.*</param>
                    </targetClasses>
                    <targetTests>
                        <param>info.jab.cli.*</param>
                    </targetTests>
                    <outputFormats>
                        <outputFormat>HTML</outputFormat>
                        <outputFormat>XML</outputFormat>
                    </outputFormats>
                    <mutationThreshold>${coverage.level}</mutationThreshold>
                    <coverageThreshold>${coverage.level}</coverageThreshold>
                    <timestampedReports>false</timestampedReports>
                    <verbose>false</verbose>
                </configuration>
                <dependencies>
                    <dependency>
                        <groupId>org.pitest</groupId>
                        <artifactId>pitest-junit5-plugin</artifactId>
                        <version>${maven-plugin-pitest-junit5.version}</version>
                    </dependency>
                </dependencies>
                <executions>
                    <execution>
                        <id>pitest-mutation-testing</id>
                        <goals>
                            <goal>mutationCoverage</goal>
                        </goals>
                        <phase>verify</phase>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</profile>
```

### Add security profile to execute dependency-check

**When to use**: Only if security scanning is selected.
**User question**: "Do you want security vulnerability scanning for dependencies? (y/n)"

Nature: General
Category: Security

Update the pom.xml with this profile

```xml
<profile>
    <id>security</id>
    <activation>
        <activeByDefault>false</activeByDefault>
    </activation>
    <build>
        <plugins>
            <plugin>
                <groupId>org.owasp</groupId>
                <artifactId>dependency-check-maven</artifactId>
                <version>${maven-plugin-dependency-check.version}</version>
                <configuration>
                    <outputDirectory>${project.build.directory}/dependency-check</outputDirectory>
                    <format>ALL</format>
                    <failBuildOnCVSS>7</failBuildOnCVSS>
                    <skipProvidedScope>false</skipProvidedScope>
                    <skipRuntimeScope>false</skipRuntimeScope>
                    <skipSystemScope>false</skipSystemScope>
                    <skipTestScope>false</skipTestScope>
                    <!-- Performance and reliability improvements -->
                    <nvdApiDelay>4000</nvdApiDelay>
                    <nvdMaxRetryCount>3</nvdMaxRetryCount>
                    <nvdValidForHours>24</nvdValidForHours>
                    <!-- Skip analyzers that might cause issues -->
                    <nodeAnalyzerEnabled>false</nodeAnalyzerEnabled>
                    <retireJsAnalyzerEnabled>false</retireJsAnalyzerEnabled>
                </configuration>
                <executions>
                    <execution>
                        <id>dependency-check</id>
                        <goals>
                            <goal>check</goal>
                        </goals>
                        <phase>verify</phase>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</profile>
```

### Add static analysis profile to execute pmd & spotbugs

**When to use**: Only if static code analysis is selected.
**User question**: "Do you want static code analysis (SpotBugs, PMD)? (y/n)"

Nature: General
Category: Static Analysis

Update the pom.xml with this profile

```xml
<profile>
    <id>find-bugs</id>
    <activation>
        <activeByDefault>false</activeByDefault>
    </activation>
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-pmd-plugin</artifactId>
                <version>${maven-plugin-pmd.version}</version>
            </plugin>
            <plugin>
                <groupId>com.github.spotbugs</groupId>
                <artifactId>spotbugs-maven-plugin</artifactId>
                <version>${maven-plugin-spotbugs.version}</version>
                <configuration>
                    <effort>Max</effort>
                    <threshold>Low</threshold>
                    <failOnError>true</failOnError>
                </configuration>
            </plugin>
        </plugins>
    </build>
    <reporting>
        <plugins>
            <!-- SpotBugs reporting for Maven site -->
            <plugin>
                <groupId>com.github.spotbugs</groupId>
                <artifactId>spotbugs-maven-plugin</artifactId>
                <version>${maven-plugin-spotbugs.version}</version>
                <configuration>
                    <effort>Max</effort>
                    <threshold>Low</threshold>
                    <includeFilterFile>src/main/spotbugs/spotbugs-include.xml</includeFilterFile>
                    <excludeFilterFile>src/main/spotbugs/spotbugs-exclude.xml</excludeFilterFile>
                </configuration>
            </plugin>
            <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-pmd-plugin</artifactId>
            <version>${maven-plugin-pmd.version}</version>
            </plugin>
        </plugins>
    </reporting>
</profile>
```

### Add sonar profile to execute sonar

Nature: General
Category: Quality

```xml
<profile>
    <id>sonar</id>
    <activation>
        <activeByDefault>false</activeByDefault>
    </activation>
    <properties>
        <!-- SonarCloud configuration -->
        <sonar.host.url>https://sonarcloud.io</sonar.host.url>
        <sonar.organization>YOUR_GITHUB_USER</sonar.organization>
        <sonar.projectKey>YOUR_GITHUB_USER_REPOSITORY_NAME</sonar.projectKey>
        <sonar.projectName>YOUR_PROJECT_NAME</sonar.projectName>
        <sonar.projectVersion>${project.version}</sonar.projectVersion>
        <sonar.sources>src/main/java</sonar.sources>
        <sonar.tests>src/test/java</sonar.tests>
        <sonar.java.binaries>target/classes</sonar.java.binaries>
        <sonar.java.test.binaries>target/test-classes</sonar.java.test.binaries>
        <sonar.jacoco.reportPath>target/jacoco.exec</sonar.jacoco.reportPath>
        <sonar.junit.reportPaths>target/surefire-reports</sonar.junit.reportPaths>
        <sonar.coverage.exclusions>**/*Test.java,**/*IT.java</sonar.coverage.exclusions>
        <sonar.java.source>${java.version}</sonar.java.source>
    </properties>
    <build>
        <plugins>
            <plugin>
                <groupId>org.sonarsource.scanner.maven</groupId>
                <artifactId>sonar-maven-plugin</artifactId>
                <version>${maven-plugin-sonar.version}</version>
            </plugin>
        </plugins>
    </build>
</profile>
```