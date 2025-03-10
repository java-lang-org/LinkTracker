package backend.academy.scrapper.repository.impl;

import backend.academy.scrapper.LinkSubscriptions;
import backend.academy.scrapper.LinkWithTagsAndFilters;
import backend.academy.scrapper.entity.ChatEntity;
import backend.academy.scrapper.entity.ChatLinkEntity;
import backend.academy.scrapper.entity.ChatLinkId;
import backend.academy.scrapper.entity.LinkEntity;
import backend.academy.scrapper.repository.ChatLinkRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class SqlChatLinkRepository implements ChatLinkRepository {
    private final JdbcClient jdbcClient;

    @Override
    @Transactional
    public boolean existsById(ChatLinkId chatLinkId) {
        return false;
    }

    @Transactional
    @Override
    public Page<LinkSubscriptions> findAllLinkSubscriptions(Pageable pageable) {
        return null;
    }

    @Transactional
    @Override
    public List<LinkWithTagsAndFilters> findLinksWithTagsAndFiltersByChatEntity(ChatEntity chatEntity) {
        return List.of();
    }

    @Transactional
    @Override
    public Optional<LinkWithTagsAndFilters> findLinkWithTagsAndFiltersByChatEntityAndLinkEntity(
            ChatEntity chatEntity, LinkEntity linkEntity) {
        return Optional.empty();
    }

    @Transactional
    @Override
    public ChatLinkEntity save(ChatLinkEntity chatLinkEntity) {
        return null;
    }

    @Transactional
    @Override
    public void deleteByChatEntity(ChatEntity chatEntity) {}

    @Transactional
    @Override
    public void deleteByChatEntityAndLinkEntity(ChatEntity chatEntity, LinkEntity linkEntity) {}
}
