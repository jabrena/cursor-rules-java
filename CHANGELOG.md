# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.7.0]

### Added

- **Java Checklist Guide**: Added `@100-java-checklist-guide` cursor rule to help developers use cursor rules effectively (#59)
- **Maven Documentation**: Added `@112-java-maven-documentation` cursor rule to generate README-DEV.md from existing pom.xml files
- **Maven Dependencies & Plugins**: Added `@111-java-maven-deps-and-plugins` cursor rule for better Maven dependency management
- **Template System**: Added multiple template files to support cursor rule generation
- **Example Project**: Added complete Maven demo project (`example/maven-demo/`) to test the new features

### Changed

- **Rule Organization**: Reorganized cursor rule numbering system for better categorization

### Removed

- **Cache Files**: Removed Maven cache files that were not useful for daily development work (#44)

### Fixed

- **Maven Plugins**: Improved cursor rules for Maven plugins with better examples and guidance (#54, #56)

## [0.6.0] 30/5/2025

### Added

- Added a new cursor rules about Maven dependencies & plugins

### Changed

- Removed Cursor rules about Books for clarity
- Moved the Cursor rule about Acceptance criterias as part of the repository about [Agile](https://github.com/jabrena/cursor-rules-agile)
- Increased consistency in all cursor rules, now all examples use asserts from AssertJ

## [0.5.0] 20/05/2025

### Added

- Added new cursor rules (Maven, Acceptance Criteria, Object oriented design, Type Design, Secure codign guidelines, REST API Design)
- Added template for future cursor rules
- Added JEP inventory

### Changed

- Updated all cursor rules

## [0.4.0] 27/04/2025

### Added

- Added new cursor rules (Refactoring, Unit Testing & Integration testing)

### Changed

- Updated all cursor rules

## [0.3.0] 06/04/2025

### Added

- Added new cursor rules (SQL, Logging, Modern Java features)

### Changed

- Updated cursor rules (Java, Effective Java, Concurrency, Functional programming, Data Oriented programming Pragmatic Unit Testing, Spring Boot & Quarkus)


## [0.2.0] 01/03/2025

### Added

- Added new cursor rules (Pragmatic Unit Testing, Quarkus)

### Changed

- Updated cursor rules (Java, Effective Java, Concurrency, Functional programming, Data Oriented programming & Spring Boot)

## [0.1.0] 09/02/2025

### Added

- Added initial cursor rules (Java, Effective Java, Concurrency, Functional programming, Data Oriented programming & Spring Boot)