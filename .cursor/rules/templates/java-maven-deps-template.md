# Java Maven Dependencies

This template provides Maven dependency configurations that should be added conditionally based on user selections from the main Maven dependencies and plugins rule.

**Usage**: Reference this template from the main rule rather than duplicating configurations.

## Code Quality Dependencies

### Add JSpecify for Null Safety

**When to use**: Recommended for all projects to improve null safety and code documentation.
**User question**: "Do you want to use JSpecify nullness annotations for better null safety? (y/n)"

**Benefits of JSpecify**:
- Provides standardized nullness annotations (@Nullable, @NonNull)
- Improves code safety and documentation
- Works with static analysis tools like NullAway and Error Prone
- Compile-time only dependency (scope: provided)
- Better tooling support compared to legacy JSR-305

Update the pom.xml with this dependency:

```xml
<dependencies>
  <!-- Null Safety Annotations -->
  <dependency>
    <groupId>org.jspecify</groupId>
    <artifactId>jspecify</artifactId>
    <version>${jspecify.version}</version>
    <scope>provided</scope>
  </dependency>
</dependencies>
```

**If JSpecify selected**:
```xml
<!-- Dependency versions -->
<jspecify.version>1.0.0</jspecify.version>
```

**Usage Example**:
```java
import org.jspecify.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class Example {
    public @Nullable String processInput(@NonNull String input) {
        if (input.isEmpty()) {
            return null; // Explicitly nullable return
        }
        return input.toUpperCase();
    }
}
```