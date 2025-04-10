package backend.academy.scrapper.repository;

import static org.assertj.core.api.Assertions.assertThat;

import backend.academy.scrapper.DateTimeUtils;
import backend.academy.scrapper.LinkType;
import backend.academy.scrapper.TestcontainersConfiguration;
import backend.academy.scrapper.entity.ChatEntity;
import backend.academy.scrapper.entity.ChatLinkFilterEntity;
import backend.academy.scrapper.entity.ChatLinkFilterId;
import backend.academy.scrapper.entity.FilterEntity;
import backend.academy.scrapper.entity.LinkEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@Transactional
class ChatLinkFilterRepositoryTest {
    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private LinkRepository linkRepository;

    @Autowired
    private FilterRepository filterRepository;

    @Autowired
    private ChatLinkFilterRepository chatLinkFilterRepository;

    private ChatEntity chatEntity;
    private LinkEntity linkEntity;
    private FilterEntity filterEntity;

    @BeforeEach
    void setUp() {
        chatEntity = new ChatEntity(1L);
        chatEntity = chatRepository.save(chatEntity);

        linkEntity = new LinkEntity();
        linkEntity.url("https://github.com/repo/owner");
        linkEntity.type(LinkType.GITHUB);
        linkEntity.lastUpdate(DateTimeUtils.now());
        linkEntity = linkRepository.save(linkEntity);

        filterEntity = new FilterEntity();
        filterEntity.name("key");
        filterEntity.pattern("value");
        filterEntity = filterRepository.save(filterEntity);
    }

    @Test
    void shouldSaveChatLinkFilter() {
        // Arrange
        ChatLinkFilterId chatLinkFilterId = new ChatLinkFilterId(chatEntity.id(), linkEntity.id(), filterEntity.id());
        ChatLinkFilterEntity chatLinkFilterEntity =
                new ChatLinkFilterEntity(chatLinkFilterId, chatEntity, linkEntity, filterEntity);

        // Act
        ChatLinkFilterEntity savedEntity = chatLinkFilterRepository.save(chatLinkFilterEntity);

        // Assert
        assertThat(savedEntity).isNotNull();
        assertThat(savedEntity.id()).isEqualTo(chatLinkFilterId);
        assertThat(savedEntity.chatEntity()).isEqualTo(chatEntity);
        assertThat(savedEntity.linkEntity()).isEqualTo(linkEntity);
        assertThat(savedEntity.filterEntity()).isEqualTo(filterEntity);
    }

    @Test
    void shouldExistById() {
        // Arrange
        ChatLinkFilterId chatLinkFilterId = new ChatLinkFilterId(chatEntity.id(), linkEntity.id(), filterEntity.id());
        ChatLinkFilterEntity chatLinkFilterEntity =
                new ChatLinkFilterEntity(chatLinkFilterId, chatEntity, linkEntity, filterEntity);

        chatLinkFilterRepository.save(chatLinkFilterEntity);

        // Act
        boolean exists = chatLinkFilterRepository.existsById(chatLinkFilterId);

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    void shouldDeleteByChatEntity() {
        // Arrange
        ChatLinkFilterId chatLinkFilterId = new ChatLinkFilterId(chatEntity.id(), linkEntity.id(), filterEntity.id());
        ChatLinkFilterEntity chatLinkFilterEntity =
                new ChatLinkFilterEntity(chatLinkFilterId, chatEntity, linkEntity, filterEntity);

        chatLinkFilterRepository.save(chatLinkFilterEntity);

        // Act
        chatLinkFilterRepository.deleteByChatEntity(chatEntity);

        // Assert
        boolean exists = chatLinkFilterRepository.existsById(chatLinkFilterId);
        assertThat(exists).isFalse();
    }

    @Test
    void shouldDeleteByChatEntityAndLinkEntity() {
        // Arrange
        ChatLinkFilterId chatLinkFilterId = new ChatLinkFilterId(chatEntity.id(), linkEntity.id(), filterEntity.id());
        ChatLinkFilterEntity chatLinkFilterEntity =
                new ChatLinkFilterEntity(chatLinkFilterId, chatEntity, linkEntity, filterEntity);

        chatLinkFilterRepository.save(chatLinkFilterEntity);

        // Act
        chatLinkFilterRepository.deleteByChatEntityAndLinkEntity(chatEntity, linkEntity);

        // Assert
        boolean exists = chatLinkFilterRepository.existsById(chatLinkFilterId);
        assertThat(exists).isFalse();
    }
}
