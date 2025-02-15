package backend.academy.scrapper;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.regex.Pattern;

public class UrlValidator {
    private static final Pattern GITHUB_PATTERN = Pattern.compile("^/([^/]+)/([^/]+)$");
    private static final Pattern STACKOVERFLOW_PATTERN = Pattern.compile("^/questions/(\\d+)(?:/[^/]*)?$");

    public static Optional<URI> isValidGitHubUrl(String url) {
        return validateUrl(url, "github.com", GITHUB_PATTERN);
    }

    public static Optional<URI> isValidStackOverflowUrl(String url) {
        return validateUrl(url, "stackoverflow.com", STACKOVERFLOW_PATTERN);
    }

    private static Optional<URI> validateUrl(String url, String expectedHost, Pattern pattern) {
        try {
            URI uri = new URI(url);

            if (!isValidScheme(uri.getScheme()) || !isValidHost(uri.getHost(), expectedHost)) {
                return Optional.empty();
            }

            if (!pattern.matcher(uri.getPath()).matches()) {
                return Optional.empty();
            }

            return Optional.of(uri);
        } catch (URISyntaxException e) {
            return Optional.empty();
        }
    }

    private static boolean isValidScheme(String scheme) {
        return scheme.equals("http") || scheme.equals("https");
    }

    private static boolean isValidHost(String actual, String expected) {
        return actual.equals(expected);
    }
}
