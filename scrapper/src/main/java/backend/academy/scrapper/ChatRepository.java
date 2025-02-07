package backend.academy.scrapper;

import backend.academy.dto.AddLinkRequest;
import backend.academy.dto.LinkResponse;
import backend.academy.dto.RemoveLinkRequest;
import java.util.List;

public interface ChatRepository {
    void registerChat(long id);

    void deleteChat(long id);

    List<LinkResponse> getLinks(long tgChatId);

    LinkResponse addLink(long tgChatId, AddLinkRequest addLinkRequest);

    LinkResponse removeLink(long tgChatId, RemoveLinkRequest removeLinkRequest);
}
