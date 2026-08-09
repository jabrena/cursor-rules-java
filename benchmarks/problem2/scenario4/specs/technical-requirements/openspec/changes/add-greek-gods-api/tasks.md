## 1. Project setup

- [ ] 1.1 Create or update the Quarkus 3.x Maven module under `benchmarks/problem2/scenario4/demo/` with REST, JDBC datasource, PostgreSQL driver, Flyway, scheduler, REST Client, ArchUnit, and test dependencies.
- [ ] 1.2 Add application configuration for datasource, Flyway, scheduler (default every `5m`; disable or override under `%test`), and external `/greek` source URL.
- [ ] 1.3 Add Flyway V1 migration matching `../../../../functional-requirements/design/schema.sql` for table `greek_god(id, name)`.
- [ ] 1.4 Scaffold Hexagonal packages per `design.md` (`domain`, `application` + `port/in` + `port/out`, `adapter/in`, `adapter/out`) with thin Quarkus bootstrap only at the root.

## 2. Read API (TDD)

- [ ] 2.1 **RED:** Add acceptance test for `GET /api/v1/gods/greek` returning `200`, `Content-Type: application/json`, a JSON array, and objects with integer `id` and string `name`.
- [ ] 2.2 **GREEN:** Implement domain `GreekGod`, outbound `GreekGodRepository` port, JDBC driven adapter (`SELECT id, name FROM greek_god`), inbound `QueryGreekGods` / `GetGreekGodsUseCase`, and REST driving adapter for `GET /api/v1/gods/greek`.
- [ ] 2.3 **Verify:** Run the module build and acceptance tests; confirm `Content-Type: application/json` and fix failures before continuing.
- [ ] 2.4 Add coverage for the known Zeus record scenario (`id`/`name` data-quality check).

## 3. Background synchronization (TDD)

- [ ] 3.1 **RED:** Add synchronization test where the upstream `/greek` response is an array of strings and existing rows keep IDs.
- [ ] 3.2 **GREEN:** Implement inbound `SynchronizeGreekGods` / `SynchronizeGreekGodsUseCase`, outbound `GodSourceClient` port, Quarkus REST Client driven adapter, scheduler driving adapter, and `name`-based upsert (insert new names; retain existing IDs).
- [ ] 3.3 **Verify:** Run the module build and sync tests; confirm upsert behavior, unique `name`, and stable IDs.
- [ ] 3.4 Ensure sync tests stub or fake the external JSON server (or outbound port) and do not depend on the live upstream.

## 4. Contract, errors, architecture, and validation

- [ ] 4.1 Align runtime OpenAPI generation or static documentation with `../../../../functional-requirements/design/greekController-oas.yaml`.
- [ ] 4.2 **RED/GREEN/Verify:** Add a failing-collaborator test for `GET /api/v1/gods/greek` that asserts HTTP `500` and a `problemDetails` response (`Content-Type: application/problem+json` with status `500`); implement exception mapping in the REST driving adapter.
- [ ] 4.3 Add ArchUnit Hexagonal architecture tests (`707`): `domain`/`application` must not depend on `adapter`; `domain` must not depend on `application`; `adapter.in` and `adapter.out` must not depend on each other; assert expected top-level package shape.
- [ ] 4.4 Confirm Gherkin coverage for listed scenarios and review API documentation for `id`/`name` consistency.
- [ ] 4.5 Run the relevant Maven verification for the demo module.
- [ ] 4.6 Run `openspec validate --all` from `benchmarks/problem2/scenario4/specs/technical-requirements`.
