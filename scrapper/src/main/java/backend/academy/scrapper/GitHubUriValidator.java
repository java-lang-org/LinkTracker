package backend.academy.scrapper;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

public class GitHubUriValidator {
    public static Optional<URI> isValidGitHubUrl(String url) {
        try {
            URI uri = new URI(url);

            if (!("http".equals(uri.getScheme()) || "https".equals(uri.getScheme()))) {
                return Optional.empty();
            }
            if (!"github.com".equals(uri.getHost())) {
                return Optional.empty();
            }
            String[] parts = uri.getPath().split("/");
            if (parts.length == 3 && !parts[1].isEmpty() && !parts[2].isEmpty()) {
                return Optional.of(uri);
            } else {
                return Optional.empty();
            }
        } catch (URISyntaxException e) {
            return Optional.empty();
        }
    }
}
