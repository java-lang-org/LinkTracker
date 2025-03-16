package backend.academy.scrapper.service.impl;

import backend.academy.dto.LinkResponse;
import backend.academy.scrapper.Link;
import backend.academy.scrapper.LinkSubscriptions;
import backend.academy.scrapper.LinkWithTagsAndFilters;
import backend.academy.scrapper.entity.ChatEntity;
import backend.academy.scrapper.entity.ChatLinkEntity;
import backend.academy.scrapper.entity.ChatLinkFilterEntity;
import backend.academy.scrapper.entity.ChatLinkFilterId;
import backend.academy.scrapper.entity.ChatLinkId;
import backend.academy.scrapper.entity.ChatLinkTagEntity;
import backend.academy.scrapper.entity.ChatLinkTagId;
import backend.academy.scrapper.entity.FilterEntity;
import backend.academy.scrapper.entity.LinkEntity;
import backend.academy.scrapper.entity.TagEntity;
import backend.academy.scrapper.repository.ChatLinkFilterRepository;
import backend.academy.scrapper.repository.ChatLinkRepository;
import backend.academy.scrapper.repository.ChatLinkTagRepository;
import backend.academy.scrapper.repository.LinkRepository;
import backend.academy.scrapper.service.FilterService;
import backend.academy.scrapper.service.LinkService;
import backend.academy.scrapper.service.TagService;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LinkServiceImpl implements LinkService {
    TagService tagService;
    FilterService filterService;
    LinkRepository linkRepository;
    ChatLinkRepository chatLinkRepository;
    ChatLinkTagRepository chatLinkTagRepository;
    ChatLinkFilterRepository chatLinkFilterRepository;

    @Override
    public LinkEntity addLink(ChatEntity chatEntity, Link link, List<String> tags, List<String> filters) {
        LinkEntity linkEntity = linkRepository.findByUrl(link.url()).orElseGet(() -> {
            LinkEntity newLinkEntity = new LinkEntity();
            newLinkEntity.url(link.url());
            newLinkEntity.type(link.linkType());
            newLinkEntity.lastUpdate(link.lastUpdate());
            return linkRepository.save(newLinkEntity);
        });

        saveChatLink(chatEntity, linkEntity);
        tagService.addTags(tags).forEach(tagEntity -> saveChatLinkTag(chatEntity, linkEntity, tagEntity));
        filterService
                .addFilters(filters)
                .forEach(filterEntity -> saveChatLinkFilter(chatEntity, linkEntity, filterEntity));

        return linkEntity;
    }

    @Override
    public void deleteChat(ChatEntity chatEntity) {
        chatLinkRepository.deleteByChatEntity(chatEntity);
        chatLinkTagRepository.deleteByChatEntity(chatEntity);
        chatLinkFilterRepository.deleteByChatEntity(chatEntity);

        cleanupUnusedEntities();
    }

    @Override
    public List<LinkResponse> getLinks(ChatEntity chatEntity) {
        return chatLinkRepository.findLinksWithTagsAndFiltersByChatEntity(chatEntity).stream()
                .map(link -> new LinkResponse(chatEntity.id(), link.url(), link.tags(), link.filters()))
                .toList();
    }

    @Override
    public List<LinkResponse> getLinksByTag(ChatEntity chatEntity, String tagName) {
        return chatLinkRepository.findLinksWithTagsAndFiltersByChatEntityAndTagName(chatEntity, tagName).stream()
                .map(link -> new LinkResponse(chatEntity.id(), link.url(), link.tags(), link.filters()))
                .toList();
    }

    @Override
    public Optional<LinkResponse> removeLink(ChatEntity chatEntity, String url) {
        Optional<LinkEntity> linkEntity = linkRepository.findByUrl(url);
        if (linkEntity.isEmpty()) {
            return Optional.empty();
        }

        Optional<LinkWithTagsAndFilters> linkWithTagsAndFilters =
                chatLinkRepository.findLinkWithTagsAndFiltersByChatEntityAndLinkEntity(
                        chatEntity, linkEntity.orElseThrow());
        if (linkWithTagsAndFilters.isEmpty()) {
            return Optional.empty();
        }

        chatLinkRepository.deleteByChatEntityAndLinkEntity(chatEntity, linkEntity.orElseThrow());
        chatLinkTagRepository.deleteByChatEntityAndLinkEntity(chatEntity, linkEntity.orElseThrow());
        chatLinkFilterRepository.deleteByChatEntityAndLinkEntity(chatEntity, linkEntity.orElseThrow());

        cleanupUnusedEntities();

        return Optional.of(new LinkResponse(
                chatEntity.id(),
                linkWithTagsAndFilters.orElseThrow().url(),
                linkWithTagsAndFilters.orElseThrow().tags(),
                linkWithTagsAndFilters.orElseThrow().filters()));
    }

    @Override
    public Page<LinkSubscriptions> findAllLinkSubscriptions(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return chatLinkRepository.findAllLinkSubscriptions(pageable);
    }

    @Override
    public void updateLastUpdateByUrl(String url, ZonedDateTime lastUpdate) {
        linkRepository.updateLastUpdateByUrl(url, lastUpdate);
    }

    private void saveChatLink(ChatEntity chatEntity, LinkEntity linkEntity) {
        ChatLinkId chatLinkId = new ChatLinkId(chatEntity.id(), linkEntity.id());
        if (!chatLinkRepository.existsById(chatLinkId)) {
            chatLinkRepository.save(new ChatLinkEntity(chatLinkId, chatEntity, linkEntity));
        }
    }

    private void saveChatLinkTag(ChatEntity chatEntity, LinkEntity linkEntity, TagEntity tagEntity) {
        ChatLinkTagId chatLinkTagId = new ChatLinkTagId(chatEntity.id(), linkEntity.id(), tagEntity.id());
        if (!chatLinkTagRepository.existsById(chatLinkTagId)) {
            chatLinkTagRepository.save(new ChatLinkTagEntity(chatLinkTagId, chatEntity, linkEntity, tagEntity));
        }
    }

    private void saveChatLinkFilter(ChatEntity chatEntity, LinkEntity linkEntity, FilterEntity filterEntity) {
        ChatLinkFilterId chatLinkFilterId = new ChatLinkFilterId(chatEntity.id(), linkEntity.id(), filterEntity.id());
        if (!chatLinkFilterRepository.existsById(chatLinkFilterId)) {
            chatLinkFilterRepository.save(
                    new ChatLinkFilterEntity(chatLinkFilterId, chatEntity, linkEntity, filterEntity));
        }
    }

    private void cleanupUnusedEntities() {
        linkRepository.deleteUnusedLinks();
        tagService.deleteUnusedTags();
        filterService.deleteUnusedFilters();
    }
}
