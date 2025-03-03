package backend.academy.scrapper;

import backend.academy.dto.AddLinkRequest;
import java.net.URI;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

@Getter
public class Link {
    private final URI uri;
    private final List<String> tags;
    private final List<String> filters;
    private final LinkType linkType;

    @Setter
    private ZonedDateTime lastUpdate;

    public static Link parse(AddLinkRequest addLinkRequest) {
        String url = addLinkRequest.url();
        List<String> tags = addLinkRequest.tags();
        List<String> filters = addLinkRequest.filters();
        ZonedDateTime now = ZonedDateTime.now();
        return UrlValidator.isValidGitHubUrl(url)
                .map(uri -> new Link(uri, tags, filters, LinkType.GITHUB, now))
                .or(() -> UrlValidator.isValidStackOverflowUrl(url)
                        .map(uri -> new Link(uri, tags, filters, LinkType.STACK_OVERFLOW, now)))
                .orElseThrow(() -> new InvalidRequestException("Invalid link: " + url));
    }

    private Link(URI uri, List<String> tags, List<String> filters, LinkType linkType, ZonedDateTime lastUpdate) {
        this.uri = uri;
        this.tags = List.copyOf(tags);
        this.filters = List.copyOf(filters);
        this.linkType = linkType;
        this.lastUpdate = lastUpdate;
    }

    public String url() {
        return uri.toString();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof Link link)) {
            return false;
        }

        return Objects.equals(uri, link.uri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uri);
    }
}
