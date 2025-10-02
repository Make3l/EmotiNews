import com.majkel.emotinews.utils.InputSanitizer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class InputSanitizerTest {
    @Test
    @DisplayName("Should trim and collapse whitespace sequences into single spaces")
    public void trimAndCollapseSpaces() {
        List<String> inputs = new ArrayList<>(Arrays.asList(
                " leading space",
                "following space ",
                "  multiple   spaces   "
        ));

        List<String> actual = inputs.stream()
                .map(InputSanitizer::filterTopic)
                .collect(Collectors.toList());

        List<String> expected = List.of(
                "leading space",
                "following space",
                "multiple spaces"
        );

        assertEquals(expected, actual, "Sanitizer should trim and collapse multi-space sequences");
    }

    @Test
    @DisplayName("Should truncate to maximum of 128 code points (not Java chars)")
    public void truncateTo128CodePoints() {
        // 130-character example (contains only ASCII so code points == length here)
        String longText = "This is a simple example text that contains only English alphabet letters and spaces, reaching exactly one hundred thirty chars14589";
        String sanitized = InputSanitizer.filterTopic(longText);

        int codePoints = sanitized.codePointCount(0, sanitized.length());
        assertEquals(128, codePoints,
                "Sanitized text must be truncated to 128 code points; actual: " + codePoints + " -> " + sanitized);
    }

    @Test
    @DisplayName("Should remove control characters (invisible control codes)")
    public void removeControlCharacters() {
        String input = "Hello\u0007World"; // contains BEL (U+0007)
        String sanitized = InputSanitizer.filterTopic(input);
        assertEquals("HelloWorld", sanitized, "Control characters should be removed from the input");
    }

    @Test
    @DisplayName("Should remove backslashes from input")
    public void removeBackslashes() {
        String input = "Hello\\\\World"; // Java literal for Hello\World
        String sanitized = InputSanitizer.filterTopic(input);
        assertEquals("HelloWorld", sanitized, "Backslashes should be removed from the input");
    }

    @Test
    @DisplayName("Should return empty string for null input")
    public void returnEmptyStringForNullInput(){
        String sanitized=InputSanitizer.filterTopic(null);
        assertNotNull(sanitized);
        assertTrue(sanitized.isEmpty());
    }

    @Test
    @DisplayName("Should return empty string for empty input")
    public void returnEmptyStringForEmptyInput(){
        String sanitized=InputSanitizer.filterTopic("");
        assertNotNull(sanitized);
        assertTrue(sanitized.isEmpty());
    }
}
