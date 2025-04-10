package backend.academy.scrapper;

import java.time.ZonedDateTime;
import java.util.List;

public record LinkSubscriptions(Link link, List<Long> chatIds) {
    public LinkSubscriptions(String url, LinkType linkType, ZonedDateTime lastUpdate, List<Long> chatIds) {
        this(Link.getInstance(url, linkType, lastUpdate), chatIds);
    }
}
