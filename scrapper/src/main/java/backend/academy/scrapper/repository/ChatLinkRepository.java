package backend.academy.scrapper.repository;

import backend.academy.scrapper.LinkWithTagsAndFilters;
import backend.academy.scrapper.entity.ChatEntity;
import backend.academy.scrapper.entity.ChatLinkEntity;
import backend.academy.scrapper.entity.ChatLinkId;
import backend.academy.scrapper.entity.LinkEntity;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ChatLinkRepository extends JpaRepository<ChatLinkEntity, ChatLinkId> {
    @Query(
            """
        SELECT new backend.academy.scrapper.LinkWithTagsAndFilters(
            linkEntity.url,
            STRING_AGG((DISTINCT(tagEntity.name)), ' '),
            STRING_AGG((DISTINCT(filterEntity.name || ':' || filterEntity.pattern)), ' ')
        )
        FROM ChatLinkEntity chatLinkEntity
        JOIN LinkEntity linkEntity
            ON chatLinkEntity.linkEntity.id = linkEntity.id
        LEFT JOIN ChatLinkTagEntity chatLinkTagEntity
            ON chatLinkEntity.chatEntity.id = chatLinkTagEntity.chatEntity.id
            AND chatLinkEntity.linkEntity.id = chatLinkTagEntity.linkEntity.id
        LEFT JOIN TagEntity tagEntity
            ON chatLinkTagEntity.tagEntity.id = tagEntity.id
        LEFT JOIN ChatLinkFilterEntity chatLinkFilterEntity
            ON chatLinkEntity.chatEntity.id = chatLinkFilterEntity.chatEntity.id
            AND chatLinkEntity.linkEntity.id = chatLinkFilterEntity.linkEntity.id
        LEFT JOIN FilterEntity filterEntity
            ON chatLinkFilterEntity.filterEntity.id = filterEntity.id
        WHERE chatLinkEntity.chatEntity.id = :#{#chatEntity.id}
        GROUP BY linkEntity.url
    """)
    List<LinkWithTagsAndFilters> findLinksWithTagsAndFiltersByChatEntity(@Param("chatEntity") ChatEntity chatEntity);

    @Query(
            """
        SELECT new backend.academy.scrapper.LinkWithTagsAndFilters(
            linkEntity.url,
            STRING_AGG((DISTINCT(tagEntity.name)), ' '),
            STRING_AGG((DISTINCT(filterEntity.name || ':' || filterEntity.pattern)), ' ')
        )
        FROM ChatLinkEntity chatLinkEntity
        JOIN LinkEntity linkEntity
            ON chatLinkEntity.linkEntity.id = linkEntity.id
        LEFT JOIN ChatLinkTagEntity chatLinkTagEntity
            ON chatLinkEntity.chatEntity.id = chatLinkTagEntity.chatEntity.id
            AND chatLinkEntity.linkEntity.id = chatLinkTagEntity.linkEntity.id
        LEFT JOIN TagEntity tagEntity
            ON chatLinkTagEntity.tagEntity.id = tagEntity.id
        LEFT JOIN ChatLinkFilterEntity chatLinkFilterEntity
            ON chatLinkEntity.chatEntity.id = chatLinkFilterEntity.chatEntity.id
            AND chatLinkEntity.linkEntity.id = chatLinkFilterEntity.linkEntity.id
        LEFT JOIN FilterEntity filterEntity
            ON chatLinkFilterEntity.filterEntity.id = filterEntity.id
        WHERE chatLinkEntity.chatEntity.id = :#{#chatEntity.id} AND chatLinkEntity.linkEntity.id = :#{#linkEntity.id}
        GROUP BY linkEntity.url
    """)
    Optional<LinkWithTagsAndFilters> findLinkWithTagsAndFiltersByChatEntityAndLinkEntity(
            @Param("chatEntity") ChatEntity chatEntity, @Param("linkEntity") LinkEntity linkEntity);

    @Modifying
    void deleteByChatEntity(ChatEntity chatEntity);

    @Modifying
    void deleteByChatEntityAndLinkEntity(ChatEntity chatEntity, LinkEntity linkEntity);
}
