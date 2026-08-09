## ADDED Requirements

### Requirement: Greek gods HTTP retrieval endpoint

The system MUST expose `GET /api/v1/gods/greek` that returns synchronized Greek
god records from the local database as a JSON array of objects.

#### Scenario: Happy path returns Greek god records

- **GIVEN** the Greek Gods API service is running
- **AND** the API base URL is `http://localhost:8080`
- **WHEN** a client sends `GET /api/v1/gods/greek`
- **THEN** the response status is `200`
- **AND** the response content type is JSON
- **AND** the response body is a list of Greek gods
- **AND** each god has attributes `id` (integer) and `name` (string)

#### Scenario: Known synchronized god is present

- **GIVEN** the local database contains a synchronized record for Zeus with `id` `1`
- **WHEN** a client sends `GET /api/v1/gods/greek`
- **THEN** the response status is `200`
- **AND** the response contains a god with `id` `1` and `name` `Zeus`

#### Scenario: Successful response uses application JSON

- **WHEN** a valid retrieval request is processed
- **THEN** the response status is `200`
- **AND** the response `Content-Type` is `application/json`
- **AND** the response body is a JSON array of objects with required fields `id` and `name`

### Requirement: Server error handling

The system MUST return HTTP `500 Internal Server Error` with a `problemDetails`
response when an unexpected server failure prevents retrieval.

#### Scenario: Unexpected server failure

- **WHEN** an unexpected server failure occurs while handling `GET /api/v1/gods/greek`
- **THEN** the response status is `500`
- **AND** the response `Content-Type` is `application/problem+json`
- **AND** the response body is a `problemDetails` document with status `500`

### Requirement: PostgreSQL persistence for Greek gods

The system MUST persist Greek god records in PostgreSQL table `greek_god` with a
generated integer primary key `id` and a required unique `name`.

#### Scenario: Schema supports id and unique name

- **WHEN** the database schema is applied
- **THEN** table `greek_god` exists with column `id` as a generated primary key
- **AND** column `name` is required and unique
- **AND** duplicate names are rejected by the database

#### Scenario: Read path queries local records

- **WHEN** a client calls `GET /api/v1/gods/greek`
- **THEN** the system reads `id` and `name` from `greek_god`
- **AND** the system does not proxy the external JSON server for that request

### Requirement: Background synchronization by name

The system MUST periodically synchronize Greek god names from
`https://my-json-server.typicode.com/jabrena/latency-problems/greek`, treating
the upstream payload as a JSON array of strings and upserting by natural key
`name` while preserving existing IDs.

#### Scenario: Upstream payload is an array of names

- **WHEN** the background updater fetches the external `/greek` endpoint
- **THEN** the system accepts a JSON array of strings
- **AND** each string is treated as a Greek god name for synchronization

#### Scenario: New names are inserted with generated IDs

- **GIVEN** the local database does not contain name `Hera`
- **WHEN** synchronization receives upstream names that include `Hera`
- **THEN** a new `greek_god` row is inserted for `Hera`
- **AND** the new row receives a generated `id`

#### Scenario: Existing names keep stable IDs

- **GIVEN** the local database already contains `{ "id": 1, "name": "Zeus" }`
- **WHEN** synchronization receives an upstream list that includes `Zeus`
- **THEN** the existing row for `Zeus` retains `id` `1`
- **AND** no duplicate `Zeus` row is created

#### Scenario: Name is the upsert natural key

- **WHEN** synchronization processes upstream names
- **THEN** matching uses `name` as the natural key
- **AND** only names absent from `greek_god` are inserted

### Requirement: Hexagonal architecture boundaries

The system MUST structure the demo module with Hexagonal Ports and Adapters
boundaries so the application core remains independent of concrete adapters,
following Plinth skill `707-technologies-hexagonal-architecture`.

#### Scenario: Core does not depend on adapters

- **WHEN** architecture dependency rules are evaluated
- **THEN** classes in `domain` and `application` packages do not depend on
  `adapter` packages
- **AND** classes in `domain` do not depend on `application` packages

#### Scenario: Adapters respect driving and driven direction

- **WHEN** architecture dependency rules are evaluated
- **THEN** driving adapters under `adapter.in` call inbound application ports
  and do not depend on `adapter.out` packages
- **AND** driven adapters under `adapter.out` implement outbound application
  ports and do not depend on `adapter.in` packages

#### Scenario: Package shape exposes Hexagonal layers

- **WHEN** the main source tree is inspected
- **THEN** top-level application packages include `domain`, `application`, and
  `adapter`
- **AND** application ports are declared under `application.port.in` and
  `application.port.out`
