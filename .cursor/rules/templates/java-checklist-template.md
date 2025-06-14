# Java Development Guide

Use the following process to improve the java development in some areas if required using the following set of Java Cursor Rules.

## Process Overview

### Step 1: Review the build system (Maven)

| Activity | Done | Prompt | Notes |
|----------|------|--------|-------|
| Review your pom.xml and Maven project | [ ] | `Help me to review the pom.xml  following the best practices for dependency management and directory structure use the cursor rule @110-java-maven-best-practices` | |
| Improve the Maven project with plugins & dependencies | [ ] | `Can you improve the pom.xml using the cursor rule @101-java-maven-deps-and-plugins.mdc` | Conversational approach|

### Step 2: Design Principles

| Activity | Done | Prompt | Notes |
|----------|------|--------|-------|
| Object-Oriented Design Review | [ ] | `Review my code for object-oriented design using the cursor rule @121-java-object-oriented-design` | |
| Type Design Review | [ ] | `Help me improve my type design using the cursor rule @122-java-type-design` | |

### Step 3: Coding Guidelines

| Activity | Done | Prompt | Notes |
|----------|------|--------|-------|
| General Java Guidelines | [ ] | `Review my code for general Java best practices using the cursor rule @123-java-general-guidelines` | |
| Secure Coding Review | [ ] | `Check my code for security issues using the cursor rule @124-java-secure-coding` | |
| Concurrency Review | [ ] | `Review my code for concurrency best practices using the cursor rule @125-java-concurrency` | |
| Logging Best Practices | [ ] | `Help me improve logging using the cursor rule @126-java-logging` | |

### Step 4: Testing

| Activity | Done | Prompt | Notes |
|----------|------|--------|-------|
| Unit Testing | [ ] | `Help me write unit tests using the cursor rule @131-java-unit-testing` | |
| Integration Testing | [ ] | `Help me write integration tests using the cursor rule @132-java-integration-testing` | If required |

### Step 5: Refactoring

| Activity | Done | Prompt | Notes |
|----------|------|--------|-------|
| Add Modern Java Features | [ ] | `Refactor my code to use modern Java features using the cursor rule @141-java-refactoring-with-modern-features` | |
| Functional Programming | [ ] | `Refactor my code to use functional programming using the cursor rule @142-java-functional-programming` | |
| Data Oriented Programming | [ ] | `Refactor my code to use data oriented programming using the cursor rule @143-java-data-oriented-programming` | |

### Step 6: Database

| Activity | Done | Prompt | Notes |
|----------|------|--------|-------|
| SQL Guidelines | [ ] | `Review my SQL code using the cursor rule @500-sql` | |

## Reference Table: Java Cursor Rules

| Rule Name | Cursor Rule | Description |
|-----------|-------------|-------------|
| Maven Best Practices | @110-java-maven-best-practices | Best practices for Maven dependency management and project structure |
| Maven Dependencies & Plugins | @111-java-maven-deps-and-plugins | Improve pom.xml with recommended plugins and dependencies |
| Object Oriented Design | @121-java-object-oriented-design | Object-oriented design principles and review |
| Type Design | @122-java-type-design | Best practices for type design in Java |
| General Java Guidelines | @123-java-general-guidelines | General Java coding best practices |
| Secure Java Coding | @124-java-secure-coding | Secure coding practices for Java |
| Concurrency | @125-java-concurrency | Best practices for concurrency in Java |
| Logging Guidelines | @126-java-logging | Logging best practices for Java applications |
| Unit Testing | @131-java-unit-testing | Guidelines for writing unit tests in Java |
| Integration Testing | @132-java-integration-testing | Guidelines for writing integration tests in Java |
| Modern Java Features | @141-java-refactoring-with-modern-features | Refactoring with modern Java (Java 8+) features |
| Functional Programming | @142-java-functional-programming | Applying functional programming in Java |
| Data Oriented Programming | @143-java-data-oriented-programming | Data-oriented programming style in Java |
| SQL Guidelines | @500-sql | SQL development best practices |

## Tips & Best Practices

- Use the checklists above to track your progress through each phase.
- Use the provided prompts directly in Cursor or your LLM-enabled IDE for best results.
- Review each rule's documentation for detailed examples and anti-patterns.
- Regularly update your dependencies and plugins for security and performance.
- Apply secure coding and logging practices throughout your codebase.
- Use modern Java features and refactor legacy code incrementally.

## Progress Tracking

- [ ] Step 1: Build System (Maven)
- [ ] Step 2: Design Principles
- [ ] Step 3: Coding Guidelines
- [ ] Step 4: Testing
- [ ] Step 5: Refactoring
- [ ] Step 6: Database

---

**Note:** This guide is self-contained and portable. Copy it into any Java project to get started with Cursor Rules for Java development. 