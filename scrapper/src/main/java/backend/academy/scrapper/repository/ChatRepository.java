package backend.academy.scrapper.repository;

import backend.academy.scrapper.NotificationMode;
import backend.academy.scrapper.entity.ChatEntity;
import java.util.Optional;

public interface ChatRepository {
    boolean existsById(Long id);

    Optional<ChatEntity> findById(Long id);

    ChatEntity save(ChatEntity chatEntity);

    void delete(ChatEntity chatEntity);

    void setNotificationMode(Long id, NotificationMode mode);
}
