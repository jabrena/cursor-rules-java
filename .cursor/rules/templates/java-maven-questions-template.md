# Java Maven Configuration Questions

**IMPORTANT**: Ask these questions to understand the project needs before making any changes to the pom.xml. Based on the answers, you will conditionally add only relevant dependencies and plugins.

## 1. Project Nature

**What type of Java project is this?**

Options:
- Java Library (for publishing to Maven Central/Nexus)
- Java CLI Application (command-line tool)  
- Java Microservice (Web service/REST API/Modular monolith)
- Other (specify)

## 2. Java Version

**Which Java version does your project target?**

Options:

- Java 17 (LTS - recommended for new projects)
- Java 21 (LTS - latest LTS version)
- Java 24 (latest features)
- Other (specify version)

## 3. Build and Quality Aspects

**What build and quality aspects are important for your project?** (Select all that apply)

Options:
- Format source code (Spotless)
- Maven Enforcer
- Compiler behaviour improvements with ErrorProne + NullAway (Ask for JSpecify)
- Unit Testing (Surefire)
- Integration testing (Failsafe)
- Code coverage reporting (JaCoCo)
- Mutation testing (PiTest)
- Security vulnerability scanning (OWASP)
- Static code analysis (SpotBugs, PMD)
- Sonar

## 4. Coverage Threshold

**What is your target coverage threshold?**

Options:
- 70% (moderate)
- 80% (recommended)
- 90% (high)
- Custom percentage (specify)

## 5. Sonar Configuration (conditional)

**Note**: This question is only asked if "Static code analysis (SpotBugs, Sonar)" was selected in question 3.

**Do you want to configure Sonar/SonarCloud integration?** (y/n)

**If yes, please provide the following information:**

### 5.1 Sonar Organization

**What is your Sonar organization identifier?**

- For SonarCloud: This is typically your GitHub username or organization name
- For SonarQube: This is your organization key as configured in SonarQube
- Example: `my-github-user` or `my-company-org`

### 5.2 Sonar Project Key

**What is your Sonar project key?**

- For SonarCloud: Usually in format `GITHUB_USER_REPOSITORY_NAME` (e.g., `john-doe_my-java-project`)
- For SonarQube: Custom project key as defined in your SonarQube instance
- Must be unique within your Sonar organization
- Example: `john-doe_awesome-java-lib`

### 5.3 Sonar Project Name

**What is your Sonar project display name?**

- Human-readable name for your project as it appears in Sonar dashboard
- Can contain spaces and special characters
- Example: `Awesome Java Library` or `My Microservice API`

## 6. Sonar Host Configuration (conditional)

**Note**: This question is only asked if Sonar configuration was enabled in question 5.

**Which Sonar service are you using?**

Options:
- SonarCloud (https://sonarcloud.io) - recommended for open source projects
- SonarQube Server (specify your server URL)

**If SonarQube Server**: Please provide your SonarQube server URL (e.g., `https://sonar.mycompany.com`)

## Instructions for AI Assistant

1. Ask ALL questions above before making any changes
2. Wait for user responses to each question
3. Based on answers, conditionally add only the requested features
4. Do not assume or add features that weren't explicitly requested
5. Customize configuration based on the specific answers provided
6. **For Sonar questions (5-6)**: Only ask if "Static code analysis (SpotBugs, Sonar)" was selected in question 3
7. **Validate Sonar parameters**: Ensure provided values follow the expected format and naming conventions
