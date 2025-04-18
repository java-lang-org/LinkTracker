package backend.academy.scrapper;

import backend.academy.scrapper.entity.ChatEntity;
import java.time.ZonedDateTime;
import java.util.List;

public record LinkSubscriptions(Link link, List<ChatEntity> chatIds) {
    public LinkSubscriptions(String url, LinkType linkType, ZonedDateTime lastUpdate, List<ChatEntity> chatIds) {
        this(Link.getInstance(url, linkType, lastUpdate), chatIds);
    }
}
