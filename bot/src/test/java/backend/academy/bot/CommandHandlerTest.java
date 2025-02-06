package backend.academy.bot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CommandHandlerTest {
    @Mock
    private BotService botService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommandHandler commandHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHandlerStartCommand_NewUser() {
        // Arrange
        long chatId = 123L;
        when(userRepository.isUserRegistered(chatId)).thenReturn(false);

        // Act
        commandHandler.handleCommand(chatId, "/start");

        // Assert
        verify(userRepository).registerUser(chatId);
        verify(botService).sendMessage(
            chatId, "Welcome! You have successfully registered. Type /help for a list of commands."
        );
    }

    @Test
    void testHandleStartCommand_ExistingUser() {
        // Arrange
        long chatId = 456L;
        when(userRepository.isUserRegistered(chatId)).thenReturn(true);

        // Act
        commandHandler.handleCommand(chatId, "/start");

        // Assert
        verify(userRepository, never()).registerUser(chatId);
        verify(botService).sendMessage(
            chatId,
            "You are already registered. Type /help for a list of commands."
        );
    }
}
