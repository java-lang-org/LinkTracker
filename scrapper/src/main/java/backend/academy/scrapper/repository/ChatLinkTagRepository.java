package backend.academy.scrapper.repository;

import backend.academy.scrapper.entity.ChatEntity;
import backend.academy.scrapper.entity.ChatLinkTagEntity;
import backend.academy.scrapper.entity.ChatLinkTagId;
import backend.academy.scrapper.entity.LinkEntity;

public interface ChatLinkTagRepository {
    boolean existsById(ChatLinkTagId chatLinkTagId);

    ChatLinkTagEntity save(ChatLinkTagEntity chatLinkTagEntity);

    void deleteByChatEntity(ChatEntity chatEntity);

    void deleteByChatEntityAndLinkEntity(ChatEntity chatEntity, LinkEntity linkEntity);
}
