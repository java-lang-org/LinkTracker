package backend.academy.scrapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import backend.academy.dto.AddLinkRequest;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ChatLinkRepositoryImplTest {
    private ChatLinkRepositoryImpl repository;

    private Link gitHubLink;
    private Link stackOverflowLink;

    @BeforeEach
    void setUp() {
        repository = new ChatLinkRepositoryImpl();

        gitHubLink = Link.parse(
                new AddLinkRequest("https://github.com/user/repo", List.of("github-tag"), List.of("filter:value")));
        stackOverflowLink = Link.parse(new AddLinkRequest(
                "https://stackoverflow.com/questions/12345", List.of("stackoverflow-tag"), List.of()));
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

    @Test
    void testRemoveExistingLink_ShouldReturnLink() {
        // Arrange
        long chatId = 1L;

        // Act
        assertTrue(repository.addLink(chatId, gitHubLink));
        Optional<Link> removedLink = repository.removeLink(chatId, gitHubLink.url());

        // Assert
        assertTrue(removedLink.isPresent());
        assertEquals(gitHubLink, removedLink.orElseThrow());
        assertTrue(repository.getLinks(chatId).isEmpty());
    }

    @Test
    void testRemoveNonExistingLink_ShouldReturnEmpty() {
        // Arrange
        long chatId = 1L;
        String nonExistingUrl = "https://notexists.com";

        // Act
        Optional<Link> removedLink = repository.removeLink(chatId, nonExistingUrl);

        // Assert
        assertTrue(removedLink.isEmpty());
    }

    @Test
    void testRemoveLinkFromEmptyChat_ShouldReturnEmpty() {
        // Arrange
        long chatId1 = 1L;
        long chatId2 = 2L;

        // Act
        assertTrue(repository.addLink(chatId1, gitHubLink));
        Optional<Link> removedLink = repository.removeLink(chatId2, gitHubLink.url());

        // Assert
        assertTrue(removedLink.isEmpty());
    }

    @Test
    void testRemoveLinkTrackedByMultipleChats_ShouldNotDeleteLinkFromRepository() {
        // Arrange
        long chatId1 = 1L;
        long chatId2 = 2L;

        // Act
        assertTrue(repository.addLink(chatId1, gitHubLink));
        assertTrue(repository.addLink(chatId2, gitHubLink));
        repository.removeLink(chatId1, gitHubLink.url());

        // Assert
        List<Link> linksForChat2 = repository.getLinks(chatId2);
        assertFalse(linksForChat2.isEmpty());
        assertEquals(gitHubLink, linksForChat2.getFirst());
    }

    @Test
    void testRemoveLinkCompletely_WhenLastChatRemovesIt() {
        // Arrange
        long chatId = 1L;

        // Act
        assertTrue(repository.addLink(chatId, stackOverflowLink));
        repository.removeLink(chatId, stackOverflowLink.url());

        // Assert
        List<ChatLink> chatLinks = repository.getLink2ChatIds();
        assertTrue(chatLinks.isEmpty());
    }
}
