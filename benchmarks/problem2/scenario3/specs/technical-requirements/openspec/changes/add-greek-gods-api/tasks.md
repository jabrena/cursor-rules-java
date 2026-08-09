## 1. Project setup

- [ ] 1.1 Create or update the Quarkus 3.x Maven module under `benchmarks/problem2/scenario3/demo/` with REST, JDBC datasource, PostgreSQL driver, scheduler, and test dependencies.
- [ ] 1.2 Add application configuration for datasource, scheduler, and external `/greek` source URL.
- [ ] 1.3 Add schema migration matching `../../../../functional-requirements/design/schema.sql` for table `greek_god(id, name)`.

## 2. Read API (TDD)

- [ ] 2.1 **RED:** Add acceptance test for `GET /api/v1/gods/greek` returning `200`, JSON array, and objects with integer `id` and string `name`.
- [ ] 2.2 **GREEN:** Implement domain/record type, JDBC repository (`SELECT id, name FROM greek_god`), service, and REST resource for `GET /api/v1/gods/greek`.
- [ ] 2.3 **Verify:** Run the module build and acceptance tests; fix failures before continuing.
- [ ] 2.4 Add coverage for the known Zeus record scenario (`id`/`name` data-quality check).

## 3. Background synchronization (TDD)

- [ ] 3.1 **RED:** Add synchronization test where the upstream `/greek` response is an array of strings and existing rows keep IDs.
- [ ] 3.2 **GREEN:** Implement the scheduled updater, external HTTP client, and `name`-based upsert (insert new names; retain existing IDs).
- [ ] 3.3 **Verify:** Run the module build and sync tests; confirm upsert behavior, unique `name`, and stable IDs.
- [ ] 3.4 Ensure sync tests stub or fake the external JSON server and do not depend on the live upstream.

## 4. Contract, errors, and validation

- [ ] 4.1 Align runtime OpenAPI generation or static documentation with `../../../../functional-requirements/design/greekController-oas.yaml`.
- [ ] 4.2 Ensure unexpected server failures on the read path can surface as HTTP `500`.
- [ ] 4.3 Confirm Gherkin coverage for both listed scenarios and review API documentation for `id`/`name` consistency.
- [ ] 4.4 Run the relevant Maven verification for the demo module.
- [ ] 4.5 Run `openspec validate --all` from `benchmarks/problem2/scenario3/specs/technical-requirements`.
