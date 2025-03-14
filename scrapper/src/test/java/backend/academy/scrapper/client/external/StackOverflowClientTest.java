package backend.academy.scrapper.client.external;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import backend.academy.scrapper.DateTimeUtils;
import backend.academy.scrapper.Link;
import backend.academy.scrapper.LinkType;
import backend.academy.scrapper.ScrapperConfig;
import backend.academy.scrapper.client.external.stackoverflow.StackOverflowClient;
import backend.academy.scrapper.client.external.stackoverflow.StackOverflowEvent;
import backend.academy.scrapper.client.external.stackoverflow.StackOverflowEventResponse;
import backend.academy.scrapper.client.external.stackoverflow.StackOverflowQuestion;
import backend.academy.scrapper.client.external.stackoverflow.StackOverflowResponse;
import backend.academy.scrapper.client.external.stackoverflow.StackOverflowUser;
import java.time.ZonedDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestClient;

class StackOverflowClientTest {
    @Mock
    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private RestClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    private final String baseUrl = "https://api.stackexchange.com/2.3";

    private final RestClient restClient = mock(RestClient.class);

    private final ScrapperConfig scrapperConfig = mock(ScrapperConfig.class);

    private final StackOverflowClient stackOverflowClient =
            new StackOverflowClient(baseUrl, restClient, scrapperConfig);

    private Link link;
    private final ZonedDateTime pastDate = DateTimeUtils.now().minusDays(1);
    private final ZonedDateTime recentDate = DateTimeUtils.now();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        link = Link.getInstance("https://stackoverflow.com/questions/12345", LinkType.STACK_OVERFLOW, pastDate);
    }

    @Test
    void shouldFormatAnswerMessageCorrectly() {
        // Arrange
        StackOverflowQuestion question = new StackOverflowQuestion(12345L, "How to test in Java?", pastDate);
        StackOverflowEvent answer =
                new StackOverflowEvent(new StackOverflowUser("TestUser"), recentDate, "Use JUnit and Mockito.");

        mockRestClient("/questions/12345");
        mockRestClient("/questions/12345/answers");
        mockRestClient("/questions/12345/comments");

        when(responseSpec.body(StackOverflowResponse.class)).thenReturn(new StackOverflowResponse(List.of(question)));
        when(responseSpec.body(StackOverflowEventResponse.class))
                .thenReturn(new StackOverflowEventResponse(List.of(answer)))
                .thenReturn(new StackOverflowEventResponse(List.of()));

        // Act
        List<String> events = stackOverflowClient.getRecentEvents(link);

        // Assert
        assertEquals(1, events.size());
        assertTrue(events.getFirst().contains("How to test in Java?"));
        assertTrue(events.getFirst().contains("by TestUser"));
        assertTrue(events.getFirst().contains("Use JUnit and Mockito."));
    }

    @Test
    void shouldFormatCommentMessageCorrectly() {
        // Arrange
        StackOverflowQuestion question = new StackOverflowQuestion(12345L, "How to test in Java?", pastDate);
        StackOverflowEvent comment =
                new StackOverflowEvent(new StackOverflowUser("DevCommenter"), recentDate, "Good answer!");

        mockRestClient("/questions/12345");
        mockRestClient("/questions/12345/answers");
        mockRestClient("/questions/12345/comments");

        when(responseSpec.body(StackOverflowResponse.class)).thenReturn(new StackOverflowResponse(List.of(question)));
        when(responseSpec.body(StackOverflowEventResponse.class))
                .thenReturn(new StackOverflowEventResponse(List.of()))
                .thenReturn(new StackOverflowEventResponse(List.of(comment)));

        // Act
        List<String> events = stackOverflowClient.getRecentEvents(link);

        // Assert
        assertEquals(1, events.size());
        assertTrue(events.getFirst().contains("How to test in Java?"));
        assertTrue(events.getFirst().contains("by DevCommenter"));
        assertTrue(events.getFirst().contains("Good answer!"));
    }

    @Test
    void shouldTruncateLongMessages() {
        // Arrange
        StackOverflowQuestion question = new StackOverflowQuestion(12345L, "How to test in Java?", pastDate);
        String longBody = "A".repeat(1000);
        StackOverflowEvent answer =
                new StackOverflowEvent(new StackOverflowUser("LongAnswerUser"), recentDate, longBody);

        mockRestClient("/questions/12345");
        mockRestClient("/questions/12345/answers");
        mockRestClient("/questions/12345/comments");

        when(responseSpec.body(StackOverflowResponse.class)).thenReturn(new StackOverflowResponse(List.of(question)));
        when(responseSpec.body(StackOverflowEventResponse.class))
                .thenReturn(new StackOverflowEventResponse(List.of(answer)))
                .thenReturn(new StackOverflowEventResponse(List.of()));

        // Act
        List<String> events = stackOverflowClient.getRecentEvents(link);

        // Assert
        assertEquals(1, events.size());
        assertTrue(events.getFirst().contains("How to test in Java?"));
        assertTrue(events.getFirst().contains("by LongAnswerUser"));
        assertTrue(events.getFirst().contains("AAA"));
        assertFalse(events.getFirst().contains("A".repeat(500)));
    }

    private void mockRestClient(String url) {
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(contains(url))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }
}
