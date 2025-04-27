package backend.academy.scrapper.repository.impl;

import backend.academy.scrapper.entity.FilterEntity;
import backend.academy.scrapper.repository.FilterRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class SqlFilterRepository implements FilterRepository {
    private static final String FIND_BY_NAME_AND_PATTEN_SQL =
            """
            SELECT id, name, pattern FROM filter WHERE name = :name AND pattern = :pattern
        """;

    private static final String INSERT_SQL =
            """
            INSERT INTO filter (name, pattern) VALUES (?, ?) RETURNING id, name, pattern
        """;

    private static final String DELETE_UNUSED_SQL =
            """
            DELETE FROM filter WHERE NOT EXISTS (SELECT 1 FROM chat_link_filter WHERE chat_link_filter.filter_id = filter.id)
        """;

    private static final String FIND_BY_CHAT_ID_AND_LINK_URL_SQL =
            """
        SELECT
            f.id,
            f.name,
            f.pattern
        FROM chat c
        JOIN chat_link cl ON c.id = cl.chat_id
        JOIN link l ON cl.link_id = l.id
        LEFT JOIN chat_link_filter clf ON cl.chat_id = clf.chat_id AND cl.link_id = clf.link_id
        LEFT JOIN filter f ON clf.filter_id = f.id
        WHERE c.id = :chatId AND l.url = :url AND f.id IS NOT NULL AND f.name IS NOT NULL AND f.pattern IS NOT NULL
    """;

    private final JdbcClient jdbcClient;

    @Override
    @Transactional
    public Optional<FilterEntity> findByNameAndPattern(String name, String pattern) {
        return jdbcClient
                .sql(FIND_BY_NAME_AND_PATTEN_SQL)
                .param("name", name)
                .param("pattern", pattern)
                .query(FilterEntity.class)
                .optional();
    }

    @Override
    @Transactional
    public FilterEntity save(FilterEntity filterEntity) {
        return jdbcClient
                .sql(INSERT_SQL)
                .params(filterEntity.name(), filterEntity.pattern())
                .query(FilterEntity.class)
                .single();
    }

    @Override
    @Transactional
    public void deleteUnusedFilters() {
        jdbcClient.sql(DELETE_UNUSED_SQL).update();
    }

    @Override
    @Transactional
    public List<FilterEntity> findByChatIdAndLinkUrl(Long chatId, String url) {
        return jdbcClient
                .sql(FIND_BY_CHAT_ID_AND_LINK_URL_SQL)
                .param("chatId", chatId)
                .param("url", url)
                .query(FilterEntity.class)
                .list();
    }
}
