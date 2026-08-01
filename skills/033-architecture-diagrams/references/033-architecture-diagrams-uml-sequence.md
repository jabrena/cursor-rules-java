---
name: 033-architecture-diagrams-uml-sequence
description: Focused UML sequence diagram guidance for the interactive architecture diagrams skill.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Java Diagrams Generator with modular step-based configuration

## Role

You are a Senior software engineer with extensive experience in Java workflow analysis and sequence diagram generation.

## Goal

Generate UML sequence diagrams only when selected by the `033-architecture-diagrams` question flow. Use this reference to analyze real Java workflows, API interactions, and business processes, then produce PlantUML sequence diagrams that match the selected output organization.

## Constraints

Apply this reference only after the SKILL.md question flow selected UML sequence diagrams.

- Read this reference only when the user selected UML sequence diagrams or All diagrams in the centralized question flow.
- Inspect repository code before generating diagrams; do not produce generic diagrams without code analysis.
- Use PlantUML sequence diagram syntax and validate renderability before final delivery.
- Organize generated files according to the user's output organization and format selections.

## Steps

### Step 1: Analyze selected sequence scope

Use the user's sequence-specific answer to decide which workflows to inspect:

- Main application flows: application entry points, user journeys, authentication, authorization, and core feature usage.
- API interactions: REST endpoints, request/response patterns, controller-to-service paths, persistence calls, and error handling.
- Complex business logic flows: orchestration, transaction boundaries, batch jobs, state transitions, and consistency handling.

For every diagram, trace actual classes, methods, messages, and alternative flows from the repository.
### Step 2: Apply sequence template guidance

Use the following template and guidelines:

# UML Sequence Diagram Generation Guidelines

## Implementation Strategy

Generate UML sequence diagrams using PlantUML syntax to illustrate key application workflows and interactions.

### Analysis Process

**For each workflow identified:**

1. **Identify main actors and components**:
- External actors (users, systems, APIs)
- Internal components (controllers, services, repositories)
- Key domain objects and entities

2. **Trace interaction flows**:
- Method calls and message passing
- Conditional logic and alternative flows
- Loop and iteration patterns
- Error handling and exception flows

3. **Determine diagram scope** based on user selection:
- **Main application flows**: Authentication, core business processes
- **API interactions**: REST endpoint flows, request/response patterns
- **Complex business logic**: Multi-step processes, workflow orchestration

### Diagram Generation Guidelines

#### Basic Sequence Diagram Structure
```plantuml
@startuml
actor Actor
participant Controller
participant Service
participant Repository

Actor -> Controller: HTTP Request
Controller -> Service: Business Logic Call
Service -> Repository: Data Access
Repository --> Service: Data Response
Service --> Controller: Business Result
Controller --> Actor: HTTP Response
@enduml
```

#### Advanced Patterns

**Alternative Flows (Authentication Example)**:
```plantuml
@startuml
actor User
participant AuthController
participant AuthService
participant Database

User -> AuthController: POST /login
AuthController -> AuthService: authenticate(credentials)
AuthService -> Database: findUserByEmail(email)

alt User exists and password valid
Database --> AuthService: User data
AuthService -> AuthService: validatePassword()
AuthService --> AuthController: JWT token
AuthController --> User: 200 OK + token
else Authentication failed
AuthService --> AuthController: AuthenticationException
AuthController --> User: 401 Unauthorized
end
@enduml
```

**Loop Patterns (Batch Processing Example)**:
```plantuml
@startuml
participant Client
participant BatchService
participant ItemProcessor
participant Database

Client -> BatchService: processBatch(items)

loop For each item
BatchService -> ItemProcessor: processItem(item)
ItemProcessor -> Database: save(processedItem)
Database --> ItemProcessor: confirmation
ItemProcessor --> BatchService: result
end

BatchService --> Client: BatchResult
@enduml
```

**Advanced PlantUML Features**:
```plantuml
@startuml
!theme plain
title Authentication and Authorization Flow

actor User
participant "Web Controller" as Controller
participant "Auth Service" as AuthService
participant "JWT Service" as JwtService
participant Database
participant "Cache" as Redis

== Authentication Phase ==
User -> Controller: POST /api/login
activate Controller
Controller -> AuthService: authenticate(credentials)
activate AuthService

AuthService -> Database: findUser(username)
activate Database
Database --> AuthService: UserEntity
deactivate Database

alt credentials valid
AuthService -> JwtService: generateToken(user)
activate JwtService
JwtService --> AuthService: JWT Token
deactivate JwtService

AuthService -> Redis: cacheUserSession(userId, token)
activate Redis
Redis --> AuthService: OK
deactivate Redis

AuthService --> Controller: AuthResponse(token)
deactivate AuthService
Controller --> User: 200 OK + JWT
deactivate Controller
else invalid credentials
AuthService --> Controller: UnauthorizedException
deactivate AuthService
Controller --> User: 401 Unauthorized
deactivate Controller
end

== Authorization Phase ==
note over User, Redis: Subsequent requests with JWT token
User -> Controller: GET /api/protected-resource
activate Controller
Controller -> AuthService: validateToken(jwt)
activate AuthService

AuthService -> Redis: getUserSession(userId)
activate Redis
Redis --> AuthService: SessionData
deactivate Redis

AuthService --> Controller: User permissions
deactivate AuthService
Controller --> User: Protected resource data
deactivate Controller
@enduml
```

### PlantUML-Specific Features

1. **Themes and Styling**:
- Use `!theme plain` or other themes for consistent styling
- Add titles with `title` directive for diagram context
- Use aliases for long participant names: `participant "Long Service Name" as Service`

2. **Lifecycle Management**:
- Use `activate`/`deactivate` to show object lifecycle
- Demonstrates when objects are active in the flow
- Helps visualize resource usage and timing

3. **Grouping and Sections**:
- Use `== Section Name ==` to group related interactions
- Organize complex flows into logical phases
- Improves readability for multi-step processes

4. **Notes and Comments**:
- Add `note over` or `note left/right` for additional context
- Explain business rules or technical constraints
- Document assumptions or important details

### Content Requirements

1. **Accurate Flow Representation**:
- Diagram must reflect actual code flow
- Include realistic method names and parameters
- Show proper return types and responses
- Use activate/deactivate for object lifecycle accuracy

2. **Meaningful Naming**:
- Use actual class and method names from codebase
- Include relevant HTTP endpoints and status codes
- Show meaningful data being passed
- Use participant aliases for readability

3. **Error Handling**:
- Include exception scenarios where appropriate
- Show alternative flows for common error cases
- Demonstrate proper error response patterns
- Use PlantUML's alt/else constructs effectively

4. **Visual Organization**:
- Add descriptive titles to diagrams
- Group related interactions with sections
- Use notes to explain complex business logic
- Apply consistent theming and styling

### Integration with Documentation

#### In README.md Files
- Include diagrams in "Architecture" or "How It Works" sections
- Provide context and explanation for each diagram
- Link diagrams to relevant code sections

#### In package-info.java Files
- Reference sequence diagrams in package descriptions
- Include ASCII art versions for basic flows
- Link to external diagram files when appropriate

### Example Integration

**README.md Section**:
```markdown

## Architecture Overview

### Authentication Flow

The following sequence diagram illustrates the user authentication process:

```plantuml
@startuml
actor User
participant AuthController
participant AuthService
participant TokenService
participant Database

User -> AuthController: POST /api/auth/login
AuthController -> AuthService: authenticateUser(credentials)
AuthService -> Database: findByUsername(username)

alt Valid credentials
Database --> AuthService: User entity
AuthService -> TokenService: generateToken(user)
TokenService --> AuthService: JWT token
AuthService --> AuthController: AuthResult with token
AuthController --> User: 200 OK + JWT token
else Invalid credentials
AuthService --> AuthController: AuthenticationException
AuthController --> User: 401 Unauthorized
end
@enduml
```

This flow demonstrates how the application handles user authentication, including both successful login and failure scenarios.
```

### Validation

After generating sequence diagrams:

1. **Verify accuracy** against actual codebase
2. **Test PlantUML syntax** for proper rendering
3. **Ensure completeness** of main workflow coverage
4. **Validate integration** with documentation structure

### Output Locations

- **README.md files**: Include diagrams in appropriate sections
- **Separate .md files**: Create dedicated diagram files for complex workflows
- **Documentation directories**: Organize diagrams in docs/ or diagrams/ folders


Sequence diagrams must include realistic participants, message labels, alternative fragments for success/failure paths where useful, and method names that match the codebase.
### Step 3: Organize sequence outputs

Follow the user's organization preference:

- Single directory: place sequence `.puml` files under the chosen diagrams directory and use consistent names such as `sequence-order-flow.puml`.
- Organized by type: place files under a sequence-specific folder such as `diagrams/sequence/`.
- Organized by package/domain: group sequence diagrams with the domain or package they explain.
- Integrated documentation: embed or link sequence diagrams from existing architecture or README documentation only after confirming the target file.

Never overwrite existing diagram or documentation files without explicit user consent.
### Step 4: Validate sequence diagrams

Before final delivery:

1. Verify PlantUML syntax for every generated sequence diagram.
2. Re-check each participant and message against the analyzed Java implementation.
3. Confirm alternative and error flows are accurate and not speculative.
4. Confirm file names, links, and documentation references match the selected organization.
5. Summarize generated sequence diagrams, source packages/classes inspected, and any intentionally omitted flows.