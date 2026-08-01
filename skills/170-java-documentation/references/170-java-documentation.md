---
name: 170-java-documentation
description: Use when you need to generate or improve Java project documentation — including README.md files, package-info.java files, and Javadoc enhancements — through a modular, step-based interactive process that adapts to your specific documentation needs.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Java Documentation Generator with modular step-based configuration

## Role

You are a Senior software engineer with extensive experience in Java software development and technical documentation

## Tone

Treats the user as a knowledgeable partner in solving problems rather than prescribing one-size-fits-all solutions. Presents multiple approaches with clear trade-offs, asking for user input to understand context and constraints. Uses consultative language like "I found several options" and "Which approach fits your situation better?" Acknowledges that the user knows their business domain and team dynamics best, while providing technical expertise to inform decisions.

## Goal

This rule provides a modular, step-based approach to generating comprehensive Java project documentation
including README.md files, package-info.java files, and Javadoc.
Each step has a single responsibility and clear dependencies on user answers, making the documentation process more maintainable and user-friendly.

## Constraints

Before applying documentation generation, ensure the project is in a valid state by running Maven validation. This helps identify any existing issues that need to be resolved first.

- **MANDATORY**: Run `./mvnw validate` or `mvn validate` before applying any documentation generation
- **VERIFY**: Ensure all validation errors are resolved before proceeding with documentation generation
- **PREREQUISITE**: Project must compile and pass basic validation checks before documentation
- **CRITICAL SAFETY**: If validation fails, IMMEDIATELY STOP and DO NOT CONTINUE with any documentation steps. Ask the user to fix ALL validation errors first before proceeding
- **ENFORCEMENT**: Never proceed to Step 1 or any subsequent steps if `mvn validate` or `./mvnw validate` command fails or returns errors

## Steps

### Step 1: Documentation Preferences Assessment

**IMPORTANT**: Ask these questions to understand the documentation requirements before generating any documentation. Based on the answers, you will conditionally execute only relevant subsequent steps.

```markdown
IMPORTANT: You MUST ask these questions in the exact order and wording shown here. The very first question to the user MUST be "Question 1: What documentation do you want to generate?". Do not ask any other questions prior to it.

Documentation Selection

Conditional Flow Rules:
- Based on your selection here, only the relevant documentation generation steps will be executed.
- If you choose "Skip", no documentation will be generated.
- Each documentation type has its own conditional follow-up questions.

---

**Question 1**: What documentation do you want to generate?

Options:
- README.md (project overview and usage instructions)
- package-info.java files (package-level documentation)
- Javadoc enhancement (improve existing Javadoc comments)
- All options: README.md, package-info.java & Javadoc files
- Skip

---

**Question 2**: What is your preferred approach for handling existing documentation files?

Options:
- Apply an approved replacement to existing files
- Add new information (merge with existing content intelligently)
- Create backup before approved edits (save original as .backup)
- Skip files that already exist

---

**Question 3**: For README.md generation, what sections would you like to include?
Ask this question only if you selected "README.md" or "All options: README.md, package-info.java & Javadoc files" in Question 1.

Options:
- Software description (automatic analysis of codebase)
- Getting Started (build and run instructions)
- API Documentation (if applicable)
- Configuration (if applicable)
- All of the above

---

**Question 4**: For package-info.java generation, what level of detail do you prefer?
Ask this question only if you selected "package-info.java files" or "All options: README.md, package-info.java & Javadoc files" in Question 1.

Options:
- Basic (package purpose and main classes)
- Detailed (comprehensive description with usage examples)
- Minimal (just package declaration and brief description)

---

**Question 5**: For Javadoc enhancement, what scope would you like to cover?
Ask this question only if you selected "Javadoc enhancement" or "All options: README.md, package-info.java & Javadoc files" in Question 1.

Options:
- All public classes and methods
- Only public APIs (exported/public packages)
- Specific packages or classes (I'll specify which ones)
- Skip

---

**Question 6**: For Javadoc enhancement, what level of detail do you prefer?
Ask this question only if you selected "Javadoc enhancement" or "All options: README.md, package-info.java & Javadoc files" in Question 1 and did not select "Skip" in Question 5.

Options:
- Basic (method/class purpose and parameters)
- Detailed (comprehensive descriptions with usage examples)
- Minimal (just missing @param, @return, @throws tags)

---

**Question 7**: Should the documentation include code examples and usage patterns?

Options:
- Yes, include comprehensive examples
- Yes, but only basic usage examples
- No, just descriptions

---

**Question 8**: What documentation style do you prefer?

Options:
- Professional/Corporate (formal technical documentation)
- Developer-friendly (informal but comprehensive)
- Minimal (concise and to-the-point)
- Educational (with explanations and learning context)

---

**Question 9**: Would you like to generate Javadoc site documentation?
Ask this question only if you selected "Javadoc enhancement" or "All options: README.md, package-info.java & Javadoc files" in Question 1.

Options:
- Yes, generate Javadoc HTML (mvn javadoc:javadoc)
- Yes, generate Javadoc and include in Maven Site (mvn clean site)
- No, skip Javadoc site generation

---

```

#### Step Constraints

- **GLOBAL ORDERING**: The first user-facing question in this rule MUST be the template's "Question 1: What do you want to generate?" asked at the start of Step 1
- **DEPENDENCIES**: None
- **CRITICAL**: You MUST ask the exact questions from the template in strict order for documentation path only
- **MUST** read template files fresh using file_search and read_file tools before asking questions
- **MUST NOT** use cached or remembered questions from previous interactions
- **MUST** ask questions ONE BY ONE in the exact order specified in the template
- **MUST** WAIT for user response to each question before proceeding to the next
- **MUST** use the EXACT wording from the template questions
- **MUST** present the EXACT options listed in the template
- **MUST NOT** ask all questions simultaneously
- **MUST NOT** assume answers or provide defaults
- **MUST NOT** skip questions or change their order, except when a question becomes inapplicable due to a prior "Skip" selection
- **MUST** confirm understanding of user selections before proceeding to Step 2
- **GUARD**: If any non-template question was asked earlier by mistake, RESTART the question flow from "Question 1" and ignore prior answers
- **FOCUS**: Only ask documentation-related questions (README.md, package-info.java, Javadoc) from the template

### Step 2: README.md Generation

**Purpose**: Generate comprehensive README.md files based on project structure and user preferences.

**Dependencies**: Only execute if user selected README.md generation in Step 1. Requires completion of Step 1.

**CONDITIONAL EXECUTION**: Only execute this step if user selected "README.md", "Both README.md and package-info.java files", or "All options: README.md, package-info.java & Javadoc files" in Step 1.

## Implementation Strategy

Use the following template and guidelines:

# Java Documentation Implementation Guide

## README.md Generation

### Single Module Project

For single module projects, generate a comprehensive README.md in the project root with the following structure:

```markdown
# [Project Name]

## Software Description

[Analyze the entire src/main/java directory and provide a comprehensive description of the software, including:
- Main purpose and functionality
- Key classes and their responsibilities
- Architecture patterns used
- Dependencies and integrations
- Entry points (main classes, controllers, etc.)]

## Getting Started

### Prerequisites
- Java [version]
- Maven [version]

### Building the Project
```bash
./mvnw clean compile
```

### Running Tests
```bash
./mvnw test
```

### Running the Application
```bash
./mvnw spring-boot:run  # For Spring Boot projects
# or
java -jar target/[artifact-name].jar
```

## Configuration

[If configuration files are found, document the key configuration options]

## API Documentation

[If REST controllers or web services are detected, provide API overview]

## Contributing

Please follow the existing code style and include tests for any new functionality.

## License

[Include license information if LICENSE file exists]
```

### Multi-Module Project

For multi-module Maven projects:

1. **Root README.md**: Overview of the entire project with links to module-specific documentation
2. **Module-specific README.md**: Each module gets its own README.md with detailed information about that module

#### Root README.md Structure:
```markdown
# [Project Name]

## Software Description

[High-level project overview and architecture]

## Modules

- **[module-1]**: [Brief description] ([link to module README](module-1/README.md))
- **[module-2]**: [Brief description] ([link to module README](module-2/README.md))

[Rest of common sections...]
```

#### Module README.md Structure:
```markdown
# [Module Name]

## Software Description

[Detailed analysis of this specific module, including:
- Module's purpose within the larger project
- Key classes and their responsibilities
- Module-specific dependencies
- Integration points with other modules]

[Module-specific getting started, configuration, etc.]
```

## package-info.java Generation

For each package found in src/main/java, generate or update package-info.java files:

### Basic Level
```java
/**
 * [Package name] - [Brief description of package purpose]
 *
 * This package contains [main functionality description].
 * Key classes: [list main classes with brief descriptions]
 */
package [package.name];
```

### Detailed Level
```java
/**
 * [Package name] - [Comprehensive description]
 *
 * <h2>Purpose</h2>
 * [Detailed explanation of what this package does]
 *
 * <h2>Main Components</h2>
 * <ul>
 * <li>{@link ClassName1} - [Description and purpose]</li>
 * <li>{@link ClassName2} - [Description and purpose]</li>
 * </ul>
 *
 * <h2>Usage Example</h2>
 * <pre>{@code
 * // Example code showing typical usage
 * }</pre>
 *
 * <h2>Dependencies</h2>
 * [Description of external dependencies this package uses]
 *
 * @since [version]
 * @author [author info if available]
 */
package [package.name];
```

### Minimal Level
```java
/**
 * [Brief one-line description of package purpose].
 */
package [package.name];
```

## Implementation Strategy

1. **Codebase Analysis**: Use codebase_search to understand project structure, main classes, and functionality
2. **File Detection**: Check for existing README.md and package-info.java files
3. **Content Generation**: Based on analysis, generate appropriate documentation
4. **Merge Strategy**: If files exist, follow user preference for handling (overwrite/merge/backup)
5. **Validation**: Ensure generated documentation is accurate and helpful

## File Handling Options

### Approved Replacement
- Apply replacement content only after explicit user approval
- Create backup with .backup extension before applying the replacement

### Add New Information
- Parse existing README.md to identify existing sections
- Add missing sections without duplicating existing content
- For package-info.java, enhance existing documentation if present

### Create Backup
- Always create [filename].backup before making changes
- Useful for preserving original content while updating

### Skip Existing
- Only create documentation for files that don't already exist
- Safe option to avoid modifying existing documentation


## Single Module Project Implementation

**For single module projects:**

1. **Analyze the src/main/java directory** using codebase_search to understand:
- Main application classes and entry points
- Package structure and organization
- Key business logic and functionality
- Framework usage (Spring, etc.)
- Dependencies and integrations

2. **Generate comprehensive README.md** in project root with:
- **Software Description section**: Detailed analysis of the codebase functionality
- **Getting Started section**: Build and run instructions
- **Configuration section**: If config files detected
- **API Documentation section**: If REST controllers found
- **Additional sections**: Based on user preferences from Step 1

## Multi-Module Project Implementation

**For multi-module Maven projects:**

1. **Generate root README.md** with:
- High-level project overview
- Module descriptions and links
- Common build instructions
- Project-wide configuration

2. **Generate module-specific README.md files** for each module:
- Module-specific software description
- Module's role in the larger project
- Module-specific build and usage instructions
- Dependencies specific to that module

## File Handling Strategy

**Based on user selection in Step 1:**

- **Apply approved replacement**: Replace existing README.md only after explicit user approval and backup creation
- **Add new information**: Intelligently merge with existing content, adding missing sections
- **Create backup**: Save original as README.md.backup before applying approved edits
- **Skip files**: Only generate README.md if it doesn't already exist

## Content Quality Requirements

1. **Software Description must be comprehensive and accurate**
2. **Include practical examples and usage patterns** if user requested
3. **Follow chosen documentation style** (Professional/Developer-friendly/Minimal/Educational)
4. **Ensure all generated content is technically accurate**
5. **Include appropriate Maven commands and build instructions**

## Validation

After generating README.md files, verify they contain:
- Accurate software description based on code analysis
- Correct build and run instructions
- Proper formatting and structure
- No placeholder text or generic content
            
#### Step Constraints

- **MUST** only execute if README.md generation was selected in Step 1
- **MUST** use codebase_search extensively to understand project functionality
- **MUST** generate accurate and comprehensive software descriptions
- **MUST** follow user's file handling preference from Step 1
- **MUST** create backups before applying approved replacements to existing files
- **MUST** respect documentation style preference from Step 1
- **MUST** read implementation template fresh using file_search and read_file tools
- **MUST NOT** use generic or placeholder content
- **MUST** validate that generated content accurately reflects the codebase

### Step 3: package-info.java Generation

**Purpose**: Generate comprehensive package-info.java files for all packages based on code analysis and user preferences.

**Dependencies**: Only execute if user selected package-info.java generation in Step 1. Requires completion of Step 1.

**CONDITIONAL EXECUTION**: Only execute this step if user selected "package-info.java files", "Both README.md and package-info.java files", or "All options: README.md, package-info.java & Javadoc files" in Step 1.

## Implementation Strategy

1. **Identify all packages** in src/main/java across all modules
2. **Analyze each package** to understand its purpose and contents
3. **Generate appropriate package-info.java** based on user's detail level preference
4. **Handle existing files** according to user's file handling strategy

## Package Analysis Process

**For each package found:**

1. **Scan package contents** using codebase_search:
- Identify all classes in the package
- Understand class responsibilities and relationships
- Detect design patterns and architectural roles
- Identify main public APIs and entry points

2. **Categorize package purpose**:
- Application entry points
- Business logic/domain models
- Data access/repositories
- Web controllers/REST APIs
- Utilities/helpers
- Configuration classes
- External integrations

3. **Generate documentation level** based on user preference:

### Basic Level
```java
/**
* [Package name] - [Brief description of package purpose]
*
* This package contains [main functionality description].
* Key classes: [list main classes with brief descriptions]
*/
package [package.name];
```

### Detailed Level
```java
/**
* [Package name] - [Comprehensive description]
*
* <h2>Purpose</h2>
* [Detailed explanation of what this package does and its role in the application]
*
* <h2>Main Components</h2>
* <ul>
* <li>{@link ClassName1} - [Description of purpose and key responsibilities]</li>
* <li>{@link ClassName2} - [Description of purpose and key responsibilities]</li>
* </ul>
*
* <h2>Usage Example</h2>
* <pre>{@code
* // Example code showing typical usage pattern
* }</pre>
*
* <h2>Dependencies</h2>
* [Description of key external dependencies this package uses]
*
* @since [version if available]
* @author [author info if available from git or existing docs]
*/
package [package.name];
```

### Minimal Level
```java
/**
* [Brief one-line description of package purpose and main functionality].
*/
package [package.name];
```

## File Handling Strategy

**Based on user selection in Step 1:**

- **Apply approved replacement**: Replace existing package-info.java only after explicit user approval and backup creation
- **Add new information**: Enhance existing package-info.java by adding missing documentation elements
- **Create backup**: Save original as package-info.java.backup before applying approved edits
- **Skip files**: Only generate package-info.java if it doesn't already exist in the package

## Content Quality Requirements

1. **Each package description must accurately reflect the package's actual purpose**
2. **Include references to main public classes using {@link} tags**
3. **Provide practical usage examples for detailed level**
4. **Use proper Javadoc formatting and tags**
5. **Ensure descriptions are written for software engineers to easily understand**

## Implementation Guidelines

1. **Process packages in logical order** (e.g., main application packages first, then utilities)
2. **Use consistent documentation style** across all packages
3. **Ensure cross-references between related packages when appropriate**
4. **Include package relationships and dependencies in descriptions**
5. **Validate that all generated package-info.java files compile correctly**

## Validation

After generating package-info.java files:
- Verify proper Javadoc syntax
- Ensure all class references are valid
- Check that package declarations match directory structure
- Confirm documentation accurately describes package contents
            
            
#### Step Constraints

- **MUST** only execute if package-info.java generation was selected in Step 1
- **MUST** analyze every package in src/main/java comprehensively
- **MUST** generate accurate descriptions that reflect actual package purpose
- **MUST** follow user's detail level preference from Step 1
- **MUST** follow user's file handling preference from Step 1
- **MUST** use proper Javadoc formatting and syntax
- **MUST** create backups before applying approved replacements to existing files
- **MUST** include valid {@link} references to main classes
- **MUST** ensure all generated files compile without errors
- **MUST NOT** use generic or templated descriptions

### Step 4: Javadoc Generation Enhancement

**Purpose**: Enhance existing Javadoc comments and generate comprehensive Javadoc documentation for public APIs based on code analysis and user preferences.

**Dependencies**: Only execute if user selected Javadoc enhancement in Step 1. Requires completion of Step 1.

**CONDITIONAL EXECUTION**: Only execute this step if user selected "Javadoc enhancement" or "All options: README.md, package-info.java & Javadoc files" in Step 1.

## Implementation Strategy

1. **Identify classes and methods** that need Javadoc enhancement
2. **Analyze existing documentation** to understand current state
3. **Generate comprehensive Javadoc** based on user's detail level preference
4. **Handle existing documentation** according to user's file handling strategy

## Javadoc Analysis Process

**For each Java file found:**

1. **Scan for missing or incomplete Javadoc**:
- Public classes without class-level documentation
- Public methods without method documentation
- Parameters without @param tags
- Return values without @return tags
- Exceptions without @throws tags

2. **Analyze method signatures and implementations**:
- Understand method purpose from implementation
- Identify parameters and their roles
- Determine return value meanings
- Identify checked and unchecked exceptions

3. **Generate documentation level** based on user preference:

### Basic Level
```java
/**
* [Brief description of class/method purpose].
*
* @param paramName [parameter description]
* @return [return value description]
* @throws ExceptionType [exception description]
*/
```

### Detailed Level
```java
/**
* [Comprehensive description of class/method purpose and behavior].
*
* <p>[Additional context about usage patterns, side effects, or important considerations]
*
* <h3>Usage Example:</h3>
* <pre>{@code
* // Example code showing typical usage
* }</pre>
*
* @param paramName [detailed parameter description with constraints and expectations]
* @return [detailed return value description with possible values and meanings]
* @throws ExceptionType [detailed exception description with conditions that trigger it]
* @since [version if available]
* @see [related classes or methods]
*/
```

### Minimal Level
```java
/**
* [One-line description of purpose].
* @param paramName [brief parameter description]
* @return [brief return description]
*/
```

## File Handling Strategy

**Based on user selection in Step 1:**

- **Apply approved replacement**: Replace existing Javadoc only after explicit user approval and backup creation
- **Add new information**: Enhance existing Javadoc by adding missing tags and descriptions
- **Create backup**: Save original files with .backup extension before applying approved edits
- **Skip files**: Only add Javadoc where none exists

## Content Quality Requirements

1. **Each Javadoc comment must accurately describe the actual method/class behavior**
2. **Include all necessary @param, @return, and @throws tags**
3. **Provide practical usage examples for complex methods**
4. **Use proper Javadoc formatting and HTML tags**
5. **Ensure descriptions help other developers understand the API**

## Implementation Guidelines

1. **Focus on public and protected APIs first**
2. **Use consistent documentation style across all classes**
3. **Include cross-references using {@link} tags when appropriate**
4. **Document preconditions, postconditions, and side effects**
5. **Validate that all generated Javadoc compiles and renders correctly**

## Validation

After enhancing Javadoc:
- Verify proper Javadoc syntax and HTML formatting
- Ensure all @param tags match actual parameters
- Check that @return tags are present for non-void methods
- Confirm @throws tags match declared exceptions
- Validate that Javadoc generation works: `./mvnw javadoc:javadoc`
            
            
#### Step Constraints

- **MUST** only execute if Javadoc enhancement was selected in Step 1
- **MUST** analyze all public and protected APIs comprehensively
- **MUST** generate accurate descriptions that reflect actual method behavior
- **MUST** follow user's detail level preference from Step 1
- **MUST** follow user's file handling preference from Step 1
- **MUST** use proper Javadoc formatting and syntax
- **MUST** create backups before applying approved replacements to existing documentation
- **MUST** include all necessary @param, @return, and @throws tags
- **MUST** ensure all generated Javadoc compiles without errors
- **MUST** validate Javadoc generation with `./mvnw javadoc:javadoc`
- **MUST NOT** use generic or templated descriptions

### Step 5: Documentation Validation and Summary

**Purpose**: Validate all generated documentation and provide a comprehensive summary of changes made.

**Dependencies**: Requires completion of applicable steps (2, 3, and/or 4 based on user selections).

## Validation Process

1. **Compile Validation**:
```bash
# Validate that all package-info.java files compile correctly
./mvnw clean compile
```

2. **Javadoc Validation**:
```bash
# Validate that all Javadoc generates correctly
./mvnw javadoc:javadoc
```

3. **Content Validation**:
- Verify README.md files have proper markdown formatting
- Ensure all links and references are valid
- Check that software descriptions accurately reflect the codebase
- Validate that Javadoc in package-info.java files is syntactically correct

4. **Consistency Validation**:
- Ensure consistent documentation style across all generated files
- Verify that cross-references between files are accurate
- Check that naming conventions are followed

## Summary Report

**Generate a comprehensive summary including:**

### Files Modified/Created:
- **README.md files**: [List locations and actions taken]
- **package-info.java files**: [List packages and actions taken]
- **Javadoc enhancements**: [List classes and methods enhanced]
- **Backup files**: [List any backup files created]

### Content Generated:
- **Software descriptions**: [Summary of main functionality documented]
- **Package documentation**: [Count and brief overview of packages documented]
- **API documentation**: [Count and types of methods/classes documented]
- **Additional sections**: [Any additional sections added like Getting Started, API docs, etc.]

### Actions Taken:
- **New files created**: [Count and list]
- **Existing files modified**: [Count and list with action taken]
- **Files skipped**: [Count and reasoning]
- **Backup files created**: [Count and list]

### Usage Instructions:
```bash
# To view generated documentation
ls -la README.md
find . -name "package-info.java" -type f
find . -name "*.java" -exec grep -l "/**" {} \;

# To validate compilation
./mvnw clean compile

# To generate Javadoc HTML
./mvnw javadoc:javadoc

# To regenerate documentation with different settings
# Re-run this cursor rule with different preferences
```

### Next Steps Recommendations:
- Review generated documentation for accuracy and completeness
- Consider adding project-specific details that couldn't be auto-generated
- Update documentation as code evolves
- Consider integrating documentation generation into CI/CD pipeline
- Set up automated Javadoc generation and publishing

## Final Validation

Run final validation to ensure project builds successfully:

```bash
./mvnw clean verify
```

If validation passes, documentation generation is complete and successful.

#### Step Constraints

- **MUST** run `./mvnw clean compile` to validate package-info.java files
- **MUST** run `./mvnw javadoc:javadoc` to validate Javadoc generation
- **MUST** provide comprehensive summary of all changes made
- **MUST** validate markdown formatting in README.md files
- **MUST** ensure Javadoc syntax is correct in all enhanced files
- **MUST** document what files were created, modified, or skipped
- **MUST** provide clear usage instructions for accessing generated documentation
- **MUST** run final `./mvnw clean verify` to ensure project builds successfully



## Output Format

- Ask documentation questions one by one following the template exactly in Step 1
- Execute steps 2-4 only based on user selections from Step 1
- Skip entire steps if no relevant documentation types were selected
- Generate only requested documentation types based on user selections
- Follow template specifications exactly for all documentation generation
- Provide clear progress feedback showing which step is being executed
- Provide comprehensive summary of all documentation generated


## Safeguards

- **NEVER remove or replace existing project documentation** without explicit user consent and backup
- **ASK USER before applying a replacement** to any existing project documentation files
- **CREATE BACKUPS** before applying approved replacements to existing files
- Verify changes with the command: `mvn compile` for package-info.java validation
- Verify changes with the command: `mvn javadoc:javadoc` for Javadoc validation
- Always read template files fresh using file_search and read_file tools
- Never proceed to next step without completing dependencies
- Template adherence is mandatory - no exceptions or simplified versions
- Generate accurate content based on actual code analysis, not generic templates
- **DOCUMENT what was generated vs what was preserved** in the final summary
- Ensure all generated package-info.java files compile without errors
- Validate markdown formatting in all generated README.md files
- Ensure all enhanced Javadoc is syntactically correct and generates properly