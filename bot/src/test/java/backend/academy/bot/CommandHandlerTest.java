package backend.academy.bot;

import backend.academy.dto.ApiErrorResponse;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommandHandlerTest {
    @Mock
    private ChatRepository chatRepository;

    @Mock
    private ScrapperClient scrapperClient;

    @InjectMocks
    private CommandHandler commandHandler;

    @Test
    void handle_Default() {
        // Arrange
        long chatId = 123L;
        String receivedText = "/start";

        when(chatRepository.getState(chatId)).thenReturn(BotState.getInstance());
        when(scrapperClient.registerChat(chatId)).thenReturn(ResponseEntity.ok().build());

        // Act
        commandHandler.handle(chatId, receivedText);

        // Assert
        verify(chatRepository, times(1)).getState(chatId);
        verify(scrapperClient, times(1)).registerChat(chatId);
        verify(chatRepository, times(1)).registerChat(chatId);
    }
}
