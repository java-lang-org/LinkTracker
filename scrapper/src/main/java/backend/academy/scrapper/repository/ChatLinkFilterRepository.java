package backend.academy.scrapper.repository;

import backend.academy.scrapper.entity.ChatEntity;
import backend.academy.scrapper.entity.ChatLinkFilterEntity;
import backend.academy.scrapper.entity.ChatLinkFilterId;
import backend.academy.scrapper.entity.LinkEntity;

public interface ChatLinkFilterRepository {
    boolean existsById(ChatLinkFilterId chatLinkFilterId);

    ChatLinkFilterEntity save(ChatLinkFilterEntity chatLinkFilterEntity);

    void deleteByChatEntity(ChatEntity chatEntity);

    void deleteByChatEntityAndLinkEntity(ChatEntity chatEntity, LinkEntity linkEntity);
}
