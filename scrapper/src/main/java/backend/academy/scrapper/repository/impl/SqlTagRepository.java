package backend.academy.scrapper.repository.impl;

import backend.academy.scrapper.entity.TagEntity;
import backend.academy.scrapper.repository.TagRepository;
import jakarta.transaction.Transactional;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SqlTagRepository implements TagRepository {
    private static final String FIND_BY_NAME_SQL = "SELECT id, name FROM tag WHERE name = :name";

    private static final String INSERT_SQL = "INSERT INTO tag (name) VALUES (:name) RETURNING id, name";

    private static final String DELETE_UNUSED_SQL =
            """
            DELETE FROM tag WHERE NOT EXISTS (SELECT 1 FROM chat_link_tag WHERE chat_link_tag.tag_id = tag.id)
        """;

    private final JdbcClient jdbcClient;

    private final RowMapper<TagEntity> rowMapper =
            (rs, rowNum) -> new TagEntity(rs.getLong("id"), rs.getString("name"));

    @Override
    @Transactional
    public Optional<TagEntity> findByName(String name) {
        return jdbcClient
                .sql(FIND_BY_NAME_SQL)
                .param("name", name)
                .query(rowMapper)
                .optional();
    }

    @Override
    @Transactional
    public TagEntity save(TagEntity tagEntity) {
        return jdbcClient
                .sql(INSERT_SQL)
                .param("name", tagEntity.name())
                .query(rowMapper)
                .single();
    }

    @Override
    @Transactional
    public void deleteUnusedTags() {
        jdbcClient.sql(DELETE_UNUSED_SQL).update();
    }
}
