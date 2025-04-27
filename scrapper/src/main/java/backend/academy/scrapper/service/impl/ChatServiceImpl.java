package backend.academy.scrapper.service.impl;

import backend.academy.dto.LinkResponse;
import backend.academy.scrapper.ChatException;
import backend.academy.scrapper.Link;
import backend.academy.scrapper.LinkSubscriptions;
import backend.academy.scrapper.NotificationMode;
import backend.academy.scrapper.entity.ChatEntity;
import backend.academy.scrapper.repository.ChatRepository;
import backend.academy.scrapper.service.LinkService;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ChatServiceImpl implements backend.academy.scrapper.service.ChatService {
    private final ChatRepository chatRepository;
    private final LinkService linkService;

    @Override
    public void registerChat(long chatId) {
        requireChatDoesNotExist(chatId);
        chatRepository.save(new ChatEntity(chatId));
    }

    @Override
    public void deleteChat(long chatId) {
        ChatEntity chatEntity = getChatEntityOrThrow(chatId);
        linkService.deleteChat(chatEntity);
        chatRepository.delete(chatEntity);
    }

    @Override
    public void setImmediate(long chatId) {
        ChatEntity chatEntity = getChatEntityOrThrow(chatId);
        chatRepository.setNotificationMode(chatEntity.id(), NotificationMode.IMMEDIATE);
    }

    @Override
    public void setDigest(long chatId) {
        ChatEntity chatEntity = getChatEntityOrThrow(chatId);
        chatRepository.setNotificationMode(chatEntity.id(), NotificationMode.DIGEST);
    }

    @Override
    public List<LinkResponse> getLinks(long chatId) {
        return linkService.getLinks(getChatEntityOrThrow(chatId));
    }

    @Override
    public List<LinkResponse> getLinksByTag(long chatId, String tagName) {
        return linkService.getLinksByTag(getChatEntityOrThrow(chatId), tagName);
    }

    @Override
    public LinkResponse addLink(long chatId, Link link, List<String> tags, List<String> filters) {
        linkService.addLink(getChatEntityOrThrow(chatId), link, tags, filters);
        return new LinkResponse(chatId, link.url(), tags, filters);
    }

    @Override
    public LinkResponse removeLink(long chatId, String url) {
        return linkService
                .removeLink(getChatEntityOrThrow(chatId), url)
                .orElseThrow(() -> new ChatException(HttpStatus.NOT_FOUND, "Link doesn't exist."));
    }

    @Override
    public Page<LinkSubscriptions> findAllLinkSubscriptions(int page, int size) {
        return linkService.findAllLinkSubscriptions(page, size);
    }

    @Override
    public void updateLastUpdateByUrl(String url, ZonedDateTime lastUpdate) {
        linkService.updateLastUpdateByUrl(url, lastUpdate);
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
