package info.jab.pml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.w3c.dom.Document;

/**
 * CLI entry point that converts agent XML sources under a directory into
 * Cursor-compatible Markdown files for installers and the skills bridge.
 *
 * <p>Transformation uses classpath stylesheet {@code agent-to-markdown.xsl}
 * (see {@link AgentMarkdownRenderer}). The XML directory is read-only input;
 * generated Markdown is written only under {@code target/}.
 *
 * <p>Usage: {@code AgentMarkdownGenerator <agentsXmlDir> <outputDir> [extraOutputDir...]}
 */
public final class AgentMarkdownGenerator {

    private AgentMarkdownGenerator() {}

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            throw new IllegalArgumentException(
                "Usage: AgentMarkdownGenerator <agentsXmlDir> <outputDir> [extraOutputDir...]"
            );
        }
        Path agentsXmlDir = Path.of(args[0]).toAbsolutePath().normalize();
        if (!Files.isDirectory(agentsXmlDir)) {
            throw new IllegalArgumentException("Agents XML directory not found: " + agentsXmlDir);
        }

        List<Path> outputDirs = new ArrayList<>();
        for (int i = 1; i < args.length; i++) {
            outputDirs.add(Path.of(args[i]).toAbsolutePath().normalize());
        }

        List<Path> xmlFiles = listAgentXmlFiles(agentsXmlDir);
        if (xmlFiles.isEmpty()) {
            throw new IllegalStateException("No agent XML files found in " + agentsXmlDir);
        }

        for (Path xmlFile : xmlFiles) {
            String markdown = renderFile(xmlFile);
            String mdFileName = toMarkdownFileName(xmlFile.getFileName().toString());
            for (Path outputDir : outputDirs) {
                Files.createDirectories(outputDir);
                Path target = outputDir.resolve(mdFileName);
                Files.writeString(target, markdown, StandardCharsets.UTF_8);
            }
            System.out.println("Generated " + mdFileName + " from " + xmlFile.getFileName());
        }
    }

    static String renderFile(Path xmlFile) throws Exception {
        try (InputStream in = Files.newInputStream(xmlFile)) {
            Document document = InventoryXmlLoader.parse(in);
            return AgentMarkdownRenderer.render(document);
        }
    }

    static String toMarkdownFileName(String xmlFileName) {
        if (!xmlFileName.endsWith(".xml")) {
            throw new IllegalArgumentException("Expected .xml file name: " + xmlFileName);
        }
        return xmlFileName.substring(0, xmlFileName.length() - ".xml".length()) + ".md";
    }

    private static List<Path> listAgentXmlFiles(Path agentsDir) throws IOException {
        List<Path> files = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(agentsDir, "plinth-*.xml")) {
            for (Path path : stream) {
                if (Files.isRegularFile(path)) {
                    files.add(path);
                }
            }
        }
        files.sort(Comparator.comparing(path -> path.getFileName().toString()));
        return List.copyOf(files);
    }
}
