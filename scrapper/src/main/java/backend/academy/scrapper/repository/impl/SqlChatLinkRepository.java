package backend.academy.scrapper.repository.impl;

import backend.academy.scrapper.LinkSubscriptions;
import backend.academy.scrapper.LinkType;
import backend.academy.scrapper.LinkWithTagsAndFilters;
import backend.academy.scrapper.entity.ChatEntity;
import backend.academy.scrapper.entity.ChatLinkEntity;
import backend.academy.scrapper.entity.ChatLinkId;
import backend.academy.scrapper.entity.LinkEntity;
import backend.academy.scrapper.repository.ChatLinkRepository;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class SqlChatLinkRepository implements ChatLinkRepository {
    private static final String EXIST_BY_ID_SQL =
            """
            SELECT EXISTS (
                SELECT 1 FROM chat_link WHERE chat_id = :chatId AND link_id = :linkId
            )
        """;

    private static final String FIND_ALL_LINK_SUBSCRIPTIONS_SQL =
            """
            SELECT
                l.url,
                l.type,
                l.last_update,
                ARRAY_AGG(cl.chat_id ORDER BY cl.chat_id) AS chat_ids
            FROM link l
            JOIN chat_link cl ON cl.link_id = l.id
            GROUP BY l.url, l.type, l.last_update
            ORDER BY l.url
            LIMIT :limit OFFSET :offset
        """;

    private static final String FIND_LINKS_WITH_TAGS_AND_FILTERS_BY_CHAT_ENTITY_SQL =
            """
            SELECT
                l.url,
                STRING_AGG(DISTINCT t.name, ' ') AS tags,
                STRING_AGG(DISTINCT f.name || ':' || f.pattern, ' ') AS filters
            FROM chat_link cl
            JOIN link l ON cl.link_id = l.id
            LEFT JOIN chat_link_tag clt ON cl.chat_id = clt.chat_id AND cl.link_id = clt.link_id
            LEFT JOIN tag t ON clt.tag_id = t.id
            LEFT JOIN chat_link_filter clf ON cl.chat_id = clf.chat_id AND cl.link_id = clf.link_id
            LEFT JOIN filter f ON clf.filter_id = f.id
            WHERE cl.chat_id = :chatId
            GROUP BY l.url
        """;

    private static final String FIND_LINKS_WITH_TAGS_AND_FILTERS_BY_CHAT_ENTITY_AND_TAG_NAME_SQL =
            """
        SELECT
            l.url,
            STRING_AGG(DISTINCT t.name, ' ') AS tags,
            STRING_AGG(DISTINCT f.name || ':' || f.pattern, ' ') AS filters
        FROM chat_link cl
        JOIN link l ON cl.link_id = l.id
        LEFT JOIN chat_link_tag clt ON cl.chat_id = clt.chat_id AND cl.link_id = clt.link_id
        LEFT JOIN tag t ON clt.tag_id = t.id
        LEFT JOIN chat_link_filter clf ON cl.chat_id = clf.chat_id AND cl.link_id = clf.link_id
        LEFT JOIN filter f ON clf.filter_id = f.id
        WHERE cl.chat_id = :chatId AND t.name = :tagName
        GROUP BY l.url
    """;

    private static final String FIND_LINK_WITH_TAGS_AND_FILTERS_BY_CHAT_ENTITY_AND_LINK_ENTITY_SQL =
            """
            SELECT
                l.url,
                STRING_AGG(DISTINCT t.name, ' ') AS tags,
                STRING_AGG(DISTINCT f.name || ':' || f.pattern, ' ') AS filters
            FROM chat_link cl
            JOIN link l ON cl.link_id = l.id
            LEFT JOIN chat_link_tag clt ON cl.chat_id = clt.chat_id AND cl.link_id = clt.link_id
            LEFT JOIN tag t ON clt.tag_id = t.id
            LEFT JOIN chat_link_filter clf ON cl.chat_id = clf.chat_id AND cl.link_id = clf.link_id
            LEFT JOIN filter f ON clf.filter_id = f.id
            WHERE cl.chat_id = :chatId AND cl.link_id = :linkId
            GROUP BY l.url
        """;

    private static final String INSERT_SQL =
            """
            INSERT INTO chat_link (chat_id, link_id)
            VALUES (:chatId, :linkId)
            RETURNING chat_id, link_id
        """;

    private static final String DELETE_BY_CHAT_ENTITY_SQL = "DELETE FROM chat_link WHERE chat_id = :chatId";

    private static final String DELETE_BY_CHAT_ENTITY_LINK_ENTITY_SQL =
            "DELETE FROM chat_link WHERE chat_id = :chatId AND link_id = :linkId";

    private static final String GET_TOTAL_COUNT_SQL =
            """
        SELECT COUNT(DISTINCT l.id)
        FROM link l
        JOIN chat_link cl ON cl.link_id = l.id
    """;

    private final JdbcClient jdbcClient;

    private final RowMapper<LinkWithTagsAndFilters> linkWithTagsAndFiltersRowMapper = (rs, rowNum) ->
            new LinkWithTagsAndFilters(rs.getString("url"), rs.getString("tags"), rs.getString("filters"));

    private final RowMapper<LinkSubscriptions> linkSubscriptionsRowMapper = (rs, rowNum) -> new LinkSubscriptions(
            rs.getString("url"),
            LinkType.valueOf(rs.getString("type")),
            rs.getTimestamp("last_update").toInstant().atZone(ZoneId.of("UTC")),
            List.of((Long[]) rs.getArray("chat_ids").getArray()));

    @Override
    @Transactional
    public boolean existsById(ChatLinkId chatLinkId) {
        return Boolean.TRUE.equals(jdbcClient
                .sql(EXIST_BY_ID_SQL)
                .param("chatId", chatLinkId.chatId())
                .param("linkId", chatLinkId.linkId())
                .query(Boolean.class)
                .single());
    }

    @Override
    @Transactional
    public Page<LinkSubscriptions> findAllLinkSubscriptions(Pageable pageable) {
        List<LinkSubscriptions> subscriptions = jdbcClient
                .sql(FIND_ALL_LINK_SUBSCRIPTIONS_SQL)
                .param("limit", pageable.getPageSize())
                .param("offset", pageable.getOffset())
                .query(linkSubscriptionsRowMapper)
                .list();

        long total = getTotalCount();
        return new PageImpl<>(subscriptions, pageable, total);
    }

    @Override
    @Transactional
    public List<LinkWithTagsAndFilters> findLinksWithTagsAndFiltersByChatEntity(ChatEntity chatEntity) {
        return jdbcClient
                .sql(FIND_LINKS_WITH_TAGS_AND_FILTERS_BY_CHAT_ENTITY_SQL)
                .param("chatId", chatEntity.id())
                .query(linkWithTagsAndFiltersRowMapper)
                .list();
    }

    @Override
    @Transactional
    public List<LinkWithTagsAndFilters> findLinksWithTagsAndFiltersByChatEntityAndTagName(
            ChatEntity chatEntity, String tagName) {
        return jdbcClient
                .sql(FIND_LINKS_WITH_TAGS_AND_FILTERS_BY_CHAT_ENTITY_AND_TAG_NAME_SQL)
                .param("chatId", chatEntity.id())
                .param("tagName", tagName)
                .query(linkWithTagsAndFiltersRowMapper)
                .list();
    }

    @Override
    @Transactional
    public Optional<LinkWithTagsAndFilters> findLinkWithTagsAndFiltersByChatEntityAndLinkEntity(
            ChatEntity chatEntity, LinkEntity linkEntity) {
        return jdbcClient
                .sql(FIND_LINK_WITH_TAGS_AND_FILTERS_BY_CHAT_ENTITY_AND_LINK_ENTITY_SQL)
                .param("chatId", chatEntity.id())
                .param("linkId", linkEntity.id())
                .query(linkWithTagsAndFiltersRowMapper)
                .optional();
    }

    @Override
    @Transactional
    public ChatLinkEntity save(ChatLinkEntity chatLinkEntity) {
        return jdbcClient
                .sql(INSERT_SQL)
                .param("chatId", chatLinkEntity.id().chatId())
                .param("linkId", chatLinkEntity.id().linkId())
                .query((rs, rowNum) -> new ChatLinkEntity(
                        new ChatLinkId(rs.getLong("chat_id"), rs.getLong("link_id")),
                        chatLinkEntity.chatEntity(),
                        chatLinkEntity.linkEntity()))
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
                .sql(DELETE_BY_CHAT_ENTITY_LINK_ENTITY_SQL)
                .param("chatId", chatEntity.id())
                .param("linkId", linkEntity.id())
                .update();
    }

    private long getTotalCount() {
        return jdbcClient.sql(GET_TOTAL_COUNT_SQL).query(Long.class).single();
    }
}
