package backend.academy.scrapper;

import java.util.List;
import java.util.Optional;

public interface ChatLinkRepository {
    List<Link> getLinks(long chatId);

    void addLink(long chatId, Link link);

    Optional<Link> removeLink(long chatId, String url);

    List<ChatLink> getLink2ChatIds();
}
