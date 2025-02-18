package backend.academy.scrapper;

import backend.academy.dto.LinkResponse;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class ChatService {
    private final ChatRepository chatRepository;
    private final ChatLinkRepository chatLinkRepository;

    public ChatService(ChatRepository chatRepository, ChatLinkRepository chatLinkRepository) {
        this.chatRepository = chatRepository;
        this.chatLinkRepository = chatLinkRepository;
    }

    public void registerChat(long chatId) {
        requireChatDoesNotExist(chatId);
        chatRepository.registerChat(chatId);
    }

    public void deleteChat(long chatId) {
        requireChatExist(chatId);
        chatRepository.deleteChat(chatId);
        chatLinkRepository.getLinks(chatId).forEach(link -> chatLinkRepository.removeLink(chatId, link.url()));
    }

    public List<LinkResponse> getLinks(long chatId) {
        requireChatExist(chatId);
        return chatLinkRepository.getLinks(chatId).stream()
                .map(link -> new LinkResponse(chatId, link.url(), link.tags(), link.filters()))
                .toList();
    }

    public LinkResponse addLink(long chatId, Link link) {
        requireChatExist(chatId);
        if (!chatLinkRepository.addLink(chatId, link)) {
            throw new InvalidRequestException("Link already exist.");
        }
        return new LinkResponse(chatId, link.url(), link.tags(), link.filters());
    }

    public LinkResponse removeLink(long chatId, String url) {
        requireChatExist(chatId);

        Optional<Link> link = chatLinkRepository.removeLink(chatId, url);
        if (link.isPresent()) {
            return new LinkResponse(
                    chatId,
                    link.orElseThrow().url(),
                    link.orElseThrow().tags(),
                    link.orElseThrow().filters());
        }

        throw new ChatException(HttpStatus.NOT_FOUND, "Link doesn't exit.");
    }

    public List<ChatLink> getLink2ChatIds() {
        return chatLinkRepository.getLink2ChatIds();
    }

    private void requireChatExist(long chatId) {
        if (!chatRepository.exists(chatId)) {
            throw new ChatException(HttpStatus.BAD_REQUEST, "You must be registered to use this command.");
        }
    }

    private void requireChatDoesNotExist(long chatId) {
        if (chatRepository.exists(chatId)) {
            throw new ChatException(HttpStatus.BAD_REQUEST, "You are already registered.");
        }
    }
}
