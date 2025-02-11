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
    void handle_WaitingForTrackUrl() {
        // Arrange
        long chatId = 123L;
        String receivedText = "http://example.com";

        when(chatRepository.getState(chatId)).thenReturn(BotState.WAITING_FOR_TRACK_URL);
        when(scrapperClient.addLinkTracking(chatId, receivedText)).thenReturn(ResponseEntity.ok().build());

        // Act
        commandHandler.handle(chatId, receivedText);

        // Assert
        verify(chatRepository, times(1)).setState(chatId, BotState.DEFAULT);
        verify(scrapperClient, times(1)).addLinkTracking(chatId, receivedText);
    }

    @Test
    void handle_WaitingForUntrackUrl() {
        // Arrange
        long chatId = 123L;
        String receivedText = "http://example.com";

        when(chatRepository.getState(chatId)).thenReturn(BotState.WAITING_FOR_UNTRACK_URL);
        when(scrapperClient.removeLinkTracking(chatId, receivedText)).thenReturn(
            (ResponseEntity) ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ApiErrorResponse(
                    "description",
                    "400",
                    "exceptionName",
                    "exceptionMessage",
                    List.of()
                )
            )
        );

        // Act
        commandHandler.handle(chatId, receivedText);

        // Assert
        verify(chatRepository, times(1)).setState(chatId, BotState.DEFAULT);
        verify(scrapperClient, times(1)).removeLinkTracking(chatId, receivedText);
    }

    @Test
    void handle_Default() {
        // Arrange
        long chatId = 123L;
        String receivedText = "/start";

        when(chatRepository.getState(chatId)).thenReturn(BotState.DEFAULT);
        when(scrapperClient.registerChat(chatId)).thenReturn(ResponseEntity.ok().build());

        // Act
        commandHandler.handle(chatId, receivedText);

        // Assert
        verify(chatRepository, times(1)).getState(chatId);
        verify(scrapperClient, times(1)).registerChat(chatId);
        verify(chatRepository, times(1)).registerChat(chatId);
    }
}
