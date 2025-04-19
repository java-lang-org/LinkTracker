package backend.academy.scrapper.repository.impl;

import backend.academy.scrapper.NotificationMode;
import backend.academy.scrapper.entity.ChatEntity;
import backend.academy.scrapper.repository.ChatRepository;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface OrmChatRepository extends ChatRepository, JpaRepository<ChatEntity, Long> {
    @Override
    @Transactional
    @Modifying
    @Query("UPDATE ChatEntity chatEntity SET chatEntity.notificationMode = :mode WHERE chatEntity.id = :id")
    void setNotificationMode(@Param("id") Long id, @Param("mode") NotificationMode mode);
}
