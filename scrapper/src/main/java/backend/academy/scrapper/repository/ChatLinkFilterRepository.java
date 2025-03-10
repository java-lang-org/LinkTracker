package backend.academy.scrapper.repository;

import backend.academy.scrapper.entity.ChatEntity;
import backend.academy.scrapper.entity.ChatLinkFilterEntity;
import backend.academy.scrapper.entity.ChatLinkFilterId;
import backend.academy.scrapper.entity.LinkEntity;
import org.springframework.data.jpa.repository.Modifying;

public interface ChatLinkFilterRepository {
    boolean existsById(ChatLinkFilterId chatLinkFilterId);

    ChatLinkFilterEntity save(ChatLinkFilterEntity chatLinkFilterEntity);

    @Modifying
    void deleteByChatEntity(ChatEntity chatEntity);

    @Modifying
    void deleteByChatEntityAndLinkEntity(ChatEntity chatEntity, LinkEntity linkEntity);
}
