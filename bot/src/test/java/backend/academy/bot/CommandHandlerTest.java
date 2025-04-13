package backend.academy.bot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import backend.academy.dto.ApiErrorResponse;
import backend.academy.dto.LinkResponse;
import backend.academy.dto.ListLinksResponse;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@Import({TestcontainersConfiguration.class, NoCacheTestConfig.class})
@SpringBootTest(properties = "spring.config.name=application-test")
class CommandHandlerTest {
    @MockitoSpyBean
    private ChatRepository chatRepository;

    @MockitoSpyBean
    private ScrapperClient scrapperClient;

    @Autowired
    private CommandHandler commandHandler;

    @AfterEach
    void tearDown() {
        clearInvocations(chatRepository, scrapperClient);
        reset(chatRepository, scrapperClient);
    }

    @Test
    void testUnknownCommand_ReturnsErrorMessage() {
        // Arrange
        long chatId = 1L;
        String unknownCommand = "/unknownCommand";

        doReturn(BotState.getInstance()).when(chatRepository).getState(chatId);

        // Act
        String response = commandHandler.handle(chatId, unknownCommand);

        // Assert
        String expectedResponse =
                """
            Unknown command.
            Use /help for more information.
            """;
        assertEquals(expectedResponse, response);
    }

    @Test
    void testRandomTextAsCommand_ReturnsErrorMessage() {
        // Arrange
        long chatId = 1L;
        String randomText = "Hello, bot!";

        doReturn(BotState.getInstance()).when(chatRepository).getState(chatId);

        // Act
        String response = commandHandler.handle(chatId, randomText);

        // Assert
        String expectedResponse =
                """
            Unknown command.
            Use /help for more information.
            """;
        assertEquals(expectedResponse, response);
    }

    @Test
    void testHelpCommand_ReturnsHelpMessage() {
        // Arrange
        long chatId = 1L;
        String receivedText = "/help";

        doReturn(BotState.getInstance()).when(chatRepository).getState(chatId);

        // Act
        String response = commandHandler.handle(chatId, receivedText);

        // Assert
        String expectedResponse =
                """
            /start - register chat
            /end - delete chat
            /track - track link
            /untrack - untrack link
            /list - show list of tracked links
            /list_by_tag - show list of tracked links by tag
            /help - list of commands
            """;
        assertEquals(expectedResponse, response);
    }

    @Test
    void handleListCommand_ShouldReturnNoTrackedLinks_WhenListIsEmpty() {
        // Arrange
        long chatId = 1L;

        doReturn(BotState.getInstance()).when(chatRepository).getState(chatId);
        doReturn(ResponseEntity.status(HttpStatus.OK).body(new ListLinksResponse(List.of(), 0)))
                .when(scrapperClient)
                .getLinks(chatId);

        // Act
        String response = commandHandler.handle(chatId, "/list");

        // Assert
        assertEquals("No tracked links.", response);
    }

    @Test
    void handleListCommand_ShouldReturnSingleLink_WhenOneTrackedLinkExists() {
        // Arrange
        long chatId = 1L;
        LinkResponse link =
                new LinkResponse(1L, "https://github.com/owner/repo", List.of("tag1", "tag2"), List.of("filter:value"));

        doReturn(BotState.getInstance()).when(chatRepository).getState(chatId);
        doReturn(ResponseEntity.status(HttpStatus.OK).body(new ListLinksResponse(List.of(link), 1)))
                .when(scrapperClient)
                .getLinks(chatId);

        // Act
        String response = commandHandler.handle(chatId, "/list");

        // Assert
        String expectedResponse =
                """
            Link:
            	url: https://github.com/owner/repo
            	tags: tag1, tag2
            	filters: filter:value
            """;
        assertEquals(expectedResponse, response);
    }

    @Test
    void handleListCommand_ShouldReturnMultipleLinks_WhenMultipleTrackedLinksExist() {
        // Arrange
        long chatId = 1L;
        LinkResponse link1 = new LinkResponse(1L, "https://github.com/owner1/repo1", List.of(), List.of());
        LinkResponse link2 = new LinkResponse(1L, "https://github.com/owner2/repo2", List.of(), List.of());

        doReturn(BotState.getInstance()).when(chatRepository).getState(chatId);
        doReturn(ResponseEntity.status(HttpStatus.OK).body(new ListLinksResponse(List.of(link1, link2), 1)))
                .when(scrapperClient)
                .getLinks(chatId);

        // Act
        String response = commandHandler.handle(chatId, "/list");

        // Assert
        String expectedResponse =
                """
            Link:
            	url: https://github.com/owner1/repo1

            Link:
            	url: https://github.com/owner2/repo2
            """;
        assertEquals(expectedResponse, response);
    }

    @Test
    void handleListCommand_ShouldReturnErrorMessage_WhenBadRequest() {
        // Arrange
        long chatId = 1L;
        ApiErrorResponse apiErrorResponse =
                new ApiErrorResponse("description", "400", "exception-name", "exception-message", List.of());

        doReturn(BotState.getInstance()).when(chatRepository).getState(chatId);
        doReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiErrorResponse))
                .when(scrapperClient)
                .getLinks(chatId);

        // Act
        String response = commandHandler.handle(chatId, "/list");

        // Assert
        String expectedResponse = "description";
        assertEquals(expectedResponse, response);
    }

    @Test
    void handleListCommand_ShouldReturnGenericErrorMessage_WhenUnexpectedStatus() {
        // Arrange
        long chatId = 1L;

        doReturn(BotState.getInstance()).when(chatRepository).getState(chatId);
        when(scrapperClient.getLinks(chatId))
                .thenReturn(
                        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());

        // Act
        String response = commandHandler.handle(chatId, "/list");

        // Assert
        String expectedResponse = "Oops, something went wrong!";
        assertEquals(expectedResponse, response);
    }
}
