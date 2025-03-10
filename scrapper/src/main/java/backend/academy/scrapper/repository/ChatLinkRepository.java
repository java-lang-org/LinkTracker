package backend.academy.scrapper.repository;

import backend.academy.scrapper.LinkSubscriptions;
import backend.academy.scrapper.LinkWithTagsAndFilters;
import backend.academy.scrapper.entity.ChatEntity;
import backend.academy.scrapper.entity.ChatLinkEntity;
import backend.academy.scrapper.entity.ChatLinkId;
import backend.academy.scrapper.entity.LinkEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChatLinkRepository {
    boolean existsById(ChatLinkId chatLinkId);

    Page<LinkSubscriptions> findAllLinkSubscriptions(Pageable pageable);

    List<LinkWithTagsAndFilters> findLinksWithTagsAndFiltersByChatEntity(ChatEntity chatEntity);

    Optional<LinkWithTagsAndFilters> findLinkWithTagsAndFiltersByChatEntityAndLinkEntity(
            ChatEntity chatEntity, LinkEntity linkEntity);

    ChatLinkEntity save(ChatLinkEntity chatLinkEntity);

    void deleteByChatEntity(ChatEntity chatEntity);

    void deleteByChatEntityAndLinkEntity(ChatEntity chatEntity, LinkEntity linkEntity);
}
