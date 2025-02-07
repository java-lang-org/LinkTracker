package backend.academy.scrapper;

import backend.academy.dto.AddLinkRequest;
import backend.academy.dto.LinkResponse;
import backend.academy.dto.RemoveLinkRequest;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;

@Repository
public class ChatRepositoryImpl implements ChatRepository {
    private final Map<Long, List<Link>> id2links;

    public ChatRepositoryImpl() {
        this.id2links = new HashMap<>();
    }

    @Override
    public void registerChat(long id) {
        id2links.computeIfAbsent(id, k -> new LinkedList<>());
    }

    @Override
    public void deleteChat(long id) {
        if (id2links.remove(id) == null) {
            throw new ChatDoesNotExistException(String.format("Chat with id=%d does not exist", id));
        }
    }

    @Override
    public List<LinkResponse> getLinks(long tgChatId) {
        List<Link> links = id2links.get(tgChatId);
        if (links == null) {
            throw new IncorrectRequestParametersException(String.format("Chat with id=%d does not exist", tgChatId));
        }

        return links.stream()
            .map(link -> new LinkResponse(tgChatId, link.url(), link.tags(), link.filters()))
            .toList();
    }

    @Override
    public LinkResponse addLink(long tgChatId, AddLinkRequest addLinkRequest) {
        List<Link> links = id2links.get(tgChatId);
        if (links == null) {
            throw new IncorrectRequestParametersException(String.format("Chat with id=%d does not exist", tgChatId));
        }

        String newUrl = addLinkRequest.uri();
        if (links.stream().anyMatch(link -> link.url().equals(newUrl))) {
            throw new IncorrectRequestParametersException(
                String.format("For chat with id=%d link with url=%s already exits", tgChatId, newUrl)
            );
        }

        Link link = new Link(newUrl, addLinkRequest.tags(), addLinkRequest.filters());
        links.add(link);

        return new LinkResponse(tgChatId, link.url(), link.tags(), link.filters());
    }

    @Override
    public LinkResponse removeLink(long tgChatId, RemoveLinkRequest removeLinkRequest) {
        List<Link> links = id2links.get(tgChatId);
        if (links == null) {
            throw new IncorrectRequestParametersException(String.format("Chat with id=%d does not exist", tgChatId));
        }

        Link removedLink = null;
        for (Link link : links) {
            if (link.url().equals(removeLinkRequest.uri())) {
                removedLink = link;
                break;
            }
        }

        if (removedLink == null) {
            throw new IncorrectRequestParametersException(
                String.format("For chat with id=%d link with url=%s does not exits", tgChatId, removeLinkRequest.uri())
            );
        }

        links.remove(removedLink);

        return new LinkResponse(tgChatId, removedLink.url(), removedLink.tags(), removedLink.filters());
    }

    private record Link(String url, List<String> tags, List<String> filters) {
    }
}
