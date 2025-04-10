package backend.academy.scrapper.repository;

import static org.assertj.core.api.Assertions.assertThat;

import backend.academy.scrapper.DateTimeUtils;
import backend.academy.scrapper.LinkType;
import backend.academy.scrapper.TestcontainersConfiguration;
import backend.academy.scrapper.entity.ChatEntity;
import backend.academy.scrapper.entity.ChatLinkEntity;
import backend.academy.scrapper.entity.ChatLinkId;
import backend.academy.scrapper.entity.LinkEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(properties = "spring.config.name=application-test")
@Transactional
class ChatLinkRepositoryTest {
    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private LinkRepository linkRepository;

    @Autowired
    private ChatLinkRepository chatLinkRepository;

    @Test
    void shouldInsertAndFindChatLink() {
        // Arrange
        ChatEntity chatEntity = new ChatEntity(1L);
        chatEntity = chatRepository.save(chatEntity);

        LinkEntity linkEntity = new LinkEntity();
        linkEntity.url("https://github.com/owner/repo");
        linkEntity.type(LinkType.GITHUB);
        linkEntity.lastUpdate(DateTimeUtils.now());
        linkEntity = linkRepository.save(linkEntity);

        ChatLinkId chatLinkId = new ChatLinkId(chatEntity.id(), linkEntity.id());
        ChatLinkEntity chatLinkEntity = new ChatLinkEntity(chatLinkId, chatEntity, linkEntity);

        // Act
        ChatLinkEntity savedChatLinkEntity = chatLinkRepository.save(chatLinkEntity);
        boolean exists = chatLinkRepository.existsById(chatLinkId);

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    void shouldDeleteChatLink() {
        // Arrange
        ChatEntity chatEntity = new ChatEntity(2L);
        chatEntity = chatRepository.save(chatEntity);

        LinkEntity linkEntity = new LinkEntity();
        linkEntity.url("https://github.com/owner/repo");
        linkEntity.type(LinkType.GITHUB);
        linkEntity.lastUpdate(DateTimeUtils.now());
        linkEntity = linkRepository.save(linkEntity);

        ChatLinkId chatLinkId = new ChatLinkId(chatEntity.id(), linkEntity.id());
        ChatLinkEntity chatLinkEntity = new ChatLinkEntity(chatLinkId, chatEntity, linkEntity);
        chatLinkRepository.save(chatLinkEntity);

        // Act
        chatLinkRepository.deleteByChatEntityAndLinkEntity(chatEntity, linkEntity);
        boolean exists = chatLinkRepository.existsById(chatLinkId);

        // Assert
        assertThat(exists).isFalse();
    }
}
