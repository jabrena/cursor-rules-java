package info.jab.pml;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Command Installer Parity Tests")
class CommandInstallerParityTest {

    @Test
    @DisplayName("Command installer resources must match the manifest bundle")
    void should_includeExactCommands_when_commandInstallerRegistersResources() {
        List<String> commandAssets = SkillIndexes.skillDescriptors()
            .filter(descriptor -> "004-commands-installation".equals(descriptor.skillId()))
            .findFirst()
            .orElseThrow()
            .resources()
            .stream()
            .map(SkillIndexes.SkillResource::targetPath)
            .toList();

        assertThat(commandAssets)
            .startsWith("assets/commands/onboarding.md", "assets/commands/update-issue.md")
            .containsExactlyElementsOf(CommandIndexes.commandFiles()
                .map(commandFile -> "assets/commands/" + commandFile)
                .toList());
    }
}
