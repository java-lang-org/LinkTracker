package backend.academy.scrapper;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@EqualsAndHashCode
public class Link {
    private final String url;
    private final URI uri;
    private final LinkType linkType;

    @Setter
    private ZonedDateTime lastUpdate;

    public static Link getInstance(String url) {
        ZonedDateTime now = DateTimeUtils.now();
        return UrlValidator.isValidGitHubUrl(url)
                .map(uri -> new Link(url, uri, LinkType.GITHUB, now))
                .or(() -> UrlValidator.isValidStackOverflowUrl(url)
                        .map(uri -> new Link(url, uri, LinkType.STACK_OVERFLOW, now)))
                .orElseThrow(() -> new InvalidRequestException("Invalid link: " + url));
    }

    public static Link getInstance(String url, LinkType linkType, ZonedDateTime zonedDateTime) {
        try {
            return new Link(url, new URI(url), linkType, zonedDateTime);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Incorrect url: ", e);
        }
    }

    private Link(String url, URI uri, LinkType linkType, ZonedDateTime lastUpdate) {
        this.url = url;
        this.uri = uri;
        this.linkType = linkType;
        this.lastUpdate = lastUpdate;
    }
}
