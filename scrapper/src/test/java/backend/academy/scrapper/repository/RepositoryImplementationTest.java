package backend.academy.scrapper.repository;

import backend.academy.scrapper.ScrapperConfig;
import backend.academy.scrapper.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class RepositoryImplementationTest {
    @Autowired
    private ScrapperConfig scrapperConfig;

    @Autowired
    private LinkRepository linkRepository;

    @Test
    void shouldUseCorrectImplementation() {
        // Arrange
        ScrapperConfig.DataBase.AccessType accessType = scrapperConfig.dataBase().accessType();

        // Act
        String linkRepositoryClassName = linkRepository.getClass().getSimpleName();

        // Assert
        switch (accessType) {
            case SQL -> assertTrue(linkRepositoryClassName.startsWith("SqlLinkRepository"));
            case ORM -> assertTrue(linkRepositoryClassName.startsWith("OrmLinkRepository"));
        }
    }
}
