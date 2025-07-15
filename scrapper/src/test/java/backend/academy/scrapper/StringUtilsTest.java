package backend.academy.scrapper;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class StringUtilsTest {
    @Test
    void testIsNullOrEmpty_withNull() {
        assertTrue(StringUtils.isNullOrEmpty(null));
    }

    @Test
    void testIsNullOrEmpty_withEmpty() {
        assertTrue(StringUtils.isNullOrEmpty(""));
    }

    @Test
    void testIsNullOrEmpty_withNonEmpty() {
        assertFalse(StringUtils.isNullOrEmpty("abc"));
    }
} 