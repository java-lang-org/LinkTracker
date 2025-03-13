package backend.academy.scrapper.repository;

import backend.academy.scrapper.ScrapperConfig;
import backend.academy.scrapper.TestcontainersConfiguration;
import backend.academy.scrapper.repository.impl.OrmChatRepository;
import backend.academy.scrapper.repository.impl.OrmFilterRepository;
import backend.academy.scrapper.repository.impl.OrmLinkRepository;
import backend.academy.scrapper.repository.impl.OrmTagRepository;
import backend.academy.scrapper.repository.impl.SqlChatRepository;
import backend.academy.scrapper.repository.impl.SqlFilterRepository;
import backend.academy.scrapper.repository.impl.SqlLinkRepository;
import backend.academy.scrapper.repository.impl.SqlTagRepository;
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

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private FilterRepository filterRepository;

    @Test
    void shouldUseCorrectImplementation() {
        // Arrange
        ScrapperConfig.DataBase.AccessType accessType = scrapperConfig.dataBase().accessType();

        // Act & Assert
        switch (accessType) {
            case SQL -> {
                assertThat(chatRepository instanceof SqlChatRepository).isTrue();
                assertThat(linkRepository instanceof SqlLinkRepository).isTrue();
                assertThat(tagRepository instanceof SqlTagRepository).isTrue();
                assertThat(filterRepository instanceof SqlFilterRepository).isTrue();
            }
            case ORM -> {
                assertThat(chatRepository instanceof OrmChatRepository).isTrue();
                assertThat(linkRepository instanceof OrmLinkRepository).isTrue();
                assertThat(tagRepository instanceof OrmTagRepository).isTrue();
                assertThat(filterRepository instanceof OrmFilterRepository).isTrue();
            }
        }
    }
}
