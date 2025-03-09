package backend.academy.scrapper.service.impl;

import backend.academy.dto.LinkResponse;
import backend.academy.scrapper.Link;
import backend.academy.scrapper.LinkSubscriptions;
import backend.academy.scrapper.entity.ChatEntity;
import backend.academy.scrapper.entity.LinkEntity;
import backend.academy.scrapper.service.LinkService;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public class SqlLinkService implements LinkService {
    @Override
    public LinkEntity addLink(ChatEntity chatEntity, Link link, List<String> tags, List<String> filters) {
        return null;
    }

    @Override
    public void deleteChat(ChatEntity chatEntity) {}

    @Override
    public List<LinkResponse> getLinks(ChatEntity chatEntity) {
        return List.of();
    }

    @Override
    public Optional<LinkResponse> removeLink(ChatEntity chatEntity, String url) {
        return Optional.empty();
    }

    @Override
    public Page<LinkSubscriptions> findAllLinkSubscriptions(int page, int size) {
        return null;
    }
}
