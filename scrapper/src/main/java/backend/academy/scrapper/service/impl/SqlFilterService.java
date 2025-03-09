package backend.academy.scrapper.service.impl;

import backend.academy.scrapper.entity.FilterEntity;
import backend.academy.scrapper.service.FilterService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SqlFilterService implements FilterService {
    private static final RowMapper<FilterEntity> ROW_MAPPER =
            (rs, rowNum) -> new FilterEntity(rs.getLong("id"), rs.getString("name"), rs.getString("pattern"));

    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public List<FilterEntity> addFilters(List<String> filters) {
        return filters.stream().map(this::saveFilter).toList();
    }

    @Override
    @Transactional
    public void deleteUnusedFilters() {
        String sql = "DELETE FROM filter WHERE id NOT IN (SELECT filter_id FROM chat_link_filter)";
        jdbcTemplate.update(sql);
    }

    private FilterEntity saveFilter(String filter) {
        String name = filter.split(":")[0];
        String pattern = filter.split(":")[1];

        String findSql = "SELECT id, name, pattern FROM filter WHERE name = ? AND pattern = ?";
        List<FilterEntity> existingFilters = jdbcTemplate.query(findSql, ROW_MAPPER, name, pattern);

        if (!existingFilters.isEmpty()) {
            return existingFilters.getFirst();
        }

        String insertSql = "INSERT INTO filter (name, pattern) VALUES (?, ?)";
        Long id = jdbcTemplate.queryForObject(insertSql, Long.class, name, pattern);

        return new FilterEntity(id, name, pattern);
    }
}
