package backend.academy.bot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CommandHandlerTest {
    private ChatRepository chatRepository;
    private ScrapperClient scrapperClient;
    private CommandHandler commandHandler;

    @BeforeEach
    void setUp() {
        chatRepository = mock(ChatRepository.class);
        scrapperClient = mock(ScrapperClient.class);
        commandHandler = new CommandHandler(chatRepository, scrapperClient);
    }

    @Test
    void testUnknownCommand_ReturnsErrorMessage() {
        // Arrange
        long chatId = 1L;
        String unknownCommand = "/unknownCommand";

        when(chatRepository.getState(chatId)).thenReturn(BotState.getInstance());

        // Act
        String response = commandHandler.handle(chatId, unknownCommand);

        // Assert
        String expectedResponse = """
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

        when(chatRepository.getState(chatId)).thenReturn(BotState.getInstance());

        // Act
        String response = commandHandler.handle(chatId, randomText);

        // Assert
        String expectedResponse = """
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
        when(chatRepository.getState(chatId)).thenReturn(BotState.getInstance());

        // Act
        String response = commandHandler.handle(chatId, receivedText);

        // Assert
        String expectedResponse = """
            /start - register chat
            /end - delete chat
            /track - track link
            /untrack - untrack link
            /list - show list of tracked links
            /help - list of commands
            """;
        assertEquals(expectedResponse, response);
    }
}
