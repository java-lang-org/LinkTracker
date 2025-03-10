package backend.academy.scrapper.repository.impl;

import backend.academy.scrapper.entity.ChatEntity;
import backend.academy.scrapper.entity.ChatLinkFilterEntity;
import backend.academy.scrapper.entity.ChatLinkFilterId;
import backend.academy.scrapper.entity.LinkEntity;
import backend.academy.scrapper.repository.ChatLinkFilterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class SqlChatLinkFilterRepository implements ChatLinkFilterRepository {
    private final JdbcClient jdbcClient;

    @Override
    @Transactional
    public boolean existsById(ChatLinkFilterId chatLinkFilterId) {
        return false;
    }

    @Override
    @Transactional
    public ChatLinkFilterEntity save(ChatLinkFilterEntity chatLinkFilterEntity) {
        return null;
    }

    @Override
    @Transactional
    public void deleteByChatEntity(ChatEntity chatEntity) {}

    @Override
    @Transactional
    public void deleteByChatEntityAndLinkEntity(ChatEntity chatEntity, LinkEntity linkEntity) {}
}
