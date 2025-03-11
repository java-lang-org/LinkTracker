package backend.academy.scrapper.repository.impl;

import backend.academy.scrapper.entity.ChatEntity;
import backend.academy.scrapper.entity.ChatLinkFilterEntity;
import backend.academy.scrapper.entity.ChatLinkFilterId;
import backend.academy.scrapper.entity.LinkEntity;
import backend.academy.scrapper.repository.ChatLinkFilterRepository;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface OrmChatLinkFilterRepository
        extends ChatLinkFilterRepository, JpaRepository<ChatLinkFilterEntity, ChatLinkFilterId> {
    @Override
    @Transactional
    @Modifying
    @Query(
            """
    DELETE FROM ChatLinkFilterEntity chatLinkFilterEntity
    WHERE chatLinkFilterEntity.chatEntity.id = :#{#chatEntity.id}
""")
    void deleteByChatEntity(@Param("chatEntity") ChatEntity chatEntity);

    @Override
    @Transactional
    @Modifying
    @Query(
            """
    DELETE FROM ChatLinkFilterEntity chatLinkFilterEntity
    WHERE chatLinkFilterEntity.chatEntity.id = :#{#chatEntity.id} AND chatLinkFilterEntity.linkEntity.id = :#{#linkEntity.id}
""")
    void deleteByChatEntityAndLinkEntity(
            @Param("chatEntity") ChatEntity chatEntity, @Param("linkEntity") LinkEntity linkEntity);
}
