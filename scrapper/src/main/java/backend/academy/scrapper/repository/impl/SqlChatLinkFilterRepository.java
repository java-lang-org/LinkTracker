package backend.academy.scrapper.repository.impl;

import backend.academy.scrapper.entity.ChatEntity;
import backend.academy.scrapper.entity.ChatLinkFilterEntity;
import backend.academy.scrapper.entity.ChatLinkFilterId;
import backend.academy.scrapper.entity.LinkEntity;
import backend.academy.scrapper.repository.ChatLinkFilterRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SqlChatLinkFilterRepository implements ChatLinkFilterRepository {
    private static final String EXIST_BY_ID_SQL =
            """
    SELECT EXISTS (SELECT 1 FROM chat_link_filter WHERE chat_id = :chatId AND link_id = :linkId AND filter_id = :filterId)
    """;

    private static final String INSERT_SQL =
            """
    INSERT INTO chat_link_filter (chat_id, link_id, filter_id) VALUES (:chatId, :linkId, :filterId) RETURNING chat_id, link_id, filter_id
    """;

    private static final String DELETE_BY_CHAT_ENTITY_SQL = "DELETE FROM chat_link_filter WHERE chat_id = :chatId";

    private static final String DELETE_BY_CHAT_ENTITY_AND_LINK_ENTITY_SQL =
            """
    DELETE FROM chat_link_filter WHERE chat_id = :chatId AND link_id = :linkId
    """;

    private final JdbcClient jdbcClient;

    @Override
    @Transactional
    public boolean existsById(ChatLinkFilterId chatLinkFilterId) {
        return Boolean.TRUE.equals(jdbcClient
                .sql(EXIST_BY_ID_SQL)
                .param("chatId", chatLinkFilterId.chatId())
                .param("linkId", chatLinkFilterId.linkId())
                .param("filterId", chatLinkFilterId.filterId())
                .query(Boolean.class)
                .single());
    }

    @Override
    @Transactional
    public ChatLinkFilterEntity save(ChatLinkFilterEntity chatLinkFilterEntity) {
        return jdbcClient
                .sql(INSERT_SQL)
                .param("chatId", chatLinkFilterEntity.id().chatId())
                .param("linkId", chatLinkFilterEntity.id().linkId())
                .param("filterId", chatLinkFilterEntity.id().filterId())
                .query(ChatLinkFilterEntity.class)
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
