package backend.academy.scrapper.client.external;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import backend.academy.scrapper.DateTimeUtils;
import backend.academy.scrapper.Link;
import backend.academy.scrapper.LinkType;
import backend.academy.scrapper.client.external.github.GitHubClient;
import backend.academy.scrapper.client.external.github.GitHubEvent;
import backend.academy.scrapper.config.GitHubConfig;
import java.time.ZonedDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestClient;

class GitHubClientTest {
    @Mock
    private GitHubConfig gitHubConfig;

    @Mock
    private RestClient restClient;

    @InjectMocks
    private GitHubClient gitHubClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetRecentEvents_PullRequestEvent() {
        // Arrange
        ZonedDateTime now = DateTimeUtils.now();
        Link link = Link.getInstance("https://github.com/test/repo", LinkType.GITHUB, now.minusDays(1));

        GitHubEvent event = new GitHubEvent(
                "PullRequestEvent",
                new GitHubEvent.Actor("testUser"),
                now,
                new GitHubEvent.Payload(new GitHubEvent.Payload.PullRequest("PR Title", "PR Description"), null));

        mockGitHubApiResponse(event);

        // Act
        List<String> events = gitHubClient.getRecentEvents(link);

        // Assert
        assertFalse(events.isEmpty());
        assertTrue(events.getFirst().contains("**PR Title** (by testUser)"));
        assertTrue(events.getFirst().contains("PR Description"));
    }

    @Test
    void testGetRecentEvents_IssueEvent() {
        // Arrange
        ZonedDateTime now = DateTimeUtils.now();
        Link link = Link.getInstance("https://github.com/test/repo", LinkType.GITHUB, now.minusDays(1));

        GitHubEvent event = new GitHubEvent(
                "IssuesEvent",
                new GitHubEvent.Actor("issueUser"),
                now,
                new GitHubEvent.Payload(null, new GitHubEvent.Payload.Issue("Issue Title", "Issue Description")));

        mockGitHubApiResponse(event);

        // Act
        List<String> events = gitHubClient.getRecentEvents(link);

        // Assert
        assertFalse(events.isEmpty());
        assertTrue(events.getFirst().contains("**Issue Title** (by issueUser)"));
        assertTrue(events.getFirst().contains("Issue Description"));
    }

    @Test
    void testGetRecentEvents_NoUpdates() {
        // Arrange
        ZonedDateTime now = DateTimeUtils.now();
        Link link = Link.getInstance("https://github.com/test/repo", LinkType.GITHUB, now.minusDays(1));

        mockGitHubApiResponse();

        // Act
        List<String> events = gitHubClient.getRecentEvents(link);

        // Assert
        assertTrue(events.isEmpty());
    }

    private void mockGitHubApiResponse(GitHubEvent... events) {
        RestClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec requestHeadersSpec = mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(contains("/repos/{owner}/{repo}/events"), any(String.class), any(String.class)))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(GitHubEvent[].class)).thenReturn(events);
    }
}
