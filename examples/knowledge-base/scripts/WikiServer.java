///usr/bin/env jbang "$0" "$@" ; exit $?

//JAVA 25
//DEPS org.commonmark:commonmark:0.21.0

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

/// Serves a `[[wikilink]]`-style Markdown wiki (as produced by the 202-knowledge-base skill) as browsable HTML.
///
/// Usage: jbang WikiServer.java [path-to-knowledge-base-or-wiki-dir] [port]
public class WikiServer {

    private static final Pattern WIKILINK = Pattern.compile("\\[\\[([^\\[\\]|]+)(?:\\|([^\\[\\]]+))?]]");
    private static final Pattern FRONT_MATTER = Pattern.compile("\\A---\\r?\\n(.*?)\\r?\\n---\\r?\\n?", Pattern.DOTALL);

    record Page(String id, String slug, String category, Path path) {}

    public static void main(String... args) throws IOException {
        Path root = resolveWikiRoot(Path.of(args.length > 0 ? args[0] : "."));
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 8787;

        List<Page> pages = scan(root);
        if (pages.isEmpty()) {
            System.err.println("No Markdown pages found under " + root.toAbsolutePath());
            System.exit(1);
        }
        Map<String, List<Page>> bySlug = indexBySlug(pages);
        Map<String, List<Page>> byCategory = groupByCategory(pages);
        Page home = findHome(pages);

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        server.createContext("/", exchange -> handle(exchange, root, pages, bySlug, byCategory, home));
        server.start();

        System.out.println("Serving wiki from " + root.toAbsolutePath());
        System.out.println("http://localhost:" + port + "/");
    }

    private static Path resolveWikiRoot(Path input) {
        if (!Files.isDirectory(input)) {
            System.err.println("Not a directory: " + input.toAbsolutePath());
            System.exit(1);
        }
        Path wikiSubdir = input.resolve("wiki");
        return Files.isDirectory(wikiSubdir) ? wikiSubdir : input;
    }

    private static List<Page> scan(Path wikiRoot) throws IOException {
        try (var walk = Files.walk(wikiRoot)) {
            return walk.filter(p -> p.toString().endsWith(".md"))
                    .filter(Files::isRegularFile)
                    .map(p -> toPage(wikiRoot, p))
                    .sorted(Comparator.comparing(Page::category).thenComparing(Page::id))
                    .collect(Collectors.toList());
        }
    }

    private static Page toPage(Path wikiRoot, Path file) {
        Path relative = wikiRoot.relativize(file);
        String id = stripExtension(relative.getFileName().toString());
        Path parent = relative.getParent();
        String category = parent == null ? "" : parent.toString().replace('\\', '/');
        return new Page(id, slug(id), category, file);
    }

    private static String stripExtension(String fileName) {
        return fileName.substring(0, fileName.length() - ".md".length());
    }

    private static String slug(String id) {
        return id.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", "-");
    }

    private static Map<String, List<Page>> indexBySlug(List<Page> pages) {
        Map<String, List<Page>> index = new TreeMap<>();
        for (Page page : pages) {
            index.computeIfAbsent(page.slug(), k -> new ArrayList<>()).add(page);
        }
        return index;
    }

    private static Map<String, List<Page>> groupByCategory(List<Page> pages) {
        Map<String, List<Page>> grouped = new TreeMap<>();
        for (Page page : pages) {
            grouped.computeIfAbsent(page.category(), k -> new ArrayList<>()).add(page);
        }
        return grouped;
    }

    private static Page findHome(List<Page> pages) {
        return pages.stream().filter(p -> p.category().isEmpty() && p.id().equalsIgnoreCase("index"))
                .findFirst()
                .or(() -> pages.stream().filter(p -> p.category().isEmpty() && p.id().equalsIgnoreCase("moc")).findFirst())
                .or(() -> pages.stream().filter(p -> p.category().isEmpty()).findFirst())
                .orElse(pages.get(0));
    }

    private static void handle(HttpExchange exchange, Path root, List<Page> pages, Map<String, List<Page>> bySlug,
                                Map<String, List<Page>> byCategory, Page home) throws IOException {
        try {
            URI uri = exchange.getRequestURI();
            String path = URLDecoder.decode(uri.getPath(), StandardCharsets.UTF_8);

            if (path.equals("/")) {
                redirect(exchange, "/page/" + home.slug());
                return;
            }
            if (path.startsWith("/page/")) {
                String requestedSlug = slug(path.substring("/page/".length()));
                List<Page> matches = bySlug.get(requestedSlug);
                if (matches == null || matches.isEmpty()) {
                    respond(exchange, 404, renderNotFound(requestedSlug, byCategory, home));
                    return;
                }
                Page page = matches.get(0);
                String body = Files.readString(page.path());
                respond(exchange, 200, renderPage(page, body, matches.size() > 1 ? matches : List.of(),
                        byCategory, bySlug, home));
                return;
            }
            respond(exchange, 404, renderNotFound(path, byCategory, home));
        } finally {
            exchange.close();
        }
    }

    private static void redirect(HttpExchange exchange, String location) throws IOException {
        exchange.getResponseHeaders().add("Location", location);
        exchange.sendResponseHeaders(302, -1);
    }

    private static void respond(HttpExchange exchange, int status, String html) throws IOException {
        byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "text/html; charset=utf-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static String renderPage(Page page, String raw, List<Page> ambiguousWith, Map<String, List<Page>> byCategory,
                                      Map<String, List<Page>> bySlug, Page home) {
        Matcher fm = FRONT_MATTER.matcher(raw);
        String frontMatter = null;
        String body = raw;
        if (fm.find()) {
            frontMatter = fm.group(1);
            body = raw.substring(fm.end());
        }

        String withResolvedLinks = replaceWikiLinks(body, bySlug);
        Parser parser = Parser.builder().build();
        Node document = parser.parse(withResolvedLinks);
        String contentHtml = HtmlRenderer.builder().build().render(document);

        StringBuilder html = new StringBuilder();
        html.append(HEAD.formatted(escape(page.id())));
        html.append("<div class=\"layout\">");
        html.append(renderSidebar(byCategory, home, page));
        html.append("<main>");
        html.append("<div class=\"breadcrumb\">").append(escape(page.category().isEmpty() ? "/" : page.category())).append("</div>");
        html.append("<h1>").append(escape(page.id())).append("</h1>");
        if (!ambiguousWith.isEmpty()) {
            html.append("<p class=\"warn\">Multiple pages share this name: ");
            for (Page other : ambiguousWith) {
                html.append("<code>").append(escape(other.category())).append("/").append(escape(other.id())).append("</code> ");
            }
            html.append("&mdash; showing the first match.</p>");
        }
        if (frontMatter != null) {
            html.append("<pre class=\"frontmatter\">").append(escape(frontMatter.strip())).append("</pre>");
        }
        html.append("<article>").append(contentHtml).append("</article>");
        html.append("</main></div></body></html>");
        return html.toString();
    }

    private static String renderNotFound(String requested, Map<String, List<Page>> byCategory, Page home) {
        StringBuilder html = new StringBuilder();
        html.append(HEAD.formatted("Not found"));
        html.append("<div class=\"layout\">");
        html.append(renderSidebar(byCategory, home, null));
        html.append("<main><h1>Page not found</h1><p>No wiki page matches <code>")
                .append(escape(requested)).append("</code>.</p></main></div></body></html>");
        return html.toString();
    }

    private static String renderSidebar(Map<String, List<Page>> byCategory, Page home, Page current) {
        StringBuilder nav = new StringBuilder();
        nav.append("<nav class=\"sidebar\"><h2><a href=\"/page/").append(home.slug()).append("\">Wiki</a></h2>");
        for (Map.Entry<String, List<Page>> entry : byCategory.entrySet()) {
            String category = entry.getKey().isEmpty() ? "(root)" : entry.getKey();
            nav.append("<h3>").append(escape(category)).append("</h3><ul>");
            for (Page page : entry.getValue()) {
                boolean active = current != null && current.path().equals(page.path());
                nav.append("<li><a class=\"").append(active ? "active" : "").append("\" href=\"/page/")
                        .append(page.slug()).append("\">").append(escape(page.id())).append("</a></li>");
            }
            nav.append("</ul>");
        }
        nav.append("</nav>");
        return nav.toString();
    }

    private static String replaceWikiLinks(String body, Map<String, List<Page>> bySlug) {
        Matcher matcher = WIKILINK.matcher(body);
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            String target = matcher.group(1).trim();
            String label = matcher.group(2) != null ? matcher.group(2).trim() : target;
            String targetSlug = slug(target);
            String replacement = bySlug.containsKey(targetSlug)
                    ? "[" + label + "](/page/" + targetSlug + ")"
                    : label + " *(missing: " + target + ")*";
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    private static String escape(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }

    private static final String HEAD = """
            <!doctype html>
            <html>
            <head>
            <meta charset="utf-8">
            <meta name="viewport" content="width=device-width, initial-scale=1">
            <title>%s &middot; Wiki</title>
            <style>
              :root { color-scheme: light dark; }
              body { margin: 0; font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif; }
              .layout { display: flex; min-height: 100vh; }
              .sidebar { width: 240px; flex-shrink: 0; padding: 1rem; border-right: 1px solid #8884; overflow-y: auto; }
              .sidebar h2 { font-size: 1rem; margin-top: 0; }
              .sidebar h2 a { text-decoration: none; color: inherit; }
              .sidebar h3 { font-size: 0.75rem; text-transform: uppercase; letter-spacing: 0.05em; opacity: 0.6; margin: 1rem 0 0.25rem; }
              .sidebar ul { list-style: none; margin: 0; padding: 0; }
              .sidebar li a { display: block; padding: 0.15rem 0; text-decoration: none; color: inherit; opacity: 0.85; }
              .sidebar li a.active { font-weight: 600; opacity: 1; }
              main { flex: 1; padding: 1.5rem 2rem; max-width: 48rem; }
              .breadcrumb { font-size: 0.8rem; opacity: 0.6; text-transform: uppercase; letter-spacing: 0.05em; }
              .frontmatter { background: #8882; padding: 0.75rem 1rem; border-radius: 6px; font-size: 0.85rem; overflow-x: auto; }
              .warn { background: #ffcc0033; padding: 0.5rem 0.75rem; border-radius: 6px; }
              article code, .frontmatter { font-family: ui-monospace, SFMono-Regular, monospace; }
              article pre { background: #8882; padding: 0.75rem 1rem; border-radius: 6px; overflow-x: auto; }
              article a { color: #3b82f6; }
            </style>
            </head>
            <body>
            """;
}
