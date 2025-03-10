package backend.academy.scrapper.repository.impl;

import backend.academy.scrapper.entity.ChatLinkTagEntity;
import backend.academy.scrapper.entity.ChatLinkTagId;
import backend.academy.scrapper.repository.ChatLinkTagRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrmChatLinkTagRepository
        extends ChatLinkTagRepository, JpaRepository<ChatLinkTagEntity, ChatLinkTagId> {}
