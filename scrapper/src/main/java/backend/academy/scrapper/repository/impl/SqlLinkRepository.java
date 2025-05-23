package backend.academy.scrapper.repository.impl;

import backend.academy.scrapper.LinkType;
import backend.academy.scrapper.entity.LinkEntity;
import backend.academy.scrapper.repository.LinkRepository;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class SqlLinkRepository implements LinkRepository {
    private static final String FIND_BY_URL_SQL = "SELECT id, url, type, last_update FROM link WHERE url = :url";

    private static final String INSERT_SQL =
            """
        INSERT INTO link (url, type, last_update)
        VALUES (:url, :type, :last_update)
        RETURNING id, url, type, last_update
    """;

    private static final String UPDATE_SQL = "UPDATE link SET last_update = :last_update WHERE url = :url";

    private static final String DELETE_UNUSED_SQL =
            """
        DELETE FROM link WHERE NOT EXISTS (SELECT 1 FROM chat_link WHERE chat_link.link_id = link.id)
    """;

    private static final String COUNT_BY_TYPE =
            """
        SELECT type, COUNT(*) AS count FROM link GROUP BY type
    """;

    private final JdbcClient jdbcClient;

    private final RowMapper<LinkEntity> rowMapper = (rs, rowNum) -> new LinkEntity(
            rs.getLong("id"),
            rs.getString("url"),
            EnumUtils.getEnum(LinkType.class, rs.getString("type")),
            rs.getTimestamp("last_update").toInstant().atZone(ZoneId.systemDefault()));

    @Override
    @Transactional
    public Optional<LinkEntity> findByUrl(String url) {
        return jdbcClient
                .sql(FIND_BY_URL_SQL)
                .param("url", url)
                .query(rowMapper)
                .optional();
    }

    @Override
    @Transactional
    public LinkEntity save(LinkEntity linkEntity) {
        return jdbcClient
                .sql(INSERT_SQL)
                .param("url", linkEntity.url())
                .param("type", linkEntity.type().name())
                .param("last_update", linkEntity.lastUpdate().toOffsetDateTime())
                .query(rowMapper)
                .single();
    }

    @Override
    @Transactional
    public void updateLastUpdateByUrl(String url, ZonedDateTime lastUpdate) {
        jdbcClient
                .sql(UPDATE_SQL)
                .param("url", url)
                .param("last_update", lastUpdate.toOffsetDateTime())
                .update();
    }

    @Override
    @Transactional
    public void deleteUnusedLinks() {
        jdbcClient.sql(DELETE_UNUSED_SQL).update();
    }

    @Override
    @Transactional
    public Map<String, Long> countByType() {
        return jdbcClient
                .sql(COUNT_BY_TYPE)
                .query((rs, rowNum) -> Map.entry(rs.getString("type"), rs.getLong("count")))
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
