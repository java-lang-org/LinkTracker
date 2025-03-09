package backend.academy.scrapper.service.impl;

import backend.academy.scrapper.entity.TagEntity;
import backend.academy.scrapper.service.TagService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SqlTagService implements TagService {
    private static final RowMapper<TagEntity> ROW_MAPPER =
            (rs, rowNum) -> new TagEntity(rs.getLong("id"), rs.getString("name"));

    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public List<TagEntity> addTags(List<String> tags) {
        return tags.stream().map(this::saveTag).toList();
    }

    @Override
    @Transactional
    public void deleteUnusedTags() {
        String sql = "DELETE FROM tag WHERE id NOT IN (SELECT tag_id FROM chat_link_tag)";
        jdbcTemplate.update(sql);
    }

    private TagEntity saveTag(String name) {
        String findSql = "SELECT id, name FROM tag WHERE name = ?";
        List<TagEntity> existingTags = jdbcTemplate.query(findSql, ROW_MAPPER, name);

        if (!existingTags.isEmpty()) {
            return existingTags.getFirst();
        }

        String insertSql = "INSERT INTO tag (name) VALUES (?) RETURNING id";
        Long id = jdbcTemplate.queryForObject(insertSql, Long.class, name);

        return new TagEntity(id, name);
    }
}
