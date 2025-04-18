package backend.academy.scrapper.repository.impl;

import backend.academy.scrapper.entity.ChatEntity;
import backend.academy.scrapper.repository.ChatRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class SqlChatRepository implements ChatRepository {
    private static final String EXISTS_BY_ID_SQL = "SELECT EXISTS(SELECT 1 FROM chat WHERE id = :id)";

    private static final String FIND_BY_ID_SQL = "SELECT id, notification_mode FROM chat WHERE id = :id";

    private static final String INSERT_SQL =
            "INSERT INTO chat (id, notification_mode) VALUES (:id, :notification_mode) RETURNING id, notification_mode";

    private static final String DELETE_SQL = "DELETE FROM chat WHERE id = :id";

    private final JdbcClient jdbcClient;

    @Override
    @Transactional
    public boolean existsById(Long id) {
        return Boolean.TRUE.equals(jdbcClient
                .sql(EXISTS_BY_ID_SQL)
                .param("id", id)
                .query(Boolean.class)
                .single());
    }

    @Override
    @Transactional
    public Optional<ChatEntity> findById(Long id) {
        return jdbcClient
                .sql(FIND_BY_ID_SQL)
                .param("id", id)
                .query(ChatEntity.class)
                .optional();
    }

    @Override
    @Transactional
    public ChatEntity save(ChatEntity chatEntity) {
        return jdbcClient
                .sql(INSERT_SQL)
                .param("id", chatEntity.id())
                .param("notification_mode", chatEntity.notificationMode().name())
                .query(ChatEntity.class)
                .single();
    }

    @Override
    @Transactional
    public void delete(ChatEntity chatEntity) {
        jdbcClient.sql(DELETE_SQL).param("id", chatEntity.id()).update();
    }
}
