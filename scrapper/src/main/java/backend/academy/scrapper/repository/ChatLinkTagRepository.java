package backend.academy.scrapper.repository;

import backend.academy.scrapper.entity.ChatEntity;
import backend.academy.scrapper.entity.ChatLinkTagEntity;
import backend.academy.scrapper.entity.ChatLinkTagId;
import backend.academy.scrapper.entity.LinkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

public interface ChatLinkTagRepository extends JpaRepository<ChatLinkTagEntity, ChatLinkTagId> {
    @Modifying
    void deleteByChatEntity(ChatEntity chatEntity);

    @Modifying
    void deleteByChatEntityAndLinkEntity(ChatEntity chatEntity, LinkEntity linkEntity);
}
