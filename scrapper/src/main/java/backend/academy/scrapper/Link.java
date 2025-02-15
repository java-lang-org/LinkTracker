package backend.academy.scrapper;

import backend.academy.dto.AddLinkRequest;
import java.net.URI;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
        Optional<URI> uri = GitHubUriValidator.isValidGitHubUrl(addLinkRequest.uri());
        if (uri.isPresent()) {
            return new Link(
                uri.get(),
                addLinkRequest.tags(),
                addLinkRequest.filters(),
                LinkType.GITHUB,
                ZonedDateTime.now()
            );
        }

        throw new InvalidRequestException("Invalid link: " + addLinkRequest.uri());
    }

    private Link(URI uri, List<String> tags, List<String> filters, LinkType linkType, ZonedDateTime lastUpdate) {
        this.uri = uri;
        this.tags = tags;
        this.filters = filters;
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
