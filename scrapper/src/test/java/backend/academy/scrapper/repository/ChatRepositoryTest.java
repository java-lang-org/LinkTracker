package backend.academy.scrapper.repository;

import static org.assertj.core.api.Assertions.assertThat;

import backend.academy.scrapper.TestcontainersConfiguration;
import backend.academy.scrapper.entity.ChatEntity;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(properties = "spring.config.name=application-test")
@Transactional
class ChatRepositoryTest {
    @Autowired
    private ChatRepository chatRepository;

    @Test
    void shouldSaveAndFindChatById() {
        // Arrange
        ChatEntity chatEntity = new ChatEntity(1L);

        ChatEntity savedChatEntity = chatRepository.save(chatEntity);

        // Act
        Optional<ChatEntity> foundChatEntity = chatRepository.findById(savedChatEntity.id());

        // Assert
        assertThat(foundChatEntity).isPresent();
        assertThat(foundChatEntity.orElseThrow().id()).isEqualTo(savedChatEntity.id());
    }

    @Test
    void shouldReturnFalseIfChatDoesNotExist() {
        // Arrange
        long id = 999L;

        // Act
        boolean exists = chatRepository.existsById(id);

        // Assert
        assertThat(exists).isFalse();
    }

    @Test
    void shouldReturnTrueIfChatExists() {
        // Arrange
        long id = 1L;
        ChatEntity chatEntity = new ChatEntity(id);

        chatRepository.save(chatEntity);

        // Act
        boolean exists = chatRepository.existsById(id);

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    void shouldDeleteChat() {
        // Arrange
        long id = 1L;
        ChatEntity chatEntity = new ChatEntity(id);

        chatRepository.save(chatEntity);

        // Act
        chatRepository.delete(chatEntity);

        // Assert
        Optional<ChatEntity> foundChatEntity = chatRepository.findById(id);
        assertThat(foundChatEntity).isEmpty();
    }
}
