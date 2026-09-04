# Knowledge base examples

Example knowledge bases scaffolded by the `202-knowledge-base` skill, plus a small
JBang tool to browse a wiki's Markdown pages as HTML.

## Layout

- `spring-petclinic/` — an example knowledge base with a `wiki/` directory
- `knowledge-base-base/` — another example knowledge base, with a different home page
  (`wiki/MOC.md` instead of `wiki/index.md`)
- `scripts/WikiServer.java` — a JBang web server that renders a wiki's `.md` pages,
  resolving `[[wikilink]]` references between them

## Run the wiki server

Requires [JBang](https://www.jbang.dev/). Pass the knowledge base directory (or its
`wiki/` subdirectory directly) and, optionally, a port (default `8787`):

```bash
jbang examples/knowledge-base/scripts/WikiServer.java examples/knowledge-base/spring-petclinic
```

Then open <http://localhost:8787/>. The server picks a home page automatically
(`index.md`, then `MOC.md`, then the first root-level page), builds a sidebar grouped
by folder (`sources/`, `entities/`, ...), and resolves `[[wikilink]]` /
`[[wikilink|label]]` syntax to page links. Pages are re-read from disk on every
request, so edits to the wiki show up on refresh without restarting the server.

To serve the other example wiki, or on a different port:

```bash
jbang examples/knowledge-base/scripts/WikiServer.java examples/knowledge-base/knowledge-base-base 8788
```

Stop the server with `Ctrl+C`.
