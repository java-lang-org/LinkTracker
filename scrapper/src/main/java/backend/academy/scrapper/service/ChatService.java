package backend.academy.scrapper.service;

import backend.academy.dto.LinkResponse;
import backend.academy.scrapper.Link;
import backend.academy.scrapper.LinkSubscriptions;
import java.time.ZonedDateTime;
import java.util.List;
import org.springframework.data.domain.Page;

public interface ChatService {
    void registerChat(long chatId);

    void deleteChat(long chatId);

    List<LinkResponse> getLinks(long chatId);

    List<LinkResponse> getLinksByTag(long chatId, String tagName);

    LinkResponse addLink(long chatId, Link link, List<String> tags, List<String> filters);

    LinkResponse removeLink(long chatId, String url);

    Page<LinkSubscriptions> findAllLinkSubscriptions(int page, int size);

    void updateLastUpdateByUrl(String url, ZonedDateTime lastUpdate);
}
