package backend.academy.scrapper.repository.impl;

import backend.academy.scrapper.entity.ChatLinkFilterEntity;
import backend.academy.scrapper.entity.ChatLinkFilterId;
import backend.academy.scrapper.repository.ChatLinkFilterRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrmChatLinkFilterRepository
        extends ChatLinkFilterRepository, JpaRepository<ChatLinkFilterEntity, ChatLinkFilterId> {}
