# spring-petclinic wiki — schema

Layer 3: this document defines the structure and conventions for this wiki. It co-evolves with the domain; propose amendments explicitly rather than drifting from it.

## Domain and scope

Knowledge base tracking the [spring-projects/spring-petclinic](https://github.com/spring-projects/spring-petclinic) reference application: its architecture, domain model, and how it demonstrates Spring Boot conventions.

## Layout

- `raw/` — Layer 1, immutable, user-curated raw sources
  - `raw/repositories/<slug>/<commit-sha>.md` — commit-pinned reference record for an ingested git repository (origin URL, full commit SHA, scope inspected); never a copy of the repository's content
- `wiki/` — Layer 2, LLM-written
  - `wiki/index.md` — catalogue of every page, one line each, grouped by category
  - `wiki/log.md` — append-only activity log, one entry per operation
  - `wiki/sources/` — source-summary pages, one per ingested source
  - `wiki/entities/` — entity pages (applications, modules, components)
  - `wiki/concepts/` — concept pages (created when the domain introduces a reusable idea worth its own page)

## Page conventions

- YAML frontmatter: `type` (`source`, `entity`, `concept`, or `analysis`), `tags`, `updated` (`YYYY-MM-DD`), `sources` (slugs the page draws on)
- Cross-references use `[[wikilink]]` syntax to another page's filename (without extension)
- Cite git-repository evidence as `<repository>@<commit-sha>:path/to/file`, pointing to the reference document under `raw/repositories/` for full provenance

## log.md entry format

```
## [YYYY-MM-DD] <operation> | <title>

<what changed: pages added/updated, contradictions flagged>
```

`<operation>` is one of `ingest`, `query`, or `lint`.
