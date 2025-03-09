package backend.academy.scrapper.service.impl;

import backend.academy.dto.LinkResponse;
import backend.academy.scrapper.Link;
import backend.academy.scrapper.LinkSubscriptions;
import backend.academy.scrapper.service.ChatService;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class SqlChatService implements ChatService {
    @Override
    public void registerChat(long chatId) {}

    @Override
    public void deleteChat(long chatId) {}

    @Override
    public List<LinkResponse> getLinks(long chatId) {
        return List.of();
    }

    @Override
    public LinkResponse addLink(long chatId, Link link, List<String> tags, List<String> filters) {
        return null;
    }

    @Override
    public LinkResponse removeLink(long chatId, String url) {
        return null;
    }

    @Override
    public Page<LinkSubscriptions> findAllLinkSubscriptions(int page, int size) {
        return null;
    }
}
