package backend.academy.scrapper;

import backend.academy.dto.LinkResponse;
import backend.academy.scrapper.entity.ChatEntity;
import backend.academy.scrapper.repository.ChatRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final LinkService linkService;

    @Transactional
    public void registerChat(long chatId) {
        requireChatDoesNotExist(chatId);
        chatRepository.save(new ChatEntity(chatId));
    }

    @Transactional
    public void deleteChat(long chatId) {
        ChatEntity chatEntity = getChatEntityOrThrow(chatId);
        linkService.deleteChat(chatEntity);
        chatRepository.delete(chatEntity);
    }

    @Transactional
    public List<LinkResponse> getLinks(long chatId) {
        return linkService.getLinks(getChatEntityOrThrow(chatId));
    }

    @Transactional
    public LinkResponse addLink(long chatId, Link link) {
        linkService.addLink(getChatEntityOrThrow(chatId), link);
        return new LinkResponse(chatId, link.url(), link.tags(), link.filters());
    }

    @Transactional
    public LinkResponse removeLink(long chatId, String url) {
        return linkService
                .removeLink(getChatEntityOrThrow(chatId), url)
                .orElseThrow(() -> new ChatException(HttpStatus.NOT_FOUND, "Link doesn't exist."));
    }

    public List<ChatLink> getLink2ChatIds() {
        return List.of();
    }

    private void requireChatDoesNotExist(long chatId) {
        if (chatRepository.existsById(chatId)) {
            throw new ChatException(HttpStatus.BAD_REQUEST, "You are already registered.");
        }
    }

    private ChatEntity getChatEntityOrThrow(long chatId) {
        return chatRepository
                .findById(chatId)
                .orElseThrow(
                        () -> new ChatException(HttpStatus.BAD_REQUEST, "You must be registered to use this command."));
    }
}
