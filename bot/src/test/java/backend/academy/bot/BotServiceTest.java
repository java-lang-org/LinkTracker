package backend.academy.bot;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class BotServiceTest {
    private final BotConfig botConfig = mock(BotConfig.class);

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommandHandler commandHandler;

    @InjectMocks
    private BotService botService;

    @BeforeEach
    void setUp() {
        when(botConfig.nThreads()).thenReturn(1);
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testProcessUpdate_ValidCommand() {
        // Arrange
        long chatId = 789L;
        String command = "/help";

        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);

        when(update.message()).thenReturn(message);

        when(message.chat()).thenReturn(chat);
        when(message.text()).thenReturn(command);

        when(chat.id()).thenReturn(chatId);

        when(userRepository.getState(chatId)).thenReturn(BotState.DEFAULT);

        // Act
        botService.processUpdate(update);

        // Assert
        verify(commandHandler).handleCommand(chatId, command);
    }

    @Test
    void testProcessUpdate_NullMessage() {
        // Arrange
        Update update = mock(Update.class);
        when(update.message()).thenReturn(null);

        // Act
        botService.processUpdate(update);

        // Assert
        verifyNoInteractions(userRepository, commandHandler);
    }
}
