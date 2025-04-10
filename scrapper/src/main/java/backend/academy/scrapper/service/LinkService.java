package backend.academy.scrapper.service;

import backend.academy.dto.LinkResponse;
import backend.academy.scrapper.Link;
import backend.academy.scrapper.LinkSubscriptions;
import backend.academy.scrapper.entity.ChatEntity;
import backend.academy.scrapper.entity.LinkEntity;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface LinkService {
    LinkEntity addLink(ChatEntity chatEntity, Link link, List<String> tags, List<String> filters);

    void deleteChat(ChatEntity chatEntity);

    List<LinkResponse> getLinks(ChatEntity chatEntity);

    List<LinkResponse> getLinksByTag(ChatEntity chatEntity, String tagName);

    Optional<LinkResponse> removeLink(ChatEntity chatEntity, String url);

    Page<LinkSubscriptions> findAllLinkSubscriptions(int page, int size);

    void updateLastUpdateByUrl(String url, ZonedDateTime lastUpdate);
}
