package backend.academy.scrapper;

import java.net.URI;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UrlValidatorTest {
    @Test
    void testValidGitHubUrl() {
        // Arrange
        String url = "https://github.com/user/repo";

        // Act
        Optional<URI> result = UrlValidator.isValidGitHubUrl(url);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("https://github.com/user/repo", result.get().toString());
    }

    @Test
    void testValidGitHubUrlWithoutHttps() {
        // Arrange
        String url = "http://github.com/user/repo";

        // Act
        Optional<URI> result = UrlValidator.isValidGitHubUrl(url);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(url, result.get().toString());
    }

    @Test
    void testInvalidGitHubUrl_MissingRepo() {
        // Arrange
        String url = "https://github.com/user/";

        // Act
        Optional<URI> result = UrlValidator.isValidGitHubUrl(url);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testInvalidGitHubUrl_WrongHost() {
        // Arrange
        String url = "https://gitlab.com/user/repo";

        // Act
        Optional<URI> result = UrlValidator.isValidGitHubUrl(url);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testValidStackOverflowUrl() {
        // Arrange
        String url = "https://stackoverflow.com/questions/12345/example";

        // Act
        Optional<URI> result = UrlValidator.isValidStackOverflowUrl(url);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(url, result.get().toString());
    }

    @Test
    void testValidStackOverflowUrlWithoutTitle() {
        // Arrange
        String url = "https://stackoverflow.com/questions/12345";

        // Act
        Optional<URI> result = UrlValidator.isValidStackOverflowUrl(url);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(url, result.get().toString());
    }

    @Test
    void testValidStackOverflowUrlHttp() {
        // Arrange
        String url = "http://stackoverflow.com/questions/12345";

        // Act
        Optional<URI> result = UrlValidator.isValidStackOverflowUrl(url);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(url, result.get().toString());
    }

    @Test
    void testInvalidStackOverflowUrl_NoId() {
        // Arrange
        String url = "https://stackoverflow.com/questions/";

        // Act
        Optional<URI> result = UrlValidator.isValidStackOverflowUrl(url);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testInvalidStackOverflowUrl_WrongPath() {
        // Arrange
        String url = "https://stackoverflow.com/question/12345";

        // Act
        Optional<URI> result = UrlValidator.isValidStackOverflowUrl(url);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testInvalidStackOverflowUrl_WrongHost() {
        // Arrange
        String url = "https://stackexchange.com/questions/12345";

        // Act
        Optional<URI> result = UrlValidator.isValidStackOverflowUrl(url);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testInvalidUrlFormat() {
        // Arrange
        String url = "not-a-url";

        // Act
        Optional<URI> result = UrlValidator.isValidGitHubUrl(url);

        // Assert
        assertFalse(result.isPresent());
    }
}
