package backend.academy.scrapper.repository;

import static org.assertj.core.api.Assertions.assertThat;

import backend.academy.scrapper.DateTimeUtils;
import backend.academy.scrapper.LinkType;
import backend.academy.scrapper.TestcontainersConfiguration;
import backend.academy.scrapper.entity.ChatEntity;
import backend.academy.scrapper.entity.ChatLinkTagEntity;
import backend.academy.scrapper.entity.ChatLinkTagId;
import backend.academy.scrapper.entity.LinkEntity;
import backend.academy.scrapper.entity.TagEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@Transactional
class ChatLinkTagRepositoryTest {
    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private LinkRepository linkRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private ChatLinkTagRepository chatLinkTagRepository;

    private ChatEntity chatEntity;
    private LinkEntity linkEntity;
    private TagEntity tagEntity;

    @BeforeEach
    void setUp() {
        chatEntity = chatRepository.save(new ChatEntity(1L));

        linkEntity = new LinkEntity();
        linkEntity.url("https://github.com/repo/owner");
        linkEntity.type(LinkType.GITHUB);
        linkEntity.lastUpdate(DateTimeUtils.now());
        linkEntity = linkRepository.save(linkEntity);

        tagEntity = new TagEntity();
        tagEntity.name("tag");
        tagEntity = tagRepository.save(tagEntity);
    }

    @Test
    void shouldSaveChatLinkTag() {
        // Arrange
        ChatLinkTagEntity chatLinkTagEntity = new ChatLinkTagEntity();
        chatLinkTagEntity.id(new ChatLinkTagId(chatEntity.id(), linkEntity.id(), tagEntity.id()));
        chatLinkTagEntity.chatEntity(chatEntity);
        chatLinkTagEntity.linkEntity(linkEntity);
        chatLinkTagEntity.tagEntity(tagEntity);

        // Act
        ChatLinkTagEntity savedEntity = chatLinkTagRepository.save(chatLinkTagEntity);

        // Assert
        assertThat(savedEntity).isNotNull();
        assertThat(savedEntity.id()).isNotNull();
        assertThat(savedEntity.chatEntity()).isEqualTo(chatEntity);
        assertThat(savedEntity.linkEntity()).isEqualTo(linkEntity);
        assertThat(savedEntity.tagEntity()).isEqualTo(tagEntity);
    }

    @Test
    void shouldCheckIfChatLinkTagExists() {
        // Arrange
        ChatLinkTagEntity chatLinkTagEntity = new ChatLinkTagEntity();
        chatLinkTagEntity.id(new ChatLinkTagId(chatEntity.id(), linkEntity.id(), tagEntity.id()));
        chatLinkTagEntity.chatEntity(chatEntity);
        chatLinkTagEntity.linkEntity(linkEntity);
        chatLinkTagEntity.tagEntity(tagEntity);

        chatLinkTagRepository.save(chatLinkTagEntity);

        // Act
        boolean exists =
                chatLinkTagRepository.existsById(new ChatLinkTagId(chatEntity.id(), linkEntity.id(), tagEntity.id()));

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    void shouldDeleteByChatEntity() {
        // Arrange
        ChatLinkTagEntity chatLinkTagEntity = new ChatLinkTagEntity();
        chatLinkTagEntity.id(new ChatLinkTagId(chatEntity.id(), linkEntity.id(), tagEntity.id()));
        chatLinkTagEntity.chatEntity(chatEntity);
        chatLinkTagEntity.linkEntity(linkEntity);
        chatLinkTagEntity.tagEntity(tagEntity);

        chatLinkTagRepository.save(chatLinkTagEntity);

        // Act
        chatLinkTagRepository.deleteByChatEntity(chatEntity);

        // Assert
        boolean exists =
                chatLinkTagRepository.existsById(new ChatLinkTagId(chatEntity.id(), linkEntity.id(), tagEntity.id()));
        assertThat(exists).isFalse();
    }

    @Test
    void shouldDeleteByChatEntityAndLinkEntity() {
        // Arrange
        ChatLinkTagEntity chatLinkTagEntity = new ChatLinkTagEntity();
        chatLinkTagEntity.id(new ChatLinkTagId(chatEntity.id(), linkEntity.id(), tagEntity.id()));
        chatLinkTagEntity.chatEntity(chatEntity);
        chatLinkTagEntity.linkEntity(linkEntity);
        chatLinkTagEntity.tagEntity(tagEntity);

        chatLinkTagRepository.save(chatLinkTagEntity);

        // Act
        chatLinkTagRepository.deleteByChatEntityAndLinkEntity(chatEntity, linkEntity);

        // Assert
        boolean exists =
                chatLinkTagRepository.existsById(new ChatLinkTagId(chatEntity.id(), linkEntity.id(), tagEntity.id()));
        assertThat(exists).isFalse();
    }
}
