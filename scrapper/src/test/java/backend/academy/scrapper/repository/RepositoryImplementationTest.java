package backend.academy.scrapper.repository;

import backend.academy.scrapper.ScrapperConfig;
import backend.academy.scrapper.TestcontainersConfiguration;
import backend.academy.scrapper.repository.impl.OrmChatRepository;
import backend.academy.scrapper.repository.impl.OrmLinkRepository;
import backend.academy.scrapper.repository.impl.SqlChatRepository;
import backend.academy.scrapper.repository.impl.SqlLinkRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class RepositoryImplementationTest {
    @Autowired
    private ScrapperConfig scrapperConfig;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private LinkRepository linkRepository;

    @Test
    void shouldUseCorrectImplementation() {
        // Arrange
        ScrapperConfig.DataBase.AccessType accessType = scrapperConfig.dataBase().accessType();

        // Act & Assert
        switch (accessType) {
            case SQL -> {
                assertThat(chatRepository instanceof SqlChatRepository).isTrue();
                assertThat(linkRepository instanceof SqlLinkRepository).isTrue();
            }
            case ORM -> {
                assertThat(chatRepository instanceof OrmChatRepository).isTrue();
                assertThat(linkRepository instanceof OrmLinkRepository).isTrue();
            }
        }
    }
}
