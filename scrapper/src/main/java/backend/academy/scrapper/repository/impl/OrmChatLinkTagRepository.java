package backend.academy.scrapper.repository.impl;

import backend.academy.scrapper.entity.ChatEntity;
import backend.academy.scrapper.entity.ChatLinkTagEntity;
import backend.academy.scrapper.entity.ChatLinkTagId;
import backend.academy.scrapper.entity.LinkEntity;
import backend.academy.scrapper.repository.ChatLinkTagRepository;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface OrmChatLinkTagRepository
        extends ChatLinkTagRepository, JpaRepository<ChatLinkTagEntity, ChatLinkTagId> {
    @Override
    @Transactional
    @Modifying
    @Query(
            """
    DELETE FROM ChatLinkTagEntity chatLinkTagEntity
    WHERE chatLinkTagEntity.chatEntity.id = :#{#chatEntity.id}
""")
    void deleteByChatEntity(@Param("chatEntity") ChatEntity chatEntity);

    @Override
    @Transactional
    @Modifying
    @Query(
            """
    DELETE FROM ChatLinkTagEntity chatLinkTagEntity
    WHERE chatLinkTagEntity.chatEntity.id = :#{#chatEntity.id} AND chatLinkTagEntity.linkEntity.id = :#{#linkEntity.id}
""")
    void deleteByChatEntityAndLinkEntity(
            @Param("chatEntity") ChatEntity chatEntity, @Param("linkEntity") LinkEntity linkEntity);
}
