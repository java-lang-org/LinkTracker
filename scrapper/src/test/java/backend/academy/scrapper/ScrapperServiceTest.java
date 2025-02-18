package backend.academy.scrapper;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import backend.academy.dto.AddLinkRequest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ScrapperServiceTest {
    @Mock
    private ChatService chatService;

    @Mock
    private GitHubClient gitHubClient;

    @Mock
    private StackOverflowClient stackOverflowClient;

    @Mock
    private BotClient botClient;

    @InjectMocks
    private ScrapperService scrapperService;

    private final Link gitHubLink = Link.parse(
            new AddLinkRequest("https://github.com/user/repo", List.of("github-tag"), List.of("filter:value")));
    private final Link stackOverflowLink = Link.parse(
            new AddLinkRequest("https://stackoverflow.com/questions/12345", List.of("stackoverflow-tag"), List.of()));
    private final List<Long> chatIds = List.of(1L, 2L, 3L);

    @BeforeEach
    void setUp() {
        reset(chatService, gitHubClient, stackOverflowClient, botClient);
    }

    @Test
    void testCheckUpdates_GitHubUpdated_ShouldNotifySubscribedChats() {
        // Arrange
        when(chatService.getLink2ChatIds()).thenReturn(List.of(new ChatLink(gitHubLink, chatIds)));
        when(gitHubClient.hasRepositoryUpdated(gitHubLink)).thenReturn(true);

        // Act
        scrapperService.checkUpdates();

        // Assert
        verify(botClient, times(1)).updates(eq(gitHubLink), anyString(), eq(chatIds));
    }

    @Test
    void testCheckUpdates_StackOverflowUpdated_ShouldNotifySubscribedChats() {
        // Arrange
        when(chatService.getLink2ChatIds()).thenReturn(List.of(new ChatLink(stackOverflowLink, chatIds)));
        when(stackOverflowClient.hasRepositoryUpdated(stackOverflowLink)).thenReturn(true);

        // Act
        scrapperService.checkUpdates();

        // Assert
        verify(botClient, times(1)).updates(eq(stackOverflowLink), anyString(), eq(chatIds));
    }

    @Test
    void testCheckUpdates_NoUpdates_ShouldNotSendNotifications() {
        // Arrange
        when(chatService.getLink2ChatIds())
                .thenReturn(List.of(new ChatLink(gitHubLink, chatIds), new ChatLink(stackOverflowLink, chatIds)));
        when(gitHubClient.hasRepositoryUpdated(gitHubLink)).thenReturn(false);
        when(stackOverflowClient.hasRepositoryUpdated(stackOverflowLink)).thenReturn(false);

        // Act
        scrapperService.checkUpdates();

        // Assert
        verify(botClient, never()).updates(any(), anyString(), anyList());
    }

    @Test
    void testCheckUpdates_EmptySubscriptions_ShouldNotCallClients() {
        // Arrange
        when(chatService.getLink2ChatIds()).thenReturn(List.of());

        // Act
        scrapperService.checkUpdates();

        // Assert
        verifyNoInteractions(gitHubClient, stackOverflowClient, botClient);
    }
}
