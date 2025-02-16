package backend.academy.scrapper;

import backend.academy.dto.AddLinkRequest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChatLinkRepositoryImplTest {
    private ChatLinkRepositoryImpl repository;

    private Link gitHubLink;
    private Link stackOverflowLink;

    @BeforeEach
    void setUp() {
        repository = new ChatLinkRepositoryImpl();

        gitHubLink = Link.parse(
            new AddLinkRequest(
                "https://github.com/user/repo",
                List.of("github-tag"),
                List.of("filter:value")
            )
        );
        stackOverflowLink = Link.parse(
            new AddLinkRequest(
                "https://stackoverflow.com/questions/12345/title",
                List.of("stackoverflow-tag"),
                List.of()
            )
        );
    }

    @Test
    void testAddNewLink_Success() {
        // Arrange
        long chatId = 1L;

        // Act
        assertTrue(repository.addLink(chatId, gitHubLink));

        // Assert
        List<Link> links = repository.getLinks(chatId);
        assertEquals(1, links.size());
        assertEquals(gitHubLink, links.getFirst());
    }

    @Test
    void testAddDuplicateLink_Fails() {
        // Arrange
        long chatId = 1L;

        // Act
        assertTrue(repository.addLink(chatId, gitHubLink));
        assertFalse(repository.addLink(chatId, gitHubLink));

        // Assert
        List<Link> links = repository.getLinks(chatId);
        assertEquals(1, links.size());
    }

    @Test
    void testAddSameLinkToDifferentChats_Success() {
        // Arrange
        long chatId1 = 1L;
        long chatId2 = 2L;

        // Act
        assertTrue(repository.addLink(chatId1, gitHubLink));
        assertTrue(repository.addLink(chatId2, gitHubLink));

        // Assert
        assertEquals(1, repository.getLinks(chatId1).size());
        assertEquals(1, repository.getLinks(chatId2).size());

        List<ChatLink> chatLinks = repository.getLink2ChatIds();
        assertEquals(1, chatLinks.size());
        assertEquals(2, chatLinks.getFirst().chatIds().size());
    }

    @Test
    void testAddMultipleLinksToChat_Success() {
        // Arrange
        long chatId = 1L;

        // Act
        assertTrue(repository.addLink(chatId, gitHubLink));
        assertTrue(repository.addLink(chatId, stackOverflowLink));

        // Assert
        List<Link> links = repository.getLinks(chatId);
        assertEquals(2, links.size());
        assertTrue(links.contains(gitHubLink));
        assertTrue(links.contains(stackOverflowLink));
    }
}
