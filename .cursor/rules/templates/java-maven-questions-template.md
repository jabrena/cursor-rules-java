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
- Compiler behaviour improvements with ErrorProne + NullAway (Ask for JSpecify)
- Integration testing (Failsafe)
- Code coverage reporting (JaCoCo)
- Mutation testing (PiTest)
- Security vulnerability scanning (OWASP)
- Static code analysis (SpotBugs)

## 4. Coverage Threshold

**What is your target coverage threshold?**

Options:
- 70% (moderate)
- 80% (recommended)
- 90% (high)
- Custom percentage (specify)

## Instructions for AI Assistant

1. Ask ALL questions above before making any changes
2. Wait for user responses to each question
3. Based on answers, conditionally add only the requested features
4. Do not assume or add features that weren't explicitly requested
5. Customize configuration based on the specific answers provided
