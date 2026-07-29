package info.jab.pml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import org.jspecify.annotations.Nullable;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Inventory of embedded agent assets, loaded from {@code agents.xml}.
 *
 * <p>Inventory {@code @file} entries reference XML sources (for example
 * {@code plinth-architect.xml}). {@link #agentFiles()} exposes the generated
 * Markdown asset names installers and the skills bridge consume
 * ({@code plinth-architect.md}).
 */
public final class AgentIndexes {

    private static final String INVENTORY_RESOURCE = "agents.xml";

    private AgentIndexes() {}

    /**
     * Returns generated agent Markdown asset file names in installation order.
     *
     * @return stream of agent file names, for example {@code plinth-architect.md}
     */
    public static Stream<String> agentFiles() {
        return loadInventorySources().stream().map(AgentIndexes::toMarkdownFileName);
    }

    /**
     * Returns agent XML source file names in installation order.
     *
     * @return stream of agent source file names, for example {@code plinth-architect.xml}
     */
    public static Stream<String> agentSources() {
        return loadInventorySources().stream();
    }

    static List<String> loadInventorySources() {
        try (InputStream stream = getResource(INVENTORY_RESOURCE)) {
            if (stream == null) {
                throw new RuntimeException("Agent inventory not found: " + INVENTORY_RESOURCE);
            }
            return parseInventory(stream);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load agent inventory", e);
        }
    }

    /**
     * Maps an inventory XML source file name to the generated Markdown asset name.
     *
     * @param sourceFile inventory {@code @file} value ending in {@code .xml}
     * @return corresponding Markdown file name ending in {@code .md}
     */
    static String toMarkdownFileName(String sourceFile) {
        if (!sourceFile.endsWith(".xml")) {
            throw new IllegalArgumentException("Agent source must be an XML file name: " + sourceFile);
        }
        return sourceFile.substring(0, sourceFile.length() - ".xml".length()) + ".md";
    }

    private static List<String> parseInventory(InputStream in) {
        try {
            Element root = InventoryXmlLoader.parse(in).getDocumentElement();
            if (!"agent-inventory".equals(root.getNodeName())) {
                throw new RuntimeException("Agent inventory root must be <agent-inventory>");
            }

            NodeList agentNodes = root.getElementsByTagName("agent");
            List<String> agentSources = new ArrayList<>();
            for (int i = 0; i < agentNodes.getLength(); i++) {
                if (!(agentNodes.item(i) instanceof Element agentEl)) {
                    continue;
                }
                if (!agentEl.getParentNode().isSameNode(root)) {
                    continue;
                }
                String file = agentEl.getAttribute("file").trim();
                if (file.isEmpty()) {
                    throw new RuntimeException("agent-inventory entry missing file attribute");
                }
                if (file.startsWith("/") || file.contains("..") || !file.endsWith(".xml")) {
                    throw new RuntimeException("Agent file must be an XML source file name: " + file);
                }
                agentSources.add(file);
            }
            if (agentSources.isEmpty()) {
                throw new RuntimeException("Agent inventory must contain at least one <agent> entry");
            }
            return List.copyOf(agentSources);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse agent inventory", e);
        }
    }

    private static @Nullable InputStream getResource(String name) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
            cl = AgentIndexes.class.getClassLoader();
        }
        InputStream in = cl.getResourceAsStream(name);
        if (in == null && !Objects.equals(cl, AgentIndexes.class.getClassLoader())) {
            in = AgentIndexes.class.getClassLoader().getResourceAsStream(name);
        }
        return in;
    }
}
