package backend.academy.scrapper.repository.impl;

import backend.academy.scrapper.entity.ChatEntity;
import backend.academy.scrapper.entity.ChatLinkTagEntity;
import backend.academy.scrapper.entity.ChatLinkTagId;
import backend.academy.scrapper.entity.LinkEntity;
import backend.academy.scrapper.repository.ChatLinkTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class SqlChatLinkTagRepository implements ChatLinkTagRepository {
    private final JdbcClient jdbcClient;

    @Override
    @Transactional
    public boolean existsById(ChatLinkTagId chatLinkTagId) {
        return false;
    }

    @Override
    @Transactional
    public ChatLinkTagEntity save(ChatLinkTagEntity chatLinkTagEntity) {
        return null;
    }

    @Override
    @Transactional
    public void deleteByChatEntity(ChatEntity chatEntity) {}

    @Override
    @Transactional
    public void deleteByChatEntityAndLinkEntity(ChatEntity chatEntity, LinkEntity linkEntity) {}
}
