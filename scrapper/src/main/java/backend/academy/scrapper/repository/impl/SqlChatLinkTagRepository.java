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
    private static final String EXIST_BY_ID_SQL =
            """
        SELECT EXISTS(SELECT 1 FROM chat_link_tag WHERE chat_id = :chatId AND link_id = :linkId AND tag_id = :tagId)
        """;

    private static final String INSERT_SQL =
            """
        INSERT INTO chat_link_tag (chat_id, link_id, tag_id) VALUES (:chatId, :linkId, :tagId) RETURNING chat_id, link_id, tag_id
        """;

    private static final String DELETE_BY_CHAT_ENTITY_SQL = "DELETE FROM chat_link_tag WHERE chat_id = :chatId";

    private static final String DELETE_BY_CHAT_ENTITY_AND_LINK_ENTITY_SQL =
            """
        DELETE FROM chat_link_tag WHERE chat_id = :chatId AND link_id = :linkId
        """;

    private final JdbcClient jdbcClient;

    @Override
    @Transactional
    public boolean existsById(ChatLinkTagId chatLinkTagId) {
        return jdbcClient
                .sql(EXIST_BY_ID_SQL)
                .param("chatId", chatLinkTagId.chatId())
                .param("linkId", chatLinkTagId.linkId())
                .param("tagId", chatLinkTagId.tagId())
                .query(Boolean.class)
                .single();
    }

    @Override
    @Transactional
    public ChatLinkTagEntity save(ChatLinkTagEntity chatLinkTagEntity) {
        return jdbcClient
                .sql(INSERT_SQL)
                .param("chatId", chatLinkTagEntity.chatEntity().id())
                .param("linkId", chatLinkTagEntity.linkEntity().id())
                .param("tagId", chatLinkTagEntity.tagEntity().id())
                .query((rs, rowNum) -> new ChatLinkTagEntity(
                        new ChatLinkTagId(rs.getLong("chat_id"), rs.getLong("link_id"), rs.getLong("tag_id")),
                        chatLinkTagEntity.chatEntity(),
                        chatLinkTagEntity.linkEntity(),
                        chatLinkTagEntity.tagEntity()))
                .single();
    }

    @Override
    @Transactional
    public void deleteByChatEntity(ChatEntity chatEntity) {
        jdbcClient
                .sql(DELETE_BY_CHAT_ENTITY_SQL)
                .param("chatId", chatEntity.id())
                .update();
    }

    @Override
    @Transactional
    public void deleteByChatEntityAndLinkEntity(ChatEntity chatEntity, LinkEntity linkEntity) {
        jdbcClient
                .sql(DELETE_BY_CHAT_ENTITY_AND_LINK_ENTITY_SQL)
                .param("chatId", chatEntity.id())
                .param("linkId", linkEntity.id())
                .update();
    }
}
